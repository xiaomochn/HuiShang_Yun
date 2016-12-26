package com.huishangyun.Channel.Orders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.View.ProgressBar_Loading;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Order_address;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.manager.AreaManager;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

public class AddressActivity extends BaseActivity {
	private LinearLayout back, build, content;
	private TextView text, text1, text2;
	private ImageView img_remind;
	private ListView listview;
	private List<Order_address> data_list = new ArrayList<Order_address>();
	private List<Order_address> data_list2 = new ArrayList<Order_address>();
	private MyAdapter myAdapter;
	private int Member_ID;
	private Bundle bundle;
	private String sheng, shi, xian;
	//给进入地址栏默认选中一个地址
	private int index = -1;
	
	private Handler myHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
			ProgressBar_Loading.dismiss(AddressActivity.this);
			if (data_list2.size() > 0) {
				img_remind.setVisibility(View.GONE);
				content.setVisibility(View.VISIBLE);
				
				data_list.clear();
				for ( int i = 0; i < data_list2.size(); i++) {
					data_list.add(data_list2.get(i));
											
				}								
				myAdapter.notifyDataSetChanged();//加载了数据不改变界面
				
			}else {
				img_remind.setVisibility(View.VISIBLE);
				content.setVisibility(View.GONE);
			}							
				break;
			case HanderUtil.case2:
				ProgressBar_Loading.dismiss(AddressActivity.this);
				showCustomToast((String) msg.obj, false);
				break;
			
			case HanderUtil.case3:
				ProgressBar_Loading.dismiss(AddressActivity.this);
				int i = (Integer)msg.obj;
				data_list.remove(i);
			    myAdapter.notifyDataSetChanged();			    
				showCustomToast("删除成功", true);
				
				if (data_list.size() > 0) {
			    	img_remind.setVisibility(View.GONE);
					content.setVisibility(View.VISIBLE);
				}else {
					img_remind.setVisibility(View.VISIBLE);
					content.setVisibility(View.GONE);
				}
				break;
			
			case HanderUtil.case4:	
				getAdress();//不用销毁进度条，显示完了再销毁
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
		setContentView(R.layout.activity_address);
		
		if (getIntent().getStringExtra("flage").equals("TUIHUO")) {//判断从哪个页面过来
			text = (TextView)findViewById(R.id.text);
			text.setText("退货信息");
		}
		Member_ID = Integer.parseInt(getIntent().getStringExtra("Member_ID"));
		text = (TextView)findViewById(R.id.text);
		text1 = (TextView)findViewById(R.id.text1);
		text2 = (TextView)findViewById(R.id.text2);
		img_remind = (ImageView)findViewById(R.id.img_remind);
		
