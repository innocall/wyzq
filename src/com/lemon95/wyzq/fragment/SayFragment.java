package com.lemon95.wyzq.fragment;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.kobjects.base64.Base64;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.manage.MiddleManager;
import com.lemon95.wyzq.model.Result;
import com.lemon95.wyzq.myview.dialog.CustomProgressDialog;
import com.lemon95.wyzq.utils.FileUtil;
import com.lemon95.wyzq.utils.ImageUtils;
import com.lemon95.wyzq.utils.LogUtils;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.utils.StringUtil;
import com.lemon95.wyzq.view.BaseUI;
import com.lemon95.wyzq.webserver.WebServiceUtils;
import com.umeng.analytics.MobclickAgent;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class SayFragment extends BaseUI {
	
	private final static String TAG = SayFragment.class.getSimpleName();
	private final static int SUBMIT1 = 0;
	private final static int SUBMIT2 = 1;
	private final static int TYPE = 2;
	private EditText shop_des;
	private TextView say_tv_camera;
	private Spinner shop_date;
	private Button login_but;
	private String filePaht;
	private String filePaht2;
	private String id;
	private String title;
	private RelativeLayout shop_rl_camera1;
	private RelativeLayout shop_rl_camera2;
	private RelativeLayout shop_rl_camera3;
	//private RelativeLayout shop_rl_camera4;
	private ImageView shop_img_camera_mentou;
	private ImageView shop_img2_camera1;
	private ImageView shop_img2_camera2;
	private ImageView shop_img2_camera3;
	//private ImageView shop_img2_camera4;
	private ImageButton shop_ib_camera1;
	private ImageButton shop_ib_camera2;
	private ImageButton shop_ib_camera3;
	//private ImageButton shop_ib_camera4;
	//private Map<Integer,String> photoMap = new HashMap<Integer,String>();
	private List<String> photoList = new LinkedList<>();
	private Result result;
	private Result result2;
	private Result res;
	private SharedPreferences sp;
	private long b2id;
	private String des;
	private String xqid;
	private String userName;
	private int j = 0;
	private CustomProgressDialog dialog;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
				case SUBMIT1:
					PromptManager.closeProgressDialog();
					if(result != null) {
						if(Integer.parseInt(result.getRows()) > 0) {
							PromptManager.showToast(context, "提交成功");
							MiddleManager.getInstance().clearNew(MainFragment.class);
							MiddleManager.getInstance().changeUI(MainFragment.class);
							return;
						}
					} 
					PromptManager.showToast(context, "提交失败");
					break;
				case SUBMIT2:
					if(result2 != null) {
						if(Integer.parseInt(result2.getRows()) > 0) {
							submitContext(b2id);
							return;
						}
					} 
					PromptManager.closeProgressDialog();
					PromptManager.showToast(context, "提交失败");
					break;
				case TYPE:
					stopProgressDialog();
					if(res != null) {
						if(Integer.parseInt(res.getRows()) > 0) {
							List<Map<String,String>> list = res.getList();
							Map<String, String> tabMap = new HashMap<String, String>();
							tabMap.put("id", "");
                			tabMap.put("title", "请选择说说类型");
                			tabMap.put("pic", "");
                			list.add(0, tabMap);
							MyAdapter adapter = new MyAdapter(list);
							shop_date.setAdapter(adapter);
							shop_date.setSelection(0,true);
						} else {
							PromptManager.showToast(context, "获取说说类型失败");
						}
					} else {
						PromptManager.showToast(context, "网络异常");
					}
					break;
			}
		};
	};
	
	public SayFragment(Context context) {
		super(context);
	}

	@Override
	public void init() {
		showInMiddle = (LinearLayout) View.inflate(context, R.layout.say_fragment, null);
		shop_des = (EditText) findViewById(R.id.shop_des);
		shop_rl_camera1 = (RelativeLayout) findViewById(R.id.shop_rl_camera1);
		shop_img_camera_mentou = (ImageView) findViewById(R.id.shop_img_camera_mentou);
		shop_img2_camera1 = (ImageView) findViewById(R.id.shop_img2_camera1);
		say_tv_camera = (TextView) findViewById(R.id.say_tv_camera);
		shop_ib_camera1 = (ImageButton) findViewById(R.id.shop_ib_camera1);
		shop_rl_camera2 = (RelativeLayout) findViewById(R.id.shop_rl_camera2);
		shop_img2_camera2 = (ImageView) findViewById(R.id.shop_img2_camera2);
		shop_ib_camera2 = (ImageButton) findViewById(R.id.shop_ib_camera2);
		shop_rl_camera3 = (RelativeLayout) findViewById(R.id.shop_rl_camera3);
		shop_img2_camera3 = (ImageView) findViewById(R.id.shop_img2_camera3);
		shop_ib_camera3 = (ImageButton) findViewById(R.id.shop_ib_camera3);
		shop_date = (Spinner) findViewById(R.id.shop_date);
//		shop_rl_camera4 = (RelativeLayout) findViewById(R.id.shop_rl_camera4);
//		shop_img2_camera4 = (ImageView) findViewById(R.id.shop_img2_camera4);
//		shop_ib_camera4 = (ImageButton) findViewById(R.id.shop_ib_camera4);
		login_but = (Button) findViewById(R.id.login_but);
		sp = context.getSharedPreferences(ConstantValue.CONFIG, Context.MODE_PRIVATE);
	}
	
	@Override
	public void onResume() {
		super.onResume();
//		filePaht = bundle.getString("filePath");
//		id = bundle.getString("id");
//		photoList.add(filePaht);
//		setImage();
		getType();
		MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(context);
	}
	
	private void startProgressDialog(){  
        if (dialog == null){  
        	dialog = CustomProgressDialog.createDialog(context);  
        	dialog.hideText();  
        }  
        dialog.show();  
    }  
	
	private void stopProgressDialog(){  
        if (dialog != null){  
        	dialog.dismiss();  
        	dialog = null;  
        }  
    }  

	@Override
	public void setListener() {
		login_but.setOnClickListener(this);
		shop_img2_camera1.setOnClickListener(this);
		shop_ib_camera1.setOnClickListener(this);
		shop_img2_camera2.setOnClickListener(this);
		shop_ib_camera2.setOnClickListener(this);
		shop_img2_camera3.setOnClickListener(this);
		shop_ib_camera3.setOnClickListener(this);
//		shop_img2_camera4.setOnClickListener(this);
//		shop_ib_camera4.setOnClickListener(this);
		shop_img_camera_mentou.setOnClickListener(this);
	}

	@Override
	public int getID() {
		return ConstantValue.SAYFRAGMENT;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.shop_img_camera_mentou:
			filePaht2 = ImageUtils.getPhotopath();
			PromptManager.pickIMage(context, CAMERA,PICTURE,filePaht2);
			break;
		case R.id.login_but:
			submitClick();
			break;
		case R.id.shop_img2_camera1:
			//查看照片
			imageWitch(photoList.get(0));
			break;
		case R.id.shop_img2_camera2:
			//查看照片
			imageWitch(photoList.get(1));
			break;
		case R.id.shop_img2_camera3:
			//查看照片
			imageWitch(photoList.get(2));
			break;
		case R.id.shop_ib_camera1:
			//删除照片
			AlertDialog.Builder builder = new Builder(context);
			builder.setCancelable(false);
			builder.setTitle("消息提示")
					.setMessage("是否删除照片？")
					.setPositiveButton("删除", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							photoList.remove(0);
							setImage();
							shop_img_camera_mentou.setVisibility(View.VISIBLE);
						}
					}).setNegativeButton("不删除", null).show();
			break;
		case R.id.shop_ib_camera2:
			//删除照片
			AlertDialog.Builder builder2 = new Builder(context);
			builder2.setCancelable(false);
			builder2.setTitle("消息提示")
			.setMessage("是否删除照片？")
			.setPositiveButton("删除", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					photoList.remove(1);
					setImage();
					shop_img_camera_mentou.setVisibility(View.VISIBLE);
				}
			}).setNegativeButton("不删除", null).show();
			break;
		case R.id.shop_ib_camera3:
			//删除照片
			AlertDialog.Builder builder3 = new Builder(context);
			builder3.setCancelable(false);
			builder3.setTitle("消息提示")
			.setMessage("是否删除照片？")
			.setPositiveButton("删除", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					photoList.remove(2);
					setImage();
					shop_img_camera_mentou.setVisibility(View.VISIBLE);
				}
			}).setNegativeButton("不删除", null).show();
			break;
		}
	}
	
	private void submitClick() {
		//隐藏键盘
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);   
        imm.hideSoftInputFromWindow(shop_des.getWindowToken(),0);   
        //获取说说类型
        Map<String,String> select = (Map<String, String>) shop_date.getSelectedItem();
        String type_des = "";
        if(select != null) {
        	 id = select.get("id");
        	 type_des = select.get("title"); 
        }
		if("请选择说说类型".equals(type_des)) {
			PromptManager.showToast(context, "请选择说说类型");
			return;
		}
		des = shop_des.getText().toString().trim();
		if(StringUtils.isBlank(des)) {
			PromptManager.showToast(context, "请输入说说内容");
			return;
		}
		if(photoList.size() == 0) {
			PromptManager.showToast(context, "请添加说说图片");
			return;
		}
		xqid = sp.getString("XQID", "");
		userName = sp.getString("USERNAME", "");
		PromptManager.showProgressDialog(context, "提交中...");
		//生成b2id
		b2id = System.currentTimeMillis();
		submitImage(b2id);
	}
	
	protected void submitContext(long b2id2) {
		//title = StringUtil.nameNick(sp.getString("REALNAME", "用户"));
		String nick = userName;
		if(nick.length() > 10) {
        	String str = nick.substring(4, 8);
        	nick = nick.replaceAll(str, "****");
        }
		title = nick;
		new Thread(){
			public void run() {
				result = WebServiceUtils.bbsadd(userName,id,xqid,title,des,b2id);
				Message msg = new Message();
				msg.what = SUBMIT1;
				handler.sendMessage(msg);
			};
		}.start();
	}
	
	public String getByte(String filePath) {
		String uploadBuffer = "";
		try {
			FileInputStream fis = new FileInputStream(filePath);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[8192];
			int count = 0;
			while ((count = fis.read(buffer)) >= 0) {
				baos.write(buffer, 0, count);
			}
			uploadBuffer = new String(Base64.encode(baos.toByteArray()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uploadBuffer;
	}
	
	/**
	 * 提交图片
	 */
	protected void submitImage(final long b2id) {
		for(int i=0;i<photoList.size();i++){
			final String fileNam = photoList.get(i);
			final String file = getByte(fileNam);
			new Thread(){
				public void run() {
					result2 = WebServiceUtils.bbspicadd(file,b2id,ImageUtils.getImageType(fileNam));
					j = j + 1;
					if(j == photoList.size()) {
						Message msg = new Message();
						msg.what = SUBMIT2;
						handler.sendMessage(msg);
					}
				};
			}.start();
		}
	}


	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(context);
		/*if(!StringUtils.isBlank(des) || photoList.size() > 0) {
			AlertDialog.Builder builder = new Builder(context);
			builder.setTitle(R.string.app_name).setMessage("确定放弃发布当条说说?").setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					photoList.clear();
					j = 0;
					shop_des.setText("");
					shop_img_camera_mentou.setVisibility(View.VISIBLE);
					shop_rl_camera1.setVisibility(View.GONE);
					shop_rl_camera2.setVisibility(View.GONE);
					shop_rl_camera3.setVisibility(View.GONE);
				}
			}).setNegativeButton("取消", null).show();
		}*/
		photoList.clear();
		j = 0;
		shop_des.setText("");
		shop_img_camera_mentou.setVisibility(View.VISIBLE);
		shop_rl_camera1.setVisibility(View.GONE);
		shop_rl_camera2.setVisibility(View.GONE);
		shop_rl_camera3.setVisibility(View.GONE);
	}
	
	public void setImage() {
		int i = photoList.size();
		say_tv_camera.setVisibility(View.GONE);
		if(i == 0) {
			say_tv_camera.setVisibility(View.VISIBLE);
			shop_img_camera_mentou.setVisibility(View.VISIBLE);
			shop_rl_camera1.setVisibility(View.GONE);
			shop_rl_camera2.setVisibility(View.GONE);
			shop_rl_camera3.setVisibility(View.GONE);
		} else if(i == 1) {
			Bitmap bitmap1 = ImageUtils.decodeFileToCompress(photoList.get(0), 100, 100);
			if(bitmap1 != null) {
				shop_rl_camera1.setVisibility(View.VISIBLE);
				shop_rl_camera2.setVisibility(View.GONE);
				shop_rl_camera3.setVisibility(View.GONE);
				shop_img2_camera1.setImageBitmap(bitmap1);
			}
		} else if(i == 2) {
			Bitmap bitmap1 = ImageUtils.decodeFileToCompress(photoList.get(0), 100, 100);
			if(bitmap1 != null) {
				shop_rl_camera1.setVisibility(View.VISIBLE);
				shop_img2_camera1.setImageBitmap(bitmap1);
			}
			Bitmap bitmap2 = ImageUtils.decodeFileToCompress(photoList.get(1), 100, 100);
			if(bitmap2 != null) {
				shop_rl_camera2.setVisibility(View.VISIBLE);
				shop_img2_camera2.setImageBitmap(bitmap2);
			}
			shop_rl_camera3.setVisibility(View.GONE);
		} else if(i == 3) {
			Bitmap bitmap1 = ImageUtils.decodeFileToCompress(photoList.get(0), 100, 100);
			if(bitmap1 != null) {
				shop_rl_camera1.setVisibility(View.VISIBLE);
				shop_img2_camera1.setImageBitmap(bitmap1);
			}
			Bitmap bitmap2 = ImageUtils.decodeFileToCompress(photoList.get(1), 100, 100);
			if(bitmap2 != null) {
				shop_rl_camera2.setVisibility(View.VISIBLE);
				shop_img2_camera2.setImageBitmap(bitmap2);
			}
			Bitmap bitmap3 = ImageUtils.decodeFileToCompress(photoList.get(2), 100, 100);
			if(bitmap3 != null) {
				shop_rl_camera3.setVisibility(View.VISIBLE);
				shop_img2_camera3.setImageBitmap(bitmap3);
			}
			shop_img_camera_mentou.setVisibility(View.GONE);
		}
		
	}
	
	private void getType() {
		startProgressDialog();
		new Thread() {
			public void run() {
				res = WebServiceUtils.bbslist(0);
				Message msg = new Message();
				msg.what = TYPE;
				handler.sendMessage(msg);
			};
		}.start();
	}
	
	/**
	 * 查看图片
	 * @param i
	 */
	public static void imageWitch(String chosenPicFile) {
         Intent intent = new Intent(Intent.ACTION_VIEW);
         intent.setDataAndType(Uri.parse("file://" + chosenPicFile),"image/*");
         MiddleManager.getInstance().activity.startActivity(intent);
	}
	
	/**
	 * 处理拍照后调用
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		PromptManager.closeAlertDialog();
		if (CAMERA == requestCode && resultCode == Activity.RESULT_OK) {
			Bitmap bitmap = ImageUtils.decodeFileToCompress2(filePaht2,w - 80,h - 80); 
			String filePaths = ImageUtils.saveScalePhoto(bitmap);  
			LogUtils.i(TAG, filePaths);
			photoList.add(filePaths);
			setImage();
		}
		if (PICTURE == requestCode && resultCode == Activity.RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumns = { MediaStore.Images.Media.DATA };
			Cursor c = context.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
			if(c != null){
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePathColumns[0]);
				String filePaths = c.getString(columnIndex);
				c.close();
				// 获取图片并显示
				photoList.add(filePaths);
				setImage();
			} else {
				//选择图片  
		        Uri uri = data.getData();   
		        try {  
		        	photoList.add(FileUtil.getRealPath(context,uri));
					setImage(); 
		        } catch (Exception e) {  
		        }  
			}
		}
	}
	
	class MyAdapter extends BaseAdapter{
		
		private List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		public MyAdapter(List<Map<String,String>> list) {
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
			Map<String,String> info = list.get(position);
			ViewHolder holder = null;
			if(convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(context, R.layout.spinner_container, null);
				holder.name_id = (TextView) convertView.findViewById(R.id.name_id);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.name_id.setText(info.get("id"));
			holder.name.setText(info.get("title"));
			return convertView;
		}
		
		class ViewHolder{
			TextView name_id;
			TextView name;
		}
	}
	
}
