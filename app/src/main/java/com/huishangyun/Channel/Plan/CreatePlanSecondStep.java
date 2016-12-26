package com.huishangyun.Channel.Plan;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Fragment.TimeFragment;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Members;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Fragment.TimeFragment.TimeFace;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

public class CreatePlanSecondStep extends BaseActivity implements TimeFace {

	private static final int START_DATE_DIALOG = 0;// 开始时间的选择
	private static final int END_DATE_DIALOG = 1;// 结束时间的类型
	private static final int PRIORITY_DIALOG = 2;
	private static final String TAG = null;
	private LinearLayout back;// 返回按钮
	private LinearLayout yesterday;// 前一天
	private LinearLayout tomorrow;// 后一天
	private TextView select_customers;// 选择客户
	private SwipeListView lv;// 选择客户列表listview
	private TextView create_plan_time;// 创建计划时间
	private Calendar c = null;// 时间
	private TimeFragment timeFragment;
	private TextView select_amount;// 选择客户数量
	MyAdapte adapter;// 自定义适配器
	ArrayList<SortModel> arrayList = new ArrayList<SortModel>();
	private PlanList list = new PlanList();
	private int Plan_ID;//计划id
	private int index;//确定是否为新增还是修改不为-1则为修改
	private LinearLayout submit;//提交
	private List<Integer> mList = new ArrayList<Integer>();

	/** 创建一个二维数组存储A分组数据 */
	ArrayList<HashMap<String, Object>> ItemLists = new ArrayList<HashMap<String, Object>>();
	private static final int PLAN_TIME = 1;
	private PlanList plandetail;
	private List<PlanList> Tlist = new ArrayList<PlanList>();
	private PlanList Tslist;
	Members aList = new Members();//获取个人数据数据
	private int flag = 0;
	private String DeleteDialogState ;//返回操作那步
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan_create_secondstep);
		//接收数据
	    Intent intent = getIntent();
	    Plan_ID = intent.getIntExtra("Plan_ID", 0);
	    index = intent.getIntExtra("index", -1);
		init();
		initData();
		adapter = new MyAdapte(this);
		lv.setAdapter(adapter);
		
			//获取历史数据
//			getNetData1(Plan_ID);
		getNetData4();
