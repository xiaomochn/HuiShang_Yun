package com.huishangyun.Office.Attendance;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Channel.Clues.ShowDialog;
import com.huishangyun.Channel.Visit.PhotoHelpDefine;
import com.huishangyun.Interface.OnDialogDown;
import com.huishangyun.Map.LocationUtil;
import com.huishangyun.Office.Attendance.MySlipSwitch.OnSwitchListener;
import com.huishangyun.Office.Clock.DBManager;
import com.huishangyun.Office.Clock.ReSetAlram;
import com.huishangyun.Util.Base64Util;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.manager.ScheduleManager;
import com.huishangyun.model.CallSystemApp;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.Methods;
import com.huishangyun.model.Schedule;
import com.huishangyun.task.UpLoadFileTask.OnUpLoadResult;
import com.huishangyun.task.UpLoadImgSignText;
import com.huishangyun.yun.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考勤主页
 * 
 * @author xsl
 * 
 */
public class MainAttendanceActivity extends BaseActivity implements OnUpLoadResult,OnDialogDown{
	private static final String TAG = null;
	private TextView current_time;// 当前时间
	private MySlipSwitch switchbutton;//滑动开关
	private TasksCompletedView mTasksView;
	private TasksCompletedView mTasksViewBig;
	private int mTotalProgress = 100;//总进度
	private int mCurrentProgress = 0;//当前进度
	private RelativeLayout back;//返回主页
	private RelativeLayout clock;//闹钟
	private LinearLayout later;//迟到、早退
	private TextView later_times;//迟到
	private TextView early_times;//早退
	private RelativeLayout attendance_shift;//行政班
	private TextView shift_text;//班次名称
	private RelativeLayout attendance_takingpicture;//拍照
	private TextView takingpicture_name;//拍照按钮名称--拍完改为预览
    private RelativeLayout note;//备注按钮层
    private LinearLayout note_write_layout;//备注书写层
    private TextView note_text;//备注内容
    private RelativeLayout go_to_work;//上班
    private RelativeLayout be_off_work;//下班
    private String photoPath;//图片保存路径
	private String FileName;//文件名
	private ProgressDialog pDialog;
	public static List<String> Img_List = new ArrayList<String>();//图片本地地址
	public static List<String> pList = new ArrayList<String>();//相机返回图片名称
	ArrayList<HashMap<String, Object>> ItemLists = new ArrayList<HashMap<String, Object>>();//班次集合
	HashMap<String, Object> map;//定义一个map函数
	private static final int ATTENDANCE_DIALOG = 0; 
	private OfficeList list = new OfficeList();
	private String Manager_ID;//登入人uid
	private int ScheduleID;//班次id
	private List<Schedule>  ScheduleList = new ArrayList<Schedule>();
	private List<Schedule>  ScheduleIDList = new ArrayList<Schedule>();
	private int CompanyID;//公司id
	private int ManagerID;
	private List<OfficeList> mlist = new  ArrayList<OfficeList>();
	private Calendar calendar;
	private String newSDpath;//压缩后的图片地址
	public static int W;
	public static int H;
	private Double Lng;// 经度
	private Double Lat;// 维度
	private String location;// 地理位置
	private String sharedPreferencesFileName;//文件名
	private String isSubmit = "0";
	private boolean isRun = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_office_attendance_mian);
		ManagerPhoneRatio();
		CompanyID = preferences.getInt(Content.COMPS_ID, 0);
		ManagerID = MyApplication.getInstance().getManagerID();
		sharedPreferencesFileName = "MainAttendanceActivity" + CompanyID + ManagerID; 
		init();
		ItemLists.clear();
		ScheduleIDList.clear();
		setItemDate();
		calendar = Calendar.getInstance();
		//获取当月迟到早退数据
		getNetData(calendar.get(Calendar.MONTH)+1);
		//启动定位服务
		LocationUtil.startLocation(MainAttendanceActivity.this, mLocationListener);
	}
	
	/**
	 * 对当前手机分辨率操作获取和存储操作
	 */
	private void ManagerPhoneRatio(){
	    HashMap<String, Integer> map = new HashMap<String, Integer>();
	    map = AttendanceSharedPreferences.getPhoneRatio(getApplicationContext(), "PhoneRatio");
	    if (map.size()>0 && map.get("HighRatio")>0 && map.get("WidthRatio")>0) {
			H = map.get("HighRatio");
			W = map.get("WidthRatio");
		}else {
			DisplayMetrics mDisplayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
			W = mDisplayMetrics.widthPixels;
			H = mDisplayMetrics.heightPixels;
			if (W>0 && H>0) {
				boolean bool = AttendanceSharedPreferences.savePhoneRatio(getApplicationContext(), "PhoneRatio", H, W);
				if (bool) {
					L.e("分辨率W：" + W);
					L.e("分辨率H：" + H);
					L.e("手机分辨率保存成功！");
				}else {
					L.e("手机分辨率保存失败！");
				}
			}
		}
	}
	
	class ProgressRunable implements Runnable {

		@Override
		public void run() {
			while (mCurrentProgress < mTotalProgress) {
				mCurrentProgress += 1;
				mTasksView.setProgress(mCurrentProgress);
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * 获得网络数据
	 */
	private void getNetData(final int month) {
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String yyjjString = getJson(month);
				Log.e(TAG, "yyjjString:" + yyjjString);
				String result = DataUtil.callWebService(
						Methods.GET_ATTENDANCE_LIST,
						getJson(month));	
				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					
					// 获取对象的Type
					Type type = new TypeToken<ZJResponse<OfficeList>>() {
					}.getType();
					ZJResponse<OfficeList> zjResponse = JsonUtil.fromJson(result,
							type);
					mlist.clear();//先清除
					mlist = zjResponse.getResults();
					if (mlist.size() != 0) {
						Log.e(TAG, "mlist:" + mlist.size());
						Log.e(TAG, "mlist====>:" + mlist.get(0).getLate());
					}
					
				    mHandler.sendEmptyMessage(4);
						
				} else {
					mHandler.sendEmptyMessage(6);
				}
				
			}
		}.start();
	}
	
	/**
	 * 设置json对象
	 *
	 */
	private String getJson(int month) {
		
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setCompany_ID(CompanyID);//公司id
		zjRequest.setMonth(month);
		zjRequest.setManager_ID(Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID,"0")));
		return JsonUtil.toJson(zjRequest);
	}
	
	/**
	 * 提交考勤数据
	 * @param Note 备注
	 * @param Operate 考勤类型
	 * @param Picture 图片名称
	 * @param Schedule_ID 行政班id
	 */
	private void setNetData(final String Note, final String Operate,final String Picture,final int Schedule_ID) {
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String yyjjString = getJson(Note,Operate,Picture,Schedule_ID);
				Log.e(TAG, "yyjjString:" + yyjjString);
				String result = DataUtil.callWebService(
						Methods.SET_ATTENDANCE_LIST,
						getJson(Note,Operate,Picture,Schedule_ID));	
				L.e("result:" + result);
				//先判断网络数据是否获取成功，防止网络不好导致程序崩溃
				if (result != null) {
					//json解析返回结果
					try {
						JSONObject jsonObject = new JSONObject(result);
						Log.e(TAG, "code:" + jsonObject.getInt("Code"));
						int code = jsonObject.getInt("Code");
						if (code==0) {
							mHandler.sendEmptyMessage(3);
						}else {
							mHandler.sendEmptyMessage(5);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}											
						
				} else {
					mHandler.sendEmptyMessage(5);
				}
				
			}
		}.start();
	}
	
	/**
	 * 设置json对象
	 *
	 */
	private String getJson(String Note, String Operate,String Picture,int Schedule_ID) {
		
//		list.setDepartment_ID(preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0));
		list.setCompany_ID(preferences.getInt(Content.COMPS_ID, 1016));//公司id
		list.setManager_ID(Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0")));
		list.setID(0);
		list.setAction("Insert");
		list.setNote(Note);
		//In：上班，Out：下班
		list.setOperate(Operate);
		list.setPicture(Picture);
		list.setSchedule_ID(Schedule_ID);
		list.setLat(Lat);
		list.setLng(Lng);
		list.setLoc(location);
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setData(list);
		return JsonUtil.toJson(zjRequest);

	}

	/**
	 * 初始化控件
	 */
	private void init() {
		// 用户编号
	    Manager_ID = preferences.getString(Constant.HUISHANGYUN_UID,"0");
		back = (RelativeLayout) findViewById(R.id.back);
		clock = (RelativeLayout) findViewById(R.id.clock);
		later = (LinearLayout) findViewById(R.id.later);
		attendance_shift = (RelativeLayout) findViewById(R.id.attendance_shift);
		shift_text = (TextView) findViewById(R.id.shift_text);
		attendance_takingpicture = (RelativeLayout) findViewById(R.id.attendance_takingpicture);
		takingpicture_name = (TextView) findViewById(R.id.takingpicture_name);
		note = (RelativeLayout) findViewById(R.id.note);
		note_write_layout = (LinearLayout) findViewById(R.id.note_write_layout);
		note_text = (TextView) findViewById(R.id.note_text);
		go_to_work = (RelativeLayout) findViewById(R.id.go_to_work);
		be_off_work = (RelativeLayout) findViewById(R.id.be_off_work);
		later_times = (TextView) findViewById(R.id.later_times);
		early_times = (TextView) findViewById(R.id.early_times);
		
		back.setOnClickListener(new ButtonClickListener());
		clock.setOnClickListener(new ButtonClickListener());
		later.setOnClickListener(new ButtonClickListener());
		attendance_shift.setOnClickListener(new ButtonClickListener());
		attendance_takingpicture.setOnClickListener(new ButtonClickListener());
		note.setOnClickListener(new ButtonClickListener());
		go_to_work.setOnClickListener(new ButtonClickListener());
		be_off_work.setOnClickListener(new ButtonClickListener());
		
	    
		current_time = (TextView) findViewById(R.id.current_time);
		switchbutton = (MySlipSwitch) findViewById(R.id.switchbutton);
		mTasksView = (TasksCompletedView) findViewById(R.id.tasks_view);
		mTasksViewBig = (TasksCompletedView) findViewById(R.id.tasks_view_big);
		mTasksViewBig.setProgress(100);
		
		switchbutton.setImageResource(R.drawable.attendance_open, R.drawable.attendance_off, R.drawable.attendance_openpoint);
		if (query().equals("0")) {
			switchbutton.setSwitchState(false);
		}else if (query().equals("1")) {
			switchbutton.setSwitchState(true);
		}
		
		//进来重新设置一次闹钟
		String Manager_ID = MyApplication.getInstance().getManagerID()+"";
		//避免无登入人时闹钟出错
		if (!Manager_ID.equals("") && Manager_ID != null) {
			new ReSetAlram(context, Manager_ID, 1, 1).setAlarm();
			new ReSetAlram(context, Manager_ID, 2, 2).setAlarm();
		}
		
		//滑动开关监听
		switchbutton.setOnSwitchListener(new OnSwitchListener() {
			
			@Override
			public void onSwitched(MySlipSwitch mySlipSwitch, boolean isSwitchOn) {
				// TODO Auto-generated method stub
				
				if(isSwitchOn) {
					 update("1");//更改数据库内容，将数据设为闹钟开启
					 showCustomToast("闹钟已开启！", true);
				} else {
					 update("0");
					 showCustomToast("闹钟已关闭！", false);
				}
				
				
			}
		});
		
		
		pList.clear();
		Img_List.clear();
		try {
			getUiData();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			L.e("UI数据获取失败");
		}
		
	}
	
	
	/**
	 * 查询数据库，闹钟是否开启
	 */
	private String query(){
		String sql = "select* from mClock where ManagerID = ? and mIndex = ?";
		DBManager dbManager = new DBManager(getApplication());
		Map<String, String> map = dbManager.queryBySQL(sql, new String[] {Manager_ID,"1"});
		if (map.size()>0) {
			Log.e(TAG, "闹钟状态===>:" + map.get("isOpen"));
			return map.get("isOpen");
		}
		return "0";
	}
	
	/**
	 * 闹钟数据修改
	 * @param isOpen “1”为开启闹钟，“0”为关闭闹钟
	 */
	private void update(String isOpen){
		DBManager dbManager = new DBManager(getApplication());
		if (dbManager.clockOnOff(Manager_ID, isOpen)) {
			Log.e(TAG, "数据修改成功！" );
		}else {
			Log.e(TAG, "数据修改失败！");
		}
	}
	
	/**
	 * 单击事件处理
	 * @author xsl
	 *
	 */
	public class ButtonClickListener implements View.OnClickListener{

		public void onClick(View v) {
			Intent intent = null;
			switch (v.getId()) {
			case R.id.back://返回
				saveUiData();
				finish();
				break;
			case R.id.clock://闹钟设置
				intent = new Intent(MainAttendanceActivity.this,AttendanceRemind.class);
				startActivity(intent);
				break;
            case R.id.later://迟到、早退
            	intent = new Intent(MainAttendanceActivity.this,MyAttendance.class);
				startActivity(intent);
				break;
			case R.id.attendance_shift://行政班
				
				new ShowDialog(MainAttendanceActivity.this, ATTENDANCE_DIALOG, ItemLists, "选择班次", MainAttendanceActivity.this,"请选择班次！",shift_text.getText().toString().trim()).customDialog();	
				break;
			case R.id.attendance_takingpicture://拍照
				
				
				if (Img_List.size() >0) {
					Intent intent1 = new Intent(MainAttendanceActivity.this,OfficePhotoSkim.class);
					intent1.putExtra("index", 1);
					intent1.putExtra("imgselect", 0);
					startActivityForResult(intent1, 1);
					
				}else {
					
					FileName = getSystemTime() + ".jpg";
					photoPath = Constant.SAVE_IMG_PATH + File.separator
							+ FileName;
					CallSystemApp.callCamera(MainAttendanceActivity.this, photoPath);
				}
				
				break;
			case R.id.note://备注
				note.setVisibility(View.GONE);
				note_write_layout.setVisibility(View.VISIBLE);
				
				break;
			case R.id.go_to_work://上班
				//In：上班，Out：下班
				if (isWrite()) {
					try {
					 //相差时间计算
			    	 Date d = new Date(System.currentTimeMillis());
					 SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
					 SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
					 String date = sf.format(d);
					 //上班时间字符串
					String Indate = date  + ScheduleList.get(0).getInTime().substring(0, 2) 
							 + ScheduleList.get(0).getInTime().substring(3, 5);
					 //上班时间毫秒数
					long InSeconds = sdf.parse(Indate).getTime();
			    	
			    	//当前时间毫秒数
			    	long NowSeconds = sdf.parse(sdf.format(d)).getTime();
					if (NowSeconds<InSeconds) {
						showAlertDialog("In", "", true);
					}else {
						showAlertDialog("In", "上班考勤时间已经过了哦！", false);
					}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
					
				}
				
				break;
			case R.id.be_off_work://下班
				if (isWrite()) {
					try {
					 //相差时间计算
			    	 Date d = new Date(System.currentTimeMillis());
					 SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
					 SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
					 String date = sf.format(d);
			    	//当前时间毫秒数
			    	long NowSeconds = sdf.parse(sdf.format(d)).getTime();
			    	 //下班时间字符串
					String  Outdate = date  + ScheduleList.get(0).getOutTime().substring(0, 2) 
							 + ScheduleList.get(0).getOutTime().substring(3, 5);
					 //上班时间毫秒数
					long OutSeconds = sdf.parse(Outdate).getTime();
					if (NowSeconds>OutSeconds) {
						showAlertDialog("Out", "", true);
					}else {
						showAlertDialog("Out", "还未到下班考勤时间！", false);
					}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
				break;
			default:
				break;
				
			}
			
		}}
		
	

	/**
	 * 线程获取当前系统时间
	 * 
	 * @author xsl
	 * 
	 */
	public class TimeThread extends Thread {
		@Override
		public void run() {
			do {
				try {
					Thread.sleep(1000);
					Message msg = new Message();
					msg.what = 0;
					mHandler.sendMessage(msg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (isRun);
		}
	}

	/**
	 * 更新UI
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				long sysTime = System.currentTimeMillis();
				SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
				/*String sysTimeStr = (String) DateFormat
						.format("HH:mm:ss", sysTime);*/
				String sysTimeStr = format.format(new Date(sysTime));
				current_time.setText(sysTimeStr);
				try {	
				 //相差时间计算
		    	 Date d = new Date(System.currentTimeMillis());
				 SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
				 SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				 String date = sf.format(d);
				 //上班时间字符串
				String Indate = date  + ScheduleList.get(0).getInTime().substring(0, 2) 
						 + ScheduleList.get(0).getInTime().substring(3, 5);
//				L.e("Indate = " + Indate);
				 //上班时间毫秒数
				long InSeconds = sdf.parse(Indate).getTime();
		    	
		    	//当前时间毫秒数
		    	long NowSeconds = sdf.parse(sdf.format(d)).getTime();
		    	 //下班时间字符串
				String  Outdate = date  + ScheduleList.get(0).getOutTime().substring(0, 2) 
						 + ScheduleList.get(0).getOutTime().substring(3, 5);
				 //上班时间毫秒数
				long OutSeconds = sdf.parse(Outdate).getTime();
				//提醒开始的时间，分别是，上班或下班时间减1800秒
				
//				L.e("InSeconds = " + InSeconds);
//				L.e("OutSeconds = " + OutSeconds);
//				L.e("NowSeconds = " + NowSeconds);
				
				if (NowSeconds -(InSeconds-1800000) >= 0 && NowSeconds -(InSeconds-1800000) <= 1800000) {
					 mTasksView.setProgress((float)((NowSeconds-InSeconds+1800000)*100.0/1800000));
				}else if (NowSeconds ==(InSeconds+60000)) {
					 mTasksView.setProgress(0);
				}
				
				if (NowSeconds -(OutSeconds-1800000) >= 0 && NowSeconds -(OutSeconds-1800000) < 1800000) {
					 mTasksView.setProgress((float)((1800000-(OutSeconds - NowSeconds))*100.0/1800000));
				}else if (NowSeconds ==(OutSeconds+60000)) {
					 mTasksView.setProgress(0);
				}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					L.e("出错了 ");
				}

				break;
				
			case 1:
				//上传图片
				File file = (File) msg.obj;
            	newSDpath = file.getAbsolutePath();
            	setImageToNet(file.getAbsolutePath());
            	
				break;
			case 2:
				//photoPath为完整路
            	Img_List.add(newSDpath);
            	takingpicture_name.setText("预览");
				break;
				
			case 3://考勤提交成功
				isSubmit="1";
				saveUiData();
				showCustomToast("考勤提交成功！", true);
				break;
			case 4:
				if (mlist.size()>0) {
					later_times.setText("迟到:" + mlist.get(0).getLate()+"次");
					early_times.setText("早退:" + mlist.get(0).getEarly()+"次");
				}
				break;
            case 5:
            	showCustomToast("考勤提交失败！", false);
				break;
			case 6:
				showCustomToast("请检查网络连接，未获得任何数据！", false);
				break;
				
				case 7:
					//对图片进行压缩
	            	new Thread(){
	            		public void run() {
	            			File file;
	                    	String newPath = Constant.SAVE_IMG_PATH + File.separator + getSystemTime() + ".jpg";
	                    	try {
	                    		file = PhotoHelpDefine.compressImage(photoPath, newPath, MyApplication.photoWidth, MyApplication.photoHeigh);
//	        					file = PhotoHelp.compressImage(photoPath, newPath);
	        					Message msg = new Message();
	        					msg.obj = file;
	        					msg.what = 1;
	        					mHandler.sendMessage(msg);
	        				} catch (Exception e) {
	        					// TODO Auto-generated catch block
	        					mHandler.sendEmptyMessage(8);
	        					e.printStackTrace();
	        					return;
	        				}
	            		};
	            	}.start();
					break;
				case 8:
					pDialog.dismiss();
	            	showCustomToast("上传失败", false);
					break;
			default:
				break;
			}
		}
	};
	
	
	
	/**
	 * 对返回数据处理
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		L.e("resultCode=="+resultCode+"requestCode=="+requestCode);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CallSystemApp.EXTRA_TAKE_PHOTOS://拍照返回
				L.d("拍照返回");
				pDialog = ProgressDialog.show(this, "请等待...", "正在上传图片...",true); 	
			    pDialog.setCancelable(true); 
			    mHandler.sendEmptyMessage(7);
				
				break;
             case 1:
            	//重新调用相机拍照
         		FileName = getSystemTime() + ".jpg";
         		photoPath = Constant.SAVE_IMG_PATH + File.separator
         				+ FileName;
         		CallSystemApp.callCamera(MainAttendanceActivity.this, photoPath);
         		
			    break;
				
			
			default:
				break;
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		isRun = true;
		super.onResume();
		if (Img_List.size()>0) {
			takingpicture_name.setText("预览");
		}else {
			takingpicture_name.setText("拍照");
		}
		
		if (query().equals("0")) {
			switchbutton.setSwitchState(false);
		}else if (query().equals("1")) {
			switchbutton.setSwitchState(true);
		}
		getNetData(calendar.get(Calendar.MONTH)+1);
	
	}

	
	/**
	 * 传图片到服务器
	 */
	private void setImageToNet(String path) {
		//获得系统时间2014-09-20
		SimpleDateFormat  formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date  curDate=new  Date(System.currentTimeMillis());//获取当前时间
		String  time =formatter.format(curDate);
		
		String companyID = preferences.getInt(Content.COMPS_ID, 1016) + "";
		String modulePage = "Attendance";//考勤服务器文件夹
		String picData = "";
		try {
			picData = Base64Util.encodeBase64File(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   UpLoadImgSignText upLoadImgSignText = new UpLoadImgSignText(picData, modulePage, FileName, companyID, time + "\n" + location);
	   upLoadImgSignText.setUpLoadResult(MainAttendanceActivity.this);
	   new Thread(upLoadImgSignText).start();
	}

	@Override
	public void onUpLoadResult(String FileName, String FilePath,
			boolean isSuccess) {
		// TODO Auto-generated method stub
		

		if (isSuccess) {
			//只存储最新的一张图片
			pList.clear();
			pList.add(FilePath);
			//图片上传成功
			L.i("------------->:" +   FilePath);
			pDialog.dismiss();
			ClueCustomToast.showToast(MainAttendanceActivity.this,
					R.drawable.toast_sucess, "图片上传成功");
			mHandler.sendEmptyMessage(2);
		
		}else {
			//图片上传失败
			L.i("------------->:" + "上传失败" );
			ClueCustomToast.showToast(MainAttendanceActivity.this,
					R.drawable.toast_defeat, "图片上传失败！");
			pDialog.dismiss();
			
		}
	}
	
	
	 /**
	  * 向二维数组赋值
	  */
	private void setItemDate(){
		
		//查询全部
		ScheduleList = ScheduleManager.getInstance(context).getSchedules();
		for (int i = 0; i < ScheduleList.size(); i++) {
			map = new HashMap<String, Object>();
			map.put("name", ScheduleList.get(i).getName());
			ItemLists.add(map);
			Log.e(TAG,"ItemLists.SIZE:" +  ItemLists.size());
			
		}
		// 启动线程
	   new TimeThread().start();
	}

	@Override
	public void onDialogDown(int position, int type) {
		// TODO Auto-generated method stub
		switch (type) {
		case ATTENDANCE_DIALOG:
			HashMap<String, Object> map = ItemLists.get(position);
			shift_text.setText((CharSequence) map.get("name"));
			L.e("====>:" + map.get("name"));
			//根据选择名称查询对应id
			ScheduleIDList = ScheduleManager.getInstance(context).getSchedulesID((map.get("name")).toString());
			ScheduleID = ScheduleIDList.get(0).getID();
			break;

		default:
			break;
		}
		
	}
	
	/**
	 * 判断是否输入完成
	 * @return 
	 */
	private boolean isWrite(){
		
		if (ScheduleID == 0) {
			showDialog("请选择班次！");
			return false;
		}
//		if (pList.size()<=0) {
//			showDialog("请拍照后提交！");
//			return false;
//		}
//		if (note_text.getText().toString().trim().equals("") ) {
//			showDialog("请填写备注！");
//			return false;
//		}
		
		return true;
	
	}
	
	public void showDialog(String TXT){
		// Toast
		 ClueCustomToast.showToast(MainAttendanceActivity.this,
				R.drawable.toast_warn, TXT);

	}
	
	
	/**
	 * 定位监听
	 */
	private BDLocationListener mLocationListener = new BDLocationListener() {

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			
			if (location != null) {
				Lng = (Double) location.getLatitude();// 经度
				Lat = (Double) location.getLongitude();// 维度
				LocationUtil.stopLocation();
				LocationUtil.startReverseGeoCode(Lng, Lat,
						getGeoCoderResultListener);	
			}else {
				return;
			}
			//LocationUtil.stopLocation();
		}
	};

	OnGetGeoCoderResultListener getGeoCoderResultListener = new OnGetGeoCoderResultListener() {

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			// TODO Auto-generated method stub
			if (result == null) {
				return;
			}

			location = result.getAddress();// 地理位置
			if (location == null || location.equals("null")) {
				location = "";
			}
			LocationUtil.stopReverseGeoCode();
		}

		@Override
		public void onGetGeoCodeResult(GeoCodeResult arg0) {
			// TODO Auto-generated method stub

		}
	};
	
	/**
	 * 保存UI数据
	 */
	private void saveUiData(){
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("isSubmit", isSubmit);
		map.put("ScheduleID", ScheduleID);
		map.put("Administration", shift_text.getText().toString().trim());
		if (pList.size()>0 && Img_List.size()>0) {
			map.put("PhotoPath", Img_List.get(0));
			map.put("PhotoUrl", pList.get(0));
		}
		map.put("Note", note_text.getText().toString().trim());
		boolean bool = AttendanceSharedPreferences.saveData(MainAttendanceActivity.this, sharedPreferencesFileName, map);
		if (bool) {
			L.e("ui数据存储成功");
		}else {
			L.e("ui数据存储失败");
		}
	}
	
	/**
	 * 获取UI数据
	 */
	private void getUiData(){
		HashMap<String, Object> map = new HashMap<String, Object>();
		map = AttendanceSharedPreferences.getData(MainAttendanceActivity.this, sharedPreferencesFileName);
		if (map.size()>0) {
			//行政班一直保存，不清除
			String ads  = map.get("Administration")+"";
			if (!ads.equals("") && ads.length()>0 && !ads.equals("null") && ads != null) {
				shift_text.setText(map.get("Administration")+"");
				ScheduleID = Integer.parseInt((String) (map.get("ScheduleID")));
			}
			if ((map.get("isSubmit")).equals("0")) {
				isSubmit = map.get("isSubmit")+"";
				String PhotoPath = (String) map.get("PhotoPath");
				String PhotoUrl = (String) map.get("PhotoUrl");
				if (PhotoPath != null && PhotoUrl != null && PhotoPath.length()>0 && PhotoUrl.length()>0
						&& !PhotoPath.equals("null") && !PhotoUrl.equals("null")) {
				pList.add(map.get("PhotoUrl")+"");
				Img_List.add(map.get("PhotoPath")+"");
				takingpicture_name.setText("预览");
				}
				if (!(map.get("Note")).equals("")) {
					note_text.setText(map.get("Note")+"");
					note.setVisibility(View.GONE);
					note_write_layout.setVisibility(View.VISIBLE);
				}else {
					note.setVisibility(View.VISIBLE);
					note_write_layout.setVisibility(View.GONE);
				}
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
	    saveUiData();
	    isRun = false;
		super.onDestroy();
	}
	
	
	/**
	 * 考勤提交提示框
	 * @param isIn "In"表示上班,"Out"表示下班
	 * @param msg 提示语
	 * @param isSub  是否直接提交考勤
	 */
	private void showAlertDialog(final String isIn,String msg,boolean isSub){
		if (isSub) {
			if (pList.size()==0) {
				setNetData(note_text.getText().toString().trim(), isIn,"", ScheduleID);
			}else {
				setNetData(note_text.getText().toString().trim(), isIn, pList.get(0), ScheduleID);
			}
		}else {
			AlertDialog.Builder alertDialog = new Builder(MainAttendanceActivity.this);
			alertDialog.setTitle("提示");
			alertDialog.setMessage(msg);
			alertDialog.setPositiveButton("确定", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if (pList.size()==0) {
						setNetData(note_text.getText().toString().trim(), isIn,"", ScheduleID);
					}else {
						setNetData(note_text.getText().toString().trim(), isIn, pList.get(0), ScheduleID);
					}
					dialog.dismiss();
				}
			});
			alertDialog.setNegativeButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			alertDialog.create().show();
		}
	
	}
}
