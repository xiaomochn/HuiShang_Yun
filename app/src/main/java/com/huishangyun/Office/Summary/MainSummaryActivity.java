package com.huishangyun.Office.Summary;


import java.util.ArrayList;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.model.Constant;
import com.huishangyun.yun.R;

public class MainSummaryActivity extends BaseActivity {
	private static final String TAG = null;
	private RelativeLayout back;//返回
	private RelativeLayout add;//小结上报
	private TextView allsummary;//全部小结
	private TextView mysummary;//我的小结
	private TextView departmentsummary;//部门小结
	private TextView searchsummary;//小结查询
	private ImageView image;//位移偏量线
	private ViewPager mPager;

	private int currIndex;//当前页卡编号
	private int bmpW;//横线图片宽度
	private int offset;//图片移动的偏移量
	private AllSummaryFragment fragment1;
	private MySummaryFragment fragment2;
	private DepartmentSummaryFragment fragment3;
	private SearchSummaryFragment fragment4;
	private ArrayList<Fragment> fragmentList;
	public int Company_ID;//公司id
	public int Manager_ID;//登录人id
	public int Department_ID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_office_summary_main);
		init();
		InitImage();
		InitViewPager();
	}
	
	/**
	 * 初始化控件
	 */
	private void init(){
		Company_ID = MyApplication.getInstance().getCompanyID();
		Manager_ID = MyApplication.getInstance().getManagerID();
		Department_ID = preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID,0);
		getIntent().putExtra("Company_ID", Company_ID);
		getIntent().putExtra("Manager_ID", Manager_ID);
		getIntent().putExtra("Department_ID", Department_ID);
		back = (RelativeLayout) findViewById(R.id.back);
		add = (RelativeLayout) findViewById(R.id.add);
		allsummary = (TextView) findViewById(R.id.allsummary);
		mysummary = (TextView) findViewById(R.id.mysummary);
		departmentsummary = (TextView) findViewById(R.id.departmentsummary);
		searchsummary = (TextView) findViewById(R.id.searchsummary);
		
		
		back.setOnClickListener(new ButtonClickListener());
		add.setOnClickListener(new ButtonClickListener());
		allsummary.setOnClickListener(new ButtonClickListener());
		mysummary.setOnClickListener(new ButtonClickListener());
		departmentsummary.setOnClickListener(new ButtonClickListener());
		searchsummary.setOnClickListener(new ButtonClickListener());
		
	}
	

	
	/**
	 * 单击事件处理
	 * @author xsl
	 *
	 */
	private class ButtonClickListener implements View.OnClickListener{

		
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back://返回
				finish();
				break;
			case R.id.add://小结上报
				Intent intent = new Intent(getApplication(),SummaryReport.class);
				startActivity(intent);
				break;
            case R.id.allsummary://全部xiaojie
            	mPager.setCurrentItem(0);
            	allsummary.setTextColor(Color.parseColor("#00658f"));
            	mysummary.setTextColor(Color.parseColor("#646464"));
            	departmentsummary.setTextColor(Color.parseColor("#646464"));
            	searchsummary.setTextColor(Color.parseColor("#646464"));
      
				break;
			case R.id.mysummary://我的小结
				mPager.setCurrentItem(1);
				allsummary.setTextColor(Color.parseColor("#646464"));
            	mysummary.setTextColor(Color.parseColor("#00658f"));
            	departmentsummary.setTextColor(Color.parseColor("#646464"));
            	searchsummary.setTextColor(Color.parseColor("#646464"));
				break;
			case R.id.departmentsummary://部门小结
				mPager.setCurrentItem(2);
				allsummary.setTextColor(Color.parseColor("#646464"));
            	mysummary.setTextColor(Color.parseColor("#646464"));
            	departmentsummary.setTextColor(Color.parseColor("#00658f"));
            	searchsummary.setTextColor(Color.parseColor("#646464"));
				break;		
			case R.id.searchsummary://小结查询
				mPager.setCurrentItem(3);
				allsummary.setTextColor(Color.parseColor("#646464"));
            	mysummary.setTextColor(Color.parseColor("#646464"));
            	departmentsummary.setTextColor(Color.parseColor("#646464"));
            	searchsummary.setTextColor(Color.parseColor("#00658f"));
				break;
          
			default:
				break;
			}
			
		}
		
	}
	
	/**
	 * 初始化图片的位移像素
	 */
	public void InitImage(){
		image = (ImageView)findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.myattendance_line).getWidth();
		image.setBackgroundColor(0xff00658f);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		Log.e(TAG, "====>" + screenW);
		offset = (screenW/4 - bmpW)/2;
		//imgageview设置平移，使下划线平移到初始位置（平移一个offset）
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		image.setImageMatrix(matrix);
	}
	
	/**
	 * 初始化ViewPager
	 */
	public void InitViewPager(){
		mPager = (ViewPager)findViewById(R.id.viewpager);
		fragmentList = new ArrayList<Fragment>();
		fragment1 = new AllSummaryFragment();
		fragment2 = new MySummaryFragment();
		fragment3 = new DepartmentSummaryFragment();
		fragment4 = new SearchSummaryFragment();
		
		fragmentList.add(fragment1);
		fragmentList.add(fragment2);
		fragmentList.add(fragment3);
		fragmentList.add(fragment4);
		
		//给ViewPager设置适配器
		mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
		mPager.setCurrentItem(0);//设置当前显示标签页为第一页
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器
		
		
	}

	

     /**
      * viewpager滑动选项卡监听
      * @author xsl
      *
      */
	public class MyOnPageChangeListener implements OnPageChangeListener{
		private int one = offset *2 +bmpW;//两个相邻页面的偏移量
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			Animation animation = new TranslateAnimation(currIndex*one,arg0*one,0,0);//平移动画
			currIndex = arg0;
			animation.setFillAfter(true);//动画终止时停留在最后一帧，不然会回到没有执行前的状态
			animation.setDuration(200);//动画持续时间0.2秒
			image.startAnimation(animation);//是用ImageView来显示动画的
			int i = currIndex + 1;
//			Toast.makeText(MyAttendance.this, "您选择了第"+i+"个页卡", Toast.LENGTH_SHORT).show();
			if (arg0==0) {
				allsummary.setTextColor(Color.parseColor("#00658f"));
            	mysummary.setTextColor(Color.parseColor("#646464"));
            	departmentsummary.setTextColor(Color.parseColor("#646464"));
            	searchsummary.setTextColor(Color.parseColor("#646464"));
			}else if (arg0==1) {
				allsummary.setTextColor(Color.parseColor("#646464"));
            	mysummary.setTextColor(Color.parseColor("#00658f"));
            	departmentsummary.setTextColor(Color.parseColor("#646464"));
            	searchsummary.setTextColor(Color.parseColor("#646464"));
			}else if (arg0==2) {
				allsummary.setTextColor(Color.parseColor("#646464"));
            	mysummary.setTextColor(Color.parseColor("#646464"));
            	departmentsummary.setTextColor(Color.parseColor("#00658f"));
            	searchsummary.setTextColor(Color.parseColor("#646464"));
			}else if (arg0==3) {
				allsummary.setTextColor(Color.parseColor("#646464"));
            	mysummary.setTextColor(Color.parseColor("#646464"));
            	departmentsummary.setTextColor(Color.parseColor("#646464"));
            	searchsummary.setTextColor(Color.parseColor("#00658f"));
				
			}
		}
		
//		public Object instantiateItem(View arg0, int arg1) {
//			if (mListViews.get(arg1).list != null && !mListViews.get(arg1).flag) {
//				mListViews.get(arg1).flag = true;
//				if (arg1 == 0)
//					handler.sendEmptyMessage(1);
//				else {
//					Message msg = handler.obtainMessage();
//					msg.what = 3;
//					msg.arg2 = arg1;
//					msg.sendToTarget();
//				}
//			}
//			((ViewPager) arg0).addView(mListViews.get(arg1).view, 0);
//			return mListViews.get(arg1).view;
//		}

	}
	
	/**
	 * Fragment适配器
	 * @author xsl
	 *
	 */
	public class MyFragmentPagerAdapter extends FragmentPagerAdapter{
		ArrayList<Fragment> list;  
		public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
			super(fm);
			 this.list = list; 
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}
		
	}
	
	
//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//		mPager.setCurrentItem(0);
//		
//	}

}
