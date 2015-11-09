package com.lemon95.wyzq.fragment;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.manage.MiddleManager;
import com.lemon95.wyzq.view.BaseUI;
import com.umeng.analytics.MobclickAgent;

import android.content.ContentValues;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

public class ShareListFragment extends BaseUI {
	
	private static final String TAG = ShareListFragment.class.getSimpleName();
	private LinearLayout share_video;

	public ShareListFragment(Context context) {
		super(context);
	}

	@Override
	public void init() {
		showInMiddle = (LinearLayout) View.inflate(context, R.layout.share_fragment, null);
		share_video = (LinearLayout) findViewById(R.id.share_video);
		
	}

	@Override
	public void setListener() {
		share_video.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch(v.getId()) {
			case R.id.share_video:
				MiddleManager.getInstance().changeUI(AddShareFragment.class);
				break;
		}
	}

	@Override
	public int getID() {
		return ConstantValue.SHARELISTFRAGMENT;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(context);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(context);
	}

}
