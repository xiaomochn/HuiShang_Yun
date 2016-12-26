package com.huishangyun.Office.WeiGuan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Adapter.FacePageAdeapter;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Util.DisplayUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.View.CirclePageIndicator;
import com.huishangyun.View.JazzyViewPager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.yun.R;

public class SendOnLooksCommentActivity extends BaseActivity {
	private TextView cancel;//取消
	private TextView send;//发送
	private EditText mNote;//文字内容
	private TextView Tittle;//标题
	private LinearLayout shareLayout;//分享条
	//添加表情
	private LinearLayout expression;//表情
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
	private Object[] mFaceoObjects;//要添加的表情对象
	private TextView TxT;
	private int ID;
	private String RealName;
	private LinearLayout takephoto;//拍照
	private LinearLayout sharetheme;//主题
	private LinearLayout locationpicture;//本地图片
	private LinearLayout soundrecording;//录音
	private LinearLayout faceClass;//表情分类
	private webServiceHelp<OnLooksList> webHelp;//提交评论数据
    private OnLooksList list = new OnLooksList();
    private static final int SEND_SUCCESS = 0x0001;//评论成功
    private static final int SEND_FAIL = 0x0002;//评论失败
    private static final int SEND_DEBUG = 0x0003;//返回错误码
    private LinearLayout locationLayout;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_office_onlooks_create);
		init();
		initFacePage();
	}

	
	/**
	 * 初始化
	 */
	@SuppressLint("NewApi")
	private void init(){
		webHelp = new webServiceHelp<OnLooksList>(Methods.SET_ONLOOKS_COMMENT, new TypeToken<ZJResponse<OnLooksList>>(){}.getType());
		webHelp.setOnServiceCallBack(onServiceCallBack);
		mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		Intent intent = getIntent();
		Tittle = (TextView) findViewById(R.id.Tittle);
		Tittle.setText(intent.getStringExtra("Tittle"));
		shareLayout = (LinearLayout) findViewById(R.id.shareLayout);
		shareLayout.setVisibility(View.GONE);
		locationLayout = (LinearLayout) findViewById(R.id.locationLayout);
		locationLayout.setVisibility(View.GONE);
		mNote = (EditText) findViewById(R.id.mNote);
		takephoto = (LinearLayout) findViewById(R.id.takephoto);
		sharetheme = (LinearLayout) findViewById(R.id.sharetheme);
		locationpicture = (LinearLayout) findViewById(R.id.locationpicture);
		soundrecording = (LinearLayout) findViewById(R.id.soundrecording);
		takephoto.setVisibility(View.INVISIBLE);
		sharetheme.setVisibility(View.INVISIBLE);
		locationpicture.setVisibility(View.INVISIBLE);
		soundrecording.setVisibility(View.INVISIBLE);
		faceClass = (LinearLayout) findViewById(R.id.faceClass);
		faceClass.setVisibility(View.GONE);
		if (intent.getStringExtra("Tittle").equals("发评论")) {
			shareLayout.setVisibility(View.GONE);
			mNote.setHint("写评论...");
		}else {
			shareLayout.setVisibility(View.VISIBLE);
			mNote.setHint("分享新鲜事...");
		}
		ID = intent.getIntExtra("ID", -1);
		RealName = intent.getStringExtra("RealName");
		cancel = (TextView) findViewById(R.id.cancel);
		send = (TextView) findViewById(R.id.send);
	
		TxT = (TextView) findViewById(R.id.TxT);
		expression = (LinearLayout) findViewById(R.id.expression);
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
		
	
		cancel.setOnClickListener(onClick);
		send.setOnClickListener(onClick);
		expression.setOnClickListener(onClick);
		recent.setOnClickListener(onClick);
		classical.setOnClickListener(onClick);
		bigexpression.setOnClickListener(onClick);
		TxT.setOnClickListener(onClick);
		mNote.setOnClickListener(onClick);
		
		showSoftInput(true);
		
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		webHelp.removeOnServiceCallBack();
		super.onDestroy();
	}
	
	/**
	 * 获取围观主题接口回调
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
					mHandler.sendEmptyMessage(SEND_SUCCESS);
					break;
				default:
					//错误码
					mHandler.sendEmptyMessage(SEND_DEBUG);
					break;
				}
			} else {
				//接口访问失败
				L.e("接口访问失败，请检查网络！");
				mHandler.sendEmptyMessage(SEND_FAIL);
			}
		}
	};
	
	
	/**
	 * 提交评论接口
	 * @param ManagerName 评论人昵称
	 * @param ID 被评论对象id
	 * @param Note 评论内容
	 * @param replyTo 回复谁谁谁
	 * @return 返回json数据
	 */
	private String getThemeJson(String ManagerName,int ID,String Note,String replyTo) {
		list.setManager_ID(MyApplication.getInstance().getManagerID());
		list.setManager_Name(ManagerName);
		list.setID(ID);
		list.setNote(Note);
		list.setReplyTo(replyTo);
		list.setCompany_ID(MyApplication.getInstance().getCompanyID());
		ZJRequest<OnLooksList> zjRequest = new ZJRequest<OnLooksList>();
		zjRequest.setData(list);
		return JsonUtil.toJson(zjRequest);
	}
	
	
	
	
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SEND_SUCCESS:
				MyApplication.getInstance().showDialog(SendOnLooksCommentActivity.this, false, "正在发送...");
				showToast(true, "评论发表成功！");
				setResult(0x0006);
				showSoftInput(false);
				finish();
				break;
			case SEND_FAIL:
				MyApplication.getInstance().showDialog(SendOnLooksCommentActivity.this, false, "正在发送...");
				showToast(false, "评论失败，请检查网络连接！");
				break;
			case SEND_DEBUG:
				MyApplication.getInstance().showDialog(SendOnLooksCommentActivity.this, false, "正在发送...");
				showToast(false, "评论失败");
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
				MyApplication.getInstance().showDialog(SendOnLooksCommentActivity.this, true, "正在发送...");
				L.e("jsonsend：" + getThemeJson(preferences.getString(Constant.XMPP_MY_REAlNAME, ""), ID, mNote.getText().toString(), RealName));
				webHelp.start(getThemeJson(preferences.getString(Constant.XMPP_MY_REAlNAME, ""), ID, mNote.getText().toString(), RealName));
				break;
			case R.id.expression://表情
				if (!mIsFaceShow) {
					mInputMethodManager.hideSoftInputFromWindow(
							mNote.getWindowToken(), 0);
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
					mInputMethodManager.showSoftInput(mNote, 0);
					mIsFaceShow = false;
				}
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
			default:
				break;
			}
		}
	};
	

	
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
							return;
						}
						mNote.getText()
								.delete(selection - 1, selection);
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
						int newHeight = DisplayUtil.sp2px(SendOnLooksCommentActivity.this, 20);
						int newWidth = DisplayUtil.sp2px(SendOnLooksCommentActivity.this, 20);
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
						ImageSpan imageSpan = new ImageSpan(SendOnLooksCommentActivity.this,
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
		 * 自定义toast显示
		 * @param isS 操作成功true，失败false
		 * @param msg 提示文字
		 */
		private void showToast(boolean isS,String msg){
			if (isS) {
				 ClueCustomToast.showToast(SendOnLooksCommentActivity.this,
						 R.drawable.toast_sucess, msg);
			}else {
				 ClueCustomToast.showToast(SendOnLooksCommentActivity.this,
						R.drawable.toast_warn, msg);
			}
			
		}
		
		/**
		 * 显示隐藏软键盘
		 * @param bool true为显示，false 为隐藏
		 */
		private void showSoftInput(final boolean bool){
			//强制弹出软键盘，延时目的是为了避免控件未加载完弹出软键盘失败
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

