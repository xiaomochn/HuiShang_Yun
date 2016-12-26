package com.huishangyun.Channel.Orders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.View.ProgressBar_Loading;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.model.Order_SetOrder;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.manager.CartManager;
import com.huishangyun.model.CartModel;
import com.huishangyun.yun.R;

public class DingDanDatailsActivity extends BaseActivity {
	private LinearLayout back, time_lin, memo_lin;	
	private RelativeLayout qingdan, rizhi;
	private Button cancel, copy;
	private TextView state, order_id, count, count2, address_name, address_phone, address_address, memo, time;
	private String OrderID, Member_ID, Member_Name, ReceiveName, Address, Mobile, Date, Note, Price;
	private int id_state;
	private String copy_orderID;
	private String results;
	private List<CartModel> data, data2;
	private Order_SetOrder order_SetOrder = new Order_SetOrder();
	
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				ProgressBar_Loading.dismiss(DingDanDatailsActivity.this);
				showCustomToast("操作成功", true);
				finish();
				break;
				
			case HanderUtil.case2:
				ProgressBar_Loading.dismiss(DingDanDatailsActivity.this);
				showCustomToast((String) msg.obj, false);
				break;
				
			case HanderUtil.case3:
				ProgressBar_Loading.dismiss(DingDanDatailsActivity.this);
				DingDanDatailsActivity.this.sendBroadcast(new Intent().setAction("ZUOFEI"));
				showCustomToast("操作成功", true);				
				finish();
				break;
				
			case HanderUtil.case4:
				ProgressBar_Loading.dismiss(DingDanDatailsActivity.this);
				showCustomToast("复制成功", true);
				Intent intent = new Intent(DingDanDatailsActivity.this, DingDanCopyActivity.class);
				intent.putExtra("order_id", copy_orderID);
				startActivity(intent);
				sendBroadcast(new Intent("ZUOFEI"));
				break;
				
			case HanderUtil.case5:
				long nub = CartManager.getInstance(DingDanDatailsActivity.this).deleteAll();
				L.e("清除购物车返回的nub:" + nub);
				if (nub > 0) {
					L.e("数据库删除成功");
				}else {
					L.e("数据库删除失败");
				}
				showCustomToast("提交成功", true);
				Intent intent2 = new Intent(DingDanDatailsActivity.this, SubmitActivity.class);
				intent2.putExtra("result", results);
				intent2.putExtra("count", count.getText().toString().substring(1, count.getText().toString().length()));
				intent2.putExtra("flage", "TUI");
				startActivity(intent2);
				
				finish();
				break;
				
			case HanderUtil.case6:
				//获取详情(界面信息还是从列表传来的，付款记录的已付金额才从这里获取)
				ProgressBar_Loading.dismiss(DingDanDatailsActivity.this);				
				
				Member_ID = order_SetOrder.getMember_ID() + "";//获取客户ID 传给复制页面(传过来的是String，传出去也是String)		
				Member_Name = order_SetOrder.getMember_Name();	
				OrderID = order_SetOrder.getOrderID();
				order_id.setText(OrderID);
				MyApplication.preferences.edit().putString("orderID", OrderID).commit();
				
				id_state = order_SetOrder.getStatus();				
				new FindActivity().getStatu(DingDanDatailsActivity.this, state, id_state);
															
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
				
				if (Date.trim().equals("")) {
					time_lin.setVisibility(View.GONE);
				}else {
					time.setText(Date.substring(0, 10));
				}
				if (Note == null || Note.trim().equals("")) {
					memo_lin.setVisibility(View.GONE);
				}else {
					memo.setText(Note);
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
		
		data = new ArrayList<CartModel>();
		data2 = new ArrayList<CartModel>();

		init();
	}
	
