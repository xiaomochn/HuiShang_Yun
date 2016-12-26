package com.huishangyun.yindao;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;

import com.huishangyun.Activity.LandActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.model.Constant;


public class ViewPagerAdapter extends PagerAdapter {

	// 界面列表
	private List<View> views;
	private Activity activity;

	public ViewPagerAdapter(List<View> views, Activity activity) {
		this.views = views;
		this.activity = activity;
	}

	// 销毁arg1位置的界面
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(views.get(arg1));
	}

	public void finishUpdate(View arg0) {
	}

	// 获得当前界面数
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		return 0;
	}

	// 初始化arg1位置的界面
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(views.get(arg1), 0);
		if (arg1 == views.size() - 1) {
			/*Button bt = (Button) arg0.findViewById(R.id.bt_start);			
			bt.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// 设置已经引导
					setGuided();
					goHome();

				}
			});*/
			views.get(arg1).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setGuided();
					goHome();
				}
			});
		}
		return views.get(arg1);
	}

	private void goHome() {		
		Intent intent = new Intent(activity, LandActivity.class);// 跳转
		activity.startActivity(intent);
		activity.finish();
		MyApplication.preferences.edit().putBoolean(Constant.HUISHANG_YINGDAO, true).commit();
	}

	/**
	 * 
	 * method desc：设置已经引导过了，下次启动不用再次引导
	 */
	private void setGuided() {
		MyApplication.preferences.edit().putBoolean(Constant.HUISHANG_YINGDAO, true).commit();
	}

	// 判断是否由对象生成界面
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}
	public Parcelable saveState() {
		return null;
	}
	public void startUpdate(View arg0) {
	}

}
