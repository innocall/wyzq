package com.lemon95.wyzq.view;

import java.util.ArrayList;
import java.util.List;

import com.lemon95.wyzq.R;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class SplashLeadActivity extends ActionBarActivity {
	
	private final static String TAG = SplashLeadActivity.class.getSimpleName();
	private List<View> pagers;
	private ViewPager viewPager;
	private PagerAdapter pagerAdapter;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
	    getActionBar().hide();
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.lead_activity);
		sp = getSharedPreferences("config", MODE_PRIVATE);
	    initViewPager();
	}

	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.ii_viewpager);
		
		pagers = new ArrayList<View>();
		View image1 = View.inflate(getApplicationContext(), R.layout.splash_image1, null);
		pagers.add(image1);

		View image2 = View.inflate(getApplicationContext(), R.layout.splash_image2, null);
		pagers.add(image2);
		
		View image3 = View.inflate(getApplicationContext(), R.layout.splash_image3, null);
		pagers.add(image3);
		
		View image4 = View.inflate(getApplicationContext(), R.layout.splash_image, null);
		pagers.add(image4);
		
		pagerAdapter = new MyPagerAdapter();
		viewPager.setAdapter(pagerAdapter);
		Button but = (Button) image4.findViewById(R.id.but);
		but.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor ed = sp.edit();
				ed.putString("version", getViersion());
				ed.commit(); //记录当前版本号
				Intent intent = new Intent(SplashLeadActivity.this,LoginActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_up);
			}
		});
	}
	
	private class MyPagerAdapter extends PagerAdapter {

		public Object instantiateItem(ViewGroup container, int position) {
			View view = pagers.get(position);
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = pagers.get(position);

			container.removeView(view);
		}

		@Override
		public int getCount() {
			return pagers.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}
	
	private String getViersion() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "version";
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onRestart() {
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
	
	
}
