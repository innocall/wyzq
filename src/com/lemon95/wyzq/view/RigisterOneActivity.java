package com.lemon95.wyzq.view;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.application.ExitApplication;
import com.lemon95.wyzq.global.ConstantValue;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RigisterOneActivity extends ActionBarActivity implements OnClickListener{
	
	private static final String TAG = RigisterOneActivity.class.getSimpleName();
	private RelativeLayout ii_common_container;
	private RelativeLayout ii_login_title;
	private RelativeLayout ii_login_title3;
	private ImageButton top_back;
	private TextView top_title2;
	private TextView type1,type2,type3,type4,type5;
	private RelativeLayout yezhu;
	private RelativeLayout qingshu;
	private RelativeLayout zuke;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		setContentView(R.layout.activity_rigister_one);
		ExitApplication.getInstance().addActivity(this);
		initView();
		setListener();
	}
	
	private void setListener() {
		top_back.setOnClickListener(this);
		yezhu.setOnClickListener(this);
		qingshu.setOnClickListener(this);
		zuke.setOnClickListener(this);
	}

	private void initView() {
		ii_common_container = (RelativeLayout) findViewById(R.id.ii_common_container);
		ii_login_title = (RelativeLayout) findViewById(R.id.ii_login_title);
		ii_login_title3 = (RelativeLayout) findViewById(R.id.ii_login_title3);
		yezhu = (RelativeLayout) findViewById(R.id.yezhu);
		qingshu = (RelativeLayout) findViewById(R.id.qingshu);
		zuke = (RelativeLayout) findViewById(R.id.zuke);
		top_back = (ImageButton) findViewById(R.id.top_back);
		top_title2 = (TextView) findViewById(R.id.top_title2);
		type1 = (TextView) findViewById(R.id.type1);
		type2 = (TextView) findViewById(R.id.type2);
		type3 = (TextView) findViewById(R.id.type3);
		type4 = (TextView) findViewById(R.id.type4);
		type5 = (TextView) findViewById(R.id.type5);
		ii_common_container.setVisibility(View.GONE);
		ii_login_title.setVisibility(View.VISIBLE);
		ii_login_title3.setVisibility(View.GONE);
		top_title2.setText("注册");
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
			case R.id.yezhu:
				Intent intent = new Intent(this,RigisterTwoActivity.class);
				intent.putExtra("type", ConstantValue.YEZHU);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.left_up);
				break;
			case R.id.qingshu:
				Intent intent2 = new Intent(this,RigisterTwoActivity.class);
				intent2.putExtra("type", ConstantValue.QINGSHU);
				startActivity(intent2);
				overridePendingTransition(R.anim.left_in, R.anim.left_up);
				break;
			case R.id.zuke:
				Intent intent3 = new Intent(this,RigisterTwoActivity.class);
				intent3.putExtra("type", ConstantValue.ZUKE);
				startActivity(intent3);
				overridePendingTransition(R.anim.left_in, R.anim.left_up);
				break;
		}
	}
	
}
