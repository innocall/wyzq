package com.lemon95.wyzq.manage;

import java.util.Observable;
import java.util.Observer;

import org.apache.commons.lang3.StringUtils;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.fragment.CommunityFragment;
import com.lemon95.wyzq.fragment.MainFragment;
import com.lemon95.wyzq.fragment.SayFragment;
import com.lemon95.wyzq.fragment.UserFragment;
import com.lemon95.wyzq.fragment.VideoListFragment;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.utils.PromptManager;
import com.umeng.analytics.MobclickAgent;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * 控制底部导航容器
 * 
 * @author Administrator
 * 
 */
public class BottomManager implements Observer {
	protected static final String TAG = "BottomManager";
	/******************* 第一步：管理对象的创建(单例模式) ***************************************************/
	// 创建一个静态实例
	private static BottomManager instrance;

	// 构造私有
	private BottomManager() {
	}

	// 提供统一的对外获取实例的入口
	public static BottomManager getInstrance() {
		if (instrance == null) {
			instrance = new BottomManager();
		}
		return instrance;
	}

	/******************* 第二步：初始化各个导航容器及相关控件设置监听 *********************************/

	/************ 底部导航 ************/
	private LinearLayout bottom_order;   // 首页
	private LinearLayout bottom_myorder; // 社区服务
	private LinearLayout bottom_wallet; // 我要说说
	private LinearLayout bottom_other; // 个人中心
	private ActionBarActivity activity;
	private RelativeLayout ii_bottom;
	
	private ImageView order_image1;
	private ImageView order_image2;
	private ImageView order_image3;
	private ImageView order_image4;
	
	private TextView order_text1;
	private TextView order_text2;
	private TextView order_text3;
	private TextView order_text4;

	public void init(ActionBarActivity activity) {
		this.activity = activity;
		bottom_order = (LinearLayout) activity.findViewById(R.id.bottom_order);
		bottom_myorder = (LinearLayout) activity.findViewById(R.id.bottom_myorder);
		bottom_wallet = (LinearLayout) activity.findViewById(R.id.bottom_wallet);
		bottom_other = (LinearLayout) activity.findViewById(R.id.bottom_other);
		ii_bottom = (RelativeLayout) activity.findViewById(R.id.ii_bottom);
		
		order_image1 = (ImageView) activity.findViewById(R.id.order_image1);
		order_image2 = (ImageView) activity.findViewById(R.id.order_image2);
		order_image3 = (ImageView) activity.findViewById(R.id.order_image3);
		order_image4 = (ImageView) activity.findViewById(R.id.order_image4);
		
		order_text1 = (TextView) activity.findViewById(R.id.order_text1);
		order_text2 = (TextView) activity.findViewById(R.id.order_text2);
		order_text3 = (TextView) activity.findViewById(R.id.order_text3);
		order_text4 = (TextView) activity.findViewById(R.id.order_text4);
		
		setListener();
	}
	
