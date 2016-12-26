package com.huishangyun.Channel.Orders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.View.ProgressBar_Loading;
import com.huishangyun.model.Order_SetOrder;
import com.huishangyun.model.Order_address;
import com.huishangyun.Channel.Plan.CustomersListActivity;
import com.huishangyun.Channel.Plan.SortModel;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.manager.AreaManager;
import com.huishangyun.manager.CartManager;
import com.huishangyun.model.CartModel;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

public class Fragment_HuoDanTui extends Fragment {
	
	private static final int MODE_PRIVATE = 0;
	private RelativeLayout info, qingdan, kehu;
	private TextView tv_kehu, name, phone, address, price, count, title;
	private EditText beizhu;
	private Button submit;
	
	private int Member_ID = 0;
	private String Member_Name;
	private ArrayList<SortModel> arrayList; 
	private List<Order_address> data = new ArrayList<Order_address>();
	private String addressStr;//线程获取到的完成的地址
	private String tel, nameStr, mobile;
	private String shengshixian;
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
						L.e("data.get(i).getArea_ID():" + data.get(i).getArea_ID());
						if (data.get(i).getArea_ID() > 0) {
							shengshixian = AreaManager.getInstance(getActivity()).getAddress(data.get(i).getArea_ID());
							
						}
						addressStr = data.get(i).getAddress();
						address.setText("退货地址：" + addressStr);					
						tel = data.get(i).getTel();
						nameStr = data.get(i).getName();
						mobile = data.get(i).getMobile();				
						name.setText(nameStr);
						phone.setText("联系电话：" + mobile);					
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
				intent.putExtra("flage", "TUI");
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
		
		View view = inflater.inflate(R.layout.fragment_huodantui, container, false);
		
		kehu = (RelativeLayout)view.findViewById(R.id.kehu);
		info = (RelativeLayout)view.findViewById(R.id.info);
		qingdan = (RelativeLayout)view.findViewById(R.id.qingdan);
		
		tv_kehu = (TextView)view.findViewById(R.id.tv_kehu);
		title = (TextView)view.findViewById(R.id.title);
		name = (TextView)view.findViewById(R.id.name);
		phone = (TextView)view.findViewById(R.id.phone);
		address = (TextView)view.findViewById(R.id.address);
		
		price = (TextView)view.findViewById(R.id.price);
		count = (TextView)view.findViewById(R.id.count);
				
		beizhu = (EditText)view.findViewById(R.id.beizhu);
		submit = (Button)view.findViewById(R.id.submit);
		
		init();
		
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Manager_ID", MODE_PRIVATE);
		Member_ID = sharedPreferences.getInt("Manager_ID", 0);
		if (Member_ID != 0) {
			kehu.setVisibility(View.GONE);
		}
		L.e("onCreateView的Member_ID：" + Member_ID);
		