//		getNetData2(create_plan_time.getText().toString(), 1, 10);
	}
	
	/**
	 * 提交新增计划数据
	 * @param ID
	 * @param Plan_ID
	 * @param BelongDate
	 * @param Member_IDS
	 */
	private void getNetData(final int ID, final int Plan_ID, final String BelongDate, 
			final String Member_IDS ,final int index,final int flag) {
		if ((Member_IDS.equals("")||Member_IDS==null) && flag != 2) {
			mHandler.sendEmptyMessage(0);
		}else {
			
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String yyjjString = getJson(ID, Plan_ID, BelongDate, Member_IDS,index);
				Log.e(TAG, "yyjjString:" + yyjjString);
				String result = DataUtil.callWebService(
						Methods.PLAN_CREATE_DETAIL,
						getJson(ID, Plan_ID, BelongDate, Member_IDS,index));
				
				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					//json解析返回结果
					try {
						JSONObject jsonObject = new JSONObject(result);
						Log.e(TAG, "code:" + jsonObject.getInt("Code"));
						int code = jsonObject.getInt("Code");
						if (flag==0) {
							Message msg =  mHandler.obtainMessage();
							msg.what = code;
							mHandler.sendMessage(msg);
						}else {
							mHandler.sendEmptyMessage(5);
						}
						
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}											
						
				} else {
					L.e("接口错误,提交失败！");
				}
				
			}
		}.start();
	 }
	}
	
	/**
	 * 设置json对象
	 * @return
	 */
	private String getJson(int ID, int Plan_ID, String BelongDate, String Member_IDS,int index) {
		if (index==0) {
			//添加时Action设为Insert，修改时设为Update   
			list.setAction("Insert");	
		}else if (index==1) {
			list.setAction("Update");
		}
		list.setID(ID);
		list.setPlan_ID(Plan_ID);
		list.setBelongDate(BelongDate);
	    list.setArray_ID(Member_IDS);
		ZJRequest<PlanList> zjRequest = new ZJRequest<PlanList>();
		zjRequest.setData(list);
		return JsonUtil.toJson(zjRequest);

	}
	

	 /**
 	 * 获取客户数据数据
 	 * @paramID
 	 */
 	private void getNetData2( final String OperationTime,final int PageIndex,final int PageSize){
 		new Thread(){
 			@Override
 			public void run() {
 				// TODO Auto-generated method stub	
 			
 				String result = DataUtil.callWebService(
 						Methods.PLAN_CUSTOMERS,
 						getJson2(OperationTime,PageIndex,PageSize));				
 				Log.e(TAG,"result----客户数据:" + result);
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
 					mHandler.sendEmptyMessage(6);
 					
 				}
 				
 			}
 		}.start();
 	}
 	
 	/**
 	 * 设置json对象
 	 * @paramID 详情id
 	 * @return
 	 */
 	private String getJson2(String OperationTime,int PageIndex,int PageSize) {
 		ZJRequest zjRequest = new ZJRequest();
 		zjRequest.setPlanID(Plan_ID);
 		zjRequest.setPageIndex(PageIndex);
 		zjRequest.setPageSize(PageSize);
 		zjRequest.setBelongDate(OperationTime);
 		return JsonUtil.toJson(zjRequest);

 	}
 	
 	
 	/**
 	 * 删除客户操作
 	 * @param Class_ID 23
 	 * @param OperationTime 2014-9-18
 	 * @param ID 6127客户memberid
 	 */
 	private void getNetData3( final int Class_ID,final String OperationTime,final int ID,final int flag){
 		new Thread(){
 			@Override
 			public void run() {
 				// TODO Auto-generated method stub	
 			
 				String result = DataUtil.callWebService(
						Methods.PLAN_DELETE,
						getJson3(Class_ID, OperationTime, ID));
 				Log.e(TAG,"result:" + getJson3(Class_ID,OperationTime,ID));
 				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
 				if (result != null) {
 					// 获取对象的Type
 					try {
						JSONObject jsonObject = new JSONObject(result);
						Log.e(TAG, "code:" + jsonObject.getInt("Code"));
						int code = jsonObject.getInt("Code");
						//假如code返回为0则表示删除成功,否则为失败
						if (code == 0) {
							if (flag==1) {
								mHandler.sendEmptyMessage(2);
							}else if (flag==2) {
								L.e("客户删除成功！");
							}
							
						}else {
							if (flag==1) {
								mHandler.sendEmptyMessage(3);
							}else if (flag==2) {
								L.e("客户删除失败！");
							}
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
 				} else {
 					mHandler.sendEmptyMessage(2);
 					
 				}
 				
 			}
 		}.start();
 	}
 	
 	/**
 	 * 设置json对象
 	 * @param ID 详情id
 	 * @return
 	 */
 	private String getJson3(int Class_ID,String OperationTime,int ID) {
 		ZJRequest zjRequest = new ZJRequest();
 		// 详情id
 		zjRequest.setID(ID);
 		zjRequest.setPlanID(Class_ID);
 		zjRequest.setOperationTime(OperationTime);
 		zjRequest.setKeywords("Delete");
 		return JsonUtil.toJson(zjRequest);

 	}
	
 	
 	 /**
		 * 获取计划历史数据
		 * @paramID
		 */
		private void getNetData4(){
			new Thread(){
				@Override
				public void run() {
					// TODO Auto-generated method stub	
					String yyjjString = getJson4();
					Log.e(TAG, "yyjjString:" + yyjjString);
					String result = DataUtil.callWebService(
							Methods.PLAN_DETAIL,
							getJson4());				
					Log.e(TAG,"result----:" + result);
					//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
					if (result != null) {
						// 获取对象的Type
						Type type = new TypeToken<ZJResponse<PlanList>>() {
						}.getType();
						ZJResponse<PlanList> zjResponse = JsonUtil.fromJson(result,
								type);
						
						// 获取对象列表
						Tslist = zjResponse.getResult();
						mHandler.sendEmptyMessage(4);
					} else {
						mHandler.sendEmptyMessage(2);
						
					}
					
				}
			}.start();
		}
		
		/**
		 * json对象
		 * @paramID 计划id
		 * @return
		 */
		private String getJson4() {
			
			ZJRequest zjRequest1 = new ZJRequest();
			zjRequest1.setID(Plan_ID);
			return JsonUtil.toJson(zjRequest1);

		}
	/**
	 * 布局控件实例化
	 */
	private void init() {
		back = (LinearLayout) findViewById(R.id.back);
		yesterday = (LinearLayout) findViewById(R.id.yesterday);
		tomorrow = (LinearLayout) findViewById(R.id.tomorrow);
		select_customers = (TextView) findViewById(R.id.select_customers);
		lv = (SwipeListView) findViewById(R.id.select_customers_listview);
		create_plan_time = (TextView) findViewById(R.id.create_plan_time);
		select_amount = (TextView) findViewById(R.id.select_amount);
		submit = (LinearLayout)findViewById(R.id.create_plan_submit);
		back.setOnClickListener(new ButtonClickListener());
		select_customers.setOnClickListener(new ButtonClickListener());
		
		
		create_plan_time.setOnClickListener(new ButtonClickListener());
		yesterday.setOnClickListener(new ButtonClickListener());
		tomorrow.setOnClickListener(new ButtonClickListener());
		submit.setOnClickListener(new ButtonClickListener());
	}

	public class ButtonClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {

			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.back:// 返回操作
				finish();
				break;
			case R.id.yesterday://昨天
				
				try {
					String s1 = create_plan_time.getText().toString();
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					Date d1 = df.parse(Tslist.getStartDate());
					Date d2 = df.parse(getDateStr(s1, -1));
					long diff = d2.getTime() - d1.getTime();
					long days = diff / (1000 * 60 * 60 * 24);
					Log.e(TAG, "days:" + days);
					if (days >= 0) {
						/**  点上一天前，把当前日期数据提交 START   */
						String  Manger_IDS = null;
						StringBuffer mangerid = new StringBuffer("");
						for (int i = 0; i < mList.size(); i++) {
							if (i>0) {
								mangerid.append(",");
							}
							mangerid.append(mList.get(i));
							  
						}
						
						Manger_IDS = mangerid.toString();

						Log.e(TAG,"Plan_ID----:" + Plan_ID);
						getNetData(0, Plan_ID, create_plan_time.getText().toString(), Manger_IDS,1,2);
						/**********End*************************/
						
						create_plan_time.setText(getDateStr(s1, -1));
						getNetData2(create_plan_time.getText().toString(), 1, 10);
					 }else {
						//提示
						 showDialog("该日期已是计划开始日期！");
					}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				
				break;
			case R.id.tomorrow://下一天
				
				//比较日期大小然后提示
				try {
				String s1 = create_plan_time.getText().toString();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date d1 = df.parse(Tslist.getEndDate());
				Date d2 = df.parse(getDateStr(s1, 1));
				long diff = d1.getTime() - d2.getTime();
				long days = diff / (1000 * 60 * 60 * 24);
				Log.e(TAG, "days:" + days);
				if (days >= 0) {
					/**  点上一天前，把当前日期数据提交 START   */
					String  Manger_IDS1 = null;
					StringBuffer mangerid1 = new StringBuffer("");
					for (int i = 0; i < mList.size(); i++) {
						if (i>0) {
							mangerid1.append(",");
						}
						mangerid1.append(mList.get(i));
						  
					}
					Manger_IDS1 = mangerid1.toString();

					Log.e(TAG,"Plan_ID----:" + Plan_ID);
					getNetData(0, Plan_ID, create_plan_time.getText().toString(), Manger_IDS1,1,2);
					
					/**********End*************************/
					select_amount.setText( mList.size() + "");
					create_plan_time.setText(getDateStr(s1, 1));
					getNetData2(create_plan_time.getText().toString(), 1, 10);
				 }else {
					//提示
					 showDialog("该日期已是计划结束日期！");
				}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

				break;
			case R.id.create_plan_time://弹出日期选项，选择计划时间
				TimeFragment timeFragment = new TimeFragment();
				timeFragment.setIndex(CreatePlanSecondStep.this, PLAN_TIME);
				timeFragment.show(getSupportFragmentManager(), "choose time");
				break;
			case R.id.select_customers://选择客户
				Intent intent = new Intent(CreatePlanSecondStep.this, CustomersListActivity.class);
				//选择个客户界面，多选传值0，单选传值1（注意传String类型）
				intent.putExtra("mode", "0");
				intent.putExtra("select", 0);
				//传递分组标题
				intent.putExtra("groupName", "组");
				//返回按钮处标题
				intent.putExtra("Tittle", "选择客户");
				Bundle bundle = new Bundle();
				bundle.putSerializable("SelectList", (Serializable) Tlist);
				intent.putExtra("bundle", bundle);
				startActivityForResult(intent, 0);
				break;
			case R.id.create_plan_submit://提交
				String  Manger_IDS1 = null;
				StringBuffer mangerid1 = new StringBuffer("");
				for (int i = 0; i < mList.size(); i++) {
					if (i>0) {
						mangerid1.append(",");
					}
					mangerid1.append(mList.get(i));
					  
				}
				Manger_IDS1 = mangerid1.toString();

				Log.e(TAG,"Plan_ID----:" + Plan_ID);
				getNetData(0, Plan_ID, create_plan_time.getText().toString(), Manger_IDS1,1,0);
				
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
	 * 往数组里赋值
	 */
	public void initData() {

		for (int i = 0; i < 3; i++) {
			/** 临时存放数据map */
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("title", "已选择客户");
			map.put("company_name", "南昌索科特科技有限公司" + i);
			map.put("company_address", "高新开发区");
			// 往数组里添加数据
			ItemLists.add(map);
		}

	}

	/**
	 * 自定义listview适配器
	 * 
	 * @author xsl
	 * 
	 */
	public class MyAdapte extends BaseAdapter {

		private LayoutInflater mInflater;// 动态布局映射

		public MyAdapte(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {// 决定listview的条数
			// TODO Auto-generated method stub
			return	mList.size();
			
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
		public View getView(final int position, View convertView,
				final ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.activity_plan_customers_listview_item, parent,
						false);
				holder.view_left = (View) convertView
						.findViewById(R.id.item_left);
				holder.view_right = (View) convertView
						.findViewById(R.id.item_right);
				holder.company_name = (TextView) convertView
						.findViewById(R.id.company_name);
				holder.company_address = (TextView) convertView
						.findViewById(R.id.company_adress);
				holder.delete = (TextView) convertView
						.findViewById(R.id.delete);

				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			final View view = convertView;

			LinearLayout.LayoutParams lp1 = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			holder.view_left.setLayoutParams(lp1);
			LinearLayout.LayoutParams lp2 = new LayoutParams(
					lv.getRightViewWidth(), LayoutParams.MATCH_PARENT);
			holder.view_right.setLayoutParams(lp2);


			for (int i = 0; i < mList.size(); i++) {
				getMenber(mList.get(position));
				holder.company_name.setText(aList.getRealName());
				holder.company_address.setText(aList.getAddress());
			}
			
			
			select_amount.setText(mList.size() + "");
			
			holder.delete.setText("删除");
			
			holder.view_right.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					// 删除后item收缩回去
					((SwipeListView) parent).hiddenRight(view);

						//执行客户数据删除操作
						getNetData3(Plan_ID, create_plan_time.getText().toString(), mList.get(position),1);
						if (mList.size()>=position) {
							mList.remove(position);
							
						}
						
						if (Tlist.size()>=position) {
							Tlist.remove(position);
						}
						
						notifyDataSetChanged();
						// 实时更新选择客户的数量
						select_amount.setText( mList.size() + "");
						

				}
			});

			// 左边长按单击事件
			holder.view_left.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					
					deleteDialog(position);	
					
					return false;
				}
			});

			return convertView;
		}

	}

	/***
	 * ----ViewHolder不是Android的开发API，而是一种设计方法，就是设计个静态类，缓存一下，省得Listview更新的时候，
	 * 还要重新操作。 ----
	 **/

	/**
	 * ViewHolder 模式, 效率提高 50%
	 * 
	 * @author XSL
	 * @version 不用这个容器会导致进入viewpager特别卡
	 */
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

	

	/**
	 * 自定义删除对话框
	 */
	private void deleteDialog(final int position) {
		
		flag = 0;
		LayoutInflater layoutInflater = LayoutInflater
				.from(CreatePlanSecondStep.this);
		View customdialog = layoutInflater.inflate(
				R.layout.activity_stock_delete_dialog, null);
		// 创建一个对话框对象
		final AlertDialog alertDialog = new AlertDialog.Builder(
				CreatePlanSecondStep.this).create();
		// 设置对话框背景颜色
		alertDialog.setIcon(R.color.white);
		// 设置对话显示位置
		alertDialog.setView(customdialog, -1, -1, 0, -1);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		ImageView cancel = (ImageView) customdialog.findViewById(R.id.cancel);
		final TextView delete = (TextView) customdialog.findViewById(R.id.delete);
		final TextView rewrite = (TextView) customdialog.findViewById(R.id.rewrite); 
		alertDialog.show();
		rewrite.setVisibility(View.GONE);
		//取消
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
				
			}
		});
		
		//删除
		delete.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				delete.setBackgroundColor(Color.parseColor("#b7e2f6"));
				alertDialog.dismiss();
				//执行客户数据删除操作
				getNetData3(Plan_ID, create_plan_time.getText().toString(), mList.get(position),1);
				if (mList.size()>=position) {
					mList.remove(position);
					
				}
				if (Tlist.size()>=position) {
					Tlist.remove(position);
				}
				adapter.notifyDataSetChanged();
				// 实时更新选择客户的数量
				select_amount.setText( mList.size() + "");
				
			}
		});
		
		
	}

	
	/**
     * 为了得到传回的数据，必须在前面的Activity中（指MainActivity类）重写onActivityResult方法
     * 
     * requestCode 请求码，即调用startActivityForResult()传递过去的值
     * resultCode 结果码，结果码用于标识返回数据来自哪个新Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK) {
    		System.out.println("resultCode=="+resultCode+"requestCode=="+requestCode);
            Bundle bundle = data.getBundleExtra("bundle");//得到新Activity 关闭后返回的数据
            arrayList = (ArrayList<SortModel>) bundle.getSerializable("result");//接收泛型
            if (arrayList.size() > 0) {
            	mList.clear();
            	for (int i = 0; i < arrayList.size(); i++) {
            		mList.add(arrayList.get(i).getID());
            		
    			}
            	
            	select_amount.setText( mList.size() + "");
            	adapter.notifyDataSetChanged();
            	index = 0;
            	
            	/**  点上一天前，把当前日期数据提交 START   */
				String  Manger_IDS1 = null;
				StringBuffer mangerid1 = new StringBuffer("");
				for (int i = 0; i < mList.size(); i++) {
					if (i>0) {
						mangerid1.append(",");
					}
					mangerid1.append(mList.get(i));
					  
				}
				Manger_IDS1 = mangerid1.toString();

				Log.e(TAG,"Plan_ID----:" + Plan_ID);
				
				getNetData(0, Plan_ID, create_plan_time.getText().toString(), Manger_IDS1,1,1);
				/**********End*************************/
				
    		}else {
    			L.e("====>arrayList无数据");
    			for (int i = 0; i < mList.size(); i++) {
    				//执行客户数据删除操作
    				getNetData3(Plan_ID, create_plan_time.getText().toString(), mList.get(i),2);
				}
    			mList.clear();
    			Tlist.clear();
				adapter.notifyDataSetChanged();
				// 实时更新选择客户的数量
				select_amount.setText( mList.size() + "");
    		}
            Log.e(TAG,"arrayList:" + arrayList.size());
		}
    	
        
    }

	@Override
	public void chooseTime(String time, int type, long timeStamp) {
		// TODO Auto-generated method stub
		switch (type) {
		case PLAN_TIME:
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
					create_plan_time.setText(time);
					getNetData2(create_plan_time.getText().toString(), 1, 10);
				}else {
					showDialog("您设定的日期不在计划日期内！");
				}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			
			break;

		default:
			break;
		}
		
	}
    
   
