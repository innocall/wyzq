package com.lemon95.wyzq.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.application.ExitApplication;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.webserver.WebServiceUtils;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TypeActivity extends ActionBarActivity implements OnClickListener{
	
	private static final String TAG = TypeActivity.class.getSimpleName();
	private final static int XIAOQU = 0;
	private RelativeLayout ii_common_container;
	private RelativeLayout ii_login_title;
	private RelativeLayout ii_login_title3;
	private ImageButton top_back;
	private TextView top_title2;
	private LinearLayout accetp_ll_load;
	private ImageView accetp_iv_load;
	private TextView accept_no_order1;
	private ListView listView;
	private List<Map<String,String>> list = new ArrayList<Map<String,String>> ();
	private AnimationDrawable animaition;
	private MyXiaoQuAdapter mAdapter;
	private String type;
	private String city;
	public static TypeActivity instance = null;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case XIAOQU:
					if(list != null && list.size() > 0) {
						mAdapter.notifyDataSetChanged();
						accetp_ll_load.setVisibility(View.GONE);
						listView.setVisibility(View.VISIBLE);
					} else {
						animaition.stop();
						accetp_iv_load.setVisibility(View.GONE);
						accept_no_order1.setText("没有获取到小区列表");
					}
					break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		setContentView(R.layout.type_container);
		type = getIntent().getStringExtra("type");
		city = getIntent().getStringExtra("city");
		if(StringUtils.isBlank(city)) {
			city = "";
		}
		ExitApplication.getInstance().addActivity(this);
		instance = this;
		initView();
		setListener();
	}
	
	private void setListener() {
		top_back.setOnClickListener(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				/*Intent intent = new Intent();
				intent.putExtra("id", map.get("id"));
				intent.putExtra("name", map.get("name"));
				setResult(1, intent);
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);*/
				Map<String,String> map = list.get(position);
				Intent intent = new Intent(getApplicationContext(),TypeFangHaoActivity.class);
				intent.putExtra("id", map.get("id"));
				intent.putExtra("name", map.get("name"));
				intent.putExtra("city", city);
				intent.putExtra("type", type);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.left_up);
			}
		});
	}
	
	private void initView() {
		ii_common_container = (RelativeLayout) findViewById(R.id.ii_common_container);
		ii_login_title = (RelativeLayout) findViewById(R.id.ii_login_title);
		ii_login_title3 = (RelativeLayout) findViewById(R.id.ii_login_title3);
		top_back = (ImageButton) findViewById(R.id.top_back);
		top_title2 = (TextView) findViewById(R.id.top_title2);
		accept_no_order1 = (TextView) findViewById(R.id.accept_no_order1);
		accetp_ll_load = (LinearLayout) findViewById(R.id.accetp_ll_load);
		accetp_iv_load = (ImageView) findViewById(R.id.accetp_iv_load);
		listView = (ListView) findViewById(R.id.list);
		ii_common_container.setVisibility(View.GONE);
		ii_login_title.setVisibility(View.VISIBLE);
		ii_login_title3.setVisibility(View.GONE);
		top_title2.setText("选择小区");
		accetp_iv_load.setBackgroundResource(R.anim.load);
		animaition = (AnimationDrawable)accetp_iv_load.getBackground();
		animaition.setOneShot(false);
		animaition.start();
		accetp_iv_load.setVisibility(View.VISIBLE);
		accetp_ll_load.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onResume() {
		super.onRestart();
		MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this);
		mAdapter = new MyXiaoQuAdapter();
		listView.setAdapter(mAdapter);
		initSpinner();
	}

	/**
	 * 初始化小区
	 */
	private void initSpinner() {
		new Thread(){
			public void run() {
				list = WebServiceUtils.xiaoqu("", city, 1000, 0,"");
				Message msg = new Message();
				msg.what = XIAOQU;
				handler.sendMessage(msg);
			};
		}.start();
	}
	
	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.top_back:
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				break;
		}
	}
	
	class MyXiaoQuAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Map<String,String> result = list.get(position);
			ViewHolder holder = null;
			if(convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(), R.layout.type_item, null);
				holder.xiaoqu_name = (TextView) convertView.findViewById(R.id.xiaoqu_name);
				holder.xiaoqu_id = (TextView) convertView.findViewById(R.id.xiaoqu_id);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.xiaoqu_name.setText(result.get("name"));
			holder.xiaoqu_id.setText(result.get("id"));
			return convertView;
		}
		
		class ViewHolder{
			TextView xiaoqu_name;
			TextView xiaoqu_id;
		}
		
	}
	
}
