package com.lemon95.wyzq.view;

import java.util.ArrayList;
import java.util.List;

import com.DevStoreDemo.threeTransformer.DepthPageTransformer;
import com.DevStoreDemo.threeTransformer.ViewPagerCompat;
import com.lemon95.wyzq.R;
import com.lemon95.wyzq.myview.pull.bitmapfun.util.ImageFetcher;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

public class ImageViewActivity extends Activity {
	private ViewPagerCompat mViewPager;
	private List<View> mImageViews = new ArrayList<View>();
	private ViewGroup group;
	private ImageFetcher mImageFetcher;
	// 用来表示每个小圆点的imageView
	private ImageView imageView;
	// 小圆点ImageViews的数组
	private ImageView[] imageViews;

	// 欢迎画面的数组
	// int[] mImgIds = new int[] { R.drawable.whatsnew_00,
	// R.drawable.whatsnew_01, R.drawable.whatsnew_02 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_before_login);
		mViewPager = (ViewPagerCompat) findViewById(R.id.id_viewpager);
		mImageFetcher = new ImageFetcher(getApplicationContext(), 240);
		mImageFetcher.setLoadingImage(R.drawable.black);
		mImageFetcher.setExitTasksEarly(false);
		initViewData();
		// 下面三个注释掉两个就能用其中一个的效果
		mViewPager.setPageTransformer(true, new DepthPageTransformer());
		mViewPager.setAdapter(new PagerAdapter() {
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				container.addView(mImageViews.get(position));
				//mImageViews.get(position).setOnTouchListener(new TouchListener());
				return mImageViews.get(position);
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {

				container.removeView(mImageViews.get(position));
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view == object;
			}

			@Override
			public int getCount() {
				return mImageViews.size();
			}
		});
	}

	private void initViewData() {
		// 强烈建议用第二种方法
		// 加入三张欢迎图片的第一种方法
		/*
		 * View layout0 = lInflater.inflate(R.layout.koei00, null); View layout1
		 * = lInflater.inflate(R.layout.koei01, null); View layout2 =
		 * lInflater.inflate(R.layout.koei02, null); mImageViews.add(layout0);
		 * mImageViews.add(layout1); mImageViews.add(layout2);
		 */
		// 加入三张欢迎图片的第二种方法
		String picPath = getIntent().getStringExtra("picPath");
		String pic = getIntent().getStringExtra("pic");
		int count = getIntent().getIntExtra("count", 0);
		if (pic != null) {
			String img[] = pic.split("<");
			for (int i = 0; i < img.length; i++) {
				ImageView imageView = new ImageView(getApplicationContext());
				imageView.setScaleType(ScaleType.FIT_CENTER);
				mImageFetcher.loadImage(img[i], imageView);
				mImageViews.add(imageView);
			}
		}
		group = (ViewGroup) findViewById(R.id.viewGroup);
		imageViews = new ImageView[mImageViews.size()];
		// 删除以下部分就没有小圆点了
		// 对小圆点的显示初始化
		for (int i = 0; i < mImageViews.size(); i++) {
			imageView = new ImageView(ImageViewActivity.this);
			imageView.setLayoutParams(new LayoutParams(60, 60));
			imageView.setPadding(60, 0, 60, 0);
			imageViews[i] = imageView;

			if (i == 0) {
				// 默认是第一张图片 小圆点是
				imageViews[i].setBackgroundResource(R.drawable.dark_dot);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.white_dot);
			}
			// 这里循环向LinearLayout中添加了view
			group.addView(imageViews[i]);
		}

		mViewPager.setOnPageChangeListener(new GuidePageChangeListener());
	}

	// 指引页面更改事件监听器(只是为了小圆点的变化)
	class GuidePageChangeListener
			implements OnPageChangeListener, com.DevStoreDemo.threeTransformer.ViewPagerCompat.OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[arg0].setBackgroundResource(R.drawable.dark_dot);
				if (arg0 != i) {
					imageViews[i].setBackgroundResource(R.drawable.white_dot);
				}
			}
		}
	}
	
	private final class TouchListener implements OnTouchListener {  
        
        /** 记录是拖拉照片模式还是放大缩小照片模式 */  
        private int mode = 0;// 初始状态    
        /** 拖拉照片模式 */  
        private static final int MODE_DRAG = 1;  
        /** 放大缩小照片模式 */  
        private static final int MODE_ZOOM = 2;  
          
        /** 用于记录开始时候的坐标位置 */  
        private PointF startPoint = new PointF();  
        /** 用于记录拖拉图片移动的坐标位置 */  
        private Matrix matrix = new Matrix();  
        /** 用于记录图片要进行拖拉时候的坐标位置 */  
        private Matrix currentMatrix = new Matrix();  
      
        /** 两个手指的开始距离 */  
        private float startDis;  
        /** 两个手指的中间点 */  
        private PointF midPoint;  
  
        @Override  
        public boolean onTouch(View v, MotionEvent event) {  
            /** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */  
            switch (event.getAction() & MotionEvent.ACTION_MASK) {  
            // 手指压下屏幕  
            case MotionEvent.ACTION_DOWN:  
                mode = MODE_DRAG;  
                // 记录ImageView当前的移动位置  
                currentMatrix.set(imageView.getImageMatrix());  
                startPoint.set(event.getX(), event.getY());  
                break;  
            // 手指在屏幕上移动，改事件会被不断触发  
            case MotionEvent.ACTION_MOVE:  
                // 拖拉图片  
                if (mode == MODE_DRAG) {  
                    float dx = event.getX() - startPoint.x; // 得到x轴的移动距离  
                    float dy = event.getY() - startPoint.y; // 得到x轴的移动距离  
                    // 在没有移动之前的位置上进行移动  
                    matrix.set(currentMatrix);  
                    matrix.postTranslate(dx, dy);  
                }  
                // 放大缩小图片  
                else if (mode == MODE_ZOOM) {  
                    float endDis = distance(event);// 结束距离  
                    if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10  
                        float scale = endDis / startDis;// 得到缩放倍数  
                        matrix.set(currentMatrix);  
                        matrix.postScale(scale, scale,midPoint.x,midPoint.y);  
                    }  
                }  
                break;  
            // 手指离开屏幕  
            case MotionEvent.ACTION_UP:  
                // 当触点离开屏幕，但是屏幕上还有触点(手指)  
            case MotionEvent.ACTION_POINTER_UP:  
                mode = 0;  
                break;  
            // 当屏幕上已经有触点(手指)，再有一个触点压下屏幕  
            case MotionEvent.ACTION_POINTER_DOWN:  
                mode = MODE_ZOOM;  
                /** 计算两个手指间的距离 */  
                startDis = distance(event);  
                /** 计算两个手指间的中间点 */  
                if (startDis > 10f) { // 两个手指并拢在一起的时候像素大于10  
                    midPoint = mid(event);  
                    //记录当前ImageView的缩放倍数  
                    currentMatrix.set(imageView.getImageMatrix());  
                }  
                break;  
            }  
            imageView.setImageMatrix(matrix);  
            return true;  
        }  
  
        /** 计算两个手指间的距离 */  
        private float distance(MotionEvent event) {  
            float dx = event.getX(1) - event.getX(0);  
            float dy = event.getY(1) - event.getY(0);  
            /** 使用勾股定理返回两点之间的距离 */  
            return FloatMath.sqrt(dx * dx + dy * dy);  
        }  
  
        /** 计算两个手指间的中间点 */  
        private PointF mid(MotionEvent event) {  
            float midX = (event.getX(1) + event.getX(0)) / 2;  
            float midY = (event.getY(1) + event.getY(0)) / 2;  
            return new PointF(midX, midY);  
        }  
  
    }  

}