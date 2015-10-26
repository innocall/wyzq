package com.lemon95.wyzq;

import android.support.v7.app.ActionBarActivity;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.model.Result;
import com.lemon95.wyzq.utils.AppSystemUtils;
import com.lemon95.wyzq.utils.DES;
import com.lemon95.wyzq.utils.NetUtil;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.view.LoginActivity;
import com.lemon95.wyzq.view.MainActivity;
import com.lemon95.wyzq.view.SplashLeadActivity;
import com.lemon95.wyzq.webserver.WebServiceUtils;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.WindowManager;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

public class SplashActivity extends ActionBarActivity {
	private final static int TOLOGIN = 0;
	private final static int UPDATE = 1;
	private final static int LOGIN = 3;
	private final static int SPLASH = 2000;
	private static final String TAG = SplashActivity.class.getSimpleName();
	private SharedPreferences sp;
	private String splash;
	private Result result;
	private String loginName;
	private String passWord;
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case TOLOGIN:
					toLogin();
					break;
				case UPDATE:
					if(result != null) {
						if(result.getRows().equals("0")) {
							toLogin();
						} else {
							alertUpdate();
						}
					} else {
						toLogin();
					}
					break;
				case LOGIN:
					if(result != null) {
						if(result.getRows().equals("0")){
							toMain();
						} else {
							toLogin1();
						}
					} else {
						toMain();
					}
					break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		initUmEng();  //初始化友盟sdk
		sp = getSharedPreferences(ConstantValue.CONFIG, MODE_PRIVATE);
		splash = sp.getString("version", "");
		if (NetUtil.isNetWorkConnected(getApplicationContext())) {
			// splash 做一个动画,进入主界面
			new Handler().postDelayed(new LoadMainTabTask2(), SPLASH - 500);
		} else {
			// splash 做一个动画,进入主界面
			new Handler().postDelayed(new LoadMainTabTask(), SPLASH);
		}
	}
	
	private class LoadMainTabTask implements Runnable {

		public void run() {
			Message msg = new Message();
			msg.what = TOLOGIN;
			handler.sendMessage(msg);
		}
	}
	
	private class LoadMainTabTask2 implements Runnable {

		public void run() {
			isUpdate();
		}
	}
	
	//直接去登录页面
	public void toLogin(){
		if(!getVersion().equals(splash)) {
			Intent intent = new Intent(SplashActivity.this, SplashLeadActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.left_up);
		} else {
			//自动登陆
			loginName = sp.getString("USERNAME", "");
			passWord = DES.decrypt(sp.getString("PASSWORD", ""), ConstantValue.KEY);
			login();
			/*Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.left_up);*/
		}
	}
	
	public void toMain() {
		Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_up);
	}

	private void initUmEng() {
		MobclickAgent.updateOnlineConfig(this);
		/** 设置是否对日志信息进行加密, 默认false(不加密)*/
		AnalyticsConfig.enableEncrypt(true);
		MobclickAgent.setDebugMode(true);  //正式发布时关闭
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onRestart() {
		MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this);
		super.onRestart();
	}
	
	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
		super.onPause();
	}
	
	private String getVersion() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "Version";
		}
	}
	
	private void alertUpdate() {
		//有新版本
		AlertDialog.Builder builder = new Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.app_name)
				.setMessage(result.getMessage())
				.setPositiveButton("更新", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						downLoadApk(result.getUrl());
					}
				}).setNegativeButton("不更新", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						toLogin();
					}
				} ).show();
	};
	
	private void isUpdate() {
		final String versionName = AppSystemUtils.getVersionName(getApplicationContext());
		new Thread(){
			@Override
			public void run() {
				super.run();
				result = WebServiceUtils.appv(versionName);
				Message msg = new Message();
				msg.what = UPDATE;
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	/**
	 * 下载apk文件
	 */
	protected void downLoadApk(String url) {
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setTitle("正在下载");
		pd.setCancelable(false);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		FinalHttp fh = new FinalHttp();
		fh.download(url,"/sdcard/lemon.apk", new AjaxCallBack<File>() {
			@Override
			public void onSuccess(File t) {
				pd.dismiss();
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setDataAndType(Uri.fromFile(t),
						"application/vnd.android.package-archive");
				startActivity(intent);
				finish();
				super.onSuccess(t);
			}

			@Override
			public void onLoading(long count, long current) {
				pd.setMax((int) count);
				pd.setProgress((int) (current));
				super.onLoading(count, current);
			}
		});
	}
	
	/**
	 * 登录
	 */
	private void login() {
		new Thread() {
			public void run() {
				result = WebServiceUtils.login(loginName, passWord);
				Message msg = new Message();
				msg.what = LOGIN;
				handler.sendMessage(msg);
			};
		}.start();
	}
	
	protected void toLogin1() {
		Map<String,String> map = result.getTab1();
		Editor ed = sp.edit();
		if(!StringUtils.isBlank(map.get("mobile"))) {
			ed.putString("USERNAME", map.get("mobile"));
		}
		if(!StringUtils.isBlank(map.get("grade"))) {
			ed.putString("GRADE", map.get("grade"));
		}
		if(!StringUtils.isBlank(map.get("created"))) {
			ed.putString("CREATED", map.get("created"));
		}
		if(!StringUtils.isBlank(map.get("xqid"))) {
			ed.putString("XQID", map.get("xqid"));
		}
		if(!StringUtils.isBlank(map.get("islive"))) {
			ed.putString("ISLIVE", map.get("islive"));
		}
		if(!StringUtils.isBlank(map.get("remobile"))) {
			ed.putString("REMOBILE", map.get("remobile"));
		}
		if(!StringUtils.isBlank(map.get("fanghao"))) {
			ed.putString("FANGHAO", map.get("fanghao"));
		}
		if(!StringUtils.isBlank(map.get("bianma"))) {
			ed.putString("BIANMA", map.get("bianma"));
		}
		if(!StringUtils.isBlank(map.get("rowId"))) {
			ed.putString("ROWID", map.get("rowId"));
		}
		if(!StringUtils.isBlank(map.get("realname"))) {
			ed.putString("REALNAME", map.get("realname"));
		}
		if(!StringUtils.isBlank(passWord)) {
			ed.putString("PASSWORD", DES.encrypt(passWord, ConstantValue.KEY));
		}
		ed.commit();
		Intent intent = new Intent(this,MainActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_up);
	}
}
