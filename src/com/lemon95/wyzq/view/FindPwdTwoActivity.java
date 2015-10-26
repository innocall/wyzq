package com.lemon95.wyzq.view;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.application.ExitApplication;
import com.lemon95.wyzq.model.Result;
import com.lemon95.wyzq.utils.LogUtils;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.utils.RandomSecquenceCreator;
import com.lemon95.wyzq.utils.StringUtil;
import com.lemon95.wyzq.webserver.WebServiceUtils;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FindPwdTwoActivity extends ActionBarActivity implements OnClickListener{
	private static final String TAG = FindPwdTwoActivity.class.getSimpleName();
	private static final int CHECK = 0;
	private RelativeLayout ii_common_container;
	private RelativeLayout ii_login_title;
	private RelativeLayout ii_login_title3;
	private ImageButton top_back;
	private TextView top_title2;
	private EditText pwd;
	private EditText pwd2;
	private EditText yzm;
	private Button login_but;
	private String loginName;
	private Result result;
	private String yzmCode;
	private String xqid = "";
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case CHECK:
					PromptManager.closeProgressDialog();
					if(result != null) {
						if(result.getRows().equals("1")) {
							PromptManager.showToast(getApplicationContext(), "重置密码成功");
							Intent intent = new Intent(FindPwdTwoActivity.this,LoginActivity.class);
							startActivity(intent);
							finish();
							overridePendingTransition(R.anim.right_in, R.anim.right_out);
						}
					}
					PromptManager.showToast(getApplicationContext(), "重置密码失败");
					break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		setContentView(R.layout.activity_findpwd_two);
		ExitApplication.getInstance().addActivity(this);
		loginName = getIntent().getStringExtra("loginName");
		yzmCode = getIntent().getStringExtra("yzmCode");
		xqid = getIntent().getStringExtra("xqid");
		initView();
		setListener();
	}
	
	private void setListener() {
		top_back.setOnClickListener(this);
		login_but.setOnClickListener(this);
	}

	private void initView() {
		ii_common_container = (RelativeLayout) findViewById(R.id.ii_common_container);
		ii_login_title = (RelativeLayout) findViewById(R.id.ii_login_title);
		ii_login_title3 = (RelativeLayout) findViewById(R.id.ii_login_title3);
		pwd = (EditText) findViewById(R.id.pwd);
		pwd2 = (EditText) findViewById(R.id.pwd2);
		yzm = (EditText) findViewById(R.id.yzm);
		login_but = (Button) findViewById(R.id.login_but);
		top_back = (ImageButton) findViewById(R.id.top_back);
		top_title2 = (TextView) findViewById(R.id.top_title2);
		ii_common_container.setVisibility(View.GONE);
		ii_login_title.setVisibility(View.VISIBLE);
		ii_login_title3.setVisibility(View.GONE);
		top_title2.setText("重置密码");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
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

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.top_back:
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				break;
			case R.id.login_but:
				String yanzhengma = yzm.getText().toString().trim();
				if(StringUtils.isBlank(yanzhengma)) {
					PromptManager.showToast(getApplicationContext(), "请输入您的验证码");
					return;
				}
				if(!yanzhengma.equals(yzmCode)) {
					PromptManager.showToast(getApplicationContext(), "验证码错误");
					return;
				}
				String password = pwd.getText().toString().trim();
				if(StringUtils.isBlank(password)){
					PromptManager.showToast(getApplicationContext(), "请设置您的密码");
					return;
				}
				if(password.length() < 6) {
					PromptManager.showToast(getApplicationContext(), "密码不能小于6位字符");
					return;
				}
				String password2 = pwd2.getText().toString().trim();
				if(StringUtils.isBlank(password2)){
					PromptManager.showToast(getApplicationContext(), "请设置您的确认密码");
					return;
				}
				if(!password.equals(password2)) {
					PromptManager.showToast(getApplicationContext(), "2次密码不一致");
					return;
				}
				setPwd(password);
				break;
		}
	}
	

	private void setPwd(final String password) {
		PromptManager.showProgressDialog(this, "设置中，请稍候...");
		new Thread(){
			public void run() {
				result = WebServiceUtils.editpassword(loginName,password,xqid);
				Message msg = new Message();
				msg.what = CHECK;
				handler.sendMessage(msg);
			};
		}.start();
	}

}
