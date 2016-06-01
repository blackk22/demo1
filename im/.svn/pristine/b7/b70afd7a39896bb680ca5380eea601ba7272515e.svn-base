package com.wonhigh.im.db;

import java.io.File;
import java.util.ArrayList;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * chat常量
 * 
 * @author USER
 * @date 2014-12-1 下午4:06:29
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public class ChatConstants implements BaseColumns {

	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.wonhigh.chat";

	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.wonhigh.chat";

	public static final String DEFAULT_SORT_ORDER = "_id ASC"; // 

	public static final String ACCOUNT = "account";// 当前登入账号

	public static final String DATE = "date";// 时间

	public static final String DIRECTION = "direction";// 发送方向：0收到，1发出

	public static final String TO_JID = "toJid";// 对方jid

	public static final String MESSAGE = "message";// 消息
	public static final String CONTENT = "content";// json消息内容

	public static final String TYPE = "type";//消息类型
	
	public static final String LOCATION_URL = "locationUrl";//文件本地地址

	public static final String DOWNLOAD_URL = "downloadUrl";//文件下载地址

	public static final String MSG_TO_ALIAS = "msgToAlias";//消息接收者昵称，仅用于解析json
	public static final String TO_ALIAS = "toAlias";//消息接收者昵称

	public static final String MSG_FROM_ALIAS = "msgFromAlias";//消息发送者昵称，仅用于解析json
	public static final String FROM_ALIAS = "fromAlias";//消息发送者昵称
	
	public static final String MSG_TO_ACCOUNT = "msgToAccount";//消息发送者账号，仅用于解析json

	public static final String BACK_UP = "backUp";//备用字段

	public static final String MSG_STATUS = "msgStatus"; // 消息是否已读

//	public static final String FILE_STATUS = "file"; // 文件接收发送 :0接收或发送中，1成功，2失败

	public static final String PACKET_ID = "packetId";// 消息id
	
	public static final String DELETE_STATUS = "delete_status";//消息是否是删除状态
	
	public static final String BRAND_NAME = "brandName";// 发消息所属的品牌
	
	public static final String COUNT = "count";// 消息条数

	// boolean mappings
	public static final int INCOMING = 0;// 收到

	public static final int OUTGOING = 1;// 发出
	
	// 清除状态
	public static final int AREADY_DELETE = 2;// 已经清除

	public static final int UN_DELETE = 1;// 尚未清除

	public static final int DS_NEW = 0; // 这个消息尚未发送/显示

	public static final int DS_SENT_OR_READ = 1; // < 这个消息已发出但还没回执, 或者已接收并且阅读

	public static final int DS_ACKED = 2; // < 消息已取得回执
	
	public static final int DS_UPLOAD_FAIL=3;//消息已插入DB,文件上传失败
	
	public static final int DS_MSG_FAIL=4;//文件上传成功，发送消息后没有收到回执或文本消息发送后没有收到回执；

//	public static final int FILE_ING = 0;//文件发送中
//
//	public static final int FILE_SUCCESS = 1;//文件发送成功
//
//	public static final int FILE_FAILURE = 2;//文件发送失败

	public static String CHAT_DATABASE_NAME;//chat库名

	public static String CHAT_TABLE_NAME;//chat表名

	public static String CHAT_AUTHORITY;//chat权限

	public static Uri CHAT_URI;//chat路径

	public static final int CHAT_DATABASE_VERSION = 6;//版本	
	
	public static final String TEXT = "text";
	public static final String TEXT_CHINESE = "[文本]";

	public static final String IMAGE = "image";
	public static final String IMAGE_CHINESE = "[图片]";

	public static final String VOICE = "voice";
	public static final String VOICE_CHINESE = "[语音]";

	public static final String VIDEO = "video";
	public static final String VIDEO_CHINESE = "[视频]";
	
	public static String SHOP_REPLY_MSG="";
	
	public static final String STORE_NAME="storeName";
	public static final String STORE_JID="storeJid";
	public static final String STORE_NO="storeNo";
	public static final String SHOP_ORG_CODE="shopOrgCode";
	public static final String UUID="uuid";
	public static final String BRAND_CODE="brandNo";
	public static final String MSHOP="mshop";
	public static final String IS_MSHOP="1";
	public static final String IS_NOT_MSHOP="0";
	
    /**
     * 聊天者logo或昵称发生变化消息类型*
     */
    public final static String CHATTER_INFO_MESSAGE = "chatterInfo";
    
    public static final String NEW_NAME="newName";//微信新昵称或门店新门店名

	public static ArrayList<String> getRequiredColumns() {
		ArrayList<String> tmpList = new ArrayList<String>();
		//		tmpList.add(ACCOUNT);
		//		tmpList.add(DATE);
		//		tmpList.add(DIRECTION);
		//		tmpList.add(JID);
		//		tmpList.add(MESSAGE);
		//		tmpList.add(FILE_STATUS);		
		tmpList.add(ACCOUNT);
		tmpList.add(DATE);
		tmpList.add(DIRECTION);
		tmpList.add(TO_JID);
		tmpList.add(MESSAGE);
		tmpList.add(TYPE);
		tmpList.add(LOCATION_URL);
		tmpList.add(DOWNLOAD_URL);
		tmpList.add(TO_ALIAS);
		tmpList.add(FROM_ALIAS);
		tmpList.add(BACK_UP);
		return tmpList;
	}

	/***
	 * 设置表名、库名、authority、URI_MATCHER以及addURI
	 * @param tableName
	 * @param dataBaseName
	 * @param authority
	 * @param URI_MATCHER
	 * @param matchCode
	 * @param matchCode2
	 */
	public static void setNames(String tableName, String dataBaseName, String authority, UriMatcher URI_MATCHER,
			int matchCode, int matchCode2) {
		ChatConstants.CHAT_TABLE_NAME = tableName;
		ChatConstants.CHAT_DATABASE_NAME = dataBaseName;
		ChatConstants.CHAT_AUTHORITY = authority;
		ChatConstants.CHAT_URI = Uri.parse("content://" + ChatConstants.CHAT_AUTHORITY + File.separator
				+ ChatConstants.CHAT_TABLE_NAME);
		URI_MATCHER.addURI(ChatConstants.CHAT_AUTHORITY, ChatConstants.CHAT_TABLE_NAME, matchCode);
		URI_MATCHER.addURI(ChatConstants.CHAT_AUTHORITY, ChatConstants.CHAT_TABLE_NAME + "/#", matchCode2);
	}

}
