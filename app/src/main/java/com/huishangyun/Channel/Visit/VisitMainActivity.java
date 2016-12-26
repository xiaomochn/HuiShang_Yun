package com.huishangyun.Channel.Visit;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Adapter.VisitAdapter;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
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
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.VisitDetails;
import com.huishangyun.swipelistview.MyXListView.MyXListViewListener;
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
 * 拜访记录界面
 * 
 * @author 熊文娟
 * 
 */
public class VisitMainActivity extends BaseActivity implements MyXListViewListener {

	private static final String TAG = null;
	private LinearLayout backLayout;// 返回
	private LinearLayout addLayout;// 新增
	private Intent mIntent;
	private MyXListView mListView;
	private List<VisitDetails> mList = new ArrayList<VisitDetails>();
	private List<VisitDetails> mList2 = new ArrayList<VisitDetails>();
	private VisitAdapter visitAdapter;
	private int pageIndex = 1;//页码
    private int pageSize = 3;//条数
    private boolean isFinish = true;//判断是否加载完成
    private TextView customer_name;//客户名
    private int Member_ID = 0;//客户id
    Members aList = new Members();
    private String keywords = "";//关键字
    private ImageView no_information;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visit_record);
		initView();
		
	}
	/**
	 * 初始化组件
	 * 
	 */
	private void initView() {
		customer_name = (TextView) findViewById(R.id.customer_name);
		//获取资源
		// TODO Auto-generated method
		backLayout = (LinearLayout) findViewById(R.id.visit_record_back);
		addLayout = (LinearLayout) findViewById(R.id.visit_record_add);
		mListView = (MyXListView) findViewById(R.id.visit_list);
		no_information = (ImageView) findViewById(R.id.no_information);
		MyApplication.getInstance().showDialog(VisitMainActivity.this, true, "Loading...");
		//传值判断
		Intent intent = getIntent();		
		if (intent.getStringExtra("falge").equals("CUSTOMER")) {					
			customer_name.setVisibility(View.VISIBLE);
			Member_ID = Integer.parseInt(intent.getStringExtra("Member_ID"));
			aList = MemberManager.getInstance(this).getMembers(Member_ID);
			Log.e(TAG, "Member_ID:" + Member_ID);
			Log.e(TAG, "aList.getRealName():" + aList.getRealName());
			customer_name.setText(aList.getRealName());
			
			getJson(Member_ID);
		
		}else {
			customer_name.setVisibility(View.GONE);
			getJson(Member_ID);
		}
		// 添加事件
		backLayout.setOnClickListener(new myOnClickListener());
		addLayout.setOnClickListener(new myOnClickListener());
		// 添加数据
		visitAdapter = new VisitAdapter(VisitMainActivity.this,mList);
		mListView.setPullLoadEnable(true);
		mListView.setAdapter(visitAdapter);
		mListView.setMyXListViewListener(this);
		
	}

	private class myOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.visit_record_back:// 返回
				finish();
				break;
			case R.id.visit_record_add:// 新增
				mIntent = new Intent(VisitMainActivity.this, VisitNewAdd.class);
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
		zjRequest.setKeywords("");
		
		//假如从客户进来，则要传入客户id得到具体客户的信息
		if (Member_ID != 0 ) {
			zjRequest.setMember_ID(member_ID);
		}
		
		zjRequest.setPageIndex(pageIndex);
		zjRequest.setPageSize(pageSize);
	    final String json = JsonUtil.toJson(zjRequest);
	   // Log.e("GetVisitList&&json--------", json);
	    
	    new Thread(){
			public void run() {
				try {
					
					String result = DataUtil.callWebService(Methods.VISIT_LIST, json);
					//获取对象的Type
					Type type = new TypeToken<ZJResponse<VisitDetails>>(){}.getType();
					ZJResponse<VisitDetails> zjResponse = JsonUtil.fromJson(result, type);
//					L.e("GetVisitList-----------", result);
					if (result != null) {
						if (pageIndex ==1) {
							//获取对象列表
							mList2.clear();					
							mList2 = zjResponse.getResults();


							Log.e("TAGS","数据"+mList2);
//								L.e("mList2-----------", mList2+"");
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
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1://刷新list数据
				
				if (msg.what == HanderUtil.case1) {
	    			mList.clear();
	    			for (int i = 0; i < mList2.size(); i++) {
	    				mList.add(mList2.get(i));
					}
	    			L.e("mList/////:" + mList.size());
	    			if (mList.size()==0) {
	    				no_information.setVisibility(View.VISIBLE);
					}else {
						no_information.setVisibility(View.GONE);
					}
	    			visitAdapter.notifyDataSetChanged();
	    			MyApplication.getInstance().showDialog(VisitMainActivity.this, false, "Loading...");
				}
				break;
				
           case 2:
				
				if (mList2.size()<=0) {
					new ClueCustomToast().showToast(VisitMainActivity.this,
							R.drawable.toast_warn, "没有更多数据！");
				}else {
					for (int i = 0; i < mList2.size(); i++) {
	    				mList.add(mList2.get(i));
					}
	    			
	    			visitAdapter.notifyDataSetChanged();
				}
				
				break;
		  case 3:
			  new ClueCustomToast().showToast(VisitMainActivity.this,
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
									
				
				isFinish = true;
			}				
			}
		}, 2000);
		pageIndex += 1;
		getJson(Member_ID);
	}
	
	/**
	 * 图片显示
	 */
	@Override
	public void showImage(ImageView imageView, String bootpath, String imgName)
			throws IOException {
		// TODO Auto-generated method stub
		super.showImage(imageView, bootpath, imgName);
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
