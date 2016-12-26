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
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Plan.CustomersListActivity;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.View.ProgressBar_Loading;
import com.huishangyun.manager.CartManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.model.Order_SetOrder;
import com.huishangyun.model.Order_address;
import com.huishangyun.Channel.Plan.SortModel;
import com.huishangyun.Fragment.TimeFragment;
import com.huishangyun.Fragment.TimeFragment.TimeFace;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.manager.AreaManager;
import com.huishangyun.model.CartModel;
import com.huishangyun.yun.R;

public class Fragment_HuoDanDing extends Fragment implements TimeFace{
	
	private RelativeLayout kehu, info, time, qingdan;
	private TextView tv_kehu, price, address, count, title, name, phone, time_dinghuo;
	private EditText beizhu;
	private Button submit;
	private static final int DINGHUO_TIME_TYPE = 0;
	private static final int MODE_PRIVATE = 0;
	private static final int RESULT_OK = 0;	
	private List<Order_address> data = new ArrayList<Order_address>();
	
	private int Member_ID = 0;
	private String Member_Name;
	private int Manager_ID;
	private String tel, addressStr, mobile, nameStr;//线程获取到的完成的地址
	private ArrayList<SortModel> arrayList; 
	private String results;
	/**
	 * 清单数据
	 */
	private List<CartModel> data_goods;
	
	private Handler myHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				ProgressBar_Loading.dismiss(getActivity());
				for (int i = 0; i < data.size(); i++) {	//选择客户id后获取到该客户的默认地址			
					if (data.get(i).isIsDefault()) {
						L.i("读接口获取到data.get(i).getArea_ID()：" + data.get(i).getArea_ID());
						if (data.get(i).getArea_ID() > 0) {
							String shengshixian = AreaManager.getInstance(getActivity()).getAddress(data.get(i).getArea_ID());					
							
						}
						addressStr = data.get(i).getAddress();	
						address.setText("收货地址：" + addressStr);					
						nameStr = data.get(i).getName();
						name.setText(nameStr);
						mobile = data.get(i).getMobile();
						phone.setText("联系电话：" + mobile);
						tel = data.get(i).getTel();	
						
						L.i("获取到选中的nameStr：" + nameStr);
						L.i("获取到选中的mobile：" + mobile);
						L.i("获取到选中的nameStr：" + nameStr);
					}				
				}
														
				break;
			case HanderUtil.case2:
				ProgressBar_Loading.dismiss(getActivity());
				((DingDanActivity) getActivity()).showCustomToast((String) msg.obj, false);
				break;
			
			case HanderUtil.case3://订单提交成功
				ProgressBar_Loading.dismiss(getActivity());
				long nub = CartManager.getInstance(getActivity()).deleteAll();
				L.e("清除购物车返回的nub:" + nub);
				if (nub > 0) {
					L.e("数据库删除成功");
				}else {
					L.e("数据库删除失败");
				}
												
				((DingDanActivity) getActivity()).showCustomToast("提交成功", true);
				Intent intent = new Intent(getActivity(), SubmitActivity.class);
				intent.putExtra("result", results);
				intent.putExtra("count", count.getText().toString());
				intent.putExtra("flage", "DING");
				startActivity(intent);

				getActivity().finish();
				break;
				
