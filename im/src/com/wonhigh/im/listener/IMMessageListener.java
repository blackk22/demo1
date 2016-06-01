package com.wonhigh.im.listener;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;

import com.wonhigh.base.util.Logger;
import com.wonhigh.im.constants.IMConstants;
import com.wonhigh.im.db.ChatConstants;
import com.wonhigh.im.entity.ChatMessage;
import com.wonhigh.im.manager.IMDBManager;
import com.wonhigh.im.manager.IMXmppManager;
import com.wonhigh.im.service.IMMainService;
import com.wonhigh.im.util.IMLogger;
import com.wonhigh.im.util.IMPreferenceUtils;
import com.wonhigh.im.util.IMXmppUtil;
import com.wonhigh.base.util.LogoImageLoader;

/***
 * 
 * TODO: 消息监听
 * 
 * @author yang.dl
 * @date 2015-3-18 下午2:23:46
 * @version 1.0.0 
 * @copyright wonhigh.cn
 */
public class IMMessageListener implements PacketListener {

	private static final String TAG = IMMessageListener.class.getSimpleName();

	private IMMainService imMainService;
	private IMDBManager mIMDBManager;

	public IMMessageListener(IMMainService imMainService) {
		this.imMainService = imMainService;
		mIMDBManager = IMDBManager.getInstance(imMainService);
	}

	@Override
	public void processPacket(Packet packet) {

		if (packet instanceof Message) {
			Message mMessage = (Message) packet;
			String messageBody = mMessage.getBody();

			if (messageBody == null) {//处理回执
				handleReceipt(mMessage);
				return;
			}

			if (isRepeatMsg(mMessage))//处理重复消息
				return;

			String fromJid = IMXmppUtil.getJid(mMessage.getFrom());//去掉后缀im，正常情况是消息来源者jid，若是百通发给微信同步过来的，则是微信的jid
			String toJid = mMessage.getTo();//消息接收者jid,返回值不包含后缀im
			IMLogger.d(TAG, "新消息fromJID=" + fromJid);
			IMLogger.d(TAG, "新消息 toJID=" + toJid);
			IMLogger.d(TAG, "新消息msg.toXML()=：" + mMessage.toXML());
			IMLogger.d(TAG, "新消息 原始chatMessage：" + messageBody);
			IMLogger.d(TAG, "新消息 原始chatMessage：" + messageBody.toString());

			if (mMessage.getType() == Message.Type.error) {
				messageBody = "<Error> " + messageBody;
			}
			
			//处理微信 门店logo 昵称修改消息
			String messageType = getMessageType(messageBody);
			if (messageType.equals(ChatConstants.CHATTER_INFO_MESSAGE)) {
				handleChatInfoChangeMessage(fromJid,mMessage, messageBody);
				return;
			}

			ChatMessage mChatMessage = getChatMessage(mMessage, messageBody, fromJid);

			if (!TextUtils.isEmpty(mChatMessage.getToAccount())
					&& !mChatMessage.getAccount().equalsIgnoreCase(mChatMessage.getToAccount())) {//true,表示这条信息不是发给自己的，是百通的同步消息
				mChatMessage = setChatMessageParams(mChatMessage);
			}

			ContentValues values = IMXmppUtil.chatMsg2ContentValues(mChatMessage);
			mIMDBManager.insertMsgToDB(values);//插入数据库
			imMainService.newMessage(fromJid, mChatMessage);

		}

	}
	