	private void setListener() {
		bottom_order.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Bundle bundle2 = new Bundle();
//				bundle2.putBoolean("isParam", false);
//				MiddleManager.getInstance().changeUI(MainFragment.class,bundle2);
				MiddleManager.getInstance().changeUI(MainFragment.class);
				MobclickAgent.onEvent(activity,"mmain");
			}
		});
		bottom_myorder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//MiddleManager.getInstance().changeUI(VideoListFragment.class);
				MiddleManager.getInstance().changeUI(CommunityFragment.class);
				MobclickAgent.onEvent(activity,"community");
			}
		});
		bottom_wallet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//MiddleManager.getInstance().say();
				SharedPreferences sp = activity.getSharedPreferences(ConstantValue.CONFIG, Context.MODE_PRIVATE);
				String ground = sp.getString("GRADE", ConstantValue.YOUKE);
				if(ConstantValue.YOUKE.equals(ground)) {
					//游客 提示完善注册信息
					PromptManager.showRigister(activity);
				} else {
					String isLive = sp.getString("ISLIVE", "");
					if(!"True".equals(isLive)) {
						//没有激活,提示用户激活
						String phone = sp.getString("USERNAME", "");
						String remobile = sp.getString("REMOBILE", "");
						PromptManager.showJiHuo(activity,phone,remobile);
						return;
					}
					MiddleManager.getInstance().changeUI(SayFragment.class);
					MobclickAgent.onEvent(activity,"say");
				}
			}
		});
		bottom_other.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MiddleManager.getInstance().changeUI(UserFragment.class);
				MobclickAgent.onEvent(activity,"userMessage");
			}
		});

	}
	
	public void initView() {
		order_image1.setImageResource(R.drawable.battery_tab_icon01_normal);
		order_image2.setImageResource(R.drawable.battery_tab_icon04_normal);
		order_image3.setImageResource(R.drawable.battery_tab_icon05_normal);
		order_image4.setImageResource(R.drawable.battery_tab_icon02_normal);
		order_text1.setTextColor(Color.parseColor("#666666"));
		order_text2.setTextColor(Color.parseColor("#666666"));
		order_text3.setTextColor(Color.parseColor("#666666"));
		order_text4.setTextColor(Color.parseColor("#666666"));
	}
	
	public void showOrderBackColor() {
		initView();
		order_image1.setImageResource(R.drawable.battery_tab_icon01_pressed);
		order_text1.setTextColor(Color.parseColor("#FF6700"));
	}
	
	public void showMyOrderBackColor() {
		initView();
		order_image2.setImageResource(R.drawable.battery_tab_icon04_pressed);
		order_text2.setTextColor(Color.parseColor("#FF6700"));
	}
	
	public void showWalletBackColor() {
		initView();
		order_image3.setImageResource(R.drawable.battery_tab_icon05_pressed);
		order_text3.setTextColor(Color.parseColor("#FF6700"));
	}
	
	public void showOtherBackColor() {
		initView();
		order_image4.setImageResource(R.drawable.battery_tab_icon02_pressed);
		order_text4.setTextColor(Color.parseColor("#FF6700"));
	}
	
	public void hideBottom() {
		ii_bottom.setVisibility(View.GONE);
	}
	
	public void showBottom() {
		ii_bottom.setVisibility(View.VISIBLE);
	}

	@Override
	public void update(Observable observable, Object data) {

		if (data != null && StringUtils.isNumeric(data.toString())) {
			int id = Integer.parseInt(data.toString());
			switch (id) {
				case ConstantValue.MAINFRAGMENT:
					showBottom();
					showOrderBackColor();
					break;
				case ConstantValue.COMMUNITYFRAGMENT:
					showBottom();
					showMyOrderBackColor();
					break;
				case ConstantValue.SAYFRAGMENT:
					showBottom();
					showWalletBackColor();
					break;
				case ConstantValue.USERFRAGMENT:
					showBottom();
					showOtherBackColor();
					break;
				case ConstantValue.VIDEOLISTFRAGMENT:
					showBottom();
					showMyOrderBackColor();
					//hideBottom();
					break;
				case ConstantValue.INFOFRAGMENT:
					hideBottom();
					break;
				case ConstantValue.OPENDORPFRAGMENT:
					hideBottom();
					break;
				case ConstantValue.DIANHUAFRAGMENT:
					hideBottom();
					break;
				case ConstantValue.INFOCONTENTFRAGMENT:
					hideBottom();
					break;
				case ConstantValue.USERMESSAGEFRAGMENT:
					hideBottom();
					break;
				case ConstantValue.UPDATEPAWFRAGMENT:
					hideBottom();
					break;
				case ConstantValue.SAYCONTENTFRAGMENT:
					hideBottom();
					break;
				case ConstantValue.PINGLUNFRAGMENT:
					hideBottom();
					break;
				case ConstantValue.MATERIALFRAGMENT:
					hideBottom();
					break;
				case ConstantValue.MYSAYFRAGMENT:
					hideBottom();
					break;
				case ConstantValue.SHARELISTFRAGMENT:
					hideBottom();
					break;
				case ConstantValue.ADDSHAREFRAGMENT:
					hideBottom();
					break;
			}
		}

	}

}
