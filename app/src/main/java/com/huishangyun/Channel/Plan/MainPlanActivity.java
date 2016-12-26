package com.huishangyun.Channel.Plan;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.Methods;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.yun.R;

public class MainPlanActivity extends BaseActivity implements MyXListView.MyXListViewListener {
	
	private static final String TAG = null;
	private LinearLayout back;// 返回键
	private LinearLayout search_plan;// 计划搜索
	private LinearLayout create_plan;// 创建计划
	private RelativeLayout top_bar;//顶部条
	private ViewPager vPager;// 页卡内容
	private TextView textView1, textView2, textView3;//选项卡标题文字
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	View view1, view2, view3;// 三个viewpager选项卡页面
	ImageView lineImg;//滚动动画图片
	DisplayMetrics dm;
	ArrayList<View> pagerList;//页卡容器
	LayoutInflater mInflater;
	MyXListView all_plan, my_plan, department_plan;//三选项卡listview
	List<PlanList> ItemLists1 = new ArrayList<PlanList>();
	List<PlanList> List1 = new ArrayList<PlanList>();
	List<PlanList> ItemLists2 = new ArrayList<PlanList>();
	List<PlanList> List2 = new ArrayList<PlanList>();
	List<PlanList> ItemLists3 = new ArrayList<PlanList>();
	List<PlanList> List3 = new ArrayList<PlanList>();
	AllListViewAdapter adapter1;//listview适配器
	MyListViewAdapter adapter2;
	DepartmentListViewAdapter adapter3;
	public static int deviceWidth;
	private int pager_size_all = 10, pager_size_my = 10, pager_size_dp = 10;// 单页显示的条数
	private int PagerIndex_all = 1,PagerIndex_my = 1,PagerIndex_department = 1 ;
	private int view_index=1;
	private ImageView all_no_information;
	private ImageView my_no_information;
	private ImageView department_no_information;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan_mian);
		init();
		viewpagerinit();
		initViewPager();
		itemClickListener();
	    MyApplication.getInstance().showDialog(MainPlanActivity.this, true, "Loading...");
		setData(0, 0, 0);
	}
	
	
	/**
	 * 根据权限设置加载数据和显示界面
	 * @param refresh_index 刷新指数 
	 * 0：刚进来加载
	 * 1：下拉刷新
	 * 2：上拉加载 
	 * @param onload_index 判断下拉刷新那个
	 * @param loadmore_index 判断上拉加载那个
	 * 
	 */
	private void setData(int refresh_index, int onload_index, int loadmore_index ){
		
		mHandler.sendEmptyMessage(4);		 
		// 用户编号
		int uid = Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID,
				"0"));
		// 所属部门id
		int department_id = preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID,
				0);
		
			switch (refresh_index) {
			case 0:
				//获取全部计划
				getNetData(preferences.getInt(Content.COMPS_ID, 1016), 0, department_id, "", 1, pager_size_all,1);
				
				
				
				//获取我的计划
				getNetData(preferences.getInt(Content.COMPS_ID, 1016), uid, 0, "", 1, pager_size_my, 2);
				
				//获取部门计划
				getNetData(preferences.getInt(Content.COMPS_ID, 1016), 0, department_id, "", 1, pager_size_dp, 3);
			
				break;
				
		case 1:
			//下拉刷新处理
			if (onload_index==1) {
				//全部商机刷新
				getNetData(preferences.getInt(Content.COMPS_ID, 1016), 0, department_id, "", 1, pager_size_all,1);
			}else if (onload_index==2) {
				//我的商机刷新
				getNetData(preferences.getInt(Content.COMPS_ID, 1016), uid, 0, "", 1, pager_size_my, 2);
			}else if (onload_index==3) {
				//部门商机刷新
				getNetData(preferences.getInt(Content.COMPS_ID, 1016), 0, department_id, "", 1, pager_size_dp, 3);
			}
			
			break;
			
			//上拉加载处理
			case 2:
				//下拉刷新处理
				if (loadmore_index==1) {
					Log.e(TAG, "onload_index:" + onload_index);
					//全部商机刷新
					getNetData(preferences.getInt(Content.COMPS_ID, 1016), 0, 0, "",PagerIndex_all, pager_size_all,4);
				}else if (loadmore_index==2) {
					//我的商机刷新
					getNetData(preferences.getInt(Content.COMPS_ID, 1016), uid, 0, "", PagerIndex_my, pager_size_my,5);
				}else if (loadmore_index==3) {
					//部门商机刷新
					getNetData(preferences.getInt(Content.COMPS_ID, 1016), 0, department_id, "",PagerIndex_department, pager_size_dp,6);
				}
				
				break;
            case 3:
            	
				break;
			default:
				break;
			}
			
	
	}
	
	/**
	 * 获取网络数据
	 * @param Company_ID 公司id
	 * @param Manager_ID 用户编号
	 * @param Department_ID 部门编号
	 * @param keywords 搜索关键字
	 * @param pageIndex 页码
	 * @param index 根据指数判断给那个数组赋值 1.2.3：为刷新对应值 --4.5.6为：加载对应值
	 */
	private void getNetData(final int Company_ID, final int Manager_ID,
			final int Department_ID, final String keywords, final int pageIndex,final int pageSize, final int index){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub	
				Log.e(TAG, "result:" + getJson(Company_ID, Manager_ID, Department_ID,
						keywords, pageIndex,pageSize));
				String result = DataUtil.callWebService(
						Methods.PLAN_LIST,
						getJson(Company_ID, Manager_ID, Department_ID,
								keywords, pageIndex, pageSize));
				Log.e(TAG, "result:" + result+index);
				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					// 获取对象的Type
					Type type = new TypeToken<ZJResponse<PlanList>>() {
					}.getType();
					ZJResponse<PlanList> zjResponse = JsonUtil.fromJson(result,
							type);
					// 获取对象列表
					// 用于下拉刷新或者刚进来时加载数据，需要清除存储的数据
					switch (index) {
					case 1:
						ItemLists1.clear();
						ItemLists1 = zjResponse.getResults();
						//通知listview改变
						mHandler.sendEmptyMessage(1);	
						break;
					case 2:
						ItemLists2.clear();
						ItemLists2 = zjResponse.getResults();
						mHandler.sendEmptyMessage(2);
						break;
					case 3:
						ItemLists3.clear();
						ItemLists3 = zjResponse.getResults();
						mHandler.sendEmptyMessage(3);
						break;

					// 用于上拉加载或者，搜索后结果很多后的加载，此时不需要清空列表
					case 4:
						ItemLists1 = zjResponse.getResults();
						mHandler.sendEmptyMessage(5);
						break;
					case 5:
						ItemLists2 = zjResponse.getResults();
						mHandler.sendEmptyMessage(6);
						break;
					case 6:
						ItemLists3 = zjResponse.getResults();
						mHandler.sendEmptyMessage(7);
						break;
					default:
						break;
					}
				} else {
					mHandler.sendEmptyMessage(8);
				}
			}
		}.start();
	}
	
	/**
	 * 设置json对象
	 * @param Company_ID 公司id
	 * @param Manager_ID 用户编号
	 * @param Department_ID 部门编号
	 * @param keywords 搜索关键字
	 * @param pageIndex 页码
	 * @return
	 */
	private String getJson(int Company_ID, int Manager_ID,
			int Department_ID, String keywords, int pageIndex, int pageSize) {
		ZJRequest zjRequest = new ZJRequest();
		
		// 公司id
		zjRequest.setCompany_ID(Company_ID);
		
		// 用户编号
		zjRequest.setManager_ID(Manager_ID);
	
		// 设置部门号，0时为相当没有部门编号查询
		zjRequest.setDepartment_ID(Department_ID);
		// 设置搜索关键字
		zjRequest.setKeywords(keywords);
		// 设置页码，默认是1
		zjRequest.setPageIndex(pageIndex);
		// 设置当页显示条数
		zjRequest.setPageSize(pageSize);
		return JsonUtil.toJson(zjRequest);

	}
	

	/**
	 * 初始化页面控件
	 */
	private void init() {
		
        //头部组件
		top_bar = (RelativeLayout) findViewById(R.id.top_bar);
		back = (LinearLayout) findViewById(R.id.back);
		search_plan = (LinearLayout) findViewById(R.id.search_plan);
		create_plan = (LinearLayout) findViewById(R.id.create_plan);
		
		//viewpager页卡名称组件
		textView1 = (TextView) findViewById(R.id.tv1);
		textView2 = (TextView) findViewById(R.id.tv2);
		textView3 = (TextView) findViewById(R.id.tv3);
		
        //对应单击事件
		back.setOnClickListener(new ButtonClickListener());
		search_plan.setOnClickListener(new ButtonClickListener());
		create_plan.setOnClickListener(new ButtonClickListener());
		textView1.setOnClickListener(new ButtonClickListener());
		textView2.setOnClickListener(new ButtonClickListener());
		textView3.setOnClickListener(new ButtonClickListener());
		
	}
	
	/**
	 * 实时更新listview列表
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				
				//通过jiang数据复制到另一个对象中然后适配器中使用，可以避免listview加载数据报错
				List1.clear();
				for ( int i = 0; i < ItemLists1.size(); i++) {
					List1.add(ItemLists1.get(i));
				}
				if (List1.size()==0) {
					all_no_information.setVisibility(View.VISIBLE);
				}else {
					all_no_information.setVisibility(View.GONE);
				}
				//全部线索
				all_plan.setAdapter(adapter1);
                adapter1.notifyDataSetChanged();
                MyApplication.getInstance().showDialog(MainPlanActivity.this, false, "Loading...");
				break;
			case 2:
				List2.clear();
				for ( int i = 0; i < ItemLists2.size(); i++) {
					List2.add(ItemLists2.get(i));
				}
				if (List2.size()==0) {
					my_no_information.setVisibility(View.VISIBLE);
				}else {
					my_no_information.setVisibility(View.GONE);
				}
			   my_plan.setAdapter(adapter2);
               adapter2.notifyDataSetChanged();
               
				break;
			case 3:
				List3.clear();
				for ( int i = 0; i < ItemLists3.size(); i++) {
					List3.add(ItemLists3.get(i));
				}
				if (List3.size()==0) {
					department_no_information.setVisibility(View.VISIBLE);
				}else {
					department_no_information.setVisibility(View.GONE);
				}
				//部门线索
				department_plan.setAdapter(adapter3);
                adapter3.notifyDataSetChanged();
                
				break;
			case 4://加载界面
//				showDialog("正在加载......");
				break;
			case 5:
				if (ItemLists1.size()<=0) {
					new ClueCustomToast().showToast(MainPlanActivity.this,
							R.drawable.toast_warn, "没有更多数据！");
				}else {
					for ( int i = 0; i < ItemLists1.size(); i++) {
						List1.add(ItemLists1.get(i));
					}
					//全部线索
					all_plan.setAdapter(adapter1);
	                adapter1.notifyDataSetChanged();
				}
				break;
			case 6:
				if (ItemLists2.size()<=0) {
					ClueCustomToast.showToast(MainPlanActivity.this,
							R.drawable.toast_warn, "没有更多数据！");
				}else {
					for ( int i = 0; i < ItemLists2.size(); i++) {
						List2.add(ItemLists2.get(i));
					}
				   my_plan.setAdapter(adapter2);
	               adapter2.notifyDataSetChanged();
				}
				break;
			case 7:
				if (ItemLists3.size()<=0) {
					ClueCustomToast.showToast(MainPlanActivity.this,
							R.drawable.toast_warn, "没有更多数据！");
				}else {
					for ( int i = 0; i < ItemLists3.size(); i++) {
						List3.add(ItemLists3.get(i));
					}
					//部门线索
					department_plan.setAdapter(adapter3);
	                adapter3.notifyDataSetChanged();
				}
				break;
			case 8:
				MyApplication.getInstance().showDialog(MainPlanActivity.this, false, "Loading...");
				ClueCustomToast.showToast(MainPlanActivity.this,
						R.drawable.toast_warn, "未获得任何数据，请检查网络是否连接！");
			   break;
			default:
				break;
			}
		};
	};


	/**
	 * 单击事件处理
	 * 
	 * @author xsl
	 * 
	 */
	public class ButtonClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.search_plan:
				//跳转到搜索界面
				Intent intent3 = new Intent(MainPlanActivity.this, PlanSearch.class);
				intent3.putExtra("flag", 0);//0表示搜索全部，1表示搜索个人
				startActivity(intent3);
				break;
				
			case R.id.create_plan:
				
				Intent intent = new Intent(MainPlanActivity.this, CreatePlanActivity.class);
				startActivity(intent);
				
				break;
				
			case R.id.tv1:
				vPager.setCurrentItem(0);
				textView2.setTextColor(Color.parseColor("#646464"));
				textView3.setTextColor(Color.parseColor("#646464"));
				textView1.setTextColor(Color.parseColor("#00658f"));
				break;

			case R.id.tv2:
				vPager.setCurrentItem(1);
				textView1.setTextColor(Color.parseColor("#646464"));
				textView3.setTextColor(Color.parseColor("#646464"));
				textView2.setTextColor(Color.parseColor("#00658f"));
				break;

			case R.id.tv3:
				vPager.setCurrentItem(2);
				textView1.setTextColor(Color.parseColor("#646464"));
				textView2.setTextColor(Color.parseColor("#646464"));
				textView3.setTextColor(Color.parseColor("#00658f"));
				break;
			default:
				break;
			}

		}

	}
	
	
	
	


	/**
	 * 实例化各组件
	 */
	private void viewpagerinit() {
		
		// ViewPager组件
		lineImg = (ImageView) findViewById(R.id.cursor);
		vPager = (ViewPager) findViewById(R.id.vPager);
		pagerList = new ArrayList<View>();
		mInflater = getLayoutInflater().from(MainPlanActivity.this);

		// 获取布局对象
		view1 = mInflater.inflate(R.layout.activity_clue_mian_all, null);
		view2 = mInflater.inflate(R.layout.activity_clue_mian_all, null);
		view3 = mInflater.inflate(R.layout.activity_clue_mian_all, null);
		
		all_plan = (MyXListView) view1.findViewById(R.id.all_listview);
		all_no_information = (ImageView) view1.findViewById(R.id.no_information);
		my_plan = (MyXListView) view2.findViewById(R.id.all_listview);
		my_no_information = (ImageView) view2.findViewById(R.id.no_information);
		department_plan = (MyXListView) view3.findViewById(R.id.all_listview);
		department_no_information = (ImageView) view3.findViewById(R.id.no_information);
		
		adapter1 = new AllListViewAdapter(this);
		adapter2 = new MyListViewAdapter(this);
		adapter3 = new DepartmentListViewAdapter(this);
		//全部线索
		all_plan.setAdapter(adapter1);
		all_plan.setPullLoadEnable(true);
		all_plan.setMyXListViewListener(this);
		//我的线索
		my_plan.setAdapter(adapter2);
		my_plan.setPullLoadEnable(true);
		my_plan.setMyXListViewListener(this);
		//部门线索
		department_plan.setAdapter(adapter3);
		department_plan.setPullLoadEnable(true);
		department_plan.setMyXListViewListener(this);
		
		// 将布局添加到容器
		pagerList.add(view1);
		pagerList.add(view2);
		pagerList.add(view3);
	}
	
	/**
	 * listview item单击事件处理
	 */
	private void itemClickListener() {
	
		// 全部列表item单击事件
				all_plan.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						// 跳转到详情页面
						Intent intent = new Intent(MainPlanActivity.this,
								PlanDetailActivity.class);
						intent.putExtra("ID", List1.get(position-1).getID());
						startActivity(intent);
					}

				});

				// 我的计划列表单击 事件
				my_plan.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub

						Intent intent = new Intent(MainPlanActivity.this,
								PlanDetailActivity.class);
						intent.putExtra("ID", List2.get(position-1).getID());
						startActivity(intent);

					}

				});

				// 部门列表item单击事件
				department_plan.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub

						Intent intent = new Intent(MainPlanActivity.this,
								PlanDetailActivity.class);
						intent.putExtra("ID", List3.get(position-1).getID());
						startActivity(intent);

					}

				});
	}
	
	
	
	/**
	 * 设置ViewPager
	 */
	private void initViewPager() {
		
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.line_task).getWidth();// 获取图片宽度
		lineImg.setBackgroundColor(0xff00658f);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		lineImg.setImageMatrix(matrix);// 设置动画初始位置

		// 添加Adapter
		vPager.setAdapter(new MyPagerAdapter(pagerList));
		vPager.setCurrentItem(0);
		vPager.setOnPageChangeListener(new MyOnPageChangeListener());

	}
	
	/**
	 * 页卡变动监听
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
	 */
	private void setTopView(int arg0) {
		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量
		Animation animation = null;
		switch (arg0) {
		case 0:
			view_index = 1;
			if (currIndex == 1) {//判断当前选中项
				animation = new TranslateAnimation(one, 0, 0, 0);
			} else if (currIndex == 2) {
				animation = new TranslateAnimation(two, 0, 0, 0);

			}
			textView1.setTextColor(Color.parseColor("#00658f"));
			textView2.setTextColor(Color.parseColor("#646464"));
			textView3.setTextColor(Color.parseColor("#646464"));
			break;
		case 1:
			view_index = 2;
			if (currIndex == 0) {
				animation = new TranslateAnimation(offset, one, 0, 0);
			} else if (currIndex == 2) {
				animation = new TranslateAnimation(two, one, 0, 0);
			}
			textView1.setTextColor(Color.parseColor("#646464"));
			textView2.setTextColor(Color.parseColor("#00658f"));
			textView3.setTextColor(Color.parseColor("#646464"));
			break;
		case 2:
			view_index = 3;
			if (currIndex == 0) {
				animation = new TranslateAnimation(offset, two, 0, 0);
			} else if (currIndex == 1) {
				animation = new TranslateAnimation(one, two, 0, 0);
			}
			textView1.setTextColor(Color.parseColor("#646464"));
			textView2.setTextColor(Color.parseColor("#646464"));
			textView3.setTextColor(Color.parseColor("#00658f"));
			break;

		default:
			break;
		}
		currIndex = arg0;
		animation.setFillAfter(true);// True:图片停在动画结束位置
		animation.setDuration(200);
		lineImg.startAnimation(animation);
	}
	

	/**
	 * viewpager适配器
	 * @author xsl
	 *
	 */
	public class MyPagerAdapter extends PagerAdapter{
		public List<View> mListViews;
		
		public MyPagerAdapter(List<View> mListViews){
			this.mListViews = mListViews;
		}
		
		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			((ViewPager) container).removeView(mListViews.get(position));
		}
		
		@Override
		public void finishUpdate(ViewGroup container) {
			// TODO Auto-generated method stub
			super.finishUpdate(container);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mListViews.size();
		}
		
		@Override
		public Object instantiateItem(View container, int position) {
			// TODO Auto-generated method stub
			((ViewPager) container).addView(mListViews.get(position), 0);
			 return mListViews.get(position);
		}
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			 return arg0 == (arg1);
		}
		
		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
			// TODO Auto-generated method stub
			super.restoreState(state, loader);
		}
		
		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public void startUpdate(View container) {
			// TODO Auto-generated method stub
		}
     }

	/**
	 * 自定义--全部计划--listview适配器
	 */
	public class AllListViewAdapter extends BaseAdapter {

		private LayoutInflater mInflater;// 动态布局映射

		public AllListViewAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		// 决定listview显示条数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
//			return ItemLists.size();
			return List1.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			 ViewHolder holder; 
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.activity_clue_listview_item, null);// 根据布局文件实例化view
				
				holder = new ViewHolder();
				
				holder.theme_name = (TextView) convertView
						.findViewById(R.id.theme_name);// 找某个控件 --标题
				holder.state = (TextView) convertView.findViewById(R.id.state);// 找某个控件--状态
				holder.state.setVisibility(View.GONE);
				holder.ps_name = (TextView) convertView
						.findViewById(R.id.ps_name);// 找某个控件 --公司名称
				holder.date = (TextView) convertView.findViewById(R.id.date);// 找某个控件--时间
				holder.iv = (ImageView) convertView.findViewById(R.id.iv);// 公司头像
				holder.date_img = (ImageView) convertView
						.findViewById(R.id.date_img);// 日期图标
				
			
				
				convertView.setTag(holder);

			} else {
				
				holder = (ViewHolder) convertView.getTag();
				
			}
			
			holder.theme_name.setText(List1.get(position).getTitle());
			Log.e("---------------", "theme_name:" + List1.get(position).getTitle());
			holder.ps_name.setText(List1.get(position).getManager_Name());
			holder.date.setText(backDate(List1.get(position).getAddDateTime()));
			holder.iv.setBackgroundResource(R.drawable.contact_ps);
			holder.date_img.setBackgroundResource(R.drawable.datetime);
			
			return convertView;
		}

	}
	
	/**
	 * 自定义--我的计划--listview适配器
	 */
	public class MyListViewAdapter extends BaseAdapter {

		private LayoutInflater mInflater;// 动态布局映射

		public MyListViewAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		// 决定listview显示条数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
//			return ItemLists.size();
			return List2.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			 ViewHolder holder; 
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.activity_clue_listview_item, null);// 根据布局文件实例化view
				
				holder = new ViewHolder();
				
				holder.theme_name = (TextView) convertView
						.findViewById(R.id.theme_name);// 找某个控件 --标题
				holder.state = (TextView) convertView.findViewById(R.id.state);// 找某个控件--状态
				holder.state.setVisibility(View.GONE);
				holder.ps_name = (TextView) convertView
						.findViewById(R.id.ps_name);// 找某个控件 --公司名称
				holder.date = (TextView) convertView.findViewById(R.id.date);// 找某个控件--时间
				holder.iv = (ImageView) convertView.findViewById(R.id.iv);// 公司头像
				holder.date_img = (ImageView) convertView
						.findViewById(R.id.date_img);// 日期图标
				
				
				
				convertView.setTag(holder);

			} else {
				
				holder = (ViewHolder) convertView.getTag();
				
			}
			
			holder.theme_name.setText(List2.get(position).getTitle());
			holder.ps_name.setText(List2.get(position).getManager_Name());
			holder.date.setText(backDate(List2.get(position).getAddDateTime()));
			holder.iv.setBackgroundResource(R.drawable.contact_ps);
			holder.date_img.setBackgroundResource(R.drawable.datetime);
			
			return convertView;
		}

	}
	
	
	/**
	 * 自定义--部门计划--listview适配器
	 */
	public class DepartmentListViewAdapter extends BaseAdapter {

		private LayoutInflater mInflater;// 动态布局映射

		public DepartmentListViewAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		// 决定listview显示条数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
//			return ItemLists.size();
			return List3.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			 ViewHolder holder; 
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.activity_clue_listview_item, null);// 根据布局文件实例化view
				
				holder = new ViewHolder();
				
				holder.theme_name = (TextView) convertView
						.findViewById(R.id.theme_name);// 找某个控件 --标题
				holder.state = (TextView) convertView.findViewById(R.id.state);// 找某个控件--状态
				holder.state.setVisibility(View.GONE);
				holder.ps_name = (TextView) convertView
						.findViewById(R.id.ps_name);// 找某个控件 --公司名称
				holder.date = (TextView) convertView.findViewById(R.id.date);// 找某个控件--时间
				holder.iv = (ImageView) convertView.findViewById(R.id.iv);// 公司头像
				holder.date_img = (ImageView) convertView
						.findViewById(R.id.date_img);// 日期图标
			
				
				convertView.setTag(holder);

			} else {
				
				holder = (ViewHolder) convertView.getTag();
				
			}
			
			holder.theme_name.setText(List3.get(position).getTitle());
			holder.ps_name.setText(List3.get(position).getManager_Name());
			holder.date.setText(backDate(List3.get(position).getAddDateTime()));
			holder.iv.setBackgroundResource(R.drawable.contact_ps);
			holder.date_img.setBackgroundResource(R.drawable.datetime);
			
			return convertView;
		}

	}
	
	/***----ViewHolder不是Android的开发API，而是一种设计方法，就是设计个静态类，缓存一下，省得Listview更新的时候，还要重新操作。 ----**/	
     
	/**
	 * ViewHolder 模式, 效率提高 50%
	 * 
	 * @author XSL
	 * @version 不用这个容器会导致进入viewpager特别卡
	 */
    static class ViewHolder { 
         //标题
        private TextView theme_name; 
        //状态
        private TextView state; 
        //日期
        private TextView date; 
        //创建人
        private TextView ps_name; 
       
        //创建人图标
        ImageView iv; 
        //日期图标
        ImageView date_img; 
       

        }

    /**
	 * 传入后台接到的时间，返回我们需要的格式
	 * @param date 传入时间参数
	 * @return
	 */
	private String backDate(String date){
		//这里包含0位但不包含5即（0，5】
		String a = date.substring(0,4);
		String b = date.substring(5,7);
		String c = date.substring(8,10);
		String datesString = a + "年" + b + "月" + c + "日" ;
		
		return datesString;		
	}




	/**
	 * 下拉刷新动作
	 * @param load_index
	 */
	private void onLoad(int load_index) {
		switch (load_index) {
		case 1:
			all_plan.stopRefresh();
			all_plan.stopLoadMore();
			all_plan.setRefreshTime();
			break;
		case 2:
			my_plan.stopRefresh();
			my_plan.stopLoadMore();
			my_plan.setRefreshTime();
			break;
		case 3:
			department_plan.stopRefresh();
			department_plan.stopLoadMore();
			department_plan.setRefreshTime();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 根据条件条件停止其它两个listview的刷新和加载动作
	 * 
	 * @param stop_index
	 * @param getpager 判断是否是加载动作0为刷新，1为加载
	 */
	private void stopLoad(int stop_index,int getpager) {
		switch (stop_index) {
		case 1:
			onLoad(2);
			onLoad(3);
			if (getpager==1) {//加载增加10条数据
//				//页码加1
				PagerIndex_all += 1;
			}else {//刷新增加1条数据

				PagerIndex_all = 1;
			}
			
			break;
		case 2:
			onLoad(1);
			onLoad(3);
			if (getpager==1) {
				PagerIndex_my = +1;
			}else {
				PagerIndex_my = 1;
			}			
			break;
		case 3:
			onLoad(1);
			onLoad(2);
			if (getpager==1) {				
				PagerIndex_department += 1;
			}else {
				PagerIndex_department = 1;
			}			
			break;

		default:
			break;
		}
	}
		
		
	/**
	 * 下拉刷新
	 */
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		stopLoad(view_index,0);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				onLoad(view_index);
			}
		}, 2000);//2s后执行onLoad（）方法
		
		//不必要传入参数默认为0传入
		setData(1, view_index, 0);
		
	}

	/**
	 * 上拉加载
	 */
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		stopLoad(view_index,1);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				onLoad(view_index);
			}
		}, 2000);
		setData(2, 0, view_index);
	}
	
}
