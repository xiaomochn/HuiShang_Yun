package com.huishangyun.Office.WeiGuan;


import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Util.FaceUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.T;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Office.Summary.ImageLoad;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.swipelistview.MyXListView.MyXListViewListener;
import com.huishangyun.yun.R;

/**
 * 我的围观评论
 * @author xsl
 *
 */
public class MyOnLooksComment extends BaseActivity implements MyXListViewListener {

	private LinearLayout back;//返回
	private MyXListView mlistview;
	private BaseAdapter adapter;
	private webServiceHelp<OnLooksList> webHelp;
	private webServiceHelp<T> webDeleteComment;//删除评论操作
	private static final int GET_LIST_SUCCESS = 0x0001;//列表数据返回成功
    private static final int GET_LIST_FAIL = 0x0002;//接口访问失败
    private static final int GET_LIST_BUG = 0x000;//返回错误码
    private static final int DELETE_COMMENT_SUCCESS = 0x0003;
    private List<OnLooksList> mList = new ArrayList<OnLooksList>();
    private int PagerIndex = 1;
    private int PagerSize = 5;
    private List<OnLooksList> cList = new ArrayList<OnLooksList>();
    private ImageView no_information;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_office_onlooks_mycomment);
		init();
	}
	
	
	/**
	 * 初始化
	 */
	private void init() {
		back = (LinearLayout) findViewById(R.id.back);
		mlistview = (MyXListView) findViewById(R.id.mlistview);
		no_information = (ImageView) findViewById(R.id.no_information);
		MyApplication.getInstance().showDialog(MyOnLooksComment.this, true, "Loading...");
		webHelp = new webServiceHelp<OnLooksList>(Methods.GET_ONLOOKS_MY_COMMENT, new TypeToken<ZJResponse<OnLooksList>>(){}.getType());
		webHelp.setOnServiceCallBack(onLooksList);
		webHelp.start(getListJson(1, 5));
		webDeleteComment = new webServiceHelp<T>(Methods.ONLOOKS_DELETE_MYSELF_COMMENT, new TypeToken<ZJResponse<T>>(){}.getType());
		webDeleteComment.setOnServiceCallBack(deleteComment);
		adapter = new mAdapter(this,mList);
		mlistview.setAdapter(adapter);
		mlistview.setPullLoadEnable(true);
		mlistview.setMyXListViewListener(this);
		back.setOnClickListener(onClick);
		mlistview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				int ID = (mList.get(position - 1).getID()).intValue();
				Intent intent = new Intent(MyOnLooksComment.this,OnLooksDetails.class);
				intent.putExtra("Tittle", "围观详情");
				intent.putExtra("ID", ID);
				intent.putExtra("canSee", false);
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		webDeleteComment.removeOnServiceCallBack();
		super.onDestroy();
	}
	
	/**
	 * 获取列表数据回调接口
	 */
	private webServiceHelp.OnServiceCallBack<OnLooksList> onLooksList = new webServiceHelp.OnServiceCallBack<OnLooksList>() {

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
	 *删除评论接口
	 */
	private webServiceHelp.OnServiceCallBack<T> deleteComment = new webServiceHelp.OnServiceCallBack<T>() {

		@Override
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<T> zjResponse) {
			// TODO Auto-generated method stub
			if (haveCallBack && zjResponse != null) {
				switch (zjResponse.getCode()) {
				case 0:
					//访问成功
				    L.e("评论删除成功！");
				    handler.sendEmptyMessage(DELETE_COMMENT_SUCCESS);
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
	 * 删除评论json
	 * @param ID 评论id
	 * @return
	 */
	private String deleteCommentJson(int ID) {
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
				MyApplication.getInstance().showDialog(MyOnLooksComment.this, false, "Loading...");
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
			case DELETE_COMMENT_SUCCESS:
				showToast(true, "评论删除成功！");
				PagerIndex = 1;
				webHelp.start(getListJson(PagerIndex, PagerSize));
				break;
			default:
				break;
			}
		};
	};
	
	private OnClickListener onClick = new OnClickListener() {
		
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
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.activity_office_onlooks_mycomment_item, null);
				holder.delete = (ImageView) convertView.findViewById(R.id.delete);
				holder.senddateTime = (TextView) convertView.findViewById(R.id.senddateTime);
				holder.mcomment = (TextView) convertView.findViewById(R.id.mcomment);
				holder.belongDepartment = (TextView) convertView.findViewById(R.id.belongDepartment);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.content = (TextView) convertView.findViewById(R.id.content);
				holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			String person_url = Constant.pathurl+
	                MyApplication.getInstance().getCompanyID()+"/Photo/" + list.get(position).getPhoto();
			new ImageLoad().displayImage(context, person_url, holder.icon, R.drawable.defaultimage02, 0, false);
			holder.senddateTime.setText(backdate(list.get(position).getAddDateTime()));
			SpannableString spanText = new SpannableString(list.get(position).getTopic());
			spanText.setSpan(new ForegroundColorSpan(Color.parseColor("#21a5de")), 0, spanText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			spanText.setSpan(new RelativeSizeSpan(1.0f), 0, spanText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			holder.content.append(spanText);
			holder.content.setText(FaceUtil.convertNormalStringToSpannableStringIntelligence(context, list.get(position).getAction_Note(), 19, true));
			holder.mcomment.setText("");//初始化
			holder.mcomment.append(FaceUtil.convertNormalStringToSpannableStringIntelligence(context, list.get(position).getNote(),16, true));
			holder.belongDepartment.setText(list.get(position).getSign());
			holder.nickname.setText(list.get(position).getRealName());
			
			holder.delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//删除该条评论操作
					webDeleteComment.start(deleteCommentJson(list.get(position).getID()));
					L.e("删除评论：" + deleteCommentJson(list.get(position).getID()));
				}
			});
		
			return convertView;
		}
     
	}
	
	public static class ViewHolder{
		private ImageView delete;//删除
		private TextView senddateTime;//评论时间
		private TextView mcomment;//我的评论
		private TextView belongDepartment;//来自猩猩的你
		private ImageView icon;//被评论人的头像
		private TextView content;//围观内容
		private TextView nickname;//被评论人昵称
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
		webHelp.start(getListJson(PagerIndex, 5));
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
 		webHelp.start(getListJson(PagerIndex, 5));
	}

	/**
	 * 自定义Toast显示
	 * @param isS 
	 * @param msg 提示内容
	 */
	private void showToast(boolean isS,String msg){
		if (isS) {
			 ClueCustomToast.showToast(MyOnLooksComment.this,
					R.drawable.toast_sucess, msg);
		}else {
			 ClueCustomToast.showToast(MyOnLooksComment.this,
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
	
}
