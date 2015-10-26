package com.lemon95.wyzq.view;

import java.util.Timer;
import java.util.TimerTask;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.model.Channel;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class AdImageView extends ActionBarActivity {
	
	private final static String TAG = AdImageView.class.getSimpleName();
	private TextView ad_tv;
	private Timer timer;
	private int time = 5;
	private Channel mChannel;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				time -= 1;
				if (time == 0) {
					stopTime();
				} else {
					ad_tv.setText(time + "秒");
				}
				break;
			}
		};
	};
	
	public void stopTime() {
		ad_tv.setText(time + "秒");
		timer.cancel();
		Intent i = new Intent(this ,VideoActivity.class);
		i.putExtra("channel", mChannel);
		startActivity(i);
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		finish();
		time = 5;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
	    getActionBar().hide();
		setContentView(R.layout.activity_ad_image);
		ad_tv = (TextView) findViewById(R.id.ad_tv);
		mChannel = (Channel) getIntent().getSerializableExtra("channel");
		timer = new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			}
			
		}, 1000, 1000);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(this);
	}
	
}
