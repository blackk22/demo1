package com.wonhigh.im.listener;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.packet.Presence;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.wonhigh.im.constants.IMConstants;
import com.wonhigh.im.manager.IMManager;
import com.wonhigh.im.manager.IMXmppManager;
import com.wonhigh.im.service.IMMainService;
import com.wonhigh.im.util.IMLogger;
import com.wonhigh.im.util.IMPreferenceUtils;

/**
 * xmpp连接监听
 * 
 * @author USER
 * @date 2014-11-21 下午2:39:00
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public class IMReConnectionListener implements ConnectionListener {

	private static final String TAG = IMReConnectionListener.class.getSimpleName();

	private IMXmppManager xmppManager;

	private IMMainService mIMMainService;

	public IMReConnectionListener(IMXmppManager xmppManager, IMMainService mIMMainService) {
		super();
		this.xmppManager = xmppManager;
		this.mIMMainService = mIMMainService;
	}

	@Override
	public void connectionClosed() {//正常关闭调用
		IMLogger.d(TAG, "connectionClosed");
		//		if(!xmppManager.getConnection().isAuthenticated()){
		//			Log.d(TAG, "connectionClosed and login");
		//			try {
		//				xmppManager.login("du123", "123456");
		//			} catch (Exception e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
		//		}
	}

	@Override
	public void connectionClosedOnError(Exception arg0) {
		/***stream:error (conflict)同一JID多次登陆，虽然openfire会分配不同的资源连接*/
		/***Read error: ssl=0x40059f60: I/O error during system call, Connection timed out，网络连接断开后*/
		IMLogger.d(TAG, "connectionClosedOnError arg0.getLocalizedMessage()=" + arg0.getLocalizedMessage());
		String msg = arg0.getLocalizedMessage();
		if (!TextUtils.isEmpty(msg) && msg.contains("stream:error (conflict)")) {
			forceLogout();
		}
	}

	//调10次，就调一次reconnectionFailed
	@Override
	public void reconnectingIn(int arg0) {
		IMLogger.d(TAG, "reconnectingIn");
	}

	@Override
	public void reconnectionFailed(Exception arg0) {
		IMLogger.d(TAG, "reconnectionFailed");
	}

	@Override
	public void reconnectionSuccessful() {
		IMLogger.d(TAG, "reconnectionSuccessful");
		/**更改状态为在线*/
		Presence presence = new Presence(Presence.Type.available);

		xmppManager.getConnection().sendPacket(presence);
	}
	/***
	 * 强制退出
	 */
	public void forceLogout() {
		IMLogger.d(TAG, "connectionClosedOnError 抢登陆了");
		mIMMainService.clearAllNotification();
		IMManager.getInstance().saveIMPassword(mIMMainService, "");
		IMPreferenceUtils.setPrefString(mIMMainService, IMConstants.SESSION, "");
		Intent broadcastIntent = new Intent(IMConstants.FINISH_ACTIVITY_BROADCAST);
		mIMMainService.sendBroadcast(broadcastIntent);
		mIMMainService.toLoginActivity();
		mIMMainService.logout();
		mIMMainService.stopSelf();// 停止服务
	}

}
