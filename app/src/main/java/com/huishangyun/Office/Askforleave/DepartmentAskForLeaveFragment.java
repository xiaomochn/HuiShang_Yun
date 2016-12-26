
package com.huishangyun.Office.Askforleave;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.Office.Summary.ImageLoad;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.model.Methods;
import com.huishangyun.swipelistview.MyXListView.MyXListViewListener;
import com.huishangyun.yun.R;

public class DepartmentAskForLeaveFragment extends Fragment implements MyXListViewListener{
	protected static final String TAG = null;
	View departmentView; 
	private MyXListView askforleavelistview;
	private AskForLeaveTripAdapter adapter;
	private int Company_ID;//公司id
	private int Manager_ID;//登录人id
	private int Department_ID;//部门id
	private int PageIndex = 1;//页码
	private List<AskForLeaveList> listdata = new ArrayList<AskForLeaveList>();
	private List<AskForLeaveList> list = new ArrayList<AskForLeaveList>();
	private ImageView no_information;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (departmentView==null) {
		departmentView = inflater.inflate(R.layout.activity_office_allaskforleavefragment, container,false);
		Company_ID = getActivity().getIntent().getIntExtra("Company_ID", 1016);
		Manager_ID = getActivity().getIntent().getIntExtra("Manager_ID", 0);
		Department_ID = getActivity().getIntent().getIntExtra("Department_ID", 0);
		//注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("ASKFORLEAVE_LIST_REFRESH");
		intentFilter.setPriority(Integer.MAX_VALUE);
		getActivity().registerReceiver(broadcastReceiver, intentFilter);
		
		init();
		getNetData(Company_ID, 0, Department_ID, PageIndex, 8, 1);
		}
		return departmentView;
	}
	
	/**
	 * 广播接收器
	 */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			int  mflag = intent.getIntExtra("mflag", -1);
			if (mflag==2) {
				getNetData(Company_ID, 0, Department_ID, PageIndex, 8, 1);
			}
			
		}
	};
	
	/**
	 * 销毁fragment
	 */
	@Override
	public void onDestroyView() {
	    Log.e(TAG , "-->onDestroyView");
	    super .onDestroyView();
	    if (null != departmentView) {
	        ((ViewGroup) departmentView.getParent()).removeView(departmentView);
	    }
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		getActivity().unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	
	/**
	 * 获取网络数据
	 * @param Company_ID 公司id
	 * @param Manager_ID 用户编号
	 * @param Department_ID 部门编号
	 * @param pageSize 单页显示条数
	 * @param pageIndex 页码
	 * @param index 根据指数判断给那个数组赋值 1：为刷新对应值 --2为：加载对应值
	 */
	private void getNetData(final int Company_ID, final int Manager_ID,
			final int Department_ID, final int pageIndex,final int pageSize, final int index){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub					
				String result = DataUtil.callWebService(
						Methods.GET_LEAVE_LIST,
						getJson(Company_ID, Manager_ID, Department_ID,
								 pageIndex,pageSize));	
				Log.e(TAG, "result:" + result + pageIndex);
				Log.e(TAG, "=========>:" + getJson(Company_ID, Manager_ID, Department_ID,
						 pageIndex,pageSize));
				
				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					// 获取对象的Type
					Type type = new TypeToken<ZJResponse<AskForLeaveList>>() {
					}.getType();
					ZJResponse<AskForLeaveList> zjResponse = JsonUtil.fromJson(result,
							type);
					// 获取对象列表
					listdata.clear();
					listdata = zjResponse.getResults();
					Log.e(TAG, "-----------111");
					if (index == 1) {//刷新
						handler.sendEmptyMessage(0);
					}else if (index == 2) {//加载更多
						handler.sendEmptyMessage(1);
						
					}					
				
				} else {
					handler.sendEmptyMessage(2);
				}
			}
		}.start();
	}
	
	/**
	 * 设置json对象
	 * @param Company_ID 公司id
	 * @param Manager_ID 用户编号
	 * @param Department_ID 部门编号
	 * @param pageSize 单页显示条数
	 * @param pageIndex 页码
	 * @return
	 */
	private String getJson(int Company_ID, int Manager_ID,
			int Department_ID,  int pageIndex, int pageSize) {
		
		ZJRequest zjRequest = new ZJRequest();
		// 公司id
		zjRequest.setCompany_ID(Company_ID);
		
		// 用户编号
		zjRequest.setManager_ID(Manager_ID);
		// 设置部门号，0时为相当没有部门编号查询
		zjRequest.setDepartment_ID(Department_ID);
		// 设置页码，默认是1
		zjRequest.setPageIndex(pageIndex);
		// 设置当页显示条数
		zjRequest.setPageSize(pageSize);
		return JsonUtil.toJson(zjRequest);

	}
	
	 /**
	  * 更新UI
	  */
	private  Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0://刷新
				list.clear();
				for (int i = 0; i < listdata.size(); i++) {
					list.add(listdata.get(i));
				}
				if (list.size()==0) {
					no_information.setVisibility(View.VISIBLE);
				}else {
					no_information.setVisibility(View.GONE);
				}
				adapter.notifyDataSetChanged();
				break;
			case 1://加载更多
				
				if (listdata.size() <= 0) {
					showDialog("没有更多数据！");
					
				}else {
					
					for (int i = 0; i < listdata.size(); i++) {
						list.add(listdata.get(i));
					}
					adapter.notifyDataSetChanged();
				}
			
				break;
			case 2:
				showDialog("未获得任何网络数据，请检查网络连接！");
				break;

			default:
				break;
			}
		};
	};
	
	/**
	 * toast
	 * 提醒方法
	 */
	public void showDialog(String TXT){
		new ClueCustomToast().showToast(getActivity(),
				R.drawable.toast_warn, TXT);

	}
	
    /**
     * 界面初始化
     */
	private void init() {
		no_information = (ImageView) departmentView.findViewById(R.id.no_information);
		askforleavelistview = (MyXListView) departmentView.findViewById(R.id.askforleavelistview);
		adapter = new AskForLeaveTripAdapter(getActivity(),list);
		askforleavelistview.setAdapter(adapter);
		askforleavelistview.setPullLoadEnable(true);
		askforleavelistview.setMyXListViewListener(this);
		
		askforleavelistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						DetailAskForLeaveActivity.class);
				intent.putExtra("Manager_ID", list.get(position-1).getManager_ID());
				startActivity(intent);
				
			}
		});
	}
	
	/**
	 * listview适配器
	 * @author xsl
	 *
	 */
	private class AskForLeaveTripAdapter extends BaseAdapter{
		private LayoutInflater mInflater;// 动态布局映射
		private List<AskForLeaveList> Lists;
		private Context context;
		public AskForLeaveTripAdapter(Context context,List<AskForLeaveList> lists) {
			this.context = context;
			this.Lists = lists;
			this.mInflater  = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Lists.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			 ViewHolder holder; 
				if (convertView == null) {
					convertView = mInflater.inflate(
							R.layout.activity_office_askforleave_all_item, null);// 根据布局文件实例化view
					holder = new ViewHolder();
					holder.date = (TextView) convertView.findViewById(R.id.date);
					holder.person_img = (ImageView) convertView.findViewById(R.id.person_img);
					holder.name = (TextView) convertView.findViewById(R.id.name);
					holder.daystime = (TextView) convertView.findViewById(R.id.daystime);
					holder.starttime = (TextView) convertView.findViewById(R.id.starttime);
					holder.endtime = (TextView) convertView.findViewById(R.id.endtime);		
					
					convertView.setTag(holder);
				} else {
					
					holder = (ViewHolder) convertView.getTag();
					
				}
				
				if (position!= 0) {
					if (backDate(Lists.get(position).getAddDateTime()).equals
							(backDate(Lists.get(position-1).getAddDateTime()))) {
						holder.date.setVisibility(View.GONE);
					}else {
						holder.date.setVisibility(View.VISIBLE);
					}
				}else {
					holder.date.setVisibility(View.VISIBLE);
				}
				
				holder.date.setText(backDate(Lists.get(position).getAddDateTime()));
				holder.name.setText(Lists.get(position).getManager_Name());
				holder.daystime.setText("(" + Lists.get(position).getDays() + "天" + ")");	
				holder.starttime.setText(backDate(Lists.get(position).getStartTime()));
				holder.endtime.setText(backDate(Lists.get(position).getEndTime()));
				
				String person_url = "http://img.huishangyun.com/UploadFile/huishang/"+ 
		                Company_ID+"/Photo/" + list.get(position).getManager_Photo();
//				ImageLoad.displayImage(person_url, holder.person_img, R.drawable.person_img, 10);
				new ImageLoad().displayImage(getActivity(), person_url, holder.person_img, R.drawable.person_img, 10, false);
			return convertView;
		}
		
	}
	
	/**
	 * 静态缓存
	 * @author xsl
	 *
	 */
	 static class ViewHolder { 
         //标题日期
        private TextView date; 
        //头像
        private ImageView person_img; 
        //创建人
        private TextView name;
        //请假天数
        private TextView daystime; 
        //开始日期
        private TextView starttime;
        //结束日期
        private TextView endtime;

        } 
	
	
	
	/**
	 * 下拉刷新
	 */
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				askforleavelistview.stopRefresh();
				askforleavelistview.stopLoadMore();
				askforleavelistview.setRefreshTime();
				
				
			}
		}, 2000);
		PageIndex = 1;
		getNetData(Company_ID, 0, Department_ID, PageIndex, 8, 1);
	}
	
	/**
	 * 上拉加载
	 */
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
        handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				askforleavelistview.stopRefresh();
				askforleavelistview.stopLoadMore();
				askforleavelistview.setRefreshTime();
				
			}
		}, 2000);
        PageIndex += 1;
        getNetData(Company_ID, 0, Department_ID, PageIndex, 8, 2);
	}
	
	
	/**
	 * 传入后台接到的时间，返回我们需要的格式
	 * @param date 传入时间参数
	 * @return
	 */
	private String backDate(String date){
		//这里包含0位但不包含5即（0，5】
		String a = date.substring(0,4);
		String b = date.substring(5,7);
		String c = date.substring(8,10);
		String datesString = a + "-" + b + "-" + c  ;
		
		return datesString;		
	}
	
	/**
	 * 传入后台接到的时间，返回我们需要的格式 ：2014-9-20 17：35
	 * @param date
	 * @return
	 */
	private String backDateMin(String date){
		String[] temp = null;
		temp = date.split("T");
		String a = temp[1].substring(0,5);
		String dataString = temp[0] + " " + a;
		return dataString;		
	}

}
