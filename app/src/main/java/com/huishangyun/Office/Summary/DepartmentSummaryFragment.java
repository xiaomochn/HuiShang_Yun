package com.huishangyun.Office.Summary;

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
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Methods;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.yun.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DepartmentSummaryFragment extends Fragment implements MyXListView.MyXListViewListener {
	protected static final String TAG = null;
	private View departmentView;
	private View allView;
	private int Company_ID;//公司id
	private int Manager_ID;//登录人id
	private int Department_ID;//部门id
	private MyXListView slistview;//listview
	private SummaryAdapter adapter;
	private List<SummaryDateList> itemLists = new ArrayList<SummaryDateList>();
	private List<SummaryDateList> list = new ArrayList<SummaryDateList>();
	private int PagerIndex = 1;
	private ImageView no_information;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (null == departmentView ) {
			departmentView = inflater.inflate(R.layout.activity_office_allbusinesstripfragment, container,false);
			Company_ID = getActivity().getIntent().getIntExtra("Company_ID", 0);
			Manager_ID = getActivity().getIntent().getIntExtra("Manager_ID", 0);
			Department_ID = getActivity().getIntent().getIntExtra("Department_ID", 0);
			init();
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
				getNetData(Company_ID, 0, Department_ID, PagerIndex, 8, 1);
			}
			
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
		no_information = (ImageView) departmentView.findViewById(R.id.no_information);
		//注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("SUMMARY_LIST_REFRESH");
		intentFilter.setPriority(Integer.MAX_VALUE);
		getActivity().registerReceiver(broadcastReceiver, intentFilter);
		
		Company_ID = getActivity().getIntent().getIntExtra("Company_ID", 1016);
		Manager_ID = getActivity().getIntent().getIntExtra("Manager_ID", 0);
		Department_ID = getActivity().getIntent().getIntExtra("Department_ID",
				0);
		slistview = (MyXListView) departmentView
				.findViewById(R.id.businesstriplistview);
		adapter = new SummaryAdapter(getActivity(),list);
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
				intent.putExtra("BelongDate", backDate(list.get(position-1).getBelongDate()));
				intent.putExtra("Manager_ID", list.get(position-1).getManager_ID());
				intent.putExtra("ID", list.get(position-1).getID());
				startActivity(intent);
				
			}
		});
		getNetData(Company_ID, 0, Department_ID, PagerIndex, 8, 1);
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
								pageIndex, pageSize));
				Log.e(TAG, "result:" + result);
				Log.e(TAG, "result:" + getJson(Company_ID, Manager_ID, Department_ID,
						 pageIndex,pageSize));
				
				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					// 获取对象的Type
					Type type = new TypeToken<ZJResponse<SummaryDateList>>() {
					}.getType();
					ZJResponse<SummaryDateList> zjResponse = JsonUtil.fromJson(result,
							type);
					// 获取对象列表
					itemLists.clear();
					itemLists = zjResponse.getResults();
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
				list.clear();
				for (int i = 0; i < itemLists.size(); i++) {
					list.add(itemLists.get(i));
				}
				if (list.size()==0) {
					no_information.setVisibility(View.VISIBLE);
				}else {
					no_information.setVisibility(View.GONE);
				}
				adapter.notifyDataSetChanged();
				break;
			case 1://加载更多
				
				if (itemLists.size() <= 0) {
					showDialog("没有更多数据！");
					
				}else {
					
					for (int i = 0; i < itemLists.size(); i++) {
						list.add(itemLists.get(i));
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
			if (convertView == null) {
				
				convertView = mInflater.inflate(R.layout.activity_office_summary_all_item, null);
				holder = new ViewHolder();
				holder.person_img = (ImageView) convertView.findViewById(R.id.person_img);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.date = (TextView) convertView.findViewById(R.id.date);
				holder.note = (TextView) convertView.findViewById(R.id.note);
				holder.belongdate = (TextView) convertView.findViewById(R.id.belongdate);
				
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (position!= 0) {
				if (backDate(lists.get(position).getBelongDate()).equals
						(backDate(lists.get(position-1).getBelongDate()))) {
					holder.belongdate.setVisibility(View.GONE);
				}else {
					holder.belongdate.setVisibility(View.VISIBLE);
				}
			}else {
				holder.belongdate.setVisibility(View.VISIBLE);
			}
			holder.name.setText(lists.get(position).getManager_Name());
			holder.belongdate.setText(backDate(lists.get(position).getBelongDate()));
			holder.date.setText(backDate(lists.get(position).getAddDateTime()));
			holder.note.setText(backwroks(lists.get(position).getWorks()));
			String person_url = "http://img.huishangyun.com/UploadFile/huishang/"+ 
	                Company_ID+"/Photo/" + lists.get(position).getManager_Photo();
//			ImageLoad.displayImage(person_url, holder.person_img, R.drawable.person_img,10);
			//new ImageLoad().displayImage(getActivity(), person_url, holder.person_img, R.drawable.person_img, 10, false);
			ImageLoader.getInstance().displayImage(person_url, holder.person_img, MyApplication.getInstance().getOptions());
			return convertView;
		}
		
	}
	
	/**
	 * listview容器
	 * @author xsl
	 *
	 */
	static class ViewHolder {

		private ImageView person_img;//头像
		private TextView name;//名字
		private TextView date;//日期
		private TextView note;//备注
		private TextView belongdate;
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
	 * 分割今天工作内容
	 * @param data
	 * @return
	 */
	private String backwroks(String data){
		String[] temp = null;
		temp = data.split("#");
		StringBuffer todaywroksBuffer = new StringBuffer("");
		for (int i = 0; i < temp.length; i++) {
			todaywroksBuffer.append((i+1)+"、");
			String reallyValue = backString(temp[i].replace((i+1) + "、", "-"));
			todaywroksBuffer.append(reallyValue + "	");
		}
		String dataString = todaywroksBuffer.toString();
		return dataString;	
	
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
		getNetData(Company_ID, 0, Department_ID, PagerIndex, 8, 1);
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
		getNetData(Company_ID, 0, Department_ID, PagerIndex, 8, 2);
	}
	
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
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		PagerIndex =1;
//		getNetData(Company_ID, 0, Department_ID, PagerIndex, 10, 1);
		
	}
}
