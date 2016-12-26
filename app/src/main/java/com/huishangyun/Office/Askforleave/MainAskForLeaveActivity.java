package com.huishangyun.Office.Askforleave;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.yun.R;

public class MainAskForLeaveActivity extends BaseActivity {
	private static final String TAG = null;
	private RelativeLayout back;//返回
	private RelativeLayout add;//新增请假
	private TextView allaskforleave;//全部请假
	private TextView myaskforleave;//我的请假
	private TextView departmentaskforleave;//部门请假
	private ImageView image;//位移偏量线
	private ViewPager mPager;

	private int currIndex;//当前页卡编号
	private int bmpW;//横线图片宽度
	private int offset;//图片移动的偏移量
	private AllAskForLeaveFragment fragment1;
	private MyAskForLeaveFragment fragment2;
	private DepartmentAskForLeaveFragment fragment3;
	private ArrayList<Fragment> fragmentList;
	public int Company_ID;//公司id
	public int Manager_ID;//登录人id
	public int Department_ID;//部门id
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_office_askforleave_main);
		init();
		InitImage();
		InitViewPager();
	}

	/**
	 * 初始化控件
	 */
	private void init(){
		//注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("ASKFORLEAVE_LIST_REFRESH");
		intentFilter.setPriority(Integer.MAX_VALUE);
		this.registerReceiver(broadcastReceiver, intentFilter);
		
		Company_ID = preferences.getInt(Content.COMPS_ID, 1016);
		Manager_ID = Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0"));
		Department_ID = preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID,0);
		getIntent().putExtra("Company_ID", Company_ID);
		getIntent().putExtra("Manager_ID", Manager_ID);
		getIntent().putExtra("Department_ID", Department_ID);
		back = (RelativeLayout) findViewById(R.id.back);
		add = (RelativeLayout) findViewById(R.id.add);
		allaskforleave = (TextView) findViewById(R.id.allaskforleave);
		myaskforleave = (TextView) findViewById(R.id.myaskforleave);
		departmentaskforleave = (TextView) findViewById(R.id.departmentaskforleave);
	
		back.setOnClickListener(new ButtonClickListener());
		add.setOnClickListener(new ButtonClickListener());
		allaskforleave.setOnClickListener(new ButtonClickListener());
		myaskforleave.setOnClickListener(new ButtonClickListener());
		departmentaskforleave.setOnClickListener(new ButtonClickListener());
		
	}
	
	/**
	 * 广播接收器
	 */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			int  mflag = intent.getIntExtra("mflag", -1);
			if (mflag==2) {
				mPager.setCurrentItem(0);
				InitViewPager();
			}
			
		}
	};
	
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(broadcastReceiver);
	};
	
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
			case R.id.add://新增请假
				Intent intent = new Intent(MainAskForLeaveActivity.this,AddAskForLeaveActivivty.class);
				startActivity(intent);
				break;
            case R.id.allaskforleave://全部请假
            	mPager.setCurrentItem(0);
            	allaskforleave.setTextColor(Color.parseColor("#00658f"));
            	myaskforleave.setTextColor(Color.parseColor("#646464"));
            	departmentaskforleave.setTextColor(Color.parseColor("#646464"));

				break;
			case R.id.myaskforleave://我的请假
				mPager.setCurrentItem(1);
				allaskforleave.setTextColor(Color.parseColor("#646464"));
            	myaskforleave.setTextColor(Color.parseColor("#00658f"));
            	departmentaskforleave.setTextColor(Color.parseColor("#646464"));
				break;
			case R.id.departmentaskforleave://部门请假
				mPager.setCurrentItem(2);
				allaskforleave.setTextColor(Color.parseColor("#646464"));
            	myaskforleave.setTextColor(Color.parseColor("#646464"));
            	departmentaskforleave.setTextColor(Color.parseColor("#00658f"));
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
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.askforleave_line).getWidth();
		image.setBackgroundColor(0xff00658f);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		Log.e(TAG, "====>" + screenW);
		offset = (screenW/3 - bmpW)/2;
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
		fragment1 = new AllAskForLeaveFragment();
		fragment2 = new MyAskForLeaveFragment();
		fragment3 = new DepartmentAskForLeaveFragment();
	
		fragmentList.add(fragment1);
		fragmentList.add(fragment2);
		fragmentList.add(fragment3);
		
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
				allaskforleave.setTextColor(Color.parseColor("#00658f"));
            	myaskforleave.setTextColor(Color.parseColor("#646464"));
            	departmentaskforleave.setTextColor(Color.parseColor("#646464"));
			}else if (arg0==1) {
				allaskforleave.setTextColor(Color.parseColor("#646464"));
            	myaskforleave.setTextColor(Color.parseColor("#00658f"));
            	departmentaskforleave.setTextColor(Color.parseColor("#646464"));
			}else if (arg0==2) {
				allaskforleave.setTextColor(Color.parseColor("#646464"));
            	myaskforleave.setTextColor(Color.parseColor("#646464"));
            	departmentaskforleave.setTextColor(Color.parseColor("#00658f"));
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
