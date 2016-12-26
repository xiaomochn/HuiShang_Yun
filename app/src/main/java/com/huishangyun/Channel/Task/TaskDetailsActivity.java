package com.huishangyun.Channel.Task;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Adapter.FileAdapter;
import com.huishangyun.Adapter.MyPagerAdapter;
import com.huishangyun.Adapter.TaskProgerAdapter;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Interface.onItemChanger;
import com.huishangyun.Util.Base64Util;
import com.huishangyun.Util.ColorUtil;
import com.huishangyun.Util.Common;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.FileUtils;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.OpenFileUtil;
import com.huishangyun.Util.PhotoHelp;
import com.huishangyun.Util.TaskProgerUtil;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.Util.Utility;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.View.MyAlerDialog;
import com.huishangyun.View.MyBuilder;
import com.huishangyun.manager.EnumManager;
import com.huishangyun.model.CallSystemApp;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.EnumKey;
import com.huishangyun.model.Methods;
import com.huishangyun.task.UpLoadFileTask;
import com.huishangyun.task.UpLoadFileTask.OnUpLoadResult;
import com.huishangyun.task.UpLoadImgSignText;
import com.huishangyun.yun.R;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务详情界面
 * @author-Pan
 * @see-无
 * @since-亿企云APP/任务
 * @version-v1.0
 * 
 */
@SuppressLint("InflateParams")
public class TaskDetailsActivity extends BaseActivity implements OnUpLoadResult{
	private ViewPager mPager;//ViewPager对象
	private List<View> pagerList;
	private Button detailBtn;// 详情按钮
	private Button progreBtn;// 进度按钮
	private Button otherBtn;
	private ImageView lineImg;// 底部线条 
	private int bmpW;// 图片资源
	private int offset;// 偏移量
	private int currIndex = 0;//当前ViewPager的位置
	private LayoutInflater mInflater;
	private View detailView;//任务详情页面
	private View progreView;//任务进度页面
	private DisplayMetrics dm;//获取屏幕尺寸
	private ListView filelist;
	private List<TaskProgerUtil> progerList;
	private ListView proglist;
	private SeekBar mBar;//进度条
	private TextView mBarTex;//进度条百分比
	private Button takePhoto;//增加图片按钮
	private Button takeVideo;//添加视频
	private Button takeRecord;//添加录音
	private Button takeFile;//添加文件
	private String photoPath = "";
	private String videoPath = "0";
	private Button sendButton;
	private Button backButton;
	private Button submitButton;
	private int ID;
	//下面是任务详情
	private TextView themeTxt;//主题
	private TextView beginTxt;//开始日期
	private TextView endTxt;//结束日期
	private TextView priority;//优先级
	private TextView executor;//执行人
	private TextView state;//状态
	private TextView customers;//商机
	private TextView opport;//客户
	private TextView created;//创建人
	private TextView createtime;//创建时间
	private TextView department;//部门
	private TextView description;//描述
	private TaskProgerAdapter progAdapter;
	private EditText miaoshu;
	private MediaRecorder mRecorder;
	private int Progress = 0;
	private static final int CHOOSE_FILE = 4;//选择文件
	private String AttachmentPath = "";//后台文件地址
	private String Attachment = "";//要上传的文件名
	private FileAdapter fileAdapter;
	private List<Map<String, Object>> fileList;
	private TaskModel model = new TaskModel();
	private RelativeLayout menuLayout;
	private LinearLayout progLayout;
	private boolean isExed = false;
	private boolean isTrue = false;
	private ImageView Wu;
	private ScrollView scrollView;
	private String filePath;
	private boolean IsRecord = false;
	private Button recordBtn;
	private LinearLayout prog_mic_layout;
	private ImageView prog_mic_img;
	private TextView cancel, confirm, tv_end;
	private int[] micimg = { R.drawable.voice_vol01, R.drawable.voice_vol02,
			R.drawable.voice_vol03, R.drawable.voice_vol04, R.drawable.voice_vol05 };
	private int RESULT_CODE = RESULT_CANCELED;
	private boolean isSame = false;
	private boolean isTuihui = false;
	private MyAlerDialog dialog;
	private Chronometer tv_start;
	private Button submit;
	private LinearLayout luyin_start, luyin_end, lin_face;
	private ImageView img_start, img_end;
	private SimpleDateFormat timeStampFormat = new SimpleDateFormat(
				"yyyyMMddHHmmss");	//获取当前时间格式实例	
	 