	private void init(){
		state = (TextView)findViewById(R.id.state);
		order_id = (TextView)findViewById(R.id.order_id);
		address_name = (TextView)findViewById(R.id.address_name);
		address_phone = (TextView)findViewById(R.id.address_phone);
		address_address = (TextView)findViewById(R.id.address_address);
		time = (TextView)findViewById(R.id.time);
		memo = (TextView)findViewById(R.id.memo);
		time_lin = (LinearLayout)findViewById(R.id.time_lin);
		memo_lin = (LinearLayout)findViewById(R.id.memo_lin);		
		count = (TextView)findViewById(R.id.count);
		count2 = (TextView)findViewById(R.id.count2);
		
		
		back = (LinearLayout)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();				
			}
		});	
		cancel = (Button)findViewById(R.id.cancel);
		copy = (Button)findViewById(R.id.copy);
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
												
				if (cancel.getText().toString().trim().equals("订单作废")) {
					AlertDialog.Builder builder = new Builder(DingDanDatailsActivity.this);
					builder.setMessage("确认作废吗？");					 
					builder.setTitle("提示");
					builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int which) {
					    	ProgressBar_Loading.showProgressBar(DingDanDatailsActivity.this, true, "Loading....");
					    	delOrder(OrderID);	
					    	
					}					 
					});					  
					builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					   public void onClick(DialogInterface dialog, int which) {					 
					   dialog.dismiss();					 
					   }					 
					});					  
					builder.create().show();
					
					
				}else if (cancel.getText().toString().trim().equals("退单")) {
					AlertDialog.Builder builder = new Builder(DingDanDatailsActivity.this);
					builder.setMessage("确认退单吗？");					 
					builder.setTitle("提示");
					builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int which) {
					    	Intent intent = new Intent(DingDanDatailsActivity.this, GoodsListActivity.class);	
							
							intent.putExtra("order_id", OrderID);//更改订单的时候需要这些数据
							intent.putExtra("flage", "TUI");
							intent.putExtra("Member_ID", Member_ID);				
							intent.putExtra("Member_Name", Member_Name);
							intent.putExtra("ReceiveName", ReceiveName);
							intent.putExtra("Address", Address);
							intent.putExtra("Mobile", Mobile);				
							intent.putExtra("Date", Date);
							intent.putExtra("Note", Note);
							intent.putExtra("Price", Price);
							startActivity(intent);
														
//							order_SetOrder.setMember_ID(Integer.parseInt(intent.getStringExtra("Member_ID")));//客户id
//							order_SetOrder.setMember_Name(intent.getStringExtra("Member_Name"));//客户名称
//							order_SetOrder.setReceiveName(intent.getStringExtra("ReceiveName"));//收货人姓名
//							order_SetOrder.setReceiveTel(intent.getStringExtra("Mobile"));
//							order_SetOrder.setReceiveMobile(intent.getStringExtra("Mobile"));
//							
//							order_SetOrder.setReceiveAddress(intent.getStringExtra("Address"));		
//							order_SetOrder.setReceiveDate(intent.getStringExtra("Date").substring(0,10));//期望到货时间
//							order_SetOrder.setNote(intent.getStringExtra("Note"));//备注
//							getGoodsLists(OrderID);		
					}					 
					});					  
					builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					   public void onClick(DialogInterface dialog, int which) {					 
					   dialog.dismiss();					 
					   }					 
					});					  
					builder.create().show();
													
				}
				
			}
		});
		copy.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(DingDanDatailsActivity.this);
				builder.setMessage("确认复制吗？");					 
				builder.setTitle("提示");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int which) {
				    	ProgressBar_Loading.showProgressBar(DingDanDatailsActivity.this, true, "Loading....");
				    	CopyOrderList(OrderID);	
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
		rizhi = (RelativeLayout)findViewById(R.id.rizhi);
		rizhi.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(DingDanDatailsActivity.this, LogActivity.class);
				intent.putExtra("order_id", OrderID);
				startActivity(intent);				
			}
		});
		qingdan = (RelativeLayout)findViewById(R.id.qingdan);
		qingdan.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (id_state == 1) {
					Intent intent = new Intent(DingDanDatailsActivity.this, GoodsListActivity.class);
					intent.putExtra("order_id", OrderID);//更改订单的时候需要这些数据
					intent.putExtra("flage", "DING");
					intent.putExtra("Member_ID", Member_ID);				
					intent.putExtra("Member_Name", Member_Name);
					intent.putExtra("ReceiveName", ReceiveName);
					intent.putExtra("Address", Address);
					intent.putExtra("Mobile", Mobile);				
					intent.putExtra("Date", Date);
					intent.putExtra("Note", Note);
					intent.putExtra("Price", Price);
					
					startActivityForResult(intent, 1);//获取更改后清单的总价
				}else {
					Intent intent = new Intent(DingDanDatailsActivity.this, GoodsList_notActivity.class);	
					intent.putExtra("flage", "DING");
					intent.putExtra("order_id", OrderID);
					startActivity(intent);
				}
				
			}
		});
		
		if (id_state == 3 || id_state == 4 || id_state == 6 || id_state == 7) {
			cancel.setVisibility(View.GONE);
			
		}else if (id_state == 5) {
			cancel.setText("退单");
			//这里本来就是这一种颜色
//			cancel.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_background_cancel));
		}
		
		getDetails(getIntent().getStringExtra("OrderID"));
	}
	
	@Override
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
		ProgressBar_Loading.showProgressBar(DingDanDatailsActivity.this, true, null);
		
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
					    							    
					    	message.what = HanderUtil.case6;
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
	 * 订单作废的方法
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
	    L.e("(订单作废)json:" + json);
	    
		new Thread(){
			public void run() {
				try {			
					String jsonString = DataUtil.callWebService(Methods.ORDER_DINGDAN_DEL, json);
					L.e("(订单作废)jsonString:" + jsonString);
					
					if (jsonString != null) {						
						Type type = new TypeToken<ZJResponse>(){}.getType();
					    ZJResponse zjResponse = JsonUtil.fromJson(jsonString, type);
					    
					    Message message = new Message();
					    if (zjResponse.getCode() == 0) {				    	
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
	 * 复制订单的方法
	 */
	private void CopyOrderList(String order_id){
		ZJRequest zjRequest = new ZJRequest();
		
		zjRequest.setOrderID(order_id);
		zjRequest.setAction("Copy");
		zjRequest.setNote("Note");
		zjRequest.setManager_ID(Integer.parseInt(MyApplication.getInstance().getSharedPreferences().
				getString(Constant.HUISHANGYUN_UID, "0")));
		zjRequest.setOperationName(MyApplication.getInstance().getSharedPreferences().
				getString(Constant.XMPP_MY_REAlNAME, ""));
		zjRequest.setCompany_ID(MyApplication.getInstance().getCompanyID());
		
//		//xsl修改【start】
//		zjRequest.set
//		zjRequest.setOperationID(MyApplication.getInstance().getManagerID());
//		zjRequest.setOperationName(MyApplication.preferences.getString(Constant.XMPP_MY_REAlNAME, ""));
//		//xsl修改【end】
		final String json = JsonUtil.toJson(zjRequest);
	    L.e("(复制订单)json:" + json);
	    
		new Thread(){
			public void run() {
				try {			
					String jsonString = DataUtil.callWebService(Methods.ORDER_DINGDAN_DEL, json);
					L.e("(复制订单)jsonString:" + jsonString);
					
					if (jsonString != null) {						
						Type type = new TypeToken<ZJResponse<String>>(){}.getType();
					    ZJResponse<String> zjResponse = JsonUtil.fromJson(jsonString, type);					    
					    Message message = new Message();
					    if (zjResponse.getCode() == 0) {				    	
					    	message.what = HanderUtil.case4;
					    	copy_orderID = zjResponse.getResult();
					    	L.e("copy_orderID:" + copy_orderID);
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
