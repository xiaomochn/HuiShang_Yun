package com.huishangyun.model;

/**
 * 保存调用webservice所用方法的类
 * @author Pan
 * @version 亿企云APP V1.0 2014-08-19 09:15
 */
public class Methods {
	
	/**
	 * 获取线索列表
	 */
	public static final String CLUE_LIST = "GetClueList";
	
	/**
	 * 新增线索
	 */
	public static final String CREATE_CLUE = "SetClue";
	
	/**
	 * 线索详情
	 */
	public static final String DETAIL_CLUE = "GetClue";
	
	/**
	 * 获取商机列表
	 */
	public static final String SUPPLY_LIST = "GetBusinessList";
	
	/**
	 * 创建商机
	 */	
	public static final String SUPPLY_CREATE = "SetBusiness";
	
	/**
	 * 获取商机详情
	 */
	public static final String SUPPLY_DETAILS = "GetBusiness";
	
	/**
	 * 商机编辑
	 */
	public static final String SUPPLY_EDIT = "SetBusiness";
	
	/**
	 * 商机更多阶段-金额-时间
	 */
	public static final String SUPPLY_STAGE = "GetBusinessStageList";
	
	/**
	 * 计划列表
	 */
	public static final String PLAN_LIST = "GetVisitPlanList";
	
	/**
	 * 创建计划
	 */
	public static final String PLAN_CREATE = "SetVisitPlan";
	
	/**
	 * 创建计划详情
	 */
	public static final String PLAN_CREATE_DETAIL = "SetVisitPlanDetal";
	
	/**
	 * 计划详情
	 */
	public static final String PLAN_DETAIL = "GetVisitPlan";
	
	/**
	 * 计划详情明细
	 */
	public static final String PLAN_DETAILS = "GetVisitPlanDetail";
	/**
	 * 计划客户删除
	 */
	public static final String PLAN_DELETE = "SetVisitPlanDetalDelete";
	
	/**
	 * 计划详情时间查询
	 */
	public static final String PLAN_DATE = "GetVisitPlanDetailListDate";
	
	/**
	 * 计划拜访客户查询
	 */
	public static final String PLAN_CUSTOMERS = "GetVisitPlanDetailList";
	
	/**
	 * 获取任务列表
	 */
	public static final String TASK_LIST = "GetTaskList";
	
	/**
	 * 添加任务
	 */
	public static final String TASK_ADD = "SetTask";
	
	/**
	 * 获取拜访列表
	 */
	public static final String VISIT_LIST="GetVisitList";
	/**
	 * 创建拜访
	 */
	public static final String VISIT_CREATE="SetVisit";
	
	/**
	 * 成列列表
	 */
	public static final String DISPLAY_LIST="GetDisplayList";
	
	/**
	 * 陈列保存
	 */
	public static final String DISPLAY_NEW="SetDisplay";
	
	/**
	 * 库存上报
	 */
	public static final String STOCK_CHECK_IN="SetMemberStock";
	
	/**
	 * 库存记录
	 */
	public static final String STOCK_DETAIL="GetMemberStockDetailList";
	
	
	/**
	 * 订单接口 
	 */	
	public static final String ORDER_GOODS_LIST = "GetProductList";//产品列表
	public static final String ORDER_CLASSLIST = "GetClassList";//产品分类列表
	public static final String ORDER_GOODS_DATAILS = "GetProduct"; //产品详情
	
	public static final String ORDER_SHOUCANG_LIST = "GetProductFavList";//收藏列表
	public static final String ORDER_SHOUCANG_ADD = "SetProductFav";//添加到收藏 
	public static final String ORDER_SHOUCANG_DEL = "SetProductFavDelete";//删除收藏

	public static final String ORDER_DINGDAN_LIST = "GetOrderList";//订单列表
	public static final String ORDER_DINGDAN_DATAILS = "GetOrder";//订单详情
	public static final String ORDER_DINGDAN_SET = "SetOrder";//创建订单
	public static final String ORDER_DINGDAN_DEL = "SetOrderOperate";//创建作废、删除、复制共用
	public static final String ORDER_DINGDAN_GOODS = "GetOrderDetailList";//获取订单产品
	public static final String GetOrdersLog = "GetOrdersLog";//订单日志
	
