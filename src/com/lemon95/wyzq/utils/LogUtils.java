package com.lemon95.wyzq.utils;

import android.util.Log;

/**
 * 控制Log输出
 * @author wu
 */
public class LogUtils {
	private static boolean param = true;

	public static void i(String tag, String msg) {
		if (param) {
			Log.i(tag, msg);
		}
	}
	
	public static void e(String tag, String msg) {
		if (param) {
			Log.e(tag, msg);
		}
	}
	
	public static void d(String tag, String msg) {
		if (param) {
			Log.d(tag, msg);
		}
	}
	
	public static void v(String tag, String msg) {
		if (param) {
			Log.v(tag, msg);
		}
	}
	
	public static void w(String tag, String msg) {
		if (param) {
			Log.w(tag, msg);
		}
	}
}
