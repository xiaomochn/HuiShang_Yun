package com.huishangyun.yindao;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.huishangyun.yun.R;

/**
 */
public class GuideActivity extends Activity implements OnPageChangeListener {

	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;

	// 底部小点图片
	/*private List<View> dots;*/
	private int oldPosition = 0;
	
	// 记录当前选中位置
	private int currentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.guide_viewpager);

		// 初始化底部小点
		/*dots = new ArrayList<View>();
		dots.add(findViewById(R.id.guide_view1));
		dots.add(findViewById(R.id.guide_view2));
		dots.add(findViewById(R.id.guide_view3));
		dots.add(findViewById(R.id.guide_view4));*/
		
		// 初始化页面
		initViews();
	}

	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(this);

		views = new ArrayList<View>();
		// 初始化引导图片列表
		views.add(inflater.inflate(R.layout.guide_one, null));
		views.add(inflater.inflate(R.layout.guide_two, null));
		views.add(inflater.inflate(R.layout.guide_three, null));
		views.add(inflater.inflate(R.layout.guide_splash, null));
		views.add(inflater.inflate(R.layout.guide_four, null));

		// 初始化Adapter
		vpAdapter = new ViewPagerAdapter(views, this);
		
		vp = (ViewPager) findViewById(R.id.guide_viewpager);
		vp.setAdapter(vpAdapter);
		// 绑定回调
		vp.setOnPageChangeListener(this);
	}


	// 当滑动状态改变时调用
	public void onPageScrollStateChanged(int arg0) {
	}

	// 当当前页面被滑动时调用
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	// 当新的页面被选中时调用
	public void onPageSelected(int position) {// 设置底部小点选中状态
		/*dots.get(oldPosition).setBackgroundResource(R.drawable.yuan_color);
		dots.get(position).setBackgroundResource(R.drawable.yuan_color_selected);*/

		oldPosition = position;
	}

}
