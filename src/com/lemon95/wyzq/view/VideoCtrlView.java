package com.lemon95.wyzq.view;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.model.Channel;
import com.lemon95.wyzq.utils.LogUtils;
import com.lemon95.wyzq.webserver.WebServiceUtils;
import com.starschina.abs.media.ThinkoPlayerCtrlView;
import com.starschina.media.ThinkoEnvironment;
import com.starschina.media.ThinkoPlayerView;
import com.starschina.media.ThinkoEnvironment.OnGetChannelsListener;
import com.starschina.networkutils.NetworkUtils;
import com.starschina.types.DChannel;
import com.starschina.types.SDKConf;
import com.starschina.volley.VolleyError;
import com.starschina.volley.Request.Method;
import com.starschina.volley.Response.ErrorListener;
import com.starschina.volley.Response.Listener;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VideoCtrlView extends ThinkoPlayerCtrlView {

	private static final String TAG = "VideoCtrlView";
	private final static int ISSHANG = 0;
	private Context mContext;
	private TextView mTitleView;
	private LinearLayout xiantai_list;
	private ListView xiantai_listView;
	private MyListAdapter mAdapter;
	private ArrayList<Channel> channellist;
	private ThinkoPlayerView mPlayerView;
	private Button btn_sel;
	private ArrayList<Channel> resultList = new ArrayList<Channel>();
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ISSHANG:
				if(resultList != null) {
					if(resultList.size() > 0) {
						mAdapter.setData(resultList);
						mAdapter.notifyDataSetChanged();
					}
				}
				break;
			}
		}
	};
	
	public VideoCtrlView(Context act, ThinkoPlayerView mPlayerView) {
		super(act);
		mContext = act;
		this.mPlayerView = mPlayerView;
		initVideoCtrlView();
	}

	private void initVideoCtrlView() {
		//初始化sdk
		/*try{
			ThinkoEnvironment.setUp(mContext);
		} catch(Exception e) {
			LogUtils.i(TAG, "key错误");
		}*/
		View layout = View.inflate(mContext, R.layout.layout_videoctrl, this);
		mTitleView = (TextView) layout.findViewById(R.id.title);
		xiantai_list = (LinearLayout) layout.findViewById(R.id.xiantai_list);
		xiantai_listView = (ListView) layout.findViewById(R.id.xiantai_listView);
		mAdapter = new MyListAdapter();
		xiantai_listView.setAdapter(mAdapter);
		Button back = (Button) layout.findViewById(R.id.btn_exit);
		btn_sel = (Button) layout.findViewById(R.id.btn_sel);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((Activity) mContext).finish();
			}
		});

		btn_sel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Toast.makeText(mContext, "点击按钮", 1).show();
				goneXuanTaiBut();
				xiantai_list.setVisibility(View.VISIBLE);
				xiantai_listView.setVisibility(View.VISIBLE);
				xiantai_list.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.xuantai_in));
			}
		});

		xiantai_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mPlayerView.prepareToPlay(resultList.get(position).id);
				setTitle(resultList.get(position).nickName);
				goneXuanTaiBut();
				xiantai_list.setVisibility(View.GONE);
				xiantai_listView.setVisibility(View.GONE);
				xiantai_list.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.xuantai_out));
			}
		});
		getChannenllist(0);
	}
	
	public void hideListView() {
		if(xiantai_list.getVisibility() == View.VISIBLE) {
			xiantai_list.setVisibility(View.GONE);
			xiantai_listView.setVisibility(View.GONE);
			xiantai_list.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.xuantai_out));
		}
	}

	public void xuanTaiBut() {
		if(btn_sel.getVisibility() == View.GONE) {
			btn_sel.setVisibility(View.VISIBLE);
			btn_sel.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.xuantai_but));
		}
	}

	public void goneXuanTaiBut() {
		if(btn_sel.getVisibility() == View.VISIBLE) {
			btn_sel.setVisibility(View.GONE);
			btn_sel.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.xuantai_but_gone));
		}
	}

	public void setTitle(String title) {
		mTitleView.setText(title);
	}
	
	public void getChannenllist(final long bid){
		
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

	class MyListAdapter extends BaseAdapter {

		private ArrayList<Channel> mDatas;

		public void setData(ArrayList<Channel> chs) {
			if(mDatas != null) {
				mDatas.clear();
			}
			mDatas = chs;
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
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.tv_item, null);
			}
			Channel channel = mDatas.get(position);
			if (channel != null) {
				TextView cnlName = (TextView) convertView.findViewById(R.id.next_epg);
				cnlName.setText(channel.nickName);
			}
			return convertView;
		}
	}
}
