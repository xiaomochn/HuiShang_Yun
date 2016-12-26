package com.huishangyun.Office.Summary;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huishangyun.Util.L;
import com.huishangyun.model.Constant;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.yun.R;

public class SummaryDetailActivity extends BaseActivity {
	private static final String TAG = null;
	private RelativeLayout back;// 返回
	private TextView week1;// 周
	private TextView week2;
	private TextView week3;
	private TextView week4;
	private TextView week5;
	private TextView week6;
	private TextView week7;
	private TextView date1;// 日期
	private TextView date2;
	private TextView date3;
	private TextView date4;
	private TextView date5;
	private TextView date6;
	private TextView date7;
	private LinearLayout date1_bg;//日期背景
	private LinearLayout date2_bg;
	private LinearLayout date3_bg;
	private LinearLayout date4_bg;
	private LinearLayout date5_bg;
	private LinearLayout date6_bg;
	private LinearLayout date7_bg;
	private MyXListView slistview;//listview
	private Drawable drawable;
	private Calendar cal = Calendar.getInstance(); 
	private int getdate;//传递进来的日期
	
	private ImageView image;//位移偏量线
	private ViewPager mPager;

	private int currIndex;//当前页卡编号
	private int bmpW;//横线图片宽度
	private int offset;//图片移动的偏移量
	private SummaryDetailFistDateFragment fragment1;
	private SummaryDetailSecondDateFragment fragment2;
	private SummaryDetailThridDateFragment fragment3;
	private SummaryDetailFourthDateFragment fragment4;
	private SummaryDetailFifthDateFragment fragment5;
	private SummaryDetailSixthDateFragmen fragment6;
	private SummaryDetailSeventhDateFragmen fragment7;
	private ArrayList<Fragment> fragmentList;
	public int Company_ID;//公司id
	public int Manager_ID;//登录人id
	public int Department_ID;//部门id
	private int ID;
	private  String belongdate;
	private String curentdate;//当前页面日期
	private int index = 0;
	private int fistCurrentItem;
	private String mTpye;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_office_summary_detail);
		Intent intent = getIntent();
		ID = intent.getIntExtra("ID", 0);
		belongdate = intent.getStringExtra("BelongDate");
		Manager_ID = intent.getIntExtra("Manager_ID", 0);
		curentdate = belongdate;

		init();
		InitImage();
		InitViewPager();
		
	}

	/**
	 * 界面初始化
	 */
	private void init() {
		mTpye = preferences.getString(Constant.HUISHANG_TYPE, "0");
		Company_ID = MyApplication.getInstance().getCompanyID();
		Department_ID = preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID,0);
		getIntent().putExtra("Company_ID", Company_ID);
		getIntent().putExtra("Department_ID", Department_ID);
		getIntent().putExtra("Manager_ID", Manager_ID);
		getIntent().putExtra("ID", ID);
		getIntent().putExtra("mTpye", mTpye);
		back = (RelativeLayout) findViewById(R.id.back);
		week1 = (TextView) findViewById(R.id.week1);
		week2 = (TextView) findViewById(R.id.week2);
		week3 = (TextView) findViewById(R.id.week3);
		week4 = (TextView) findViewById(R.id.week4);
		week5 = (TextView) findViewById(R.id.week5);
		week6 = (TextView) findViewById(R.id.week6);
		week7 = (TextView) findViewById(R.id.week7);
		date1 = (TextView) findViewById(R.id.date1);
		date2 = (TextView) findViewById(R.id.date2);
		date3 = (TextView) findViewById(R.id.date3);
		date4 = (TextView) findViewById(R.id.date4);
		date5 = (TextView) findViewById(R.id.date5);
		date6 = (TextView) findViewById(R.id.date6);
		date7 = (TextView) findViewById(R.id.date7);
		date1_bg = (LinearLayout) findViewById(R.id.date1_bg);
		date2_bg = (LinearLayout) findViewById(R.id.date2_bg);
		date3_bg = (LinearLayout) findViewById(R.id.date3_bg);
		date4_bg = (LinearLayout) findViewById(R.id.date4_bg);
		date5_bg = (LinearLayout) findViewById(R.id.date5_bg);
		date6_bg = (LinearLayout) findViewById(R.id.date6_bg);
		date7_bg = (LinearLayout) findViewById(R.id.date7_bg);
		slistview = (MyXListView) findViewById(R.id.mlistview);
		
		
		
		/**********可以不要************/
		drawable= getResources().getDrawable(R.drawable.summarydetail_dot); 
		// 这一步必须要做,否则不会显示.  
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  
		/************可以不要**************/
		back.setOnClickListener(buttOnClickListener);
		date1.setOnClickListener(buttOnClickListener);
		date2.setOnClickListener(buttOnClickListener);
		date3.setOnClickListener(buttOnClickListener);
		date4.setOnClickListener(buttOnClickListener);
		date5.setOnClickListener(buttOnClickListener);
		date6.setOnClickListener(buttOnClickListener);
		date7.setOnClickListener(buttOnClickListener);
		setdate();
		

	}

	
	/**
	 * 单击事件处理
	 */
	private OnClickListener buttOnClickListener = new OnClickListener() {
		

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.back://返回
				finish();
				break;
			case R.id.date1://第一个日期
				setDrawables();
				if (date1.getCurrentTextColor() != 0xffe60000) {
					date1.setTextColor(0xffffffff);
				}
				date1.setCompoundDrawables(null, null, null, drawable);
				date1_bg.setBackgroundColor(0xff05cc76);
				mPager.setCurrentItem(0);
				break;
			case R.id.date2:
				setDrawables();
				if (date2.getCurrentTextColor() != 0xffe60000) {
					date2.setTextColor(0xffffffff);
				}
				date2.setCompoundDrawables(null, null, null, drawable);
				date2_bg.setBackgroundColor(0xff05cc76);
				mPager.setCurrentItem(1);
				break;
			case R.id.date3:
				setDrawables();
				if (date3.getCurrentTextColor() != 0xffe60000) {
					date3.setTextColor(0xffffffff);
				}
				date3.setCompoundDrawables(null, null, null, drawable);
				date3_bg.setBackgroundColor(0xff05cc76);
				mPager.setCurrentItem(2);
				break;
			case R.id.date4:
				setDrawables();
				if (date4.getCurrentTextColor() != 0xffe60000) {
					date4.setTextColor(0xffffffff);
				}
				date4.setCompoundDrawables(null, null, null, drawable);
				date4_bg.setBackgroundColor(0xff05cc76);
				mPager.setCurrentItem(3);
				break;
			case R.id.date5:
				setDrawables();
				if (date5.getCurrentTextColor() != 0xffe60000) {
					date5.setTextColor(0xffffffff);
				}
				date5.setCompoundDrawables(null, null, null, drawable);
				date5_bg.setBackgroundColor(0xff05cc76);
				mPager.setCurrentItem(4);
				break;
			case R.id.date6:
				setDrawables();
				if (date6.getCurrentTextColor() != 0xffe60000) {
					date6.setTextColor(0xffffffff);
				}
				date6.setCompoundDrawables(null, null, null, drawable);
				date6_bg.setBackgroundColor(0xff05cc76);
				mPager.setCurrentItem(5);
				break;
			case R.id.date7:
				setDrawables();
				if (date7.getCurrentTextColor() != 0xffe60000) {
					date7.setTextColor(0xffffffff);
				}
				date7.setCompoundDrawables(null, null, null, drawable);
				date7_bg.setBackgroundColor(0xff05cc76);
				mPager.setCurrentItem(6);
				break;
			default:
				break;
			}

		}
	};
	
	/**
	 * 清空显示
	 */
	private void setDrawables(){
		/*****************可以不要*************************/
		date1.setCompoundDrawables(null, null, null, drawable);
		date2.setCompoundDrawables(null, null, null, drawable);
		date3.setCompoundDrawables(null, null, null, drawable);
		date4.setCompoundDrawables(null, null, null, drawable);
		date5.setCompoundDrawables(null, null, null, drawable);
		date6.setCompoundDrawables(null, null, null, drawable);
		date7.setCompoundDrawables(null, null, null, drawable);
		/*****************可以不要*************************/
		
		if (date1.getCurrentTextColor() != 0xffe60000) {
			date1.setTextColor(0xff646464);
		}
		if (date2.getCurrentTextColor() != 0xffe60000) {
			date2.setTextColor(0xff646464);
		}
		if (date3.getCurrentTextColor() != 0xffe60000) {
			date3.setTextColor(0xff646464);
		}
		if (date4.getCurrentTextColor() != 0xffe60000) {
			date4.setTextColor(0xff646464);
		}
		if (date5.getCurrentTextColor() != 0xffe60000) {
			date5.setTextColor(0xff646464);
		}
		if (date6.getCurrentTextColor() != 0xffe60000) {
			date6.setTextColor(0xff646464);
		}
		if (date7.getCurrentTextColor() != 0xffe60000) {
			date7.setTextColor(0xff646464);
		}
		date1_bg.setBackgroundColor(0xffffffff);
		date2_bg.setBackgroundColor(0xffffffff);
		date3_bg.setBackgroundColor(0xffffffff);
		date4_bg.setBackgroundColor(0xffffffff);
		date5_bg.setBackgroundColor(0xffffffff);
		date6_bg.setBackgroundColor(0xffffffff);
		date7_bg.setBackgroundColor(0xffffffff);
		
	}
	
	/**
	 * 对日期的计算和显示
	 */
	private void setdate(){
		
		try {
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date d1 = df.parse(year + "-" + (month+1) + "-" + day);
			Date d2 = df.parse(belongdate);
			long diff = d1.getTime() - d2.getTime();
			long days = diff / (1000 * 60 * 60 * 24);
			index = (int) days;//相差天数
			Log.e(TAG, " index:" + index);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (index<=3) {
			date1.setText((Integer.parseInt(getDateStr(6,date1).substring(8,10)))+"");
			date2.setText((Integer.parseInt(getDateStr(5,date2).substring(8,10)))+"");
			date3.setText((Integer.parseInt(getDateStr(4,date3).substring(8,10)))+"");
			date4.setText((Integer.parseInt(getDateStr(3,date4).substring(8,10)))+"");
			date5.setText((Integer.parseInt(getDateStr(2,date5).substring(8,10)))+"");
			date6.setText((Integer.parseInt(getDateStr(1,date6).substring(8,10)))+"");
			date7.setText((Integer.parseInt(getDateStr(0,date7).substring(8,10)))+"");
			week1.setText(getWeekdays(getDateStr(6,week1)));
			week2.setText(getWeekdays(getDateStr(5,week2)));
			week3.setText(getWeekdays(getDateStr(4,week3)));
			week4.setText(getWeekdays(getDateStr(3,week4)));
			week5.setText(getWeekdays(getDateStr(2,week5)));
			week6.setText(getWeekdays(getDateStr(1,week6)));
			week7.setText(getWeekdays(getDateStr(0,week7)));
			fistCurrentItem = 6-index;
			Log.e(TAG, "fistCurrentItem:" + fistCurrentItem);
			
			
		}else if (index>3) {
			date1.setText((DayTime(3,date1).substring(8,10))+"");
			date2.setText((DayTime(2,date2).substring(8,10))+"");
			date3.setText((DayTime(1,date3).substring(8,10))+"");
			date4.setText((DayTime(0,date4).substring(8,10))+"");
			date5.setText((DayTime(-1,date5).substring(8,10))+"");
			date6.setText((DayTime(-2,date6).substring(8,10))+"");
			date7.setText((DayTime(-3,date7).substring(8,10))+"");
			week1.setText(getWeekdays(DayTime(3,week1)));
			week2.setText(getWeekdays(DayTime(2,week2)));
			week3.setText(getWeekdays(DayTime(1,week3)));
			week4.setText(getWeekdays(DayTime(0,week4)));
			week5.setText(getWeekdays(DayTime(-1,week5)));
			week6.setText(getWeekdays(DayTime(-2,week6)));
			week7.setText(getWeekdays(DayTime(-3,week7)));
			fistCurrentItem = 3;
		}
		
		
	}

   /**
	* 获取指定日后 后 dayAddNum 天的 日期  
    * @paramday  日期，格式为String："2013-9-3";
    * @param dayAddNum 增加天数 格式为int;  
     * @return  
     */  
   public static String getDateStr(int dayAddNum,TextView tView) { 
	   Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
	   String dateString = year + "-" + (month+1) + "-" + day;
	   L.e("获得的日期：" + dateString);
       SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
       Date nowDate = null;  
       try {  
           nowDate = df.parse(dateString);  
        } catch (ParseException e) {  
            e.printStackTrace();  
       }  
        Date newDate2 = new Date(nowDate.getTime() - dayAddNum * 24 * 60 * 60 * 1000);  
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");  
        String dateOk = simpleDateFormat.format(newDate2);
        if (getWeekdays(dateOk).equals("周六")||getWeekdays(dateOk).equals("周日")) {
        	tView.setTextColor(0xffe60000);
		}
        L.e("加减后的日期：" + dateOk);
        return dateOk;  
    }  

   
   /**
	 * 时间加减
	 * @param dayAddNum
	 * @return
	 */
	private String DayTime(int dayAddNum,TextView textView){
		String s1 = belongdate;
		  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
	       Date nowDate = null;  
	       try {  
	           nowDate = df.parse(belongdate);  
	        } catch (ParseException e) {  
	            e.printStackTrace();  
	       }  
	        Date newDate2 = new Date(nowDate.getTime() - dayAddNum * 24 * 60 * 60 * 1000);  
	        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");  
	        String dateOk = simpleDateFormat.format(newDate2);
	        if (getWeekdays(dateOk).equals("周六")||getWeekdays(dateOk).equals("周日")) {
	        	textView.setTextColor(0xffe60000);
			}
			return dateOk;
	}
	
	
	 public  String getDate(int dayAddNum) { 
		 String s1 = curentdate;
		  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
	       Date nowDate = null;  
	       try {  
	           nowDate = df.parse(curentdate);  
	        } catch (ParseException e) {  
	            e.printStackTrace();  
	       }  
	        Date newDate2 = new Date(nowDate.getTime() - dayAddNum * 24 * 60 * 60 * 1000);  
	        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");  
	        String dateOk = simpleDateFormat.format(newDate2);
	      
	        return dateOk;  
	    }  
	
	/**
	 * 根据日期查询星期
	 * @param date
	 * @return
	 */
	private static String getWeekdays(String date) {
		String Week = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(format.parse(date));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			Week += "周日";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 2) {
			Week += "周一";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 3) {
			Week += "周二";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 4) {
			Week += "周三";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 5) {
			Week += "周四";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 6) {
			Week += "周五";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 7) {
			Week += "周六";
		}

		return Week;
	}

	
	/**
	 * 初始化图片的位移像素
	 */
	public void InitImage(){
		image = (ImageView)findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.myattendance_line).getWidth();
		image.setBackgroundColor(0xff21a5de);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		Log.e(TAG, "====>" + screenW);
		offset = (screenW - 6*bmpW)/7;
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
		fragment1 = new SummaryDetailFistDateFragment();
		fragment2 = new SummaryDetailSecondDateFragment();
		fragment3 = new SummaryDetailThridDateFragment();
		fragment4 = new SummaryDetailFourthDateFragment();
		fragment5 = new SummaryDetailFifthDateFragment();
		fragment6 = new SummaryDetailSixthDateFragmen();
		fragment7 = new SummaryDetailSeventhDateFragmen();
		
		fragmentList.add(fragment1);
		fragmentList.add(fragment2);
		fragmentList.add(fragment3);
		fragmentList.add(fragment4);
		fragmentList.add(fragment5);
		fragmentList.add(fragment6);
		fragmentList.add(fragment7);
		
		//给ViewPager设置适配器
		mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
		mPager.setCurrentItem(0);//设置当前显示标签页为第一页
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器
		mPager.setOffscreenPageLimit(4);
		mPager.setCurrentItem(fistCurrentItem);
		Log.e(TAG, "aaaaaaaaaa:" + belongdate);
		
		
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
			belongdate = getDate(fistCurrentItem-arg0);
			Log.e(TAG, "=======>" + belongdate);
			Log.e(TAG, "arg0=======>" + arg0);
			
