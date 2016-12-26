package com.huishangyun.View;

import com.baidu.android.bbalbs.common.a.c;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

public class DataAlertDialog extends AlertDialog.Builder{
	private Context context;
	public DataAlertDialog(Context context) {
		
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public AlertDialog create() {
		// TODO Auto-generated method stub
		
		//AlertDialog alertDialog = new AlertDialog();
		return super.create();
	}

	

}
