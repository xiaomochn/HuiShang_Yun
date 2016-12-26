package com.huishangyun.Activity;

import com.gotye.api.GotyeUser;
import com.huishangyun.ImageLoad.ImageLoader;
import com.huishangyun.Office.Businesstrip.ImagePagerActivity;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.View.RoundAngleImageView;
import com.huishangyun.manager.DepartmentManager;
import com.huishangyun.model.Content;
import com.huishangyun.model.Managers;
import com.huishangyun.App.MyApplication;
import com.huishangyun.yun.R;

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

/**
 * 人员详情
 *
 * @author Pan
 */
public class ManagerInfo extends BaseMainActivity {
    private String JID;
    private String imgUrl = "";
    private String Name;
    private LinearLayout backLayout;//返回
    private LinearLayout chatLayout;
    private ImageView userImg;
    private TextView userName;
    private TextView backName;
    private TextView infoName;
    private TextView sexTex;
    private TextView signTxt;
    private TextView numberTxt;
    private TextView depart;
    private TextView dutyTxt;
    private TextView phoneTxt;
    private TextView mailTxt;
    private TextView noteTxt;

    private LinearLayout CallPhone;
    private LinearLayout SendMessage;
    private LinearLayout SendChat;
    private LinearLayout layout;
    private Managers managers = null;
    private LinearLayout call_phone;
    private LinearLayout call_messages;
    private LinearLayout call_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberinfo);
        JID = getIntent().getStringExtra("JID");
        initView();
        initListener();
        getUserInfo(JID);
    }

    private void initView() {
        backLayout = (LinearLayout) findViewById(R.id.member_back);
        chatLayout = (LinearLayout) findViewById(R.id.member_chat);
        userImg = (ImageView) findViewById(R.id.member_user_img);
        userName = (TextView) findViewById(R.id.member_user_name);
        backName = (TextView) findViewById(R.id.member_title_txt);
        infoName = (TextView) findViewById(R.id.member_info_name);
        sexTex = (TextView) findViewById(R.id.member_sex);
        signTxt = (TextView) findViewById(R.id.member_sign);
        numberTxt = (TextView) findViewById(R.id.member_number);
        depart = (TextView) findViewById(R.id.member_depart);
        dutyTxt = (TextView) findViewById(R.id.member_duty);
        phoneTxt = (TextView) findViewById(R.id.member_phone);
        mailTxt = (TextView) findViewById(R.id.member_mail);
        noteTxt = (TextView) findViewById(R.id.member_note);
        layout = (LinearLayout) findViewById(R.id.manager_layout);
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
            Intent intent = null;
            switch (v.getId()) {
                case R.id.member_back:
                    finish();
                    break;

                case R.id.member_chat:
                    Intent intent2 = new Intent(ManagerInfo.this, CharHistory.class);
                    intent2.putExtra("to", JID);
                    intent2.putExtra("name", Name);
                    intent2.putExtra("type", 0);
                    intent2.putExtra("isGroup", false);
                    startActivity(intent2);
                    break;
                case R.id.call_phone:
                    if (managers != null) {
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + managers.getMobile()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //启动
                        startActivity(intent);
                    } else {
                        showCustomToast("无法获取联系人资料", false);
                    }

                    break;
                case R.id.call_messages:
                    if (managers != null) {
                        intent = new Intent();
                        //系统默认的action，用来打开默认的短信界面
                        intent.setAction(Intent.ACTION_SENDTO);
                        //需要发短息的号码
                        intent.setData(Uri.parse("smsto:" + managers.getMobile()));
                        startActivity(intent);
                    } else {
                        showCustomToast("无法获取联系人资料", false);
                    }

                    break;
                case R.id.call_chat:
                    //打开聊天界面
                    intent = new Intent(ManagerInfo.this, Chat.class);
                    intent.putExtra("JID", JID);
                    intent.putExtra("type", 2);
                    intent.putExtra("name", Name);
                    intent.putExtra("messtype", 0);
                    intent.putExtra("chat_name", Name);
                    intent.putExtra("Sign", "");
                    GotyeUser gotyeUser = new GotyeUser(JID);
                    intent.putExtra("user", gotyeUser);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.member_user_img:
                    Intent aintent = new Intent(ManagerInfo.this, ImagePagerActivity.class);
                    // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                    aintent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{"http://img.huishangyun.com/UploadFile/huishang/" +
                            MyApplication.preferences.getInt(Content.COMPS_ID, 1016) + "/Photo/" + imgUrl});
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
                    managers = (Managers) msg.obj;
                    userName.setText(managers.getRealName());
                    infoName.setText(managers.getRealName());
                /*if (managers.getSex().equals("M")) {
					sexTex.setText("男");
				} else if (managers.getSex().equals("W")){
					sexTex.setText("女");
				}*/
                    sexTex.setText(managers.getSex() + "");
                    signTxt.setText(managers.getSign());
                    numberTxt.setText("");
                    depart.setText(managers.getDepartment_Name());
                    dutyTxt.setText(managers.getRole_Name());
                    phoneTxt.setText(managers.getMobile());
                    mailTxt.setText(managers.getEmail());
                    noteTxt.setText(managers.getNote());
                    imgUrl = managers.getPhoto();
                    userImg.setImageResource(R.drawable.contact_person);
				/*new ImageLoader(ManagerInfo.this).DisplayImage("http://img.huishangyun.com/UploadFile/huishang/"+
						MyApplication.preferences.getInt(Content.COMPS_ID, 1016)+"/Photo/" + managers.getPhoto(), userImg, false);*/
                    com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage("http://img.huishangyun.com/UploadFile/huishang/" +
                            MyApplication.preferences.getInt(Content.COMPS_ID, 1016) + "/Photo/" + managers.getPhoto(), userImg, MyApplication.getInstance().getOptions());
                    break;

                case HanderUtil.case2:
                    showCustomToast("获取资料失败,请更新数据!", false);
                    break;

                default:
                    break;
            }
        }

        ;
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
            Managers managers = DepartmentManager.getInstance(ManagerInfo.this).getManagerInfo(OFUserName);
            if (managers != null) {
                Message msg = new Message();
                msg.obj = managers;
                msg.what = HanderUtil.case1;
                mHandler.sendMessage(msg);
            } else {
                mHandler.sendEmptyMessage(HanderUtil.case2);
            }
        }

    }


}
