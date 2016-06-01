package com.wonhigh.im.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.wonhigh.im.util.IMLogger;

/**
 * TODO: 增加描述
 * 
 * @author USER
 * @date 2014-11-24 下午12:20:30
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public class RosterDBHelper extends SQLiteOpenHelper {

	private static final String TAG = RosterDBHelper.class.getSimpleName();

//	public static String databaseName = "roster.db";//表名
//
//	private static final int DATABASE_VERSION = 1;//版本
//
//	public static String tableName = "roster";

	public RosterDBHelper(Context context) {
		super(context, RosterConstants.ROSTER_DATABASE_NAME, null, RosterConstants.ROSTER_DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {	

		IMLogger.d(TAG, "creating new roster table");

		db.execSQL("CREATE TABLE " + RosterConstants.ROSTER_TABLE_NAME + " (" + RosterConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ RosterConstants.JID + " TEXT UNIQUE ON CONFLICT REPLACE, " + RosterConstants.ACCOUNT + " TEXT, "
				+ RosterConstants.ALIAS + " TEXT, " + RosterConstants.ALIASSPELL + " TEXT, " + RosterConstants.STATUS_MODE + " INTEGER, "
				+ RosterConstants.STATUS_MESSAGE + " TEXT, " + RosterConstants.GROUP + " TEXT);");
		//		db.execSQL("CREATE INDEX idx_roster_alias ON " + TABLE_NAME + " (" + RosterConstants.ALIAS + ")");//索引
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		IMLogger.d(TAG, "onUpgrade: from " + oldVersion + " to " + newVersion);

		switch (oldVersion) {
		default:
			db.execSQL("DROP TABLE IF EXISTS " + RosterConstants.ROSTER_TABLE_NAME);
			onCreate(db);
		}
	}

}
