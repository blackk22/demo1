package com.wonhigh.im.service;

/**
 * xmpp连接状态变化回调
 * 
 * @author USER
 * @date 2014-11-25 下午4:31:36
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public interface IMConnectionStatusCallback {

	public void connectionStatusChanged(int connectedState, String reason);
}
