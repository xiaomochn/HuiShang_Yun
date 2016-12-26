package com.huishangyun.Channel.Orders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.manager.EnumManager;
import com.huishangyun.model.Content;
import com.huishangyun.model.EnumKey;
import com.huishangyun.model.Order_SetOrder;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.View.ProgressBar_Loading;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Enum;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

public class FindActivity extends BaseActivity {
	private LinearLayout back, find, list, search_hint;	
	private EditText edt;
	private ListView listView;
	
	private List<Order_SetOrder> data = new ArrayList<Order_SetOrder>();
	private List<Order_SetOrder> data2 = new ArrayList<Order_SetOrder>();
	private MyAdapter myAdapter;
	
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				ProgressBar_Loading.dismiss(FindActivity.this);
				data.clear(); 
				for ( int i = 0; i < data2.size(); i++) {
					data.add(data2.get(i));
				}
				if (data.size() > 0) {
					list.setVisibility(View.VISIBLE);
					myAdapter.notifyDataSetChanged();  
				}else {
					L.e("进入没有数据页面");
//					showCustomToast("没有相关订单", false);
					search_hint.setVisibility(View.VISIBLE);
					list.setVisibility(View.GONE);
				}								
				//dismissDialog();
				break;
				
			case HanderUtil.case2:
				ProgressBar_Loading.dismiss(FindActivity.this);
				showCustomToast((String) msg.obj, false);
				break;

