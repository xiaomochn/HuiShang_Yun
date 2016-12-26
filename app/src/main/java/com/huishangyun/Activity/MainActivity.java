package com.huishangyun.Activity;

import java.util.ArrayList;
import java.util.List;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeChatTarget;
import com.gotye.api.GotyeChatTargetType;
import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeMessageStatus;
import com.gotye.api.GotyeNotify;
import com.gotye.api.GotyeStatusCode;
import com.gotye.api.GotyeUser;
import com.huishangyun.Fragment.App_Fragment;
import com.huishangyun.Fragment.ContactFragment;
import com.huishangyun.Fragment.SettingFragment;
import com.huishangyun.Util.L;
import com.huishangyun.manager.MessageManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Notice;
import com.huishangyun.service.HSChatService;
import com.huishangyun.yun.R;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Fragment.MsgFragment;
import com.huishangyun.model.ChartHisBean;

/**
 * @author Pan 主界面布局，采用fragment替代tabhost
 */
public class MainActivity extends BaseMainActivity {
    private Fragment[] mFragments;// fragment实体类都封装在这里
    private RadioButton msgButton, contactButton, workButton, officeButton/*, settingButton*/;// 底部的radiobuttom
    public RadioGroup radioGroup;
    private RelativeLayout title;
    private TextView tvTitle;
    private MsgFragment msgFragment;
    private ContactFragment contactFragment;
    private SettingFragment settingFragment;
    private List<ChartHisBean> inviteNotices = new ArrayList<ChartHisBean>();
    private TextView noticePaopao;
    private ContacterReceiver receiver;
    private boolean IsWorkSelect = false;
    private boolean IsContactSelect = false;
    private LinearLayout NetLayout;// 网络变化布局
    private ProgressBar progressBar;
    private static MainActivity INSTANCE;
    public boolean isLogin = false;
    private App_Fragment app_Fragment;

    //private LinearLayout CompanyApp;
    public static MainActivity getInstance() {
        return INSTANCE;
    }

