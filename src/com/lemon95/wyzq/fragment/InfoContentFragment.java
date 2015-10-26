package com.lemon95.wyzq.fragment;

import java.util.Map;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.manage.MiddleManager;
import com.lemon95.wyzq.manage.TitleManager;
import com.lemon95.wyzq.model.Result;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.view.BaseUI;
import com.lemon95.wyzq.webserver.WebServiceUtils;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InfoContentFragment extends BaseUI{
	
	private final static String TAG = InfoContentFragment.class.getSimpleName();
	private final static int INFO = 0;
	private String ggid;
	private TextView info_content;
	private TextView info_title;
	private TextView info_time;
	private TextView info_count;
	private Result result;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
				case INFO:
					PromptManager.closeProgressDialog();
					if(result != null) {
						if(Integer.parseInt(result.getRows()) > 0) {
							Map<String, String> tabMap = result.getTab1();
							TitleManager.getInstance().showLoginTitle(tabMap.get("title"));
							info_content.setText(Html.fromHtml(tabMap.get("content")));
							info_title.setText(tabMap.get("title"));
							info_time.setText(tabMap.get("addtime"));
							info_count.setText(tabMap.get("hot") + "次");
						} else {
							PromptManager.showToast(context, "服务器数据获取失败");
						}
					} else {
						PromptManager.showToast(context, "服务器数据获取失败");
					}
					break;
			}
		};
	};
	
	public InfoContentFragment(Context context) {
		super(context);
	}

	@Override
	public void init() {
		showInMiddle = (LinearLayout) View.inflate(context, R.layout.info_content_fragment, null);
		info_content = (TextView) findViewById(R.id.info_content);
		info_title = (TextView) findViewById(R.id.info_title);
		info_time = (TextView) findViewById(R.id.info_time);
		info_count = (TextView) findViewById(R.id.info_count);
	}

	@Override
	public void setListener() {
		
	}

	@Override
	public int getID() {
		return ConstantValue.INFOCONTENTFRAGMENT;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		ggid = bundle.getString("id");
		MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(context);
		newsinfo();
	}
	
	private void newsinfo() {
		PromptManager.showProgressDialog(context, "加载中...");
		new Thread(){
			@Override
			public void run() {
				super.run();
				result = WebServiceUtils.newsinfo(ggid);
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
		MiddleManager.getInstance().clearNew(InfoContentFragment.class);
	}


}
