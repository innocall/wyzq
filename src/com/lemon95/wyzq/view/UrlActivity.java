package com.lemon95.wyzq.view;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.utils.LogUtils;
import com.umeng.analytics.MobclickAgent;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

@SuppressLint("SetJavaScriptEnabled")
public class UrlActivity extends ActionBarActivity implements OnClickListener {

	private static final String TAG = UrlActivity.class.getSimpleName();
	private ImageButton top_back;
	private TextView top_title2;
	private WebView webView;
	private WebSettings webSettings;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		setContentView(R.layout.activity_webview);
		top_back = (ImageButton) findViewById(R.id.top_back);
		top_title2 = (TextView) findViewById(R.id.top_title2);
		webView = (WebView) findViewById(R.id.content);
		String title = getIntent().getStringExtra("title");
		url = getIntent().getStringExtra("url");
		top_title2.setText(title);
		top_back.setOnClickListener(this);
		showWebView();
	}

	private void showWebView() {
		//webSettings = webView.getSettings();
		webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true); // 支持js
//		webSettings.setUseWideViewPort(true); // 将图片调整到适合webview大小
//		webSettings.setLoadWithOverviewMode(true);
//		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // 关闭缓存
//		webSettings.setAllowFileAccess(true); // 可以访问文件
//		webSettings.setJavaScriptCanOpenWindowsAutomatically(true); // 支持通过JS打开新窗口
//		webSettings.setLoadsImagesAutomatically(true);// 支持自动加载图片
//		webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
//		webSettings.setBlockNetworkImage(true); // 图片放在后面加载
		//webView.requestFocus();
		//启用数据库  
		webSettings.setDatabaseEnabled(true);    
		//设置定位的数据库路径  
		String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath(); 
		webSettings.setGeolocationDatabasePath(dir);   
		//启用地理定位  
		webSettings.setGeolocationEnabled(true);  
		//开启DomStorage缓存
		webSettings.setDomStorageEnabled(true);
		webView.setWebViewClient(new WebViewClient() {
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				LogUtils.i(TAG, url);
				if("http://a.app.qq.com/o/simple.jsp?pkgname=com.Kingdee.Express".equals(url)) {
					return true;
				} else {
					view.loadUrl(url);
				}
				return super.shouldOverrideUrlLoading(view, url);
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
			}

			@Override
			public void onPageFinished(WebView view, String url) {
			}

			/* 错误处理 */
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				String tels = webView.getUrl();
				if (tels.startsWith("tel:")) {
					Uri uri = Uri.parse(tels);
					Intent intent = new Intent("android.intent.action.CALL", uri);
					startActivity(intent);
					webView.goBack(); // Web返回上一页
				} else {
					webView.loadUrl("file:///android_asset/error/error.html");
				}
			}
			
			@Override
		    public void onReceivedSslError(WebView view,SslErrorHandler handler,SslError error){
		        handler.proceed();
		    }

		});
		
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				//proDlg.setMessage("已经加载:"+newProgress+" %");
			}

			/* 构建Dialog显示网页alert对话框 */
			@Override
			public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
				// 构建一个Builder来显示网页中的alert对话框
				AlertDialog.Builder builder = new AlertDialog.Builder(UrlActivity.this);
				builder.setTitle("消息提示");
				builder.setMessage(message);
				builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
					}
				});
				builder.setCancelable(false);
				builder.create();
				builder.show();
				return true;
			}

			/**
			 * 覆盖默认的window.confirm展示界面，避免title里显示为
			 */
			public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
				builder.setTitle("消息提示").setMessage(message)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
					}
				}).setNeutralButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						result.cancel();
					}
				});
				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						result.cancel();
					}
				});

				// 禁止响应按back键的事件
				builder.setCancelable(false);
				AlertDialog dialog = builder.create();
				dialog.show();
				return true;
			}
			
			@Override
		    public void onReceivedIcon(WebView view, Bitmap icon) {
				super.onReceivedIcon(view, icon);
			}
			
			@Override
			public void onGeolocationPermissionsShowPrompt(String origin,Callback callback) {
				callback.invoke(origin, true, false);  
				super.onGeolocationPermissionsShowPrompt(origin, callback);
			}
		});
		
		webView.loadUrl(url);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			close();
			return true;
		} else if( keyCode == KeyEvent.KEYCODE_MENU ) {
			//屏蔽菜单键
	        return true;
	    }
		return false;
	}

	private void close() {
		LogUtils.i(TAG, webView.getUrl());
		if (webView.getUrl().equals("file:///android_asset/error/error.html")) {
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_up);
		} else if (webView.getUrl().equals(url)) {
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_up);
		} else {
			webView.goBack(); // Web返回上一页
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.top_back:
				close();
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
}
