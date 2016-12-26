package com.huishangyun.Office.Businesstrip;

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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Methods;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.Office.Summary.ImageLoad;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.widget.CircleBitmapDisplayer;
import com.huishangyun.yun.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class DepartmentBusinessTripFragment extends Fragment implements MyXListView.MyXListViewListener {
	protected static final String TAG = null;
	View departmentView;
	private MyXListView businesstriplistview;
	private BusinessTripAdapter adapter;
	private int Company_ID;//公司id
	private int Manager_ID;//登录人id
	private int Department_ID;//部门id
	private int PageIndex = 1;//页码
	private List<BusinessTripdata> listdata = new ArrayList<BusinessTripdata>();
	private List<BusinessTripdata> list = new ArrayList<BusinessTripdata>();
	private ImageView no_information;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (departmentView==null) {
	    departmentView = inflater.inflate(R.layout.activity_office_allbusinesstripfragment,container, false);
	    Company_ID = getActivity().getIntent().getIntExtra("Company_ID", 1016);
		Manager_ID = getActivity().getIntent().getIntExtra("Manager_ID", 0);
		Department_ID = getActivity().getIntent().getIntExtra("Department_ID", 0);
		
		//注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("BUSINESSTRIP_LIST_REFRESH");
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
						Methods.GET_BUSINESSTRIP_LIST,
						getJson(Company_ID, Manager_ID, Department_ID,
								pageIndex, pageSize));
				Log.e(TAG, "result:" + result + pageIndex);
				
				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					// 获取对象的Type
					Type type = new TypeToken<ZJResponse<BusinessTripdata>>() {
					}.getType();
					ZJResponse<BusinessTripdata> zjResponse = JsonUtil.fromJson(result,
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
//					Toast.makeText(getActivity(), "没有更多数据！！", Toast.LENGTH_SHORT).show();
					showDialog("没有更多数据！");
				}else {
					
					for (int i = 0; i < listdata.size(); i++) {
						list.add(listdata.get(i));
					}
					adapter.notifyDataSetChanged();
				}
			
				break;
			case 2:
//				Toast.makeText(getActivity(), "未获得任何网络数据，请检查网络连接！", Toast.LENGTH_SHORT).show();
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
     * 界面初始化
     */
	private void init() {
		no_information = (ImageView) departmentView.findViewById(R.id.no_information);
		businesstriplistview = (MyXListView) departmentView.findViewById(R.id.businesstriplistview);
		adapter = new BusinessTripAdapter(getActivity(),list);
		businesstriplistview.setAdapter(adapter);
		businesstriplistview.setPullLoadEnable(true);
		businesstriplistview.setMyXListViewListener(this);
		
		businesstriplistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						BusinessTripDetailActivity.class);
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
	private class BusinessTripAdapter extends BaseAdapter{
		private LayoutInflater mInflater;// 动态布局映射
		private List<BusinessTripdata> Lists;
        private Context context;
		public BusinessTripAdapter(Context context,List<BusinessTripdata> lists) {
			this.context = context;
			this.Lists = lists;
			this.mInflater  = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
//			return Lists.size();
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
							R.layout.activity_office_businesstrip_listview_item, null);// 根据布局文件实例化view
					holder = new ViewHolder();
					holder.date = (TextView) convertView.findViewById(R.id.date);
					holder.person_img = (ImageView) convertView.findViewById(R.id.person_img);
					holder.name = (TextView) convertView.findViewById(R.id.name);
					holder.start_address = (TextView) convertView.findViewById(R.id.start_address);
					holder.end_address = (TextView) convertView.findViewById(R.id.end_address);
					holder.time = (TextView) convertView.findViewById(R.id.time);		
					holder.state = (TextView) convertView.findViewById(R.id.state);
					holder.days = (TextView) convertView.findViewById(R.id.days);
					holder.days_linear = (LinearLayout) convertView.findViewById(R.id.data_linear);
					convertView.setTag(holder);
				} else {
					
					holder = (ViewHolder) convertView.getTag();
					
				}

				//第一条，都显示日期条
				if (position == 0) {
					holder.days_linear.setVisibility(View.VISIBLE);
				}else {
					//当前日期等于上一日期，隐藏日期条
					if (backDate(Lists.get(position).getAddDateTime()).
							equals(backDate(Lists.get(position - 1).getAddDateTime()))) {
						holder.days_linear.setVisibility(View.GONE);

					}else {
						holder.days_linear.setVisibility(View.VISIBLE);
					}
				}
				
				holder.date.setText(backDate(Lists.get(position).getAddDateTime()));
				holder.name.setText(Lists.get(position).getManager_Name());
				holder.start_address.setText(Lists.get(position).getStartCity());
				holder.end_address.setText(Lists.get(position).getEndCity());
				holder.time.setText(backDateMin(Lists.get(position).getAddDateTime()));
				holder.days.setText(Lists.get(position).getDays() + "天"); 
				
				String person_url = "http://img.huishangyun.com/UploadFile/huishang/"+ 
		                Company_ID+"/Photo/" + list.get(position).getManager_Photo();
				//new ImageLoad().displayImage(getActivity(), person_url, holder.person_img, R.drawable.person_img, 10, false);
				com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(person_url,holder.person_img,options);
				if (Lists.get(position).getFlag()==0) {//提交，未出发
					//出发地点和结束地点颜色控制
					/*holder.start_address.setTextColor(0xffe95811);
					holder.end_address.setTextColor(0xffe95811);*/
					holder.state.setVisibility(View.INVISIBLE);
					
					
					
				}else if (Lists.get(position).getFlag()==1) {//已出发
					//出发地点和结束地点颜色控制
					/*holder.start_address.setTextColor(0xff21a5de);
					holder.end_address.setTextColor(0xffe95811);*/
					holder.state.setVisibility(View.INVISIBLE);
					
					
					
				}else if (Lists.get(position).getFlag()==2) {//已到达
					/*holder.start_address.setTextColor(0xff21a5de);
					holder.end_address.setTextColor(0xff21a5de);*/
					holder.state.setVisibility(View.INVISIBLE);
					
					
				}else if (Lists.get(position).getFlag()==3) {//取消
					//出发地点和结束地点颜色控制
					/*holder.start_address.setTextColor(0xffe95811);
					holder.end_address.setTextColor(0xffe95811);*/
					holder.state.setVisibility(View.VISIBLE);
					holder.state.setText("已取消");
					
					
				}
			
			return convertView;
		}
		
	}

	DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.displayer(new CircleBitmapDisplayer())
			.showImageOnLoading(R.drawable.contact_person) //设置图片在下载期间显示的图片
			.showImageForEmptyUri(R.drawable.contact_person)//设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.contact_person)  //设置图片加载/解码过程中错误时候显示的图片
			.build();
	
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
        //出发地点
        private TextView start_address; 
        //结束地点
        private TextView end_address;
        //详细创建时间
        private TextView time;
        //状态
        private TextView state; 
        //天数
        private TextView days;

		private LinearLayout days_linear;

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
				businesstriplistview.stopRefresh();
				businesstriplistview.stopLoadMore();
				businesstriplistview.setRefreshTime();
				
				
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
				businesstriplistview.stopRefresh();
				businesstriplistview.stopLoadMore();
				businesstriplistview.setRefreshTime();
				
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
