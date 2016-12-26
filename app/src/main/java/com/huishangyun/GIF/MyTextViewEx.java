package com.huishangyun.GIF;

import java.util.Hashtable;
import java.util.Vector;

import com.huishangyun.Util.L;

import android.content.Context;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.widget.TextView;
public class MyTextViewEx extends TextView implements Runnable {
	public static boolean mRunning = true;
	private Vector<GifDrawalbe> drawables;
	private Hashtable<Integer, GifDrawalbe> cache;
	private final int SPEED = 250;
	private Context context = null;
	
	public MyTextViewEx(Context context, AttributeSet attr) {
		super(context, attr);
		this.context = context;
		
		drawables = new Vector<GifDrawalbe>();
		cache = new Hashtable<Integer, GifDrawalbe>();
		
		new Thread(this).start();
	}
	
	public MyTextViewEx(Context context) {
		super(context);
		this.context = context;
		
		drawables = new Vector<GifDrawalbe>();
		cache = new Hashtable<Integer, GifDrawalbe>();
		
		new Thread(this).start();
	}

	public void insertGif(String str) {
		if (drawables.size() > 0)
			drawables.clear();
		L.e("传入文字为:" + str);
		SpannableString spannableString = ExpressionUtil.getExpressionString(context, str, cache, drawables);
		setText(spannableString);
	}

	@Override
	public void run() {
		while (mRunning) {
			if (super.hasWindowFocus()) {
				for (int i = 0; i < drawables.size(); i++) {
					drawables.get(i).run();
				}
				postInvalidate();
			}
			sleep();
		}
	}

	private void sleep() {
		try {
			Thread.sleep(SPEED);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		mRunning = false;
		drawables.clear();
		drawables = null;
	}
	
	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		L.e("-------------------onDetachedFromWindow-------------------");
	//	destroy();
	}

}
