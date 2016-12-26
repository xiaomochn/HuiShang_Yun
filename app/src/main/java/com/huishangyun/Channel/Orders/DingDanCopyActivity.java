package com.huishangyun.Channel.Orders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.View.ProgressBar_Loading;
import com.huishangyun.manager.AreaManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.model.Order_SetOrder;
import com.huishangyun.model.Order_address;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Plan.CustomersListActivity;
import com.huishangyun.Channel.Plan.SortModel;
import com.huishangyun.Fragment.TimeFragment;
import com.huishangyun.Fragment.TimeFragment.TimeFace;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.model.CartModel;
import com.huishangyun.yun.R;

public class DingDanCopyActivity extends BaseActivity implements TimeFace{
	private LinearLayout back;
	private RelativeLayout info, qingdan, kehu, time;
	private TextView tv_kehu, address, price, count, title, name, phone, time_dinghuo;
	private EditText beizhu;
	private Button submit;
	private static final int DINGHUO_TIME_TYPE = 0;	
	
	private ArrayList<SortModel> arrayList; //客户界面返回的集合
	private List<Order_address> data_address = new ArrayList<Order_address>();//获取地址集合

	private String goods = "";//进入复制传入要复制的清单字符串，改变清单再重置字符串。
	private List<CartModel> data_qingdan = new ArrayList<CartModel>();
	
	private Order_SetOrder order_SetOrder;
	/**
	 * 清单数据
	 */
	private List<CartModel> data_goods;
	
	private int Member_ID = 0;
	private String Member_Name, OrderID;
	private String ReceiveName, Address, Mobile, Date, Note, Price;
	private int year, month, day, hour;
	
	private Handler myHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				ProgressBar_Loading.dismiss(DingDanCopyActivity.this);
				
				for (int i = 0; i < data_address.size(); i++) {	//选择客户id后获取到该客户的默认地址			
					if (data_address.get(i).isIsDefault()) {
						String shengshixian = AreaManager.getInstance(DingDanCopyActivity.this).getAddress(data_address.get(i).getArea_ID());
						L.e("getArea_ID:" + data_address.get(i).getArea_ID());
						L.e("shengshixian:" + shengshixian);
						Address = shengshixian + data_address.get(i).getAddress();					
						ReceiveName = data_address.get(i).getName();
						Mobile = data_address.get(i).getMobile();
						
						address.setText("收货地址：" + Address);
						name.setText(ReceiveName);					
						phone.setText("联系电话：" + Mobile);
	//					tel = data_address.get(i).getTel();					
					}				
				}
														
				break;
			case HanderUtil.case2:
				ProgressBar_Loading.dismiss(DingDanCopyActivity.this);
				showCustomToast((String) msg.obj, false);
				break;
			
			case HanderUtil.case3://订单提交成功
				ProgressBar_Loading.dismiss(DingDanCopyActivity.this);
				Intent intent = new Intent(DingDanCopyActivity.this, SubmitActivity.class);
				intent.putExtra("count", count.getText().toString());
				intent.putExtra("result", OrderID);
				intent.putExtra("flage", "DING");
				L.e("复制得到的订单号：" + OrderID);
				startActivity(intent);
//				finish();								
				break;
			
			case HanderUtil.case4:	
				//获取到未改变的清单的字符串
				break;
				
			case HanderUtil.case5:	
				
				break;	
				
			case HanderUtil.case6:	
				ProgressBar_Loading.dismiss(DingDanCopyActivity.this);
				Member_Name = order_SetOrder.getMember_Name();
				ReceiveName = order_SetOrder.getReceiveName();
				Address = order_SetOrder.getReceiveAddress();
				Mobile = order_SetOrder.getReceiveMobile();
				L.e("ReceiveName:" + ReceiveName);
				L.e("Mobile:" + Mobile);
				
				Date = order_SetOrder.getReceiveDate().substring(0,10);
				Note = order_SetOrder.getNote();
				Price = order_SetOrder.getPrice() + "";
				
