/* 
 * Copyright ©，2008-2014，HuiShang Tech.Co.,Ltd.
 * 作者：hbzhang
 * 版本：1.0
 * 日期：2014/7/30
 * 说明：渠道-客户列表，客户管理，主要分：我的客户列表、部门客户列表、全部客户列表、新增客户、查询客户、客户分组浏览
 * 备注：无
 */
package com.huishangyun.Channel.Customers;

import java.util.ArrayList;
import java.util.List;

import com.huishangyun.Util.L;
import com.huishangyun.model.Constant;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Adapter.MyFragmentPagerAdapter;
import com.huishangyun.yun.R;

import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.LinearLayout.LayoutParams;

/**
 * 渠道-客户列表
 * <br><br>
 * 客户管理，主要分：我的客户列表、部门客户列表、全部客户列表、新增客户、查询客户、客户分组浏览
 * 
 * @author hbzhang
 * @version V1.0 2014/7/30
 * @see无
 * @since 亿企云app/渠道
 * 
 */
public class CustomerMainActivity extends BaseActivity {
	private static final String TAG = "CustomerActivity.java";
	private static final boolean isDebug = false;
	private ViewPager mViewPager;
	private LinearLayout search_customer;//搜索客户
	private LinearLayout create_customer;//创建客户
	private LinearLayout more_option;//更多
	private Button allButton;//全部客户
	private Button launchbButton;//我的客户
	private Button acceptedButton;//部门客户
	private AllFragment allFragment;
	private LaunchbFragment launchbFragment;
	private AcceptedFragment acceptedFragment;
	private List<Fragment> fragments;
	private ImageView lineImg;// 底部线条
	private int bmpW;// 图片资源
	private int offset;// 偏移量
	private int currIndex = 0;
	private LayoutInflater mInflater;
	private DisplayMetrics dm;//获取屏幕尺寸
	private PopupWindow mWindow;//菜单选项
	private LinearLayout backBtn;// 返回按钮
	private LinearLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_channel_customer);
		inirView();
		initViewPager();
		initPoptWindow();
		initListener();
	}
	
	/**
	 * 实例化组件
	 */
	private void inirView() {
		backBtn = (LinearLayout) findViewById(R.id.customdialog_back);
		search_customer = (LinearLayout) findViewById(R.id.search_customer);
		create_customer = (LinearLayout) findViewById(R.id.create_customer);
		more_option = (LinearLayout) findViewById(R.id.more_option);
		allButton = (Button) findViewById(R.id.task_all);
		launchbButton = (Button) findViewById(R.id.task_launch);
		acceptedButton = (Button) findViewById(R.id.task_accepted);
		lineImg = (ImageView) findViewById(R.id.task_line);
		mViewPager = (ViewPager) findViewById(R.id.customer_main_viewpager);
		mInflater = LayoutInflater.from(CustomerMainActivity.this);
		layout = (LinearLayout) findViewById(R.id.task_scroll_layout);
		if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
			layout.setVisibility(View.GONE);
			allButton.setText("全部");
			launchbButton.setText("");
			acceptedButton.setText("");
		} else {
			layout.setVisibility(View.VISIBLE);
			allButton.setText("全部");
			launchbButton.setText("我的客户");
			acceptedButton.setText("部门客户");
		}
	}
	
	
	/**
	 * 设置ViewPager
	 */
	private void initViewPager() {
		bmpW = lineImg.getWidth();// 获取图片宽度
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		fragments = new ArrayList<Fragment>();
		allFragment = new AllFragment();
		launchbFragment = new LaunchbFragment();
	    acceptedFragment = new AcceptedFragment();
		if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
			fragments.add(allFragment);
		} else {
			fragments.add(allFragment);
			fragments.add(launchbFragment);
			fragments.add(acceptedFragment);
		}
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
		mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments));
		mViewPager.setCurrentItem(0);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mViewPager.setOffscreenPageLimit(3);
		allButton.setTextColor(0xff00658f);
		launchbButton.setTextColor(0xff646464);
		acceptedButton.setTextColor(0xff646464);
	}
	
	
	/**
	 * 添加点击时间监听
	 */
	private void initListener() {
		backBtn.setOnClickListener(mListener);
		create_customer.setOnClickListener(mListener);
		more_option.setOnClickListener(mListener);
		search_customer.setOnClickListener(mListener);
		allButton.setOnClickListener(mListener);
		acceptedButton.setOnClickListener(mListener);
		launchbButton.setOnClickListener(mListener);
	}
	
	
	/**
	 * 创建PopuptWindow对象
	 */
	private void initPoptWindow(){
		//加载布局
		View mPopView = mInflater.inflate(R.layout.custormer_popwindow, null);
		mWindow = new PopupWindow(mPopView);
		
		//设置获取焦点
		mWindow.setFocusable(true); 
		
		//防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
		mWindow.setBackgroundDrawable(new PaintDrawable()); 
		
		//设置点击窗口外边窗口消失 
		mWindow.setOutsideTouchable(true); 		
		mWindow.update();
		
		 // 设置弹出窗体的宽
		mWindow.setWidth(dm.widthPixels / 2);
		
        // 设置弹出窗体的高
		mWindow.setHeight(LayoutParams.WRAP_CONTENT);
		
		//获取控件对象
		LinearLayout groupBtn = (LinearLayout) mPopView.findViewById(R.id.custormer_pop_group);
		LinearLayout ratingBtn = (LinearLayout) mPopView.findViewById(R.id.custormer_pop_rating);
		LinearLayout typeBtn = (LinearLayout) mPopView.findViewById(R.id.custormer_pop_type);
		
		//设置点击时间监听
		groupBtn.setOnClickListener(mListener);
		ratingBtn.setOnClickListener(mListener);
		typeBtn.setOnClickListener(mListener);
		
	}
	
	
	/**
	 * 点击时间监听
	 */
	private OnClickListener mListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent mIntent = null;
			switch (v.getId()) {
			case R.id.customdialog_back://返回按钮
				finish();
				break;
			case R.id.create_customer://添加按钮
				mIntent = new Intent(CustomerMainActivity.this, CustomerSet.class);
				mIntent.putExtra("flage", "chuangjian");
				startActivityForResult(mIntent, 0);/*requestCode*/
				mIntent = null;
				break;
			case R.id.more_option://更多
				L.e("点击更多按钮");
				mWindow.showAsDropDown(more_option);//弹出菜单选项
				
				break;
			case R.id.search_customer://搜索
				mIntent = new Intent(CustomerMainActivity.this, CustomerSearchActivity.class);
				break;
			case R.id.task_all://全部
				mViewPager.setCurrentItem(0);
				currIndex = 0;
				break;
			case R.id.task_launch://我的
				mViewPager.setCurrentItem(1);
				currIndex = 1;
				break;
			case R.id.task_accepted://部门的
				mViewPager.setCurrentItem(2);
				currIndex = 2;
				break;
				
			case R.id.custormer_pop_type://状态
				L.e("点击了状态");
				mWindow.dismiss();
				mIntent = new Intent(CustomerMainActivity.this, CustomerMenuActivity.class);
				mIntent.putExtra("Type", 5);
				break;
				
			case R.id.custormer_pop_group://时间
				L.e("点击了时间");
				mIntent = new Intent(CustomerMainActivity.this, CustomerMenuActivity.class);
				mIntent.putExtra("Type", 3);
				mWindow.dismiss();
				break;
				
			case R.id.custormer_pop_rating://优先级
				L.e("点击了优先级");
				mIntent = new Intent(CustomerMainActivity.this, CustomerMenuActivity.class);
				mIntent.putExtra("Type", 4);
				mWindow.dismiss();
				break;

			default://其他
				break;
			}
			if (mIntent != null) {
				startActivity(mIntent);
			}
		}
	};
	
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
			allButton.setTextColor(0xff00658f);
			launchbButton.setTextColor(0xff646464);
			acceptedButton.setTextColor(0xff646464);
			break;
		case 1:
			if (currIndex == 0) {
				animation = new TranslateAnimation(offset, one, 0, 0);
			} else if (currIndex == 2) {
				animation = new TranslateAnimation(two, one, 0, 0);
			}
			
			allButton.setTextColor(0xff646464);
			launchbButton.setTextColor(0xff00658f);
			acceptedButton.setTextColor(0xff646464);
			break;
		case 2:
			if (currIndex == 0) {
				animation = new TranslateAnimation(offset, two, 0, 0);
			} else if (currIndex == 1) {
				animation = new TranslateAnimation(one, two, 0, 0);
			}
			allButton.setTextColor(0xff646464);
			launchbButton.setTextColor(0xff646464);
			acceptedButton.setTextColor(0xff00658f);
			break;

		default:
			break;
		}
		currIndex = arg0;
		animation.setFillAfter(true);// True:图片停在动画结束位置
		animation.setDuration(300);
		lineImg.startAnimation(animation);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		L.e("resultCode = " + resultCode);
		if (resultCode == RESULT_OK) {
			L.e("-------------------------------刷新列表");
			allFragment.startRefresh();
			acceptedFragment.startRefresh();
			launchbFragment.startRefresh();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
