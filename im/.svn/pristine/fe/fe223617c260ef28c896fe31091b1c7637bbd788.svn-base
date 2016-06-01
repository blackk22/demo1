
package com.wonhigh.im.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 
 * @类名: PreferenceUtils
 * @描述: TODO(这里用一句话描述这个类的作用)
 * @作者: du.xg
 * @日期: 2014-6-19 上午11:50:18
 * @修改人:
 * @修改时间: 2014-6-19 上午11:50:18
 * @修改内容:
 * @版本: V1.0 
 * @版权:Copyright © 2014 wonhigh. All rights reserved.
 */
public class IMPreferenceUtils {
    public static String getPrefString(Context context, String key,
        final String defaultValue) {
        final SharedPreferences settings =
            PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(key, defaultValue);
    }
    
    public static void setPrefString(Context context, final String key,
        final String value) {
        final SharedPreferences settings =
            PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putString(key, value).commit();
    }
    
    public static boolean getPrefBoolean(Context context, final String key,
        final boolean defaultValue) {
        final SharedPreferences settings =
            PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(key, defaultValue);
    }
    
    public static boolean hasKey(Context context, final String key) {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .contains(key);
    }
    
    public static void setPrefBoolean(Context context, final String key,
        final boolean value) {
        final SharedPreferences settings =
            PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putBoolean(key, value).commit();
    }
    
    public static void setPrefInt(Context context, final String key,
        final int value) {
        final SharedPreferences settings =
            PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putInt(key, value).commit();
    }
    
    public static int getPrefInt(Context context, final String key,
        final int defaultValue) {
        final SharedPreferences settings =
            PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getInt(key, defaultValue);
    }
    
    public static void setPrefFloat(Context context, final String key,
        final float value) {
        final SharedPreferences settings =
            PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putFloat(key, value).commit();
    }
    
    public static float getPrefFloat(Context context, final String key,
        final float defaultValue) {
        final SharedPreferences settings =
            PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getFloat(key, defaultValue);
    }
    
    public static void setSettingLong(Context context, final String key,
        final long value) {
        final SharedPreferences settings =
            PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putLong(key, value).commit();
    }
    
    public static long getPrefLong(Context context, final String key,
        final long defaultValue) {
        final SharedPreferences settings =
            PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getLong(key, defaultValue);
    }
    
    public static void clearPreference(Context context,
        final SharedPreferences p) {
        final Editor editor = p.edit();
        editor.clear();
        editor.commit();
    }
}
