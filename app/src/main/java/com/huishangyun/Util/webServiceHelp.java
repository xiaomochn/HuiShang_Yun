package com.huishangyun.Util;

import java.lang.reflect.Type;

import android.os.Handler;
import android.os.Message;

/**
 * 访问webServices帮助类
 * @author Pan
 *
 */
@SuppressWarnings("hiding")
public class webServiceHelp<T>{
	
	private String methodName;
	private String json;
	private OnServiceCallBack<T> onServiceCallBack = null;
	private Type type;
	
	/**
	 * 接口数据回调接口
	 * @author Pan
	 *
	 * @param <T> 要获取的对象,可不填
	 */
	public interface OnServiceCallBack<T>{
		
		/**
		 * 访问接口获取到的数据
		 * @param haveCallBack 是否有访问结果
		 * @param zjResponse 返回的结果
		 */
		public abstract void onServiceCallBack(boolean haveCallBack,ZJResponse<T> zjResponse);
	}
	
	/**
	 * 构造方法,传人相应值
	 * @param methodName 要访问的接口名称
	 * @param type 要转换的对象
	 * 
	 */
	public webServiceHelp(String methodName, Type type) {
		this.type = type;
		this.methodName = methodName;
	}
	
	
	
	
	/**
	 * 设置监听接口(开启线程之前必须调用)
	 * @param onServiceCallBack
	 */
	public void setOnServiceCallBack(OnServiceCallBack<T> onServiceCallBack) {
		this.onServiceCallBack = onServiceCallBack;
	}
	
	
	/**
	 * 移除监听接口(onDestroy方法中必须调用)
	 */
	public void removeOnServiceCallBack() {
		if (this.onServiceCallBack != null) {
			this.onServiceCallBack = null;
		}
		
	}
	
	/**
	 * 开始访问接口
	 */
	public void start(String json) {
		this.json = json;
		new Thread(new getServiceInfo()).start();
	}
	
	/**
	 * 线程访问结果接收器
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			//
			case HanderUtil.case1:
				if (onServiceCallBack != null) {
					onServiceCallBack.onServiceCallBack(true, (ZJResponse<T>) msg.obj);
				}
				break;
				
			case HanderUtil.case2:
				if (onServiceCallBack != null) {
					onServiceCallBack.onServiceCallBack(false, null);
				}
				break;

			default:
				break;
			}
		};
	};
	
	/**
	 * 访问接口获取数据的线程
	 * @author Administrator
	 *
	 */
	private class getServiceInfo implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			//访问接口获取数据
			String result = DataUtil.callWebService(methodName, json);
			if (result != null) {
				//获取到数据并解析
				L.e("调接口返回数据 :" + result);
				try {
					
					ZJResponse<T> zjResponse = JsonUtil.fromJson(result, type);
					Message msg = new Message();
					msg.obj = zjResponse;
					msg.what = HanderUtil.case1;
					mHandler.sendMessage(msg);
				} catch (Exception e) {
					// TODO: handle exception
					//访问失败
					e.printStackTrace();
					mHandler.sendEmptyMessage(HanderUtil.case2);
				}
				
			} else {
				//访问失败
				mHandler.sendEmptyMessage(HanderUtil.case2);
			}
		}
		
	}
}
