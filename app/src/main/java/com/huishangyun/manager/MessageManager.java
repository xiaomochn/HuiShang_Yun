package com.huishangyun.manager;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.huishangyun.Util.L;
import com.huishangyun.Util.StringUtil;
import com.huishangyun.db.SQLiteTemplate;
import com.huishangyun.db.SQLiteTemplate.RowMapper;
import com.huishangyun.model.ChartHisBean;
import com.huishangyun.model.Constant;
import com.huishangyun.model.IMMessage;
import com.huishangyun.model.Notice;

public class MessageManager {
	private static MessageManager messageManager = null;
	private static DBManager manager = null;

	private MessageManager(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.LOGIN_SET, Context.MODE_PRIVATE);
		String databaseName = sharedPre.getString(Constant.XMPP_LOGIN_NAME, null);
		manager = DBManager.getInstance(context);
	}

	public static MessageManager getInstance(Context context) {

		if (messageManager == null) {
			messageManager = new MessageManager(context);
		}

		return messageManager;
	}

	/**
	 * 
	 * 保存消息.
	 * 
	 * @param msg
	 * @author status
	 * @update 2015-5-16 下午3:23:15
	 */
	public long saveIMMessage(IMMessage msg) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		if (StringUtil.notEmpty(msg.getContent())) {
			contentValues.put("content", StringUtil.doEmpty(msg.getContent()));
		}
		if (StringUtil.notEmpty(msg.getFromSubJid())) {
			contentValues.put("msg_from",
					StringUtil.doEmpty(msg.getFromSubJid()));
		}
		contentValues.put("msg_type", msg.getMsgType());
		contentValues.put("msg_time", msg.getTime());
		contentValues.put("is_group", msg.getMesstype());
		contentValues.put("group_username", msg.getGroup_name());
		contentValues.put("Msg_Category", msg.getMsg_Category());
		L.d("结束");
		return st.insert("im_msg_his", contentValues);
	}

	/**
	 * 
	 * 更新状态.
	 * 
	 * @param status
	 * @author pan
	 * @update 2015-5-16 下午3:22:44
	 */
	public void updateStatus(String id, Integer status) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("status", status);
		st.updateById("im_msg_his", id, contentValues);
	}

	/**
	 * 
	 * 查找与某人的聊天记录聊天记录
	 * 
	 * @param pageNum
	 *            第几页
	 * @param pageSize
	 *            要查的记录条数
	 * @return
	 * @author pan
	 * @update 2014-7-2 上午9:31:04
	 */
	public List<IMMessage> getMessageListByFrom(String fromUser, int pageNum,
			int pageSize, boolean isGroup) {
		if (StringUtil.empty(fromUser)) {
			return null;
		}
		List<IMMessage> list;
		int fromIndex = (pageNum - 1) * pageSize;
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		if (isGroup) {
			list = st.queryForList(
					new RowMapper<IMMessage>() {
						@Override
						public IMMessage mapRow(Cursor cursor, int index) {
							IMMessage msg = new IMMessage();
							msg.setContent(cursor.getString(cursor
									.getColumnIndex("content")));
							msg.setFromSubJid(cursor.getString(cursor
									.getColumnIndex("msg_from")));
							msg.setMsgType(cursor.getInt(cursor
									.getColumnIndex("msg_type")));
							msg.setTime(cursor.getString(cursor
									.getColumnIndex("msg_time")));
							msg.setMesstype(cursor.getInt(cursor.getColumnIndex("is_group")));
							msg.setGroup_name(cursor.getString(cursor
									.getColumnIndex("group_username")));
							msg.setMsg_Category(cursor.getString(cursor.getColumnIndex("Msg_Category")));
							return msg;
						}
					},
					"select content,msg_from, msg_type,msg_time , is_group, group_username, Msg_Category from im_msg_his where group_username=? order by msg_time desc limit ? , ? ",
					new String[] { "" + fromUser, "" + fromIndex, "" + pageSize });
		} else {
			
			list = st.queryForList(
					new RowMapper<IMMessage>() {
						@Override
						public IMMessage mapRow(Cursor cursor, int index) {
							IMMessage msg = new IMMessage();
							msg.setContent(cursor.getString(cursor
									.getColumnIndex("content")));
							msg.setFromSubJid(cursor.getString(cursor
									.getColumnIndex("msg_from")));
							msg.setMsgType(cursor.getInt(cursor
									.getColumnIndex("msg_type")));
							msg.setTime(cursor.getString(cursor
									.getColumnIndex("msg_time")));
							msg.setMesstype(cursor.getInt(cursor.getColumnIndex("is_group")));
							msg.setGroup_name(cursor.getString(cursor
									.getColumnIndex("group_username")));
							msg.setMsg_Category(cursor.getString(cursor.getColumnIndex("Msg_Category")));
							return msg;
						}
					},
					"select content,msg_from, msg_type,msg_time , is_group, group_username, Msg_Category from im_msg_his where msg_from=? and is_group = 0 order by msg_time desc limit ? , ? ",
					new String[] { "" + fromUser, "" + fromIndex, "" + pageSize });
		}
		return list;

	}
	
	
	public List<IMMessage> searchMessages(String fromUser, String keyword, boolean isGroup) {
		if (StringUtil.empty(fromUser)) {
			return null;
		}
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<IMMessage> list;
		if (isGroup) {
			list = st.queryForList(
					new RowMapper<IMMessage>() {
						@Override
						public IMMessage mapRow(Cursor cursor, int index) {
							IMMessage msg = new IMMessage();
							msg.setContent(cursor.getString(cursor
									.getColumnIndex("content")));
							msg.setFromSubJid(cursor.getString(cursor
									.getColumnIndex("msg_from")));
							msg.setMsgType(cursor.getInt(cursor
									.getColumnIndex("msg_type")));
							msg.setTime(cursor.getString(cursor
									.getColumnIndex("msg_time")));
							msg.setMesstype(cursor.getInt(cursor.getColumnIndex("is_group")));
							msg.setGroup_name(cursor.getString(cursor
									.getColumnIndex("group_username")));
							msg.setMsg_Category(cursor.getString(cursor.getColumnIndex("Msg_Category")));
							return msg;
						}
					},
					"select content,msg_from, msg_type,msg_time , is_group, group_username, Msg_Category from im_msg_his where group_username=? and content like '%" + keyword + "%' order by msg_time desc",
					new String[] { "" + fromUser});
		} else {
			list = st.queryForList(
					new RowMapper<IMMessage>() {
						@Override
						public IMMessage mapRow(Cursor cursor, int index) {
							IMMessage msg = new IMMessage();
							msg.setContent(cursor.getString(cursor
									.getColumnIndex("content")));
							msg.setFromSubJid(cursor.getString(cursor
									.getColumnIndex("msg_from")));
							msg.setMsgType(cursor.getInt(cursor
									.getColumnIndex("msg_type")));
							msg.setTime(cursor.getString(cursor
									.getColumnIndex("msg_time")));
							msg.setMesstype(cursor.getInt(cursor.getColumnIndex("is_group")));
							msg.setGroup_name(cursor.getString(cursor
									.getColumnIndex("group_username")));
							msg.setMsg_Category(cursor.getString(cursor.getColumnIndex("Msg_Category")));
							return msg;
						}
					},
					"select content,msg_from, msg_type,msg_time , is_group, group_username, Msg_Category from im_msg_his where msg_from=? and is_group = 0  and content like '%" + keyword + "%' order by msg_time desc",
					new String[] { "" + fromUser});
		}
		
		return list;
		
	}

	/**
	 * 
	 * 查找与某人的聊天记录总数
	 * 
	 * @return
	 * @author pan
	 * @update 2014-7-2 上午9:31:04
	 */
	public int getChatCountWithSb(String fromUser, boolean isGroup) {
		if (StringUtil.empty(fromUser)) {
			return 0;
		}
		int i = 0;
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		if (isGroup) {
			i = st
					.getCount(
							"select _id,content,msg_from msg_type  from im_msg_his where group_username=?",
							new String[] { "" + fromUser });
		} else {
			i = st
					.getCount(
							"select _id,content,msg_from msg_type  from im_msg_his where msg_from=? and is_group = 0 ",
							new String[] { "" + fromUser });
		}
		return i;

	}

	/**
	 * 删除与某人的聊天记录 author pan
	 * 
	 * @param fromUser
	 */
	public int delChatHisWithSb(String fromUser, boolean isGroup) {
		if (StringUtil.empty(fromUser)) {
			return 0;
		}
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		if (isGroup) {
			return st.deleteByCondition("im_msg_his", "group_username=?",
					new String[] { "" + fromUser });
		} else {
			return st.deleteByCondition("im_msg_his", "msg_from=? and is_group = 0 ",
					new String[] { "" + fromUser });
		}
		
	
	}

	/**
	 * 
	 * 获取最近聊天人聊天最后一条消息和未读消息总数
	 * 
	 * @return
	 * @author pan
	 * @update 2014-5-16 下午3:22:53
	 */
	public List<ChartHisBean> getRecentContactsWithLastMsg() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<ChartHisBean> list = st
				.queryForList(
						new RowMapper<ChartHisBean>() {

							@Override
							public ChartHisBean mapRow(Cursor cursor, int index) {
								ChartHisBean notice = new ChartHisBean();
								notice.setId(cursor.getString(cursor
										.getColumnIndex("_id")));
								notice.setContent(cursor.getString(cursor
										.getColumnIndex("content")));
								notice.setFrom(cursor.getString(cursor
										.getColumnIndex("msg_from")));
								notice.setNoticeTime(cursor.getString(cursor
										.getColumnIndex("msg_time")));
								notice.setNoticeType(cursor.getInt(cursor
										.getColumnIndex("is_group")));
								notice.setMsg_Category(cursor.getString(cursor.getColumnIndex("Msg_Category")));
								return notice;
							}
						},
						"select m.[_id],m.[content],m.[msg_time],m.[msg_from],m.[is_group],m.[Msg_Category] from im_msg_his  m join (select msg_from,max(msg_time) as time from im_msg_his group by msg_from) as tem  on  tem.time=m.msg_time and tem.msg_from=m.msg_from ",
						new String[]{});
		L.d("List.size() = " + list.size());
		st = SQLiteTemplate.getInstance(manager, false);
		for (ChartHisBean b : list) {
			int count = st
					.getCount(
							"select _id from im_notice where status=? and notice_from=? ",
							new String[] { "" + Notice.UNREAD,
									 b.getFrom() });
			L.d(b.getFrom() + "的未读消息总数为" + count);
			b.setNoticeSum(count);
		}
		return list;
	}
	
	/**
	 * 判断是否有时间相同的聊天记录
	 */
	
	public boolean IsTimeEquals(String time){
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st.isExistsByField("im_msg_his", "msg_time", time);
	}
	
	public void Canaell(){
		messageManager = null;
		manager.Canaell();
		
	}

	
	
}
