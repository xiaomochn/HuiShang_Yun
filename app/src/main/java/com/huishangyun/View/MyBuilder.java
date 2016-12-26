package com.huishangyun.View;

import android.content.Context;

public class MyBuilder extends MyAlerDialog.Builder{
	private Context context;
	public MyBuilder(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public MyAlerDialog create() {
		// TODO Auto-generated method stub
		
		return new MyAlerDialog(context);
	}

}
