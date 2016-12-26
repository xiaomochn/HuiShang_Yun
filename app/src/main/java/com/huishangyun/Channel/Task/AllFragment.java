package com.huishangyun.Channel.Task;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Adapter.TaskAdapter;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.TakeDetails;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.model.Content;
import com.huishangyun.swipelistview.MyXListView.MyXListViewListener;
import com.huishangyun.yun.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

/**
 * 全部任务列表界面
 * @author Pan
 *
 */
public class AllFragment extends Fragment implements MyXListViewListener {
	private View view;
	private MyXListView mListView;
	private List<TakeDetails> mList = new ArrayList<TakeDetails>();
	private TaskAdapter adapter;
	private int PageIndex = 1;
	private boolean isLoadMore = false;
	private ImageView No;
	private com.huishangyun.Util.webServiceHelp<TakeDetails> webServiceHelp;
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.task_alllistview, container, false);
		initView();
		return view;
	}
	
	/**
	 * 实例化组件
	 */
	private void initView() {
		mListView = (MyXListView) view.findViewById(R.id.task_all_listview);
		adapter = new TaskAdapter(getActivity(), mList);
		mListView.setPullLoadEnable(true);//设置能下拉（这两个属性是上拉加载下拉刷新的）
		mListView.setMyXListViewListener(this);
		mListView.setOnItemClickListener(mClickListener);
		mListView.setAdapter(adapter);
		mListView.startRefresh();
		PageIndex = 1;
		isLoadMore = false;
		No = (ImageView) view.findViewById(R.id.task_all_no);
		No.setVisibility(View.GONE);
		webServiceHelp = new webServiceHelp<TakeDetails>(Methods.TASK_LIST, new TypeToken<ZJResponse<TakeDetails>>(){}.getType());
		webServiceHelp.setOnServiceCallBack(onServiceCallBack);
		//new Thread(new getAllList(getJson(PageIndex, ""))).start();
		//访问数据
		webServiceHelp.start(getJson(PageIndex, ""));
		L.e("UID = " + MyApplication.preferences.getString(Constant.HUISHANGYUN_UID, "0"));
		L.e("jsonAll = " + getJson(1, ""));
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		webServiceHelp.removeOnServiceCallBack();
		super.onDestroy();
	}
	
	/**
	 * 获取Json数据
	 * @param PageIndex 页码
	 * @param Keywords 搜索关键字
	 * @return
	 */
	private String getJson(int PageIndex, String Keywords) {
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setCompany_ID(MyApplication.preferences.getInt(Content.COMPS_ID, 0));
		if (MyApplication.preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
			zjRequest.setManager_ID(Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANGYUN_UID, "0")));
		} else {
			zjRequest.setDepartment_ID(MyApplication.preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0));
		}
		zjRequest.setKeywords(Keywords);
		zjRequest.setClass_ID(0);
		zjRequest.setPageIndex(PageIndex);
		zjRequest.setPageSize(10);
		if (((TaskMainActivity)getActivity()).Member_ID != -1) {
			zjRequest.setMember_ID(((TaskMainActivity)getActivity()).Member_ID);
		}
		return JsonUtil.toJson(zjRequest);
		
	}
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		PageIndex = 1;
		isLoadMore = false;
		//new Thread(new getAllList(getJson(PageIndex, ""))).start();
		webServiceHelp.start(getJson(PageIndex, ""));
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		isLoadMore = true;
		//new Thread(new getAllList(getJson(PageIndex, ""))).start();
		webServiceHelp.start(getJson(PageIndex, ""));
	}
	
	/**
	 * 列表点击事件监听
	 */
	private OnItemClickListener mClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			L.d("点击的位置是:" + arg2);
			if (arg2 == 0) {
				return;
			}
			//跳转到任务列表界面
			Intent mIntent = new Intent(getActivity(), TaskDetailsActivity.class);
			mIntent.putExtra("ID", mList.get(arg2 - 1).getID());
			startActivityForResult(mIntent, 0);
		}
	};
	
	/**
	 * 调用刷新
	 */
	public void startRefresh() {
		if (mListView != null) {
			mListView.startRefresh2();
		}
	}
	/**
	 * 
	 */
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			mListView.setRefreshTime();
			mListView.stopLoadMore();
			mListView.stopRefresh();
			switch (msg.what) {
			case HanderUtil.case1://获取到内容
				//adapter.notifyDataSetChanged();
				List<TakeDetails> results = (List<TakeDetails>) msg.obj;
				if (results.size() == 0) {
					if (!isLoadMore) {
						mList.clear();
						adapter.notifyDataSetChanged();
						No.setVisibility(View.VISIBLE);
					} else {
						((TaskMainActivity)getActivity()).showCustomToast("没有更多数据", false);
					}
					return;
				}
				No.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);
				if (!isLoadMore) {//是否为上拉加载
					mList.clear();
					for (int i = 0; i < results.size(); i++) {
						mList.add(results.get(i));
					}
					
				} else {
					for (int i = 0; i < results.size(); i++) {
						mList.add((TakeDetails) results.get(i));
					}
				}
				//通知刷新
				PageIndex++;
				adapter.notifyDataSetChanged();
				break;
			case HanderUtil.case2://无法加载内容
				//T.showShort(getActivity(), (CharSequence) msg.obj);
				((TaskMainActivity)getActivity()).showCustomToast((String) msg.obj, false);
				break;

			default:
				break;
			}
		}
	};
	
	/**
	 * 接口返回数据监听
	 */
	private com.huishangyun.Util.webServiceHelp.OnServiceCallBack<TakeDetails> onServiceCallBack = new com.huishangyun.Util.webServiceHelp.OnServiceCallBack<TakeDetails>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<TakeDetails> zjResponse) {
			// TODO Auto-generated method stub
			if (haveCallBack && zjResponse != null) {
				if (zjResponse.getCode() == 0) {//是否有返回结果
					Message msg = new Message();
					msg.what = HanderUtil.case1;
					msg.obj = zjResponse.getResults();
					mHandler.sendMessage(msg);
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
}
