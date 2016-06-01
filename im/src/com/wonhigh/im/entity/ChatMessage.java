package com.wonhigh.im.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.wonhigh.im.db.ChatConstants;
import com.wonhigh.im.util.IMLogger;

/***
 * 
 * TODO: 聊天消息实体
 * 
 * @author yang.dl
 * @date 2015-2-27 上午10:49:42
 * @version 1.0.0 
 * @copyright wonhigh.cn
 */
public class ChatMessage {
	public static final String TAG = ChatMessage.class.getSimpleName();
	/**id,在表中的id值*/
	private int msgId;
	/**当前登录账户*/
	private String account;
	/**消息类型*/
	private String type;
	//	/**消息内容*/
	//	private String content;
	/**消息内容*/
	private String message;
	/**文件下载地址*/
	public String downloadUrl;
	/**文件本地保存地址*/
	private String locationUrl;
	/**消息接收方昵称*/
	private String toAlias;
	/**消息发送方昵称*/
	private String fromAlias;
	/**XMPP生成的id值*/
	private String packetId;
	/**备用属性 */
	private String backUp;
	/** 对方jid，跟消息方向无关*/
	private String toJid;
	/**消息时间*/
	private long date;
	/**发送阅读状态*/
	private int msgStatus;
	/**消息去向*/
	private int direction;
	/**消息接收方的账号名*/
	private String toAccount;
	/**品牌**/

	private String brandName;
	/**删除状态**/
	private int deleteStatus;
	/**是否选择**/
	private boolean isSelected;

	private int chatterType;//收发信息者角色

	public static final int NONE_TYPE = -1;//收发信息者，无角色
	public static final int SHOP = 0;//收发信息者，门店角色
	public static final int SUPPORTER = 1;//收发信息者，客服角色

	private String storeName;//门店名称 ,便于无好友关系，需显示门店名称
	private String storeJid;//门店jid，便于无好友关系，需查询门店信息
	private String storeNo;//门店机构编码，便于无好友关系，需查询门店信息

	public ChatMessage() {
	};

	public ChatMessage(String message) {
		IMLogger.d(TAG, "ChatMessage：" + message);

		JSONObject msgObject;
		try {
			msgObject = new JSONObject(message);
			String type = msgObject.optString(ChatConstants.TYPE);
			String content = msgObject.optString(ChatConstants.CONTENT);
			if (type.equals(ChatConstants.IMAGE)) {
				content = ChatConstants.IMAGE_CHINESE;
			} else if (type.equals(ChatConstants.VOICE)) {
				content = ChatConstants.VOICE_CHINESE;
			} else if (type.equals(ChatConstants.VIDEO)) {
				content = ChatConstants.VIDEO_CHINESE;
			}
			setType(type);
			setMessage(content);
			setDownloadUrl(msgObject.optString(ChatConstants.DOWNLOAD_URL));
			setLocationUrl(msgObject.optString(ChatConstants.LOCATION_URL));
			setFromAlias(msgObject.optString(ChatConstants.MSG_FROM_ALIAS));
			setToAlias(msgObject.optString(ChatConstants.MSG_TO_ALIAS));
			setToAccount(msgObject.optString(ChatConstants.MSG_TO_ACCOUNT));
			setPacketId(msgObject.optString(ChatConstants.PACKET_ID));
			String brandName = msgObject.optString(ChatConstants.BRAND_NAME);
			if (!TextUtils.isEmpty(brandName)) {
				//				setBrandName(brandName.toUpperCase());
				setBrandName(brandName);
			} else {
				setBrandName("");
			}

			setDeleteStatus(ChatConstants.UN_DELETE);
			setBackUp(getBackUpJson(msgObject));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public int getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(int deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public int getMsgId() {
		return msgId;
	}

	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getToAlias() {
		return toAlias;
	}

	public void setToAlias(String toAlias) {
		this.toAlias = toAlias;
	}

	public String getFromAlias() {
		return fromAlias;
	}

	public void setFromAlias(String fromAlias) {
		this.fromAlias = fromAlias;
	}

	public String getPacketId() {
		return packetId;
	}

	public void setPacketId(String packetId) {
		this.packetId = packetId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLocationUrl() {
		return locationUrl;
	}

	public void setLocationUrl(String locationUrl) {
		this.locationUrl = locationUrl;
	}

	public String getBackUp() {
		return backUp;
	}

	public void setBackUp(String backUp) {
		this.backUp = backUp;
	}

	public String getToJid() {
		return toJid;
	}

	public void setToJid(String toJid) {
		this.toJid = toJid;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public int getMsgStatus() {
		return msgStatus;
	}

	public void setMsgStatus(int msgStatus) {
		this.msgStatus = msgStatus;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public void reset() {
		this.type = "";
		this.downloadUrl = "";
		this.message = "";
		this.fromAlias = "";
		this.toAlias = "";
		this.packetId = "";
		this.toJid = "";
		this.toAccount = "";
	}

	public String getToAccount() {
		return toAccount;
	}

	public void setToAccount(String toAccount) {
		this.toAccount = toAccount;
	}

	public int getChatterType() {
		return chatterType;
	}

	public void setChatterType(int chatterType) {
		this.chatterType = chatterType;
	}

	public String toMsgString() {
		return "toJid：" + toJid + " type：" + type + " downloadUrl：" + downloadUrl + " message：" + message
				+ " fromAlias：" + fromAlias + " toAlias：" + toAlias + " packetId=" + packetId + "  toAccount："
				+ toAccount;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getStoreJid() {
		return storeJid;
	}

	public void setStoreJid(String storeJid) {
		this.storeJid = storeJid;
	}

	public String getBackUpJson(JSONObject msgObject) {//组装backup字段
		String storeJid = msgObject.optString(ChatConstants.STORE_JID);
		String storeName = msgObject.optString(ChatConstants.STORE_NAME);
		String storeNo = msgObject.optString(ChatConstants.SHOP_ORG_CODE);
//		String uuid = msgObject.optString(ChatConstants.UUID);
		String brandCode = msgObject.optString(ChatConstants.BRAND_CODE);
		String mshop = msgObject.optString(ChatConstants.MSHOP);
		//		if(TextUtils.isEmpty(storeJid)||TextUtils.isEmpty(storeName)||TextUtils.isEmpty(storeNo),){//没有值就不保存DB中
		//			return "";
		//		}
		JSONObject backUpJson = new JSONObject();
		try {
			backUpJson.put(ChatConstants.STORE_JID, storeJid);
			backUpJson.put(ChatConstants.STORE_NAME, storeName);
			backUpJson.put(ChatConstants.STORE_NO, storeNo);
//			backUpJson.put(ChatConstants.UUID, uuid);
			backUpJson.put(ChatConstants.BRAND_CODE, brandCode);
			backUpJson.put(ChatConstants.MSHOP, mshop);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return backUpJson.toString();
	}

	public String getStoreNo() {
		return storeNo;
	}

	public void setStoreNo(String storeNo) {
		this.storeNo = storeNo;
	}

}
