package com.wonhigh.im.util;

import android.util.Log;

import com.wonhigh.im.BuildConfig;

/**
 * @ClassName: Logger
 * @Description: TODO(日志工具类)
 * @Author: 王富彬
 * @Date: 2014-2-19 下午10:24:00
 */
public class IMLogger {
	private final static String TAG = "IMLogger";

	public static boolean isDebug = BuildConfig.DEBUG;

	private static int LOGLEVEL = 6;

	private static int VERBISE = 1;

	private static int DEBUG = 2;

	private static int INFO = 3;

	private static int WARN = 4;

	private static int ERROR = 5;


	public static void v(String tag, String msg) {
		if (LOGLEVEL > VERBISE && isDebug) {
			Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (LOGLEVEL > DEBUG && isDebug) {
			Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (LOGLEVEL > INFO && isDebug) {
			Log.i(tag, msg);
		}
	}

	public static void i(String tag, String field, String msg) {
		if (LOGLEVEL > INFO && isDebug) {
			Log.i(tag, field + " = " + msg);
		}
	}

	public static void w(String tag, String msg) {
		if (LOGLEVEL > WARN && isDebug) {
			Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (LOGLEVEL > ERROR && isDebug) {
			Log.e(tag, msg);
		}
	}

	public static void e(String tag, String field, String msg) {
		if (LOGLEVEL > INFO && isDebug) {
			Log.e(tag, field + " = " + msg);
		}
	}

	public static void i(String msg) {
		if (isDebug) {
			Log.i(TAG, msg);
		}
	}

	public static void d(String msg) {
		if (isDebug) {
			Log.d(TAG, msg);
		}
	}

	public static void e(String msg) {
		if (isDebug) {
			Log.e(TAG, msg);
		}
	}

	public static void w(String msg) {
		if (isDebug) {
			Log.w(TAG, msg);
		}
	}

	public static void v(String msg) {
		if (isDebug) {
			Log.v(TAG, msg);
		}
	}


}