			default:
				break;
			}
		};
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_find);
		
		search_hint = (LinearLayout)findViewById(R.id.search_hint);
		list = (LinearLayout)findViewById(R.id.list);
		edt = (EditText)findViewById(R.id.edt);
		//获取焦点就弹出输入框
		edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			   public void onFocusChange(View v, boolean hasFocus) {
			       if (hasFocus) {
			    	   getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			       }
			   }
		});
		edt.addTextChangedListener(new Text());
		
		back = (LinearLayout)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();				
			}
		});
		
		find = (LinearLayout)findViewById(R.id.research);
		find.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (getIntent().getStringExtra("flage").trim().equals("DING")) {
					getList(edt.getText().toString(), 1);	//订货单传1，退货单传-1	
				
				}else {
					getList(edt.getText().toString(), -1);	//订货单传1，退货单传-1	
				}
				
			}
		});	
		
		listView = (ListView)findViewById(R.id.list_order);
		myAdapter = new MyAdapter(this);
		listView.setAdapter(myAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent intent;
				if (getIntent().getStringExtra("flage").equals("TUI")) {
					intent = new Intent(FindActivity.this, TuiDanDatailsActivity.class);
				}else {
					intent = new Intent(FindActivity.this, DingDanDatailsActivity.class);
				}
								
				Bundle bundle = new Bundle();
				intent.putExtra("State", data.get(position).getStatus());
				intent.putExtra("OrderID", data.get(position).getOrderID());
				intent.putExtra("Count", data.get(position).getPrice() + "");
				
				//下面这些传的值都是详情里面不用复制时要传的。
				intent.putExtra("Member_ID", data.get(position).getMember_ID() + "");//客户ID是为了复制订单的时候使用
				intent.putExtra("Member_Name", data.get(position).getMember_Name());
				intent.putExtra("ReceiveName", data.get(position).getReceiveName());
				intent.putExtra("Address", data.get(position).getReceiveAddress());
				intent.putExtra("Mobile", data.get(position).getReceiveMobile());				
				intent.putExtra("Date", data.get(position).getReceiveDate());
				intent.putExtra("Note", data.get(position).getNote());
				startActivity(intent);														
			}
		});
	}
	
	private class Text implements TextWatcher{
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {			
		}
		public void afterTextChanged(Editable s) {
			if (edt.getText().toString().equals("")) {
				list.setVisibility(View.GONE);
				search_hint.setVisibility(View.GONE);
			}else {
				if (getIntent().getStringExtra("flage").trim().equals("DING")) {
					getList(edt.getText().toString(), 1);	//订货单传1，退货单传-1	
				
				}else {
					getList(edt.getText().toString(), -1);	//订货单传1，退货单传-1	
				}
			}
			
		}		
	}
	
	/**
	 * 传入关键字    获取订单列表线程方法
	 * @param json
	 */
	private void getList(String keyWord, int flag){		
		ProgressBar_Loading.showProgressBar(FindActivity.this, true, "Loading....");
		
		int Manager_ID = Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0"));
		int Company_ID = preferences.getInt(Content.COMPS_ID, 1016);
		int Department_ID = preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0);
		
		//实例化请求类
	    ZJRequest zjRequest = new ZJRequest(); 
	    
	    if (Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANG_TYPE,"0")) == 1) {
	    	//账户id：Manager_ID
		    zjRequest.setManager_ID(Manager_ID);
	    }else {
	    	zjRequest.setManager_ID(0);
		}
	    
	    //Company_ID
	    zjRequest.setCompany_ID(Company_ID);
	    //Department_ID
	    zjRequest.setDepartment_ID(Department_ID);
	    zjRequest.setPageIndex(1);
	    zjRequest.setKeywords(keyWord);
	    zjRequest.setType(flag);//订货单传1，退货单传-1
	    
	    final String json = JsonUtil.toJson(zjRequest);
	    L.e("(订/退单查询)jsonString:" + json);
	    	    
	    //activity.showDialog("正在加载...");	    
		new Thread(){
	    	public void run() {
	    		try {
					String jsonString = DataUtil.callWebService(Methods.ORDER_DINGDAN_LIST, json);
					L.e("(订/退单查询)json:" + jsonString);
					if (jsonString != null) {
						Type type = new TypeToken<ZJResponse<Order_SetOrder>>(){}.getType();
					    ZJResponse<Order_SetOrder> zjResponse = JsonUtil.fromJson(jsonString, type);
					    if (zjResponse.getCode() == 0) {
					    	
					    	data2.clear();//因为第一次加载和删除都要调用，所以这里先clear一下。
						    data2 = zjResponse.getResults();
						    myHandler.sendEmptyMessage(HanderUtil.case1);
						    
						} else {
							Message message = new Message();
						    message.what = HanderUtil.case2;
						    message.obj = zjResponse.getDesc();
						    myHandler.sendMessage(message);
						}
					}								    					    
				} catch (Exception e) {
					e.printStackTrace();
				}
	    	};
		}.start();
	}
	
	private class MyAdapter extends BaseAdapter{
		
		private LayoutInflater inflater;// 用来导入布局

		public MyAdapter(Context context) {// 构造器
			this.inflater = LayoutInflater.from(context);
		}
		
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View view, ViewGroup parent) {
			
			final ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.list_dingdan, null);
				
				holder.name = (TextView) view.findViewById(R.id.name);
				holder.tongzhi = (TextView) view.findViewById(R.id.tongzhi);
				holder.nub = (TextView) view.findViewById(R.id.nub);
				holder.price = (TextView) view.findViewById(R.id.price);
				holder.time = (TextView) view.findViewById(R.id.time);
				holder.img = (ImageView) view.findViewById(R.id.img);
				holder.img2 = (ImageView) view.findViewById(R.id.img2);
				
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}			
			
			getStatu(FindActivity.this, holder.tongzhi, data.get(position).getStatus());
			
			holder.name.setText(data.get(position).getMember_Name());			
			holder.nub.setText(data.get(position).getOrderID());     //这个还需改变
			holder.price.setText(data.get(position).getPrice() + "");
			String timeStr =  data.get(position).getReceiveDate().substring(0,10);
			holder.time.setText(timeStr);
										
			return view;
		}
		
		public final class ViewHolder {// 写这个类是为了点击list的时候好调用view
			public TextView name;
			public TextView tongzhi;
			public TextView nub;
			public ImageView img;
			public TextView price;
			public ImageView img2;
			public TextView time;
		}
	}
	
	
	/**
	 * 传给订单详情
	 * @param status
	 * @return
	 */
	public static void getStatu(Context context, TextView textView, int status){
		
		//0:已删除,1:新订单, 2:已付款, 3:已分配, 4:已发货, 5:已收货,6:完成,7:作废
		//"订单待审核=1", "订单已审核=3", "订单已发货=4", "订单已取消=7", "订单确认收货=5", 
		Enum enum1 = EnumManager.getInstance(context).getEmunForIntKey(EnumKey.ENUM_ORDERS, "" + status);
		textView.setText(enum1.getLab());
		
		switch (status) {
		case 0:
			textView.setTextColor(context.getResources().getColor(R.color.over));//删除
			break;
			
		case 1:
			textView.setTextColor(context.getResources().getColor(R.color.early));//新订单
			break;
			
		case 2:
			textView.setTextColor(context.getResources().getColor(R.color.early));//已付款
			break;
			
		case 3:
			textView.setTextColor(context.getResources().getColor(R.color.early));//已分配
			break;
			
		case 4:
			textView.setTextColor(context.getResources().getColor(R.color.early));//已发货
			break;
			
		case 5:
			textView.setTextColor(context.getResources().getColor(R.color.late));//已收货
			break;
			
		case 6:
			textView.setTextColor(context.getResources().getColor(R.color.over));//完成
			break;
			
		case 7:
			textView.setTextColor(context.getResources().getColor(R.color.over));//已作废
			break;

		default:
			break;
		}
	}
}
