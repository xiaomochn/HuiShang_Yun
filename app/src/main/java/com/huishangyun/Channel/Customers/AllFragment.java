package com.huishangyun.Channel.Customers;

import java.util.ArrayList;
import java.util.List;

import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Members;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.swipelistview.MyXListView.MyXListViewListener;
import com.huishangyun.yun.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 所有的客户
 * @author Pan
 *
 */
public class AllFragment extends Fragment implements MyXListViewListener, OnItemClickListener{
	private View mView;
	private MemberManager memberManager;
	private MyXListView myXListView;
	private CustomerListAdapter adapter;
	private List<Members> mList;
	private int PageIndex = 1;
	private boolean isLoadMore = false;
	private ImageView No;
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.fragment_customer_all, container, false);
		initView();
		//获取客户列表
		myXListView.startRefresh();
		new Thread(new getMembers(PageIndex)).start();
		return mView;
	}
	
	/**
	 * 实例化组件
	 */
	private void initView() {
		memberManager = MemberManager.getInstance(getActivity());
		myXListView = (MyXListView) mView.findViewById(R.id.customer_all_listview);
		myXListView.setPullLoadEnable(true);//设置能下拉（这两个属性是上拉加载下拉刷新的）
		myXListView.setMyXListViewListener(this);
		mList = new ArrayList<Members>();
		adapter = new CustomerListAdapter(getActivity(), mList);
		myXListView.setAdapter(adapter);
		myXListView.setOnItemClickListener(this);
		No = (ImageView) mView.findViewById(R.id.customer_all_no);
		No.setVisibility(View.GONE);
		PageIndex = 1;
		isLoadMore = false;
	}
	
	public void startRefresh() {
		if (myXListView != null) {
			mList.clear();
			adapter.notifyDataSetChanged();
			myXListView.startRefresh2();
		}
	}
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			myXListView.stopLoadMore();
			myXListView.stopRefresh();
			myXListView.setRefreshTime();
			switch (msg.what) {
			case HanderUtil.case1:
				List<Members> list = (List<Members>) msg.obj;
				if (!isLoadMore) {
					mList.clear();
					isLoadMore = true;
				} 
				for (Members members : list) {
					mList.add(members);
				}
				if (mList.size() == 0) {
					No.setVisibility(View.VISIBLE);
				} else {
					No.setVisibility(View.GONE);
				}
				PageIndex++;
				adapter.notifyDataSetChanged();
				break;
			case HanderUtil.case2:
				((CustomerMainActivity)getActivity()).showCustomToast((String) msg.obj, false);
				break;
			case HanderUtil.case3:
				mList.clear();
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * 获取客户列表
	 * @author Pan
	 *
	 */
	private class getMembers implements Runnable {
		private int pageIndex;
		public getMembers(int pageIndex) {
			this.pageIndex = pageIndex;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			List<Members> list = new ArrayList<Members>();
			//更具权限来判断加载哪些数据
			if (MyApplication.preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
				list = memberManager.getMembersForMID(Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANGYUN_UID, "0")), pageIndex);
			} else {
				list = memberManager.getMembersForDID(MyApplication.preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0), pageIndex);
			}
			
			if (list == null || list.size() == 0) {
				if (!isLoadMore) {
					mHandler.sendEmptyMessage(HanderUtil.case3);
				} else {
					Message msg = new Message();
					msg.what = HanderUtil.case2;
					msg.obj = "没有更多数据";
					mHandler.sendMessage(msg);
				}
				
				return;
			}
			Log.i("--------------", "客户list:" + list.toString());
			Message msg = new Message();
			msg.what = HanderUtil.case1;
			msg.obj = list;
			mHandler.sendMessage(msg);
			
		}
		
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		PageIndex = 1;
		isLoadMore = false;
		new Thread(new getMembers(PageIndex)).start();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		new Thread(new getMembers(PageIndex)).start();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (arg2 == 0) {
			return;
		}
		Intent mIntent = new Intent(getActivity(), CustomerInfo.class);
		mIntent.putExtra("flage", "xiangqing");
		mIntent.putExtra("MemberID", mList.get(arg2 - 1).getID());
		startActivityForResult(mIntent, 0);
	}
}
