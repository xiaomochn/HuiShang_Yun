package com.huishangyun.Office.WeiGuan;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;

import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.L;
import com.huishangyun.App.MyApplication;
import com.huishangyun.model.Constant;
import com.huishangyun.yun.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageView;

/**
 * 文件下载--弹出对话框--播放下载后的音频（读取音频长度倒计时播放，可中途暂停和取消）
 * （带有存储功能，如果已下载过的文件不会再去下载）
 * 用法示例:new LoadFileTools(MainOnlookersActivivty.this).loadData(fileName);
 * @author xsl 
 *
 */
public class LoadFileTools {
	
	private  String backPath;//返回的文件路径
    private  Context mcontext;
    private  MediaPlayer player; 
    private  File file;
    private  long TimeTotal = 0;//播放时间总时长
    public LoadFileTools(Context context){
    	this.mcontext = context;
    }

	/** 
	 * 下载文件
	 * @return 
	 * @return 
	 */
	public  void loadData(ImageView imageView) {
		// 判断sd卡是否挂载状态
		String sdState = Environment.getExternalStorageState();
		String path = (String) imageView.getTag();
		L.e("===========:" + path);
		// 判断sd卡是否可读写
		if (sdState.equals(Environment.MEDIA_MOUNTED)) {
			file = new File(Environment.getExternalStorageDirectory()
					.toString() + "/HSY_Yun/auto/" + path.hashCode() + ".amr");
			if (file.exists()) {// 文件存在
				backPath = file.getAbsolutePath();
				mHandler.sendEmptyMessage(0);
			} else {// 文件不存在
				downLoad(path);
			}
		}
	}
	
	/**
	 * 开启线程下载
	 */
	private  void downLoad(final String path){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				//下载地址。。。。里面地址可根据需求更改
				byte[] data = sendPost(Constant.pathurl+
		                MyApplication.getInstance().getCompanyID()+"/Action/" + path); 
				if (data != null) {
					boolean bool = savaFillScard(data, path);
					if (bool) {
						L.e("下载的录音保存成功！");
						mHandler.sendEmptyMessage(0);
					}else {
						L.e("下载的录音保存失败！");
						mHandler.sendEmptyMessage(1);
					}
				}
			}
		}.start();
	}
	
	/**
	 * 下载数据流
	 * 
	 * @param path
	 * @return
	 */
	private  byte[] sendPost(String path) {
		/*HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(path);
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toByteArray(response.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}*/
		try {
			URL imageUrl = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
		    byte[] buffer = new byte[4096];
		    int n = 0;
		    while (-1 != (n = is.read(buffer))) {
		        output.write(buffer, 0, n);
		    }
		    return output.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 向sd卡存储数据
	 * 
	 * @param data
	 *            保存数据
	 * @param path
	 *            保存文件夹路径
	 */
	public  boolean savaFillScard(byte[] data, String Paths) {
		boolean flag = false;
		// 数据I/O流
		FileOutputStream fileOutputStream = null;
		// 读取sd卡根目录
		String path = Environment.getExternalStorageDirectory().toString();
		
		File file = new File(path + "/HSY_Yun/auto/" + Paths.hashCode() + ".amr");//存放文件的路径
		
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			try {
				fileOutputStream = new FileOutputStream(file);
				//写入数据
				fileOutputStream.write(data);
				flag = true;
				backPath = file.getAbsolutePath();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fileOutputStream != null) {
					try {
						fileOutputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			flag = false;
		}

		return flag;

	}
	
	/**
	 * UI操作
	 */
	private  Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				showAlertDialog();
				break;
			case 1:
				ClueCustomToast.showToast(mcontext,
						R.drawable.toast_warn, "录音下载失败！");
				break;
			default:
				break;
			}
		};
	};

	private  void showAlertDialog(){
		LayoutInflater layoutInflater = LayoutInflater.from(mcontext);
		View view = layoutInflater.inflate(R.layout.activity_office_onlooks_recording_play, null);
		AlertDialog dialog = new AlertDialog.Builder(mcontext).create();
		dialog.setView(view, 0, 0, 0, 0);
		final Chronometer tv_start = (Chronometer)view.findViewById(R.id.tv_start);
		final ImageView img_start = (ImageView)view.findViewById(R.id.img_start);
		//设置点击外面不消失弹出框
		dialog.setCanceledOnTouchOutside(false);
		//监听窗口是否消失
		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				L.e("dialog窗体消失,释放所有播放信息");
				//窗口消失了将会走这里
				try {
					tv_start.setBase(SystemClock.elapsedRealtime());
					tv_start.stop();
					player.release();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});

		img_start.setOnClickListener(new OnClickListener() {//点击开始录音
			public void onClick(View v) {
				boolean isPlaying = false;	
				try {
					isPlaying = player.isPlaying();
					if (isPlaying) {
						// 假如在播放再次点击就是暂停
						player.pause();
						// 停止计时
						tv_start.stop();
						img_start.setImageResource(R.drawable.record_end);
					} else {
						player.start();
						tv_start.start();
						img_start.setImageResource(R.drawable.record_play);

					}
					
				} catch (Exception e) {
					// TODO: handle exception
					player = new MediaPlayer();
					// 将计时器清零
					tv_start.setBase(SystemClock.elapsedRealtime());
					// 复位计时
					tv_start.start();
					img_start
							.setImageResource(R.drawable.record_play);
					playRecordingMusic(Uri.parse(backPath));

					// 计时器监听
					tv_start.setOnChronometerTickListener(new OnChronometerTickListener() {

						@Override
						public void onChronometerTick(
								Chronometer chronometer) {
							// TODO Auto-generated method stub
							if (TimeTotal < 1000) {// 假如计时到0停止计时
								tv_start.setText("00:00");
								tv_start.stop();
								img_start.setImageResource(R.drawable.record_end);
							} else {
								// 1s跳动一次1000毫秒等于一秒
								TimeTotal = TimeTotal - 1000;
								Date date = new Date(TimeTotal);
								SimpleDateFormat format = new SimpleDateFormat(
										"mm:ss");
								String str = format.format(date);
								tv_start.setText(str);
							}
						}
					});
				}
			
				 //监听播放是否完成
				player.setOnCompletionListener(new OnCompletionListener() {
					
					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						//播放完成停止并释放播放器占用的内存
						mp.stop();
						mp.release();
						L.e("播放完成，播放器释放");
						img_start.setImageResource(R.drawable.record_end);
					}
				});
			}
		});
		dialog.show();
	}
	
	
	
	
	/**
	 * 指定音频播放
	 * @param pickUri
	 */
	private  void playRecordingMusic(Uri uri){
		try {
			player.setDataSource(mcontext, uri);
		} catch (Exception e) {
			e.printStackTrace();
		}
		final AudioManager audioManager = (AudioManager) mcontext
				.getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
			player.setAudioStreamType(AudioManager.STREAM_ALARM);
			//设置循环播放true为可循环
			player.setLooping(false);
			try {
				//播放准备
				player.prepare();
				//监听播放播放状态并获取音频总时长
				player.setOnPreparedListener(new OnPreparedListener() {
					
					@Override
					public void onPrepared(MediaPlayer mp) {
						//获取播放文件时长
						TimeTotal = mp.getDuration();
					}
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			player.start();
		}
	}
	
	
}
