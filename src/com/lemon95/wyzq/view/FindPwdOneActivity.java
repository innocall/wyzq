package com.lemon95.wyzq.view;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.application.ExitApplication;
import com.lemon95.wyzq.global.ConstantValue;
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

public class FindPwdOneActivity extends ActionBarActivity implements OnClickListener{
	
	private static final String TAG = FindPwdOneActivity.class.getSimpleName();
	private static final int CHECK = 0;
	private static final int MSG = 1;
	private RelativeLayout ii_common_container;
	private RelativeLayout ii_login_title;
	private RelativeLayout ii_login_title3;
	private ImageButton top_back;
	private TextView top_title2;
	private EditText login_phone;
	private Button login_but;
	private String loginName;
	private Result result;
	private String yzmCode;
	private boolean result2;
	private String xqid = "";
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case CHECK:
					if(result != null){
						if(Integer.parseInt(result.getRows()) > 0) {
							Map<String,String> list = result.getTab1();
							if(list != null && list.size() > 0) {
								//是业主 ，可以发送验证码
								xqid = list.get("xqid");
								yzmCode = RandomSecquenceCreator.getRandomCode(5); //生成随机数
								String str = yzmCode + " 天健E生活验证码。【柠檬95】";
								LogUtils.i(TAG, str);
								sendYzm(loginName,str);
							} else {
								PromptManager.closeProgressDialog();
								PromptManager.showToast(getApplicationContext(), "该手机号未注册");
							}
						} else {
							PromptManager.closeProgressDialog();
							PromptManager.showToast(getApplicationContext(), "该手机号未注册");
						}
					} else {
						PromptManager.closeProgressDialog();
						PromptManager.showToast(getApplicationContext(), "服务器异常请稍候再试");
					}
					break;
				case MSG:
					if(result2) {
						PromptManager.closeProgressDialog();
						PromptManager.showToast(getApplicationContext(), "验证码发送成功");
						//进入下一页,输入验证码和重置密码
						Intent intent = new Intent(FindPwdOneActivity.this,FindPwdTwoActivity.class);
						intent.putExtra("xqid", xqid);
						intent.putExtra("yzmCode", yzmCode);
						intent.putExtra("loginName", loginName);
						startActivity(intent);
						overridePendingTransition(R.anim.left_in, R.anim.left_up);
					} else {
						PromptManager.closeProgressDialog();
						PromptManager.showToast(getApplicationContext(), "验证码发送失败");
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
		setContentView(R.layout.activity_findpwd_one);
		ExitApplication.getInstance().addActivity(this);
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
		login_phone = (EditText) findViewById(R.id.login_phone);
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
				loginName = login_phone.getText().toString().trim();
				if(StringUtils.isBlank(loginName)){
					PromptManager.showToast(getApplicationContext(), "请输入您的手机号");
					return;
				}
				if(!StringUtil.isPhone(loginName)) {
					PromptManager.showToast(getApplicationContext(), "请输入正确格式的手机号");
					return;
				}
				checkPhone(loginName);
				break;
		}
	}
	
	/**
	 * 发送验证码
	 * @param phone
	 */
	private void sendYzm(final String phone,final String yzm) {
		new Thread(){
			@Override
			public void run() {
				super.run();
				result2 = WebServiceUtils.senYzm(phone,yzm);
				Message msg = new Message();
				msg.what = MSG;
				handler.sendMessage(msg);
			}
		}.start();
	}

	private void checkPhone(final String loginName2) {
		PromptManager.showProgressDialog(this, "获取中，请稍候...");
		new Thread(){
			public void run() {
				result = WebServiceUtils.info(loginName2);
				Message msg = new Message();
				msg.what = CHECK;
				handler.sendMessage(msg);
			};
		}.start();
	}
}