				tv_kehu.setText(Member_Name);
				address.setText("收货地址：" + Address);
				name.setText(ReceiveName);
								
				phone.setText("联系电话：" + Mobile);
//				time_dinghuo.setText(Date);
				beizhu.setText(Note);
				price.setText("￥" + Price);
				count.setText(Price);
				
				Member_ID = order_SetOrder.getMember_ID();
				Member_Name = order_SetOrder.getMember_Name();	
				
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
		setContentView(R.layout.activity_dinghuodan);
				
		init();
		ProgressBar_Loading.showProgressBar(DingDanCopyActivity.this, true, "Loading....");
		getListDatails(OrderID);
		getGoodsLists(OrderID);
	}
	
	public void onResume() {
		L.e("进入订货单的onResume方法");
		//在这里获取清单/购物车的数据集合，一个是一进来获取到总价，另一个提交订单时要获取到清单产品详细信息
//		data_goods = CartManager.getInstance(DingDanCopyActivity.this).getCartModels();			    
//	    float money_count = 0;
//	    for (int i = 0; i < data_goods.size(); i++) {					
//			money_count = money_count + data_goods.get(i).getPrice() * data_goods.get(i).getQuantity();
//						
//		}
	    
//	    Intent intent = getIntent();//Fragment内用到getActivity()
//		if (intent.getStringExtra("flage").equals("gouwuche")) {
//			price.setText("￥" + intent.getStringExtra("count"));//统一传来的总价为数字，单位自加。			
//			count.setText(intent.getStringExtra("count"));//布局加了￥，传值的时候方便
//			
//		}else if (intent.getStringExtra("flage").equals("xiangqing")) {
//			price.setText("￥" + money_count);	
//			count.setText(money_count + "");
//		}
	    		
		super.onResume();
	}
	
	private void init(){
		//注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constant.DELETE_ADDRESS_ACTION);
		intentFilter.setPriority(Integer.MAX_VALUE);
		this.registerReceiver(broadcastReceiver, intentFilter);
				
		beizhu = (EditText)findViewById(R.id.beizhu);
		
		tv_kehu = (TextView)findViewById(R.id.tv_kehu);
		address = (TextView)findViewById(R.id.address);
		price = (TextView)findViewById(R.id.price);
		count = (TextView)findViewById(R.id.count);
		title = (TextView)findViewById(R.id.title);
		name = (TextView)findViewById(R.id.name);
		phone = (TextView)findViewById(R.id.phone);
		time_dinghuo = (TextView)findViewById(R.id.time_dinghuo);
		
		Intent intent = getIntent();
		OrderID = intent.getStringExtra("order_id");	
		
		back = (LinearLayout)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		time = (RelativeLayout)findViewById(R.id.time);
		//在fragment不能用showDialog，要用DialogFragment，这里调用它的一个接口并实现它的方法chooseTime。
		time.setOnClickListener(new OnClickListener() {
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@SuppressLint("NewApi")
			public void onClick(View v) {
	            
				TimeFragment timeFragment = new TimeFragment();
				timeFragment.setIndex(DingDanCopyActivity.this,
						DINGHUO_TIME_TYPE);
				timeFragment.show(getSupportFragmentManager(), "dialog");
			}			
		});
		
		kehu = (RelativeLayout)findViewById(R.id.kehu);
		kehu.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(DingDanCopyActivity.this, CustomersListActivity.class);
				//选择客户，多选传“0”，单选传“1”（字符串类型）
				intent.putExtra("mode", "0");
				intent.putExtra("select", 1);
				//传递分组名称
				intent.putExtra("groupName", "分类");
				intent.putExtra("Tittle", "选择客户");
				startActivityForResult(intent, 2);
				
				tv_kehu.setText("");
				name.setText("");//跳转的时候防止把默认的删除了回来的时候还有，所以跳转时就清空
				address.setText("收货地址：");				
				phone.setText("联系电话：");
			}
		});
		info = (RelativeLayout)findViewById(R.id.info);
		info.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (Member_ID == 0) {
					showCustomToast("请先选择客户！", false);
//					Toast.makeText(DingDanCopyActivity.this, "请先选择客户！", Toast.LENGTH_SHORT).show();
				}else {
					Intent intent = new Intent(DingDanCopyActivity.this, AddressActivity.class);
					intent.putExtra("flage", "DINGHUO");
					intent.putExtra("Member_ID", Member_ID + "");
//					intent.putExtra("Name", Member_Name);
					startActivityForResult(intent, 1);
				}	
			}
		});
		qingdan = (RelativeLayout)findViewById(R.id.qingdan);
		qingdan.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(DingDanCopyActivity.this, GoodsListActivity.class);
				intent.putExtra("order_id", OrderID);//更改订单的时候需要这些数据
				intent.putExtra("Member_ID", Member_ID + "");	
				intent.putExtra("flage", "DING");					
				L.e("复制时要传递的Member_ID：" + Member_ID);
				intent.putExtra("Member_Name", Member_Name);
				intent.putExtra("ReceiveName", ReceiveName);
				intent.putExtra("Address", Address);
				intent.putExtra("Mobile", Mobile);				
				intent.putExtra("Date", Date);
				intent.putExtra("Note", Note);
				intent.putExtra("Price", Price);
				
				startActivityForResult(intent, 3);
			}
		});
		
		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH) + 1;
		day = c.get(Calendar.DAY_OF_MONTH);
		hour = c.get(Calendar.HOUR_OF_DAY);
		if (month < 10 && day < 10) {
			time_dinghuo.setText(year + "-0" + month + "-0" + day);
			
		}else if(month > 10 && day < 10){
			time_dinghuo.setText(year + "-" + month + "-0" + day);
			
		}else if(month < 10 && day > 10){			
			time_dinghuo.setText(year + "-0" + month + "-" + day);
			
		}else {
			time_dinghuo.setText(year + "0" + month + "0" + day);
		}
		submit = (Button)findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!time_dinghuo.getText().toString().equals("")) {
					int nian = Integer.parseInt(time_dinghuo.getText().toString().substring(0, 4));
					int yue;	
					if (time_dinghuo.getText().toString().substring(5, 6).equals("0")) {
						yue = Integer.parseInt(time_dinghuo.getText().toString().substring(6, 7));
					}else {
						yue = Integer.parseInt(time_dinghuo.getText().toString().substring(5, 7));
					}				
					int ri = Integer.parseInt(time_dinghuo.getText().toString().substring(8, 10));

										
					if (nian == year) {//年份必须大于
						if (yue > month) {//月份也大于的时候不做限制
														
						}else if(yue == month){//月份等于的时候日期必须大于
							if (ri > day) {
								
							}else if (ri == day) {
								if (hour > Common.hours) {//今天几点之前不能改
									showCustomToast("到货时间不合理", false);
									return;
								}
							}else {
								showCustomToast("到货时间不合理", false);
								return;
							}	
							
						}else {
							showCustomToast("到货时间不合理", false);
							return;
						}														
					}else if (nian > year) {
						
					}else {
						showCustomToast("到货时间不合理", false);
						return;
					}
				}
				
				if (!name.getText().toString().equals("")) {
										
//					for (int i = 0; i < data_goods.size(); i++) {
//						goods = goods + data_goods.get(i).getProduct_ID() + "," + data_goods.get(i).getUnit_ID() + "," + 
//								data_goods.get(i).getPrice() + "," + data_goods.get(i).getQuantity() + "#";
//					}
					L.e("提交时的字符串：" + goods);					
					ProgressBar_Loading.showProgressBar(DingDanCopyActivity.this, true, "Loading....");
					UpdateOrder(goods);
																						
				}else {
					showCustomToast("请选择地址", false);
//					Toast.makeText(DingDanCopyActivity.this, "请选择地址！", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == 2) {//从地址列表选择
//			title.setVisibility(View.GONE);
			address.setVisibility(View.VISIBLE);
			name.setVisibility(View.VISIBLE);
			phone.setVisibility(View.VISIBLE);
			
			String shengshixian = AreaManager.getInstance(DingDanCopyActivity.this).getAddress(data.getIntExtra("Area_ID", 0));
			Address = shengshixian + data.getStringExtra("address");			

			ReceiveName = data.getStringExtra("name");
			Mobile = data.getStringExtra("phone");
			L.e("复制订单时选择到的地址：" + Address);
			address.setText("收货地址：" + Address);
			name.setText(ReceiveName);
			phone.setText("联系电话：" + Mobile);
			
		}else if(requestCode == 2){//选择客户
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getBundleExtra("bundle");
				arrayList = (ArrayList<SortModel>) bundle.getSerializable("result");
				
				Member_ID = arrayList.get(0).getID();
				Member_Name = arrayList.get(0).getCompany_name();
				tv_kehu.setText(Member_Name);
				ProgressBar_Loading.showProgressBar(DingDanCopyActivity.this, true, "Loading....");
				getAdress();
			}						
			
		//清单传来的产品字符串
		}else if (requestCode == 3 && resultCode == RESULT_OK) {
			if (data != null) {
				goods = data.getStringExtra("goods");
				L.e("获取到清单传来的字符串：" + goods);
				price.setText("￥" + data.getStringExtra("COUNT"));			
				count.setText(data.getStringExtra("COUNT"));//布局加了￥，传值的时候方便
			}						
		}
	}
	
		
	/**
	 * 获取订单详情
	 * @param json
	 */
	private void getListDatails(String orderID){						
		//实例化请求类
	    ZJRequest zjRequest = new ZJRequest();
	    zjRequest.setOrderID(orderID);
	    final String json = JsonUtil.toJson(zjRequest);
	    L.e("(订单详情)jsonString:" + json);
	    
	    //activity.showDialog("正在加载...");	    
		new Thread(){
	    	public void run() {
	    		try {
					String jsonString = DataUtil.callWebService(Methods.ORDER_DINGDAN_DATAILS, json);
					L.e("(订单详情)json:" + jsonString);
					if (jsonString != null) {
						Type type = new TypeToken<ZJResponse<Order_SetOrder>>(){}.getType();
					    ZJResponse zjResponse = JsonUtil.fromJson(jsonString, type);
					    if (zjResponse.getCode() == 0) {
					    	order_SetOrder = new Order_SetOrder();
					    	order_SetOrder = (Order_SetOrder) zjResponse.getResult();
					    	
						    myHandler.sendEmptyMessage(HanderUtil.case6);
						    
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
	
	/**
	 * 修改订单
	 */
	private void UpdateOrder(String goods){
		int Manager_ID = Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANGYUN_UID,"0"));//业务员编号
		Intent intent = getIntent();
		//封装进Insert的
		ZJRequest<Order_SetOrder> zjRequest = new ZJRequest<Order_SetOrder>();
		Order_SetOrder order_SetOrder = new Order_SetOrder();
		
		order_SetOrder.setAction("Update");
		order_SetOrder.setOrderID(intent.getStringExtra("order_id"));
		order_SetOrder.setType(1);//订单类型
		order_SetOrder.setManager_ID(Manager_ID);//业务员编号
		order_SetOrder.setManager_Name(MyApplication.getInstance().getSharedPreferences().
				getString(Constant.XMPP_MY_REAlNAME, ""));
		
		order_SetOrder.setMember_ID(Member_ID);//客户id
		order_SetOrder.setMember_Name(Member_Name);//客户名称
		order_SetOrder.setReceiveName(ReceiveName);//收货人姓名
		order_SetOrder.setReceiveTel(Mobile);
		order_SetOrder.setReceiveMobile(Mobile);
		
		order_SetOrder.setReceiveAddress(Address);		
		order_SetOrder.setReceiveDate(time_dinghuo.getText().toString());//期望到货时间
		order_SetOrder.setNote(beizhu.getText().toString());//备注
		order_SetOrder.setPrice(Float.parseFloat(count.getText().toString()));
		order_SetOrder.setPriceModify(Float.parseFloat(count.getText().toString()));
		order_SetOrder.setArray_ID(goods);//产品清单的数据组成的字符串
		
		zjRequest.setData(order_SetOrder);		
		final String json = JsonUtil.toJson(zjRequest);
	    L.e("(修改订单)json:" + json);
	   	  	    
		new Thread(){
			public void run() {
				try {			
					String jsonString = DataUtil.callWebService(Methods.ORDER_DINGDAN_SET, json);
					L.e("(修改订单)jsonString:" + jsonString);
					
					if (jsonString != null) {						
						Type type = new TypeToken<ZJResponse<Order_address>>(){}.getType();
					    ZJResponse zjResponse = JsonUtil.fromJson(jsonString, type);
					    
					    Message message = new Message();
					    if (zjResponse.getCode() == 0) {
//					    	data = zjResponse.getResults();					    	
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
	 * 获取订单产品列表（进入了复制订单页面，不改变清单列表直接再提交时  要获取原本的清单字符串）
	 */
	private void getGoodsLists(String orderID){
		//封装进Insert的
		ZJRequest zjRequest = new ZJRequest();			
		zjRequest.setOrderID(orderID);
		
	    final String json = JsonUtil.toJson(zjRequest);		
		L.e("(获取订单产品列表)json:" + json);
	    
		new Thread(){
			public void run() {
				try {			
					String jsonString = DataUtil.callWebService(Methods.ORDER_DINGDAN_GOODS, json);
					L.e("(获取订单产品列表)jsonString:" + jsonString);
					
					if (jsonString != null) {						
						Type type = new TypeToken<ZJResponse<CartModel>>(){}.getType();
					    ZJResponse zjResponse = JsonUtil.fromJson(jsonString, type);
					    
					    Message message = new Message();
					    if (zjResponse.getCode() == 0) {
					    	data_qingdan = zjResponse.getResults();	
					    	for (int i = 0; i < data_qingdan.size(); i++) {
								goods = goods + data_qingdan.get(i).getProduct_ID() + "," + data_qingdan.get(i).getUnit_ID() + "," + 
										data_qingdan.get(i).getPrice() + "," + data_qingdan.get(i).getQuantity() + "#";
							}
					    	goods = goods.substring(0, goods.length() - 1);
							L.e("要复制的产品清单的字符串（未修改）：" + goods);
					    	message.what = HanderUtil.case4;
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
	 * 显示地址
	 */
	private void getAdress(){
		//实例化请求类
	    ZJRequest zjRequest = new ZJRequest(); 
	    //业务员id
	    zjRequest.setMember_ID(Member_ID);
	    //把要传的参数组装成json字符串。
	    final String json = JsonUtil.toJson(zjRequest);
		
		L.e("(选择客户后获取默认地址)json:" + json);
	    
		new Thread(){
			public void run() {
				try {			
					String jsonString = DataUtil.callWebService(Methods.ORDER_ADDRESS_SHOW, json);
					L.e("(选择客户后获取默认地址)jsonString:" + jsonString);
					
					if (jsonString != null) {						
						Type type = new TypeToken<ZJResponse<Order_address>>(){}.getType();
					    ZJResponse zjResponse = JsonUtil.fromJson(jsonString, type);
					    
					    Message message = new Message();
					    if (zjResponse.getCode() == 0) {
					    	data_address = zjResponse.getResults();					    	
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
	
	/**
	 * 选择时间对话框
	 * @param time
	 * @param type
	 */
	public void chooseTime(String time, int type, long timeStamp) {
		// TODO Auto-generated method stub
		switch (type) {
		case DINGHUO_TIME_TYPE:
			time_dinghuo.setText(time);
			break;

		default:
			break;
		}
	}
	
	/**
	 * 广播接收器
	 */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			name.setText("");
			address.setText("收货地址：");				
			phone.setText("联系电话：");
		}		
	};
}
