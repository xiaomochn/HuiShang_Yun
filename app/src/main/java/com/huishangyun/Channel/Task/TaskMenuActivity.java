package com.huishangyun.Channel.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Adapter.TaskAdapter;
import com.huishangyun.Adapter.TaskMenuAdapter;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.TakeDetails;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.manager.EnumManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.EnumKey;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.model.Content;
import com.huishangyun.model.Methods;
import com.huishangyun.swipelistview.MyXListView.MyXListViewListener;
import com.huishangyun.yun.R;

/**
 * 任务更多界面
 * 
 * @author Pan
 * 
 */
public class TaskMenuActivity extends BaseActivity implements MyXListViewListener, OnClickListener{
	private ListView listView;
	private TaskMenuAdapter mAdapter;
	private String groupField = "";
	private int type;
	private List<MenuMoths> mList;
	private int PageIndex = 1;
	private boolean isLoadMore = false;
	private MyXListView myXListView;
	private TaskAdapter adapter;
	private List<TakeDetails> list = new ArrayList<TakeDetails>();
	private String imsi;
	private String imei;
	private String Product_ID;
	private boolean isSelect = false;
	private LinearLayout backLayout;//返回按钮
	private LinearLayout create_task;//创建任务
	private LinearLayout more_option;//更多
	private LayoutInflater mInflater;
	private PopupWindow mWindow;//菜单选项
	private DisplayMetrics dm;//获取屏幕尺寸
	private TextView more_year;
	//获取任务列表线程
	private webServiceHelp<TakeDetails> taskServiceHelp;
	//获取分类列表线程
	private webServiceHelp<MenuMoths> menuServiceHelp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_menu);
		initView();
		//获取分类
		initPoptWindow();
		showDialog("正在加载...");
		taskServiceHelp = new webServiceHelp<TakeDetails>(Methods.TASK_LIST, new TypeToken<ZJResponse<TakeDetails>>(){}.getType());
		taskServiceHelp.setOnServiceCallBack(taskOnServiceCallBack);
		menuServiceHelp = new webServiceHelp<MenuMoths>(Methods.TASK_GETMENU, new TypeToken<ZJResponse<MenuMoths>>() {}.getType());
		menuServiceHelp.setOnServiceCallBack(menuOnServiceCallBack);
		//new Thread(new getType(getJson(groupField))).start();
		menuServiceHelp.start(getJson(groupField));
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		taskServiceHelp.removeOnServiceCallBack();
		menuServiceHelp.removeOnServiceCallBack();
		super.onDestroy();
	}
	
	/**
	 * 实例化组件
	 */
	private void initView() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年");       
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
		String str = formatter.format(curDate);
		mInflater = LayoutInflater.from(TaskMenuActivity.this);
		listView = (ListView) this.findViewById(R.id.task_menu_listview);
		myXListView = (MyXListView) this.findViewById(R.id.task_menu_myxlistview);
		myXListView.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		more_option = (LinearLayout) findViewById(R.id.task_more_option);
		backLayout = (LinearLayout) findViewById(R.id.task_menu_back);
		create_task = (LinearLayout) findViewById(R.id.menu_create_customer);
		groupField = getIntent().getStringExtra("Field");
		type = getIntent().getIntExtra("Type", 0);
		mList = new ArrayList<MenuMoths>();
		mAdapter = new TaskMenuAdapter(TaskMenuActivity.this, mList);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(mClickListener);
		adapter = new TaskAdapter(TaskMenuActivity.this, list);
		myXListView.setPullLoadEnable(true);//设置能下拉（这两个属性是上拉加载下拉刷新的）
		myXListView.setMyXListViewListener(this);
		myXListView.setOnItemClickListener(mClickListener);
		myXListView.setAdapter(adapter);
		backLayout.setOnClickListener(this);
		create_task.setOnClickListener(this);
		more_option.setOnClickListener(this);
		dm = new DisplayMetrics();
		more_year = (TextView) findViewById(R.id.task_more_year);
		more_year.setText(str);
		more_year.setVisibility(View.GONE);
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
				case 0:
					imsi = EnumManager.getInstance(TaskMenuActivity.this).getIntKey(mList.get(arg2).getTitle(), EnumKey.ENUM_TASKSTATUS);
					//new Thread(new getAllList(getJson(PageIndex, null, Integer.parseInt(imsi), null))).start();
					taskServiceHelp.start(getJson(PageIndex, null, Integer.parseInt(imsi), null));
					showDialog("正在加载...");
					break;
				case 1:
					imei = mList.get(arg2).getTitle();
					//new Thread(new getAllList(getJson(PageIndex, Integer.parseInt(imei), null, null))).start();
					taskServiceHelp.start(getJson(PageIndex, Integer.parseInt(imei), null, null));
					showDialog("正在加载...");
					break;
				case 2:
					Product_ID = EnumManager.getInstance(TaskMenuActivity.this).getIntKey(mList.get(arg2).getTitle(), EnumKey.ENUM_TASKFLAG);
					//new Thread(new getAllList(getJson(PageIndex, null, null, Integer.parseInt(Product_ID)))).start();
					taskServiceHelp.start(getJson(PageIndex, null, null, Integer.parseInt(Product_ID)));
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
				Intent mIntent = new Intent(TaskMenuActivity.this, TaskDetailsActivity.class);
				mIntent.putExtra("ID", list.get(arg2 - 1).getID());
				startActivity(mIntent);
			}
			
		}
	};
	
	
	private void startGetAll() {
		switch (type) {
		case 0:
			//String imsi = EnumManager.getInstance(TaskMenuActivity.this).getIntKey(mList.get(arg2).getTitle(), EnumKey.ENUM_TASKSTATUS);
			//new Thread(new getAllList(getJson(PageIndex, null, Integer.parseInt(imsi), null))).start();
			taskServiceHelp.start(getJson(PageIndex, null, Integer.parseInt(imsi), null));
			showDialog("正在加载...");
			break;
		case 1:
			//String imei = mList.get(arg2).getTitle();
			//new Thread(new getAllList(getJson(PageIndex, Integer.parseInt(imei), null, null))).start();
			taskServiceHelp.start(getJson(PageIndex, Integer.parseInt(imei), null, null));
			showDialog("正在加载...");
			break;
		case 2:
			//String Product_ID = EnumManager.getInstance(TaskMenuActivity.this).getIntKey(mList.get(arg2).getTitle(), EnumKey.ENUM_TASKFLAG);
			//new Thread(new getAllList(getJson(PageIndex, null, null, Integer.parseInt(Product_ID)))).start();
			taskServiceHelp.start(getJson(PageIndex, null, null, Integer.parseInt(Product_ID)));
			showDialog("正在加载...");
			break;

		default:
			break;
		}
	}
	
	/**
	 * 生成查询语句
	 * @param groupField
	 * @return
	 */
	private String getJson(String groupField) {
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setKeywords(groupField);
		zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 0));
		if (preferences.getString(Constant.HUISHANG_TYPE, "1").equals("1")) {
			zjRequest.setManager_ID(Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANGYUN_UID, "0")));
		} else {
			zjRequest.setDepartment_ID(MyApplication.preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0));
		}
		return JsonUtil.toJson(zjRequest);
	}
	
	/**
	 * 生成查询语句
	 * @param groupField
	 * @return
	 */
	private String getJson(int PageIndex, Integer Month, Integer Status, Integer Flag) {
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setCompany_ID(MyApplication.preferences.getInt(Content.COMPS_ID, 0));
		if (preferences.getString(Constant.HUISHANG_TYPE, "1").equals("1")) {
			zjRequest.setManager_ID(Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANGYUN_UID, "0")));
		} else {
			zjRequest.setDepartment_ID(MyApplication.preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0));
		}
		zjRequest.setKeywords("");
		zjRequest.setMonth(Month);
		zjRequest.setStatus(Status);
		zjRequest.setFlag(Flag);
		zjRequest.setClass_ID(0);
		zjRequest.setPageIndex(PageIndex);
		zjRequest.setPageSize(10);
		return JsonUtil.toJson(zjRequest);
		
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
				more_year.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged();
				break;
			case HanderUtil.case2:
				showCustomToast((String) msg.obj, false);
				break;
			case HanderUtil.case3:
				if (type == 1) {
					more_year.setVisibility(View.VISIBLE);
				} else {
					more_year.setVisibility(View.GONE);
				}
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
	 * 获取分类监听
	 */
	private webServiceHelp.OnServiceCallBack<MenuMoths> menuOnServiceCallBack = new webServiceHelp.OnServiceCallBack<MenuMoths>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<MenuMoths> zjResponse) {
			// TODO Auto-generated method stub
			if (haveCallBack && zjResponse != null) {
				if (zjResponse.getCode() == 0) {
					List<MenuMoths> list = zjResponse.getResults();
					//L.e("result = " + result);
					mList.clear();
					for (MenuMoths menuMoths : list) {
						menuMoths.setType(TaskMenuActivity.this.type);
						mList.add(menuMoths);
					}
					mHandler.sendEmptyMessage(HanderUtil.case3);
				} else {//服务器查询错误
					Message msg = new Message();
					msg.what = HanderUtil.case2;
					msg.obj = zjResponse.getDesc();
					mHandler.sendMessage(msg);
				}
			} else {
				Message msg = new Message();
				msg.what = HanderUtil.case2;
				msg.obj = "获取分类失败";
				mHandler.sendMessage(msg);
			}
		}
	};
	
	/**
	 * 获取任务列表监听
	 */
	private webServiceHelp.OnServiceCallBack<TakeDetails> taskOnServiceCallBack = new webServiceHelp.OnServiceCallBack<TakeDetails>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<TakeDetails> zjResponse) {
			// TODO Auto-generated method stub
			if (haveCallBack && zjResponse != null) {
				if (zjResponse.getCode() == 0) {//是否有返回结果
					List<TakeDetails> results;
					if (!isLoadMore) {//是否为上拉加载
						mList.clear();
						results = zjResponse.getResults();
						for (int i = 0; i < results.size(); i++) {
							list.add(results.get(i));
						}
						
					} else {
						for (int i = 0; i < zjResponse.getResults().size(); i++) {
							list.add((TakeDetails) zjResponse.getResults().get(i));
						}
					}
					//通知刷新
					mHandler.sendEmptyMessage(HanderUtil.case1);
					PageIndex++;
				} else {
					Message msg = new Message();
					msg.obj = zjResponse.getDesc();
					msg.what = HanderUtil.case2;
					mHandler.sendMessage(msg);
				}
			} else {
				Message msg = new Message();
				msg.obj = "无法访问服务器";
				msg.what = HanderUtil.case2;
				mHandler.sendMessage(msg);
			}
		}
	};
	
	/**
	 * 创建PopuptWindow对象
	 */
	private void initPoptWindow(){
		//加载布局
		View mPopView = mInflater.inflate(R.layout.task_popwindow, null);
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
		LinearLayout stateBtn = (LinearLayout) mPopView.findViewById(R.id.task_pop_state);
		LinearLayout timeBtn = (LinearLayout) mPopView.findViewById(R.id.task_pop_time);
		LinearLayout priorityBtn = (LinearLayout) mPopView.findViewById(R.id.task_pop_priority);
		
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
			mWindow.showAsDropDown(v);//弹出菜单选项
			break;
		case R.id.menu_create_customer://创建任务
			mIntent = new Intent(TaskMenuActivity.this, TaskAddActivity.class);
			break;
		case R.id.task_menu_back://返回按钮
			finish();
			break;
		case R.id.task_pop_state://状态
			L.e("点击了状态");
			/*mIntent = new Intent(TaskMainActivity.this, TaskMenuActivity.class);
			mIntent.putExtra("Field", "Status");
			mIntent.putExtra("Type", 0);*/
			groupField = "Status";
			type = 0;
			mWindow.dismiss();
			showDialog("正在加载...");
			PageIndex = 1;
			list.clear();
			adapter.notifyDataSetChanged();
			//new Thread(new getType(getJson(groupField))).start();
			menuServiceHelp.start(getJson(groupField));
			break;
			
		case R.id.task_pop_time://时间
			L.e("点击了时间");
			/*mIntent = new Intent(TaskMainActivity.this, TaskMenuActivity.class);
			mIntent.putExtra("Field", "Month");
			mIntent.putExtra("Type", 1);*/
			groupField = "Month";
			type = 1;
			mWindow.dismiss();
			showDialog("正在加载...");
			PageIndex = 1;
			list.clear();
			adapter.notifyDataSetChanged();
			//new Thread(new getType(getJson(groupField))).start();
			menuServiceHelp.start(getJson(groupField));
			break;
			
		case R.id.task_pop_priority://优先级
			L.e("点击了优先级");
			/*mIntent = new Intent(TaskMainActivity.this, TaskMenuActivity.class);
			mIntent.putExtra("Field", "Flag");
			mIntent.putExtra("Type", 2);*/
			groupField = "Flag";
			type = 2;
			PageIndex = 1;
			mWindow.dismiss();
			showDialog("正在加载...");
			list.clear();
			adapter.notifyDataSetChanged();
			//new Thread(new getType(getJson(groupField))).start();
			menuServiceHelp.start(getJson(groupField));
			
			break;
		default:
			break;
		}
		if (mIntent != null) {
			startActivity(mIntent);
		}
	}
}
