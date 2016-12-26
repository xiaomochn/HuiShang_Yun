package com.huishangyun.Office.Attendance;

import java.util.ArrayList;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.yun.R;

/**
 * 考勤界面（我的考勤和部门考勤）
 * @author xsl
 *
 */
public class MyAttendance extends BaseActivity {
	private static final String TAG = null;
	private RelativeLayout back;//返回
	private TextView myattendance;//我的考勤
	private TextView departmentattendance;//部门考勤
	private ImageView image;//位移偏量线
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentList;
	private TextView view1, view2, view3, view4;
	private int currIndex;//当前页卡编号
	private int bmpW;//横线图片宽度
	private int offset;//图片移动的偏移量
	private MyAttendanceFistFragment fragment1;
	private MyAttendanceSecondFragment fragment2;
	public int Company_ID;//公司id
	public int Manager_ID;//登录人id
	private String MangerName;//登入人名称
	private String UseName;//登入人帐号
	private String PassWords;//登入人密码
	private LinearLayout bar;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_office_attendance_my);
		init();
		InitImage();
		InitViewPager();
	}
	
	/**
	 * 初始化控件
	 */
	private void init(){
		UseName = preferences.getString(Constant.USERNAME, "");
		PassWords = preferences.getString(Constant.PASSWORD, "");
		Company_ID = preferences.getInt(Content.COMPS_ID, 0);
		Manager_ID = Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0"));
		MangerName = preferences.getString(Constant.XMPP_MY_REAlNAME,"");
		getIntent().putExtra("UseName", UseName);
		getIntent().putExtra("PassWords", PassWords);
		getIntent().putExtra("Company_ID", Company_ID);
		getIntent().putExtra("Manager_ID", Manager_ID);
		getIntent().putExtra("MangerName", MangerName);
		back = (RelativeLayout) findViewById(R.id.back);
		myattendance = (TextView) findViewById(R.id.myattendance);
		departmentattendance = (TextView) findViewById(R.id.departmentattendance);
		bar = (LinearLayout) findViewById(R.id.bar);
		if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
			bar.setVisibility(View.GONE);
		}else{
			bar.setVisibility(View.VISIBLE);
		}
		
		back.setOnClickListener(new ButtonClickListener());
		myattendance.setOnClickListener(new ButtonClickListener());
		departmentattendance.setOnClickListener(new ButtonClickListener());
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
            case R.id.myattendance://我的考勤
            	mPager.setCurrentItem(0);
            	myattendance.setTextColor(Color.parseColor("#21a5de"));
            	departmentattendance.setTextColor(Color.parseColor("#646464"));
            	
				break;
			case R.id.departmentattendance://部门考勤
				mPager.setCurrentItem(1);
				myattendance.setTextColor(Color.parseColor("#646464"));
            	departmentattendance.setTextColor(Color.parseColor("#21a5de"));
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
		if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
			image.setVisibility(View.GONE);
		}else {
			image.setVisibility(View.VISIBLE);
		}
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.myattendance_line).getWidth();
		image.setBackgroundColor(0xff21a5de);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		Log.e(TAG, "====>" + screenW);
		offset = (screenW - 2*bmpW)/4;
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
		if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
			fragment1 = new MyAttendanceFistFragment();
			fragmentList.add(fragment1);
		}else {
			fragment1 = new MyAttendanceFistFragment();
			fragment2 = new MyAttendanceSecondFragment();
			fragmentList.add(fragment1);
			fragmentList.add(fragment2);
		}
		
		
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
				myattendance.setTextColor(Color.parseColor("#21a5de"));
            	departmentattendance.setTextColor(Color.parseColor("#646464"));
			}else if (arg0==1) {
				myattendance.setTextColor(Color.parseColor("#646464"));
            	departmentattendance.setTextColor(Color.parseColor("#21a5de"));
			}
		}
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



}
