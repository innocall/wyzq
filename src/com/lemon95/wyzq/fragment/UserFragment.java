package com.lemon95.wyzq.fragment;

import java.io.File;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.SplashActivity;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.manage.MiddleManager;
import com.lemon95.wyzq.model.Result;
import com.lemon95.wyzq.utils.AppSystemUtils;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.view.BaseUI;
import com.lemon95.wyzq.view.LoginActivity;
import com.lemon95.wyzq.webserver.WebServiceUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

public class UserFragment extends BaseUI {
	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
	private final static String TAG = UserFragment.class.getSimpleName();
	private final static int UPDATE = 0;
	private RelativeLayout other_user;
	private RelativeLayout update_paw;
	private RelativeLayout other_material;
	private RelativeLayout other_update;
	private RelativeLayout other_exit;
	private RelativeLayout update_share;
	private RelativeLayout update_say;
	private Result result;
	private String versionName;
	private String ground = "";
	private SharedPreferences sp;
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case UPDATE:
					PromptManager.closeProgressDialog();
					if(result != null) {
						if(result.getRows().equals("0")) {
							PromptManager.showErrorDialog(context, "您当前版本是" + versionName +",已是最新版本！");
						} else {
							alertUpdate();
						}
					} else {
						PromptManager.showToast(context, "数据获取失败");
					}
					break;
			}
		};
	};
	
	public UserFragment(Context context) {
		super(context);
	}

	@Override
	public void init() {
		showInMiddle = (LinearLayout) View.inflate(context, R.layout.user_fragment, null);
		other_user = (RelativeLayout) findViewById(R.id.other_user);
		update_paw = (RelativeLayout) findViewById(R.id.update_paw);
		other_material = (RelativeLayout) findViewById(R.id.other_material);
		other_update = (RelativeLayout) findViewById(R.id.other_update);
		other_exit = (RelativeLayout) findViewById(R.id.other_exit);
		update_share = (RelativeLayout) findViewById(R.id.update_share);
		update_say = (RelativeLayout) findViewById(R.id.update_say);
		sp = context.getSharedPreferences(ConstantValue.CONFIG, Context.MODE_PRIVATE);
	}

	@Override
	public void setListener() {
		other_user.setOnClickListener(this);
		update_paw.setOnClickListener(this);
		other_material.setOnClickListener(this);
		other_update.setOnClickListener(this);
		other_exit.setOnClickListener(this);
		update_share.setOnClickListener(this);
		update_say.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch(v.getId()) {
			case R.id.other_user:
				MiddleManager.getInstance().changeUI(UserMessageFragment.class);
				break;
			case R.id.update_paw:
				MiddleManager.getInstance().changeUI(UpdatePawFragment.class);
				break;
			case R.id.other_material:
				MiddleManager.getInstance().changeUI(MaterialFragment.class);
				break;
			case R.id.other_update:
				isUpdate();
				break;
			case R.id.other_exit:
				exitLogin();
				break;
			case R.id.update_share:
				// 是否只有已登录用户才能打开分享选择页
		        mController.openShare(MiddleManager.getInstance().activity, false);
				break;
			case R.id.update_say:
				if(ConstantValue.YOUKE.equals(ground)) {
					//游客 提示完善注册信息
					PromptManager.showRigister(context);
				} else {
					MiddleManager.getInstance().changeUI(MySayFragment.class);
				}
				break;
		}
	}
	
	private void exitLogin() {
		AlertDialog.Builder builder = new Builder(context);
		builder.setCancelable(false);
		builder.setTitle(R.string.app_name).setMessage("退出当前账号?").setPositiveButton("退出", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Editor ed = sp.edit();
				ed.remove("PASSWORD");
				ed.commit();
				Intent intent = new Intent(MiddleManager.getInstance().activity, LoginActivity.class);
				MiddleManager.getInstance().activity.startActivity(intent);
				MiddleManager.getInstance().clear();
				MiddleManager.getInstance().activity.finish();
				MiddleManager.getInstance().activity.overridePendingTransition(R.anim.left_in, R.anim.left_up);
			}
		}).setNegativeButton("取消", null).show();
	}

	private void isUpdate() {
		PromptManager.showProgressDialog(context, "检查中...");
		versionName = AppSystemUtils.getVersionName(context);
		new Thread(){
			@Override
			public void run() {
				super.run();
				result = WebServiceUtils.appv(versionName);
				Message msg = new Message();
				msg.what = UPDATE;
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	private String getVersion() {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "Version";
		}
	}
	
	private void alertUpdate() {
		//有新版本
		AlertDialog.Builder builder = new Builder(context);
		builder.setCancelable(false);
		builder.setTitle(R.string.app_name)
				.setMessage(result.getMessage())
				.setPositiveButton("更新", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						downLoadApk(result.getUrl());
					}
				}).setNegativeButton("不更新", null).show();
	};
	
	/**
	 * 下载apk文件
	 */
	protected void downLoadApk(String url) {
		final ProgressDialog pd = new ProgressDialog(context);
		pd.setTitle("正在下载");
		pd.setCancelable(false);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		FinalHttp fh = new FinalHttp();
		fh.download(url,"/sdcard/lemon.apk", new AjaxCallBack<File>() {
			@Override
			public void onSuccess(File t) {
				pd.dismiss();
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setDataAndType(Uri.fromFile(t),
						"application/vnd.android.package-archive");
				MiddleManager.getInstance().activity.startActivity(intent);
				MiddleManager.getInstance().activity.finish();
				super.onSuccess(t);
			}

			@Override
			public void onLoading(long count, long current) {
				pd.setMax((int) count);
				pd.setProgress((int) (current));
				super.onLoading(count, current);
			}
		});

	}

	@Override
	public int getID() {
		return ConstantValue.USERFRAGMENT;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		ground = sp.getString("GRADE", ConstantValue.YOUKE);
		if(ConstantValue.YEZHU.equals(ground)) {
			other_material.setVisibility(View.VISIBLE);
		} else {
			other_material.setVisibility(View.GONE);
		}
		mController.getConfig().removePlatform(SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN);
		MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(context);
		configPlatforms();
		setShareContent();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(context);
	}
	
	/**
     * 配置分享平台参数</br>
     */
    private void configPlatforms() {
        // 添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        // 添加腾讯微博SSO授权
        /*mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
        // 添加QQ、QZone平台
        addQQQZonePlatform();*/
        // 添加微信、微信朋友圈平台
        addWXPlatform();
    }
    
    /**
	 * 设置分享
	 */
	private void setShareContent() {
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// 设置分享内容
		mController.setShareContent("小伙伴们都在用天健E生活，快快加入吧！");
		// 设置分享图片, 参数2为图片的url地址
		mController.setShareMedia(new UMImage(MiddleManager.getInstance().activity, "http://www.lemon95.com/images/logo.jpg"));
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		/**使用SSO授权必须添加如下代码 */
	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}
	
	private void addWXPlatform() {
		String appID = ConstantValue.appId;
		String appSecret = ConstantValue.appSecret;
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(context,appID,appSecret);
		wxHandler.addToSocialSDK();
		// 添加微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(context,appID,appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

}
