package com.huishangyun.Interface;

import java.util.List;

/**
 * 选择类别监听
 * @author Pan
 */
public interface onTypeSelect {
	
	/**
	 * 选择类别监听
	 * @param type 对象列表
	 * @param typeName 类别名称
	 */
	public void onTypeSelectListener(String typeName, int type);
	
	
}
