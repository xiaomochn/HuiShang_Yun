package com.huishangyun.LightApp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.MainClueActivity;
import com.huishangyun.Channel.Clues.MainClueOrdinaryEntryActivity;
import com.huishangyun.Channel.Competing.CompetingMainActivity;
import com.huishangyun.Channel.Customers.CustomerMainActivity;
import com.huishangyun.Channel.Display.DisplayMainActivity;
import com.huishangyun.Channel.Opport.MainOpportActivity;
import com.huishangyun.Channel.Opport.MainOpportOrdinaryEntryActivity;
import com.huishangyun.Channel.Orders.OrderMainActivity;
import com.huishangyun.Channel.Plan.MainPlanActivity;
import com.huishangyun.Channel.Plan.MainPlanOrdinaryEntryActivity;
import com.huishangyun.Channel.Task.TaskMainActivity;
import com.huishangyun.Channel.Visit.VisitMainActivity;
import com.huishangyun.Channel.stock.StockMainActivity;
import com.huishangyun.Channel.xiaoliang.XiaoLiang;
import com.huishangyun.Channel.xiaoliang.XiaoShouPlan;
import com.huishangyun.Office.Approval.MainApproval;
import com.huishangyun.Office.Askforleave.MainAskForLeaveActivity;
import com.huishangyun.Office.Askforleave.MainAskForLeaveOrdinaryEntryActivity;
import com.huishangyun.Office.Attendance.MainAttendanceActivity;
import com.huishangyun.Office.Businesstrip.MainBusinesTrip;
import com.huishangyun.Office.Businesstrip.MainOrdinaryEntryActivity;
import com.huishangyun.Office.Information.MainInformation;
import com.huishangyun.Office.Location.ManagersLocation;
import com.huishangyun.Office.Location.Map;
import com.huishangyun.Office.Markman.MarkMainActivity;
import com.huishangyun.Office.Summary.MainSummaryActivity;
import com.huishangyun.Office.Summary.MainSummaryOrdinaryEntryActivity;
import com.huishangyun.Office.WeiGuan.MainOnlookersActivivty;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.model.Constant;
import com.huishangyun.model.SubAppList;
import com.huishangyun.yun.R;

/**
 * 动态子菜单帮助类
 * @author forong
 *
 */
public class SubAppUtils {
	private static SubAppUtils subAppUtils = null;
	private static final String CRM_NAME = "Crm";
	private static final String OA_NAME = "OA";
	private static final String ERP_NAME = "Erp";
	
	public synchronized static SubAppUtils getInstance() {
		if (subAppUtils == null) {
			subAppUtils = new SubAppUtils();
		}
		return subAppUtils;
	}
	
	private SubAppUtils() {
		
	}
	
