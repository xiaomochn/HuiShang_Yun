package com.huishangyun.Channel.Orders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.View.ProgressBar_Loading;
import com.huishangyun.model.Methods;
import com.huishangyun.model.OrderLog;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.yun.R;

public class LogActivity extends Activity {
	private LinearLayout back;
	private ListView listView;
	private MyAdapter myAdapter;
	private List<OrderLog> data = new ArrayList<OrderLog>();
	private List<OrderLog> data2 = new ArrayList<OrderLog>();
	
	private Handler myHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				ProgressBar_Loading.dismiss(LogActivity.this);
				if (data2.size() > 0) {
					data.clear();
					for (int i = 0; i < data2.size(); i++) {
						data.add(data2.get(i));						
					}
					myAdapter.notifyDataSetChanged();															
				}				
				break;
				
			case HanderUtil.case2:
				ProgressBar_Loading.dismiss(LogActivity.this);	
//				showCustomToast((String) msg.obj, false);
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
		setContentView(R.layout.activity_log);
		
		init();
		ProgressBar_Loading.showProgressBar(LogActivity.this, true, "Loading....");
		getList();
	}
	
	private void init(){
		back = (LinearLayout)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
							
		listView = (ListView)findViewById(R.id.listview);
		myAdapter = new MyAdapter(LogActivity.this);
		listView.setAdapter(myAdapter);
	}
	
	/**
	 * 自定义Adapter
	 * @author Administrator
	 */
	public class MyAdapter extends BaseAdapter{
		private LayoutInflater inflater;// 用来导入布局
		
		public MyAdapter(Context context) {// 构造器
			this.inflater = LayoutInflater.from(context);
		}		
		public int getCount() {
			return data.size();
		}
		public Object getItem(int position) {
			return data.get(position);
		}
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			final ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.list_log, null);
				holder.state = (TextView) view.findViewById(R.id.state);
				holder.time = (TextView) view.findViewById(R.id.time);
//				holder.info = (TextView) view.findViewById(R.id.info);
									
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
				
			holder.state.setText(data.get(position).getNote());
//			holder.info.setText();获取数据与界面不符
			String date =  data.get(position).getAddDateTime().substring(0, 10);
			String time = data.get(position).getAddDateTime().substring(11, 16);
			holder.time.setText(date + "  " + time);
						
			return view;
		}
		
		public final class ViewHolder {// 写这个类是为了点击list的时候好调用view			
			public TextView state;
//			public TextView info;
			public TextView time;
		}
	}
	
	private void getList(){
		String orderID = getIntent().getStringExtra("order_id");
		//实例化请求类
	    ZJRequest zjRequest = new ZJRequest();
	    zjRequest.setOrderID(orderID);	    
	    zjRequest.setPageIndex(1);
	    zjRequest.setPageSize(1000000);
	    
	    final String json = JsonUtil.toJson(zjRequest);
	    L.e("(日志列表)jsonString:" + json);
	        
		new Thread(){
	    	public void run() {
	    		try {
					String jsonString = DataUtil.callWebService(Methods.GetOrdersLog, json);
					L.e("(日志列表)json:" + jsonString);
					if (jsonString != null) {
						Type type = new TypeToken<ZJResponse<OrderLog>>(){}.getType();
					    ZJResponse<OrderLog> zjResponse = JsonUtil.fromJson(jsonString, type);
					    if (zjResponse.getCode() == 0) {
					    						    						    	
					    	data2.clear();
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
}
