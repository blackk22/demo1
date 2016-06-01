package com.wonhigh.im.manager;

import java.io.File;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.ping.packet.Ping;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.wonhigh.im.R;
import com.wonhigh.im.constants.IMConstants;
import com.wonhigh.im.db.ChatConstants;
import com.wonhigh.im.db.RosterConstants;
import com.wonhigh.im.entity.ChatMessage;
import com.wonhigh.im.listener.IMMessageListener;
import com.wonhigh.im.listener.IMReConnectionListener;
import com.wonhigh.im.listener.IMRosterListener;
import com.wonhigh.im.service.IMMainService;
import com.wonhigh.im.util.IMFileUtil;
import com.wonhigh.im.util.IMLogger;
import com.wonhigh.im.util.IMPreferenceUtils;
import com.wonhigh.im.util.IMXmppUtil;

/**
 * TODO: 增加描述
 * 
 * @author USER
 * @date 2014-11-21 下午4:00:48
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public class IMXmppManager {

	private static final String TAG = IMXmppManager.class.getSimpleName();

	private static final int PACKET_TIMEOUT = 30000;

	private static IMXmppManager instance;

	private IMMainService imMainService;

	/**
	 * 连接
	 */
	private XMPPConnection connection;

	private ConnectionListener connectionListener;

	/**
	 * 花名册
	 */
	private Roster mRoster;

	private RosterListener mRosterListener;

	private PacketListener mMessageListener;

	private IMDBManager mIMDBManager;

	static {
		registerSmackProviders();
		initReconnectionManager();
	}

	public IMXmppManager(IMMainService imMainService) {
		this.imMainService = imMainService;
		mIMDBManager = IMDBManager.getInstance(imMainService);
		if (connection == null) {
			getConnection();
		}

	}

	/**
	 * 获取一个单例对象
	 * @return
	 */
	public static IMXmppManager getInstance(IMMainService imMainService) {

		if (instance == null) {
			instance = new IMXmppManager(imMainService);

		}
		return instance;
	}

	/**
	 * 关闭连接
	 */
	public void closeConnection() {

		if (connection != null) {

			if (connection.isConnected()) {
				connection.removeConnectionListener(connectionListener);
				connection.disconnect();
			}
			connection = null;
		}

	}

	/**
	 * 获取一个连接
	 * @return
	 * @throws XMPPException 
	 */
	public XMPPConnection getConnection() {

		if (connection == null || !connection.isConnected()) {

			closeConnection();

			connection = new XMPPConnection(getConnConfig());
			DeliveryReceiptManager.getInstanceFor(connection).enableAutoReceipts();//设置接到消息如果对方要求回执，则返回回执
			//			try {
			//				connection.connect();
			//			} catch (XMPPException e) {
			//				e.printStackTrace();
			//			}

			//			registerRosterListener();//注册监听

		}
		//		Presence presence = mRoster.getPresence("");

		return connection;

	}

	private ConnectionConfiguration getConnConfig() {
		ConnectionConfiguration connConfig = null;
		String openfireIP = IMPreferenceUtils.getPrefString(imMainService, IMConstants.IM_SERVER, IMConstants.IM_DEFAULT_SERVER);
		connConfig = new ConnectionConfiguration(openfireIP, IMConstants.IM_PORT);
		IMLogger.d(TAG, "getConnConfig openfireIP="+openfireIP);
		IMLogger.d(TAG, "getConnConfig IMConstants.IM_PORT="+IMConstants.IM_PORT);
		//		connConfig.setSASLAuthenticationEnabled(false);//不启用验证

		connConfig.setReconnectionAllowed(true);//启用自带的重连 --ydl

		connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
		//			connConfig.setTruststorePath("/system/etc/security/cacerts.bks");
		//			connConfig.setTruststorePassword("changeit");
		//			connConfig.setTruststoreType("bks");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			connConfig.setTruststoreType("AndroidCAStore");
			connConfig.setTruststorePassword(null);
			connConfig.setTruststorePath(null);
		} else {
			connConfig.setTruststoreType("BKS");
			String path = System.getProperty("javax.net.ssl.trustStore");
			if (path == null) {
				path = System.getProperty("java.home") + File.separator + "etc" + File.separator + "security"
						+ File.separator + "cacerts.bks";
			}
			connConfig.setTruststorePath(path);
		}

		connConfig.setSendPresence(true);

		return connConfig;
	}

	/**
	 * 注册所有监听监听
	 */
	private void registerAllListener() {

		if (isAuthenticated()) {
			registerMessageListener();
			registerMessageSendFailureListener();
			registerPongListener();

			// sendOfflineMessages();
			if (imMainService == null) {
				connection.disconnect();
				return;
			}
			//我们需要“ping”服务,让它知道我们实际上是相连的,即使没有名册条目会进来
			imMainService.rosterChanged();
		}
	}

	/**
	 * 是否连接
	 * @return
	 */
	public boolean isAuthenticated() {
		if (connection != null) {
			return (connection.isConnected() && connection.isAuthenticated());
		}
		return false;
	}

	public boolean login(String account, String password) throws Exception {

		if (connection.isConnected()) {//判断是否还连接着服务器，需要先断开
			try {
				connection.disconnect();
			} catch (Exception e) {
				IMLogger.d(TAG, "断开连接失败: " + e);
			}
		}

		try {
			registerRosterListener();// 监听联系人动态变化
			connection.connect();
			if (!connection.isConnected()) {
				throw new Exception("SMACK connect failed without exception!");
			}
			connection.addConnectionListener(new IMReConnectionListener(this, imMainService));

			if (!connection.isAuthenticated()) {
				connection.login(account, password, "im");
			}
			 setStatusFromConfig();// 更新在线状态
		} catch (XMPPException e) {
			throw new Exception(e.getLocalizedMessage(), e.getWrappedThrowable());
		} catch (Exception e) {
			// 实际上我们只关心IllegalState or NullPointer or XMPPEx.
			IMLogger.d(TAG, "login(): " + Log.getStackTraceString(e));
			throw new Exception(e.getLocalizedMessage(), e.getCause());
		}
		registerAllListener();// 注册监听其他的事件，比如新消息
		Presence presence = new Presence(Presence.Type.available);

		connection.sendPacket(presence);
		return connection.isAuthenticated();
	}
	/***
	 * 更新账号状态为在线
	 */
	public void setStatusFromConfig() {
		boolean messageCarbons = IMPreferenceUtils.getPrefBoolean(imMainService, IMConstants.MESSAGE_CARBONS, true);
		String statusMode = IMPreferenceUtils.getPrefString(imMainService, IMConstants.STATUS_MODE, IMConstants.AVAILABLE);
		String statusMessage = IMPreferenceUtils.getPrefString(imMainService, IMConstants.STATUS_MESSAGE,
				imMainService.getString(R.string.status_online));
		int priority = IMPreferenceUtils.getPrefInt(imMainService, IMConstants.PRIORITY, 0);
		if (messageCarbons)
			CarbonManager.getInstanceFor(connection).sendCarbonsEnabled(true);

		Presence presence = new Presence(Presence.Type.available);
		Mode mode = Mode.valueOf(statusMode);
		presence.setMode(mode);
		presence.setStatus(statusMessage);
		presence.setPriority(priority);
		connection.sendPacket(presence);
	}

	public boolean logout() {
		IMLogger.d(TAG, "logout");

		try {
			mRoster.removeRosterListener(mRosterListener);
			connection.removePacketListener(mMessageListener);
			//			connection.removePacketSendingListener(mSendFailureListener);
			connection.removePacketListener(mPongListener);
			((AlarmManager) imMainService.getSystemService(Context.ALARM_SERVICE)).cancel(mPingAlarmPendIntent);
			((AlarmManager) imMainService.getSystemService(Context.ALARM_SERVICE)).cancel(mPongTimeoutAlarmPendIntent);
			imMainService.unregisterReceiver(mPingAlarmReceiver);
			imMainService.unregisterReceiver(mPingTimeoutAlarmReceiver);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (connection.isConnected()) {
			new Thread() {
				public void run() {
					IMLogger.d(TAG, "shutDown thread started");
					connection.disconnect();
					IMLogger.d(TAG, "shutDown thread finished");
				}
			}.start();
		}
		setStatusOffline();

		return true;
	}

	private void setStatusOffline() {
		ContentValues values = new ContentValues();
		values.put(RosterConstants.STATUS_MODE, "离线");
		imMainService.getContentResolver().update(RosterConstants.ROSTER_URI, values, null, null);
	}

	//	public void sendTextMessage(String toJID, String message) {
	//		Log.i("aaaa", "toJID=" + toJID);
	//		Log.i("aaaa", "message=" + message);
	//		final Message newMessage = new Message(toJID, Message.Type.chat);
	//		newMessage.setBody(message);
	//		DeliveryReceiptManager.addDeliveryReceiptRequest(newMessage);//要求返回回执		
	//		ContentValues values = IMXmppUtil.getContentValuesForChatMessage(IMXmppUtil.getAccount(imMainService),
	//				ChatConstants.OUTGOING, toJID, message, isAuthenticated() ? ChatConstants.DS_SENT_OR_READ
	//						: ChatConstants.DS_NEW, System.currentTimeMillis(), newMessage.getPacketID());
	//		imMainService.getContentResolver().insert(ChatConstants.CHAT_URI, values);
	//		if (isAuthenticated()) {
	//			connection.sendPacket(newMessage);
	//			imMainService.startMsgReceiptAlarm(newMessage.getPacketID(), 30);
	//		}
	//	}
	/***
	 * 发文本消息
	 * @param mChatMessage
	 */
	public void sendTextMessage(ChatMessage mChatMessage) {
		IMLogger.d(TAG, "toJID=" + mChatMessage.getToJid());
		String msg = IMXmppUtil.assembleMsg(mChatMessage);
		IMLogger.d(TAG, "toSendMessage=" + msg);
		final Message newMessage = new Message(mChatMessage.getToJid(), Message.Type.chat);
		newMessage.setBody(msg);
		DeliveryReceiptManager.addDeliveryReceiptRequest(newMessage);//要求返回回执
		int msgStatus = ChatConstants.DS_MSG_FAIL;
		if (IMXmppUtil.isWeiXinMember(mChatMessage.getToJid())) {//微信会员默认收到了回执
			if (isAuthenticated())
				msgStatus = ChatConstants.DS_ACKED;

		} else {
			if (isAuthenticated())
				msgStatus = ChatConstants.DS_SENT_OR_READ;
		}
		IMLogger.d(TAG, "msgStatus=" + msgStatus);
		ContentValues values = IMXmppUtil.chatMsg2ContentValues(IMXmppUtil.getAccount(imMainService),
				ChatConstants.OUTGOING, mChatMessage.getToJid(), mChatMessage, msgStatus, System.currentTimeMillis(),
				newMessage.getPacketID());
		imMainService.getContentResolver().insert(ChatConstants.CHAT_URI, values);
		if (isAuthenticated()) {
			connection.sendPacket(newMessage);
			imMainService.startMsgReceiptAlarm(newMessage.getPacketID(), 30);
		}
	}

	/***
	 * 重复文本消息
	 * @param toJID
	 * @param message
	 * @param packetId
	 * @param isReSend
	 */
	public void resendTextMessage(ChatMessage mChatMessage) {
		if (isAuthenticated()) {
			final Message newMessage = new Message(mChatMessage.getToJid(), Message.Type.chat);
			newMessage.setPacketID(mChatMessage.getPacketId());
			String msg = IMXmppUtil.assembleMsg(mChatMessage);
			newMessage.setBody(msg);
			DeliveryReceiptManager.addDeliveryReceiptRequest(newMessage);//要求返回回执
			if (IMXmppUtil.isWeiXinMember(mChatMessage.getToJid())) {//微信会员默认收到了回执
				mIMDBManager.updateMsgStatusByMsgId(mChatMessage.getMsgId(), ChatConstants.DS_ACKED);
			}
			connection.sendPacket(newMessage);
			imMainService.startMsgReceiptAlarm(newMessage.getPacketID(), 30);
		} else {
			mIMDBManager.updateMsgStatusByMsgId(mChatMessage.getMsgId(), ChatConstants.DS_MSG_FAIL);
		}
	}

	/***
	 * 发多媒体消息
	 * @param mChatMessage
	 */
	public void sendMultimediaMessage(ChatMessage mChatMessage) {
		/***发送消息到服务端，不需要插入DB*/
		if (isAuthenticated()) {
			//			final Message newMessage = new Message(mChatMessage.getToJid(), Message.Type.chat);
			//			newMessage.setPacketID(mChatMessage.getPacketId());
			//			String msg = IMXmppUtil.assembleMsg(mChatMessage);
			//			newMessage.setBody(msg);
			//			DeliveryReceiptManager.addDeliveryReceiptRequest(newMessage);//要求返回回执	
			//			if (isWeiXinMember(mChatMessage.getToJid())) {//微信会员默认收到了回执
			//				mIMDBManager.updateMsgStatusByMsgId(mChatMessage.getMsgId(), ChatConstants.DS_ACKED);
			//			}
			//			connection.sendPacket(newMessage);
			//			imMainService.startMsgReceiptAlarm(newMessage.getPacketID(), 30);
			executeSendMultimediaMessage(mChatMessage);
		} else {
			mIMDBManager.updateMsgStatusByMsgId(mChatMessage.getMsgId(), ChatConstants.DS_MSG_FAIL);
		}
	}

	/***
	 * 重发多媒体消息
	 * @param mChatMessage
	 */
	public void resendMultimediaMessage(ChatMessage mChatMessage) {
		/***发送消息到服务端，不需要插入DB*/
		if (isAuthenticated()) {
			//			final Message newMessage = new Message(mChatMessage.getToJid(), Message.Type.chat);
			//			newMessage.setPacketID(mChatMessage.getPacketId());
			//			String msg = IMXmppUtil.assembleMsg(mChatMessage);
			//			newMessage.setBody(msg);
			//			DeliveryReceiptManager.addDeliveryReceiptRequest(newMessage);//要求返回回执	
			//			if (isWeiXinMember(mChatMessage.getToJid())) {//微信会员默认收到了回执
			//				mIMDBManager.updateMsgStatusByMsgId(mChatMessage.getMsgId(), ChatConstants.DS_ACKED);
			//			}
			//			connection.sendPacket(newMessage);
			//			imMainService.startMsgReceiptAlarm(newMessage.getPacketID(), 30);
			executeSendMultimediaMessage(mChatMessage);
		} else {
			mIMDBManager.updateMsgStatusByMsgId(mChatMessage.getMsgId(), ChatConstants.DS_MSG_FAIL);
		}
	}

	/***
	 * 执行发送多媒体消息
	 * @param mChatMessage
	 */
	public void executeSendMultimediaMessage(ChatMessage mChatMessage) {
		final Message newMessage = new Message(mChatMessage.getToJid(), Message.Type.chat);
		newMessage.setPacketID(mChatMessage.getPacketId());
		String msg = IMXmppUtil.assembleMsg(mChatMessage);
		newMessage.setBody(msg);
		DeliveryReceiptManager.addDeliveryReceiptRequest(newMessage);//要求返回回执	
		if (IMXmppUtil.isWeiXinMember(mChatMessage.getToJid())) {//微信会员默认收到了回执
			mIMDBManager.updateMsgStatusByMsgId(mChatMessage.getMsgId(), ChatConstants.DS_ACKED);
		}
		connection.sendPacket(newMessage);
		imMainService.startMsgReceiptAlarm(newMessage.getPacketID(), 30);
	}

	//	public ChatMessage insertMsgToDB(String toJID, String message) { 
	//		Message newMessage = new Message(toJID, Message.Type.chat);
	//		newMessage.setBody(message);
	//		ContentValues mContentValues = IMXmppUtil.getContentValuesForChatMessage(IMXmppUtil.getAccount(imMainService),
	//				ChatConstants.OUTGOING, toJID, message, ChatConstants.DS_SENT_OR_READ, System.currentTimeMillis(),
	//				newMessage.getPacketID());
	//		imMainService.getContentResolver().insert(ChatConstants.CHAT_URI, mContentValues);
	//		String msg = appendPacketId(message, newMessage.getPacketID());
	//		return new ChatMessage(msg);
	//	}

	public ChatMessage insertMsgToDB(ChatMessage mChatMessage) {
		Message newMessage = new Message(mChatMessage.getToJid(), Message.Type.chat);
		newMessage.setBody(IMXmppUtil.assembleMsg(mChatMessage));
		ContentValues mContentValues = IMXmppUtil.chatMsg2ContentValues(IMXmppUtil.getAccount(imMainService),
				ChatConstants.OUTGOING, mChatMessage.getToJid(), mChatMessage, ChatConstants.DS_NEW,
				System.currentTimeMillis(), newMessage.getPacketID());
		Uri contentUri = imMainService.getContentResolver().insert(ChatConstants.CHAT_URI, mContentValues);
		long msgId = ContentUris.parseId(contentUri);
		mChatMessage.setMsgId(Long.valueOf(msgId).intValue());
		mChatMessage.setPacketId(newMessage.getPacketID());
		//		String msg = appendPacketId(mChatMessage.getMessage(), newMessage.getPacketID());
		return mChatMessage;
	}

	//	public String appendPacketId(String message, String packetId) {
	//		StringBuffer mBuffer = new StringBuffer(message);
	//		/**替换掉末端的大括号，追加内容*/
	//		return mBuffer.toString().replace("}", ",") + "\"packetId\":" + "\"" + packetId + "\"}";
	//	}

	/**
	 * 接收离线消息，需要初始化一些属性
	 */
	public void getOfflineMessage() {

	}

	/**
	 * 设置头像
	 * @param filePath
	 */
	public void setAvatar(final String filePath) {
		new Thread() {
			@Override
			public void run() {
				//
				VCard vCard = new VCard();
				vCard.setAvatar(IMFileUtil.readFile(filePath));
				try {
					vCard.save(connection);
				} catch (XMPPException e) {
					e.printStackTrace();
				}
			}

		}.start();
	}

	/**
	 * 获取用户头像
	 * @param jid
	 */
	public void getAvatar(final String jid, final String path) {
		new Thread() {

			@Override
			public void run() {
				final VCard vcard = new VCard();
				try {
					vcard.load(connection, jid);
				} catch (XMPPException e1) {
					e1.printStackTrace();
				}
				byte[] avatar = vcard.getAvatar();
				if (vcard == null || avatar == null) {
					return;
				}

				IMFileUtil.writeFile(avatar, path);
			}
		}.start();
	}

	public Roster getmRoster() {
		return mRoster;
	}

	/**
	 * 注册联系人监听监听
	 */
	private void registerRosterListener() {
		mRoster = connection.getRoster();
		if (imMainService == null) {
			IMLogger.d(TAG, "imMainService==null");
		}
		mRosterListener = new IMRosterListener(imMainService, mRoster);
		mRoster.addRosterListener(mRosterListener);
	}

	/**
	 * 注册消息监听
	 */
	private void registerMessageListener() {
		if (mMessageListener != null) {
			connection.removePacketListener(mMessageListener);
		}
		PacketTypeFilter filter = new PacketTypeFilter(Message.class);

		mMessageListener = new IMMessageListener(imMainService);

		connection.addPacketListener(mMessageListener, filter);

		//		connection.addPacketListener(new PacketListener() {
		//
		//			@Override
		//			public void processPacket(Packet arg0) {
		//				// TODO Auto-generated method stub
		//				if(arg0 instanceof Presence){
		//					Presence p=(Presence) arg0;
		//					Log.i("aaaa", "p.getType()="+p.getType());
		//					if(p.getType()==Presence.Type.unavailable){
		//						Log.i("aaaa", "下线了。。。。。。。。。。。。。。。。");
		//						Log.i("aaaa", "p.getFrom"+p.getFrom());
		//						Log.i("aaaa", "p.getTo"+p.getTo());
		//					}
		//					if(p.getType()==Presence.Type.available){
		//						
		//						Log.i("aaaa", "上线了。。。。。。。。。。。。。。。。");
		//						Log.i("aaaa", "p.getTo"+p.getTo());
		//					}
		//					
		//				}
		//
		//			}
		//		}, new PacketTypeFilter(Presence.class));

	}

	/**
	 * 注册消息发送失败监听
	 */
	private void registerMessageSendFailureListener() {

	}

	/*****************************asmack自动重连不完善，手动实现重连***************************************************/

	private PacketListener mPongListener;

	// ping-pong服务器
	private String mPingID;

	private long mPingTimestamp;

	private PendingIntent mPongTimeoutAlarmPendIntent;//处理ping超时闹钟

	private PendingIntent mPingAlarmPendIntent;//处理ping闹钟

	private static final String PING_ALARM = "com.wonhigh.im.PING_ALARM";

	private static final String PONG_TIMEOUT_ALARM = "com.wonhigh.im.PONG_TIMEOUT_ALARM";

	private Intent mPingAlarmIntent = new Intent(PING_ALARM);

	private Intent mPingTimeoutAlarmIntent = new Intent(PONG_TIMEOUT_ALARM);

	private PingTimeoutAlarmReceiver mPingTimeoutAlarmReceiver = new PingTimeoutAlarmReceiver();

	private PingAlarmReceiver mPingAlarmReceiver = new PingAlarmReceiver();

	/**
	 * 
	 */
	private void registerPongListener() {

		mPingID = null;

		if (mPongListener != null)
			connection.removePacketListener(mPongListener);

		mPongListener = new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				if (packet == null)
					return;

				if (packet.getPacketID().equals(mPingID)) {
					mPingID = null;
					//接收到返回的pingid则倒计时广播取消
					((AlarmManager) imMainService.getSystemService(Context.ALARM_SERVICE))
							.cancel(mPongTimeoutAlarmPendIntent);
				}
			}

		};

		connection.addPacketListener(mPongListener, new PacketTypeFilter(IQ.class));

		mPingAlarmPendIntent = PendingIntent.getBroadcast(imMainService.getApplicationContext(), 0, mPingAlarmIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mPongTimeoutAlarmPendIntent = PendingIntent.getBroadcast(imMainService.getApplicationContext(), 0,
				mPingTimeoutAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		imMainService.registerReceiver(mPingAlarmReceiver, new IntentFilter(PING_ALARM));
		imMainService.registerReceiver(mPingTimeoutAlarmReceiver, new IntentFilter(PONG_TIMEOUT_ALARM));
		//90s后发送广播，然后每隔90s重发
		((AlarmManager) imMainService.getSystemService(Context.ALARM_SERVICE)).setInexactRepeating(
				AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 50 * 1000, 50 * 1000, mPingAlarmPendIntent);

	}

	/**
	 * 接收到此广播则认为超时断开连接
	 */
	private class PingTimeoutAlarmReceiver extends BroadcastReceiver {
		public void onReceive(Context ctx, Intent i) {
			IMLogger.d(TAG, "Ping timeout receiver");
			imMainService.postConnectionFailed(IMMainService.PONG_TIMEOUT);
			//			logout();// 超时就断开连接
		}
	}

	/**
	 * 接收到此广播开始ping
	 */
	private class PingAlarmReceiver extends BroadcastReceiver {
		public void onReceive(Context ctx, Intent i) {
			if (connection.isAuthenticated()) {
				sendServerPing();
			} else
				IMLogger.d(TAG, "Ping: alarm received, but not connected to server.");
		}
	}

	/**
	 * ping服务端
	 */
	public void sendServerPing() {
		if (mPingID != null) {
			return; // 一个ping还在进行
		}
		Ping ping = new Ping();
		ping.setType(Type.GET);
		ping.setTo(IMPreferenceUtils.getPrefString(imMainService, IMConstants.IM_SERVER, IMConstants.IM_DEFAULT_SERVER));
		mPingID = ping.getPacketID();
		mPingTimestamp = System.currentTimeMillis();
		IMLogger.d(TAG, "Ping: sending ping " + mPingID);

		connection.sendPacket(ping);

		// 超时广播30s+3s
		((AlarmManager) imMainService.getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + PACKET_TIMEOUT + 3000, mPongTimeoutAlarmPendIntent);
	}

	//	PacketFilter rosterPF = new PacketTypeFilter(RosterPacket.class);
	//    PacketFilter IQPF = new PacketTypeFilter(IQ.class);
	//    PacketFilter MSGPF = new PacketTypeFilter(Message.class);
	//    PacketFilter PresencePF = new PacketTypeFilter(Presence.class);
	//    PacketFilter AMPF = new PacketTypeFilter(AuthMechanism.class);
	//    PacketFilter REPF = new PacketTypeFilter(Response.class);

	static void registerSmackProviders() {

		ProviderManager pm = ProviderManager.getInstance();

		//回执 接受者无法找到<request xmlns='urn:xmpp:receipts'/>节点，因为DeliveryReceipt.Provider()生成的是received节点。
		pm.addExtensionProvider(DeliveryReceipt.ELEMENT, DeliveryReceipt.NAMESPACE, new DeliveryReceipt.Provider());
		pm.addExtensionProvider(DeliveryReceiptRequest.ELEMENT, new DeliveryReceiptRequest().getNamespace(),
				new DeliveryReceiptRequest.Provider());

		//vCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());
	}

	/***
	 * 实现掉线后系统重联  --ydl
	 */
	static void initReconnectionManager() {

		try {
			Class.forName("org.jivesoftware.smack.ReconnectionManager");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setVariableNull() {
		this.imMainService = null;
		this.instance = null;//静态变量置空，退出再登陆，imMainService重新赋值--ydl
	}

	/***
	 * 获取登陆者jid
	 * @return
	 */
	public String getLoginJid() {
		if (connection != null)
			return connection.getUser();
		return "";
	}
	
	


}
