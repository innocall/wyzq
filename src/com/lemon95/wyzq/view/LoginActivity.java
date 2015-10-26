package com.lemon95.wyzq.view;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.application.ExitApplication;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.manage.MiddleManager;
import com.lemon95.wyzq.model.Result;
import com.lemon95.wyzq.utils.DES;
import com.lemon95.wyzq.utils.NetUtil;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.webserver.WebServiceUtils;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity implements OnClickListener{
	
	private final static String TAG = LoginActivity.class.getSimpleName();
	private final static int LOGIN = 0;
	private EditText login_phone;
	private EditText login_password;
	private Button login_but;
	private Button login_rig_but;
	private Button login_rig_visitor;
	private Result result;
	private TextView wangjipaw;
	private SharedPreferences sp;
	private long waitTime = 2000;
	private long touchTime = 0;
	private String passWord;
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
				case LOGIN:
					PromptManager.closeProgressDialog();
					if(result != null) {
						if(result.getRows().equals("0")){
							PromptManager.showToast(getApplicationContext(), result.getMessage());
						} else {
							toLogin();
						}
					} else {
						PromptManager.showToast(getApplicationContext(), "服务器异常");
					}
					break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		setContentView(R.layout.activity_login);
		sp = getSharedPreferences(ConstantValue.CONFIG, MODE_PRIVATE);
		ExitApplication.getInstance().addActivity(this);
		initView();
		setListener();
	}
	
	private void initView() {
		login_phone = (EditText) findViewById(R.id.login_phone);
		login_password = (EditText) findViewById(R.id.login_password);
		login_but = (Button) findViewById(R.id.login_but);
		login_rig_but = (Button) findViewById(R.id.login_rig_but);
		wangjipaw = (TextView) findViewById(R.id.wangjipaw);
		login_rig_visitor = (Button) findViewById(R.id.login_rig_visitor);
		String mobile = sp.getString("USERNAME", "");
		login_phone.setText(mobile);
	}
	
	private void setListener() {
		login_but.setOnClickListener(this);
		login_rig_but.setOnClickListener(this);
		login_rig_visitor.setOnClickListener(this);
		wangjipaw.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.login_but:
				login();
				break;
			case R.id.login_rig_but:
				//PromptManager.showToast(getApplicationContext(), "注册");
				Intent intent = new Intent(this,RigisterOneActivity.class);
				//Intent intent = new Intent(this,RigisterActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.left_up);
				break;
			case R.id.login_rig_visitor:
				//PromptManager.showToast(getApplicationContext(), "游客");
				Intent intent2 = new Intent(this,YouKeRigisterOneActivity.class);
				startActivity(intent2);
				overridePendingTransition(R.anim.left_in, R.anim.left_up);
				break;
			case R.id.wangjipaw:
				Intent intent3 = new Intent(this,FindPwdOneActivity.class);
				startActivity(intent3);
				overridePendingTransition(R.anim.left_in, R.anim.left_up);
				break;
		}
	}
	
	/**
	 * 登录
	 */
	private void login() {
		MobclickAgent.onEvent(this,"login");
		if (NetUtil.isNetWorkConnected(getApplicationContext())) {
			final String loginName = login_phone.getText().toString().trim();
			passWord = login_password.getText().toString().trim();
			if(StringUtils.isBlank(loginName)) {
				PromptManager.showToast(getApplicationContext(), "请输入登录手机号");
				return;
			}
			if(StringUtils.isBlank(passWord)) {
				PromptManager.showToast(getApplicationContext(), "请输入登录密码");
				return;
			}
			PromptManager.showProgressDialog(this, "登录中...");
			new Thread() {
				public void run() {
					result = WebServiceUtils.login(loginName, passWord);
					Message msg = new Message();
					msg.what = LOGIN;
					handler.sendMessage(msg);
				};
			}.start();
		} else {
			PromptManager.showNoNetWork(LoginActivity.this);
		}
	}
	
	protected void toLogin() {
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - touchTime) >= waitTime) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				touchTime = currentTime;
			} else {
				MobclickAgent.onKillProcess(this);
				ExitApplication.getInstance().exit();
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onResume() {
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

}
