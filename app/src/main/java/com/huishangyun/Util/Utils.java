package com.huishangyun.Util;

import com.huishangyun.model.Constant;

public class Utils {
	/**
	 * 根据jid获取用户名
	 * 
	 * @param jid
	 * @return
	 */
	public static String getJidToUsername(String jid) {
		return jid.split("@")[0];
	}

	/**
	 * @param username
	 * @return
	 */
	public static String getUserNameToJid(String username) {
		return username + "@" + Constant.SERVER_NAME;
	}

	public static String getUserMultiChatName(String UserMultiChatName) {
		return UserMultiChatName.split("/")[UserMultiChatName.split("/").length-1];
	}
	public static String getMultiChatName(String UserMultiChatName){
		L.d("------------getMultiChatName-------------"+UserMultiChatName.split("/")[0]);
		return UserMultiChatName.split("/")[0];
	}
}
