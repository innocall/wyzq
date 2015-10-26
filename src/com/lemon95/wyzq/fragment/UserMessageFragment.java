package com.lemon95.wyzq.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.model.Result;
import com.lemon95.wyzq.view.BaseUI;
import com.lemon95.wyzq.webserver.WebServiceUtils;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserMessageFragment extends BaseUI {
	
	private final static String TAG = UserMessageFragment.class.getSimpleName();
	private final static int XIAOQU = 0;
	private TextView user_names;
	private TextView user_phone;
	private TextView user_xiaoqu;
	private TextView user_address;
	private SharedPreferences sp;
	private List<Map<String,String>> list = new ArrayList<Map<String,String>> ();
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case XIAOQU:
					if(list != null && list.size() > 0) {
						user_xiaoqu.setText(list.get(0).get("name"));
					}
					break;
			}
		};
	};
	
	public UserMessageFragment(Context context) {
		super(context);
	}

	@Override
	public void init() {
		showInMiddle = (LinearLayout) View.inflate(context, R.layout.user_message_fragment, null);
		user_names = (TextView) findViewById(R.id.user_names);
		user_phone = (TextView) findViewById(R.id.user_phone);
		user_xiaoqu = (TextView) findViewById(R.id.user_xiaoqu);
		user_address = (TextView) findViewById(R.id.user_address);
		sp = context.getSharedPreferences(ConstantValue.CONFIG, Context.MODE_PRIVATE);
	}

	@Override
	public void setListener() {
		
	}

	@Override
	public int getID() {
		return ConstantValue.USERMESSAGEFRAGMENT;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		String name = sp.getString("REALNAME", "");
		user_names.setText(name);
		String phone = sp.getString("USERNAME", "");
		String xqid = sp.getString("XQID", "");
		user_phone.setText(phone);
		user_address.setText(sp.getString("FANGHAO", ""));
		getXiaoQu(xqid);
		MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(context);
	}
	
	private void getXiaoQu(final String xqid) {
		new Thread(){
			public void run() {
				list = WebServiceUtils.xiaoqu("","",1000,0,xqid);
				Message msg = new Message();
				msg.what = XIAOQU;
				handler.sendMessage(msg);
			};
		}.start();
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(context);
	}

}
