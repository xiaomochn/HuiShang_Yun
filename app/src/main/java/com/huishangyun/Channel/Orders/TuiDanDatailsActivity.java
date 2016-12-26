package com.huishangyun.Channel.Orders;

import java.lang.reflect.Type;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.View.ProgressBar_Loading;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.model.Order_SetOrder;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.yun.R;

public class TuiDanDatailsActivity extends BaseActivity {
	private LinearLayout back, time_lin, memo_lin;
	private RelativeLayout qingdan, relativeLayout, rizhi;
	private Button cancel, copy;
	private TextView text, text1, text2, text3, text4;
	private TextView state, order_id, count, count2, address_name, address_phone, address_address, memo, time;
	private String Member_ID, Member_Name, ReceiveName, Address, Mobile, Date, Note, Price;
	private String OrderID;
	private int id_state;
	private Order_SetOrder order_SetOrder = new Order_SetOrder();
	
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				ProgressBar_Loading.dismiss(TuiDanDatailsActivity.this);
				showCustomToast("操作成功", true);
				finish();
				sendBroadcast(new Intent("ZUOFEI"));
				break;
				
			case HanderUtil.case2:
				ProgressBar_Loading.dismiss(TuiDanDatailsActivity.this);
				showCustomToast((String) msg.obj, false);
				break;
				
			case HanderUtil.case3:
				//获取详情(界面信息还是从列表传来的，付款记录的已付金额才从这里获取)
				ProgressBar_Loading.dismiss(TuiDanDatailsActivity.this);				
				
				Member_ID = order_SetOrder.getMember_ID() + "";//获取客户ID 传给复制页面(传过来的是String，传出去也是String)		
				Member_Name = order_SetOrder.getMember_Name();	
				OrderID = order_SetOrder.getOrderID();
				order_id.setText(OrderID);
				MyApplication.preferences.edit().putString("orderID", OrderID).commit();
				
				id_state = order_SetOrder.getStatus();				
				new FindActivity().getStatu(TuiDanDatailsActivity.this, state, id_state);
															
				Date = order_SetOrder.getReceiveDate();
				Note = order_SetOrder.getNote();						
				ReceiveName = order_SetOrder.getReceiveName();
				Address = order_SetOrder.getReceiveAddress();
				Mobile = order_SetOrder.getReceiveMobile();
				Price = order_SetOrder.getPrice() + "";
				
				address_name.setText("收货人：" + ReceiveName);
				address_phone.setText("联系电话：" + Mobile);
				address_address.setText("收货地址：" + Address);				
				count.setText("￥" + Price);
				count2.setText("￥" + Price);
								
				if (Note == null || Note.trim().equals("")) {
					memo_lin.setVisibility(View.GONE);
				}else {
					memo.setText(Note);
				}
			
