package com.wonhigh.im.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.wonhigh.im.constants.IMConstants;
import com.wonhigh.im.db.ChatConstants;
import com.wonhigh.im.db.ChatProvider;
import com.wonhigh.im.db.RosterConstants;
import com.wonhigh.im.util.IMLogger;
import com.wonhigh.im.util.IMPreferenceUtils;

/**
 * TODO: 增加描述
 * 
 * @author USER
 * @date 2014-12-5 下午4:53:36
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public class IMDBManager {

	public static final String TAG = IMDBManager.class.getSimpleName();

	private static IMDBManager instance;

	private Context context;

	private ContentResolver contentResolver;

	private String account;

	public IMDBManager(Context context) {
		this.context = context;

		contentResolver = context.getContentResolver();

		account = IMPreferenceUtils.getPrefString(context, IMConstants.IM_ACCOUNT, "");
	}

	public static IMDBManager getInstance(Context context) {

		if (instance == null) {

			instance = new IMDBManager(context);

		}
		return instance;
	}

	public Cursor queryAllRoster() {

		Cursor query = contentResolver.query(RosterConstants.ROSTER_URI, null, null, null, null);

		return query;
	}

	public Cursor queryChatByJid(String jid) {
		// 查询字段
		//		String[] projection = new String[] { ChatConstants._ID, ChatConstants.ACCOUNT, ChatConstants.DATE,
		//				ChatConstants.DIRECTION, ChatConstants.JID, ChatConstants.MESSAGE, ChatConstants.MSG_STATUS,
		//				ChatConstants.FILE_STATUS, ChatConstants.PACKET_ID, };
		String[] projection = new String[] { ChatConstants._ID, ChatConstants.ACCOUNT, ChatConstants.DATE,
				ChatConstants.DIRECTION, ChatConstants.TO_JID, ChatConstants.MESSAGE, ChatConstants.MSG_STATUS,
				ChatConstants.LOCATION_URL, ChatConstants.DOWNLOAD_URL, ChatConstants.FROM_ALIAS,
				ChatConstants.TO_ALIAS, ChatConstants.TYPE, ChatConstants.BACK_UP, ChatConstants.PACKET_ID,
				ChatConstants.BRAND_NAME};
		//		String selection = RosterConstants.ACCOUNT + " = ? and " + RosterConstants.JID + " = ?";
		String selection = ChatConstants.ACCOUNT + " = ? and " + ChatConstants.TO_JID + " = ?";

		//		String SORT_ORDER = ChatConstants._ID + " ASC";

		Cursor query = contentResolver.query(ChatConstants.CHAT_URI, projection, selection,
				new String[] { account, jid }, null);

		return query;
	}

	/***
	 * 查询与jid的所有聊天记录，分页的
	 * @param jid
	 * @param from
	 * @param to
	 * @return
	 */

	public Cursor queryChatsByJid(String jid, int from, int to) {
		/**查询字段*/
		//		String[] projection = new String[] { ChatConstants._ID, ChatConstants.ACCOUNT, ChatConstants.DATE,
		//				ChatConstants.DIRECTION, ChatConstants.JID, ChatConstants.MESSAGE, ChatConstants.MSG_STATUS,
		//				ChatConstants.FILE_STATUS, ChatConstants.PACKET_ID, };
		String[] projection = new String[] { ChatConstants._ID, ChatConstants.ACCOUNT, ChatConstants.DATE,
				ChatConstants.DIRECTION, ChatConstants.TO_JID, ChatConstants.MESSAGE, ChatConstants.MSG_STATUS,
				ChatConstants.LOCATION_URL, ChatConstants.DOWNLOAD_URL, ChatConstants.FROM_ALIAS,
				ChatConstants.TO_ALIAS, ChatConstants.TYPE, ChatConstants.BACK_UP, ChatConstants.PACKET_ID,
				ChatConstants.BRAND_NAME};
		//		String selection = RosterConstants.ACCOUNT + " =? and " + RosterConstants.JID + " = ?";
		String selection = ChatConstants.ACCOUNT + " = ? and " + ChatConstants.TO_JID + " = ?";
		/**从from开始的requestCount条数据，包括from*/
		int requestCount = to - from + 1;
		String SORT_ORDER = ChatConstants._ID + " ASC LIMIT " + from + " ," + requestCount;
		Cursor cursor = contentResolver.query(ChatConstants.CHAT_URI, projection, selection, new String[] { account,
				jid }, SORT_ORDER);

		return cursor;
	}

	/**
	 * 根据MsgId查聊天记录
	 * @param msgId
	 * @param jid
	 * @return
	 */
	public Cursor queryChatsByMsgId(int msgId, String jid) {
		String[] projection = new String[] { ChatConstants._ID, ChatConstants.ACCOUNT, ChatConstants.DATE,
				ChatConstants.DIRECTION, ChatConstants.TO_JID, ChatConstants.MESSAGE, ChatConstants.MSG_STATUS,
				ChatConstants.LOCATION_URL, ChatConstants.DOWNLOAD_URL, ChatConstants.FROM_ALIAS,
				ChatConstants.TO_ALIAS, ChatConstants.TYPE, ChatConstants.BACK_UP, ChatConstants.PACKET_ID,
				ChatConstants.BRAND_NAME};
		String selection = ChatConstants.ACCOUNT + " = ? and " + ChatConstants._ID + " >= ? and "
				+ ChatConstants.TO_JID + " = ?";
		Cursor cursor = contentResolver.query(ChatConstants.CHAT_URI, projection, selection, new String[] { account,
				msgId + "", jid }, null);
		return cursor;
	}

	/****
	 * 查询最近聊天
	 * @return
	 */

	public Cursor queryRecentMsg() {
		String selection = ChatConstants.ACCOUNT + " = '" + account 
				+ "' and " + ChatConstants.DELETE_STATUS + " = "+ ChatConstants.UN_DELETE
				+ " and " + ChatConstants.DATE + " in (select max(" + ChatConstants.DATE
				+ ") from " + ChatConstants.CHAT_TABLE_NAME + " group by " + ChatConstants.TO_JID
				+ " having count(*)>0)";// 查询合并重复jid字段的所有聊天对象

		//		String[] projection = new String[] { ChatConstants._ID, ChatConstants.ACCOUNT, ChatConstants.DATE,
		//				ChatConstants.DIRECTION, ChatConstants.JID, ChatConstants.MESSAGE, ChatConstants.MSG_STATUS,
		//				ChatConstants.FILE_STATUS, ChatConstants.PACKET_ID };
		// 查询字段
		String[] projection = new String[] { ChatConstants._ID, ChatConstants.ACCOUNT, ChatConstants.DATE,
				ChatConstants.DIRECTION, ChatConstants.TO_JID, ChatConstants.MESSAGE, ChatConstants.MSG_STATUS,
				ChatConstants.LOCATION_URL, ChatConstants.DOWNLOAD_URL, ChatConstants.FROM_ALIAS,
				ChatConstants.TO_ALIAS, ChatConstants.TYPE, ChatConstants.BACK_UP, ChatConstants.PACKET_ID,
				ChatConstants.BRAND_NAME};

		String SORT_ORDER = ChatConstants.DATE + " DESC";
		Cursor cursor = contentResolver.query(ChatConstants.CHAT_URI, projection, selection, null, SORT_ORDER);
		return cursor;
	}

	/***
	 * 查询未读新消息
	 * @return
	 */
	public Cursor queryNewMsgNum() {
		String selection = ChatConstants.ACCOUNT + " = '" + account + "' AND " + ChatConstants.DIRECTION + " = "
				+ ChatConstants.INCOMING + " AND " + ChatConstants.MSG_STATUS + " = " + ChatConstants.DS_NEW;// 新消息数量字段

		Cursor cursor = contentResolver.query(ChatConstants.CHAT_URI, null, selection, null, null);
		return cursor;
	}

	/***
	 * 查询联系人，根据组名
	 * @return
	 */
	public Cursor queryRoster(String groupName) {
		String[] projection = new String[] { RosterConstants.ACCOUNT, RosterConstants.JID, RosterConstants.ALIAS,
				RosterConstants.STATUS_MODE, RosterConstants.ALIASSPELL, RosterConstants.STATUS_MESSAGE,
				RosterConstants.GROUP };// 查询字段
		//		String selection = RosterConstants.ACCOUNT + " = ? and " + RosterConstants.GROUP + " = ?";
		String openfireIP = IMPreferenceUtils.getPrefString(context, IMConstants.OPENFIRE_IP, "");
		String selection = RosterConstants.ACCOUNT + " = ? and " 
		                 + RosterConstants.GROUP + " like '%" + groupName + "%' and "
				         + RosterConstants.JID + " like '%" + openfireIP + "%'";
		Cursor cursor = contentResolver.query(RosterConstants.ROSTER_URI, projection, selection, new String[] {
				account}, null);
		return cursor;
	}

	/****
	 * 查询昵称
	 * @param jid
	 * @return
	 */

	public Cursor queryAliasByJid(String jid) {
		String[] projection = new String[] { RosterConstants.ALIAS, };// 查询字段
		String selection = RosterConstants.ACCOUNT + " = ? and " + RosterConstants.JID + " = ?";
		Cursor cursor = contentResolver.query(RosterConstants.ROSTER_URI, projection, selection, new String[] {
				account, jid }, null);
		return cursor;
	}

	/***
	 * 查询分组
	 * @param jid
	 * @return
	 */
	public Cursor queryGroupByJid(String jid) {
		String[] projection = new String[] { RosterConstants.GROUP, };// 查询字段
		String selection = RosterConstants.ACCOUNT + " = ? and " + RosterConstants.JID + " = ?";
		Cursor cursor = contentResolver.query(RosterConstants.ROSTER_URI, projection, selection, new String[] {
				account, jid }, null);
		return cursor;
	}

	/**
	 * 查询聊天记录的数目
	 * @param jid
	 * @return
	 */
	public Cursor queryChatsCount(String jid) {
		//		String[] projection = new String[] { ChatConstants._ID, ChatConstants.ACCOUNT, ChatConstants.DATE,
		//				ChatConstants.DIRECTION, ChatConstants.JID, ChatConstants.MESSAGE, ChatConstants.MSG_STATUS,
		//				ChatConstants.FILE_STATUS, ChatConstants.PACKET_ID, };
		// 查询字段
		String[] projection = new String[] { ChatConstants._ID, ChatConstants.ACCOUNT, ChatConstants.DATE,
				ChatConstants.DIRECTION, ChatConstants.TO_JID, ChatConstants.MESSAGE, ChatConstants.MSG_STATUS,
				ChatConstants.LOCATION_URL, ChatConstants.DOWNLOAD_URL, ChatConstants.FROM_ALIAS,
				ChatConstants.TO_ALIAS, ChatConstants.TYPE, ChatConstants.BACK_UP, ChatConstants.PACKET_ID };
		//		String selection = RosterConstants.ACCOUNT + " =? and " + RosterConstants.JID + " = ?";
		String selection = ChatConstants.ACCOUNT + " = ? and " + ChatConstants.TO_JID + " = ?";
		Cursor cursor = contentResolver.query(ChatConstants.CHAT_URI, projection, selection, new String[] { account,
				jid }, null);
		return cursor;
	}

	/****
	 * 查询上一条信息的时间
	 * @param jid
	 * @return
	 */

	public Cursor queryLastMsgDate(String jid) {
		String[] projection = new String[] { ChatConstants.DATE, };// 查询字段
		String selection = ChatConstants.ACCOUNT + " = ? and " + ChatConstants.TO_JID + " = ? and "
				+ ChatConstants.DIRECTION + " = " + ChatConstants.INCOMING;
		Cursor cursor = contentResolver.query(ChatConstants.CHAT_URI, projection, selection, new String[] { account,
				jid }, null);
		return cursor;
	}

	/***
	 * 查询消息id
	 * @param jid
	 * @param packetId
	 * @return
	 */
	public Cursor queryMsgID(String jid, String packetId) {
		String[] projection = new String[] { ChatConstants._ID };// 查询字段
		String selection = ChatConstants.ACCOUNT + " = ? and " + ChatConstants.TO_JID + " = ? and "
				+ ChatConstants.PACKET_ID + " = ?";
		Cursor cursor = contentResolver.query(ChatConstants.CHAT_URI, projection, selection, new String[] { account,
				jid, packetId }, null);
		return cursor;
	}

	/***
	 * 删除聊天记录
	 */
	public int deleteChats() {
		return contentResolver
				.delete(ChatConstants.CHAT_URI, ChatConstants.ACCOUNT + " = ? ", new String[] { account });
	}

	/***
	 * 删除通讯录
	 * @return
	 */
	public int deleteRosters() {
		return contentResolver.delete(RosterConstants.ROSTER_URI, RosterConstants.ACCOUNT + " = ? ",
				new String[] { account });
	}

	/***
	 * 根据关键字搜索聊天记录
	 * @param searchKey
	 * @return
	 */
	public Cursor queryChatsByKey(String searchKey) throws Exception {
		// 查询字段
		String[] projection = new String[] { ChatConstants._ID, ChatConstants.ACCOUNT, ChatConstants.DATE,
				ChatConstants.DIRECTION, ChatConstants.TO_JID, ChatConstants.MESSAGE, ChatConstants.MSG_STATUS,
				ChatConstants.LOCATION_URL, ChatConstants.DOWNLOAD_URL, ChatConstants.FROM_ALIAS,
				ChatConstants.TO_ALIAS, ChatConstants.TYPE, ChatConstants.BACK_UP, ChatConstants.PACKET_ID };
		String selection = ChatConstants.ACCOUNT + " = ? and " + ChatConstants.TYPE + " = ? and "
				+ ChatConstants.MESSAGE + " like '%" + replaceSpecialChar(searchKey) + "%'";

		String SORT_ORDER = ChatConstants.DATE + " DESC";

		return contentResolver.query(ChatConstants.CHAT_URI, projection, selection, new String[] { account,
				ChatConstants.TEXT }, SORT_ORDER);
	}

	/***
	 * 替换特殊字符,单引号字符处理,通配符'_'处理
	 * @param content
	 * @return
	 */
	public String replaceSpecialChar(String content) {
		content = content.replace("_", "/_").replace("'", "''").replace("/", "//").replaceAll("\"", "\\\\\"")
				.replace("\\", "\\\\").replace("%", "/%");
		return content;
	}

	/***
	 * 根据关键字搜索用户
	 * @param searchKey
	 * @return
	 */
	public Cursor queryRosterByKey(String groupName,String searchKey) throws Exception {
		String[] projection = new String[] { RosterConstants.ACCOUNT, RosterConstants.JID, RosterConstants.ALIAS,
				RosterConstants.STATUS_MODE, RosterConstants.STATUS_MESSAGE, RosterConstants.GROUP };// 查询字段
		//		String selection = RosterConstants.ACCOUNT + " = '" + account + "' and " + RosterConstants.ALIAS + " like '%"
		//				+ replaceSpecialChar(searchKey) + "%' escape '/' ";
		String selection = RosterConstants.ACCOUNT + " = ? and " 
				          + RosterConstants.GROUP + " like '%" + groupName + "%' and "
		                  + RosterConstants.ALIAS + " like '%"+ replaceSpecialChar(searchKey) + "%' escape '/' ";
		return contentResolver.query(RosterConstants.ROSTER_URI, projection, selection, new String[] { account }, null);

	}

	/***
	 * 根据关键字以及jid搜索聊天记录
	 * @param jid
	 * @param searchKey
	 * @return
	 */
	public Cursor queryChatsByJidAndKey(String jid, String searchKey) throws Exception {
		//		String[] projection = new String[] { ChatConstants._ID, ChatConstants.ACCOUNT, ChatConstants.DATE,
		//				ChatConstants.DIRECTION, ChatConstants.JID, ChatConstants.MESSAGE, ChatConstants.MSG_STATUS,
		//				ChatConstants.FILE_STATUS, ChatConstants.PACKET_ID, };
		// 查询字段
		String[] projection = new String[] { ChatConstants._ID, ChatConstants.ACCOUNT, ChatConstants.DATE,
				ChatConstants.DIRECTION, ChatConstants.TO_JID, ChatConstants.MESSAGE, ChatConstants.MSG_STATUS,
				ChatConstants.LOCATION_URL, ChatConstants.DOWNLOAD_URL, ChatConstants.FROM_ALIAS,
				ChatConstants.TO_ALIAS, ChatConstants.TYPE, ChatConstants.BACK_UP, ChatConstants.PACKET_ID };
		//		searchKey = "'%\"content\":%" + replaceSpecialChar(searchKey) + "%\"" + ",\"type\":\"text\"%'";
		//		String selection = ChatConstants.MESSAGE + " like " + searchKey + "  " + " and "
		//				+ RosterConstants.JID + " = ?";

		String selection = ChatConstants.ACCOUNT + " = ? and " + ChatConstants.TYPE + " = ? and "
				+ ChatConstants.TO_JID + " = ? and " + ChatConstants.MESSAGE + " like '%"
				+ replaceSpecialChar(searchKey) + "%' escape '/'";
		return contentResolver.query(ChatConstants.CHAT_URI, projection, selection, new String[] { account,
				ChatConstants.TEXT, jid }, null);

	}

	/***
	 * 根据id查找聊天记录
	 * @param chatId
	 * @return
	 */
	public Cursor queryChatsByID(String chatId) {
		//		String[] projection = new String[] { ChatConstants._ID, ChatConstants.ACCOUNT, ChatConstants.DATE,
		//				ChatConstants.DIRECTION, ChatConstants.JID, ChatConstants.MESSAGE, ChatConstants.MSG_STATUS,
		//				ChatConstants.FILE_STATUS, ChatConstants.PACKET_ID, };
		// 查询字段
		String[] projection = new String[] { ChatConstants._ID, ChatConstants.ACCOUNT, ChatConstants.DATE,
				ChatConstants.DIRECTION, ChatConstants.TO_JID, ChatConstants.MESSAGE, ChatConstants.MSG_STATUS,
				ChatConstants.LOCATION_URL, ChatConstants.DOWNLOAD_URL, ChatConstants.FROM_ALIAS,
				ChatConstants.TO_ALIAS, ChatConstants.TYPE, ChatConstants.BACK_UP, ChatConstants.PACKET_ID };
		String selection = ChatConstants._ID + " =?";
		return contentResolver.query(ChatConstants.CHAT_URI, projection, selection, new String[] { chatId }, null);

	}

	/***
	 * 查询最近消息
	 * @param jid
	 * @return
	 */
	public Cursor queryNewMsgNum(String jid) {

		String selection = ChatConstants.ACCOUNT + " = '" + account + "' AND " + ChatConstants.TO_JID + " = '" + jid
				+ "' AND " + ChatConstants.DIRECTION + " = " + ChatConstants.INCOMING + " AND "
				+ ChatConstants.MSG_STATUS + " = " + ChatConstants.DS_NEW;// 新消息数量字段
		return contentResolver.query(ChatConstants.CHAT_URI, null, selection, null, null);
	}

	/***
	 * 更新文件状态和消息内容
	 * @param id
	 * @param fileStatus
	 * @param message
	 */

	//	public void updateMsgAndStatus(int id, int readStatus, String message) {
	//		Uri rowuri = Uri.parse("content://" + ChatConstants.CHAT_AUTHORITY + "/" + ChatConstants.CHAT_TABLE_NAME + "/"
	//				+ id);
	//		ContentValues values = new ContentValues();
	//		values.put(ChatConstants.MSG_STATUS, readStatus);
	//		values.put(ChatConstants.MESSAGE, message);
	//		contentResolver.update(rowuri, values, null, null);
	//	}

	public void updateDownloadUrl(int msgId, String url) {
		Uri rowuri = Uri.parse("content://" + ChatConstants.CHAT_AUTHORITY + "/" + ChatConstants.CHAT_TABLE_NAME + "/"
				+ msgId);
		ContentValues values = new ContentValues();
		values.put(ChatConstants.DOWNLOAD_URL, url);
		contentResolver.update(rowuri, values, null, null);
	}

	public void updateLocationUrl(int msgId, String url) {
		Uri rowuri = Uri.parse("content://" + ChatConstants.CHAT_AUTHORITY + "/" + ChatConstants.CHAT_TABLE_NAME + "/"
				+ msgId);
		ContentValues values = new ContentValues();
		values.put(ChatConstants.LOCATION_URL, url);
		contentResolver.update(rowuri, values, null, null);
	}

	/***
	 * 删除指定消息
	 * @param msgId  
	 * @return
	 */
	public int deleteMsgById(String msgId) {
		Uri rowuri = Uri.parse("content://" + ChatConstants.CHAT_AUTHORITY + "/" + ChatConstants.CHAT_TABLE_NAME + "/"
				+ msgId);
		return contentResolver.delete(rowuri, null, null);
	}

	/***
	 * 更新信息状态
	 * 
	 * @return
	 */
	public boolean updateAllMsgStatus() {
		ContentValues values = new ContentValues();
		values.put(ChatConstants.MSG_STATUS, ChatConstants.DS_NEW);
		String where = ChatConstants.ACCOUNT + " = ?";
		return contentResolver.update(ChatConstants.CHAT_URI, values, where, new String[] { account }) > 0;
	}

	/***
	 * 更新信息状态
	 * @param msgStatus
	 * @return
	 */
	public boolean updateAllMsgStatus(int msgStatus) {
		ContentValues values = new ContentValues();
		values.put(ChatConstants.MSG_STATUS, msgStatus);
		String where = ChatConstants.ACCOUNT + " = ? and " + ChatConstants.DIRECTION + " = ? and "
				+ ChatConstants.MSG_STATUS + " = ?";
		return contentResolver.update(ChatConstants.CHAT_URI, values, where, new String[] { account,
				ChatConstants.INCOMING + "", ChatConstants.DS_NEW + "" }) > 0;
	}

	/**
	 * 根据消息id更新消息状态
	 * @param msgId
	 * @param status
	 * @return
	 */
	public boolean updateMsgStatusByMsgId(int msgId, int status) {
		//		Uri rowuri = Uri.parse("content://" + ChatConstants.CHAT_AUTHORITY + "/" + ChatConstants.CHAT_TABLE_NAME + "/"
		//				+ msgId);
		//		ContentValues values = new ContentValues();
		//				String where = ChatConstants.MSG_STATUS + " !=" + ChatConstants.DS_ACKED;
		//		values.put(ChatConstants.MSG_STATUS, status);
		//		return contentResolver.update(rowuri, values, where, new String[] { ChatConstants.IMAGE });

		ContentValues values = new ContentValues();
		values.put(ChatConstants.MSG_STATUS, status);
		String where = ChatConstants._ID + " = ? and " + ChatConstants.MSG_STATUS + " <> ?";
		return contentResolver.update(ChatConstants.CHAT_URI, values, where, new String[] { String.valueOf(msgId),
				String.valueOf(ChatConstants.DS_ACKED) }) > 0;
	}

	/***
	 * 根据packetId更新消息状态
	 * @param packetId
	 * @param status
	 * @return
	 */

	public boolean updateMsgStatusByPacketId(String packetId, int status) {

		ContentValues cv = new ContentValues();
		cv.put(ChatConstants.MSG_STATUS, status);
		String where = ChatConstants.PACKET_ID + " = ? and " + ChatConstants.MSG_STATUS + " <> ?";
		return contentResolver.update(ChatConstants.CHAT_URI, cv, where, new String[] { packetId,
				ChatConstants.DS_ACKED + "" }) > 0;
		//		return contentResolver.update(ChatConstants.CHAT_URI, cv, ChatConstants.PACKET_ID + " = ? AND "
		//				+ ChatConstants.MSG_STATUS + " != " + ChatConstants.DS_ACKED + " AND " + ChatConstants.DIRECTION
		//				+ " = " + ChatConstants.OUTGOING, new String[] { packetId }) > 0;
	}

	/***
	 * 插入数据库
	 * @param mContentValues
	 * @return
	 */
	public Uri insertMsgToDB(ContentValues mContentValues) {
		return contentResolver.insert(ChatConstants.CHAT_URI, mContentValues);

	}

	/***
	 * 查询指定packetId条目是否存在
	 * @param packetId
	 * @return
	 */
	public Cursor queryChatByPacketId(String packetId) {
		String[] projection = new String[] { ChatConstants._ID, ChatConstants.ACCOUNT, ChatConstants.DATE,
				ChatConstants.DIRECTION, ChatConstants.TO_JID, ChatConstants.MESSAGE, ChatConstants.MSG_STATUS,
				ChatConstants.LOCATION_URL, ChatConstants.DOWNLOAD_URL, ChatConstants.FROM_ALIAS,
				ChatConstants.TO_ALIAS, ChatConstants.TYPE, ChatConstants.BACK_UP, ChatConstants.PACKET_ID };
		String selection = ChatConstants.PACKET_ID + " =?";
		return contentResolver.query(ChatConstants.CHAT_URI, projection, selection, new String[] { packetId }, null);

	}

	/***
	 * 查询登陆账号的所有聊天记录
	 * @return
	 */
	public Cursor queryAllChat() {
		// 查询字段
		String[] projection = new String[] { ChatConstants._ID, ChatConstants.ACCOUNT, ChatConstants.DATE,
				ChatConstants.DIRECTION, ChatConstants.TO_JID, ChatConstants.MESSAGE, ChatConstants.MSG_STATUS,
				ChatConstants.LOCATION_URL, ChatConstants.DOWNLOAD_URL, ChatConstants.FROM_ALIAS,
				ChatConstants.TO_ALIAS, ChatConstants.TYPE, ChatConstants.BACK_UP, ChatConstants.PACKET_ID };
		String selection = ChatConstants.ACCOUNT + " = ? ";
		Cursor query = contentResolver.query(ChatConstants.CHAT_URI, projection, selection, new String[] { account },
				null);

		return query;
	}

	/***
	 * 更新指定msgId的ToJid
	 * @param msgId
	 * @param newToJid
	 * @return
	 */
	public int updateChatDBToJidByMsgId(String msgId, String newToJid) {
		ContentValues cv = new ContentValues();
		cv.put(ChatConstants.TO_JID, newToJid);
		String selection = ChatConstants._ID + " = ? ";
		return contentResolver.update(ChatConstants.CHAT_URI, cv, selection, new String[] { msgId });
	}

	/***
	 * 更新登陆账号所有聊天记录的ToJid
	 * @param chatMessageCursor
	 * @param newOpenfireIP
	 */
	public void updateAllChatToJid(Cursor chatMessageCursor, String newOpenfireIP) {
		if (chatMessageCursor != null) {
			chatMessageCursor.moveToFirst();
			while (!chatMessageCursor.isAfterLast()) {
				String msgId = (chatMessageCursor.getString(chatMessageCursor.getColumnIndex(ChatConstants._ID)));
				String oldToJid = chatMessageCursor.getString(chatMessageCursor.getColumnIndex(ChatConstants.TO_JID));
				String[] splitToJid = oldToJid.split("@");
				String newToJid = splitToJid[0] + "@" + newOpenfireIP;
				IMLogger.d(TAG, "updateChatDBToJidByMsgId result=" + updateChatDBToJidByMsgId(msgId, newToJid));
				chatMessageCursor.moveToNext();
			}
			chatMessageCursor.close();
		}
	}

	/***
	 * 更新chat表的ToAlias字段根据msgId
	 * @param msgId
	 * @param toAlias
	 * @return
	 */
	public int updateToAliasByMsgId(String msgId, String toAlias) {
		ContentValues cv = new ContentValues();
		cv.put(ChatConstants.TO_ALIAS, toAlias);
		String where = ChatConstants._ID + " = ? ";
		return contentResolver.update(ChatConstants.CHAT_URI, cv, where, new String[] { msgId });
	}

	/***
	 * 更新chat表的FromAlias字段根据msgId
	 * @param msgId
	 * @param fromAlias
	 * @return
	 */
	public int updateFromAliasByMsgId(String msgId, String fromAlias) {
		ContentValues cv = new ContentValues();
		cv.put(ChatConstants.FROM_ALIAS, fromAlias);
		String where = ChatConstants._ID + " = ? ";
		return contentResolver.update(ChatConstants.CHAT_URI, cv, where, new String[] { msgId });
	}

	/***
	 * 查询最近聊天门店的名称以及jid 机构编码
	 * @param toJid 
	 * @return
	 */
	public Cursor queryStoreInfo(String toJid) {
		// 查询字段
		String[] projection = new String[] { ChatConstants.BACK_UP };
		String SORT_ORDER = ChatConstants.DATE + " DESC";
		//只有是微信发过来的消息，ChatConstants.BACK_UP才不为空
		String selection = ChatConstants.ACCOUNT + " = ? and " + ChatConstants.TO_JID + " = ? and "
				+ ChatConstants.BACK_UP + " != ?";
		Cursor query = contentResolver.query(ChatConstants.CHAT_URI, projection, selection, new String[] { account,
				toJid, "" }, SORT_ORDER);
		return query;
	}

	/**
	 * 查询最新消息中的所有品牌
	 */
	public Cursor queryAllBrand() {
		String selection = ChatConstants.ACCOUNT + " = '" + account+"'"
				+ " and " + ChatConstants.DELETE_STATUS +" = "+ ChatConstants.UN_DELETE
				+ " and " + ChatConstants.DATE
				+ " in (select max(" + ChatConstants.DATE + ") from " + ChatConstants.CHAT_TABLE_NAME + " group by "
				+ ChatConstants.TO_JID + " having count(*)>0)";// 查询合并重复jid字段的所有聊天对象

		String[] projection = new String[] { ChatConstants.BRAND_NAME };
		String SORT_ORDER = ChatConstants.DATE + " DESC";
		Cursor cursor = contentResolver.query(ChatConstants.CHAT_URI, projection, selection, null, SORT_ORDER);
		return cursor;

	}

	/**
	 * 根据品牌查询最近联系人
	 * @param brandName
	 * @return
	 */
	public Cursor queryRecentChatByBrand(String[] brandName) {
		String selection = null;

		String start = ChatConstants.ACCOUNT + " = '" + account+ "' " 
				      +" and "+ChatConstants.DELETE_STATUS +" = "+ChatConstants.UN_DELETE;

		String end = " and " + ChatConstants.DATE + " in (select max("
				+ ChatConstants.DATE + ") from " + ChatConstants.CHAT_TABLE_NAME
		         +" group by " + ChatConstants.TO_JID + " having count(*)>0)";

		StringBuffer buffer = new StringBuffer();

		//如果品牌为空，那就返回所以品牌
		if (null == brandName || brandName.length == 0) {
			selection = start + end;
		} else {
			buffer.append(" and " + ChatConstants.BRAND_NAME + " in (");
			for (int i = 0; i < brandName.length; i++) {
				if (i == brandName.length - 1) {
					buffer.append("?)");
				} else {
					buffer.append("?,");
				}
			}
			IMLogger.d(TAG, buffer.toString());
			selection = start + buffer.toString() + end;
		}

		// 查询字段
		String[] projection = new String[] { ChatConstants._ID, ChatConstants.ACCOUNT, ChatConstants.DATE,
				ChatConstants.DIRECTION, ChatConstants.TO_JID, ChatConstants.MESSAGE, ChatConstants.MSG_STATUS,
				ChatConstants.LOCATION_URL, ChatConstants.DOWNLOAD_URL, ChatConstants.FROM_ALIAS,
				ChatConstants.TO_ALIAS, ChatConstants.TYPE, ChatConstants.BACK_UP, ChatConstants.PACKET_ID,
				ChatConstants.DELETE_STATUS, ChatConstants.BRAND_NAME };

		String SORT_ORDER = ChatConstants.DATE + " DESC";

		Cursor cursor = contentResolver.query(ChatConstants.CHAT_URI, projection, selection, brandName, SORT_ORDER);
		return cursor;

	}

	/**
	 * 修改删除状态
	 * @param id
	 */
	public void updateMsgDeletetatus(String jid) {
		List<Integer> chatIds = queryUnReadChats(jid);
		for (int i = 0; i < chatIds.size(); i++) {
			Uri rowuri = Uri.parse(ChatConstants.CHAT_URI + "/" + chatIds.get(i));
			ContentValues values = new ContentValues();
			values.put(ChatConstants.DELETE_STATUS, ChatConstants.AREADY_DELETE);
			values.put(ChatConstants.MSG_STATUS, ChatConstants.DS_SENT_OR_READ);
			contentResolver.update(rowuri, values, null, null);
		}

	}

	//把收到的并且是未讀的取出來和發出去的
	public List<Integer> queryUnReadChats(String jid) {
		List<Integer> chatIds = new ArrayList<Integer>();
		String[] projection = new String[] { ChatConstants._ID };// 查询字段
		String selection = ChatConstants.ACCOUNT + " =? and " 
		                + ChatConstants.TO_JID + " = ? and "
		                 + ChatConstants.DELETE_STATUS + " = ? ";
		Cursor cursor = contentResolver.query(ChatConstants.CHAT_URI, projection, selection, new String[] { account,
				jid, ChatConstants.UN_DELETE + "" }, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			int chatId = cursor.getInt(cursor.getColumnIndex(ChatConstants._ID));
			chatIds.add(chatId);
			cursor.moveToNext();
		}
		cursor.close();
		return chatIds;
	}
	
	
	
	/***
	 * 查询联系人，根据组名
	 * @return
	 */
	public String queryStoreBrandByJid(String jid) {
		String[] projection = new String[] {RosterConstants.GROUP };// 查询字段
		String selection = RosterConstants.JID + " = ? ";
		Cursor cursor = contentResolver.query(RosterConstants.ROSTER_URI, projection, selection, new String[] {jid}, null);
		cursor.moveToFirst();
		String groupName = "";
		while (!cursor.isAfterLast()) {
			groupName = cursor.getString(cursor.getColumnIndex(RosterConstants.GROUP));
			cursor.moveToNext();
		}
		String[] groupSplitName = groupName.split("-");
		if(null != groupSplitName && groupSplitName.length >1){
			String brandName = groupName.split("-")[1];
			if(!TextUtils.isEmpty(brandName)){
				return brandName;
			}else{
				return "";
			}
		}else{
			return "";
		}
		
	}

	/**
	 * 删除指定聊天记录
	 * @param id
	 * @return
	 */
	public boolean deleteChat(int id) {
		String selection = ChatConstants._ID + " = ?";
		int delete = contentResolver.delete(ChatConstants.CHAT_URI, selection, new String[] { id + "" });
		if (delete == 1) {
			return true;
		}
		return false;
	}
	
	/**根据jid查出最老这条消息的品牌**/
	public String queryOldestMsgBrand(String jid){
		String brandName = "";
		String[] projection = new String[] { ChatConstants.BRAND_NAME };
		String selection = ChatConstants.TO_JID + " = ? and "
				         + ChatConstants.ACCOUNT + " = '" + account + "' and " 
				         + ChatConstants.DATE + " = (select min(" + ChatConstants.DATE
		                 + ") from " + ChatConstants.CHAT_TABLE_NAME+" where "
				         + ChatConstants.TO_JID +" = ? "
		                 +")"; 


		Cursor cursor = contentResolver.query(ChatConstants.CHAT_URI, projection, selection,
				new String[] { jid,jid }, null);
		
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			brandName = cursor.getString(cursor.getColumnIndex(ChatConstants.BRAND_NAME));
		}
		cursor.close();
		return brandName;
	}
	
    /**
     * 更新收到消息的fromAlias
     *
     * @param jid
     * @param newFromAlias
     * @return
     */
    public int updateAllChatDBFromAliasByJid(String jid, String newFromAlias) {
        ContentValues cv = new ContentValues();
        cv.put(ChatConstants.FROM_ALIAS, newFromAlias);
        String selection = ChatConstants.TO_JID + " = ? and " + ChatConstants.DIRECTION + " = ? and " + ChatConstants.FROM_ALIAS + " != ?";
        return contentResolver.update(ChatConstants.CHAT_URI, cv, selection, new String[]{jid, ChatConstants.INCOMING + "", newFromAlias});
    }

    /**
     * 更新放出去消息的toAlias
     *
     * @param jid
     * @param newToAlias
     * @return
     */
    public int updateAllChatDBToAliasByJid(String jid, String newToAlias) {
        ContentValues cv = new ContentValues();
        cv.put(ChatConstants.TO_ALIAS, newToAlias);
        String selection = ChatConstants.TO_JID + " = ? and " + ChatConstants.DIRECTION + " = ? and " + ChatConstants.TO_ALIAS + " != ?";
        return contentResolver.update(ChatConstants.CHAT_URI, cv, selection, new String[]{jid, ChatConstants.OUTGOING + "", newToAlias});
    }

}
