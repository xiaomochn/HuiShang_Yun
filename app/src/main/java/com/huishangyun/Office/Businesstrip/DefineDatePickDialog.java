package com.huishangyun.Office.Businesstrip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import com.huishangyun.yun.R;

/**
 * 日期时间选择控件 使用方法： private EditText inputDate;//需要设置的日期时间文本编辑框 private String
 * initDateTime="2014年9月3日 14:44",//初始日期时间值 在点击事件中使用：
 * inputDate.setOnClickListener(new OnClickListener() {
 * 
 * @Override public void onClick(View v) { DateTimePickDialogUtil
 *           dateTimePicKDialog=new
 *           DateTimePickDialogUtil(SinvestigateActivity.this,initDateTime);
 *           dateTimePicKDialog.dateTimePicKDialog(inputDate);
 * 
 *           } });
 * 
 * @author xsl
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DefineDatePickDialog implements OnDateChangedListener,
		OnTimeChangedListener {
	private DatePicker datePicker;
	private TimePicker timePicker;
	private AlertDialog ad;
	private String dateTime;
	private String initDateTime;
	private Activity activity;
	private Calendar calendar;
	private  int i = 0;
	String[] minuts = new String[]{"00","30","00","30","00","30","00","30","00","30","00","30","00","30"};

	/**
	 * 日期时间弹出选择框构造函数
	 * 
	 * @param activity
	 *            ：调用的父activity
	 * @param initDateTime
	 *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
	 */
	public DefineDatePickDialog(Activity activity, String initDateTime) {
		this.activity = activity;
		this.initDateTime = initDateTime;

	}

	public void init(DatePicker datePicker, TimePicker timePicker) {
		calendar = Calendar.getInstance();
		if (!(null == initDateTime || "".equals(initDateTime))) {
			calendar = this.getCalendarByInintData(initDateTime);
		} else {
			initDateTime = calendar.get(Calendar.YEAR) + "年"
					+ calendar.get(Calendar.MONTH) + "月"
					+ calendar.get(Calendar.DAY_OF_MONTH) + "日 "
					+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
					+ calendar.get(Calendar.MINUTE);
		}

		datePicker.init(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), this);
		
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(0);
		//分时间间隔显示方式设置
		setNumberPickerTextSize(timePicker);
//		timePicker.setIs24HourView(true);
//		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
//		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
	}
	
	
	
	 private List<NumberPicker> findNumberPicker(ViewGroup viewGroup)
	     {
	         List<NumberPicker> npList = new ArrayList<NumberPicker>();
	         View child = null;
	         
	         if (null != viewGroup)
	         {
	             for (int i = 0; i < viewGroup.getChildCount(); i++)
	             {
	                 child = viewGroup.getChildAt(i);
	                 if (child instanceof NumberPicker)
	                 {
	                     npList.add((NumberPicker)child);
	                 }
	                 else if (child instanceof LinearLayout)
	                 {
	                     List<NumberPicker> result = findNumberPicker((ViewGroup)child);
	                     if (result.size() > 0)
	                     {
	                         return result;
	                     }
	                 }
	             }
	         }
	         
	         return npList;
	     }

	
	
	@SuppressLint("NewApi")
	private void setNumberPickerTextSize(ViewGroup viewGroup){
	         List<NumberPicker> npList = findNumberPicker(viewGroup);
	         if (null != npList)
	         {
	             for (NumberPicker mMinuteSpinner : npList)
	             {
	              if(mMinuteSpinner.toString().contains("id/minute")){
	              mMinuteSpinner.setMinValue(0);  
	              mMinuteSpinner.setMaxValue(minuts.length-1);  
	              mMinuteSpinner.setDisplayedValues(minuts);  
	             }
	              
	             }
	         }
	     }
	


	/**
	 * 弹出日期时间选择框方法
	 * 
	 * @param inputDate
	 *            :为需要设置的日期时间文本编辑框
	 * @return
	 */
	public AlertDialog dateTimePicKDialog(final TextView inputDate) {
		
		LinearLayout dateTimeLayout = (LinearLayout) activity
				.getLayoutInflater().inflate(R.layout.office_common_datetime, null);
		datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
		timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
		
		init(datePicker, timePicker);
		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(this);

		ad = new AlertDialog.Builder(activity)
				.setTitle(initDateTime)
				.setView(dateTimeLayout)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						inputDate.setText(dateTime);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						inputDate.setText("");
					}
				}).show();
		
		return ad;
	}
	
	public void setAlertDialog(AlertDialog ad) {
		this.ad = ad;
		onDateChanged(null, 0, 0, 0);
	}
	
	public String getDataTime() {
		return dateTime;
	}
	
	
	/**
	 * 获取builder对象
	 * @return
	 */
	public AlertDialog.Builder dateTimePicKDialog() {
		LinearLayout dateTimeLayout = (LinearLayout) activity
				.getLayoutInflater().inflate(R.layout.office_common_datetime, null);
		datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
		timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
		init(datePicker, timePicker);
//		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(this);
		AlertDialog.Builder builder = new Builder(activity).setTitle(initDateTime)
				.setView(dateTimeLayout);

		return builder;
	}


	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		onDateChanged(null, 0, 0, 0);
	}

	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// 获得日历实例
		Calendar calendar = Calendar.getInstance();

		calendar.set(datePicker.getYear(), datePicker.getMonth(),
				datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
				backMin((timePicker.getCurrentMinute()%4)*30));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		dateTime = sdf.format(calendar.getTime());
		ad.setTitle(dateTime);
	}
	
	private int backMin(int min){
		if (min==60) {
			min =  0;
		}else if (min==90) {
			min =  30;
		}
		return min;
	}
 
	/**
	 * 实现将初始日期时间2014年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
	 * 
	 * @param initDateTime
	 *            初始日期时间值 字符串型
	 * @return Calendar
	 */
	private Calendar getCalendarByInintData(String initDateTime) {
		// 将初始日期时间2014年07月02日 16:45 拆分成年 月 日 时 分 秒
		String date = spliteString(initDateTime, "日", "index", "front"); // 日期
		String time = spliteString(initDateTime, "日", "index", "back"); // 时间

		String yearStr = spliteString(date, "年", "index", "front"); // 年份
		String monthAndDay = spliteString(date, "年", "index", "back"); // 月日

		String monthStr = spliteString(monthAndDay, "月", "index", "front"); // 月
		String dayStr = spliteString(monthAndDay, "月", "index", "back"); // 日

		String hourStr = spliteString(time, ":", "index", "front"); // 时
		String minuteStr = spliteString(time, ":", "index", "back"); // 分

		int currentYear = Integer.valueOf(yearStr.trim()).intValue();
		int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
		int currentDay = Integer.valueOf(dayStr.trim()).intValue();
		int currentHour = Integer.valueOf(hourStr.trim()).intValue();
		int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();
		calendar.set(currentYear, currentMonth, currentDay, currentHour,
				currentMinute);
		
		return calendar;
	}

	/**
	 * 截取子串
	 * 
	 * @param srcStr
	 *            源串
	 * @param pattern
	 *            匹配模式
	 * @param indexOrLast
	 * @param frontOrBack
	 * @return
	 */
	public static String spliteString(String srcStr, String pattern,
			String indexOrLast, String frontOrBack) {
		String result = "";
		int loc = -1;
		if (indexOrLast.equalsIgnoreCase("index")) {
			loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
		} else {
			loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
		}
		if (frontOrBack.equalsIgnoreCase("front")) {
			if (loc != -1)
				result = srcStr.substring(0, loc); // 截取子串
		} else {
			if (loc != -1)
				result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
		}
		return result;
	}

}
