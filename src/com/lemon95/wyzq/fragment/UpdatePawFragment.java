package com.lemon95.wyzq.fragment;

import org.apache.commons.lang3.StringUtils;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.manage.MiddleManager;
import com.lemon95.wyzq.model.Result;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.view.BaseUI;
import com.lemon95.wyzq.webserver.WebServiceUtils;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class UpdatePawFragment extends BaseUI {
	
	private static final String TAG = UpdatePawFragment.class.getSimpleName();
	private final static int LOGIN = 0;
	private final static int UPDATE = 1;
	private EditText pwd;
	private EditText login_password;
	private EditText login_password2;
	private Button login_but;
	private SharedPreferences sp;
	private String phone;
	private String xqid;
	private Result result;
	private Result result2;
	private String password;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
				case LOGIN:
					if(result != null) {
						if(result.getRows().equals("0")){
							PromptManager.showToast(context, "原密码错误");
						} else {
							updatePwd();
						}
					} else {
						PromptManager.showToast(context, "服务器异常");
					}
					break;
				case UPDATE:
					if(result != null) {
						if(result.getRows().equals("1")){
							PromptManager.showToast(context, "修改密码成功,请重新登录");
							Intent intent = new Intent();
							intent.setClassName("com.lemon95.wyzq","com.lemon95.wyzq.view.LoginActivity");
							MiddleManager.getInstance().activity.startActivity(intent);
							MiddleManager.getInstance().clear();
						} else {
							PromptManager.showToast(context, result.getMessage());
						}
					} else {
						PromptManager.showToast(context, "服务器异常");
					}
					break;
			}
		};
	};
	
	public UpdatePawFragment(Context context) {
		super(context);
	}

	@Override
	public void init() {
		showInMiddle = (LinearLayout) View.inflate(context, R.layout.pwd_fragment, null);
		pwd = (EditText) findViewById(R.id.pwd);
		login_password = (EditText) findViewById(R.id.login_password);
		login_password2 = (EditText) findViewById(R.id.login_password2);
		login_but = (Button) findViewById(R.id.login_but);
		sp = context.getSharedPreferences(ConstantValue.CONFIG, Context.MODE_PRIVATE);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		phone = sp.getString("USERNAME", "");
		xqid = sp.getString("XQID", "");
		MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(context);
	}
	
	@Override
	public void setListener() {
		login_but.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch(v.getId()) {
			case R.id.login_but:
				String new_password = pwd.getText().toString().trim();
				if(StringUtils.isBlank(new_password)) {
					PromptManager.showToast(context, "请输入您的原密码");
					return;
				}
				password = login_password.getText().toString().trim();
				if(StringUtils.isBlank(password)) {
					PromptManager.showToast(context, "请输入您的新密码");
					return;
				}
				if(password.length() < 6) {
					PromptManager.showToast(context, "新密码不能小于6为");
					return;
				}
				String password2 = login_password2.getText().toString().trim();
				if(StringUtils.isBlank(password2)) {
					PromptManager.showToast(context, "请输入您的确认密码");
					return;
				}
				if(!password.equals(password2)) {
					PromptManager.showToast(context, "2次密码不一致");
					return;
				}
				checkPassword(new_password);
				break;
		}
	}

	private void checkPassword(final String new_password) {
		new Thread() {
			public void run() {
				result = WebServiceUtils.login(phone, new_password);
				Message msg = new Message();
				msg.what = LOGIN;
				handler.sendMessage(msg);
			};
		}.start();
	}
	
	protected void updatePwd() {
		new Thread(){
			public void run() {
				result2 = WebServiceUtils.editpassword(phone,password,xqid);
				Message msg = new Message();
				msg.what = UPDATE;
				handler.sendMessage(msg);
			};
		}.start();
	}

	@Override
	public int getID() {
		return ConstantValue.UPDATEPAWFRAGMENT;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(context);
	}

}
