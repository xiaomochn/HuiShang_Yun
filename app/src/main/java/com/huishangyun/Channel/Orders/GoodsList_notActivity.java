package com.huishangyun.Channel.Orders;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.ImageLoad.ImageLoader;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.View.ProgressBar_Loading;
import com.huishangyun.manager.ProductManager;
import com.huishangyun.model.CartModel;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.model.Methods;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.model.Order_GoodsList;
import com.huishangyun.yun.R;

public class GoodsList_notActivity extends BaseActivity{
	
	private LinearLayout back;
	private ListView listView;	
	private TextView money, submit, text;

	private List<CartModel> data, data2;
	private MyAdapter myAdapte;
	
	private double money_count = 0.0;
	
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				ProgressBar_Loading.dismiss(GoodsList_notActivity.this);
				data.clear(); 
				for ( int i = 0; i < data2.size(); i++) {
					data.add(data2.get(i));
				}
				initCount();
				myAdapte.notifyDataSetChanged();  				
				//dismissDialog();
				break;
				
			case HanderUtil.case2:
				ProgressBar_Loading.dismiss(GoodsList_notActivity.this);
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
		setContentView(R.layout.activity_shoping_car);
		
		text = (TextView)findViewById(R.id.text);
		if (getIntent().getStringExtra("flage").equals("DING")) {
			text.setText("产品清单");//看要不要做修改
		}else {
			
		}		
		init();
	}
	
	/**
	 * 一些控件的点击方法
	 */
	private void init(){
		back = (LinearLayout)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		money = (TextView)findViewById(R.id.money);
		
		listView = (ListView)findViewById(R.id.listview);
		myAdapte = new MyAdapter(this);
		data =new ArrayList<CartModel>();
		data2 =new ArrayList<CartModel>();				
		listView.setAdapter(myAdapte);
		
		//传入订单id读取该订单的清单列表
		L.e("该订单的产品清单：" + getIntent().getStringExtra("order_id"));
		ProgressBar_Loading.showProgressBar(GoodsList_notActivity.this, true, "Loading....");
		getGoodsLists(getIntent().getStringExtra("order_id"));	
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(GoodsList_notActivity.this, DatailsActivity.class);
				//把点击产品的id传给产品详情页。
				intent.putExtra("id", data.get(arg2).getProduct_ID() + "");
				L.e("订单清单的产品id:" + data.get(arg2).getProduct_ID());							
					
				startActivity(intent);	

			}
		});		
				
		submit = (TextView)findViewById(R.id.submit);
		submit.setVisibility(View.GONE);
	}
	
	/**
	 * 获取订单产品列表
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
					    	data2.clear();
					    	data2 = zjResponse.getResults();					    	
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
	 * 自定义的BaseAdapter类
	 * @author Administrator
	 *
	 */
	private class MyAdapter extends BaseAdapter{
		
		public LayoutInflater inflater;// 用来导入布局		
		
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

		private TextView nub;
		private EditText edt;
		public View getView(final int position, View view, ViewGroup parent) {
			
			final ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.list_goodslist_not, null);
				holder.img = (ImageView) view.findViewById(R.id.img);
				holder.tv = (TextView) view.findViewById(R.id.name);
				holder.price = (TextView) view.findViewById(R.id.price);
				holder.nub = (TextView) view.findViewById(R.id.nub);
				holder.danwei = (TextView) view.findViewById(R.id.danwei);
								
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
			int Product_ID = data.get(position).getProduct_ID();
			L.e("Product_ID:" + Product_ID);
			if (Product_ID > 0) {
				Order_GoodsList order_GoodsList = 
						ProductManager.getInstance(GoodsList_notActivity.this).getGoodsList(Product_ID);
				
				if(order_GoodsList != null){
					new ImageLoader(GoodsList_notActivity.this).DisplayImage(
							Common.getPath() + order_GoodsList.getSmallImg(), holder.img, false);	
				}
											
				holder.tv.setText(data.get(position).getProduct_Name());
				holder.price.setText("￥" + data.get(position).getPrice() + "/" + data.get(position).getUnit_Name());			
				
				//把传过来的flort类型数量转换为没有小数点
				String goods_nub = data.get(position).getQuantity().toString();
				holder.nub.setText(goods_nub.substring(0, goods_nub.indexOf(".")));	
				holder.danwei.setText(data.get(position).getUnit_Name());
			}
																									
			return view;
		}
				
		public class ViewHolder {// 写这个类是为了点击list的时候好调用view
			public ImageView img;
			public TextView tv;
			public TextView price;
			public TextView nub;
			public TextView danwei;
		}	
						
	}
	
	/**
	 * 把获取产品总数和总价的方法集成在一起，以便几处的调用
	 */
	private void initCount(){	    
	    money_count = 0;
	    for (int i = 0; i < data.size(); i++) {	
	    	L.e("测试getPrice:" + data.get(i).getPrice());
	    	L.e("测试getQuantity:" + data.get(i).getQuantity());
	    	L.e("测试money_count:" + data.get(i).getPrice() * data.get(i).getQuantity());
			money_count = money_count + data.get(i).getPrice() * data.get(i).getQuantity();
					
		}
	    money.setText(String.valueOf(money_count));
	}
				
}
