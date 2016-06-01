package com.wonhigh.im.service;

import java.util.ArrayList;

import com.wonhigh.im.constants.IMConstants;
import com.wonhigh.im.util.IMLogger;
import com.wonhigh.im.util.IMPreferenceUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

/**
 * im的广播接收器
 * 
 * @author USER
 * @date 2014-11-25 下午4:52:10
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public class IMBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "IMNetBroadcastReceiver";

	public static final String BOOT_COMPLETED_ACTION = "com.wonhigh.im.action.BOOT_COMPLETED";

	public static ArrayList<IMNetChangeEventHandler> mListeners = new ArrayList<IMNetChangeEventHandler>();

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		IMLogger.d(TAG, "action = " + action);
		if (TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)) {//网络变化
			if (mListeners.size() > 0) // 通知接口完成加载
				for (IMNetChangeEventHandler handler : mListeners) {
					handler.onNetChange();
				}
		} else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {//系统关闭
			IMLogger.d(TAG, "System shutdown, stopping service.");
			Intent xmppServiceIntent = new Intent(context, IMMainService.class);
			context.stopService(xmppServiceIntent);
		} else {
			if (!TextUtils.isEmpty(IMPreferenceUtils.getPrefString(context, IMConstants.IM_PASSWORD, ""))
					&& IMPreferenceUtils.getPrefBoolean(context, IMConstants.IM_AUTO_START, true)) {
				Intent i = new Intent(context, IMMainService.class);
				i.setAction(BOOT_COMPLETED_ACTION);
				context.startService(i);
			}
		}
	}

}
