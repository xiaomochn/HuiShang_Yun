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
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.manager.ProductManager;
import com.huishangyun.model.ClassModel;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

public class Fragment_Classify extends Fragment{
	
	private ListView listView;
	private ImageView img;
	
	private List<ClassModel> data = new ArrayList<ClassModel>();
	private List<ClassModel> data2 = new ArrayList<ClassModel>();
	private MyAdapter myAdapter;
	private String name;
	private int class_ID;
//	private BaseActivity baseActivity;
	
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
//				listView.stopRefresh();
//				//获取上次存入的时间
//				listView.setRefreshTime(Common.getTime());	
//				//每次刷新前把当前时间存入
//				Common.postTime(baseActivity.getTime());
				
				myAdapter.notifyDataSetChanged();
				break;
				
			case HanderUtil.case2:
				((OrderMainActivity) getActivity()).showCustomToast((String) msg.obj, false);			
				break;
				
			case HanderUtil.case3:
				
				Intent intent = new Intent(getActivity(), FenleiActivity.class);
				intent.putExtra("class_ID", class_ID + "");
				intent.putExtra("name", name);
				startActivity(intent);
				break;				
			default:
				break;
			}
		};
	};
	
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_classify, container, false);
		
		img = (ImageView)view.findViewById(R.id.no_img);
//		baseActivity = new BaseActivity();
		listView = (ListView)view.findViewById(R.id.listview_classify);
//		listView.setPullLoadEnable(true);//设置能下拉（这两个属性是上拉加载下拉刷新的）
//		listView.setMyXListViewListener(this);//设置接口
		
		myAdapter = new MyAdapter(getActivity());
		listView.setAdapter(myAdapter);					
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				name = data.get(position).getName();
				class_ID = data.get(position).getID();
				
				getData(class_ID, false);
															
			}
		});	    
	    
		return view;
	}	
	
		
	/**
	 * 获取产品列表线程
	 * 最上一级PartentID为0，下一级列表时根据上一级ID来查找的，
	 * @param json
	 */
	private void getClassifyList(int PartentID){
		//实例化请求类
	    ZJRequest zjRequest = new ZJRequest();
	    //设置你需要的属性
	    zjRequest.setManager_ID(2171);
	    zjRequest.setCompany_ID(1016);
	    zjRequest.setPageIndex(1);
	    zjRequest.setPageSize(100);
	    zjRequest.setKeywords("");
	    zjRequest.setParentID(PartentID);//后台没有PartentID这个属性，所以用代替setMonth
	    final String json = JsonUtil.toJson(zjRequest);
	    L.e("(Fragment_Classify)jsonString:" + json);
	    	    
		new Thread(){
		    	public void run() {
		    		try {
						String jsonString = DataUtil.callWebService(Methods.ORDER_CLASSLIST, json);
						L.e("json:" + jsonString);
						if (jsonString != null) {
							Type type = new TypeToken<ZJResponse<ClassModel>>(){}.getType();
						    ZJResponse zjResponse = JsonUtil.fromJson(jsonString, type);
						    if (zjResponse.getCode() == 0) {

						    	data.clear();
						    	data = zjResponse.getResults();
						    	
						    	if (data.size() > 0) {
						    		//接口获取到的数据存入数据库
							    	for (int i = 0; i < data.size(); i++) {									
										ProductManager.getInstance(getActivity()).saveClass(data.get(i));
							    	}
						    		myHandler.sendEmptyMessage(HanderUtil.case1);
						    		
								}else {
									myHandler.sendEmptyMessage(HanderUtil.case3);
								}
							    
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
				view = inflater.inflate(R.layout.list_fenlei, null);
				holder.tv = (TextView) view.findViewById(R.id.tv);
									
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.tv.setText(data.get(position).getName());		
			return view;
		}
		
		public final class ViewHolder {// 写这个类是为了点击list的时候好调用view
			public TextView tv;
		}
	}

	/**
	 * 从数据库获取数据通知数据改变的方法
	 * 传入一个页码数
	 */
	private void getData(int ID, boolean isParentID){
		List<ClassModel> data3 = new ArrayList<ClassModel>();
		//从数据库获取的方法		
		data3 = ProductManager.getInstance(getActivity()).getClassModels(ID, isParentID);
		data.clear();
		if (data3.size() > 0) {
			L.i("分类data3.size() > 0");
			for ( int i = 0; i < data3.size(); i++) {
				data.add(data3.get(i));
			}	
			L.e("(getData)data:"+ data.toString());
		
			myAdapter.notifyDataSetChanged();
			img.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}else {
			L.i("分类data3.size() < 0");
			Intent intent = new Intent(getActivity(), FenleiActivity.class);
			intent.putExtra("class_ID", class_ID + "");
			intent.putExtra("name", name);
			startActivity(intent);
			L.e("数据库传入的class_ID" + class_ID);
			L.e("数据库传入的name" + name);

		}		
	}
	
//	public boolean onKeyDown(int keyCode, KeyEvent event) {		
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//        	L.e("进入返回事件！");
//
//        	if (baseActivity.PingService()) {
//    			getClassifyList(class_ID);
//    			
//    		}else {
//    			getData(class_ID, false);	
//    		}
//
//        }
//        return true;
//	}
	
	
	/**
	 * 在onResume方法里调用接口，第一次进来时会运行，滑动其他页面再回来时也会运行
	 */
	public void onResume() {
		super.onResume();
		
		L.e("调用了onResume方法！");	
//		getData(0, false);
		//不能复用这个方法，因为如果第一次进来没分类data3.size()<0 会进入分类详情页面
		
		List<ClassModel> data3 = new ArrayList<ClassModel>();
		//从数据库获取的方法		
		data3 = ProductManager.getInstance(getActivity()).getClassModels(0, false);
		data.clear();
		if (data3.size() > 0) {
			for ( int i = 0; i < data3.size(); i++) {
				data.add(data3.get(i));
			}	
			L.e("(getData)data:"+ data.toString());
		
			myAdapter.notifyDataSetChanged();
			img.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}else {
			img.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		}		
	}

	/**
	 * 下拉刷新  上拉加载
	 */
//	public void onRefresh() {		
//		if (baseActivity.PingService()) {
//			getClassifyList(class_ID);
//			
//		}else {
//			listView.stopRefresh();
//			//获取系统的时间
//			listView.setRefreshTime("");	
//			T.showShort(getActivity(), "当前网络不可用");
//		}								
//	}
//	
//	@Override
//	public void onLoadMore() {
//		new Handler().postDelayed(new Runnable() {
//			public void run() {
//				listView.stopLoadMore();
//				T.showShort(getActivity(), "没有更多数据");								
//			}
//		}, 2000);
//	}
}
