package com.huishangyun.Channel.Display;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Adapter.DisplayAdapter;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.DisplayDetails;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.Members;
import com.huishangyun.model.Methods;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.yun.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 陈列界面
 * 
 * @author 熊文娟
 * 
 */
public class DisplayMainActivity extends BaseActivity implements MyXListView.MyXListViewListener {
	
	private LinearLayout addLayout;//新增
	private LinearLayout backLayout;//返回
	private Intent mIntent;
    private MyXListView mListView;
    private DisplayAdapter displayAdapter;
    private List<DisplayDetails> mList = new ArrayList<DisplayDetails>();
    private List<DisplayDetails> mList2 = new ArrayList<DisplayDetails>();

    private int pageIndex = 1;//页码
    private int pageSize = 3;//条数
    private boolean isFinish = true;//判断是否加载完成
    
    private TextView customer_name;//客户名
    private int Member_ID = 0;//客户id
    Members aList = new Members();
    private String keywords = "";//关键字
    private ImageView no_information;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);
		initView();
	}

	/**
	 * 初始化组件
	 * 
	 */
	private void initView() {
		customer_name = (TextView) findViewById(R.id.customer_name);
		//获取资源
		addLayout = (LinearLayout) findViewById(R.id.display_add);
		backLayout=(LinearLayout) findViewById(R.id.display_back);
		mListView = (MyXListView)findViewById(R.id.display_list);
		no_information = (ImageView) findViewById(R.id.no_information);
		MyApplication.getInstance().showDialog(DisplayMainActivity.this, true, "Loading...");
		//传值判断
		Intent intent = getIntent();		
		if (intent.getStringExtra("falge").equals("CUSTOMER")) {					
			customer_name.setVisibility(View.VISIBLE);
			Member_ID = Integer.parseInt(intent.getStringExtra("Member_ID"));
			//获得客户信息
			aList = MemberManager.getInstance(this).getMembers(Member_ID);
			customer_name.setText(aList.getRealName());
			
			getJson(Member_ID);
		
		}else {
			customer_name.setVisibility(View.GONE);
			getJson(Member_ID);
		}
		
		// 添加监听事件
		addLayout.setOnClickListener(new myOnClickListener());
		backLayout.setOnClickListener(new myOnClickListener());
						
	    mListView.setPullLoadEnable(true);		
		mListView.setMyXListViewListener(this);
		
		//添加数据		
		displayAdapter = new DisplayAdapter(DisplayMainActivity.this, mList);		
		mListView.setAdapter(displayAdapter);		
	}

	private class myOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.display_back:// 返回
				finish();
				break;
			case R.id.display_add:// 新增
				mIntent = new Intent(DisplayMainActivity.this, DisplayNewAdd.class);
				if (Member_ID !=0){
					mIntent.putExtra("Member_ID", Member_ID);//客户编号
					mIntent.putExtra("MemberName", aList.getRealName());//客户名称
					}else {
						mIntent.putExtra("Member_ID", Member_ID);//客户编号
						mIntent.putExtra("MemberName", "无");//客户名称
					}
				startActivityForResult(mIntent, 0);
				break;
			default:
				break;
			}

		}

	}
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1://刷新list数据
				
				if (msg.what == HanderUtil.case1) {
	    			mList.clear();
	    			for (int i = 0; i < mList2.size(); i++) {
	    				mList.add(mList2.get(i));
					}
	    			if (mList.size()==0) {
	    				no_information.setVisibility(View.VISIBLE);
					}else {
						no_information.setVisibility(View.GONE);
					}
	    			displayAdapter.notifyDataSetChanged();
	    			MyApplication.getInstance().showDialog(DisplayMainActivity.this, false, "Loading...");
				}
				break;
				
			case 2:
				
				if (mList2.size()<=0) {
					new ClueCustomToast().showToast(DisplayMainActivity.this,
							R.drawable.toast_warn, "没有更多数据！");
				}else {
					for (int i = 0; i < mList2.size(); i++) {
	    				mList.add(mList2.get(i));
					}
	    			
	    			displayAdapter.notifyDataSetChanged();
				}
				
				break;
		  case 3:
			  MyApplication.getInstance().showDialog(DisplayMainActivity.this, false, "Loading...");
			  new ClueCustomToast().showToast(DisplayMainActivity.this,
						R.drawable.toast_warn, "未获得任何数据，请检查网络是否连接！");
			  break;

			default:
				break;
			}
		};
	};
	

	/**
	 * 下拉刷新
	 */
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mListView.stopRefresh();
				mListView.stopLoadMore();
				mListView.setRefreshTime();
			}
		}, 2000);
		pageIndex = 1;
		getJson(Member_ID);
		
	}

	/**
	 * 上拉加载
	 */
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		//onLoad();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
			if (isFinish == true) {
				isFinish = false;					
				mListView.stopRefresh();
				mListView.stopLoadMore();
				mListView.setRefreshTime();
							
//				pageIndex = pageIndex + 1;
				
				isFinish = true;
			}				
			}
		}, 2000);
//		pageSize = 	pageSize + 3;
		pageIndex += 1;
		getJson(Member_ID);
			
	}
	
	/**
	 * 查询语句
	 */
	private void getJson(int member_ID){
		int uid = Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0"));
		int department_id = preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID,0);
		ZJRequest zjRequest=new ZJRequest();
		zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 1016));
		if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
			zjRequest.setManager_ID(uid);
			zjRequest.setDepartment_ID(0);
		}else {
			zjRequest.setManager_ID(0);
			zjRequest.setDepartment_ID(department_id);
		}
		
		//假如从客户进来，则要传入客户id得到具体客户的信息
		if (Member_ID != 0 ) {
			zjRequest.setMember_ID(member_ID);
		}
		zjRequest.setKeywords("");
		zjRequest.setPageIndex(pageIndex);
		zjRequest.setPageSize(pageSize);
	    final String json = JsonUtil.toJson(zjRequest);
	    Log.e("DisplayAdapter&&json--------", json);
	    
	    new Thread(){
			public void run() {
				try {
					
					String result = DataUtil.callWebService(Methods.DISPLAY_LIST, json);
					//获取对象的Type
					Type type = new TypeToken<ZJResponse<DisplayDetails>>(){}.getType();
					ZJResponse<DisplayDetails> zjResponse = JsonUtil.fromJson(result, type);
					L.e("GetDisplayList-----------", result);
					if (result != null) {
						if (pageIndex ==1) {
							//获取对象列表
							mList2 = zjResponse.getResults();
							
								L.e("mList2-----------", mList2+"");
								mHandler.sendEmptyMessage(HanderUtil.case1);
								
							
						}else {
							mList2.clear();					
							mList2 = zjResponse.getResults();
							mHandler.sendEmptyMessage(2);
							
						}
						
					}else {
						mHandler.sendEmptyMessage(3);
					}
					
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();

	}
	
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		if (arg1 == 1) {
			//到这个页面，自动刷新
			onRefresh();
		}
		super.onActivityResult(arg0, arg1, arg2);
	}
}
