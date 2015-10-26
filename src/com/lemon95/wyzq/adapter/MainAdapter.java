package com.lemon95.wyzq.adapter;

import java.util.List;
import java.util.Map;
import com.lemon95.wyzq.R;
import com.lemon95.wyzq.model.ImageViewModel;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.tsz.afinal.FinalBitmap;

public class MainAdapter extends BaseAdapter implements OnClickListener{
	
	private Context context;
	private List<Map<String,ImageViewModel>> mainList;
	private FinalBitmap fb;
	private Bitmap bitmap;
	
	public MainAdapter(Context context,List<Map<String,ImageViewModel>> mainList) {
		this.context = context;
		this.mainList = mainList;
		this.fb = FinalBitmap.create(context);
		bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.zufang_main_default);
	}

	@Override
	public int getCount() {
		return mainList.size();
	}

	@Override
	public Object getItem(int position) {
		return mainList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Map<String,ImageViewModel> map = mainList.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.main_item, null);
			holder.image1 = (RelativeLayout) convertView.findViewById(R.id.image1);
			holder.image2 = (RelativeLayout) convertView.findViewById(R.id.image2);
			holder.main_title1 = (TextView) convertView.findViewById(R.id.main_title1);
			holder.main_title2 = (TextView) convertView.findViewById(R.id.main_title2);
			holder.main_type1 = (TextView) convertView.findViewById(R.id.main_type1);
			holder.main_type2 = (TextView) convertView.findViewById(R.id.main_type2);
			holder.main_time1 = (TextView) convertView.findViewById(R.id.main_time1);
			holder.main_time2 = (TextView) convertView.findViewById(R.id.main_time2);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ImageViewModel imageView1 = map.get("1");
		if(imageView1 != null) {
			//holder.image1.setBackgroundResource(imageView1.getBitmap());
			holder.main_title1.setText(imageView1.getTitle());
			holder.main_type1.setText(imageView1.getTitle());
			holder.main_time1.setText(imageView1.getAddTime());
			//fb.display(imageView, uri, loadingBitmap);
			holder.image1.setOnClickListener(this);
		}
		ImageViewModel imageView2 = map.get("2");
		if(imageView2 != null) {
			//holder.image2.setBackgroundResource(imageView2.getBitmap());
			holder.main_title2.setText(imageView2.getTitle());
			holder.main_type2.setText(imageView2.getTitle());
			holder.main_time2.setText(imageView2.getAddTime());
			holder.image2.setOnClickListener(this);
		}
		return convertView;
	}
	
	class ViewHolder {
		RelativeLayout image1;
		RelativeLayout image2;
		TextView main_title1;
		TextView main_title2;
		TextView main_type1;
		TextView main_type2;
		TextView main_time1;
		TextView main_time2;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.image1:
				
				break;
			case R.id.image2:
				
				break;
		}
		
	}

}
