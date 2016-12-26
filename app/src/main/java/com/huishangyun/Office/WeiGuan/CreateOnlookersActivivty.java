package com.huishangyun.Office.WeiGuan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.reflect.TypeToken;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Channel.Clues.ShowDialog;
import com.huishangyun.Channel.Visit.PhotoHelpDefine;
import com.huishangyun.Interface.OnDialogDown;
import com.huishangyun.Map.Location;
import com.huishangyun.Map.LocationUtil;
import com.huishangyun.Util.Base64Util;
import com.huishangyun.Util.DisplayUtil;
import com.huishangyun.Util.FaceUtil;
import com.huishangyun.Util.FileUtils;
import com.huishangyun.Util.L;
import com.huishangyun.Util.PhotoHelp;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.View.CirclePageIndicator;
import com.huishangyun.View.JazzyViewPager;
import com.huishangyun.View.MarqueeText;
import com.huishangyun.model.CallSystemApp;
import com.huishangyun.model.Constant;
import com.huishangyun.Adapter.FacePageAdeapter;
import com.huishangyun.model.Methods;
import com.huishangyun.task.UpLoadFileTask;
import com.huishangyun.task.UpLoadImgSignText;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.T;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.model.Content;
import com.huishangyun.task.UpLoadFileTask.OnUpLoadResult;
import com.huishangyun.yun.R;

/**
 * 创建围观主页
 * @author xsl
 *
 */
public class CreateOnlookersActivivty extends Activity implements OnDialogDown,OnUpLoadResult {
	private TextView cancel;//取消
	private TextView send;//发送
	private EditText mNote;//文字内容
	private LinearLayout mLayout;//动态布局
	private TextView Tittle;//标题
	private LinearLayout shareLayout;//分享条
	//分享至
	private TextView myself;//自己
	private TextView mycompany;//公司
	private TextView mydepartment;//部门
	private TextView all;//部门及以下部门
	//添加表情、拍照、主题、本地图片、录音
	private LinearLayout expression;//表情
	private LinearLayout takephoto;//拍照
	private LinearLayout sharetheme;//主题
	private LinearLayout locationpicture;//本地图片
	private LinearLayout soundrecording;//录音
	private LinearLayout mFaceRoot;//表情页
	private TextView recent;//最近
	private TextView classical;//经典
	private TextView bigexpression;//大表情
	private int mCurrentPage = 0;// 当前表情页
	private JazzyViewPager mFaceViewPager;
	private List<String> mFaceMapKeys;// 表情对应的字符串数组
	private Map<String, Integer> mFaceMap;
	private boolean mIsFaceShow = false;// 是否显示表情
	private InputMethodManager mInputMethodManager;
	private ImageView face_show_btn;//表情布局块
	private MediaRecorder mRecorder = null; //录音
	private boolean isRecording = false;//是否正在录音
	private String currentRecordPath;//当前录音文件存放路径
	private Object[] mFaceoObjects;//要添加的表情对象
	private TextView TxT;
	private AlertDialog dialog;
	private String wNote;
	private double Lng;//经度
	private double Lat;//维度
	private String Loc;//地理位置
	
