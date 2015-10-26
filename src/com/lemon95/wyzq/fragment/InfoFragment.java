package com.lemon95.wyzq.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.adapter.CommunityInfoAdapter;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.manage.MiddleManager;
import com.lemon95.wyzq.model.Result;
import com.lemon95.wyzq.myview.listview.OnRefreshListener;
import com.lemon95.wyzq.myview.listview.RefreshListView;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.view.BaseUI;
import com.lemon95.wyzq.webserver.WebServiceUtils;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InfoFragment extends BaseUI implements OnRefreshListener{
	
	private final static String TAG = InfoFragment.class.getSimpleName();
	private final static int INFO = 0;
	private LinearLayout accetp_ll_load;
	private ImageView accetp_iv_load;
	private TextView accept_no_order1;
	private AnimationDrawable animaition;
	private RefreshListView list;
	private CommunityInfoAdapter adapter;
	private Result result;
	private SharedPreferences sp;
	private String xqid;
	private String totleCount = "0";
	private boolean isResl2 = false;
	private int pageId = 1;
	private List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
				case INFO:
					PromptManager.closeProgressDialog();
					if(isResl2) {
						list.hideFooterView();
						isResl2 = false;
					}
					if(result != null) {
						totleCount = result.getFldtotalRecord();
						if(listMap.size() > 0) {
							
							adapter.notifyDataSetChanged();
							accetp_ll_load.setVisibility(View.GONE);
							list.setVisibility(View.VISIBLE);
						} else {
							animaition.stop();
							accetp_iv_load.setVisibility(View.GONE);
							accept_no_order1.setText("您所在的社区当前未发布社区公告");
						}
					} else {
						animaition.stop();
						accetp_iv_load.setVisibility(View.GONE);
						accept_no_order1.setText("您所在的社区当前未发布社区公告");
					}
					break;
			}
		};
	};
	
	public InfoFragment(Context context) {
		super(context);
	}

	@Override
	public void init() {
		showInMiddle = (LinearLayout) View.inflate(context, R.layout.info_fragment, null);
		
		accetp_ll_load = (LinearLayout)findViewById(R.id.accetp_ll_load);
		accetp_iv_load = (ImageView)findViewById(R.id.accetp_iv_load);
		accept_no_order1 = (TextView)findViewById(R.id.accept_no_order1);
		accetp_iv_load.setBackgroundResource(R.anim.load);
		
		animaition = (AnimationDrawable)accetp_iv_load.getBackground();
		animaition.setOneShot(false);
		animaition.start();
		accetp_ll_load.setVisibility(View.VISIBLE);
		list = (RefreshListView)findViewById(R.id.list);
		accetp_ll_load.setVisibility(View.GONE);
		sp = context.getSharedPreferences(ConstantValue.CONFIG, Context.MODE_PRIVATE);
		xqid = sp.getString("XQID", "");
	}

	@Override
	public void setListener() {
		list.setOnRefreshListener(this);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Map<String,String> map = listMap.get(arg2);
				if(map == null) {
					PromptManager.showToast(context, "");
				} else {
					Bundle bundle2 = new Bundle();
					bundle2.putString("id", map.get("id"));
					MiddleManager.getInstance().changeUI(InfoContentFragment.class, bundle2);
				}
			}
		});
	}

	@Override
	public int getID() {
		return ConstantValue.INFOFRAGMENT;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(context);
		adapter = new CommunityInfoAdapter(context, listMap);
		list.setAdapter(adapter);
		getInfo(pageId);
	}
	
	private void getInfo(final int pageId) {
		PromptManager.showProgressDialog(context, "加载中...");
		new Thread(){
			@Override
			public void run() {
				super.run();
				result = WebServiceUtils.news(xqid,pageId,listMap);
				Message msg = new Message();
				msg.what = INFO;
				handler.sendMessage(msg);
			}
		}.start();
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(context);
		listMap.clear();
	}

	@Override
	public void onLoadingMore() {
		if(listMap == null) {
			list.hideFooterView();
			return;
		}
		if(Integer.parseInt(totleCount) == listMap.size()) {
			list.hideFooterView();
			//PromptManager.showToast(context, "已是最后一页");
		} else {
			isResl2 = true;
			pageId = pageId + 1;
			getInfo(pageId);
		}
	}

}
