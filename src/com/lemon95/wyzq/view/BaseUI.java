package com.lemon95.wyzq.view;

import java.util.List;
import java.util.Map;

import com.lemon95.wyzq.fragment.SayFragment;
import com.lemon95.wyzq.manage.MiddleManager;
import com.lemon95.wyzq.model.Result;
import com.lemon95.wyzq.utils.DisplayUtil;
import com.lemon95.wyzq.utils.ImageUtils;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.webserver.WebServiceUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * 所有界面的基类
 * @author wuxt
 */
public abstract class BaseUI implements View.OnClickListener {

	protected final static int CAMERA = 0;
	protected final static int PICTURE = 1;
	private final static int TYPE = 2;
	protected Context context;
	protected Bundle bundle;
	// 显示到中间容器
	protected ViewGroup showInMiddle;
	private Result res;
	List<Map<String,String>> list;
	private String filePath;
	private String filePath2;
	protected int w,h;
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch(TYPE) {
				case TYPE:
					if(res != null) {
						if(Integer.parseInt(res.getRows()) > 0) {
							list = res.getList();
							String []itmes = new String [list.size()];
							for(int i=0; i<list.size(); i++) {
								itmes[i] = list.get(i).get("title");
							}
							showListView(itmes);
						}
					} else {
						PromptManager.showToast(context, "没有获取到说说类型");
					}
					break;
			}
		};
	};

	public BaseUI(Context context) {
		this.context = context;
		Point point = DisplayUtil.getScreenMetrics(context);
		w = point.x;
		h = point.y;
		init();
		setListener();
	}

	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}

	/**
	 * 界面的初始化
	 * 
	 * @return
	 */
	public abstract void init();

	/**
	 * 设置监听
	 * 
	 * @return
	 */
	public abstract void setListener();

	/**
	 * 获取需要在中间容器加载的内容
	 * 
	 * @return
	 */
	public View getChild() {
		// 设置layout参数

		// root=null
		// showInMiddle.getLayoutParams()=null
		// root!=null
		// return root

		// 当LayoutParams类型转换异常，向父容器看齐
		if (showInMiddle.getLayoutParams() == null) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
			showInMiddle.setLayoutParams(params);
		}

		return showInMiddle;
	}

	/**
	 * 获取每个界面的标示——容器联动时的比对依据
	 * 
	 * @return
	 */
	public abstract int getID();

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	public View findViewById(int id) {
		return showInMiddle.findViewById(id);
	}

	/**
	 * 访问网络的工具
	 * 
	 * @author Administrator
	 * 
	 * @param <Params>
	 */
	/*
	 * protected abstract class MyHttpTask<Params> extends AsyncTask<Params,
	 * Void, Message> {
	 *//**
		 * 类似与Thread.start方法 由于final修饰，无法Override，方法重命名 省略掉网络判断
		 * 
		 * @param params
		 * @return
		 *//*
		 * public final AsyncTask<Params, Void, Message> executeProxy( Params...
		 * params) { if (NetUtil.checkNet(context)) { return
		 * super.execute(params); } else { PromptManager.showNoNetWork(context);
		 * } return null; }
		 * 
		 * }
		 */

	/**
	 * 要出去的时候调用
	 */
	public void onPause() {
	}

	/**
	 * 进入到界面之后
	 */
	public void onResume() {
	}

	/**
	 * 顶部右侧提交按钮
	 */
	public void submit() {
	}

	/**
	 * 返回按钮
	 */
	public void goBack() {
	}

	/**
	 * 查看消息按钮
	 */
	public void clickWatchMessage() {

	}

	/**
	 * 我要说说
	 */
	public void say() {
		filePath2 = ImageUtils.getPhotopath();
		PromptManager.pickIMage(context, CAMERA, PICTURE,filePath2); // 弹出选择照片框
	}

	/**
	 * 处理拍照后调用
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA && resultCode == Activity.RESULT_OK) {
			 Bitmap bitmap = ImageUtils.decodeFileToCompress2(filePath2,w - 80,h - 80); 
			 filePath = ImageUtils.saveScalePhoto(bitmap);  
			// 显示图片
			getType();
		}
		if (requestCode == PICTURE && resultCode == Activity.RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumns = { MediaStore.Images.Media.DATA };
			Cursor c = context.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
			if(c != null){
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePathColumns[0]);
				filePath = c.getString(columnIndex);
				c.close();
				// 获取图片并显示
				getType();
			}
		}
	}
	
	private void getType() {
		new Thread() {
			public void run() {
				res = WebServiceUtils.bbslist(0);
				Message msg = new Message();
				msg.what = TYPE;
				handler.sendMessage(msg);
			};
		}.start();
	}
	
	public void showListView(String []items) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("选择标签");
		builder.setItems(items, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Bundle bundle1 = new Bundle();
				bundle1.putString("filePath", filePath);
				Map<String,String> map = list.get(which);
				if(map != null) {
					bundle1.putString("id", map.get("id"));
					bundle1.putString("title", map.get("title"));
				}
				MiddleManager.getInstance().changeUI(SayFragment.class,bundle1);
			}
		});
		builder.show();
	}

}
