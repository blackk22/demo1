package com.wonhigh.im.manager;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import com.wonhigh.im.constants.IMConstants;
import com.wonhigh.im.service.IMMainService;
import com.wonhigh.im.util.IMPreferenceUtils;
import com.wonhigh.im.util.IMXmppUtil;

/**
 * TODO: 增加描述
 * 
 * @author USER
 * @date 2014-12-5 下午2:03:53
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public class IMManager {

	public static final int SERVICE_LOGIN_FLAG = 1;
	public static final int SERVICE_MAIN_FLAG = 2;
	public static final int SERVICE_CHAT_FLAG = 3;
	private static IMManager instance;

	public IMManager() {
	}

	/**
	 * 获取一个单例对象
	 * @return
	 */
	public static IMManager getInstance() {

		if (instance == null) {

			instance = new IMManager();

		}
		return instance;
	}

	public void IMInit(Context context) {
		//		IMNotificationManager.getInstance(context).setNotificationIntent();
	}

	public void IMOnCreate(Context context) {

		context.startService(new Intent(context, IMMainService.class));
	}

	public void IMOnResume(Context context, ServiceConnection connection, String jid, int flag) {

		IMBindXMPPService(context, connection, jid, flag);

	}

	public void IMOnPause() {

	}

	public void IMOnDestroy(Context context, ServiceConnection connection) {
		IMUnbindXMPPService(context, connection);
	}

	/**
	 * 获取im登入账号
	 * @param context
	 * @return
	 */
	public String getIMAccount(Context context) {
		return IMPreferenceUtils.getPrefString(context, IMConstants.IM_ACCOUNT, "");
	}

	/**
	 * 获取im登入密码
	 * @param context
	 * @return
	 */
	public String getIMPassword(Context context) {
		return IMPreferenceUtils.getPrefString(context, IMConstants.IM_PASSWORD, "");
	}

	/**
	 * 保存im登入账号
	 * @param context
	 * @return
	 */
	public void saveIMAccount(Context context, String account) {

		IMPreferenceUtils.setPrefString(context, IMConstants.IM_ACCOUNT, account);
	}

	/**
	 * 保存im登入密码
	 * @param context
	 * @return
	 */
	public void saveIMPassword(Context context, String password) {

		IMPreferenceUtils.setPrefString(context, IMConstants.IM_PASSWORD, password);
	}

	/**
	 * 绑定service
	 * @param context
	 * @param connection
	 * @param jid 
	 * @param flag
	 */
	public void IMBindXMPPService(Context context, ServiceConnection connection, String jid, int flag) {

		Intent intent = new Intent(context, IMMainService.class);
		switch (flag) {
		case SERVICE_LOGIN_FLAG:
			intent.setAction(IMMainService.RECONNECT_LOGIN_ACTION);
			break;
		case SERVICE_MAIN_FLAG:
			break;
		case SERVICE_CHAT_FLAG:
			Uri chatURI = Uri.parse(jid);
			intent.setData(chatURI);

		default:
			break;
		}
		context.bindService(intent, connection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);

	}

	private void IMUnbindXMPPService(Context context, ServiceConnection connection) {
		context.unbindService(connection);
	}
	
	/***
	 * 判断jid是否属于微信用户
	 * @param toJid
	 * @return
	 */
	public static boolean isWeiXinMember(String toJid) {
		return IMXmppUtil.isWeiXinMember(toJid);
	}
	
	/***
	 * 从jid获取账号名（即登陆名）
	 * @return
	 */
	public static String getAccountFromJid(String jid) {
		return IMXmppUtil.getAccountFromJid(jid);
	}

}
