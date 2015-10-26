package com.lemon95.wyzq.myview.listview;


import com.lemon95.wyzq.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class RefreshListView extends ListView implements OnScrollListener {

	private static final String TAG = "RefreshListView";

	private OnRefreshListener mOnRefershListener;
	private boolean isScrollToBottom; // 是否滑动到底部
	private View footerView; // 脚布局的对象
	private int footerViewHeight; // 脚布局的高度
	private boolean isLoadingMore = false; // 是否正在加载更多中
	
	public RefreshListView(Context context) {
		super(context);
		initFooterView();
		this.setOnScrollListener(this);
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initFooterView();
		this.setOnScrollListener(this);
	}

	/**
	 * 初始化脚布局
	 */
	private void initFooterView() {
		footerView = View.inflate(getContext(), R.layout.listview_footer, null);
		footerView.measure(0, 0);
		footerViewHeight = footerView.getMeasuredHeight();
		footerView.setPadding(0, -footerViewHeight, 0, 0);
		this.addFooterView(footerView);
	}

	/**
	 * 当滚动状态改变时回调
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {
			// 判断当前是否已经到了底部
			if (isScrollToBottom && !isLoadingMore) {
				isLoadingMore = true;
				// 当前到底部
				Log.i(TAG, "加载更多数据");
				footerView.setPadding(0, 0, 0, 0);
				this.setSelection(this.getCount());

				if (mOnRefershListener != null) {
					mOnRefershListener.onLoadingMore();
				}
			}
		}
	}


	/**
	 * 设置刷新监听事件
	 * 
	 * @param listener
	 */
	public void setOnRefreshListener(OnRefreshListener listener) {
		mOnRefershListener = listener;
	}


	public void hideFooterView() {
		footerView.setPadding(0, -footerViewHeight, 0, 0);
		isLoadingMore = false;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (getLastVisiblePosition() == (totalItemCount - 1)) {
			isScrollToBottom = true;
		} else {
			isScrollToBottom = false;
		}
	}
}
