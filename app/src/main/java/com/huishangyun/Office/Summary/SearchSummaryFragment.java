package com.huishangyun.Office.Summary;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.manager.DepartmentManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Departments;
import com.huishangyun.model.Methods;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.yun.R;
import com.nostra13.universalimageloader.core.ImageLoader;


/**
 * 小结查询fragment
 * @author xsl
 *
 */
public class SearchSummaryFragment extends Fragment implements MyXListView.MyXListViewListener {
	protected static final String TAG = null;
	private View searchView;
	private LinearLayout txt_layout;
	private int Company_ID;//公司id
	private int Manager_ID;//登录人id
	private int Department_ID;//部门id
	private MyXListView slistview;//listview
	private SummaryAdapter adapter;
	private TextView selectdate;//选择日期
	private TextView selectdepartment;//选择部门
	private LinearLayout searchbtn;//查询按钮
	private EditText edit_search_text;//输入的查询条件
	Dialog dialog;
	private List<SummaryDateList> itemLists = new ArrayList<SummaryDateList>();
	private List<SummaryDateList> list = new ArrayList<SummaryDateList>();
	private int PagerIndex = 1;
	ArrayList<HashMap<String, Object>> ItemLists = new ArrayList<HashMap<String, Object>>();	
	HashMap<String, Object> map;//定义一个map函数
    private	int mPosition = -1;
    private ImageView mImageView ;//定义一个imageview监听自定义对话框对勾图片地址
    private int selectDepartment_ID;
    private int check = 0;
    private ImageView no_information;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (searchView==null) {
		searchView = inflater.inflate(R.layout.activity_office_summary_searchfragment,container, false);
		Company_ID = getActivity().getIntent().getIntExtra("Company_ID", 0);
		Manager_ID = getActivity().getIntent().getIntExtra("Manager_ID", 0);
		Department_ID = getActivity().getIntent().getIntExtra("Department_ID", 0);
		init();
		}
		return searchView;
		
	}

	/**
	 * 广播接收器
	 */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			int  mflag = intent.getIntExtra("mflag", -1);
			if (mflag==2) {
//				getNetData(Company_ID, 0, Department_ID, "", "", PagerIndex, 8, 1,false);
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
		no_information = (ImageView) searchView.findViewById(R.id.no_information);
		//注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("SUMMARY_LIST_REFRESH");
		intentFilter.setPriority(Integer.MAX_VALUE);
		getActivity().registerReceiver(broadcastReceiver, intentFilter);
		
		txt_layout = (LinearLayout) searchView.findViewById(R.id.txt_layout);
		txt_layout.setPadding(0, 0, 0, 0);
		selectdate = (TextView) searchView.findViewById(R.id.selectdate);
		selectdepartment = (TextView) searchView.findViewById(R.id.selectdepartment);
		searchbtn = (LinearLayout) searchView.findViewById(R.id.searchbtn);
		edit_search_text = (EditText) searchView.findViewById(R.id.edit_search_text);
		selectdate.setOnClickListener(buttonClickListener);
		selectdepartment.setOnClickListener(buttonClickListener);
		searchbtn.setOnClickListener(buttonClickListener);
		edit_search_text.setOnClickListener(buttonClickListener);
		slistview = (MyXListView) searchView.findViewById(R.id.mlistview);
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
//		getNetData(Company_ID, 0, Department_ID, "", "", PagerIndex, 8, 1,false);
	}
	
	
	/**
	 * 单击事件处理
	 */
	private OnClickListener buttonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
		
			switch (v.getId()) {
			case R.id.selectdate://选择日期
				selectDate();
				break;
			case R.id.selectdepartment://选择部门
				ItemLists.clear();
				setItemDate();
				Dialog();
				break;
			case R.id.edit_search_text:
				
				break;
			case R.id.searchbtn://查询按钮
				check = 1;
				PagerIndex = 1;
				getNetData(Company_ID, 0, selectDepartment_ID, selectdate.getText().toString(),
						edit_search_text.getText().toString().trim(), PagerIndex, 8, 1,true);
				
				break;

			default:
				break;
			}
			
		}
	};
	
	
	private void getNetData(final int Company_ID, final int Manager_ID,
			final int Department_ID,final String BelongDate,final String Manager_Name,
			final int pageIndex,final int pageSize, final int index,final Boolean isSearch){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub					
				String result = DataUtil.callWebService(
						Methods.GET_SUMMARY_LIST,
						getJson(Company_ID, Manager_ID, Department_ID,BelongDate,
								Manager_Name,pageIndex,pageSize));	
				Log.e(TAG, "result:" + result);
				Log.e(TAG, "result:" + getJson(Company_ID, Manager_ID, Department_ID,BelongDate,
						Manager_Name,pageIndex,pageSize));
				
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
					Log.e(TAG, "-----------0000000000");
				
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
			int Department_ID, String BelongDate,
			String Manager_Name, int pageIndex, int pageSize) {
		
		ZJRequest zjRequest = new ZJRequest();
		// 公司id
		zjRequest.setCompany_ID(Company_ID);
		// 用户编号
		zjRequest.setManager_ID(Manager_ID);
		// 设置部门号，0时为相当没有部门编号查询
		zjRequest.setDepartment_ID(Department_ID);
		
		if (!BelongDate.trim().equals("")) {
			zjRequest.setBelongDate(BelongDate);
		}
		if (!Manager_Name.trim().equals("")) {
			zjRequest.setManager_Name(Manager_Name);
		}
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
			String person_url = Constant.pathurl+Company_ID+"/Photo/" + lists.get(position).getManager_Photo();
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
		if (check!=0) {
			getNetData(Company_ID, 0, selectDepartment_ID, selectdate.getText().toString(),
					edit_search_text.getText().toString().trim(), 1, 8, 1,false);	
		}else {
//			getNetData(Company_ID, 0, Department_ID, "", "", PagerIndex, 8, 1,false);
		}
		
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
		
		if (check!=0) {
			PagerIndex += 1;
			getNetData(Company_ID, 0, selectDepartment_ID, selectdate.getText().toString(),
					edit_search_text.getText().toString().trim(), PagerIndex, 8, 2,false);	
		}else {
//			PagerIndex += 1;
//			getNetData(Company_ID, 0, Department_ID, "", "", PagerIndex, 8, 2,false);
		}
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		PagerIndex =1;
//		getNetData(Company_ID, 0, Department_ID, "", "", PagerIndex, 10, 1);
		
	}

	/**
	 * 日期选择
	 */
	private void selectDate(){
		Calendar cal = Calendar.getInstance();
		dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// TODO Auto-generated method stub
				Date date = new Date(year-1900, monthOfYear, dayOfMonth);
				SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");
				String time = format.format(date);
				selectdate.setText(time);
			}
		}, cal.get(Calendar.YEAR),
		cal.get(Calendar.MONTH), 
		cal.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}
	
	/**
	  * 向二维数组赋值
	  */
	private void setItemDate(){
		List<Departments>  list = new ArrayList<Departments>();
		//查询全部部门
		list = DepartmentManager.getInstance(getActivity()).getllDepartments();;
		for (int i = 0; i < list.size(); i++) {
			map = new HashMap<String, Object>();
			map.put("name", list.get(i).getName());
			map.put("id", list.get(i).getID());
			ItemLists.add(map);
			Log.e(TAG,"ItemLists.SIZE:" +  ItemLists.size());
		}

	}

	/**
	 * 显示悬浮框
	 */
	public void Dialog(){
		mPosition = -1;
		LayoutInflater layoutInflater = LayoutInflater
				.from(getActivity());
		View  customdialog= layoutInflater.inflate(R.layout.activity_clue_new_customdialog, null);
		
        //创建一个对话框对象
		final AlertDialog alertDialog = new AlertDialog.Builder(
				getActivity()).create();
		//设置对话框背景颜色
		alertDialog.setIcon(R.color.white);
		//设置对话显示位置
		alertDialog.setView(customdialog, -1, -1, 0, -1);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//自定义对话框确定按钮
		TextView customdialog_sure = (TextView) customdialog.findViewById(R.id.customdialog_sure);
		ListView customdialog_listview =(ListView) customdialog.findViewById(R.id.customdialog_listview);
		TextView dialog_titlestr = (TextView) customdialog.findViewById(R.id.dialog_titlestr);
		dialog_titlestr.setText("选择部门");
		ImageView cancel =(ImageView) customdialog.findViewById(R.id.cancel);
		//这个控件本身没有用，只是用来过渡第一次按键效果，使mImageView.setVisibility(View.INVISIBLE);不会报空指针。
		mImageView =(ImageView) customdialog.findViewById(R.id.cancel_nice);
		alertDialog.show();
		DialogAdapter dialog_listview_adapter = new DialogAdapter(getActivity());
		customdialog_listview.setAdapter(dialog_listview_adapter);
		
		//listview的item单击事件
		customdialog_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub	
				
				ImageView img = (ImageView) view.findViewById(R.id.customdialoglistview_item_img);
				TextView textView = (TextView) view.findViewById(R.id.customdialoglistview_item_text);
				//判断控件是否隐藏
				switch (img.getVisibility()) {
//				case View.VISIBLE:					
//					img.setVisibility(View.INVISIBLE);						
//					break;
					
				case View.INVISIBLE:					
					mImageView.setVisibility(View.INVISIBLE);
					img.setVisibility(View.VISIBLE);
					//设置文字
					//mtextview = textView.getText().toString();
					mPosition = position;
					mImageView = img;
					break;
				default:
					break;
				}

			}			
		});
		
		//自定义单击事件，确定按钮单击事件
		customdialog_sure.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//设置文字
				//source.setText(mtextview);
				if (mPosition == -1) {
//					alertDialog.cancel();
					new ClueCustomToast().showToast(getActivity(),
							R.drawable.toast_warn, "请选择部门！");
					
				}else {
					selectdepartment.setText((CharSequence) ItemLists.get(mPosition).get("name"));
					selectDepartment_ID = Integer.parseInt(ItemLists.get(mPosition).get("id") + "");
					alertDialog.cancel();
				}
				
			}
		});
		
		//取消按钮单击事件
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.cancel();
			}
		});
	}
	
	/**
	 * 自定义对话框lsitview适配器
	 * @author XSL
	 *
	 */
	 private class DialogAdapter extends BaseAdapter{
		 
		 private LayoutInflater mInflater;// 动态布局映射

			public DialogAdapter(Context context) {
				this.mInflater = LayoutInflater.from(context);
			}			

			// 决定listview显示条数
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return ItemLists.size();
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
				convertView = mInflater.inflate(R.layout.activity_clue_new_customdialog_listviewitem, null);
				TextView customdialoglistview_item_text = (TextView)convertView.findViewById(R.id.customdialoglistview_item_text);
				ImageView customdialoglistview_item_img = (ImageView)convertView.findViewById(R.id.customdialoglistview_item_img);
				customdialoglistview_item_text.setText((String) ItemLists.get(position).get("name"));
				return convertView;
			}
	 }		
	 
	 @Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		    if (null != searchView) {
		        ((ViewGroup) searchView.getParent()).removeView(searchView);
		    }
	}
	
}