		listview = (ListView)findViewById(R.id.listview);
		content = (LinearLayout)findViewById(R.id.content);		
		back = (LinearLayout)findViewById(R.id.back);
		build = (LinearLayout)findViewById(R.id.build);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		build.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(AddressActivity.this, AddressNewActivity.class);
				//判断是订单还是退单列表的跳转
				if (text.getText().toString().trim().equals("退货信息")) {					
					intent.putExtra("DINGTUI", "TUI");
				}else {
					intent.putExtra("DINGTUI", "DING");
				}				
				intent.putExtra("flage", "ZENG");
				intent.putExtra("Member_ID", Member_ID + "");
				L.e("新建地址传递的Member_ID" + Member_ID);
				startActivity(intent);
//				finish();
			}
		});
		myAdapter = new MyAdapter(this);				
		listview.setAdapter(myAdapter);				
	}
	
	@Override
	protected void onResume() {				
		super.onResume();
		L.e("进入onResume！");
		ProgressBar_Loading.showProgressBar(AddressActivity.this, true, "Loading....");
		getAdress();
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
		
		L.e("(地址显示)json:" + json);
	    
		new Thread(){
			public void run() {
				try {			
					String jsonString = DataUtil.callWebService(Methods.ORDER_ADDRESS_SHOW, json);
					L.e("(地址显示)jsonString:" + jsonString);
					
					if (jsonString != null) {						
						Type type = new TypeToken<ZJResponse<Order_address>>(){}.getType();
					    ZJResponse zjResponse = JsonUtil.fromJson(jsonString, type);
					    
					    Message message = new Message();
					    if (zjResponse.getCode() == 0) {
					    	data_list2.clear();
					    	data_list2 = zjResponse.getResults();					    	
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
	 * 删除地址
	 */
	private void delAddress(int ID, final int position){
		//实例化请求类
	    ZJRequest zjRequest = new ZJRequest(); 
	    //业务员id
	    zjRequest.setID(ID);
	  //把要传的参数组装成json字符串。
	    final String json = JsonUtil.toJson(zjRequest);
		
		L.e("(地址删除)json:" + json);
	    
		new Thread(){
			public void run() {
				try {			
					String jsonString = DataUtil.callWebService(Methods.ORDER_ADDRESS_DEL, json);
					L.e("(地址删除)jsonString:" + jsonString);
					
					if (jsonString != null) {						
						Type type = new TypeToken<ZJResponse<Order_address>>(){}.getType();
					    ZJResponse zjResponse = JsonUtil.fromJson(jsonString, type);
					    
					    Message message = new Message();
					    if (zjResponse.getCode() == 0) {					    					    	
					    	message.what = HanderUtil.case3;
					    	message.obj = position;
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
	
	private class MyAdapter extends BaseAdapter{
		
		private LayoutInflater inflater;// 用来导入布局

		public MyAdapter(Context context) {// 构造器
			this.inflater = LayoutInflater.from(context);
		}
				
		public int getCount() {
			return data_list.size();
		}

		@Override
		public Object getItem(int position) {
			return data_list.get(position);
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
				view = inflater.inflate(R.layout.list_address, null);
				
				holder.tv_name = (TextView) view.findViewById(R.id.name);
				holder.tv_address = (TextView) view.findViewById(R.id.address);
				holder.linearLayout1 = (LinearLayout) view.findViewById(R.id.moren);
				holder.img1 = (ImageView) view.findViewById(R.id.img1);
				holder.tv1 = (TextView) view.findViewById(R.id.tv1);
				
				holder.linearLayout2 = (LinearLayout) view.findViewById(R.id.bianji);
				holder.img2 = (ImageView) view.findViewById(R.id.img2);
				holder.tv2 = (TextView) view.findViewById(R.id.tv2);
				
				holder.linearLayout3 = (LinearLayout) view.findViewById(R.id.del);
				holder.img3 = (ImageView) view.findViewById(R.id.img3);
				holder.tv3 = (TextView) view.findViewById(R.id.tv3);
												
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			L.e("县的ID：", data_list.get(position).getArea_ID() + "");
			String shengshixian = "";
			if (data_list.get(position).getArea_ID() > 0) {
				shengshixian = AreaManager.getInstance(AddressActivity.this).getAddress(data_list.get(position).getArea_ID());
				L.e("shengshixian:" + shengshixian);
							
			}				
			holder.tv_name.setText("收货人：" + data_list.get(position).getName());
			holder.tv_address.setText("收货地址：" + data_list.get(position).getAddress());
			
			if (data_list.get(position).isIsDefault()) {//查一遍bool值是true的改变颜色，false的改为灰色。
				holder.tv1.setTextColor(getResources().getColor(R.color.tab_select));
				holder.img1.setImageResource(R.drawable.moren_sel);
				
				text1.setText("收货人：" + data_list.get(position).getName());
				text2.setText("联系电话：" + data_list.get(position).getMobile());
		
			}else {
				holder.tv1.setTextColor(getResources().getColor(R.color.text_no));
				holder.img1.setImageResource(R.drawable.moren);
			}
			
			
			listview.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					
					bundle = new Bundle();
					bundle.putString("name", data_list.get(position).getName());
					bundle.putInt("Area_ID", data_list.get(position).getArea_ID());
					L.e("listview传递的收货人名字:" + data_list.get(position).getName());
					L.e("listview传递的收货人地址:" + data_list.get(position).getAddress());
					bundle.putString("phone", data_list.get(position).getMobile());
					bundle.putString("tel", data_list.get(position).getTel());
					bundle.putString("address", data_list.get(position).getAddress());
					
					Intent intent = new Intent();
					intent.putExtras(bundle);
					setResult(2, intent);
					finish();
				}			
			});
			
			holder.linearLayout2.setOnClickListener(new OnClickListener() {//编辑地址
				public void onClick(View v) {
					Intent intent = new Intent(AddressActivity.this, AddressNewActivity.class);
					//新增地址和编辑地址两处都要传递
					if (text.getText().toString().trim().equals("退货信息")) {					
						intent.putExtra("DINGTUI", "TUI");
					}else {
						intent.putExtra("DINGTUI", "DING");
					}
					intent.putExtra("flage", "GAI");
					intent.putExtra("Member_ID", Member_ID + "");
					
					intent.putExtra("name", data_list.get(position).getName());					
					intent.putExtra("moble", data_list.get(position).getMobile());
					intent.putExtra("tel", data_list.get(position).getTel());

					//修改的时候把县的id要传过去给修改的ID3赋值，因为若不修改的话ID3就为0了。
					intent.putExtra("Area_ID", data_list.get(position).getArea_ID());
									
					intent.putExtra("adderss", data_list.get(position).getAddress());
					intent.putExtra("id", data_list.get(position).getID() + "");
					
					startActivity(intent);
				}
			});
			
			holder.linearLayout3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(AddressActivity.this);
					builder.setMessage("确认删除吗？");					 
					builder.setTitle("提示");
					builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int which) {
				 		
				    	if (data_list.get(position).isIsDefault()) {//当删除的是默认的地址 就发送一个广播。					    	
							AddressActivity.this.sendBroadcast(new Intent().setAction(Constant.DELETE_ADDRESS_ACTION));
							
							text1.setText("收货人：                ");//删除默认项上面的地址也清空
							text2.setText("联系电话：");
				    	}
					    	
				    	ProgressBar_Loading.showProgressBar(AddressActivity.this, true, "Loading....");
					    delAddress(data_list.get(position).getID(), position);					    
					    				   					    
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
												
			holder.linearLayout1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				if (data_list.get(position).isIsDefault()) {
					
				}else {
					ProgressBar_Loading.showProgressBar(AddressActivity.this, true, "Loading....");
					changeIsDefault(position);	
				}

			}
		});
			return view;
		}
		
		public final class ViewHolder {// 写这个类是为了点击list的时候好调用view			
			public TextView tv_name;
			public TextView tv_address;
			public LinearLayout linearLayout1;
			public TextView tv1;
			public ImageView img1;
			
			public LinearLayout linearLayout2;
			public TextView tv2;
			public ImageView img2;
			
			public LinearLayout linearLayout3;
			public TextView tv3;
			public ImageView img3;
		}
	}
	
	/**
	 * 修改某一项的默认设置
	 * @param position
	 * @param isMoRen
	 */
	private void changeIsDefault(int position){
		//封装进Insert的
		ZJRequest<Order_address> zjRequest = new ZJRequest<Order_address>();
		Order_address order_address = data_list.get(position);
		order_address.setMember_ID(Member_ID);
		order_address.setAction("Update");//更改的时候Action
		order_address.setIsDefault(true);//将新增的地址设为默认地址
		
		zjRequest.setData(order_address);
		final String json = JsonUtil.toJson(zjRequest);
	    L.e("(更改默认)json:" + json);
	    			    
		new Thread(){
			public void run() {
				String jsonString = DataUtil.callWebService(Methods.ORDER_ADDRESS_ADD, json);
				L.e("(更改默认)jsonString:" + jsonString);
				try {										
					if (jsonString != null) {
						Type type = new TypeToken<ZJResponse<Order_address>>(){}.getType();
					    ZJResponse zjResponse = JsonUtil.fromJson(jsonString, type);
					    
					    Message message = new Message();
					    if (zjResponse.getCode() == 0) {					    	
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
}
