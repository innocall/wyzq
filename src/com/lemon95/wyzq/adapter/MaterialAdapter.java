package com.lemon95.wyzq.adapter;

import java.util.List;
import java.util.Map;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.webserver.WebServiceUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MaterialAdapter extends BaseAdapter {
	
	private final static int DELETE = 0;
	private final static int JIHUO = 1;
	private Context context;
	private List<Map<String,String>> list;
	private boolean isSucces = false;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
				case DELETE:
					if(isSucces) {
						Bundle bundle = msg.getData();
						int position = bundle.getInt("position", 1000);
						if(position != 1000) {
							list.remove(position);
							notifyDataSetChanged();
							PromptManager.showToast(context, "删除成功");
						}
						return;
					}
					PromptManager.showToast(context, "删除失败");
					break;
				case JIHUO:
					if(isSucces) {
						Bundle bundle = msg.getData();
						int position = bundle.getInt("position", 1000);
						if(position != 1000) {
							Map<String,String> map = list.get(position);
							map.remove("islive");
							map.put("islive", "True");
							list.remove(position);
							list.add(position, map);
							notifyDataSetChanged();
							PromptManager.showToast(context, "激活成功");
						}
						return;
					}
					PromptManager.showToast(context, "激活失败");
					break;
			}
		};
	};
	
	public MaterialAdapter(Context context,List<Map<String,String>> list) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Map<String,String> userInfo = list.get(position);
		ViewHolder holder = null;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.material_item, null);
			holder.jihuo = (TextView) convertView.findViewById(R.id.jihuo);
			holder.delete = (TextView) convertView.findViewById(R.id.delete);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(userInfo.get("realname"));
		if(userInfo.get("islive").equals("False")) {
			holder.jihuo.setVisibility(View.VISIBLE);
		} else {
			holder.jihuo.setVisibility(View.GONE);
		}
		holder.jihuo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				jihuo(userInfo,position);
			}
		});
		holder.delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(context);
				builder.setCancelable(false);
				builder.setTitle(R.string.app_name).setMessage("是否删除"+ userInfo.get("realname") +"的账号?").setPositiveButton("删除", new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						delete(userInfo,position);
					}
					
				}).setNegativeButton("取消", null).show();
			}
		});
		return convertView;
	}
	
	protected void jihuo(final Map<String, String> userInfo, final int position) {
		new Thread() {
			public void run() {
				isSucces = WebServiceUtils.jihuo(userInfo.get("mobile"),userInfo.get("xqid"));
				Message msg = new Message();
				msg.what = JIHUO;
				Bundle bundle = new Bundle();
				bundle.putInt("position", position);
				msg.setData(bundle);
				handler.sendMessage(msg);
			};
		}.start();
	}

	class ViewHolder{
		TextView name;
		TextView delete;
		TextView jihuo;
	}
	
	private void delete(final Map<String,String> userInfo,final int position) {
		new Thread() {
			public void run() {
				isSucces = WebServiceUtils.vipdelete(userInfo.get("mobile"),userInfo.get("xqid"),userInfo.get("remobile"));
				Message msg = new Message();
				msg.what = DELETE;
				Bundle bundle = new Bundle();
				bundle.putInt("position", position);
				msg.setData(bundle);
				handler.sendMessage(msg);
			};
		}.start();
	}

}
