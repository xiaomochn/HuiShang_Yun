package com.huishangyun.View;

import java.lang.reflect.Field;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

/**
 * 自定义的日期控件，只有年和月，没有日
 * 
 * @author hbzhang 20140530
 */
public class YMPickerDialog extends DatePickerDialog
{
	public YMPickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear)
	{
		super(context, callBack, year, monthOfYear, 3);
		this.setTitle(year + "年" + (monthOfYear + 1) + "月");
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day)
	{
		super.onDateChanged(view, year, month, day);
		this.setTitle(year + "年" + (month + 1) + "月");
	}

	/**
	 * 查找出‘日’的View，并将其隐藏
	 * 
	 * @see android.app.DatePickerDialog#show()
	 */
	@Override
	public void show()
	{
		super.show();
		
		DatePicker datePicker = findDatePicker((ViewGroup) this.getWindow().getDecorView());
		Class pickerClass = datePicker.getClass();
		Field[] fields = pickerClass.getDeclaredFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			if ("mDayPicker".equals(fieldName) || "mDaySpinner".equals(fieldName)) {
				field.setAccessible(true);
				try
				{
					View dayView;
					dayView = (View) field.get(datePicker);
					dayView.setVisibility(View.GONE);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				break;
			}
		}
	}

	/**
	 * 从当前Dialog中查找DatePicker子控件
	 * 
	 * @param group
	 * @return
	 */
	private DatePicker findDatePicker(ViewGroup group)
	{
		if (group != null)
		{
			for (int i = 0, j = group.getChildCount(); i < j; i++)
			{
				View child = group.getChildAt(i);
				if (child instanceof DatePicker)
				{
					return (DatePicker) child;
				}
				else if (child instanceof ViewGroup)
				{
					DatePicker result = findDatePicker((ViewGroup) child);
					if (result != null)
						return result;
				}
			}
		}
		return null;
	}
}
