package com.huishangyun.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MyScrollLayout extends ViewGroup{
private static final String TAG = "ScrollLayout";
	
	public Scroller mScroller;
	public VelocityTracker mVelocityTracker;
	
	public int mCurScreen;
	public int mDefaultScreen = 0;
	
	public static final int TOUCH_STATE_REST = 0;
	public static final int TOUCH_STATE_SCROLLING = 1;
	
	public static final int SNAP_VELOCITY = 600;
	
	public int mTouchState = TOUCH_STATE_REST;
	public int mTouchSlop;
	public float mLastMotionX;
	public float mLastMotionY;
	
	public MyScrollLayout(Context context) {
		super(context);
	}
	public MyScrollLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public MyScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScroller = new Scroller(context);
		mCurScreen = mDefaultScreen;
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if(changed){
			int childLeft = 0;
			final int childCount = getChildCount();
			Log.d(TAG, "childCount == "+childCount);
			for(int i=0;i<childCount;i++){
				final View childView = getChildAt(i);
				if(childView.getVisibility() != View.GONE){
					final int width = childView.getMeasuredWidth();
					childView.layout(childLeft, 0, childLeft+width, childView.getMeasuredHeight());
					childLeft += width;
				}
			}
		}
	}
	
	 @Override  
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {   
//	    	Log.e(TAG, "onMeasure --被执行--");
	        super.onMeasure(widthMeasureSpec, heightMeasureSpec);   
	  
	        final int width = MeasureSpec.getSize(widthMeasureSpec);   
	        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);   
	        if (widthMode != MeasureSpec.EXACTLY) {   
	            throw new IllegalStateException("ScrollLayout only canmCurScreen run at EXACTLY mode!"); 
	        }   
	  
	        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);   
	        if (heightMode != MeasureSpec.EXACTLY) {   
	            throw new IllegalStateException("ScrollLayout only can run at EXACTLY mode!");
	        }   
	  
	        // The children are given the same width and height as the scrollLayout   
	        final int count = getChildCount();   
	        for (int i = 0; i < count; i++) {   
	            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);   
	        }   
	        // Log.e(TAG, "moving to screen "+mCurScreen);   
	        scrollTo(mCurScreen * width, 0);         
	    }  
	    
	    /**
	     * According to the position of current layout
	     * scroll to the destination page.
	     */
	    public void snapToDestination() {
	    	final int screenWidth = getWidth();
	    	final int destScreen = (getScrollX()+ screenWidth/2)/screenWidth;
	    	snapToScreen(destScreen);
	    }
	    
	    public Scroller getScroller(){
	    	return mScroller;
	    }
	    
	    public void snapToScreen(int whichScreen) {
	    	whichScreen = Math.max(0, Math.min(whichScreen, getChildCount()-1));
	    	if (getScrollX() != (whichScreen*getWidth())) {
	    		
	    		final int delta = whichScreen*getWidth()-getScrollX();
	    		mScroller.startScroll(getScrollX(), 0, 
	    				delta, 0, Math.abs(delta)*2);
	    		mCurScreen = whichScreen;
	    		invalidate();		// Redraw the layout
	    	}
	    }
	    
	    public void setToScreen(int whichScreen) {
	    	whichScreen = Math.max(0, Math.min(whichScreen, getChildCount()-1));
	    	mCurScreen = whichScreen;
	    	scrollTo(whichScreen*getWidth(), 0);
	    }
	    
	    public int getCurScreen() {
	    	return mCurScreen;
	    }
	    
	    @Override
	    public void computeScroll(){
	    	if(mScroller.computeScrollOffset()){
	    		scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
	    		postInvalidate();
	    	}
	    }
	    
	    @Override
	    public boolean onTouchEvent(MotionEvent event){
	    	if(mVelocityTracker == null){
	    		mVelocityTracker = VelocityTracker.obtain();
	    	}
	    	mVelocityTracker.addMovement(event);
	    	
	    	final int action = event.getAction();
	    	final float x = event.getX();
	    	final float y = event.getY();
	    	
	    	switch(action){
	    	case MotionEvent.ACTION_DOWN:
	    		if(!mScroller.isFinished()){
	    			mScroller.abortAnimation();
	    		}
	    		mLastMotionX = x;
	    		break;
	    	case MotionEvent.ACTION_MOVE:
	    		int deltaX = (int)(mLastMotionX - x);
	    		mLastMotionX = x;
	    		scrollBy(deltaX, 0);
	    		break;
	    	case MotionEvent.ACTION_UP:
	    		final VelocityTracker velocityTracker = mVelocityTracker;
	    		velocityTracker.computeCurrentVelocity(1000);
	    		int velocityX = (int)velocityTracker.getXVelocity();
	    		if(velocityX > SNAP_VELOCITY && mCurScreen >0){
	    			snapToScreen(mCurScreen - 1);
	    		}else if(velocityX < -SNAP_VELOCITY && mCurScreen<getChildCount()-1){
	    			snapToScreen(mCurScreen + 1);
	    		}else{
	    			snapToDestination();
	    		}
	    		
	    		if(mVelocityTracker != null){
	    			mVelocityTracker.recycle();
	    			mVelocityTracker = null;
	    		}
	    		mTouchState = TOUCH_STATE_REST;
	    		break;
	    	case MotionEvent.ACTION_CANCEL:
	    		mTouchState = TOUCH_STATE_REST;
	    		break;
	    	}
	    	return true;
	    }
	    
	    @Override
	    public boolean onInterceptTouchEvent(MotionEvent ev){
	    	final int action = ev.getAction();
	    	if((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)){
	    		return true;
	    	}
	    	
	    	final float x = ev.getX();
	    	final float y = ev.getY();
	    	
	    	switch(action){
	    	case MotionEvent.ACTION_MOVE:
	    		final int xDiff = (int)Math.abs(mLastMotionX - x);
	    		if(xDiff > mTouchSlop){
	    			mTouchState = TOUCH_STATE_SCROLLING;
	    		}
	    		break;
	    	case MotionEvent.ACTION_DOWN:
	    		mLastMotionX = x;
	    		mLastMotionY = y;
	    		mTouchState = mScroller.isFinished()? TOUCH_STATE_REST:TOUCH_STATE_SCROLLING;
	    		break;
	    	case MotionEvent.ACTION_CANCEL:
	    	case MotionEvent.ACTION_UP:
	    		mTouchState = TOUCH_STATE_REST;
	    		break;
	    	}
	    	
	    	return mTouchState != TOUCH_STATE_REST;    	
	    }
}
