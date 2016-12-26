package com.huishangyun.Fragment;

import com.huishangyun.App.MyApplication;
import com.huishangyun.LightApp.LightAppFragment;
import com.huishangyun.LightApp.OfficeFragment;
import com.huishangyun.Util.L;
import com.huishangyun.model.Constant;
import com.huishangyun.LightApp.ChannelFragment;
import com.huishangyun.LightApp.ErpFragment;
import com.huishangyun.yun.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class App_Fragment extends Fragment{
	private Fragment[] mFragments;// fragment实体类都封装在这里
	private RadioGroup mRadioGroup;
	private RadioButton mChannelButton, mOfficeButton, mLightButton, mErpButton;
	private ChannelFragment mChannelFragment;
	private OfficeFragment mOfficeFragment;
	private LightAppFragment mAppFragment;
	private ErpFragment mErpFragment;
	private View mView;
	//记录fragment的个数
	private int mFragmentSize = 0;
	//初始选中项
	private int mFirstSelect = 0;
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.fragment_app, container, false);
		initView();
		return mView;
	}
	
	/**
	 * 实例化组件
	 */
	private void initView() {
		mFragments = new Fragment[4];
		mChannelFragment = (ChannelFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_channel);
		mOfficeFragment = (OfficeFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_office);
		mAppFragment = (LightAppFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_applight);
		mErpFragment = (ErpFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_apperp);
		
		mRadioGroup = (RadioGroup) mView.findViewById(R.id.app_radiogroup);
		mChannelButton = (RadioButton) mView.findViewById(R.id.app_channel);
		mOfficeButton = (RadioButton) mView.findViewById(R.id.app_office);
		mLightButton = (RadioButton) mView.findViewById(R.id.app_light);
		mErpButton = (RadioButton) mView.findViewById(R.id.app_erp);
		
		mFragments[0] = mChannelFragment;
		mFragments[1] = mOfficeFragment;
		mFragments[2] = mAppFragment;
		mFragments[3] = mErpFragment;

		mChannelButton.setVisibility(View.GONE);
		mOfficeButton.setVisibility(View.GONE);
		mLightButton.setVisibility(View.GONE);
		mErpButton.setVisibility(View.GONE);


		String[] appList = MyApplication.preferences.getString(Constant.HUISHANG_APPLIST, Constant.HUISHANG_SERVICE).split(",");
		for (int i = 0; i < appList.length; i++) {
			if (appList[i].equals(Constant.HUISHANG_CHANNEL)) {
				//mFragments[i] = mChannelFragment;
				mChannelButton.setVisibility(View.VISIBLE);
				if (i == 0) {
					mFirstSelect = 0;
					mChannelButton.setChecked(true);
				}
			} else if (appList[i].equals(Constant.HUISHANG_OFFICE)) {
				//mFragments[i] = mOfficeFragment;
				mOfficeButton.setVisibility(View.VISIBLE);
				if (i == 0) {
					mFirstSelect = 1;
					mOfficeButton.setChecked(true);
				}
			} else if (appList[i].equals(Constant.HUISHANG_SERVICE)) {
				//mFragments[i] = mAppFragment;
				mLightButton.setVisibility(View.VISIBLE);
				if (i == 0) {
					mFirstSelect = 2;
					mOfficeButton.setChecked(true);
				}
			}
			
			else if (appList[i].equals(Constant.HUISHANG_ERP)) {
				//mFragments[i] = mAppFragment;
				mErpButton.setVisibility(View.VISIBLE);
				if (i == 0) {
					mFirstSelect = 3;
					mOfficeButton.setChecked(true);
				}
			}
		}
		mFragmentSize = appList.length;
		L.e("mFragments = " + mFragments.length);
		if (mFragmentSize <= 1) {
			mRadioGroup.setVisibility(View.GONE);
		}
		
		mChannelButton.setOnCheckedChangeListener(onCheckedChangeListener);
		mOfficeButton.setOnCheckedChangeListener(onCheckedChangeListener);
		mLightButton.setOnCheckedChangeListener(onCheckedChangeListener);
		mErpButton.setOnCheckedChangeListener(onCheckedChangeListener);
		setFragmentIndicator(mFirstSelect);
	}
	
	private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				selectMenu(buttonView);
			}
		}

		
	};
	
	private void selectMenu(CompoundButton compoundButton) {
		switch (compoundButton.getId()) {
		case R.id.app_channel:
			setFragmentIndicator(0);
			break;
		case R.id.app_office:
			setFragmentIndicator(1);
			break;
		case R.id.app_light:
			setFragmentIndicator(2);
			break;
		case R.id.app_erp:
			setFragmentIndicator(3);
			break;

		default:
			break;
		}
	}
	
	public void getLightApp() {
		if (mAppFragment != null) {
			mAppFragment.getLightApp();
		}
	}
	
	/**
	 * 设置选中项
	 */
	private void setFragmentIndicator(int whichIsDefault) {
		// 使用commitAllowingStateLoss替换commit。使用commit特中情况下会报异常：Can not perform this action after onSaveInstanceState
		getActivity().getSupportFragmentManager().beginTransaction().hide(mFragments[0])
		.hide(mFragments[1]).hide(mFragments[2]).hide(mFragments[3]).show(mFragments[whichIsDefault]).commitAllowingStateLoss();
		
	}
}
