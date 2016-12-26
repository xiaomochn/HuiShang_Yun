package com.huishangyun.View;

import com.huishangyun.Util.Common;
import com.huishangyun.Util.L;

import android.app.AlertDialog;
import android.content.Context;

public class MyAlerDialog extends AlertDialog{

	protected MyAlerDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		L.e("dismiss");
		Common.stopRecovid();
		super.dismiss();
	}
	
	

}
