package com.huishangyun.Channel.Orders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.View.ProgressBar_Loading3;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.Methods;
import com.huishangyun.model.Order_SetOrder;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.yun.R;

public class Fragment_DingDanMy extends Fragment implements MyXListView.MyXListViewListener {

	private ImageView img;
	private MyXListView listView;
	private List<Order_SetOrder> data = new ArrayList<Order_SetOrder>();
	private List<Order_SetOrder> data2 = new ArrayList<Order_SetOrder>();
	private MyAdapter myAdapter;
	private int index = 1, size = 10;
	private boolean flag = true; 
	private View view;
	
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				ProgressBar_Loading3.dismiss(getActivity());
				listView.stopRefresh();
				listView.stopLoadMore();
				listView.setRefreshTime();
//				data.clear(); 				
				if (data2.size() > 0) {	
					img.setVisibility(View.GONE);
					for ( int i = 0; i < data2.size(); i++) {
						data.add(data2.get(i));					
					}
					myAdapter.notifyDataSetChanged();
					
				}else {
					if (index == 1) {
						img.setVisibility(View.VISIBLE);
	//					((DingDanListActivity) getActivity()).showCustomToast("没有更多数据", false);  								
					}
				}
				
				break;
				
			case HanderUtil.case2:
				ProgressBar_Loading3.dismiss(getActivity());
				listView.stopRefresh();
				listView.setRefreshTime();
				((DingDanListActivity) getActivity()).showCustomToast((String) msg.obj, false);
				break;
				
			case HanderUtil.case3:	
				ProgressBar_Loading3.dismiss(getActivity());
				data.remove((Integer)msg.obj - 1);
		    	myAdapter.notifyDataSetChanged();
		    	
				((DingDanListActivity) getActivity()).showCustomToast("删除成功", false);
				if (data.size() <= 0) {
		    		img.setVisibility(View.VISIBLE);
				}
				//getList();
				break;
				
			case HanderUtil.case4:	
				listView.stopRefresh();
				listView.stopLoadMore();
				listView.setRefreshTime();
				if (data2.size() > 0) {										
					
				}else {
					((DingDanListActivity) getActivity()).showCustomToast("没有更多数据", false);  
				}
				break;
				
