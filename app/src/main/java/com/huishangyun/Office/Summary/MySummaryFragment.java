package com.huishangyun.Office.Summary;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Methods;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.swipelistview.MyXListView.MyXListViewListener;
import com.huishangyun.yun.R;

public class MySummaryFragment extends Fragment implements MyXListViewListener {
	protected static final String TAG = null;
	private View myView;
	private int Company_ID;//公司id
	private int Manager_ID;//登录人id
	private int Department_ID;//部门id
	private MyXListView slistview;//listview
	private SummaryAdapter adapter;
	private List<SummaryDateList> myitemLists = new ArrayList<SummaryDateList>();
	private List<SummaryDateList> mylist = new ArrayList<SummaryDateList>();
	private int PagerIndex = 1;
	private ImageView no_information;
	private String ctxt;//上下文对象转string对象
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (null == myView) {
			myView = inflater.inflate(R.layout.activity_office_summary_my,container, false);
			Company_ID = getActivity().getIntent().getIntExtra("Company_ID", 0);
			Manager_ID = getActivity().getIntent().getIntExtra("Manager_ID", 0);
			Department_ID = getActivity().getIntent().getIntExtra("Department_ID", 0);
			init();
		}
		return myView;
	}

	/**
	 * 广播接收器
	 */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			int  mflag = intent.getIntExtra("mflag", -1);
			
			getNetData(Company_ID, Manager_ID, Department_ID, PagerIndex, 10, 1);
		
			
		}
	};
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		getActivity().unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}
	/**
	 * 初始化界面
	 */
	private void init() {
		no_information = (ImageView) myView.findViewById(R.id.no_information);
		//注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("SUMMARY_LIST_REFRESH");
		intentFilter.setPriority(Integer.MAX_VALUE);
		getActivity().registerReceiver(broadcastReceiver, intentFilter);
		
		slistview = (MyXListView) myView.findViewById(R.id.mylistview);
		adapter = new SummaryAdapter(getActivity(),mylist);
		slistview.setAdapter(adapter);
		slistview.setPullLoadEnable(true);
		slistview.setMyXListViewListener(this);
		
		slistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				//跳转到详情界面
				Intent intent = new Intent(getActivity(),SummaryDetailActivity.class);
				//传值
				intent.putExtra("BelongDate", backDate(mylist.get(position-1).getBelongDate()));
				intent.putExtra("Manager_ID", mylist.get(position-1).getManager_ID());
				intent.putExtra("ID", mylist.get(position-1).getID());
				startActivity(intent);
				
			}
		});
		ctxt = getActivity().toString().trim();
		L.e("上下文对象：" + ctxt);
		if (ctxt.startsWith("MainSummaryOrdinaryEntryActivity")) {
			MyApplication.getInstance().showDialog(getActivity(), true, "Loading...");
		}
		getNetData(Company_ID, Manager_ID, Department_ID, PagerIndex, 10, 1);
	}
	
	/**
	 * 
	 * @param Company_ID
	 * @param Manager_ID
	 * @param Department_ID
	 * @param pageIndex
	 * @param pageSize
	 * @param index
	 */
	private void getNetData(final int Company_ID, final int Manager_ID,
			final int Department_ID, final int pageIndex,final int pageSize, final int index){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub					
				String result = DataUtil.callWebService(
						Methods.GET_SUMMARY_LIST,
						getJson(Company_ID, Manager_ID, Department_ID,
								 pageIndex,pageSize));	
//				Log.e(TAG, "result:" + result + pageIndex);
//				Log.e(TAG, "result:" + getJson(Company_ID, Manager_ID, Department_ID,
//						 pageIndex,pageSize));
				
				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					// 获取对象的Type
					Type type = new TypeToken<ZJResponse<SummaryDateList>>() {
					}.getType();
					ZJResponse<SummaryDateList> zjResponse = JsonUtil.fromJson(result,
							type);
					// 获取对象列表
					myitemLists.clear();
					myitemLists = zjResponse.getResults();
					Log.e(TAG, "-----------111");
					if (index == 1) {//刷新
						mHandler.sendEmptyMessage(0);
					}else if (index == 2) {//加载更多
						mHandler.sendEmptyMessage(1);
						
					}					
				
				} else {
					mHandler.sendEmptyMessage(2);
				}
			}
		}.start();
	}
	
	
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
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0://刷新
				mylist.clear();
				for (int i = 0; i < myitemLists.size(); i++) {
					mylist.add(myitemLists.get(i));
				}
				if (mylist.size()==0) {
					no_information.setVisibility(View.VISIBLE);
				}else {
					no_information.setVisibility(View.GONE);
				}
				adapter.notifyDataSetChanged();
				if (ctxt.startsWith("MainSummaryOrdinaryEntryActivity")) {
					MyApplication.getInstance().showDialog(getActivity(), false, "Loading...");
				}
				break;
			case 1://加载更多
				
				if (myitemLists.size() <= 0) {
					showDialog("没有更多数据！");
					
				}else {
					
					for (int i = 0; i < myitemLists.size(); i++) {
						mylist.add(myitemLists.get(i));
					}
					adapter.notifyDataSetChanged();
				}
			
				break;
			case 2:
				if (ctxt.startsWith("MainSummaryOrdinaryEntryActivity")) {
					MyApplication.getInstance().showDialog(getActivity(), false, "Loading...");
				}
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
		ClueCustomToast.showToast(getActivity(),
				R.drawable.toast_warn, TXT);

	}
	
	
	
	/**
	 * 自定义适配器
	 * @author xsl
	 *
	 */
	private class SummaryAdapter extends BaseAdapter{
		private LayoutInflater mInflater;//动态映射
		private Context context;
		private List<SummaryDateList> lists;
        private SummaryAdapter(Context context,List<SummaryDateList> lists){
        	this.context = context;
        	this.mInflater  = LayoutInflater.from(context);
        	this.lists = lists;
        }
      
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lists.size();
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
			String[] temp = lists.get(position).getWorks().split("#");
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.activity_office_summary_my_item, null);
				holder = new ViewHolder();
				holder.layout = (LinearLayout) convertView.findViewById(R.id.layout);
				holder.belongdate = (TextView) convertView.findViewById(R.id.belongdate); 
				holder.weekday = (TextView) convertView.findViewById(R.id.weekday); 
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.layout.removeAllViews();
			for (int i = 0; i < temp.length; i++) {
				LinearLayout mLayout = (LinearLayout) mInflater.inflate(R.layout.activity_office_summary_my_note_item, null);
		        TextView note = (TextView) mLayout.findViewById(R.id.note);
		        String reallyValue = backString(temp[i].replace((i+1) + "、", "-"));
		        note.setText((i + 1) + "、" + reallyValue);
				holder.layout.addView(mLayout);
			}
			
			holder.belongdate.setText(backDate(lists.get(position).getBelongDate()));
			holder.weekday.setText(getWeekDay(lists.get(position).getBelongDate()));
			return convertView;
		}
		
	}
	
	
	/**
	 * listview容器
	 * @author xsl
	 *
	 */
	static class ViewHolder {
		//动态添加layout
		private LinearLayout layout;
		private TextView belongdate;
		private TextView weekday;
	}
	
	/**
	 * 字符串分割
	 * @param str
	 * @return
	 */
	private String backString(String str){
		String aString = "";
		String[] temper = null;
		temper = str.split("-");
		for (int i = 0; i < temper.length; i++) {
			aString = aString + temper[i];
		}
		return aString;
		
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
	 * 根据日期查询星期
	 * @param date
	 * @return
	 */
	private static String getWeekDay(String date) {
		String Week = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(format.parse(date));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			Week += "周日";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 2) {
			Week += "周一";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 3) {
			Week += "周二";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 4) {
			Week += "周三";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 5) {
			Week += "周四";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 6) {
			Week += "周五";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 7) {
			Week += "周六";
		}

		return Week;
	}

	/**
	 * 上拉刷新
	 */
	public void onRefresh() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			public void run() {
				slistview.stopRefresh();
				slistview.stopLoadMore();
				slistview.setRefreshTime();
			}
		}, 2000);
		PagerIndex = 1;
		getNetData(Company_ID, Manager_ID, Department_ID, PagerIndex, 10, 1);
	}

	/**
	 * 下拉加载
	 */
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			public void run() {
				slistview.stopRefresh();
				slistview.stopLoadMore();
				slistview.setRefreshTime();
			}
		}, 2000);
		PagerIndex += 1;
		getNetData(Company_ID, Manager_ID, Department_ID, PagerIndex, 10, 2);
	}
	
	/**
	 * 销毁fragment
	 */
	@Override
	public void onDestroyView() {
	    Log.e(TAG , "-->onDestroyView");
	    super .onDestroyView(); 
	    if (null != myView) {
	        ((ViewGroup) myView.getParent()).removeView(myView);
	    }
	}
	
}
