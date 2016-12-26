package com.huishangyun.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Visit.VistList;
import com.huishangyun.ImageLoad.ImageLoader;
import com.huishangyun.Util.FileTools;
import com.huishangyun.Util.L;
import com.huishangyun.Util.T;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.View.RoundedImageView;
import com.huishangyun.manager.DepartmentManager;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Managers;
import com.huishangyun.model.Members;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PhoneService extends Service {
	private TelephonyManager tm;
	private PhoneReceiver phoneReceiver;
	private WindowManager windowManager;
	private View view;
	private Chronometer chronometer;
	private boolean isMyContact = false;
	private long startTime = 0;
	private long endTime = 0;
	private int member_ID = 0;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		MyApplication.isPhoneServiceRun = true;
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		phoneReceiver = new PhoneReceiver();
		registerReceiver(phoneReceiver, intentFilter);
		tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
		tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		L.e("启动了电话监听");
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		tm = null;
		L.e("停止了电话监听");
		unregisterReceiver(phoneReceiver);
		MyApplication.isPhoneServiceRun = false;
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 通话状态监听
	 * 
	 * @author Pan
	 * 
	 */
	public class PhoneReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {// 打电话
				String phoneNumber = intent
						.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

				phoneNumber = phoneNumber.replace(" ", "");
				L.e("拨出号码为:" + phoneNumber);
				onPhoneIDLE();
				onCallPhone(context, phoneNumber);
				//new Thread(new TestThread(tm)).start();
			} else { // 接电话
			}
		}

	}
	
	private String getJson() {
		ZJRequest<VistList> zjRequest = new ZJRequest<VistList>();
		VistList vistList = new VistList();
		vistList.setType("电话拜访");
		vistList.setAction("Insert");
		int time = (int) ((endTime - startTime) / 1000);
		int h = time / 3600;
		int m = (time % 3600) / 60;
		int s = (time % 3600) % 60; 
		String note = h + "时" + m + "分" + s + "秒";
		vistList.setNote(note);
		vistList.setMember_ID(member_ID);
		vistList.setManager_ID(MyApplication.getInstance().getManagerID());
		vistList.setIsAdd(false);
		SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date = new Date(System.currentTimeMillis());
		vistList.setBelongDate(sfd.format(date));
		zjRequest.setData(vistList);
		L.e("note : " + note);
		return JsonUtil.toJson(zjRequest);
	}

	/**
	 * 判断及显示通话界面
	 * 
	 * @param context
	 * @param phoneNumber
	 */
	private void onCallPhone(Context context, String phoneNumber) {
		boolean isManager = true;
		Members members = null;
		Managers managers = DepartmentManager.getInstance(PhoneService.this)
				.getManagerForPhone(phoneNumber);
		if (managers == null) {
			isManager = false;
			members = MemberManager.getInstance(PhoneService.this)
					.getMemberForPhone(phoneNumber);
			if (members == null) {
				// 不是我们的用户号码来电
				isMyContact = false;
				L.e("数据库没有该客户");
				return;
			} else {
				isMyContact = true;
				member_ID = members.getID();
			}
		}
		
		/*
		 * Intent intent = new Intent(PhoneService.this, PhoneActivity.class);
		 * if (isManager) { intent.putExtra("number", phoneNumber);
		 * intent.putExtra("name", managers.getRealName());
		 * intent.putExtra("companyName",
		 * MyApplication.preferences.getString(Constant.HUISHANG_COMPANY_NAME,
		 * "未知")); intent.putExtra("imgUrl",
		 * "http://img.huishangyun.com/UploadFile/huishang/" +
		 * MyApplication.getInstance().getCompanyID() + "/Photo/" +
		 * managers.getPhoto()); } else { intent.putExtra("number",
		 * phoneNumber); intent.putExtra("name", members.getContact());
		 * intent.putExtra("companyName", members.getRealName());
		 * intent.putExtra("imgUrl",
		 * "http://img.huishangyun.com/UploadFile/huishang/" +
		 * MyApplication.getInstance().getCompanyID() + "/Photo/" +
		 * members.getPhoto()); }
		 * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * 
		 * startActivity(intent);
		 */
		windowManager = (WindowManager) context.getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.TOP;
		view = LayoutInflater.from(context).inflate(R.layout.activity_phone,
				null);
		TextView phoneNum = (TextView) view.findViewById(R.id.phone_num);
		TextView nameView = (TextView) view.findViewById(R.id.phone_name);
		TextView companyName = (TextView) view.findViewById(R.id.phone_com);
		RoundedImageView roundedImageView = (RoundedImageView) view
				.findViewById(R.id.phone_ima);
		roundedImageView.setImageResource(R.drawable.callremind_defaulthead);
		LinearLayout layout = (LinearLayout) view
				.findViewById(R.id.phone_layout);
		layout.setOnClickListener(mClickListener);
		if (isManager) {
			nameView.setText(managers.getRealName());
			companyName.setText(MyApplication.preferences.getString(
					Constant.HUISHANG_COMPANY_NAME, "未知"));
			new ImageLoader(PhoneService.this).DisplayImage(Constant.pathurl+ MyApplication.getInstance().getCompanyID()
							+ "/Photo/" + managers.getPhoto(),
					roundedImageView, false);

			String url = Constant.pathurl+ MyApplication.getInstance().getCompanyID() + "/Photo/" + managers.getPhoto();
			L.e("图片地址 = " + url);

			FileTools.decodePhoto(url, roundedImageView);
		} else {
			nameView.setText(members.getContact());
			companyName.setText(members.getRealName());

			String url = Constant.pathurl
					+ MyApplication.getInstance().getCompanyID() + "/Photo/"
					+ members.getPhoto();
			L.e("图片地址 = " + url);
			FileTools.decodePhoto(url, roundedImageView);
		}
		chronometer = (Chronometer) view.findViewById(R.id.chronometer1);
		chronometer.setVisibility(View.INVISIBLE);
		phoneNum.setText(phoneNumber);
		windowManager.addView(view, params);

	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.phone_layout:
				onPhoneIDLE();
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 去除自定义界面
	 */
	private void onPhoneIDLE() {
		if (windowManager != null && view != null) {
			chronometer.stop();
			windowManager.removeView(view);
			windowManager = null;
			view = null;
			chronometer = null;
		}
	}
	
	
	/**
	 * 来电监听
	 */
	private PhoneStateListener phoneStateListener = new PhoneStateListener() {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				L.e("挂断");
				if (chronometer != null && isMyContact) {
					chronometer.stop();
				}
				if (isMyContact) {

					//mHandler.postDelayed(runnable, 2000);
					endTime = System.currentTimeMillis();
					webServiceHelp<T> mServiceHelp = new webServiceHelp<T>(Methods.VISIT_CREATE, new TypeToken<ZJResponse>(){}.getType());
					mServiceHelp.setOnServiceCallBack(new webServiceHelp.OnServiceCallBack<T>() {

						@Override
						public void onServiceCallBack(boolean haveCallBack,
								ZJResponse<T> zjResponse) {
							// TODO Auto-generated method stub
							if (haveCallBack && zjResponse != null) {
								if (zjResponse.getCode() == 0) {
									L.e("上传成功");
								} else {
									L.e(zjResponse.getDesc());
								}
							} else {
								L.e("服务器无法访问");
							}
						}
					});
					mServiceHelp.start(getJson());
				}

				onPhoneIDLE();
				
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				L.e("接听");
				if (chronometer != null && isMyContact) {

					chronometer.setVisibility(View.VISIBLE);
					chronometer.setBase(SystemClock.elapsedRealtime()); // 会将
																		// chronometer
																		// 的显示清零
					chronometer.start();
				}
				startTime = System.currentTimeMillis();
				break;
			case TelephonyManager.CALL_STATE_RINGING:

				incomingNumber = incomingNumber.replace(" ", "");
				L.e("响铃:来电号码" + incomingNumber);
				onPhoneIDLE();
				onCallPhone(PhoneService.this, incomingNumber);
				break;

			default:
				break;
			}
		};
	};

}
