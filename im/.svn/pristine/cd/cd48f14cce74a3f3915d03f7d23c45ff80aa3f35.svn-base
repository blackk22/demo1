package com.wonhigh.im.db;

import java.io.File;
import java.util.ArrayList;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * roster常量
 * 
 * @author USER
 * @date 2014-11-24 下午1:48:28
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public final class RosterConstants implements BaseColumns {

	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.wonhigh.roster";

	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.wonhigh.roster";
	
	public static final String ALIASSPELL = "spell";

	public static final String ACCOUNT = "account";//登入账户

	public static final String JID = "jid";//openfire jid

	public static final String ALIAS = "alias";//昵称

	public static final String STATUS_MODE = "status_mode";//在线状态，对应0,1,2,3,4

	public static final String STATUS_MESSAGE = "status_message";//状态信息

	public static final String GROUP = "roster_group";//好友分组

	public static final String DEFAULT_SORT_ORDER = STATUS_MODE + " DESC, " + ALIAS + " COLLATE NOCASE";
	
	public static String ROSTER_DATABASE_NAME;//roster库名

	public static String ROSTER_TABLE_NAME;//roster表名

	public static String ROSTER_AUTHORITY;//roster权限

	public static Uri ROSTER_URI;//roster路径

	public static final int ROSTER_DATABASE_VERSION = 2;//版本	
	
	public static final String GROUP_INSIDE="inside";//门店组
	
	public static final String GROUP_FOLLOWER="follower";//微信组

	public static ArrayList<String> getRequiredColumns() {
		ArrayList<String> tmpList = new ArrayList<String>();
		tmpList.add(ACCOUNT);
		tmpList.add(JID);
		tmpList.add(ALIAS);
		tmpList.add(STATUS_MODE);
		tmpList.add(STATUS_MESSAGE);
		tmpList.add(ALIASSPELL);
		tmpList.add(GROUP);
		return tmpList;
	}
	/***
	 * 设置表名、库名、authority、URI_MATCHER
	 * @param tableName
	 * @param dataBaseName
	 * @param authority
	 * @param URI_MATCHER
	 * @param matchCode
	 * @param matchCode2
	 */
	public static void setNames(String tableName, String dataBaseName, String authority, UriMatcher URI_MATCHER,
			int matchCode, int matchCode2) {
		RosterConstants.ROSTER_TABLE_NAME = tableName;
		RosterConstants.ROSTER_DATABASE_NAME = dataBaseName;
		RosterConstants.ROSTER_AUTHORITY = authority;
		RosterConstants.ROSTER_URI = Uri.parse("content://" + RosterConstants.ROSTER_AUTHORITY + File.separator
				+ RosterConstants.ROSTER_TABLE_NAME);
		URI_MATCHER.addURI(RosterConstants.ROSTER_AUTHORITY, RosterConstants.ROSTER_TABLE_NAME, matchCode);
		URI_MATCHER.addURI(RosterConstants.ROSTER_AUTHORITY, RosterConstants.ROSTER_TABLE_NAME + "/#", matchCode2);
	}

}
