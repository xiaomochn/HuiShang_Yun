package com.huishangyun.Office.Attendance;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 跑马灯效果
 * @author xsl
 *
 */
public class MarqueeText extends TextView {

	public MarqueeText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MarqueeText(Context context) {
		super(context);
		init();
	}
	
	public MarqueeText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		if(focused)
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if(hasWindowFocus)
			super.onWindowFocusChanged(hasWindowFocus);
	}

	@Override
	public boolean isFocused() {
		return true;
	}
	
	private void init() {
		setEllipsize(TruncateAt.MARQUEE);  //对应android:ellipsize="marquee"
		setMarqueeRepeatLimit(-1);  //对应android:marqueeRepeatLimit="marquee_forever"
		setSingleLine();  //等价于setSingleLine(true)
	}
	
}