			default:
				break;
			}
		};
	};
	
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		if(view == null) { 
			view = inflater.inflate(R.layout.fragment_all, container, false);
		
			img = (ImageView) view.findViewById(R.id.no_img);
			listView = (MyXListView)view.findViewById(R.id.listview);	
			listView.setPullLoadEnable(true);//设置能下拉（这两个属性是上拉加载下拉刷新的）
			listView.setMyXListViewListener(this);//设置接口
			
			myAdapter = new MyAdapter(getActivity());
			listView.setAdapter(myAdapter);
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent;
					if (getActivity().getIntent().getStringExtra("flage").equals("TUI")) {
						intent = new Intent(getActivity(), TuiDanDatailsActivity.class);
					}else {
						intent = new Intent(getActivity(), DingDanDatailsActivity.class);
					}
					
					intent.putExtra("OrderID", data.get(position-1).getOrderID());
					
//					Bundle bundle = new Bundle();
//					intent.putExtra("State", data.get(position-1).getStatus());					
//					intent.putExtra("Count", data.get(position-1).getPrice() + "");
//					
//					//下面这些传的值都是详情里面不用复制时要传的。
//					intent.putExtra("Member_ID", data.get(position-1).getMember_ID() + "");//客户ID是为了复制订单的时候使用
//					intent.putExtra("Member_Name", data.get(position-1).getMember_Name());
//					intent.putExtra("ReceiveName", data.get(position-1).getReceiveName());
//					intent.putExtra("Address", data.get(position-1).getReceiveAddress());
//					intent.putExtra("Mobile", data.get(position-1).getReceiveMobile());				
//					intent.putExtra("Date", data.get(position-1).getReceiveDate());
//					intent.putExtra("Note", data.get(position-1).getNote());
					startActivity(intent);	
					
				}
			});	
			
			listView.setOnItemLongClickListener(new OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> parent, View view,
						final int position, long id) {
					 
					if (data.get(position-1).getStatus() == 6 || 
							data.get(position-1).getStatus() == 7) {
						
						AlertDialog.Builder builder = new Builder(getActivity());
						builder.setMessage("确认删除吗？");					 
						builder.setTitle("提示");
						builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog, int which) {
					 				
						    	ProgressBar_Loading3.showProgressBar(getActivity(), true, "Loading....");
						    	delOrder(data.get(position-1).getOrderID(), position);
						    	
						    }					 
						});					  
						builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						   public void onClick(DialogInterface dialog, int which) {					 
						   dialog.dismiss();					 
						   }					 
						});					  
						builder.create().show();
					}			
						
					return true;
				}
			});
			data.clear();
			L.e("进度条");
			ProgressBar_Loading3.showProgressBar(getActivity(), true, "Loading....");
			getList(1);
			
			//注册广播
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction("ZUOFEI");
			intentFilter.setPriority(Integer.MAX_VALUE);
			getActivity().registerReceiver(broadcastReceiver, intentFilter);
		}
		//判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。  
	    ViewGroup parent = (ViewGroup)view.getParent();  
	    if(parent != null) {  
	        parent.removeView(view);  
	    } 
		return view;
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

		@Override
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
				
			FindActivity.getStatu(getActivity(), holder.tongzhi, data.get(position).getStatus());
			holder.name.setText(data.get(position).getMember_Name());
			holder.nub.setText(data.get(position).getOrderID());     //这个还需改变
			holder.price.setText(data.get(position).getPrice() + "");
			String timeStr =  data.get(position).getAddDateTime().substring(0,10);
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
	 * 获取订单列表线程方法
	 * @param json
	 */
	private void getList(int index){		
		int Manager_ID = Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANGYUN_UID, "0"));
		int Company_ID = MyApplication.preferences.getInt(Content.COMPS_ID, 1016);
		int Department_ID = MyApplication.preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0);
		
		//实例化请求类
	    ZJRequest zjRequest = new ZJRequest(); 
	    //账户id：Manager_ID
	    zjRequest.setManager_ID(Manager_ID);//高级账号全部订单为0，我的订单传自己的Manager_ID
	    //Company_ID
	    zjRequest.setCompany_ID(Company_ID);
	    //Department_ID
	    zjRequest.setDepartment_ID(0);
	    zjRequest.setPageIndex(index);
	    zjRequest.setPageSize(size);
	    zjRequest.setKeywords("");
	    if (MyApplication.preferences.getInt("STATE", 0) == -1) {
	    	zjRequest.setType(-1);
		}else if(MyApplication.preferences.getInt("STATE", 0) == 1){
			zjRequest.setType(1);//订货单传1，退货单传-1
		}
	    final String json = JsonUtil.toJson(zjRequest);
	    L.e("(订单我的)jsonString:" + json);
	    
	    //activity.showDialog("正在加载...");	    
		new Thread(){
	    	public void run() {
	    		try {
					String jsonString = DataUtil.callWebService(Methods.ORDER_DINGDAN_LIST, json);
					L.e("(订单我的)json:" + jsonString);
					if (jsonString != null) {
						Type type = new TypeToken<ZJResponse<Order_SetOrder>>(){}.getType();
					    ZJResponse<Order_SetOrder> zjResponse = JsonUtil.fromJson(jsonString, type);
					    if (zjResponse.getCode() == 0) {
					    						    						    	
					    	data2.clear();//因为第一次加载和删除都要调用，所以这里先clear一下。
					    	data2 = zjResponse.getResults();
					    	flag = true;					    	
						    myHandler.sendEmptyMessage(HanderUtil.case1);
						} else {
							Message message = new Message();
						    message.what = HanderUtil.case2;
						    flag = true;
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
	 * 获取订单列表线程方法(上拉加载的时候)
	 * @param json
	 */
	private void getList2(int index){		
		int Manager_ID = Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANGYUN_UID, "0"));
		int Company_ID = MyApplication.preferences.getInt(Content.COMPS_ID, 1016);
		int Department_ID = MyApplication.preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0);
		
		//实例化请求类
	    ZJRequest zjRequest = new ZJRequest(); 
	    //账户id：Manager_ID
	    zjRequest.setManager_ID(Manager_ID);//高级账号全部订单为0，我的订单传自己的Manager_ID
	    //Company_ID
	    zjRequest.setCompany_ID(Company_ID);
	    //Department_ID
	    zjRequest.setDepartment_ID(0);
	    zjRequest.setPageIndex(index);
	    zjRequest.setPageSize(size);
	    zjRequest.setKeywords("");
	    if (getActivity().getIntent().getStringExtra("flage").equals("TUI")) {
	    	zjRequest.setType(-1);
		}else {
			zjRequest.setType(1);//订货单传1，退货单传-1
		}
	    final String json = JsonUtil.toJson(zjRequest);
	    L.e("(订单)jsonString:" + json);
	    
	    //activity.showDialog("正在加载...");	    
		new Thread(){
	    	public void run() {
	    		try {
					String jsonString = DataUtil.callWebService(Methods.ORDER_DINGDAN_LIST, json);
					L.e("json:" + jsonString);
					if (jsonString != null) {
						Type type = new TypeToken<ZJResponse<Order_SetOrder>>(){}.getType();
					    ZJResponse<Order_SetOrder> zjResponse = JsonUtil.fromJson(jsonString, type);
					    if (zjResponse.getCode() == 0) {
					    						    						    	
					    	data2.clear();//因为第一次加载和删除都要调用，所以这里先clear一下。
					    	data2 = zjResponse.getResults();
					    	flag = true;					    	
						    myHandler.sendEmptyMessage(HanderUtil.case4);
						} else {
							Message message = new Message();
						    message.what = HanderUtil.case2;
						    flag = true;
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
	 * 删除订单的方法
	 * @param goods
	 */
	private void delOrder(String order_id, final int position){
		ZJRequest zjRequest = new ZJRequest();
						
		zjRequest.setOrderID(order_id);	
		zjRequest.setAction("Delete");
		zjRequest.setNote("Note");
		zjRequest.setManager_ID(Integer.parseInt(MyApplication.getInstance().getSharedPreferences().
				getString(Constant.HUISHANGYUN_UID, "0")));
		zjRequest.setOperationName(MyApplication.getInstance().getSharedPreferences().
				getString(Constant.XMPP_MY_REAlNAME, ""));
		zjRequest.setCompany_ID(MyApplication.getInstance().getCompanyID());
		
		final String json = JsonUtil.toJson(zjRequest);
	    L.e("(删除订单)json:" + json);
	    
		new Thread(){
			public void run() {
				try {			
					String jsonString = DataUtil.callWebService(Methods.ORDER_DINGDAN_DEL, json);
					L.e("(删除订单)jsonString:" + jsonString);
					
					if (jsonString != null) {						
						Type type = new TypeToken<ZJResponse>(){}.getType();
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
	
	
	@Override
	public void onRefresh() {
		
		if (flag == true) {
			flag = false;//走线程之前改为false，走完才能再加载	
			data.clear();
			index = 1;
			getList(index);															
		}			
	}

	@Override
	public void onLoadMore() {
		
		if (flag == true) {
			flag = false;//走线程之前改为false，走完才能再加载
			index = index + 1;
			getList2(index);//上拉加载的时候不需要清除																										
		}		
	}
	
	/**
	 * 广播接收器
	 */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			data.clear();
			getList(1);
		}
		
	};
}
