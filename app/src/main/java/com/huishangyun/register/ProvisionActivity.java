package com.huishangyun.register;

import android.os.Bundle;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.yun.R;

/**
 * 服务条款页面
 * @author frpan_000
 *
 */
public class ProvisionActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_register_provision);
		initBackTitle("服务条款");
	}
}
