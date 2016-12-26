package com.huishangyun.Channel.Customers;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Adapter.TaskMenuAdapter;
import com.huishangyun.Channel.Task.MenuMoths;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Members;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.swipelistview.MyXListView.MyXListViewListener;
import com.huishangyun.yun.R;

/**
 * 客户更多页面
 * @author Pan
 *
 */
public class CustomerMenuActivity extends BaseActivity implements MyXListViewListener, OnClickListener {
	private ListView listView;
	private TaskMenuAdapter mAdapter;
	private int type;
	private List<MenuMoths> mList;
	private int PageIndex = 1;
	private boolean isLoadMore = false;
	private MyXListView myXListView;
	private CustomerListAdapter adapter;
	private List<Members> list = new ArrayList<Members>();
	private String imsi;
	private String imei;
	private String Product_ID;
	private boolean isSelect = false;
	private LinearLayout backLayout;//返回按钮
	private LinearLayout create_customer;//创建任务
	private LinearLayout more_option;//更多
	private LayoutInflater mInflater;
	private PopupWindow mWindow;//菜单选项
	private DisplayMetrics dm;//获取屏幕尺寸
	private int ID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_menu);
		initView();
		//获取分类
		initPoptWindow();
		showDialog("正在加载...");
		new Thread(new getType(type)).start();
	}
	
	/**
	 * 实例化组件
	 */
	private void initView() {
		mInflater = LayoutInflater.from(CustomerMenuActivity.this);
		listView = (ListView) this.findViewById(R.id.task_menu_listview);
		myXListView = (MyXListView) this.findViewById(R.id.task_menu_myxlistview);
		myXListView.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		more_option = (LinearLayout) findViewById(R.id.task_more_option);
		backLayout = (LinearLayout) findViewById(R.id.task_menu_back);
		create_customer = (LinearLayout) findViewById(R.id.menu_create_customer);
		type = getIntent().getIntExtra("Type", 0);
		mList = new ArrayList<MenuMoths>();
		mAdapter = new TaskMenuAdapter(CustomerMenuActivity.this, mList);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(mClickListener);
		adapter = new CustomerListAdapter(CustomerMenuActivity.this, list);
		myXListView.setPullLoadEnable(true);//设置能下拉（这两个属性是上拉加载下拉刷新的）
		myXListView.setMyXListViewListener(this);
		myXListView.setOnItemClickListener(mClickListener);
		myXListView.setAdapter(adapter);
		backLayout.setOnClickListener(this);
		create_customer.setOnClickListener(this);
		more_option.setOnClickListener(this);
		TextView textView = (TextView) findViewById(R.id.custemor_menu_txt);
		textView.setText("  客户");
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
	}
	
	/**
	 * listview点击事件监听
	 */
	private OnItemClickListener mClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if (arg0.getId() == R.id.task_menu_listview) {
				myXListView.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
				switch (type) {
				case 3:
					ID = mList.get(arg2).getID();
					new Thread(new getAllList(ID, PageIndex)).start();
					showDialog("正在加载...");
					break;
				case 4:
					ID = mList.get(arg2).getID();
					new Thread(new getAllList(ID, PageIndex)).start();
					showDialog("正在加载...");
					break;
				case 5:
					ID = mList.get(arg2).getID();
					new Thread(new getAllList(ID, PageIndex)).start();
					showDialog("正在加载...");
					break;

				default:
					break;
				}
			} else {
				if (arg2 == 0) {
					return;
				}
				//跳转到任务列表界面
				Intent mIntent = new Intent(CustomerMenuActivity.this, CustomerInfo.class);
				mIntent.putExtra("flage", "xiangqing");
				mIntent.putExtra("MemberID", list.get(arg2 - 1).getID());
				startActivityForResult(mIntent, 0);
			}
			
		}
	};
	
	
	private void startGetAll() {
		switch (type) {
		case 3:
			new Thread(new getAllList(ID, PageIndex)).start();
			showDialog("正在加载...");
			break;
		case 4:
			new Thread(new getAllList(ID, PageIndex)).start();
			showDialog("正在加载...");
			break;
		case 5:
			new Thread(new getAllList(ID, PageIndex)).start();
			showDialog("正在加载...");
			break;

		default:
			break;
		}
	}
	
	/**
	 * 结果返回
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dismissDialog();
			myXListView.setRefreshTime();
			myXListView.stopLoadMore();
			myXListView.stopRefresh();
			switch (msg.what) {
			case HanderUtil.case1:
				adapter.notifyDataSetChanged();
				break;
			case HanderUtil.case2:
				showCustomToast((String) msg.obj, false);
				break;
			case HanderUtil.case3:
				mAdapter.notifyDataSetChanged();
				myXListView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * 查询服务器内部类
	 * @author Pan
	 *
	 */
	private class getType implements Runnable {
		private int type;

		public getType(int type) {
			this.type = type;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			List<MenuMoths> list = new ArrayList<MenuMoths>();
			int ID;
			boolean haveType;
			if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {//判断是否有权限
				ID = Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0"));
				haveType = false;
			} else {
				ID = preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0);
				haveType = true;
			}
			switch (type) {
			case 3://分组
				list = MemberManager.getInstance(CustomerMenuActivity.this).getMemberGroups(ID, haveType);
				break;
			case 4://等级
				list = MemberManager.getInstance(CustomerMenuActivity.this).getLevelCount(ID, haveType);
				break;
			case 5://类型
				list = MemberManager.getInstance(CustomerMenuActivity.this).getMemberType(ID, haveType);
				break;
			default:
				break;
			}
			//是否有返回值
			if (list == null || list.size() == 0) {
				Message msg = new Message();
				msg.what = HanderUtil.case2;
				msg.obj = "获取失败";
				mHandler.sendMessage(msg);
				return;
			}
			mList.clear();
			for (MenuMoths menuMoths : list) {
				menuMoths.setType(CustomerMenuActivity.this.type);
				mList.add(menuMoths);
			}
			mHandler.sendEmptyMessage(HanderUtil.case3);
		}

	}
	
	
	/**
	 * 获取客户列表的线程
	 * @author Pan
	 *
	 */
	private class getAllList implements Runnable {
		
		private int Group_ID;
		private int pageIndex;
		
		public getAllList(int Group_ID, int pageIndex) {
			L.e("json = " + Group_ID);
			this.Group_ID = Group_ID;
			this.pageIndex = pageIndex;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			List<Members> results = new ArrayList<Members>();
			int ID;
			boolean haveType;
			if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
				ID = Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0"));
				haveType = false;
			} else {
				ID = preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0);
				haveType = true;
			}
			switch (type) {
			case 3:
				results = MemberManager.getInstance(CustomerMenuActivity.this).getMemberGroupForID(Group_ID, ID, pageIndex, haveType);
				break;
			case 4:
				results = MemberManager.getInstance(CustomerMenuActivity.this).getMemberLevelForID(Group_ID, ID, pageIndex, haveType);
				break;
			case 5:
				results = MemberManager.getInstance(CustomerMenuActivity.this).getMeneberTypeForID(Group_ID, ID, pageIndex, haveType);
				break;

			default:
				break;
			}
			if (results == null || results.size() == 0) {
				Message msg = new Message();
				msg.what = HanderUtil.case2;
				msg.obj = "没有更多了";
				mHandler.sendMessage(msg);
				return;
			}
			if (!isLoadMore) {
				list.clear();
			} 
			for (Members members : results) {
				list.add(members);
			}
			mHandler.sendEmptyMessage(HanderUtil.case1);
			PageIndex++;
		}
		
	}
	
	/**
	 * 创建PopuptWindow对象
	 */
	private void initPoptWindow(){
		//加载布局
		View mPopView = mInflater.inflate(R.layout.custormer_popwindow, null);
		mWindow = new PopupWindow(mPopView);
		
		//设置获取焦点
		mWindow.setFocusable(true); 
		
		//防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
		mWindow.setBackgroundDrawable(new PaintDrawable()); 
		
		//设置点击窗口外边窗口消失 
		mWindow.setOutsideTouchable(true); 		
		mWindow.update();
		
		 // 设置弹出窗体的宽
		mWindow.setWidth(dm.widthPixels / 2);
		
        // 设置弹出窗体的高
		mWindow.setHeight(LayoutParams.WRAP_CONTENT);
		
		//获取控件对象
		LinearLayout stateBtn = (LinearLayout) mPopView.findViewById(R.id.custormer_pop_group);
		LinearLayout timeBtn = (LinearLayout) mPopView.findViewById(R.id.custormer_pop_rating);
		LinearLayout priorityBtn = (LinearLayout) mPopView.findViewById(R.id.custormer_pop_type);
		
		//设置点击时间监听
		stateBtn.setOnClickListener(this);
		timeBtn.setOnClickListener(this);
		priorityBtn.setOnClickListener(this);
		
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		PageIndex = 1;
		isLoadMore = false;
		startGetAll();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		isLoadMore = true;
		startGetAll();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent mIntent = null;
		switch (v.getId()) {
		case R.id.task_more_option://更多
			L.e("点击更多按钮");
			mWindow.showAsDropDown(more_option);//弹出菜单选项
			break;
		case R.id.menu_create_customer://创建任务
			//mIntent = new Intent(TaskMenuActivity.this, TaskAddActivity.class);
			mIntent = new Intent(CustomerMenuActivity.this, CustomerSet.class);
			mIntent.putExtra("flage", "chuangjian");
			startActivityForResult(mIntent, 0);/*requestCode*/
			mIntent = null;
			break;
		case R.id.task_menu_back://返回按钮
			finish();
			break;
		case R.id.custormer_pop_group://分组
			L.e("点击了分组");
			type = 3;
			mWindow.dismiss();
			showDialog("正在加载...");
			PageIndex = 1;
			list.clear();
			isLoadMore = false;
			adapter.notifyDataSetChanged();
			new Thread(new getType(type)).start();
			break;
			
		case R.id.custormer_pop_type://类型
			L.e("点击了类型");
			type = 5;
			mWindow.dismiss();
			showDialog("正在加载...");
			PageIndex = 1;
			isLoadMore = false;
			list.clear();
			adapter.notifyDataSetChanged();
			new Thread(new getType(type)).start();
			break;
			
		case R.id.custormer_pop_rating://等级
			L.e("点击了等级");
			type = 4;
			PageIndex = 1;
			mWindow.dismiss();
			showDialog("正在加载...");
			list.clear();
			isLoadMore = false;
			adapter.notifyDataSetChanged();
			new Thread(new getType(type)).start();
			
			break;
		default:
			break;
		}
		if (mIntent != null) {
			startActivity(mIntent);
		}
	}
}
