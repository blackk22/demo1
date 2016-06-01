package com.wonhigh.im.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wonhigh.im.db.ChatConstants;
import com.wonhigh.im.manager.IMDBManager;
import com.wonhigh.im.util.IMLogger;

/**
 * 接收消息回执超时广播，接收到此广播意味着该消息在指定时间内没接收到回执
 * 
 * @author USER
 * @date 2014-12-24 下午3:03:52
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public class IMMsgReceiptReceiver extends BroadcastReceiver {

	private static final String TAG = IMMsgReceiptReceiver.class.getSimpleName();

	private IMMainService imMainService;

	public IMMsgReceiptReceiver(IMMainService imMainService) {
		super();
		this.imMainService = imMainService;
		
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		String packetId = intent.getStringExtra("packetId");
		
		IMLogger.d(TAG, "超时 消息未取得回执 packetId ==" + packetId);

		imMainService.cancelMsgReceiptAlarm(packetId);
//		IMDBManager.getInstance(imMainService).updateMsgStatusByPacketId(packetId, ChatConstants.DS_NEW);
		IMDBManager.getInstance(imMainService).updateMsgStatusByPacketId(packetId, ChatConstants.DS_MSG_FAIL);
	}

}
