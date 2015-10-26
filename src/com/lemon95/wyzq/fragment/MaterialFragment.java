package com.lemon95.wyzq.fragment;

import java.util.List;
import java.util.Map;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.adapter.MaterialAdapter;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.view.BaseUI;
import com.lemon95.wyzq.webserver.WebServiceUtils;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MaterialFragment extends BaseUI {

	private static final String TAG = MaterialFragment.class.getSimpleName();
	private static final int IMG = 0;
	private SharedPreferences sp;
	private String userName;
	private String xqid;
	private ListView listView;
	private TextView textView;
	private MaterialAdapter adapter;
	private List<Map<String,String>> list;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
				case IMG:
					if(list != null) {
						if(list.size() > 0) {
							textView.setVisibility(View.GONE);
							listView.setVisibility(View.VISIBLE);
							adapter = new MaterialAdapter(context, list);
							listView.setAdapter(adapter);
							return;
						}
					}
					textView.setVisibility(View.VISIBLE);
					listView.setVisibility(View.GONE);
					break;
			}
		};
	};
	
	public MaterialFragment(Context context) {
		super(context);
	}

	@Override
	public void init() {
		showInMiddle = (LinearLayout) View.inflate(context, R.layout.material_fragment, null);
		listView = (ListView) findViewById(R.id.listView);
		textView = (TextView) findViewById(R.id.textView);
		sp = context.getSharedPreferences(ConstantValue.CONFIG, Context.MODE_PRIVATE);
		userName = sp.getString("USERNAME", "");
		xqid = sp.getString("XQID", "");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(context);
		getMyVip();
	}

	private void getMyVip() {
		new Thread(){
			public void run() {
				list = WebServiceUtils.myvip(userName,xqid);
				Message msg = new Message();
				msg.what = IMG;
				handler.sendMessage(msg);
			};
		}.start();
	}

	@Override
	public void setListener() {

	}

	@Override
	public int getID() {
		return ConstantValue.MATERIALFRAGMENT;
	}
	
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(context);
	}
	
	@Override
	public void submit() {
		super.submit();
		PromptManager.showToast(context, "等待添加");
	}

}
