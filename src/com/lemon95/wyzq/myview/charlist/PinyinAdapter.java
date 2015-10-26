package com.lemon95.wyzq.myview.charlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.utils.StringUtil;

import net.sourceforge.pinyin4j.PinyinHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PinyinAdapter extends BaseExpandableListAdapter {

	// 字符串
	private List<String> mContactsName;
	private List<String> mContactsNumber;
	private ArrayList<Bitmap> mContactsPhonto;
	private AssortPinyinList assort = new AssortPinyinList();
	private Context context;
	private LayoutInflater inflater;
	// 中文排序
	private LanguageComparator_CN cnSort = new LanguageComparator_CN();

	public PinyinAdapter(Context context, ArrayList<String> mContactsName,ArrayList<String> mContactsNumber,ArrayList<Bitmap> mContactsPhonto) {
		super();
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.mContactsName = mContactsName;
		this.mContactsNumber = mContactsNumber;
		this.mContactsPhonto = mContactsPhonto;
		//long time = System.currentTimeMillis();
		// 排序
		sort();
		//Toast.makeText(context,String.valueOf(System.currentTimeMillis() - time), 1).show();

	}

	private void sort() {
		// 分类
		for (String str : mContactsName) {
			assort.getHashList().add(str);
		}
		assort.getHashList().sortKeyComparator(cnSort);
		for(int i=0,length=assort.getHashList().size();i<length;i++)
		{
			Collections.sort((assort.getHashList().getValueListIndex(i)),cnSort);
		}
		
	}

	public Object getChild(int group, int child) {
		// TODO Auto-generated method stub
		return assort.getHashList().getValueIndex(group, child);
	}

	public long getChildId(int group, int child) {
		// TODO Auto-generated method stub
		return child;
	}

	public View getChildView(int group, int child, boolean arg2,
			View contentView, ViewGroup arg4) {
		ViewHolder holder = null;
		if (contentView == null) {
			holder = new ViewHolder();
			contentView = LayoutInflater.from(context).inflate(R.layout.colorlist, null);
			holder.iamge = (TextView) contentView.findViewById(R.id.color_image);
			holder.title = (TextView) contentView.findViewById(R.id.color_title);
			holder.text = (TextView) contentView.findViewById(R.id.color_text);
			contentView.setTag(holder);
		} else {
			holder = (ViewHolder) contentView.getTag();
		}
		String name = assort.getHashList().getValueIndex(group, child);
		holder.title.setText(name);
		int position = -1;
		for(int i=0; i<mContactsName.size();i++) {
			if(mContactsName.get(i).equals(name)) {
				position = i;
				break;
			}
		}
		if(position >= 0) {
			// 绘制联系人号码
			holder.text.setText(mContactsNumber.get(position));
			// 绘制联系人头像
			if(!StringUtils.isBlank(name)) {
				name = StringUtil.filterStr(name).replaceAll(" ", "");
				Random r = new Random();
				int i = r.nextInt(6);
				if(i == 0) {
					holder.iamge.setBackgroundResource(R.drawable.bg_phone1);
				} else if(i == 1){
					holder.iamge.setBackgroundResource(R.drawable.bg_phone2);
				} else if(i == 2){
					holder.iamge.setBackgroundResource(R.drawable.bg_phone3);
				} else if(i == 3){
					holder.iamge.setBackgroundResource(R.drawable.bg_phone4);
				} else if(i == 4){
					holder.iamge.setBackgroundResource(R.drawable.bg_phone5);
				} else if(i == 5){
					holder.iamge.setBackgroundResource(R.drawable.bg_phone6);
				}
				if(name.length() == 1) {
					holder.iamge.setText(name);
				} else if(name.length() == 2){
					holder.iamge.setText(name.substring(1, 2));
				} else if(name.length() > 2) {
					holder.iamge.setText(name.substring(2, 3));
				}
			} else {
				
			}
			
		//	holder.iamge.setImageBitmap(mContactsPhonto.get(position));
		}
		return contentView;
	}
	
	class ViewHolder{
		private TextView iamge;
		private TextView title;
		private TextView text;
	}

	public int getChildrenCount(int group) {
		// TODO Auto-generated method stub
		return assort.getHashList().getValueListIndex(group).size();
	}

	public Object getGroup(int group) {
		// TODO Auto-generated method stub
		return assort.getHashList().getValueListIndex(group);
	}

	public int getGroupCount() {
		// TODO Auto-generated method stub
		return assort.getHashList().size();
	}

	public long getGroupId(int group) {
		// TODO Auto-generated method stub
		return group;
	}

	public View getGroupView(int group, boolean arg1, View contentView,
			ViewGroup arg3) {
		if (contentView == null) {
			contentView = inflater.inflate(R.layout.list_group_item, null);
			contentView.setClickable(true);
		}
		TextView textView = (TextView) contentView.findViewById(R.id.name);
		textView.setText(assort.getFirstChar(assort.getHashList()
				.getValueIndex(group, 0)));
		// 禁止伸展

		return contentView;
	}

	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

	public AssortPinyinList getAssort() {
		return assort;
	}

}
