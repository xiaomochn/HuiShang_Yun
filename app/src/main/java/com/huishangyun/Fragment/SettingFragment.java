package com.huishangyun.Fragment;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Activity.ChangeUserInfo;
import com.huishangyun.Activity.LandActivity;
import com.huishangyun.Activity.SettingAbout;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.FileTools;
import com.huishangyun.Util.GetFileSizeUtil;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.Service;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.View.RoundAngleImageView;
import com.huishangyun.manager.AreaManager;
import com.huishangyun.manager.DepartmentManager;
import com.huishangyun.manager.EnumManager;
import com.huishangyun.manager.GroupManager;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.manager.OperationTime;
import com.huishangyun.manager.ProductManager;
import com.huishangyun.manager.ScheduleManager;
import com.huishangyun.manager.ServiceManager;
import com.huishangyun.manager.UnitPriceManager;
import com.huishangyun.model.AreaModel;
import com.huishangyun.model.Enum;
import com.huishangyun.register.NewPasswordActivity;
import com.huishangyun.service.HSChatService;
import com.huishangyun.service.LocationService;
import com.huishangyun.service.PhoneService;
import com.huishangyun.task.ChekUpdata;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ServiceMenu;
import com.huishangyun.model.ClassModel;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.Departments;
import com.huishangyun.model.GroupUser;
import com.huishangyun.model.IMGroup;
import com.huishangyun.model.Managers;
import com.huishangyun.model.MemberGroups;
import com.huishangyun.model.MemberLinks;
import com.huishangyun.model.Members;
import com.huishangyun.model.Methods;
import com.huishangyun.model.OperTime;
import com.huishangyun.model.Order_GoodsList;
import com.huishangyun.model.ProductPrice;
import com.huishangyun.model.ProductUnit;
import com.huishangyun.model.Schedule;
import com.huishangyun.yun.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SettingFragment extends BaseFragment implements
		OnCheckedChangeListener {
	private ImageView imageView;// 用户头像
	private TextView nameView;// 用户名称
	private LinearLayout userLayout;
	private LinearLayout about;// 关于
	private LinearLayout canaell;// 注销
	private LinearLayout updata;
	private LinearLayout exitbtn;// 退出
	private LinearLayout infoBtn;
	private LinearLayout chekNew;//检查更新
	private LinearLayout newPass;
	private LinearLayout fragment_setting_fx;
	private LinearLayout fragment_setting_cache;//清除缓存
	private static final String APP_CACAHE_DIRNAME = "/webcache";
	private TextView chekTxt;
	private SharedPreferences preferences;
	private InputStream is;
	private String nikename;
	private Intent intent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_setting, container,
				false);
		initview(view);
		return view;
	}

	/**
	 * 实例化个组件
	 * 
	 * @param view
	 */
	private void initview(View view) {
		imageView = (ImageView) view
				.findViewById(R.id.fragment_setting_icon);
		nameView = (TextView) view.findViewById(R.id.fragment_setting_name);
		about = (LinearLayout) view.findViewById(R.id.fragment_setting_about);
		canaell = (LinearLayout) view
				.findViewById(R.id.fragment_setting_canaell);
		updata = (LinearLayout) view.findViewById(R.id.fragment_setting_updata);
		exitbtn = (LinearLayout) view.findViewById(R.id.fragment_setting_exit);
		infoBtn = (LinearLayout) view.findViewById(R.id.fragment_setting_tx);
		userLayout = (LinearLayout) view
				.findViewById(R.id.fragment_setting_userinfo);
		newPass = (LinearLayout) view.findViewById(R.id.fragment_setting_newpass);
		chekNew = (LinearLayout) view.findViewById(R.id.fragment_setting_updatanew);
		chekTxt = (TextView) view.findViewById(R.id.fragment_setting_updata_txt);
		fragment_setting_fx = (LinearLayout) view.findViewById(R.id.fragment_setting_fx);
		fragment_setting_cache = (LinearLayout) view.findViewById(R.id.fragment_setting_cache);
		// contactSwitch =
		// (Switch)view.findViewById(R.id.fragment_setting_save_contact);
		preferences = getActivity().getSharedPreferences(Constant.LOGIN_SET, 0);
		// contactSwitch.setChecked(preferences.getBoolean(Constant.SAVE_CONTACT,
		// false));
		// contactSwitch.setOnCheckedChangeListener(this);
		// preferences.getBoolean(Constant.SAVE_CONTACT, false);
		preferences.edit().putBoolean(Constant.SAVE_CONTACT, false).commit();
		userLayout.setOnClickListener(listener);
		canaell.setOnClickListener(listener);
		about.setOnClickListener(listener);
		exitbtn.setOnClickListener(listener);
		updata.setOnClickListener(listener);
		infoBtn.setOnClickListener(listener);
		chekNew.setOnClickListener(listener);
		newPass.setOnClickListener(listener);
		fragment_setting_fx.setOnClickListener(listener);
		fragment_setting_cache.setOnClickListener(listener);//清楚缓存
		if (MyApplication.preferences.getBoolean(Constant.HUISHANG_HAVE_UPDATA, false)) {
			chekTxt.setText(getActivity().getResources().getString(R.string.updatanew_true));
		} else {
			chekTxt.setText(getActivity().getResources().getString(R.string.updatanew_no));
		}
		setChekInfo();
		setUserInfo();
		//getCacheSize();//获取缓存大小的方法
	}
	
	/**
	 * 设置更新信息
	 */
	private void setChekInfo() {
		if (MyApplication.preferences.getBoolean(Constant.HUISHANG_HAVE_UPDATA, false)) {
			chekTxt.setText(getActivity().getResources().getString(R.string.updatanew_true));
			chekTxt.setTextColor(0xFFFF0000);
		} else {
			chekTxt.setText(getActivity().getResources().getString(R.string.updatanew_no));
			chekTxt.setTextColor(0xFF969696);
		}
	}

	/**
	 * 按键监听
	 */
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {

			case R.id.fragment_setting_userinfo:
				// 修改用户信息
				intent = new Intent(getActivity(), ChangeUserInfo.class);
				startActivityForResult(intent, 0);
				break;
			case R.id.fragment_setting_about:
				// 关于
				intent = new Intent(getActivity(), SettingAbout.class);
				startActivity(intent);
				break;
			case R.id.fragment_setting_canaell:
				// 注销
				//((MainActivity) (getActivity())).unregis();
				//((MainActivity) (getActivity())).stopService(getActivity());
				/*MyApplication.isExit = true;
				new Thread(){
					public void run() {
						XmppConnectionManager.getInstance().disconnect();
					};
				}.start();
				if (IMChatService.getInstance() != null) {
					IMChatService.getInstance().stopChatService();
				}*/
				
				MyApplication.getInstance().getGotyeAPI().logout();
				getActivity().stopService(new Intent(getActivity(), HSChatService.class));
				if(MyApplication.isPhoneServiceRun) {
					getActivity().stopService(new Intent(getActivity(), PhoneService.class));
				}
				if (MyApplication.preferences.getInt(Constant.HUISHANG_LOCATION_TYPE, -1) == 0) {
					getActivity().stopService(new Intent(getActivity(), LocationService.class));
				}
				
				MyApplication.preferences.edit().putBoolean(Constant.IS_AUTOLOGIN, false)
						.commit();
				MyApplication.preferences.edit().putString(Constant.PASSWORD, "").commit();
				intent = new Intent(getActivity(), LandActivity.class);
				MyApplication.getInstance().Canaell();
				getActivity().finish();
				startActivity(intent);
				// restartApplication();
				break;
			case R.id.fragment_setting_exit:
				// 退出
				//((MainActivity) (getActivity())).unregis();
				/*MyApplication.isExit = true;
				new Thread(){
					public void run() {
						XmppConnectionManager.getInstance().disconnect();
					};
				}.start();
				if (IMChatService.getInstance() != null) {
					IMChatService.getInstance().stopChatService();
				}*/
				MyApplication.getInstance().getGotyeAPI().logout();
				getActivity().stopService(new Intent(getActivity(), HSChatService.class));
				if(MyApplication.isPhoneServiceRun) {
					getActivity().stopService(new Intent(getActivity(), PhoneService.class));
				}
				if (MyApplication.preferences.getInt(Constant.HUISHANG_LOCATION_TYPE, -1) == 0) {
					getActivity().stopService(new Intent(getActivity(), LocationService.class));
				}
				MyApplication.getInstance().Canaell();
				getActivity().finish();
				MyApplication.getInstance().removeActivity();
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
				break;

			case R.id.fragment_setting_updata:
				updataInfo();
				break;
			//清除缓存按钮
			case R.id.fragment_setting_cache:
				clearCacheFolder(getActivity().getCacheDir(), System.currentTimeMillis());//删除此时之前的缓存.
				showToast(true, "删除成功！");
				break;
				
			case R.id.fragment_setting_tx:
				showNetUnAvailableDialog();
				break;
			case R.id.fragment_setting_updatanew://检查更新按钮
				if (MyApplication.preferences.getBoolean(Constant.HUISHANG_HAVE_UPDATA, false)) {
					//显示下载对话框
					new ChekUpdata(getActivity()).showChekUpdata(MyApplication.preferences.getString(Constant.HUISHANG_CHEKNEW_URL, ""), false);
				} else {
					//无更新，显示提示
					((BaseActivity)getActivity()).showCustomToast(getActivity().getResources().getString(R.string.updatanew_no), true);
				} 
				break;
			case R.id.fragment_setting_newpass:
				Intent intent = new Intent(getActivity(), NewPasswordActivity.class);
				getActivity().startActivity(intent);
				break;
				case R.id.fragment_setting_fx:
					String pathurl=MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_WEBDOMAIN, "http://webapp.huishangyun.com");
					String share_meg=getResources().getString(R.string.app_name)+pathurl+"/open.aspx?p="+getResources().getString(R.string.app_enname)+"是一个社交化工作空间，提供了通讯录、外勤管理、销售订单、企业微信、任务中心、客户管理、移动审批等协作应用，为用户带来简单、及时、准确的工作体验。";
					Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
					sendIntent.setData(Uri.parse("smsto:"));
					sendIntent.putExtra("sms_body", share_meg);
					getActivity().startActivity(sendIntent);
					break;

			default:
				break;
			}
		}

	};
	
	public void showNetUnAvailableDialog() {
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle("提示!");
		builder.setMessage("是否在通知栏显示进程");
		builder.setPositiveButton("开启", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				MyApplication.preferences.edit().putBoolean(Constant.HUISHANG_MESSAGE, true).commit();
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				MyApplication.preferences.edit().putBoolean(Constant.HUISHANG_MESSAGE, false).commit();
				dialog.dismiss();
			}
			
		});
		builder.show();
	}
	/**
	 * 自定义toast显示
	 * @param isS 操作成功true，失败false
	 * @param msg 提示文字
	 */
	private void showToast(boolean isS,String msg){
		if (isS) {
			ClueCustomToast.showToast(getActivity(),
					R.drawable.toast_sucess, msg);
		}else {
			ClueCustomToast.showToast(getActivity(),
					R.drawable.toast_warn, msg);
		}

	}
	private int clearCacheFolder(File dir, long numDays) {
		int deletedFiles = 0;
		if (dir!= null && dir.isDirectory()) {
			try {
				for (File child:dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, numDays);
					}
					if (child.lastModified() < numDays) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}
//	/**
//	 * 获取缓存的大小
//	 */
//	private void getCacheSize() {
//		GetFileSizeUtil fz = GetFileSizeUtil.getInstance();
//		String cacheSize = "0m";
//		try {
//			cacheSize = fz.FormetFileSize(fz.getFileSize(new File(MyApplication.getDiskCacheDir())));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		//cache_size.setText(cacheSize);
//	}


	/**
	 * 更新基础数据
	 */
	public void updataInfo() {
		
		new Thread(new UpdataInfo()).start();
		((BaseActivity)getActivity()).showNotDialog("正在更新数据...");
		
	}

	/**
	 * 设置用户信息
	 */
	public void setUserInfo() {
		nameView.setText(preferences.getString(Constant.XMPP_MY_REAlNAME, ""));
		/*new ImageLoader(getActivity()).DisplayImage(
				"http://img.huishangyun.com/UploadFile/huishang/" +  MyApplication.getInstance().getCompanyID() + "/Photo/"
						+ preferences.getString("headurl", ""), imageView, false);*/
		imageView.setImageResource(R.drawable.contact_person);
		String url = "http://img.huishangyun.com/UploadFile/huishang/" + MyApplication.getInstance().getCompanyID() + "/Photo/"
				+ preferences.getString("headurl", "");
		ImageLoader.getInstance().displayImage(url,imageView,MyApplication.getInstance().getOptions());
		//FileTools.decodePhoto(, imageView);
		
	}

	/**
	 * 按钮监听
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		/*
		 * case R.id.fragment_setting_save_contact: //设置是否储存联系人列表
		 * preferences.edit().putBoolean(Constant.SAVE_CONTACT,
		 * isChecked).commit(); break;
		 */

		default:
			break;
		}
	}

	/**
	 * 获取访问服务器的Json
	 * 
	 * @return
	 */
	private String getJson(String Table_Name) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(0);// 获取当前时间
		String time = formatter.format(curDate);
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 0));
		OperTime operTime = new OperTime();
		operTime = OperationTime.getInstance(getActivity()).getOperTime(Table_Name);
		if (operTime == null || operTime.getOperationTime() == null || operTime.getOperationTime().equals("")) {
			zjRequest.setOperationTime(time);
		} else {
			zjRequest.setOperationTime(operTime.getOperationTime());
		}
		L.e("json == " + JsonUtil.toJson(zjRequest));
		return JsonUtil.toJson(zjRequest);

	}
	
	/**
	 * 获取访问服务器的Json
	 * 
	 * @return
	 */
	private String getJson5(String Table_Name) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(0);// 获取当前时间
		String time = formatter.format(curDate);
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 0));
		zjRequest.setType(1);
		OperTime operTime = new OperTime();
		operTime = OperationTime.getInstance(getActivity()).getOperTime(Table_Name);
		if (operTime == null || operTime.getOperationTime() == null || operTime.getOperationTime().equals("")) {
			zjRequest.setOperationTime(time);
		} else {
			zjRequest.setOperationTime(operTime.getOperationTime());
		}
		L.e("json == " + JsonUtil.toJson(zjRequest));
		return JsonUtil.toJson(zjRequest);

	}
	
	private String getJson3(String Table_Name) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(0);// 获取当前时间
		String time = formatter.format(curDate);
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 0));
		OperTime operTime = new OperTime();
		operTime = OperationTime.getInstance(getActivity()).getOperTime(Table_Name);
		if (operTime == null || operTime.getOperationTime() == null || operTime.getOperationTime().equals("")) {
			zjRequest.setOperationTime(time);
		} else {
			zjRequest.setOperationTime(operTime.getOperationTime());
		}
		zjRequest.setManager_ID(Integer.parseInt(MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANGYUN_UID, "0")));
		L.e("json == " + JsonUtil.toJson(zjRequest));
		return JsonUtil.toJson(zjRequest);
	}
	
	private String getJson4(String Table_Name) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(0);// 获取当前时间
		String time = formatter.format(curDate);
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 0));
		OperTime operTime = new OperTime();
		operTime = OperationTime.getInstance(getActivity()).getOperTime(Table_Name);
		if (operTime == null || operTime.getOperationTime() == null || operTime.getOperationTime().equals("")) {
			zjRequest.setOperationTime(time);
		} else {
			zjRequest.setOperationTime(operTime.getOperationTime());
		}
		//zjRequest.setManager_ID(Integer.parseInt(MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANGYUN_UID, "0")));
		L.e("json == " + JsonUtil.toJson(zjRequest));
		return JsonUtil.toJson(zjRequest);
	}
	
	private String getJson2(String Table_Name) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(0);// 获取当前时间
		String time = formatter.format(curDate);
		ZJRequest zjRequest = new ZJRequest();
		if (MyApplication.preferences.getString(Constant.HUISHANG_TYPE, "").equals("1")) {
			zjRequest.setManager_ID(Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANGYUN_UID, "")));
		} else {
			zjRequest.setDepartment_ID(MyApplication.preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0));
		}
		zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 0));
		OperTime operTime = new OperTime();
		operTime = OperationTime.getInstance(getActivity()).getOperTime(Table_Name);
		Log.e("TAGS" , "operTime="+operTime);
		if (operTime == null || operTime.getOperationTime() == null || operTime.getOperationTime().equals("")) {
			zjRequest.setOperationTime(time);
		} else {
			zjRequest.setOperationTime(operTime.getOperationTime());
		}
		Log.e("TAGS" , JsonUtil.toJson(zjRequest));
		return JsonUtil.toJson(zjRequest);
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case HanderUtil.case1:
				((BaseActivity)getActivity()).dismissDialog();
				((BaseActivity)getActivity()).showCustomToast("更新成功", true);
				getActivity().sendBroadcast(new Intent().setAction(Constant.HUISHANG_OK_ACTION));
				break;
			case HanderUtil.case2:
				((BaseActivity)getActivity()).dismissDialog();
				((BaseActivity)getActivity()).showCustomToast("更新失败", false);
				break;
			case HanderUtil.case3:
				L.e("正在更新" + (String) msg.obj);
				((BaseActivity)getActivity()).updataDialog((String) msg.obj);
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * 更新数据的线程
	 * @author Pan
	 *
	 */
	private class UpdataInfo implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (getSchedule() && getDepartment() && getManager() && getMemberGroup() && getMember()  && getMemberLink()
					&& getGroup() && getGroupUser() && getEnum() && getProductList() && getClassList() && getService() && getServiceMenu()
					&& getProductPriceList() && getProductUnitList()) { //依次更新数据
				mHandler.sendEmptyMessage(HanderUtil.case1);
			} else {
				//更新数据失败
				mHandler.sendEmptyMessage(HanderUtil.case2);
			}
		}

		/**
		 * 更新部门列表
		 */
		private boolean getDepartment() {
			Message msg = new Message();
			msg.what = HanderUtil.case3;
			msg.obj = "正在更新部门列表...";
			mHandler.sendMessage(msg);
			String departlist = DataUtil.callWebService(
					Methods.UPDATA_DEPARTMENT, getJson(DepartmentManager.DEPARMENT_TABLENAME));
			L.e(departlist);
			if (departlist != null) {
				Type type = new TypeToken<ZJResponse<Departments>>() {
				}.getType();
				ZJResponse<Departments> zjResponse = JsonUtil.fromJson(
						departlist, type);
				if (zjResponse.getCode() == 0) {
					List<Departments> departments = zjResponse.getResults();
					for (int i = 0; i < departments.size(); i++) {
						DepartmentManager.getInstance(getActivity())
								.saveDepartments(departments.get(i));
						OperTime operTime = new OperTime();
						operTime.setOperationTime(departments.get(i).getOperationTime());
						operTime.setTable_Name(DepartmentManager.DEPARMENT_TABLENAME);
						OperationTime.getInstance(getActivity()).saveTime(operTime);
					}
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}

		}
		
		/**
		 * 更新人员列表
		 */
		private boolean getManager() {
			Message msg = new Message();
			msg.what = HanderUtil.case3;
			msg.obj = "正在更新同事列表...";
			mHandler.sendMessage(msg);
			String departlist = DataUtil.callWebService(
					Methods.UPDATA_MANAGER, getJson(DepartmentManager.MANAGER_TABLENAME));
			L.e(departlist);
			if (departlist != null) {
				Type type = new TypeToken<ZJResponse<Managers>>() {
				}.getType();
				ZJResponse<Managers> zjResponse = JsonUtil.fromJson(
						departlist, type);
				if (zjResponse.getCode() == 0) {
					List<Managers> departments = zjResponse.getResults();
					for (int i = 0; i < departments.size(); i++) {
						DepartmentManager.getInstance(getActivity())
								.saveManagers(departments.get(i));
						OperTime operTime = new OperTime();
						operTime.setOperationTime(departments.get(i).getOperationTime());
						operTime.setTable_Name(DepartmentManager.MANAGER_TABLENAME);
						OperationTime.getInstance(getActivity()).saveTime(operTime);
					}
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}

		}
		
		/**
		 * 更新客户列表
		 */
		private boolean getMemberGroup() {
			Message msg = new Message();
			msg.what = HanderUtil.case3;
			msg.obj = "正在更新客户分组...";
			mHandler.sendMessage(msg);
			String departlist = DataUtil.callWebService(
					Methods.UPDATA_MEMBERGROUP, getJson(MemberManager.MEMBER_GROUP_TABLE_NAME));
			L.e(departlist);
			if (departlist != null) {
				Type type = new TypeToken<ZJResponse<MemberGroups>>() {
				}.getType();
				ZJResponse<MemberGroups> zjResponse = JsonUtil.fromJson(
						departlist, type);
				if (zjResponse.getCode() == 0) {
					List<MemberGroups> memberGroups = zjResponse.getResults();
					for (int i = 0; i < memberGroups.size(); i++) {
						MemberManager.getInstance(getActivity()).saveGroups(memberGroups.get(i));
						OperTime operTime = new OperTime();
						operTime.setOperationTime(memberGroups.get(i).getOperationTime());
						operTime.setTable_Name(MemberManager.MEMBER_GROUP_TABLE_NAME);
						OperationTime.getInstance(getActivity()).saveTime(operTime);
					}
					
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}

		}
		
		/**
		 * 更新客户详情
		 */
		private boolean getMember() {
			Message msg = new Message();
			msg.what = HanderUtil.case3;
			msg.obj = "正在更新客户列表...";
			mHandler.sendMessage(msg);
			String departlist = DataUtil.callWebService(
					Methods.UPDATA_MEMBER, getJson2(MemberManager.MEMBER_TABLE_NAME));
			Log.e("TAGS","departlist="+departlist);
			if (departlist != null) {
				Type type = new TypeToken<ZJResponse<Members>>() {
				}.getType();
				ZJResponse<Members> zjResponse = JsonUtil.fromJson(
						departlist, type);
				if (zjResponse.getCode() == 0) {
					List<Members> departments = zjResponse.getResults();
					for (int i = 0; i < departments.size(); i++) {
						MemberManager.getInstance(getActivity()).saveMembers(departments.get(i));
						OperTime operTime = new OperTime();
						operTime.setOperationTime(departments.get(i).getOperationTime());
						operTime.setTable_Name(MemberManager.MEMBER_TABLE_NAME);
						OperationTime.getInstance(getActivity()).saveTime(operTime);
						operTime.setTable_Name(MemberManager.MEMBER_IN_GROUP);
						OperationTime.getInstance(getActivity()).saveTime(operTime);
					}
					
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}

		}
		
		
		/**
		 * 更新客户详情
		 */
		private boolean getMemberLink() {
			Message msg = new Message();
			msg.what = HanderUtil.case3;
			msg.obj = "正在更新客户联系人...";
			mHandler.sendMessage(msg);
			String departlist = DataUtil.callWebService(
					Methods.GET_MEMBERLINK_LIST, getJson2(MemberManager.MEMBER_LINKS_TABLE_NAME));
			L.e(departlist);
			if (departlist != null) {
				Type type = new TypeToken<ZJResponse<MemberLinks>>() {
				}.getType();
				ZJResponse<MemberLinks> zjResponse = JsonUtil.fromJson(
						departlist, type);
				if (zjResponse.getCode() == 0) {
					List<MemberLinks> departments = zjResponse.getResults();
					for (int i = 0; i < departments.size(); i++) {
						MemberManager.getInstance(getActivity()).saveMemberLinks(departments.get(i));
						OperTime operTime = new OperTime();
						operTime.setOperationTime(departments.get(i).getOperationTime());
						operTime.setTable_Name(MemberManager.MEMBER_LINKS_TABLE_NAME);
						OperationTime.getInstance(getActivity()).saveTime(operTime);
					}
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}

		}
		
		/*private boolean getSite() {
			Message msg = new Message();
			msg.what = HanderUtil.case3;
			msg.obj = "正在更新城市列表...";
			mHandler.sendMessage(msg);
			String departlist = DataUtil.callWebService(
					Methods.UPDATA_SITE, getJson(AreaManager.AREA_TABLENAME));
			if (departlist != null) {
				Type type = new TypeToken<ZJResponse<AreaModel>>() {
				}.getType();
				ZJResponse<AreaModel> zjResponse = JsonUtil.fromJson(
						departlist, type);
				if (zjResponse.getCode() == 0) {
					List<AreaModel> departments = zjResponse.getResults();
					for (int i = 0; i < departments.size(); i++) {
						AreaManager.getInstance(getActivity()).saveArea(departments.get(i));
					}
					if (departments.size() > 0) {
						OperTime operTime = new OperTime();
						operTime.setOperationTime(departments.get(0).getOperationTime());
						operTime.setTable_Name(AreaManager.AREA_TABLENAME);
						OperationTime.getInstance(getActivity()).saveTime(operTime);
					}
					
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}
		}*/
		
		private boolean getEnum() {
			Message msg = new Message();
			msg.what = HanderUtil.case3;
			msg.obj = "正在更新数据字典...";
			mHandler.sendMessage(msg);
			String departlist = DataUtil.callWebService(
					Methods.UPDATA_ENUM, getJson(EnumManager.EMUN_TABLENAME));
			L.e(departlist);
			if (departlist != null) {
				Type type = new TypeToken<ZJResponse<com.huishangyun.model.Enum>>() {
				}.getType();
				ZJResponse<Enum> zjResponse = JsonUtil.fromJson(
						departlist, type);
				if (zjResponse.getCode() == 0) {
					List<Enum> departments = zjResponse.getResults();
					for (int i = 0; i < departments.size(); i++) {
						EnumManager.getInstance(getActivity()).saveEmun(departments.get(i));
						OperTime operTime = new OperTime();
						operTime.setOperationTime(departments.get(i).getOperationTime());
						operTime.setTable_Name(EnumManager.EMUN_TABLENAME);
						OperationTime.getInstance(getActivity()).saveTime(operTime);
					}
					
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}
		}
		
		/**
		 * 更新产品分类
		 * @return
		 */
		private boolean getClassList() {
			Message msg = new Message();
			msg.what = HanderUtil.case3;
			msg.obj = "正在更新产品分类...";
			mHandler.sendMessage(msg);
			ZJRequest zjRequest = new ZJRequest();
			zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 0));
			zjRequest.setPageIndex(1);
			zjRequest.setPageSize((int) Long.MAX_VALUE);
			zjRequest.setType(2);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date curDate = new Date(0);// 获取当前时间
			String time = formatter.format(curDate);
			OperTime operTime = new OperTime();
			operTime = OperationTime.getInstance(getActivity()).getOperTime(ProductManager.CLASS_TABLENAME);
			if (operTime == null || operTime.getOperationTime() == null || operTime.getOperationTime().equals("")) {
				zjRequest.setOperationTime(time);
			} else {
				zjRequest.setOperationTime(operTime.getOperationTime());
			}
			String departlist = DataUtil.callWebService(
					Methods.ORDER_CLASSLIST, JsonUtil.toJson(zjRequest));
			L.e(departlist);
			if (departlist != null) {
				Type type = new TypeToken<ZJResponse<ClassModel>>() {
				}.getType();
				ZJResponse<ClassModel> zjResponse = JsonUtil.fromJson(
						departlist, type);
				if (zjResponse.getCode() == 0) {
					List<ClassModel> departments = zjResponse.getResults();
					for (int i = 0; i < departments.size(); i++) {
						ProductManager.getInstance(getActivity()).saveClass(departments.get(i));
						OperTime operTime2 = new OperTime();
						operTime2.setOperationTime(departments.get(i).getOperationTime());
						operTime2.setTable_Name(ProductManager.CLASS_TABLENAME);
						OperationTime.getInstance(getActivity()).saveTime(operTime2);
					}
					return true;
				} else {
					return false;
				}
			}
			return false;
		}
		
		private boolean getProductList() {
			Message msg = new Message();
			msg.what = HanderUtil.case3;
			msg.obj = "正在更新产品列表..";
			mHandler.sendMessage(msg);
			String departlist = DataUtil.callWebService(
					Methods.UPDATA_GOODSLISTS, getJson(ProductManager.PRODUCT_TABLENAME));
			L.e("产品列表 ： " + departlist);
			if (departlist != null) {
				Type type = new TypeToken<ZJResponse<Order_GoodsList>>() {
				}.getType();
				ZJResponse<Order_GoodsList> zjResponse = JsonUtil.fromJson(
						departlist, type);
				if (zjResponse.getCode() == 0) {
					List<Order_GoodsList> departments = zjResponse.getResults();
					for (int i = 0; i < departments.size(); i++) {
						Order_GoodsList order_GoodsList = departments.get(i);
						ProductManager.getInstance(getActivity()).saveProductInfo(order_GoodsList);
						OperTime operTime = new OperTime();
						operTime.setOperationTime(departments.get(i).getOperationTime());
						operTime.setTable_Name(ProductManager.PRODUCT_TABLENAME);
						OperationTime.getInstance(getActivity()).saveTime(operTime);
					}
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}
		}
		
		private boolean getSchedule() {
			Message msg = new Message();
			msg.what = HanderUtil.case3;
			msg.obj = "正在更新行政班列表..";
			mHandler.sendMessage(msg);
			String departlist = DataUtil.callWebService(
					Methods.GET_SCHEDULIST, getJson(ScheduleManager.SCHE_TABLENAME));
			if (departlist != null) {
				Type type = new TypeToken<ZJResponse<Schedule>>() {
				}.getType();
				ZJResponse<Schedule> zjResponse = JsonUtil.fromJson(
						departlist, type);
				if (zjResponse.getCode() == 0) {
					List<Schedule> departments = zjResponse.getResults();
					for (int i = 0; i < departments.size(); i++) {
						ScheduleManager.getInstance(getActivity()).saveSche(departments.get(i));
						OperTime operTime = new OperTime();
						operTime.setOperationTime(departments.get(i).getOperationTime());
						operTime.setTable_Name(ScheduleManager.SCHE_TABLENAME);
						OperationTime.getInstance(getActivity()).saveTime(operTime);
					}
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}
			
		}
		
		
		
		private boolean getService() {
			Message msg = new Message();
			msg.what = HanderUtil.case3;
			msg.obj = "正在更新应用列表";
			mHandler.sendMessage(msg);
			String departlist = DataUtil.callWebService(
					Methods.GET_SERVICE, getJson5(ServiceManager.SERVICE_NAME));
			L.e(departlist);
			if (departlist != null) {
				L.e("应用列表 = " + departlist);
				Type type = new TypeToken<ZJResponse<Service>>() {
				}.getType();
				ZJResponse<Service> zjResponse = JsonUtil.fromJson(
						departlist, type);
				if (zjResponse.getCode() == 0) {
					List<Service> departments = zjResponse.getResults();
					for (int i = 0; i < departments.size(); i++) {
						//ScheduleManager.getInstance(getActivity()).saveSche(schedules);
						ServiceManager.getInstance(getActivity()).saveService(departments.get(i));
						OperTime operTime = new OperTime();
						operTime.setOperationTime(departments.get(i).getOperationTime());
						operTime.setTable_Name(ServiceManager.SERVICE_NAME);
						OperationTime.getInstance(getActivity()).saveTime(operTime);
					}
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}
			
		}
		
		
		private boolean getServiceMenu() {
			Message msg = new Message();
			msg.what = HanderUtil.case3;
			msg.obj = "正在更新应用菜单";
			mHandler.sendMessage(msg);
			String departlist = DataUtil.callWebService(
					Methods.GET_SERVICE_MENU, getJson(ServiceManager.SERVICE_MENU_NAME));
			L.e(departlist);
			if (departlist != null) {
				Log.e("应用菜单 = ", departlist);
				Type type = new TypeToken<ZJResponse<ServiceMenu>>() {
				}.getType();
				ZJResponse<ServiceMenu> zjResponse = JsonUtil.fromJson(
						departlist, type);

				if (zjResponse.getCode() == 0) {
					List<ServiceMenu> departments = zjResponse.getResults();
					for (int i = 0; i < departments.size(); i++) {
						//ScheduleManager.getInstance(getActivity()).saveSche(schedules);
						ServiceManager.getInstance(getActivity()).saveServiceMenu(departments.get(i));
						OperTime operTime = new OperTime();
						operTime.setOperationTime(departments.get(i).getOperationTime());
						operTime.setTable_Name(ServiceManager.SERVICE_MENU_NAME);
						OperationTime.getInstance(getActivity()).saveTime(operTime);
					}
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}
			
		}
		
		/**
		 * 获取群列表
		 * @return
		 */
		private boolean getGroup() {
			Message msg = new Message();
			msg.what = HanderUtil.case3;
			msg.obj = "正在更新群列表";
			mHandler.sendMessage(msg);
			String departlist = DataUtil.callWebService(
					Methods.GET_GROUP_LIST, getJson3(GroupManager.GROUP_TABLENAME));
			L.e(departlist);
			if (departlist != null) {
				L.e("应用菜单 = " + departlist);
				Type type = new TypeToken<ZJResponse<IMGroup>>() {
				}.getType();
				ZJResponse<IMGroup> zjResponse = JsonUtil.fromJson(
						departlist, type);
				if (zjResponse.getCode() == 0) {
					List<IMGroup> departments = zjResponse.getResults();
					for (int i = 0; i < departments.size(); i++) {
						//ScheduleManager.getInstance(getActivity()).saveSche(schedules);
						//ServiceManager.getInstance(getActivity()).saveServiceMenu(schedules);
						GroupManager.getInstance(getActivity()).saveGroup(departments.get(i));
						OperTime operTime = new OperTime();
						operTime.setOperationTime(departments.get(i).getOperationTime());
						operTime.setTable_Name(GroupManager.GROUP_TABLENAME);
						OperationTime.getInstance(getActivity()).saveTime(operTime);
					}
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}
			
		}
		
		
		/**
		 * 获取群列表
		 * @return
		 */
		private boolean getGroupUser() {
			Message msg = new Message();
			msg.what = HanderUtil.case3;
			msg.obj = "正在更新群成员列表";
			mHandler.sendMessage(msg);
			String departlist = DataUtil.callWebService(
					Methods.GET_GROUP_USER_LIST, getJson(GroupManager.GROUP_USER_TABLENAME));
			L.e(departlist);
			if (departlist != null) {
				L.e("应用菜单 = " + departlist);
				Type type = new TypeToken<ZJResponse<GroupUser>>() {
				}.getType();
				ZJResponse<GroupUser> zjResponse = JsonUtil.fromJson(
						departlist, type);
				if (zjResponse.getCode() == 0) {
					List<GroupUser> departments = zjResponse.getResults();
					for (int i = 0; i < departments.size(); i++) {
						//ScheduleManager.getInstance(getActivity()).saveSche(schedules);
						//ServiceManager.getInstance(getActivity()).saveServiceMenu(schedules);
						GroupManager.getInstance(getActivity()).saveGroupUser(departments.get(i));
						OperTime operTime = new OperTime();
						operTime.setOperationTime(departments.get(i).getOperationTime());
						operTime.setTable_Name(GroupManager.GROUP_USER_TABLENAME);
						OperationTime.getInstance(getActivity()).saveTime(operTime);
					}
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}
			
		}
		
		private boolean getProductUnitList() {
			Message msg = new Message();
			msg.what = HanderUtil.case3;
			msg.obj = "正在更新单位";
			mHandler.sendMessage(msg);
			String departlist = DataUtil.callWebService(
					Methods.GetProductUnitList, getJson4(UnitPriceManager.UNIT_NAME));
			if (departlist != null) {
				L.e("应用菜单 = " + departlist);
				Type type = new TypeToken<ZJResponse<ProductUnit>>() {
				}.getType();
				ZJResponse<ProductUnit> zjResponse = JsonUtil.fromJson(
						departlist, type);
				if (zjResponse.getCode() == 0) {
					List<ProductUnit> departments = zjResponse.getResults();
					for (int i = 0; i < departments.size(); i++) {
						//ScheduleManager.getInstance(getActivity()).saveSche(schedules);
						UnitPriceManager.getInstance(getActivity()).saveUnit(departments.get(i));
						OperTime operTime = new OperTime();
						operTime.setOperationTime(departments.get(i).getOperationTime());
						operTime.setTable_Name(UnitPriceManager.UNIT_NAME);
						OperationTime.getInstance(getActivity()).saveTime(operTime);
					}
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}
			
		}
		
		private boolean getProductPriceList() {
			Message msg = new Message();
			msg.what = HanderUtil.case3;
			msg.obj = "正在更新价格";
			mHandler.sendMessage(msg);
			String departlist = DataUtil.callWebService(
					Methods.GetProductPriceList, getJson4(UnitPriceManager.PRICE_NAME));
			if (departlist != null) {
				L.e("应用菜单 = " + departlist);
				Type type = new TypeToken<ZJResponse<ProductPrice>>() {
				}.getType();
				ZJResponse<ProductPrice> zjResponse = JsonUtil.fromJson(
						departlist, type);
				if (zjResponse.getCode() == 0) {
					List<ProductPrice> departments = zjResponse.getResults();
					for (ProductPrice schedules : departments) {
						//ScheduleManager.getInstance(getActivity()).saveSche(schedules);
						UnitPriceManager.getInstance(getActivity()).savePrice(schedules);
						OperTime operTime = new OperTime();
						operTime.setOperationTime(schedules.getOperationTime());
						operTime.setTable_Name(UnitPriceManager.PRICE_NAME);
						OperationTime.getInstance(getActivity()).saveTime(operTime);
					}
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}
			
		}
		
		
		
		
		
		

	}
}
