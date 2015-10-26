package com.lemon95.wyzq.view;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
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
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RigisterThreeActivity extends ActionBarActivity implements OnClickListener{
	
	private static final String TAG = RigisterThreeActivity.class.getSimpleName();
	private static final int CHECKPHONE = 0;
	private static final int MSG = 1;
	private static final int DUANXIN = 2;
	private static final int RIGISTER = 3;
	private String type;
	private String xiaoId;
	private String fangId;
	private String city;
	private RelativeLayout ii_common_container;
	private RelativeLayout ii_login_title;
	private RelativeLayout ii_login_title3;
	private ImageButton top_back;
	private TextView top_title2;
	private EditText login_phone,login_yzm,login_password,login_password2;
	private Button login_rig_visitor;
	private Button login_but;
	private String yzmCode = "";
	private String phone;
	private Result result;
	private boolean result2;
	private Timer timer;
	private int time = 120;
	private Result r;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case CHECKPHONE:
					if(result != null){
						if(Integer.parseInt(result.getRows()) > 0) {
							Map<String,String> list = result.getTab1();
							if(list != null && list.get("grade").equals(ConstantValue.YEZHU)) {
								if(list.get("xqid").equals(xiaoId)) {
									if(list.get("fanghao").equals(fangId)) {
										//是业主 ，可以发送验证码
										yzmCode = RandomSecquenceCreator.getRandomCode(5); //生成随机数
										String str = yzmCode + " 天健E生活验证码。【柠檬95】";
										LogUtils.i(TAG, str);
										sendYzm(phone,str);
									} else {
										PromptManager.showToast(getApplicationContext(), "您选择的小区房号和物业录入的不一致，请联系物业解决");
									}
								} else {
									PromptManager.showToast(getApplicationContext(), "您选择的小区名称和物业录入的不一致，请联系物业解决");
								}
							} else {
								PromptManager.showToast(getApplicationContext(), "您的信息不在库中，请和物业联系");
							}
						} else {
							PromptManager.showToast(getApplicationContext(), "您的信息不在库中，请和物业联系");
						}
					} else {
						PromptManager.showToast(getApplicationContext(), "服务器异常请稍候再试");
					}
					break;
				case MSG:
					if(result2) {
						startTime();
						PromptManager.showToast(getApplicationContext(), "验证码发送成功");
					} else {
						PromptManager.showToast(getApplicationContext(), "验证码发送失败");
					}
					break;
				case DUANXIN:
					time -= 1;
					if (time == 0) {
						stopTime();
					} else {
						login_rig_visitor.setText(time + "秒");
					}
					break;
				case RIGISTER:
					PromptManager.closeProgressDialog();
					if(r != null) {
						if(r.getRows().equals("1")) {
							PromptManager.showToast(getApplicationContext(), "注册成功");
							Intent intent = new Intent(RigisterThreeActivity.this,LoginActivity.class);
							startActivity(intent);
							finish();
							overridePendingTransition(R.anim.right_in, R.anim.right_out);
						} else {
							PromptManager.showToast(getApplicationContext(), r.getMessage());
						}
					} else {
						PromptManager.showToast(getApplicationContext(), "注册失败");
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
		setContentView(R.layout.activity_rigister_three);
		type = getIntent().getStringExtra("type");
		xiaoId = getIntent().getStringExtra("xiaoId");
		fangId = getIntent().getStringExtra("fangId");
		city = getIntent().getStringExtra("city");
		ExitApplication.getInstance().addActivity(this);
		initView();
		setListener();
	}
	
	private void setListener() {
		top_back.setOnClickListener(this);
		login_rig_visitor.setOnClickListener(this);
		login_but.setOnClickListener(this);
	}

	private void initView() {
		ii_common_container = (RelativeLayout) findViewById(R.id.ii_common_container);
		ii_login_title = (RelativeLayout) findViewById(R.id.ii_login_title);
		ii_login_title3 = (RelativeLayout) findViewById(R.id.ii_login_title3);
		ii_common_container.setVisibility(View.GONE);
		ii_login_title.setVisibility(View.VISIBLE);
		ii_login_title3.setVisibility(View.GONE);
		top_back = (ImageButton) findViewById(R.id.top_back);
		top_title2 = (TextView) findViewById(R.id.top_title2);
		top_title2.setText("注册");
		login_phone = (EditText) findViewById(R.id.login_phone);
		login_yzm = (EditText) findViewById(R.id.login_yzm);
		login_password = (EditText) findViewById(R.id.login_password);
		login_password2 = (EditText) findViewById(R.id.login_password2);
		login_rig_visitor = (Button) findViewById(R.id.login_rig_visitor);
		login_but = (Button) findViewById(R.id.login_but);
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
		if(timer != null) {
			timer.cancel();
		}
		time = 120;
		login_rig_visitor.setClickable(true);
		login_rig_visitor.setText("获取验证码");
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.top_back:
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
			break;
		case R.id.login_rig_visitor:
			phone = login_phone.getText().toString().trim();
			if(StringUtils.isBlank(phone)) {
				PromptManager.showToast(getApplicationContext(), "请输入手机号");
				return;
			}
			if(!StringUtil.isPhone(phone)) {
				PromptManager.showToast(getApplicationContext(), "请输入正确格式的手机号");
				return;
			}
			checkPhone(phone);
			break;
		case R.id.login_but:
			String phone2 = login_phone.getText().toString().trim();
			if(StringUtils.isBlank(phone2)) {
				PromptManager.showToast(getApplicationContext(), "请输入手机号");
				return;
			}
			if(!StringUtil.isPhone(phone2)) {
				PromptManager.showToast(getApplicationContext(), "请输入正确格式的手机号");
				return;
			}
			String yz = login_yzm.getText().toString().trim();
			if(StringUtils.isBlank(yz)) {
				PromptManager.showToast(getApplicationContext(), "验证码不能为空");
				return;
			}
			if(!yzmCode.equalsIgnoreCase(yz)) {
				PromptManager.showToast(getApplicationContext(), "验证码错误");
				return;
			}
			String password1 = login_password.getText().toString().trim();
			if(StringUtils.isBlank(password1)) {
				PromptManager.showToast(getApplicationContext(), "请输入密码");
				return;
			}
			if(password1.length() < 6) {
				PromptManager.showToast(getApplicationContext(), "密码不能小于6位");
				return;
			}
			String password2 = login_password2.getText().toString().trim();
			if(StringUtils.isBlank(password2)) {
				PromptManager.showToast(getApplicationContext(), "请输入确认密码");
				return;
			}
			if(!password1.equals(password2)) {
				PromptManager.showToast(getApplicationContext(), "2次密码不一致");
				return;
			}
			PromptManager.showProgressDialog(this, "注册中...");
			rigister(phone2,password1);
			break;
		}
	}
	
	/**
	 * 注册
	 * @param phone2
	 * @param password1
	 */
	private void rigister(final String phone2, final String password1) {
		new Thread(){
			@Override
			public void run() {
				super.run();
				r = WebServiceUtils.add2(phone2,city,ConstantValue.YEZHU,xiaoId,password1,"","","",fangId);
				Message msg = new Message();
				msg.what = RIGISTER;
				handler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 检查手机号是否为房主
	 * @param phone2
	 */
	private void checkPhone(final String phone2) {
		new Thread(){
			@Override
			public void run() {
				super.run();
				result = WebServiceUtils.info(phone2);
				Message msg = new Message();
				msg.what = CHECKPHONE;
				handler.sendMessage(msg);
			}
		}.start();
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
	
	public void startTime() {
		timer = new Timer();
		login_rig_visitor.setClickable(false);
		login_rig_visitor.setText(time + "秒");
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = DUANXIN;
				handler.sendMessage(msg);
			}
		}, 1000, 1000);
	}
	
	public void stopTime() {
		time = 120;
		login_rig_visitor.setClickable(true);
		login_rig_visitor.setText("获取验证码");
		timer.cancel();
	}
}
