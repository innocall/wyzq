package com.lemon95.wyzq.manage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.fragment.MainFragment;
import com.lemon95.wyzq.fragment.MySayFragment;
import com.lemon95.wyzq.utils.LogUtils;
import com.lemon95.wyzq.utils.MemoryManager;
import com.lemon95.wyzq.utils.SoftMap;
import com.lemon95.wyzq.view.BaseUI;
import com.starschina.media.ThinkoEnvironment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Global;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 中间容器的管理工具
 * 
 * @author Administrator
 * 
 */
public class MiddleManager extends Observable {
	private static final String TAG = "MiddleManager";
	private static MiddleManager instance = new MiddleManager();
	public ActionBarActivity activity;

	private MiddleManager() {
	}

	public static MiddleManager getInstance() {
		return instance;
	}

	private RelativeLayout middle;

	public void setMiddle(RelativeLayout middle) {
		this.middle = middle;
	}
	
	public void setActivity(ActionBarActivity activity) {
		this.activity = activity;
	}

	// 利用手机内存空间，换应用应用的运行速度
	private static Map<String, BaseUI> VIEWCACHE;// K
	// :唯一的标示BaseUI的子类

	static {
		// 16M，如果不足<16M(模拟器)
		// 32M，真机
		if (MemoryManager.hasAcailMemory()) {
			VIEWCACHE = new HashMap<String, BaseUI>();
		} else {
			VIEWCACHE = new SoftMap<String, BaseUI>();
		}
	}

	// 每增加一个界面150K——16M
	// 内存不足
	// 处理的方案：
	// 第一种：控制VIEWCACHE集合的size
	// 第二种：Fragment代替，replace方法，不会缓存界面
	// 第三种：降低BaseUI的应用级别
	// 强引用：当前（GC宁可抛出OOM，不会回收BaseUI）
	// 软引用：在OOM之前被GC回收掉
	// 弱引用：一旦被GC发现了就回收
	// 虚引用：一旦创建了就被回收了

	// 都存在优缺点
	// 第一种：代码实现简单，适应性不强
	// 第二种：上一个Fragment被回收了，当内存充足的时候，运行速度损失过多
	// 第三种：优点，缺点：虽然引用级别降低，但是必须等待GC去回收，必须要提供给GC一个回收的时间，所以一旦申请内存速度过快，不适用
	// 瀑布流——Lrucache

	private BaseUI currentUI;// 当前正在展示

	private LinkedList<String> HISTORY = new LinkedList<String>();// 用户操作的历史记录

	public BaseUI getCurrentUI() {
		return currentUI;
	}

