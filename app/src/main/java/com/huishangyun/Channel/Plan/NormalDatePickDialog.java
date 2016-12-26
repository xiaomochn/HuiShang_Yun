package com.huishangyun.Channel.Plan;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;

/**
 * 自定义日期控件对话框
 * 使用方法：如果想作为普通日期控件调用：NormalDatePickDialog.setDate(getContext(),start_date, null);
 * 如果想自定义刚开始显示时间：NormalDatePickDialog.setDate(getContext(),end_date, "2014-11-12");
 * @author xsl
 *
 */
public class NormalDatePickDialog {
	
	public NormalDatePickDialog(){
	}
	
	/**
	 * 日期控件对话框
	 * @param context 上下文对象
	 * @param tv  要写入的TextView控件
	 * @param str 格式必须是：2014-12-11
	 */
	public static void setDate(Context context,final TextView tv,String str){
		Calendar cal = Calendar.getInstance();
		String[] temper = null;
		int year;
		int monthOfYear;
		int dayOfMonth;
		if (str==null||str.equals("") ) {
			 year = cal.get(Calendar.YEAR);
			 monthOfYear = cal.get(Calendar.MONTH);
			 dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		}else {
			temper = str.split("-");
			year = Integer.parseInt(temper[0]);
			monthOfYear = Integer.parseInt(temper[1])-1;
			dayOfMonth = Integer.parseInt(temper[2]);
		}
	
		Dialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// TODO Auto-generated method stub
				Date date = new Date(year-1900, monthOfYear, dayOfMonth);
				SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");
				String time = format.format(date);
				tv.setText(time);
			}
		}, year, monthOfYear, dayOfMonth);
		dialog.show();
	}
	
	
	
}
