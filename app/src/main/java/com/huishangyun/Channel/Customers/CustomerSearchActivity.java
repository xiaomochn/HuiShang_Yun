package com.huishangyun.Channel.Customers;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Members;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.swipelistview.MyXListView.MyXListViewListener;
import com.huishangyun.yun.R;

public class CustomerSearchActivity extends BaseActivity implements MyXListViewListener{
	private RelativeLayout back;// 返回
	private RelativeLayout search;// 搜索
	private EditText ed_text;// 编辑框
	private RelativeLayout transparent_background;// 半透明界面
	private LinearLayout search_hint;// 搜索提示
	private LinearLayout listview_backgroud;// listview层
	private MyXListView listview;
	private List<Members> mDetails;
	private CustomerListAdapter adapter;
	private boolean isLoadMore;
	private int PageIndex = 1;
	private String Keywords = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clue_search);
		initView();
		mDetails = new ArrayList<Members>();
		adapter = new CustomerListAdapter(CustomerSearchActivity.this, mDetails);
		listview.setAdapter(adapter);
		listview.setPullLoadEnable(true);
		listview.setMyXListViewListener(this);
		listview.setOnItemClickListener(mClickListener);
		isLoadMore = false;
	}
	
	/**
	 * 实例化控件
	 */
	private void initView() {
		back = (RelativeLayout) findViewById(R.id.back);
		search = (RelativeLayout) findViewById(R.id.search);
		ed_text = (EditText) findViewById(R.id.ed_text);
		// 将提示语改成：请输入主题
		ed_text.setHint("请输入关键字");
		transparent_background = (RelativeLayout) findViewById(R.id.transparent_background);
		search_hint = (LinearLayout) findViewById(R.id.search_hint);
		listview_backgroud = (LinearLayout) findViewById(R.id.listview_backgroud);
		listview = (MyXListView) findViewById(R.id.search_listview);
		back.setOnClickListener(mListener);
		transparent_background.setOnClickListener(mListener);
		
		// 强制弹出软键盘，延时目的是为了避免控件未加载完弹出软键盘失败
		Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// 让软键盘自动弹出
				ed_text.setFocusable(true);
				ed_text.setFocusableInTouchMode(true);
				ed_text.requestFocus();
				InputMethodManager inputManager = (InputMethodManager) ed_text
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(ed_text, 0);
			}
		}, 200);
		
		ed_text.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				Keywords = s.toString().trim();
				if (s.toString().trim().length() > 0) {
					// //输入有内容，先清除列表
					// list.clear();
					// adapter.notifyDataSetChanged();
					// 隐藏半透明布局,显示listview
					mDetails.clear();
					adapter.notifyDataSetChanged();
					transparent_background.setVisibility(View.GONE);
					listview_backgroud.setVisibility(View.VISIBLE);
					search_hint.setVisibility(View.GONE);
					
					// 搜索
					isLoadMore = true;
					PageIndex = 1;
					new Thread(new searchTask(Keywords,PageIndex)).start();

				} else {
					// 显示半透明布局
					transparent_background.setVisibility(View.VISIBLE);
					listview_backgroud.setVisibility(View.GONE);
					search_hint.setVisibility(View.GONE);
					mDetails.clear();
					PageIndex = 1;
					adapter.notifyDataSetChanged();
				}
			}
		});
	}
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HanderUtil.case1://获取到内容
				search_hint.setVisibility(View.GONE);
				transparent_background.setVisibility(View.GONE);
				listview_backgroud.setVisibility(View.VISIBLE);
				adapter.notifyDataSetChanged();
				break;
			case HanderUtil.case2://无法加载内容
				search_hint.setVisibility(View.VISIBLE);
				transparent_background.setVisibility(View.GONE);
				listview_backgroud.setVisibility(View.GONE);
				//T.showShort(CustomerSearchActivity.this, (CharSequence) msg.obj);
				break;

			default:
				break;
			}
			listview.setRefreshTime();
			listview.stopLoadMore();
			listview.stopRefresh();
		};
	};
	
	/**
	 * 搜索任务线程
	 * @author Pan
	 *
	 */
	private class searchTask implements Runnable {
		
		private String keywords;
		private int pageIndex;
		public searchTask(String keywords, int pageIndex) {
			this.keywords = keywords;
			this.pageIndex = pageIndex;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			List<Members> list = new ArrayList<Members>();

				if (MyApplication.preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
					try {
						list = MemberManager.getInstance(CustomerSearchActivity.this).searchMemberForMID(keywords,
                                    Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANGYUN_UID, "0")), pageIndex);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				} else {
                    list = MemberManager.getInstance(CustomerSearchActivity.this).searchMemberForDID(keywords,
                                MyApplication.preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0), pageIndex);
                }

			if (list == null || list.size() == 0) {
				Message msg = new Message();
				msg.obj = "没有更多了";
				msg.what = HanderUtil.case2;
				mHandler.sendMessage(msg);
				return;
			}
			if (!isLoadMore) {//是否为上拉加载
				mDetails.clear();
			}
			for (Members members : list) {
				mDetails.add(members);
			}
			//通知刷新
			PageIndex++;
			mHandler.sendEmptyMessage(HanderUtil.case1);
		}
		
	}
	
	/**
	 * 点击事件监听
	 */
	private OnClickListener mListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.back:
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);				 
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				finish();
				break;
			case R.id.ed_text:

				break;
			case R.id.transparent_background:
				finish();
				break;
			default:
				break;

			}
		}
	};
	
	/**
	 * 列表点击事件
	 */
	private OnItemClickListener mClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if (arg2 == 0) {
				return;
			}
			Intent mIntent = new Intent(CustomerSearchActivity.this, CustomerInfo.class);
			mIntent.putExtra("flage", "xiangqing");
			mIntent.putExtra("MemberID", mDetails.get(arg2 - 1).getID());
			startActivity(mIntent);
		}
	};

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		PageIndex = 1;
		isLoadMore = false;
		new Thread(new searchTask(Keywords, PageIndex)).start();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		isLoadMore = true;
		new Thread(new searchTask(Keywords, PageIndex)).start();
	}
}
