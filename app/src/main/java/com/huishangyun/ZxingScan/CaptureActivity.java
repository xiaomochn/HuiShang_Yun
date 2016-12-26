package com.huishangyun.ZxingScan;

import java.io.IOException;
import java.util.Vector;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.ZxingScan.decoding.CaptureActivityHandler;
import com.huishangyun.ZxingScan.decoding.InactivityTimer;
import com.huishangyun.ZxingScan.view.ViewfinderView;
import com.huishangyun.yun.R;
import com.huishangyun.ZxingScan.camera.CameraManager;

/**
 * 主Activity
 */
public class CaptureActivity extends BaseActivity implements Callback
{
	private String TAG = "CaptureActivity.java";
	private boolean isDebug = false;
	
	private CaptureActivityHandler handler;
	/** 二维码框框 */
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	/** 扫描结果显示 */
	private TextView txtResult;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	/** 扫描到后，嘀一声 */
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	/** 手机振动器 */
	private boolean vibrate;
	
	/** 当前是否是竖屏（否则为横屏） */
	public static boolean IS_PORTRAIT = true;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_zxing);
		// 初始化 CameraManager
		CameraManager.init(getApplication());

		// 二维码框框
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		txtResult = (TextView) findViewById(R.id.txtResult);
		txtResult.setOnClickListener(new View.OnClickListener(){
			public void onClick(View arg0){
				// 重新扫描
				if (handler != null)
				{
					handler.restartPreviewAndDecode();
				}
			}
		});
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		
		// 竖屏
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			IS_PORTRAIT = true;
		}
		// 横屏
		else
		{
			IS_PORTRAIT = false;
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		/** 摄像头预览 */
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface)
		{
			initCamera(surfaceHolder);
		}
		else
		{
			surfaceHolder.addCallback(this);
			// 表明该Surface不包含原生数据,Surface用到的数据由其他对象提供,在Camera图像预览中就使用该类型的Surface,有Camera
			// 负责提供给预览Surface
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		// 扫描到后，嘀一声
		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		// 如果当前不为‘声音模式’（比如静音），则设置为不‘扫描到，不嘀’。
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
		{
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (handler != null)
		{
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy()
	{
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * 摄像头初始化
	 */
	private void initCamera(SurfaceHolder surfaceHolder)
	{
		try
		{
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe)
		{
			return;
		} catch (RuntimeException e)
		{
			return;
		}
		
		// 【重要】该对象启动相机，实现自动聚焦，创建DecodeThread线程，DecodeThread创建Decodehandler，这个对象就获取从相机得到的原始byte数据，开始解码的第一步工作，
		// 从获取的byte中解析qr图来，并解析出qr图中的字符，将这块没有分析的字符抛送到CaptureActivityHandler中handle，
		// 该类调用main activity的decode函数完成对字符的分析，最后显示在界面上（刷新UI，最好在UI线程里完成）。
		if (handler == null)
		{
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		if (!hasSurface)
		{
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		hasSurface = false;
	}

	public ViewfinderView getViewfinderView()
	{
		return viewfinderView;
	}

	public Handler getHandler()
	{
		return handler;
	}

	public void drawViewfinder()
	{
		viewfinderView.drawViewfinder();
	}

	public void handleDecode(Result obj, Bitmap barcode)
	{
		inactivityTimer.onActivity();
		viewfinderView.drawResultBitmap(barcode);
		playBeepSoundAndVibrate();
						
		//txtResult.setText("二维码格式：" + obj.getBarcodeFormat().toString() + "\n二维码内容：" + obj.getText());
		Intent intent = getIntent();// 传送扫描的结果返回。
		Log.i("-------------------------------", "扫描的内容：" + obj.getText());
		intent.putExtra("text", obj.getText());//
		setResult(2, intent);
		finish();
		
	}

	/**
	 * 初始化扫描成功的嘀嘀声。
	 */
	private void initBeepSound()
	{
		if (playBeep && mediaPlayer == null)
		{
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			// 设置当前控制的音量流为：音乐播放流
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.qrcode_completed);
			try{
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e){
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate()
	{
		if (playBeep && mediaPlayer != null)
		{
			mediaPlayer.start();
		}
		if (vibrate)
		{
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * 当前音乐播放完后，倒带到音乐头部，便于下一次播放。 When the beep has finished playing, rewind to
	 * queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener()
	{
		public void onCompletion(MediaPlayer mediaPlayer){
			mediaPlayer.seekTo(0);
		}
	};

}