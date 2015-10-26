package com.lemon95.wyzq.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;

/**
 * 图片处理工具�?
 * 
 * @author wu
 * @version 1.0
 * @date 2014.08.29
 */
public class ImageUtils {

	/**
	 * 获取本地压缩
	 * 
	 * @param pathName
	 *            图片路径
	 * @param dw
	 *            屏幕width
	 * @param dh
	 *            屏幕heigth
	 * @return
	 */
	public static Bitmap decodeFileToCompress(String pathName, int dw, int dh) {
		// TODO Auto-generated method stub
		Options opts = new Options();
		opts.inJustDecodeBounds = true; // 获取图片信息但不加载到内�?
		Bitmap b = BitmapFactory.decodeFile(pathName, opts);
		opts.inJustDecodeBounds = false;
		// 获取图片大小
		int imageX = (int) Math.ceil(opts.outWidth / (float) dw);
		int imageY = (int) Math.ceil(opts.outHeight / (float) dh);
		int sample = 1;
		if (imageX > imageY && imageX >= 1) {
			sample = imageX;
		}
		if (imageY > imageX && imageY >= 1) {
			sample = imageY;
		}
		opts.inSampleSize = sample;
		b = BitmapFactory.decodeFile(pathName, opts);
		return b;
	}

	/**
	 * 根据路径获取图片资源（已缩放）
	 * 
	 * @param url
	 *            图片存储路径
	 * @param width
	 *            缩放的宽度
	 * @param height
	 *            缩放的高度
	 * @return
	 */
	public static Bitmap getBitmapFromUrl(String url, double width, double height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 设置了此属性一定要记得将值设置为false
		Bitmap bitmap = BitmapFactory.decodeFile(url);
		// 防止OOM发生
		options.inJustDecodeBounds = false;
		int mWidth = bitmap.getWidth();
		int mHeight = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = 1;
		float scaleHeight = 1;
		// 按照固定宽高进行缩放
		// 这里希望知道照片是横屏拍摄还是竖屏拍摄
		// 因为两种方式宽高不同，缩放效果就会不同
		// 这里用了比较笨的方式
		if (mWidth <= mHeight) {
			scaleWidth = (float) (width / mWidth);
			scaleHeight = (float) (height / mHeight);
		} else {
			scaleWidth = (float) (height / mWidth);
			scaleHeight = (float) (width / mHeight);
		}
		matrix.postRotate(90); /* 翻转90度 */
		// 按照固定大小对图片进行缩放
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, mWidth, mHeight, matrix, true);
		// 用完了记得回收
		bitmap.recycle();
		return newBitmap;
	}

	/**
	 * 存储缩放的图片
	 * 
	 * @param data
	 *            图片数据
	 */
	public static String saveScalePhoto(Bitmap bitmap) {
		String sdState = Environment.getExternalStorageState();
		if (!sdState.equals(Environment.MEDIA_MOUNTED)) {
			return "";
		}
		String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
		String fileName = "/sdcard/pintu/";
		FileOutputStream fos = null;
		File file = new File(fileName);
		file.mkdirs();// 创建文件夹
		fileName = fileName + name;
		try {
			fos = new FileOutputStream(fileName);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.flush();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileName;
	}

	public static Bitmap decodeFileToCompress2(String pathName, int dw, int dh) {
		// TODO Auto-generated method stub
		Options opts = new Options();
		opts.inJustDecodeBounds = true; // 获取图片信息但不加载到内�?
		Bitmap b = BitmapFactory.decodeFile(pathName, opts);
		opts.inJustDecodeBounds = false;
		// 获取图片大小
		int imageX = (int) Math.ceil(opts.outWidth / (float) dw);
		int imageY = (int) Math.ceil(opts.outHeight / (float) dh);
		int sample = 2;
		if (imageX > imageY && imageX >= 1) {
			sample = imageX;
		}
		if (imageY > imageX && imageY >= 1) {
			sample = imageY;
		}
		opts.inSampleSize = sample;
		b = BitmapFactory.decodeFile(pathName, opts);
		return b;
	}

	/**
	 * 根据图片路径获取图片类型
	 * @param filePath 路径
	 * @return 类型
	 */
	public static String getImageType(String filePath) {
		String type = "png";
		if (filePath != null && !"".equals(filePath)) {
			type = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
		}
		return type;
	}
	
	/** 
     * 获取原图片存储路径 
     * @return 
     */  
    public static String getPhotopath() {  
        // 照片全路径  
        String fileName = "";  
        // 文件夹路径  
        String pathUrl = Environment.getExternalStorageDirectory()+"/mymy/";  
        String imageName = "imageTest.jpg";  
        File file = new File(pathUrl);  
        file.mkdirs();// 创建文件夹  
        fileName = pathUrl + imageName;  
        return fileName;  
    } 

}
