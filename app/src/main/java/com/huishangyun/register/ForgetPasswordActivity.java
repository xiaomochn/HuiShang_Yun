package com.huishangyun.register;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.yun.R;

/**
 * 找回密码页面
 * @author forong
 *
 */
public class ForgetPasswordActivity extends BaseActivity {
	
	private ForgetPasswordFragment rPasswordFragment;
	private FragmentTransaction transaction;
	public static final int SHOW_FORGET = 1;
	public static final int SHOW_NEWPASS = 2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	       // TODO Auto-generated method stub
	       super.onCreate(savedInstanceState);
	       setContentView(R.layout.activity_forgetpassword);
           initBackTitle("找回密码");
           initView();
    }
	
	/**
	 * 实例化组件 
	 */
	private void initView() {
	//	frameLayout = (FrameLayout) findViewById(R.id.forget_layout);
		rPasswordFragment = new ForgetPasswordFragment();
		transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.forget_layout, rPasswordFragment);
		transaction.commitAllowingStateLoss();
	}
	
}