    public static MainActivity act;//获取参数
    private String model;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        MyApplication.getInstance().getGotyeAPI().addListener(mDelegate);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        model = getIntent().getStringExtra("model");//得到引导页传过来的参数
        //Log.e("TAGS","model传过来的值：="+model);
        if (model != null) {
            init();
            setFragmentIndicator(2);
            initTitle("应用");
            title.setVisibility(View.VISIBLE);
        } else {
            init();
            INSTANCE = this;
            int state = MyApplication.getInstance().getGotyeAPI().isOnline();
            MyApplication.getInstance().getNewAd();
            if (state != 1) {
                setErrorTip(0);
            } else {
                setErrorTip(1);
            }
            //启动位置发送，每隔15分钟发送一次
            //new MangerLocation().sendLocationInfo(getApplicationContext(), true);
        }
    }

    /**
     * 实例化各组件
     */
    private void init() {
        //传值给fragment
        mFragments = new Fragment[4];
        mFragments[0] = getSupportFragmentManager().findFragmentById(
                R.id.fragment_msg);
        mFragments[1] = getSupportFragmentManager().findFragmentById(
                R.id.fragment_contact);
        mFragments[2] = getSupportFragmentManager().findFragmentById(
                R.id.fragment_app);
        mFragments[3] = getSupportFragmentManager().findFragmentById(
                R.id.fragment_setting);
        /*mFragments[4] = getSupportFragmentManager().findFragmentById(
				R.id.fragment_setting);*/
        msgFragment = (MsgFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_msg);
        contactFragment = (ContactFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_contact);
        settingFragment = (SettingFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_setting);
        app_Fragment = (App_Fragment) getSupportFragmentManager().findFragmentById(R.id.fragment_app);
        msgButton = (RadioButton) findViewById(R.id.radio_button1);
        contactButton = (RadioButton) findViewById(R.id.radio_button2);
        workButton = (RadioButton) findViewById(R.id.radio_button3);
        officeButton = (RadioButton) findViewById(R.id.radio_button4);
        //settingButton = (RadioButton) findViewById(R.id.radio_button5);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        title = (RelativeLayout) findViewById(R.id.title);
        //CompanyApp = (LinearLayout) findViewById(R.id.main_apption);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        msgButton.setOnCheckedChangeListener(listener);
        contactButton.setOnCheckedChangeListener(listener);
        workButton.setOnCheckedChangeListener(listener);
        officeButton.setOnCheckedChangeListener(listener);
        //settingButton.setOnCheckedChangeListener(listener);
        receiver = new ContacterReceiver();
        noticePaopao = (TextView) findViewById(R.id.new_msg_count);
        inviteNotices = MessageManager.getInstance(MainActivity.this)
                .getRecentContactsWithLastMsg();
        if (model != null) {
            setFragmentIndicator(2);
            workButton.setChecked(true);
            msgButton.setChecked(false);
        } else {
            setFragmentIndicator(0);
        }

        NetLayout = (LinearLayout) findViewById(R.id.net_status_bar_top);
        NetLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = null;
                // 判断手机系统的版本 即API大于10 就是3.0或以上版本
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    intent = new Intent(
                            android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName component = new ComponentName(
                            "com.android.settings",
                            "com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                startActivity(intent);
            }
        });
		/*CompanyApp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent appIntent = new Intent();
				showCustomToast("此处进入企业应用", true);
			}
		});*/
        IntentFilter filter = new IntentFilter();
        // 好友请求
        filter.addAction(Constant.ROSTER_SUBSCRIPTION);
        filter.addAction(Constant.NEW_MESSAGE_ACTION);
        filter.addAction(Constant.ACTION_RECONNECT_STATE);
        filter.addAction(Constant.ROSTER_DELETED);
        filter.addAction(Constant.ROSTER_PRESENCE_CHANGED);
        filter.addAction(Constant.ROSTER_UPDATED);
        filter.addAction(Constant.UPDATA_UI);
        filter.addAction(Constant.EXTRUSION_LINE);
        filter.addAction(Constant.SERVERISDOWN);
        filter.addAction(Constant.HUISHANG_OK_ACTION);
        registerReceiver(receiver, filter);
        progressBar = (ProgressBar) findViewById(R.id.ivTitleProgress);
        progressBar.setVisibility(View.GONE);
        //更新资料
        if (!MyApplication.isServiceRunning) {
            settingFragment.updataInfo();
        }

    }

    /**
     * 设置监听
     */

    private OnCheckedChangeListener listener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
            // TODO Auto-generated method stub
            if (arg1) {
                selectMenu(arg0);
            }

        }
    };

    /**
     * 设置底部菜单选中项
     *
     * @updata 2014-05-19 10:44:46
     */
    private void selectMenu(CompoundButton compoundButton) {
        IsWorkSelect = false;
        IsContactSelect = false;
        switch (compoundButton.getId()) {
            case R.id.radio_button1:
                setFragmentIndicator(0);
                initTitle("企信");
                title.setVisibility(View.VISIBLE);
                break;
            case R.id.radio_button2:
                setFragmentIndicator(1);
                initTitle("通讯录");
                title.setVisibility(View.VISIBLE);
                IsContactSelect = true;
                break;
            case R.id.radio_button3:
                setFragmentIndicator(2);
                initTitle("应用");
                title.setVisibility(View.VISIBLE);
                IsWorkSelect = true;
                break;
            case R.id.radio_button4:
                setFragmentIndicator(3);
                initTitle("设置");
                title.setVisibility(View.VISIBLE);
                break;
			
		/*case R.id.radio_button5:
			setFragmentIndicator(4);
			initTitle("设置");
			settingButton.setTextColor(0xFF21a5de);
			title.setVisibility(View.VISIBLE);
			break;*/

            default:
                break;
        }
    }

    /**
     * 设置选中项
     */
    private void setFragmentIndicator(int whichIsDefault) {
        // 使用commitAllowingStateLoss替换commit。使用commit特中情况下会报异常：Can not perform this action after onSaveInstanceState
        getSupportFragmentManager().beginTransaction().hide(mFragments[0])
                .hide(mFragments[1]).hide(mFragments[2]).hide(mFragments[3])
                .show(mFragments[whichIsDefault]).commitAllowingStateLoss();
    }

    /**
     * 返回键监听
     */
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
		/*if (IsWorkSelect) {
//			workFragment = (WorkFragment) getSupportFragmentManager()
//					.findFragmentById(R.id.fragment_work);
//			if (!workFragment.onBackPressed()) {
				BackPressed();
//			}
		} else*/
        if (IsContactSelect) {
            if (!contactFragment.onBackPressed()) {
                BackPressed();
            }
        } else {
            BackPressed();
        }

    }

    /**
     * 返回键方法调用
     */
    private void BackPressed() {
		/*Intent home = new Intent(Intent.ACTION_MAIN);  
	    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
	    home.addCategory(Intent.CATEGORY_HOME);  
	    startActivity(home);*/
        moveTaskToBack(false);
    }

    /**
     * 设置头部信息
     */
    public void initTitle(String title) {
        tvTitle.setText(title);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mainRefresh();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        MyApplication.getInstance().getGotyeAPI().removeListener(mDelegate);
        unregisterReceiver(receiver);
        INSTANCE = null;
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(R.string.app_name);

    }

    /**
     * 广播接收器
     *
     * @author pan
     */
    private class ContacterReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            Notice notice = (Notice) intent.getSerializableExtra("notice");
            L.d(getClass(), "有广播进入" + action);


            if (Constant.ROSTER_SUBSCRIPTION.equals(action)) {
                subscripUserReceive(intent
                        .getStringExtra(Constant.ROSTER_SUB_FROM));
            } else if (Constant.NEW_MESSAGE_ACTION.equals(action)) {
                // intent.putExtra("noticeId", noticeId);
                String noticeId = intent.getStringExtra("noticeId");
                //msgReceive(notice);
            } else if (Constant.UPDATA_UI.equals(action)) {
                if (intent.getBooleanExtra(Constant.UPDATA_UI_NAME, true)) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            } else if (Constant.EXTRUSION_LINE.equals(action)) {
				/*onExtrusionline("警告",
						"您的账号在另一台设备登录,如果不是您本人操作，请尽快联系管理人员修改密码!",
						"重新登录", "退出");*/
                //unOnLine("警告", "您的账号在另一台设备登录!");
            } else if (Constant.SERVERISDOWN.equals(action)) {
				/*onExtrusionline("提示",
						"连接出现异常,是否重新登录",
						"重新登录", "退出");*/
                //unOnLine("提示", "连接出现异常,是否重新登录!");
            } else if (Constant.HUISHANG_OK_ACTION.equals(action)) {
                contactFragment.updataInfo();
                app_Fragment.getLightApp();
                mainRefresh();
                if (!MyApplication.isServiceRunning) {
                    //启动线程并连接聊天服务
                    int code = MyApplication.getInstance().getGotyeAPI().isOnline();
                    if (code != 0) {
                        L.i("已经登陆了...");
                    }
                }
            }
        }

    }

    private void subscripUserReceive(final String subFrom) {
        Notice notice = new Notice();
        notice.setFrom(subFrom);
        notice.setNoticeType(Notice.CHAT_MSG);
    }

    /**
     * 下方气泡设置,
     */
    private void setPaoPao() {
        if (null != inviteNotices && inviteNotices.size() > 0) {
            int paoCount = 0;
            for (ChartHisBean c : inviteNotices) {
                Integer countx = c.getNoticeSum();
                paoCount += (countx == null ? 0 : (countx > 99 ? 99 : countx));
            }
            if (paoCount == 0) {
                noticePaopao.setVisibility(View.GONE);
                return;
            }
            noticePaopao.setText(paoCount + "");
            noticePaopao.setVisibility(View.VISIBLE);
        } else {
            noticePaopao.setVisibility(View.GONE);
        }
    }

    private void setErrorTip(int code) {
//		code=api.getOnLineState();
        if (code == 1) {
            NetLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        } else {
            NetLayout.setVisibility(View.VISIBLE);
            if (code == -1) {
                progressBar.setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.net_status_bar_info_top)).setText("正在连接登陆...");
                NetLayout.setVisibility(View.VISIBLE);

            } else {
                progressBar.setVisibility(View.GONE);
                ((TextView) findViewById(R.id.net_status_bar_info_top)).setText("当前未登陆或网络异常");
                NetLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    // 页面刷新
    private void mainRefresh() {
        updateUnReadTip();
        if (msgFragment != null) {
            msgFragment.refreshList();
        }
       // msgFragment.refreshList();
		/*if (contactsFragment != null) {
			contactsFragment.refresh();
		}*/

    }


    // 更新提醒
    public void updateUnReadTip() {
        if (msgFragment != null) {
            msgFragment.refreshList();
        }
        //int unreadCount = MyApplication.getInstance().getGotyeAPI().getTotalUnreadMsgCount();
        int unreadCount = MyApplication.getInstance().getGotyeAPI().getTotalUnreadMessageCount();//获取所有未读数量
        noticePaopao.setVisibility(View.VISIBLE);
        if (unreadCount > 0 && unreadCount < 100) {
            noticePaopao.setText(String.valueOf(unreadCount));
        } else if (unreadCount >= 100) {
            noticePaopao.setText("99");
        } else {
            noticePaopao.setVisibility(View.GONE);
        }
    }
    //清理推送通知  在初始化时调用
    private void clearNotify() {
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == 1) {// 修改用户信息返回为成功
            settingFragment.setUserInfo();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private GotyeDelegate mDelegate = new GotyeDelegate(){
        @Override
        public void onLogin(int code, GotyeUser user) {
            setErrorTip(1);
        }

        @Override
        public void onLogout(int code) {
            L.i("onLogout");
            setErrorTip(0);
            mainRefresh();
        }

        @Override
        public void onReconnecting(int code, GotyeUser user) {
            if (code== GotyeStatusCode.CodeOK){
                setErrorTip(1);
                mainRefresh();
            }else {
                setErrorTip(-1);
            }
        }

        @Override
        public void onSendNotify(int code, GotyeNotify notify) {
            mainRefresh();//刷新
        }

        @Override
        public void onReceiveNotify(GotyeNotify notify) {
            mainRefresh();//刷新
        }
        @Override
        public void onReceiveMessage(GotyeMessage message) {
            if (msgFragment != null) {
                msgFragment.refreshList();
            }
            if (message.getSender().getName().indexOf("0_n_") == 0) {
               // MyApplication.getInstance().getGotyeAPI().markMessagesAsRead(message);//设置为已读
                noticePaopao.setVisibility(View.GONE);
                return;
            }

            if (message.getStatus() == GotyeMessageStatus.GotyeMessageStatusUnread) {
                updateUnReadTip();
//                if (!MyApplication.getInstance().getGotyeAPI().isNewMsgNotify()) {
//                    return;
//                }
//                if (message.getReceiver().getType() == GotyeChatTargetType.GotyeChatTargetTypeGroup) {
//                    if (MyApplication.getInstance().getGotyeAPI().isNotReceiveGroupMsg()) {
//                        return;
//                   }
//                   if (MyApplication.getInstance().getGotyeAPI().isGroupDontdisturb(((GotyeGroup) message.getReceiver()).getGroupID())) {
//                       return;
//                    }
//                }
              //  beep.playBeepSoundAndVibrate();
            }
        }
    };
}
