package com.huishangyun.Activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.google.gson.reflect.TypeToken;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeUser;
import com.huishangyun.App.MyApplication;
import com.huishangyun.GIF.MyTextViewEx;
import com.huishangyun.Util.FileTools;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ServiceUtil;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.Util.WebHelp;
import com.huishangyun.manager.DepartmentManager;
import com.huishangyun.manager.MessageManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.IMMessage;
import com.huishangyun.model.MessageData;
import com.huishangyun.model.MessageType;
import com.huishangyun.yun.R;
import com.huishangyun.ImageLoad.ImageLoader;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.model.Content;

public class CharHistory extends BaseMainActivity{
	private List<IMMessage> msgList;
	private List<IMMessage> searchList;
	private MessageManager msgManager;
	private ListView listView;
	private MsgHisListAdapter adapter;
	private MsgHisListAdapter searchAdapter;
	private String to;
	private int pageSize = 10;
	private int currentPage = 1;
	private int pageCount;// 总页数
	private int recordCount;// 记录总数
	private ImageView imageViewLeft;// 上一页
	private ImageView imageViewRight;// 上一页
	private EditText editTextPage;// 当前页
	private ImageView delBtn;
	private TextView textViewPage;// 总页数
	private String data ;
	private int messtype;
	private MediaPlayer mPlayer = null;
	private EditText et_Keyword;
	private ListView listViewSearch;
	private AlertDialog alertDialog;
	private GotyeGroup group;
	private GotyeUser user;
	private boolean isGroup = false;
	
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chathistory);
		init();
		closeInput();
	};
	
	private void init(){
		to = getIntent().getStringExtra("to");
		data = getIntent().getStringExtra("name");
		messtype = getIntent().getIntExtra("type", 0);
		isGroup = getIntent().getBooleanExtra("isGroup", false);
		msgManager = MessageManager.getInstance(context);
		initBackTitle(data);
		if (to == null)
			return;
		recordCount = MessageManager.getInstance(context)
				.getChatCountWithSb(to, isGroup);
		pageCount = (recordCount + pageSize - 1) / pageSize;
		imageViewLeft = (ImageView) findViewById(R.id.imageViewLeft);
		imageViewRight = (ImageView) findViewById(R.id.imageViewRight);
		editTextPage = (EditText) findViewById(R.id.editTextPage);
		et_Keyword = (EditText) findViewById(R.id.et_search_keyword);
		et_Keyword.addTextChangedListener(mTextWatcher);
		editTextPage.setText(currentPage + "");
		// 下一页
		imageViewRight.setOnClickListener(nextClick);
		// 上一页
		imageViewLeft.setOnClickListener(preClick);
		// 总页数
		textViewPage = (TextView) findViewById(R.id.textViewPage);
		textViewPage.setText("" + pageCount);
		if (pageCount == 1) {
			imageViewLeft.setImageResource(R.drawable.chatrecord_leftclick_01);
			imageViewRight.setImageResource(R.drawable.chatrecord_rightclick);
		} else {
			imageViewLeft.setImageResource(R.drawable.chatrecord_leftclick_01);
		}
		
		// 删除
		delBtn = (ImageView) findViewById(R.id.buttonDelete);
		
		if (recordCount == 0) {
			delBtn.setClickable(false);
			delBtn.setImageResource(R.drawable.chatrecord_nowrite);
		} else {
			delBtn.setClickable(true);
			delBtn.setImageResource(R.drawable.chatrecord_delete);
			delBtn.setOnClickListener(delClick);
		}
		listView = (ListView) findViewById(R.id.listViewHistory);
		msgList = msgManager.getMessageListByFrom(to, currentPage, pageSize, isGroup);
		searchList = new ArrayList<IMMessage>();
		if (msgList != null && msgList.size() > 0) {
			Collections.sort(msgList);
			adapter = new MsgHisListAdapter(context, msgList);
			listView.setAdapter(adapter);
		}
		searchAdapter = new MsgHisListAdapter(context, searchList);
		listView.setOnScrollListener(listener);
		listViewSearch = (ListView) findViewById(R.id.listViewSearch);
		listViewSearch.setAdapter(searchAdapter);
		editTextPage.addTextChangedListener(editTextWatcher);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		FileTools.stopPlaying(null, null);
		super.onDestroy();
	}

	private class MsgHisListAdapter extends BaseAdapter {

		private List<IMMessage> items;
		private Context context;
		private LayoutInflater inflater;

		public MsgHisListAdapter(Context context, List<IMMessage> items) {
			this.context = context;
			this.items = items;

		}

		public void refreshList(List<IMMessage> items) {
			Collections.sort(items);
			this.items = items;
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return items == null ? 0 : items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			inflater = LayoutInflater.from(context);
			final IMMessage message = items.get(position);
			final Holder holder;
			if (convertView == null) {
				holder = new Holder();
                /*if (message.getMsg_Category().equals(MessageType.MESSAGE_SERVICE)) {*/
				convertView = this.inflater.inflate(R.layout.chathistoryitem,
						null);
				holder.name = (TextView) convertView
						.findViewById(R.id.tvHistoryName);
				holder.time = (TextView) convertView
						.findViewById(R.id.tvHistoryTime);
				holder.content = (MyTextViewEx) convertView
						.findViewById(R.id.tvMsgItem);
				holder.msgimg = (ImageView) convertView.findViewById(R.id.chat_History_msgimg);
				holder.record = (LinearLayout) convertView.findViewById(R.id.chat_History_record);
				holder.player = (ImageView) convertView.findViewById(R.id.chat_History_player);
				holder.file = (LinearLayout) convertView.findViewById(R.id.chat_History_file);
				holder.filename = (TextView) convertView.findViewById(R.id.chat_History_filename);
				//服务号部分
				holder.service_linear = (LinearLayout) convertView.findViewById(R.id.chat_history_linea);
				holder.service_time = (TextView) convertView.findViewById(R.id.chat_history_times);
				holder.service_title = (TextView) convertView.findViewById(R.id.chat_history_titles);
				holder.service_img= (ImageView) convertView.findViewById(R.id.chat_history_imgs);
				holder.service_body = (TextView) convertView.findViewById(R.id.chat_history_bodys);
				//服务号task部分
				holder.chat_hostroy_lins = (RelativeLayout) convertView.findViewById(R.id.chat_hostroy_lins);
				holder.chat_service_time = (TextView) convertView.findViewById(R.id.chat_service_time);
				holder.chat_hostroy_FlowName = (TextView) convertView.findViewById(R.id.chat_hostroy_FlowName);
				holder.chat_hostroy_title = (TextView) convertView.findViewById(R.id.chat_hostroy_title);
				holder.chat_hostroy_date= (TextView) convertView.findViewById(R.id.chat_hostroy_date);
				holder.chat_hostroy_image = (ImageView) convertView.findViewById(R.id.chat_hostroy_image);
				//时间 易注释
//					holder.service_date = (TextView) convertView.findViewById(R.id.chat_service_date);
				holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.chathistory_rel);
				convertView.setTag(holder);
                /*}else {
                    convertView = this.inflater.inflate(R.layout.chathistoryitem,
							null);

					convertView.setTag(holder);
				}*/


			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.name.setText(message.getFromSubJid());
			if (message.getMsgType() == 1) {
				long longtime = TimeUtil.getLongtime(message.getTime());
				holder.time.setText(TimeUtil.getChatTime(longtime));
				holder.name.setText("我");
			}
			try {
				final MessageData<String> messageData = JsonUtil.fromJson(message.getContent(), new TypeToken<MessageData<String>>() {
				}.getType());
				holder.service_linear.setVisibility(View.GONE);
				holder.relativeLayout.setVisibility(View.VISIBLE);
				if (message.getMsgType() == 0) {
					long longtime = TimeUtil.getLongtime(message.getTime());
					holder.time.setText(TimeUtil.getChatTime(longtime));
					String name = DepartmentManager.getInstance(context).getManager(message.getFromSubJid());
					if (name == null) {
						name = MemberManager.getInstance(context).getMember(message.getFromSubJid());
						if (name == null) {
							name = message.getFromSubJid();
						}
					}
					holder.name.setText(name);
				}
				
				//判断是否为录音
				if (messageData.getMessageCategory().equals(MessageType.MESSAGE_VIDEO)) {
					//录音
					holder.content.setVisibility(View.GONE);
					holder.file.setVisibility(View.GONE);
					holder.record.setVisibility(View.VISIBLE);
					holder.msgimg.setVisibility(View.GONE);
					//下载录音并添加点击事件监听
					FileTools.decodeRecording(Constant.pathurl+
							MyApplication.getInstance().getCompanyID() +"/Chat/" + messageData.getMessageContent(),
							false, holder.record, holder.player);
					
				} else if (messageData.getMessageCategory().equals(MessageType.MESSAGE_PHOTO)) {
					//照片
					holder.content.setVisibility(View.GONE);
					holder.file.setVisibility(View.GONE);
					holder.record.setVisibility(View.GONE);
					holder.msgimg.setVisibility(View.VISIBLE);
					holder.msgimg.setImageResource(R.drawable.chat_service_load);
					//加载图片
					/*new ImageLoader(Chat.this).DisplayImage("http://img.huishangyun.com/UploadFile/huishang/"+
							MyApplication.getInstance().getCompanyID() +"/Chat/" + message.getContent(), holder.msgimg, false);*/
					FileTools.decodeImage(Constant.pathurl+
							MyApplication.getInstance().getCompanyID() +"/Chat/" + messageData.getMessageContent(), holder.msgimg, CharHistory.this);
					//显示缩略图
					com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(Constant.pathurl+
							MyApplication.getInstance().getCompanyID() +"/Chat/100x100/" + messageData.getMessageContent(), holder.msgimg);
					
				} else if (messageData.getMessageCategory().equals(MessageType.MESSAGE_FILE)) {
					//文件
					holder.content.setVisibility(View.GONE);
					holder.file.setVisibility(View.VISIBLE);
					holder.record.setVisibility(View.GONE);
					holder.msgimg.setVisibility(View.GONE);
					//设置相应数据并添加点击事件
					holder.filename.setText(messageData.getMessageContent().split("#")[0]);
					holder.file.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							FileTools.decodeFile(Constant.pathurl +
									MyApplication.getInstance().getCompanyID() + "/Chat/" + messageData.getMessageContent().split("#")[1], CharHistory.this);
						}
					});
				} else {
					//普通消息
					holder.content.setVisibility(View.VISIBLE);
					holder.file.setVisibility(View.GONE);
					holder.record.setVisibility(View.GONE);
					holder.msgimg.setVisibility(View.GONE);
					/*holder.content.setText(FaceUtil
							.convertNormalStringToSpannableString(CharHistory.this,
									message.getContent(), false));*/
					holder.content.insertGif(messageData.getMessageContent());
				}
			} catch (Exception e) {
				// TODO: handle exception
				MessageData<ServiceUtil> messageData = JsonUtil.fromJson(message.getContent(), new TypeToken<MessageData<ServiceUtil>>(){}.getType());

				holder.service_linear.setVisibility(View.VISIBLE);
				holder.relativeLayout.setVisibility(View.GONE);
				long longtime = TimeUtil.getLongtime(message.getTime());
				holder.service_time.setText(TimeUtil.getChatTime(longtime));
				final ServiceUtil sUtil = messageData.getMessageContent();
				holder.service_title.setText(sUtil.getTitle());
				try {
					holder.service_body.setText(sUtil.getContent());
					//holder.service_date.setText(sUtil.getSendDate());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				if (TextUtils.isEmpty(sUtil.getCover())
						|| sUtil.getCover() == null) {// 判断是否有封面
					holder.service_img.setVisibility(View.GONE);
				} else {
					holder.service_img.setImageResource(R.drawable.chat_service_load);
					com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(sUtil.getCover(), holder.service_img);
				}

				holder.service_linear.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						//这里打开webview查看详细的信息
						/*Intent intent = new Intent(CharHistory.this,ChatWebview.class);
						intent.putExtra("httpurl", sUtil.getBodyhttpurl());
						startActivity(intent);*/
						//这里打开webview查看详细的信息
						Intent intent = new Intent(CharHistory.this,ChatWebview.class);
						//intent.putExtra("httpurl", getResources().getString(R.string.chat_webview_url) + sUtil.getBodyhttpurl());
						String httpurl = getResources().getString(R.string.chat_webview_url) + "d=" + preferences.getInt(Content.COMPS_ID, 0) 
								+ "&n=" + preferences.getString(Constant.USERNAME, "") + "&p="
								+ preferences.getString(Constant.PASSWORD, "") + "&u=message/View.aspx?id=" + sUtil.getID();
						intent.putExtra("httpurl",httpurl);
						startActivity(intent);
					}
				});
			}
			return convertView;
		}

	}
	
	class Holder {
		public TextView name;
		public TextView time;
		public MyTextViewEx content;
		
		public ImageView msgimg;
		public LinearLayout record;
		public ImageView player;
		public LinearLayout file;
		public TextView filename;
		private RelativeLayout relativeLayout;
		
		//以下是服务号内容
		public LinearLayout service_linear;
		public TextView service_time;
		public TextView service_title;
		public ImageView service_img;
		public TextView service_body;
		public TextView service_date;
		//以下task服务号内容
		public RelativeLayout chat_hostroy_lins;
		public TextView chat_service_time;
		public TextView chat_hostroy_FlowName;
		public TextView chat_hostroy_title;
		public TextView chat_hostroy_date;
		public ImageView chat_hostroy_image;
	}
	
	private OnClickListener nextClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (currentPage >= pageCount) {
				return;
			}
			
			currentPage += 1;
			editTextPage.setText(currentPage + "");
			
		}
	};
	private OnClickListener preClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (currentPage <= 1) {
				return;
			}
			
			currentPage = currentPage - 1;
			editTextPage.setText(currentPage + "");
		/*	msgList = msgManager
					.getMessageListByFrom(to, currentPage, pageSize);
			adapter.refreshList(msgList);*/
		}
	};
	
	/**
	 * 删除按钮
	 */
	private OnClickListener delClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			alertDialog = new AlertDialog.Builder(CharHistory.this).setTitle("是否删除")
					.setMessage("确定删除聊天记录").setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							alertDialog.dismiss();
						}
					}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//msgManager.delChatHisWithSb(to, isGroup);
							if (isGroup) {
								GotyeGroup gotyeGroup = new GotyeGroup(Integer.parseInt(to));
								MyApplication.getInstance().getGotyeAPI().deleteSession(gotyeGroup, true);
							} else {
								GotyeUser gotyeUser = new GotyeUser(to);
								MyApplication.getInstance().getGotyeAPI().deleteSession(gotyeUser, true);
							}
							
							alertDialog.dismiss();
							//关闭历史记录页面,打开主页面
							finish();
							Intent intent = new Intent(CharHistory.this, MainActivity.class);
							startActivity(intent);
						}
					}).create();
			alertDialog.show();
			
		}
	};


	
	private int FirstPosition;
	private int LastPosition;
	private OnScrollListener listener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView paramAbsListView, int paramInt) {
			// TODO Auto-generated method stub
			paramAbsListView.getFirstVisiblePosition();
		}
		
		@Override
		public void onScroll(AbsListView paramAbsListView, int firstVisibleItem,
				int visibleItemCount, int totalItemCoun) {
			// TODO Auto-generated method stub
			FirstPosition = paramAbsListView.getFirstVisiblePosition();
			LastPosition = paramAbsListView.getLastVisiblePosition();
		}
	};
	
	/**
	 * 搜索聊天记录
	 */
	private TextWatcher mTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			if (s.length() > 0) {
				searchList = msgManager.searchMessages(to, s.toString().trim(), isGroup);
				L.e("searchList = " + searchList);
					/*searchAdapter = new  MsgHisListAdapter(context, searchList);
					listViewSearch.setAdapter(searchAdapter);*/
				searchAdapter.refreshList(searchList);
				
				listView.setVisibility(View.GONE);
				listViewSearch.setVisibility(View.VISIBLE);
			} else {
				listView.setVisibility(View.VISIBLE);
				listViewSearch.setVisibility(View.GONE);
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private TextWatcher editTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			if (s.length() > 0 && !s.toString().equals("0") && Integer.parseInt(s.toString()) <= pageCount) {
				currentPage = Integer.parseInt(s.toString());
				if (currentPage == 1) {
					imageViewLeft.setImageResource(R.drawable.chatrecord_leftclick_01);
				} else {
					imageViewLeft.setImageResource(R.drawable.chatrecord_leftclick);
				}
				
				if (currentPage == pageCount) {
					imageViewRight.setImageResource(R.drawable.chatrecord_rightclick);
				} else {
					imageViewRight.setImageResource(R.drawable.chatrecord_rightclick_01);
				}
				msgList = msgManager
						.getMessageListByFrom(to, currentPage, pageSize, isGroup);
				adapter.refreshList(msgList);
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
	};
	
}
