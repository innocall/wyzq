package com.lemon95.wyzq.fragment;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.manage.MiddleManager;
import com.lemon95.wyzq.myview.image.SlideShowView;
import com.lemon95.wyzq.myview.image.SlideShowView.OnNumberKeyboard;
import com.lemon95.wyzq.utils.AsyncImageLoader;
import com.lemon95.wyzq.utils.DisplayUtil;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.view.BaseUI;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CommunityFragment extends BaseUI{
	
	private final static String TAG = CommunityFragment.class.getSimpleName();
	private SlideShowView slideshowView;
	private AsyncImageLoader imageLoad;
	private int w,h;
	private int[] imageUrls = new int[]{R.drawable.banner1,R.drawable.banner2,R.drawable.banner3,R.drawable.banner4};
	private ImageView home_video;
	private ImageView home_info;
	private ImageView home_open;
	private ImageView home_baoxiu;
	private ImageView home_dianhua;
	private ImageView home_share;
	private String ground = "";
	private String isLive = "";
	private SharedPreferences sp;
	
	public CommunityFragment(Context context) {
		super(context);
	}

	@Override
	public void init() {
		showInMiddle = (LinearLayout) View.inflate(context, R.layout.community_fragment, null);
		home_video = (ImageView) findViewById(R.id.home_video);
		home_info = (ImageView) findViewById(R.id.home_info);
		home_open = (ImageView) findViewById(R.id.home_open);
		home_baoxiu = (ImageView) findViewById(R.id.home_baoxiu);
		home_dianhua = (ImageView) findViewById(R.id.home_dianhua);
		home_share = (ImageView) findViewById(R.id.home_share);
		slideshowView = (SlideShowView) findViewById(R.id.slideshowView);
		Point p = DisplayUtil.getScreenMetrics(context);
		w = p.x;
		h = p.y;
		imageLoad = new AsyncImageLoader(context, w, h, R.drawable.banner4);
		sp = context.getSharedPreferences(ConstantValue.CONFIG, Context.MODE_PRIVATE);
	}

	@Override
	public void setListener() {
		slideshowView.setonNumberKeyboard(new OnNumberKeyboard() {

			@Override
			public void OnSetNumber(View v) {
				int currentItem = slideshowView.getCurrentItem();
				if (currentItem == 0) {
					//PromptManager.showToastTest(context, "您点击了第1张");
					MobclickAgent.onEvent(context,"click_banner1");
				}
				if (currentItem == 1) {
					//PromptManager.showToastTest(context, "您点击了第2张");
					MobclickAgent.onEvent(context,"click_banner2");
				}
				if (currentItem == 2) {
					//PromptManager.showToastTest(context, "您点击了第3张");
					MobclickAgent.onEvent(context,"click_banner3");
				}
				if (currentItem == 3) {
					//PromptManager.showToastTest(context, "您点击了第4张");
					MobclickAgent.onEvent(context,"click_banner4");
				}
			}
		});
		home_video.setOnClickListener(this);
		home_info.setOnClickListener(this);
		home_open.setOnClickListener(this);
		home_baoxiu.setOnClickListener(this);
		home_dianhua.setOnClickListener(this);
		home_share.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch(v.getId()) {
			case R.id.home_video:
				MobclickAgent.onEvent(context,"home_video");
				MiddleManager.getInstance().changeUI(VideoListFragment.class);
				break;
			case R.id.home_info:
				MobclickAgent.onEvent(context,"home_info");
				if(ConstantValue.YOUKE.equals(ground)) {
					//游客 提示完善注册信息
					PromptManager.showRigister(context);
				} else {
					if(!"True".equals(isLive)) {
						//没有激活,提示用户激活
						String phone = sp.getString("USERNAME", "");
						String remobile = sp.getString("REMOBILE", "");
						PromptManager.showJiHuo(context,phone,remobile);
						return;
					}
					MiddleManager.getInstance().changeUI(InfoFragment.class);
				}
				break;
			case R.id.home_open:
				MobclickAgent.onEvent(context,"home_open");
				if(ConstantValue.YOUKE.equals(ground)) {
					//游客 提示完善注册信息
					PromptManager.showRigister(context);
				} else {
					if(!"True".equals(isLive)) {
						//没有激活,提示用户激活
						String phone = sp.getString("USERNAME", "");
						String remobile = sp.getString("REMOBILE", "");
						PromptManager.showJiHuo(context,phone,remobile);
						return;
					}
					MiddleManager.getInstance().changeUI(OpenDorpFragment.class);
				}
				break;
			case R.id.home_baoxiu:
				MobclickAgent.onEvent(context,"home_baoxiu");
				if(ConstantValue.YOUKE.equals(ground)) {
					//游客 提示完善注册信息
					PromptManager.showRigister(context);
				} else {
					if(!"True".equals(isLive)) {
						//没有激活,提示用户激活
						String phone = sp.getString("USERNAME", "");
						String remobile = sp.getString("REMOBILE", "");
						PromptManager.showJiHuo(context,phone,remobile);
						return;
					}
					Intent intent = new Intent();
					intent.setClassName("com.lemon95.wyzq", "com.lemon95.wyzq.view.UrlActivity");
					intent.putExtra("title", "快递服务");
					intent.putExtra("url", context.getString(R.string.sf_url));
					MiddleManager.getInstance().activity.startActivity(intent);
				}
				break;
			case R.id.home_dianhua:
				MobclickAgent.onEvent(context,"home_dianhua");
				if(ConstantValue.YOUKE.equals(ground)) {
					//游客 提示完善注册信息
					PromptManager.showRigister(context);
				} else {
					if(!"True".equals(isLive)) {
						//没有激活,提示用户激活
						String phone = sp.getString("USERNAME", "");
						String remobile = sp.getString("REMOBILE", "");
						PromptManager.showJiHuo(context,phone,remobile);
						return;
					}
					MiddleManager.getInstance().changeUI(DianhuaFragment.class);
				}
				break;
			case R.id.home_share:
				MobclickAgent.onEvent(context,"home_share");
				if(ConstantValue.YOUKE.equals(ground)) {
					//游客 提示完善注册信息
					PromptManager.showRigister(context);
				} else {
					if(!"True".equals(isLive)) {
						//没有激活,提示用户激活
						String phone = sp.getString("USERNAME", "");
						String remobile = sp.getString("REMOBILE", "");
						PromptManager.showJiHuo(context,phone,remobile);
						return;
					}
					MiddleManager.getInstance().changeUI(ShareListFragment.class);
				}
				break;
		}
	}

	@Override
	public int getID() {
		return ConstantValue.COMMUNITYFRAGMENT;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		ground = sp.getString("GRADE", ConstantValue.YOUKE);
		isLive = sp.getString("ISLIVE", "");
		MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(context);
		slideshowView.initData(imageUrls, imageLoad);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(slideshowView != null) {
			slideshowView.stopPlay();
		}
		MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(context);
	}

}
