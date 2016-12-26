package com.huishangyun.Office.Attendance;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.huishangyun.Channel.Clues.ShowDialog;
import com.huishangyun.Interface.OnDialogDown;
import com.huishangyun.Office.Clock.DBManager;
import com.huishangyun.Office.Clock.ReSetAlram;
import com.huishangyun.Office.Wheel.StrericWheelAdapter;
import com.huishangyun.Office.Wheel.WheelView;
import com.huishangyun.Util.L;
import com.huishangyun.model.Constant;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.yun.R;


public class AttendanceRemind extends BaseActivity implements OnDialogDown {
	private static final String TAG = null;
	private RelativeLayout back;//返回
	private LinearLayout gotowork;//上班
	private LinearLayout gooffwork;//下班
	private TextView Monday;//星期一
	private TextView Tuesday;//星期二
	private TextView Wednesday;//星期三
	private TextView Thursday;//星期四
	private TextView Friday;//星期五
	private TextView Saturday;//星期六
	private TextView Sunday;//星期日
	private TextView All_week;//全
	
	private TextView ring;//响铃
	private TextView shake;//震动
	private TextView ring_shake;//响铃/震动
	
	private SeekBar voice_seekbar;//音量条
	private TextView voice_percent;//音量百分比
	
	private TextView music_filename;//音乐文件名
	private RelativeLayout select_musiclayout;
	private TextView confirm;//确认键
	private int gotowork_flag = 0,gooffwork_flag = 0;
	private int Monday_flag = 0,Tuesday_flag = 0,Wednesday_flag = 0,Thursday_flag = 0,Friday_flag = 0,
			    Saturday_flag = 0,Sunday_flag = 0,All_week_flag = 0,ring_flag = 0,shake_flag = 0,ring_shake_flag = 0;
	
