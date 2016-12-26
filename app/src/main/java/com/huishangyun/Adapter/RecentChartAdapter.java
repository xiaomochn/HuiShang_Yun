package com.huishangyun.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.gotye.api.GotyeChatTarget;
import com.gotye.api.GotyeChatTargetType;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeMessage;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.FaceUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ServiceUtil;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.manager.DepartmentManager;
import com.huishangyun.manager.GroupManager;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.manager.ServiceManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.MessageData;
import com.huishangyun.model.MessageType;
import com.huishangyun.service.HSChatService;
import com.huishangyun.yun.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class RecentChartAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<GotyeChatTarget> inviteUsers;
    private Context context;

    public RecentChartAdapter(Context context, List<GotyeChatTarget> inviteUsers) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.inviteUsers = inviteUsers;
    }

	/*public void setNoticeList(List<ChartHisBean> inviteUsers) {
		this.inviteUsers = inviteUsers;
		this.notifyDataSetChanged();
	}*/

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return inviteUsers.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return inviteUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolderx holderx = null;
        GotyeChatTarget notice = inviteUsers.get(position);

        GotyeMessage lastMsg = MyApplication.getInstance().getGotyeAPI().getLastMessage(notice);
        int count = MyApplication.getInstance().getGotyeAPI().getUnreadMessageCount(notice);//未读消息数量

        boolean isServer = false;//企业服务号,提交消息回执
        boolean isSystem=false;//系统消息号
        String senderID = lastMsg.getSender().getName();
        int companyID = MyApplication.getInstance().getCompanyID();
        if (senderID.indexOf(companyID + "_s_") == 0||senderID.indexOf("0_n_") == 0) {
            isServer = true;
        }
        try {
            if (convertView == null) {
                L.e("convertView == null");
                holderx = new ViewHolderx();
                convertView = mInflater.inflate(R.layout.item_recent_msg, null);
                holderx.itemIcon = (ImageView) convertView.findViewById(R.id.icon);
                holderx.newTitle = (TextView) convertView.findViewById(R.id.recent_list_item_name);
                holderx.newContent = (TextView) convertView.findViewById(R.id.recent_list_item_msg);
                holderx.newDate = (TextView) convertView.findViewById(R.id.recent_list_item_time);
                holderx.paopao = (TextView) convertView.findViewById(R.id.unreadmsg);
                holderx.line = (RelativeLayout) convertView.findViewById(R.id.msg_line);
                holderx.noline = (RelativeLayout) convertView.findViewById(R.id.msg_line_no);
                convertView.setTag(R.drawable.about, holderx);
            } else {
                L.e("convertView != null");
                holderx = (ViewHolderx) convertView.getTag(R.drawable.about);
            }
            //Integer ppCount = MyApplication.getInstance().getGotyeAPI().getUnreadMsgcounts(notice);
            //查询数据库，没有就从网络获取
            if (isServer) {
                MessageData<ServiceUtil> messageData = JsonUtil.fromJson(lastMsg.getText(), new TypeToken<MessageData<ServiceUtil>>() {
                }.getType());
                ServiceUtil Content = messageData.getMessageContent();
                if (Content != null) {//判断返回的解析结果是否为空
                    holderx.newContent.setText(Content.getTitle());
                } else {
                    holderx.newContent.setText(FaceUtil.convertNormalStringToSpannableString(context, messageData.getMessageContent().getTitle(), true));
                }
                String name = ServiceManager.getInstance(context).getServiceName(notice.getName());
                holderx.newTitle.setText(name);
               // Log.e("TAGS","name头像ser="+name);
                String headurl = ServiceManager.getInstance(context).getServiceImg(notice.getName());

                com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(Constant.pathurl + companyID + "/Photo/" + headurl, holderx.itemIcon, MyApplication.getInstance().getOptions());

            }else {
                if (notice.getType() == GotyeChatTargetType.GotyeChatTargetTypeUser) {
                    String name = DepartmentManager.getInstance(context).getManager(notice.getName());
                    String names = ServiceManager.getInstance(context).getServiceName(notice.getName());
                    if (name == null || name.equals("")) {
                        name = MemberManager.getInstance(context).getMember(notice.getName());
                        if (name == null || name.equals("")) {
                            if (name == null || name.equals("")) {
                                name = notice.getName();
                            }
                        }
                    }
                    //_s_系统消息部分
                    if (notice.getName().contains("_s_")){
                        holderx.newTitle.setText(names);
                        //获取头像消息
                        holderx.itemIcon.setImageResource(R.drawable.contact_person);
                        String headurl = ServiceManager.getInstance(context).getServiceImg(notice.getName());
                        if (headurl == null) {
                            headurl = ServiceManager.getInstance(context).getServiceImg(notice.getName());
                        }
                        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(Constant.pathurl +companyID + "/Photo/" + headurl, holderx.itemIcon, MyApplication.getInstance().getOptions());
                    }else{
                        holderx.newTitle.setText(name);
                        //获取普通人员 头像信息
                        holderx.itemIcon.setImageResource(R.drawable.contact_person);
                        String headurl = DepartmentManager.getInstance(context).getManagerPhoto(notice.getName());
                        if (headurl == null) {
                            headurl = MemberManager.getInstance(context).getMemberPhoto(notice.getName());
                        }
                        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(Constant.pathurl +companyID + "/Photo/" + headurl, holderx.itemIcon, MyApplication.getInstance().getOptions());
                    }
                } else {
                  //  GotyeGroup group = MyApplication.getInstance().getGotyeAPI().requestGroupInfo(notice.getId(), false);//易注释掉
                   // GotyeGroup group=new GotyeGroup();

                    GotyeGroup group = MyApplication.getInstance().getGotyeAPI().getGroupDetail(notice, false);
                    String headurl = "";
                    if (group != null) {
                        String name = GroupManager.getInstance(context).getGroupName(group.getId() + "");
                        if (name == null) {
                            if (TextUtils.isEmpty(group.getGroupName())) {
                                holderx.newTitle.setText("" + group.getId());
                            } else {
                                holderx.newTitle.setText("" + group.getGroupName());
                            }
                        } else {
                            holderx.newTitle.setText("" + name);
                        }
                        headurl = GroupManager.getInstance(context).getPhoto(group.getId() + "");
                    } else {
                        holderx.newTitle.setText("" + notice.getId());
                    }

                    holderx.itemIcon.setImageResource(R.drawable.contact_group);
                    com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(Constant.pathurl + companyID + "/Photo/" + headurl, holderx.itemIcon, MyApplication.getInstance().getGroupOptions());
                }
                MessageData<String> messageData = JsonUtil.fromJson(lastMsg.getText(), new TypeToken<MessageData<String>>() {
                }.getType());
                if (messageData.getMessageCategory().equals(MessageType.MESSAGE_VIDEO)) {
                    holderx.newContent.setText("[语音]");
                } else if (messageData.getMessageCategory().equals(MessageType.MESSAGE_PHOTO)) {
                    holderx.newContent.setText("[图片]");
                } else if (messageData.getMessageCategory().equals(MessageType.MESSAGE_FILE)) {
                    holderx.newContent.setText("[文件]");
                } else {
                    holderx.newContent.setText(FaceUtil.convertNormalStringToSpannableString(context, messageData.getMessageContent().toString(), true));
                }
            }

            if (position != inviteUsers.size() - 1) {
                holderx.line.setVisibility(View.GONE);
                holderx.noline.setVisibility(View.VISIBLE);
            } else {
                holderx.line.setVisibility(View.VISIBLE);
                holderx.noline.setVisibility(View.GONE);
            }
            SimpleDateFormat sdf = new SimpleDateFormat(Constant.MS_FORMART);
            //long date = TimeUtil.getLongtime(notice.getNoticeTime());
            long date = lastMsg.getDate() * 1000;
            holderx.newDate.setText(TimeUtil.getChatTime(date));

            if (count > 0) {
                holderx.paopao.setVisibility(View.VISIBLE);
                holderx.paopao.setText(String.valueOf(count));
            } else {
                holderx.paopao.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        holderx.newContent.setTag(notice);

        return convertView;
    }


    static class ViewHolderx {
        public ImageView itemIcon;
        public TextView newTitle;
        public TextView newContent;
        public TextView newDate;
        public TextView paopao;
        public RelativeLayout line;
        public RelativeLayout noline;
    }

}
