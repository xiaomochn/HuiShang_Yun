package com.huishangyun.Interface;

import android.R.integer;

/**
 * 悬浮框选择返回结果
 * @author Pan
 *
 */
public interface OnDialogDown {
	
	/**
	 * 悬浮框选择返回结果
	 * @param position-当前选中项在list中的位置
	 * @param type-返回结果的类型
	 */
	public void onDialogDown(int position, int type);
}