//			//发送普通广播消息机制
//			Intent intent = new Intent(); 
//			intent.setAction("SUMMARY_DETAIL");
//			intent.putExtra("belongdate", belongdate);
//			intent.putExtra("index", arg0);
//			SummaryDetail.this.sendBroadcast(intent);
			getIntent().putExtra("belongdate", belongdate);
			getIntent().putExtra("index", arg0);
			
			
			
			if (arg0==0) {
				setDrawables();
				if (date1.getCurrentTextColor() != 0xffe60000) {
					date1.setTextColor(0xffffffff);
				}
				date1.setCompoundDrawables(null, null, null, drawable);
				date1_bg.setBackgroundColor(0xff05cc76);
			}else if (arg0==1) {
				setDrawables();
				if (date2.getCurrentTextColor() != 0xffe60000) {
					date2.setTextColor(0xffffffff);
				}
				date2.setCompoundDrawables(null, null, null, drawable);
				date2_bg.setBackgroundColor(0xff05cc76);
			}else if (arg0==2) {
				setDrawables();
				if (date3.getCurrentTextColor() != 0xffe60000) {
					date3.setTextColor(0xffffffff);
				}
				date3.setCompoundDrawables(null, null, null, drawable);
				date3_bg.setBackgroundColor(0xff05cc76);
				
			}else if (arg0==3) {
				setDrawables();
				if (date4.getCurrentTextColor() != 0xffe60000) {
					date4.setTextColor(0xffffffff);
				}
				date4.setCompoundDrawables(null, null, null, drawable);
				date4_bg.setBackgroundColor(0xff05cc76);
				
			}else if (arg0==4) {
				setDrawables();
				if (date5.getCurrentTextColor() != 0xffe60000) {
					date5.setTextColor(0xffffffff);
				}
				date5.setCompoundDrawables(null, null, null, drawable);
				date5_bg.setBackgroundColor(0xff05cc76);
			}else if (arg0==5) {
				setDrawables();
				if (date6.getCurrentTextColor() != 0xffe60000) {
					date6.setTextColor(0xffffffff);
				}
				date6.setCompoundDrawables(null, null, null, drawable);
				date6_bg.setBackgroundColor(0xff05cc76);
			}else if (arg0==6) {
				setDrawables();
				if (date7.getCurrentTextColor() != 0xffe60000) {
					date7.setTextColor(0xffffffff);
				}
				date7.setCompoundDrawables(null, null, null, drawable);
				date7_bg.setBackgroundColor(0xff05cc76);
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
