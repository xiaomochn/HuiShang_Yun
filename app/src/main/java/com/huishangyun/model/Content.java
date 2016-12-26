package com.huishangyun.model;

public class Content {
	
	public static final String SPNAME = "baseinfo";
	public static final String IS_AUTO_LOGIN = "is_auto_login";//是否自动登陆
	public static final String IS_RMB_PSD = "is_remember_psd";//是否记住密码
	public static final String ACCOUNT = "account";//账号
	public static final String PASSWORD = "password";//密码
	public static final int REUQEST_CODE_OK_TAKEPHOTO = 119;//拍照成功requestCode
	public static final int REUQEST_CODE_OK_TAKEIMAGE = 120;//选图成功requestCode
	
	public static final String PATH_RECOED_IMG = "/HSY_Yun/img/";
	public static final String PATH_RECOED_MEDIA = "/HSY_Yun/media/";
	public static final int MSG_TYPE_TEXT = 1;//消息类型-文字
	public static final int MSG_TYPE_IMAGE = 2;//消息类型-图片
	public static final int MSG_TYPE_PICTURE = 4;//消息类型-图片
	public static final int MSG_TYPE_MEDIA = 3;//消息类型-录音
	
	
	//----------temp-----临时数据------------测试用-----------
	public static final String TEMP_SP_IMGCOUNT = "temp_imgcount"; 
	public static final String TEMP_SP_MEDIACOUNT = "temp_mediacount"; 

	public static final String COMPS_ID = "comp_id";
	public static final String COMPS_NAME = "comp_name";
	public static final String COMPS_IMGURL = "comp_imgurl";
	public static final String COMPS_DOMAIN = "comp_admindomain";
}
