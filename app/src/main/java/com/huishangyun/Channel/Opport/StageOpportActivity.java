package com.huishangyun.Channel.Opport;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.PaintDrawable;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

public class StageOpportActivity extends BaseActivity {
	protected static final String TAG = null;
	private LinearLayout back;// 返回键
	private LinearLayout search_opport;// 商机搜索
	private LinearLayout create_opport;// 创建商机
	private LinearLayout more_option;// 更多商机
	private RelativeLayout top_bar;//顶部条
	private LinearLayout search_bar;//搜索条
	private RelativeLayout rl_canvers;//半透明层
	private EditText ed_text;//搜索框
	PopupWindow mWindow;//popupwindows
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
	ListView all_opport, my_opport, department_opport;//三选项卡listview
	List<CreateBusinses> ItemLists = new ArrayList<CreateBusinses>();
	List<CreateBusinses> List = new ArrayList<CreateBusinses>();
	StageListViewAdapter adapter;//阶段listview适配器
	private LinearLayout linearLayout1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opport_mian);
		
		init();
		viewpagerinit();
		initViewPager();
		initPoptWindow();
		getNetData();
	}

	/**
	 * 页面控件实例化
	 */
	private void init() {
		linearLayout1 = (LinearLayout)findViewById(R.id.linearLayout1);
		linearLayout1.setVisibility(View.GONE);
        //头部组件
		top_bar = (RelativeLayout) findViewById(R.id.top_bar);
		back = (LinearLayout) findViewById(R.id.back);
		search_opport = (LinearLayout) findViewById(R.id.search_opport);
		search_opport.setVisibility(View.GONE);
		create_opport = (LinearLayout) findViewById(R.id.create_opport);
		more_option = (LinearLayout) findViewById(R.id.more_option);
		
	
		
		//viewpager页卡名称组件
		textView1 = (TextView) findViewById(R.id.tv1);
		textView2 = (TextView) findViewById(R.id.tv2);
		textView3 = (TextView) findViewById(R.id.tv3);
		
        //对应单击事件
		back.setOnClickListener(new ButtonClickListener());
//		search_opport.setOnClickListener(new ButtonClickListener());
		create_opport.setOnClickListener(new ButtonClickListener());
		more_option.setOnClickListener(new ButtonClickListener());

		
	}

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
			case R.id.back://返回主界面
				finish();
				Intent intent = new Intent(StageOpportActivity.this,MainOpportActivity.class);
				startActivity(intent);
				break;

			case R.id.create_opport:
				//跳转到创建商机页面
				Intent createintent = new Intent(StageOpportActivity.this,
						CreateOpportActivity.class);
				startActivity(createintent);

				break;
				
                   
