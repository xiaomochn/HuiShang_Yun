package com.huishangyun.register;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.yun.R;

public class NewPasswordActivity extends BaseActivity {
	private FragmentTransaction transaction;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgetpassword);
		initBackTitle("修改密码");
		transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.forget_layout, new NewPasswordFragment());
		transaction.commitAllowingStateLoss();
	}
}
