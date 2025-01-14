package com.huishangyun.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeText extends TextView {
	/** 是否允许跑马灯效果? */
	public static boolean allowMarquee = true;

	public MarqueeText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MarqueeText(Context context) {
		super(context);
	}
	
	public MarqueeText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isFocused()
	{
		if (allowMarquee)
		{
			return true;
		}
		
		return super.isFocused();
	}
	
}
