package com.huishangyun.Office.WeiGuan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Channel.Visit.PictureSkim;
import com.huishangyun.Office.Summary.ImageLoad;
import com.huishangyun.Util.FaceUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.T;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.Util.webServiceHelp.OnServiceCallBack;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;

/**
 * 围观详情主页
 * @author xsl
 *
 */
public class OnLooksDetails extends BaseActivity implements XScrollView.IXScrollViewListener {
	
	private LinearLayout back;//返回
	private TextView Title;//标题
	private TextView topComment;
	private TextView topPraise;
	private TextView comment;//评论
	private TextView praise;//赞
	private ImageView icon;//详情所属人
	private TextView nickname;//昵称
	private TextView senddateTime;//发表时间
	private TextView belongDepartment;//所属部门
	private TextView content;//内容
	private LinearLayout mLayout;//动态控件
	private XScrollView mScroll;
	private LinearLayout group_layout;
	private LinearLayout dynamicLayout;
	private MyListView mlistview;
	private BaseAdapter adapter;
	private boolean isBreak = false;
	private boolean isFirstIn = true;
	private boolean isPraise = false;
	private View scrollView;
	private TextView giveComment;//评论
	private TextView givePraise;//点赞
	private ImageView delete;//删除
	private View delView;
	private webServiceHelp<OnLooksList> webServiceHelpDetails;//详情
	private webServiceHelp<OnLooksList> webServiceHelpComment;//评论列表
	private webServiceHelp<T> webHelpPraise;//点赞
	private webServiceHelp<OnLooksList> webHelpPraiseList;//获取点赞列表
	private webServiceHelp<T> webHelpDelete;//删除围观操作
	private OnLooksList detailsList = new OnLooksList();
	private List<OnLooksList> detailsListcomment = new ArrayList<OnLooksList>();
	private List<OnLooksList> mcomment = new ArrayList<OnLooksList>();
	private List<OnLooksList> detailsListPraise = new ArrayList<OnLooksList>();
	private List<OnLooksList> mPraise = new ArrayList<OnLooksList>();
	private static final int GET_DETAILS_SUCCESS = 0x0001;//获取详情成功
	private static final int GET_DETAILS_FAIL = 0x0002;//接口访问失败
	private static final int GET_DETAILS_BUG = 0x0003;//返回错误码
	private static final int GET_DETAILS_COMMENT = 0x0004;//获取评论成功
	private static final int GET_DETAILS_PRAISE = 0x0005;//获取点赞列表成功
	private Intent intent;
	private int PagerIndex = 1;
	private int PagerSize = 5;
	private boolean isComment = true;
	private int requestCode = 0x0006;
	private int PraiseIndex = -1;//原始点赞数
	private TextView locationAddress;
	private LinearLayout locationLayout;
	private View showLine;
	private LinearLayout mianLayout;
	private ImageView ImageBig;
	private GridView gridView;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_office_onlooks_details);
		detailsListcomment.clear();
		detailsListPraise.clear();
		init();
	}
	
	/**
	 * 初始化
	 */
	private void init() {
		intent = getIntent();
		webServiceHelpDetails = new webServiceHelp<OnLooksList>(Methods.GET_ONLOOKS_DETAILS, new TypeToken<ZJResponse<OnLooksList>>(){}.getType());
		webServiceHelpDetails.setOnServiceCallBack(onServiceCallBackDetails);
		webServiceHelpDetails.start(getDetailsJson(intent.getIntExtra("ID", -1)));
		webServiceHelpComment = new webServiceHelp<OnLooksList>(Methods.GET_ONLOOKS_DETAILS_COMMENT, new TypeToken<ZJResponse<OnLooksList>>(){}.getType());
		webServiceHelpComment.setOnServiceCallBack(onServiceCallBackDetailsComment);
		webServiceHelpComment.start(getDetailsCommentJson(intent.getIntExtra("ID", -1),1));
		webHelpPraise = new webServiceHelp<T>(Methods.SET_ONLOOKS_PRAISE, new TypeToken<ZJResponse<T>>(){}.getType());
		webHelpPraise.setOnServiceCallBack(onLooksPraise);
		webHelpDelete = new webServiceHelp<T>(Methods.ONLOOKS_DELETE_MYSELF, new TypeToken<ZJResponse<T>>(){}.getType());
		webHelpDelete.setOnServiceCallBack(onLooksDelete);
		//点赞列表
		webHelpPraiseList = new webServiceHelp<OnLooksList>(Methods.GET_ONLOOKS_PRAISE_LIST, new TypeToken<ZJResponse<OnLooksList>>(){}.getType());
		webHelpPraiseList.setOnServiceCallBack(onServiceCallBackPraiseList);
		
		back = (LinearLayout) findViewById(R.id.back);
		Title = (TextView) findViewById(R.id.Title);
		Title.setText(intent.getStringExtra("Tittle"));
		topComment = (TextView) findViewById(R.id.topComment); 
		topPraise = (TextView) findViewById(R.id.topPraise); 
		giveComment = (TextView) findViewById(R.id.giveComment); 
		givePraise = (TextView) findViewById(R.id.givePraise); 
		delete = (ImageView) findViewById(R.id.delete); 
		delView = (View) findViewById(R.id.delView);
		if (intent.getBooleanExtra("canSee", false)) {
			delete.setVisibility(View.VISIBLE);
			delView.setVisibility(View.VISIBLE);
		}else {
			delete.setVisibility(View.GONE);
			delView.setVisibility(View.GONE);
		}
		mScroll = (XScrollView) findViewById(R.id.mScroll);
		mScroll.setPullRefreshEnable(true);
        mScroll.setPullLoadEnable(true);
        mScroll.setAutoLoadEnable(true);
        mScroll.setIXScrollViewListener(this);
        mScroll.setRefreshTime(getTime());
        LayoutInflater inflater = LayoutInflater.from(this);
		scrollView = inflater.inflate(
				R.layout.activity_office_onlooks_details_scrollview_item, null);
		ImageBig = (ImageView) scrollView.findViewById(R.id.imageBig);
		gridView = (GridView) scrollView.findViewById(R.id.gridview);
		mianLayout = (LinearLayout) scrollView.findViewById(R.id.mianLayout);
		mianLayout.setVisibility(View.INVISIBLE);
		icon = (ImageView) scrollView.findViewById(R.id.icon);
		locationAddress = (TextView) scrollView.findViewById(R.id.locationAddress);
		locationLayout = (LinearLayout) scrollView.findViewById(R.id.locationLayout);
		showLine = (View) scrollView.findViewById(R.id.showLine);
		nickname = (TextView) scrollView.findViewById(R.id.nickname);
		senddateTime = (TextView) scrollView.findViewById(R.id.senddateTime);
		belongDepartment = (TextView) scrollView.findViewById(R.id.belongDepartment);
		content = (TextView) scrollView.findViewById(R.id.content);
		mLayout = (LinearLayout) scrollView.findViewById(R.id.mLayout);
		comment = (TextView) scrollView.findViewById(R.id.comment);
		praise = (TextView) scrollView.findViewById(R.id.praise);
		dynamicLayout = (LinearLayout) scrollView.findViewById(R.id.dynamicLayout);
		mlistview = (MyListView) scrollView.findViewById(R.id.mlistview);
		mScroll.setView(scrollView);//添加数据
		group_layout = (LinearLayout) findViewById(R.id.group_layout); 
		adapter = new mAdapter(this,detailsListcomment);
		mlistview.setAdapter(adapter);
		setListViewHeightBasedOnChildren(mlistview);
		if (intent.getStringExtra("Tittle").equals("围观详情")) {
			//设置scrollview滚动到顶部
			mScroll.smoothScrollTo(0,20);
		}else if (intent.getStringExtra("Tittle").equals("围观评论") && detailsListcomment.size()>=5) {
			group_layout.setVisibility(View.VISIBLE);
		}else {
			mScroll.smoothScrollTo(0,20);
		}
		back.setOnClickListener(onClick);
		comment.setOnClickListener(onClick);
		praise.setOnClickListener(onClick);
		giveComment.setOnClickListener(onClick);
		givePraise.setOnClickListener(onClick);
		delete.setOnClickListener(onClick);
	
		//监听scrollview是否滚动到底部
		mScroll.setOnScrollToBottomLintener(new XScrollView.OnScrollToBottomListener() {
			
			@Override
			public void onScrollBottomListener(boolean isBottom) {
				// TODO Auto-generated method stub
				if (isBottom) {
					isBreak = true;
				}else {
					isBreak = false;
				}
				
			}
		});
		
		mScroll.setOnScrollPostionLintener(new XScrollView.OnScrollPostionListener() {
			
			@Override
			public void OnScrollPostionListener(long postion) {
				// TODO Auto-generated method stub
				if (isFirstIn) {
					isFirstIn = false;
			   }else {
				    int[] location = new int[2];  
				    group_layout.getLocationOnScreen(location);   
		            int y = location[1]; 
		            int[] location1 = new int[2];  
		            dynamicLayout.getLocationOnScreen(location1);   
			         int y1 = location1[1];
				if ( y1 <= y) {
					group_layout.setVisibility(View.VISIBLE);
				}else {
					group_layout.setVisibility(View.GONE);
				}
			}
			}
		});
	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		webServiceHelpDetails.removeOnServiceCallBack();
		webServiceHelpComment.removeOnServiceCallBack();
		webHelpPraise.removeOnServiceCallBack();
		webHelpDelete.removeOnServiceCallBack();
		super.onDestroy();
	}
	
	/**
	 * 获取围观详情接口回调
	 */
	private OnServiceCallBack<OnLooksList> onServiceCallBackDetails = new OnServiceCallBack<OnLooksList>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<OnLooksList> zjResponse) {
			// TODO Auto-generated method stub
			if (haveCallBack && zjResponse != null) {
				switch (zjResponse.getCode()) {
				case 0:
					//访问成功
					detailsList  = zjResponse.getResult();
					if (detailsList != null) {
						mHandler.sendEmptyMessage(GET_DETAILS_SUCCESS);
					}
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
	 * 获取详情json
	 * @return
	 */
	private String getDetailsJson(int ID) {
		ZJRequest<T> zjRequest = new ZJRequest<T>();
		zjRequest.setID(ID);
		L.e("获取围观详情json = " + JsonUtil.toJson(zjRequest));
		return JsonUtil.toJson(zjRequest);

	}
	
	/**
	 * 获取围观详情接口回调
	 */
	private OnServiceCallBack<OnLooksList> onServiceCallBackDetailsComment = new OnServiceCallBack<OnLooksList>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<OnLooksList> zjResponse) {
			// TODO Auto-generated method stub
			if (haveCallBack && zjResponse != null) {
				switch (zjResponse.getCode()) {
				case 0:
					//访问成功
					mcomment  = zjResponse.getResults();
				    mHandler.sendEmptyMessage(GET_DETAILS_COMMENT);
					break;
				default:
					//错误码
					mHandler.sendEmptyMessage(GET_DETAILS_BUG);
					break;
				}
			} else {
				//接口访问失败
				L.e("接口访问失败，请检查网络！");
				mHandler.sendEmptyMessage(GET_DETAILS_FAIL);
			}
		}
	};
	
	
	/**
	 * 获取详情评论json
	 * @return
	 */
	private String getDetailsCommentJson(int ID,int PageIndex ) {
		ZJRequest<T> zjRequest = new ZJRequest<T>();
		zjRequest.setID(ID);
		zjRequest.setPageIndex(PageIndex);
		zjRequest.setPageSize(5);
		return JsonUtil.toJson(zjRequest);

	}
	
	/**
	 * 点赞接口回调
	 */
	private OnServiceCallBack<T> onLooksPraise = new OnServiceCallBack<T>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<T> zjResponse) {
			// TODO Auto-generated method stub
			if (haveCallBack && zjResponse != null) {
				switch (zjResponse.getCode()) {
				case 0:
					//访问成功
					L.e("点赞成功！");
					break;

				default:
					//错误码
					showToast(false, "错误码！");
					break;
				}
			} else {
				//接口访问失败
				showToast(false, "未获得任何数据，请检查网络连接！");
			}
		}
	};
	
	/**
	 * 点赞json对象
	 * @param Action_ID
	 * @return
	 */
	private String getPraiseJson(int Action_ID,boolean isPraise) {
		ZJRequest<T> zjRequest = new ZJRequest<T>();
       //		点赞时：Action=Insert
       //		取消时：Action=Delete
		if (isPraise) {
			zjRequest.setAction("Insert");
		}else {
			zjRequest.setAction("Delete");
		}
		zjRequest.setCompany_ID(MyApplication.getInstance().getCompanyID());
		zjRequest.setManager_ID(MyApplication.getInstance().getManagerID());
		zjRequest.setManager_Name(preferences.getString(Constant.XMPP_MY_REAlNAME, ""));
		zjRequest.setID(Action_ID);
		return JsonUtil.toJson(zjRequest);

	}
	
	
	/**
	 * 围观删除
	 */
	private OnServiceCallBack<T> onLooksDelete = new OnServiceCallBack<T>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<T> zjResponse) {
			// TODO Auto-generated method stub
			if (haveCallBack && zjResponse != null) {
				switch (zjResponse.getCode()) {
				case 0:
					//访问成功
					L.e("本条围观删除成功！");
					setResult(0x0004);
					finish();
					break;
				default:
					//错误码
					showToast(false, "错误码！");
					break;
				}
			} else {
				//接口访问失败
				showToast(false, "未获得任何数据，请检查网络连接！");
			}
		}
	};
	
	
	/**
	 * 删除围观操作
	 * @param ID
	 * @return
	 */
	private String deleteJson(int ID) {
		ZJRequest<T> zjRequest = new ZJRequest<T>();
		zjRequest.setManager_ID(MyApplication.getInstance().getManagerID());
		zjRequest.setCompany_ID(MyApplication.getInstance().getCompanyID());
		zjRequest.setDepartment_ID(preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID,0));
		zjRequest.setID(ID);
		return JsonUtil.toJson(zjRequest);

	}
	
	
	/**
	 * 获取围观详情接口回调
	 */
	private OnServiceCallBack<OnLooksList> onServiceCallBackPraiseList = new OnServiceCallBack<OnLooksList>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<OnLooksList> zjResponse) {
			// TODO Auto-generated method stub
			if (haveCallBack && zjResponse != null) {
				switch (zjResponse.getCode()) {
				case 0:
					//访问成功
					mPraise = zjResponse.getResults();
					mHandler.sendEmptyMessage(GET_DETAILS_PRAISE);
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
	 * 获取点赞列表json
	 * @param ID
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	private String PraiseListJson(int ID,int pageIndex) {
		ZJRequest<T> zjRequest = new ZJRequest<T>();
		zjRequest.setID(ID);
		zjRequest.setPageIndex(pageIndex);
		zjRequest.setPageSize(5);
		return JsonUtil.toJson(zjRequest);

	}
	
	/**
	 * 更新UI
	 */
	 private Handler mHandler = new Handler(){
		 public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_DETAILS_SUCCESS:
				try {
					String ImageUrl = "http://img.huishangyun.com/UploadFile/huishang/"+ 
			                MyApplication.getInstance().getCompanyID()+"/Photo/" + detailsList.getPhoto();
					new ImageLoad().displayImage(context, ImageUrl,icon, R.drawable.defaultimage02, 0, false);
				} catch (Exception e) {
					// TODO: handle exception
				   e.printStackTrace();
				}
				if (detailsList.getShowGps()) {
					locationLayout.setVisibility(View.VISIBLE);
					showLine.setVisibility(View.VISIBLE);
					locationAddress.setText(detailsList.getLoc());
				}else {
					showLine.setVisibility(View.GONE);
					locationLayout.setVisibility(View.GONE);
				}
				nickname.setText(detailsList.getRealName());
				senddateTime.setText(backdate(detailsList.getAddDateTime()));
				belongDepartment.setText(detailsList.getSign());
				content.setText("");
				if (!detailsList.getTopic().equals("")) {
					SpannableString spanText = new SpannableString(detailsList.getTopic());
					/*spanText.setSpan(new ForegroundColorSpan(Color.parseColor("#21a5de")), 0, spanText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
					spanText.setSpan(new RelativeSizeSpan(1.0f), 0, spanText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);*/
					spanText.setSpan(new ClickableSpan(){
						@Override
						public void updateDrawState(TextPaint ds) {
							ds.setColor(Color.parseColor("#21a5de")); //设置链接的文本颜色
							ds.setUnderlineText(false); //去掉下划线
						}
						@Override
						public void onClick(View widget) {
							// TODO Auto-generated method stub
							L.e("点击了话题:" + detailsList.getTopic());
							//showCustomToast("点击了话题:" + list.get(position).getTopic(), true);
							Intent intent = new Intent(OnLooksDetails.this, TopOnlookActivity.class);
							intent.putExtra("Topic", detailsList.getTopic());
							startActivity(intent);
							
						}
						
					}, 0, spanText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
					
					content.append(spanText);
				}
				/*SpannableString spanText = new SpannableString(detailsList.getTopic());
				spanText.setSpan(new ForegroundColorSpan(Color.parseColor("#21a5de")), 0, spanText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				spanText.setSpan(new RelativeSizeSpan(1.0f), 0, spanText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				content.append(spanText);*/
				content.append(FaceUtil.convertNormalStringToSpannableStringIntelligence(getApplication(), detailsList.getNote(), 19,true));
				content.setMovementMethod(LinkMovementMethod.getInstance());//设置超链接为可点击状态
				comment.setText("评论  " + detailsList.getComment());
				praise.setText("点赞  " + detailsList.getGood() + "");
				topComment.setText("评论  " + detailsList.getComment());
				topPraise.setText("点赞  " + detailsList.getGood() + "");
				//只初始化一次
				if (PraiseIndex == -1) {
					PraiseIndex =  detailsList.getGood();
				}
				dongtai();
				MyApplication.getInstance().showDialog(OnLooksDetails.this, false, "Loading...");
				mianLayout.setVisibility(View.VISIBLE);
				break;
			case GET_DETAILS_FAIL:
				MyApplication.getInstance().showDialog(OnLooksDetails.this, false, "Loading...");
				showToast(false, "接口访问失败，请检查网络连接！");		
				break;
			case GET_DETAILS_BUG:
				MyApplication.getInstance().showDialog(OnLooksDetails.this, false, "Loading...");
				showToast(false, "错误码！");
				break;
			case GET_DETAILS_COMMENT:
				isComment = true;
				if (PagerIndex != 1) {
					if (mcomment.size()>0) {
						//detailsListcomment.clear();
						for (int i = 0; i < mcomment.size(); i++) {
							detailsListcomment.add(mcomment.get(i));
						}
						adapter = new mAdapter(OnLooksDetails.this, detailsListcomment);
						mlistview.setAdapter(adapter);
						setListViewHeightBasedOnChildren(mlistview);
					}else {
						showToast(false, "没有更多数据！");
					}
				}else {
					detailsListcomment = mcomment;
					adapter = new mAdapter(OnLooksDetails.this, detailsListcomment);
					mlistview.setAdapter(adapter);
					setListViewHeightBasedOnChildren(mlistview);
				}
				break;
			case GET_DETAILS_PRAISE:
				isComment = false;
				if (PagerIndex != 1) {
					if (mPraise.size()>0) {
						for (int i = 0; i < mPraise.size(); i++) {
							detailsListPraise.add(mPraise.get(i));
						}
						adapter = new mAdapter(OnLooksDetails.this, detailsListPraise);
						mlistview.setAdapter(adapter);
						setListViewHeightBasedOnChildren(mlistview);
					}else {
						showToast(false, "没有更多数据！");
					}
				}else {
					detailsListPraise = mPraise;
					adapter = new mAdapter(OnLooksDetails.this, detailsListPraise);
					mlistview.setAdapter(adapter);
					setListViewHeightBasedOnChildren(mlistview);
				}
				break;
			default:
				break;
			}
		 };
	 };
	
	
	 
	 /**
	  * 动态添加控件
	  */
	 private void dongtai(){
//		  mLayout.removeAllViews();
//		    int indexImg = 0;
//		    int indexFile = 0;
//			String[] strImage =  (detailsList.getAttachmentPath()).split("#");
//			String[]  strFile =  (detailsList.getAttachment()).split("#");
//			for (int i = 0; i < strImage.length; i++) {
//				if ((strImage[i].toString().trim()).length()>0) {
//					indexImg++;
//				}
//			}
//			for (int i = 0; i < strFile.length; i++) {
//				if ((strFile[i].toString().trim()).length()>0) {
//					indexFile++;
//				}
//			}
		 
			 int indexImg = 0;
			    int indexFile = 0;
			    final List<String> allData = new ArrayList<String>();
			    String[] strImage =  (detailsList.getAttachmentPath()).split("#");
				String[]  strFile =  (detailsList.getAttachment()).split("#");
				for (int i = 0; i < strImage.length; i++) {
					if ((strImage[i].toString().trim()).length()>0) {
						allData.add(strImage[i]);
						indexImg++;
					}
				}
				
				for (int i = 0; i < strFile.length; i++) {
					if ((strFile[i].toString().trim()).length()>0) {
						allData.add(strFile[i]);
						indexFile++;
					}
				}
				
			if (indexFile == 0 && indexImg == 1) {
				showLine.setVisibility(View.GONE);
				ImageBig.setVisibility(View.VISIBLE);
				gridView.setVisibility(View.GONE);
				String ImageUrl = "http://img.huishangyun.com/UploadFile/huishang/"+ 
		                MyApplication.getInstance().getCompanyID()+"/Action/500x500/" + allData.get(0);
				new ImageLoad().displayImage(context, ImageUrl,ImageBig, R.drawable.defaultimage02, 0, false);
				ImageBig.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(context,PictureSkim.class);
						intent.putExtra("index", 5);
						intent.putExtra("imgselect", 0);
						intent.putExtra("imgUri", allData.get(0));
						context.startActivity(intent);
					}
				});
				
			}else {
				showLine.setVisibility(View.GONE);
				gridView.setVisibility(View.VISIBLE);
				ImageBig.setVisibility(View.GONE);
				mGirdViewBaseAdapter girdViewAdapter = new mGirdViewBaseAdapter(context,allData,indexImg);
				gridView.setAdapter(girdViewAdapter);
			}

