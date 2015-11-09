package com.lemon95.wyzq.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.doordu.sdk.http.HttpResponseService;
import com.doordu.sdk.http.HttpResponseService.SuccessCallback;
import com.doordu.sdk.http.ResultInfo;
import com.doordu.sdk.model.DoorduRoom;
import com.lemon95.wyzq.R;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.manage.MiddleManager;
import com.lemon95.wyzq.myview.arclayout.Arc;
import com.lemon95.wyzq.myview.arclayout.ArcLayout;
import com.lemon95.wyzq.utils.LogUtils;
import com.lemon95.wyzq.view.BaseUI;
import com.umeng.analytics.MobclickAgent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * @author wu
 *
 */
public class OpenDorpFragment extends BaseUI {
	
	private final static String TAG = OpenDorpFragment.class.getSimpleName();
	protected static final int USERID = 0;
	protected static final int LOGIN = 1;
	private SharedPreferences sp;
	private String phone;
	private ArcLayout arcLayout;
	private String userID;  //注册的用户id，门禁中很多地方用到
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case LOGIN:
					login();
					break;
				case USERID:
					getRoom();
					break;
			}
		};
	};
	
	public OpenDorpFragment(Context context) {
		super(context);
	}

	@Override
	public void init() {
		showInMiddle = (LinearLayout) View.inflate(context, R.layout.opendrop_fragment, null);
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		phone = sp.getString("USERNAME", "");
		arcLayout = (ArcLayout) findViewById(R.id.arc_layout);
		arcLayout.setArc(Arc.BOTTOM);
	}

	@Override
	public void setListener() {
		
	}

	@Override
	public int getID() {
		return ConstantValue.OPENDORPFRAGMENT;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(context);
		// 注册只返回一个userID字符串，全文通用，很多请求都用到此id
		rigisterUser(phone);
	}
	
	/**
	 * 注册用户
	 * @param phone2 用户手机号
	 */
	private void rigisterUser(String phone2) {
		HttpResponseService.getInService().doorduRegisterd(context, phone2,
		        new SuccessCallback() {
		        @Override
		        public void onResult(ResultInfo info) {
		        	LogUtils.i(TAG, "rigister code = " + info.getRetCode());
		            if (info.getRetCode() == 200) {
		                userID = String.valueOf(info.getObj());
		                Message msg = new Message();
		                msg.what = LOGIN;
		                handler.sendMessage(msg);
		                LogUtils.i(TAG, "dorpID = " + userID);
		            }
		    }
		});
	}
	
	/**
	 * 登陆门禁
	 */
	private void login() {
		HttpResponseService.getInService().doorduLogin(context, phone,new SuccessCallback() {

			@Override
			public void onResult(ResultInfo info) {
				if (info.getRetCode() == 200) {
					LogUtils.i(TAG, "登陆成功");
					userID = String.valueOf(info.getObj());
					Message msg = new Message();
	                msg.what = USERID;
	                handler.sendMessage(msg);
				} else {
					LogUtils.i(TAG, "登陆失败");
				}
			}
			
		});
	}
	
	/**
	 * 获取房间门号
	 */
	private void getRoom() {
		//DoorduRoom为小区实体类，信息有门禁主要以及后面所开门或各种请求所需要的的参数
		final List<DoorduRoom> doorduRooms = new ArrayList<DoorduRoom>();
		HttpResponseService.getInService().getDoorduRoomList(context, userID, new SuccessCallback() {

	        @Override
	        public void onResult(ResultInfo arg0) {
	            if (arg0.getRetCode() == 200) {
	                doorduRooms.clear();
	                doorduRooms.addAll((Collection<? extends DoorduRoom>) arg0.getObj());
	                if (doorduRooms == null || doorduRooms.size() <= 0) {
	                    return;
		            }
		        }
	        }
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(context);
		MiddleManager.getInstance().clearNew(OpenDorpFragment.class);
	}

}
