package com.huishangyun.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.widget.TextView;

import com.huishangyun.yun.R;

/**
 * 设置字体颜色
 * @author Pan
 *
 */
public class ColorUtil {
	
	/**
	 * 设置任务界面颜色
	 * @param mContext
	 * @param stact
	 * @param textView
	 */
	public static void setTaskColor(Context mContext, int stact, TextView textView) {
		
		switch (stact) {
		case 0:
			textView.setTextColor(mContext.getResources().getColor(R.color.task_ch_0));
			break;
		case 1:
			textView.setTextColor(mContext.getResources().getColor(R.color.task_ch_1));
			break;
		case 2:
			textView.setTextColor(mContext.getResources().getColor(R.color.task_ch_2));
			break;
		case 3:
			textView.setTextColor(mContext.getResources().getColor(R.color.task_ch_3));
			break;
		case 4:
			textView.setTextColor(mContext.getResources().getColor(R.color.task_ch_4));
			break;
		default:
			break;
		}
	}
}
