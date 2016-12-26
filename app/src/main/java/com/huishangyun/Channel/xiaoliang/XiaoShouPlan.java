package com.huishangyun.Channel.xiaoliang;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ShowDialog;
import com.huishangyun.Channel.Plan.SortModel;
import com.huishangyun.Interface.OnDialogDown;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.T;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.Channel.Plan.CustomersListActivity;
import com.huishangyun.yun.R;

public class XiaoShouPlan extends BaseActivity implements OnDialogDown {
	
	private LinearLayout back;
	private ListView listView;
	private TextView customer, time, goods, danwei;
	private EditText nubs;
	
	private List<PlanModle> mList = new ArrayList<PlanModle>();
	private MyAdapter myAdapter;
	private static final int SOURCE_DIALOG = 0;
	
	private int member_ID = 0;
	private String Member_Name;
	private Integer Product_ID; // 产品ID
	private String Product_Name; // 产品名称
	private Integer Unit_ID; // 单位ID
	private String Unit_Name; // 单位名称
	private Float Qty0; // 数量
	private String CycleType; // 周期 （day 或者 month）
	private String BelongDate; // 上报时间
	
	private int widths, heights;
	
	private webServiceHelp<T> mServiceHelp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.xiaoshoujihua);
						
		back = (LinearLayout) findViewById(R.id.back);
		customer = (TextView) findViewById(R.id.customer);
		time = (TextView) findViewById(R.id.time);
		goods = (TextView) findViewById(R.id.goods);
		danwei = (TextView) findViewById(R.id.danwei);
		nubs = (EditText) findViewById(R.id.nubs);
		listView = (ListView)findViewById(R.id.listview);		
		myAdapter = new MyAdapter(this);
		listView.setAdapter(myAdapter);
		Common_Xiao.setListViewHeight(listView, myAdapter);
		mServiceHelp = new webServiceHelp<T>(Methods.SET_XIAOSHOUPLAN, new TypeToken<ZJResponse>(){}.getType());
		mServiceHelp.setOnServiceCallBack(onServiceCallBack);
		init();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mServiceHelp.removeOnServiceCallBack();
		super.onDestroy();
	}
	
	/**
	 * 添加按钮
	 * @param view
	 */
	public void bt_add(View view) {
		if (customer.getText().toString().trim().equals("")) {
			showCustomToast("客户不为空", false);
		}else if(time.getText().toString().trim().equals("")){
			showCustomToast("周期不为空", false);
		}else if(goods.getText().toString().trim().equals("")){
			showCustomToast("产品不为空", false);
		}else if(danwei.getText().toString().trim().equals("")){
//			showCustomToast("单位不为空", false);
		}else if(nubs.getText().toString().trim().equals("")){
			showCustomToast("数量不为空", false);
		}else {
			PlanModle xModle= new PlanModle();
			xModle.setCompany_ID(MyApplication.getInstance().getCompanyID());
			xModle.setDepartment_ID(MyApplication.preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0));
			xModle.setDepartment_Name(MyApplication.preferences.getString(Constant.HUISHANG_DEPARTMENT_NAME, ""));
			xModle.setManager_ID(Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANGYUN_UID, "0")));
			xModle.setManager_Name(MyApplication.preferences.getString(Constant.XMPP_MY_REAlNAME, ""));
			xModle.setProduct_ID(Product_ID);
			xModle.setProduct_Name(Product_Name);
			xModle.setMember_ID(member_ID);
			xModle.setMember_Name(Member_Name);
			xModle.setUnit_ID(Unit_ID);
			xModle.setUnit_Name(Unit_Name);
			String timeString = time.getText().toString().trim();
			CycleType = timeString.length() <= 7 ? "month" : "day";
			xModle.setCycleType(CycleType);
			BelongDate = getTimeString();
			xModle.setBelongDate(BelongDate);
			xModle.setQty0(Integer.parseInt(nubs.getText().toString().trim()));
			mList.add(xModle);
			myAdapter.notifyDataSetChanged();
			Common_Xiao.setListViewHeight(listView, myAdapter);
		}
	}
	
	/**
	 * 提交按钮
	 * @param view
	 */
	public void bt_submit(View view) {
		if (mList.size() == 0) {
			showCustomToast("请填写数据", false);
		} else {
			showNotDialog("正在提交数据");
			ZJRequest<PlanModle> zjRequest = new ZJRequest<PlanModle>();
			zjRequest.setDatas(mList);
			L.e("json = " + JsonUtil.toJson(zjRequest));
			mServiceHelp.start(JsonUtil.toJson(zjRequest));
		}
		
	}
	
	private void init(){
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();				
			}
		});
		customer.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(XiaoShouPlan.this, CustomersListActivity.class);
				//选择个客户/人员界面，客户传值“0”，人员传值“1”（注意传String类型）
				intent.putExtra("mode", "0");
				//多选传0，单选传1
				intent.putExtra("select", 1);
				//传递分组名称
				intent.putExtra("groupName", "分类");
				intent.putExtra("Tittle", "选择客户");
				startActivityForResult(intent, 1);				
			}
		});		
		goods.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent1 = new Intent(XiaoShouPlan.this, CustomersListActivity.class);
				//选择个客户/人员界面，客户传值“0”，人员传值“1”（注意传String类型）
				intent1.putExtra("mode", "2");
				//多选传0，单选传1
				intent1.putExtra("select", 1);
				//传递分组名称
				intent1.putExtra("groupName", "分类");
				intent1.putExtra("Tittle", "产品选择");
				startActivityForResult(intent1, 2);				
			}
		});
		time.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				ArrayList<HashMap<String, Object>> data_time = new ArrayList<HashMap<String, Object>>();
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("name", "月");
				data_time.add(map);
				
				map = new HashMap<String, Object>();				
				map.put("name", "日");
				data_time.add(map);
				
				//传入参数，实现一个借口的回调方法。
				new ShowDialog(XiaoShouPlan.this, SOURCE_DIALOG, data_time, "选择周期",
						XiaoShouPlan.this, "请选择周期！",null).customDialog();				
			}
		});
	}
	
	
	private ArrayList<SortModel> arrayList = new ArrayList<SortModel>();//客户数据
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		//获取客户
		if (requestCode == 1 && resultCode == RESULT_OK) {
			Bundle bundle = data.getBundleExtra("bundle");//得到新Activity 关闭后返回的数据
	        arrayList = (ArrayList<SortModel>) bundle.getSerializable("result");//接收泛型
	        if (arrayList.size() > 0) {
	        	customer.setText((arrayList.get(0).getCompany_name()).trim());
	        	Member_Name = arrayList.get(0).getCompany_name();
	        	member_ID = arrayList.get(0).getID();
			}	    
			
	    //获取产品	
		}else if (requestCode == 2 && resultCode == RESULT_OK) {
			Bundle bundle1 = data.getBundleExtra("bundle");
	        arrayList = (ArrayList<SortModel>) bundle1.getSerializable("result");
	        if (arrayList.size() > 0) {
	        	
	        	goods.setText(arrayList.get(0).getCompany_name());//产品名称
				danwei.setText(arrayList.get(0).getProduct_Unit_Name());//产品单位
				Product_ID = arrayList.get(0).getID();
				Product_Name = arrayList.get(0).getCompany_name();
				Unit_ID = arrayList.get(0).getProduct_Unit_ID();
				Unit_Name = arrayList.get(0).getProduct_Unit_Name();
			}	
	        
		}
	}	
	
	private class MyAdapter extends BaseAdapter{
		
		private LayoutInflater inflater;// 用来导入布局

		public MyAdapter(Context context) {// 构造器
			this.inflater = LayoutInflater.from(context);
		}		
		public int getCount() {
			return mList.size();
		}
		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		public View getView(final int position, View view, ViewGroup parent) {
			
			final ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.list_xiaoshoujihua, null);
				
				holder.goods = (TextView) view.findViewById(R.id.goods);
				holder.nubs = (TextView) view.findViewById(R.id.nubs);
				
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.goods.setText(mList.get(position).getProduct_Name());
			String nub =  mList.get(position).getQty0() + "" + mList.get(position).getUnit_Name(); 
			holder.nubs.setText(nub);
			
			return view;
		}
		
		public final class ViewHolder {// 写这个类是为了点击list的时候好调用view
			public TextView goods;
			public TextView nubs;
		}
	}
		
	@Override
	public void onDialogDown(int position, int type) {
		switch (type) {
		case SOURCE_DIALOG:			
			if (position == 0) {				
				new Common_Xiao().datePickDialog2(this, time);
				
			}else if (position == 1) {
				new Common_Xiao().datePickDialog(this, time);
			}				
			break;

		default:
			break;
		}
	}	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				showCustomToast("提交成功!", true);
				finish();
				break;
			case HanderUtil.case2:
				showCustomToast((String) msg.obj, false);
				break;

			default:
				break;
			}
		};
	};
	
	private webServiceHelp.OnServiceCallBack<T> onServiceCallBack = new webServiceHelp.OnServiceCallBack<T>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<T> zjResponse) {
			dismissDialog();
			// TODO Auto-generated method stub
			if (haveCallBack && zjResponse != null) {
				if (zjResponse.getCode() == 0) {
					mHandler.sendEmptyMessage(HanderUtil.case1);
				} else {
					Message msg = new Message();
					msg.what = HanderUtil.case2;
					msg.obj = zjResponse.getDesc();
					mHandler.sendMessage(msg);
				}
			} else {
				Message msg = new Message();
				msg.what = HanderUtil.case2;
				msg.obj = "无法访问服务器";
				mHandler.sendMessage(msg);
			}
		}
	};
	
	private  String getTimeString(){
		Date date = new Date();
		return ("20"+(date.getYear()+"").substring(1, 3))+
				(((date.getMonth()+"").length() + 1) == 1 ? "0"+(date.getMonth()+1) : (date.getMonth()+1))+
				((date.getDate()+"").length() == 1 ? "0"+date.getDate() : date.getDate());
	}
		
}
