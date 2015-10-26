package com.lemon95.wyzq.fragment;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.view.BaseUI;

import android.content.ContentValues;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

public class ShareListFragment extends BaseUI {
	
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
				
				break;
		}
	}

	@Override
	public int getID() {
		return ConstantValue.SHARELISTFRAGMENT;
	}

}
