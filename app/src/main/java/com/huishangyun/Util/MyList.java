package com.huishangyun.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MyList extends ListView {

	public MyList(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int height = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, height);
	}
}
