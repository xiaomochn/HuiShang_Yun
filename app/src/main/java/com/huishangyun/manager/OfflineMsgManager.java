package com.huishangyun.manager;

import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.ServiceResolve;
import com.huishangyun.Util.ServiceUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;

import android.content.Context;

/**
 * 
 * 离线信息管理类.
 * 
 * @author pan
 */
public class OfflineMsgManager {
	private static OfflineMsgManager offlineMsgManager = null;
	private Context context;
	private long oldTime = 0;
	private String url = "http://220.175.122.90:83/im/im.asmx";
	private static final String namespace = "http://im.huishangyun.com/";
	// SoapHeaders身份验证的方法名
	private static String soapheadername = "CredentialSoapHeader";
	private static String uesrid = "suokete";
	private static String password = "skt*2014";
	
	private OfflineMsgManager(Context context) {
		this.context = context;
	}

	public static OfflineMsgManager getInstance(Context context) {
		if (offlineMsgManager == null) {
			offlineMsgManager = new OfflineMsgManager(context);
		}
		return offlineMsgManager;
	}
	
	/**
	 * 
	 * @return SoapHeaders身份验证
	 */
	private static Element[] getSoapHeader() {
		Element[] header = new Element[1];
		header[0] = new Element().createElement(namespace, soapheadername);
		Element userName = new Element().createElement(namespace, "UserID");
		userName.addChild(Node.TEXT, uesrid);
		header[0].addChild(Node.ELEMENT, userName);
		Element pass = new Element().createElement(namespace, "PassWord");
		pass.addChild(Node.TEXT, password);
		header[0].addChild(Node.ELEMENT, pass);
		return header;
	}

	/**
	 * 
	 * 处理离线消息.
	 * 
	 * @param connection
	 * @author pan
	 */
	/*public void dealOfflineMsg(XMPPConnection connection) {
		OfflineMessageManager offlineManager = new OfflineMessageManager(
				connection);
		try {
			boolean isSend ;
			List<org.jivesoftware.smack.packet.Message> it = offlineManager
					.getMessages();
			L.d("离线消息数量: "+ offlineManager.getMessageCount());
			for (Message message : it) {
				L.d("收到离线消息" + it.hasNext());
				//org.jivesoftware.smack.packet.Message message = it.next();
				L.d("进入离线消息" + it.hashCode());
                if (message != null && message.getBody() != null
    					&& !message.getBody().equals("null")) {
                	if (message.getFrom().endsWith(XmppConnectionManager.getInstance().getConnection().getUser())) {
        				L.d("接收到自己说的话，不予显示");
        				return;
        			}
        			if (oldTime == 0) {
        				oldTime = System.currentTimeMillis();
        				isSend = true;
        			}else {
        				if (System.currentTimeMillis() - oldTime > 2000) {
        					isSend = true;
        				}else {
        					isSend = false;
        				}
        				oldTime = System.currentTimeMillis();
        			}
        			
        			IMMessage msg = new IMMessage();
        			//获取发送时间
        			DefaultPacketExtension messageTime = message.getExtension("query", "jabber:iq:time");
        			String time = messageTime.getValue(IMMessage.KEY_TIME);
        			String messageType = messageTime.getValue(IMMessage.KEY_TYPE);
        			if (time == null) {
        				//没有发送时间的话就获取当前系统时间
        				time = DateUtil.date2Str(Calendar.getInstance(),
        						Constant.MS_FORMART);
        			}
        			L.d("time = " + time );
        			L.d("time2 = " + message.getLanguage());
        			if (MessageManager.getInstance(context).IsTimeEquals(time)) {
        				L.d("数据库中有相同的记录");
        				return;
        			}
        			msg.setTime(time);
        			String from = message.getFrom().split("/")[0];
        			msg.setContent(message.getBody());
        			msg.setMsg_Category(messageType);
        			
        			if (Message.Type.error == message.getType()) {
        				msg.setType(IMMessage.ERROR);
        			} else {
        				msg.setType(IMMessage.SUCCESS);
        			}
        			
        			msg.setFromSubJid(from);

        			// 生成通知
        			NoticeManager noticeManager = NoticeManager
        					.getInstance(context);
        			Notice notice = new Notice();
        			notice.setTitle("会话信息");
        			notice.setNoticeType(Notice.CHAT_MSG);
        			notice.setContent(message.getBody());
        			notice.setFrom(from);
        			notice.setStatus(Notice.UNREAD);
        			notice.setNoticeTime(time);
        			notice.setMsg_Category(messageType);

        			// 历史记录
        			IMMessage newMessage = new IMMessage();
        			newMessage.setMsgType(0);
        			newMessage.setFromSubJid(from);
        			newMessage.setContent(message.getBody());
        			newMessage.setTime(time);
        			newMessage.setMsg_Category(messageType);
        			if (message.getType() == Type.chat) {
        				
        				if (messageType.equals(MessageType.MESSAGE_SERVICE)) {//是服务号推送信息
        					newMessage.setMesstype(0);
        					newMessage.setMsgType(0);
        					msg.setMesstype(0);
        					notice.setNoticeType(Notice.SYS_MSG);//是系统推送的消息
        					receiptSerivce(StringUtil.getUserNameByJid(message.getTo()), message.getBody());
        				}else {//是常规聊天信息
        					newMessage.setMesstype(0);
        					notice.setNoticeType(Notice.CHAT_MSG);
        					msg.setMesstype(0);
        				}
        			}else {
        				newMessage.setMesstype(1);
        				newMessage.setGroup_name(message.getFrom().split("/")[1]);
        				msg.setMesstype(1);
        				msg.setGroup_name(message.getFrom().split("/")[1]);
        			}
        			L.d("from = " + message.getFrom());
        			MessageManager.getInstance(context).saveIMMessage(newMessage);
        			long noticeId = -1;

        			noticeId = noticeManager.saveNotice(notice);

        			if (noticeId != -1) {
        					Intent intent = new Intent(Constant.NEW_MESSAGE_ACTION);
        					intent.putExtra(IMMessage.IMMESSAGE_KEY, msg);
        					intent.putExtra("notice", notice);
        					if (isSend) {
								
							}
        					MyApplication.getInstance().sendBroadcast(intent);
        					if (isSend) {
        						if (message.getType() == Type.chat) {
        							setNotiType(R.drawable.logo,
        									"你有新消息",
        									notice.getContent(), Chat.class, from,1,0);
        						}else {
        							setNotiType(R.drawable.logo,
        									"你有新群消息",
        									notice.getContent(), Chat.class, from,1,1);
        						}
        					}
        				}


    			}
			}
			
			//offlineManager.deleteMessages();
			L.e("删除离线消息");
			connection.sendPacket(new Presence(Presence.Type.available));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}*/
	
