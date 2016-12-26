package com.huishangyun.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import com.huishangyun.App.MyApplication;
import com.huishangyun.model.LoginConfig;

public interface BaseActivityIm {
	/**
	 * 
	 * 获取MyAppliction.
	 * 
	 * @author pan
	 * @update 2014-7-6 上午9:05:51
	 */
	public abstract MyApplication getMyApplication();

	/**
	 * 
	 * 终止服务.
	 * 
	 * @author pan
	 * @update 2014-7-6 上午9:05:51
	 */
	public abstract void stopService(Context context);

	/**
	 * 
	 * 开启服务.
	 * 
	 * @author pan
	 * @update 2014-7-6 上午9:05:44
	 */
	public abstract void startService();

	/**
	 * 
	 * 校验网络-如果没有网络就弹出设置,并返回true.
	 * 
	 * @return
	 * @author pan
	 * @update 2014-7-6 上午9:03:56
	 */
	public abstract boolean validateInternet();

	/**
	 * 
	 * 校验网络-如果没有网络就返回true.
	 * 
	 * @return
	 * @author pan
	 * @update 2014-7-6 上午9:05:15
	 */
	public abstract boolean hasInternetConnected();

	/**
	 * 
	 * 退出应用.
	 * 
	 * @author pan
	 * @update 2014-7-6 上午9:06:42
	 */
	public abstract void isExit();

	/**
	 * 
	 * 判断GPS是否已经开启.
	 * 
	 * @return
	 * @author pan
	 * @update 2014-7-6 上午9:04:07
	 */
	public abstract boolean hasLocationGPS();

	/**
	 * 
	 * 判断基站是否已经开启.
	 * 
	 * @return
	 * @author pan
	 * @update 2014-7-6 上午9:07:34
	 */
	public abstract boolean hasLocationNetWork();

	/**
	 * 
	 * 检查内存卡.
	 * 
	 * @author pan
	 * @update 2014-7-6 上午9:07:51
	 */
	public abstract boolean checkMemoryCard();

	/**
	 * 
	 * 显示toast.
	 * 
	 * @param text
	 *            内容
	 * @param longint
	 *            内容显示多长时间
	 * @author pan
	 * @update 2014-7-6 上午9:12:02
	 */
	public abstract void showToast(String text, int longint);

	/**
	 * 
	 * 短时间显示toast.
	 * 
	 * @param text
	 * @author pan
	 * @update 2014-7-6 上午9:12:46
	 */
	public abstract void showToast(String text);

	/**
	 * 
	 * 获取进度条.
	 * 
	 * @return
	 * @author pan
	 * @update 2014-7-6 上午9:14:38
	 */
	public abstract ProgressDialog getProgressDialog();

	/**
	 * 
	 * 返回当前Activity上下文.
	 * 
	 * @return
	 * @author pan
	 * @update 2014-7-6 上午9:19:54
	 */
	public abstract Context getContext();

	/**
	 * 
	 * 获取当前登录用户的SharedPreferences配置.
	 * 
	 * @return
	 * @author pan
	 * @update 2014-7-6 上午9:23:02
	 */
	public SharedPreferences getLoginUserSharedPre();

	/**
	 * 
	 * 保存用户配置.
	 * 
	 * @param loginConfig
	 * @author pan
	 * @update 2014-7-6 上午9:58:31
	 */
	public void saveLoginConfig(LoginConfig loginConfig);

	/**
	 * 
	 * 获取用户配置.
	 *
	 * @author pan
	 * @update 2014-7-6 上午9:59:49
	 * @return LoginConfig 登录信息
	 */
	public LoginConfig getLoginConfig();

	/**
	 * 
	 * 用户是否在线（当前网络是否重连成功）
	 *
	 * @author pan
	 * @update 2014-7-6 上午9:59:49
	 */
	public boolean getUserOnlineState();

	/**
	 * 设置用户在线状态 true 在线 false 不在线
	 * 
	 * @param isOnline
	 */
	public void setUserOnlineState(boolean isOnline);

	/**
	 * 
	 * 发出Notification的method.
	 * 
	 * @param iconId
	 *            图标
	 * @param contentTitle
	 *            标题
	 * @param contentText
	 *            你内容
	 * @param activity
	 * @author pan
	 * @update 2014-5-14 下午12:01:55
	 */
	public void setNotiType(int iconId, String contentTitle,
			String contentText, Class activity, String from);
}
