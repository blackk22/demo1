package com.wonhigh.base.util;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * TODO: 增加描述
 * 更新数据库操作类。
 * @author user
 * @date 2015-7-10 上午10:33:58
 * @version 1.0.0
 * @copyright wonhigh.cn
 */
public class DataBaseUpgradeUtil {
	/**
	 * 
	 * @param helper:数据库的SQLiteOpenHelper
	 * @param db:数据库SqliteDatabase
	 * @param tableName:需要升级的表
	 * @param addKeySize：需要增加字段的个数
	 * @param fieldValue：需要增加字段的默认值
	 */
	public static void upgradeDatabase(SQLiteOpenHelper helper, SQLiteDatabase db, String tableName, int addKeySize,
			String[] fieldValue) {
		String TEMP_TABLE_NAME = "temp_" + tableName;
		StringBuffer buffer = new StringBuffer();
		int fieldValueSize = fieldValue.length;
		if (addKeySize > 0) {
			for (int i = 0; i < addKeySize; i++) {
				if (i + 1 > fieldValueSize) {
					buffer.append(",' '");
				} else {
					buffer.append(", '" + fieldValue[i] + "'");
				}
			}
			db.execSQL("alter table " + tableName + " rename to " + TEMP_TABLE_NAME);
			helper.onCreate(db);
			db.execSQL("insert into " + tableName + " select * " + buffer.toString() + "from " + TEMP_TABLE_NAME);
			db.execSQL("drop table " + TEMP_TABLE_NAME);
		} else {

		}
	}
}
