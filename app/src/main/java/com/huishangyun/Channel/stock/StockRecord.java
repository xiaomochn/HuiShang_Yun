package com.huishangyun.Channel.stock;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Adapter.StockRecordAdapter;
import com.huishangyun.model.Members;
import com.huishangyun.model.Methods;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.model.Content;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.swipelistview.MyXListView.MyXListViewListener;
import com.huishangyun.yun.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 库存记录界面
 * 
 * @author 熊文娟
 * 
 */

public class StockRecord extends BaseActivity implements MyXListViewListener {

	protected static final String TAG = null;
	private LinearLayout backLayout = null;//返回
	private LinearLayout addLayout = null;// 新增
	private MyXListView mListView=null;
	private Intent mIntent = null;
	private StockRecordAdapter adapter=null;
	private int Member_ID;//库存详情id
	private TextView txt_client;//客户名称
	List<StockList> ItemLists = new ArrayList<StockList>();
	List<StockList> lists = new ArrayList<StockList>();
	private int PagerIndex = 1;
	private int PagerSzie = 3;
	Members aList = new Members();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_record);
		
		initView();
		// 添加数据
		adapter = new StockRecordAdapter(this,lists);
		mListView.setPullLoadEnable(true);
		mListView.setAdapter(adapter);
		mListView.setMyXListViewListener(this);
	}

	
	
	private void getNetData(final int Member_ID, final int PagerSize, final int PagerIndex) {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				android.util.Log.e(TAG,"提交数据：" + getJson(Member_ID, PagerSize, PagerIndex));
				String result = DataUtil.callWebService(
						Methods.STOCK_DETAIL,
						getJson(Member_ID, PagerSize, PagerIndex));
				
				// 先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					
					Log.e(TAG, "------>:" + result);
//					// 获取对象的Type
					// 获取对象的Type
					Type type = new TypeToken<ZJResponse<StockList>>() {
					}.getType();
					ZJResponse<StockList> zjResponse = JsonUtil.fromJson(result,
							type);
					if (PagerIndex == 1) {
						ItemLists.clear();
						ItemLists = zjResponse.getResults();
						Log.e(TAG, "***********"+ ItemLists.size());
					   mHandler.sendEmptyMessage(0);
					}else {
						ItemLists.clear();
						ItemLists = zjResponse.getResults();
						Log.e(TAG, "***********"+ ItemLists.size());
					   mHandler.sendEmptyMessage(2);
					}
				
				} else {
					mHandler.sendEmptyMessage(3);

				}

			}
		}.start();
	}

	/**
	 * 设置json对象
	 * 
	 * @param
	 * @return
	 */
	private String getJson(int Member_ID, int PagerSize, int PagerIndex ) {
		int Company_ID = preferences.getInt(Content.COMPS_ID, 1016);
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setCompany_ID(Company_ID);
		zjRequest.setMember_ID(Member_ID);
		zjRequest.setPageSize(PagerSize);
		zjRequest.setPageIndex(PagerIndex);
		return JsonUtil.toJson(zjRequest);

	}
	
	/**
	 * 初始化组件
	 * 
	 */
	private void initView() {
		// 获取资源
		backLayout = (LinearLayout) findViewById(R.id.root_back);
		addLayout = (LinearLayout) findViewById(R.id.root_stock_add);
		mListView = (MyXListView) findViewById(R.id.stock_list);
		txt_client = (TextView) findViewById(R.id.txt_client);
		
		Intent intent = getIntent();
		if (intent.getStringExtra("falge").equals("CUSTOMER")) {					
			
			Member_ID = Integer.parseInt(intent.getStringExtra("Member_ID"));
			aList = MemberManager.getInstance(this).getMembers(Member_ID);
			try {
				Log.e(TAG, "Member_ID:" + Member_ID);
				Log.e(TAG, "aList.getRealName():" + aList.getRealName());
				txt_client.setText(aList.getRealName());
				getNetData(Member_ID, PagerSzie, PagerIndex);
			} catch (Exception e) {
				// TODO: handle exception
			    ClueCustomToast.showToast(this, R.drawable.toast_defeat, "该客户不存在或已做修改！");
			    finish();
			}
			
		
		}else {	
			Member_ID = intent.getIntExtra("Member_ID", -1);
			getNetData(Member_ID, PagerSzie, PagerIndex);
		}
		
		getNetData(Member_ID, PagerSzie, PagerIndex);
		
		
		// 添加事件
		backLayout.setOnClickListener(new myOnClickListener());
		addLayout.setOnClickListener(new myOnClickListener());
	}

	private class myOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.root_back:// 返回
				finish();
				break;
			case R.id.root_stock_add:// 新增
				mIntent = new Intent(StockRecord.this, StockReport.class);
				startActivity(mIntent);
				break;

			default:
				break;
			}

		}

	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (ItemLists.size()>0) {
					txt_client.setText(ItemLists.get(0).getMember_Name());
					lists.clear();
					for (int i = 0; i < ItemLists.size(); i++) {
						lists.add(ItemLists.get(i));
					}
					adapter.notifyDataSetInvalidated();
				}
				
				break;
				
           case 2:
				
				if (ItemLists.size()<=0) {
					new ClueCustomToast().showToast(StockRecord.this,
							R.drawable.toast_warn, "没有更多数据！");
				}else {
					if (ItemLists.size()>0) {
						txt_client.setText(ItemLists.get(0).getMember_Name());
						for (int i = 0; i < ItemLists.size(); i++) {
							lists.add(ItemLists.get(i));
						}
						adapter.notifyDataSetInvalidated();
					}
				}
				
				break;
		  case 3:
			  new ClueCustomToast().showToast(StockRecord.this,
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
		PagerIndex =1;
		getNetData(Member_ID, 3, 1);
	}

	/**
	 * 上拉加载
	 */
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		//onLoad();
//		PagerSzie = PagerSzie + 3;
		PagerIndex +=1;
		getNetData(Member_ID, PagerSzie, PagerIndex);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mListView.stopRefresh();
				mListView.stopLoadMore();
				mListView.setRefreshTime();
				
			}
		}, 2000);
	}
}
