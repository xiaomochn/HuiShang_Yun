package com.huishangyun.Channel.Plan;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Members;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Fragment.TimeFragment;
import com.huishangyun.Fragment.TimeFragment.TimeFace;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.model.Methods;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.yun.R;

public class PlanDetailActivity extends BaseActivity implements TimeFace{
	protected static final String TAG = null;
	private ListView mListView;
	private LinearLayout back;// 返回
	private ImageView rewrite;// 编辑
	private TextView date;//日期
	private LinearLayout yesterday;//前一天
	private LinearLayout tomorrow;//后一天
	private MyAdapte adapter;
	private int ID;
	private PlanList plandetail;
	private List<PlanList> Tlist = new ArrayList<PlanList>();
	private PlanList Tslist;
	private List<PlanList> list = new ArrayList<PlanList>();
	Members aList = new Members();//获取个人数据数据

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitty_plan_detail);
		Intent intent = getIntent();
		ID = intent.getIntExtra("ID", 0);
		Log.e(TAG, "ID:" + ID);
		init();
		getNetData1();
		
		
	}

	/**
	 * 布局控件实例化
	 */
	private void init() {
		back = (LinearLayout) findViewById(R.id.back);
		rewrite = (ImageView) findViewById(R.id.rewrite);
		mListView = (ListView)findViewById(R.id.customers_listview);
		date = (TextView)findViewById(R.id.create_plan_time);
		yesterday = (LinearLayout)findViewById(R.id.yesterday);
		tomorrow = (LinearLayout)findViewById(R.id.tomorrow);
		adapter = new MyAdapte(this);
		mListView.setAdapter(adapter);
		
		Log.e(TAG,"----+1");
		
		back.setOnClickListener(new ButtonClickListener());
		date.setOnClickListener(new ButtonClickListener());
		rewrite.setOnClickListener(new ButtonClickListener());
		yesterday.setOnClickListener(new ButtonClickListener());
		tomorrow .setOnClickListener(new ButtonClickListener());
	

	}
	
	/**
	 * 单击事件处理
	 * @author xsl
	 *
	 */
	public class ButtonClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.create_plan_time:
				TimeFragment timeFragment = new TimeFragment();
				timeFragment.setIndex(PlanDetailActivity.this, 0);
				timeFragment.show(getSupportFragmentManager(), "choose time");
				break;
			case R.id.yesterday:
				
				try {
					String s1 = date.getText().toString();
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					Date d1 = df.parse(Tslist.getStartDate());
					Date d2 = df.parse(getDateStr(s1, -1));
					long diff = d2.getTime() - d1.getTime();
					long days = diff / (1000 * 60 * 60 * 24);
					Log.e(TAG, "days:" + days);
					if (days >= 0) {
						date.setText(getDateStr(s1, -1));
						getNetData2(date.getText().toString(), 1, 10);
					 }else {
						//提示
						 showDialog("该日期已是计划开始日期！");
					}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				break;
			case R.id.tomorrow:
				//比较日期大小然后提示
				try {
				String s1 = date.getText().toString();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date d1 = df.parse(Tslist.getEndDate());
				Date d2 = df.parse(getDateStr(s1, 1));
				long diff = d1.getTime() - d2.getTime();
				long days = diff / (1000 * 60 * 60 * 24);
				Log.e(TAG, "days:" + days);
				if (days >= 0) {
					date.setText(getDateStr(s1, 1));
					getNetData2(date.getText().toString(), 1, 10);
				 }else {
					 showDialog("该日期已是计划结束日期！");
				}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			
				break;
			case R.id.rewrite:
				//跳转到创建计划界面
				Intent intent = new Intent(PlanDetailActivity.this, CreatePlanActivity.class);
				intent.putExtra("ID", ID);
				startActivity(intent);
				break;
			default:
				break;
			}
			
		}

		
	}
	
	
	  /** 获取指定日后 后 dayAddNum 天的 日期  
	    * @param day  日期，格式为String："2013-9-3";  
	    * @param dayAddNum 增加天数 格式为int;  
	     * @return  
	     */  
	   public static String getDateStr(String day,int dayAddNum) {  
	       SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
	       Date nowDate = null;  
	       try {  
	           nowDate = df.parse(day);  
	        } catch (ParseException e) {  
	            e.printStackTrace();  
	       }  
	        Date newDate2 = new Date(nowDate.getTime() + dayAddNum * 24 * 60 * 60 * 1000);  
	        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");  
	        String dateOk = simpleDateFormat.format(newDate2);  
	        return dateOk;  
	    }  


 	
 	
 	 /**
 		 * 获取计划历史数据
 		 * @param ID
 		 */
 		private void getNetData1(){
 			new Thread(){
 				@Override
 				public void run() {
 					// TODO Auto-generated method stub	
 					String yyjjString = getJson1();
 					Log.e(TAG, "yyjjString:" + yyjjString);
 					String result = DataUtil.callWebService(
 							Methods.PLAN_DETAIL,
 							getJson1());				
 					Log.e(TAG,"result----:" + result);
 					//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
 					if (result != null) {
 						// 获取对象的Type
 						Type type = new TypeToken<ZJResponse<PlanList>>() {
 						}.getType();
 						ZJResponse<PlanList> zjResponse = JsonUtil.fromJson(result,
 								type);
 						// 获取对象列表
 						/*ItemLists = zjResponse.getResult();*/
 						Tslist = zjResponse.getResult();						
 						mHandler.sendEmptyMessage(2);
 					} else {
 						mHandler.sendEmptyMessage(2);
 						
 					}
 					
 				}
 			}.start();
 		}
 		
 		/**
 		 * json对象
 		 * @param ID 计划id
 		 * @return
 		 */
 		private String getJson1() {
 			
 			ZJRequest zjRequest1 = new ZJRequest();
// 			zjRequest1.setID(plandetail.getID());
 			zjRequest1.setID(ID);
 			return JsonUtil.toJson(zjRequest1);

 		}
 	
 	 /**
 	 * 获取客户数据数据
 	 * @param ID
 	 */
 	private void getNetData2( final String OperationTime,final int PageIndex,final int PageSize){
 		new Thread(){
 			@Override
 			public void run() {
 				// TODO Auto-generated method stub	
 			
 				String result = DataUtil.callWebService(
 						Methods.PLAN_CUSTOMERS,
 						getJson2(OperationTime,PageIndex,PageSize));				
 				L.e("result----:" + getJson2(OperationTime, PageIndex, PageSize));
 				L.e("result----:" + result);
 				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
 				if (result != null) {
 					// 获取对象的Type
 					Type type = new TypeToken<ZJResponse<PlanList>>() {
 					}.getType();
 					ZJResponse<PlanList> zjResponse = JsonUtil.fromJson(result,
 							type);
 					// 获取对象列表
 					/*ItemLists = zjResponse.getResult();*/
 					Tlist = zjResponse.getResults();
 					mHandler.sendEmptyMessage(1);
 				} else {
                 L.e("没有客户数据！");
 					
 				}
 				
 			}
 		}.start();
 	}
 	
 	/**
 	 * 设置json对象
 	 * @param ID 详情id
 	 * @return
 	 */
 	private String getJson2(String OperationTime,int PageIndex,int PageSize) {
 		ZJRequest zjRequest = new ZJRequest();
 		zjRequest.setPlanID(ID);
 		zjRequest.setPageIndex(PageIndex);
 		zjRequest.setPageSize(PageSize);
 		zjRequest.setBelongDate(OperationTime);
 		return JsonUtil.toJson(zjRequest);

 	}
 	
 	public Handler mHandler = new Handler(){
 		public void handleMessage(android.os.Message msg) {
 			super.handleMessage(msg);
 			switch (msg.what) {
			case 1:
				list.clear();
				for ( int i = 0; i < Tlist.size(); i++) {
					list.add(Tlist.get(i));
				}
				Log.e(TAG, "list.size:" + list.size());
				Log.e(TAG, "item.size:" + Tlist.size());
				
				adapter.notifyDataSetChanged();
				
				break;
			case 2:
				date.setText(backDate(Tslist.getStartDate()));
				getNetData2(date.getText().toString(), 1, 10);
				enbleRewrite();
				break;

			default:
				break;
			}
 		};
 	};
 	
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
		String datesString = a + "-" + b + "-" + c ;
		
		return datesString;		
	}
	
	
	/**
	 * 自定义适配器
	 * @author xsl
	 *
	 */
	public class MyAdapte extends BaseAdapter{
		
		private LayoutInflater mInflater;// 动态布局映射

		public MyAdapte(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

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
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.activity_plan_customers_listview_item, parent,
						false);
				holder.view_left = (View) convertView
						.findViewById(R.id.item_left);
				holder.company_name = (TextView) convertView
						.findViewById(R.id.company_name);
				holder.company_address = (TextView) convertView
						.findViewById(R.id.company_adress);

				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			
			getMenber(list.get(position).getMember_ID());
			try {
				holder.company_name.setText(aList.getRealName());
				holder.company_address.setText(aList.getAddress());
			} catch (Exception e) {
				// TODO: handle exception
				holder.company_name.setText(list.get(position).getMember_Name());
				holder.company_address.setText(list.get(position).getAddress());
			}
			
			return convertView;
		}
		
	}
	static class ViewHolder {
		View view_left;
		View view_right;
		// 标题
		private TextView title;
		// 公司名称
		private TextView company_name;
		// 公司地址
		private TextView company_address;
		// 删除按钮
		private TextView delete;

	}
	
	//获取所有客户信息
	private void getMenber(int Member_ID){
		aList = MemberManager.getInstance(this).getMembers(Member_ID);
	}

	@Override
	public void chooseTime(String time, int type, long timeStamp) {
		// TODO Auto-generated method stub
		try {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d1 = df.parse(Tslist.getStartDate());
		Date d2 = df.parse(Tslist.getEndDate());
		Date d3 = df.parse(time);
		long diff1 = d3.getTime() - d1.getTime();
		long diff2 = d2.getTime() - d3.getTime();
		long days1 = diff1 / (1000 * 60 * 60 * 24);
		long days2 = diff2/ (1000 * 60 * 60 * 24);
		if (days1 >= 0 && days2 >= 0) {
			date.setText(time);
			getNetData2(date.getText().toString(), 1, 10);
		}else {
			showDialog("您设定的日期不在计划日期内！");
//			Toast.makeText(PlanDetailActivity.this, "您设定的日期不在计划日期内！", Toast.LENGTH_SHORT).show();
		}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void showDialog(String TXT){
		new ClueCustomToast().showToast(PlanDetailActivity.this,
				R.drawable.toast_warn, TXT);

	}
	
	/**
	 * 判断计划是否可以修改
	 */
	private void enbleRewrite(){
		try {
			// 如果日期已经开始 执行则不能修改
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			Date date1 = new Date(year - 1900, month, day); // 获取时间转换为Date对象
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			String time = sf.format(date1);
			Date d1 = sf.parse(backDate(Tslist.getStartDate()));
			Date d2 = sf.parse(time);
			long diff = d1.getTime() - d2.getTime();
			long days = diff / (1000 * 60 * 60 * 24);
			if (days > 0) {
				rewrite.setVisibility(View.VISIBLE);
			} else {
				rewrite.setVisibility(View.INVISIBLE);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	
	}
}
