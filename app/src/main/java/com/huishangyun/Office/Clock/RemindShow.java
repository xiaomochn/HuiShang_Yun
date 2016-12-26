package com.huishangyun.Office.Clock;

import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.L;
import com.huishangyun.model.Constant;
import com.huishangyun.yun.R;

/**
 * 广播闹钟接收后，提醒界面展示，调用当前activity设置下一个闹钟
 * 这个activity设置为可以在桌面弹出显示
 * @author xsl
 *
 */
public class RemindShow extends BaseActivity {
	
	private String alrm_Time;//闹钟时间
	private String Manager_ID;//登入人id
	private TextView sure;//确定按钮
	private TextView alrmTime;
	private MediaPlayer player;
	private Vibrator vib;
	private TextView alram_hint;//提醒语句
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_alarm_widget);
        Manager_ID = preferences.getString(Constant.HUISHANGYUN_UID, "0");
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        player = new MediaPlayer();
        Intent intent = getIntent();
        int index = intent.getIntExtra("index", 0);
        L.e("index==============:" + index);
        sure = (TextView) findViewById(R.id.sure);
        alrmTime = (TextView) findViewById(R.id.alrmTime);
        alram_hint = (TextView) findViewById(R.id.alram_hint);
        if (index==1) {
        	alram_hint.setText("上班时间到了，记得打卡哦！");
		}else if (index==2) {
			alram_hint.setText("下班时间到了，记得打卡哦！");
		}
        queryBySQL(index+"");
        
        sure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				player.stop();
				vib.cancel();
				finish();
			}
		}); 
        
	}
	
	/**
	 * 查询数据库，闹钟是否开启
	 * 返回最近一个闹钟和当前离今天的时间
	 */
	private void queryBySQL(String index){
		L.e("查询数据库");
		String sql = "select* from mClock where ManagerID = ? and mIndex = ?";
		DBManager dbManager = new DBManager(context);
		Map<String, String> map = dbManager.queryBySQL(sql, new String[] {Manager_ID,index});
		if (map.size()>0) {
			alrm_Time = map.get("alarmTime");
			String pickUri = map.get("pickUri");
			alrmTime.setText(alrm_Time.substring(0,2) + ":" + alrm_Time.substring(2,4));
			if (map.get("isOpen").equals("1")) {
				if (!map.get("repeatDays").equals("")) {//必须选择了重复闹钟日期
					L.e("Manager_ID:::::::::::::"+Manager_ID);
					L.e("Integer.parseInt(index):::::::::::::"+Integer.parseInt(index));
					new ReCallAlramSet(getApplicationContext(), Manager_ID,Integer.parseInt(index),
							Integer.parseInt(index)).setAlarm();
					//从数据库获得当前闹钟的音量，并设置，然后播放音乐
					setAlarmVolume(map.get("Volume"));
					if (map.get("Vib").equals("2")) {
						//开启手机震动
						setVibrate();
					}else if (map.get("Vib").equals("3")) {
						//开启手机震动
						setVibrate();
						playAlarmMusic(Uri.parse(pickUri));
					}else if (map.get("Vib").equals("1")) {
						playAlarmMusic(Uri.parse(pickUri));
					}
				}else {
					return;
				}
			
			}
		}
	}
	
	/**
	 * 设置系统闹钟音量
	 */
	private void setAlarmVolume(String progress){
		 AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		 int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_ALARM );
		 int progress1 =  (int) ((Integer.parseInt(progress))/(100/max));//转换成音频设置值
		 L.e("progress1：" + progress1);
		 mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, progress1, 0);
		 
	}
	
	/**
	 * 开启手机震动
	 */
	private void setVibrate(){
		//开启手机震动
		 vib.vibrate(new long[]{1000,1000,1000,1000,500},0);
	}
	
	/**
	 * 指定音频播放
	 * @param pickUri
	 */
	private void playAlarmMusic(Uri pickUri){
		try {
			player.setDataSource(context, pickUri);
		} catch (Exception e) {
			e.printStackTrace();
		}
		final AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
			player.setAudioStreamType(AudioManager.STREAM_ALARM);
			//设置循环播放
			player.setLooping(true);
			try {
				player.prepare();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			player.start();
			stopMediaPlayer();
		}
	}
	
	/**
	 * 延迟停止播放
	 */
	private void stopMediaPlayer(){
		Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				player.stop();
				vib.cancel();
			}
		}, 120000);//两分钟后停止响铃
	}
}
