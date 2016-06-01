package com.wonhigh.im.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import com.wonhigh.im.util.IMLogger;

/**
 * 好友花名册
 * 
 * @author USER
 * @date 2014-11-24 下午12:18:29
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public class RosterProvider extends ContentProvider {

	private static final String TAG = RosterProvider.class.getSimpleName();

	//	public static  String AUTHORITY = "com.wonhigh.im.provider.roster";
	//
	//	public static  String TABLE_NAME = "roster";
	//	
	//	public static String DATABASE_NAME = "roster.db";
	//
	//	public static  Uri ROSTER_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

	public static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

	public static final int ROSTER = 1;//查询所有

	public static final int ROSTER_ID = 2;//匹配单个

	private SQLiteOpenHelper mOpenHelper;

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		int count;
		switch (URI_MATCHER.match(uri)) {

		case ROSTER:
			count = db.delete(RosterConstants.ROSTER_TABLE_NAME, where, whereArgs);
			break;

		case ROSTER_ID:
			String segment = uri.getPathSegments().get(1);

			if (TextUtils.isEmpty(where)) {
				where = "_id=" + segment;
			} else {
				where = "_id=" + segment + " AND (" + where + ")";
			}

			count = db.delete(RosterConstants.ROSTER_TABLE_NAME, where, whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Cannot delete from URL: " + uri);
		}

		getContext().getContentResolver().notifyChange(RosterConstants.ROSTER_URI, null);
		notifyChange();

		return count;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		int match = URI_MATCHER.match(uri);
		switch (match) {
		case ROSTER:
			return RosterConstants.CONTENT_TYPE;
		case ROSTER_ID:
			return RosterConstants.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URL");
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// TODO Auto-generated method stub
		if (URI_MATCHER.match(uri) != ROSTER) {
			throw new IllegalArgumentException("不能插入该uri: " + uri);
		}

		ContentValues values = (initialValues != null) ? new ContentValues(initialValues) : new ContentValues();

		for (String colName : RosterConstants.getRequiredColumns()) {
			if (values.containsKey(colName) == false) {
				throw new IllegalArgumentException("没有这一列: " + colName);
			}
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		long rowId = db.insert(RosterConstants.ROSTER_TABLE_NAME, RosterConstants.JID, values);

		if (rowId < 0) {
			throw new SQLException("插入数据失败 " + uri);
		}

		Uri noteUri = ContentUris.withAppendedId(RosterConstants.ROSTER_URI, rowId);

		notifyChange();

		return noteUri;

	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		mOpenHelper = new RosterDBHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri url, String[] projectionIn, String selection, String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
		int match = URI_MATCHER.match(url);
		String groupBy = null;

		switch (match) {
		case ROSTER:
			qBuilder.setTables(RosterConstants.ROSTER_TABLE_NAME);
			break;

		case ROSTER_ID:
			qBuilder.setTables(RosterConstants.ROSTER_TABLE_NAME);
			qBuilder.appendWhere("_id=");
			qBuilder.appendWhere(url.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("未知URI " + url);
		}

		String orderBy;
		if (TextUtils.isEmpty(sortOrder) && match == ROSTER) {
			orderBy = RosterConstants.DEFAULT_SORT_ORDER;// 默认按在线状态排序
		} else {
			orderBy = sortOrder;
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor ret = qBuilder.query(db, projectionIn, selection, selectionArgs, groupBy, null, orderBy);

		if (ret == null) {
			infoLog("花名册查询失败");
		} else {
			ret.setNotificationUri(getContext().getContentResolver(), url);
		}

		return ret;
	}

	@Override
	public int update(Uri url, ContentValues values, String where, String[] whereArgs) {
		int count;
		long rowId = 0;
		int match = URI_MATCHER.match(url);
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		switch (match) {
		case ROSTER:
			count = db.update(RosterConstants.ROSTER_TABLE_NAME, values, where, whereArgs);
			break;
		case ROSTER_ID:
			String segment = url.getPathSegments().get(1);
			rowId = Long.parseLong(segment);
			count = db.update(RosterConstants.ROSTER_TABLE_NAME, values, "_id=" + rowId, whereArgs);
			break;
		default:
			throw new UnsupportedOperationException("不能更新该 URI: " + url);
		}

		notifyChange();

		return count;

	}

	/***************************************************************************/

	private Handler mNotifyHandler = new Handler();

	long last_notify = 0;

	private void notifyChange() {
		mNotifyHandler.removeCallbacks(mNotifyChange);
		long ts = System.currentTimeMillis();
		if (ts > last_notify + 500) {
			mNotifyChange.run();
		} else {
			mNotifyHandler.postDelayed(mNotifyChange, 200);
		}
		last_notify = ts;
	}

	private Runnable mNotifyChange = new Runnable() {
		public void run() {
			IMLogger.d(TAG, "花名册有更新");
			getContext().getContentResolver().notifyChange(RosterConstants.ROSTER_URI, null);
		}
	};

	private static void infoLog(String data) {
		IMLogger.d(TAG, data);
	}

}