			default:
				break;
			}
		};
	};
	
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_huodanding, container, false);
		
		Manager_ID = Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANGYUN_UID,"0"));//业务员编号
		
		kehu = (RelativeLayout)view.findViewById(R.id.kehu);
		info = (RelativeLayout)view.findViewById(R.id.info);
		qingdan = (RelativeLayout)view.findViewById(R.id.qingdan);
		time = (RelativeLayout)view.findViewById(R.id.time);
		tv_kehu = (TextView)view.findViewById(R.id.tv_kehu);
		time_dinghuo = (TextView)view.findViewById(R.id.time_dinghuo);
		
		address = (TextView)view.findViewById(R.id.address);
		title = (TextView)view.findViewById(R.id.title);
		
		name = (TextView)view.findViewById(R.id.name);
		phone = (TextView)view.findViewById(R.id.phone);
		
		beizhu = (EditText)view.findViewById(R.id.beizhu);
		submit = (Button)view.findViewById(R.id.submit);
		
		price = (TextView)view.findViewById(R.id.price);	
		count = (TextView)view.findViewById(R.id.count);
											
		init();
		
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Manager_ID", MODE_PRIVATE);
		Member_ID = sharedPreferences.getInt("Manager_ID", 0);
		if (Member_ID != 0) {
			kehu.setVisibility(View.GONE);
		}
		L.e("onCreateView的Member_ID：" + Member_ID);
		return view;
	}
	
	/**
	 * 从客户进来传Member_ID默认一个地址，从订单进来没有默认地址。
	 * Manager_ID查客户，Member_ID查地址。
	 */
	public void onResume() {
		L.e("进入订货单的onResume方法");	
		//所有到这个界面或改变价格后回到这个界面 都要重新计算一遍价钱
		data_goods = CartManager.getInstance(getActivity()).getCartModels();
	    float money_count = 0;
	    for (int i = 0; i < data_goods.size(); i++) {					
			money_count = money_count + data_goods.get(i).getPrice() * data_goods.get(i).getQuantity();
						
		}	   
		price.setText("￥" + money_count);	
		count.setText(money_count + "");
		
		super.onResume();
	}
	
	private void init() {
		//注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constant.DELETE_ADDRESS_ACTION);
		intentFilter.setPriority(Integer.MAX_VALUE);
		getActivity().registerReceiver(broadcastReceiver, intentFilter);
		
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

					Calendar c = Calendar.getInstance();
					int year = c.get(Calendar.YEAR);
					int month = c.get(Calendar.MONTH) + 1;
					int day = c.get(Calendar.DAY_OF_MONTH);
					int hour = c.get(Calendar.HOUR_OF_DAY);
					
					L.e("nian:" + nian);
					L.e("yue:" + yue);
					L.e("ri:" + ri);
					if (nian == year) {//年份必须大于
						if (yue > month) {//月份也大于的时候不做限制
							
							
						}else if(yue == month){//月份等于的时候日期必须大于
							if (ri > day) {
								
							}else if (ri == day) {
								if (hour > Common.hours) {//今天几点之前不能改
									((DingDanActivity) getActivity()).showCustomToast("到货时间不合理", false);
									return;
								}
							}else {
								((DingDanActivity) getActivity()).showCustomToast("到货时间不合理", false);
								return;
							}
							
						}else {
							((DingDanActivity) getActivity()).showCustomToast("到货时间不合理", false);
							return;
						}														
					}else if (nian > year) {
						
					}else {
						((DingDanActivity) getActivity()).showCustomToast("到货时间不合理", false);
						return;
					}
				}
				
												
				if (name.getText().toString().trim().equals("")) {										
					((DingDanActivity) getActivity()).showCustomToast("请选择客户地址", false);
					
				}else if (address.getText().toString().trim().equals("收货地址：")) {					
					((DingDanActivity) getActivity()).showCustomToast("客户地址不完整", false);
					
				}else if (phone.getText().toString().trim().equals("联系电话：")) {					
					((DingDanActivity) getActivity()).showCustomToast("客户地址不完整", false);
					
				}else {
					if (data_goods.size() > 0) {
						String goods = "";
						for (int i = 0; i < data_goods.size(); i++) {
							goods = goods + data_goods.get(i).getProduct_ID() + "," + data_goods.get(i).getUnit_ID() + "," + 
									data_goods.get(i).getPrice() + "," + data_goods.get(i).getQuantity() + "#";
						}
						goods = goods.substring(0, goods.length() - 1);
						L.e("产品清单的字符串：" + goods);
						ProgressBar_Loading.showProgressBar(getActivity(), true, "Loading....");
						submitOrder(goods);
												
					}else {
						((DingDanActivity) getActivity()).showCustomToast("产品清单为空", false);
					}
				}
				
			}
		});
		kehu.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), CustomersListActivity.class);
				//选择客户，多选传“0”，单选传“1”（字符串类型）
				intent.putExtra("mode", "0");
				intent.putExtra("select", 1);
				//传递分组名称
				intent.putExtra("groupName", "分类");
				intent.putExtra("Tittle", "选择客户");
				startActivityForResult(intent, 2);	
				
				name.setText("");//跳转的时候防止把默认的删除了回来的时候还有，所以跳转时就清空
				address.setText("收货地址：");				
				phone.setText("联系电话：");
			}
		});
			
		info.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (Member_ID == 0) {
					((DingDanActivity) getActivity()).showCustomToast("请先选择客户", false);
				}else {
					Intent intent = new Intent(getActivity(), AddressActivity.class);
					intent.putExtra("flage", "DINGHUO");
					intent.putExtra("Member_ID", Member_ID + "");
//					intent.putExtra("Name", Member_Name);
					startActivityForResult(intent, 1);
				}				
			}
		});
		qingdan.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ShopCar_GoodsListActivity.class);	
				intent.putExtra("flage", "DINGDAN");
				startActivityForResult(intent, 3);//从清单获取到总价
			}
		});
		
		//在fragment不能用showDialog，要用DialogFragment，这里调用它的一个接口并实现它的方法chooseTime。
		time.setOnClickListener(new OnClickListener() {
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@SuppressLint("NewApi")
			public void onClick(View v) {
	            
				TimeFragment timeFragment = new TimeFragment();
				timeFragment.setIndex(Fragment_HuoDanDing.this,
						DINGHUO_TIME_TYPE);
				timeFragment.show(getActivity().getSupportFragmentManager(), "dialog");
			}			
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == 2) {//从地址列表选择
			title.setVisibility(View.GONE);
			address.setVisibility(View.VISIBLE);
			name.setVisibility(View.VISIBLE);
			phone.setVisibility(View.VISIBLE);
			
			if (data.getIntExtra("Area_ID", 0) > 0) {
				L.e("从获取客户得到的Area_ID：" + data.getIntExtra("Area_ID", 0));
				String shengshixian = AreaManager.getInstance(getActivity()).getAddress(data.getIntExtra("Area_ID", 0));
				
			}
			addressStr = data.getStringExtra("address");	
			address.setText("收货地址：" + addressStr);			
			nameStr = data.getStringExtra("name");			
			mobile = data.getStringExtra("phone");
			name.setText(nameStr);
			phone.setText("联系电话：" + mobile);	
			L.i("从获取客户得到的nameStr：" + nameStr);
			L.i("从获取客户得到的mobile：" + mobile);
			
		}else if(requestCode == 2){//选择客户
			if (resultCode == getActivity().RESULT_OK) {
				Bundle bundle = data.getBundleExtra("bundle");
				arrayList = (ArrayList<SortModel>) bundle.getSerializable("result");
				
				Member_ID = arrayList.get(0).getID();
				Member_Name = arrayList.get(0).getCompany_name();
				tv_kehu.setText(Member_Name);
				ProgressBar_Loading.showProgressBar(getActivity(), true, "Loading....");
				getAdress();
			}									
			
		}
	}
	
	/**
	 * 创建订单
	 */
	private void submitOrder(String goods){
		//封装进Insert的
		ZJRequest<Order_SetOrder> zjRequest = new ZJRequest<Order_SetOrder>();
		Order_SetOrder order_SetOrder = new Order_SetOrder();
		
		order_SetOrder.setAction("Insert");
		order_SetOrder.setType(1);//订单类型
		
		order_SetOrder.setManager_ID(Manager_ID);//业务员编号
		order_SetOrder.setManager_Name(MyApplication.getInstance().getSharedPreferences().
				getString(Constant.XMPP_MY_REAlNAME, ""));
		order_SetOrder.setMember_ID(Member_ID);//客户id
		order_SetOrder.setMember_Name(Member_Name);//客户名称
		order_SetOrder.setReceiveName(nameStr);//收货人姓名
		order_SetOrder.setReceiveTel(tel);
		order_SetOrder.setReceiveMobile(mobile);
		
		//order_SetOrder.setReceivePostCode(true);//邮编
		order_SetOrder.setReceiveAddress(addressStr);	
		if(!time_dinghuo.getText().toString().equals("")){//该参数可以不传，但为空的时候不传此空参数。
			order_SetOrder.setReceiveDate(time_dinghuo.getText().toString());//期望到货时间
		}		
		order_SetOrder.setNote(beizhu.getText().toString());//备注
		order_SetOrder.setPrice(Float.parseFloat(count.getText().toString()));
		order_SetOrder.setArray_ID(goods);//产品清单的数据组成的字符串
		//xsl修改【start】
		order_SetOrder.setOperationID(MyApplication.getInstance().getManagerID());
		order_SetOrder.setOperationName(MyApplication.preferences.getString(Constant.XMPP_MY_REAlNAME, ""));
		//xsl修改【end】
		zjRequest.setData(order_SetOrder);		
		final String json = JsonUtil.toJson(zjRequest);
	    L.e("(创建订单)json:" + json);
	    
		new Thread(){
			public void run() {
				try {			
					String jsonString = DataUtil.callWebService(Methods.ORDER_DINGDAN_SET, json);
					L.e("(创建订单)jsonString:" + jsonString);
					
					if (jsonString != null) {						
						Type type = new TypeToken<ZJResponse<String>>(){}.getType();
					    ZJResponse<String> zjResponse = JsonUtil.fromJson(jsonString, type);
					    
					    Message message = new Message();
					    if (zjResponse.getCode() == 0) {
					    	results = zjResponse.getResult();	
					    	L.e("results:" + results);
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
					    	data = zjResponse.getResults();					    	
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
	
	@Override
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
	
	public void onDestroy() {//注销广播
		getActivity().unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	};
}
