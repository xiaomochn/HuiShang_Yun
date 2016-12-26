
package com.huishangyun.Channel.Plan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class SwipeListView extends ListView {
    private Boolean mIsHorizontal;

    private View mPreItemView;

    private View mCurrentItemView;

    private float mFirstX;

    private float mFirstY;

    private int mRightViewWidth = 150;

    // private boolean mIsInAnimation = false;
    private final int mDuration = 150;

    private final int mDurationStep = 10;

    private boolean mIsShown;

    /**
     * 鏄惁鍏佽footer or Header Swipe
     */
    private boolean mIsFooterCanSwipe = false;

    private boolean mIsHeaderCanSwipe = false;

    public SwipeListView(Context context) {
        super(context);
    }

    public SwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * return true, deliver to listView. return false, deliver to child. if
     * move, return true
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float lastX = ev.getX();
        float lastY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsHorizontal = null;
                mFirstX = lastX;
                mFirstY = lastY;
                int motionPosition = pointToPosition((int)mFirstX, (int)mFirstY);

                if (motionPosition >= 0) {
                    View currentItemView = getChildAt(motionPosition - getFirstVisiblePosition());
                    mPreItemView = mCurrentItemView;
                    mCurrentItemView = currentItemView;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = lastX - mFirstX;
                float dy = lastY - mFirstY;

                if (Math.abs(dx) >= 5 && Math.abs(dy) >= 5) {
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsShown && (mPreItemView != mCurrentItemView || isHitCurItemLeft(lastX))) {
                    System.out.println("1---> hiddenRight");
                    /**
                     * 鎯呭喌涓�細
                     * <p>
                     * 涓�釜Item鐨勫彸杈瑰竷灞�凡缁忔樉绀猴紝
                     * <p>
                     * 杩欐椂鍊欑偣鍑讳换鎰忎竴涓猧tem, 閭ｄ箞閭ｄ釜鍙宠竟甯冨眬鏄剧ず鐨刬tem闅愯棌鍏跺彸杈瑰竷灞�                     */
                    hiddenRight(mPreItemView);
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    private boolean isHitCurItemLeft(float x) {
        return x < getWidth() - mRightViewWidth;
    }

    /**
     * @param dx
     * @param dy
     * @return judge if can judge scroll direction
     */
    private boolean judgeScrollDirection(float dx, float dy) {
        boolean canJudge = true;

        if (Math.abs(dx) > 30 && Math.abs(dx) > 2 * Math.abs(dy)) {
            mIsHorizontal = true;
        } else if (Math.abs(dy) > 30 && Math.abs(dy) > 2 * Math.abs(dx)) {
            mIsHorizontal = false;
        } else {
            canJudge = false;
        }

        return canJudge;
    }

    /**
     * @param posX
     * @param posY
     * @return judge if can footer judge
     */
    private boolean judgeFooterView(float posX, float posY) {
        // if footer can swipe
        if (mIsFooterCanSwipe) {
            return true;
        }
        // footer cannot swipe
        int selectPos = pointToPosition((int)posX, (int)posY);
        if (selectPos >= (getCount() - getFooterViewsCount())) {
            // is footer ,can not swipe
            return false;
        }
        // not footer can swipe
        return true;
    }

    /**
     * @param posX
     * @param posY
     * @return judge if can judge scroll direction
     */
    private boolean judgeHeaderView(float posX, float posY) {
        // if header can swipe
        if (mIsHeaderCanSwipe) {
            return true;
        }
        // header cannot swipe
        int selectPos = pointToPosition((int)posX, (int)posY);
        if (selectPos >= 0 && selectPos < getHeaderViewsCount()) {
            // is header ,can not swipe
            return false;
        }
        // not header can swipe
        return true;
    }

    /**
     * return false, can't move any direction. return true, cant't move
     * vertical, can move horizontal. return super.onTouchEvent(ev), can move
     * both.
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float lastX = ev.getX();
        float lastY = ev.getY();
        // test footer and header
        if (!judgeFooterView(mFirstX, mFirstY) || !judgeHeaderView(mFirstX, mFirstY)) {
            return super.onTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = lastX - mFirstX;
                float dy = lastY - mFirstY;
                // confirm is scroll direction
                if (mIsHorizontal == null) {
                    if (!judgeScrollDirection(dx, dy)) {
                        break;
                    }
                }

                if (mIsHorizontal) {
                    if (mIsShown && mPreItemView != mCurrentItemView) {
                        /**
                         * 鎯呭喌浜岋細
                         * <p>
                         * 涓�釜Item鐨勫彸杈瑰竷灞�凡缁忔樉绀猴紝
                         * <p>
                         * 杩欐椂鍊欏乏鍙虫粦鍔ㄥ彟澶栦竴涓猧tem,閭ｄ釜鍙宠竟甯冨眬鏄剧ず鐨刬tem闅愯棌鍏跺彸杈瑰竷灞�                         * <p>
                         * 鍚戝乏婊戝姩鍙Е鍙戣鎯呭喌锛屽悜鍙虫粦鍔ㄨ繕浼氳Е鍙戞儏鍐典簲
                         */
                        hiddenRight(mPreItemView);
                    }

                    if (mIsShown && mPreItemView == mCurrentItemView) {
                        dx = dx - mRightViewWidth;
                    }

                    // can't move beyond boundary
                    if (dx < 0 && dx > -mRightViewWidth) {
                        mCurrentItemView.scrollTo((int)(-dx), 0);
                    }

                    return true;
                } else {
                    if (mIsShown) {
                        /**
                         * 鎯呭喌涓夛細
                         * <p>
                         * 涓�釜Item鐨勫彸杈瑰竷灞�凡缁忔樉绀猴紝
                         * <p>
                         * 杩欐椂鍊欎笂涓嬫粴鍔↙istView,閭ｄ箞閭ｄ釜鍙宠竟甯冨眬鏄剧ず鐨刬tem闅愯棌鍏跺彸杈瑰竷灞�                         */
                        hiddenRight(mPreItemView);
                    }
                }

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                clearPressedState();
                if (mIsShown) {
                    /**
                     * 鎯呭喌鍥涳細
                     * <p>
                     * 涓�釜Item鐨勫彸杈瑰竷灞�凡缁忔樉绀猴紝
                     * <p>
                     * 杩欐椂鍊欏乏鍙虫粦鍔ㄥ綋鍓嶄竴涓猧tem,閭ｄ釜鍙宠竟甯冨眬鏄剧ず鐨刬tem闅愯棌鍏跺彸杈瑰竷灞�                     */
                    hiddenRight(mPreItemView);
                }

                if (mIsHorizontal != null && mIsHorizontal) {
                    if (mFirstX - lastX > mRightViewWidth / 2) {
                        showRight(mCurrentItemView);
                    } else {
                        /**
                         * 鎯呭喌浜旓細
                         * <p>
                         * 鍚戝彸婊戝姩涓�釜item,涓旀粦鍔ㄧ殑璺濈瓒呰繃浜嗗彸杈筕iew鐨勫搴︾殑涓�崐锛岄殣钘忎箣銆�                         */
                        hiddenRight(mCurrentItemView);
                    }

                    return true;
                }

                break;
        }

        return super.onTouchEvent(ev);
    }

    private void clearPressedState() {
        // TODO current item is still has background, issue
        mCurrentItemView.setPressed(false);
        setPressed(false);
        refreshDrawableState();
        // invalidate();
    }

    /**
	 * 向左滑动效果
	 * 
	 * @param view
	 */
	private void showRight(View view) {
		Message msg = new MoveHandler().obtainMessage();
		msg.obj = view;
		msg.arg1 = view.getScrollX();
		msg.arg2 = mRightViewWidth;
		msg.what=1;
		msg.sendToTarget();

		mIsShown = true;
	}

	/**
	 * 隐藏右边删除按钮
	 * 
	 * @param view
	 */
    public void hiddenRight(View view) {
        if (mCurrentItemView == null) {
            return;
        }
        Message msg = new MoveHandler().obtainMessage();//
        msg.obj = view;
        msg.arg1 = view.getScrollX();
        msg.arg2 = 0;
        msg.what=2;
        msg.sendToTarget();

        mIsShown = false;
    }

    /**
     * show or hide right layout animation
     * 向右滑动或者隐藏按钮事件处理
     */
    @SuppressLint("HandlerLeak")
    class MoveHandler extends Handler {
        private final String TAG = null;

		int stepX = 0;

        int fromX;

        int toX;
        int xy;
        View view;

        private boolean mIsInAnimation = false;

        private void animatioOver() {
            mIsInAnimation = false;
            stepX = 0;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (stepX == 0) {
                if (mIsInAnimation) {
                    return;
                }
                mIsInAnimation = true;
                view = (View)msg.obj;
                fromX = msg.arg1;
                toX = msg.arg2;
                xy = msg.what;
                
              //if语句添加原因：处理删除listview 的item时 ，视觉上关联和其它Item问题。
                if (xy==2) {
                	 stepX = (int)((toX - fromX) * mDurationStep * 1.0 / 0);
                	 Log.e(TAG, "K-------");
				}else {
					 stepX = (int)((toX - fromX) * mDurationStep * 1.0 / mDuration);	
				}
//                stepX = (int)((toX - fromX) * mDurationStep * 1.0 / mDuration);
                if (stepX < 0 && stepX > -1) {
                    stepX = -1;
                } else if (stepX > 0 && stepX < 1) {
                    stepX = 1;
                }
                if (Math.abs(toX - fromX) < 10) {
                    view.scrollTo(toX, 0);
                    animatioOver();
                    return;
                }
            }


            fromX += stepX;
            boolean isLastStep = (stepX > 0 && fromX > toX) || (stepX < 0 && fromX < toX);
            if (isLastStep) {
                fromX = toX;
            }

            view.scrollTo(fromX, 0);
            invalidate();

            if (!isLastStep) {
                this.sendEmptyMessageDelayed(0, mDurationStep);
            } else {
                animatioOver();
            }
        }
    }

    public int getRightViewWidth() {
        return mRightViewWidth;
    }

    public void setRightViewWidth(int mRightViewWidth) {
        this.mRightViewWidth = mRightViewWidth;
    }

    /**
     * 璁剧疆list鐨勮剼鏄惁鑳藉swipe
     * 
     * @param canSwipe
     */
    public void setFooterViewCanSwipe(boolean canSwipe) {
        mIsFooterCanSwipe = canSwipe;
    }

    /**
     * 璁剧疆list鐨勫ご鏄惁鑳藉swipe
     * 
     * @param canSwipe
     */
    public void setHeaderViewCanSwipe(boolean canSwipe) {
        mIsHeaderCanSwipe = canSwipe;
    }

    /**
     * 璁剧疆 footer and header can swipe
     * 
     * @param footer
     * @param header
     */
    public void setFooterAndHeaderCanSwipe(boolean footer, boolean header) {
        mIsHeaderCanSwipe = header;
        mIsFooterCanSwipe = footer;
    }
}
