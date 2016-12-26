package com.huishangyun.Fragment;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeChatTarget;
import com.gotye.api.GotyeChatTargetType;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeMessageType;
import com.gotye.api.GotyeUser;
import com.huishangyun.Activity.Chat;
import com.huishangyun.Adapter.RecentChartAdapter;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.L;
import com.huishangyun.manager.DepartmentManager;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.model.Constant;
import com.huishangyun.swipemenulistview.SwipeMenu;
import com.huishangyun.swipemenulistview.SwipeMenuCreator;
import com.huishangyun.swipemenulistview.SwipeMenuItem;
import com.huishangyun.swipemenulistview.SwipeMenuListView;
import com.huishangyun.yun.R;

import java.util.ArrayList;
import java.util.List;

public class MsgFragment extends BaseFragment {

	private SwipeMenuListView listView;
	private Activity mActivity=getActivity();
	private RecentChartAdapter noticeAdapter;
	private LinearLayout nouser;
	//private List<ChartHisBean> inviteNotices = new ArrayList<ChartHisBean>();
	private List<GotyeChatTarget> mChatTargets = new ArrayList<GotyeChatTarget>();

	private GotyeAPI api = GotyeAPI.getInstance();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_msg, container,false);
		initview(view);
		refreshList();
		return view;
	}
	
	/**
	 * 对list进行时间排序
	 */
	@SuppressWarnings("unchecked")
	private void SortList(){
		/*ComparatorNoticeUtil cUtil = new ComparatorNoticeUtil();
		Collections.sort(inviteNotices, cUtil);*/
	}

	/**
	 * 实例化组件
	 * 
	 * @param view
	 */
	private void initview(View view) {
		listView = (SwipeMenuListView)view.findViewById(R.id.listview_main);
		nouser = (LinearLayout)view.findViewById(R.id.fragment_msg_nouser);
		/*inviteNotices = MessageManager.getInstance(getActivity())
				.getRecentContactsWithLastMsg();*/
		SortList();
		noticeAdapter = new RecentChartAdapter(getActivity(), mChatTargets);
		listView.setAdapter(noticeAdapter);
//		listView.setAdapter(noticeAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				L.d(getClass(), "点击lstview");
				NotificationManager nm = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
				nm.cancel(0);
				createChat((GotyeChatTarget) v.findViewById(R.id.recent_list_item_msg).getTag(),arg2,
						((TextView)v.findViewById(R.id.recent_list_item_name)).getText());
			}
		});

		// step 1. create a MenuCreator左侧滑删除
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				// Create different menus depending on the view type
				switch (menu.getViewType()) {
					case 0:
						createMenu1(menu);
						break;
				}
			}
			private void createMenu1(SwipeMenu menu) {
				SwipeMenuItem item1 = new SwipeMenuItem(
						mActivity);
				item1.setBackground(new ColorDrawable(getResources().getColor(R.color.h_red)));
				item1.setWidth(200);
				item1.setTitle("删除");
				item1.setTitleSize(16);
				item1.setTitleColor(getResources().getColor(R.color.white));
				menu.addMenuItem(item1);
			}
		};
		// set creator
		listView.setMenuCreator(creator);
		//删除按钮点击事件
		listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				GotyeChatTarget gotyeChatTarget = mChatTargets.get(position);
				api.deleteSession(gotyeChatTarget, false);
				refreshList();//刷新消息列表
				return false;
			}
		});
	}
	/**
	 * 刷新列表
	 * @param inviteNotices
	 */
	/*public void refreshList(List<ChartHisBean> inviteNotices){
		this.inviteNotices.clear();
		for (ChartHisBean chartHisBean : inviteNotices) {
			this.inviteNotices.add(chartHisBean);
		}
		SortList();
		
		handler.sendEmptyMessage(HanderUtil.case2);
	}*/
	
	/**
	 * 刷新消息列表
	 */
	public void refreshList() {

		List<GotyeChatTarget> sessions = MyApplication.getInstance().getGotyeAPI().getSessionList();

		mChatTargets.clear();
		if (sessions != null) { //查看历史消息是否为空
			L.e("sessions.size = " + sessions.size());
			for (GotyeChatTarget gotyeChatTarget : sessions) { //过滤掉其他类型的数据
				if (MyApplication.getInstance().getGotyeAPI().getLastMessage(gotyeChatTarget).getType() == GotyeMessageType.GotyeMessageTypeText) {
					String senderID=MyApplication.getInstance().getGotyeAPI().getLastMessage(gotyeChatTarget).getSender().getName();
					if (senderID.indexOf("0_n_") == -1) {
						mChatTargets.add(gotyeChatTarget);
					}
				}
			}
		}

		//GotyeChatTarget gotyeChatTarget = new GotyeChatTarget();
		GotyeUser gotyeUser = new GotyeUser(Constant.HUISHANG_SYSTEM_ID);
		//隐藏系统命令会话
		MyApplication.getInstance().getGotyeAPI().activeSession(gotyeUser);
		MyApplication.getInstance().getGotyeAPI().deactiveSession(gotyeUser);

		//GotyeChatTarget gotyeChatTarget = new GotyeChatTarget();
		GotyeUser gotyeUser2 = new GotyeUser(Constant.HUISHANG_ONLOOK_ID);
		//隐藏系统命令会话
		MyApplication.getInstance().getGotyeAPI().activeSession(gotyeUser2);
		MyApplication.getInstance().getGotyeAPI().deactiveSession(gotyeUser2);

		GotyeUser gotyeUser3 = new GotyeUser(Constant.HUISHANG_ORDER_ID);
		//隐藏系统命令会话
		MyApplication.getInstance().getGotyeAPI().activeSession(gotyeUser3);
		MyApplication.getInstance().getGotyeAPI().deactiveSession(gotyeUser3);

		//日报设置为已读
		GotyeUser gotyeUser4 = new GotyeUser(Constant.HUISHANG_WorkLog_ID);
		//隐藏系统命令会话
		MyApplication.getInstance().getGotyeAPI().activeSession(gotyeUser4);
		MyApplication.getInstance().getGotyeAPI().deactiveSession(gotyeUser4);

		noticeAdapter.notifyDataSetChanged();
		if (mChatTargets.size() == 0) {
			listView.setVisibility(View.GONE);
			nouser.setVisibility(View.VISIBLE);
		}else {
			listView.setVisibility(View.VISIBLE);
			nouser.setVisibility(View.GONE);
		}
		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		refreshList();
		super.onResume();
	}
	
	/**
	 * 创建一个聊天
	 * 
	 * @paramuser
	 */
	protected void createChat(GotyeChatTarget chatTarget, int posion, CharSequence charSequence) {
		L.d(getClass(),"进入聊天");
		Intent intent = new Intent(getActivity(), Chat.class);
		int type = 1;
		String name;
		String JID;
		int messstype = 0;
		L.e("chatTarget = " + chatTarget);
		if (chatTarget.getType() == GotyeChatTargetType.GotyeChatTargetTypeUser) { //单聊
			JID = chatTarget.getName();
			name = DepartmentManager.getInstance(getActivity()).getManager(chatTarget.getName());
			if (name == null) {
				type = 2;
				name = MemberManager.getInstance(getActivity()).getMember(chatTarget.getName());
				if (name == null) {
					name = chatTarget.getName();
				}
			}
		} else { //群聊  requestGroupInfo  getGroupDetail
			//GotyeGroup group = MyApplication.getInstance().getGotyeAPI().requestGroupInfo(chatTarget.getId(), false);//之前的
			//GotyeGroup group=new GotyeGroup();//暂时这么写
			GotyeGroup group = MyApplication.getInstance().getGotyeAPI().getGroupDetail(chatTarget, false);

			messstype = 1;
			if (group != null) {
				if (TextUtils.isEmpty(group.getGroupName())) {
					//holderx.newTitle.setText("群：" + group.getId());
					name = group.getId() + "";
				} else {
					//holderx.newTitle.setText("群：" + group.getGroupName());
					name = group.getGroupName();
				}
				JID = group.getId() + "";
				
			} else {
				//holderx.newTitle.setText("群：" + notice.getId());
				name = chatTarget.getId() + "";
				JID = chatTarget.getId() + "";
			}
		}
		
		/*GotyeAPI.getInstance().markSingleMessageAsRead(chatTarget);*/
		
		if (chatTarget.getType() == GotyeChatTargetType.GotyeChatTargetTypeUser) {
			intent.putExtra("user", (GotyeUser) chatTarget);
			// updateList();
		} else if (chatTarget.getType() == GotyeChatTargetType.GotyeChatTargetTypeGroup) {
			intent.putExtra("group", (GotyeGroup) chatTarget);
		}
		refreshList();
		intent.putExtra("JID", JID);
		intent.putExtra("chat_name", charSequence);
		intent.putExtra("name", name);
		intent.putExtra("type", type);
		intent.putExtra("messtype", messstype);
		startActivity(intent);

	}

}