	private TextView  confirm, tv_end;
	private Chronometer tv_start;
	private LinearLayout luyin_start, luyin_end;
	private ImageView  img_start, img_end;
	//存放p拍照、图片、录音文件
	public static List<OnLooksList> mList = new ArrayList<OnLooksList>();
	private String takePhotoFileName;
	private	String takePhotoPath;
	private ArrayList<HashMap<String, Object>> ItemLists = new ArrayList<HashMap<String, Object>>();
	private static final int SOURCE_DIALOG = 1;
	private webServiceHelp<OnLooksList> mServiceHelp;//发送围观数据
	private webServiceHelp<OnLooksList> webHelp;//获取主题数据
    private OnLooksList list = new OnLooksList();
    private List<OnLooksList> ThemList = new ArrayList<OnLooksList>();
    private SpannableString spanText;//主题
    private List<String> pList = new ArrayList<String>();//文件地址
    private ProgressDialog pDialog;//上传文件对话框
    private static final int UpPhoto = 0x0001;
    private static final int UpImage = 0x0002;
    private static final int UpRecord = 0x0003;
    private static final int CompressImage = 0x0004;
    private static final int UpFail = 0x0005;
    private static final int SENDSUCESS = 0x0006;
    private  int Isp; 
    private String  PicturePath;//选择图片完整图片路径
    private boolean isUpFile = false;//是否上传的是录音文件
    private int Flag = 0;//抄送对象,默认抄送给公司
    private String themeNote = "";
    private LinearLayout faceClass;
    private MediaPlayer player = new MediaPlayer();;
    private boolean isPause = false;
    private LinearLayout locationLayout;//定位条
    private ImageView locationImg;//定位图标
    private MarqueeText locationAddress;//定位地址
    private ImageView cancelImg;//取消图标
    private boolean isLocation = false;
    private long TimeTotal;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_office_onlooks_create);
		mList.clear();
		pList.clear();
		init();
		initFacePage();
		//定位
		LocationUtil.startLocation(CreateOnlookersActivivty.this, mLocationListener);
	}

	
	/**
	 * 初始化
	 */
	@SuppressLint("NewApi")
	private void init(){
		webHelp = new webServiceHelp<OnLooksList>(Methods.GET_ONLOOKS_THEME, new TypeToken<ZJResponse<OnLooksList>>(){}.getType());
		webHelp.setOnServiceCallBack(onServiceCallBackTheme);
		webHelp.start(getThemeJson());
		mServiceHelp = new webServiceHelp<OnLooksList>(Methods.SET_ONLOOKS_CREATE, new TypeToken<ZJResponse>(){}.getType());
		mServiceHelp.setOnServiceCallBack(onServiceCallBack);
		mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		Intent intent = getIntent();
		Tittle = (TextView) findViewById(R.id.Tittle);
		Tittle.setText(intent.getStringExtra("Tittle"));
		shareLayout = (LinearLayout) findViewById(R.id.shareLayout);
		mNote = (EditText) findViewById(R.id.mNote);
		if (intent.getStringExtra("Tittle").equals("发评论")) {
			shareLayout.setVisibility(View.GONE);
			mNote.setHint("写评论...");
		}else {
			shareLayout.setVisibility(View.VISIBLE);
			mNote.setHint("分享新鲜事...");
		}
		
		//软键盘删除键监听
		mNote.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                	if (!mNote.getText().toString().contains("[主题]")) {
                		themeNote = "";
					}
                	
                	L.e("themeNote:" + themeNote);
                }
                return false;
            }
        });
		
	

		faceClass = (LinearLayout) findViewById(R.id.faceClass);
		faceClass.setVisibility(View.GONE);
		cancel = (TextView) findViewById(R.id.cancel);
		send = (TextView) findViewById(R.id.send);
		locationLayout = (LinearLayout) findViewById(R.id.locationLayout);
		locationImg = (ImageView) findViewById(R.id.locationImg);
		locationAddress = (MarqueeText) findViewById(R.id.locationAddress);
		cancelImg = (ImageView) findViewById(R.id.cancelImg);
		cancelImg.setVisibility(View.GONE);
		
	
		myself = (TextView) findViewById(R.id.myself);
		mycompany = (TextView) findViewById(R.id.mycompany);
		mydepartment = (TextView) findViewById(R.id.mydepartment);
		all = (TextView) findViewById(R.id.all);
		TxT = (TextView) findViewById(R.id.TxT);
		mLayout = (LinearLayout) findViewById(R.id.mLayout);
		
		expression = (LinearLayout) findViewById(R.id.expression);
		takephoto = (LinearLayout) findViewById(R.id.takephoto);
		sharetheme = (LinearLayout) findViewById(R.id.sharetheme);
		locationpicture = (LinearLayout) findViewById(R.id.locationpicture);
		soundrecording = (LinearLayout) findViewById(R.id.soundrecording);
		recent = (TextView) findViewById(R.id.recent);
		classical = (TextView) findViewById(R.id.classical);
		bigexpression = (TextView) findViewById(R.id.bigexpression);
		face_show_btn = (ImageView) findViewById(R.id.face_show_btn);
		
		mFaceRoot = (LinearLayout) findViewById(R.id.face_ll);
		mFaceRoot.setVisibility(View.GONE);
		mFaceViewPager = (JazzyViewPager) findViewById(R.id.face_pager);
		Set<String> keySet = MyApplication.getInstance().getFaceMap().keySet();
		mFaceMapKeys = new ArrayList<String>();
		mFaceMapKeys.addAll(keySet);
		mFaceMap = MyApplication.getInstance().getFaceMap();
		mFaceoObjects =  MyApplication.getInstance().getFaceMap().values().toArray();

		mycompany.setText(MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_COMPANY_NAME, "公司"));
		//如果部门为顶级部门则不添加部门
		if (!MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_COMPANY_NAME, "公司")
				.equals(MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_DEPARTMENT_NAME, "部门"))) {
			mydepartment.setText(MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_DEPARTMENT_NAME, "部门"));
			all.setText(MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_DEPARTMENT_NAME, "部门") + "及下级部门");
		}

	
		cancel.setOnClickListener(onClick);
		send.setOnClickListener(onClick);
		myself.setOnClickListener(onClick);
		mycompany.setOnClickListener(onClick);
		mydepartment.setOnClickListener(onClick);
		all.setOnClickListener(onClick);
		
		expression.setOnClickListener(onClick);
		takephoto.setOnClickListener(onClick);
		sharetheme.setOnClickListener(onClick);
		locationpicture.setOnClickListener(onClick);
		
		soundrecording.setOnClickListener(onClick);
		recent.setOnClickListener(onClick);
		classical.setOnClickListener(onClick);
		bigexpression.setOnClickListener(onClick);
		TxT.setOnClickListener(onClick);
		mNote.setOnClickListener(onClick);
		locationLayout.setOnClickListener(onClick);
		showSoftInput(true);
		//判断是否有话题
		if (getIntent().getStringExtra("Topic") != null) {
			L.e("Topic = " + getIntent().getStringExtra("Topic"));
			wNote = reW(mNote.getText().toString());
			mNote.setText("");
			Bitmap bm = TxtChangBitmap.writeImage(this, getIntent().getStringExtra("Topic").trim(), 17);
			 //  根据Bitmap对象创建ImageSpan对象
	        ImageSpan imageSpan = new ImageSpan(this, bm);
	         //  创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
	        SpannableString spannableString = new SpannableString("[主题]");
	         //  用ImageSpan对象替换face
	        spannableString.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	         //  将随机获得的图像追加到EditText控件的最后
	        mNote.append(spannableString);
	        themeNote = getIntent().getStringExtra("Topic").trim();
			mNote.append(FaceUtil.convertNormalStringToSpannableString(this, wNote, false));
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mServiceHelp.removeOnServiceCallBack();
		webHelp.removeOnServiceCallBack();
		LocationUtil.stopLocation();
		LocationUtil.stopReverseGeoCode();
		super.onDestroy();
	}
	
	/**
	 * 获取围观主题接口回调
	 */
	private webServiceHelp.OnServiceCallBack<OnLooksList> onServiceCallBackTheme = new webServiceHelp.OnServiceCallBack<OnLooksList>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<OnLooksList> zjResponse) {
			// TODO Auto-generated method stub
			if (haveCallBack && zjResponse != null) {
				switch (zjResponse.getCode()) {
				case 0:
					//访问成功
					ThemList  = zjResponse.getResults();
					ItemLists.clear();
					setItemDate();
					break;
				default:
					//错误码
					
					break;
				}
			} else {
				//接口访问失败
				L.e("接口访问失败，请检查网络！");
			}
		}
	};
	
	/**
	 * 获取主题json
	 * @return
	 */
	private String getThemeJson() {
		ZJRequest<T> zjRequest = new ZJRequest<T>();
		zjRequest.setCompany_ID(MyApplication.getInstance().getCompanyID());
		return JsonUtil.toJson(zjRequest);

	}
	
	/**
	 * 发送围观接口回调
	 */
	private webServiceHelp.OnServiceCallBack<OnLooksList> onServiceCallBack = new webServiceHelp.OnServiceCallBack<OnLooksList>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<OnLooksList> zjResponse) {
			// TODO Auto-generated method stub
			if (haveCallBack && zjResponse != null) {
				switch (zjResponse.getCode()) {
				case 0:
					//访问成功
					mHandler.sendEmptyMessage(SENDSUCESS);
					break;

				default:
					//错误码
					showToast(false, "围观发表失败！");
					break;
				}
			} else {
				//接口访问失败
				showToast(false, "接口访问失败！");
			}
		}
	};
	
	/**
	 * json对象
	 * @param Topic 主题
	 * @param Note 内容
	 * @param Attachment 原附件名#分隔
	 * @param AttachmentPath 附件Img接口返回文件名#分隔
	 * @param Flag 抄送范围(0:全公司,1:仅自己,2:所在部门,3:组织架构层级)
	 * @param Lng 定位经度
	 * @param Lat 定位纬度
	 * @param loc 定位地址
	 * @return 
	 */
	private String getJson(String Topic, String Note, String Attachment,
			String AttachmentPath, int Flag, double Lng, double Lat,
			String loc) {
		list.setManager_ID(MyApplication.getInstance().getManagerID());
		list.setTopic(Topic);
		list.setNote(Note);
		list.setAttachment(Attachment);
		list.setAttachmentPath(AttachmentPath);
		list.setFlag(Flag);
		list.setLng(Lng);
		list.setLat(Lat);
		list.setLoc(loc);
		list.setShowGps(isLocation);
		ZJRequest<OnLooksList> zjRequest = new ZJRequest<OnLooksList>();
		zjRequest.setData(list);
		return JsonUtil.toJson(zjRequest);

	}
	
	
	
	/**
	 * 动态加载控件
	 */
	private void addLayout(){
		mLayout.removeAllViews();
		for (int i = 0; i < mList.size(); i++) {
	    L.e("次数：" + i);
		LayoutInflater inflater = LayoutInflater.from(this);
		final View view = inflater.inflate(R.layout.activity_office_onlookers_create_item, null);
		LinearLayout itemLayout = (LinearLayout) view.findViewById(R.id.itemLayout);
		ImageView picture = (ImageView) view.findViewById(R.id.picture);
		TextView fileName = (TextView) view.findViewById(R.id.fileName);
		final ImageView delete = (ImageView) view.findViewById(R.id.delete);
		delete.setId(i);
		View topLine = (View) view.findViewById(R.id.topLine);
		if (i==0) {
			topLine.setVisibility(View.VISIBLE);
		}else {
			topLine.setVisibility(View.GONE);
		}
		Bitmap bm = null;
		if (mList.get(i).getIsWho()==1) {
			fileName.setText(mList.get(i).getTakePhotoName());
			try {//拍照
			   bm = getBitmap(mList.get(i).getTakePhotoAbsolutePath(), 80, 80);
			   picture.setImageBitmap(bm);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
		}else if (mList.get(i).getIsWho()==2) {
			fileName.setText(mList.get(i).getPictureName());
			try {//图片
			  bm = getBitmap(mList.get(i).getPictureAbsolutePath(), 80, 80);
			  picture.setImageBitmap(bm);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		
		}else if (mList.get(i).getIsWho()==3) {//录音
			fileName.setText(mList.get(i).getRecordName());
			picture.setBackgroundResource(R.drawable.service_recordfile);
			
		}
		if ( i == 0) {
			topLine.setVisibility(View.VISIBLE);
		}else {
			topLine.setVisibility(View.GONE);
		}
		mLayout.addView(view);
        delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mLayout.removeViewAt(delete.getId());
				mList.remove(delete.getId());
				pList.remove(delete.getId());
			}
		 });
		}
		
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case CompressImage:
				pDialog = ProgressDialog.show(CreateOnlookersActivivty.this, "请等待...", "正在上传图片...",true); 	
			    pDialog.setCancelable(true);
			    pDialog.show();
				//对图片进行压缩
            	new Thread(){
            		public void run() {
            			File file;
                    	String newPath =  Constant.SAVE_IMG_PATH + File.separator + System.currentTimeMillis() + ".jpg";
                    	try {
                    		if (Isp == 0x0001) {
                    			file = PhotoHelpDefine.compressImage(takePhotoPath, newPath, MyApplication.photoWidth, MyApplication.photoHeigh);
//                    			file = PhotoHelp.compressImage(takePhotoPath, newPath);
            					Message msg = new Message();
            					msg.obj = file;
            					msg.what = UpPhoto;
            					mHandler.sendMessage(msg);
							}else if (Isp == 0x0002) {
								L.e("PicturePath:" + PicturePath);
								file = PhotoHelp.compressImage(PicturePath, newPath);
            					Message msg = new Message();
            					msg.obj = file;
            					msg.what = UpImage;
            					mHandler.sendMessage(msg);
							}
        					
        				} catch (Exception e) {
        					// TODO Auto-generated catch block
        					mHandler.sendEmptyMessage(UpFail);
        					e.printStackTrace();
        					return;
        				}
            		};
            	}.start();
				break;
			case UpPhoto:
				File file = (File) msg.obj;
				upLoadDataToNet(file.getAbsolutePath(),true);
				break;	
			case UpImage:
				File file1 = (File) msg.obj;
				upLoadDataToNet(file1.getAbsolutePath(),true);
				break;
			case UpRecord:
				pDialog = ProgressDialog.show(CreateOnlookersActivivty.this, "请等待...", "正在上传录音...",true); 	
			    pDialog.setCancelable(true);
			    pDialog.show();
				upLoadDataToNet(currentRecordPath,false);
				break;
			case UpFail:
               //图片上传失败
				L.e("图片上传失败！");
				break;
			case SENDSUCESS:
				MyApplication.getInstance().showDialog(CreateOnlookersActivivty.this, false, "正在发送...");
				sharetheme.setClickable(true);
				showToast(true, "围观发表成功！");
				CreateOnlookersActivivty.this.setResult(0x1111);
				showSoftInput(false);
				CreateOnlookersActivivty.this.finish();
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * 单击事件
	 */
	private OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.cancel://取消
				showSoftInput(false);
				finish();
				break;
			case R.id.send://发送
				String Attachment = "";
				String AttachmentPath = "";
				StringBuffer strPicture = new StringBuffer("");
				StringBuffer strRecord = new StringBuffer("");
				for (int i = 0; i < pList.size(); i++) {
					if (isImage(pList.get(i))) {
						if ((strPicture.toString().trim()).length()>0) {
							strPicture.append("#");
							strPicture.append(pList.get(i));
						}else {
							strPicture.append(pList.get(i));
						}
						AttachmentPath = strPicture.toString().trim();
					}else {
						if ((strRecord.toString().trim()).length()>0) {
							strRecord.append("#");
							strRecord.append(pList.get(i));
						}else {
							strRecord.append(pList.get(i));
						}
						Attachment = strRecord.toString().trim();
					} 
				}
				if (themeNote.length()>0) {
					String json  = getJson(themeNote, reW(mNote.getText().toString()), Attachment, AttachmentPath, Flag, Lng, Lat, Loc);
					L.e("json对象：" + json);
					mServiceHelp.start(json);
				}else {
					String json  = getJson("", mNote.getText().toString(), Attachment, AttachmentPath, Flag, Lng, Lat, Loc);
					L.e("json对象：" + json);
					mServiceHelp.start(json);
				}
				break;
			
			case R.id.myself://自己
				myself.setTextColor(Color.parseColor("#21a5de"));
				mycompany.setTextColor(Color.parseColor("#969696"));
				mydepartment.setTextColor(Color.parseColor("#969696"));
				all.setTextColor(Color.parseColor("#969696"));
				Flag = 1;
				break;
			case R.id.mycompany://公司
				myself.setTextColor(Color.parseColor("#969696"));
				mycompany.setTextColor(Color.parseColor("#21a5de"));
				mydepartment.setTextColor(Color.parseColor("#969696"));
				all.setTextColor(Color.parseColor("#969696"));
				Flag = 0;
				break;
			case R.id.mydepartment://部门
				myself.setTextColor(Color.parseColor("#969696"));
				mycompany.setTextColor(Color.parseColor("#969696"));
				mydepartment.setTextColor(Color.parseColor("#21a5de"));
				all.setTextColor(Color.parseColor("#969696"));
				Flag = 2;
				break;
			case R.id.all://部门及部门以下
				myself.setTextColor(Color.parseColor("#969696"));
				mycompany.setTextColor(Color.parseColor("#969696"));
				mydepartment.setTextColor(Color.parseColor("#969696"));
				all.setTextColor(Color.parseColor("#21a5de"));
				Flag = 3;
				break;
			case R.id.expression://表情
				if (!mIsFaceShow) {
					showSoftInput(false);
//					mInputMethodManager.hideSoftInputFromWindow(
//							mNote.getWindowToken(), 0);
					try {
						Thread.sleep(80);// 解决此时会黑一下屏幕的问题
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					face_show_btn.setImageResource(R.drawable.onlooks_keyboard);
					mFaceRoot.setVisibility(View.VISIBLE);
					mIsFaceShow = true;
//					//将菜单缩下去
//					addBtn.setImageResource(R.drawable.chat_iconmore);
//					menuLayout.setVisibility(View.GONE);
//					mIsMenuShow = false;
//					
//					//将录音换成输入框
//					mChatEditText.setVisibility(View.VISIBLE);
//					et_chat_video.setVisibility(View.GONE);
//					mIsVideShow = false;
//					face_video.setImageResource(R.drawable.chat_iconvoice);
					
				} else {
					mFaceRoot.setVisibility(View.GONE);
					face_show_btn
							.setImageResource(R.drawable.onlooks_expression);
					showSoftInput(true);
					mIsFaceShow = false;
				}
				break;
			case R.id.takephoto://拍照
			    takePhotoFileName = System.currentTimeMillis() + ".jpg";
				takePhotoPath = Constant.SAVE_IMG_PATH + File.separator
						+ takePhotoFileName;
				CallSystemApp.callCamera(CreateOnlookersActivivty.this, takePhotoPath);
				showSoftInput(false);
				break;
			case R.id.sharetheme://主题
				sharetheme.setClickable(true);
				new ShowDialog(CreateOnlookersActivivty.this, SOURCE_DIALOG, ItemLists, "选择主题", CreateOnlookersActivivty.this,"请选择主题！",null).customDialog();
				break;
			case R.id.locationpicture://本地图片
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, Content.REUQEST_CODE_OK_TAKEIMAGE);
				showSoftInput(false);
				break;
			case R.id.soundrecording:
				initAlertDialog();
				break;
			case R.id.recent://最近
				classical.setBackgroundColor(Color.parseColor("#ffffff"));
				bigexpression.setBackgroundColor(Color.parseColor("#ffffff"));
				recent.setBackgroundColor(Color.parseColor("#f1f1f1"));
				mFaceRoot.setVisibility(View.VISIBLE);
				break;
			case R.id.classical://经典
				classical.setBackgroundColor(Color.parseColor("#f1f1f1"));
				bigexpression.setBackgroundColor(Color.parseColor("#ffffff"));
				recent.setBackgroundColor(Color.parseColor("#ffffff"));	
				mFaceRoot.setVisibility(View.VISIBLE);
				mFaceMapKeys.clear();
				mFaceoObjects =  MyApplication.getInstance().getFaceMap().values().toArray();
				mFaceMap = MyApplication.getInstance().getFaceMap();
				Set<String> keySet = MyApplication.getInstance().getFaceMap().keySet();
				mFaceMapKeys = new ArrayList<String>();
				mFaceMapKeys.addAll(keySet);
				initFacePage();
				break;
			case R.id.bigexpression://大表情
				classical.setBackgroundColor(Color.parseColor("#ffffff"));
				bigexpression.setBackgroundColor(Color.parseColor("#f1f1f1"));
				recent.setBackgroundColor(Color.parseColor("#ffffff"));
				mFaceRoot.setVisibility(View.VISIBLE);
				mFaceMapKeys.clear();
				mFaceoObjects =  MyApplication.getInstance().getBigFaceMap().values().toArray();
				mFaceMap = MyApplication.getInstance().getBigFaceMap();
				Set<String> keySet1 = MyApplication.getInstance().getBigFaceMap().keySet();
				mFaceMapKeys = new ArrayList<String>();
				mFaceMapKeys.addAll(keySet1);
				initFacePage();
				break;
			case R.id.TxT:
				showSoftInput(true);
				break;
			case R.id.mNote://编辑框
				showSoftInput(true);
				break;
			case R.id.locationLayout://定位条
				if (!isLocation) {
					locationImg.setBackgroundResource(R.drawable.location);
					locationAddress.setTextColor(Color.parseColor("#21a5de"));
					locationAddress.setText(Loc);
					cancelImg.setVisibility(View.VISIBLE);
					isLocation = true;
				}else {
					locationImg.setBackgroundResource(R.drawable.unlocation);
					locationAddress.setTextColor(Color.parseColor("#969696"));
					locationAddress.setText("定位");
					cancelImg.setVisibility(View.GONE);
					isLocation = false;
				}
				break;
			default:
				break;
			}
		}
	};
	
	
	

    /**
     * 根据后缀名判断是否是图片文件
     * 
     * @param type
     * @return 是否是图片结果true or false
     */
    public static boolean isImage(String fileName) {
    	 String type = getFileType(fileName);
            if (type != null
                            && (type.equals("jpg") || type.equals("gif")
                                            || type.equals("png") || type.equals("jpeg")
                                            || type.equals("bmp") || type.equals("wbmp")
                                            || type.equals("ico") || type.equals("jpe"))) {
                    return true;
            }
            return false;
    }
    
    /**
     * 获取文件后缀名
     * 
     * @param fileName
     * @return 文件后缀名
    */
    public static String getFileType(String fileName) {
            if (fileName != null) {
                    int typeIndex = fileName.lastIndexOf(".");
                    if (typeIndex != -1) {
                            String fileType = fileName.substring(typeIndex + 1)
                                            .toLowerCase();
                            return fileType;
                    }
            }
            return "";
    }
    
	
	/**
	 * 初始化表情
	 */
	private void initFacePage() {
		List<View> lv = new ArrayList<View>();
		//初始化的时候页码清0；
		mCurrentPage = 0;
		//每页20个表情
		int pagerIndex = mFaceMap.size()/20 + 1;
		for (int i = 0; i < pagerIndex; i++) {
			lv.add(getGridView(i));
		}
		FacePageAdeapter adapter = new FacePageAdeapter(lv, mFaceViewPager);
		mFaceViewPager.setAdapter(adapter);
		mFaceViewPager.setCurrentItem(mCurrentPage);
		CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(mFaceViewPager);
		adapter.notifyDataSetChanged();
		indicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int i) {
				mCurrentPage = i;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// do nothing
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// do nothing
			}
		});
	}
	
	/**
	 * 返回表情页
	 * 
	 * @param i
	 * @return
	 */
	private GridView getGridView(int i) {
		GridView gv = new GridView(this);
		gv.setNumColumns(7);
		gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// 屏蔽GridView默认点击效果
		gv.setBackgroundColor(Color.TRANSPARENT);
		gv.setCacheColorHint(Color.TRANSPARENT);
		gv.setHorizontalSpacing(10);
		gv.setVerticalSpacing(1);
		gv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		gv.setGravity(Gravity.CENTER);
		gv.setAdapter(new WeiGuanFaceAdapter(this, i,mFaceMap));
		gv.setOnTouchListener(forbidenScroll());
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == MyApplication.NUM) {// 删除键的位置
					int selection = mNote.getSelectionStart();
					String text = mNote.getText().toString();
					if (selection > 0) {
						String text2 = text.substring(selection - 1);
						if ("]".equals(text2)) {
							int start = text.lastIndexOf("[");
							int end = selection;
							mNote.getText().delete(start, end);
							if (!mNote.getText().toString().contains("[主题]")) {
		                		themeNote = "";
							}
							L.e("themeNote:" + themeNote);
							return;
						}

						mNote.getText().delete(selection - 1, selection);

					}
				} else {
					int count = mCurrentPage * MyApplication.NUM + arg2;
					// 注释的部分，在EditText中显示字符串
					// String ori = msgEt.getText().toString();
					// int index = msgEt.getSelectionStart();
					// StringBuilder stringBuilder = new StringBuilder(ori);
					// stringBuilder.insert(index, keys.get(count));
					// msgEt.setText(stringBuilder.toString());
					// msgEt.setSelection(index + keys.get(count).length());

					// 下面这部分，在EditText中显示表情
					Bitmap bitmap = BitmapFactory.decodeResource(
							getResources(), (Integer)mFaceoObjects[count]);
					if (bitmap != null) {
						int rawHeigh = bitmap.getHeight();
						int rawWidth = bitmap.getHeight();
						int newHeight = DisplayUtil.sp2px(CreateOnlookersActivivty.this, 20);
						int newWidth = DisplayUtil.sp2px(CreateOnlookersActivivty.this, 20);
						// 计算缩放因子
						float heightScale = ((float) newHeight) / rawHeigh;
						float widthScale = ((float) newWidth) / rawWidth;
						// 新建立矩阵
						Matrix matrix = new Matrix();
						matrix.postScale(heightScale, widthScale);
						// 设置图片的旋转角度
						// matrix.postRotate(-30);
						// 设置图片的倾斜
						// matrix.postSkew(0.1f, 0.1f);
						// 将图片大小压缩
						// 压缩后图片的宽和高以及kB大小均会变化
						Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
								rawWidth, rawHeigh, matrix, true);
						ImageSpan imageSpan = new ImageSpan(CreateOnlookersActivivty.this,
								newBitmap);
						String emojiStr = mFaceMapKeys.get(count);
						SpannableString spannableString = new SpannableString(
								emojiStr);
						spannableString.setSpan(imageSpan,
								emojiStr.indexOf('['),
								emojiStr.indexOf(']') + 1,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						mNote.append(spannableString);
					} else {
						String ori = mNote.getText().toString();
						int index = mNote.getSelectionStart();
						StringBuilder stringBuilder = new StringBuilder(ori);
						stringBuilder.insert(index, mFaceMapKeys.get(count));
						mNote.setText(stringBuilder.toString());
						mNote.setSelection(index
								+ mFaceMapKeys.get(count).length());
					}
				}
			}
		});
		return gv;
	}

	  // 防止乱pageview乱滚动
		private OnTouchListener forbidenScroll() {
			return new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						return true;
					}
					return false;
				}
			};
		}
		
		/**
		 * 开始录音
		 */
		private void startRecording(){
			if (!isRecording) {
			//设置sdcard的路径  
	       String FileName = Environment.getExternalStorageDirectory().getAbsolutePath(); 
	       File file = new File(FileName+"/HSY_Yun/auto/" + System.currentTimeMillis()+".amr");
	       //不存在则创建
	       if (!file.getParentFile().exists()) {
	    	   file.getParentFile().mkdirs();
		      }
			 mRecorder = new MediaRecorder();  
             mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  
             mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);  
             mRecorder.setOutputFile(file.getAbsolutePath());  
             mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); 
             
             try {  
                 mRecorder.prepare(); 
                 mRecorder.start(); 
                 currentRecordPath = file.getAbsolutePath();
                 isRecording = true;
             } catch (IOException e) {
                 L.e("录音启动失败,请检查录音权限是否开启"); 
                 isRecording = false;
                 currentRecordPath = null;
             }  
            
			}else {
				stopRecording();
			}
		}
		
		/**
		 * 停止录音
		 */
		private void stopRecording(){
			if (mRecorder!=null) {
				mRecorder.stop();  
	            mRecorder.release();  
	            mRecorder = null; 
	            isRecording = false;
			}
			
		}
		
		
		
		/**
		 * 录音弹出框
		 */
		private void initAlertDialog(){
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			View view = layoutInflater.inflate(R.layout.activity_office_onlooks_recording, null);
			dialog = new AlertDialog.Builder(this).create();
			dialog.setView(view, 0, 0, 0, 0);					
			
			tv_start = (Chronometer)view.findViewById(R.id.tv_start);
			tv_end = (TextView)view.findViewById(R.id.tv_end);
			cancel = (TextView)view.findViewById(R.id.cancel);
			confirm = (TextView)view.findViewById(R.id.confirm);
			img_start = (ImageView)view.findViewById(R.id.img_start);
			img_end = (ImageView)view.findViewById(R.id.img_end);				
			luyin_start = (LinearLayout)view.findViewById(R.id.lin_start);
			luyin_end = (LinearLayout)view.findViewById(R.id.lin_end);
			
			img_start.setOnClickListener(new OnClickListener() {//点击开始录音
				public void onClick(View v) {
					if (isRecording == false) {		
						//将计时器清零  
						tv_start.setBase(SystemClock.elapsedRealtime());
						//开始计时
						tv_start.start();  		            
						img_start.setImageResource(R.drawable.record_start01);
						//开始录音
						startRecording();
					}else {
						//完成录音
						stopRecording();//停止录音
						luyin_start.setVisibility(View.GONE);
						luyin_end.setVisibility(View.VISIBLE);						
						tv_start.stop();
						tv_end.setText(tv_start.getText().toString());
					}								
				}
			});
			img_end.setOnClickListener(new OnClickListener() {//点击播放录音
				public void onClick(View v) {
//					boolean isPlaying = false;	
//					try {
//						isPlaying = player.isPlaying();
//						if (isPlaying) {
//							// 假如在播放再次点击就是暂停
//							player.pause();
//							// 停止计时
//							tv_start.stop();
//							img_end.setImageResource(R.drawable.record_end);
//						} else {
//							player.start();
//							tv_start.start();
//							img_end.setImageResource(R.drawable.record_play);
//
//						}
//						
//					} catch (Exception e) {
//						// TODO: handle exception
//						player = new MediaPlayer();
//						// 将计时器清零
//						tv_start.setBase(SystemClock.elapsedRealtime());
//						// 复位计时
//						tv_start.start();
//						img_end
//								.setImageResource(R.drawable.record_play);
//						playRecordingMusic(Uri.parse(currentRecordPath));
//
//						// 计时器监听
//						tv_start.setOnChronometerTickListener(new OnChronometerTickListener() {
//
//							@Override
//							public void onChronometerTick(
//									Chronometer chronometer) {
//								// TODO Auto-generated method stub
//								if (TimeTotal < 1000) {// 假如计时到0停止计时
//									tv_start.setText("00:00");
//									tv_start.stop();
//									img_end.setImageResource(R.drawable.record_end);
//								} else {
//									// 1s跳动一次1000毫秒等于一秒
//									TimeTotal = TimeTotal - 1000;
//									Date date = new Date(TimeTotal);
//									SimpleDateFormat format = new SimpleDateFormat(
//											"mm:ss");
//									String str = format.format(date);
//									tv_start.setText(str);
//								}
//							}
//						});
//					}
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
					if (currentRecordPath != null) {
						mHandler.sendEmptyMessage(UpRecord);
					}
					dialog.dismiss();			
				}
			});		

			//点击外面不能取消对话框
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();	
		}
		
		
		/**
		 * 指定音频播放
		 * @param pickUri
		 */
		private  void playRecordingMusic(Uri uri){
			try {
				player.setDataSource(CreateOnlookersActivivty.this, uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
			final AudioManager audioManager = (AudioManager) CreateOnlookersActivivty.this
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
		
		@SuppressWarnings("deprecation")
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			if (resultCode == RESULT_OK) {
				switch (requestCode) {
				case CallSystemApp.EXTRA_TAKE_PHOTOS://拍照返回
					
					 mHandler.sendEmptyMessage(CompressImage);
					 Isp = 0x0001;
					break;
				case Content.REUQEST_CODE_OK_TAKEIMAGE:
					L.d("选图返回");
                     if (null == data) {
                             return;
                     }
                     Uri originalUri = data.getData();
          
					Uri mFileUri;
					try {
					
                             String[] proj = { MediaStore.Images.Media.DATA };
                             // 好像是android多媒体数据库的封装接口，具体的看Android文档
                             Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                             // 按我个人理解 这个是获得用户选择的图片的索引值
                             int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                             // 将光标移至开头 
                             cursor.moveToFirst();
                             // 最后根据索引值获取图片路径
                              PicturePath = cursor.getString(column_index);
                             if (PicturePath==null) {
                            	 PicturePath = FileUtils.getPath(CreateOnlookersActivivty.this, originalUri);
							}
                             mHandler.sendEmptyMessage(CompressImage);
                            mFileUri = Uri.parse(PicturePath);
                     } catch (Exception e) {
                             mFileUri = originalUri;
                     }
					Isp = 0x0002;
					break;
				default:
					break;
				}
			}
			
			super.onActivityResult(requestCode, resultCode, data);
		}
		
		/**
		 * 从sd卡获得图片进行处理
		 * 
		 * @param imageName
		 *            图片文件名 path 文件sd卡完整路径
		 * @param reqWith
		 *            要求处理后的宽
		 * @param reqHeight
		 *            高
		 * @return
		 */
		private  Bitmap getBitmap(String path, int reqWith, int reqHeight) {
			// 判断sd卡是否挂载状态
			String sdState = Environment.getExternalStorageState();
			// 读取sd卡根目录
			// String path = Environment.getExternalStorageDirectory().toString();
			Bitmap bitmap = null;
			File imagePic;
			// 假如sd可读写
			if (sdState.equals(Environment.MEDIA_MOUNTED)) {
				imagePic = new File(path);
				if (imagePic.exists()) {
					try {

						// 对位图进行解码的参数设置
						BitmapFactory.Options options = new BitmapFactory.Options();
						// 在对位图进行解码的过程中，避免申请内存空间
						options.inJustDecodeBounds = true;
						BitmapFactory.decodeStream(new FileInputStream(imagePic),
								null, options);
						// 对图片进行一定比例的压缩处理
						options.inSampleSize = computeSampleSize(options, -1, reqWith*reqHeight);
						options.inJustDecodeBounds = false;// 真正输出位图

						bitmap = BitmapFactory.decodeStream(new FileInputStream(
								imagePic), null, options);

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			return bitmap;
		}
		
		
		 /**
	     * 动态计算options
	     * @param options
	     * @param minSideLength
	     * @param maxNumOfPixels
	     * @return
	     */
	    public int computeSampleSize(BitmapFactory.Options options,
	            int minSideLength, int maxNumOfPixels) {
	        int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels);
	        int roundedSize;
	        if (initialSize <= 8 ) {
	            roundedSize = 1;
	            while (roundedSize < initialSize) {
	                roundedSize <<= 1;
	            }
	        } else {
	            roundedSize = (initialSize + 7) / 8 * 8;
	        }

	        return roundedSize;
	    }

	    private int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
	        double w = options.outWidth;
	        double h = options.outHeight;

	        int lowerBound = (maxNumOfPixels == -1) ? 1 :
	                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
	        int upperBound = (minSideLength == -1) ? 128 :
	                (int) Math.min(Math.floor(w / minSideLength),
	                Math.floor(h / minSideLength));

	        if (upperBound < lowerBound) {
	            // return the larger one when there is no overlapping zone.
	            return lowerBound;
	        }

	        if ((maxNumOfPixels == -1) &&
	                (minSideLength == -1)) {
	            return 1;
	        } else if (minSideLength == -1) {
	            return lowerBound;
	        } else {
	            return upperBound;
	        }
	    }
		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			showSoftInput(true);
			super.onResume();
		}

		/**
		 * 初始化列表数据
		 */
		private void setItemDate() {
			// TODO Auto-generated method stub
			for (int j = 0; j < ThemList.size(); j++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("name", ThemList.get(j).getTitle());
				ItemLists.add(map);
			}
		}
		
		
		@Override
		public void onDialogDown(int position, int type) {
			// TODO Auto-generated method stub
			switch (type) {
			case SOURCE_DIALOG:
				HashMap<String, Object> map = ItemLists.get(position);
				wNote = reW(mNote.getText().toString());
				mNote.setText("");
				Bitmap bm = TxtChangBitmap.writeImage(this, (String)map.get("name").toString().trim(), 17);
				 //  根据Bitmap对象创建ImageSpan对象
		        ImageSpan imageSpan = new ImageSpan(this, bm);
		         //  创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
		        SpannableString spannableString = new SpannableString("[主题]");
		         //  用ImageSpan对象替换face
		        spannableString.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		         //  将随机获得的图像追加到EditText控件的最后
		        mNote.append(spannableString);
		        themeNote = (String)map.get("name").toString().trim();
				mNote.append(FaceUtil.convertNormalStringToSpannableString(this, wNote, false));
				break;

			default:
				break;
			}
		}
		
		/**
		 * 去除文字中的主题
		 * @return
		 */
		private String reW(String str){
			String[] text;
			if (str.contains("[主题]")) {
				text = str.split("[主题]");
				String[] S = text[2].split("]",2);
				for (int i = 0; i < S.length; i++) {
					L.e("内容：" + S[i] + i);
				}
				return S[1];
			}else {
				return str;
			}
		}
		
		/**
		 * 定位监听
		 */
		private BDLocationListener mLocationListener = new BDLocationListener() {
			
			@Override
			public void onReceivePoi(BDLocation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
			     Lng =  (double) location.getLatitude();//经度
				 Lat = (double) location.getLongitude();//维度
				 L.e("Lng: " + Lng);
				 L.e("Lat: " + Lat);
				 LocationUtil.stopLocation();
				LocationUtil.startReverseGeoCode(Lng, Lat, getGeoCoderResultListener);
			}
		};
		
		OnGetGeoCoderResultListener getGeoCoderResultListener = new OnGetGeoCoderResultListener() {
			
			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
				// TODO Auto-generated method stub
				if (result == null) {
					return;
				}
				Loc = result.getAddress();
				if (Loc.equals("null")) {
					Loc = "";
				}
				LocationUtil.stopReverseGeoCode();
			}
			
			@Override
			public void onGetGeoCodeResult(GeoCodeResult arg0) {
				// TODO Auto-generated method stub
				
			}
		};
		
		/**
		 * 传图片到服务器
		 */
		private void upLoadDataToNet(String path,boolean isImage) {
			String companyID = MyApplication.getInstance().getCompanyID() + "";
			String modulePage = "Action";
			String picData = "";
			try {
				picData = Base64Util.encodeBase64File(path);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (isImage) {
				//传图片到服务器
				   UpLoadImgSignText upLoadImgSignText = new UpLoadImgSignText(picData, modulePage, System.currentTimeMillis()+".jpg", companyID, "");
				   upLoadImgSignText.setUpLoadResult(CreateOnlookersActivivty.this);
				   new Thread(upLoadImgSignText).start();
				   isUpFile = false;
			}else {
			   //传文件到服务器
			   UpLoadFileTask  upLoadFileTask = new UpLoadFileTask(picData, modulePage,System.currentTimeMillis()+".amr", companyID);
			   upLoadFileTask.setUpLoadResult(CreateOnlookersActivivty.this);
			   new Thread(upLoadFileTask).start();
			   isUpFile = true;
			}
		}

		
		@Override
		public void onUpLoadResult(String FileName, String FilePath,
				boolean isSuccess) {
			// TODO Auto-generated method stub
			if (isSuccess) {
				if (isUpFile) {
					showToast(true, "录音上传成功");
					OnLooksList list = new OnLooksList();
					list.setPictureAbsolutePath(currentRecordPath);
					String[] temper = currentRecordPath.split("/");
					list.setRecordName(temper[temper.length-1]);
					list.setIsWho(3);
					mList.add(list);
					addLayout();
					pList.add(FilePath);
					pDialog.dismiss();
				}else {
					pList.add(FilePath);
					L.e("pList'''''''':" + pList.size());
					//图片上传成功
					L.i("------------->:" +   FilePath);
					pDialog.dismiss();
					showToast(true, "图片上传成功！");
					if (Isp == 0x0001) {
						L.d("拍照返回");
						OnLooksList list = new OnLooksList();
						list.setIsWho(1);
						list.setTakePhotoName(takePhotoFileName);
						list.setTakePhotoAbsolutePath(takePhotoPath);
						mList.add(list);
						addLayout();
					}else if (Isp == 0x0002) {
						OnLooksList list1 = new OnLooksList();
						list1.setIsWho(2);
						String[] temper = PicturePath.split("/");
						list1.setPictureName(temper[temper.length-1]);
						list1.setPictureAbsolutePath(PicturePath);
						mList.add(list1);
	                    addLayout();
					}
				}
				
			}else {
				//图片上传失败
				L.i("------------->:" + "上传失败" );
				showToast(false, "文件上传失败！");
				pDialog.dismiss();
			}
		}
		
		/**
		 * 自定义toast显示
		 * @param isS 操作成功true，失败false
		 * @param msg 提示文字
		 */
		private void showToast(boolean isS,String msg){
			if (isS) {
				 ClueCustomToast.showToast(CreateOnlookersActivivty.this,
						 R.drawable.toast_sucess, msg);
			}else {
				 ClueCustomToast.showToast(CreateOnlookersActivivty.this,
						R.drawable.toast_warn, msg);
			}
			
		}
		
		/**
		 * 显示隐藏软键盘
		 * @param bool true为显示，false 为隐藏
		 */
	private void showSoftInput(final boolean bool) {
		// 强制弹出软键盘，延时目的是为了避免控件未加载完弹出软键盘失败
		if (bool) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub

					// 让软键盘自动弹出
					mInputMethodManager.showSoftInput(mNote, 0);
					mFaceRoot.setVisibility(View.GONE);
					face_show_btn
							.setImageResource(R.drawable.onlooks_expression);
					mIsFaceShow = false;
				}
			}, 200);
		} else {
			// 强制隐藏缉键盘
			mInputMethodManager.toggleSoftInput(0,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}

		}
	
	
}