	public static final String ORDER_ADDRESS_ADD = "SetMemberAdress";//收货地址添加
	public static final String ORDER_ADDRESS_SHOW = "GetMemberAdressList";//收货地址显示
	public static final String ORDER_ADDRESS_DEL = "SetMemberAdressDelete";//收货地址删除
	
	/**
	 * 获取竞品列表
	 */
	public static final String COMPETING_LIST="GetCompetitionList";
	
	/**
	 * 新增竞品
	 */
	public static final String COMPETING_NEW="SetCompetition";
	/**
	 * 获取部门列表
	 */
	public static final String UPDATA_DEPARTMENT = "GetDepartmentList";
	
	/**
	 * 获取人员列表
	 */
	public static final String UPDATA_MANAGER = "GetManagerList";
	
	/**
	 * 获取客户分组
	 */
	public static final String UPDATA_MEMBERGROUP = "GetMemberGroupList";
	
	/**
	 * 获取客户
	 */
	public static final String UPDATA_MEMBER = "GetMemberList";
	public static final String SET_MEMBER = "SetMember";
	
	
	public static final String GET_MEMBERLINK_LIST = "GetMemberLinkList";
	
	/**
	 * 获取省市县
	 */
	public static final String UPDATA_SITE = "GetSiteDateList";
	
	public static final String TASK_GETTASK = "GetTask";
	
	public static final String UPDATA_ENUM = "GetSysEumList";
	
	public static final String TASK_GETTASKPROG = "GetTaskProgressList";
	
	public static final String TASK_SETTASKPROG = "SetTaskProgress";
	
	public static final String TASK_DELETE = "SetTaskDelete";
	
	public static final String TASK_GETMENU = "GetTaskStageList";
	
	public static final String UPDATA_MEMBERLINK = "GetMemberLinkList";
	
	public static final String UPDATA_GOODSLISTS = "GetProductList";
	
	/**
	 * 客户等级
	 */
	public static final String CUSTOMER_GETLEVEL = "GetMemberLevelList";
	
	public static final String GET_MEMBER = "GetMember";
	
	public static final String SET_MEMBER_DELETE = "SetMemberDelete";
	
	public static final String SET_TASK_OPER = "SetTaskOperate";
	
	
	
	
	
	/***************office【办公】接口【Start】******************/
	/**
	 * 办公首页数据
	 */
	public static final String GET_OFFICE_DATA = "GetPhotoList";
	/**
	 * 设置考勤主页数据
	 */
	public static final String SET_ATTENDANCE_LIST = "SetAttendance";
	
	/**
	 * 获得考勤数据
	 */
	public static final String GET_ATTENDANCE_LIST = "GetAttendanceList";
	/**
	 * 出差列表接口
	 */
	public static final String GET_BUSINESSTRIP_LIST = "GetTravelList";
	
	/**
	 * 出差新增
	 */
	public static final String SET_BUSINESSTRIP = "SetTravel";
	
	/**
	 * 提交出差详情出发提交数据
	 */
	public static final String SET_BUSINESSTRIP_DETIAL_SETOUT = "SetTravelStart";
	/**
	 * 提交出差详情到达提交数据
	 */
	public static final String SET_BUSINESSTRIP_DETIAL_ARRIVE = "SetTravelArrive";
	/**
	 * 出差取消
	 */
	public static final String BUSINESSTRIP_CANCEL = "TravelOperate";
	/**
	 * 获得出差人员id列表
	 */
	public static final String GET_BUSINESSTRIP_MANGAGER_LIST = "GetTravelManagerList";
	
	/**
	 * 小结上报
	 */
	public static final String SUMMARY_REPORT = "SetWorkLog";
	/**
	 * 小结列表
	 */
	public static final String GET_SUMMARY_LIST = "GetWorkLogList";
	
	/**
	 * 提交请假列表
	 */
	public static final String SET_LEAVE_DATA = "SetLeave";
	
	/**
	 * 获得请假列表
	 */
	public static final String GET_LEAVE_LIST = "GetLeaveList";
	/**
	 * 获得请假列表
	 */
	public static final String GET_LEAVE_MANGAGER_LIST = "GetLeaveManagerList";
	
	/**
	 * 发送围观数据
	 */
	public static final String SET_ONLOOKS_CREATE = "SetAction";
	