	  //语音文件保存路径  
    private String autoName = null; 
    private String autoPath = null;
    private SoundPool spPool;//声明一个SoundPool
    private int music;//定义一个整型用load（）来设置suondID
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_details);
		initView();
		initViewPager();
		initBackTitle("任务详情");
		showDialog("正在加载...");
		getTask(ID);
	}
	
	@Override
	public void initBackTitle(String title) {
		// TODO Auto-generated method stub
		//super.initBackTitle(title);
		backTitle = (TextView)this.findViewById(R.id.title_title);
		backBtn = (LinearLayout)this.findViewById(R.id.title_back1);
		backTitle.setText(title);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(RESULT_CODE);
				finish();
			}
		});
	}
	
	/**
	 * 实例化各组件
	 */
	@SuppressLint("InflateParams")
	@SuppressWarnings("static-access")
	private void initView() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constant.HUISHANG_RE_ACTION);
		registerReceiver(receiver, intentFilter);
		ID = getIntent().getIntExtra("ID", 0);
		mPager = (ViewPager)this.findViewById(R.id.viewpager_miantask);
		lineImg = (ImageView)this.findViewById(R.id.task_line);
		detailBtn = (Button)this.findViewById(R.id.task_all);
		progreBtn = (Button)this.findViewById(R.id.task_launch);
		menuLayout = (RelativeLayout)this.findViewById(R.id.task_details_turnlayout);
		//设置按钮文字
		detailBtn.setText("任务详情");
		progreBtn.setText("执行进度");
		spPool = new SoundPool(music, AudioManager.STREAM_SYSTEM, 5);
		//把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		music = spPool.load(TaskDetailsActivity.this, R.raw.qrcode_completed, 1); 
		//初始化ViewPager
		pagerList = new ArrayList<View>();
		mInflater = getLayoutInflater().from(TaskDetailsActivity.this);
		detailView = mInflater.inflate(R.layout.task_detail_info, null);
		progreView = mInflater.inflate(R.layout.task_detail_progre, null);
		scrollView = (ScrollView) progreView.findViewById(R.id.task_detail_scroll);
		initDetailView(detailView);
		recordBtn = (Button) progreView.findViewById(R.id.task_prog_record);
		recordBtn.setVisibility(View.GONE);
		recordBtn.setOnTouchListener(onTouchListener);
		prog_mic_layout = (LinearLayout) progreView.findViewById(R.id.prog_mic_layout);
		prog_mic_img = (ImageView) progreView.findViewById(R.id.prog_mic_img);
		prog_mic_layout.setVisibility(View.GONE);
		Wu = (ImageView) progreView.findViewById(R.id.task_detail_wu);
		progLayout = (LinearLayout) progreView.findViewById(R.id.task_detail_linear);
		filelist = (ListView) progreView.findViewById(R.id.task_prog_filelist);
		mBar = (SeekBar) progreView.findViewById(R.id.sBar);
		mBarTex = (TextView) progreView.findViewById(R.id.sBartxt);
		mBar.setOnSeekBarChangeListener(seekBarChangeListener);
		mBar.setProgress(0);
		mBarTex.setText("0%");
		progerList = new ArrayList<TaskProgerUtil>();
		
		//设置进度列表
		proglist = (ListView) progreView.findViewById(R.id.task_prog_list);
		progAdapter = new TaskProgerAdapter(TaskDetailsActivity.this, progerList);
		proglist.setAdapter(progAdapter);
		
		//计算listview的高度
		setListViewHeight(proglist);
		fileList = new ArrayList<Map<String,Object>>();
		fileAdapter = new FileAdapter(TaskDetailsActivity.this, fileList);
		filelist.setAdapter(fileAdapter);
		
		//点击列表查看文件
		filelist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Map<String,Object> map = fileList.get(arg2);
				String filePath = (String) map.get("file");
				L.e("filepath = " + filePath);
				File file = new File(filePath);
				OpenFileUtil.openFile(file, TaskDetailsActivity.this);
			}
		});
		submitButton = (Button) progreView.findViewById(R.id.task_prog_submit);
		miaoshu = (EditText) progreView.findViewById(R.id.task_prog_miaoshu);
		//计算listview的高度
		setListViewHeight(filelist);
		fileAdapter.setOnItemChanger(new onItemChanger() {
			
			@Override
			public void itemChaenged(int position) {
				// TODO Auto-generated method stub
				//重新计算高度
				fileList.remove(position);
				fileAdapter.refresh();
				setListViewHeight(filelist);
			}
		});
		pagerList.add(detailView);
		pagerList.add(progreView);
		detailBtn.setOnClickListener(mListener);
		progreBtn.setOnClickListener(mListener);
		
		//初始化文件选择按钮
		takePhoto = (Button) progreView.findViewById(R.id.task_prog_takephotp);
		takeVideo = (Button) progreView.findViewById(R.id.task_prog_takevideo);
		takeRecord = (Button) progreView.findViewById(R.id.task_prog_takerecord);
		takeFile = (Button) progreView.findViewById(R.id.task_prog_takefile);
		backButton = (Button) this.findViewById(R.id.task_details_back);
		sendButton = (Button) this.findViewById(R.id.task_details_turn);
		backButton.setOnClickListener(mListener);
		sendButton.setOnClickListener(mListener);
		takePhoto.setOnClickListener(mListener);
		takeVideo.setOnClickListener(mListener);
		takeRecord.setOnClickListener(mListener);
		takeFile.setOnClickListener(mListener);
		submitButton.setOnClickListener(mListener);
		
	}
	
	/**
	 * 实例化详情组件
	 * @param view
	 */
	private void initDetailView(View view) {
		themeTxt = (TextView) view.findViewById(R.id.task_detail_theme);
		beginTxt = (TextView) view.findViewById(R.id.task_detail_begin);
		endTxt = (TextView) view.findViewById(R.id.task_detail_end);
		priority = (TextView) view.findViewById(R.id.task_detail_priority);
		executor = (TextView) view.findViewById(R.id.task_detail_executor);
		state = (TextView) view.findViewById(R.id.task_detail_state);
		opport = (TextView) view.findViewById(R.id.task_detail_opportunities);
		customers = (TextView) view.findViewById(R.id.task_detail_customers);
		createtime = (TextView) view.findViewById(R.id.task_detail_createtime);
		created = (TextView) view.findViewById(R.id.task_detail_created);
		department = (TextView) view.findViewById(R.id.task_detail_department);
		description = (TextView) view.findViewById(R.id.task_detail_description);
	}
	
	/**
	 * 查询任务详情
	 * @param ID
	 * 
	 */
	private void getTask(final int ID) {
		new Thread(new GetTaskDetails(new ResultForTask<TaskModel>() {
			
			@Override
			public void onResult(int resultCode, TaskModel details) {
				// TODO Auto-generated method stub
				dismissDialog();
				switch (resultCode) {
				case RESULT_OK://查询成功
					 if (EnumManager.getInstance(context).getEmunForIntKey(EnumKey.ENUM_TASKFLAG, "" + details.getFlag())==null) {
	                    	showCustomToast("相应任务已被删除！", false);
							finish();
							return;
						}
					model = details;
					themeTxt.setText(details.getTitle());
					beginTxt.setText(TimeUtil.getTime(details.getStartTime()));
					endTxt.setText(TimeUtil.getTime(details.getEndTime()));
					String prior = EnumManager.getInstance(context).getEmunForIntKey(EnumKey.ENUM_TASKFLAG, "" + details.getFlag()).getLab();
					priority.setText(prior);
					executor.setText(details.getExeManager_Name());
					String str = EnumManager.getInstance(context).getEmunForIntKey(EnumKey.ENUM_TASKSTATUS,  "" + details.getStatus()).getLab();
					state.setText(str);
					ColorUtil.setTaskColor(TaskDetailsActivity.this, details.getStatus(), state);
					opport.setText(details.getMember_Name());
					customers.setText(details.getBusiness_Title());
					createtime.setText(TimeUtil.getTime(details.getAddDateTime()));
					department.setText(details.getDepartment_Name());
					description.setText(details.getNote());
					created.setText(details.getManager_Name());
					int exeID = details.getExeManager_ID();
					if (exeID == details.getManager_ID() && exeID == Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, ""))) {
						backButton.setVisibility(View.GONE);
						if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
							menuLayout.setVisibility(View.GONE);
						} else {
							isTuihui = true;
							backButton.setVisibility(View.GONE);
						}
						
						isSame = true;
					}
					if (exeID == details.getManager_ID()) {
						
					}
					if (details.getStatus() == 4) {
						progLayout.setVisibility(View.GONE);
						menuLayout.setVisibility(View.GONE);
						isTrue = true;
					}
					if (str.equals("退回")) {
						backButton.setVisibility(View.GONE);
					}
					try {
						SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date date = formatDate.parse(TimeUtil.getTimeForT(details.getEndTime()));
						long time = date.getTime();
						L.e("逾期时间" + time);
						L.e("当前时间" + new Date().getTime());
						if ((time + 86400000) < new Date().getTime() && (details.getStatus() == 1 || details.getStatus() == 2)) {
							state.setText("逾期");
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (exeID != Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, ""))) {
						isExed = false;
						menuLayout.setVisibility(View.GONE);
						progLayout.setVisibility(View.GONE);
					} else {
						isExed = true;
						if (preferences.getString(Constant.HUISHANG_TYPE, "0").equals("1")) {
							sendButton.setVisibility(View.GONE);
						}
					}
					getTaskProg(ID);
					break;
					
				case RESULT_FULL://查询失败
					showCustomToast("查询失败", false);
					break;

				default:
					break;
				}
			}

			@Override
			public void onResults(int resultCode, List<TaskModel> resultDatas) {
				// TODO Auto-generated method stub
				
			}
		}, ID)).start();
		
	}
	
	/**
	 * 获取任务进度
	 * @param ID
	 */
	private void getTaskProg(int ID) {
		new Thread(new GetTaskProgressList(ID, new ResultForTask<TaskProgerUtil>() {

			@Override
			public void onResult(int resultCode, TaskProgerUtil resultData) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onResults(int resultCode,
					List<TaskProgerUtil> resultDatas) {
				// TODO Auto-generated method stub
				if (resultCode == RESULT_OK) {
					progerList.clear();
					L.e("isExed = " + isExed);
					if (isExed) {
						Wu.setVisibility(View.GONE);
						L.e("隐藏1");
					} else {
						if (resultDatas.size() == 0) {
							Wu.setVisibility(View.VISIBLE);
							L.e("显示");
						} else {
							Wu.setVisibility(View.GONE);
							L.e("隐藏2");
						}
					}
					for (TaskProgerUtil taskProgerUtil : resultDatas) {
						progerList.add(taskProgerUtil);
					}
					if (resultDatas.size() > 0) {
						TaskProgerUtil taskProgerUtil = resultDatas.get(resultDatas.size() - 1);
						mBar.setProgress(taskProgerUtil.getProgressNum());
					}
					
					
					progAdapter.notifyDataSetChanged();
					setListViewHeight(proglist);
					
					
				} 
				
			}
		})).start();
	}
	
	/**
	 * 设置ViewPager
	 */
	private void initViewPager() {
		bmpW = lineImg.getWidth();// 获取图片宽度
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		// 设置图片宽度
		FrameLayout.LayoutParams mParams = (FrameLayout.LayoutParams) lineImg
				.getLayoutParams();
		mParams.width = dm.widthPixels / 3;
		lineImg.setLayoutParams(mParams);
		lineImg.setBackgroundColor(0xff00658f);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		lineImg.setImageMatrix(matrix);// 设置动画初始位置

		// 添加Adapter
		mPager.setAdapter(new MyPagerAdapter(pagerList));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		detailBtn.setTextColor(0xff00658f);
		progreBtn.setTextColor(0xff646464);
	}
	
	/**
	 * 页卡变动监听
	 * 
	 * @author pan
	 * 
	 */
	private class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			setTopView(arg0);
			L.d("arg0 == " + arg0);
		}

	}
	
	/**
	 * 设置顶部偏移
	 * 
	 * @param arg0
	 */
	private void setTopView(int arg0) {
		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		Animation animation = null;
		switch (arg0) {
		case 0:
			animation = new TranslateAnimation(one, 0, 0, 0);
			detailBtn.setTextColor(0xff00658f);
			progreBtn.setTextColor(0xff646464);
			if ((isExed && !isTrue && !isSame) || isTuihui) {
				menuLayout.setVisibility(View.VISIBLE);
			}
			break;
		case 1:
			animation = new TranslateAnimation(offset, one, 0, 0);
			detailBtn.setTextColor(0xff646464);
			progreBtn.setTextColor(0xff00658f);
			if (isExed) {
				menuLayout.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
		currIndex = arg0;
		animation.setFillAfter(true);// True:图片停在动画结束位置
		animation.setDuration(300);
		lineImg.startAnimation(animation);
	}
	
	/**
	 * 点击事件监听
	 */
	private OnClickListener mListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent mIntent = null;
			switch (v.getId()) {
			case R.id.task_all://点击了头部的任务详情
				mPager.setCurrentItem(0);
				currIndex = 0;
				break;
			case R.id.task_launch://点击了头部的执行进度
				mPager.setCurrentItem(1);
				currIndex = 0;
				break;
				
			case R.id.task_prog_takephotp://选择照片
				
				/*mIntent = new Intent("android.media.action.IMAGE_CAPTURE");
				photoPath = Constant.SAVE_IMG_PATH + File.separator
						+ getSystemTime() + ".jpg";
				mIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(new File(photoPath)));
				startActivityForResult(mIntent, EXTRA_TAKE_PHOTOS);
				mIntent = null;*/
				photoPath = Constant.SAVE_IMG_PATH + File.separator
						+ getSystemTime() + ".jpg";
				CallSystemApp.callCamera(TaskDetailsActivity.this, photoPath);
				
				break;
			case R.id.task_prog_takevideo://选择摄像
				
				videoPath = Constant.SAVE_IMG_PATH + File.separator
						+ getSystemTime() + ".3gp";
				CallSystemApp.callVideo(TaskDetailsActivity.this, videoPath);
				break;
			case R.id.task_prog_takerecord://选择录音
				/*CallSystemApp.callRecord(TaskDetailsActivity.this);*/
				/*if (!IsRecord) {
					submitButton.setVisibility(View.GONE);
					recordBtn.setVisibility(View.VISIBLE);
					IsRecord = true;
				} else {
					submitButton.setVisibility(View.VISIBLE);
					recordBtn.setVisibility(View.GONE);
					IsRecord = false;
				}*/
				IsRecord = false;
			    initRecovod();
				break;
			case R.id.task_prog_takefile://选择文件
				mIntent = new Intent(TaskDetailsActivity.this, TaskChooseFileActivity.class);
				startActivityForResult(mIntent, CHOOSE_FILE);
				mIntent = null;
				
				break;
			case R.id.task_details_turn://任务转派
				mIntent = new Intent(TaskDetailsActivity.this, TaskSendActivity.class);
				mIntent.putExtra("model", model);
				break;
			case R.id.task_details_back://任务退回
				mIntent = new Intent(TaskDetailsActivity.this, TaskBackActivity.class);
				mIntent.putExtra("model", model);
				break;
			case R.id.task_prog_submit://提交按钮
				if (miaoshu.getText().toString().trim().equals("")) {//判断是否输入描述字段
					showCustomToast("请输入描述", false);
					return;
				}
				submitTaskProger();
				break;
			default:
				break;
			}
			if (mIntent != null) {
				startActivity(mIntent);
			}
		}
	};
	
	
	private void initRecovod(){
		LayoutInflater layoutInflater = LayoutInflater.from(TaskDetailsActivity.this);
		View view = layoutInflater.inflate(R.layout.alertdialog_luyin, null);
		dialog =  new MyBuilder(TaskDetailsActivity.this).create();
		dialog.setView(view, 0, 0, 0, 0);
		tv_start = (Chronometer)view.findViewById(R.id.tv_start);
		tv_end = (TextView)view.findViewById(R.id.tv_end);
		cancel = (TextView)view.findViewById(R.id.cancel);
		confirm = (TextView)view.findViewById(R.id.confirm);
		img_start = (ImageView)view.findViewById(R.id.img_start);
		img_end = (ImageView)view.findViewById(R.id.img_end);				
		luyin_start = (LinearLayout)view.findViewById(R.id.lin_start);
		luyin_end = (LinearLayout)view.findViewById(R.id.lin_end);
		
		//如果录音过程中直接退出  再次登陆初始化
				if (Common.mRecorder != null) {
					
					Common.stopLu();
				}
				tv_start.stop();
				//将计时器清零  
				tv_start.setBase(SystemClock.elapsedRealtime());				
				img_start.setImageResource(R.drawable.record_start);//图标回初始状态
				
				
				//设置sdcard的路径 ，在点击开始录音后创建路径		 		       				
				String filename = timeStampFormat.format(new Date());
				autoName = filename + ".mp3";
				autoPath = Constant.SAVE_IMG_PATH + "/" + autoName;
				
				img_start.setOnClickListener(new OnClickListener() {//点击开始录音
					public void onClick(View v) {
						if (IsRecord == false) {		
							spPool.play(music, 1, 1, 0, 0, 1);
							//将计时器清零  
							tv_start.setBase(SystemClock.elapsedRealtime());
							//开始计时
							tv_start.start();  		            
							img_start.setImageResource(R.drawable.record_start01);

							
						    Common.startLu(autoPath);
							IsRecord = true;
							
						}else {
							//完成录音
							Common.stopLu();
							luyin_start.setVisibility(View.GONE);
							luyin_end.setVisibility(View.VISIBLE);
							IsRecord = false;
																
							tv_start.stop();
							tv_end.setText(tv_start.getText().toString());
						}								
					}
				});
				img_end.setOnClickListener(new OnClickListener() {//点击播放录音
					public void onClick(View v) {
								
					}
				});
				//取消
				cancel.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				//确定
				confirm.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						/*((MainActivity)getActivity()).showDialog("正在上传文件");	
						upFile(autoPath, false);	*/
						dialog.dismiss();
						File file = new File(autoPath);
						showDialog("正在上传文件");
						String base64 = "";
						filePath = autoPath;
						try {
							base64 = Base64Util.encodeBase64File(autoPath);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							dismissDialog();
							showCustomToast("文件上传失败", false);
							return;
							
						}
						L.e("base64码 = " + base64);

						//传入参数
						UpLoadFileTask upLoadFileTask = new UpLoadFileTask(base64, "Task", 
								new File(autoPath).getName(), preferences.getInt(Content.COMPS_ID, 0) + "");
						//设置监听接口
						upLoadFileTask.setUpLoadResult(TaskDetailsActivity.this);
						
						//开启上传
						new Thread(upLoadFileTask).start();
					}
				});		

				
				//点击外面不能取消对话框
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();	
		
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dismissDialog();
			switch (msg.what) {
			case HanderUtil.case1:
				Attachment = "";
				AttachmentPath = "";
				fileList.clear();
				fileAdapter.notifyDataSetChanged();
				miaoshu.setText("");
				setListViewHeight(filelist);
				showCustomToast("提交成功", true);
				getTask(ID);
				if (Progress == 100) {
					progLayout.setVisibility(View.GONE);
					menuLayout.setVisibility(View.GONE);
					isTrue = true;
				}
				RESULT_CODE = RESULT_OK;
				setResult(RESULT_CODE);
				break;
			case HanderUtil.case2:
				showCustomToast((String) msg.obj, false);
				break;
			case HanderUtil.case3:
				File file = (File) msg.obj;
				String base64 = "";
				if (FileUtils.isFileTooBig(file.getAbsolutePath())) {
					showCustomToast("文件过大", false);
					dismissDialog();
					return;
				}
				try {
					filePath = file.getAbsolutePath();
					base64 = Base64Util.encodeBase64File(file.getAbsolutePath());
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					dismissDialog();
					showCustomToast("文件不存在", false);
					return;
				}
				//传入参数
				UpLoadImgSignText upLoadFileTask = new UpLoadImgSignText(base64, "Task",
						file.getName(), MyApplication.getInstance().getCompanyID() + "", "");
				//设置监听接口
				upLoadFileTask.setUpLoadResult(TaskDetailsActivity.this);
				
				//开启上传
				new Thread(upLoadFileTask).start();
				break;
			case HanderUtil.case4:
				dismissDialog();
				showCustomToast("上传失败", false);
				break;

			default:
				break;
			}
		};
	};
	
	/**
	 * 提交线程
	 */
	private void submitTaskProger() {
		for (Map<String, Object> map : fileList) {
			if (Attachment.equals("")) {
				Attachment = (String) map.get("filename");
				
			} else {
				Attachment = Attachment + "#" + (String) map.get("filename");  
			}
			
			if (AttachmentPath.equals("")) {
				AttachmentPath = (String) map.get("filepath");
			} else {
				AttachmentPath = AttachmentPath + "#" + (String) map.get("filepath");  
			}
		}
		ZJRequest<TaskProgerUtil> zjRequest = new ZJRequest<TaskProgerUtil>();
		TaskProgerUtil taskProgerUtil = new TaskProgerUtil();
		taskProgerUtil.setAction("Insert");
		taskProgerUtil.setID(0);
		taskProgerUtil.setTask_ID(ID);
		if (fileList.size() != 0) {
			taskProgerUtil.setAttachment(Attachment);
			taskProgerUtil.setAttachmentPath(AttachmentPath);
		}
		taskProgerUtil.setManager_ID(Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0")));
		taskProgerUtil.setNote(miaoshu.getText().toString().trim());
		taskProgerUtil.setProgressNum(Progress);
		zjRequest.setData(taskProgerUtil);
		final String json = JsonUtil.toJson(zjRequest);
		L.e("json = " + json);
		showDialog("正在提交");
		new Thread(){
			public void run() {
				String result = DataUtil.callWebService(Methods.TASK_SETTASKPROG, json);
				L.e("result = " + result);
				if (result != null) {
					Type type = new TypeToken<ZJResponse>(){}.getType();
					ZJResponse zjResponse = JsonUtil.fromJson(result, type);
					if (zjResponse.getCode() == 0) {
						mHandler.sendEmptyMessage(HanderUtil.case1);
					} else {
						Message msg = new Message();
						msg.what = HanderUtil.case2;
						msg.obj = zjResponse.getDesc();
						mHandler.sendMessage(msg);
					}
				} else {
					Message msg = new Message();
					msg.what = HanderUtil.case2;
					msg.obj = "提交失败";
					mHandler.sendMessage(msg);
				}
			};
		}.start();
	}
	
	
	
	/**
	 * seekbar拖到监听
	 */
	private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			Progress = progress;
			mBar.setProgress(progress);
			mBarTex.setText(progress + "%");
		}
	};
	
	/**
	 * 计算listview的高度
	 * @param listView
	 */
	@SuppressWarnings("static-access")
	private void setListViewHeight(ListView listView) {
		Utility.setListViewHeightBasedOnChildren(listView);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CallSystemApp.EXTRA_TAKE_PHOTOS://拍照返回
			L.d("拍照返回");
			if (resultCode ==  RESULT_OK) {
				showDialog("正在上传照片");
				/*String base64 = "";
				if (FileUtils.isFileTooBig(photoPath)) {
					showCustomToast("文件过大", false);
					dismissDialog();
					return;
				}
				try {
					filePath = photoPath;
					base64 = Base64Util.encodeBase64File(photoPath);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					dismissDialog();
					showCustomToast("文件不存在", false);
					return;
				}
				//传入参数
				UpLoadFileTask upLoadFileTask = new UpLoadFileTask(base64, "Task", 
						new File(photoPath).getName(), preferences.getInt(Content.COMPS_ID, 0) + "");
				//设置监听接口
				upLoadFileTask.setUpLoadResult(TaskDetailsActivity.this);
				
				//开启上传
				new Thread(upLoadFileTask).start();*/
				new Thread(){
					public void run() {
						String newPath = Constant.SAVE_IMG_PATH + File.separator + getSystemTime() + ".jpg";
						try {
							File file = PhotoHelp.compressImage(photoPath, newPath);
							Message msg = new Message();
							msg.obj = file;
							msg.what = HanderUtil.case3;
							mHandler.sendMessage(msg);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							mHandler.sendEmptyMessage(HanderUtil.case4);
						}
					};
				}.start();
			}
			break;
		case CallSystemApp.EXTRA_TAKE_VIDEOS://录像返回
			if (resultCode ==  RESULT_OK) {
				showDialog("正在上传录像");
				String base64 = "";
				if (FileUtils.isFileTooBig(videoPath)) {
					showCustomToast("文件过大", false);
					dismissDialog();
					return;
				}
				try {
					filePath = videoPath;
					base64 = Base64Util.encodeBase64File(videoPath);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					dismissDialog();
					showCustomToast("文件不存在", false);
					return;
				}
				//传入参数
				UpLoadFileTask upLoadFileTask = new UpLoadFileTask(base64, "Task", 
						new File(videoPath).getName(), preferences.getInt(Content.COMPS_ID, 0) + "");
				//设置监听接口
				upLoadFileTask.setUpLoadResult(TaskDetailsActivity.this);
				
				//开启上传
				new Thread(upLoadFileTask).start();
			}
			break;
		case CallSystemApp.EXTRA_TAKE_RECORDS://录音返回
			L.d("录音返回");
			if (resultCode ==  RESULT_OK) {
				showDialog("正在上传文件");
				String base64 = "";
				try {
					if (FileUtils.isFileTooBig(FileUtils.getFilePath(TaskDetailsActivity.this, data.getData()))) {
						showCustomToast("文件过大", false);
						dismissDialog();
						return;
					}
					L.e("录音文件地址为:" + data.getData().getPath());
					//base64 = Base64Util.encodeBase64File(data.getData().getPath());
					filePath = FileUtils.getFilePath(TaskDetailsActivity.this, data.getData());
					base64 = Base64Util.encodeBase64File(filePath);
					L.e("base64码 = " + base64);
					if (base64 == null) {
						showCustomToast("文件不存在", false);
						dismissDialog();
						return;
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					dismissDialog();
					showCustomToast("文件不存在", false);
					return;
				}
				//传入参数
				UpLoadFileTask upLoadFileTask = new UpLoadFileTask(base64, "Task", 
						new File(FileUtils.getFilePath(TaskDetailsActivity.this, data.getData())).getName(), preferences.getInt(Content.COMPS_ID, 0) + "");
				//设置监听接口
				upLoadFileTask.setUpLoadResult(TaskDetailsActivity.this);
				
				//开启上传
				new Thread(upLoadFileTask).start();
			}
			break;
		case CHOOSE_FILE://选择文件返回
			L.v("选择文件返回");
			if (resultCode ==  RESULT_OK) {
				showDialog("正在上传文件");
				String base64 = "";
				if (FileUtils.isFileTooBig(data.getData().getPath())) {
					showCustomToast("文件过大", false);
					dismissDialog();
					return;
				}
				for (Map<String, Object> map : fileList) {
					if (map.get("filename").equals(data.getExtras().getString("FileName"))) {
						showCustomToast("文件已上传", false);
						dismissDialog();
						return;
					}
				}
				try {
					filePath = data.getData().getPath();
					base64 = Base64Util.encodeBase64File(filePath);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					dismissDialog();
					showCustomToast("文件不存在", false);
					return;
				}
				//传入参数
				UpLoadFileTask upLoadFileTask = new UpLoadFileTask(base64, "Task", 
						data.getExtras().getString("FileName"), preferences.getInt(Content.COMPS_ID, 0) + "");
				//设置监听接口
				upLoadFileTask.setUpLoadResult(TaskDetailsActivity.this);
				
				//开启上传
				new Thread(upLoadFileTask).start();
			}
			break;

		default:
			break;
		}
		
	}

	@Override
	public void onUpLoadResult(String FileName, String FilePath, boolean isSuccess) {
		// TODO Auto-generated method stub
		dismissDialog();
		if (isSuccess) {//是否上传成功
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("filename", FileName);
			map.put("filepath", FilePath);
			map.put("file", filePath);
			L.e("文件地址：" + filePath);
			fileList.add(map);
			fileAdapter.notifyDataSetChanged();
			setListViewHeight(filelist);
			showCustomToast("附件上传成功", true);
			
		} else {
			showCustomToast("附件上传失败", false);
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		mRecorder = null;
		super.onDestroy();
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			TaskDetailsActivity.this.setResult(RESULT_CODE);
			finish();
		}
		
	};
	
	private OnTouchListener onTouchListener = new OnTouchListener(){

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			// 开始录音
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				startRecording();
				recordBtn.setText("松开上传");
				return true;
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				stopRecording();
				recordBtn.setText("按住说话");
				return false;
			}
			return false;

		}
		
	};
	
	private long startTime = 0;
	private long endTime = 0;
	private int SPACE = 100;// 间隔取样时间
	private int BASE = 600;
	
	/**
	 * 录音
	 */
	private void startRecording() {
		try {
			L.d("开始录音");
			// File file =
			// File.createTempFile(java.lang.System.currentTimeMillis()+"",
			// ".amr",
			// Constant.SAVE_AUTO_PATH);
			AutoFilePath = Constant.SAVE_AUTO_PATH + File.separator
					+ getSystemTime() + ".amr";
			File file = new File(AutoFilePath);
			if (!file.exists()) {
				L.d("创建文件" + file.getAbsolutePath());
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			// 保存到指定文件
			mRecorder.setOutputFile(file.getAbsolutePath());
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			mRecorder.prepare();

			// 开始录音
			mRecorder.start();
			startTime = getSystemTime();
			prog_mic_layout.setVisibility(View.VISIBLE);
			updateMicStatus();
		} catch (Exception e) {
			mRecorder = null;
			e.printStackTrace();
		}
	}

	private String AutoFilePath;
	/**
	 * 停止并保存录音,返回时间
	 */
	private long stopRecording() {
		L.d("停止录音");
		endTime = getSystemTime();
		long amrTime = endTime - startTime;
		if (amrTime > 1000) {
			mRecorder.stop();
			mRecorder.reset();
			mRecorder.release();
			prog_mic_layout.setVisibility(View.GONE);
			mRecorder = null;
			File file = new File(AutoFilePath);
			showDialog("正在上传文件");
			String base64 = "";
			filePath = AutoFilePath;
			try {
				base64 = Base64Util.encodeBase64File(AutoFilePath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				dismissDialog();
				showCustomToast("文件上传失败", false);
				return -1;
				
			}
			L.e("base64码 = " + base64);

			//传入参数
			UpLoadFileTask upLoadFileTask = new UpLoadFileTask(base64, "Task", 
					new File(AutoFilePath).getName(), preferences.getInt(Content.COMPS_ID, 0) + "");
			//设置监听接口
			upLoadFileTask.setUpLoadResult(TaskDetailsActivity.this);
			
			//开启上传
			new Thread(upLoadFileTask).start();
		}else {
			L.d("录音时间太短");
			prog_mic_layout.setVisibility(View.GONE);
			mRecorder.reset();
			mRecorder.release();
			mRecorder = null;
			showCustomToast("录音时间太短", false);
		}
		
		return amrTime;
	}

	/**
	 * 实时获取音量大小
	 */
	private void updateMicStatus() {
		if (mRecorder != null && prog_mic_img != null) {
			int ratio = mRecorder.getMaxAmplitude() / BASE;
			int db = 0;// 分贝
			if (ratio > 1)
				db = (int) (20 * Math.log10(ratio));
			L.d("分贝值：" + db + "     " + Math.log10(ratio));
			switch (db / 4) {
			case 0:
				prog_mic_img.setImageResource(micimg[0]);
				break;
			case 1:
				prog_mic_img.setImageResource(micimg[0]);
				break;
			case 2:
				prog_mic_img.setImageResource(micimg[1]);
				break;
			case 3:
				prog_mic_img.setImageResource(micimg[2]);
				break;
			case 4:
				prog_mic_img.setImageResource(micimg[3]);
				break;
			case 5:
				prog_mic_img.setImageResource(micimg[4]);
				break;
			}

			mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
		}
	}
	
	private Runnable mUpdateMicStatusTimer = new Runnable() {
		public void run() {
			updateMicStatus();
		}
	};
	
	
}
