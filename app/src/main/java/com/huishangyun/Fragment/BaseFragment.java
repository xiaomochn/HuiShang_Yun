package com.huishangyun.Fragment;

import java.util.Calendar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import com.huishangyun.App.MyApplication;;

public class BaseFragment extends Fragment{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication myApp = MyApplication.getInstance();
	}
	/**
	 * toast提示
	 * 
	 * @param text
	 *            -提示内容
	 * @author Junhui
	 */
	public void showToast(String text) {
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 设置网络对话框
	 * 
	 * @author Junhui
	 */
	public void showNetUnAvailableDialog() {
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle("提示!");
		builder.setMessage("无可用网络，是否前去设置?");
		builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent("/");
				ComponentName cm = new ComponentName("com.android.settings",
						"com.android.settings.WirelessSettings");
				intent.setComponent(cm);
				intent.setAction("android.intent.action.VIEW");
				startActivityForResult(intent, 0);
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	/**
	 * 获取日期，精确到日
	 * 格式:2013-03-02*/
	public String getDate(){
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		return year+"-"+formatDate(month)+"-"+formatDate(day);
	}
	
	/**
	 * 获取时间，精确到秒
	 * 格式:09:23:51*/
	public String getTime(){
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);
		
		return formatDate(hour)+":"+formatDate(minute)+":"+formatDate(sec);
	}
	
	/**
	 * 格式化日期
	 * 如 3 返回 03*/
	public String formatDate(int params){
		if(params < 10)
			return "0"+params;
		else 
			return ""+params;
	}
}
