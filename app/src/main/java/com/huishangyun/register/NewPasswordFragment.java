package com.huishangyun.register;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.T;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.View.MyDialog;
import com.huishangyun.View.MyDialog.OnMyDialogClickListener;
import com.huishangyun.yun.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class NewPasswordFragment extends Fragment{
	private View mView;
	private EditText forget_new_pass;
	private EditText forget_confirm_pass;
	private Button forget_submit;
	private webServiceHelp<T> mServiceHelp;
	private String newPassword = "";
	private String confirmPassword = "";
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.fragment_new_password, container, false);
		initView();
		return mView;
	}
	
	/**
	 * 实例化组件
	 */
	private void initView() {
		forget_new_pass = (EditText) mView.findViewById(R.id.forget_new_pass);
		forget_confirm_pass = (EditText) mView.findViewById(R.id.forget_confirm_pass);
		forget_submit = (Button) mView.findViewById(R.id.forget_submit);
		mServiceHelp = new webServiceHelp<T>(Methods.SET_MANAGER_PASSWORD, new TypeToken<ZJResponse>(){}.getType());
		mServiceHelp.setOnServiceCallBack(onServiceCallBack);
		forget_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				newPassword = forget_new_pass.getText().toString().trim();
				confirmPassword = forget_confirm_pass.getText().toString().trim();
				if (newPassword.length() == 0 || newPassword.length() <6) {
					((NewPasswordActivity) getActivity()).showCustomToast("密码不能低于6个字符", false);
				} else if (confirmPassword.length() == 0 || confirmPassword.length() <6) {
					((NewPasswordActivity) getActivity()).showCustomToast("密码不能低于6个字符", false);
				} else if (!newPassword.equals(confirmPassword)){
					((NewPasswordActivity) getActivity()).showCustomToast("两次密码输入不一致", false);
				} else {
					ZJRequest zjRequest = new ZJRequest();
					zjRequest.setManager_ID(Integer.parseInt(MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANGYUN_UID, "0")));
					zjRequest.setPassword(confirmPassword);
					mServiceHelp.start(JsonUtil.toJson(zjRequest));
					((NewPasswordActivity) getActivity()).showNotDialog("请稍候...");
				}
			}
		});
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mServiceHelp.removeOnServiceCallBack();
		super.onDestroy();
	}
	
	/**
	 * 数据返回接口
	 */
	private webServiceHelp.OnServiceCallBack<T> onServiceCallBack = new webServiceHelp.OnServiceCallBack<T>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<T> zjResponse) {
			// TODO Auto-generated method stub
			((NewPasswordActivity) getActivity()).dismissDialog();
			if (haveCallBack && zjResponse != null) {
				MyApplication.getInstance().getSharedPreferences().edit().putString(Constant.PASSWORD, confirmPassword).commit();
				MyDialog myDialog = new MyDialog(getActivity());
				myDialog.setCancel(false);
				myDialog.setTitle("修改成功");
				myDialog.setMessage("您的密码已修改,下次登陆请使用新密码");
				myDialog.setOnMyDialogClickListener(new OnMyDialogClickListener() {
					
					@Override
					public void onTrueClick(MyDialog dialog) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onFlaseClick(MyDialog dialog) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						getActivity().finish();
					}
				});
				myDialog.setFalseText("确定");
				myDialog.showTrue(false);
				myDialog.showFalse(true);
				myDialog.show();
			} else {
				((NewPasswordActivity) getActivity()).showCustomToast("无法连接服务器", false);
			}
		}
	};
}
