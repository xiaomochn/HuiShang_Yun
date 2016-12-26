package com.huishangyun.register;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Util.CompanyInfo;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.model.Methods;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.yun.R;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * 注册手机号码
 * @author frpan_000
 *
 */
public class RegisterPhoneFragment extends Fragment{
	private View mView;
	private EditText numberEdit;
	private EditText messageEdit;
	private Button chekMessage;
	private Button submitBtn;
	private webServiceHelp<CompanyInfo> getMessageHelp;
	private webServiceHelp<CompanyInfo> setMessageHelp;
	private TimeCount time;
	private String number = ""; 
	private RegisterFragment.OnFragmentChanger<CompanyInfo> onFragmentChanger;

    /**
     *
     * @param onFragmentChanger
     */
    public void setOnFragmentChanger( RegisterFragment.OnFragmentChanger<CompanyInfo> onFragmentChanger) {
        this.onFragmentChanger = onFragmentChanger;
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.fragment_register_phone, container, false);
		initView();
		return mView;
	}
	
	private void initView() {
		numberEdit = (EditText) mView.findViewById(R.id.register_phone_number);
		messageEdit = (EditText) mView.findViewById(R.id.register_number);
		chekMessage = (Button) mView.findViewById(R.id.register_chek_number);
		submitBtn = (Button) mView.findViewById(R.id.register_submit);
		getMessageHelp = new webServiceHelp<CompanyInfo>(Methods.SET_SMS, new TypeToken<ZJResponse<CompanyInfo>>(){}.getType());
		setMessageHelp = new webServiceHelp<CompanyInfo>(Methods.GET_SMS, new TypeToken<ZJResponse<CompanyInfo>>(){}.getType());
		getMessageHelp.setOnServiceCallBack(getMessageBack);
		setMessageHelp.setOnServiceCallBack(setMessageBack);
		time = new TimeCount(90000, 1000);//构造CountDownTimer对象
		chekMessage.setOnClickListener(onClickListener);
		submitBtn.setOnClickListener(onClickListener);
	}
	
	/**
	 * 获取验证码
	 */
	private webServiceHelp.OnServiceCallBack<CompanyInfo> getMessageBack = new webServiceHelp.OnServiceCallBack<CompanyInfo>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<CompanyInfo> zjResponse) {
			// TODO Auto-generated method stub
			((RegisterMainActivity)getActivity()).dismissDialog();
			if (zjResponse != null && haveCallBack) {
				switch (zjResponse.getCode()) {
				case 0:
					if (zjResponse.getResult() != null) {
					//case 1://号码已注册
						onFragmentChanger.onFragmentChanger(RegisterFragment.FAIL_ID, (CompanyInfo) zjResponse.getResult(), "");
						//break;
					} else {
						time.start();
					}
					
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
	
	/**
	 * 提交验证码
	 */
	private webServiceHelp.OnServiceCallBack<CompanyInfo> setMessageBack = new webServiceHelp.OnServiceCallBack<CompanyInfo>() {
		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<CompanyInfo> zjResponse) {
			// TODO Auto-generated method stub
			((RegisterMainActivity)getActivity()).dismissDialog();
			if (haveCallBack && zjResponse != null) {
				switch (zjResponse.getCode()) {
				case 0://验证成功
					onFragmentChanger.onFragmentChanger(RegisterFragment.SUCCESS_ID, null, number);
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
	
	/**
	 * 点击事件监听
	 */
	private OnClickListener onClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.register_chek_number:
				if (isNumberChek()) {
					((RegisterMainActivity)getActivity()).showNotDialog("正在获取验证码");
					ZJRequest zjRequest = new ZJRequest();
					number = numberEdit.getText().toString().trim();
					zjRequest.setLoginName(number);
					zjRequest.setFlag(1);
					getMessageHelp.start(JsonUtil.toJson(zjRequest));
				}
				
				break;
				
			case R.id.register_submit:
				if (isMessageChek()) {
					((RegisterMainActivity)getActivity()).showNotDialog("正在验证");
					ZJRequest zjRequest = new ZJRequest();
					zjRequest.setLoginName(number);
					zjRequest.setKeywords(messageEdit.getText().toString().trim());
					setMessageHelp.start(JsonUtil.toJson(zjRequest));
				}
				break;

			default:
				break;
			}
		}
		
	};
	
	/**
	 * 判断验证码是否输入正确
	 * @return
	 */
	private boolean isMessageChek() {
		if (messageEdit.getText().toString().trim().length() !=6) {
			((RegisterMainActivity)getActivity()).showCustomToast("请输入正确的验证码", false);
			return false;
		}
		return true;
	}
	
	/**
	 * 号码是否正确
	 * @return
	 */
	private boolean isNumberChek() {
		if(numberEdit.getText().toString().trim().length() < 11) {
			((RegisterMainActivity)getActivity()).showCustomToast("请输入正确的号码", false);
			return false;
		}
		return true;
	}
	
	/**
	 * 计时器类
	 * @author frpan_000
	 *
	 */
	private class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			 super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
		}
		 @Override
		 public void onFinish() {//计时完毕时触发
			 chekMessage.setText("重新获取");
			 chekMessage.setClickable(true);
		 }
		 @Override
		 public void onTick(long millisUntilFinished){//计时过程显示
			 chekMessage.setClickable(false);
			 chekMessage.setText(millisUntilFinished /1000+"秒重新获取");
		 }
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		getMessageHelp.removeOnServiceCallBack();
		setMessageHelp.removeOnServiceCallBack();
		super.onDestroy();
	}
}
