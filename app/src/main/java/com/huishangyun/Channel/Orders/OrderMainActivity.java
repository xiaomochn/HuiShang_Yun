package com.huishangyun.Channel.Orders;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Order_ProductFav;
import com.huishangyun.yun.R;

public class OrderMainActivity extends BaseActivity implements GetNub{

	/**
	 * 数据库操作
	 */
	private MySql mySql;
	private SQLiteDatabase db;
	private int count;
	
	private ViewPager mPager;	
	/**
	 * 下面滑动fragment的集合
	 */
	private ArrayList<Fragment> fragmentList;
			
	/**
	 * 上面三个导航的文字
	 */
	private TextView view1, view2, view3;
	private ImageView img;
	private LinearLayout back, more;	
	private FrameLayout gouwuche, shoucang;
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
	
	private PopupWindow popupWindow;
	private TextView tv_car, tv_shoucang;
	
	public static String Manager_ID, Company_ID;
	
	private SharedPreferences sharedPreferences;	
	private int Member_ID = 0;//从客户界面过来获取的客户ID
	/**
	 * 获取收藏数量的变量
	 * shoucang_nub：为了不重复读接口，把获取的数量传给产品详情
	 */
	private int shoucang_nub;
	private List<Order_ProductFav> list_data = new ArrayList<Order_ProductFav>();
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				shoucang_nub = list_data.size();
				tv_shoucang.setText(shoucang_nub + "");
				
				if (list_data.size() > 0) {
					tv_shoucang.setVisibility(View.VISIBLE);
				}else {
					tv_shoucang.setVisibility(View.INVISIBLE);
				}
				break;
				
			case HanderUtil.case2:
				//T.showShort(ShouCangActivity.this, (CharSequence) msg.obj);
				break;
							
			default:
				break;
			}
		};
	};
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_order);
		
		//判断是否是从客户界面过来的
		L.e("Member_ID:" + getIntent().getStringExtra("Member_ID"));
		if (getIntent().getStringExtra("Member_ID") != null) {
			L.e("从客户来的创建");	
			sharedPreferences = getSharedPreferences("Manager_ID", MODE_PRIVATE);
			Editor editor = sharedPreferences.edit();
			Member_ID = Integer.parseInt(getIntent().getStringExtra("Member_ID"));
			editor.putInt("Manager_ID", Member_ID).commit();
		}
		
		Manager_ID = preferences.getString(Constant.HUISHANGYUN_UID, "0");
		// 获得手机的宽度和高度
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		widths = dm.widthPixels;
		heights = dm.heightPixels;
		
		tv_car = (TextView)findViewById(R.id.nub_gouwuche);
		tv_shoucang = (TextView)findViewById(R.id.nub_shoucang);
				
		init();
		InitTextView();
		InitImage();
		InitViewPager();	
		
//		mySql = new MySql(OrderMainActivity.this, "mement.db", null, 1);//这里的名字是创建的数据库文件的名称
//		//根据数据库辅助类得到数据库。
//		db = mySql.getWritableDatabase();						
	}
	
	/**
	 * 顶部控件的点击方法
	 */
	private void init(){
		back = (LinearLayout)findViewById(R.id.back_main_bt);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				L.i("进入退出！！！");
				finish();				
			}
		});
		more = (LinearLayout)findViewById(R.id.xiala_main);
		more.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				initPopuptWindow();
				
				popupWindow.showAsDropDown(v);  
			}
		});
		shoucang = (FrameLayout)findViewById(R.id.shoucang_main);
		shoucang.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				L.i("进入收藏！！！");
				Intent intent = new Intent(OrderMainActivity.this, ShouCangActivity.class);	
				startActivity(intent);			
			}
		});
		gouwuche = (FrameLayout)findViewById(R.id.gouwuche_main);
		gouwuche.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				L.i("进入购物车！！！");
				Intent intent = new Intent(OrderMainActivity.this, ShopCarActivity.class);					
				startActivity(intent);
			}
		});
	}
	
		
	/**  
	* 创建PopupWindow  
	*/   
	public void initPopuptWindow() {	
				
		//获取自定义布局文件pop.xml的视图   
		View popupWindow_view = getLayoutInflater().inflate(R.layout.popupwindow, null,false);   		
		
		//创建PopupWindow实例,把上面得到屏幕宽高设置过来 
		popupWindow = new PopupWindow(popupWindow_view, widths/2, 
				ViewGroup.LayoutParams.WRAP_CONTENT, true);   
		//设置动画效果   
		//popupWindow.setAnimationStyle(R.style.AnimationFade);
		
		//设置此参数获得焦点，否则无法点击 
		popupWindow.setFocusable(true); 
		//防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
		popupWindow.setBackgroundDrawable(new PaintDrawable()); 
		//设置点击窗口外边窗口消失 
		popupWindow.setOutsideTouchable(true); 		
		popupWindow.update();  
		
		LinearLayout dingdan = (LinearLayout)popupWindow_view.findViewById(R.id.dingdan);
		LinearLayout tuidan = (LinearLayout)popupWindow_view.findViewById(R.id.tuidan);
		dingdan.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANG_TYPE,"0")) == 1) {
					L.e("权限：" + Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANG_TYPE,"0")));
					Intent intent = new Intent(OrderMainActivity.this, DingDan_CommonActivity.class);
					intent.putExtra("flage", "DING");//因为订单和退单复用了两种权限的订单/退单列表的各个页面，所以要区分开
					startActivity(intent);
				}else {
					Intent intent = new Intent(OrderMainActivity.this, DingDanListActivity.class);
					intent.putExtra("flage", "DING");
					startActivity(intent);
				}
								
				popupWindow.dismiss();
			}
		});
		tuidan.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANG_TYPE,"0")) == 1) {
					L.e("权限：" + Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANG_TYPE,"0")));
					Intent intent = new Intent(OrderMainActivity.this, DingDan_CommonActivity.class);
					intent.putExtra("flage", "TUI");
					startActivity(intent);
				}else {
					Intent intent = new Intent(OrderMainActivity.this, DingDanListActivity.class);					
					intent.putExtra("flage", "TUI");						
					startActivity(intent);
				}				
