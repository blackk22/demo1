package com.wonhigh.im.service;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.text.TextUtils;
import com.wonhigh.im.R;
import com.wonhigh.im.constants.IMConstants;
import com.wonhigh.im.db.ChatConstants;
import com.wonhigh.im.entity.ChatMessage;
import com.wonhigh.im.manager.IMDBManager;
import com.wonhigh.im.manager.IMNotificationManager;
import com.wonhigh.im.manager.IMXmppManager;
import com.wonhigh.im.util.IMLogger;
import com.wonhigh.im.util.IMNetUtil;
import com.wonhigh.im.util.IMPreferenceUtils;

/**
 * TODO: 增加描述
 * 
 * @author USER
 * @date 2014-11-25 上午11:31:20
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public class IMMainService extends Service implements IMNetChangeEventHandler {

	private static final String TAG = IMMainService.class.getSimpleName();

	public static final String PONG_TIMEOUT = "连接超时";// 连接超时

	public static final String NETWORK_ERROR = "网络错误";// 网络错误

	public static final String LOGIN_FAILED = "登录失败";// 登录失败

	public static final String DISCONNECTED_WITHOUT_WARNING = "disconnected without warning";// 没有警告的断开连接

	public static final String LOGOUT = "logout";// 手动退出

	public static final int CONNECTED = 0;//已连接

	public static final int DISCONNECTED = -1;//未连接

	public static final int CONNECTING = 1;//正在连接

	/**
	 * 是否第一次登入，第一次登入不需重连
	 */
	public static final String FIRST_BINDER_ACTION = "com.wonhigh.im.aciton.binder";

	private boolean mIsFirstBinderAction;//

	private IBinder mBinder = new IMBinder();

	//	private HashSet<String> mIsBoundTo = new HashSet<String>();//记录已广播消息

	private Handler mMainHandler = new Handler();

	private IMXmppManager mXmppManager;

	private ActivityManager mActivityManager;

	private Thread mConnectingThread;

	private IMConnectionStatusCallback mConnectionStatusCallback;

	/**
	 * 重连
	 */
	private static final int RECONNECT_AFTER = 5;

	private static final int RECONNECT_MAXIMUM = 1 * 60;// 最大重连时间间隔

	public static final String RECONNECT_LOGIN_ACTION = "com.wonhigh.im.action.LOGIN";

	private static final String RECONNECT_ALARM_ACTION = "com.wonhigh.im.action.RECONNECT_ALARM";

	protected int mConnectedState = DISCONNECTED; // 是否已经连接. 当前连接状态，默认为未连接

	public int mReconnectTimeout = RECONNECT_AFTER;

	private Intent mAlarmIntent = new Intent(RECONNECT_ALARM_ACTION);

	private PendingIntent mPAlarmIntent;

	private BroadcastReceiver mAlarmReceiver;

	private Vibrator vibrator;//震动
	/**
	 * 
	 */
	public static final String MESSAGE_RECEIPT_ALARM = "com.wonhigh.im.MESSAGE_RECEIPT_ALARM";

	private IMMsgReceiptReceiver imMsgReceiptReceiver;

	@Override
	public IBinder onBind(Intent intent) {

		IMLogger.d(TAG, "[SERVICE] onBind");
		//		String chatPartner = intent.getDataString();
		//		if ((chatPartner != null)) {
		//			mIsBoundTo.add(chatPartner);
		//		}
		String action = intent.getAction();
		if (!TextUtils.isEmpty(action) && TextUtils.equals(action, RECONNECT_LOGIN_ACTION)) {
			mIsFirstBinderAction = true;
		} else {
			mIsFirstBinderAction = false;
		}

		return mBinder;
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		//		String chatPartner = intent.getDataString();
		//		if ((chatPartner != null)) {
		//			mIsBoundTo.add(chatPartner);
		//		}
		String action = intent.getAction();
		if (!TextUtils.isEmpty(action) && TextUtils.equals(action, RECONNECT_LOGIN_ACTION)) {
			mIsFirstBinderAction = true;
		} else {
			mIsFirstBinderAction = false;
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		//		String chatPartner = intent.getDataString();
		//		if ((chatPartner != null)) {
		//			mIsBoundTo.remove(chatPartner);
		//		}
		return true;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		IMBroadcastReceiver.mListeners.add(this);
		mActivityManager = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
		mAlarmReceiver = new ReconnectAlarmReceiver(this);
		mPAlarmIntent = PendingIntent.getBroadcast(this, 0, mAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		registerReceiver(mAlarmReceiver, new IntentFilter(RECONNECT_ALARM_ACTION));
		vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

		//消息回执广播
		imMsgReceiptReceiver = new IMMsgReceiptReceiver(IMMainService.this);
		registerReceiver(imMsgReceiptReceiver, new IntentFilter(MESSAGE_RECEIPT_ALARM));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null && intent.getAction() != null
				&& TextUtils.equals(intent.getAction(), IMBroadcastReceiver.BOOT_COMPLETED_ACTION)) {
			String account = IMPreferenceUtils.getPrefString(IMMainService.this, IMConstants.IM_ACCOUNT, "");
			String password = IMPreferenceUtils.getPrefString(IMMainService.this, IMConstants.IM_PASSWORD, "");
			if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password))
				Login(account, password);
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		IMBroadcastReceiver.mListeners.remove(this);
		((AlarmManager) getSystemService(Context.ALARM_SERVICE)).cancel(mPAlarmIntent);// 取消重连闹钟
		unregisterReceiver(mAlarmReceiver);// 注销广播监听
		unregisterReceiver(imMsgReceiptReceiver);
		logout();
	}

	public void Login(final String account, final String password) {

		if (IMNetUtil.getNetworkState(this) == IMNetUtil.NETWORN_NONE) {
			connectionFailed(NETWORK_ERROR);
			return;
		}
		if (mConnectingThread != null) {
			IMLogger.d(TAG, "有一个连接正在运行!");
			return;
		}
		mConnectingThread = new Thread() {
			@Override
			public void run() {
				try {
					postConnecting();
					mXmppManager = IMXmppManager.getInstance(IMMainService.this);
					if (mXmppManager.login(account, password)) {
						// 登陆成功
						postConnectionScuessed();
						String loginJid = mXmppManager.getLoginJid();
						saveOpenfireIP(loginJid);
						saveLoginJid(loginJid);
					} else {
						// 登陆失败
						postConnectionFailed(LOGIN_FAILED);
					}
				} catch (Exception e) {
					String message = e.getLocalizedMessage();
					// 登陆失败
					if (e.getCause() != null)
						message += "\n" + e.getCause().getLocalizedMessage();
					postConnectionFailed(message);
					e.printStackTrace();
				} finally {
					if (mConnectingThread != null)
						synchronized (mConnectingThread) {
							mConnectingThread = null;
						}
				}
			}

		};
		mConnectingThread.start();

	}

	public boolean logout() {

		boolean isLogout = false;
		if (mConnectingThread != null) {
			synchronized (mConnectingThread) {
				try {
					mConnectingThread.interrupt();
					mConnectingThread.join(50);
				} catch (InterruptedException e) {
					IMLogger.d(TAG, "doDisconnect: failed catching connecting thread");
				} finally {
					mConnectingThread = null;
				}
			}
		}
		if (mXmppManager != null) {
			isLogout = mXmppManager.logout();
			mXmppManager.setVariableNull();
			mXmppManager = null;
			this.clearAllNotification();
		}
		connectionFailed(LOGOUT);// 手动退出
		return isLogout;
	}

	//	public void getOfflineMessage() {
	//
	//		if (mXmppManager != null) {
	//			//			mXmppManager.getOfflineMessage();
	//		}
	//	}

	/***
	 * 保存openfire ip
	 * @param user
	 */
	public void saveOpenfireIP(String loginJid) {
		if (!TextUtils.isEmpty(loginJid)) {
			String newOpenfireIP = loginJid.substring(loginJid.indexOf("@") + 1, loginJid.indexOf("/"));
			//			newOpenfireIP = "99.99.99.993";
			String oldOpenfireIP = IMPreferenceUtils.getPrefString(IMMainService.this, IMConstants.OPENFIRE_IP, "");
			if (!oldOpenfireIP.equals(newOpenfireIP)) {
				IMPreferenceUtils.setPrefString(IMMainService.this, IMConstants.OPENFIRE_IP, newOpenfireIP);
				//修改数据库的聊天记录
				Cursor mCursor = IMDBManager.getInstance(this).queryAllChat();
				IMLogger.d(TAG, "mCursor.getCount=" + mCursor.getCount());
				IMDBManager.getInstance(this).updateAllChatToJid(mCursor, newOpenfireIP);
			}
		}
	}

	/***
	 * 保存登陆jid
	 * @param loginJid
	 */
	public void saveLoginJid(String loginJid) {
		if (!TextUtils.isEmpty(loginJid)) {
			String newLoginJid = loginJid.substring(0, loginJid.indexOf("/"));
			IMPreferenceUtils.setPrefString(IMMainService.this, IMConstants.IM_MY_JID, newLoginJid);
		}
	}

	/**
	 * 发消息
	 * @param user
	 * @param message
	 */
	//	public void sendTextMessage(String user, String message) {
	//		if (mXmppManager != null) {
	//			mXmppManager.sendTextMessage(user, message);
	//		} else {
	//			//			IMXmppManager.sendOfflineMessage(getContentResolver(), user, message);
	//		}
	//	}

	public void sendTextMessage(ChatMessage mChatMessage) {
		if (mXmppManager != null)
			mXmppManager.sendTextMessage(mChatMessage);

	}

	/**
	 * 重发文本消息
	 * @param mChatMessage
	 */
	public void resendTextMessage(ChatMessage mChatMessage) {
		if (mXmppManager != null)
			//			mXmppManager = IMXmppManager.getInstance(IMMainService.this);
			mXmppManager.resendTextMessage(mChatMessage);
	}

	/****
	 * 多媒体文件上传成功，调用该方法发送多媒体消息
	 * @param toJID
	 * @param message
	 */
	public void sendMultimediaMessage(ChatMessage mChatMessage) {
		if (mXmppManager != null)
			//			mXmppManager = IMXmppManager.getInstance(IMMainService.this);
			//		mXmppManager.sendMultimediaMessage(mChatMessage.getToJid(), mChatMessage.getMessage(),
			//				mChatMessage.getPacketId());
			mXmppManager.sendMultimediaMessage(mChatMessage);

	}

	/***
	 * 重发多媒体消息
	 * @param toJID
	 * @param message
	 */
	public void resendMultimediaMessage(ChatMessage mChatMessage) {
		if (mXmppManager != null)
			//			mXmppManager = IMXmppManager.getInstance(IMMainService.this);
			//		mXmppManager.resendMultimediaMessage(mChatMessage.getToJid(), mChatMessage.getMessage(),
			//				mChatMessage.getPacketId());
			mXmppManager.resendMultimediaMessage(mChatMessage);

	}

	/***
	 * 多媒体文件上传前，调用该方法把msg到插入DB
	 * @param toJID
	 * @param message
	 * @return 返回ChatMessage
	 */
	//	public ChatMessage insertMsgToDB(String toJID, String message) {
	//		ChatMessage msg = new ChatMessage();
	//		if (mXmppManager == null)
	//			mXmppManager = IMXmppManager.getInstance(IMMainService.this);
	//		msg = mXmppManager.insertMsgToDB(toJID, message);
	//		return msg;
	//	}

	public ChatMessage insertMsgToDB(ChatMessage mChatMessage) {
		ChatMessage msg = null;
		if (mXmppManager != null) {
			msg = mXmppManager.insertMsgToDB(mChatMessage);
		}
		return msg;
	}

	/**
	 * 是否连接上服务器
	 * @return
	 */
	public boolean isAuthenticated() {
		if (mXmppManager != null) {
			return mXmppManager.isAuthenticated();
		}

		return false;
	}

	/***
	 * 清空指定jid的通知
	 * @param fromJid
	 */
	public void clearNotificationsByJid(String toJid) {
		IMNotificationManager.getInstance(IMMainService.this).clearNotificationByJid(toJid);
	}

	/**
	 * 联系人改变
	 */
	public void rosterChanged() {
		// gracefully handle^W ignore events after a disconnect
		//		if (mXmppManager == null)
		//			return;
		if (mXmppManager != null && !mXmppManager.isAuthenticated()) {
			IMLogger.d(TAG, "rosterChanged(): disconnected without warning");
			connectionFailed(DISCONNECTED_WITHOUT_WARNING);
		}
	}

	/***
	 * 收到新消息，发通知
	 * @param fromJid
	 * @param mChatMessage
	 */
	public void newMessage(final String fromJid, final ChatMessage mChatMessage) {
		mMainHandler.post(new Runnable() {
			public void run() {

				if (IMPreferenceUtils.getPrefBoolean(IMMainService.this, IMConstants.ISVOICE, true)) {
					MediaPlayer.create(IMMainService.this, R.raw.office).start();
				}
				if (IMPreferenceUtils.getPrefBoolean(IMMainService.this, IMConstants.ISSHOCK, true)) {
					if (null != vibrator) {
						vibrator.vibrate(300);
					}
				}
				if (mChatMessage.getDirection() == ChatConstants.OUTGOING)//如果是发送方，则通知栏提醒
					return;
				if (!isAppOnForeground()) {
					//					String fromUserName = IMXmppUtil.getNameForJID(mXmppManager.getmRoster(), fromJid);
					//					if (TextUtils.isEmpty(fromUserName)) {//没有好友关系
					//						fromUserName = mChatMessage.getFromAlias();
					//					}
					String fromUserName = mChatMessage.getFromAlias();
					IMLogger.d(TAG, "newMessage fromJid=" + fromJid);
					//					IMNotificationManager.getInstance(IMMainService.this).notifyClient(fromJid, fromUserName,
					//							mChatMessage.getMessage(), !mIsBoundTo.contains(fromJid));
					IMNotificationManager.getInstance(IMMainService.this).notifyClient(fromJid, fromUserName,
							mChatMessage.getMessage());
				}
			}

		});
	}

	public void setAvatar(String filePath) {

		//		if (mXmppManager == null)
		//			return;
		if (mXmppManager != null && mXmppManager.isAuthenticated()) {
			mXmppManager.setAvatar(filePath);
		}
	}

	public void getAvatar(String jid, String path) {
		//		if (mXmppManager == null)
		//			return;
		if (mXmppManager != null && mXmppManager.isAuthenticated()) {
			mXmppManager.getAvatar(jid, path);
		}
	}

	/**
	 * 启动消息回执闹钟，开始计时等待回执
	 * @param packetId 消息id
	 * @param second 时长 多少秒
	 */
	public void startMsgReceiptAlarm(String packetId, int second) {
		Intent intent = new Intent(MESSAGE_RECEIPT_ALARM);
		intent.putExtra("packetId", packetId);
		PendingIntent pi = PendingIntent.getBroadcast(this, packetId.hashCode(), intent, 0); //通过getBroadcast第二个参数区分闹钟

		AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + second * 1000, pi);//设置闹铃
	}

	/**
	 * 取消消息回执闹钟
	 * @param id
	 */
	public void cancelMsgReceiptAlarm(String id) {

		Intent intent = new Intent(MESSAGE_RECEIPT_ALARM);
		AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(this, id.hashCode(), intent, 0);
		am.cancel(pi);
	}

	/**
	 * 是否在后台运行
	 * @return
	 */
	public boolean isAppOnForeground() {
		List<RunningTaskInfo> taskInfos = mActivityManager.getRunningTasks(1);
		IMLogger.d(TAG, "getPackageName()=" + getPackageName());
		IMLogger.d(TAG,
				"taskInfos.get(0).topActivity.getPackageName()=" + taskInfos.get(0).topActivity.getPackageName());
		if (taskInfos.size() > 0 && TextUtils.equals(getPackageName(), taskInfos.get(0).topActivity.getPackageName())) {
			return true;
		}

		return false;
	}

	/**
	 * 非UI线程连接失败反馈
	 * @param reason
	 */
	public void postConnectionFailed(final String reason) {
		mMainHandler.post(new Runnable() {
			public void run() {
				connectionFailed(reason);
			}
		});
	}

	private void postConnectionScuessed() {
		mMainHandler.post(new Runnable() {
			public void run() {
				connectionScuessed();
			}

		});
	}

	// 连接中，通知界面线程做一些处理
	private void postConnecting() {
		// TODO Auto-generated method stub
		mMainHandler.post(new Runnable() {
			public void run() {
				connecting();
			}
		});
	}

	private void connectionFailed(String reason) {
		mConnectedState = DISCONNECTED;// 更新当前连接状态
		if (TextUtils.equals(reason, LOGOUT)) {// 如果是手动退出
			((AlarmManager) getSystemService(Context.ALARM_SERVICE)).cancel(mPAlarmIntent);
			return;
		}
		// 回调
		if (mConnectionStatusCallback != null) {
			mConnectionStatusCallback.connectionStatusChanged(mConnectedState, reason);
			if (mIsFirstBinderAction) // 如果是第一次登录,就算登录失败也不需要继续
				return;
		}

		// 无网络连接时,直接返回
		if (IMNetUtil.getNetworkState(this) == IMNetUtil.NETWORN_NONE) {
			((AlarmManager) getSystemService(Context.ALARM_SERVICE)).cancel(mPAlarmIntent);
			return;
		}
		String account = IMPreferenceUtils.getPrefString(IMMainService.this, IMConstants.IM_ACCOUNT, "");
		String password = IMPreferenceUtils.getPrefString(IMMainService.this, IMConstants.IM_PASSWORD, "");
		// 无保存的帐号密码时，也直接返回
		if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
			return;
		}
		// 如果不是手动退出并且需要重新连接，则开启重连闹钟
		if (IMPreferenceUtils.getPrefBoolean(this, IMConstants.IM_AUTO_START, true)) {
			((AlarmManager) getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis() + mReconnectTimeout * 1000, mPAlarmIntent);
			//			mReconnectTimeout = mReconnectTimeout * 2;
			//			if (mReconnectTimeout > RECONNECT_MAXIMUM)
			//				mReconnectTimeout = RECONNECT_MAXIMUM;
		} else {
			((AlarmManager) getSystemService(Context.ALARM_SERVICE)).cancel(mPAlarmIntent);
		}

	}

	private void connectionScuessed() {
		mConnectedState = CONNECTED;// 已经连接上
		mReconnectTimeout = RECONNECT_AFTER;// 重置重连的时间

		if (mConnectionStatusCallback != null)
			mConnectionStatusCallback.connectionStatusChanged(mConnectedState, "");
	}

	private void connecting() {
		// TODO Auto-generated method stub
		mConnectedState = CONNECTING;// 连接中
		if (mConnectionStatusCallback != null)
			mConnectionStatusCallback.connectionStatusChanged(mConnectedState, "");
	}

	@Override
	public void onNetChange() {
		// TODO Auto-generated method stub

	}

	/***
	 * 清空所有通知
	 */
	public void clearAllNotification() {
		IMNotificationManager.getInstance(IMMainService.this).clearAllNotification();
	}

	/**
	 * 连接状态变化回调
	 * @param cb
	 */
	public void registerConnectionStatusCallback(IMConnectionStatusCallback cb) {
		mConnectionStatusCallback = cb;
	}

	public void unRegisterConnectionStatusCallback() {
		mConnectionStatusCallback = null;
	}

	public class IMBinder extends Binder {
		public IMMainService getService() {
			return IMMainService.this;
		}
	}

	public void toLoginActivity() {
		Intent toLoginintent = new Intent();
		toLoginintent.setClassName(this, IMConstants.LOGIN_ACTIVITY_NAME);
		toLoginintent.putExtra(IMConstants.SHOW_FORECE_LOGOUT_DIALOG, true);
		toLoginintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(toLoginintent);
	}

}
