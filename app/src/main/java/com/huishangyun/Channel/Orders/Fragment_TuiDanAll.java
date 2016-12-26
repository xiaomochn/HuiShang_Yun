package com.huishangyun.Channel.Orders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.View.ProgressBar_Loading;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.model.Order_SetOrder;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.model.Content;
import com.huishangyun.swipelistview.MyXListView.MyXListViewListener;
import com.huishangyun.yun.R;

/**
 * 没用上的类，退货单列表和订货单共用了
 * @author Administrator
 *
 */
public class Fragment_TuiDanAll extends Fragment  implements MyXListViewListener{
	
	private MyXListView listView;
	private List<Order_SetOrder> data = new ArrayList<Order_SetOrder>();
	private List<Order_SetOrder> data2 = new ArrayList<Order_SetOrder>();
	private MyAdapter myAdapter;
	
	private int index = 1, size = 1;
	private boolean flag = true; 
	/**
	 * 用来存放点击过的list子项的集合
	 */
	private List<Integer> list = new ArrayList<Integer>();
 	/**
	 * 创建一个构造方法获取到接口
	 * @param getNub
	 */	
	
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				ProgressBar_Loading.dismiss(getActivity());
				listView.stopRefresh();
				listView.setRefreshTime();
				
				data.clear(); 
				for ( int i = 0; i < data2.size(); i++) {
					data.add(data2.get(i));
				}
				myAdapter.notifyDataSetChanged();  
//				
//				activity.dismissDialog();
				break;
				
			case HanderUtil.case2:
				ProgressBar_Loading.dismiss(getActivity());
				listView.stopRefresh();
				listView.setRefreshTime();
//				T.showShort(ShouCangActivity.this, (CharSequence) msg.obj);
				break;

			default:
				break;
			}
		};
	};
	
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_all, container, false);
		
		listView = (MyXListView)view.findViewById(R.id.listview);	
		myAdapter = new MyAdapter(getActivity());
		listView.setAdapter(myAdapter);						
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent intent = new Intent(getActivity(), TuiDanDatailsActivity.class);
				startActivity(intent);
				
			}
		});
		ProgressBar_Loading.showProgressBar(getActivity(), true, "Loading....");
		getList(1);
		return view;
	}
	
	/**
	 * 获取退货单列表线程方法
	 * @param json
	 */
	private void getList(int index){		
		int Manager_ID = Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANGYUN_UID, "0"));
		int Company_ID = MyApplication.preferences.getInt(Content.COMPS_ID, 1016);
		int Department_ID = MyApplication.preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0);
		
		//实例化请求类
	    ZJRequest zjRequest = new ZJRequest(); 
	    //账户id：Manager_ID
	    zjRequest.setManager_ID(Manager_ID);
	    //Company_ID
	    zjRequest.setCompany_ID(Company_ID);
	    //Department_ID
	    zjRequest.setDepartment_ID(Department_ID);
	    zjRequest.setPageIndex(index);
	    zjRequest.setPageSize(size);
	    zjRequest.setKeywords("");
	    zjRequest.setType(-1);//订货单传1，退货单传-1
	    final String json = JsonUtil.toJson(zjRequest);
	    L.e("(退货单)jsonString:" + json);
	    
	    //activity.showDialog("正在加载...");
	    
		new Thread(){
	    	public void run() {
	    		try {
					String jsonString = DataUtil.callWebService(Methods.ORDER_DINGDAN_LIST, json);
					L.e("(退货单)json:" + jsonString);
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

	@Override
	public void onRefresh() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				listView.stopRefresh();
				listView.setRefreshTime();
				if (flag == true) {
					flag = false;//走线程之前改为false，走完才能再加载
					index = 1;					
					getList(index);
					flag = true;															
				}
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				listView.stopLoadMore();
				if (flag == true) {
					flag = false;//走线程之前改为false，走完才能再加载
					index = index + 1;
					if (data2.size() == size) {
						getList(index);
						
					}else {
						((TuiDanListActivity) getActivity()).showCustomToast("没有更多数据", false);
						
					}
					flag = true;															
				}
			}
		}, 2000);
	}
}
