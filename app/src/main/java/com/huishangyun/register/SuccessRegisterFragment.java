package com.huishangyun.register;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Util.CompanyInfo;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.model.Methods;
import com.huishangyun.register.RegisterFragment.OnFragmentChanger;
import com.huishangyun.yun.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 验证成功页面
 * @author frpan_000
 *
 */
public class SuccessRegisterFragment extends Fragment{
	private View mView;
	private EditText companyNameEdt;
	private EditText yumingEditText;
	private EditText passWordEdt;
	private Button submitBtn;
	private CheckBox agreeBox;
	private TextView readTxt;
	private boolean isRead = true;
	private webServiceHelp<CompanyInfo> mServiceHelp;
	private String number;
	private OnFragmentChanger<CompanyInfo> onFragmentChanger;

    public  void setOnFragmentChanger(OnFragmentChanger<CompanyInfo> onFragmentChanger) {
        this.onFragmentChanger = onFragmentChanger;
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.fragment_register_success, container, false);
		initView();
		return mView;
	}
	
	/**
	 * 实例化组件
	 */
	private void initView() {
		companyNameEdt = (EditText) mView.findViewById(R.id.register_company_name);
		yumingEditText = (EditText) mView.findViewById(R.id.register_domain);
		passWordEdt = (EditText) mView.findViewById(R.id.register_password);
		submitBtn = (Button) mView.findViewById(R.id.register_submit);
		agreeBox = (CheckBox) mView.findViewById(R.id.register_agree);
		readTxt = (TextView) mView.findViewById(R.id.register_read);
		readTxt.setOnClickListener(onClickListener);
		submitBtn.setOnClickListener(onClickListener);
		agreeBox.setChecked(isRead);
		agreeBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				isRead = isChecked;
			}
		});
		mServiceHelp = new webServiceHelp<CompanyInfo>(Methods.SET_COMPANY, new TypeToken<ZJResponse<CompanyInfo>>(){}.getType());
		mServiceHelp.setOnServiceCallBack(onServiceCallBack);
		
	}
	
	/**
	 * 设置号码
	 * @param number
	 */
	public void setNumber(String number) {
		this.number = number;
	}
	
	/**
	 * 创建返回
	 */
	private webServiceHelp.OnServiceCallBack<CompanyInfo> onServiceCallBack = new webServiceHelp.OnServiceCallBack<CompanyInfo>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<CompanyInfo> zjResponse) {
			// TODO Auto-generated method stub
			if (haveCallBack && zjResponse != null) {
				switch (zjResponse.getCode()) {
				case 0:
					((RegisterMainActivity)getActivity()).showCustomToast("注册成功", true);
					onFragmentChanger.onFragmentChanger(RegisterFragment.SUCCESS_IN_ID, (CompanyInfo) zjResponse.getResult(), number);
					break;

				default:
					((RegisterMainActivity)getActivity()).showCustomToast(zjResponse.getDesc(), false);
					break;
				}
			} else {
				((RegisterMainActivity)getActivity()).showCustomToast("无法连接服务器", false);
			}
		}
		
	};
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mServiceHelp.removeOnServiceCallBack();
		super.onDestroy();
	}
	
	private boolean isCanSubmit() {
		if (companyNameEdt.getText().toString().trim().equals("")) {
			((RegisterMainActivity)getActivity()).showCustomToast("请输入公司名称", false);
			return false;
		} else if (yumingEditText.getText().toString().trim().equals("")) {
			((RegisterMainActivity)getActivity()).showCustomToast("请输入公司域名", false);
			return false;
		} else if (passWordEdt.getText().toString().trim().equals("") && passWordEdt.getText().toString().trim().length() < 6) {
			((RegisterMainActivity)getActivity()).showCustomToast("请输入正确的密码", false);
			return false;
		} else if(!isRead) {
			((RegisterMainActivity)getActivity()).showCustomToast("请勾选服务条款", false);
			return false;
		}
		return true;
	}
	
	
	/**
	 * 点击事件监听
	 */
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.register_read:
				Intent intent = new Intent(getActivity(), ProvisionActivity.class);
				startActivity(intent);
				break;
			case R.id.register_submit:
				if (isCanSubmit()) {
					ZJRequest<Company> zjRequest = new ZJRequest<Company>();
					Company company = new Company();
					company.setMobile(number);
					company.setAdminDomain(yumingEditText.getText().toString().trim());
					company.setName(companyNameEdt.getText().toString().trim());
					company.setPassword(passWordEdt.getText().toString().trim());
					zjRequest.setData(company);
					L.e("提交的数据:" + JsonUtil.toJson(zjRequest));
					mServiceHelp.start(JsonUtil.toJson(zjRequest));
				}
				break;

			default:
				break;
			}
		}
	};
}
