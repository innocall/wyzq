package com.lemon95.wyzq.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppSystemUtils {

	// 版本�?
	public static String getVersionName(Context context) {
		return getPackageInfo(context).versionName;
	}

	// 版本�?
	public static int getVersionCode(Context context) {
		return getPackageInfo(context).versionCode;
	}

	private static PackageInfo getPackageInfo(Context context) {
		PackageInfo pi = null;
		try {
			PackageManager pm = context.getPackageManager();
			pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_CONFIGURATIONS);
			return pi;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pi;
	}

}
