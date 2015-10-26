package com.lemon95.wyzq.fragment;

import java.util.ArrayList;
import com.lemon95.wyzq.R;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.manage.MiddleManager;
import com.lemon95.wyzq.model.Channel;
import com.lemon95.wyzq.utils.AsyncImageLoader;
import com.lemon95.wyzq.utils.LogUtils;
import com.lemon95.wyzq.utils.NetUtil;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.view.BaseUI;
import com.lemon95.wyzq.webserver.WebServiceUtils;
import com.starschina.media.ThinkoEnvironment;
import com.starschina.media.ThinkoEnvironment.OnGetChannelsListener;
import com.starschina.types.DChannel;
import com.starschina.types.Epg;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class VideoListFragment extends BaseUI {

	private static final String TAG = VideoListFragment.class.getSimpleName();
	private final static int ISSHANG = 0;
	private ListView listVideo;
	private LinearLayout accetp_ll_load;
	private ImageView accetp_iv_load;
	private TextView accept_no_order1;
	private ArrayList<Channel> resultList;
	private AsyncImageLoader asyncImageLoader;
	private MyListAdapter mAdapter;
	private AnimationDrawable animaition;
	private TextView tab1;
	private TextView tab2;
	private TextView tab3;
	private int point = 2;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ISSHANG:
				if(resultList != null) {
					if(resultList.size() > 0) {
						mAdapter.setData(resultList);
						mAdapter.notifyDataSetChanged();
						accetp_ll_load.setVisibility(View.GONE);
						listVideo.setVisibility(View.VISIBLE);
					} else {
						animaition.stop();
						accetp_iv_load.setVisibility(View.GONE);
						accept_no_order1.setText("没有获取到节目列表");
					}
				} else {
					animaition.stop();
					accetp_iv_load.setVisibility(View.GONE);
					accept_no_order1.setText("没有获取到节目列表");
				}
				break;
			}
		}
	};
	
	public VideoListFragment(Context context) {
		super(context);
	}

	@Override
	public void init() {
		//初始化sdk
		try{
			ThinkoEnvironment.setUp(context);
		} catch(Exception e) {
			LogUtils.i(TAG, "key错误");
		}
		showInMiddle = (LinearLayout) View.inflate(context, R.layout.videolist_fragment, null);
		
		accetp_ll_load = (LinearLayout)findViewById(R.id.accetp_ll_load);
		accetp_iv_load = (ImageView)findViewById(R.id.accetp_iv_load);
		accept_no_order1 = (TextView)findViewById(R.id.accept_no_order1);
		tab1 = (TextView)findViewById(R.id.tab1);
		tab2 = (TextView)findViewById(R.id.tab2);
		tab3 = (TextView)findViewById(R.id.tab3);
		accetp_iv_load.setBackgroundResource(R.anim.load);
		animaition = (AnimationDrawable)accetp_iv_load.getBackground();
		animaition.setOneShot(false);
		animaition.start();
		accetp_iv_load.setVisibility(View.VISIBLE);
		accetp_ll_load.setVisibility(View.VISIBLE);
		asyncImageLoader = new AsyncImageLoader(context,200,200);
		
		listVideo = (ListView) findViewById(R.id.list);
		
		mAdapter = new MyListAdapter();
		listVideo.setAdapter(mAdapter);
	}

	@Override
	public void setListener() {
		tab1.setOnClickListener(this);
		tab2.setOnClickListener(this);
		tab3.setOnClickListener(this);
		listVideo.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				isNet(arg2);
			}
		});
	}

	/**
	 * 判断网络连接方式
	 */
	protected void isNet(final int arg2) {
		if(NetUtil.isNetWorkConnected(context)) {
			if(NetUtil.isWIFIConnection(context)) {
				Intent intent = new Intent();
				intent.putExtra("channel", mAdapter.getItem(arg2));
				//intent.setClassName("com.lemon95.wyzq", "com.lemon95.wyzq.view.AdImageView");
				intent.setClassName("com.lemon95.wyzq", "com.lemon95.wyzq.view.VideoActivity");
				MiddleManager.getInstance().activity.startActivityForResult(intent, 1);
			} else {
				AlertDialog.Builder builder = new Builder(context);
				builder.setCancelable(false);
				builder.setTitle(R.string.app_name).setMessage("您当前用的是手机流量，确认播放?").setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.putExtra("channel", mAdapter.getItem(arg2));
						//intent.setClassName("com.lemon95.wyzq", "com.lemon95.wyzq.view.AdImageView");
						intent.setClassName("com.lemon95.wyzq", "com.lemon95.wyzq.view.VideoActivity");
						MiddleManager.getInstance().activity.startActivityForResult(intent, 1);
					}
				}).setNegativeButton("取消", null).show();
			}
		} else {
			PromptManager.showToast(context, "请检查您的网络");
		}
	}

	@Override
	public int getID() {
		return ConstantValue.VIDEOLISTFRAGMENT;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//通过api获取频道列表
		getChannenllist(point);
		MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(context);
	}
	
	public void getChannenllist(final long bid){
		if(bid == 2) {
			tab1.setBackgroundColor(Color.parseColor("#ffffff"));
			tab2.setBackgroundColor(Color.parseColor("#efefef"));
			tab3.setBackgroundColor(Color.parseColor("#efefef"));
		} else if(bid == 1) {
			tab1.setBackgroundColor(Color.parseColor("#efefef"));
			tab2.setBackgroundColor(Color.parseColor("#ffffff"));
			tab3.setBackgroundColor(Color.parseColor("#efefef"));
		} else if(bid == 3) {
			tab1.setBackgroundColor(Color.parseColor("#efefef"));
			tab2.setBackgroundColor(Color.parseColor("#efefef"));
			tab3.setBackgroundColor(Color.parseColor("#ffffff"));
		}
		
		ThinkoEnvironment.getChannelList(new OnGetChannelsListener() {

			@Override
			public void getChannelList(DChannel[] channellist) {
				isUpLive(channellist,bid);
			}
		});
	}
	
	protected void isUpLive(final DChannel[] channellist,final long bid) {
		if(resultList != null) {
			resultList.clear();
		}
		new Thread(){
			public void run() {
				resultList = WebServiceUtils.tv(channellist,bid);
				Message msg = new Message();
				msg.what = ISSHANG;
				handler.sendMessage(msg);
			};
		}.start();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		//MiddleManager.getInstance().clearNew(VideoListFragment.class);
		MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(context);
		//sdk释放
		//ThinkoEnvironment.tearDown();
	}
	
	private class MyListAdapter extends BaseAdapter {
			
		private ArrayList<Channel> mDatas= new ArrayList<Channel>();
		
		public void setData(ArrayList<Channel> channellist){
			mDatas.clear();
			mDatas.addAll(channellist);
		}
		
		@Override
		public int getCount() {
			return mDatas != null ? mDatas.size() : 0;
		}

		@Override
		public Channel getItem(int position) {
			return mDatas != null ? mDatas.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				convertView = View.inflate(context, R.layout.item_video_view_demo, null);
			}
			Channel channel = mDatas.get(position);
			if(channel != null){
				Epg e = channel.getNext();
				TextView cnlName = (TextView)convertView.findViewById(R.id.tv_cnlname);
				TextView tv_nextname = (TextView)convertView.findViewById(R.id.tv_nextname);
				cnlName.setText(channel.getNickName().replaceAll("（米）", ""));
				if(e != null) {
					tv_nextname.setVisibility(View.VISIBLE);
					tv_nextname.setText("即将播出：" + e.name);
				} else {
					tv_nextname.setVisibility(View.GONE);
				}
				ImageView imageView = (ImageView)convertView.findViewById(R.id.image);
				asyncImageLoader.DisplayImage(channel.getVideoImg(), imageView);
			}
			return convertView;
		}
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch(v.getId()) {
			case R.id.tab1:
//				tab1.setBackgroundColor(Color.parseColor("#ffffff"));
//				tab2.setBackgroundColor(Color.parseColor("#efefef"));
//				tab3.setBackgroundColor(Color.parseColor("#efefef"));
				point = 2;
				getChannenllist(point);  //央视
				break;
			case R.id.tab2:
				point = 1;
				getChannenllist(point);  //卫视
				break;
			case R.id.tab3:
				point = 3;
				getChannenllist(point);  //地方台
				break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1) {
			LogUtils.i(TAG, "后退进入");
			tab1.setBackgroundColor(Color.parseColor("#ffffff"));
			tab2.setBackgroundColor(Color.parseColor("#efefef"));
			tab3.setBackgroundColor(Color.parseColor("#efefef"));
			getChannenllist(point);
		}
	}
	
}