	/**
	 * 获取围观主题
	 */
	public static final String GET_ONLOOKS_THEME = "GetActionTopicList";
	
	/**
	 * 围观列表
	 */
	public static final String GET_ONLOOKS_LIST = "GetActionList";
	
	/**
	 * 点赞接口
	 */
	public static final String SET_ONLOOKS_PRAISE = "SetActionGood";
	
	/**
	 * 详情数据接口
	 */
	public static final String GET_ONLOOKS_DETAILS = "GetAction";
	
	/**
	 * 详情评论列表接口
	 */
	public static final String GET_ONLOOKS_DETAILS_COMMENT = "GetActionCommentList";
	/**
	 * 发表评论接口
	 */
	public static final String SET_ONLOOKS_COMMENT = "SetActionComment";
	
	/**
	 * 删除自己评论接口
	 */
	public static final String ONLOOKS_DELETE_MYSELF_COMMENT = "DelActionComment";
	
	/**
	 * 删除自己的围观
	 */
	public static final String ONLOOKS_DELETE_MYSELF = "DelAction";
	
	/**
	 * 我的评论列表接口
	 */
	public static final String GET_ONLOOKS_MY_COMMENT = "GetActionCommentListBySelf";
	
	/**
	 * 获取点赞列表接口
	 */
	public static final String GET_ONLOOKS_PRAISE_LIST = "GetActionGoodList";
	
	/**
	 * 发送地理位置信息到服务器
	 */
	public static final String SET_LOCATION_INFO = "SetLocation";
	
	/**
	 * 获取最近一次位置信息
	 */
	public static final String GET_LOCATION_INFO = "GetLastLocationList";
	
	/***************office【办公】接口【End】******************/
	
	public static final String GET_ROOM_LIST = "GetRoomList";
	
	//public static final String GET_COMPANY_ID = "GetCompany";
	public static final String GET_COMPANY_ID = "GetCompany";
	
	public static final String MANAGER_CHEK_LOGIN = "ManagerCheckLogin";
	
	public static final String GET_MANAGER_INFO = "GetManager";
	
	public static final String SET_MANAGER_INFO = "SetManager";
	
	public static final String SET_MESSAGE_STATUS = "SetMessageStatus";
	
	public static final String GET_SCHEDULIST = "GetAttendanceScheduleList";
	
	/**
	 * 2014.12.10 游建诺
	 * start
	 * */
	//上报销售量
	public  static final String SET_XIAOSHOULIANG = "SetMemberSales";
	
	/**end*/
	
	public static final String SET_XIAOSHOUPLAN = "SetMemberSalesPlan";
	
	public static final String DELETE_OFFMESSAGE = "OfflineDelete";
	
	public static final String GET_SERVICE = "GetServerList";
	
	public static final String GET_SERVICE_MENU = "GetServerMenuList";
	
	public static final String SET_SMS = "SetSms";
	
	public static final String GET_SMS = "GetSms";
	
	public static final String SET_COMPANY = "SetCompany";
	
	public static final String GET_MANAGER_PASSWORD = "GetManagerPass";
	
	public static final String SET_MANAGER_PASSWORD = "SetManagerPass";
	
	public static final String GET_GROUP_LIST = "GetGroupList";
	
	public static final String GET_GROUP_USER_LIST = "GetGroupUserList";
	
	public static final String GetProductUnitList = "GetProductUnitList";
	
	public static final String GetProductPriceList = "GetProductPriceList";
	
	/**
	 * 发送系统命令
	 */
	public static final String SEND_SYS_MSG = "SendSysMsg";

	/**
	 * 获取历史轨迹
	 */
	public static final String HUISHANG_GetLocationList = "GetLocationList";

	/**
	 * 获取广告图
	 */
	public static final String HUISHANG_GETAD_LIST = "GetAdList";
	
	public static final String HUISHANG_GET_CALLCALENDARLIST = "GetCallCalendarList";

	public static final String HUISHANG_GET_CALLLIST = "GetCallList";

	public static final String HUISHANG_GET_CALLCOMPANYLIST = "GetCallCompanyList";

	public static final String HUISHNAG_SET_CALL = "SetCall";

	public static final String HUISHANG_SET_CALL_STATUS = "SetCallStatus";

	/**
	 * 获取标签主题
	 */
	public static final String GET_VISIT_TAGS = "GetVisitTagList";
	
}
