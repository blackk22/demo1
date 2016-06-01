package com.wonhigh.im.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wonhigh.base.util.DataBaseUpgradeUtil;
import com.wonhigh.im.util.IMLogger;

/**
 * TODO: 增加描述
 * 
 * @author USER
 * @date 2014-12-1 下午4:06:55
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public class ChatDBHelper extends SQLiteOpenHelper {

	private static final String TAG = ChatDBHelper.class.getSimpleName();

	//	public static String databaseName ;//库名
	//	
	//	public static String tableName ;//表名

	//	private static final int DATABASE_VERSION = 2;//版本	

	public ChatDBHelper(Context context) {
		super(context, ChatConstants.CHAT_DATABASE_NAME, null, ChatConstants.CHAT_DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		IMLogger.d(TAG, "creating new chat table");  

		//		db.execSQL("CREATE TABLE " + ChatConstants.CHAT_TABLE_NAME + " (" + ChatConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
		//				+ ChatConstants.ACCOUNT + " TEXT," + ChatConstants.DATE + " INTEGER," + ChatConstants.DIRECTION
		//				+ " INTEGER," + ChatConstants.JID + " TEXT," + ChatConstants.MESSAGE + " TEXT,"
		//				+ ChatConstants.DELIVERY_STATUS + " INTEGER," + ChatConstants.FILE_STATUS + " INTEGER,"
		//				+ ChatConstants.PACKET_ID + " TEXT);");

		db.execSQL("CREATE TABLE " + ChatConstants.CHAT_TABLE_NAME + " ("
		          + ChatConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
				  + ChatConstants.ACCOUNT + " TEXT," 
		          + ChatConstants.DATE + " INTEGER," 
				  + ChatConstants.DIRECTION + " INTEGER," 
		          + ChatConstants.TO_JID + " TEXT,"
				  + ChatConstants.MESSAGE + " TEXT," 
		          + ChatConstants.TYPE + " TEXT," 
				  + ChatConstants.LOCATION_URL + " TEXT," 
		          + ChatConstants.DOWNLOAD_URL + " TEXT," 
				  + ChatConstants.TO_ALIAS + " TEXT,"
				  + ChatConstants.FROM_ALIAS + " TEXT," 
				  + ChatConstants.MSG_STATUS + " INTEGER,"
				  + ChatConstants.PACKET_ID + " TEXT," 
				  + ChatConstants.BACK_UP + " TEXT,"
				  + ChatConstants.DELETE_STATUS + " INTEGER,"
				  + ChatConstants.BRAND_NAME + " TEXT"  
				  +");");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		IMLogger.d(TAG, "onUpgrade: from " + oldVersion + " to " + newVersion);
		DataBaseUpgradeUtil.upgradeDatabase(ChatDBHelper.this,db,ChatConstants.CHAT_TABLE_NAME,2,new String[]{ChatConstants.UN_DELETE+"",""});
	       

//		switch (oldVersion) {
//		case 3:
//			db.execSQL("UPDATE " + ChatConstants.CHAT_TABLE_NAME + " SET READ=1");
//		case 4:
//			db.execSQL("ALTER TABLE " + ChatConstants.CHAT_TABLE_NAME + " ADD " + ChatConstants.PACKET_ID + " TEXT");
//			break;
//		default:
//			db.execSQL("DROP TABLE IF EXISTS " + ChatConstants.CHAT_TABLE_NAME);
//			onCreate(db);
//		}
	}

}
