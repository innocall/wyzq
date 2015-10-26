package com.lemon95.wyzq.utils;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.TypedValue;

public class ImageUtil {
	/**
	 * 旋转Bitmap
	 * 
	 * @param b
	 * @param rotateDegree
	 * @return
	 */
	public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
		Matrix matrix = new Matrix();
		matrix.postRotate((float) rotateDegree);
		Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
				b.getHeight(), matrix, false);
		return rotaBitmap;
	}

	/**
	 * 图片写文�?
	 * @param context
	 * @param bitmap
	 * @param gText
	 * @return
	 */
	public static Bitmap drawTextToBitmap(Context context,Bitmap photo, String gText) {
	   gText = DateUtils.getDate() + " " + gText;
       int width = photo.getWidth(), hight = photo.getHeight();
       Bitmap icon = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888); //建立�?��空的BItMap  
       Canvas canvas = new Canvas(icon);//初始化画布绘制的图像到icon�? 
        
       Paint photoPaint = new Paint(); //建立画笔  
       photoPaint.setDither(true); //获取跟清晰的图像采样  
       photoPaint.setFilterBitmap(true);//过滤�?��  
        
       Rect src = new Rect(0, 0, photo.getWidth(), photo.getHeight());//创建�?��指定的新矩形的坐�? 
       Rect dst = new Rect(0, 0, width, hight);//创建�?��指定的新矩形的坐�? 
       canvas.drawBitmap(photo, src, dst, photoPaint);//将photo 缩放或则扩大�?dst使用的填充区photoPaint  
        
       Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);//设置画笔  
       textPaint.setTextSize(18);//字体大小  
       //textPaint.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimensionPixelSize(R.dimen.accept_text)); 
       textPaint.setTypeface(Typeface.DEFAULT_BOLD);//采用默认的宽�? 
       textPaint.setColor(Color.RED);//采用的颜�? 
       canvas.drawText(gText, width - 170, hight-30, textPaint);//绘制上去字，�?��未知x,y采用那只笔绘�?
       canvas.save(Canvas.ALL_SAVE_FLAG); 
       canvas.restore(); 
       return icon;
	}
}