		return view;
	}
	
	public void onResume() {
		L.e("进入订货单的onResume方法");		
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
				
				if (name.getText().toString().trim().equals("")) {										
					((DingDanActivity) getActivity()).showCustomToast("请选择客户地址", false);
					
				}else if (address.getText().toString().trim().equals("退货地址：")) {					
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
		info.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (Member_ID == 0) {
					((DingDanActivity) getActivity()).showCustomToast("请先选择客户", false);
				}else {
					Intent intent = new Intent(getActivity(), AddressActivity.class);
					intent.putExtra("flage", "TUIHUO");
					intent.putExtra("Member_ID", Member_ID + "");
					intent.putExtra("Name", Member_Name);
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
		kehu.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), CustomersListActivity.class);
				intent.putExtra("mode", "0");//0是客户，1是人员
				intent.putExtra("select", 1);//0是多选，1是单选
				//传递分组名称
				intent.putExtra("groupName", "分类");
				intent.putExtra("Tittle", "选择客户");
				startActivityForResult(intent, 2);
				
				name.setText("");//跳转的时候防止把默认的删除了回来的时候还有，所以跳转时就清空
				address.setText("退货地址：");				
				phone.setText("联系电话：");								
			}
		});
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == 2) {
			title.setVisibility(View.GONE);
			address.setVisibility(View.VISIBLE);
			name.setVisibility(View.VISIBLE);
			phone.setVisibility(View.VISIBLE);
			
			if (data.getIntExtra("Area_ID", 0) > 0) {
				String shengshixian = AreaManager.getInstance(getActivity()).getAddress(data.getIntExtra("Area_ID", 0));			
				
			}
			addressStr = data.getStringExtra("address");
			address.setText("退货地址：" + addressStr);
			nameStr = data.getStringExtra("name");			
			mobile = data.getStringExtra("phone");
			name.setText(nameStr);
			phone.setText("联系电话：" + mobile);	
			
		}else if(requestCode == 2){//选择客户
			if (resultCode == -1) {
				Bundle bundle = data.getBundleExtra("bundle");
				arrayList = (ArrayList<SortModel>) bundle.getSerializable("result");
				
				Member_ID = arrayList.get(0).getID();
				Member_Name = arrayList.get(0).getCompany_name();
				tv_kehu.setText(Member_Name);
				L.e("Member_Name:" + Member_Name);
				ProgressBar_Loading.showProgressBar(getActivity(), true, "Loading....");
				getAdress();
			}
			
		}
	}
	
	/**
	 * 创建退货单
	 */
	private void submitOrder(String goods){
		//封装进Insert的
		ZJRequest<Order_SetOrder> zjRequest = new ZJRequest<Order_SetOrder>();
		Order_SetOrder order_SetOrder = new Order_SetOrder();
		
		order_SetOrder.setAction("Insert");
		order_SetOrder.setType(-1);//订单类型
		int Manager_ID = Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANGYUN_UID,"0"));//业务员编号
		order_SetOrder.setManager_ID(Manager_ID);//业务员编号
		order_SetOrder.setManager_Name(MyApplication.getInstance().getSharedPreferences().
				getString(Constant.XMPP_MY_REAlNAME, ""));
		order_SetOrder.setMember_ID(Member_ID);//客户id
		order_SetOrder.setMember_Name(Member_Name);//客户名称
		order_SetOrder.setReceiveName(nameStr);//收货人姓名
		order_SetOrder.setReceiveTel(tel);
		order_SetOrder.setReceiveMobile(mobile);
		
		order_SetOrder.setReceiveAddress(addressStr);		
		order_SetOrder.setNote(beizhu.getText().toString());//备注
		order_SetOrder.setPrice(Float.parseFloat(count.getText().toString()));
		order_SetOrder.setArray_ID(goods);//产品清单的数据组成的字符串
		
		zjRequest.setData(order_SetOrder);		
		final String json = JsonUtil.toJson(zjRequest);
	    L.e("(创建退货单)json:" + json);
	    
		new Thread(){
			public void run() {
				try {			
					String jsonString = DataUtil.callWebService(Methods.ORDER_DINGDAN_SET, json);
					L.e("(创建退货单)jsonString:" + jsonString);
					
					if (jsonString != null) {						
						Type type = new TypeToken<ZJResponse<String>>(){}.getType();
					    ZJResponse<String> zjResponse = JsonUtil.fromJson(jsonString, type);
					    
					    Message message = new Message();
					    if (zjResponse.getCode() == 0) {
					    	results = zjResponse.getResult();				    	
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
		
		L.e("(退货单选择客户后获取默认地址)json:" + json);
	    
		new Thread(){
			public void run() {
				try {			
					String jsonString = DataUtil.callWebService(Methods.ORDER_ADDRESS_SHOW, json);
					L.e("(退货单选择客户后获取默认地址)jsonString:" + jsonString);
					
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
	
	/**
	 * 广播接收器
	 */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			name.setText("");
			address.setText("退货地址：");				
			phone.setText("联系电话：");
		}
		
	};
	
	
	public void onDestroy() {
		getActivity().unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	};
	
}
