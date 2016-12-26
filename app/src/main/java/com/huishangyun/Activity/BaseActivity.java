package com.huishangyun.Activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.L;
import com.huishangyun.View.MyProgressDialog;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.LoginConfig;
import com.huishangyun.yun.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 
 * @author pan
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class BaseActivity extends FragmentActivity implements BaseActivityIm{
	
	public TextView tvItem, tvTitle;
	public ImageView imgIcon1, imgIcon2;
	
	/**
	 * 通知页面头部组件 -- title名
	 * */
	public TextView tvWorkTitle;
	/**
	 * 通知页面头部组件 -- 返回图标
	 */
	public ImageView imgWorkBack;
	/**
	 * 通知页面头部组件 -- 操作图标
	 * */
	public ImageView imgWorkIcon;
	public TextView tvChatTitle;
	public Context context;
	
	//scroll_top 界面6元素
	public Button btn1, btn2, btn3;
	public ImageView imgRfh, imgPos;
	public LinearLayout liSum;
	public SharedPreferences preferences;
	public MyApplication application;
	public String IMEI,IMSI;
	public TelephonyManager tmManager;
	protected TextView backTitle;
	protected LinearLayout backBtn;
	private MyReceiver receiver;
	protected MyProgressDialog progressDialog;
	private String dialogTag = "MyDilog";
		
	//private LinearLayout progLayout;
	//private TextView progTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		MyApplication myApp = MyApplication.getInstance();
		context = this;
		preferences = getSharedPreferences(Constant.LOGIN_SET, 0);
		application = MyApplication.getInstance();
		tmManager  = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		IMSI = tmManager.getSubscriberId();
        IMEI = tmManager.getDeviceId();
        L.d("IMSI = " + IMSI);
        L.d("IMEI = " + IMEI);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.FINISH_ACTIVITY);
        receiver = new MyReceiver();
        registerReceiver(receiver, filter);
        application.addActivity(this);
        createFile();
        
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setAction(Constant.START_NOTIF);
		sendBroadcast(intent);
		L.e("onResume");
		//preferences.edit().putBoolean(Constant.HUISHANG_SCREEN_CANSHOW, false).commit();
		super.onResume();
	}
	
	@Override
	protected void onPause() {

		// TODO Auto-generated method stub
		Intent intent = new Intent();
		L.e("onPause");
		intent.setAction(Constant.START_NOTIF);
		sendBroadcast(intent);
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		L.d("注销广播");
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		//停止的时候设置可以锁屏弹窗
		//preferences.edit().putBoolean(Constant.HUISHANG_SCREEN_CANSHOW, true).commit();
		L.e("onStop");
		super.onStop();
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		//重启不可以锁屏弹窗
		preferences.edit().putBoolean(Constant.HUISHANG_SCREEN_CANSHOW, false).commit();
		L.e("onRestart");
		super.onRestart();
	}
	
	
	/**
	 * 初始化头部
	 * @param title-标题
	 */
	public void initBackTitle(String title){
		backTitle = (TextView)this.findViewById(R.id.title_title);
		backBtn = (LinearLayout)this.findViewById(R.id.title_back1);
		backTitle.setText(title);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	
	/**
	 * 初始化通知头部
	 * @param title-标题,resid-返回图标 null-为默认， listener-返回监视null-为默认
	 * iconVisibile-操作图标是否可见*/
	public void initWorkTitle(String title, Integer resid, OnClickListener listener,
			int iconVisibile){
		tvWorkTitle = (TextView)this.findViewById(R.id.tv_title_work_title);
		imgWorkBack = (ImageView)this.findViewById(R.id.img_title_work_back);
		imgWorkIcon = (ImageView)this.findViewById(R.id.img_title_work_icon);
		
		tvWorkTitle.setText(title);
		if(resid != null)
			imgWorkBack.setImageResource(resid);
		if(listener != null){
			imgWorkBack.setOnClickListener(listener);
			imgWorkIcon.setOnClickListener(listener);
		}			
		else
			imgWorkBack.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					finish();
				}});
		if(iconVisibile == View.VISIBLE)
			imgWorkIcon.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 控制scroll_top中,选中的按钮不可被点击
	 * */
	public void changeButtonEnable(Button btn){
		btn2.setEnabled(true);
		btn1.setEnabled(true);
		btn3.setEnabled(true);
		if(btn.equals(btn2))
			btn2.setEnabled(false);
		else if(btn.equals(btn1))
			btn1.setEnabled(false);
		else if(btn.equals(btn3))
			btn3.setEnabled(false);
	}
	
	/**
	 * scroll_top
	 * 获取游标划动起始位置
	 * */
	public int getStartPos(){
		if(!btn1.isEnabled())
			return 0;
		else if(!btn2.isEnabled())
			return 1;
		else
			return 2;
	}
	
	/**
	 * scroll_top
	 * 获取平移动画-同时设置按钮不可被点击
	 * @param startPos-起始位置,endPos结束位置*/
	public AnimationSet getAnimation(int startPos, int endPos){
		
		if(endPos == 0)
			changeButtonEnable(btn1);
		else if(endPos == 1)
			changeButtonEnable(btn2);
		else if(endPos == 2)
			changeButtonEnable(btn3);
		
		AnimationSet _AnimationSet = new AnimationSet(true);	
		float bitPix = getResources().getDimensionPixelSize(R.dimen.width_20_80);
		Animation animTrans = new TranslateAnimation(startPos*bitPix , endPos * bitPix, 0f, 0f);	
		_AnimationSet.addAnimation(animTrans);
		_AnimationSet.setFillBefore(false);
		_AnimationSet.setFillAfter(true);
		animTrans.setDuration(300);
		return _AnimationSet;
	}
	
	/**
	 * 获取位移动画效果
	 * */
	public AnimationSet getAnimationSet(float fromX, float toX, float fromY, float toY, long duration){
		AnimationSet _AnimationSet = new AnimationSet(true);	
		Animation animTrans = new TranslateAnimation(fromX , toX, fromY, toY);	
		_AnimationSet.addAnimation(animTrans);
		_AnimationSet.setFillBefore(false);
		_AnimationSet.setFillAfter(true);
		animTrans.setDuration(duration);
		
		return _AnimationSet;
	}
	
	/**
	 * 根据ID绑定view-视图
	 * */
	public View bindView(View parent, View view, int id){
		if(view instanceof TextView){
			view = (TextView)parent.findViewById(id);
		}
		else if(view instanceof ImageView){
			view = (ImageView)parent.findViewById(id);
		}
		else if(view instanceof EditText){
			view = (EditText)parent.findViewById(id);
		}
		else if(view instanceof LinearLayout){
			view = (LinearLayout)parent.findViewById(id);
		}
		else if(view instanceof RelativeLayout){
			view = (RelativeLayout)parent.findViewById(id);
		}
		else {
			showToast("未提供x组件的 bindView 方法 ... ");
		}
		
		return view;
	}
	
	

	/**
	 * 从sdCard显示图片
	 * 
	 * @param imageView
	 *            -组件, bootpath-路径,imgName-图片名称
	 * @author Junhui
	 */
	public void showImage(ImageView imageView, String bootpath, String imgName)
			throws IOException {
		Log.d(getLocalClassName(), "showImage -- 被执行 --- imgName == " + imgName);
		if (isHavedSDcard()) {
			try {
				String path = bootpath + "/" + imgName;
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = getInSampleSize(path);
				Bitmap bmLocal = BitmapFactory.decodeFile(path, options);
				imageView.setImageBitmap(bmLocal);
			} catch (NullPointerException e) {
				e.printStackTrace();
				imageView.setBackgroundColor(Color.RED);
			}
		} else {
			imageView.setBackgroundColor(Color.WHITE);
		}
	}
	
	/**
	 * 根据图片大小，返回缩小比例
	 * */
	public int getInSampleSize(String path){
		File file = new File(path);
		long length = file.length();
		return (int)((length/1024)*3);
	}

	/**
	 * 判断是否有SDcard
	 * 
	 * @return true-有,false-无
	 * @author Junhui
	 */
	public boolean isHavedSDcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}
	
	
	public void  ChekSDcard() {
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			new AlertDialog.Builder(context)
					.setMessage("请检查内存卡")
					.setPositiveButton("设置",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									Intent intent = new Intent(
											Settings.ACTION_SETTINGS);
									context.startActivity(intent);
								}
							})
					.setNegativeButton("退出",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									finish();
								}
							}).create().show();
		}
	}
	@Override
	public boolean validateInternet() {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			openWirelessSet();
			return false;
		} else {
			NetworkInfo[] info = manager.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		openWirelessSet();
		return false;
	}
	@Override
	public boolean hasLocationNetWork() {
		LocationManager manager = (LocationManager) context
				.getSystemService(context.LOCATION_SERVICE);
		if (manager
				.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void openWirelessSet() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder
				.setTitle("提示")
				.setMessage("无网络连接，请检查你的网络设置")
				.setPositiveButton("设置",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								/*Intent intent = new Intent(
										Settings.ACTION_DATA_ROAMING_SETTINGS);
								ComponentName cName = new ComponentName("com.android.phone","com.android.phone.Settings");
								intent.setComponent(cName);
								context.startActivity(intent);*/
								if(android.os.Build.VERSION.SDK_INT > 10 ){
								     //3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
									context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
								} else {
									context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
								}
							}
						})
				.setNegativeButton("关闭",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.cancel();
							}
						});
		dialogBuilder.show();
	}

	/**
	 * 判断文件夹是否存在
	 * 
	 * @param path
	 *            -路径,fileName-文件名
	 * @return true-存在,false-不存在
	 * @author Junhui
	 */
	public boolean isHasFile(String path, String fileName) {
		File file = new File(path + "/" + fileName);
		if (!file.exists())
			return false;
		return true;
	}

	/**
	 * 删除文件
	 * 
	 * @param path
	 *            -路径,name-文件名
	 * @author Junhui
	 */
	public void deleteFile(String path, String name) {
		File file = new File(path + "/" + name);
		if (file.exists()) // 不存在返回 false
			if (file.isFile()) // 为文件时调用删除文件方法
				deleteFile(path + name);

	}

	/**
	 * 获取SDcard根目录
	 * */
	public String getRootPath() {
		return Environment.getExternalStorageDirectory().toString();
	}

	/**
	 * toast提示
	 * 
	 * @param text
	 *            -提示内容
	 * @author Junhui
	 */
	@Override
	public void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 检测网络状态
	 * 
	 * @param context
	 *            -容器
	 * @return true-可用,false-不可用
	 */
	public static boolean isNetWorkAvailable(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			return (info != null && info.isConnected());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 动态创建线性布局
	 * 
	 * @param iOrientation
	 *            -对齐方式-垂直/水平
	 * @author Junhui
	 */
	public LinearLayout createLayout(int iOrientation) {
		LinearLayout layout = new LinearLayout(this);
		layout.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		layout.setOrientation(iOrientation);
		return layout;
	}

	/**
	 * 设置网络对话框
	 * 
	 * @author Junhui
	 */
	public void showNetUnAvailableDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("提示!");
		builder.setMessage("无可用网络，是否前去设置?");
		builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent("/");
				ComponentName cm = new ComponentName("com.android.settings",
						"com.android.settings.WirelessSettings");
				intent.setComponent(cm);
				intent.setAction("android.intent.action.VIEW");
				startActivityForResult(intent, 0);
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	/**
	 * 往shared 写入数据
	 * 
	 * @param key
	 *            -键,Object-写入的对象
	 * @author Junhui
	 * */
	public void writeObjectToShared(String key, Object object) {
		SharedPreferences sp = this.getSharedPreferences(Content.SPNAME,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		if (object instanceof Boolean) {
			editor.putBoolean(key, (Boolean) object);
		} else if (object instanceof String) {
			editor.putString(key, (String) object);
		} else if (object instanceof Integer) {
			editor.putInt(key, (Integer) object);
		}

		editor.commit();
	}

	/**
	 * 从shared里获取 String 数据
	 * 
	 * @param key
	 *            -键, defaultValues-默认返回值
	 * @return value
	 * @author Junhui
	 */
	public String getStringFromShared(String key, String defaultValue) {
		SharedPreferences sp = this.getSharedPreferences(Content.SPNAME,
				Context.MODE_PRIVATE);
		return sp.getString(key, defaultValue);
	}

	/**
	 * 从shared里获取 boolean 数据
	 * 
	 * @param key
	 *            -键, defaultValues-默认返回值
	 * @return value
	 * @author Junhui
	 */
	public Boolean getBooleanFromShared(String key, boolean defaultValue) {
		SharedPreferences sp = this.getSharedPreferences(Content.SPNAME,
				Context.MODE_PRIVATE);
		return sp.getBoolean(key, defaultValue);
	}

	/**
	 * 从shared里获取 int 数据
	 * 
	 * @param key
	 *            -键, defaultValues-默认返回值
	 * @return value
	 * @author Junhui
	 */
	public Integer getIntFromShared(String key, int defaultValue) {
		SharedPreferences sp = this.getSharedPreferences(Content.SPNAME,
				Context.MODE_PRIVATE);
		return sp.getInt(key, defaultValue);
	}

	/**
	 * 提示卸载原有APP
	 * */
	protected void showInstallDialog(String title, String message,
			final String pkg) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				startUninstall(pkg);
			}
		});
		builder.show();
	}

	/**
	 * 判断有无安装某软件
	 * 
	 * @param pkgName
	 *            -包名
	 */
	protected boolean isApkInstalled(final String pkgName) {
		try {
			getPackageManager().getPackageInfo(pkgName, 0);
			return true;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 卸载包
	 * 
	 * @param pkg
	 *            -包名称
	 */
	protected void startUninstall(String pkg) {
		Uri packageURI = Uri.parse("package:" + pkg);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		startActivity(uninstallIntent);
	}

	/**
	 * 将APP拖入通知栏
	 */
	public void showNotification(int icon, String appName, String message) {

		Intent intent = new Intent();
		ComponentName componentName = new ComponentName(
				"com.suokete",
				"com.suokete.activity.MainActivity");
		intent.setComponent(componentName);
		intent.setAction("Android.intent.action.MAIN");
		intent.addCategory("Android.intent.category.LAUNCHER");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		Notification notif = new Notification(icon, appName + "正在运行",
				System.currentTimeMillis());
		notif.flags = Notification.FLAG_NO_CLEAR;
		notif.setLatestEventInfo(this, appName, message, contentIntent);

		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.notify(R.string.app_name, notif);
	}

	/**
	 * 关闭图标通知
	 */
	public void doExit() {
		// this.finish();
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(R.string.app_name);
	}

	/**
	 * 判断sdcard上剩余空间
	 * 
	 * @param sizeMb
	 *            -MB
	 * @return true-有剩余，false-剩余不足
	 */
	public boolean isAvaiableSpace(int sizeMb) {
		boolean ishasSpace = false;
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			String sdcard = Environment.getExternalStorageDirectory().getPath();
			StatFs statFs = new StatFs(sdcard);
			long blockSize = statFs.getBlockSize();
			long blocks = statFs.getAvailableBlocks();
			long availableSpare = (blocks * blockSize) / (1024 * 1024);
			Log.d("剩余空间", "availableSpare = " + availableSpare);
			if (availableSpare > sizeMb) {
				ishasSpace = true;
			}
		}
		return ishasSpace;
	}
	
	/**
	 * 获取在dimen里的像素
	 * @param dimenId - 像素id
	 * @return 像素*/
	public double getDimenPix(int dimenId){
		return getResources().getDimensionPixelSize(dimenId);
	}
	
	/**
	 * 获取日期，精确到日
	 * 格式:2013-03-02*/
	public String getDate(){
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		return year+"-"+formatDate(month)+"-"+formatDate(day);
	}
	
	/**
	 * 获取时间，精确到秒
	 */
	public String getTime(){
		long time = getSystemTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		Date date = new Date(time);
		return formatter.format(date);
	}
	
	public String getTime2() {
		long time = getSystemTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(time);
		return formatter.format(date);
	}
	
	/**
	 * 格式化日期
	 * 如 3 返回 03
	 */
	public String formatDate(int params){
		if(params < 10)
			return "0"+params;
		else 
			return ""+params;
	}
	
	/**
	 * 做参考用
	 * 返回弹出框PopupWindow
	 * @param resid-布局id，listener-点击监视, params-布局长宽参数,可为空
	 * @return PopupWindow */
	protected PopupWindow getPopupWindow(int resid, OnClickListener listener, 
			LayoutParams params){
		LinearLayout layWindow = (LinearLayout)LayoutInflater.from(this)
				.inflate(resid, null);
		if(params == null){
			layWindow.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		}
		else{
			layWindow.setLayoutParams(params);
		}
		PopupWindow result = new PopupWindow(this);
		result.setWidth(LayoutParams.MATCH_PARENT);
		result.setHeight(LayoutParams.WRAP_CONTENT);
		
		LinearLayout temp1 = (LinearLayout)layWindow.findViewById(R.id.lay_pop_email_del);
		LinearLayout temp2 = (LinearLayout)layWindow.findViewById(R.id.lay_pop_email_replay);
		LinearLayout temp3 = (LinearLayout)layWindow.findViewById(R.id.lay_pop_email_forword);
			
		if(listener != null){
			temp1.setOnClickListener(listener);
			temp2.setOnClickListener(listener);
			temp3.setOnClickListener(listener);
		}else
			Log.i("inof -->>", "注意,弹出框没有监视!!!");	
		
		result.setBackgroundDrawable(getResources().getDrawable(R.color.h_Transparent));
		result.setContentView(layWindow);
		result.setFocusable(true); //设置Popuresultindow可获得焦点
		result.setTouchable(true);
		result.setOutsideTouchable(true);
		
		return result;
	}
	
	//----------------
	//----------------
	//---以下为测试方法----
	//----------------
	//----------------
	
	public List<Map<String, Object>> getData(int start, int end){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(int i=start;i<end;i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", "name "+ i);
			map.put("sex", i%2==0?"女":"男");
			list.add(map);
		}
		return list;
	}
	
	public List<Map<String, Object>> getData(int start, int end, Integer resid1, Integer resid2){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(int i=start;i<end;i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", "name "+ i);
			map.put("sex", i%2==0?"女":"男");
			if(resid1 != null)
				map.put("icon1", resid1);
			if(resid2 != null)
				map.put("icon2", resid2);
			list.add(map);
		}
		return list;
	}
	
	public List<Map<String, Object>> getNoticeData(int start, int end){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(int i=start;i<end;i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", i+"-放假通知:根据国家规定，并结合我公司实际，决定：清明节放假日期从2013年 4月4日至7日，共3天。");
			map.put("company", "XXX公司");
			map.put("date", getDate());
			list.add(map);
		}
		return list;
	}
	
	/**
	 * 获取当前网络设置
	 */
	public String getApnType(Context context){
			        String apntype="";
			        Uri uriSet = Uri.parse("content://telephony/carriers/preferapn");  
			        Cursor c = context.getContentResolver().query(uriSet,
			                null, null, null, null);
			        if(c != null && c.getCount() >= 1){
			            c.moveToFirst();
			            String user=c.getString(c.getColumnIndex("user"));
			            if(user.startsWith("ctnet")){
			                apntype="ctnet";
			            }else if(user.startsWith("ctwap")){
			                apntype="ctwap";
			            }
			        }
			        c.close();
			        return apntype;
			    }
	/**
	 * 设备联网类型
	 */
	public void setApnType(Context context,String type){   
    	Uri uri = Uri.parse("content://telephony/carriers");  
    	try{
    	Cursor cr = context.getContentResolver().query(uri, null, null, null, null);      
    	while(cr!=null && cr.moveToNext()){ 
    		if(cr.getString(cr.getColumnIndex("apn")).equals("#777")&&cr.getString(cr.getColumnIndex("user")).startsWith(type))
    		{
    			Uri uriSet = Uri.parse("content://telephony/carriers/preferapn");    
        		ContentResolver resolver = context.getContentResolver();   
        		ContentValues values = new ContentValues();   
        		values.put("apn_id", cr.getString(cr.getColumnIndex("_id")));   
        		resolver.update(uriSet, values, null, null);
    		}
    	}
    	}catch(Exception e){}
   }
	 /** 
     *  
     * 关闭键盘事件 
     *  
     * @author pan
     */  
    public void closeInput() {  
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
        if (inputMethodManager != null && this.getCurrentFocus() != null) {  
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()  
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
        }  
    }

	@Override
	public MyApplication getMyApplication() {
		// TODO Auto-generated method stub
		return application;
	}

	@Override
	public void stopService(Context context) {
		// TODO Auto-generated method stub
		// 好友联系人服务
		
		// 聊天服务
		//Intent chatServer = new Intent(context, IMChatService.class);
		//context.stopService(chatServer);
	}

	@Override
	public void startService() {
		// TODO Auto-generated method stub
		// 聊天服务
		//Intent chatServer = new Intent(context, IMChatService.class);
		//context.startService(chatServer);
	}

	@Override
	public boolean hasInternetConnected() {
		// TODO Auto-generated method stub
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE);
		if (manager != null) {
			NetworkInfo network = manager.getActiveNetworkInfo();
			if (network != null && network.isConnectedOrConnecting()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void isExit() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(context).setTitle("退出").setMessage("确定退出吗?")
		.setNeutralButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				stopService(context);
				finish();
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).show();
	}

	@Override
	public boolean hasLocationGPS() {
		// TODO Auto-generated method stub
		LocationManager manager = (LocationManager) context
				.getSystemService(context.LOCATION_SERVICE);
		if (manager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean checkMemoryCard() {
		// TODO Auto-generated method stub
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			new AlertDialog.Builder(context)
					.setTitle("提示")
					.setMessage("请检查内存卡")
					.setPositiveButton("设置",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									Intent intent = new Intent(
											Settings.ACTION_SETTINGS);
									context.startActivity(intent);
								}
							})
					.setNegativeButton("退出",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									finish();
									System.gc();
								}
							}).create().show();
			return false;
		} else {
			return true;
		}
		
	}


	@Override
	public ProgressDialog getProgressDialog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return context;
	}

	@Override
	public SharedPreferences getLoginUserSharedPre() {
		// TODO Auto-generated method stub
		return preferences;
	}

	@Override
	public void saveLoginConfig(LoginConfig loginConfig) {
		// TODO Auto-generated method stub
		preferences.edit()
		.putString(Constant.XMPP_HOST, loginConfig.getXmppHost())
		.commit();
		preferences.edit()
				.putInt(Constant.XMPP_PORT, loginConfig.getXmppPort()).commit();
		preferences
				.edit()
				.putString(Constant.XMPP_SEIVICE_NAME,
						loginConfig.getXmppServiceName()).commit();
		preferences.edit()
				.putString(Constant.USERNAME, loginConfig.getUsername())
				.commit();
		preferences.edit()
				.putString(Constant.PASSWORD, loginConfig.getPassword())
				.commit();
		preferences.edit()
				.putBoolean(Constant.IS_AUTOLOGIN, loginConfig.isAutoLogin())
				.commit();
		preferences.edit()
				.putBoolean(Constant.IS_NOVISIBLE, loginConfig.isNovisible())
				.commit();
		preferences.edit()
				.putBoolean(Constant.IS_REMEMBER, loginConfig.isRemember())
				.commit();
		preferences.edit()
				.putBoolean(Constant.IS_ONLINE, loginConfig.isOnline())
				.commit();
		preferences.edit()
				.putBoolean(Constant.IS_FIRSTSTART, loginConfig.isFirstStart())
				.commit();
		preferences.edit()
				.putString(Constant.XMPP_LOGIN_NAME, loginConfig.getXmppLoginName())
				.commit();
	}

	@Override
	public LoginConfig getLoginConfig() {
		// TODO Auto-generated method stub
		LoginConfig loginConfig = new LoginConfig();
		String a = preferences.getString(Constant.XMPP_HOST, null);
		loginConfig.setUsername(preferences.getString(Constant.USERNAME, null));
		loginConfig.setPassword(preferences.getString(Constant.PASSWORD, null));

		loginConfig.setAutoLogin(preferences.getBoolean(Constant.IS_AUTOLOGIN,
				getResources().getBoolean(R.bool.is_autologin)));
		loginConfig.setNovisible(preferences.getBoolean(Constant.IS_NOVISIBLE,
				getResources().getBoolean(R.bool.is_novisible)));
		loginConfig.setRemember(preferences.getBoolean(Constant.IS_REMEMBER,
				getResources().getBoolean(R.bool.is_remember)));
		loginConfig.setFirstStart(preferences.getBoolean(
				Constant.IS_FIRSTSTART, true));
		loginConfig.setXmppLoginName(preferences.getString(Constant.XMPP_LOGIN_NAME, ""));
		preferences.getInt(Content.COMPS_ID, 1020);
		preferences.getString(Content.COMPS_IMGURL, "");
		preferences.getString(Content.COMPS_NAME, "南昌索科特科技有限公司");
		preferences.getString(Content.COMPS_DOMAIN, "yqy");
		return loginConfig;
	}

	@Override
	public boolean getUserOnlineState() {
		// TODO Auto-generated method stub
		return preferences.getBoolean(Constant.IS_ONLINE, true);
	}

	@Override
	public void setUserOnlineState(boolean isOnline) {
		// TODO Auto-generated method stub
		preferences.edit().putBoolean(Constant.IS_ONLINE, isOnline).commit();
	}

	@Override
	public void setNotiType(int iconId, String contentTitle,
			String contentText, Class activity, String from) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showToast(String text, int longint) {
		// TODO Auto-generated method stub
		
	}
	
	private class MyReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			L.d("接收到关闭广播");
			finish();
		}
		
	}
	/**
	 * 检查文件夹是否已创建！
	 */
	private void createFile(){
		File autofile = new File(Constant.SAVE_AUTO_PATH);
		File imgFile = new File(Constant.SAVE_IMG_PATH);
		if (!autofile.exists()) {
			autofile.mkdirs();
		}
		if (!imgFile.exists()) {
			imgFile.mkdirs();
		}
	}
	/**
	 * Ping服务器
	 * @return
	 */
	public boolean PingService() {
		//return CheckServicerUrl("http://wap.baidu.com");
		
		Runtime run = Runtime.getRuntime(); 
		Process proc = null; 
		try { 
		String str = "ping -c 1 -i 0.2 -W 1 www.baidu.com"; 
		//System.out.println(str);
		L.v(str);
		proc = run.exec(str); 
		int result = proc.waitFor(); 
		if(result == 0) 
		{ 
		//Toast.makeText(ClientActivity.this, "ping连接成功", Toast.LENGTH_SHORT).show(); 
			L.e("ping连接成功");
			return true;
		} 
		else 
		{ 
		//Toast.makeText(ClientActivity.this, "ping测试失败", Toast.LENGTH_SHORT).show(); 
			L.e("ping测试失败");
			return false;
		} 
		} catch (IOException e) { 
		e.printStackTrace(); 
		} catch (InterruptedException e) { 
		e.printStackTrace(); 
		} finally { 
		proc.destroy(); 
		} 
		return false;
	}
	
	/**
	 * 获取系统时间
	 * @return
	 */
	public long getSystemTime() {
		
		return System.currentTimeMillis();
	}
	
	public void initDialog() {
		/*progLayout = (LinearLayout) this.findViewById(R.id.loadprogerssbar);
		progTextView = (TextView) this.findViewById(R.id.ladprog_txt);*/
	}
	
	/**
	 * 显示进度条
	 * @param message-需要显示的文字
	 */
	public void showDialog(String message) {
		try {
			
		
		/*progressDialog.setMessage(message);
		progressDialog.show(getFragmentManager(), dialogTag);*/
		progressDialog = new MyProgressDialog();
        progressDialog.setMessage(message);
        progressDialog.setCancelable(true);
		progressDialog.show(getSupportFragmentManager(), message);
		//progTextView.setText(message);
		//progLayout.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void showNotDialog(String message) {
		try {
				/*progressDialog.setMessage(message);
				progressDialog.show(getFragmentManager(), dialogTag);*/
				progressDialog = new MyProgressDialog();
		        progressDialog.setMessage(message);
		        progressDialog.setCancelable(false);
				progressDialog.show(getSupportFragmentManager(), message);
				//progTextView.setText(message);
				//progLayout.setVisibility(View.VISIBLE);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
	}
	
	/**
	 * 更新进度条文字
	 * @param message
	 */
	public void updataDialog(String message) {
		try {
			/*if (progressDialog == null) {
				progressDialog = new MyProgressDialog();
		        progressDialog.setMessage(message);
		        progressDialog.setCancelable(false);
				progressDialog.show(getSupportFragmentManager(), dialogTag);
			} else {*/
				progressDialog.updataMenssage(message);
			//}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 销毁进度条
	 */
	public void dismissDialog() {
		try {
			progressDialog.dismiss();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		//progLayout.setVisibility(View.GONE);
	}
	
	/**
	 * 显示自定义的Toast
	 * @param message
	 * @param isSuccess
	 */
	public void showCustomToast(String message, boolean isSuccess) {
		if (isSuccess) {
			ClueCustomToast.showToast(BaseActivity.this, R.drawable.toast_sucess, message);
		} else {
			ClueCustomToast.showToast(BaseActivity.this, R.drawable.toast_warn, message);
		}
	}
	
	/**
	 * 获取文件路径
	 * @param uri
	 * @return
	 */
	public String getFilePath(Uri uri) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };  
		   
        Cursor cursor = getContentResolver().query(uri,  
                filePathColumn, null, null, null);  
        cursor.moveToFirst();  
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);  
        String picturePath = cursor.getString(columnIndex); 
        return picturePath;
	}
	
}
