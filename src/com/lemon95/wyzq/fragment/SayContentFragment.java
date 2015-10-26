package com.lemon95.wyzq.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import com.lemon95.wyzq.R;
import com.lemon95.wyzq.adapter.PingLunAdapter;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.manage.MiddleManager;
import com.lemon95.wyzq.manage.TitleManager;
import com.lemon95.wyzq.model.Result;
import com.lemon95.wyzq.myview.pull.bitmapfun.util.ImageFetcher;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.view.BaseUI;
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
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SayContentFragment extends BaseUI {
	
	// 首先在您的Activity中添加如下成员变量
	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
	private static final String TAG = SayContentFragment.class.getSimpleName();
	private static final int IMG = 0;
	private static final int PINLUN = 1;
	private static final int ZAN = 2;
	private static final int DELETE = 3;
	private static final int ZANCOUNT = 4;
	private String cc;
	private String type;
	private String time;
	private String nickName;
	private String b2id;
	private String username;
	private String tss = "0";
	private int zanCount = 0;
	private TextView name_id;
	private TextView time_id;
	private ImageView big_image;
	private ImageView big_image2;
	private ImageView big_image3;
	private ImageView img1;
	private ImageView img2;
	private ImageView img3;
	private Result result;
	private Result result2;
	private Result result3;
	private Result result4;
	private Result result5;
	private List<Map<String,String>> list;
	private List<Map<String,String>> list2 = new ArrayList<Map<String,String>>();
	private ImageFetcher mImageFetcher;
	private LinearLayout sya_id;
	private LinearLayout fenxiang_id;
	private LinearLayout zan;
	private TextView news_title;
	private boolean isParam = true;
	private ListView lv_test;
	private PingLunAdapter adaper;
	private TextView pinlun_id;
	private TextView zan_count;
	private SharedPreferences sp;
	private LinearLayout caidan;
	private Button login_but;
	private Button quxiao_but;
	private String ground = "";
	private String isLive = "";
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
				case IMG:
					if(result != null) {
						if(Integer.parseInt(result.getRows()) > 0) {
							list = result.getList();
							for(int i =0 ;i<list.size();i++) {
								if( i==0) {
									img1.setVisibility(View.VISIBLE);
									mImageFetcher.loadImage(list.get(i).get("pic"), img1);
									big_image.setVisibility(View.VISIBLE);
									mImageFetcher.loadImage(list.get(i).get("pic"), big_image);
								} else if(i==1) {
									img2.setVisibility(View.VISIBLE);
									mImageFetcher.loadImage(list.get(i).get("pic"), img2);
									mImageFetcher.loadImage(list.get(i).get("pic"), big_image2);
								} else if(i==2) {
									img3.setVisibility(View.VISIBLE);
									mImageFetcher.loadImage(list.get(i).get("pic"), img3);
									mImageFetcher.loadImage(list.get(i).get("pic"), big_image3);
								}
							}
							getShareContent();
							return;
						}
					}
					PromptManager.showToast(context, "图片加载失败");
					break;
				case PINLUN:
					if(result2 != null) {
						if(Integer.parseInt(result2.getRows()) > 0) {
							List<Map<String,String>> li = result2.getList();
							pinlun_id.setText(li.size() + "评论");
							list2.clear();
							list2.addAll(li);
							adaper.notifyDataSetChanged();
							return;
						} 
					}
					break;
				case ZAN:
					if(result3 != null) {
						if(result3.getRows().equals("1")) {
							zanCount = zanCount + 1;
							zan_count.setText(zanCount + "赞");
							return;
						} else {
							PromptManager.showToast(context, result3.getMessage());
							return;
						}
					}
					PromptManager.showToast(context, "赞失败");
					break;
				case DELETE:
					PromptManager.closeProgressDialog();
					if(result4 != null) {
						if(result4.getRows().equals("1")) {
//							PromptManager.showToast(context, "删除成功");
//							Bundle bundle2 = new Bundle();
//							bundle2.putBoolean("isParam", true);
							if("0".equals(tss)) {
								MiddleManager.getInstance().clearNew(MainFragment.class);
								MiddleManager.getInstance().changeUI(MainFragment.class);
							} else {
								MiddleManager.getInstance().clearNew(MySayFragment.class);
								MiddleManager.getInstance().changeUI(MySayFragment.class);
							}
							return;
						} else {
							PromptManager.showToast(context, "删除失败");
							return;
						}
					}
					PromptManager.showToast(context, "删除失败");
					break;
				case ZANCOUNT:
					if(result5 != null) {
						if(result5.getRows().equals("1")) {
							List<Map<String,String>> list = result5.getList();
							if(list != null && list.size() > 0){
								zan_count.setText(list.get(0).get("zan") + "赞");
							}
						} 
					}
					break;
			}
		}
		
	};
	
	public SayContentFragment(Context context) {
		super(context);
	}

	@Override
	public void init() {
		showInMiddle = (LinearLayout) View.inflate(context, R.layout.say_content_fragment, null);
		lv_test = (ListView) findViewById(R.id.lv_test);
		caidan = (LinearLayout) findViewById(R.id.caidan);
		login_but = (Button) findViewById(R.id.login_but);
		quxiao_but = (Button) findViewById(R.id.quxiao_but);
		View view = LayoutInflater.from(context).inflate(R.layout.say_top_item, null);
		name_id = (TextView) view.findViewById(R.id.name_id);
		time_id = (TextView) view.findViewById(R.id.time_id);
		pinlun_id = (TextView) view.findViewById(R.id.pinlun_id);
		zan_count = (TextView) view.findViewById(R.id.zan_count);
		news_title = (TextView) view.findViewById(R.id.news_title);
		big_image = (ImageView) view.findViewById(R.id.big_image);
		big_image2 = (ImageView) view.findViewById(R.id.big_image2);
		big_image3 = (ImageView) view.findViewById(R.id.big_image3);
		img1 = (ImageView) view.findViewById(R.id.img1);
		img2 = (ImageView) view.findViewById(R.id.img2);
		img3 = (ImageView) view.findViewById(R.id.img3);
		sya_id = (LinearLayout) view.findViewById(R.id.sya_id);
		fenxiang_id = (LinearLayout) view.findViewById(R.id.fenxiang_id);
		zan = (LinearLayout) view.findViewById(R.id.zan);
		lv_test.addHeaderView(view);
		mImageFetcher = new ImageFetcher(context, 240);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo);
		mImageFetcher.setExitTasksEarly(false);
		sp = context.getSharedPreferences(ConstantValue.CONFIG, Context.MODE_PRIVATE);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(context);
		mController.getConfig().removePlatform(SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN);
		cc = bundle.getString("cc", "");
		type = bundle.getString("type", "");
		time = bundle.getString("time", "");
		nickName = bundle.getString("nickName", "");
		username = bundle.getString("username", "");
		tss = bundle.getString("tss", "0");
		ground = sp.getString("GRADE", ConstantValue.YOUKE);
		isLive = sp.getString("ISLIVE", "");
		String userName = sp.getString("USERNAME", "");
		if(userName.equals(username)) {
			TitleManager.getInstance().hideTitle4(true);
		} else {
			TitleManager.getInstance().hideTitle4(false);
		}
		b2id = bundle.getString("b2id", "");
		zanCount = bundle.getInt("zan");
		zan_count.setText(zanCount + "赞");
		name_id.setText(nickName);
		time_id.setText(time.substring(5, time.length()));
		String color = "F8B62D";
        if("邻里互助".equals(type)) {
        	color = "F8B62D";
        } else if("乐趣分享".equals(type)) {
        	color = "EA5353";
        } else if("跳蚤市场".equals(type)) {
        	color = "2ECC71";
        } else if("邻里商机".equals(type)) {
        	color = "3498DB";
        } else if("投诉建议".equals(type)) {
        	color = "9B59B6";
        } else if("活动发起".equals(type)) {
        	color = "E74C3C";
        } else if("乐趣分享".equals(type)) {
        	color = "E67E22";
        }
        type = " " + type + " ";
        int end = type.length();
		SpannableStringBuilder style=new SpannableStringBuilder(type + " " + cc);
        style.setSpan(new BackgroundColorSpan(Color.parseColor("#" + color)),0,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.WHITE), 0, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        news_title.setText(style);
		getImage(b2id);
		SharedPreferences sp = context.getSharedPreferences(ConstantValue.CONFIG, Context.MODE_PRIVATE);
		getPingLun(sp.getString("USERNAME", ""));
		adaper = new PingLunAdapter(context,list2);
		lv_test.setAdapter(adaper);
		configPlatforms();
		//获取赞数量
		getZan(b2id);
	}

	private void getZan(String b2id2) {
		new Thread(){
			public void run() {
				result5 = WebServiceUtils.bbsinfo(b2id);
				Message msg = new Message();
				msg.what = ZANCOUNT;
				handler.sendMessage(msg);
			};
		}.start();
	}

	/**
	 * 获取评论
	 * @param b2id2
	 * @param string
	 */
	private void getPingLun(final String userName) {
		new Thread(){
			public void run() {
				result2 = WebServiceUtils.bbscomment(b2id,1000,0,"");
				Message msg = new Message();
				msg.what = PINLUN;
				handler.sendMessage(msg);
			};
		}.start();
	}

	/**
	 * 获取照片
	 * @param b2id
	 */
	private void getImage(final String b2id) {
		new Thread(){
			public void run() {
				result = WebServiceUtils.bbspic(b2id);
				Message msg = new Message();
				msg.what = IMG;
				handler.sendMessage(msg);
			};
		}.start();
	}

	@Override
	public void setListener() {
		img1.setOnClickListener(this);
		img2.setOnClickListener(this);
		img3.setOnClickListener(this);
		sya_id.setOnClickListener(this);
		big_image.setOnClickListener(this);
		big_image2.setOnClickListener(this);
		big_image3.setOnClickListener(this);
		fenxiang_id.setOnClickListener(this);
		zan.setOnClickListener(this);
		login_but.setOnClickListener(this);
		quxiao_but.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch(v.getId()) {
			case R.id.img1:
				big_image.setVisibility(View.VISIBLE);
				big_image2.setVisibility(View.GONE);
				big_image3.setVisibility(View.GONE);
				break;
			case R.id.img2:
				big_image.setVisibility(View.GONE);
				big_image2.setVisibility(View.VISIBLE);
				big_image3.setVisibility(View.GONE);
				break;
			case R.id.img3:
				big_image.setVisibility(View.GONE);
				big_image2.setVisibility(View.GONE);
				big_image3.setVisibility(View.VISIBLE);
				break;
			case R.id.sya_id:
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
					Bundle bundle2 = new Bundle();
					bundle2.putString("b2id", b2id);
					bundle2.putString("nickName", nickName);
					bundle2.putString("cc", cc);
					bundle2.putString("type", type);
					bundle2.putString("time", time);
					bundle2.putString("username", username);
					MiddleManager.getInstance().changeUI(PingLunFragment.class,bundle2);
				}
				break;
			case R.id.big_image:
				if(list != null && list.size() > 0) {
					witchImage(list.get(0).get("pic"),1);
				}
				break;
			case R.id.big_image2:
				if(list != null && list.size() > 1) {
					witchImage(list.get(1).get("pic"),2);
				}
				break;
			case R.id.big_image3:
				if(list != null && list.size() > 2) {
					witchImage(list.get(2).get("pic"),3);
				}
				break;
			case R.id.fenxiang_id:
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
					// 是否只有已登录用户才能打开分享选择页
			        mController.openShare(MiddleManager.getInstance().activity, false);
				}
				break;
			case R.id.zan:
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
					bbszan();
				}
				break;
			case R.id.login_but:
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
					//删除
					delete();
				}
				break;
			case R.id.quxiao_but:
				if(caidan.getVisibility() == View.VISIBLE) {
					caidan.setVisibility(View.GONE);
					caidan.startAnimation(AnimationUtils.loadAnimation(context, R.anim.caidan_out));
				}
				break;
		}
	}
	
	private void delete() {
		AlertDialog.Builder builder = new Builder(context);
		builder.setCancelable(false);
		builder.setTitle(R.string.app_name).setMessage("是否确认删除?").setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteBss();
			}
		}).setNegativeButton("取消", null).show();
	}

	protected void deleteBss() {
		PromptManager.showProgressDialog(context, "删除中，请稍候...");
		new Thread() {
			public void run() {
				result4 = WebServiceUtils.bbsdelete(username,b2id);
				Message msg = new Message();
				msg.what = DELETE;
			    handler.sendMessage(msg);
			};
		}.start();
	}

	private void bbszan() {
		 new Thread(){
			 public void run() {
				 result3 = WebServiceUtils.bbszan(b2id);
				 Message msg = new Message();
				 msg.what = ZAN;
				 handler.sendMessage(msg);
			 };
		 }.start();
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

	/**
	 * 查看图片,后期添加
	 * @param string
	 */
	private void witchImage(String picPath,int count) {
		String pic = picPath;
		for(int i =0 ;i<list.size();i++) {
			if( i==0) {
				String str = list.get(i).get("pic");
				if(!picPath.equals(str)) {
					pic = pic + "<" + str;
				} 
			} else if(i==1) {
				String str = list.get(i).get("pic");
				if(!picPath.equals(str)) {
					pic = pic + "<" + str;
				} 
			} else if(i==2) {
				String str = list.get(i).get("pic");
				if(!picPath.equals(str)) {
					pic = pic + "<" + str;
				}
			}
		}
		Intent intent = new Intent();
		intent.setClassName("com.lemon95.wyzq", "com.lemon95.wyzq.view.ImageViewActivity");
		intent.putExtra("picPath", picPath);
		intent.putExtra("pic", pic);
		intent.putExtra("count", list.size());
		MiddleManager.getInstance().activity.startActivity(intent);
	}

	@Override
	public int getID() {
		return ConstantValue.SAYCONTENTFRAGMENT;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MiddleManager.getInstance().clearNew(SayContentFragment.class);
		MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(context);
	}
	
	@Override
	public void submit() {
		super.submit();
		if(caidan.getVisibility() == View.GONE) {
			caidan.setVisibility(View.VISIBLE);
			caidan.startAnimation(AnimationUtils.loadAnimation(context, R.anim.caidan_in));
		}
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
	
	/**
	 * 设置分享
	 */
	private void getShareContent() {
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// 设置分享内容
		mController.setShareContent(cc);
		// 设置分享图片, 参数2为图片的url地址
		if(list.size() > 0) {
			mController.setShareMedia(new UMImage(MiddleManager.getInstance().activity, list.get(0).get("pic")));
		}
	}

}
