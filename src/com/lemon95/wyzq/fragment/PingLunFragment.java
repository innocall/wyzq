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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class PingLunFragment extends BaseUI {

	private static final String TAG = PingLunFragment.class.getSimpleName();
	private static final int SUBMIT = 0;
	private EditText shop_des;
	private Button login_but;
	private SharedPreferences sp;
	private String userName;
	private String b2id;
	private Result result;
	private String nickName;
	private String cc;
	private String type;
	private String time;
	private String username;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
				case SUBMIT:
					if(result != null) {
						if(Integer.parseInt(result.getRows()) > 0) {
							PromptManager.showToast(context, "提交成功");
							Bundle bundle2 = new Bundle();
							bundle2.putString("b2id", b2id);
							bundle2.putString("nickName", nickName);
							bundle2.putString("cc", cc);
							bundle2.putString("type", type);
							bundle2.putString("time", time);
							bundle2.putString("username", username);
							MiddleManager.getInstance().changeUI(SayContentFragment.class,bundle2);
							return;
						}
					} 
					PromptManager.showToast(context, "提交失败");
					break;
			}
		};
	};
	
	public PingLunFragment(Context context) {
		super(context);
	}

	@Override
	public void init() {
		showInMiddle = (LinearLayout) View.inflate(context, R.layout.say_item, null);
		shop_des = (EditText) findViewById(R.id.shop_des);
		login_but = (Button) findViewById(R.id.login_but);
		sp = context.getSharedPreferences(ConstantValue.CONFIG, Context.MODE_PRIVATE);
		userName = sp.getString("USERNAME", "");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		b2id = bundle.getString("b2id", "");
		nickName = bundle.getString("nickName", "");
		time = bundle.getString("time", "");
		cc = bundle.getString("cc", "");
		type = bundle.getString("type", "");
		username = bundle.getString("username", "");
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
				final String sya_content = shop_des.getText().toString().trim();
				if(StringUtils.isBlank(sya_content)) {
					PromptManager.showToast(context, "评论不能为空");
					return;
				}
				//隐藏键盘
				InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);   
		        imm.hideSoftInputFromWindow(shop_des.getWindowToken(),0);   
				new Thread(){
					public void run() {
						result = WebServiceUtils.bbscommentadd(userName,b2id,sya_content);
						Message msg = new Message();
						msg.what = SUBMIT;
						handler.sendMessage(msg);
					};
				}.start();
				break;
		}
	}

	@Override
	public int getID() {
		return ConstantValue.PINGLUNFRAGMENT;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MiddleManager.getInstance().clearNew(PingLunFragment.class);
		MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(context);
	}

}
