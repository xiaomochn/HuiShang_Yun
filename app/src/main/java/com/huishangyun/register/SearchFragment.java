package com.huishangyun.register;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.CompanyInfo;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.yun.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 查找分销商页面
 * @author frpan_000
 *
 */
public class SearchFragment extends Fragment{
	private View mView;
	private webServiceHelp<CompanyInfo> mServiceHelp;
	private EditText searchEdit;
	private TextView searchResult;
	private CompanyInfo mCompanyInfo;
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.fragment_search, container, false);
		initView();
		L.e("onCreateView");
		return mView;
	}
	
	/**
	 * 实例化组件
	 */
	private void initView() {
		searchEdit = (EditText) mView.findViewById(R.id.et_search_keyword);
		searchResult = (TextView) mView.findViewById(R.id.register_result);
		searchResult.setVisibility(View.GONE);
		searchEdit.addTextChangedListener(mTextWatcher);
		searchResult.setOnClickListener(mListener);
		mServiceHelp = new webServiceHelp<CompanyInfo>(Methods.GET_COMPANY_ID,
				new TypeToken<ZJResponse<CompanyInfo>>(){}.getType());
		mServiceHelp.setOnServiceCallBack(onServiceCallBack);
		mCompanyInfo = new CompanyInfo();
	}
	
	/**
	 * 访问服务器结果
	 */
	private webServiceHelp.OnServiceCallBack<CompanyInfo> onServiceCallBack = new webServiceHelp.OnServiceCallBack<CompanyInfo>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<CompanyInfo> zjResponse) {
			// TODO Auto-generated method stub
			if (zjResponse != null) {
				switch (zjResponse.getCode()) {
				case 0:
					Message msg = new Message();
					msg.what = HanderUtil.case1;
					msg.obj = zjResponse.getResult();
					mHandler.sendMessage(msg);
					break;

				default:
					
					/*Message msg2 = new Message();
					msg2.what = HanderUtil.case2;
					msg2.obj = zjResponse.getDesc();
					mHandler.sendMessage(msg2);*/
					break;
				}
			} else {
				Message msg = new Message();
				msg.what = HanderUtil.case2;
				msg.obj = "无法访问服务器";
				mHandler.sendMessage(msg);
				
			}
		}
	};
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				mCompanyInfo = (CompanyInfo) msg.obj;
				searchResult.setText(mCompanyInfo.getName());
				searchResult.setVisibility(View.VISIBLE);
				break;
			case HanderUtil.case2:
				if (!msg.obj.equals("无记录")) {
					((RegisterMainActivity)getActivity()).showCustomToast((String) msg.obj, false);
				}
				break;
			case HanderUtil.case3:
				
				break;

			default:
				break;
			}
		};
	};
	
	public void onDestroy() {
		mServiceHelp.removeOnServiceCallBack();
		super.onDestroy();
	};
	
	/**
	 * 文字变化监听
	 */
	private TextWatcher mTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			searchResult.setVisibility(View.GONE);
			if (s.length() > 0) {
				ZJRequest zjRequest = new ZJRequest();
				zjRequest.setKeywords(s.toString().trim());
				L.e("访问服务器json = " + JsonUtil.toJson(zjRequest));
				mServiceHelp.start(JsonUtil.toJson(zjRequest));
			}
		}
	};
	
	/**
	 * 点击事件监听
	 */
	private OnClickListener mListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent data = new Intent();
			Bundle datas = new Bundle();
			datas.putString("name", mCompanyInfo.getSysName());
			datas.putString("path", mCompanyInfo.getLogoImg());
			datas.putInt("ID", mCompanyInfo.getID());
			datas.putString("AdminDomain", mCompanyInfo.getAdminDomain());
			data.putExtras(datas);
			getActivity().setResult(1, data);
			MyApplication.preferences.edit().putString(Constant.HUISHANG_COMPANY_NAME, mCompanyInfo.getName()).commit();
			MyApplication.preferences.edit().putString(Constant.HUISHANG_APPLIST, mCompanyInfo.getAppList()).commit();

			MyApplication.preferences.edit().putString(Constant.HUISHANG_WEBDOMAIN, mCompanyInfo.getWebDomain()).commit();
			MyApplication.preferences.edit().putString(Constant.HUISHANG_WFHOST, mCompanyInfo.getWFHost()).commit();

			L.e("subApp = " + mCompanyInfo.getSubAppList());
			MyApplication.getInstance().getSharedPreferences().edit().putString(Constant.HUISHANG_SUBAPP_LIST, mCompanyInfo.getSubAppList()).commit();
			getActivity().finish();
			((RegisterMainActivity)getActivity()).closeInput();
		}
	};
}
