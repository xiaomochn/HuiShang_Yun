package com.huishangyun.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gotye.api.GotyeUser;
import com.huishangyun.ImageLoad.ImageLoader;
import com.huishangyun.Office.Businesstrip.ImagePagerActivity;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.View.RoundAngleImageView;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Members;
import com.huishangyun.yun.R;
import com.huishangyun.App.MyApplication;
import com.huishangyun.model.Content;

/**
 * 客户页面
 * @author Pan
 *
 */
public class PrivateData extends BaseMainActivity{
	private String JID;
	private String Name;
	private String imgUrl = "";
	private LinearLayout backLayout;//返回
	private LinearLayout chatLayout;
	private ImageView userImg;
	private TextView userName;
	private TextView backName;
	private TextView companyName;
	private TextView infoName;
	private TextView dutyName;//职务
	private TextView telNumber;
	private TextView phoneNumber;
	private TextView addressTxt;
	private TextView E_mail;
	private TextView noteTxt;
	private LinearLayout call_phone;
	private LinearLayout call_messages;
	private LinearLayout call_chat;
	private Members members;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_pridata);
		JID = getIntent().getStringExtra("JID");
		initView();
		initListener();
		getUserInfo(JID);
	}
	
	/**
	 * 实例化各组件
	 */
	private void initView(){
		backLayout = (LinearLayout) findViewById(R.id.info_back);
		chatLayout = (LinearLayout) findViewById(R.id.info_chat);
		userImg = (ImageView) findViewById(R.id.pridata_user_img);
		userName = (TextView) findViewById(R.id.pridata_user_name);
		companyName = (TextView) findViewById(R.id.pridata_company_name);
		infoName = (TextView) findViewById(R.id.pridata_info_name);
		dutyName = (TextView) findViewById(R.id.pridata_duty);
		telNumber = (TextView) findViewById(R.id.pridata_num_tel);
		phoneNumber = (TextView) findViewById(R.id.pridata_num_phone);
		addressTxt = (TextView) findViewById(R.id.pridata_address);
		E_mail = (TextView) findViewById(R.id.pridata_e_mail);
		noteTxt = (TextView) findViewById(R.id.pridata_note);
		backName = (TextView) findViewById(R.id.info_title_txt);
		Name = getIntent().getStringExtra("Name");
		backName.setText(getIntent().getStringExtra("Name"));
		call_chat = (LinearLayout) findViewById(R.id.call_chat);
		call_phone = (LinearLayout) findViewById(R.id.call_phone);
		call_messages = (LinearLayout) findViewById(R.id.call_messages);
		call_chat.setOnClickListener(mClickListener);
		call_messages.setOnClickListener(mClickListener);
		call_phone.setOnClickListener(mClickListener);
		userImg.setOnClickListener(mClickListener);
	}
	
	/**
	 * 添加监听事件
	 */
	private void initListener() {
		backLayout.setOnClickListener(mClickListener);
		chatLayout.setOnClickListener(mClickListener);
	}
	
	/**
	 * 点击事件监听
	 */
	private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.info_back:
				finish();
				break;
				
			case R.id.info_chat:
				Intent intent2 = new Intent(PrivateData.this, CharHistory.class);
				intent2.putExtra("to", JID);
				intent2.putExtra("name", Name);
				intent2.putExtra("type", 0);
				intent2.putExtra("isGroup", false);
				startActivity(intent2);
				break;
				
			case R.id.call_chat:
				Intent chaIntent = new Intent(PrivateData.this, Chat.class);
				chaIntent.putExtra("JID", JID);
				chaIntent.putExtra("type", 2);
				chaIntent.putExtra("name", Name);
				chaIntent.putExtra("messtype", 0);
				chaIntent.putExtra("chat_name", Name);
				chaIntent.putExtra("Sign", "");
				GotyeUser gotyeUser = new GotyeUser(JID);
				chaIntent.putExtra("user", gotyeUser);
				startActivity(chaIntent);
				finish();
				break;
			case R.id.call_messages:
				if (members != null) {
					Intent intent = new Intent();
					//系统默认的action，用来打开默认的短信界面
					intent.setAction(Intent.ACTION_SENDTO);
					//需要发短息的号码
					intent.setData(Uri.parse("smsto:" + members.getMobile()));
					startActivity(intent);
				} else {
					showCustomToast("无法获取联系人资料", false);
				}
				
				break;

			case R.id.call_phone:
				if (members != null) {
					Intent intent3 = new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + members.getMobile())); 
					intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					//启动
					startActivity(intent3);
				} else {
					showCustomToast("无法获取联系人资料", false);
				}
				
				break;
				case R.id.pridata_user_img:
					Intent aintent = new Intent(PrivateData.this, ImagePagerActivity.class);
					// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
					aintent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{Constant.pathurl+
							MyApplication.getInstance().getCompanyID() + "/Photo/" + imgUrl});
					aintent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
					startActivity(aintent);
					break;



			default:
				break;
			}
		}
	};
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				members = (Members) msg.obj;
				userName.setText(members.getContact());
				companyName.setText(members.getRealName());
				infoName.setText(members.getContact());
				dutyName.setText("");
				telNumber.setText("");
				phoneNumber.setText(members.getMobile());
				addressTxt.setText(members.getAddress());
				E_mail.setText("");
				noteTxt.setText(members.getNote());
				userImg.setImageResource(R.drawable.contact_person);
				/*new ImageLoader(PrivateData.this).DisplayImage("http://img.huishangyun.com/UploadFile/huishang/"+
						MyApplication.preferences.getInt(Content.COMPS_ID, 1016)+"/Photo/" + members.getPhoto(), userImg, false);*/
				imgUrl = members.getPhoto();
				com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(Constant.pathurl+
						MyApplication.getInstance().getCompanyID() +"/Photo/" + members.getPhoto(),userImg, MyApplication.getInstance().getOptions());
				break;
				
			case HanderUtil.case2:
				showCustomToast("获取资料失败,请更新数据!", false);
				break;

			default:
				break;
			}
		};
	};
	
	private void getUserInfo(String OFUserName) {
		new Thread(new GetUserInfo(OFUserName)).start();
	}
	
	private class GetUserInfo implements Runnable {
		private String OFUserName;
		public GetUserInfo(String OFUserName) {
			this.OFUserName = OFUserName;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Members members = MemberManager.getInstance(PrivateData.this).getMemberInfo(OFUserName);
			if (members != null) {
				Message msg = new Message();
				msg.obj = members;
				msg.what = HanderUtil.case1;
				mHandler.sendMessage(msg);
			} else {
				mHandler.sendEmptyMessage(HanderUtil.case2);
			}
		}
		
	}
}