    private Calendar calendar;
    private String Manager_ID;
    private String alrm_Time;//闹钟设置时间
    private AudioManager mAudioManager;
    //闹钟铃声系统存放路径
    private String strAlarmFolder = "/sdcard/music/alarms";  
    private String pickUri;
    private String repeatDays;//重复闹钟星期
    private String alarmTime;//闹钟时间
    private String isOpen;//闹钟是否开启
    List<String> repeatlist = new ArrayList<String>();
	private String index = "1";
	private String Volume;//当前闹钟系统音量
    private String MusicName;//当前选择铃声
    private String Vib = "1";//手机响铃方式
    private Cursor cursor2;//最新数据音频文件cursor
    ArrayList<HashMap<String, Object>> MusicLists = new ArrayList<HashMap<String,Object>>(); 
    private static final int MUSIC_DIALOG = 0; 
    private int FLAG;
    private AlertDialog.Builder builder = null;
	private  AlertDialog ad = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_office_attendance_remind);
		Manager_ID = preferences.getString(Constant.HUISHANGYUN_UID, "0");
		//获取闹钟系统服务
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//		delete("1");
//		delete("2");
		setAlram(index);
		init();
		//刚进来对对应Member_ID添加一条数据，以后的操作都是对数据进行修改
		//先查询看相关数据是否添加
	}
	
	
    /**
     * 设置参数
     */
	private void setAlram(String index){
		L.e("index" + index);
		if (!query(index)) {//假如没有查询没有数据，则建立登入人闹钟数据
			insertEmpty(index);
			query(index);
		}
	}
	
	/**
	 * 查询数据库，看当前人是否有闹钟记录数据
	 */
	private boolean query(String flag){
		String sql = "select* from mClock where mIndex = ? and ManagerID = ?";
		DBManager dbManager = new DBManager(getApplication());
		Map<String, String> map = dbManager.queryBySQL(sql, new String[] {flag,Manager_ID});
		if (map.size()>0) {	///假如当前登入人有数据，设置界面显示相应数据
			repeatDays = map.get("repeatDays");
			alarmTime = map.get("alarmTime");
			isOpen = map.get("isOpen");
			Volume = map.get("Volume");
			MusicName = map.get("MusicName");
			pickUri = map.get("pickUri");
			Vib = map.get("Vib");
			mHandler.sendEmptyMessage(0);
			return true;
		}else {
			return false;
		}
		
	}
	
	/**
	 * 删除当前登入人数据库内容
	 */
	private void delete(String index){
		DBManager dbManager = new DBManager(getApplication());
		if (dbManager.clockDelete(index,Manager_ID)) {
			Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();;
		}
	}
	
	
	/**
	 * 向数据库表单插入数据
	 * 给当前登入人建立一条空数据
	 */
	private void insertEmpty(String flag){
		DBManager dbManager = new DBManager(getApplication());
		 Date d = new Date(System.currentTimeMillis());
		 SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmm");
		 String date = sf.format(d);
		 String hours = date.substring(8, 10);//时
		 String Minute = date.substring(10, 12);//分
		 L.e( "date:" + date);
		 L.e( "hours:" + hours);
		 L.e( "Minute:" + Minute);
		 String time = hours + Minute;
		 L.e( "time:" + time);
		//获取闹钟铃声音量
		int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_ALARM );
		int current = mAudioManager.getStreamVolume( AudioManager.STREAM_ALARM );
		int progress = (int) ((current/(max*1.0))*100);
		// 得到系统铃声的uri
		Uri pickedUri_alarm = RingtoneManager
				.getActualDefaultRingtoneUri(AttendanceRemind.this,
						RingtoneManager.TYPE_ALARM);
		String alarmName = getAlarmName(pickedUri_alarm);
		String uri = pickedUri_alarm.toString().trim();
		//获取当前闹钟铃声
		if (dbManager.clockAdd(index,Manager_ID, "0", time, "",uri,progress + "",alarmName,"1")) {
			L.e( "无当前登入人闹钟数据，添加空数据成功！");
		}
	}
	
	/**
	 * 更新UI
	 */
   private Handler mHandler = new Handler(){
	   public void handleMessage(android.os.Message msg) {
		   super.handleMessage(msg);
		   switch (msg.what) {
		case 0:
			ring.setBackgroundResource(R.drawable.bt_sel);
			ring_flag=1;
			shake_flag = 0;
			shake.setBackgroundResource(R.drawable.edt_note);
			ring_shake_flag = 0;
			ring_shake.setBackgroundResource(R.drawable.edt_note);
			//获取闹钟铃声音量
			voice_seekbar.setProgress(Integer.parseInt(Volume));
			voice_percent.setText(Volume + "%");
			music_filename.setText(MusicName);
			setVib(Vib);
			//初始化滚轮数据
	        initWheel(R.id.hours,new String[]{"01","02","03","04","05","06","07","08","09","10","11","12",
	        		                          "13","14","15","16","17","18","19","20","21","22","23","00"}
	                                          ,Integer.parseInt(alarmTime.substring(0, 2))-1);
	        initWheel1(R.id.minutes,new String[]{"01","02","03","04","05","06","07","08","09","10",
	        		                             "11","12","13","14","15","16","17","18","19","20",
	                                             "21","22","23","24","25","26","27","28","29","30",
	                                             "31","32","33","34","35","36","37","38","39","40",
	                                             "41","42","43","44","45","46","47","48","49","50",
	                                             "51","52","53","54","55","56","57","58","59","00",}
	                                             ,Integer.parseInt(alarmTime.substring(2, 4))-1);
			String[] temp = null;
			temp = repeatDays.split(",");
			begin();
			for (int i = 0; i < temp.length; i++) {
				if (!temp[i].equals("")) {
					setBegainData(temp[i]);
				}else {
					break;
				}
				
			}
			break;
      case 1:
	   new ShowDialog(AttendanceRemind.this, MUSIC_DIALOG, MusicLists, "选择音乐",
				AttendanceRemind.this, "请选择音频！",music_filename.getText().toString().trim()).customDialog();
	  break;
		default:
			break;
		}
	   };
   };
   
   /**
    * 先全部初始化，然后再根据条件设置
    */
   private void begin(){
	
	 		Monday.setBackgroundResource(R.drawable.remindset_weekbg);
	 		Monday_flag = 0;
	 		Tuesday.setBackgroundResource(R.drawable.remindset_weekbg);
	 		Tuesday_flag=0;
	 		Wednesday.setBackgroundResource(R.drawable.remindset_weekbg);
	 		Wednesday_flag=0;
	 		Thursday.setBackgroundResource(R.drawable.remindset_weekbg);
	 		Thursday_flag=0;
	 		Friday.setBackgroundResource(R.drawable.remindset_weekbg);
	 		Friday_flag=0;
	 		Saturday.setBackgroundResource(R.drawable.remindset_weekbg);
	 		Saturday_flag=0;
	 		Sunday.setBackgroundResource(R.drawable.remindset_weekbg);
	 		Sunday_flag=0;
   }
   
   /**
    * 初始化手机响铃方式
    * @param vib
    */
   private void setVib(String vib){
  
		if (vib.equals("1")) {
			ring.setBackgroundResource(R.drawable.bt_sel);
			ring_flag=1;
			shake_flag = 0;
			shake.setBackgroundResource(R.drawable.edt_note);
			ring_shake_flag = 0;
			ring_shake.setBackgroundResource(R.drawable.edt_note);
			Vib = "1";

		}else if (vib.equals("2")) {
			shake.setBackgroundResource(R.drawable.bt_sel);
			shake_flag=1;
			ring_flag = 0;
			ring.setBackgroundResource(R.drawable.edt_note);
			ring_shake_flag = 0;
			ring_shake.setBackgroundResource(R.drawable.edt_note);
			Vib = "2";
		}else if (vib.equals("3")) {
			ring_shake.setBackgroundResource(R.drawable.bt_sel);
			ring_shake_flag=1;
			ring_flag = 0;
			ring.setBackgroundResource(R.drawable.edt_note);
			shake_flag = 0;
			shake.setBackgroundResource(R.drawable.edt_note);
			Vib = "3";
		}
   }
   
	/**
	 * 设置初始星期数据
	 */
	private void setBegainData(String index) {
		
		if (index.equals("2")) {
			Monday.setBackgroundResource(R.drawable.remindset_pressweekbg);
			Monday_flag = 1;
		} else if (index.equals("3")) {
			Tuesday.setBackgroundResource(R.drawable.remindset_pressweekbg);
			Tuesday_flag = 1;
		} else if (index.equals("4")) {
			Wednesday.setBackgroundResource(R.drawable.remindset_pressweekbg);
			Wednesday_flag = 1;
		} else if (index.equals("5")) {
			Thursday.setBackgroundResource(R.drawable.remindset_pressweekbg);
			Thursday_flag = 1;
		} else if (index.equals("6")) {
			Friday.setBackgroundResource(R.drawable.remindset_pressweekbg);
			Friday_flag = 1;
		} else if (index.equals("7")) {
			Saturday.setBackgroundResource(R.drawable.remindset_pressweekbg);
			Saturday_flag = 1;
		} else if (index.equals("1")) {
			Sunday.setBackgroundResource(R.drawable.remindset_pressweekbg);
			Sunday_flag = 1;
		}
	}
   
	
   
	/**
	 * 控件初始化
	 */
	private void init() {
		
		back = (RelativeLayout) findViewById(R.id.back);
		Monday = (TextView) findViewById(R.id.Monday);
		Tuesday = (TextView) findViewById(R.id.Tuesday);
		Wednesday = (TextView) findViewById(R.id.Wednesday);
		Thursday = (TextView) findViewById(R.id.Thursday);
		Friday = (TextView) findViewById(R.id.Friday);
		Saturday = (TextView) findViewById(R.id.Saturday);
		Sunday = (TextView) findViewById(R.id.Sunday);
		All_week = (TextView) findViewById(R.id.All_week);
		gotowork = (LinearLayout) findViewById(R.id.gotowork);
		gooffwork = (LinearLayout) findViewById(R.id.gooffwork);
				
		ring = (TextView) findViewById(R.id.ring);
		shake = (TextView) findViewById(R.id.shake);
		ring_shake = (TextView) findViewById(R.id.ring_shake);
				
		voice_seekbar = (SeekBar) findViewById(R.id.voice_seekbar);
		voice_percent = (TextView) findViewById(R.id.voice_percent);
		
		music_filename = (TextView) findViewById(R.id.music_filename);
		select_musiclayout = (RelativeLayout) findViewById(R.id.select_musiclayout);
//		//设置闹钟当前铃声名称显示
//		getName(RingtoneManager.TYPE_ALARM, music_filename);
		confirm = (TextView) findViewById(R.id.confirm);
		//默认显示上班设置界面
		if (gotowork_flag == 0 ) {
			gotowork.setBackgroundResource(R.drawable.attendance_pressstate);
			gooffwork.setBackgroundResource(R.drawable.attendance_currentstateright);
			gotowork_flag = 1;
			gooffwork_flag = 0;
		}
		
		
		back.setOnClickListener(new ButtonClickListener());
		gotowork.setOnClickListener(new ButtonClickListener());
		gooffwork.setOnClickListener(new ButtonClickListener());
		Monday.setOnClickListener(new ButtonClickListener());
		Tuesday.setOnClickListener(new ButtonClickListener());
		Wednesday.setOnClickListener(new ButtonClickListener());
		Thursday.setOnClickListener(new ButtonClickListener());
		Friday.setOnClickListener(new ButtonClickListener());
		
		Saturday.setOnClickListener(new ButtonClickListener());
		Sunday.setOnClickListener(new ButtonClickListener());
		All_week.setOnClickListener(new ButtonClickListener());
		ring.setOnClickListener(new ButtonClickListener());
		shake.setOnClickListener(new ButtonClickListener());
		ring_shake.setOnClickListener(new ButtonClickListener());
		confirm.setOnClickListener(new ButtonClickListener());
		select_musiclayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FLAG = 0;
				//打开一个对话框，选择是音频来源。。。。。。。。。
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
				alertDialog.setTitle("请选择铃声来源");
				alertDialog.setSingleChoiceItems(new String[] { "扫描SD卡音频","读取系统铃声" }, 0, new AlertDialogClick()); 
				alertDialog.setPositiveButton("确定", new AlertDialogClick()); 
				alertDialog.setNegativeButton("取消", new AlertDialogClick());
				alertDialog.show();
			}
		});
		
		//通过seekbar设置实时音量
		voice_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_ALARM );
				int progress1 =  (int) (progress/(100/max));//转换成音频设置值
				mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, progress1, 0);
				voice_percent.setText(progress + "%");
				Volume = progress + "";
			}
		});
		

		
	}
	
	private class ButtonClickListener implements View.OnClickListener{
		

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back://返回
			    finish();
				break;
			case R.id.gotowork://上班
				if (gotowork_flag == 0 ) {
					gotowork.setBackgroundResource(R.drawable.attendance_pressstate);
					gooffwork.setBackgroundResource(R.drawable.attendance_currentstateright);
					gotowork_flag = 1;
					gooffwork_flag = 0;
					index = "1";
					setAlram("1");
				}
				break;
			case R.id.gooffwork://下班
				if (gooffwork_flag == 0 ) {
					gooffwork.setBackgroundResource(R.drawable.attendance_pressstateright);
					gotowork.setBackgroundResource(R.drawable.attendance_currentstate);
					gooffwork_flag = 1;
					gotowork_flag = 0;
					index = "2";
					setAlram("2");
					
				}
				break;
			case R.id.Monday://星期一
				if (Monday_flag == 0) {
					Monday.setBackgroundResource(R.drawable.remindset_pressweekbg);
					Monday_flag = 1;
				}else {
					Monday.setBackgroundResource(R.drawable.remindset_weekbg);
					Monday_flag = 0;
				}
				break;
			case R.id.Tuesday://星期二
				if (Tuesday_flag==0) {
					Tuesday.setBackgroundResource(R.drawable.remindset_pressweekbg);
					Tuesday_flag=1;
				}else {
					Tuesday.setBackgroundResource(R.drawable.remindset_weekbg);
					Tuesday_flag=0;
				}
				break;
			case R.id.Wednesday://星期三
				if (Wednesday_flag==0) {
					Wednesday.setBackgroundResource(R.drawable.remindset_pressweekbg);
					Wednesday_flag=1;
				}else {
					Wednesday.setBackgroundResource(R.drawable.remindset_weekbg);
					Wednesday_flag=0;
				}
				break;
			case R.id.Thursday://星期四
				if (Thursday_flag==0) {
					Thursday.setBackgroundResource(R.drawable.remindset_pressweekbg);
					Thursday_flag=1;
				}else {
					Thursday.setBackgroundResource(R.drawable.remindset_weekbg);
					Thursday_flag=0;
				}
				break;	
			case R.id.Friday://星期五
				if (Friday_flag==0) {
					Friday.setBackgroundResource(R.drawable.remindset_pressweekbg);
					Friday_flag=1;
				}else {
					Friday.setBackgroundResource(R.drawable.remindset_weekbg);
					Friday_flag=0;
				}
				break;
			case R.id.Saturday://星期六
				if (Saturday_flag==0) {
					Saturday.setBackgroundResource(R.drawable.remindset_pressweekbg);
					Saturday_flag=1;
				}else {
					Saturday.setBackgroundResource(R.drawable.remindset_weekbg);
					Saturday_flag=0;
				}
				break;
			case R.id.Sunday://星期日
				if (Sunday_flag==0) {
					Sunday.setBackgroundResource(R.drawable.remindset_pressweekbg);
					Sunday_flag=1;
				}else {
					Sunday.setBackgroundResource(R.drawable.remindset_weekbg);
					Sunday_flag=0;
				}
				break;
			case R.id.All_week://全
				if (All_week_flag==0) {
					
					Monday.setBackgroundResource(R.drawable.remindset_pressweekbg);
					Monday_flag = 1;
					Tuesday.setBackgroundResource(R.drawable.remindset_pressweekbg);
					Tuesday_flag=1;
					Wednesday.setBackgroundResource(R.drawable.remindset_pressweekbg);
					Wednesday_flag=1;
					Thursday.setBackgroundResource(R.drawable.remindset_pressweekbg);
					Thursday_flag=1;
					Thursday.setBackgroundResource(R.drawable.remindset_pressweekbg);
					Thursday_flag=1;
					Friday.setBackgroundResource(R.drawable.remindset_pressweekbg);
					Friday_flag=1;
					Saturday.setBackgroundResource(R.drawable.remindset_pressweekbg);
					Saturday_flag=1;
					Sunday.setBackgroundResource(R.drawable.remindset_pressweekbg);
					Sunday_flag=1;
					
//					All_week.setBackgroundResource(R.drawable.remindset_pressallweekbg);
					All_week_flag=1;
				}else {
					begin();
					
//					All_week.setBackgroundResource(R.drawable.remindset_allweekbg);
					All_week_flag=0;
				}
				break;
			case R.id.ring://响铃
				if (ring_flag==0) {
					ring.setBackgroundResource(R.drawable.bt_sel);
					ring_flag=1;
					shake_flag = 0;
					shake.setBackgroundResource(R.drawable.edt_note);
					ring_shake_flag = 0;
					ring_shake.setBackgroundResource(R.drawable.edt_note);
					Vib = "1";

				}
				break;
			case R.id.shake://震动
				if (shake_flag==0) {
					shake.setBackgroundResource(R.drawable.bt_sel);
					shake_flag=1;
					ring_flag = 0;
					ring.setBackgroundResource(R.drawable.edt_note);
					ring_shake_flag = 0;
					ring_shake.setBackgroundResource(R.drawable.edt_note);
					Vib = "2";
				}
				break;
			case R.id.ring_shake://响铃/震动
				if (ring_shake_flag==0) {
					ring_shake.setBackgroundResource(R.drawable.bt_sel);
					ring_shake_flag=1;
					ring_flag = 0;
					ring.setBackgroundResource(R.drawable.edt_note);
					shake_flag = 0;
					shake.setBackgroundResource(R.drawable.edt_note);
					Vib = "3";
				}
				
				break;
			case R.id.confirm://确定
				 SimpleDateFormat sdf = null;
				 String date = null;
				//取值的方法
			    String hours = getWheelValue(R.id.hours);
			    String minutes = getWheelValue(R.id.minutes);
			    String alarmTime = hours + minutes;
			    L.e("++++pickUri:" + alarmTime);
			    //拼接设置重复日期字符串
			    //数据库数据写入
			    L.e("++++pickUri:" + pickUri);
			    L.e("++++MusicName:" + MusicName);
			    if (getRepeatDays().equals("")||getRepeatDays()==null) {
					showCustomToast("请选择闹钟重复周期", false);
				}else {
					setClockBySQL(index,"1", alarmTime, getRepeatDays(),pickUri,Volume,MusicName,Vib);
					new ReSetAlram(getApplicationContext(), Manager_ID,
							Integer.parseInt(index), Integer.parseInt(index)).setAlarm();
				}
				break;
			
			default:
				break;
			}
			
		}
		
	}
	
	
	 
	/**
	 * 将设置好的闹钟数据存入数据库
	 * （修改数据操作，因为已经对当前登入人填写了空数据）
	 */
	private boolean setClockBySQL(String index,String isOpen,String alarmTime,
			String repeatDays,String pickUri,String Volume,String MusicName,String Vib){
		DBManager dbManager = new DBManager(getApplication());
		if (dbManager.clockUpdate(index,Manager_ID, isOpen, alarmTime, repeatDays,pickUri,Volume,MusicName,Vib)) {
			L.e(TAG, "数据修改成功！" );
			L.e("index:" + index + "\n" + "isOpen:" + isOpen + "\n" + "Manager_ID:" + Manager_ID 
					+ "\n" + "isOpen:" + isOpen + "\n"
				       + "alarmTime:" + alarmTime + "\n" 
						+ "repeatDays:" + repeatDays+ "\n" + "pickUri:" + pickUri +
						"\n" + "Volume:" + Volume + "\n" + "MusicName:" + MusicName 
						+ "\n" + "Vib:" + Vib);
			if (index.equals("1")) {
				showCustomToast("上班闹钟设置成功！", true);
			}else if (index.equals("2")) {
				showCustomToast("下班闹钟设置成功！", true);
			}
			return true;
		}else {
			
			L.e("index:" + index + "\n" + "isOpen:" + isOpen + "\n" + "Manager_ID:" + Manager_ID 
					+ "\n" + "isOpen:" + isOpen + "\n"
				       + "alarmTime:" + alarmTime + "\n" 
						+ "repeatDays:" + repeatDays+ "\n" + "pickUri:" + pickUri +
						"\n" + "Volume:" + Volume + "\n" + "MusicName:" + MusicName 
						+ "\n" + "Vib:" + Vib);
			L.e(TAG, "数据修改失败！");
			if (index.equals("1")) {
				showCustomToast("上班闹钟设置失败！", false);
			}else if (index.equals("2")) {
				showCustomToast("下班闹钟设置失败！", false);
			}
			
			return false;
		}
	}
	
	/**
	 * 根据设置导出重复日期字符串
	 */
	private String getRepeatDays(){
		 //拼接重复闹钟字符串
	    StringBuffer plansStringBuffer = new StringBuffer("");
	    int index = 0;
		if (Monday_flag==1) {
			if (index != 0) {
				plansStringBuffer.append(",");
			}else {
				index = 1;
			}
			plansStringBuffer.append("2");
		}
		if (Tuesday_flag == 1) {
			if (index != 0) {
				plansStringBuffer.append(",");
			}else {
				index = 1;
			}
			plansStringBuffer.append("3");
			
		}
		if (Wednesday_flag == 1) {
			if (index != 0) {
				plansStringBuffer.append(",");
			}else {
				index = 1;
			}
			plansStringBuffer.append("4");
			
		}
		if (Thursday_flag == 1) {
			if (index != 0) {
				plansStringBuffer.append(",");
			}else {
				index = 1;
			}
			plansStringBuffer.append("5");
		}
		if (Friday_flag == 1) {
			if (index != 0) {
				plansStringBuffer.append(",");
			}else {
				index = 1;
			}
			plansStringBuffer.append("6");
		}
		if (Saturday_flag == 1) {
			if (index != 0) {
				plansStringBuffer.append(",");
			}else {
				index = 1;
			}
			plansStringBuffer.append("7");
		}
		if (Sunday_flag == 1) {
			if (index != 0) {
				plansStringBuffer.append(",");
			}else {
				index = 1;
			}
			plansStringBuffer.append("1");
		}
		return plansStringBuffer.toString();
	}
	
	
	/**
	 * 获取当前控件的值
	 * @param id R.id.控件名
	 * @return
	 */
	 private String getWheelValue(int id) {
	    	return getWheel(id).getCurrentItemValue();
	    }
	   
	    /**
	     * Returns wheel by Id
	     * @param id the wheel Id
	     * @return the wheel with passed Id
	     * 我添加的
	     * 
	     */
	    private WheelView getWheel(int id) {
	    	return (WheelView) findViewById(id);
	    }
	    
	    private void initWheel(int id,String[] strContents,int initial) {
	        WheelView wheel = getWheel(id);
	        wheel.setAdapter(new StrericWheelAdapter(strContents));
	        wheel.setCurrentItem(initial);//滚轮初始位置
	        wheel.setLabel("时");
	        
	        wheel.setCyclic(true);
	        wheel.setInterpolator(new AnticipateOvershootInterpolator());
	    }
	    
	    private void initWheel1(int id,String[] strContents,int initial) {
	        WheelView wheel = getWheel(id);
	        wheel.setAdapter(new StrericWheelAdapter(strContents));
	        wheel.setCurrentItem(initial);//设置初始时间
	        wheel.setLabel("分");
	        
	        wheel.setCyclic(true);
	        wheel.setInterpolator(new AnticipateOvershootInterpolator());
	    }
	    
	    /**
	     * 返回值设置
	     */
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        if (resultCode != RESULT_OK || (requestCode != 1 && requestCode != 2)) {
	              return;
	         }
	        
	         try  
             {  
                 //得到我们选择的铃声  
                 Uri pickedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);  
                 //将我们选择的铃声设置成为默认  
                 if (pickedUri != null)  
                 {  
				RingtoneManager.setActualDefaultRingtoneUri(
						AttendanceRemind.this,RingtoneManager.TYPE_ALARM,
						pickedUri);
				 pickUri = pickedUri.toString();
				 L.e("pickUri=========>:" + pickUri);
                 }  
             }  
             catch (Exception e)  {  
            	 e.printStackTrace();
             }  
	         getName(RingtoneManager.TYPE_ALARM, music_filename);
	  }
	    
	    /**
		 * 获得当前选中铃声的名称
		 * 
		 * @param type
		 *            铃声的类型
		 * @param button
		 *            按钮
		 */
		private void getName(int type, TextView tv) {
			Uri pickedUri = RingtoneManager.getActualDefaultRingtoneUri(
					AttendanceRemind.this, type);
			// 当picked_Uri=null, 显示静音
			if (pickedUri == null) {
				String ring_name = "silent";
				tv.setText(ring_name);
				MusicName = ring_name;
			}else {
				// 查询数据库，查询当前的铃声名称并显示
				Cursor cursor = this.getContentResolver()
						.query(pickedUri,
								new String[] { MediaStore.Audio.Media.TITLE }, null,
								null, null);
				if (cursor != null) {
					if (cursor.moveToFirst()) {
						String ring_name = cursor.getString(0);
						tv.setText(ring_name);
						MusicName = ring_name;
					}
					cursor.close();
				}
			}
			
		}
		
	/**
	 * 获取当前系统闹铃铃声
	 */
	private String getAlarmName(Uri pickedUri) {
		String alarmName = null;
		// 当picked_Uri=null, 显示静音
		if (pickedUri == null) {
			String ring_name = "silent";
			alarmName = ring_name;
			return null;
		}else {
			try {
			// 查询数据库，查询当前的铃声名称并显示
			Cursor cursor = this.getContentResolver()
					.query(pickedUri,
							new String[] {MediaStore.Audio.Media.TITLE}, null,
							null, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					String ring_name = cursor.getString(0);
					alarmName = ring_name;
				}
				cursor.close();
			}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				L.e("主动抛异常报错：" + "no such column: title (code 1): , while compiling: SELECT title FROM system WHERE (name=?)");
				return null;
			}
		}
	
		return alarmName;
	}
	
	

	/**
	 * 发送广播去浏览存储卡更新系统音频文件数据库，避免数据库中还保留已删除音频文件信息。
	 */
	private void scanSdCard() {

		
		builder = new AlertDialog.Builder(context);
		builder.setMessage("正在扫描存储卡...");
		//设置点击屏幕不消失。
		builder.setCancelable(false);
		ad = builder.create();
		ad.show();
		//这个方法可以在4.4以上运行
		MediaScannerConnection.scanFile(this, new String[] { Environment
				.getExternalStorageDirectory().getAbsolutePath().toString() }, null,
				new MediaScannerConnection.OnScanCompletedListener() {
					/*
					 * (non-Javadoc)
					 * 监听扫描文件完成后的操作。
					 * @see
					 * android.media.MediaScannerConnection.OnScanCompletedListener
					 * #onScanCompleted(java.lang.String, android.net.Uri)
					 */
					public void onScanCompleted(String path, Uri uri) {
						
						cursor2 = context.getContentResolver().query(
								MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
								new String[] { MediaStore.Audio.Media.TITLE,
										MediaStore.Audio.Media.DURATION,
										MediaStore.Audio.Media.ARTIST,
										MediaStore.Audio.Media._ID,
										MediaStore.Audio.Media.DISPLAY_NAME,
										MediaStore.Audio.Media.DATA }, null,
								null, null);
							ad.cancel();
							
						 getMusicInfo(cursor2);
					}
				});
	}

   
	/**
	 * 将查询出来的音频文件信息装如集合
	 * @param cursor
	 * @return 
	 */
	private void getMusicInfo(Cursor cursor){
		cursor.moveToFirst();
		for(int i=0; i < cursor.getCount();i++){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("musicId", cursor.getInt(3));//音频id
			map.put("name", cursor.getString(0));//音频名称
			map.put("musicPath", cursor.getString(5));//音频路径
			MusicLists.add(map);
			cursor.moveToNext();
		}
		mHandler.sendEmptyMessage(1);
	}


	@Override
	public void onDialogDown(int position, int type) {
		// TODO Auto-generated method stub
		switch (type) {
		case MUSIC_DIALOG:
			HashMap<String, Object> map = MusicLists.get(position);
			MusicName = (String) map.get("name");
			pickUri = (String) map.get("musicPath");
			L.e("音频文件绝对路径pickUri:" + pickUri);
			music_filename.setText((CharSequence) map.get("name"));
			break;

		default:
			break;
		}
	}
	
	/**
	 * 选择音频文件对话框单击事件类
	 * 
	 * @author xsl
	 * 
	 */
	private class AlertDialogClick implements DialogInterface.OnClickListener {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// which表示单击的按钮索引，所有的选项索引都是大于0，按钮索引都是小于0的。
			if (which >= 0) {
				// 如果单击的是列表项，将当前列表项的索引保存在index中。
				// 如果想单击列表项后关闭对话框，可在此处调用dialog.cancel()
				// 或是用dialog.dismiss()方法。
				FLAG = which;
			} else {
				// 用户单击的是【确定】按钮
				if (which == DialogInterface.BUTTON_POSITIVE) {
					if (FLAG==0) {
						if (MusicLists.size()<=0) {
							scanSdCard();
						}else {
						
						new ShowDialog(AttendanceRemind.this, MUSIC_DIALOG, MusicLists, "选择音乐",
								AttendanceRemind.this, "请选择音频！",music_filename.getText().toString().trim()).customDialog();
					
						}
					}else if (FLAG==1) {
						//打开系统铃声设置  
		                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);  
		                //设置铃声类型和title  
		                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);  
		                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "设置亿企云闹钟铃声"); 
		                L.e("pickUri:" + pickUri);
		                //获取数据库铃声的uri
						Uri pickedUri_alarm = Uri.parse(pickUri);
						L.e("pickedUri_alarm:" + pickedUri_alarm);
						// Put checkmark next to the current ringtone for this contact
						if (pickedUri_alarm != null) {
							intent.putExtra(
									RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
									pickedUri_alarm);
						}
		                //当设置完成之后返回到当前的Activity  
		                startActivityForResult(intent, Integer.parseInt(index)); 
					
				} else if (which == DialogInterface.BUTTON_NEGATIVE) {
					// 用户单击的是【取消】按钮
					dialog.dismiss();
				}

			}

		}
	}
  }
	

}
