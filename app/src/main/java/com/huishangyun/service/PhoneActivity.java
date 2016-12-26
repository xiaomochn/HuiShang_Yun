package com.huishangyun.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.FileTools;
import com.huishangyun.Util.L;
import com.huishangyun.View.RoundedImageView;
import com.huishangyun.yun.R;

public class PhoneActivity extends BaseActivity{
	private View view;
	private Chronometer chronometer;
	private TelephonyManager tm;
	private WindowManager windowManager;
	
	@SuppressLint("ServiceCast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		/*WindowManager windowManager = getWindowManager();*/
		windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;    
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        		| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE 
        		| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;    
        params.height = WindowManager.LayoutParams.WRAP_CONTENT; 
        params.format = PixelFormat.TRANSPARENT;
        params.gravity = Gravity.TOP;
        view = LayoutInflater.from(context).inflate(R.layout.activity_phone, null);
       // windowManager.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        windowManager.addView(view, params);
		TextView phoneNum = (TextView) view.findViewById(R.id.phone_num);
        TextView nameView = (TextView) view.findViewById(R.id.phone_name);
        TextView companyName = (TextView) view.findViewById(R.id.phone_com);
        RoundedImageView roundedImageView = (RoundedImageView) view.findViewById(R.id.phone_ima);
        roundedImageView.setImageResource(R.drawable.callremind_defaulthead);
        chronometer = (Chronometer) view.findViewById(R.id.chronometer1);
        chronometer.setVisibility(View.INVISIBLE);
        phoneNum.setText(getIntent().getStringExtra("number"));
        nameView.setText(getIntent().getStringExtra("name"));
		companyName.setText(getIntent().getStringExtra("companyName"));
			
			/*String url = "http://img.huishangyun.com/UploadFile/huishang/" +
    				MyApplication.getInstance().getCompanyID() + "/Photo/" + members.getPhoto();*/	
			//L.e("图片地址 = " + url);
			//new ImageLoader(PhoneService.this).DisplayImage(url, roundedImageView, false);
			FileTools.decodePhoto(getIntent().getStringExtra("imgUrl"), roundedImageView);
	        L.e("-----------------------------------------------------------------------");
	        
	        tm = (TelephonyManager)getSystemService(Service.TELEPHONY_SERVICE); 
			tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	        //windowManager.addView(view, params);
		//setContentView(R.layout.activity_phone);
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		phoneStateListener = null;
		super.onDestroy();
	}
	
	private void onPhoneIDLE() {
		if (windowManager != null) {
			windowManager.removeView(view);
			windowManager = null;
		}
		finish();
	}
	
	
	/**
	 * 来电监听
	 */
	private PhoneStateListener phoneStateListener = new PhoneStateListener(){
		
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: 
				L.e("挂断2");
				if (chronometer != null ) {
					chronometer.stop();
				}
				onPhoneIDLE();
				break; 
			case TelephonyManager.CALL_STATE_OFFHOOK: 
				L.e("接听2");
				if (chronometer != null) {
					chronometer.setVisibility(View.VISIBLE);
					chronometer .setBase(SystemClock.elapsedRealtime ()); // 会将 chronometer 的显示清零
			        chronometer .start();
				}
				break; 
			/*case TelephonyManager.CALL_STATE_RINGING: 
				L.e("响铃:来电号码" + incomingNumber );
				//onCallPhone(PhoneService.this, incomingNumber);
				break; */

			default:
				break;
			}
		};
	};
}
