package com.wonhigh.im.util;

import java.util.Collection;

import net.sourceforge.pinyin4j.PinyinHelper;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.ContentValues;
import android.content.Context;
import com.wonhigh.im.constants.IMConstants;
import com.wonhigh.im.db.ChatConstants;
import com.wonhigh.im.db.RosterConstants;
import com.wonhigh.im.entity.ChatMessage;

/**
 * TODO: 增加描述
 * 
 * @author USER
 * @date 2014-12-3 上午10:30:40
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public class IMXmppUtil {

	/**
	 * 获取当前登入用户名
	 * @return
	 */
	public static String getAccount(Context context) {
		return IMPreferenceUtils.getPrefString(context, IMConstants.IM_ACCOUNT, "");
	}

	/**
	 * 获取jid
	 * @param xx@172.17.210.108/im
	 * @return xx@172.17.210.108
	 */
	public static String getJid(String from) {
		String[] res = from.split("/");
		return res[0].toLowerCase();
	}

	/**
	 * 根据jid获取昵称
	 * @param roster
	 * @param jid
	 * @return
	 */
	public static String getNameForJID(Roster roster, String jid) {
		if (null != roster.getEntry(jid) && null != roster.getEntry(jid).getName()
				&& roster.getEntry(jid).getName().length() > 0) {
			return roster.getEntry(jid).getName();
		} else {
			return jid;
		}
	}

	/**
	 * 获取昵称
	 * @param rosterEntry
	 * @return
	 */
	public static String getAlias(RosterEntry rosterEntry) {
		String name = rosterEntry.getName();
		if (name != null && name.length() > 0) {
			return name;
		}
		name = StringUtils.parseName(rosterEntry.getUser());
		if (name.length() > 0) {
			return name;
		}
		return rosterEntry.getUser();
	}

	/**
	 * 获取组名
	 * @param groups
	 * @return
	 */
	public static String getGroup(RosterEntry entry) {
		Collection<RosterGroup> groups = entry.getGroups();
		for (RosterGroup group : groups) {
			return group.getName();
		}
		return "";
	}

	/**
	 * 用户在线状态，暂时不处理
	 * @param presence
	 * @return
	 */
	public static int getStatusInt(Presence presence) {

		return 1;
	}

	//	public static ContentValues getContentValuesForChatMessage(String account, int direction, String JID,
	//			String message, int delivery_status, long ts, String packetID) {
	//
	//		ContentValues values = new ContentValues();
	//		values.put(ChatConstants.ACCOUNT, account);
	//		values.put(ChatConstants.DIRECTION, direction);
	//		values.put(ChatConstants.JID, JID);
	//		values.put(ChatConstants.MESSAGE, message);
	//		values.put(ChatConstants.DELIVERY_STATUS, delivery_status);
	//		values.put(ChatConstants.DATE, ts);
	//		values.put(ChatConstants.PACKET_ID, packetID);
	//		//删除该字段
	//		//		values.put(ChatConstants.FILE_STATUS, ChatConstants.FILE_ING);
	//		return values;
	//
	//	}

	public static ContentValues chatMsg2ContentValues(String account, int direction, String JID,
			ChatMessage mChatMessage, int msgStatus, long ts, String packetID) {
		ContentValues values = new ContentValues();
		values.put(ChatConstants.ACCOUNT, account);
		values.put(ChatConstants.DIRECTION, direction);
		values.put(ChatConstants.TO_JID, JID);
		values.put(ChatConstants.MESSAGE, mChatMessage.getMessage());
		values.put(ChatConstants.TYPE, mChatMessage.getType());
		values.put(ChatConstants.FROM_ALIAS, mChatMessage.getFromAlias());
		values.put(ChatConstants.TO_ALIAS, mChatMessage.getToAlias());
		values.put(ChatConstants.DOWNLOAD_URL, mChatMessage.getDownloadUrl());
		values.put(ChatConstants.LOCATION_URL, mChatMessage.getLocationUrl());
		values.put(ChatConstants.BACK_UP, mChatMessage.getBackUp());
		values.put(ChatConstants.MSG_STATUS, msgStatus);
		values.put(ChatConstants.DATE, ts);
		values.put(ChatConstants.PACKET_ID, packetID);
		values.put(ChatConstants.DELETE_STATUS, ChatConstants.UN_DELETE);
		values.put(ChatConstants.BRAND_NAME, mChatMessage.getBrandName());

		return values;

	}

	public static ContentValues chatMsg2ContentValues(ChatMessage mChatMessage) {
		ContentValues values = new ContentValues();
		values.put(ChatConstants.ACCOUNT, mChatMessage.getAccount());
		values.put(ChatConstants.DIRECTION, mChatMessage.getDirection());
		values.put(ChatConstants.TO_JID, mChatMessage.getToJid());
		values.put(ChatConstants.MESSAGE, mChatMessage.getMessage());
		values.put(ChatConstants.TYPE, mChatMessage.getType());
		values.put(ChatConstants.FROM_ALIAS, mChatMessage.getFromAlias());
		values.put(ChatConstants.TO_ALIAS, mChatMessage.getToAlias());
		values.put(ChatConstants.DOWNLOAD_URL, mChatMessage.getDownloadUrl());
		values.put(ChatConstants.LOCATION_URL, mChatMessage.getLocationUrl());
		values.put(ChatConstants.BACK_UP, mChatMessage.getBackUp());
		values.put(ChatConstants.MSG_STATUS, mChatMessage.getMsgStatus());
		values.put(ChatConstants.DATE, mChatMessage.getDate());
		values.put(ChatConstants.PACKET_ID, mChatMessage.getPacketId());
		values.put(ChatConstants.DELETE_STATUS, mChatMessage.getDeleteStatus());
		values.put(ChatConstants.BRAND_NAME, mChatMessage.getBrandName());
		return values;

	}

	public static ContentValues getContentValuesForRosterEntry(RosterEntry entry, Roster roster, String account) {
		final ContentValues values = new ContentValues();
		values.put(RosterConstants.JID, entry.getUser());
		values.put(RosterConstants.ALIAS, IMXmppUtil.getAlias(entry));
		Presence presence = roster.getPresence(entry.getUser());
		values.put(RosterConstants.STATUS_MODE, IMXmppUtil.getStatusInt(presence));
		values.put(RosterConstants.STATUS_MESSAGE, presence.getStatus());
		values.put(RosterConstants.GROUP, IMXmppUtil.getGroup(entry));
		values.put(RosterConstants.ALIASSPELL, SpellDateUtil.getSpell(IMXmppUtil.getAlias(entry)).toUpperCase());
		values.put(RosterConstants.ACCOUNT, account);
		return values;
	}
	

	/***
	 * 组装msg待发送
	 * @param mChatMessage
	 * @return
	 */
	public static String assembleMsg(ChatMessage mChatMessage) {
		JSONObject msgObject = new JSONObject();
		try {
			msgObject.put(ChatConstants.CONTENT, mChatMessage.getMessage());
			msgObject.put(ChatConstants.TYPE, mChatMessage.getType());
			msgObject.put(ChatConstants.LOCATION_URL, "");//不需要发送
			msgObject.put(ChatConstants.DOWNLOAD_URL, mChatMessage.getDownloadUrl());
			msgObject.put(ChatConstants.MSG_TO_ALIAS, mChatMessage.getToAlias());
			msgObject.put(ChatConstants.MSG_FROM_ALIAS, mChatMessage.getFromAlias());
			msgObject.put(ChatConstants.MSG_TO_ACCOUNT, mChatMessage.getToAccount());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msgObject.toString();

	}

	/***
	 * 判断jid是否属于微信用户
	 * @param toJid
	 * @return
	 */
	public static boolean isWeiXinMember(String toJid) {
		String[] splitJid = toJid.split(IMConstants.JID_SPLIT_CHARACTER);
		if (splitJid[0].length() >= 20) {
			return true;
		}
		return false;
	}

	/***
	 * 从jid获取账号名（即登陆名）
	 * @return
	 */
	public static String getAccountFromJid(String jid) {
		String[] splitJid = jid.split(IMConstants.JID_SPLIT_CHARACTER);
		return splitJid[0];
	}

}
