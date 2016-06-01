package com.wonhigh.im.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.wonhigh.im.R;
import com.wonhigh.im.constants.IMConstants;
import com.wonhigh.im.db.ChatConstants;
import com.wonhigh.im.util.IMLogger;

/**
 * 通知栏消息管理类
 * 
 * @author USER
 * @date 2014-11-25 上午10:50:19
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public class IMNotificationManager {
	public static final String TAG = IMNotificationManager.class.getSimpleName();

	//	private static final String APP_NAME = "百通";	

	private static IMNotificationManager instance;

	private Context context;

	private NotificationManager mNotificationManager;

	private Notification mNotification;

	private Intent mNotificationIntent;

	private IMDBManager mIMDBManager;

	public IMNotificationManager(Context context) {
		this.context = context;
		mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		mIMDBManager = IMDBManager.getInstance(context);
	}

	/**
	 * 获取一个单例对象
	 * @return
	 */
	public static IMNotificationManager getInstance(Context context) {

		if (instance == null) {

			instance = new IMNotificationManager(context);

		}
		return instance;
	}

	private Map<String, Integer> mNotificationId = new HashMap<String, Integer>();
	private int mLastNotificationId = 0;

	/**
	 * 通知client
	 * @param fromJid
	 * @param fromUserName
	 * @param message
	 * @param showNotification
	 */
	//	public void notifyClient(String fromJid, String fromUserName, String message, boolean showNotification) {
	public void notifyClient(String fromJid, String fromUserName, String message) {
		setNotification(fromJid, fromUserName, message);
		mNotificationManager.notify(getNotifyId(fromJid), mNotification);
	}

	/**
	 * 处理内容
	 * @param fromJid
	 * @param fromUserId
	 * @param message
	 */
	@SuppressLint("NewApi")
	private void setNotification(String fromJid, String fromUserName, String message) {

		//		String ticker = getNotificationMessage(fromJid, fromUserName, message);

		if (mNotificationIntent == null) {
			mNotificationIntent = new Intent();
			mNotificationIntent.setClassName(context, IMConstants.DEFAULT_NOTIFI_ACTIVITY);
			mNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		mNotificationIntent.putExtra(ChatConstants.TO_JID, fromJid);
		mNotificationIntent.putExtra(ChatConstants.TO_ALIAS, fromUserName);
//		PendingIntent pendingIntent = PendingIntent.getActivity(context, getNotifyId(fromJid), mNotificationIntent,
//				PendingIntent.FLAG_UPDATE_CURRENT);//第二个参数必须不一样，才能达到传递不同fromJid的效果
		PendingIntent pendingIntent = PendingIntent.getActivity(context, getNotifyId(fromJid), mNotificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);//第二个参数必须不一样，才能达到传递不同fromJid的效果
		Cursor mCursor = mIMDBManager.queryNewMsgNum(fromJid);
		int unreadCount = mCursor.getCount();
		IMLogger.d(TAG, "fromJid="+fromJid+"  unreadCount="+unreadCount);
		String contentText = "";
		if (unreadCount == 1||unreadCount==0) {
			contentText = message;
		} else if (unreadCount > 1 && unreadCount <= 99) {
			contentText = "[" + unreadCount + "条未读信息]" + message;
		} else {
			contentText = "[" + unreadCount + "++条未读信息]" + message;
		}
		IMLogger.d(TAG, "contentText="+contentText);
		mNotification = new Notification.Builder(context).setTicker(fromUserName + ":" + message)//设置在status bar上显示的提示文字
				.setContentTitle(fromUserName)//设置在下拉status bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
				.setContentText(contentText)//TextView中显示的详细内容
				.setSmallIcon(R.drawable.ic_launcher)//设置状态栏中的小图片，尺寸一般建议在24×24
				.setContentIntent(pendingIntent).setWhen(System.currentTimeMillis()).setAutoCancel(true).build();

		//		mNotification.flags |= Notification.FLAG_AUTO_CANCEL; //FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。

	}

	/***
	 * //获取Notification的id，若无则新建一个
	 * @param fromJid
	 * @return
	 */
	public int getNotifyId(String fromJid) {
		int notifyId = 0;
		if (mNotificationId.containsKey(fromJid)) {//获取Notification的id，若无则新建一个
			notifyId = mNotificationId.get(fromJid);
		} else {
			mLastNotificationId++;
			notifyId = mLastNotificationId;
			mNotificationId.put(fromJid, Integer.valueOf(notifyId));
		}
		return notifyId;
	}

	/***
	 * 清空指定jid的通知
	 * @param fromJid
	 */
	public void clearNotificationByJid(String toJid) {
		int notifyId = 0;
		if (mNotificationId.containsKey(toJid)) {
			notifyId = mNotificationId.get(toJid);
			mNotificationManager.cancel(notifyId);
		}
	}

	/***
	 * 清空所有通知
	 */
	public void clearAllNotification() {
		Iterator<String> mIterarot = mNotificationId.keySet().iterator();
		while (mIterarot.hasNext()) {
			String fromJid = mIterarot.next();
			mNotificationManager.cancel(mNotificationId.get(fromJid));
		}
	}

	//	public void resetNotificationCounter(String userJid) {
	//	}
	//
	//	/**
	//	 * 设置点击通知后跳转，需要制定跳转到某个activity以及传递的参数
	//	 */
	//	public void setNotificationIntent(ArrayList<Class> classes) {
	//
	//		mNotificationIntent = new Intent(context, classes.get(1));
	//		//				mNotificationIntent.putExtra("Name", fromJid);
	//		mNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	//
	//	}

}
