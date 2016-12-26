package com.huishangyun.Channel.Task;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Matrix;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Adapter.MyFragmentPagerAdapter;
import com.huishangyun.Util.L;
import com.huishangyun.model.Constant;
import com.huishangyun.yun.R;

/**
 * 任务列表界面
 * 
 * @author Pan
 * 
 * @version V1.0 2014/8/05
 * @see无
 * @since 亿企云app/渠道
 * 
 *                      -ooo8ooo-
 *                     o888888888o
 *                     888" . "888
 *                     (|  -_-  |)
 *                     0 \  =  / 0
 *                       / --- \
 *                       
 *                       
 *                     画不出来了.......
 * 
 */
public class TaskMainActivity extends BaseActivity {

	private ViewPager mViewPager;// ViewPager对象
	private List<Fragment> mList;
	private LinearLayout backBtn;// 返回按钮
	private LinearLayout addBtn;// 添加按钮
	private LinearLayout moreBtn;// 更多按钮
	private LinearLayout searchBtn;// 搜索按钮
	private Button allBtn;// 全部按钮
	private Button launchBtn;// 发起按钮
	private Button acceptedBtn;// 接受按钮
	private ImageView lineImg;// 底部线条
	private int bmpW;// 图片资源
	private int offset;// 偏移量
	private int currIndex = 0;
	private LayoutInflater mInflater;
	private DisplayMetrics dm;//获取屏幕尺寸
	private PopupWindow mWindow;//菜单选项
	private int pageIndex = 1;
	private static final int SELECT_TYPE = 0;
	private static final int SELECT_TIME = 1;
	private static final int SELECT_PROG = 2;
	private AllFragment all;
	private LaunchFragment launch;
	private AcceptedFragment Accepted;
	public int Member_ID;
	private LinearLayout layout;
	private TextView customer_name;
	private String Name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_list);
		initView();
		initViewPager();
		initPoptWindow();
		initListener();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	/**
	 * 实例化各组件
	 */
	private void initView() {
		// 头部组件
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constant.HUISHANG_RE_ACTION);
		registerReceiver(receiver, intentFilter);
		Member_ID = Integer.parseInt(getIntent().getStringExtra("Member_ID"));
		Name = getIntent().getStringExtra("Member_Name");
		backBtn = (LinearLayout) findViewById(R.id.title_back);
		addBtn = (LinearLayout) findViewById(R.id.title_add);
		moreBtn = (LinearLayout) findViewById(R.id.title_more);
		searchBtn = (LinearLayout) findViewById(R.id.title_search);
		customer_name = (TextView) findViewById(R.id.task_customer_name);
		customer_name.setText(Name);
		if (Member_ID == -1) {
			customer_name.setVisibility(View.GONE);
		} else {
			customer_name.setVisibility(View.VISIBLE);
		}
		// ViewPager组件
		allBtn = (Button) findViewById(R.id.task_all);
		launchBtn = (Button) findViewById(R.id.task_launch);
		acceptedBtn = (Button) findViewById(R.id.task_accepted);
		lineImg = (ImageView) findViewById(R.id.task_line);
		mViewPager = (ViewPager) findViewById(R.id.viewpager_miantask);
		mViewPager.setOffscreenPageLimit(3);
		mInflater = LayoutInflater.from(TaskMainActivity.this);
		layout = (LinearLayout) findViewById(R.id.task_scroll_layout);
		if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
			layout.setVisibility(View.GONE);
			allBtn.setText("全部");
			launchBtn.setText("");
			acceptedBtn.setText("");
			//addBtn.setVisibility(View.GONE);
		} else {
			layout.setVisibility(View.VISIBLE);
			allBtn.setText("全部");
			launchBtn.setText("我发起的");
			acceptedBtn.setText("我接受的");
			//addBtn.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 设置ViewPager
	 */
	private void initViewPager() {
		bmpW = lineImg.getWidth();// 获取图片宽度
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mList = new ArrayList<Fragment>();
		all = new AllFragment();
		launch = new LaunchFragment();
	    Accepted = new AcceptedFragment();
		if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
			mList.add(all);
		} else {
			mList.add(all);
			mList.add(launch);
			mList.add(Accepted);
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
		mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), mList));
		mViewPager.setCurrentItem(0);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		allBtn.setTextColor(0xff00658f);
		launchBtn.setTextColor(0xff646464);
		acceptedBtn.setTextColor(0xff646464);
	}
	
	/**
	 * 添加点击时间监听
	 */
	private void initListener() {
		backBtn.setOnClickListener(mListener);
		addBtn.setOnClickListener(mListener);
		moreBtn.setOnClickListener(mListener);
		searchBtn.setOnClickListener(mListener);
		allBtn.setOnClickListener(mListener);
		acceptedBtn.setOnClickListener(mListener);
		launchBtn.setOnClickListener(mListener);
	}
	
	/**
	 * 创建PopuptWindow对象
	 */
	private void initPoptWindow(){
		//加载布局
		View mPopView = mInflater.inflate(R.layout.task_popwindow, null);
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
		LinearLayout stateBtn = (LinearLayout) mPopView.findViewById(R.id.task_pop_state);
		LinearLayout timeBtn = (LinearLayout) mPopView.findViewById(R.id.task_pop_time);
		LinearLayout priorityBtn = (LinearLayout) mPopView.findViewById(R.id.task_pop_priority);
		
		//设置点击时间监听
		stateBtn.setOnClickListener(mListener);
		timeBtn.setOnClickListener(mListener);
		priorityBtn.setOnClickListener(mListener);
		
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
			case R.id.title_back://返回按钮
				finish();
				break;
			case R.id.title_add://添加按钮
				mIntent = new Intent(TaskMainActivity.this, TaskAddActivity.class);
				startActivityForResult(mIntent, 0);
				mIntent = null;
				break;
			case R.id.title_more://更多
				L.e("点击更多按钮");
				mWindow.showAsDropDown(v);//弹出菜单选项
				
				break;
			case R.id.title_search://搜索
				mIntent = new Intent(TaskMainActivity.this, SearchActivity.class);
				break;
			case R.id.task_all://全部
				mViewPager.setCurrentItem(0);
				currIndex = 0;
				break;
			case R.id.task_launch://发起
				mViewPager.setCurrentItem(1);
				currIndex = 1;
				break;
			case R.id.task_accepted://接受
				mViewPager.setCurrentItem(2);
				currIndex = 2;
				break;
				
			case R.id.task_pop_state://状态
				L.e("点击了状态");
				mIntent = new Intent(TaskMainActivity.this, TaskMenuActivity.class);
				mIntent.putExtra("Field", "Status");
				mIntent.putExtra("Type", 0);
				mWindow.dismiss();
				break;
				
			case R.id.task_pop_time://时间
				L.e("点击了时间");
				mIntent = new Intent(TaskMainActivity.this, TaskMenuActivity.class);
				mIntent.putExtra("Field", "Month");
				mIntent.putExtra("Type", 1);
				mWindow.dismiss();
				break;
				
			case R.id.task_pop_priority://优先级
				L.e("点击了优先级");
				mIntent = new Intent(TaskMainActivity.this, TaskMenuActivity.class);
				mIntent.putExtra("Field", "Flag");
				mIntent.putExtra("Type", 2);
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
			allBtn.setTextColor(0xff00658f);
			launchBtn.setTextColor(0xff646464);
			acceptedBtn.setTextColor(0xff646464);
			break;
		case 1:
			if (currIndex == 0) {
				animation = new TranslateAnimation(offset, one, 0, 0);
			} else if (currIndex == 2) {
				animation = new TranslateAnimation(two, one, 0, 0);
			}
			
			allBtn.setTextColor(0xff646464);
			launchBtn.setTextColor(0xff00658f);
			acceptedBtn.setTextColor(0xff646464);
			break;
		case 2:
			if (currIndex == 0) {
				animation = new TranslateAnimation(offset, two, 0, 0);
			} else if (currIndex == 1) {
				animation = new TranslateAnimation(one, two, 0, 0);
			}
			allBtn.setTextColor(0xff646464);
			launchBtn.setTextColor(0xff646464);
			acceptedBtn.setTextColor(0xff00658f);
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
	protected void onActivityResult(int requstCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		L.e("进入onActivityResult");
		if (resultCode == RESULT_OK) {
			//刷新各Litsview
			L.e("进入刷新");
			all.startRefresh();
			launch.startRefresh();
			Accepted.startRefresh();
		}
		super.onActivityResult(requstCode, resultCode, data);
	}
	
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			all.startRefresh();
			launch.startRefresh();
			Accepted.startRefresh();
		}
		
	};
}
