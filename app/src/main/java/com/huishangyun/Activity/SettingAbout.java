package com.huishangyun.Activity;

import com.huishangyun.yun.R;


import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 设置中的关于
 * @author 熊文娟
 * @
 */

public class SettingAbout extends BaseActivity{
	/** 回退 */
	private LinearLayout backImg = null;
	private TextView versionTxt = null;// 版本
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题
		setContentView(R.layout.setting_about);
		initUI();
	}
	/**
	 * 初始化UI
	 */
	private void initUI() {
		// TODO Auto-generated method stub;
		backImg = (LinearLayout) findViewById(R.id.about_back);
		versionTxt = (TextView) findViewById(R.id.about_version);
		versionTxt.setText("V"+getResources().getString(R.string.versionName));//版本
		backImg.setOnClickListener(new myOnClickListener());

	}
	private class myOnClickListener implements
			android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			// 回退
			case R.id.about_back:
				finish();
				break;
			default:
				break;
			}
		}

	}
}

