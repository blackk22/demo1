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
import android.text.TextUtils;
import com.wonhigh.im.util.IMLogger;

/***
 * 
 * TODO: 聊天记录提供者
 * 
 * @author yang.dl
 * @date 2015-2-27 上午9:32:14
 * @version 1.0.0 
 * @copyright wonhigh.cn
 */
public class ChatProvider extends ContentProvider {

	protected static final String TAG = ChatProvider.class.getSimpleName();
	
	public static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

	public static final int MESSAGES = 1;

	public static final int MESSAGE_ID = 2;
	
//	public static String AUTHORITY = "com.wonhigh.im.provider.chat";
//
//	public static String TABLE_NAME = "chat";
//
//	public static String DATABASE_NAME = "chat.db";
//
//	public static Uri CHAT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

	//	static {
	//		URI_MATCHER.addURI(AUTHORITY, "chat", MESSAGES);
	//		URI_MATCHER.addURI(AUTHORITY, "chat/#", MESSAGE_ID);
	//	}

	private SQLiteOpenHelper mOpenHelper;

	@Override
	public int delete(Uri url, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (URI_MATCHER.match(url)) {

		case MESSAGES:
			count = db.delete(ChatConstants.CHAT_TABLE_NAME, where, whereArgs);
			break;
		case MESSAGE_ID:
			String segment = url.getPathSegments().get(1);

			if (TextUtils.isEmpty(where)) {
				where = "_id=" + segment;
			} else {
				where = "_id=" + segment + " AND (" + where + ")";
			}

			count = db.delete(ChatConstants.CHAT_TABLE_NAME, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Cannot delete from URL: " + url);
		}

		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	@Override
	public String getType(Uri url) {
		int match = URI_MATCHER.match(url);
		switch (match) {
		case MESSAGES:
			return ChatConstants.CONTENT_TYPE;
		case MESSAGE_ID:
			return ChatConstants.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URL");
		}
	}

	@Override
	public Uri insert(Uri url, ContentValues initialValues) {
		if (URI_MATCHER.match(url) != MESSAGES) {
			throw new IllegalArgumentException("Cannot insert into URL: " + url);
		}

		ContentValues values = (initialValues != null) ? new ContentValues(initialValues) : new ContentValues();

		for (String colName : ChatConstants.getRequiredColumns()) {
			if (values.containsKey(colName) == false) {
				throw new IllegalArgumentException("Missing column: " + colName);
			}
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		long rowId = db.insert(ChatConstants.CHAT_TABLE_NAME, ChatConstants.DATE, values);

		if (rowId < 0) {
			throw new SQLException("Failed to insert row into " + url);
		}

		Uri noteUri = ContentUris.withAppendedId(ChatConstants.CHAT_URI, rowId);
		getContext().getContentResolver().notifyChange(noteUri, null);
		return noteUri;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new ChatDBHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri url, String[] projectionIn, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
		int match = URI_MATCHER.match(url);

		switch (match) {
		case MESSAGES:
			qBuilder.setTables(ChatConstants.CHAT_TABLE_NAME);
			break;
		case MESSAGE_ID:
			qBuilder.setTables(ChatConstants.CHAT_TABLE_NAME);
			qBuilder.appendWhere("_id=");
			qBuilder.appendWhere(url.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = ChatConstants.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor ret = qBuilder.query(db, projectionIn, selection, selectionArgs, null, null, orderBy);

		if (ret == null) {
			IMLogger.d(TAG, "ChatProvider.query: failed");
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
		case MESSAGES:
			count = db.update(ChatConstants.CHAT_TABLE_NAME, values, where, whereArgs);
			break;
		case MESSAGE_ID:
			String segment = url.getPathSegments().get(1);
			rowId = Long.parseLong(segment);
			count = db.update(ChatConstants.CHAT_TABLE_NAME, values, "_id=" + rowId, null);
			break;
		default:
			throw new UnsupportedOperationException("Cannot update URL: " + url);
		}

		IMLogger.d(TAG, "*** notifyChange() rowId: " + rowId + " url " + url);

		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

}
