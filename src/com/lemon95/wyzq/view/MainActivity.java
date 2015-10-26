package com.lemon95.wyzq.view;

import com.feiyucloud.sdk.FYCall;
import com.feiyucloud.sdk.FYCallListener;
import com.feiyucloud.sdk.FYClient;
import com.feiyucloud.sdk.FYClientListener;
import com.feiyucloud.sdk.FYError;
import com.lemon95.wyzq.R;
import com.lemon95.wyzq.application.ExitApplication;
import com.lemon95.wyzq.fragment.MainFragment;
import com.lemon95.wyzq.manage.BottomManager;
import com.lemon95.wyzq.manage.MiddleManager;
import com.lemon95.wyzq.manage.TitleManager;
import com.lemon95.wyzq.utils.LogUtils;
import com.starschina.media.ThinkoEnvironment;
import com.umeng.analytics.MobclickAgent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements FYClientListener{
	
	private static final String TAG = MainActivity.class.getSimpleName();
	private RelativeLayout middle;// 中间占着位置的容器
	private long waitTime = 2000;
	private long touchTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
	    getActionBar().hide();
		setContentView(R.layout.main_activity);
		
//		FYClient.instance().init(getApplicationContext(), "111", true);
//	    FYClient.addListener(this);
		
		ExitApplication.getInstance().addActivity(this);
		//友盟统计
		MobclickAgent.openActivityDurationTrack(false);
		init();
	}

	private void init() {
		TitleManager manager = TitleManager.getInstance();
		manager.init(this);
		manager.showCommonTitle("首页");

		BottomManager.getInstrance().init(this);
		BottomManager.getInstrance().showOrderBackColor();

		// 初始化中间控件
		middle = (RelativeLayout) findViewById(R.id.ii_middle);
		MiddleManager.getInstance().setMiddle(middle);
		MiddleManager.getInstance().setActivity(this);

		// 建立观察者和被观察者之间的关系（标题和底部导航添加到观察者的容器里面）
		MiddleManager.getInstance().addObserver(TitleManager.getInstance());
		MiddleManager.getInstance().addObserver(BottomManager.getInstrance());
		MiddleManager.getInstance().changeUI(MainFragment.class);
	}

	/**
	 * 出来返回值
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		MiddleManager.getInstance().onActivityResult(requestCode, resultCode,
				data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			boolean result = MiddleManager.getInstance().goBack(); // 返回键操作失败
			if (!result) { 
				//Toast.makeText(MainActivity.this, "是否退出系统",1).show(); PromptManager.showExitSystem(this); 
				long currentTime = System.currentTimeMillis();
				if ((currentTime - touchTime) >= waitTime) {
					Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
					touchTime = currentTime;
				} else {
					MobclickAgent.onKillProcess(this);
					ExitApplication.getInstance().exit();
				}
			}
			return false;
			//return true;
		} else if(keyCode == KeyEvent.KEYCODE_MENU) {
			return false;
		}
	
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		//初始化 电话
	    /*if (!FYClient.instance().isConnected()) {
            String appId = "4F239DE03CDAA5F5C6BF7CB4174D2D8E";
            String appToken = "100E71E1C170C133D69074231D2B03D6";
            String accountId = "FY4F239OHJ7M6";
            String accountPwd = "JP40WD";
            FYClient.instance().connect(appId, appToken, accountId, accountPwd);
        } else {
            LogUtils.i(TAG,"FeiyuClient already connected.");
        } */
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		overridePendingTransition(R.anim.left_in, R.anim.left_up);
	}
	
    @Override
    public void onConnecting() {
    	// 正在连接
    	LogUtils.i(TAG, "正在连接");
    }
     
    @Override
    public void onConnectionFailed(FYError error) {
    	// 连接失败
    	LogUtils.i(TAG, "正在失败");
    }
     
    @Override
    public void onConnectionSuccessful() {
    	// 连接成功
    	LogUtils.i(TAG, "正在成功");
    }
    
    @Override
    protected void onDestroy() {
       /* if (FYClient.instance().isConnected()) {
        	LogUtils.i(TAG,"disconnect FeiyuClient");
            FYClient.instance().disconnect();
        } else {
        	LogUtils.i(TAG,"FeiyuClient already disconnected.");
        }*/
    	//sdk释放
		LogUtils.i(TAG, "释放sdk");
		ThinkoEnvironment.tearDown();
        super.onDestroy();
    }
	
   
}
