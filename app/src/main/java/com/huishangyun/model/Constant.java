package com.huishangyun.model;

import android.os.Environment;

public class Constant {
	/**
	 * 所有的action的监听的必须要以"ACTION_"开头
	 * 
	 */
	/**
	 * 花名册有删除的ACTION和KEY
	 */
	public static final String ROSTER_DELETED = "roster.deleted";
	public static final String ROSTER_DELETED_KEY = "roster.deleted.key";

	/**
	 * 花名册有更新的ACTION和KEY
	 */
	public static final String ROSTER_UPDATED = "roster.updated";
	public static final String ROSTER_UPDATED_KEY = "roster.updated.key";

	/**
	 * 花名册有增加的ACTION和KEY
	 */
	public static final String ROSTER_ADDED = "roster.added";
	public static final String ROSTER_ADDED_KEY = "roster.added.key";

	/**
	 * 花名册中成员状态有改变的ACTION和KEY
	 */
	public static final String ROSTER_PRESENCE_CHANGED = "roster.presence.changed";
	public static final String ROSTER_PRESENCE_CHANGED_KEY = "roster.presence.changed.key";

	/**
	 * 收到好友邀请请求
	 */
	public static final String ROSTER_SUBSCRIPTION = "roster.subscribe";
	public static final String ROSTER_SUB_FROM = "roster.subscribe.from";
	public static final String NOTICE_ID = "notice.id";

	public static final String NEW_MESSAGE_ACTION = "roster.newmessage";

	/**
	 * 我的消息
	 */
	public static final String MY_NEWS = "my.news";
	public static final String MY_NEWS_DATE = "my.news.date";

	/**
	 * 服务器的配置
	 */
	public static final String LOGIN_SET = "yqy_login_set";// 登录设置
	public static final String USERNAME = "username";// 账户
	public static final String PASSWORD = "password";// 密码
	public static final String XMPP_HOST = "xmpp_host";// 地址
	public static final String XMPP_PORT = "xmpp_port";// 端口
	public static final String XMPP_SEIVICE_NAME = "xmpp_service_name";// 服务名
	public static final String IS_AUTOLOGIN = "isAutoLogin";// 是否自动登录
	public static final String IS_NOVISIBLE = "isNovisible";// 是否隐身
	public static final String IS_REMEMBER = "isRemember";// 是否记住账户密码
	public static final String IS_FIRSTSTART = "isFirstStart";// 是否首次启动
	public static final String XMPP_LOGIN_NAME = "xmpp_login_name";
	public static final String XMPP_MY_REAlNAME = "xmpp_my_RealName";
	public static final String XMPP_LOGIN_PASSWORD = "xmpp_login_password";
	/**
	 * 登录提示
	 */
	public static final int LOGIN_SECCESS = 0;// 成功
	public static final int HAS_NEW_VERSION = 1;// 发现新版本
	public static final int IS_NEW_VERSION = 2;// 当前版本为最新
	public static final int LOGIN_ERROR_ACCOUNT_PASS = 3;// 账号或者密码错误
	public static final int SERVER_UNAVAILABLE = 4;// 无法连接到服务器
	public static final int LOGIN_ERROR = 5;// 连接失败

	public static final String XMPP_CONNECTION_CLOSED = "xmpp_connection_closed";// 连接中断

	public static final String LOGIN = "login"; // 登录
	public static final String RELOGIN = "relogin"; // 重新登录

	/**
	 * 好友列表 组名
	 */
	public static final String ALL_FRIEND = "所有好友";// 所有好友
	public static final String NO_GROUP_FRIEND = "未分组好友";// 所有好友
	/**
	 * 系统消息
	 */
	public static final String ACTION_SYS_MSG = "action_sys_msg";// 消息类型关键字
	public static final String MSG_TYPE = "broadcast";// 消息类型关键字
	public static final String SYS_MSG = "sysMsg";// 系统消息关键字
	public static final String SYS_MSG_DIS = "系统消息";// 系统消息
	public static final String ADD_FRIEND_QEQUEST = "好友请求";// 系统消息关键字
	/**
	 * 请求某个操作返回的状态值
	 */
	public static final int SUCCESS = 0;// 存在
	public static final int FAIL = 1;// 不存在
	public static final int UNKNOWERROR = 2;// 出现莫名的错误.
	public static final int NETWORKERROR = 3;// 网络错误
	/***
	 * 企业通讯录根据用户ｉｄ和用户名去查找人员中的请求ｘｍｌ是否包含自组织
	 */
	public static final int containsZz = 0;
	/***
	 * 创建请求分组联系人列表xml分页参数
	 */
	public static final String currentpage = "1";// 当前第几页
	public static final String pagesize = "1000";// 当前页的条数

