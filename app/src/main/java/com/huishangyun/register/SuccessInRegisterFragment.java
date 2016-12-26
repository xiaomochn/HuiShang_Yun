package com.huishangyun.register;

import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.CompanyInfo;
import com.huishangyun.model.Constant;
import com.huishangyun.yun.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 注册成功页面
 * @author frpan_000
 *
 */
public class SuccessInRegisterFragment extends Fragment{
	private View mView;
	private TextView result;
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.fragment_register_success_in, container, false);
		result = (TextView) mView.findViewById(R.id.register_success_result);
		return mView;
	}
	
	
	public void setCompanyInfo(final CompanyInfo companyInfo) {
		result.setText(companyInfo.getName());
		result.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent data = new Intent();
				Bundle datas = new Bundle();
				datas.putString("name", companyInfo.getSysName());
				datas.putString("path", companyInfo.getLogoImg());
				datas.putInt("ID", companyInfo.getID());
				datas.putString("AdminDomain", companyInfo.getAdminDomain());
				data.putExtras(datas);
				getActivity().setResult(1, data);
				MyApplication.preferences.edit().putString(Constant.HUISHANG_COMPANY_NAME, companyInfo.getName()).commit();
				MyApplication.preferences.edit().putString(Constant.HUISHANG_APPLIST, companyInfo.getAppList()).commit();
				getActivity().finish();
				((RegisterMainActivity)getActivity()).closeInput();
			}
		});
	}
}