//				Intent intent = new Intent(OrderMainActivity.this, TuiDanListActivity.class);	
//				startActivity(intent);
				popupWindow.dismiss();
			}
		});
				
	}
	
	
	public void InitTextView() {
		L.i("进入InitTextView()");
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
		L.i("进入InitImage()");
		img = (ImageView) findViewById(R.id.img_xian);
		
		//取控件textView当前的布局参数
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) img.getLayoutParams(); 
		
		linearParams.width = widths / 3;// 控件的宽
		linearParams.height = (int) (linearParams.width * bili);// 控件的高
		img.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件
		bmpW = img.getWidth();

//		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.line)
//				.getWidth();

		
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
		L.i("进入InitViewPager()");
		mPager = (ViewPager) findViewById(R.id.viewpager);
		fragmentList = new ArrayList<Fragment>();

		// 通过实例化fragment类得到的对象添加到fragments集合
		Fragment_All fragment1 = new Fragment_All();
		Fragment_Classify fragment2 = new Fragment_Classify();
		Fragment_CuXiao fragment3 = new Fragment_CuXiao();

		Bundle bundle = new Bundle();
		bundle.putString("Manager_ID", Manager_ID);
		fragment1.setArguments(bundle);
         
		fragmentList.add(fragment1);
		fragmentList.add(fragment2);
		fragmentList.add(fragment3);

		// 给ViewPager设置适配器
		mPager.setAdapter(new MyFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentList));
		mPager.setCurrentItem(0);// 设置当前显示标签页为第一页
		mPager.setBackgroundColor(Color.WHITE);
		view1.setTextColor(0xff00658f);// 默认选项的字体是选中的颜色。

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
				view1.setTextColor(0xff00658f);
				break;
			case 1:
				view2.setTextColor(0xff00658f);
				break;
			case 2:
				view3.setTextColor(0xff00658f);
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

	/**
	 * 点击购物车红点数字加减的方法
	 */
	public void addNub() {	
		count = Integer.parseInt(tv_car.getText().toString()) + 1;
		tv_car.setVisibility(View.VISIBLE);
		tv_car.setText(count + "");
	}

	
	public void deletNub() {									
		count = Integer.parseInt(tv_car.getText().toString()) - 1;
		if (count > 0) {
			tv_car.setText(count + "");
			tv_car.setVisibility(View.VISIBLE);
			
		}else {
			count = 0;
			tv_car.setText(count + "");
			tv_car.setVisibility(View.GONE);
		}											
	}
	
	/**
	 * 每次都获取购物车数据库的总条数
	 */
	protected void onResume() {		
		L.i("进入onResume()");
		//公共的从数据库获取购物车数量
		Common.getCarCounts(OrderMainActivity.this, tv_car);
		//tv_car.setText(CartManager.getInstance(OrderMainActivity.this).getSize());
		//获取到收藏个数的方法
		Common.getShouCangCounts(OrderMainActivity.this, tv_shoucang);
							
		super.onResume();
	}
		
}
