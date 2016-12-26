package com.huishangyun.Activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeChatTarget;
import com.gotye.api.GotyeChatTargetType;
import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeNotify;
import com.gotye.api.GotyeRoom;
import com.gotye.api.GotyeUser;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.Base64Util;
import com.huishangyun.Util.FileUtils;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ServiceMenu;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.manager.MessageManager;
import com.huishangyun.manager.ServiceManager;
import com.huishangyun.model.IMMessage;
import com.huishangyun.model.MessageData;
import com.huishangyun.model.MessageType;
import com.huishangyun.task.UpLoadFileTask;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.task.UpLoadImgSignText;

/**
 * 聊天窗口父类
 *
 * @author pan
 */
public abstract class BaseChatActivity extends BaseMainActivity {
    protected Chat chat;
    private List<GotyeMessage> message_pool = null;
    protected String to;// 聊天人
    protected int type;
    /**
     * 个性签名
     */
    protected String m_sSign = "";
    protected String user_name;
    protected static int pageSize = 10;
    protected int messtype;
    protected int i = 0;
    protected CharSequence chat_name = "";//聊天人姓名
    protected int linkListSize = 0;
    public int chatType = 0;

    protected GotyeUser o_user, user;
    protected GotyeGroup o_group, group;
    protected GotyeUser currentLoginUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);


    }

	/*@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
		L.e("走了onNewIntent");
		to = getIntent().getStringExtra("JID");
		type = getIntent().getIntExtra("type", 1);
		m_sSign = getIntent().getStringExtra("Sign");
		user_name = getIntent().getStringExtra("name");
		messtype = getIntent().getIntExtra("messtype", 0);
		chat_name = getIntent().getCharSequenceExtra("chat_name");
		if (messtype == 0) {
			chatType = messtype;
		} else {
			chatType = 2;
		}
		
		o_user=user = (GotyeUser) getIntent().getSerializableExtra("user");
		o_group=group = (GotyeGroup) getIntent().getSerializableExtra("group");
		currentLoginUser = MyApplication.getInstance().getGotyeAPI().getCurrentLoginUser();
		L.d("JID =" + to);
		MyApplication.getInstance().getGotyeAPI().addListener(this);
		if (to == null)
			return;
	}*/

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        //unregisterReceiver(receiver);
        //取消激活
        if (chatType == 0) {
            //更新阅读状态
            GotyeAPI.getInstance().markMessagesAsRead(user,false);
            GotyeAPI.getInstance().deactiveSession(user);
        } else {
            //更新阅读状态
            GotyeAPI.getInstance().markMessagesAsRead(group,false);
            GotyeAPI.getInstance().deactiveSession(group);
        }
        MyApplication.getInstance().getGotyeAPI().removeListener(mDelegate);
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    protected List<GotyeMessage> loadData() {
        List<GotyeMessage> messages = null;
        if (user != null) {
            messages = MyApplication.getInstance().getGotyeAPI().getMessageList(user, true);

        } else if (group != null) {
            messages = MyApplication.getInstance().getGotyeAPI().getMessageList(group, true);

        }
        if (messages == null) {
            messages = new ArrayList<GotyeMessage>();
        }
        for (GotyeMessage msg : messages) {
            MyApplication.getInstance().getGotyeAPI().downloadMediaInMessage(msg);
            L.e("msg = " + msg);
            //判断是否可以存储到数据库
            if (!MessageManager.getInstance(this).IsTimeEquals(TimeUtil.getLongToString((msg.getDate() * 1000)))) {
                IMMessage imMessage = new IMMessage();
                if (msg.getReceiver().getType() == GotyeChatTargetType.GotyeChatTargetTypeGroup) {
                    //保存群ID号
                    imMessage.setGroup_name(msg.getReceiver().getId() + "");
                    imMessage.setMesstype(1);
                    imMessage.setFromSubJid(msg.getSender().getName());
                } else {
                    //0不是群
                    imMessage.setMesstype(0);
                    imMessage.setFromSubJid(to);
                }
                //保存内容
                imMessage.setContent(msg.getText());
                //保存时间
                imMessage.setTime(TimeUtil.getLongToString((msg.getDate() * 1000)));
                //1为发送,0为接收
                if (msg.getSender().getName().equals(MyApplication.getInstance().getGotyeAPI().getLoginUser().getName())) {
                    imMessage.setMsgType(1);
                } else {
                    imMessage.setMsgType(0);
                }
                MessageManager.getInstance(this).saveIMMessage(imMessage);
            }
        }
        return messages;
    }

    protected abstract void onSendMessage(GotyeMessage message);

    /**
     * 发送消息
     *
     * @param messageType
     */
    public GotyeMessage sendMessage(String message, String messageType) {
        //
        MessageData<String> messageData = new MessageData<String>();
        messageData.setMessageContent(message);
        messageData.setMessageCategory(messageType);
        GotyeMessage gotyeMessage;
        //转成json
        if (messtype == 0) {
            gotyeMessage = GotyeMessage.createTextMessage(currentLoginUser, o_user, JsonUtil.toJson(messageData));
        } else {
            gotyeMessage = GotyeMessage.createTextMessage(currentLoginUser, o_group, JsonUtil.toJson(messageData));
        }
        GotyeAPI.getInstance().sendMessage(gotyeMessage);
		/*IMMessage imMessage = new IMMessage();
		if (group != null) {
			//保存群ID号
			imMessage.setGroup_name(group.getId() + "");
			imMessage.setMesstype(1);
		} else {
			//0不是群
			imMessage.setMesstype(0);
		}
		//保存内容
		imMessage.setFromSubJid(to);
		imMessage.setContent(gotyeMessage.getText());
		//保存时间
		imMessage.setTime(TimeUtil.getLongToString((gotyeMessage.getDate() * 1000)));
		//1为发送,0为接收
		imMessage.setMsgType(1);
		
		MessageManager.getInstance(this).saveIMMessage(imMessage);*/
        return gotyeMessage;
    }


    /**
     * 获取SDcard根目录
     */
    public String getRootPath() {
        return Environment.getExternalStorageDirectory().toString();
    }

    /**
     * 发送文件(发送各种文件用此方法)
     *
     * @param filePath
     * @param messageType
     */
    public void sendFileMesage(final String filePath, final String messageType) {
        String base64;
        try {
            base64 = Base64Util.encodeBase64File(filePath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            showCustomToast("文件不存在", false);
            dismissDialog();
            e.printStackTrace();
            return;
        }
        UpLoadFileTask upLoadFileTask = new UpLoadFileTask(base64, MessageType.MESSAGE_UPLOAD_MPDULE, new File(filePath).getName(),
                MyApplication.getInstance().getCompanyID() + "");
        upLoadFileTask.setUpLoadResult(new UpLoadFileTask.OnUpLoadResult() {

            @Override
            public void onUpLoadResult(String FileName, String FilePath,
                                       boolean isSuccess) {
                // TODO Auto-generated method stub
                dismissDialog();
                if (isSuccess) {
                    //上传文件成功,发送消息给对方
                    if (messageType == MessageType.MESSAGE_FILE) {
                        setMessage(sendMessage(FileName + "#" + FilePath, messageType));

                    } else if (messageType == MessageType.MESSAGE_VIDEO) {
                        long lenght = 0;
                        try {
                            lenght = FileUtils.getAmrDuration(new File(filePath));
                        } catch (Exception e) {
                            e.printStackTrace();
                            lenght = 0;
                        }
                        lenght = (lenght / 1000) - 5;
                        setMessage(sendMessage(FilePath + "#" + lenght + "''", messageType));
                    } else {
                        setMessage(sendMessage(FilePath, messageType));
                    }

                } else {
                    showCustomToast("发送失败", false);
                }
            }
        });
        //开始上传
        new Thread(upLoadFileTask).start();
    }

    /**
     * 发送图片
     * @param filePath
     * @param messageType
     */
    public void sendImgMessage(final String filePath, final String messageType) {
        String base64;
        try {
            base64 = Base64Util.encodeBase64File(filePath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            showCustomToast("文件不存在", false);
            dismissDialog();
            e.printStackTrace();
            return;
        }

        UpLoadImgSignText upLoadFileTask = new UpLoadImgSignText(base64, MessageType.MESSAGE_UPLOAD_MPDULE, new File(filePath).getName(),
                MyApplication.getInstance().getCompanyID() + "", "");
        upLoadFileTask.setUpLoadResult(new UpLoadFileTask.OnUpLoadResult() {
            @Override
            public void onUpLoadResult(String FileName, String FilePath, boolean isSuccess) {
                dismissDialog();
                if (isSuccess) {
                    //上传文件成功,发送消息给对方
                    if (messageType == MessageType.MESSAGE_FILE) {
                        setMessage(sendMessage(FileName + "#" + FilePath, messageType));

                    } else if (messageType == MessageType.MESSAGE_VIDEO) {
                        long lenght = 0;
                        try {
                            lenght = FileUtils.getAmrDuration(new File(filePath));
                        } catch (Exception e) {
                            e.printStackTrace();
                            lenght = 0;
                        }
                        lenght = (lenght / 1000) - 5;
                        setMessage(sendMessage(FilePath + "#" + lenght + "''", messageType));
                    } else {
                        setMessage(sendMessage(FilePath, messageType));
                    }

                } else {
                    showCustomToast("发送失败", false);
                }
            }
        });
        //开始上传
        new Thread(upLoadFileTask).start();
    }

    /**
     * 获取一级菜单
     *
     * @param OFUserName
     * @return
     */
    protected List<ServiceMenu> getFristMenu(String OFUserName) {
        L.e("OFUserName = " + OFUserName);
        Integer serviceID = ServiceManager.getInstance(this).getServiceID(OFUserName);
        if (serviceID == null) {
            return null;
        }
        List<ServiceMenu> serviceMenus = new ArrayList<ServiceMenu>();
        serviceMenus = ServiceManager.getInstance(this).getFristMenu(serviceID);
        return serviceMenus;
    }

    private boolean isMyMessage(GotyeMessage message) {
        if (message.getSender() != null
                && user.getName().equals(message.getSender().getName())
                && currentLoginUser.getName().equals(message.getReceiver().getName())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取二级菜单
     *
     * @param ID
     * @return
     */
    protected List<ServiceMenu> getSecondMenu(int ID) {
        L.e("ID = " + ID);
        List<ServiceMenu> serviceMenus = new ArrayList<ServiceMenu>();
        serviceMenus = ServiceManager.getInstance(this).getSecondMenu(ID);
        return serviceMenus;
    }
    private GotyeDelegate mDelegate = new GotyeDelegate(){
        @Override
        public void onGetMessageList(int code, List<GotyeMessage> list) {
            super.onGetMessageList(code, list);
            if (chatType == 0) {
                List<GotyeMessage> listmessages = MyApplication.getInstance().getGotyeAPI().getMessageList(o_user, false);
                if (listmessages != null) {
                    for (GotyeMessage temp : listmessages) {

                        MyApplication.getInstance().getGotyeAPI().downloadMediaInMessage(temp);
                    }
                    refreshData(listmessages);
                } else {
                    //ToastUtil.show(this, "没有历史记录");
                    showCustomToast("没有历史记录", false);
                }
            } else {
                List<GotyeMessage> listmessages = MyApplication.getInstance().getGotyeAPI().getMessageList(o_group, false);
                if (listmessages != null) {
                    for (GotyeMessage temp : listmessages) {
                        MyApplication.getInstance().getGotyeAPI().downloadMediaInMessage(temp);
                    }
                    refreshData(listmessages);
                } else {
                    showCustomToast("没有历史记录", false);
                }
            }
        }
        @Override
        public void onReceiveMessage(GotyeMessage message) {
            super.onReceiveMessage(message);
            if (chatType == 0) {
                if (isMyMessage(message)) {
				/*adapter.addMsgToBottom(message);
				pullListView.setSelection(adapter.getCount());*/
                    setMessage(message);
                    MyApplication.getInstance().getGotyeAPI().downloadMediaInMessage(message);
                }
            } else {
                if (message.getReceiver().getId() == group.getGroupID()) {
				/*adapter.addMsgToBottom(message);
				pullListView.setSelection(adapter.getCount());*/
                    setMessage(message);
                    MyApplication.getInstance().getGotyeAPI().downloadMediaInMessage(message);
                }
            }
        }

        @Override
        public void onUserDismissGroup(GotyeGroup group, GotyeUser user) {
            super.onUserDismissGroup(group, user);
            if (group != null && group.getGroupID() == group.getGroupID()) {
                Toast.makeText(getBaseContext(), "群主解散了该群,会话结束", Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        }

        @Override
        public void onUserJoinGroup(GotyeGroup group, GotyeUser user) {
            super.onUserJoinGroup(group, user);
        }

        @Override
        public void onUserKickedFromGroup(GotyeGroup group, GotyeUser kicked, GotyeUser actor) {
            super.onUserKickedFromGroup(group, kicked, actor);
            if (group != null && group.getGroupID() ==group.getGroupID()) {
                if (kicked.getName().equals(currentLoginUser.getName())) {
                    Toast.makeText(getBaseContext(), "您被踢出了群,会话结束",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        }
    };

    protected abstract void setMessage(GotyeMessage message);

    protected abstract void refreshData(List<GotyeMessage> listmessages);

}
