package com.huishangyun.Office.WeiGuan;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Channel.Visit.PictureSkim;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Office.Summary.ImageLoad;
import com.huishangyun.Util.FaceUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Constant;
import com.huishangyun.Util.L;
import com.huishangyun.Util.T;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.Util.webServiceHelp.OnServiceCallBack;
import com.huishangyun.model.Methods;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.swipelistview.MyXListView.MyXListViewListener;
import com.huishangyun.yun.R;

public class MyOnlookersActivivty extends BaseActivity implements MyXListViewListener{
	
	private LinearLayout back;//返回
	private MyXListView mlistview;
    private BaseAdapter adapter;
    private Animation animation;//动画
    private webServiceHelp<OnLooksList> webHelp;
    private webServiceHelp<T> webHelpPraise;
    private webServiceHelp<T> webHelpDelete;//删除围观操作
    private List<OnLooksList> mList = new ArrayList<OnLooksList>();
    private static final int GET_LIST_SUCCESS = 0x0001;//列表数据返回成功
    private static final int GET_LIST_FAIL = 0x0002;//接口访问失败
    private static final int GET_LIST_BUG = 0x000;//返回错误码
    private static final int DELETE_SUCCESS = 0x0003;
    private int PagerIndex = 1;
    private int PagerSize = 5;
    private List<OnLooksList> cList = new ArrayList<OnLooksList>();
    private int requestCode = 0x0004;
    private ImageView no_information;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_office_onlooks_my);
		init();
	}
	
	
	/**
	 * 初始化
	 */
	private void init(){
		webHelpPraise = new webServiceHelp<T>(Methods.SET_ONLOOKS_PRAISE, new TypeToken<ZJResponse<T>>(){}.getType());
		webHelpPraise.setOnServiceCallBack(onLooksPraise);
		webHelpDelete = new webServiceHelp<T>(Methods.ONLOOKS_DELETE_MYSELF, new TypeToken<ZJResponse<T>>(){}.getType());
		webHelpDelete.setOnServiceCallBack(onLooksDelete);
		animation = AnimationUtils.loadAnimation(this, R.anim.office_onlooks_praise);
		back = (LinearLayout) findViewById(R.id.back);
		mlistview = (MyXListView) findViewById(R.id.mlistview);
		no_information = (ImageView) findViewById(R.id.no_information);
		adapter = new mAdapter(this,mList);
		mlistview.setAdapter(adapter);
		mlistview.setPullLoadEnable(true);
		mlistview.setMyXListViewListener(this);
		MyApplication.getInstance().showDialog(MyOnlookersActivivty.this, true, "Loading...");
		webHelp = new webServiceHelp<OnLooksList>(Methods.GET_ONLOOKS_LIST, new TypeToken<ZJResponse<OnLooksList>>(){}.getType());
		webHelp.setOnServiceCallBack(onLooksList);
		L.e("我的围观：" + getListJson(1,5));
		webHelp.start(getListJson(1,5));
		back.setOnClickListener(onClick);
		mlistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				int ID = (mList.get(position - 1).getID()).intValue();
				Intent intent = new Intent(MyOnlookersActivivty.this,OnLooksDetails.class);
				intent.putExtra("Tittle", "围观详情");
				intent.putExtra("ID", ID);
				intent.putExtra("canSee", true);
				startActivityForResult(intent, requestCode);
			}
		});
		
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		webHelp.removeOnServiceCallBack();
		webHelpPraise.removeOnServiceCallBack();
		webHelpDelete.removeOnServiceCallBack();
		super.onDestroy();
	}
	
	/**
	 * 获取列表数据回调接口
	 */
	private OnServiceCallBack<OnLooksList> onLooksList = new OnServiceCallBack<OnLooksList>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<OnLooksList> zjResponse) {
			// TODO Auto-generated method stub
			if (haveCallBack && zjResponse != null) {
				switch (zjResponse.getCode()) {
				case 0:
					//访问成功
					cList = zjResponse.getResults();
					handler.sendEmptyMessage(GET_LIST_SUCCESS);
					break;
				default:
					//错误码
					handler.sendEmptyMessage(GET_LIST_BUG);
					break;
				}
			} else {
				//接口访问失败
				handler.sendEmptyMessage(GET_LIST_FAIL);
			}
		}
	};
	
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
					L.e("本条围观删除成功！刷新界面");
					handler.sendEmptyMessage(DELETE_SUCCESS);
					break;
				default:
					//错误码
					handler.sendEmptyMessage(GET_LIST_BUG);
					break;
				}
			} else {
				//接口访问失败
				handler.sendEmptyMessage(GET_LIST_FAIL);
			}
		}
	};
	
	/**
	 * 列表json对象
	 * @param PageIndex 页码
	 * @param PageSize 单页条数
	 * @return
	 */
	private String getListJson(int PageIndex,int PageSize) {
		ZJRequest<T> zjRequest = new ZJRequest<T>();
		zjRequest.setManager_ID(MyApplication.getInstance().getManagerID());
		zjRequest.setPageIndex(PageIndex);
		zjRequest.setPageSize(PageSize);
		return JsonUtil.toJson(zjRequest);

	}
	
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
	 * 更新ui
	 */
	private Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_LIST_SUCCESS:
				if (PagerIndex != 1) {
					if (cList.size()>0) {
						for (int i = 0; i < cList.size(); i++) {
							mList.add(cList.get(i));
						}
						adapter.notifyDataSetChanged();
					}else {
						showToast(false, "没有更多数据！");
					}
				}else {
					
					if (cList.size()>0) {
						mList.clear();					
					}
					
					for (int i = 0; i < cList.size(); i++) {
						mList.add(cList.get(i));
					}
					adapter.notifyDataSetChanged();
				}
				MyApplication.getInstance().showDialog(MyOnlookersActivivty.this, false, "Loading...");
				if (mList.size()<=0) {
					no_information.setVisibility(View.VISIBLE);
				}else {
					no_information.setVisibility(View.GONE);
				}
				break;
			case GET_LIST_FAIL:
				showToast(false, "未获得任何数据，请检查网络连接！");
				break;
			case GET_LIST_BUG:
				showToast(false, "错误码！");
				break;
			case DELETE_SUCCESS:
				showToast(true, "围观删除成功！");
				 PagerIndex = 1;
				 webHelp.start(getListJson(PagerIndex,PagerSize));
				break;
			default:
				break;
			}
		};
	};
	 
	
	
	private OnClickListener onClick = new OnClickListener() {
		private Intent mIntent;
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.back://返回
				finish();
				break;
			
			default:
				break;
			}
		}
		
	};
	
	
	/**
	 * listview
	 * 适配器
	 * @author xsl
	 *
	 */
	public class mAdapter extends BaseAdapter{

		private Context context;
		private LayoutInflater inflater;
		private List<OnLooksList> list;
		public mAdapter(Context context,List<OnLooksList> list){
			this.context = context;
			inflater = LayoutInflater.from(context);
			this.list = list;
		}
		
		public  void Updata(){
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.activity_office_onlooks_main_item, null);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
				holder.senddateTime = (TextView) convertView.findViewById(R.id.senddateTime);
				holder.belongDepartment = (TextView) convertView.findViewById(R.id.belongDepartment);
				holder.content = (TextView) convertView.findViewById(R.id.content);
				holder.mLayout = (LinearLayout) convertView.findViewById(R.id.mLayout);
				holder.cmLayout = (LinearLayout) convertView.findViewById(R.id.cmLayout);
				holder.comment = (TextView) convertView.findViewById(R.id.comment);
				holder.pLayout = (LinearLayout) convertView.findViewById(R.id.pLayout);
				holder.praise = (TextView) convertView.findViewById(R.id.praise);
				holder.deleteline = (View) convertView.findViewById(R.id.deleteline);
				holder.img_del = (ImageView) convertView.findViewById(R.id.img_del);
				holder.locationLayout = (LinearLayout) convertView.findViewById(R.id.locationLayout);
				holder.locationAddress = (TextView) convertView.findViewById(R.id.locationAddress);
				holder.showLine = (View) convertView.findViewById(R.id.showLine);
				holder.gridview = (GridView) convertView.findViewById(R.id.gridview);
				holder.imageBig = (ImageView) convertView.findViewById(R.id.imageBig);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			if (list.get(position).getShowGps()) {
				holder.locationLayout.setVisibility(View.VISIBLE);
				holder.showLine.setVisibility(View.VISIBLE);
				holder.locationAddress.setText(list.get(position).getLoc());
			}else {
				holder.showLine.setVisibility(View.GONE);
				holder.locationLayout.setVisibility(View.GONE);
			}
			holder.deleteline.setVisibility(View.VISIBLE);
			holder.img_del.setVisibility(View.VISIBLE);
			String person_url = Constant.pathurl+
	                MyApplication.getInstance().getCompanyID()+"/Photo/" + list.get(position).getPhoto();
			new ImageLoad().displayImage(context, person_url, holder.icon, R.drawable.defaultimage02, 0, false);
			holder.nickname.setText(list.get(position).getRealName());
			holder.senddateTime.setText(backdate(list.get(position).getAddDateTime()));
			holder.belongDepartment.setText(list.get(position).getSign());
			holder.content.setText("");
			if (!list.get(position).getTopic().equals("")) { // 判断是否有话题
				SpannableString spanText = new SpannableString(list.get(position).getTopic());
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
						L.e("点击了话题:" + list.get(position).getTopic());
						//showCustomToast("点击了话题:" + list.get(position).getTopic(), true);
						Intent intent = new Intent(MyOnlookersActivivty.this, TopOnlookActivity.class);
						intent.putExtra("Topic", list.get(position).getTopic());
						startActivity(intent);
						
					}
					
				}, 0, spanText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				
				holder.content.append(spanText);
			}
			/*SpannableString spanText = new SpannableString(list.get(position).getTopic());
			spanText.setSpan(new ForegroundColorSpan(Color.parseColor("#21a5de")), 0, spanText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			spanText.setSpan(new RelativeSizeSpan(1.0f), 0, spanText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			holder.content.append(spanText);*/
			holder.content.append(FaceUtil.convertNormalStringToSpannableStringIntelligence(context, list.get(position).getNote(), 19, true));
			holder.content.setMovementMethod(LinkMovementMethod.getInstance());//设置超链接为可点击状态
			holder.comment.setText(list.get(position).getComment()+"");
	    	holder.praise.setText(list.get(position).getGood()+"");
			
		    

		    int indexImg = 0;
		    int indexFile = 0;
		    final List<String> allData = new ArrayList<String>();
			String[] strImage =  (list.get(position).getAttachmentPath()).split("#");
			String[]  strFile =  (list.get(position).getAttachment()).split("#");
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
				holder.showLine.setVisibility(View.GONE);
				holder.imageBig.setVisibility(View.VISIBLE);
				holder.gridview.setVisibility(View.GONE);
				String ImageUrl =Constant.pathurl+
		                MyApplication.getInstance().getCompanyID()+"/Action/500x500/" + allData.get(0);
				new ImageLoad().displayImage(context, ImageUrl, holder.imageBig, R.drawable.defaultimage02, 0, false);
				holder.imageBig.setOnClickListener(new OnClickListener() {
					
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
				holder.showLine.setVisibility(View.GONE);
				holder.gridview.setVisibility(View.VISIBLE);
				holder.imageBig.setVisibility(View.GONE);
				mGirdViewBaseAdapter girdViewAdapter = new mGirdViewBaseAdapter(context,allData,indexImg);
				holder.gridview.setAdapter(girdViewAdapter);
			}
			
			holder.cmLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(MyOnlookersActivivty.this,OnLooksDetails.class);
					intent.putExtra("Tittle", "围观评论");
					intent.putExtra("ID", list.get(position).getID());
					intent.putExtra("canSee", true);
					startActivityForResult(intent, requestCode);
				}
			});
			
			holder.pLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!holder.isPraise) {
						Drawable drawable= getResources().getDrawable(R.drawable.onlooks_praise);
						/// 这一步必须要做,否则不会显示.
						drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
						holder.praise.setCompoundDrawables(drawable,null,null,null);
						holder.praise.setText((Integer.parseInt(holder.praise.getText().toString().trim()) + 1)+"");
						holder.isPraise = true;
						webHelpPraise.start(getPraiseJson(list.get(position).getID(),true));
						L.e("点赞json：" + getPraiseJson(list.get(position).getID(),true));
						
					}else {
						Drawable drawable= getResources().getDrawable(R.drawable.onlooks_unpraise);
						/// 这一步必须要做,否则不会显示.
						drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
						holder.praise.setCompoundDrawables(drawable,null,null,null);
						holder.praise.setText((Integer.parseInt(holder.praise.getText().toString().trim()) - 1)+"");
						holder.isPraise = false;
						webHelpPraise.start(getPraiseJson(list.get(position).getID(),false));
						L.e("点赞json：" + getPraiseJson(list.get(position).getID(),false));
					}
					holder.praise.startAnimation(animation);
				}
			});
			
			holder.img_del.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//删除本条围观
					L.e("deleteJson:" + deleteJson(list.get(position).getID()));
					webHelpDelete.start(deleteJson(list.get(position).getID()));
					
				}
			});
			
			return convertView;
		}
     
	}
	
	public static class ViewHolder{
		private ImageView icon;//头像
		private TextView nickname;//昵称
		private TextView senddateTime;//发表时间
		private TextView belongDepartment;//来自猩猩的你
		private TextView content;//内容
		private LinearLayout mLayout;//动态添加控件布局
		private LinearLayout cmLayout;//评论布局
		private TextView comment;//评论
		private LinearLayout pLayout;//点赞布局
		private TextView praise;//点赞
		private boolean isPraise;
		private View deleteline;
		private ImageView img_del;//删除
		private LinearLayout locationLayout;
		private TextView locationAddress;
		private View showLine;
		private GridView gridview;
		private ImageView imageBig;
    }

	
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mlistview.stopLoadMore();
				mlistview.stopRefresh();
				mlistview.setRefreshTime();
			}
		}, 2000);
		 PagerIndex = 1;
		 webHelp.start(getListJson(PagerIndex,5));
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
         handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mlistview.stopLoadMore();
				mlistview.stopRefresh();
				mlistview.setRefreshTime();
			}
		}, 2000);
        PagerIndex += 1;
        PagerSize += 5;
      	webHelp.start(getListJson(PagerIndex,5));
      
	}
	
	/**
	 * 自定义Toast显示
	 * @param isS 
	 * @param msg 提示内容
	 */
	private void showToast(boolean isS,String msg){
		if (isS) {
			 ClueCustomToast.showToast(MyOnlookersActivivty.this,
					R.drawable.toast_sucess, msg);
		}else {
			 ClueCustomToast.showToast(MyOnlookersActivivty.this,
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
			//刷新列表
			 PagerIndex = 1;
			 webHelp.start(getListJson(PagerIndex,5));
		}
		super.onActivityResult(arg0, arg1, arg2);
	}
	
}
