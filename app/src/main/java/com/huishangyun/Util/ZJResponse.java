package com.huishangyun.Util;

import java.util.List;

/**
 * 统一的接口返回数据传递类
 * @author Pan
 * @see 无
 * @version 亿企云 V1.0 2014-08-15
 * @param <T> 泛型
 */
public class ZJResponse<T> {
	private Integer Code;//返回码
	private String Desc;//返回内容说明
	private T Result;//对象
	private List<T> Results;//对象列表
	
	public Integer getCode() {
		return Code;
	}
	public void setCode(Integer code) {
		Code = code;
	}
	public String getDesc() {
		return Desc;
	}
	public void setDesc(String desc) {
		Desc = desc;
	}
	public <T> T getResult() {
		return  (T) Result;
	}
	public void setResult(T result) {
		Result = result;
	}
	public <T> List<T> getResults() {
		return  (List<T>) Results;
	}
	public  void setResults(List<T> results) {
		Results = results;
	}
	
}
