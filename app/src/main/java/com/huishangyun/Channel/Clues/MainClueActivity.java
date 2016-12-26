package com.huishangyun.Channel.Clues;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.Methods;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.swipelistview.MyXListView.MyXListViewListener;
import com.huishangyun.yun.R;

/**
 *  线索主页
 * @author  xsl
 *
 */
public class MainClueActivity extends BaseActivity implements MyXListViewListener {

	protected static final String TAG = null;
	private ViewPager viewPager;//viewpager
	private ImageView imageView;//顶部滚动条--图片横线
	private List<View> lists = new ArrayList<View>();
	private MyAdapter myAdapter;//viewpager适配器
	private Bitmap cursor;
	private int offSet;
	private int currentItem;
	private Matrix matrix = new Matrix();
	private int bmWidth;//宽度
	private Animation animation;
	private TextView tv1;//全部栏
	private TextView tv2;//我的线索栏
	private TextView tv3;//部门线索栏
	private RelativeLayout search;
	private RelativeLayout addclue;// 新增线索按钮
	private MyXListView all_listview;// 全部栏目listview
	private MyXListView my_listview;// 我的线索栏目listview
	private MyXListView department_listview;// 部门线索栏目listview
	View convertView, convertView1, convertView2, convertView3, convertView4; // 新建一个view，用于寻找三个viewpager布局
	private RelativeLayout back_mian;//返回九宫格按钮
	RelativeLayout head_bar;//头栏
	private int view_index = 1;//定义一个数来确认当天viewpager在那一页
	CharSequence filterContent;//获取搜索编辑框文本内容
	
	private int pager_size_all = 10, pager_size_my = 10, pager_size_dp = 10;// 单页显示的条数
	private int pager_size_all_index = 1, pager_size_my_index = 1, pager_size_dp_index = 1;// 单页显示的条数
	
	//定义三个二维数组用于存储，全部线索、我的线索，部门线索
	List<Clue> ItemLists = new ArrayList<Clue>();
	List<Clue> list = new ArrayList<Clue>();
	List<Clue> ItemLists1 = new ArrayList<Clue>();
	List<Clue> list1 = new ArrayList<Clue>();
	List<Clue> ItemLists2 = new ArrayList<Clue>();
	List<Clue> list2 = new ArrayList<Clue>();
	//定义三个listview适配器
	MyListViewAdapter adapter;
	MyListViewAdapter1 adapter1;
	MyListViewAdapter2 adapter2;	
	private ImageView all_no_information;
	private ImageView my_no_information;
	private ImageView department_no_information;
	
