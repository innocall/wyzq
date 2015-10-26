package com.lemon95.wyzq.receiver;


import com.lemon95.wyzq.webserver.WebServiceUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 关闭视频时上传日志
 * @author wu
 *
 */
public class ReceiveBroadUPLoadLog extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		 /* 获取Intent对象中的数据 */
	     String phone = intent.getStringExtra("phone");
	     String xqid = intent.getStringExtra("xqid");
	     String tvid = intent.getStringExtra("tvid");
	     String startDate = intent.getStringExtra("startDate");
	     String endDate = intent.getStringExtra("endDate");
	     upLoadLog(phone,xqid,tvid,startDate,endDate);
	}

	private void upLoadLog(final String phone, final String xqid, final String tvid, final String startDate, final String endDate) {
		new Thread() {
			public void run() {
				WebServiceUtils.tvlog(phone,xqid,tvid,startDate,endDate);
			};
		}.start();
	}

}
