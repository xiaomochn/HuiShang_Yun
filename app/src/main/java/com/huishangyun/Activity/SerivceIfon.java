package com.huishangyun.Activity;

import com.huishangyun.ImageLoad.ImageLoader;
import com.huishangyun.Office.Attendance.MySlipSwitch;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.StringUtil;
import com.huishangyun.View.RoundAngleImageView;
import com.huishangyun.manager.ServiceManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.Service;
import com.huishangyun.yun.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 服务号详情页面
 * @author Pan
 *
 */
public class SerivceIfon extends BaseMainActivity{
	private MySlipSwitch mSwitch;
	private ImageView serivce_img;
	private TextView serivce_name;
	private TextView serivce_maoshu;
	private LinearLayout serivce_history;
	private String OFUserName;
	private String Name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serviceinfo);
		initBackTitle("详细资料");
		OFUserName = getIntent().getStringExtra("JID");
		Name = getIntent().getStringExtra("Name");
		initView();
		//获取服务号资料
		getServiceInfo(OFUserName);
	}
	
	/**
	 * 实例化组件
	 */
	private void initView() {
		mSwitch = (MySlipSwitch) findViewById(R.id.serivce_tixing);
		//设置开关图片
		mSwitch.setImageResource(R.drawable.serviceicon_onbg, R.drawable.serviceicon_offbg, R.drawable.serviceicon_onoff);
		//设置点击方法
		mSwitch.setOnSwitchListener(new MySlipSwitch.OnSwitchListener() {
			
			@Override
			public void onSwitched(MySlipSwitch slipSwitch, boolean isSwitchOn) {
				// TODO Auto-generated method stub
				L.e("isChecked = " + isSwitchOn);
				if (isSwitchOn) {//开启消息
					String Names = preferences.getString(Constant.HUISHANG_SERVICE_NAME, "");
					if (Names.equals("")) {//没有设置过服务号消息过滤
						preferences.edit().putString(Constant.HUISHANG_SERVICE_NAME, OFUserName).commit();
					} else { //设置过消息过滤
						Names = Names + "#" + OFUserName;
						preferences.edit().putString(Constant.HUISHANG_SERVICE_NAME, Names).commit();
					}
				} else { //关闭消息过滤
					String Names = preferences.getString(Constant.HUISHANG_SERVICE_NAME, "");
					L.e("取出的名称是:" + Names);
					String OFNames[] = Names.split("#");
					String NewNames = "";
					for (int i = 0; i < OFNames.length; i++) {
						if (!OFNames[i].equals(OFUserName)) {
							//去除和服务号相同的名字
							if (NewNames.equals("")) {
								NewNames = OFNames[i];
							} else {
								NewNames = NewNames + "#" + OFNames[i];
							}
						}
					}
					L.e("插入的字符串为:" + NewNames);
					preferences.edit().putString(Constant.HUISHANG_SERVICE_NAME, NewNames).commit();
				}
			}
		});
		serivce_img = (ImageView) findViewById(R.id.serivce_img);
		serivce_name = (TextView) findViewById(R.id.serivce_name);
		serivce_maoshu = (TextView) findViewById(R.id.serivce_maoshu);
		serivce_history = (LinearLayout) findViewById(R.id.serivce_history);
		//设置监听
		serivce_history.setOnClickListener(mClickListener);
		serivce_name.setText(getIntent().getStringExtra("Name"));
		String Names[] = preferences.getString(Constant.HUISHANG_SERVICE_NAME, "").split("#");
		mSwitch.updateSwitchState(false);
		for (int i = 0; i < Names.length; i++) {
			if (Names[i].equals(OFUserName)) {
				mSwitch.updateSwitchState(true);
			}
		}
	}
	
	/**
	 * 点击事件监听
	 */
	private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.serivce_history: //查看历史消息
				Intent intent2 = new Intent(SerivceIfon.this, CharHistory.class);
				intent2.putExtra("to", OFUserName);
				intent2.putExtra("name", Name);
				intent2.putExtra("type", 0);
				intent2.putExtra("isGroup", false);
				startActivity(intent2);
				break;

			default:
				break;
			}
		}
	};
	
	private void getServiceInfo(String OFUserName) {
		new Thread(new GetServiceInfo(StringUtil.getUserNameByJid(OFUserName))).start();
	}
	
	/**
	 * 获取服务号详情
	 * @author Pan
	 *
	 */
	private class GetServiceInfo implements Runnable {
		private String OFUserName; //用户名
		public GetServiceInfo(String OFUserName) {
			this.OFUserName = OFUserName;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Service managers = ServiceManager.getInstance(SerivceIfon.this).getService(OFUserName);
			if (managers != null) {
				Message msg = new Message();
				msg.what = HanderUtil.case1;
				msg.obj = managers;
				mHandler.sendMessage(msg);
			} else {
				mHandler.sendEmptyMessage(HanderUtil.case2);
			}
		}
		
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			
			case HanderUtil.case1:
				Service managers = (Service) msg.obj;
				com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(Constant.pathurl+
						MyApplication.preferences.getInt(Content.COMPS_ID, 1016)+"/Photo/" + managers.getPhoto(), serivce_img, MyApplication.getInstance().getGroupOptions());
				String url=Constant.pathurl+MyApplication.preferences.getInt(Content.COMPS_ID, 1016)+"/Photo/" + managers.getPhoto();
				Log.e("TAGS","url="+url);
				MyApplication.preferences.edit().putString(Constant.HUISHANG_CHAT_URLS, url).commit();
				serivce_name.setText(managers.getName());
				serivce_maoshu.setText(managers.getNote());
				break;
				
			case HanderUtil.case2:
				showCustomToast("获取资料失败", false);
				break;

			default:
				break;
			}
		};
	};
}