	/**
	 * 获取手机屏幕分辨率的类和手机的宽高
	 */
	private DisplayMetrics dm; 
	private int widths, heights;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clue_main);		

		init();
		initeCursor();
		viewPagerOnClick();	
		MyApplication.getInstance().showDialog(MainClueActivity.this, true, "Loading...");
		// 创建三个适配器
		 adapter = new MyListViewAdapter(this);
		 adapter1 = new MyListViewAdapter1(this);
		 adapter2 = new MyListViewAdapter2(this);
		 
	     all_listview.setAdapter(adapter);
		 all_listview.setPullLoadEnable(true);
		 all_listview.setMyXListViewListener(this);
		 
		 my_listview.setAdapter(adapter1);
		 my_listview.setPullLoadEnable(true);
		 my_listview.setMyXListViewListener(this);
		 
		 department_listview.setAdapter(adapter2);
		 department_listview.setPullLoadEnable(true);
		 department_listview.setMyXListViewListener(this);
		setData(0, 0, 0);		
		itemOnClickListener();

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
		
				
				 
		// 用户编号
		int uid = Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID,
				"0"));
		// 所属部门id
		int department_id = preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID,
				0);
		
			
			switch (refresh_index) {
			case 0:
				
				//获取全部线索
				getNetData(preferences.getInt(Content.COMPS_ID, 1016), 0, department_id, "", 1, pager_size_all,1);
				
				
				//获取我的线索
				getNetData(preferences.getInt(Content.COMPS_ID, 1016), uid, 0, "", 1, pager_size_my, 2);
				
				
				//获取部门线索
				getNetData(preferences.getInt(Content.COMPS_ID, 1016), 0, department_id, "", 1, pager_size_dp, 3);
				
				break;
				
		case 1:
			//下拉刷新处理
			if (onload_index==1) {
				//全部线索刷新
				getNetData(preferences.getInt(Content.COMPS_ID, 1016), 0, department_id, "", 1, pager_size_all,1);
				L.e("刷新全部");
			}else if (onload_index==2) {
				//我的线索刷新
				getNetData(preferences.getInt(Content.COMPS_ID, 1016), uid, 0, "", 1, pager_size_my, 2);
			}else if (onload_index==3) {
				//部门线索刷新
				getNetData(preferences.getInt(Content.COMPS_ID, 1016), 0, department_id, "", 1, pager_size_dp, 3);
			}
			
			break;
			
			//上拉加载处理
			case 2:
				
				if (loadmore_index==1) {
					Log.e(TAG, "onload_index:" + onload_index);
					//全部线索刷新
					getNetData(preferences.getInt(Content.COMPS_ID, 1016), 0, department_id, "",pager_size_all_index, pager_size_all,4);
				}else if (loadmore_index==2) {
					//我的线索刷新
					getNetData(preferences.getInt(Content.COMPS_ID, 1016), uid, 0, "", pager_size_my_index, pager_size_my,5);
				}else if (loadmore_index==3) {
					//部门线索刷新
					getNetData(preferences.getInt(Content.COMPS_ID, 1016), 0, department_id, "", pager_size_dp_index, pager_size_dp,6);
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
				String result = DataUtil.callWebService(
						Methods.CLUE_LIST,
						getJson(Company_ID, Manager_ID, Department_ID,
								keywords, pageIndex,pageSize));				
				Log.e(TAG, "result:" + result+index);
				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					// 获取对象的Type
					Type type = new TypeToken<ZJResponse<Clue>>() {
					}.getType();
					ZJResponse<Clue> zjResponse = JsonUtil.fromJson(result,
							type);
					// 获取对象列表
					// 用于下拉刷新或者刚进来时加载数据，需要清除存储的数据
					switch (index) {
					case 1:
						ItemLists.clear();
						ItemLists = zjResponse.getResults();
						//通知listview改变
						handler.sendEmptyMessage(1);
						break;
					case 2:
						ItemLists1.clear();
						ItemLists1 = zjResponse.getResults();
						handler.sendEmptyMessage(2);
						break;
					case 3:
						ItemLists2.clear();
						ItemLists2 = zjResponse.getResults();
						handler.sendEmptyMessage(3);
						break;

					// 用于上拉加载或者，搜索后结果很多后的加载，此时不需要清空列表
					case 4:
						ItemLists = zjResponse.getResults();
						handler.sendEmptyMessage(5);
						break;
					case 5:
						ItemLists1 = zjResponse.getResults();
						handler.sendEmptyMessage(6);
						break;
					case 6:
						ItemLists2 = zjResponse.getResults();
						handler.sendEmptyMessage(7);
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
	 * 布局控件加载，初始化布局
	 */
	private void init() {
		imageView = (ImageView) findViewById(R.id.cursor);
		tv1 = (TextView) findViewById(R.id.tv1);
		tv2 = (TextView) findViewById(R.id.tv2);
		tv3 = (TextView) findViewById(R.id.tv3);
		viewPager = (ViewPager) findViewById(R.id.vPager);
		search = (RelativeLayout) findViewById(R.id.search);
		addclue = (RelativeLayout) findViewById(R.id.addclue);
		// sos = (LinearLayout)findViewById(R.id.sos);
		head_bar = (RelativeLayout) findViewById(R.id.head_bar);
		back_mian =(RelativeLayout) findViewById(R.id.back_mian);

		// 先找到布局，然后往布局里加载listview控件
		convertView = LayoutInflater.from(MainClueActivity.this).inflate(
				R.layout.activity_clue_mian_all, null);
		all_listview = (MyXListView) convertView.findViewById(R.id.all_listview);
		all_no_information = (ImageView) convertView.findViewById(R.id.no_information);

		convertView1 = LayoutInflater.from(MainClueActivity.this).inflate(
				R.layout.activity_clue_mian_all, null);
		my_listview = (MyXListView) convertView1.findViewById(R.id.all_listview);
		my_no_information = (ImageView) convertView1.findViewById(R.id.no_information);
		
		convertView2 = LayoutInflater.from(MainClueActivity.this).inflate(
				R.layout.activity_clue_mian_all, null);
		department_listview = (MyXListView) convertView2
				.findViewById(R.id.all_listview);
		department_no_information = (ImageView) convertView2.findViewById(R.id.no_information);
		
		convertView3 = LayoutInflater.from(this).inflate(
				R.layout.activity_clue_main_search, null);

		lists.add(convertView);
		lists.add(convertView1);
		lists.add(convertView2);
		tv1.setOnClickListener(new ButtonClickListener());
		tv2.setOnClickListener(new ButtonClickListener());
		tv3.setOnClickListener(new ButtonClickListener());
		search.setOnClickListener(new ButtonClickListener());
		addclue.setOnClickListener(new ButtonClickListener());
		back_mian.setOnClickListener(new ButtonClickListener());

	}
	
	/**
	 * listview的item单击事件
	 */
	private void itemOnClickListener() {
		// 全部listview单击事件
		all_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainClueActivity.this,
						DetailClueActivity.class);
				// 将integer转int
				int int_id = (list.get(position - 1).getID()).intValue();
				// 传值
				intent.putExtra("ID", int_id + "");
				startActivity(intent);
			}
		});

		// 我的线索listview单击事件
		my_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainClueActivity.this,
						DetailClueActivity.class);
				
				// 将integer转int
				int int_id = (list1.get(position - 1).getID()).intValue();
				// 传值
				intent.putExtra("ID", int_id + "");

				startActivity(intent);
			}

		});

		// 部门线索listview单击事件
		department_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainClueActivity.this,
						DetailClueActivity.class);
				// 将integer转int
				int int_id = (list2.get(position - 1).getID()).intValue();
				// 传值
				intent.putExtra("ID", int_id + "");
			
				startActivity(intent);
			}

		});
	}

	/**
	 * 单击事件处理
	 * 
	 * @author Administrator
	 * 
	 */
	public class ButtonClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.tv1:
				viewPager.setCurrentItem(0);
				tv2.setTextColor(Color.parseColor("#646464"));
				tv3.setTextColor(Color.parseColor("#646464"));
				tv1.setTextColor(Color.parseColor("#00658f"));
				break;

			case R.id.tv2:
				viewPager.setCurrentItem(1);
				tv1.setTextColor(Color.parseColor("#646464"));
				tv3.setTextColor(Color.parseColor("#646464"));
				tv2.setTextColor(Color.parseColor("#00658f"));
				break;

			case R.id.tv3:
				viewPager.setCurrentItem(2);
				tv1.setTextColor(Color.parseColor("#646464"));
				tv2.setTextColor(Color.parseColor("#646464"));
				tv3.setTextColor(Color.parseColor("#00658f"));
				break;
			case R.id.search://跳转搜索界面
				Intent intent = new Intent(MainClueActivity.this, ClueSearchActivity.class);
				intent.putExtra("index", 0);
				startActivity(intent);
				break;

			case R.id.addclue://跳转创建线索界面
				Intent intent1 = new Intent(MainClueActivity.this,
						NewClueActivity.class);
				startActivityForResult(intent1, 0);
				break;
				
				//返回九宫格按钮
			case R.id.back_mian:
				finish();
			
				break;
				

			default:
				break;
			}
		}
	}

	
	


	/**
	 * viewpager顶行滚动条处理
	 */
	private void initeCursor() {
		cursor = BitmapFactory.decodeResource(getResources(), R.drawable.line_task);
		imageView.setBackgroundColor(0xff00658f);
		bmWidth = cursor.getWidth();
		DisplayMetrics dm;
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		widths = dm.widthPixels;
		heights = dm.heightPixels;
		offSet = (widths / 3 - bmWidth) / 2;
//		matrix.setTranslate(offSet, 0);
		matrix.postTranslate(offSet, 0);
		imageView.setImageMatrix(matrix); // 需要iamgeView的scaleType为matrix
		currentItem = 0;
		
	}

	/**
	 * viewpager单击事件
	 */
	private void viewPagerOnClick() {
		myAdapter = new MyAdapter(lists);
		viewPager.setAdapter(myAdapter);
		viewPager.setOffscreenPageLimit(3);
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) { // 当滑动式，顶部的imageView是通过animation缓慢的滑动
				// TODO Auto-generated method stub
				switch (arg0) {
				case 0:
					view_index = 1;
					if (currentItem == 1) {
						animation = new TranslateAnimation(
								offSet * 2 + bmWidth, 0, 0, 0);
					} else if (currentItem == 2) {
						animation = new TranslateAnimation(offSet * 4 + 2
								* bmWidth, 0, 0, 0);
					}
					tv2.setTextColor(Color.parseColor("#646464"));
					tv3.setTextColor(Color.parseColor("#646464"));
					tv1.setTextColor(Color.parseColor("#00658f"));
					break;
				case 1:
					view_index = 2;
					if (currentItem == 0) {
						animation = new TranslateAnimation(0, offSet * 2
								+ bmWidth, 0, 0);
					} else if (currentItem == 2) {
						animation = new TranslateAnimation(1 * offSet + 2
								* bmWidth, offSet * 2 + bmWidth, 0, 0);
					}
					tv1.setTextColor(Color.parseColor("#646464"));
					tv3.setTextColor(Color.parseColor("#646464"));
					tv2.setTextColor(Color.parseColor("#00658f"));
					break;
				case 2:
					view_index = 3;
					if (currentItem == 0) {
						animation = new TranslateAnimation(0, 4 * offSet + 2
								* bmWidth, 0, 0);
					} else if (currentItem == 1) {
						animation = new TranslateAnimation(
								offSet * 2 + bmWidth, 4 * offSet + 2 * bmWidth,
								0, 0);
					}
					tv1.setTextColor(Color.parseColor("#646464"));
					tv2.setTextColor(Color.parseColor("#646464"));
					tv3.setTextColor(Color.parseColor("#00658f"));
				}
				currentItem = arg0;
				animation.setDuration(200);
				animation.setFillAfter(true);
				imageView.startAnimation(animation);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * viewpager适配器
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyAdapter extends PagerAdapter {

		List<View> viewLists;

		public MyAdapter(List<View> lists) {
			viewLists = lists;
		}

		@Override
		public int getCount() { // 获得size
			// TODO Auto-generated method stub
			return viewLists.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View view, int position, Object object) // 销毁Item
		{
			((ViewPager) view).removeView(viewLists.get(position));
		}

		@Override
		public Object instantiateItem(View view, int position) // 实例化Item
		{
			((ViewPager) view).addView(viewLists.get(position), 0);
			return viewLists.get(position);
		}

	}

	/**
	 * 自定义--全部线索--listview适配器
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
			return list.size();
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
				
				holder.theme_name.setText(list.get(position).getNote());
				//对状态值进行中文设定和颜色处理
				int i = Integer.parseInt(list.get(position).getStatus());
				switch (i) {
				case 1:
					holder.state.setTextColor(0xffe60000);
					holder.state.setText("未处理");
					break;
				case 2:
					holder.state.setTextColor(0xff21a5de);
					holder.state.setText("转商机");
					break;
				case 3:
					holder.state.setTextColor(0xff969696);
					holder.state.setText("已关闭");
					break;
				default:
					break;
				}
		
		    if (!list.get(position).getCompany().equals("")) {
				holder.ps_name.setText(list.get(position).getCompany());
			}else {
				holder.ps_name.setText(list.get(position).getName());
			}
				holder.date.setText(backDate(list.get(position).getAddDateTime()));
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
	 * 自定义--我的线索--listview适配器
	 */
	public class MyListViewAdapter1 extends BaseAdapter {

		private LayoutInflater mInflater;// 动态布局映射

		public MyListViewAdapter1(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		// 决定listview显示条数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list1.size();
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
				
				holder.theme_name.setText(list1.get(position).getNote());
				//对状态值进行中文设定和颜色处理
				int i = Integer.parseInt(list1.get(position).getStatus());
				switch (i) {
				case 1:
					holder.state.setTextColor(0xffe60000);
					holder.state.setText("未处理");
					break;
				case 2:
					holder.state.setTextColor(0xff21a5de);
					holder.state.setText("转商机");
					break;
				case 3:
					holder.state.setTextColor(0xff969696);
					holder.state.setText("已关闭");
					break;
				default:
					break;
				}
		
				if (!list1.get(position).getCompany().equals("")) {
					holder.ps_name.setText(list1.get(position).getCompany());
				}else {
					holder.ps_name.setText(list1.get(position).getName());
				}
				holder.date.setText(backDate(list1.get(position).getAddDateTime()));
				holder.iv.setBackgroundResource(R.drawable.contact_ps);
				holder.date_img.setBackgroundResource(R.drawable.datetime);	
				holder.date_img.setBackgroundResource(R.drawable.datetime);
			return convertView;
		}

	}

	/**
	 * 自定义--部门线索--listview适配器
	 */
	public class MyListViewAdapter2 extends BaseAdapter {

		private LayoutInflater mInflater;// 动态布局映射

		public MyListViewAdapter2(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		// 决定listview显示条数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list2.size();
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
				
				holder.theme_name.setText(list2.get(position).getNote());
				//对状态值进行中文设定和颜色处理
				int i = Integer.parseInt(list2.get(position).getStatus());
				switch (i) {
				case 1:
					holder.state.setTextColor(0xffe60000);
					holder.state.setText("未处理");
					break;
				case 2:
					holder.state.setTextColor(0xff21a5de);
					holder.state.setText("转商机");
					break;
				case 3:
					holder.state.setTextColor(0xff969696);
					holder.state.setText("已关闭");
					break;
				default:
					break;
				}
		
				if (!list2.get(position).getCompany().equals("")) {
					holder.ps_name.setText(list2.get(position).getCompany());
				}else {
					holder.ps_name.setText(list2.get(position).getName());
				}
				holder.date.setText(backDate(list2.get(position).getAddDateTime()));
				holder.iv.setBackgroundResource(R.drawable.contact_ps);
				holder.date_img.setBackgroundResource(R.drawable.datetime);	
			return convertView;
		}

	}
	
	/**
	 * 当搜索框输入有内容时更新listview列表 
	 */
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 3:
				Toast.makeText(MainClueActivity.this, "没有更多数据！", Toast.LENGTH_SHORT).show();
				break;
			case 4:
				dismissDialog();
				break;
			case 5:
				showDialog("正在加载......");
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * 实时更新listview列表
	 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				list.clear();
				for (int i = 0; i < ItemLists.size(); i++) {
					list.add(ItemLists.get(i));
				}
				if (list.size()==0) {
					all_no_information.setVisibility(View.VISIBLE);
				}else {
					all_no_information.setVisibility(View.GONE);
				}
				adapter.notifyDataSetChanged();
				MyApplication.getInstance().showDialog(MainClueActivity.this, false, "Loading...");
				break;
			case 2:
				list1.clear();
				for (int i = 0; i < ItemLists1.size(); i++) {
					list1.add(ItemLists1.get(i));
				}
				if (list1.size()==0) {
					my_no_information.setVisibility(View.VISIBLE);
				}else {
					my_no_information.setVisibility(View.GONE);
				}
				adapter1.notifyDataSetChanged();
//				handler.sendEmptyMessage(4);
				break;
			case 3:
				list2.clear();
				for (int i = 0; i < ItemLists2.size(); i++) {
					list2.add(ItemLists2.get(i));
				}
				if (list2.size()==0) {
					department_no_information.setVisibility(View.VISIBLE);
				}else {
					department_no_information.setVisibility(View.GONE);
				}
				adapter2.notifyDataSetChanged();
//				handler.sendEmptyMessage(4);
				break;
				
			case 4:
				dismissDialog();
				break;
				
			case 5:
				if (ItemLists.size()<=0) {
					new ClueCustomToast().showToast(MainClueActivity.this,
							R.drawable.toast_warn, "没有更多数据！");
				}else {
					for (int i = 0; i < ItemLists.size(); i++) {
						list.add(ItemLists.get(i));
					}
					adapter.notifyDataSetChanged();
				}
				
				break;
			case 6:
				if (ItemLists1.size()<=0) {
					new ClueCustomToast().showToast(MainClueActivity.this,
							R.drawable.toast_warn, "没有更多数据！");
				}else {
					for (int i = 0; i < ItemLists1.size(); i++) {
						list1.add(ItemLists1.get(i));
					}
					adapter.notifyDataSetChanged();
				}
				break;
				
			case 7:
				if (ItemLists2.size()<=0) {
					new ClueCustomToast().showToast(MainClueActivity.this,
							R.drawable.toast_warn, "没有更多数据！");
				}else {
					for (int i = 0; i < ItemLists2.size(); i++) {
						list2.add(ItemLists2.get(i));
					}
					adapter.notifyDataSetChanged();
				}
				break;
			case 8:
				MyApplication.getInstance().showDialog(MainClueActivity.this, false, "Loading...");
				new ClueCustomToast().showToast(MainClueActivity.this,
						R.drawable.toast_warn, "未获得任何数据，请检查网络是否连接！");
				break;
				
			default:
				break;
			}
		};
};
	
	/**
	 * 下拉刷新动作
	 * @param load_index
	 */
	private void onLoad(int load_index) {
		switch (load_index) {
		case 1:
			all_listview.stopRefresh();
			all_listview.stopLoadMore();
			all_listview.setRefreshTime();
			break;
		case 2:
			my_listview.stopRefresh();
			my_listview.stopLoadMore();
			my_listview.setRefreshTime();
			break;
		case 3:
			department_listview.stopRefresh();
			department_listview.stopLoadMore();
			department_listview.setRefreshTime();
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
//				pager_number_all += 1;
				//单页显示条数加1
//				pager_size_all +=10;
				pager_size_all_index +=1;
			}else {//刷新增加1条数据
//				pager_size_all +=1;
			}
			
			break;
		case 2:
			onLoad(1);
			onLoad(3);
			if (getpager==1) {
				pager_size_my +=10;
				pager_size_my_index +=1;
			}else {
//				pager_size_my +=1;
			}			
			break;
		case 3:
			onLoad(1);
			onLoad(2);
			if (getpager==1) {				
//				pager_size_dp +=10;
				pager_size_dp_index +=1;
			}else {
//				pager_size_dp +=1;
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
	 * 根据请求码或者返回码判断是否要刷新
	 */
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		if (arg1 == 1) {
			//自动刷新
			setData(0, 0, 0);
		}
		super.onActivityResult(arg0, arg1, arg2);
	}
}
