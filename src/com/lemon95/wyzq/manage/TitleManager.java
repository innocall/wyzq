package com.lemon95.wyzq.manage;

import java.util.Observable;
import java.util.Observer;

import org.apache.commons.lang3.StringUtils;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.global.ConstantValue;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * 管理标题容器的工具
 * 
 * @author Administrator
 * 
 */
public class TitleManager implements Observer {
	// 显示和隐藏

	private static TitleManager instance = new TitleManager();

	private TitleManager() {
	}

	public static TitleManager getInstance() {
		return instance;
	}

	private RelativeLayout commonContainer;  //无返回
	private RelativeLayout loginContainer;   //有返回
	private RelativeLayout ii_login_title3;   //带提交
	private RelativeLayout ii_login_title4;   //带菜单

	private ImageButton top_back;// 返回
	private ImageButton top_back3;// 返回
	private ImageButton top_back4;// 返回
	private ImageButton top_right4;// 返回

	private TextView top_title;// 无返回标题内容
	private TextView top_title2;// 有返回标题内容
	private TextView top_title3;// 有返回标题内容
	private TextView top_title4;// 有返回标题内容
	private Button top_submit;
	
	private ActionBarActivity activity;

	public void init(ActionBarActivity activity) {
		this.activity = activity;
		commonContainer = (RelativeLayout) activity.findViewById(R.id.ii_common_container);
		loginContainer = (RelativeLayout) activity.findViewById(R.id.ii_login_title);
		ii_login_title3 = (RelativeLayout) activity.findViewById(R.id.ii_login_title3);
		ii_login_title4 = (RelativeLayout) activity.findViewById(R.id.ii_login_title4);
		
		top_back = (ImageButton) activity.findViewById(R.id.top_back);
		top_back3 = (ImageButton) activity.findViewById(R.id.top_back3);
		top_back4 = (ImageButton) activity.findViewById(R.id.top_back4);
		top_right4 = (ImageButton) activity.findViewById(R.id.top_right4);

		top_title = (TextView) activity.findViewById(R.id.top_title);
		top_title2 = (TextView) activity.findViewById(R.id.top_title2);
		top_title3 = (TextView) activity.findViewById(R.id.top_title3);
		top_title4 = (TextView) activity.findViewById(R.id.top_title4);
		top_submit = (Button) activity.findViewById(R.id.top_submit);

		setListener();
	}

	private void setListener() {
		top_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MiddleManager.getInstance().goBack();
			}
		});
		
		top_back3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MiddleManager.getInstance().goBack();
			}
		});
		
		top_back4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MiddleManager.getInstance().goBack();
			}
		});
		
		top_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MiddleManager.getInstance().submit();
			}
		});
		
		top_right4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MiddleManager.getInstance().submit();
			}
		});
		
		

	}

	private void initTitle() {
		commonContainer.setVisibility(View.GONE);
		loginContainer.setVisibility(View.GONE);
		ii_login_title3.setVisibility(View.GONE);
		ii_login_title4.setVisibility(View.GONE);
	}

	/**
	 * 显示无返回
	 * @param title 标题
	 */
	public void showCommonTitle(String title) {
		initTitle();
		commonContainer.setVisibility(View.VISIBLE);
		top_title.setText(title);
	}

	/**
	 * 显示有返回
	 */
	public void showLoginTitle(String title) {
		initTitle();
		loginContainer.setVisibility(View.VISIBLE);
		top_title2.setText(title);
	}
	
	/**
	 * 显示有提交
	 */
	public void showSubmitTitle(String title,String title2) {
		initTitle();
		ii_login_title3.setVisibility(View.VISIBLE);
		top_title3.setText(title);
		top_submit.setText(title2);
	}
	
	/**
	 * 显示有菜单
	 */
	public void showSubmitTitle(String title) {
		initTitle();
		ii_login_title4.setVisibility(View.VISIBLE);
		top_title4.setText(title);
	}
	
	public void hideTitle4(boolean isParam){
		if(isParam) {
			top_right4.setVisibility(View.VISIBLE);
		} else {
			top_right4.setVisibility(View.GONE);
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		if (data != null && StringUtils.isNumeric(data.toString())) {
			int id = Integer.parseInt(data.toString());
			switch (id) {
				case ConstantValue.MAINFRAGMENT:
					showCommonTitle("首页");
					break;
				case ConstantValue.COMMUNITYFRAGMENT:
					showCommonTitle("社区服务");
					break;
				case ConstantValue.SAYFRAGMENT:
					showCommonTitle("我要说说");
					break;
				case ConstantValue.USERFRAGMENT:
					showCommonTitle("更多");
					break;
				case ConstantValue.VIDEOLISTFRAGMENT:
					showLoginTitle("手机电视");
					//showCommonTitle("手机电视");
					break;
				case ConstantValue.INFOFRAGMENT:
					showLoginTitle("社区公告");
					break;
				case ConstantValue.OPENDORPFRAGMENT:
					showLoginTitle("一键开门");
					break;
				case ConstantValue.DIANHUAFRAGMENT:
					showLoginTitle("免费电话");
					break;
				case ConstantValue.INFOCONTENTFRAGMENT:
					//showLoginTitle("公告详情");
					break;
				case ConstantValue.USERMESSAGEFRAGMENT:
					showLoginTitle("个人信息");
					break;
				case ConstantValue.UPDATEPAWFRAGMENT:
					showLoginTitle("修改密码");
					break;
				case ConstantValue.SAYCONTENTFRAGMENT:
//					showLoginTitle("帖子详情");
					showSubmitTitle("帖子详情");
					break;
				case ConstantValue.PINGLUNFRAGMENT:
					showLoginTitle("发表评论");
					break;
				case ConstantValue.MYSAYFRAGMENT:
					showLoginTitle("我的说说");
					break;
				case ConstantValue.MATERIALFRAGMENT:
					showLoginTitle("房下账号");
					break;
				case ConstantValue.SHARELISTFRAGMENT:
					showLoginTitle("多屏分享");
					break;
			}
		}

	}
}
