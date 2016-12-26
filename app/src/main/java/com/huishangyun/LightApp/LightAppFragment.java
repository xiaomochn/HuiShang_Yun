package com.huishangyun.LightApp;

import java.util.ArrayList;
import java.util.List;

import com.gotye.api.GotyeUser;
import com.huishangyun.Activity.Chat;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.FileTools;
import com.huishangyun.Util.L;
import com.huishangyun.Util.Service;
import com.huishangyun.Util.StringUtil;
import com.huishangyun.View.RoundAngleImageView;
import com.huishangyun.manager.ServiceManager;
import com.huishangyun.model.Constant;
import com.huishangyun.yun.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LightAppFragment extends Fragment{
	private View mView;
	private ListView appListView;
	private AppAdapter mAdapter;
	private List<Service> mList;
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.fragment_lightapp, container, false);
		initView();
		return mView;
	}
	
	/**
	 * 实例化组件
	 */
	private void initView() {
		appListView = (ListView) mView.findViewById(R.id.light_applist);
		mList = new ArrayList<Service>();
		mAdapter = new AppAdapter(getActivity(), mList);
		appListView.setAdapter(mAdapter);
		appListView.setOnItemClickListener(onItemClickListener);
		getLightApp();
	}
	
	/**
	 * 获取轻应用
	 */
	public void getLightApp() {
		
		new Thread(){
			public void run() {
				List<Service> mList = ServiceManager.getInstance(getActivity()).getServices();
				if (mList != null) {
					Message msg = new Message();
					msg.obj = mList;
					msg.what = HanderUtil.case1;
					mHandler.sendMessage(msg);
				}
			};
		}.start();
	}
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			createChat(mList.get(arg2),arg2);
			
		}
	};
	
	protected void createChat(Service service,int posion) {
		L.d(getClass(),"进入聊天");
		Intent intent = new Intent(getActivity(), Chat.class);
		
		intent.putExtra("JID",  StringUtil.getJidByName(service.getLoginName()));
		intent.putExtra("chat_name", service.getName());
		intent.putExtra("name", service.getName());
		intent.putExtra("type", 1);
		intent.putExtra("messtype", 0);
		intent.putExtra("user", new GotyeUser(service.getLoginName()));
		startActivity(intent);
	}
	
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				mList.clear();
				for (Service managers : (List<Service>)msg.obj) {
					mList.add(managers);
				}
				mAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		};
	};
	
	private class AppAdapter extends BaseAdapter {
		private List<Service> managers;
		private Context mContext;
		private LayoutInflater mInflater;
		private AppAdapter (Context mContext, List<Service> managers) {
			this.managers = managers;
			this.mContext = mContext;
			this.mInflater = LayoutInflater.from(mContext);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return managers.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return managers.get(position);
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
				convertView = mInflater.inflate(R.layout.app_list_item, null);
				mHolder.img = (ImageView) convertView.findViewById(R.id.app_icon);
				mHolder.name = (TextView) convertView.findViewById(R.id.app_name);
				mHolder.size = (TextView) convertView.findViewById(R.id.app_unreadmsg);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			mHolder.name.setText(managers.get(position).getName());
			/*FileTools.decodePhoto("http://img.huishangyun.com/UploadFile/huishang/" +
					MyApplication.getInstance().getCompanyID() + "/Photo/" + managers.get(position).getPhoto(), mHolder.img );*/
			ImageLoader.getInstance().displayImage(Constant.pathurl +
					MyApplication.getInstance().getCompanyID() + "/Photo/" + managers.get(position).getPhoto(), mHolder.img, MyApplication.getInstance().getGroupOptions());
			return convertView;
		}
		
	}
	
	static class ViewHolder {
		private ImageView img;
		private TextView name;
		private TextView size;
	}
}
