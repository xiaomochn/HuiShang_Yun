package com.huishangyun.Office.WeiGuan;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 自定义listview重写onMeasure方法
 * 
 * @author xsl
 * 
 */
public class MyListView extends ListView {

	public MyListView(Context context) {

		super(context);

	}

	public MyListView(Context context, AttributeSet attrs) {

		super(context, attrs);

	}

	public MyListView(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);

	}

	//重写onMeasure准确计算listview的高度，防止textview只能一行显示时正常
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,

		MeasureSpec.AT_MOST);

		super.onMeasure(widthMeasureSpec, expandSpec);

	}
	
}
