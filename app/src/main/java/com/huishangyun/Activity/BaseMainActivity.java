package com.huishangyun.Activity;

import com.huishangyun.model.Constant;
import com.huishangyun.App.MyApplication;

public class BaseMainActivity extends BaseActivity{
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		MyApplication.preferences.edit().putBoolean(Constant.HUISHANG_ISMAIN_TOP, false).commit();
		super.onStop();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		MyApplication.preferences.edit().putBoolean(Constant.HUISHANG_ISMAIN_TOP, true).commit();
		super.onResume();
	}
}