				if (id_state == 1 || id_state == 2) {	
					relativeLayout.setVisibility(View.VISIBLE);
					copy.setText("退单作废");
					copy.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_background_cancel));
				}else {
					relativeLayout.setVisibility(View.GONE);
				}
				
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
		setContentView(R.layout.activity_dingdandatails);
			
		init();
	}
	
	private void init(){
		//复用布局的操作
		relativeLayout = (RelativeLayout)findViewById(R.id.relativelayout);
		cancel = (Button)findViewById(R.id.cancel);//把取消隐藏复制改为隐藏
		cancel.setVisibility(View.GONE);
		
		copy = (Button)findViewById(R.id.copy);
		text = (TextView)findViewById(R.id.text);
		text1 = (TextView)findViewById(R.id.text1);
		text2 = (TextView)findViewById(R.id.text2);
		text3 = (TextView)findViewById(R.id.text3);
		text4 = (TextView)findViewById(R.id.text4);		
		text.setText("退单详情");
		text1.setText("退单状态：");
		text2.setText("退货单号：");
		text3.setText("应退金额");
		text4.setText("退货信息");
								
		//获取传递的数据
		state = (TextView)findViewById(R.id.state);
		order_id = (TextView)findViewById(R.id.order_id);
		address_name = (TextView)findViewById(R.id.address_name);
		address_phone = (TextView)findViewById(R.id.address_phone);
		address_address = (TextView)findViewById(R.id.address_address);
//		time = (TextView)findViewById(R.id.time);
		memo = (TextView)findViewById(R.id.memo);
		time_lin = (LinearLayout)findViewById(R.id.time_lin);
		time_lin.setVisibility(View.GONE);
		memo_lin = (LinearLayout)findViewById(R.id.memo_lin);				
		count = (TextView)findViewById(R.id.count);
		count2 = (TextView)findViewById(R.id.count2);
								
		back = (LinearLayout)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();				
			}
		});	
		rizhi = (RelativeLayout)findViewById(R.id.rizhi);
		rizhi.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(TuiDanDatailsActivity.this, LogActivity.class);
				intent.putExtra("order_id", OrderID);
				startActivity(intent);				
			}
		});
		qingdan = (RelativeLayout)findViewById(R.id.qingdan);
		qingdan.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (id_state == 1) {
					Intent intent = new Intent(TuiDanDatailsActivity.this, GoodsListActivity.class);
					intent.putExtra("order_id", OrderID);//更改订单的时候需要这些数据
					intent.putExtra("Member_ID", Member_ID);				
					intent.putExtra("Member_Name", Member_Name);
					intent.putExtra("ReceiveName", ReceiveName);
					intent.putExtra("Address", Address);
					intent.putExtra("Mobile", Mobile);				
					intent.putExtra("Date", Date);
					intent.putExtra("Note", Note);
					intent.putExtra("Price", Price);
					
					intent.putExtra("flage", "DING");	
					startActivityForResult(intent, 1);//获取更改后清单的总价	
				
				}else {
					Intent intent = new Intent(TuiDanDatailsActivity.this, GoodsList_notActivity.class);	
					intent.putExtra("flage", "DING");
					intent.putExtra("order_id", OrderID);
					startActivity(intent);
				}
						
			}
		});
		
		copy.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(TuiDanDatailsActivity.this);
				builder.setMessage("确认作废吗？");					 
				builder.setTitle("提示");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int which) {
				    	ProgressBar_Loading.showProgressBar(TuiDanDatailsActivity.this, true, "Loading....");
				    	delOrder(OrderID);
				    	
				}					 
				});					  
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int which) {					 
				   dialog.dismiss();					 
				   }					 
				});					  
				builder.create().show();				
			}
		});
		
		getDetails(getIntent().getStringExtra("OrderID"));
	}
	
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg0 == 1 && arg1 == RESULT_OK) {
			count.setText("￥" + arg2.getStringExtra("COUNT"));
			count2.setText("￥" + arg2.getStringExtra("COUNT"));
		}
		super.onActivityResult(arg0, arg1, arg2);
	}
	
	/**
	 * 获取订单详情的方法
	 * @param goods
	 */
	private void getDetails(String order_id){
		ProgressBar_Loading.showProgressBar(TuiDanDatailsActivity.this, true, null);
		
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setOrderID(order_id);
		final String json = JsonUtil.toJson(zjRequest);
	    L.e("(订单详情)json:" + json);
	    
		new Thread(){
			public void run() {
				try {			
					String jsonString = DataUtil.callWebService(Methods.ORDER_DINGDAN_DATAILS, json);
					L.e("(订单详情)jsonString:" + jsonString);
					
					if (jsonString != null) {						
						Type type = new TypeToken<ZJResponse<Order_SetOrder>>(){}.getType();
					    ZJResponse zjResponse = JsonUtil.fromJson(jsonString, type);
					    
					    Message message = new Message();
					    if (zjResponse.getCode() == 0) {	
					    	order_SetOrder = (Order_SetOrder) zjResponse.getResult();
					    							    
					    	message.what = HanderUtil.case3;
						    myHandler.sendMessage(message);
						    
						} else {								
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
	
	/**
	 * 退货单作废的方法
	 * @param goods
	 */
	private void delOrder(String order_id){
		ZJRequest zjRequest = new ZJRequest();
						
		zjRequest.setOrderID(order_id);	
		zjRequest.setAction("Cancel");//
		zjRequest.setNote("Note");
		zjRequest.setManager_ID(Integer.parseInt(MyApplication.getInstance().getSharedPreferences().
				getString(Constant.HUISHANGYUN_UID, "0")));
		zjRequest.setOperationName(MyApplication.getInstance().getSharedPreferences().
				getString(Constant.XMPP_MY_REAlNAME, ""));
		zjRequest.setCompany_ID(MyApplication.getInstance().getCompanyID());
		
		final String json = JsonUtil.toJson(zjRequest);
	    L.e("(退货单作废)json:" + json);
	    
		new Thread(){
			public void run() {
				try {			
					String jsonString = DataUtil.callWebService(Methods.ORDER_DINGDAN_DEL, json);
					L.e("(退货单作废)jsonString:" + jsonString);
					
					if (jsonString != null) {						
						Type type = new TypeToken<ZJResponse>(){}.getType();
					    ZJResponse zjResponse = JsonUtil.fromJson(jsonString, type);
					    
					    Message message = new Message();
					    if (zjResponse.getCode() == 0) {				    	
					    	message.what = HanderUtil.case1;
						    myHandler.sendMessage(message);
						    
						} else {								
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
}