	/***
	 * 创建请求xml操作类型
	 */
	public static final String add = "00";// 增加
	public static final String rename = "01";// 增加
	public static final String remove = "02";// 增加

	/**
	 * 重连接
	 */
	/**
	 * 重连接状态acttion
	 * 
	 */
	public static final String ACTION_RECONNECT_STATE = "action_reconnect_state";
	/**
	 * 描述冲连接状态的关机子，寄放的intent的关键字
	 */
	public static final String RECONNECT_STATE = "reconnect_state";
	/**
	 * 描述冲连接，
	 */
	public static final boolean RECONNECT_STATE_SUCCESS = true;
	public static final boolean RECONNECT_STATE_FAIL = false;
	/**
	 * 是否在线的SharedPreferences名称
	 */
	public static final String PREFENCE_USER_STATE = "prefence_user_state";
	public static final String IS_ONLINE = "is_online";
	/**
	 * 精确到毫秒
	 */
	public static final String MS_FORMART = "yyyy-MM-dd HH:mm:ss SSS";
	public static final String SERVER_NAME = "im.huishangyun.com";
	/**
	 * 是否储存联系人
	 */
	public static final String SAVE_CONTACT = "save_contact";
	
	public static final String HAVE_NEW_GROUP = "have_new_group";
	/**
	 * 图片保存路径
	 */
	public static final String SAVE_IMG_PATH = Environment.getExternalStorageDirectory().toString()+"/HSY_Yun/img";
	/**
	 * 语音保存路径
	 */
	public static final String SAVE_AUTO_PATH = Environment.getExternalStorageDirectory().toString()+"/HSY_Yun/auto";
	
	public static final String SAVE_DATABASE_PATH = Environment.getExternalStorageDirectory().toString()+"/HSY_Yun/database/";
	
	public static final int SERVICE_TO_FOREGROUND = 201;
	/**
	 * 是否在后台运行
	 */
	public static final String FOREGROUND_ISBACK = "is_foregrund";
	/**
	 * 变前台Service,并发出通知
	 */
	public static final String START_NOTIF = "roster.start.notiy";
	/**
	 * 关闭Activity的广播
	 */
	public static final String FINISH_ACTIVITY = "roster.finish.activity";
	/**
	 * UI界面更新的acttion	
	 */
	public static final String UPDATA_UI = "roster.updata.ui";
	/**
	 * 描述是否更新界面
	 */
	public static final String UPDATA_UI_NAME = "reconnect_updata";
	
	public static final boolean UPDATA_UI_TRUE = true;
	public static final boolean UPDATA_UI_FALSE = false;
	
	public static final String UPDATA_CONTACT_TYPE = "updata_contact_type";
	/**
	 * 保存用户信息
	 */
	public static final String CHANGE_NAME = "change_name";
	public static final String CHANGE_BIAOQIAN = "change_biaoqian";
	public static final String CHANGE_PHONE = "change_phone";
	public static final String CHANGE_EMAIL = "change_email";
	public static final String CHANGE_BEIZHU = "change_beizhu";
	
	
	/**
	 * 用户被挤下线的广播
	 */
	public static final String EXTRUSION_LINE = "roster.action.Extrusionline";
	/**
	 * 服务器被关闭的广播
	 */
	public static final String SERVERISDOWN = "roster.action.Serverdown";
	
	/**
	 * 保存部门ID
	 */
	public static final String HUISHANG_DEPARTMENT_ID = "DepartmentID";
	
	/**
	 * 保存部门名称
	 */
	public static final String HUISHANG_DEPARTMENT_NAME = "DepartmentName";
	
	/**
	 * 保存权限
	 */
	public static final String HUISHANG_TYPE = "Save_type";
	
	/**
	 * 保存UID
	 */
	public static final String HUISHANGYUN_UID = "huishangyun_uid";
	
	public static final String DELETE_ADDRESS_ACTION = "huishang.delete.address";
	
	public static final String HUISHANG_RE_ACTION = "huishang.re.action";
	
	public static final String HUISHANG_OK_ACTION = "huishang.ok.action";
	
	
	
	
	public static final String HUISHANG_MESSAGE = "huishang_message_action";
	
	public static final String HUISHANG_SCREEN_SHOW = "huishang_screen_show";
	
	public static final String HUISHANG_SCREEN_ACTION = "huishang.screen.action";
	
	public static final String HUISHANG_SERVICE_NAME = "huishang_servicce_name";
	