public Handler mHandler = new Handler(){
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case 0:
			//添加成功重新获取数据，更新列表
			getNetData2(create_plan_time.getText().toString(), 1, 10);
			new ClueCustomToast().showToast(CreatePlanSecondStep.this,
    				R.drawable.toast_sucess, "提交成功！" );
			if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
				Intent intent = new Intent(CreatePlanSecondStep.this,MainPlanOrdinaryEntryActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}else {
				Intent intent = new Intent(CreatePlanSecondStep.this,MainPlanActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		
			finish();
			
			break;
			
        case 1:
        	mList.clear();
        	
			for ( int i = 0; i < Tlist.size(); i++) {
				mList.add(Tlist.get(i).getMember_ID());
				Log.e(TAG, "Tlist:" + Tlist.get(i).getID());
				
			}
			adapter.notifyDataSetChanged();
			// 实时更新选择客户的数量
			select_amount.setText( mList.size() + "");
			
			break;
        case 2:
        	new ClueCustomToast().showToast(CreatePlanSecondStep.this,
    				R.drawable.toast_sucess, "客户删除成功！" );
			break;
        case 3:
        	new ClueCustomToast().showToast(CreatePlanSecondStep.this,
    				R.drawable.toast_defeat, "客户删除失败！" );
	    break;
        case 4:
        	create_plan_time.setText(backDate(Tslist.getStartDate()));
        	getNetData2(create_plan_time.getText().toString(), 1, 10);
        	break;
        case 5:
        	getNetData2(create_plan_time.getText().toString(), 1, 10);
        	select_amount.setText( mList.size() + "");
        	break;
        case 6:
        	new ClueCustomToast().showToast(CreatePlanSecondStep.this,
    				R.drawable.toast_defeat, "请检查网络连接！" );
        	break;
        	case 7:
        		showDialog("服务器数据交接异常，接口或进行了修改！");
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

    //获取所有客户信息
	private void getMenber(int Member_ID){
		aList = MemberManager.getInstance(this).getMembers(Member_ID);
	}
	
	public void showDialog(String TXT){
		new ClueCustomToast().showToast(CreatePlanSecondStep.this,
				R.drawable.toast_warn, TXT);

	}

}
