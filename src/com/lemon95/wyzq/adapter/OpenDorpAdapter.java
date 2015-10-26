package com.lemon95.wyzq.adapter;

import java.util.List;
import java.util.Map;
import com.lemon95.wyzq.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OpenDorpAdapter extends BaseAdapter {
	
	private Context context;
	private List<Map<String,String>> list2;
	
	public OpenDorpAdapter(Context context,List<Map<String,String>> list2) {
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
            convertView = layoutInflator.inflate(R.layout.dorp_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        if(position % 2 == 0) {
        	holder.img.setImageResource(R.drawable.dorp_01);
        } else {
        	holder.img.setImageResource(R.drawable.dorp_02);
        }
        holder.name.setText(map.get("men"));
		return convertView;
	}
	
	class ViewHolder {
		ImageView img;
		TextView name;
	}

}
