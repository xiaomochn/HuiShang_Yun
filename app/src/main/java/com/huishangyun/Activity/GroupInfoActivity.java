package com.huishangyun.Activity;

import java.util.ArrayList;
import java.util.List;

import com.huishangyun.Util.DensityUtils;
import com.huishangyun.View.*;
import com.huishangyun.View.MyGridView;
import com.huishangyun.manager.DepartmentManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.GroupUser;
import com.huishangyun.model.Managers;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.FileTools;
import com.huishangyun.manager.GroupManager;
import com.huishangyun.yun.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GroupInfoActivity extends BaseMainActivity{
	private GridView managerGridView;
	private RelativeLayout lookHistory;
	private TextView groupName;
	private TextView managerIndex;
	private List<Managers> managerList = new ArrayList<Managers>();
	private List<GroupUser> users =  new ArrayList<GroupUser>();
	private Integer ID;
	private MyAdapter myAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_info);
		initView();
		initBackTitle("群资料");
	}
	
	/**
	 * 实例化组件
	 */
	private void initView() {
		managerGridView = (GridView) findViewById(R.id.group_managers);
		//LinearLayout.LayoutParams linearParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtils.dip2px(context,350));
	//	managerGridView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
		lookHistory = (RelativeLayout) findViewById(R.id.group_history);
		groupName = (TextView) findViewById(R.id.group_name);
		managerIndex = (TextView) findViewById(R.id.group_index);
		groupName.setText(getIntent().getStringExtra("Name") + "");
		lookHistory.setOnClickListener(onClickListener);
		ID = GroupManager.getInstance(GroupInfoActivity.this).getGroupID(getIntent().getStringExtra("JID"));
		if (ID == null) {
			showCustomToast("请更新基础数据", false);
			return;
		}
		users = GroupManager.getInstance(GroupInfoActivity.this).getGroupUsers(ID + "");
		for (GroupUser groupUser : users) {
			Managers managers;
			managers = DepartmentManager.getInstance(this).getManagerInfo(groupUser.getUser_ID());
			if (managers != null) {
				managerList.add(managers);
			}
		}
		managerIndex.setText(managerList.size() + "人");
		myAdapter = new MyAdapter(managerList);
		managerGridView.setAdapter(myAdapter);
		managerGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GroupInfoActivity.this, ManagerInfo.class);
				intent.putExtra("JID", managerList.get(arg2).getOFUserName());
				intent.putExtra("Name", managerList.get(arg2).getRealName());
				startActivity(intent);
			}
		});
	}
	
	/**
	 * 点击事件监听
	 */
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.group_history:
				Intent intent2 = new Intent(GroupInfoActivity.this, CharHistory.class);
				intent2.putExtra("to", getIntent().getStringExtra("JID"));
				intent2.putExtra("name", getIntent().getStringExtra("Name"));
				intent2.putExtra("type", 0);
				intent2.putExtra("isGroup", true);
				startActivity(intent2);
				break;

			default:
				break;
			}
		}
	};
	
	public class MyAdapter extends BaseAdapter {
		
		public List<Managers> managers;
		
		public MyAdapter(List<Managers> managers) {
			this.managers = managers;
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
				convertView = LayoutInflater.from(GroupInfoActivity.this).inflate(R.layout.group_user_item, null);
				mHolder.userImg = (ImageView) convertView.findViewById(R.id.user_img);
				mHolder.userName = (TextView) convertView.findViewById(R.id.user_name);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			mHolder.userName.setText(managers.get(position).getRealName());
			mHolder.userImg.setImageResource(R.drawable.contact_person);
			/*FileTools.decodePhoto("http://img.huishangyun.com/UploadFile/huishang/" +
					MyApplication.getInstance().getCompanyID() + "/Photo/" + managers.get(position).getPhoto(), mHolder.userImg);*/
			ImageLoader.getInstance().displayImage(Constant.pathurl+
					MyApplication.getInstance().getCompanyID() + "/Photo/" + managers.get(position).getPhoto(), mHolder.userImg, MyApplication.getInstance().getOptions());
			return convertView;
		}
		
	}
	
	static class ViewHolder {
		public TextView userName;
		public ImageView userImg;
	}
	
	
}
