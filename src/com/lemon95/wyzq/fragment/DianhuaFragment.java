package com.lemon95.wyzq.fragment;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.feiyucloud.sdk.FYCall;
import com.feiyucloud.sdk.FYCallListener;
import com.feiyucloud.sdk.FYClient;
import com.feiyucloud.sdk.FYClientListener;
import com.feiyucloud.sdk.FYError;
import com.lemon95.wyzq.R;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.manage.MiddleManager;
import com.lemon95.wyzq.model.Result;
import com.lemon95.wyzq.myview.charlist.AssortView;
import com.lemon95.wyzq.myview.charlist.AssortView.OnTouchAssortListener;
import com.lemon95.wyzq.myview.charlist.PinyinAdapter;
import com.lemon95.wyzq.utils.LogUtils;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.view.BaseUI;
import com.lemon95.wyzq.webserver.WebServiceUtils;
import com.umeng.analytics.MobclickAgent;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DianhuaFragment extends BaseUI{
	
	private final static String TAG = DianhuaFragment.class.getSimpleName();
	/** 获取库Phon表字段 **/
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };

	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** 头像ID **/
	private static final int PHONES_PHOTO_ID_INDEX = 2;

	/** 联系人的ID **/
	private static final int PHONES_CONTACT_ID_INDEX = 3;
	
	private static final int CALL = 4;

	/** 联系人名称 **/
	private ArrayList<String> mContactsName = new ArrayList<String>();
	
	/** 联系人头像 **/
	private ArrayList<String> mContactsNumber = new ArrayList<String>();

	/** 联系人头像 **/
	private ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();

	private PinyinAdapter adapter;
	private ExpandableListView eListView;
	private AssortView assortView;
	private Result result;
	private SharedPreferences sp;
	private EditText phone;
	private Button login_rig_but;
	private LinearLayout dianhua_id;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what){
				case CALL:
					PromptManager.closeProgressDialog();
					if(result != null) {
						if(result.getRows().equals("0")) {
							PromptManager.showToast(context, "呼叫成功,请稍候！");
						} else {
							PromptManager.showToast(context, result.getMessage());
						}
					} else {
						PromptManager.showToast(context, "呼叫失败!");
					}
					break;
			}
		};
	};
	
	public DianhuaFragment(Context context) {
		super(context);
	}

	@Override
	public void init() {
		showInMiddle = (LinearLayout) View.inflate(context, R.layout.phone_fragment, null);
		eListView = (ExpandableListView) findViewById(R.id.lists);
		assortView = (AssortView) findViewById(R.id.assort);
		login_rig_but = (Button) findViewById(R.id.login_rig_but);
		phone = (EditText) findViewById(R.id.phone);
		dianhua_id = (LinearLayout) findViewById(R.id.dianhua_id);
		sp = context.getSharedPreferences(ConstantValue.CONFIG, Context.MODE_PRIVATE);
	}

	@Override
	public void setListener() {
		login_rig_but.setOnClickListener(this);
	}

	@Override
	public int getID() {
		return ConstantValue.DIANHUAFRAGMENT;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(context);
		/*添加联系人*/
		addUser("柠檬免费电话","4009304063");
		/** 得到手机通讯录联系人信息 **/
		getPhoneContacts();
		adapter = new PinyinAdapter(context,mContactsName,mContactsNumber,mContactsPhonto);
		eListView.setAdapter(adapter);
		 //展开所有
		for (int i = 0, length = adapter.getGroupCount(); i < length; i++) {
			eListView.expandGroup(i);
		}
		
		//字母按键回调
		assortView.setOnTouchAssortListener(new OnTouchAssortListener() {
			
			View layoutView=LayoutInflater.from(context).inflate(R.layout.alert_dialog_menu_layout, null);
			TextView text =(TextView) layoutView.findViewById(R.id.content);
			PopupWindow popupWindow ;
			public void onTouchAssortListener(String str) {
			   int index=adapter.getAssort().getHashList().indexOfKey(str);
			   if(index!=-1) {
					eListView.setSelectedGroup(index);;
			   }
			   if(popupWindow!=null){
				    text.setText(str);
			   } else{   
				      popupWindow = new PopupWindow(layoutView,150, 150,false);
					// 显示在Activity的根视图中心
					popupWindow.showAtLocation(MiddleManager.getInstance().activity.getWindow().getDecorView(),Gravity.CENTER, 0, 0);
				}
				text.setText(str);
			}

			public void onTouchAssortUP() {
				if(popupWindow!=null)
				popupWindow.dismiss();
				popupWindow=null;
			}
		});
		
		eListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				String name = (String) adapter.getChild(groupPosition, childPosition);
				int position = -1;
				for(int i=0; i<mContactsName.size();i++) {
					if(mContactsName.get(i).equals(name)) {
						position = i;
						break;
					}
				}
				if(position >= 0) {
					callPhone(mContactsNumber.get(position));
				} else {
					PromptManager.showToast(context, "呼叫失败");
				}
				return false;
			}
		});
	}
	
	private void addUser(String name, String phone) {
		 /* 往 raw_contacts 中添加数据，并获取添加的id号*/  
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");  
        ContentResolver resolver = context.getContentResolver();  
        ContentValues values = new ContentValues();  
        long contactId = ContentUris.parseId(resolver.insert(uri, values));  
          
        /* 往 data 中添加数据（要根据前面获取的id号） */  
        // 添加姓名  
        uri = Uri.parse("content://com.android.contacts/data");  
        values.put("raw_contact_id", contactId);  
        values.put("mimetype", "vnd.android.cursor.item/name");  
        values.put("data2", name);  
        resolver.insert(uri, values);  
          
        // 添加电话  
        values.clear();  
        values.put("raw_contact_id", contactId);  
        values.put("mimetype", "vnd.android.cursor.item/phone_v2");  
        values.put("data2", "2");  
        values.put("data1", phone);  
        resolver.insert(uri, values);  
          
        // 添加Email  
        /*values.clear();  
        values.put("raw_contact_id", contactId);  
        values.put("mimetype", "vnd.android.cursor.item/email_v2");  
        values.put("data2", "2");  
        values.put("data1", "zhouguoping@qq.com");  
        resolver.insert(uri, values); */
	}

	protected void callPhone(final String phone) {
		//PromptManager.showProgressDialog(context, "拨打中...");
		//PromptManager.showToast(context, phone);
		String zhujiao = sp.getString("USERNAME", "");
		Intent intent = new Intent();
        intent.putExtra("Flag_Incoming", false);
        intent.putExtra("CallNumber", phone);
        intent.putExtra("zhujiao", zhujiao);
        intent.putExtra("CallType", 2);  //2回拨  
        intent.setClassName("com.lemon95.wyzq", "com.lemon95.wyzq.view.call.InCallActivity");
        MiddleManager.getInstance().activity.startActivity(intent);
		
		/*new Thread(){
			public void run() {
				result = WebServiceUtils.call(zhujiao,phone);
				Message msg = new Message();
				msg.what = CALL;
				handler.sendMessage(msg);
			};
		}.start();*/
	}

	/** 得到手机通讯录联系人信息 **/
	private void getPhoneContacts() {
		ContentResolver resolver = context.getContentResolver();

		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;

				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				// 得到联系人ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

				// 得到联系人头像Bitamp
				Bitmap contactPhoto = null;

				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if (photoid > 0) {
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(resolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					contactPhoto = BitmapFactory.decodeResource(context.getResources(),R.drawable.contact_photo);
				}

				mContactsName.add(contactName);
				mContactsNumber.add(phoneNumber);
				mContactsPhonto.add(contactPhoto);
			}

			phoneCursor.close();
		}
	}

	/** 得到手机SIM卡联系人人信息 **/
	private void getSIMContacts() {
		ContentResolver resolver = context.getContentResolver();
		// 获取Sims卡联系人
		Uri uri = Uri.parse("content://icc/adn");
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
				null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				// Sim卡中没有联系人头像

				mContactsName.add(contactName);
				mContactsNumber.add(phoneNumber);
			}

			phoneCursor.close();
		}
	}
	
	class MyListAdapter extends BaseAdapter {
		

		public int getCount() {
			// 设置绘制数量
			return mContactsName.size();
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView iamge = null;
			TextView title = null;
			TextView text = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.colorlist, null);
				iamge = (ImageView) convertView.findViewById(R.id.color_image);
				title = (TextView) convertView.findViewById(R.id.color_title);
				text = (TextView) convertView.findViewById(R.id.color_text);
			}
			// 绘制联系人名称
			title.setText(mContactsName.get(position));
			// 绘制联系人号码
			text.setText(mContactsNumber.get(position));
			// 绘制联系人头像
			iamge.setImageBitmap(mContactsPhonto.get(position));
			return convertView;
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mContactsName.clear();
		mContactsNumber.clear();
		mContactsPhonto.clear();
		MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(context);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch(v.getId()) {
			case R.id.login_rig_but:
				//拨打
				String beijiao = phone.getText().toString().trim();
				if(StringUtils.isBlank(beijiao)) {
					PromptManager.showToast(context, "请输入您要拨打的号码");
					return;
				}
				callPhone(beijiao);
				break;
		}
	}
	
}
