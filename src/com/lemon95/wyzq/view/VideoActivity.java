package com.lemon95.wyzq.view;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.application.ExitApplication;
import com.lemon95.wyzq.manage.MiddleManager;
import com.lemon95.wyzq.model.Channel;
import com.lemon95.wyzq.model.Result;
import com.lemon95.wyzq.receiver.ReceiveBroadUPLoadLog;
import com.lemon95.wyzq.utils.DateUtils;
import com.lemon95.wyzq.utils.LogUtils;
import com.lemon95.wyzq.webserver.WebServiceUtils;
import com.starschina.abs.media.ThinkoPlayerListener;
import com.starschina.media.ThinkoPlayerView;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class VideoActivity extends Activity{
	private final static int ISSHANG = 0;
	private final static String log = "com.lemon.log";
	private static final String TAG = VideoActivity.class.getSimpleName();
	private ThinkoPlayerView mPlayerView;
	private LoadingView mLoadingView;
	private VideoCtrlView mCtrlView;
	private Result result;
	private Channel mChannel;
	private boolean isStop = false;
	private Timer timer;
	private int tvId;
	private String xqid;
	private SharedPreferences sp;
	private LoadingView2 mLoadingView2;
	private String startDate;
	private ReceiveBroadUPLoadLog myReceiver;
	private PowerManager.WakeLock mWakeLock;
	private long waitTime = 2000;
	private long touchTime = 0;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
				case 1:
					mCtrlView.goneXuanTaiBut();
					timer.cancel();
					break;
				case ISSHANG:
					if(result != null) {
						if(result.getRows().equals("1")) {
							mLoadingView2.setText(result.getMessage());
						}
					}
					break;
			}
		};
	};
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_player);
		myReceiver = new ReceiveBroadUPLoadLog();
		IntentFilter intentFilter = new IntentFilter(log);
		registerReceiver(myReceiver, intentFilter);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		xqid = sp.getString("XQID", "");
		
		mChannel = (Channel) getIntent().getSerializableExtra("channel");
		
		mPlayerView = (ThinkoPlayerView) findViewById(R.id.player);
		mPlayerView.setPlayerListener(mListener);
		mPlayerView.setAppPackageName("com.lemon95.wyzq");
		
		//自定义loading view
		mLoadingView = new LoadingView(this);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mPlayerView.setLoadingView(mLoadingView, lp);
		
		//自定义播控界面
		mCtrlView = new VideoCtrlView(this,mPlayerView);
		mPlayerView.setMediaCtrlView(mCtrlView);
		mCtrlView.setTitle(mChannel.nickName);
		mCtrlView.setVisibility(View.INVISIBLE);
		
		mLoadingView2 = new LoadingView2(this,xqid,mChannel.videoId);
		mPlayerView.addView(mLoadingView2);
		//加载推送文字
		new Thread(){
			public void run() {
				result = WebServiceUtils.push(xqid);
				Message msg = new Message();
				msg.what = ISSHANG;
				handler.sendMessage(msg);
			};
		}.start();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if(action == MotionEvent.ACTION_DOWN) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				
				@Override
				public void run() {
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
				}
			}, 5000);
			//Toast.makeText(getApplicationContext(), "点击屏幕", 1).show();
			mCtrlView.xuanTaiBut();
			mCtrlView.hideListView();
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LogUtils.i(TAG, "开始");
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag"); 
		mPlayerView.prepareToPlay(mChannel.id);
		//注册Receiver
		startDate = DateUtils.dateTime2StringNotS(new Date());
		tvId = mChannel.videoId;
		MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mPlayerView.stop();
		if(mWakeLock != null) {
			mWakeLock.acquire(); 
			mWakeLock.release();
		}
		LogUtils.i(TAG, "停止");
		Intent intent = new Intent();
		intent.setAction(log);
		String phone = sp.getString("USERNAME", "");
		intent.putExtra("xqid", xqid);
		intent.putExtra("phone", phone);
		intent.putExtra("tvid", tvId + "");
		intent.putExtra("startDate", startDate);
		intent.putExtra("endDate", DateUtils.dateTime2StringNotS(new Date()));
		sendBroadcast(intent);
		MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(this);
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mLoadingView2.opPuse();
		mPlayerView.release();
		unregisterReceiver(myReceiver);
	}

	private ThinkoPlayerListener mListener = new ThinkoPlayerListener() {
		
		@Override
		public void onNetworkSpeedUpdate(int arg0) {
			Log.i("onNetworkSpeedUpdate", arg0 + "");
		}
		
		@Override
		public boolean onInfo(int arg0, int arg1) {
			return false;
		}
		
		@Override
		public boolean onError(int arg0, int arg1) {
			Log.e("demo", "onError[arg0:"+arg0+",arg1:"+arg1+"]");
			return false;
		}
		
		@Override
		public void onCompletion() {
			Log.i("demo", "onCompletion");
		}
		
		@Override
		public void onBuffer(float arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPrepared() {
			// TODO Auto-generated method stub
			
		}
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			//boolean result = MiddleManager.getInstance().goBack(); // 返回键操作失败
			//if (!result) { 
				long currentTime = System.currentTimeMillis();
				if ((currentTime - touchTime) >= waitTime) {
					Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
					touchTime = currentTime;
				} else {
					finish();
				}
			//}
			return false;
			//return true;
		} else if(keyCode == KeyEvent.KEYCODE_MENU) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