	public static final String HUISHANG_SCREEN_CANSHOW = "huishang_screen_canshow";
	
	public static final String HUISHANG_SCREEN_YIQIYUN = "YiQiYun_Android";
	
	public static final String HUISHANG_ISRESTART = "huishang_isrestart";
	
	public static final String HUISHANG_COMPANY_NAME = "huishang_conpany_name";
	
	public static final String HUISHANG_ISMAIN_TOP = "huishang_ismain_top";
	
	/**
	 * 启动页面是否显示
	 */
	public static final String HUISHANG_YINGDAO = "huishang_yingdao1.0.0";
	
	public static final String HUISHANG_APPLIST = "huishang_applist";
	
	public static final String HUISHANG_SERVICE = "Server";
	
	public static final String HUISHANG_CHANNEL = "Crm";
	
	public static final String HUISHANG_OFFICE = "OA";
	
	public static final String HUISHANG_ERP = "Erp";
	
	public static final String HUISHANG_HAVE_UPDATA = "huishang_have_updata";
	
	public static final String HUISHANG_CHEKNEW_URL = "huishang_cheknew_url";

	public static final String HUISHANG_WEBDOMAIN = "huishang_webdomain";
	public static final String HUISHANG_WFHOST = "huishang_wfhost";

	
	/**
	 * 位置信息通知
	 */
	public static final String HUISHANG_ACTION_LOCATION = "huishang.action.location";
	
	/**
	 * 系统消息服务号
	 */
	public static final String HUISHANG_SYSTEM_ID = "0_n_hssystem";
	
	/**
	 * 围观推送
	 */
	public static final String HUISHANG_ONLOOK_ID = "0_n_hsaction";

	/**
	 * 日报推送
	 */
	public static final String HUISHANG_WorkLog_ID = "0_n_hsworklog";
	
	/**
	 * 订单
	 */
	public static final String HUISHANG_ORDER_ID = "0_n_hsorders";
	
	/**
	 * 子菜单json
	 */
	public static final String HUISHANG_SUBAPP_LIST = "huishang_subapp_list";

    /**
     * 定位类型
     */
    public static final String HUISHANG_LOCATION_TYPE = "huishang_location_type";

    public static final String HUISHANG_LOCATION_START_DATA = "huishang_location_start_data";

    public static final String HUISHANG_LOCATION_END_DATA = "huishang_location_end_data";

    public static final String HUISHANG_LOCATION_WEEK = "huishang_location_week";

    public static final String HUISHANG_LOCATION_FERQUENCY = "huishang_location_ferquency";

    public static final String HUISHANG_LOCATION_TIMELIST = "huishang_location_timelist";

	public static final String HUISHANG_INDEX_IMGURL = "huishang_index_imgurl";

	public static final String HUISHANG_INDEX_GOURL = "huishang_index_gourl";
	public static final String HUISHANG_CHAT_URL = "huishang_chat_url";

	public static final String HUISHANG_SIGN_TIME = "huishang_sign_time";
	public static final String HUISHANG_CHIOCE_TAGS = "huishang_chioce_tags";

	public static final String HUISHANG_CHAT_URLS = "huishang_chat_urls";

	/**
	 * 城市数据库升级请在copy后的数据+1,程序将自动拷贝数据库到文件夹
	 */
	public static final String HUISHANG_CITYDB_COPY= "huishang_city_copy_1";

	/*
	* 图片服务器配置信息
	* */
	//图片服务器路径
	public static final String pathurl = "http://img.huishangyun.com/UploadFile/huishang/";
	//命名空间
	public static final String namespace = "http://img.huishangyun.com/";
	//接口地址
	public static final String url = "http://img.huishangyun.com/imgws.asmx";
	// SoapHeaders身份验证的方法名
	public static String soapheadername = "HSSoapHeader";
	public static String uesrid = "huishang";
	public static String password = "hs2015";

	/*
     * 数据接口服务器配置信息
     * */
	public static final String TAG = "DataUtil.java";
	public static final boolean isDebug = true;
	public static final String app_namespace = "http://huishangyun.com/";
	//新外网接口
	public static final String app_url = "http://is.huishangyun.com/app/AppWS.asmx";
	//private static final String app_url = "http://220.175.122.90:88/app/AppWS.asmx";
	// SoapHeaders身份验证的方法名
	public static String app_soapheadername = "HSSoapHeader";
	public static String app_uesrid = "huishang";
	public static String app_password = "hs2015";
	//图片名称
	public static String huishang_img = "/HSY_Yun/img/";
}
