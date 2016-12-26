package com.huishangyun.Channel.Orders;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Methods;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.model.CartModel;
import com.huishangyun.yun.R;



public class TuiGoodsListActivity extends Activity {
	private LinearLayout back;
	private ListView listView;	
	private TextView money, submit, text;
	private List<Map<String, Object>> data;
	
	private double money_count = 0.0;
	private boolean isCheck = true;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_shoping_car);
		
		text = (TextView)findViewById(R.id.text);
		text.setText("产品清单");		
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
						

		listView = (ListView)findViewById(R.id.listview);		
		listView.setAdapter(new myAdapter(this));
		
		money = (TextView)findViewById(R.id.money);
		getMoney();
		money.setText(String.valueOf(money_count));	
		
		submit = (TextView)findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
			}
		});
	}

	/**
	 * 获取下面合计总价的方法
	 */
	private void getMoney(){ 
		for (int i = 0; i < data.size(); i++) {					
			money_count = money_count + (Double)data.get(i).get("prices") * 
					(Integer)data.get(i).get("number");
		}
	}

	/**
	 * 自定义的BaseAdapter类
	 * @author Administrator
	 *
	 */
	private class myAdapter extends BaseAdapter{
		
		private LayoutInflater inflater;// 用来导入布局		
		
		public myAdapter(Context context) {// 构造器
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

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			
			final ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.list_goodslist_tui, null);
				holder.img = (ImageView) view.findViewById(R.id.img);
				holder.tv = (TextView) view.findViewById(R.id.name);
				holder.tv2 = (TextView) view.findViewById(R.id.price);
				holder.lin = (LinearLayout) view.findViewById(R.id.lin);
				holder.tv3 = (TextView) view.findViewById(R.id.nub);
				holder.tv4 = (TextView) view.findViewById(R.id.nub2);
				holder.img2 = (ImageView) view.findViewById(R.id.delet);
				holder.add = (ImageView) view.findViewById(R.id.add);
				holder.del = (ImageView) view.findViewById(R.id.del);
								
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}						
			holder.tv.setText(data.get(position).get("name").toString());
			holder.tv2.setText(data.get(position).get("prices").toString());			
			holder.tv3.setText(data.get(position).get("number").toString());
			
			holder.add.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int nub_count1 = Integer.parseInt(holder.tv3.getText().toString());				
					nub_count1 = nub_count1 + 1;
					holder.tv3.setText(String.valueOf(nub_count1));
					
					//点击一次增加，加一次这个item的单价	
					money_count = money_count + (Double)(data.get(position).get("prices"));						
					money.setText(String.valueOf(money_count));
					
					//增加后要改变data里面数量的值，以便后面删除notifyDataSetChanged()初始化为增加前的
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("number", nub_count1);
					map.put("prices", data.get(position).get("prices"));
					map.put("name", data.get(position).get("name"));
					data.set(position, map);	
				}
			});
			
			holder.del.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {					
					int nub_count2 = Integer.parseInt(holder.tv3.getText().toString());					
					if (nub_count2 - 1 < 1) {//小于1，赋值1总价不变
						nub_count2 = 1;
						holder.tv3.setText(String.valueOf(nub_count2));
						
					}else {//大于或等于1						
						nub_count2 = nub_count2 - 1;
						holder.tv3.setText(String.valueOf(nub_count2));
						//点击一次减少，减一次这个item的单价
						money_count = money_count - (Double)data.get(position).get("prices");			
						money.setText(String.valueOf(money_count));
						
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("number", nub_count2);
						map.put("prices", data.get(position).get("prices"));
						map.put("name", data.get(position).get("name"));
						data.set(position, map);
					}
				}
			});
			
			holder.img2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (isCheck == true) {
						holder.img2.setImageResource(R.drawable.un);
						holder.del.setVisibility(View.INVISIBLE);
						holder.add.setVisibility(View.INVISIBLE);
						holder.tv3.setVisibility(View.INVISIBLE);
						holder.tv4.setVisibility(View.VISIBLE);
						isCheck = false;
						
					}else if (isCheck == false) {
						holder.img2.setImageResource(R.drawable.sel);
						holder.del.setVisibility(View.VISIBLE);
						holder.add.setVisibility(View.VISIBLE);
						holder.tv3.setVisibility(View.VISIBLE);
						holder.tv4.setVisibility(View.GONE);							
						isCheck = true;
					}
					
//					AlertDialog.Builder builder = new Builder(TuiGoodsListActivity.this);
//					builder.setMessage("确认删除吗？");					 
//					builder.setTitle("提示");
//					builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//					    public void onClick(DialogInterface dialog, int which) {
//				 		
//					    data.remove(position);					    
//					    notifyDataSetChanged();		
//					    count.setText(data.size()+"件");	
//					    
//					    nub = 0.0;
//					    getMoney();
//					    money.setText(String.valueOf(nub));	
//					    }					 
//					});					  
//					builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//					   public void onClick(DialogInterface dialog, int which) {					 
//
//					   dialog.dismiss();					 
//					   }					 
//					});					  
//					builder.create().show();					 										
				}
			});
			
			return view;
		}
		
		public final class ViewHolder {// 写这个类是为了点击list的时候好调用view
			public LinearLayout lin;
			public ImageView img;
			public TextView tv;
			public TextView tv2;
			public TextView tv3;
			public TextView tv4;
			public ImageView add;
			public ImageView del;
			public ImageView img2;
		}
	}
	
	/**
	 * 获取订单产品列表
	 */
	private void getGoodsList(String orderID){
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
//					    if (zjResponse.getCode() == 0) {
//					    	data2.clear();
//					    	data2 = zjResponse.getResults();					    	
//					    	message.what = HanderUtil.case1;
//						    myHandler.sendMessage(message);
//						    
//						} else {								
//						    message.what = HanderUtil.case2;
//						    message.obj = zjResponse.getDesc();
//						    myHandler.sendMessage(message);
//						}
					}		
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
}