	/**
	 * 如果需要传递数据给目标界面
	 * 
	 * @param targetClazz
	 * @param ssqBundle
	 */
	public void changeUI(Class<? extends BaseUI> targetClazz, Bundle bundle) {

		// 判断：当前正在展示的界面和切换目标界面是否相同
		if (currentUI != null && currentUI.getClass() == targetClazz) {
			return;
		}

		BaseUI targetUI = null;
		// 一旦创建过，重用
		// 判断是否创建了——曾经创建过的界面需要存储
		String key = targetClazz.getSimpleName();
		if (VIEWCACHE.containsKey(key)) {
			// 创建了，重用
			targetUI = VIEWCACHE.get(key);
		} else {
			// 否则，创建
			try {
				Constructor<? extends BaseUI> constructor = targetClazz.getConstructor(Context.class);
				targetUI = constructor.newInstance(getContext());
				VIEWCACHE.put(key, targetUI);
			} catch (Exception e) {
				e.printStackTrace();
				//throw new RuntimeException("constructor new instance error");
			}
		}

		if (targetUI != null) {
			targetUI.setBundle(bundle);
		}

		Log.i(TAG, targetUI.toString());

		if (currentUI != null) {
			// 在清理掉当前正在展示的界面之前——onPause方法
			currentUI.onPause();
		}
		// 切换界面的核心代码
		middle.removeAllViews();
		// FadeUtil.fadeOut(child1, 2000);
		View child = targetUI.getChild();
		middle.addView(child);
		child.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.ia_view_change));
		
		// FadeUtil.fadeIn(child, 2000, 1000);

		// 在加载完界面之后——onResume
		targetUI.onResume();

		currentUI = targetUI;

		// 将当前显示的界面放到栈顶
		HISTORY.addFirst(key);

		// 当中间容器切换成功时，处理另外的两个容器的变化
		changeTitleAndBottom();

	}

	/**
	 * 切换界面:解决问题“三个容器的联动”
	 * 
	 * @param ui
	 */
	public void changeUI(Class<? extends BaseUI> targetClazz) {
		// 判断：当前正在展示的界面和切换目标界面是否相同
		if (currentUI != null && currentUI.getClass() == targetClazz) {
			return;
		}

		BaseUI targetUI = null;
		// 一旦创建过，重用
		// 判断是否创建了——曾经创建过的界面需要存储
		String key = targetClazz.getSimpleName();
		if (VIEWCACHE.containsKey(key)) {
			// 创建了，重用
			targetUI = VIEWCACHE.get(key);
		} else {
			// 否则，创建
			try {
				Constructor<? extends BaseUI> constructor = targetClazz.getConstructor(Context.class);
				targetUI = constructor.newInstance(getContext());
				VIEWCACHE.put(key, targetUI);
			} catch (Exception e) {
				e.printStackTrace();
				//throw new RuntimeException("constructor new instance error");
			}
		}

		Log.i(TAG, targetUI.toString());

		if (currentUI != null) {
			// 在清理掉当前正在展示的界面之前——onPause方法
			currentUI.onPause();
		}
		// 切换界面的核心代码
		middle.removeAllViews();
		// FadeUtil.fadeOut(child1, 2000);
		View child = targetUI.getChild();
		middle.addView(child);
		child.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.ia_view_change));
		// FadeUtil.fadeIn(child, 2000, 1000);

		// 在加载完界面之后——onResume
		targetUI.onResume();

		currentUI = targetUI;

		// 将当前显示的界面放到栈顶
		HISTORY.addFirst(key);

		// 当中间容器切换成功时，处理另外的两个容器的变化
		changeTitleAndBottom();
	}

	private void changeTitleAndBottom() {
		// 1、界面一对应未登陆标题和通用导航
		// 2、界面二对应通用标题和玩法导航

		// 当前正在展示的如果是第一个界面
		// 方案一：存在问题，比对的依据：名称 或者 字节码
		// 在界面处理初期，将所有的界面名称确定
		// 如果是字节码，将所有的界面都的创建完成
		// if(currentUI.getClass()==FirstUI.class){
		// TitleManager.getInstance().showUnLoginTitle();
		// BottomManager.getInstrance().showCommonBottom();
		// }
		// if(currentUI.getClass().getSimpleName().equals("SecondUI")){
		// TitleManager.getInstance().showCommonTitle();
		// BottomManager.getInstrance().showGameBottom();
		// }

		// 方案二：更换比对依据

		/*
		 * switch (currentUI.getID()) { case ConstantValue.VIEW_FIRST: TitleManager.getInstance().showUnLoginTitle(); BottomManager.getInstrance().showCommonBottom(); //
		 * LeftManager\RightManager break; case ConstantValue.VIEW_SECOND: TitleManager.getInstance().showCommonTitle(); BottomManager.getInstrance().showGameBottom(); break; case
		 * 3: TitleManager.getInstance().showCommonTitle(); BottomManager.getInstrance().showGameBottom(); break; }
		 */

		// 降低三个容器的耦合度
		// 当中间容器变动的时候，中间容器“通知”其他的容器，你们该变动了，唯一的标示传递，其他容器依据唯一标示进行容器内容的切换
		// 通知：
		// 广播：多个应用
		// 为中间容器的变动增加了监听——观察者设计模式

		// ①将中间容器变成被观察的对象
		// ②标题和底部导航变成观察者
		// ③建立观察者和被观察者之间的关系（标题和底部导航添加到观察者的容器里面）
		// ④一旦中间容器变动，修改boolean，然后通知所有的观察者.updata()

		setChanged();
		notifyObservers(currentUI.getID());
	}

	/**
	 * 切换界面:解决问题“中间容器中，每次切换没有判断当前正在展示和需要切换的目标是不是同一个”
	 * 
	 * @param ui
	 */
	public void changeUI3(Class<? extends BaseUI> targetClazz) {
		// 判断：当前正在展示的界面和切换目标界面是否相同
		if (currentUI != null && currentUI.getClass() == targetClazz) {
			return;
		}

		BaseUI targetUI = null;
		// 一旦创建过，重用
		// 判断是否创建了——曾经创建过的界面需要存储
		String key = targetClazz.getSimpleName();
		if (VIEWCACHE.containsKey(key)) {
			// 创建了，重用
			targetUI = VIEWCACHE.get(key);
		} else {
			// 否则，创建
			try {
				Constructor<? extends BaseUI> constructor = targetClazz.getConstructor(Context.class);
				targetUI = constructor.newInstance(getContext());
				VIEWCACHE.put(key, targetUI);
			} catch (Exception e) {
				throw new RuntimeException("constructor new instance error");
			}
		}

		Log.i(TAG, targetUI.toString());

		// 切换界面的核心代码
		middle.removeAllViews();
		// FadeUtil.fadeOut(child1, 2000);
		View child = targetUI.getChild();
		middle.addView(child);
		child.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.ia_view_change));
		// FadeUtil.fadeIn(child, 2000, 1000);

		currentUI = targetUI;

		// 将当前显示的界面放到栈顶
		HISTORY.addFirst(key);
	}

	/**
	 * 切换界面:解决问题“在标题容器中每次点击都在创建一个目标界面”
	 * 
	 * @param ui
	 */
	public void changeUI2(Class<? extends BaseUI> targetClazz) {
		BaseUI targetUI = null;
		// 一旦创建过，重用
		// 判断是否创建了——曾经创建过的界面需要存储
		String key = targetClazz.getSimpleName();
		if (VIEWCACHE.containsKey(key)) {
			// 创建了，重用
			targetUI = VIEWCACHE.get(key);
		} else {
			// 否则，创建
			try {
				Constructor<? extends BaseUI> constructor = targetClazz.getConstructor(Context.class);
				targetUI = constructor.newInstance(getContext());
				VIEWCACHE.put(key, targetUI);
			} catch (Exception e) {
				throw new RuntimeException("constructor new instance error");
			}
		}

		Log.i(TAG, targetUI.toString());

		// 切换界面的核心代码
		middle.removeAllViews();
		// FadeUtil.fadeOut(child1, 2000);
		View child = targetUI.getChild();
		middle.addView(child);
		child.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.ia_view_change));
		// FadeUtil.fadeIn(child, 2000, 1000);
	}

	/**
	 * 切换界面
	 * 
	 * @param ui
	 */
	public void changeUI1(BaseUI ui) {
		// 切换界面的核心代码
		middle.removeAllViews();
		// FadeUtil.fadeOut(child1, 2000);
		View child = ui.getChild();
		middle.addView(child);
		child.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.ia_view_change));
		// FadeUtil.fadeIn(child, 2000, 1000);
	}

	public Context getContext() {
		return middle.getContext();
	}

	/**
	 * 返回键处理
	 * 
	 * @return
	 */
	public boolean goBack() {
		// 记录一下用户操作历史
		// 频繁操作栈顶（添加）——在界面切换成功
		// 获取栈顶
		// 删除了栈顶
		// 有序集合
		if (HISTORY.size() > 0) {
			// 当用户误操作返回键（不退出应用）
			if (HISTORY.size() == 1) {
				return false;
			}
			
			String key1 = HISTORY.getFirst();
			
			//主页面退出
			if(key1.equals("MainFragment") || key1.equals("CommunityFragment") || key1.equals("SayFragment") ||key1.equals("UserFragment")) {
				return false;
			}
			// Throws:NoSuchElementException - if this LinkedList is empty.
			HISTORY.removeFirst();
			
			/*if(key1.equals("VideoListFragment")) {
				//sdk释放
				LogUtils.i(TAG, "释放sdk");
				ThinkoEnvironment.tearDown();
			}*/
			
			/*if(key1.equals("MySayFragment")) {
				clearNew(MySayFragment.class);
			}*/
			
			if (HISTORY.size() > 0) {
				// Throws:NoSuchElementException - if this LinkedList is empty.
				String key = HISTORY.getFirst();
				
				BaseUI targetUI = VIEWCACHE.get(key);
				if (targetUI != null) {
					
					currentUI.onPause();
					middle.removeAllViews();
					middle.addView(targetUI.getChild());
					targetUI.onResume();
					currentUI = targetUI;
					View child = currentUI.getChild();
					child.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.right_in));
					changeTitleAndBottom();
				}else{
					// 处理方式一：创建一个新的目标界面：存在问题——如果有其他的界面传递给被删除的界面
					// 处理方式二：寻找一个不需要其他界面传递数据——跳转到首页
					changeUI(MainFragment.class);
					//PromptManager.showToast(getContext(), "应用在低内存下运行");
				}
				return true;
			}
		}
		return false;
	}
	
	/*public void goBack() {
		currentUI.goBack();
	}
*/
	public void clear() {
		HISTORY.clear();
		VIEWCACHE.clear();
	}
	
	public void clearNew(Class clazz) {
		HISTORY.remove(clazz.getSimpleName());
		VIEWCACHE.remove(clazz.getSimpleName());
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		currentUI.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 顶部右侧按钮事件
	 */
	public void submit() {
		currentUI.submit();
	}
	
	/**
	 * 我要说说
	 */
	public void say() {
		currentUI.say();
	}

	/**
	 * 查看消息按钮事件
	 */
	public void clickWatchMessage() {
		currentUI.clickWatchMessage();
	}

}
