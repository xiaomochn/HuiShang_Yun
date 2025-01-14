package com.huishangyun.manager;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.huishangyun.Util.StringUtil;
import com.huishangyun.db.SQLiteTemplate;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Notice;
import com.huishangyun.db.SQLiteTemplate.RowMapper;

public class NoticeManager {
	private static NoticeManager noticeManager = null;
	private static DBManager manager = null;

	private NoticeManager(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.LOGIN_SET, Context.MODE_PRIVATE);
		String databaseName = sharedPre.getString(Constant.XMPP_LOGIN_NAME, null);
		manager = DBManager.getInstance(context);
	}

	public static NoticeManager getInstance(Context context) {

		if (noticeManager == null) {
			noticeManager = new NoticeManager(context);
		}

		return noticeManager;
	}

	/**
	 * 
	 * 保存消息.
	 * 
	 * @param notice
	 * @author pan
	 * @update 2015-5-16 下午3:23:15
	 */
	public long saveNotice(Notice notice) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		if (StringUtil.notEmpty(notice.getTitle())) {
			contentValues.put("title", StringUtil.doEmpty(notice.getTitle()));
		}
		if (StringUtil.notEmpty(notice.getContent())) {
			contentValues.put("content",
					StringUtil.doEmpty(notice.getContent()));
		}
		if (StringUtil.notEmpty(notice.getTo())) {
			contentValues.put("notice_to", StringUtil.doEmpty(notice.getTo()));
		}
		if (StringUtil.notEmpty(notice.getFrom())) {
			contentValues.put("notice_from",
					StringUtil.doEmpty(notice.getFrom()));
		}
		contentValues.put("type", notice.getNoticeType());
		contentValues.put("status", notice.getStatus());
		contentValues.put("notice_time", notice.getNoticeTime());
		contentValues.put("Msg_Category", notice.getMsg_Category());
		return st.insert("im_notice", contentValues);
	}

	/**
	 * 
	 * 获取所有未读消息.
	 * 
	 * @return
	 * @author pan
	 * @update 2015-5-16 下午3:22:53
	 */
	public List<Notice> getUnReadNoticeList() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Notice> list = st.queryForList(new RowMapper<Notice>() {

			@Override
			public Notice mapRow(Cursor cursor, int index) {
				Notice notice = new Notice();
				notice.setId(cursor.getString(cursor.getColumnIndex("_id")));
				notice.setContent(cursor.getString(cursor
						.getColumnIndex("content")));
				notice.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				notice.setFrom(cursor.getString(cursor
						.getColumnIndex("notice_from")));
				notice.setTo(cursor.getString(cursor
						.getColumnIndex("notice_to")));
				notice.setNoticeType(cursor.getInt(cursor
						.getColumnIndex("type")));
				notice.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
				notice.setMsg_Category(cursor.getString(cursor.getColumnIndex("Msg_Category")));
				return notice;
			}

		}, "select * from im_notice where status=" + Notice.UNREAD + "", null);
		return list;
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
		st.updateById("im_notice", id, contentValues);
	}

	/**
	 * 
	 * 更新添加好友状态.
	 * 
	 * @param status
	 * @author pan
	 * @update 2015-5-16 下午3:22:44
	 */
	public void updateAddFriendStatus(String id, Integer status, String content) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("status", status);
		contentValues.put("content", content);
		st.updateById("im_notice", id, contentValues);
	}

	/**
	 * 
	 * 获取未读消息的条数.
	 * 
	 * @return
	 * @author pan
	 * @update 2015-5-16 下午6:22:03
	 */
	public Integer getUnReadNoticeCount() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st.getCount("select _id from im_notice where status=?",
				new String[] { "" + Notice.UNREAD });
	}

	/**
	 * 
	 * 更具主键获取消息.
	 * 
	 * @param id
	 * @author pan
	 * @update 2015-5-16 下午5:35:33
	 */
	public Notice getNoticeById(String id) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st.queryForObject(new RowMapper<Notice>() {

			@Override
			public Notice mapRow(Cursor cursor, int index) {
				Notice notice = new Notice();
				notice.setId(cursor.getString(cursor.getColumnIndex("_id")));
				notice.setContent(cursor.getString(cursor
						.getColumnIndex("content")));
				notice.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				notice.setFrom(cursor.getString(cursor
						.getColumnIndex("notice_from")));
				notice.setTo(cursor.getString(cursor
						.getColumnIndex("notice_to")));
				notice.setNoticeType(cursor.getInt(cursor
						.getColumnIndex("type")));
				notice.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
				notice.setMsg_Category(cursor.getString(cursor.getColumnIndex("Msg_Category")));
				return notice;
			}

		}, "select * from im_notice where _id=?", new String[] { id });
	}

	/**
	 * 
	 * 获取所有未读聊消息.(分类)1 好友添加 2系统 消息 3 聊天
	 * 
	 * @return
	 * @author pan
	 * @update 2015-5-16 下午3:22:53
	 */
	public List<Notice> getUnReadNoticeListByType(int type) {

		String sql;
		String[] str = new String[] { "" + Notice.UNREAD, "" + type };
		sql = "select * from im_notice where status=? and type=? order by notice_time desc";
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Notice> list = st.queryForList(new RowMapper<Notice>() {

			@Override
			public Notice mapRow(Cursor cursor, int index) {
				Notice notice = new Notice();
				notice.setId(cursor.getString(cursor.getColumnIndex("_id")));
				notice.setContent(cursor.getString(cursor
						.getColumnIndex("content")));
				notice.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				notice.setFrom(cursor.getString(cursor
						.getColumnIndex("notice_from")));
				notice.setTo(cursor.getString(cursor
						.getColumnIndex("notice_to")));
				notice.setNoticeType(cursor.getInt(cursor
						.getColumnIndex("type")));
				notice.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
				notice.setNoticeTime(cursor.getString(cursor
						.getColumnIndex("notice_time")));
				notice.setMsg_Category(cursor.getString(cursor.getColumnIndex("Msg_Category")));
				return notice;
			}
		}, sql, str);
		return list;
	}

	/**
	 * 
	 * 获取未读消息的条数（分类）.1 好友添加 2系统 消息 3 聊天
	 * 
	 * @return
	 * @author pan
	 * @update 2015-5-16 下午6:22:03
	 */
	public Integer getUnReadNoticeCountByType(int type) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st.getCount(
				"select _id from im_notice where status=? and type=?",
				new String[] { "" + Notice.UNREAD, "" + type });
	}

	/**
	 * 
	 * 获取来自某人未读消息的条数（分类）.1 好友添加 2系统 消息 3 聊天
	 * 
	 * @return
	 * @author pan
	 * @update 2015-7-5 下午1:59:53
	 */
	public Integer getUnReadNoticeCountByTypeAndFrom(int type, String from) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st
				.getCount(
						"select _id from im_notice where status=? and type=? and motice_from=?",
						new String[] { "" + Notice.UNREAD, "" + type, from });
	}

	/**
	 * 
	 * 更新某人所有通知状态.
	 * 
	 * @param status
	 * @author pan
	 * @update 2015-5-16 下午3:22:44
	 */
	public void updateStatusByFrom(String xfrom, Integer status) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues values = new ContentValues();
		values.put("status", status);
		st.update("im_notice", values, "notice_from=?", new String[] { ""
				+ xfrom });
	}

	/**
	 * 
	 * 分页获取所有聊消息.(分类)1 好友添加 2系统 消息 3 聊天 降序排列
	 * 
	 * @param isRead
	 *            0 已读 1 未读 2 全部
	 * @param type
	 *            2系统， 3聊天，1 好友添加
	 * @return
	 * @author pan
	 * @update 2015-7-6 下午3:22:53
	 */
	public List<Notice> getNoticeListByTypeAndPage(int type, int isRead,
			int pageNum, int pageSize) {
		int fromIndex = (pageNum - 1) * pageSize;
		StringBuilder sb = new StringBuilder();
		String[] str = null;
		sb.append("select * from im_notice where type=?");
		if (Notice.UNREAD == isRead || Notice.READ == isRead) {
			str = new String[] { "" + type, "" + isRead, "" + fromIndex,
					"" + pageSize };
			sb.append(" and status=? ");
		} else {
			str = new String[] { "" + type, "" + fromIndex, "" + pageSize };
		}
		sb.append(" order by notice_time desc limit ? , ? ");
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Notice> list = st.queryForList(new RowMapper<Notice>() {

			@Override
			public Notice mapRow(Cursor cursor, int index) {
				Notice notice = new Notice();
				notice.setId(cursor.getString(cursor.getColumnIndex("_id")));
				notice.setContent(cursor.getString(cursor
						.getColumnIndex("content")));
				notice.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				notice.setFrom(cursor.getString(cursor
						.getColumnIndex("notice_from")));
				notice.setTo(cursor.getString(cursor
						.getColumnIndex("notice_to")));
				notice.setNoticeType(cursor.getInt(cursor
						.getColumnIndex("type")));
				notice.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
				notice.setNoticeTime(cursor.getString(cursor
						.getColumnIndex("notice_time")));
				notice.setMsg_Category(cursor.getString(cursor.getColumnIndex("Msg_Category")));
				return notice;
			}
		}, sb.toString(), str);
		return list;
	}

	/**
	 * 
	 * 分页获取所有聊消息.(分类)1 好友添加 2系统 消息 3 聊天 降序排列
	 * 
	 * @param isRead
	 *            0 已读 1 未读 2 全部
	 * @return
	 * @author pan
	 * @update 2015-7-6 下午3:22:53
	 */
	public List<Notice> getNoticeListByTypeAndPage(int isRead) {

		StringBuilder sb = new StringBuilder();
		String[] str = null;
		sb.append("select * from im_notice where  type in(1,2)");
		if (Notice.UNREAD == isRead || Notice.READ == isRead) {
			str = new String[] { "" + isRead };
			sb.append(" and status=? ");
		}
		sb.append(" order by notice_time desc");
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<Notice> list = st.queryForList(new RowMapper<Notice>() {

			@Override
			public Notice mapRow(Cursor cursor, int index) {
				Notice notice = new Notice();
				notice.setId(cursor.getString(cursor.getColumnIndex("_id")));
				notice.setContent(cursor.getString(cursor
						.getColumnIndex("content")));
				notice.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				notice.setFrom(cursor.getString(cursor
						.getColumnIndex("notice_from")));
				notice.setTo(cursor.getString(cursor
						.getColumnIndex("notice_to")));
				notice.setNoticeType(cursor.getInt(cursor
						.getColumnIndex("type")));
				notice.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
				notice.setNoticeTime(cursor.getString(cursor
						.getColumnIndex("notice_time")));
				notice.setMsg_Category(cursor.getString(cursor.getColumnIndex("Msg_Category")));
				return notice;
			}
		}, sb.toString(), str);
		return list;
	}

	/**
	 * 根据id删除记录
	 * 
	 * @author Juan强
	 * @param noticeId
	 */
	public void delById(String noticeId) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		st.deleteById("im_notice", noticeId);
	}

	/**
	 * 
	 * 删除全部记录
	 * 
	 * @update 2013-4-15 下午6:33:19
	 */
	public void delAll() {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		st.execSQL("delete from im_notice");
	}

	/**
	 * 删除与某人的通知 author pan
	 * 
	 * @param fromUser
	 */
	public int delNoticeHisWithSb(String fromUser) {
		if (StringUtil.empty(fromUser)) {
			return 0;
		}
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		return st.deleteByCondition("im_notice", "notice_from=?",
				new String[] { "" + fromUser });
	}
	
	public void Canaell(){
		noticeManager = null;
	}
}
