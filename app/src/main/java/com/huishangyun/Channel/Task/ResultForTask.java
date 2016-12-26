package com.huishangyun.Channel.Task;

import java.util.List;

/**
 * 传递结果的接口
 * @author Pan
 *
 */
public interface ResultForTask <T>{
	/**
	 * 查询成功返回值
	 */
	public static final int RESULT_OK = 0;
	
	/**
	 * 查询失败返回值
	 */
	public static final int RESULT_FULL = 1;
	
	/**
	 * 返回结果
	 * @param resultCode
	 * @param resultData
	 */
	public void onResult(int resultCode,T resultData);
	
	/**
	 * 返回结果
	 * @param resultCode
	 * @param resultDatas
	 */
	public void onResults(int resultCode,List<T> resultDatas);
}
