package com.huishangyun.register;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.yun.R;

/**
 * 注册主页面
 * @author frpan_000
 *
 */
public class RegisterMainActivity extends BaseActivity {
	private SearchFragment searchFragment;
	private RegisterFragment registerFragment;
	private FragmentTransaction transaction;
	private FrameLayout mFrameLayout;
	private RadioGroup mRadioGroup;
	private RadioButton searchBtn;
	private RadioButton regisBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_main);
		initBackTitle("选择单位");
		initView();
		setResult(0);
	}
	private void initView() {
		transaction = getSupportFragmentManager().beginTransaction();
		searchFragment = new SearchFragment();
		registerFragment = new RegisterFragment();
		mRadioGroup = (RadioGroup) findViewById(R.id.regis_radio);
		searchBtn = (RadioButton) findViewById(R.id.register_search);
		regisBtn = (RadioButton) findViewById(R.id.register_regis);
		mFrameLayout = (FrameLayout) findViewById(R.id.regis_layout);
		transaction.add(R.id.regis_layout, searchFragment);
		transaction.add(R.id.regis_layout, registerFragment);
		transaction.commitAllowingStateLoss();
		//mRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);

        if (MyApplication.getInstance().getCompanyID() == 0) {
            getSupportFragmentManager().beginTransaction().hide(registerFragment).hide(searchFragment).show(registerFragment).commitAllowingStateLoss();
            regisBtn.setChecked(true);
        } else  {
            getSupportFragmentManager().beginTransaction().hide(registerFragment).hide(searchFragment).show(searchFragment).commitAllowingStateLoss();
            searchBtn.setChecked(true);
        }
        searchBtn.setOnCheckedChangeListener(onCheckedChangeListener);
        regisBtn.setOnCheckedChangeListener(onCheckedChangeListener);

	}
	private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				switch (buttonView.getId()) {
				case R.id.register_search:
					/*if (null == searchFragment) {
						searchFragment = new SearchFragment();
					}
					transaction = getSupportFragmentManager().beginTransaction();
					transaction.replace(R.id.regis_layout, searchFragment);
					transaction.commit();*/
					getSupportFragmentManager().beginTransaction().hide(registerFragment).hide(searchFragment).show(searchFragment).commitAllowingStateLoss();
					break;
					
				case R.id.register_regis:
//					if (null == registerFragment) {
//						registerFragment = new RegisterFragment();
//					}
//					transaction = getSupportFragmentManager().beginTransaction();
//					transaction.replace(R.id.regis_layout, registerFragment);
//					transaction.commit();
					getSupportFragmentManager().beginTransaction().hide(searchFragment).hide(registerFragment).show(registerFragment).commitAllowingStateLoss();
					registerFragment.showFragment(registerFragment.SHOW_ID);
					break;

				default:
					break;
				}
			}
		}
	};

	
}
