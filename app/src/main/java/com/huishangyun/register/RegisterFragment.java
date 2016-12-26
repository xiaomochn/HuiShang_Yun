package com.huishangyun.register;

import com.huishangyun.Util.CompanyInfo;
import com.huishangyun.yun.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 注册页面
 * @author frpan_000
 *
 */
public class RegisterFragment extends Fragment{
	private RegisterPhoneFragment rPhoneFragment;
	private FailRegisterFragment rFailFragment;
	private SuccessRegisterFragment rSuccessFragment;
	private SuccessInRegisterFragment rSuccessInFragment;
	private FragmentTransaction transaction;
	private View mView;
	public static final int PHONE_ID = 0;
	public static final int FAIL_ID = 1;
	public static final int SUCCESS_ID = 2;
    public static final int SUCCESS_IN_ID = 3;

    public int SHOW_ID = 0;
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.fragment_register, container, false);
		initView();
		return mView;
	}
	
	private void initView() {
		transaction = getActivity().getSupportFragmentManager().beginTransaction();
		rPhoneFragment = new RegisterPhoneFragment();
        rPhoneFragment.setOnFragmentChanger(phoneChanger);
		rFailFragment = new FailRegisterFragment();
        rFailFragment.setOnFragmentChanger(failChange);
		rSuccessFragment = new SuccessRegisterFragment();
        rSuccessFragment.setOnFragmentChanger(regisChanger);
		rSuccessInFragment = new SuccessInRegisterFragment();
		transaction.add(R.id.regis_fragment_layout, rPhoneFragment);
		transaction.add(R.id.regis_fragment_layout, rFailFragment);
		transaction.add(R.id.regis_fragment_layout, rSuccessFragment);
		transaction.add(R.id.regis_fragment_layout, rSuccessInFragment);
		transaction.commitAllowingStateLoss();
		SHOW_ID = PHONE_ID;
		showFragment(PHONE_ID);
	}
	
	
	public interface OnFragmentChanger<T> {
		public abstract void onFragmentChanger(int showID, T result, String number);
	}
	
	/**
	 * 验证号码返回
	 */
	private OnFragmentChanger<CompanyInfo> phoneChanger = new OnFragmentChanger<CompanyInfo>() {

		@Override
		public void onFragmentChanger(int showID, CompanyInfo result, String number) {
			// TODO Auto-generated method stub
			switch (showID) {
			case FAIL_ID:
				rFailFragment.setCompanyInfo(result);
				showFragment(showID);
				break;
			case SUCCESS_ID:
				rSuccessFragment.setNumber(number);
				showFragment(showID);
				break;
			default:
				break;
			}
		}
	};

    private OnFragmentChanger<CompanyInfo> failChange = new OnFragmentChanger<CompanyInfo>() {
        @Override
        public void onFragmentChanger(int showID, CompanyInfo result, String number) {
            showFragment(showID);
        }
    };
	
	/**
	 * 注册返回
	 */
	private OnFragmentChanger<CompanyInfo> regisChanger = new OnFragmentChanger<CompanyInfo>() {

		@Override
		public void onFragmentChanger(int showID, CompanyInfo result,
				String number) {
			// TODO Auto-generated method stub
			showFragment(showID);
			rSuccessInFragment.setCompanyInfo(result);
		}
	};
	
	/**
	 * 设置显示的fragment
	 * @param showID
	 */
	public void showFragment(int showID) {
		switch (showID) {
		case PHONE_ID:
			SHOW_ID = PHONE_ID;
			getActivity().getSupportFragmentManager().beginTransaction().hide(rFailFragment).hide(rSuccessFragment).hide(rSuccessInFragment).hide(rPhoneFragment).show(rPhoneFragment).commitAllowingStateLoss();
			break;
			
		case FAIL_ID:
			SHOW_ID = FAIL_ID;
			getActivity().getSupportFragmentManager().beginTransaction().hide(rPhoneFragment).hide(rSuccessFragment).hide(rSuccessInFragment).hide(rFailFragment).show(rFailFragment).commitAllowingStateLoss();
			break;
			
		case SUCCESS_ID:
			SHOW_ID = SUCCESS_ID;
			getActivity().getSupportFragmentManager().beginTransaction().hide(rFailFragment).hide(rPhoneFragment).hide(rSuccessInFragment).hide(rSuccessFragment).show(rSuccessFragment).commitAllowingStateLoss();
			break;
			
		case SUCCESS_IN_ID:
			SHOW_ID = SUCCESS_IN_ID;
			getActivity().getSupportFragmentManager().beginTransaction().hide(rFailFragment).hide(rSuccessFragment).hide(rPhoneFragment).hide(rSuccessInFragment).show(rSuccessInFragment).commitAllowingStateLoss();
			break;

		default:
			break;
		}
	}
}
