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
 * 
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
	List<Map<String, String>> list;
	private String filePath;
	private String filePath2;
	protected int w, h;

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
		
	}

	/**
	 * 处理拍照后调用
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
	}

}
