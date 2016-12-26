package com.huishangyun.Channel.xiaoliang;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.huishangyun.View.YMPickerDialog;

public class Common_Xiao {
	
//	private Context context;
//	public Common_Xiao(Context context){
//		this.context = context;
//	}
	
	/**
	 * 选择年、月、日时间控件
	 */
	public void datePickDialog(Context context, final TextView tv){
		Calendar cal = Calendar.getInstance();
		Dialog dialog = new DatePickerDialog(context, 
				new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// TODO Auto-generated method stub
				Date date = new Date(year - 1900, monthOfYear, dayOfMonth); //获取时间转换为Date对象  
                SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
                String times = sf1.format(date);
                tv.setText(times);
			}
		}, cal.get(Calendar.YEAR),
		cal.get(Calendar.MONTH), 
		cal.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}
	
	/**
	 * 选择年、月时间控件
	 */
	public void datePickDialog2(Context context, final TextView tv){		
		final Calendar cal = Calendar.getInstance();		
		YMPickerDialog mDialog = new YMPickerDialog(context,
			new DatePickerDialog.OnDateSetListener() {
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					Date date = new Date(year - 1900, monthOfYear, dayOfMonth); //获取时间转换为Date对象  
	                SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM");
	                String times = sf1.format(date);
	                tv.setText(times);
				}
			}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
		mDialog.show();
		
	}
	
	// 获取listview的高度
 	public static void setListViewHeight(ListView listView, BaseAdapter adapter) {
		adapter = (BaseAdapter) listView.getAdapter();
		if (adapter == null) {
			return;
		}
		int totalHeight = 0;
		// listAdapter.getCount()返回数据项的数目
		for (int i = 0, len = adapter.getCount(); i < len; i++) {
			View listItem = adapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (adapter.getCount() - 1));
//		 		params.height += 5;// 因为我在listview的属性添加了padding=5dip

		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
//			Log.i("-----------", "获取到listview的高度:" + params);
		listView.setLayoutParams(params);
 	}
}
