package com.huishangyun.Fragment;

import java.util.ArrayList;
import java.util.List;


import com.gotye.api.GotyeGroup;
import com.huishangyun.Activity.Chat;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.manager.GroupManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.IMGroup;
import com.huishangyun.Util.FileTools;
import com.huishangyun.yun.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 群组列表
 * @author Administrator
 *
 */
public class GroupFragment extends Fragment{
	private View mView;
	private ListView mListView;
	private GroupAdapter adapter;
	private List<IMGroup> mList = new ArrayList<IMGroup>();
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.contact_pager3, container, false);
		initView();
		getGroup();
		return mView;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private void initView() {
		mListView = (ListView) mView.findViewById(R.id.listview_contact_record);
		adapter = new GroupAdapter(mList);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),Chat.class);
				intent.putExtra("JID", mList.get(arg2).getOpenID());
				intent.putExtra("type", 1);
				intent.putExtra("name", mList.get(arg2).getName());
				intent.putExtra("messtype", 2);
				intent.putExtra("chat_name", mList.get(arg2).getName());
				intent.putExtra("Sign", "");
				GotyeGroup gotyeUser = new GotyeGroup(Integer.parseInt(mList.get(arg2).getOpenID()));
				intent.putExtra("group", gotyeUser);
				startActivity(intent);
			}
		});
	}
	
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				mList.clear();
				List<IMGroup> roomLists = (List<IMGroup>) msg.obj;
				for (IMGroup roomList : roomLists) {
					//mUserChat = new MultiUserChat(XmppConnectionManager.getInstance().getConnection(), arg1);
					mList.add(roomList);
				}
				adapter.notifyDataSetChanged();
				break;
				
			case HanderUtil.case2:
				ClueCustomToast.showToast(getActivity(), R.drawable.toast_warn, (String) msg.obj);
				break;

			default:
				break;
			}
		};
	};
	
	/**
	 * 获取群组
	 */
	public void getGroup() {
		if (mListView != null) {
			List<IMGroup> roomLists = GroupManager.getInstance(getActivity()).getImGroups();
			mList.clear();
			for (IMGroup roomList : roomLists) {
				mList.add(roomList);
			}
			adapter.notifyDataSetChanged();
		}
	}
	
	
	
	/**
	 * 分组详情
	 * @author Pan
	 *
	 */
	private class GroupAdapter extends BaseAdapter{
		private List<IMGroup> mList;
		public GroupAdapter(List<IMGroup> mList) {
			this.mList = mList;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder mHolder;
			if (convertView == null) {
				mHolder = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_group, null);
				mHolder.img = (ImageView) convertView.findViewById(R.id.group_icon);
				mHolder.name = (TextView) convertView.findViewById(R.id.group_name);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			mHolder.name.setText(mList.get(position).getName());
			/*FileTools.decodePhoto("http://img.huishangyun.com/UploadFile/huishang/" +
					MyApplication.getInstance().getCompanyID() + "/Photo/" + mList.get(position).getPhoto(), mHolder.img);*/
			ImageLoader.getInstance().displayImage(Constant.pathurl +
					MyApplication.getInstance().getCompanyID() + "/Photo/" + mList.get(position).getPhoto(), mHolder.img, MyApplication.getInstance().getGroupOptions());
			return convertView;
		}
		
	}
	
	
	static class ViewHolder {
		public ImageView img;
		public TextView name;
	}
}
