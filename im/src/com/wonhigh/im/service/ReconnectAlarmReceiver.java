package com.wonhigh.im.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.wonhigh.im.constants.IMConstants;
import com.wonhigh.im.util.IMLogger;
import com.wonhigh.im.util.IMPreferenceUtils;

/**
 * 重连闹钟广播
 * 
 * @author USER
 * @date 2014-11-25 下午5:40:34
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public class ReconnectAlarmReceiver extends BroadcastReceiver {

	private static final String TAG = "ReconnectAlarmReceiver";

	private IMMainService imMainService;

	public ReconnectAlarmReceiver(IMMainService imMainService) {
		super();
		// TODO Auto-generated constructor stub
		this.imMainService = imMainService;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		IMLogger.d(TAG, "接到闹钟广播");
		if (!IMPreferenceUtils.getPrefBoolean(imMainService, IMConstants.IM_AUTO_START, true)) {
			return;
		}
		if (imMainService.mConnectedState != IMMainService.DISCONNECTED) {
			IMLogger.d(TAG, "尝试重新连接中止：我们再次连接！");
			return;
		}
		String account = IMPreferenceUtils.getPrefString(imMainService, IMConstants.IM_ACCOUNT, "");
		String password = IMPreferenceUtils.getPrefString(imMainService, IMConstants.IM_PASSWORD, "");
		if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
			IMLogger.d(TAG, "account = null || password = null");
			return;
		}
		imMainService.Login(account, password);
	}

}
