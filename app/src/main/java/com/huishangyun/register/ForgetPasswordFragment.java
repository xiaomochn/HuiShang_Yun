package com.huishangyun.register;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.T;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.Util.webServiceHelp.OnServiceCallBack;
import com.huishangyun.View.MyDialog;
import com.huishangyun.View.MyDialog.OnMyDialogClickListener;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ForgetPasswordFragment extends Fragment{
	private View mView;
	private EditText phoneNumber;
	private Button submitBtn;
	private webServiceHelp<T> mServiceHelp;
	private String phoneNum = "";
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.fragment_forget_pass, container, false);
		initView();
		return mView;
	}
	
	private void initView() {
		phoneNumber = (EditText) mView.findViewById(R.id.forget_phone_number);
		submitBtn = (Button) mView.findViewById(R.id.forget_submit);
		submitBtn.setOnClickListener(onClickListener);
		mServiceHelp = new webServiceHelp<T>(Methods.GET_MANAGER_PASSWORD, new TypeToken<ZJResponse>(){}.getType());
		mServiceHelp.setOnServiceCallBack(onServiceCallBack);
	}
	
	/**
	 * 点击事件监听
	 */
	public OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			phoneNum = phoneNumber.getText().toString().trim();
			if (!phoneNum.equals("") && phoneNum.length() > 10) {
				ZJRequest zjRequest = new ZJRequest();
				zjRequest.setLoginName(phoneNum);
				zjRequest.setCompany_ID(MyApplication.getInstance().getCompanyID());
				L.e("json = " + JsonUtil.toJson(zjRequest));
				mServiceHelp.start(JsonUtil.toJson(zjRequest));
				((ForgetPasswordActivity) getActivity()).closeInput();
				((ForgetPasswordActivity) getActivity()).showNotDialog("请稍候...");
			} else {
				((ForgetPasswordActivity) getActivity()).showCustomToast("请输入正确的手机号码", false);
			}
		}
		
	};
	
	public void onDestroy() {
		super.onDestroy();
		mServiceHelp.removeOnServiceCallBack();
	};
	
	/**
	 * 接口数据返回监听
	 */
	private OnServiceCallBack<T> onServiceCallBack = new OnServiceCallBack<T>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				final ZJResponse<T> zjResponse) {
			// TODO Auto-generated method stub
			((ForgetPasswordActivity) getActivity()).dismissDialog();
			if (haveCallBack && zjResponse != null) {
				MyDialog myDialog = new MyDialog(getActivity());
				myDialog.setCancel(false);
				myDialog.setTitle("找回密码");
				myDialog.setMessage(zjResponse.getDesc());
				myDialog.setOnMyDialogClickListener(new OnMyDialogClickListener() {
					
					@Override
					public void onTrueClick(MyDialog dialog) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onFlaseClick(MyDialog dialog) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						if (zjResponse.getCode() == 0) {
							getActivity().finish();
						}
					}
				});
				myDialog.setFalseText("确定");
				myDialog.showTrue(false);
				myDialog.showFalse(true);
				myDialog.show();
			} else {
				((ForgetPasswordActivity) getActivity()).showCustomToast("无法链接到服务器", false);
			}
		}
	};
}
