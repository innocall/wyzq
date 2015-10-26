package com.lemon95.wyzq.view;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.model.Result;
import com.lemon95.wyzq.myview.textview.MyTextView;
import com.lemon95.wyzq.utils.ImageUtils;
import com.lemon95.wyzq.webserver.WebServiceUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
/**
 *读取视频的loading界面的配置 
 * 
 */
public class LoadingView2 extends RelativeLayout {
	
	private Context mContext;
	private MyTextView textView;
	private ImageView guanggao;
	private Timer time;
	private Result result;
	private String xqid;
	private int tvid;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case 0:
					showGuangGao();
					break;
				case 1:
					hiadGuangGao();
					break;
				case 2:
					if(result != null) {
						if(result.getRows().equals("1")) {
							String url = result.getMessage();
							downImage(url);
						}
					}
					break;
			}
		};
	};
	
	public void hiadGuangGao() {
		guanggao.setVisibility(View.GONE);
		guanggao.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.xuantai_but_gone));
	}
	
	/**
	 * 下载图片
	 */
	protected void downImage(String url) {
		final String fileEnd = url.substring(url.lastIndexOf("."), url.length());
		FinalHttp fh = new FinalHttp();
		fh.download(url,"/sdcard/" + tvid + fileEnd, new AjaxCallBack<File>() {
			@Override
			public void onSuccess(File t) {
				WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
				int width = wm.getDefaultDisplay().getWidth();
				int height = wm.getDefaultDisplay().getHeight();
				Bitmap bit = ImageUtils.decodeFileToCompress("/sdcard/" + tvid + fileEnd, width, height);
				guanggao.setImageBitmap(bit);
				time.schedule(new TimerTask() {
					@Override
					public void run() {
						Message msg = new Message();
						msg.what = 0;
						handler.sendMessage(msg);
					}
				}, 10000, 1000 * 60 * 60 );
				super.onSuccess(t);
			}

			@Override
			public void onLoading(long count, long current) {
				super.onLoading(count, current);
			}
		});
	}

	public void showGuangGao() {
		guanggao.setVisibility(View.VISIBLE);
		guanggao.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.xuantai_but));
		Message msg = new Message();
		msg.what = 1;
		handler.sendMessageDelayed(msg, 5000);//显示5s
		
	}
	
	public LoadingView2(Context context,String xqid,int tvid) {
		super(context);
		this.xqid = xqid;
		this.tvid = tvid;
		initView(context);
	}
	
	public LoadingView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	
	private void initView(Context context) {
		mContext = context;
		time = new Timer();
		View loadingView = View.inflate(context, R.layout.view_loading2, this);
		textView = (MyTextView) loadingView.findViewById(R.id.textView1);
		guanggao = (ImageView) loadingView.findViewById(R.id.guanggao);
		textView.setCircleTimes(100);
		textView.setSpeed(20);
		//加载广告
		new Thread(){
			public void run() {
				result = WebServiceUtils.xiaoqu_ad(xqid,tvid);
				Message msg = new Message();
				msg.what = 2;
				handler.sendMessage(msg);
			};
		}.start();
	}
	
	public void setText(String text) {
		textView.setText(text);
	}
	
	
	public void opPuse() {
		time.cancel();
	}
}
