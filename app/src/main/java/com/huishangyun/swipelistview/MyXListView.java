package com.huishangyun.swipelistview;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.huishangyun.Util.L;
import com.huishangyun.yun.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

/**
 * 下拉刷新和上拉加载的Listview
 * @author Pan
 * @version 亿企云APP V1.0 2014-08-22 09:42
 *
 */
public class MyXListView extends ListView implements OnScrollListener{
	private float mLastY = -1; // 保存事件Y位移
	private Scroller mScroller; // 用于滚动
	private OnScrollListener mScrollListener; // 用户的滚动听

	// 该接口以触发刷新和加载更多。
	private MyXListViewListener mListViewListener;

	// --标题视图
	private XListViewHeader mHeaderView;
	// 标题查看内容，用它来计算标头的高度。而隐藏
	// 当禁止上拉刷新。
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	private int mHeaderViewHeight; // 头视图的高度
	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing = false; // 是否正在刷新.

	// -- 页脚视图
	private XListViewFooter mFooterView;
	private boolean mEnablePullLoad;
	private boolean mPullLoading;
	private boolean mIsFooterReady = false;

	// 总的列表项，用来检测是ListView的底部。
	private int mTotalItemCount;

	// 对于mScroller，从滚动页眉或页脚回来。
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400; // 向后滚动时间
	private final static int PULL_LOAD_MORE_DELTA = 50; // 当下拉位移 >= 50px
														// 在底部，触发
														// 加载更多。
	private final static float OFFSET_RADIO = 1.8f; // 仿iOS的下拉
													// 功能。

	/**
	 * @param context
	 */
	
	public MyXListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}
	
	public MyXListView(Context context) {
		super(context);
		initWithContext(context);
	}
	
	public MyXListView(Context context, AttributeSet attrs, int arg2) {
		super(context, attrs, arg2);
		initWithContext(context);
	}
	
	private void initWithContext(Context context) {
		mScroller = new Scroller(context, new DecelerateInterpolator());
		// XListView需要scroll事件，并会分派事件
		// 用户的监听器（作为代理）。
		super.setOnScrollListener(this);

		// 初始化标题视图
		mHeaderView = new XListViewHeader(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView
				.findViewById(R.id.xlistview_header_content);
		mHeaderTimeView = (TextView) mHeaderView
				.findViewById(R.id.xlistview_header_time);
		addHeaderView(mHeaderView);

		// 初始化页脚高度
		mFooterView = new XListViewFooter(context);

		// 初始化头高度
		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mHeaderViewHeight = mHeaderViewContent.getHeight();
						getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
		});
	}
	
	
	@Override
	public void setAdapter(ListAdapter adapter) {
		// 确保XListViewFooter是最后页脚视图，只添加一次。
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			addFooterView(mFooterView);
		}
		super.setAdapter(adapter);
	}
	
	
	/**
	 * 启用或禁用下拉刷新功能。
	 * 
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) { // disable, hide the content
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}
	
	
	/**
	 * 启用或禁用拉起负载更多的功能。
	 * 
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			mFooterView.hide();
			mFooterView.setOnClickListener(null);
			removeFooterView(mFooterView);
		} else {
			mPullLoading = false;
			mFooterView.show();
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
			// 既有“拉”和“单击”将调用加载更多。
			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}
	
	/**
	 * 设置下拉文字
	 */
	public void setIsChat() {
		mHeaderView.setIsChat();
	}
	
	public void setTimeLayoutGone() {
		mHeaderView.setTimeLayoutGone();
	}
	
	
	/**
	 * 停止刷新，重置标题视图。
	 */
	public void stopRefresh() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight();
		}
	}
	
	/**
	 *停止加载更多，复位页脚视图。
	 */
	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
		}
	}

	/**
	 * 设置最后刷新时间
	 * 
	 */
	public void setRefreshTime() {
		long time = System.currentTimeMillis();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		Date date = new Date(time);
		String updataTime = formatter.format(date);
		mHeaderTimeView.setText(updataTime);
	}
	
	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	private void updateHeaderHeight(float delta) {
		mHeaderView.setVisiableHeight((int) delta
				+ mHeaderView.getVisiableHeight());
		if (mEnablePullRefresh && !mPullRefreshing) { //未处于刷新状态，更新箭头
			if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
				mHeaderView.setState(XListViewHeader.STATE_READY);
			} else {
				mHeaderView.setState(XListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0); // 每次滚动到页首
	}

	/**
	 * 重置视图的高度。
	 */
	private void resetHeaderHeight() {
		int height = mHeaderView.getVisiableHeight();
		if (height == 0) // not visible.
			return;
		// 刷新中的头没有完全显示。什么也不做。
		if (mPullRefreshing && height <= mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0; // 默认：回滚动隐藏头。
		// 正在刷新, 只是向后滚动来显示所有的标题。
		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);
		// 触发computeScroll
		invalidate();
	}
	
	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
													// more.
				mFooterView.setState(XListViewFooter.STATE_READY);
			} else {
				mFooterView.setState(XListViewFooter.STATE_NORMAL);
			}
		}
		mFooterView.setBottomMargin(height);

		// setSelection(mTotalItemCount - 1); // scroll to bottom
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			invalidate();
		}
	}
	
	public void startLoadMore() {
		mPullLoading = true;
		mFooterView.setState(XListViewFooter.STATE_LOADING);
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
	}
	
	public void startRefresh(){
		mPullLoading = true;
		mFooterView.setState(XListViewFooter.STATE_LOADING);
	}
	
	/**
	 * 调用刷新
	 */
	public void startRefresh2(){
		mPullRefreshing = true;
		mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
		if (mListViewListener != null) {
			mListViewListener.onRefresh();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			L.v("数据监测：" + getFirstVisiblePosition() + "---->"
					+ getLastVisiblePosition());
			if (getFirstVisiblePosition() == 0
					&& (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
				// 中的第一项表示，标题显示或拉下。
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1
					&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
				// 最后一个项目，已经拉或要拉起来。
				updateFooterHeight(-deltaY / OFFSET_RADIO);
			}
			break;
		default:
			mLastY = -1; // 重置
			if (getFirstVisiblePosition() == 0) {
				// 调用刷新
				if (mEnablePullRefresh
						&& mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
					mPullRefreshing = true;
					mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
					if (mListViewListener != null) {
						mListViewListener.onRefresh();
					}
				}
				resetHeaderHeight();
			}
			if (getLastVisiblePosition() == mTotalItemCount - 1) {
				// invoke load more.
				if (mEnablePullLoad
						&& mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
					startLoadMore();
				}
				resetFooterHeight();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}
	
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// 发送到用户的监听器
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	public void setMyXListViewListener(MyXListViewListener l) {
		mListViewListener = l;
	}
	
	/**
	 * 你可以听ListView.OnScrollListener还是这一个。它会调用 
	 * onXScrolling在页眉/页脚滚动回.
	 */
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	/**
	 * 实现此接口来获得刷新/负载更多的事件。
	 */
	public interface MyXListViewListener {
		/**
		 * 下拉刷新
		 */
		public void onRefresh();
		
		/**
		 * 上拉加载
		 */
		public void onLoadMore();
	}
	
}
