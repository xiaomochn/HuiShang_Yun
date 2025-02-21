package com.huishangyun.yindao;

import com.huishangyun.Activity.LandActivity;
import com.huishangyun.yun.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;



/**
 *启动画面 (1)判断是否是首次加载应用--采取读取SharedPreferences的方法
 *     (2)是，则进入GuideActivity；否，则进入MainActivity (3)3s后执行(2)操作
 */
public class SplashActivity extends Activity {
	boolean isFirstIn = false;

	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;	

	/**
	 * Handler:跳转到不同界面
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				goHome();
				break;
			case GO_GUIDE:
				goGuide();
				break;
			}
			super.handleMessage(msg);
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.guide_splash);
		
		init();
	}

	private void init() {
		// 读取SharedPreferences中需要的数据
		// 使用SharedPreferences来记录程序的使用次数
		//first_pref是这个保存文件的文件名
		SharedPreferences preferences = getSharedPreferences("first_pref", MODE_PRIVATE);

		// 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
		isFirstIn = preferences.getBoolean("isFirstIn", true);

		// 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
		if (!isFirstIn) {
			// 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
			mHandler.sendEmptyMessageDelayed(GO_HOME, 3000);
		} else {
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, 3000);
		}

	}

	private void goHome() {
		Intent intent = new Intent(SplashActivity.this, LandActivity.class);
		SplashActivity.this.startActivity(intent);
//		overridePendingTransition(R.anim.si, R.anim.hold); 
		SplashActivity.this.finish();
	}

	private void goGuide() {
		Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
		SplashActivity.this.startActivity(intent);
//		overridePendingTransition(R.anim.fade, R.anim.hold); 
		SplashActivity.this.finish();
	}
					
	
}
