package com.lemon95.wyzq.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dodola.model.DuitangInfo;
import com.dodowaterfall.Helper;
import com.dodowaterfall.widget.ScaleImageView;
import com.lemon95.wyzq.R;
import com.lemon95.wyzq.adapter.MainAdapter;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.manage.MiddleManager;
import com.lemon95.wyzq.model.ImageViewModel;
import com.lemon95.wyzq.model.Result;
import com.lemon95.wyzq.myview.image.SlideShowView;
import com.lemon95.wyzq.myview.image.SlideShowView.OnNumberKeyboard;
import com.lemon95.wyzq.myview.listview.RefreshListView;
import com.lemon95.wyzq.myview.pull.XListView;
import com.lemon95.wyzq.myview.pull.XListView.IXListViewListener;
import com.lemon95.wyzq.myview.pull.bitmapfun.util.ImageFetcher;
import com.lemon95.wyzq.myview.pull.internal.PLA_AdapterView;
import com.lemon95.wyzq.myview.pull.internal.PLA_AdapterView.OnItemClickListener;
import com.lemon95.wyzq.utils.AsyncImageLoader;
import com.lemon95.wyzq.utils.DisplayUtil;
import com.lemon95.wyzq.utils.LogUtils;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.utils.RandomSecquenceCreator;
import com.lemon95.wyzq.view.BaseUI;
import com.lemon95.wyzq.webserver.WebServiceUtils;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainFragment extends BaseUI implements IXListViewListener {
	
	private final static String TAG = MainFragment.class.getSimpleName();
	private final static int MSG = 0;
	private SlideShowView slideshowView;
	private AsyncImageLoader imageLoad;
	private int w,h;
	private int[] imageUrls = new int[]{R.drawable.banner1,R.drawable.banner2,R.drawable.banner3,R.drawable.banner4};
	private XListView mAdapterView;
	private ImageFetcher mImageFetcher;
    private StaggeredAdapter mAdapter;
	private String xqid;
	private SharedPreferences sp;
	private int currentPage = 1;
	private LinkedList<DuitangInfo> mInfos;
	private int totle = 0;
	private boolean isParam = true;;
	ContentTask task = new ContentTask(context, 2);
	private class ContentTask extends AsyncTask<String, Integer, List<DuitangInfo>> {

        private Context mContext;
        private int mType = 1;

        public ContentTask(Context context, int type) {
            super();
            mContext = context;
            mType = type;
        }

        @Override
        protected List<DuitangInfo> doInBackground(String... params) {
            try {
                return parseNewsJSON(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<DuitangInfo> result) {
        	//1为下拉刷新 2为加载更多
            if (mType == 1) {
                //mAdapter.addItemTop(result);
            	mAdapter.setList(result);
                mAdapter.notifyDataSetChanged();
                mAdapterView.stopRefresh();
            } else if (mType == 2) {
                mAdapterView.stopLoadMore();
                mAdapter.addItemLast(result);
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onPreExecute() {
        }

        public List<DuitangInfo> parseNewsJSON(String url) throws IOException {
        	List<DuitangInfo> duitangs = new ArrayList<DuitangInfo>();
            String json = "";
            if (Helper.checkConnection(mContext)) {
                try {
                   // json = Helper.getStringFromUrl(url);
                   json = WebServiceUtils.bbslist(xqid,"",ConstantValue.PAGESIZE,Integer.parseInt(url));
                } catch (Exception e) {
                    Log.e("IOException is : ", e.toString());
                    e.printStackTrace();
                    return duitangs;
                }
            }
            Log.d("MainActiivty", "json:" + json);
            try {
                if (null != json) {
                    JSONObject newsObject = new JSONObject(json);
                    String rows = newsObject.getString("rows");
                    if(Integer.parseInt(rows) >= 1) {
                		JSONArray tab = new JSONArray(newsObject.getString("tab"));
                		totle = tab.getJSONObject(0).getInt("fldtotalRecord");
                		//resutl.setFldTotalPage(tab.getJSONObject(0).getString("fldTotalPage"));
                		//resutl.setFldtotalRecord(tab.getJSONObject(0).getString("fldtotalRecord"));
                		JSONArray tab1 = new JSONArray(newsObject.getString("tab1"));
                		int len = tab1.length();
                		if(len % 2 == 0) {
                		} else {
                			if(len > 2) {
                				len = len - 1;
                			}
                		}
                		for(int j=0; j<len; j++) {
                			DuitangInfo newsInfo1 = new DuitangInfo();
                			JSONObject newsInfoLeftObject = tab1.getJSONObject(j);
//        					model1.setXqid(tab1.getJSONObject(j).getString("xqid"));
//            				model1.setUserName(tab1.getJSONObject(j).getString("username"));
//            				model1.setAddTime(tab1.getJSONObject(j).getString("addtime"));
//            				model1.setTitle(tab1.getJSONObject(j).getString("title"));
//            				model1.setCc(tab1.getJSONObject(j).getString("cc"));
//            				model1.setZhuti(tab1.getJSONObject(j).getString("zhuti"));
//            				model1.setPicurl(tab1.getJSONObject(j).getString("picurl"));
            				newsInfo1.setAlbid(newsInfoLeftObject.isNull("b2id") ? "" : newsInfoLeftObject.getString("b2id"));
                            newsInfo1.setIsrc(newsInfoLeftObject.isNull("picurl") ? "" : newsInfoLeftObject.getString("picurl"));
                            newsInfo1.setMsg(newsInfoLeftObject.isNull("cc") ? "" : newsInfoLeftObject.getString("cc"));
                            newsInfo1.setDateTime(newsInfoLeftObject.isNull("addtime") ? "" : newsInfoLeftObject.getString("addtime"));
                            newsInfo1.setType(newsInfoLeftObject.isNull("zhuti") ? "" : newsInfoLeftObject.getString("zhuti"));
                            newsInfo1.setNickName(newsInfoLeftObject.isNull("title") ? "" : newsInfoLeftObject.getString("title"));
                            newsInfo1.setPinglun(newsInfoLeftObject.isNull("pinglun") ? "" : newsInfoLeftObject.getString("pinglun"));
                            newsInfo1.setZan(newsInfoLeftObject.isNull("zan") ? "" : newsInfoLeftObject.getString("zan"));
                            newsInfo1.setUsername(newsInfoLeftObject.isNull("username") ? "" : newsInfoLeftObject.getString("username"));
                            //newsInfo1.setHeight(newsInfoLeftObject.getInt("iht"));
                            //newsInfo1.setHeight(RandomSecquenceCreator.getHeight());
                            if(j % 4 == 0) {
                            	newsInfo1.setHeight(200);
                            } else if(j % 4 == 1){
                            	newsInfo1.setHeight(170);
                            } else if(j % 4 == 2){
                            	newsInfo1.setHeight(200);
                            } else if(j % 4 == 3){
                            	newsInfo1.setHeight(170);
                            }
            				duitangs.add(newsInfo1);
                		}
                	}
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return duitangs;
        }
    }
	
	
	public MainFragment(Context context) {
		super(context);
	}

	@Override
	public void init() {
		showInMiddle = (LinearLayout) View.inflate(context, R.layout.main_fragment, null);
		mAdapterView = (XListView)findViewById(R.id.lv_test);
		mAdapterView.setPullLoadEnable(true);
	    mAdapterView.setXListViewListener(this);
	    
	    mAdapter = new StaggeredAdapter(context, mAdapterView);
	    
	    mImageFetcher = new ImageFetcher(context, 240);
	    mImageFetcher.setLoadingImage(R.drawable.empty_photo);
		
		View view = LayoutInflater.from(context).inflate(R.layout.lv_test, null);
		slideshowView = (SlideShowView) view.findViewById(R.id.slideshowView);
		Point p = DisplayUtil.getScreenMetrics(context);
		w = p.x;
		h = p.y;
		imageLoad = new AsyncImageLoader(context, w, h, R.drawable.banner4);
		mAdapterView.addHeaderView(view);
		mAdapterView.setAdapter(mAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		sp = context.getSharedPreferences(ConstantValue.CONFIG, Context.MODE_PRIVATE);
		xqid = sp.getString("XQID", "");
		MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(context);
		slideshowView.initData(imageUrls, imageLoad);
		
		mImageFetcher.setExitTasksEarly(false);
		/*if(bundle != null) {
			isParam = bundle.getBoolean("isParam", false);
			LogUtils.i(TAG, isParam + "");
		}*/
		if(isParam) {
			isParam = false;
			AddItemToContainer(currentPage, 2);
		}
		
	}
	
	/**
     * 添加内容
     * @param pageindex
     * @param type
     * 1为下拉刷新 2为加载更多
     */
    private void AddItemToContainer(int pageindex, int type) {
        if (task.getStatus() != Status.RUNNING) {
            ContentTask task = new ContentTask(context, type);
            task.execute(pageindex + "");
        }
    }
	
	@Override
	public void setListener() {
		slideshowView.setonNumberKeyboard(new OnNumberKeyboard() {

			@Override
			public void OnSetNumber(View v) {
				int currentItem = slideshowView.getCurrentItem();
				if (currentItem == 0) {
					//PromptManager.showToastTest(context, "您点击了第1张");
					MobclickAgent.onEvent(context,"click_banner1");
				}
				if (currentItem == 1) {
					//PromptManager.showToastTest(context, "您点击了第2张");
					MobclickAgent.onEvent(context,"click_banner2");
				}
				if (currentItem == 2) {
					//PromptManager.showToastTest(context, "您点击了第3张");
					MobclickAgent.onEvent(context,"click_banner3");
				}
				if (currentItem == 3) {
					//PromptManager.showToastTest(context, "您点击了第4张");
					MobclickAgent.onEvent(context,"click_banner4");
				}
			}
		});
		mAdapterView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
				//PromptManager.showToast(context, position + "  " + mInfos.size());
				DuitangInfo du = mInfos.get(position - 2);
				Bundle bundle2 = new Bundle();
				bundle2.putString("cc", du.getMsg());
				bundle2.putString("type", du.getType());
				bundle2.putString("time", du.getDateTime());
				bundle2.putString("nickName", du.getNickName());
				bundle2.putString("b2id", du.getAlbid());
				bundle2.putString("username", du.getUsername());
				bundle2.putInt("zan", Integer.parseInt(du.getZan()));
				MiddleManager.getInstance().changeUI(SayContentFragment.class,bundle2);
			}
		});
		
	}

	@Override
	public int getID() {
		return ConstantValue.MAINFRAGMENT;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(slideshowView != null) {
			slideshowView.stopPlay();
		}
		MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(context);
	}

	@Override
    public void onRefresh() {
        AddItemToContainer(1, 1);
    }

    @Override
    public void onLoadMore() {
    	if(totle == mInfos.size() || totle == mInfos.size() + 1) {
    		mAdapterView.stopLoadMore();
    		PromptManager.showToast(context, "已是最后一页");
    	} else {
    		AddItemToContainer(++currentPage, 2);
    	}
    }
	
	public class StaggeredAdapter extends BaseAdapter {
        private Context mContext;
        private XListView mListView;

        public StaggeredAdapter(Context context, XListView xListView) {
            mContext = context;
            mInfos = new LinkedList<DuitangInfo>();
            mListView = xListView;
        }
        
        public void setList(List<DuitangInfo> list) {
        	mInfos.clear();
        	mInfos.addAll(list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            DuitangInfo duitangInfo = mInfos.get(position);

            if (convertView == null) {
                LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
                convertView = layoutInflator.inflate(R.layout.infos_list, null);
                holder = new ViewHolder();
                holder.imageView = (ScaleImageView) convertView.findViewById(R.id.news_pic);
                holder.contentView = (TextView) convertView.findViewById(R.id.news_title);
                holder.timeView = (TextView) convertView.findViewById(R.id.news_time);
                holder.pinglun = (TextView) convertView.findViewById(R.id.pinglun);
                convertView.setTag(holder);
            }

            holder = (ViewHolder) convertView.getTag();
            holder.imageView.setImageWidth(duitangInfo.getWidth());
            holder.imageView.setImageHeight(duitangInfo.getHeight());
            holder.pinglun.setText(duitangInfo.getPinglun());
            String type = duitangInfo.getType();
            String color = "F8B62D";
            if("邻里互助".equals(type)) {
            	color = "F8B62D";
            } else if("乐趣分享".equals(type)) {
            	color = "EA5353";
            } else if("跳蚤市场".equals(type)) {
            	color = "2ECC71";
            } else if("邻里商机".equals(type)) {
            	color = "3498DB";
            } else if("投诉建议".equals(type)) {
            	color = "9B59B6";
            } else if("活动发起".equals(type)) {
            	color = "E74C3C";
            } else if("乐趣分享".equals(type)) {
            	color = "E67E22";
            }
            type = " " + type + " ";
            int end = type.length();
            String content = duitangInfo.getMsg();
            if(content.length() < 12) {
            } else {
            	content = content.substring(0,12) + "...";
            }
            SpannableStringBuilder style=new SpannableStringBuilder(type + " " + content);
            style.setSpan(new BackgroundColorSpan(Color.parseColor("#" + color)),0,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            style.setSpan(new ForegroundColorSpan(Color.WHITE), 0, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.contentView.setText(style);
            holder.timeView.setText(duitangInfo.getDateTime());
            mImageFetcher.loadImage(duitangInfo.getIsrc(), holder.imageView);
            return convertView;
        }

        class ViewHolder {
            ScaleImageView imageView;
            TextView contentView;
            TextView timeView;
            TextView pinglun;
        }

        @Override
        public int getCount() {
            return mInfos.size();
        }

        @Override
        public Object getItem(int arg0) {
            return mInfos.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        public void addItemLast(List<DuitangInfo> datas) {
            mInfos.addAll(datas);
        }

        public void addItemTop(List<DuitangInfo> datas) {
            for (DuitangInfo info : datas) {
                mInfos.addFirst(info);
            }
        }
    }

	
}