	/**
	 * 区分消息的消息类型
	 *
	 * @param message
	 * @return
	 */
	public String getMessageType(String message) {
		JSONObject msgObject = null;
		String type = "";
		try {
			msgObject = new JSONObject(message);
			type = msgObject.optString(ChatConstants.TYPE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return type;
	}
	
	/**
	 * 处理关注者或者门店logo 名称（昵称、门店名）发送变化
	 *
	 * @param message
	 * @param messageBody
	 */
	public void handleChatInfoChangeMessage(String fromJid,Message message, String messageBody) {
		if (TextUtils.isEmpty(fromJid))
			return;
		Logger.d(TAG, "新消息 chatterInfo发生变化的jid=" + fromJid);
		Logger.d(TAG, "新消息msg.toXML()=：" + message.toXML());
		Logger.d(TAG, "新消息 原始chatMessage：" + messageBody);
		LogoImageLoader.removeFromCache(imMainService, fromJid, LogoImageLoader.ORIGINAL_LOGO);//删除缩略图缓存
		LogoImageLoader.removeFromCache(imMainService, fromJid, LogoImageLoader.SMALL_LOGO);//删除缩略图缓存
		String myJid = this.getLoginJid();
		if (!fromJid.equals(myJid)) {//自己头像发送变化后可以不用管
			Intent chatterInfoChangIntent = new Intent(IMConstants.CHATTER_INFO_CHANGE_BROADCAST);
			String newName = getMessageNewName(messageBody);
			if (!TextUtils.isEmpty(newName)) {
                mIMDBManager.updateAllChatDBFromAliasByJid(fromJid,newName);
                mIMDBManager.updateAllChatDBToAliasByJid(fromJid, newName);
				chatterInfoChangIntent.putExtra(ChatConstants.NEW_NAME, newName);
			}
			imMainService.sendBroadcast(chatterInfoChangIntent);
		}

	}
	
    /**
     * 获取logo消息里面newName
     *
     * @param message
     * @return
     */
    public String getMessageNewName(String message) {
        JSONObject msgObject = null;
        String newName = "";
        try {
            msgObject = new JSONObject(message);
            newName = msgObject.optString(ChatConstants.NEW_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newName;
    }
	
	/**根据jid获取品牌**/
	public void getStoreBrandByJid(String fromJid){
		
	}

	/***
	 * 处理回执
	 */
	public void handleReceipt(Message msg) {
		DeliveryReceipt dr = (DeliveryReceipt) msg.getExtension(DeliveryReceipt.ELEMENT, DeliveryReceipt.NAMESPACE);
		if (dr != null) {
			String packetId = dr.getId();
			IMLogger.d(TAG, "消息回执：packetId ==" + packetId);
			imMainService.cancelMsgReceiptAlarm(packetId);
			IMDBManager.getInstance(imMainService).updateMsgStatusByPacketId(packetId, ChatConstants.DS_ACKED);//收到回执，将消息更新为对方已得到
		}
	}

	/***
	 * 避免接收重复消息
	 * @param msg
	 */
	public boolean isRepeatMsg(Message msg) {
		String packetId = msg.getPacketID();
		IMLogger.d(TAG, "新消息或消息回执：packetId ==" + packetId);
		Cursor chatCursor = mIMDBManager.queryChatByPacketId(packetId);
		if (chatCursor != null && chatCursor.getCount() > 0) {
			IMLogger.d(TAG, "----收到重复消息----");
			return true;
		}
		return false;
	}

	/***
	 * 返回消息时间
	 * @return
	 */
	public long getTime(Message msg) {
		try {
			DelayInformation timestamp = (DelayInformation) msg.getExtension("x", "jabber:x:delay");
			if (timestamp != null) {
				return timestamp.getStamp().getTime();
			} else {
				return System.currentTimeMillis();
			}
		} catch (Exception e) {
			return System.currentTimeMillis();
		}
	}

	/**
	 *  解析messageBody
	 * @param mMessage
	 * @param messageBody
	 * @param fromJid
	 * @return
	 */
	public ChatMessage getChatMessage(Message mMessage, String messageBody, String fromJid) {
		ChatMessage mChatMessage = new ChatMessage(messageBody);//解析
		if (TextUtils.isEmpty(mChatMessage.getBrandName())) {
			String brandName = mIMDBManager.queryStoreBrandByJid(fromJid);
			if (!TextUtils.isEmpty(brandName)) {
				//				mChatMessage.setBrandName(brandName.toUpperCase());
				mChatMessage.setBrandName(brandName);
			} else {
				mChatMessage.setBrandName("");
			}
		}
		mChatMessage.setPacketId(mMessage.getPacketID());
		mChatMessage.setDate(getTime(mMessage));
		String myAccount = IMXmppUtil.getAccount(imMainService);//登陆账号
		mChatMessage.setAccount(myAccount);
		mChatMessage.setToJid(fromJid);//对方的jid,不区分消息方向
		mChatMessage.setMsgStatus(ChatConstants.DS_NEW);
		mChatMessage.setDirection(ChatConstants.INCOMING);
		return mChatMessage;
	}

	/***  
	 * 把消息当成是自己发送的
	 * @param mChatMessage
	 * @return
	 */
	public ChatMessage setChatMessageParams(ChatMessage mChatMessage) {
		mChatMessage.setMsgStatus(ChatConstants.DS_ACKED);//默认收到了回执
		mChatMessage.setDirection(ChatConstants.OUTGOING);//默认当成是自己发的
		String fromAlias = IMPreferenceUtils.getPrefString(imMainService, IMConstants.MY_ALIAS, "");
		if (TextUtils.isEmpty(fromAlias)) {
			fromAlias = IMXmppUtil.getAccount(imMainService);//如果没有昵称，则把登陆账号当昵称
		}
		mChatMessage.setFromAlias(fromAlias);//当成是自己发的
		if (mChatMessage.getType().equals(ChatConstants.TEXT)) {
			mChatMessage.setMessage(mChatMessage.getMessage() + "  " + ChatConstants.SHOP_REPLY_MSG);
		} else {
			mChatMessage.setMessage(ChatConstants.SHOP_REPLY_MSG);
		}
		IMLogger.d(TAG, "----消息不是给自己的---");
		IMLogger.d(TAG, "修正后 chatMessage=" + mChatMessage.toMsgString());
		return mChatMessage;
	}
	
	public String getLoginJid() {
		String loginJid = IMXmppManager.getInstance(imMainService).getLoginJid();
		if (!TextUtils.isEmpty(loginJid)) {
			loginJid = loginJid.substring(0, loginJid.indexOf("/"));
			return loginJid;
		}
		return "";
	}

}
