package com.lemon95.wyzq.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class FileUtil {
	private static final String TAG = "FileUtil";
	private static final File parentPath = Environment.getExternalStorageDirectory();
	private static String storagePath = "";
	private static final String DST_FOLDER_NAME = "PlayCamera";

	/**
	 * 初始化保存路�?
	 * 
	 * @return
	 */
	private static String initPath() {
		if (storagePath.equals("")) {
			storagePath = parentPath.getAbsolutePath() + "/" + DST_FOLDER_NAME;
			File f = new File(storagePath);
			if (!f.exists()) {
				f.mkdir();
			}
		}
		return storagePath;
	}

	/**
	 * 保存Bitmap到sdcard
	 * 
	 * @param b
	 */
	public static void saveBitmap(String jpegName, Bitmap b) {

		Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			LogUtils.i(TAG, "saveBitmap成功");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtils.i(TAG, "saveBitmap:失败");
			e.printStackTrace();
		}

	}

	public static String getRealPath(Context mContext,Uri fileUrl){
		String fileName = fileUrl.toString();
		if(fileUrl!= null){
		   if (fileName.startsWith("file")){
		       fileName = fileName.replace("file://", "");
		   }
		}
	   return fileName;
	}
	
	public static String getVideoPath(String fileUrl){
		if(!StringUtils.isBlank(fileUrl)){
			fileUrl = fileUrl.substring(fileUrl.lastIndexOf("/") + 1, fileUrl.length());
		}
	    return fileUrl;
	}
	
	/**
	 * 随机获取视频图片
	 * @param filePath
	 * @return
	 */
	public static Bitmap getVideoThumbnail(String filePath) {
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try {
			retriever.setDataSource(filePath);
			bitmap = retriever.getFrameAtTime();
		} 
		catch(IllegalArgumentException e) {
			e.printStackTrace();
		} 
		catch (RuntimeException e) {
			e.printStackTrace();
		} 
		finally {
			try {
				retriever.release();
			} 
			catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

}
