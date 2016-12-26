package com.huishangyun.Channel.Customers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Channel.Plan.HorizontalListView;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.PinyinUtil;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.model.MemberGroups;
import com.huishangyun.yun.R;

/**
 * 选择客户分组
 * @author Pan
 *
 */
public class CustomerChooseGroup extends BaseActivity implements ChooseGroupAdapter.OnItemCheked {
	private ListView mListView;//分组列表
	private SideBar sideBar;
	private TextView mTextView;
	private ChooseGroupAdapter adapter;
	private List<GroupModel> mList;
	private MemberManager memberManager;
	private int ParentID = 0;
	int count = 0;// 记录选中个数
	private HorizontalListView horizon_listview;// 横向listview
	private HListViewAdapter hListViewAdapter;
	private List<GroupModel> hListviewList = new ArrayList<GroupModel>();
	private LinearLayout submit;
	private TextView size;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_group);
		initView();
		initBackTitle("选择分组");
		showDialog("请稍后...");
		new Thread(new GetGroup(0, false)).start();
		setResult(RESULT_CANCELED);
	}
	
	/**
	 * 实例化组件
	 */
	private void initView() {
		mListView = (ListView) findViewById(R.id.customer_group_list);
		mTextView = (TextView) findViewById(R.id.choose_position);
		sideBar = (SideBar) findViewById(R.id.choose_scroller);
		horizon_listview = (HorizontalListView) findViewById(R.id.choose_horizon_listview);
		submit = (LinearLayout) findViewById(R.id.choose_sure);
		size = (TextView) findViewById(R.id.choose_queding);
		hListViewAdapter = new HListViewAdapter(CustomerChooseGroup.this, hListviewList);
		mTextView.setVisibility(View.INVISIBLE);
		mList = new ArrayList<GroupModel>();
		adapter = new ChooseGroupAdapter(this, mList, this);
		mListView.setAdapter(adapter);
		mListView.setTextFilterEnabled(true);
		sideBar.setTextView(mTextView);
		sideBar.setListView(mListView);
		horizon_listview.setAdapter(hListViewAdapter);
		memberManager = MemberManager.getInstance(CustomerChooseGroup.this);
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle data = new Bundle();
				data.putSerializable("Group_List", (Serializable) hListviewList);
				intent.putExtras(data);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		mListView.setOnItemClickListener(onItemClickListener);
	}
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			showDialog("正在加载...");
			new Thread(new GetGroup(mList.get(arg2).getID(), false)).start();
		}
		
	};
	
	/**
	 * 返回键监听
	 */
	@Override
	public void onBackPressed() {
		if (ParentID != 0) {//是否需要返回
			new Thread(new GetGroup(ParentID, true)).start();
		} else {
			finish();
		}
	};
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dismissDialog();
			switch (msg.what) {
			case HanderUtil.case1:
				adapter.notifyDataSetChanged();
				break;
				
			case HanderUtil.case2:
				showCustomToast((String) msg.obj, false);
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * 获取分组的线程
	 * @author Pan
	 *
	 */
	private class GetGroup implements Runnable {
		private int ID;
		private boolean isParent;
		public GetGroup(int ID, boolean isParent) {
			this.ID = ID;
			this.isParent = isParent;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			List<MemberGroups> list = memberManager.getGroups(ID, isParent);
			if (list == null || list.size() == 0) {//没有返回值了
				Message msg = new Message();
				msg.what = HanderUtil.case2;
				msg.obj = "没有更多了...";
				mHandler.sendMessage(msg);
				return;
			}
			List<GroupModel> adapterGroups = adapter.getSelects();
			L.e("选中项有" + adapterGroups.size());
			mList.clear();
			for (MemberGroups memberGroups : list) {//取出数据
				GroupModel groupModel = new GroupModel();
				groupModel.setID(memberGroups.getID());
				groupModel.setName(memberGroups.getName());
				int have = 0;
				for (int i = 0; i < adapterGroups.size(); i++) {
					L.e("adapterGroups:" + adapterGroups.get(i).getID());
					L.e("memberGroups" + memberGroups.getID());
					if (adapterGroups.get(i).getID().equals(memberGroups.getID())) {
						have = 1;
						break;
					} 
				}
				if (have == 0) {
					L.e("没相同的");
					groupModel.setSelect(false);
				} else {
					L.e("有相同的");
					groupModel.setSelect(true);
				}
				String pinyin = PinyinUtil.getPingYin(memberGroups.getName());
				groupModel.setPinyin(pinyin);
				groupModel.setTitle(pinyin.charAt(0) + "");
				groupModel.setParentID(memberGroups.getParentID());
				mList.add(groupModel);
			}
			ParentID = mList.get(0).getParentID();
			Collections.sort(mList, new GroupComparator());
			mHandler.sendEmptyMessage(HanderUtil.case1);
		}
		
	}

	@Override
	public void onItemCheked(List<GroupModel> models) {
		// TODO Auto-generated method stub
		hListviewList.clear();
		for (GroupModel groupModel : models) {
			hListviewList.add(groupModel);
		}
		hListViewAdapter.notifyDataSetChanged();
		size.setText("确定( " + hListviewList.size() + " )");
	}
}
