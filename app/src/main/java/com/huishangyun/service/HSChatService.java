package com.huishangyun.service;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.google.gson.reflect.TypeToken;
import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeChatTarget;
import com.gotye.api.GotyeChatTargetType;
import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeMessageType;
import com.gotye.api.GotyeNotify;
import com.gotye.api.GotyeStatusCode;
import com.gotye.api.GotyeUser;

import com.huishangyun.Activity.LandActivity;
import com.huishangyun.Activity.ScreenActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Orders.DingDanDatailsActivity;
import com.huishangyun.Map.Location;
import com.huishangyun.Map.LocationUtil;
import com.huishangyun.Office.Summary.SummaryDetailActivity;
import com.huishangyun.Office.WeiGuan.OnLooksDetails;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.DateUtil;
import com.huishangyun.Util.FileUtils;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ScreenObserver;
import com.huishangyun.Util.ServiceUtil;
import com.huishangyun.Util.StringUtil;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.View.MyDialog;
import com.huishangyun.manager.DepartmentManager;
import com.huishangyun.manager.GroupManager;
import com.huishangyun.manager.MemberManager;
import com.huishangyun.manager.MessageManager;
import com.huishangyun.manager.NoticeManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.IMMessage;
import com.huishangyun.model.MessageData;
import com.huishangyun.model.MessageType;
import com.huishangyun.model.Methods;
import com.huishangyun.model.Notice;
import com.huishangyun.model.ScreenEntity;
import com.huishangyun.Activity.Chat;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.T;
import com.huishangyun.View.MyDialog.OnMyDialogClickListener;
import com.huishangyun.yun.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class HSChatService extends Service {

    public static final String ACTION_LOGIN = "gotyeim.login";
    private ActivityManager mActivityManager;
    private String mPackageName;
    private List<ScreenEntity> screenEntities;
    private ScreenObserver mScreenObserver;
    private String loactionUser = "";

    //private GotyeAPI api;
    //  private static final String API_KEY = "96b6add1-fac2-43f4-b4c3-727efa58dcc5";
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mActivityManager = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        mPackageName = getPackageName();
        mScreenObserver = new ScreenObserver(this);
        mScreenObserver.begin(screenStateListener);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.START_NOTIF);
        registerReceiver(StartNotif, intentFilter);

        //  api = GotyeAPI.getInstance();
        //  api.init(this, API_KEY); ///< 使用活动上下文以及appkey初始化亲加API
        //添加推送消息监听

        //api.initIflySpeechRecognition();///< 如需开启语音识别功能，请添加此行
        // api.beginReceiveOfflineMessage();//开始接收离线消息

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String loginname = MyApplication.getInstance().getSharedPreferences().getString(Constant.XMPP_LOGIN_NAME, "");
        String password = MyApplication.getInstance().getSharedPreferences().getString(Constant.XMPP_LOGIN_PASSWORD, "");
        //易暂时注释
        int code = MyApplication.getInstance().getGotyeAPI().login(loginname, password);
        if (code==GotyeStatusCode.CodeWaitingCallback){
            //登录成功
            MyApplication.getInstance().getGotyeAPI().addListener(mDelegate);
        }
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 广播接收器，当应用后台运行时将Service变成前台应用
     */
    private BroadcastReceiver StartNotif = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            L.d("接收到广播！");
            if (!isAppOnForeground()) {
                // 发出通知并将Service变成前台Service
                if (MyApplication.getInstance().getSharedPreferences().getBoolean(Constant.HUISHANG_MESSAGE, false)) {
                    if (intent.getAction().equals(Constant.START_NOTIF)) { //判断用户是否设置显示通知栏
                        L.d("发出通知！");
                        setNotiType(R.drawable.logo,
                                getResources().getString(R.string.app_name),
                                "正在后台运行");
                    }
                }
            } else {
                // 清除通知,并将Service还原成后台Service
                if (MyApplication.preferences.getBoolean(Constant.HUISHANG_ISMAIN_TOP, true)) {
                    L.d("清除通知！");
                    stopForeground(true);
                }
            }
        }

    };

    /**
     * 锁屏监听
     */
    private ScreenObserver.ScreenStateListener screenStateListener = new ScreenObserver.ScreenStateListener() {

        @Override
        public void onScreenOn() {
            //屏幕点亮
            // TODO Auto-generated method stub
            L.e("onScreenOn");
        }

        @Override
        public void onScreenOff() {
            // TODO Auto-generated method stub
            //锁屏
            L.e("onScreenOff");
            MyApplication.preferences.edit().putBoolean(Constant.HUISHANG_SCREEN_CANSHOW, true).commit();
        }

        @Override
        public void onUserPresent() {
            // TODO Auto-generated method stub
            //解锁
            MyApplication.preferences.edit().putBoolean(Constant.HUISHANG_SCREEN_CANSHOW, false).commit();
            L.e("onUserPresent");
        }
    };


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        MyApplication.getInstance().getGotyeAPI().removeListener(mDelegate);
        /*Intent localIntent = new Intent();
        localIntent.setClass(this, YQYChatService.class); // 銷毀時重新啟動Service
		this.startService(localIntent);*/
        mScreenObserver.unregisterListener();
        unregisterReceiver(StartNotif);
        super.onDestroy();
    }


    private long endTime = 0;

    /**
     * 开启定位
     *
     * @param OFUserName
     */
    private void startLocation(final String OFUserName) {
        L.e("进入定位");
        LocationUtil.startLocation(HSChatService.this, new BDLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation location) {
                // TODO Auto-generated method stub
                if (location == null) {
                    L.e("定位失败!");
                    return;
                }

                //获取纬度
                double Latitude = location.getLatitude();
                //获取经度
                double Longitude = location.getLongitude();
                L.e("定位成功!" + Latitude + "," + Longitude);
                LocationUtil.stopLocation();
                webServiceHelp<T> mServiceHelp = new webServiceHelp<T>(Methods.SEND_SYS_MSG, new TypeToken<ZJResponse<T>>() {
                }.getType());
                mServiceHelp.setOnServiceCallBack(new webServiceHelp.OnServiceCallBack<T>() {

                    @Override
                    public void onServiceCallBack(boolean haveCallBack,
                                                  ZJResponse<T> zjResponse) {
                        // TODO Auto-generated method stub
                        if (zjResponse != null) {
                            L.e("zjResponse = " + zjResponse.getCode());
                        } else {
                            L.e("位置信息发送失败");
                        }
                    }
                });
                //生产字符串
                Location location2 = new Location();
                location2.setLatitude(Latitude);
                location2.setLongitude(Longitude);
                location2.setType(MessageType.MESSAGE_LOCATION_RESULT);

                ZJRequest zjRequest = new ZJRequest();
                zjRequest.setNote(location2.getLocationStr());
                zjRequest.setOFUserName(OFUserName);
                L.e("OFUserName = " + JsonUtil.toJson(zjRequest));
                mServiceHelp.start(JsonUtil.toJson(zjRequest));
            }

            @Override
            public void onReceivePoi(BDLocation location) {
                // TODO Auto-generated method stub

            }
        });
    }

    /**
     * 将消息回执返回到服务器
     *
     * @return
     * @paramJID
     */

    public static void receiptSerivce(final String ID, final String SenderID) {

        //是服务号，同时将消息回执返回到服务器
        L.e("是服务号，同时将消息回执返回到服务器");
        new Thread() {
            public void run() {
                ZJRequest zjRequest = new ZJRequest();
                /*ServiceUtil sUtil = ServiceResolve.ResolveInfo(msg);*/
                zjRequest.setID(Integer.parseInt(ID));
                zjRequest.setOFUserName(MyApplication.preferences.getString(Constant.XMPP_LOGIN_NAME, ""));
                zjRequest.setCompany_ID(MyApplication.getInstance().getCompanyID());
                zjRequest.setKeywords(SenderID);
                String json = JsonUtil.toJson(zjRequest);
                Log.e("TAGS", "JSON=" + json);
                java.lang.reflect.Type type = new TypeToken<ZJResponse>() {
                }.getType();
                String result = DataUtil.callWebService(Methods.SET_MESSAGE_STATUS, json);
                Log.e("TAGS", "result=" + result);
                if (result != null) {
                    ZJResponse zjResponse = JsonUtil.fromJson(result, type);
                    if (zjResponse.getCode() == 0) {
                        L.e("回执成功");
                    }
                }
            }

            ;
        }.start();
    }

    /**
     * 将消息推送到通知栏
     *
     * @parammessage 消息内容
     */
    @SuppressLint("NewApi")
    private void sendMessageNotify(ServiceUtil serviceUtil) {
        Intent intent = new Intent(this, OnLooksDetails.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Tittle", "围观详情");
        intent.putExtra("ID", Integer.parseInt(serviceUtil.getItemID()));
        intent.putExtra("canSee", false);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(serviceUtil.getTitle());
        builder.setContentText(serviceUtil.getContent());
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent(contentIntent);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setSmallIcon(R.drawable.logo);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        //MyNotificationManager.sendNotification(this, notification);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int flag = random.nextInt(1000) % (1000 - 10 + 1) + 10;
        L.e("生成的随机数为:" + flag);
        notificationManager.notify(flag, notification);
        //判断是否要语音播报
    }

    /**
     * 工作日志消息
     * <p/>
     * 消息内容
     */
    @SuppressLint("NewApi")
    private void sendWorkLogNotify(ServiceUtil serviceUtil) {
        String[] workLog = serviceUtil.getContent().split("&");
        Intent intent = new Intent(this, SummaryDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // intent.putExtra("Tittle", "日报详情");
        intent.putExtra("BelongDate", workLog[1]);
        intent.putExtra("Manager_ID", Integer.parseInt(workLog[2]));
        intent.putExtra("ID", Integer.parseInt(serviceUtil.getItemID()));

        PendingIntent contentIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(serviceUtil.getTitle());
        builder.setContentText(workLog[0]);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent(contentIntent);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setSmallIcon(R.drawable.logo);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        //MyNotificationManager.sendNotification(this, notification);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int flag = random.nextInt(1000) % (1000 - 10 + 1) + 10;
        L.e("生成的随机数为:" + flag);
        notificationManager.notify(flag, notification);

    }


    /**
     * 将订单消息推送到通知栏
     * <p/>
     * 消息内容
     */
    @SuppressLint("NewApi")
    private void sendOrderNotify(ServiceUtil serviceUtil) {
        Intent intent = new Intent(this, DingDanDatailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("OrderID", serviceUtil.getItemID());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 1,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(serviceUtil.getTitle());
        builder.setContentText(serviceUtil.getContent());
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent(contentIntent);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setSmallIcon(R.drawable.logo);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        //注释掉,暂时不清楚需要传递的值
        // MyNotificationManager.sendNotification(this, notification);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int flag = random.nextInt(1000) % (1000 - 10 + 1) + 10;
        L.e("生成的随机数为:" + flag);
        notificationManager.notify(flag, notification);

    }

//    /*
//    * 判断app当前是在线还是离线
//    * */
//    private boolean getappState() {
//        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
//        List<RunningTaskInfo> list = am.getRunningTasks(100);
//        String pkName = this.getPackageName();
//        for (RunningTaskInfo info : list) {
//            if (info.topActivity.getPackageName().equals(pkName) && info.baseActivity.getPackageName().equals(pkName)) {
//                return true;
//            }
//        }
//        return true;
//    }

    private GotyeDelegate mDelegate = new GotyeDelegate() {
        @Override
        public void onReceiveMessage(GotyeMessage message) {
            String msg = null;
            if (message.getType() == GotyeMessageType.GotyeMessageTypeText) {
                String senderID = message.getSender().getName();
                int companyID = MyApplication.getInstance().getCompanyID();
                boolean isServer = false;//企业服务号,提示消息，并显示记录
                boolean isCmd = false;//系统命令号,既不提示消息，也不显示记录
                boolean isSystem = false;//系统消息号,仅提示消息，不显示记录
                boolean isNomal = false;//普通消息
                if (senderID == Constant.HUISHANG_SYSTEM_ID) {
                    isCmd = true;
                } else if (senderID.indexOf("0_n_") == 0) {
                    isSystem = true;
                } else if (senderID.indexOf(companyID + "_s_") == 0) {
                    isServer = true;
                } else {
                    isNomal = true;
                }
                msg = message.getSender().getName() + "发来了一条消息";
                if (isCmd) {
                    // 解析位置信息
                    MessageData<String> messageData = JsonUtil.fromJson(message.getText(), new TypeToken<MessageData<String>>() {
                    }.getType());
                    String Note[] = messageData.getMessageContent().split("\\*");
                    loactionUser = Note[3];
                    if (Note[2].equals(MessageType.MESSAGE_LOCATION_GET)) {
                        // 获取位置信息
                        startLocation(Note[3]);
                    } else if (Note[2].equals(MessageType.MESSAGE_LOCATION_RESULT)) { // 位置结果
                        L.e("收到结果");
                        // 收到定位结果,将位置信息以广播形式发送出去
                        Location location = new Location();
                        location.setLatitude(Double.parseDouble(Note[0]));
                        location.setLongitude(Double.parseDouble(Note[1]));
                        location.setType(Note[2]);
                        Intent intent = new Intent();
                        intent.setAction(Constant.HUISHANG_ACTION_LOCATION);
                        intent.putExtra(MessageType.MESSAGE_LOCATION_RESULT, location);
                        sendBroadcast(intent);
                    }
                } else {
                    if (isServer || isSystem) {
                        //企业服务号和系统服务号消息处理
                        MessageData<ServiceUtil> messageData = JsonUtil.fromJson(message.getText(), new TypeToken<MessageData<ServiceUtil>>() {
                        }.getType());
                        ServiceUtil sUtil = messageData.getMessageContent(); // 解析服务号信息
                        receiptSerivce(sUtil.getID(), senderID);
                        if (isSystem) {
                            switch (senderID) {
                                case Constant.HUISHANG_ONLOOK_ID://围观提醒
                                    sendMessageNotify(sUtil);
                                    //MediaPlayer.create(this, R.raw.office).start();
                                    break;
                                case Constant.HUISHANG_ORDER_ID://订单提醒
                                    sendOrderNotify(sUtil);
                                    break;
                                case Constant.HUISHANG_WorkLog_ID://日志提醒
                                    sendWorkLogNotify(sUtil);
                                    break;
                            }
                        }
                    }
                    // 判断是否可以存储到数据库
                    if (!MessageManager.getInstance(getApplicationContext()).IsTimeEquals(TimeUtil.getLongToString((message.getDate() * 1000)))) {
                        IMMessage imMessage = new IMMessage();
                        if (message.getReceiver().getType() == GotyeChatTargetType.GotyeChatTargetTypeGroup) {
                            // 保存群ID号
                            imMessage.setGroup_name(message.getReceiver().getId() + "");
                            imMessage.setMesstype(1);
                        } else {
                            // 0不是群
                            imMessage.setMesstype(0);
                        }
                        // 保存内容
                        imMessage.setFromSubJid(message.getSender().getName());
                        imMessage.setContent(message.getText());
                        // 保存时间
                        imMessage.setTime(TimeUtil.getLongToString((message.getDate() * 1000)));
                        // 1为发送,0为接收
                        imMessage.setMsgType(0);
                        MessageManager.getInstance(getApplicationContext()).saveIMMessage(imMessage);
                    }
                    // 判断是否可以响铃
                    if ((message.getDate() * 1000) - endTime > 2000) {
                        MediaPlayer.create(getApplicationContext(), R.raw.office).start();
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    /*
                     * long [] pattern = {150,250,150,250}; // 停止 开启 停止 开启
					 * vibrator.vibrate(pattern,-1); //重复两次上面的pattern
					 * 如果只想震动一次，index设 - 1
					 */
                        vibrator.vibrate(200);
                    }
                    endTime = (message.getDate() * 1000);

                    boolean screenLock = MyApplication.preferences.getBoolean(Constant.HUISHANG_SCREEN_CANSHOW, false);
                    Log.e("TAGS", "screenLock=" + screenLock);
                    if (screenLock) {
                        String time = TimeUtil.getLongToString((message.getDate() * 1000));
                        onScreenLock(message.getSender().getName(), time, message);

                    } else {
                        if (message.getReceiver().getType() == GotyeChatTargetType.GotyeChatTargetTypeGroup) {
                            setNotiType(R.drawable.logo, "您有新的群消息", message, Chat.class, message.getSender().getName(), 1, 2, true);
                        } else {
                            setNotiType(R.drawable.logo, "您有新的消息", message, Chat.class, message.getSender().getName(), 1, 0, false);
                        }
                    }
                }
            }
        }

        @Override
        public void onLogin(int code, GotyeUser user) {
            if (code == GotyeStatusCode.CodeOK || code == GotyeStatusCode.CodeOfflineLoginOK || code == GotyeStatusCode.CodeReloginOK) {
                if (code == GotyeStatusCode.CodeOfflineLoginOK) {
                    //Toast.makeText(YQYChatService.this, "您当前处于离线状态", Toast.LENGTH_SHORT).show();
                    Log.e("TAGS", "离线=" + "处于离线状态");

                } else if (code == GotyeStatusCode.CodeOK) {
                    L.i("登陆成功");
                }
            } else {
                // 失败,可根据code定位失败原因
                L.i("登录失败 code=" + code);
            }
            Log.e("TAGS", "code=" + code);
        }

        @Override
        public void onLogout(int code) {
            if (code == GotyeStatusCode.CodeForceLogout) {
                MyDialog myDialog = new MyDialog(getApplicationContext());
                myDialog.setTitle("警告");
                myDialog.setMessage("您的账号在另外一台设备上登录了!即将退出");
                myDialog.setCancel(false);
                myDialog.showTrue(false);
                myDialog.setFalseText("确定");
                myDialog.setOnMyDialogClickListener(new OnMyDialogClickListener() {

                    @Override
                    public void onTrueClick(MyDialog dialog) {

                    }

                    @Override
                    public void onFlaseClick(MyDialog dialog) {
                        // TODO Auto-generated method stub
                        try {
                            dialog.dismiss();
                            MyApplication.getInstance().getGotyeAPI().logout();
                            // getActivity().stopService(new Intent(getActivity(), YQYChatService.class));
                            if (MyApplication.isPhoneServiceRun) {
                                HSChatService.this.stopService(new Intent(HSChatService.this, PhoneService.class));
                            }
                            if (MyApplication.getInstance().getSharedPreferences().getInt(Constant.HUISHANG_LOCATION_TYPE, -1) == 0) {
                                HSChatService.this.stopService(new Intent(HSChatService.this, LocationService.class));
                            }
                            MyApplication.preferences.edit().putString(Constant.PASSWORD, "").commit();
                            stopSelf();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        MyApplication.getInstance().Canaell();
                        MyApplication.getInstance().removeActivity();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                });
                myDialog.setWindowsType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                myDialog.show();
            } else if (code == GotyeStatusCode.CodeNetworkDisConnected) {
                // Toast.makeText(this, "您的账号掉线了！", Toast.LENGTH_SHORT).show();
                /*
                 * Intent intent = new Intent(getBaseContext(), LoginPage.class);
				 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				 * startActivity(intent);
				 */
            } else {
//                Toast.makeText(HSChatService.this, "退出登陆！", Toast.LENGTH_SHORT).show();
//                MyApplication.clearHasLogin(HSChatService.this);
//                Intent i = new Intent(HSChatService.this, WelcomePage.class);
//                startActivity(i);
            }
        }

        @Override
        public void onSendMessage(int code, GotyeMessage message) {
            L.e("发送回执 = " + message.getStatus());
//        if (message.getStatus() == GotyeMessage.STATUS_SENT) {
//            // 发送成功
//        }
            //判断是否可以存储到数据库
		/*try {
			//如果可以解析说明是定位信息,不存储
			JsonUtil.fromJson(message.getText(), new TypeToken<MessageData<Location>>(){}.getType());
		} catch (Exception e) {*/
            // TODO: handle exception
            //存储到本地数据库
            if (!MessageManager.getInstance(getApplicationContext()).IsTimeEquals(TimeUtil.getLongToString((message.getDate() * 1000)))) {
                IMMessage imMessage = new IMMessage();
                if (message.getReceiver().getType() == GotyeChatTargetType.GotyeChatTargetTypeGroup) {
                    //保存群ID号
                    imMessage.setGroup_name(message.getReceiver().getId() + "");
                    imMessage.setMesstype(1);
                } else {
                    //0不是群
                    imMessage.setMesstype(0);
                }
                //保存内容
                imMessage.setFromSubJid(message.getSender().getName());
                imMessage.setContent(message.getText());
                //保存时间
                imMessage.setTime(TimeUtil.getLongToString((message.getDate() * 1000)));
                //1为发送,0为接收
                imMessage.setMsgType(1);

                MessageManager.getInstance(getApplicationContext()).saveIMMessage(imMessage);
            }
        }
        //接收离线消息
        @Override
        public void onGetMessageList(int code, List<GotyeMessage> list) {
            int companyID = MyApplication.getInstance().getCompanyID();
            String sendlist="";
            for (GotyeMessage message : list) {
                String senderID = message.getSender().getName();
                if (senderID.indexOf(companyID + "_s_") == 0){
                    sendlist+="#"+senderID;
                }
            }
            //向服务器发送回执
            if (!sendlist.equals("")){
                sendlist=sendlist.substring(1);
                receiptSerivce("0", sendlist);
            }
        }
    };

    /**
     * 处于锁屏状态
     *
     * @param from
     * @param time
     * @param notice
     */
    private void onScreenLock(String from, String time, GotyeMessage notice) {
        new Thread(new OnScrennLock(from, time, notice)).start();
    }

    private class OnScrennLock implements Runnable {
        private String from;
        private String time;
        private GotyeMessage notice;

        public OnScrennLock(String from, String time, GotyeMessage notice) {
            this.from = from;
            this.time = time;
            this.notice = notice;
        }

        @Override
        public void run() {
            String Names[] = MyApplication.preferences.getString(Constant.HUISHANG_SERVICE_NAME, "").split("#");
            for (int i = 0; i < Names.length; i++) {
                if (Names[i].equals(from)) {
                    L.e("-----------------------该消息在过滤列表状态");
                    return;
                }
            }
            if (!MyApplication.preferences.getBoolean(Constant.HUISHANG_SCREEN_SHOW, false)) { //判断弹出窗口是否显示
                L.e("-----------------------窗口未显示");
                MyApplication.preferences.edit().putBoolean(Constant.HUISHANG_SCREEN_SHOW, true).commit();
                //弹出窗口状态设置为显示
                screenEntities = new ArrayList<ScreenEntity>();
                ScreenEntity screenEntity = new ScreenEntity();
                screenEntity.setName(from);
                screenEntity.setTime(time);
                screenEntity.setContent(getContent(notice));
                screenEntity.setSize("1");
                //判断是否是群
                if (notice.getReceiver().getType() == GotyeChatTargetType.GotyeChatTargetTypeUser) {
                    screenEntity.setIsGroup(false);
                } else {
                    screenEntity.setIsGroup(true);
                    screenEntity.setID((int) notice.getReceiver().getId());
                }
                screenEntities.add(screenEntity);
                //显示弹出窗口
                Intent mIntent = new Intent(HSChatService.this, ScreenActivity.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mIntent.putExtra("screenEntities", (Serializable) screenEntities);
                startActivity(mIntent);

            } else { //弹出窗口为显示状态
                L.e("-----------------------窗口已显示");
                for (int i = 0; i < screenEntities.size(); i++) {
                    ScreenEntity screenEntity = screenEntities.get(i);
                    //判断是否有重复的数据
                    if (screenEntity.getIsGroup()) {
                        if (screenEntity.getID() == notice.getReceiver().getId()) {
                            int size = Integer.parseInt(screenEntity.getSize());
                            size++;
                            screenEntity.setContent(getContent(notice));
                            screenEntity.setSize(size + "");
                            screenEntity.setTime(time);
                            screenEntity.setContent(getContent(notice));
                            //判断是否是群
                            screenEntity.setIsGroup(true);
                            screenEntity.setName(notice.getSender().getName());
                            screenEntity.setID((int) notice.getReceiver().getId());
                            //下面通知弹出窗口刷新数据
                            screenEntities.set(i, screenEntity);
                            //发送广播通知刷新
                            Intent mIntent = new Intent();
                            mIntent.setAction(Constant.HUISHANG_SCREEN_ACTION);
                            mIntent.putExtra("screenEntities", (Serializable) screenEntities);
                            sendBroadcast(mIntent);
                            return;
                        }

                    } else if (screenEntity.getName().equals(from)) {
                        //对其数量+1
                        int size = Integer.parseInt(screenEntity.getSize());
                        size++;
                        screenEntity.setContent(getContent(notice));
                        screenEntity.setSize(size + "");
                        screenEntity.setTime(time);
                        screenEntity.setContent(getContent(notice));
                        //判断是否是群
                        if (notice.getReceiver().getType() == GotyeChatTargetType.GotyeChatTargetTypeUser) {
                            screenEntity.setIsGroup(false);
                        } else {
                            screenEntity.setIsGroup(true);
                            screenEntity.setID((int) notice.getReceiver().getId());
                        }
                        //下面通知弹出窗口刷新数据
                        screenEntities.set(i, screenEntity);
                        //发送广播通知刷新
                        Intent mIntent = new Intent();
                        mIntent.setAction(Constant.HUISHANG_SCREEN_ACTION);
                        mIntent.putExtra("screenEntities", (Serializable) screenEntities);
                        sendBroadcast(mIntent);
                        return;
                    }
                }
                //没有相同联系人发来的消息
                ScreenEntity screenEntity = new ScreenEntity();
                screenEntity.setName(from);
                screenEntity.setTime(time);
                screenEntity.setContent(getContent(notice));
                screenEntity.setSize("1");
                //判断是否是群
                if (notice.getReceiver().getType() == GotyeChatTargetType.GotyeChatTargetTypeUser) {
                    screenEntity.setIsGroup(false);
                } else {
                    screenEntity.setIsGroup(true);
                    screenEntity.setID((int) notice.getReceiver().getId());
                }
                screenEntities.add(screenEntity);
                //通知已显示的窗口显示消息
                Intent mIntent = new Intent();
                mIntent.setAction(Constant.HUISHANG_SCREEN_ACTION);
                mIntent.putExtra("screenEntities", (Serializable) screenEntities);
                sendBroadcast(mIntent);
            }
        }

    }


    /**
     * 发出Notification的method.
     *
     * @param iconId       图标
     * @param contentTitle 标题
     * @param activity
     * @paramcontentText 你内容
     * @author pan
     * @update 2015-5-14 下午12:01:55
     */
    @SuppressLint("NewApi")
    public void setNotiType(int iconId, String contentTitle, GotyeMessage message, Class activity, String from, int type, int messtype, boolean isGroup) {
        /*
		 * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
        String name = null;
        Intent notifyIntent = new Intent(this, Chat.class);
        if (isGroup) {
            name = GroupManager.getInstance(this).getGroupName(message.getReceiver().getId() + "");
            if (name == null) {
                name = message.getReceiver().getId() + "";
            }
            GotyeGroup gotyeGroup = new GotyeGroup(message.getReceiver().getId());
			/*notifyIntent.putExtra("group", gotyeGroup);
			notifyIntent.putExtra("messtype", 2);
			notifyIntent.putExtra("name", message.getSender().getName());
			notifyIntent.putExtra("chat_name", name);
			notifyIntent.putExtra("type", type);
			notifyIntent.putExtra("JID", message.getSender().getName());*/
            L.e("message.getReceiver().getId() = " + message.getReceiver().getId());
            notifyIntent.putExtra("JID", message.getReceiver().getId() + "");
            notifyIntent.putExtra("type", 1);
            notifyIntent.putExtra("name", name);
            notifyIntent.putExtra("messtype", 2);
            notifyIntent.putExtra("chat_name", name);
            notifyIntent.putExtra("Sign", "");
            notifyIntent.putExtra("group", gotyeGroup);
        } else {
            name = DepartmentManager.getInstance(this).getManager(message.getSender().getName());
            if (name == null) {
                name = MemberManager.getInstance(this).getMember(message.getSender().getName());
                if (name == null) {
                    name = message.getSender().getName();
                }
            }
            GotyeUser gotyeUser = new GotyeUser(message.getSender().getName());
			/*notifyIntent.putExtra("user", gotyeUser);
			notifyIntent.putExtra("messtype", 0);
			notifyIntent.putExtra("name", message.getSender().getName());
			notifyIntent.putExtra("chat_name", name);
			notifyIntent.putExtra("type", type);
			notifyIntent.putExtra("JID", message.getSender().getName());*/

            notifyIntent.putExtra("JID", message.getSender().getName());
            notifyIntent.putExtra("type", 1);
            notifyIntent.putExtra("name", name);
            notifyIntent.putExtra("messtype", 0);
            notifyIntent.putExtra("chat_name", name);
            notifyIntent.putExtra("Sign", "");
            notifyIntent.putExtra("user", gotyeUser);
        }


        PendingIntent appIntent = PendingIntent.getActivity(this, 0,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification noti = new Notification.Builder(this)
                .setContentTitle(contentTitle)
                .setContentText(getContent(message))
                .setSmallIcon(iconId)
                .setContentIntent(appIntent)
                .setTicker(getContent(message))
                .setWhen(System.currentTimeMillis())
                .build();

        try {
            //获取到头像空间的id
            Class<?> clazz = Class.forName("com.android.internal.R$id");

            Field field = clazz.getField("icon");
            field.setAccessible(true);
            int id_icon = field.getInt(null);
            L.e("id_icon = " + id_icon);
            //获取到本地文件的路径
            String pthoto;
            if (!isGroup) {
                pthoto = DepartmentManager.getInstance(this).getManagerPhoto(message.getSender().getName());
                if (pthoto == null) {
                    pthoto = MemberManager.getInstance(this).getMemberPhoto(message.getSender().getName());
                    if (pthoto == null) {
                        pthoto = "";
                    }
                }
            } else {
                pthoto = GroupManager.getInstance(this).getPhoto(message.getSender().getId() + "");
                if (pthoto == null) {
                    pthoto = "";
                }
            }

            String filePath = Constant.pathurl + MyApplication.getInstance().getCompanyID() + "/Photo/" + pthoto;
            //获取到后缀
            String end = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length()).toLowerCase();
            File file = new File(Constant.SAVE_IMG_PATH + File.separator + filePath.hashCode() + "." + end);
            //设置头像
            //L.e("bitmap = " + bitmap);
            noti.contentView.setImageViewBitmap(id_icon, FileUtils.chatBitmap(file.getAbsolutePath()));
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        if (!isAppOnForeground()) {
            startForeground(Constant.SERVICE_TO_FOREGROUND, noti);
        } else {
            if (!MyApplication.preferences.getBoolean(Constant.HUISHANG_ISMAIN_TOP, true)) {
                startForeground(Constant.SERVICE_TO_FOREGROUND, noti);
            }
        }
        //stopForeground(true);
    }


    /**
     * 判断应用程序位于堆栈的顶层
     *
     * @return
     */
    public boolean isAppOnForeground() {
        List<RunningTaskInfo> tasksInfo = mActivityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            L.i("top Activity = "
                    + tasksInfo.get(0).topActivity.getPackageName());
            // 应用程序位于堆栈的顶层
            if (mPackageName.equals(tasksInfo.get(0).topActivity
                    .getPackageName())) {
                return true;
            }
        }
        return false;
    }


    public String getContent(GotyeMessage gotyeMessage) {
        try {
            MessageData<ServiceUtil> messageData = JsonUtil.fromJson(gotyeMessage.getText(), new TypeToken<MessageData<ServiceUtil>>() {
            }.getType());
            ServiceUtil Content = messageData.getMessageContent();
            L.e("Content = " + Content);
            if (Content != null) {//判断返回的解析结果是否为空
                return Content.getTitle();
            } else {
                return gotyeMessage.getText();
            }
        } catch (Exception e) {
            // TODO: handle exception
            MessageData<String> messageData = JsonUtil.fromJson(gotyeMessage.getText(), new TypeToken<MessageData<String>>() {
            }.getType());
            if (messageData.getMessageCategory().equals(MessageType.MESSAGE_VIDEO)) {
                return "[语音]";
            } else if (messageData.getMessageCategory().equals(MessageType.MESSAGE_PHOTO)) {
                return "[图片]";
            } else if (messageData.getMessageCategory().equals(MessageType.MESSAGE_FILE)) {
                return "[文件]";
            } else {
                return messageData.getMessageContent();
            }

        }
    }

    /**
     * 让service从后台变为前台
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewApi")
    private void setNotiType(int icon, String appName, String message) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.huishangyun.Activity",
                "MainActivity");
        intent.setComponent(componentName);
        intent.setAction("Android.intent.action.MAIN");
        intent.addCategory("Android.intent.category.LAUNCHER");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 1,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        @SuppressWarnings("deprecation")
		/*Notification notif = new Notification(icon, message,
				System.currentTimeMillis());*/
                Notification notif = new Notification.Builder(this).
                setContentTitle(appName).
                setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .setSmallIcon(icon).build();
        try {
            //获取到头像空间的id
            Class<?> clazz = Class.forName("com.android.internal.R$id");

            Field field = clazz.getField("icon");
            field.setAccessible(true);
            int id_icon = field.getInt(null);
            L.e("id_icon = " + id_icon);
            //获取到本地文件的路径
            String filePath = Constant.pathurl + MyApplication.getInstance().getCompanyID() + "/Photo/"
                    + MyApplication.preferences.getString("headurl", "");
            //获取到后缀
            String end = filePath.substring(filePath.lastIndexOf(".")
                    + 1, filePath.length()).toLowerCase();
            File file = new File(Constant.SAVE_IMG_PATH + File.separator + filePath.hashCode() + "." + end);
            //设置头像
            //L.e("bitmap = " + bitmap);
            notif.contentView.setImageViewBitmap(id_icon, FileUtils.chatBitmap(file.getAbsolutePath()));

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
		
		/*notif.contentView.setImageViewBitmap(id_icon, bitmap);
		im*/
        //notif.setLatestEventInfo(this, appName, message, contentIntent);
        startForeground(Constant.SERVICE_TO_FOREGROUND, notif);
    }

}
