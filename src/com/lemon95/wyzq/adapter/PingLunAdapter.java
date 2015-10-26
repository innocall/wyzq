package com.lemon95.wyzq.adapter;

import java.util.List;
import java.util.Map;
import com.lemon95.wyzq.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PingLunAdapter extends BaseAdapter {
	
	private Context context;
	private List<Map<String,String>> list2;
	
	public PingLunAdapter(Context context,List<Map<String,String>> list2) {
		this.context = context;
		this.list2 = list2;
	}

	@Override
	public int getCount() {
		return list2.size();
	}

	@Override
	public Object getItem(int position) {
		return list2.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Map<String,String> map = list2.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
            convertView = layoutInflator.inflate(R.layout.pinglun_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        String nick = map.get("username");
        if(nick.length() > 10) {
        	String str = nick.substring(4, 8);
        	nick = nick.replaceAll(str, "****");
        }
        holder.name.setText(nick);
        holder.time.setText(map.get("addtime"));
        holder.content.setText(map.get("cc"));
		return convertView;
	}
	
	class ViewHolder {
		TextView name;
		TextView time;
		TextView content;
	}

}
