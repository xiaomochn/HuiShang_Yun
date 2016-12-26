package com.huishangyun.Fragment;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.huishangyun.Adapter.MyFragmentPagerAdapter;
import com.huishangyun.Util.L;
import com.huishangyun.yun.R;

public class ContactFragment extends BaseFragment implements OnClickListener {
	
	private ViewPager mViewPager;// ViewPager对象
	private View mView;
	private CollFragment collFragment;
	private GroupFragment groupFragment;
	private CustFragment custFragment;
	private List<Fragment> mList;
	private Button collBtn;// 同事按钮
	private Button groupBtn;// 群组按钮
	private Button custBtn;// 客户列表
	private ImageView lineImg;
	private int bmpW;// 图片资源
	private int offset;// 偏移量
	private int currIndex = 0;
	private DisplayMetrics dm;//获取屏幕尺寸
	
	/** 当前列表所属的组ID */
	private int m_iCurrentDepartment_ID = 0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.fragment_contact, container,false);
		initview(mView);
		initViewPager();
		return mView;
	}
	/**
	 * 实例化各组件
	 * @param view
	 */
	private void initview(View view) {
		collBtn = (Button) mView.findViewById(R.id.contact_tongshi);
		groupBtn = (Button) mView.findViewById(R.id.contact_qun);
		custBtn = (Button) mView.findViewById(R.id.contact_kehu);
		lineImg = (ImageView) mView.findViewById(R.id.contact_line);
		mViewPager = (ViewPager) mView.findViewById(R.id.viewpager_contact);
	}
	
	/**
	 * 设置ViewPager
	 */
	private void initViewPager() {
		bmpW = lineImg.getWidth();// 获取图片宽度
		dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		mList = new ArrayList<Fragment>();
		collFragment = new CollFragment();
		groupFragment = new GroupFragment();
		custFragment = new CustFragment();
		mList.add(collFragment);
		mList.add(groupFragment);
		mList.add(custFragment);
		// 设置图片宽度
		FrameLayout.LayoutParams mParams = (FrameLayout.LayoutParams) lineImg
				.getLayoutParams();
		mParams.width = dm.widthPixels / 3;
		lineImg.setLayoutParams(mParams);
		lineImg.setBackgroundColor(0xff00658f);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		lineImg.setImageMatrix(matrix);// 设置动画初始位置
		
		// 添加Adapter
		mViewPager.setAdapter(new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager(), mList));
		mViewPager.setCurrentItem(0);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mViewPager.setOffscreenPageLimit(3);
		collBtn.setTextColor(0xff00658f);
		groupBtn.setTextColor(0xff646464);
		custBtn.setTextColor(0xff646464);
		collBtn.setOnClickListener(this);
		groupBtn.setOnClickListener(this);
		custBtn.setOnClickListener(this);
	}
	
	
	
	/**
	 * 页卡变动监听
	 * 
	 * @author pan
	 * 
	 */
	private class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			setTopView(arg0);
			L.d("arg0 == " + arg0);
		}

	}
	
	
	/**
	 * 设置顶部偏移
	 * 
	 * @param arg0
	 */
	private void setTopView(int arg0) {
		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量
		Animation animation = null;
		switch (arg0) {
		case 0:
			if (currIndex == 1) {//判断当前选中项
				animation = new TranslateAnimation(one, 0, 0, 0);
			} else if (currIndex == 2) {
				animation = new TranslateAnimation(two, 0, 0, 0);

			}
			collBtn.setTextColor(0xff00658f);
			groupBtn.setTextColor(0xff646464);
			custBtn.setTextColor(0xff646464);
			break;
		case 1:
			if (currIndex == 0) {
				animation = new TranslateAnimation(offset, one, 0, 0);
			} else if (currIndex == 2) {
				animation = new TranslateAnimation(two, one, 0, 0);
			}
			
			collBtn.setTextColor(0xff646464);
			groupBtn.setTextColor(0xff00658f);
			custBtn.setTextColor(0xff646464);
			break;
		case 2:
			if (currIndex == 0) {
				animation = new TranslateAnimation(offset, two, 0, 0);
			} else if (currIndex == 1) {
				animation = new TranslateAnimation(one, two, 0, 0);
			}
			collBtn.setTextColor(0xff646464);
			groupBtn.setTextColor(0xff646464);
			custBtn.setTextColor(0xff00658f);
			break;

		default:
			break;
		}
		currIndex = arg0;
		animation.setFillAfter(true);// True:图片停在动画结束位置
		animation.setDuration(300);
		lineImg.startAnimation(animation);
	}
	
	
	/**
	 * 返回键调用方法
	 * @return
	 */
	public boolean onBackPressed(){
		switch (currIndex) {
		case 0:
			if (!collFragment.onBackPressed()) {
				return false;
			}
			break;
		case 1:
			
			break;
		case 2:
			if (!custFragment.onBackPressed()) {
				return false;
			}
			break;

		default:
			break;
		}
		return true;
		
	} 
	
	public void updataInfo() {
		
		if (collFragment != null) {
			collFragment.getContacts(false, 0);
		}
		
		if (custFragment != null) {
			custFragment.getContacts(false, 0);
		}
		
		if (groupFragment != null) {
			groupFragment.getGroup();
		}
		
	}
	


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.contact_tongshi:
			mViewPager.setCurrentItem(0);
			break;
		case R.id.contact_qun:
			mViewPager.setCurrentItem(1);
			break;
		case R.id.contact_kehu:
			mViewPager.setCurrentItem(2);
			break;

		default:
			break;
		}
	}
	
	
}