	/**
	 * 获取Crm模块子菜单
	 * @return
	 */
	public List<SubAppList> getCrmAppLists() {
		List<SubAppList> crmAppLists = new ArrayList<SubAppList>();
		try {
			List<SubAppList> mList = JsonUtil.fromJson(MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_SUBAPP_LIST, ""),
					new TypeToken<List<SubAppList>>(){}.getType());
			for (SubAppList subAppList : mList) {
				if (subAppList.getSubCode().equals(CRM_NAME)) {
					crmAppLists.add(subAppList);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return crmAppLists;
	}
	
	/**
	 * 获取OA子菜单
	 * @return
	 */
	public List<SubAppList> getOAAppLists() {
		List<SubAppList> oaAppLists = new ArrayList<SubAppList>();
		try {
			List<SubAppList> mList = JsonUtil.fromJson(MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_SUBAPP_LIST, ""),
					new TypeToken<List<SubAppList>>(){}.getType());
			for (SubAppList subAppList : mList) {
				if (subAppList.getSubCode().equals(OA_NAME)) {
					oaAppLists.add(subAppList);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return oaAppLists;
	}
	
	/**
	 * 获取Erp子菜单
	 * @return
	 */
	public List<SubAppList> getErpAppLists() {
		List<SubAppList> erpAppLists = new ArrayList<SubAppList>();
		try {
			List<SubAppList> mList = JsonUtil.fromJson(MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_SUBAPP_LIST, ""),
					new TypeToken<List<SubAppList>>(){}.getType());
			for (SubAppList subAppList : mList) {
				if (subAppList.getSubCode().equals(ERP_NAME)) {
					erpAppLists.add(subAppList);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return erpAppLists;
	}
	
	/**
	 * 获取图标
	 * @param code
	 * @return
	 */
	public int getImageResource(String code) {
		if (code.equals(SubAppCode.MemberVisitPlan)) {
			return R.drawable.channel_plan;
		} else if (code.equals(SubAppCode.MemberVisit)) {
			return R.drawable.channel_visit;
		} else if (code.equals(SubAppCode.MemberDisplay)) {
			return R.drawable.channel_display;
		} else if (code.equals(SubAppCode.MemberCompetition)) {
			return R.drawable.channel_summary;
		} else if (code.equals(SubAppCode.MemberStock)) {
			return R.drawable.channel_inventory;
		} else if (code.equals(SubAppCode.Orders)) {
			return R.drawable.channel_order;
		} else if (code.equals(SubAppCode.SalesAct)) {
			return R.drawable.channel_sales;
		} else if (code.equals(SubAppCode.SalesPlan)) {
			return R.drawable.channel_salesplanning;
		} else if (code.equals(SubAppCode.Member)) {
			return R.drawable.channel_customer;
		} else if (code.equals(SubAppCode.Clue)) {
			return R.drawable.channel_clue;
		} else if (code.equals(SubAppCode.Business)) {
			return R.drawable.channel_opportunity;
		} else if (code.equals(SubAppCode.Task)) {
			return R.drawable.channel_task;
		} else if (code.equals(SubAppCode.Attendance)) {
			return R.drawable.office_attendance;
		} else if (code.equals(SubAppCode.Travel)) {
			return R.drawable.office_business;
		} else if (code.equals(SubAppCode.Leave)) {
			return R.drawable.office_leave;
		} else if (code.equals(SubAppCode.Article)) {
			return R.drawable.office_door;
		} else if (code.equals(SubAppCode.WorkLog)) {
			return R.drawable.office_daily;
		} else if (code.equals(SubAppCode.WorkFlow)) {
			return R.drawable.office_approval;
		} else if (code.equals(SubAppCode.Action)) {
			return R.drawable.office_onlooker;
		} else if (code.equals(SubAppCode.Location)) {
			return R.drawable.office_position;
		} else if (code.equals(SubAppCode.Call)) {
			return R.drawable.channel_makeapp;
		}
		return 0;
	}
	
	/**
	 * 启动相应页面
	 * @param context
	 * @param code
	 */
	public void startActivity(Context context, String code) {
		Intent startIntent = null;
		if (code.equals(SubAppCode.MemberVisitPlan)) {
			if (MyApplication.preferences.getString(Constant.HUISHANG_TYPE, "-1").equals("1")) {
				startIntent = new Intent(context, MainPlanOrdinaryEntryActivity.class);
			}else {
				startIntent = new Intent(context, MainPlanActivity.class);
			}
			//return R.drawable.channel_plan;
		} else if (code.equals(SubAppCode.MemberVisit)) {
			startIntent = new Intent(context, VisitMainActivity.class);
			startIntent.putExtra("falge", "qudao");
			startIntent.putExtra("Member_ID", "0");
			//return R.drawable.channel_visit;
		} else if (code.equals(SubAppCode.MemberDisplay)) {
			startIntent = new Intent(context, DisplayMainActivity.class);
			startIntent.putExtra("falge", "qudao");
			startIntent.putExtra("Member_ID", "0");
			//return R.drawable.channel_display;
		} else if (code.equals(SubAppCode.MemberCompetition)) {
			startIntent = new Intent(context, CompetingMainActivity.class);
			startIntent.putExtra("falge", "qudao");
			startIntent.putExtra("Member_ID", "0");
			//return R.drawable.channel_summary;
		} else if (code.equals(SubAppCode.MemberStock)) {
			startIntent = new Intent(context, StockMainActivity.class);
			//return R.drawable.channel_inventory;
		} else if (code.equals(SubAppCode.Orders)) {
			startIntent = new Intent(context, OrderMainActivity.class);
			//return R.drawable.channel_order;
		} else if (code.equals(SubAppCode.SalesAct)) {
			startIntent = new Intent(context, XiaoLiang.class);
			//return R.drawable.channel_sales;
		} else if (code.equals(SubAppCode.SalesPlan)) {
			startIntent = new Intent(context, XiaoShouPlan.class);
			//return R.drawable.channel_salesplanning;
		} else if (code.equals(SubAppCode.Member)) {
			startIntent = new Intent(context, CustomerMainActivity.class);
			//return R.drawable.channel_customer;
		} else if (code.equals(SubAppCode.Clue)) {
			//startIntent = new Intent(context, Clue.class);
			if (MyApplication.preferences.getString(Constant.HUISHANG_TYPE, "-1").equals("1")) {
				startIntent = new Intent(context, MainClueOrdinaryEntryActivity.class);
			}else {
				startIntent = new Intent(context, MainClueActivity.class);
			}
			//return R.drawable.channel_clue;
		} else if (code.equals(SubAppCode.Business)) {
			if (MyApplication.preferences.getString(Constant.HUISHANG_TYPE, "-1").equals("1")) {
				startIntent = new Intent(context, MainOpportOrdinaryEntryActivity.class);
				startIntent.putExtra("falge", "qudao");
				startIntent.putExtra("Member_ID", "0");
			}else {
				startIntent = new Intent(context, MainOpportActivity.class);
			}
		} else if (code.equals(SubAppCode.Task)) {
			startIntent = new Intent(context, TaskMainActivity.class);
			startIntent.putExtra("Member_ID", "-1");
			startIntent.putExtra("Member_Name", "");
			//return R.drawable.channel_task;
		} else if (code.equals(SubAppCode.Attendance)) {
			startIntent = new Intent(context, MainAttendanceActivity.class);
			//return R.drawable.office_attendance;
		} else if (code.equals(SubAppCode.Travel)) {
			if (MyApplication.preferences.getString(Constant.HUISHANG_TYPE, "-1").equals("1")) {
				startIntent = new Intent(context,MainOrdinaryEntryActivity.class);	
			}else {
				startIntent = new Intent(context,MainBusinesTrip.class);
			}
			//return R.drawable.office_business;
		} else if (code.equals(SubAppCode.Leave)) {
			if (MyApplication.preferences.getString(Constant.HUISHANG_TYPE, "-1").equals("1")) {
				startIntent = new Intent(context,MainAskForLeaveOrdinaryEntryActivity.class);	
			}else {
				startIntent = new Intent(context,MainAskForLeaveActivity.class);
			}
			//return R.drawable.office_leave;
		} else if (code.equals(SubAppCode.Article)) {
			startIntent = new Intent(context, MainInformation.class);
			//return R.drawable.office_door;
		} else if (code.equals(SubAppCode.WorkLog)) {
			if (MyApplication.preferences.getString(Constant.HUISHANG_TYPE, "-1").equals("1")) {
				startIntent = new Intent(context,MainSummaryOrdinaryEntryActivity.class);	
			}else {
				startIntent = new Intent(context,MainSummaryActivity.class);
			}
			//return R.drawable.office_daily;
		} else if (code.equals(SubAppCode.WorkFlow)) {
			startIntent = new Intent(context, MainApproval.class);
			//return R.drawable.office_approval;
		} else if (code.equals(SubAppCode.Action)) {
			startIntent = new Intent(context, MainOnlookersActivivty.class);
			//return R.drawable.office_onlooker;
		} else if (code.equals(SubAppCode.Location)) {
			startIntent = new Intent(context, ManagersLocation.class);
			//return R.drawable.office_position;
		} else if (code.equals(SubAppCode.Call)) {
			startIntent = new Intent(context, MarkMainActivity.class);
		}
		//启动相应页面
		if (startIntent != null) {
			context.startActivity(startIntent);
		}
	}
	
}
