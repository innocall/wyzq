package com.lemon95.wyzq.utils;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.fragment.MainFragment;
import com.lemon95.wyzq.manage.MiddleManager;
import com.lemon95.wyzq.myview.dialog.CustomProgressDialog;
import com.lemon95.wyzq.webserver.WebServiceUtils;

import android.R.animator;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 提示信息的管�?
 */

public class PromptManager {
	private static ProgressDialog dialog;
	private static AlertDialog mAlertDialog;
	private static CustomProgressDialog dialog2;
	
	public static void showProgressDialog(Context context, String str) {
		dialog = new ProgressDialog(context);
		dialog.setCancelable(false);
		dialog.setMessage(str);
		dialog.show();
	}

	public static void closeProgressDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}
	
	public static void startProgressDialog(Context context){  
        if (dialog2 == null){  
        	dialog2 = CustomProgressDialog.createDialog(context);  
        	dialog2.hideText();  
        }  
        dialog2.setCancelable(false);
        dialog2.show();  
    }  
	
	public static void stopProgressDialog(){  
        if (dialog2 != null){  
        	dialog2.dismiss();  
        	dialog2 = null;  
        }  
    }  

	/**
	 * 当判断当前手机没有网络时使用
	 * 
	 * @param context
	 */
	public static void showNoNetWork(final Context context) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setCancelable(false);
		builder.setTitle(R.string.app_name).setMessage("当前无网络?").setPositiveButton("设置", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 跳转到系统的网络设置界面
				if (android.os.Build.VERSION.SDK_INT > 10) {
					// 3.0以上打开设置界面
					context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
				} else {
					context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
				}
			}
		}).setNegativeButton("知道了?", null).show();
	}


	/**
	 * 显示错误提示
	 * 
	 * @param context
	 * @param msg
	 */
	public static void showErrorDialog(Context context, String msg) {
		new AlertDialog.Builder(context)//
				.setIcon(R.drawable.logo_min)//
				.setTitle(R.string.app_name)//
				.setCancelable(false)//
				.setMessage(msg)//
				.setNegativeButton(context.getString(R.string.is_positive), null)//
				.show();
	}

	public static void showToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

	public static void showToast(Context context, int msgResId) {
		Toast.makeText(context, msgResId, Toast.LENGTH_LONG).show();
	}

	// 当测试阶段时true
	private static final boolean isShow = true;

	/**
	 * @param context
	 * @param msg
	 */
	public static void showToastTest(Context context, String msg) {
		if (isShow) {
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		}
	}
	
	public static void closeAlertDialog() {
		if(mAlertDialog != null) {
			mAlertDialog.hide();
		}
	}
	
	public static void pickIMage(final Context context,final int CAMERA,final int PICTURE,final String filePath) {
		AlertDialog.Builder builder = new Builder(context);
		mAlertDialog = builder.create();
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.pic_dialog, null);
		LinearLayout cam_id = (LinearLayout) view.findViewById(R.id.cam_id);
		cam_id.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				camera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);  
                File out = new File(filePath);  
                Uri uri = Uri.fromFile(out);  
                // 获取拍照后未压缩的原图片，并保存在uri路径中  
                camera.putExtra(MediaStore.EXTRA_OUTPUT, uri);   
				MiddleManager.getInstance().activity.startActivityForResult(camera, CAMERA);
			}
		});
		LinearLayout pic_id = (LinearLayout) view.findViewById(R.id.pic_id);
		pic_id.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent picture = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				MiddleManager.getInstance().activity.startActivityForResult(picture, PICTURE);
			}
		});
		mAlertDialog.show();
		mAlertDialog.getWindow().setContentView(view);
	}
	
	
	public static void pickVideo(final Context context,final int CAMERA,final int PICTURE) {
		AlertDialog.Builder builder = new Builder(context);
		mAlertDialog = builder.create();
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.pic_video_dialog, null);
		LinearLayout cam_id = (LinearLayout) view.findViewById(R.id.cam_id);
		cam_id.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// MediaStore.ACTION_VIDEO_CAPTURE
				// 从一个既存的Camera应用中申请视频功能的Intent动作类型
				Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
				// MediaStore.EXTRA_VIDEO_QUALITY：这个值的范围是0~1，0的时候质量最差且文件最小，1的时候质量最高且文件最大。
				intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
				MiddleManager.getInstance().activity.startActivityForResult(intent, CAMERA);
			}
		});
		LinearLayout pic_id = (LinearLayout) view.findViewById(R.id.pic_id);
		pic_id.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();  
				 /* 开启Pictures画面Type设定为image */  
				 //intent.setType(“image/*”); 
				 //intent.setType(“audio/*”); //选择音频 
				 //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式） 
				 //intent.setType(“video/*;image/*”);//同时选择视频和图片   
				 /* 使用Intent.ACTION_GET_CONTENT这个Action */  
				intent.setAction(Intent.ACTION_GET_CONTENT);  
				intent.setType("video/*"); 
				MiddleManager.getInstance().activity.startActivityForResult(intent, PICTURE);
			}
		});
		mAlertDialog.show();
		mAlertDialog.getWindow().setContentView(view);
	}
	
	public static void showRigister(final Context context) {
		AlertDialog.Builder builder = new Builder(context);
		mAlertDialog = builder.create();
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.youke_msg, null);
		TextView quxiao = (TextView) view.findViewById(R.id.quxiao);
		quxiao.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mAlertDialog.hide();
			}
		});
		TextView queding = (TextView) view.findViewById(R.id.queding);
		queding.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mAlertDialog.hide();
				Intent intent = new Intent();
				intent.putExtra("isParam", "1");
				intent.setClassName("com.lemon95.wyzq", "com.lemon95.wyzq.view.RigisterActivity");
				MiddleManager.getInstance().activity.startActivity(intent);
				MiddleManager.getInstance().clear();
				MiddleManager.getInstance().activity.finish();
				MiddleManager.getInstance().activity.overridePendingTransition(R.anim.left_in, R.anim.left_up);
			}
		});
		mAlertDialog.show();
		mAlertDialog.getWindow().setContentView(view);
	}

	public static void showJiHuo(final Context context,final String phone,final String remobile) {
		AlertDialog.Builder builder = new Builder(context);
		mAlertDialog = builder.create();
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.youke_msg, null);
		TextView quxiao = (TextView) view.findViewById(R.id.quxiao);
		TextView msg = (TextView) view.findViewById(R.id.msg);
		msg.setText("此功能只对激活账号开放，您的账号还未激活，请通知房主激活您的账号！");
		quxiao.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mAlertDialog.hide();
			}
		});
		TextView queding = (TextView) view.findViewById(R.id.queding);
		queding.setText("通知房主");
		queding.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mAlertDialog.hide();
				PromptManager.showToast(context, "短信发送成功");
				String msg = "手机号为：" + phone + "的用户，邀请您激活您的'天健E生活'下他的房下账号！【柠檬95】";
				sendMsg(remobile,msg);
			}
		});
		mAlertDialog.show();
		mAlertDialog.getWindow().setContentView(view);
	}
	
	protected static void sendMsg(final String remobile,final String msg) {
		new Thread(){
			public void run() {
				WebServiceUtils.senYzm(remobile, msg);
			};
		}.start();
	}
	
}
