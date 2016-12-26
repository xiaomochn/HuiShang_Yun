package com.huishangyun.Channel.Orders;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.yun.R;

public class DingDanListActivity extends BaseActivity {

	private ViewPager mPager;
	
	/**
	 * 下面滑动fragment的集合
	 */
	private ArrayList<Fragment> fragmentList;
			
	/**
	 * 上面三个导航的文字
	 */
	private TextView view1, view2, view3, text;
	private ImageView img;
	private LinearLayout back, add, find;	
	/**
	 * 图片的高宽的比例
	 */
	private double bili = 0.025;
	
	/**
	 * 横线图片宽度
	 */
	private int bmpW;
	
	/**
	 * 图片移动的偏移量
	 */
	private int offset;
	
	/**
	 * 当前选中页卡编号
	 */
	private int currIndex;
	
	/**
	 * 获取手机屏幕分辨率的类和手机的宽高
	 */
	private DisplayMetrics dm; 
	private int widths, heights;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dingdanlist);
//		intent.putExtra("flage", "DING");
		if (getIntent().getStringExtra("flage").trim().equals("TUI")) {
			text = (TextView) findViewById(R.id.text);
        	text.setText("退货单");
		}
		
		// 获得手机的宽度和高度
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		widths = dm.widthPixels;
		heights = dm.heightPixels;
		
		//订单列表和退单列表复用了界面这里做判断
		if (getIntent().getStringExtra("flage").equals("TUI")) {
			MyApplication.preferences.edit().putInt("STATE", -1).commit();
		}else if (getIntent().getStringExtra("flage").equals("DING")) {
			MyApplication.preferences.edit().putInt("STATE", 1).commit();
		}
		
		init();
		InitTextView();
		InitImage();
		InitViewPager();		
	}
	
	/**
	 * 顶部控件的点击方法
	 */
	private void init(){
		back = (LinearLayout)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();				
			}
		});
		add = (LinearLayout)findViewById(R.id.add);
		add.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(DingDanListActivity.this, OrderMainActivity.class);//OrderMainActivity	
				startActivity(intent);	
				finish();
			}
		});
		find = (LinearLayout)findViewById(R.id.find);
		find.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(DingDanListActivity.this, FindActivity.class);
				if (getIntent().getStringExtra("flage").equals("TUI")) {
					intent.putExtra("flage", "TUI");
				}else {
					intent.putExtra("flage", "DING");
				}
				startActivity(intent);			
			}
		});
		
	}
			
	public void InitTextView() {
		view1 = (TextView) findViewById(R.id.fragment1);
		view2 = (TextView) findViewById(R.id.fragment2);
		view3 = (TextView) findViewById(R.id.fragment3);

		view1.setOnClickListener(new txListener(0));
		view2.setOnClickListener(new txListener(1));
		view3.setOnClickListener(new txListener(2));
	}

	public class txListener implements View.OnClickListener {
		private int index = 0;

		public txListener(int i) {
			index = i;
		}

		public void onClick(View v) {

			mPager.setCurrentItem(index);
		}
	}

	/**
	 * 初始化图片的位移像素
	 */
	public void InitImage() {

		img = (ImageView) findViewById(R.id.img_xian);
		
		//取控件textView当前的布局参数
		 LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) img.getLayoutParams(); 
		
		 linearParams.width = widths / 3;// 控件的宽
		 linearParams.height = (int) (linearParams.width * bili);// 控件的高
		 img.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件
		
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.line)
				.getWidth();
		// 若布局ImageView用的是图片则用上面的，用的是颜色就用下面的获取,但这样下划线与文字对不齐。
		// bmpW = img.getLayoutParams().width;
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 3 - bmpW) / 2;

		// imgageview设置平移，使下划线平移到初始位置（平移一个offset）
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		img.setImageMatrix(matrix);
	}

	/**
	 *  初始化ViewPager
	 */
	public void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.viewpager);
		fragmentList = new ArrayList<Fragment>();

		// 通过实例化fragment类得到的对象添加到fragments集合		
		Fragment_DingDanAll fragment1 = new Fragment_DingDanAll();
		Fragment_DingDanMy fragment2 = new Fragment_DingDanMy();
		Fragment_DindDanBuMen fragment3 = new Fragment_DindDanBuMen();
		
		fragmentList.add(fragment1);
		fragmentList.add(fragment2);
		fragmentList.add(fragment3);

		// 给ViewPager设置适配器
		mPager.setAdapter(new MyFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentList));
		mPager.setCurrentItem(0);// 设置当前显示标签页为第一页
		view1.setTextColor(getResources().getColor(R.color.tab_select));// 默认选项的字体是选中的颜色。

		mPager.setOnPageChangeListener(new MyOnPageChangeListener());// 页面变化时的监听器

		// 从要跳转的activity获取代号，然后选择适应的fragment。
		Intent intent = getIntent();
		int id = intent.getIntExtra("fragment", -1);
		if (id == 1) {
			mPager.setCurrentItem(0);
		} else if (id == 2) {
			mPager.setCurrentItem(1);
		} else if (id == 3) {
			mPager.setCurrentItem(2);
		}

	}

	private PagerAdapter PageAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * ViewPager滑动切换监听器
	 * @author Administrator
	 *
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {
		private int one = offset * 2 + bmpW;// 两个相邻页面的偏移量

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
			Animation animation = new TranslateAnimation(currIndex * one, arg0
					* one, 0, 0);// 平移动画
			currIndex = arg0;
			animation.setFillAfter(true);// 动画终止时停留在最后一帧，不然会回到没有执行前的状态
			animation.setDuration(200);// 动画持续时间0.2秒
			img.startAnimation(animation);// 是用ImageView来显示动画的

			view1.setTextColor(getResources().getColor(R.color.tab_no));
			view2.setTextColor(getResources().getColor(R.color.tab_no));
			view3.setTextColor(getResources().getColor(R.color.tab_no));

			switch (arg0) {
			case 0:
				view1.setTextColor(getResources().getColor(R.color.tab_select));
				break;
			case 1:
				view2.setTextColor(getResources().getColor(R.color.tab_select));
				break;
			case 2:
				view3.setTextColor(getResources().getColor(R.color.tab_select));
				break;

			default:
				break;
			}
		}
	}

	public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
		ArrayList<Fragment> list;

		public MyFragmentPagerAdapter(FragmentManager fm,
				ArrayList<Fragment> list) {
			super(fm);
			this.list = list;

		}

		public int getCount() {
			return list.size();
		}

		public Fragment getItem(int arg0) {
			return list.get(arg0);
		}

	}
		
}