	/**
	 * 删除离线消息
	 */
	public static void deleteMessages() {
		L.e("删除离线消息");
		/*webServiceHelp<T> mServiceHelp = new webServiceHelp<T>(Methods.DELETE_OFFMESSAGE, new TypeToken<ZJResponse>(){}.getType());
		mServiceHelp.setOnServiceCallBack(new OnServiceCallBack<T>() {

			@Override
			public void onServiceCallBack(boolean haveCallBack,
					ZJResponse<T> zjResponse) {
				// TODO Auto-generated method stub
				if (haveCallBack && zjResponse != null) {
					L.e("zjResponse = " + zjResponse.getCode());
					if (zjResponse.getCode() == 0) {
						L.e("zjResponse = " + zjResponse.getCode());
						L.e("删除成功");
					} else {
						L.e(zjResponse.getDesc());
					}
					
				} else {
					L.e("接口无法访问");
				}
			}
		});
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setOFUserName(MyApplication.preferences.getString(Constant.XMPP_LOGIN_NAME, ""));
		mServiceHelp.start(JsonUtil.toJson(zjRequest));*/
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setOFUserName(MyApplication.preferences.getString(Constant.XMPP_LOGIN_NAME, ""));
		L.e("json = " + JsonUtil.toJson(zjRequest));
		String result = DataUtil.callWebService(Methods.DELETE_OFFMESSAGE, JsonUtil.toJson(zjRequest));
		if (result != null) {
			ZJResponse zjResponse = JsonUtil.fromJson(result, new TypeToken<ZJResponse>(){}.getType());
			if (zjResponse.getCode() == 0) {
				L.e("zjResponse = " + zjResponse.getCode());
				L.e("删除成功");
			} else {
				L.e(zjResponse.getDesc());
			}
		} else {
			L.e("接口无法访问");
		}
		
	}
	
	
	/**
	 * 将消息回执返回到服务器
	 * 
	 * @param JID
	 * @return
	 */
	
	private void receiptSerivce(final String JID, final String msg) {
		//是服务号，同时将消息回执返回到服务器
		new Thread(){
			public void run() {
				/*ZJRequest<MessageStatus> zjRequest = new ZJRequest<MessageStatus>();
				MessageStatus messageStatus = new MessageStatus();
				messageStatus.setOFUserName(JID);
				ServiceUtil sUtil = ServiceResolve
						.ResolveInfo(msg);
				messageStatus.setMessage_ID(Integer.parseInt(sUtil.getBodyhttpurl()));
				zjRequest.setData(messageStatus);
				String json = JsonUtil.toJson(zjRequest);
				java.lang.reflect.Type type = new TypeToken<ZJResponse>(){}.getType();
				String result = DataUtil.callWebService(Methods.SET_MESSAGE_STATUS, json);
				L.e("result = " + result);
				if (result != null) {
					ZJResponse zjResponse = JsonUtil.fromJson(result, type);
					if (zjResponse.getCode() == 0) {
						L.e("回执成功");
					}
				}*/
				ZJRequest zjRequest = new ZJRequest();
				ServiceUtil sUtil = ServiceResolve
						.ResolveInfo(msg);
				//zjRequest.setID(Integer.parseInt(sUtil.getBodyhttpurl()));
				zjRequest.setOFUserName(JID);
				String json = JsonUtil.toJson(zjRequest);
				/*java.lang.reflect.Type type = new TypeToken<ZJResponse>(){}.getType();
				String result = DataUtil.callWebService(Methods.SET_MESSAGE_STATUS, json);
				L.e("result = " + result);
				if (result != null) {
					ZJResponse zjResponse = JsonUtil.fromJson(result, type);
					if (zjResponse.getCode() == 0) {
						L.e("回执成功");
					}
				}*/
				
			};
		}.start();
	}
}
