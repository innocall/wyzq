package com.lemon95.wyzq.adapter;

import java.util.List;
import java.util.Map;

import com.lemon95.wyzq.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommunityInfoAdapter extends BaseAdapter {
	
	private Context context;
	private List<Map<String,String>> list;
	
	public CommunityInfoAdapter(Context context,List<Map<String,String>> list) {
		this.context = context;
		this.list = list;
	}

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
		Map<String,String> userInfo = list.get(position);
		ViewHolder holder = null;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.info_item, null);
			holder.info_item_tv_title = (TextView) convertView.findViewById(R.id.info_item_tv_title);
			holder.info_item_tv_sj = (TextView) convertView.findViewById(R.id.info_item_tv_sj);
			holder.info_item_count = (TextView) convertView.findViewById(R.id.info_item_count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.info_item_tv_title.setText(userInfo.get("title"));
		holder.info_item_tv_sj.setText(userInfo.get("addtime"));
		holder.info_item_count.setText("浏览量：" + userInfo.get("hot"));
		return convertView;
	}
	
	class ViewHolder{
		TextView info_item_tv_title;
		TextView info_item_tv_sj;
		TextView info_item_count;
	}

}