//		 for (int i1 = 0; i1 < (indexImg + indexFile); i1++) {
//				LayoutInflater inflater = LayoutInflater.from(this);
//				View mView = inflater.inflate(R.layout.activity_office_onlookers_create_item, null);
//				final LinearLayout itemLayout = (LinearLayout) mView.findViewById(R.id.itemLayout);
//				if (i1<indexImg) {
//					itemLayout.setTag(strImage[i1]);
//				}else {
//					itemLayout.setTag(strFile[i1-indexImg]);
//				}
//				ImageView picture = (ImageView) mView.findViewById(R.id.picture);
//				final TextView fileName = (TextView) mView.findViewById(R.id.fileName);
//				ImageView delete = (ImageView) mView.findViewById(R.id.delete);
//				View topLine = (View) mView.findViewById(R.id.topLine);
//				View viewLine = (View) mView.findViewById(R.id.viewLine);
//				delete.setVisibility(View.GONE);
//				if (i1==0) {
//					topLine.setVisibility(View.VISIBLE);
//				}else {
//					topLine.setVisibility(View.GONE);
//				}
//				if (i1 == (indexImg + indexFile)-1) {
//					viewLine.setVisibility(View.GONE);
//				}else {
//					viewLine.setVisibility(View.VISIBLE);
//				}
//				if (i1<indexImg) {
//					String ImageUrl = "http://img.huishangyun.com/UploadFile/huishang/"+ 
//			                MyApplication.getInstance().getCompanyID()+"/Action/100x100/" + strImage[i1];
//					ImageLoad.displayImage(ImageUrl, picture, R.drawable.defaultimage02,0);
//					fileName.setText(strImage[i1]);
//				}else {
//					fileName.setText(strFile[i1-indexImg]);
//					picture.setBackgroundResource(R.drawable.service_recordfile);
//				}
//				
//                itemLayout.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						// 跳转浏览图片或者播放录音
//						String strImg =  (String) itemLayout.getTag();
//					    L.e("围观图片地址：" + strImg);
//						//判断文件是录音还是图片
//						if (JudgeIsImage.isImage(strImg)) {
//							Intent intent1 = new Intent(context,PictureSkim.class);
//							intent1.putExtra("index", 5);
//							intent1.putExtra("imgselect", 0);
//							intent1.putExtra("imgUri", strImg);
//							context.startActivity(intent1);
//							
//						}else {
//							//跳出播放录音窗口
//							fileName.setTag(strImg);
////							new LoadFileTools(OnLooksDetails.this).loadData(fileName);
//						}
//					}
//				});
//				
//				mLayout.addView(mView);
//			}
		}
	 
	 
	/**
	* 动态获取ListView的高度
	* @param listView
	*/
	private void setListViewHeightBasedOnChildren(ListView listView) {
	    if(listView == null) return;
	    ListAdapter listAdapter = listView.getAdapter();
	    if (listAdapter == null) {
	        // pre-condition
	        return;
	    }
	    int totalHeight = 0;
	    //加4原因：增加高度使显示完整
	    for (int i = 0; i < listAdapter.getCount(); i++) {
	        View listItem = listAdapter.getView(i, null, listView);
	        listItem.measure(0, 0);
	        totalHeight += listItem.getMeasuredHeight();
	    }
	    ViewGroup.LayoutParams params = listView.getLayoutParams();
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount()-1));
	    listView.setLayoutParams(params);
	}
	
	/**
	 * 单击事件
	 */
	private OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			
			case R.id.back://返回
				finish();
				break;
			case R.id.comment://评论
				isComment = true;
				comment.setBackgroundResource(R.drawable.onlooks_select);
				praise.setBackgroundResource(R.drawable.onlooks_select_line);
				topComment.setBackgroundResource(R.drawable.onlooks_select);
				topPraise.setBackgroundResource(R.drawable.onlooks_select_line);
				comment.setTextColor(0xff21a5de);
				topComment.setTextColor(0xff21a5de);
				praise.setTextColor(0xff969696);
				topPraise.setTextColor(0xff969696);
				if (detailsListcomment.size()>0) {
					adapter = new mAdapter(OnLooksDetails.this, detailsListcomment);
					mlistview.setAdapter(adapter);
					setListViewHeightBasedOnChildren(mlistview);
				}else {
					PagerIndex = 1;
					webServiceHelpComment.start(getDetailsCommentJson(intent.getIntExtra("ID", -1),PagerIndex));
				}
				
				break;
			case R.id.praise://赞
				isComment = false;
				praise.setBackgroundResource(R.drawable.onlooks_select);
				comment.setBackgroundResource(R.drawable.onlooks_select_line);
				topPraise.setBackgroundResource(R.drawable.onlooks_select);
				topComment.setBackgroundResource(R.drawable.onlooks_select_line);
				comment.setTextColor(0xff969696);
				topComment.setTextColor(0xff969696);
				praise.setTextColor(0xff21a5de);
				topPraise.setTextColor(0xff21a5de);
				if (detailsListPraise.size()>0) {
					adapter = new mAdapter(OnLooksDetails.this, detailsListPraise);
					mlistview.setAdapter(adapter);
					setListViewHeightBasedOnChildren(mlistview);
				}else {
					PagerIndex = 1;
					webHelpPraiseList.start(PraiseListJson(intent.getIntExtra("ID", -1),PagerIndex));
				}
				break;
			case R.id.topComment://评论
				L.e("单击到了");
				isComment = true;
				comment.setBackgroundResource(R.drawable.onlooks_select);
				praise.setBackgroundResource(R.drawable.onlooks_select_line);
				topComment.setBackgroundResource(R.drawable.onlooks_select);
				topPraise.setBackgroundResource(R.drawable.onlooks_select_line);
				comment.setTextColor(0xff21a5de);
				topComment.setTextColor(0xff21a5de);
				praise.setTextColor(0xff969696);
				topPraise.setTextColor(0xff969696);
				if (detailsListcomment.size()>0) {
					adapter = new mAdapter(OnLooksDetails.this, detailsListcomment);
					mlistview.setAdapter(adapter);
					setListViewHeightBasedOnChildren(mlistview);
				}else {
					PagerIndex = 1;
					webServiceHelpComment.start(getDetailsCommentJson(intent.getIntExtra("ID", -1),PagerIndex));
				}
				
				break;
			case R.id.topPraise://赞
				isComment = false;
				praise.setBackgroundResource(R.drawable.onlooks_select);
				comment.setBackgroundResource(R.drawable.onlooks_select_line);
				topPraise.setBackgroundResource(R.drawable.onlooks_select);
				topComment.setBackgroundResource(R.drawable.onlooks_select_line);
				comment.setTextColor(0xff969696);
				topComment.setTextColor(0xff969696);
				praise.setTextColor(0xff21a5de);
				topPraise.setTextColor(0xff21a5de);
				if (detailsListPraise.size()>0) {
					adapter = new mAdapter(OnLooksDetails.this, detailsListPraise);
					mlistview.setAdapter(adapter);
					setListViewHeightBasedOnChildren(mlistview);
				}else {
					PagerIndex = 1;
					webHelpPraiseList.start(PraiseListJson(intent.getIntExtra("ID", -1),PagerIndex));
				}
				break;
				
			case R.id.giveComment://去评论
				Intent mIntent = new Intent(OnLooksDetails.this,SendOnLooksCommentActivity.class);
				mIntent.putExtra("Tittle", "发评论");
				mIntent.putExtra("ID", intent.getIntExtra("ID", -1));
				mIntent.putExtra("RealName", detailsList.getRealName());
				startActivityForResult(mIntent, requestCode);
				break;
			case R.id.givePraise://点赞
				if (isPraise) {
					isPraise = false;
					if (PraiseIndex>0) {
						praise.setText("点赞  " + PraiseIndex);
						topPraise.setText("点赞  " + PraiseIndex);
						webHelpPraise.start(getPraiseJson(intent.getIntExtra("ID", -1),false));
						givePraise.setTextColor(Color.parseColor("#969696"));
					}
				}else {
					isPraise = true;
					praise.setText("点赞  " + (PraiseIndex+1));
					topPraise.setText("点赞  " + (PraiseIndex+1));
					webHelpPraise.start(getPraiseJson(intent.getIntExtra("ID", -1),true));
					givePraise.setTextColor(Color.parseColor("#21a5de"));
				}
				
				break;
			case R.id.delete://删除
				webHelpDelete.start(deleteJson(intent.getIntExtra("ID", -1)));
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * listview适配器
	 * @author xsl
	 *
	 */
	private class mAdapter extends BaseAdapter{
        private LayoutInflater inflater;
        private List<OnLooksList> list;
        private boolean isEmptry = false;
		public mAdapter(Context context,List<OnLooksList> list){
			inflater = LayoutInflater.from(context);
			this.list = list;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (list.size()==0) {
				isEmptry = true;
				return 1;
			}else {
				isEmptry = false;
				return list.size();
			}
			
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView==null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.activivty_office_onlooks_details_item, null);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
				holder.senddateTime = (TextView) convertView.findViewById(R.id.senddateTime);
				holder.content = (TextView) convertView.findViewById(R.id.content);
				holder.view = (View) convertView.findViewById(R.id.view);
				holder.allLayout = (LinearLayout) convertView.findViewById(R.id.allLayout);
				holder.emptyTextView = (TextView) convertView.findViewById(R.id.emptyTextView);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (isEmptry) {
				holder.allLayout.setVisibility(View.GONE);
				holder.emptyTextView.setVisibility(View.VISIBLE);
				holder.view.setVisibility(View.GONE);
			}else {
				if (list.size()<5 && position == (list.size()-1)) {
					holder.emptyTextView.setVisibility(View.VISIBLE);
					holder.view.setVisibility(View.GONE);
				}else {
					holder.view.setVisibility(View.VISIBLE);
					holder.emptyTextView.setVisibility(View.GONE);
				}
					try {
						String ImageUrl = Constant.pathurl+
				                MyApplication.getInstance().getCompanyID()+"/Photo/" + list.get(position).getPhoto();
						new ImageLoad().displayImage(context, ImageUrl,holder.icon, R.drawable.defaultimage02, 0, false);
						holder.nickname.setText(list.get(position).getRealName());
						holder.senddateTime.setText(backdate(list.get(position).getAddDateTime()));
						if (isComment) {
							holder.content.setVisibility(View.VISIBLE);
							holder.content.setText(FaceUtil.convertNormalStringToSpannableStringIntelligence(context, list.get(position).getNote(),19, true));
						}else {
							holder.content.setVisibility(View.GONE);
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
			}
			
			return convertView;
		}
		
	}
	
	private static class ViewHolder{
		private LinearLayout allLayout;
		private ImageView icon;//头像
		private TextView nickname;//昵称
		private TextView senddateTime;//发送时间
		private TextView content;//内容
		private View view;
		private TextView emptyTextView;  
	}

	
	  private void onLoad() {
	        mScroll.stopRefresh();
	        mScroll.stopLoadMore();
	        mScroll.setRefreshTime(getTime());
	    }

	    public String getTime() {
	        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
	    }
	   
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				onLoad();
			}
		}, 2000);
		PagerIndex = 1;
		if (isComment) {
			webServiceHelpComment.start(getDetailsCommentJson(intent.getIntExtra("ID", -1),PagerIndex));
		}else {
			webHelpPraiseList.start(PraiseListJson(intent.getIntExtra("ID", -1),PagerIndex));
		}
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
	mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				onLoad();
			}
		}, 2000);
		PagerIndex += 1;
		//PagerSize += 5;
		if (isComment) {
			webServiceHelpComment.start(getDetailsCommentJson(intent.getIntExtra("ID", -1),PagerIndex));
		}else {
			
			webHelpPraiseList.start(PraiseListJson(intent.getIntExtra("ID", -1),PagerIndex));
		 }
	}
	
	/**
	 * 自定义Toast显示
	 * @param isS 
	 * @param msg 提示内容
	 */
	private void showToast(boolean isS,String msg){
		if (isS) {
			 ClueCustomToast.showToast(OnLooksDetails.this,
					R.drawable.toast_sucess, msg);
		}else {
			 ClueCustomToast.showToast(OnLooksDetails.this,
					R.drawable.toast_warn, msg);
		}
		
	}
	
	/**
	 * 日期分割字符串
	 * @return
	 */
	private String backdate(String str){
		String[] s = str.split("T");
		String s1 = s[0];
		String s2 = s[1].substring(0, 5);
		return s1 + " " + s2;
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		if (arg1 == requestCode && arg0 == requestCode) {
			//详情刷新
			webServiceHelpDetails.start(getDetailsJson(intent.getIntExtra("ID", -1)));
			//刷新列表
			PagerIndex = 1;
			webServiceHelpComment.start(getDetailsCommentJson(intent.getIntExtra("ID", -1),PagerIndex));
			isComment = true;
		}
		super.onActivityResult(arg0, arg1, arg2);
	}
}
