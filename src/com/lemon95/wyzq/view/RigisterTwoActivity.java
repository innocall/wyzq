package com.lemon95.wyzq.view;

import org.apache.commons.lang3.StringUtils;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.application.ExitApplication;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.utils.PromptManager;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RigisterTwoActivity extends ActionBarActivity implements OnClickListener{
	
	private static final String TAG = RigisterTwoActivity.class.getSimpleName();
	private RelativeLayout ii_common_container;
	private RelativeLayout ii_login_title;
	private RelativeLayout ii_login_title3;
	private RelativeLayout city;
	private RelativeLayout xiaoqu;
	private RelativeLayout fanghao;
	private ImageButton top_back;
	private TextView top_title2;
	private TextView city_text;
	private TextView xiaoqu_text;
	private TextView fanghao_text;
	private TextView fang_id;
	private TextView xiaoqu_id;
	private TextView city_id;
	private Button rig_but;
	private String type;
	private String id;
	private String name;
	private String xqid;
	private String xqidName;
	private String cityName;
	public static RigisterTwoActivity instance = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		setContentView(R.layout.activity_rigister_two);
		type = getIntent().getStringExtra("type");
		id = getIntent().getStringExtra("id");
		name = getIntent().getStringExtra("name");
		xqid = getIntent().getStringExtra("xqid");
		xqidName = getIntent().getStringExtra("xqidName");
		cityName = getIntent().getStringExtra("city");
		ExitApplication.getInstance().addActivity(this);
		instance = this;
		initView();
		setListener();
	}
	
	private void setListener() {
		top_back.setOnClickListener(this);
		xiaoqu.setOnClickListener(this);
		city.setOnClickListener(this);
		fanghao.setOnClickListener(this);
		rig_but.setOnClickListener(this);
	}

	private void initView() {
		ii_common_container = (RelativeLayout) findViewById(R.id.ii_common_container);
		ii_login_title = (RelativeLayout) findViewById(R.id.ii_login_title);
		ii_login_title3 = (RelativeLayout) findViewById(R.id.ii_login_title3);
		city = (RelativeLayout) findViewById(R.id.city);
		xiaoqu = (RelativeLayout) findViewById(R.id.xiaoqu);
		fanghao = (RelativeLayout) findViewById(R.id.fanghao);
		top_back = (ImageButton) findViewById(R.id.top_back);
		city_text = (TextView) findViewById(R.id.city_text);
		top_title2 = (TextView) findViewById(R.id.top_title2);
		xiaoqu_text = (TextView) findViewById(R.id.xiaoqu_text);
		fanghao_text = (TextView) findViewById(R.id.fanghao_text);
		city_id = (TextView) findViewById(R.id.city_id);
		xiaoqu_id = (TextView) findViewById(R.id.xiaoqu_id);
		fang_id = (TextView) findViewById(R.id.fang_id);
		rig_but = (Button) findViewById(R.id.rig_but);
		ii_common_container.setVisibility(View.GONE);
		ii_login_title.setVisibility(View.VISIBLE);
		ii_login_title3.setVisibility(View.GONE);
		top_title2.setText("注册");
		xiaoqu_id.setText(xqid);
		xiaoqu_text.setText(xqidName);
		fang_id.setText(id);
		fanghao_text.setText(name);
		city_text.setText(cityName);
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
			case R.id.city:
				Intent intent = new Intent(this,CityActivity.class);
				intent.putExtra("type", type);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.left_up);
				break;
			case R.id.xiaoqu:
				String citys2 = city_text.getText().toString().trim();
				if(StringUtils.isBlank(citys2)) {
					PromptManager.showToast(getApplicationContext(), "请选择城市");
					return;
				}
				Intent intent3 = new Intent(this,TypeActivity.class);
				intent3.putExtra("type", type);
				intent3.putExtra("city", citys2);
				startActivity(intent3);
				overridePendingTransition(R.anim.left_in, R.anim.left_up);
				break;
			case R.id.fanghao:
				String xiao1 = xiaoqu_text.getText().toString().trim();
				String citys = city_text.getText().toString().trim();
				String xiaoId1 = xiaoqu_id.getText().toString().trim();
				if(StringUtils.isBlank(citys)) {
					PromptManager.showToast(getApplicationContext(), "请选择城市");
					return;
				}
				if(StringUtils.isBlank(xiao1)) {
					PromptManager.showToast(getApplicationContext(), "请选择小区");
					return;
				}
				Intent intent1 = new Intent(this,TypeFangHaoActivity.class);
				intent1.putExtra("type", type);
				intent1.putExtra("id", xiaoId1);
				intent1.putExtra("name", xiao1);
				intent1.putExtra("city", citys);
				startActivity(intent1);
				overridePendingTransition(R.anim.left_in, R.anim.left_up);
				break;
			case R.id.rig_but:
				String xiao = xiaoqu_text.getText().toString().trim();
				String xiaoId = xiaoqu_id.getText().toString().trim();
				if(StringUtils.isBlank(xiao)) {
					PromptManager.showToast(getApplicationContext(), "请选择小区");
					return;
				}
				String fang = fanghao_text.getText().toString().trim();
				String fangId = fang_id.getText().toString().trim();
				if(StringUtils.isBlank(fang)) {
					PromptManager.showToast(getApplicationContext(), "请选择房号");
					return;
				}
				if(ConstantValue.YEZHU.equals(type)) {
					Intent intent2 = new Intent(this,RigisterThreeActivity.class);
					intent2.putExtra("type", type);
					intent2.putExtra("xiaoId", xiaoId);
					intent2.putExtra("fangId", fangId);
					intent2.putExtra("city", cityName);
					startActivity(intent2);
					overridePendingTransition(R.anim.left_in, R.anim.left_up);
				} else if(ConstantValue.QINGSHU.equals(type)) {
					Intent intent2 = new Intent(this,RigisterZuKeThreeActivity.class);
//					Intent intent2 = new Intent(this,RigisterQinShuThreeActivity.class);
					intent2.putExtra("xiaoId", xiaoId);
					intent2.putExtra("fangId", fangId);
					intent2.putExtra("type", type);
					intent2.putExtra("city", cityName);
					startActivity(intent2);
					overridePendingTransition(R.anim.left_in, R.anim.left_up);
				} else if(ConstantValue.ZUKE.equals(type)){
					Intent intent2 = new Intent(this,RigisterZuKeThreeActivity.class);
					intent2.putExtra("xiaoId", xiaoId);
					intent2.putExtra("fangId", fangId);
					intent2.putExtra("type", type);
					intent2.putExtra("city", cityName);
					startActivity(intent2);
					overridePendingTransition(R.anim.left_in, R.anim.left_up);
				} else {
					PromptManager.showToast(getApplicationContext(), "请重新选择您的身份");
				}
				break;
		}
	}
	
}
