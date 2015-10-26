package com.lemon95.wyzq.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

public class NetUtil {
	/**
	 * 检查用户的网络:是否有网络
	 */
	/*public static boolean checkNet(Context context) {
		// 判断：WIFI链接
		boolean isWIFI = isWIFIConnection(context);
		// 判断：Mobile链接
		boolean isMOBILE = isMOBILEConnection(context);

		// 如果Mobile在链接，判断是哪个APN被选中了
		if (isMOBILE) {
			// APN被选中,的代理信息是否有内容，如果有wap方式
			readAPN(context);// 判断是哪个APN被选中了
		}

		if (!isWIFI && !isMOBILE) {
			return false;
		}

		return true;
	}*/
	
	/**
	 * 判断网络状态
	 */
	public static boolean isNetWorkConnected(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}


	/**
	 * 判断：Mobile链接
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isMOBILEConnection(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}

	/**
	 * 判断：WIFI链接
	 * @param context
	 * @return
	 */
	public static boolean isWIFIConnection(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}
}