//				break;
			case R.id.more_option:
				//弹出popupwindow
                mWindow.showAsDropDown(more_option);
				break;
				
				//popupwindow单击事件
			case R.id.opport_pop_stage://本页面
				mWindow.dismiss();
				
				break;

			case R.id.opport_pop_time://跳转到时间页面
				mWindow.dismiss();
				finish();
				Intent intent1 = new Intent(StageOpportActivity.this,TimeOpportActivity.class);
				startActivity(intent1);
				break;
				
			case R.id.opport_pop_priority://跳转到金额页面
				mWindow.dismiss();
				finish();
				Intent intent2 = new Intent(StageOpportActivity.this,
						MoneyOpportActivity.class);
				startActivity(intent2);
				break;
				
			default:
				break;
			}

		}

	}
	
	/**
 	 * 获取详情数据
 	 * @param ID
 	 */
 	private void getNetData(){
 		new Thread(){
 			@Override
 			public void run() {
 				// TODO Auto-generated method stub	
 			
 				String result = DataUtil.callWebService(
 						Methods.SUPPLY_STAGE,
 						getJson());				
 				Log.e(TAG,"result:" + getJson());
 				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
 				if (result != null) {
 					// 获取对象的Type
 					Type type = new TypeToken<ZJResponse<CreateBusinses>>() {
 					}.getType();
 					ZJResponse<CreateBusinses> zjResponse = JsonUtil.fromJson(result,
 							type);
 					// 获取对象列表
 					/*ItemLists = zjResponse.getResult();*/
 					ItemLists = zjResponse.getResults();
 					mHandler.sendEmptyMessage(1);
 				} else {
 				
 					
 				}
 				
 			}
 		}.start();
 	}
 	
 	/**
 	 * 设置json对象
 	 * @return
 	 */
 	private String getJson() {
 		ZJRequest zjRequest = new ZJRequest();
 		zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 1016));
 		zjRequest.setKeywords("SalesStage");
 		zjRequest.setDepartment_ID(preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID,0));
 		return JsonUtil.toJson(zjRequest);

 	}
 	
	/**
	 * 创建PopuptWindow对象
	 */
	private void initPoptWindow(){
		//加载布局
		View mPopView = mInflater.inflate(R.layout.activity_apport_popuwindow, null);
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
		LinearLayout stage = (LinearLayout) mPopView.findViewById(R.id.opport_pop_stage);
		LinearLayout time = (LinearLayout) mPopView.findViewById(R.id.opport_pop_time);
		LinearLayout priority = (LinearLayout) mPopView.findViewById(R.id.opport_pop_priority);
		
		//设置点击时间监听
		stage.setOnClickListener(new ButtonClickListener());
		time.setOnClickListener(new ButtonClickListener());
		priority.setOnClickListener(new ButtonClickListener());
		
	}

	/**
	 * viewpager实例化各组件
	 */
	private void viewpagerinit() {
		
		// ViewPager组件
		lineImg = (ImageView) findViewById(R.id.cursor);
		lineImg.setVisibility(View.GONE);
		vPager = (ViewPager) findViewById(R.id.vPager);
		pagerList = new ArrayList<View>();
		mInflater = getLayoutInflater().from(StageOpportActivity.this);

		// 获取布局对象
		view1 = mInflater.inflate(R.layout.activity_opport_morelistview, null);
		view2 = mInflater.inflate(R.layout.activity_clue_mian_my, null);
		view3 = mInflater.inflate(R.layout.activity_clue_main_department, null);
		
		all_opport = (ListView) view1.findViewById(R.id.all_listview);
		adapter = new StageListViewAdapter(this);
		all_opport.setAdapter(adapter);

		
		// 将布局添加到容器
		pagerList.add(view1);
		pagerList.add(view2);
		pagerList.add(view3);
	}
	
	
	/**
	 * 设置ViewPager
	 */
	private void initViewPager() {
		
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.line_task).getWidth();// 获取图片宽度
		dm = new DisplayMetrics();
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
			if (currIndex == 1) {//判断当前选中项
				animation = new TranslateAnimation(one, 0, 0, 0);
			} else if (currIndex == 2) {
				animation = new TranslateAnimation(two, 0, 0, 0);

			}
			textView1.setTextColor(Color.parseColor("#21a5de"));
			textView2.setTextColor(Color.parseColor("#646464"));
			textView3.setTextColor(Color.parseColor("#646464"));
			break;
		case 1:
			if (currIndex == 0) {
				animation = new TranslateAnimation(offset, one, 0, 0);
			} else if (currIndex == 2) {
				animation = new TranslateAnimation(two, one, 0, 0);
			}
			textView1.setTextColor(Color.parseColor("#646464"));
			textView2.setTextColor(Color.parseColor("#21a5de"));
			textView3.setTextColor(Color.parseColor("#646464"));
			break;
		case 2:
			if (currIndex == 0) {
				animation = new TranslateAnimation(offset, two, 0, 0);
			} else if (currIndex == 1) {
				animation = new TranslateAnimation(one, two, 0, 0);
			}
			textView1.setTextColor(Color.parseColor("#646464"));
			textView2.setTextColor(Color.parseColor("#646464"));
			textView3.setTextColor(Color.parseColor("#21a5de"));
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
//			return mListViews.size();
			return 1;
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
	 * 自定义--阶段--listview适配器
	 */
	public class StageListViewAdapter extends BaseAdapter {

		private LayoutInflater mInflater;// 动态布局映射

		public StageListViewAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		// 决定listview显示条数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return List.size();

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
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolderStage holder; 
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.activity_opport_stage_listview_item, null);// 根据布局文件实例化view
				
				holder = new ViewHolderStage();
				holder.item = (LinearLayout) convertView.findViewById(R.id.item);
				holder.stage = (TextView) convertView
						.findViewById(R.id.stage);// 找某个控件 --阶段名称
				holder.view = (View) convertView.findViewById(R.id.view);
				holder.number = (TextView) convertView.findViewById(R.id.number);// 找某个控件--此阶段数量				
				holder.stage_img = (ImageView) convertView.findViewById(R.id.stage_img);//阶段图标	
				convertView.setTag(holder);

			} else {
				
				holder = (ViewHolderStage) convertView.getTag();
				
			}
			
			if (position==0) {
				holder.view.setVisibility(View.VISIBLE);
			}else {
				holder.view.setVisibility(View.GONE);
			}
			
			holder.stage.setText(List.get(position).getTitle());
			holder.number.setText(List.get(position).getCo() + "");			
			holder.stage_img.setBackgroundResource(R.drawable.opport_stage_group);
			
			holder.item.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(StageOpportActivity.this, MainOpportActivity.class);
					intent.putExtra("index", 1);
					Log.e(TAG, "=====>" + List.get(position).getTitle());
					intent.putExtra("Class_ID", stageChange(List.get(position).getTitle()));
					startActivity(intent);
					finish();
				}
			}); 
			
			
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
    static class ViewHolderStage { 
         //阶段名称
        private TextView stage; 
        //此阶段条数
        private TextView number; 
   
        //阶段图标
        private ImageView stage_img;
        //item
        private LinearLayout item;
        //item头部
        private View view;
        }
    
    /**
     * 更新数据列表
     */
	public Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				
				break;
            case 1:
            	List.clear();
				for ( int i = 0; i < ItemLists.size(); i++) {
					List.add(ItemLists.get(i));
				}
				Log.e(TAG, "list.size:" + List.size());
				Log.e(TAG, "item.size:" + ItemLists.size());
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};
	
	private int stageChange(String data){
		int index = 0 ;
		if (data.equals("初期沟通")) {
			index = 1;
		}else if (data.equals("立项评估")) {
			index = 2;
		}else if (data.equals("需求分析")) {
			index = 3;
		}else if (data.equals("方案制定")) {
			index = 4;
		}else if (data.equals("招投标/竞争")) {
			index = 5;
		}else if (data.equals("商务谈判")) {
			index = 6;
		}else if (data.equals("合同签约")) {
			index = 7;
		}
		return index;
	}

}
