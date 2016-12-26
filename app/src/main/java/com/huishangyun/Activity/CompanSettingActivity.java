package com.huishangyun.Activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.CompanyInfo;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJResponse;
/**
 * 
 * @author pan
 * 设置分销商标识
 *
 */
public class CompanSettingActivity extends BaseActivity{
	private Button btnback;
	private ListView listView;
	private EditText editText;
	private ListAdapter adapter;
	private List<CompanyInfo> comps;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compan_setting);
		setResult(0);
		init();
	}
	/**
	 * 实例化各组件
	 */
	private void init(){
		comps = new ArrayList<CompanyInfo>();
		btnback = (Button)findViewById(R.id.comp_setting_back);
		listView = (ListView)findViewById(R.id.comp_setting_listview);
		editText = (EditText)findViewById(R.id.et_compan_search);
		editText.setOnKeyListener(m_CompanOnKeyListener);
		btnback.setOnClickListener(listener);
		adapter = new ListAdapter();
		listView.setAdapter(adapter);
		editText.addTextChangedListener(new EdidtextListener());
		listView.setVisibility(View.GONE);
	}
	
	/**
	 * 公司设置编辑框，回车搜索键事件监听
	 * @return
	 */
	private View.OnKeyListener m_CompanOnKeyListener = new View.OnKeyListener()
	{
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event)
		{
			if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
				// 先隐藏键盘
				((InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				
				String strSearch = editText.getText().toString();
				if (strSearch.length() > 0) {
					getCompan(strSearch.toString());
				}else {
					listView.setVisibility(View.GONE);
				}

			}
			
			return false;
		}
	};
	
	private Activity getActivity()
	{
		return CompanSettingActivity.this;
	}
	
	OnClickListener listener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.comp_setting_back:
				//点击返回键返回上一界面
				closeInput();
				finish();
				break;

			default:
				break;
			}
		}
		
	};
	
	/**
	 * 
	 * @author pan
	 * 监控EditText字数变化，控制ListView显示
	 *
	 */
	private class EdidtextListener implements TextWatcher{

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if (s.length() > 0) {
				getCompan(s.toString());
			}else {
				listView.setVisibility(View.GONE);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				listView.setVisibility(View.VISIBLE);
				comps.clear();
				comps.add((CompanyInfo) msg.obj);
				adapter.notifyDataSetChanged();
				break;
				
			case HanderUtil.case2:
				showCustomToast((String) msg.obj, false);
				break;

			default:
				break;
			}
		};
	};
	
	/**
	 * 调查查询接口获得公司名称
	 * @param str 搜索公司名称字符串
	 * @param bNeedNotify 查询为空是否提示用户
	 */
	private void getCompan(final String keywords){
		new Thread(){
			public void run() {
				ZJRequest zjRequest = new ZJRequest();
				zjRequest.setKeywords(keywords);
				String json = JsonUtil.toJson(zjRequest);
				String result = DataUtil.callWebService(Methods.GET_COMPANY_ID, json);
				Log.e("result = " , result);
				Type type = new TypeToken<ZJResponse<CompanyInfo>>(){}.getType();
				if (result == null) {
					Message msg = new Message();
					msg.what = HanderUtil.case2;
					msg.obj = "无法连接服务器";
					mHandler.sendMessage(msg);
					return;
				}
				ZJResponse<CompanyInfo> zjResponse = JsonUtil.fromJson(result, type);
				if (zjResponse.getCode() != 0) {
					Message msg = new Message();
					msg.what = HanderUtil.case2;
					msg.obj = zjResponse.getDesc();
					mHandler.sendMessage(msg);
					return;
				}
				Message msg = new Message();
				msg.what = HanderUtil.case1;
				msg.obj = zjResponse.getResult();
				mHandler.sendMessage(msg);
			};
		}.start();
	}
	
	
	
	/**
	 * 
	 * @author pan
	 * 分销商列表适配器
	 */
	private class ListAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return comps.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return comps.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			final CompanyInfo companyInfo = comps.get(position);
			if (convertView == null) {
				convertView = LayoutInflater.from(CompanSettingActivity.this).inflate(R.layout.list_item_compan, null);
				holder = new ViewHolder();
				holder.textView = (TextView)convertView.findViewById(R.id.list_item_compan_name);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			final int ID = companyInfo.getID();
			final String Name =  companyInfo.getName();
			final String path = companyInfo.getLogoImg();
			final String AdminDomain = companyInfo.getAdminDomain();
			holder.textView.setText(Name);
			holder.textView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//选择分销商
					Intent data = new Intent();
					Bundle datas = new Bundle();
					datas.putString("name", companyInfo.getSysName());
					datas.putString("path", path);
					datas.putInt("ID", ID);
					datas.putString("AdminDomain", AdminDomain);
					data.putExtras(datas);
					CompanSettingActivity.this.setResult(1, data);
					MyApplication.getInstance().getSharedPreferences().edit().putString(Constant.HUISHANG_COMPANY_NAME, companyInfo.getName()).commit();
					MyApplication.getInstance().getSharedPreferences().edit().putString(Constant.HUISHANG_APPLIST, companyInfo.getAppList()).commit();
					L.e("subApp = " + companyInfo.getSubAppList());
					MyApplication.getInstance().getSharedPreferences().edit().putString(Constant.HUISHANG_SUBAPP_LIST, companyInfo.getSubAppList()).commit();
					finish();
					closeInput();
				}
			});
			return convertView;
		}
		
		private class ViewHolder{
			private TextView textView;
		}
		
	}
}
