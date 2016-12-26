package com.huishangyun.Activity;


import com.google.gson.reflect.TypeToken;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Fragment.SettingFragment;
import com.huishangyun.Office.Approval.MainApproval;
import com.huishangyun.Util.FileTools;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.model.Constant;
import com.huishangyun.model.LoginResult;
import com.huishangyun.service.LocationService;
import com.huishangyun.service.PhoneService;
import com.huishangyun.task.ChekUpdata;
import com.huishangyun.yindao.GuideActivity;
import com.huishangyun.model.Content;
import com.huishangyun.model.Methods;
import com.huishangyun.service.HSChatService;
import com.huishangyun.yun.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

public class IndexActivity extends BaseActivity {
    private SharedPreferences preferences;
    private webServiceHelp<LoginResult> mServiceHelp;
    private ImageView index_img;
    private boolean isFast = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        Uri uri = getIntent().getData();
//        if (uri != null) {
//            if (MainActivity.act != null) {
//                MainActivity.act.finish();
//            }
//            String host = uri.getHost();
//            if (host.equals("huishangyun.com")) {
//                String model = uri.getQueryParameter("model");
//                String id = uri.getQueryParameter("id");
//                switch (model) {
//                    case "action":
//                        break;
//                    case "worklog":
//                        Intent intent = new Intent(IndexActivity.this, MainActivity.class);
//                        intent.putExtra("id", id);
//                        startActivity(intent);
//                        break;
//                    case "order":
//                        break;
//                }
//            }
//        } else {
            if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            /*finish();
            return;*/
                isFast = false;
            }
            index_img = (ImageView) findViewById(R.id.index_img);
            DisplayMetrics mDisplayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
            MyApplication.widthPixels = mDisplayMetrics.widthPixels;
            MyApplication.heightPixels = mDisplayMetrics.heightPixels;
            MyApplication.densityDpi = mDisplayMetrics.densityDpi;
            if (MyApplication.densityDpi <= 160) {
                MyApplication.LandLogSize = "ldpi";
            } else if (MyApplication.densityDpi <= 240) {
                MyApplication.LandLogSize = "hdpi";
            } else if (MyApplication.densityDpi <= 320) {
                MyApplication.LandLogSize = "xhdpi";
            } else {
                MyApplication.LandLogSize = "xxhdpi";
            }
            L.e("densityDpi = " + mDisplayMetrics.densityDpi);
            L.e("xdpi = " + mDisplayMetrics.xdpi);
            while (MyApplication.widthPixels == 0
                    || MyApplication.heightPixels == 0) {
                //死循环，防止屏幕数据获取失败
                DisplayMetrics mDisplayMetrics1 = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics1);
                MyApplication.widthPixels = mDisplayMetrics1.widthPixels;
                MyApplication.heightPixels = mDisplayMetrics1.heightPixels;
            }
            L.d("Width :" + MyApplication.widthPixels);
            L.d("Height:" + MyApplication.heightPixels);
            preferences = getSharedPreferences(Constant.LOGIN_SET, 0);
            //new Handler().postDelayed(runnable, 3000);
            mServiceHelp = new webServiceHelp<LoginResult>(Methods.MANAGER_CHEK_LOGIN,
                    new TypeToken<ZJResponse<LoginResult>>() {
                    }.getType());
            mServiceHelp.setOnServiceCallBack(onServiceCallBack);

            if (checkMemoryCard() && validateInternet()) {
                new Thread(runnable).start();
                String imgurl = MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_INDEX_IMGURL, "");
                String imggo = MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_INDEX_GOURL, "");
                if (!imgurl.equals("")) {
                    //加载图片
                /*ImageLoader.getInstance().displayImage("http://img.huishangyun.com/UploadFile/huishang/AD/" + imgurl,
						index_img, MyApplication.getInstance().getIndexOptions());*/
                    FileTools.decodeImage2(Constant.pathurl+"AD/" + imgurl,
                            index_img);
                }
            }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (mServiceHelp != null) {
            mServiceHelp.removeOnServiceCallBack();
        }

        super.onDestroy();
    }

    private webServiceHelp.OnServiceCallBack<LoginResult> onServiceCallBack = new webServiceHelp.OnServiceCallBack<LoginResult>() {

        @Override
        public void onServiceCallBack(boolean haveCallBack,
                                      ZJResponse<LoginResult> zjResponse) {
            // TODO Auto-generated method stub
            if (haveCallBack && zjResponse != null) {

                switch (zjResponse.getCode()) {
                    case 0:
                        LoginResult loginResult = zjResponse.getResult();
                        MyApplication.preferences.edit().putString(Constant.XMPP_LOGIN_NAME, loginResult.getOFUserName()).commit();
                        MyApplication.preferences.edit().putString(Constant.XMPP_LOGIN_PASSWORD, loginResult.getOFPassword()).commit();
                        MyApplication.preferences.edit().putString(Constant.HUISHANGYUN_UID, loginResult.getID() + "").commit();
                        MyApplication.preferences.edit().putInt(Constant.HUISHANG_DEPARTMENT_ID, loginResult.getDepartmentID()).commit();
                        MyApplication.preferences.edit().putString(Constant.HUISHANG_DEPARTMENT_NAME, loginResult.getDepartmentName()).commit();
                        MyApplication.preferences.edit().putString(Constant.XMPP_MY_REAlNAME, loginResult.getRealName()).commit();
                        MyApplication.preferences.edit().putString("headurl", loginResult.getPhoto()).commit();
                        MyApplication.preferences.edit().putString(Constant.HUISHANG_TYPE, loginResult.getType() + "").commit();
                        MyApplication.preferences.edit().putBoolean(Constant.HUISHANG_SCREEN_SHOW, false).commit();
                        MyApplication.isExit = false;
                        L.e("zjResponse.getDesc() = " + zjResponse.getDesc());
                        if (zjResponse.getDesc() != null && !zjResponse.getDesc().equals("")) {//有更新
                            MyApplication.preferences.edit().putBoolean(Constant.HUISHANG_HAVE_UPDATA, true).commit();
                            MyApplication.preferences.edit().putString(Constant.HUISHANG_CHEKNEW_URL, zjResponse.getDesc()).commit();
                        } else {//无更新
                            MyApplication.preferences.edit().putBoolean(Constant.HUISHANG_HAVE_UPDATA, false).commit();
                            MyApplication.preferences.edit().putString(Constant.HUISHANG_CHEKNEW_URL, "").commit();
                        }

                        if (loginResult.getLbsConfig() != null) {
                            LoginResult.LbsConfigs lbsConfigs = loginResult.getLbsConfig();
                            MyApplication.preferences.edit().putInt(Constant.HUISHANG_LOCATION_TYPE, lbsConfigs.getLocationType()).commit();
                            MyApplication.preferences.edit().putString(Constant.HUISHANG_LOCATION_START_DATA, lbsConfigs.getStartDate()).commit();
                            MyApplication.preferences.edit().putString(Constant.HUISHANG_LOCATION_END_DATA, lbsConfigs.getEndDate()).commit();
                            MyApplication.preferences.edit().putString(Constant.HUISHANG_LOCATION_WEEK, lbsConfigs.getWeekDay()).commit();
                            MyApplication.preferences.edit().putInt(Constant.HUISHANG_LOCATION_FERQUENCY, lbsConfigs.getFrequency()).commit();
                            MyApplication.preferences.edit().putString(Constant.HUISHANG_LOCATION_TIMELIST, lbsConfigs.getTimeList()).commit();
                        }

                        Intent imIntent = new Intent(IndexActivity.this, HSChatService.class);
                        startService(imIntent);
                        if (!MyApplication.isPhoneServiceRun) {
                            Intent phoneIntent = new Intent(IndexActivity.this, PhoneService.class);
                            startService(phoneIntent);
                        }
                        if (MyApplication.preferences.getInt(Constant.HUISHANG_LOCATION_TYPE, -1) == 0) {
                            Intent locationIntent = new Intent(IndexActivity.this, LocationService.class);
                            startService(locationIntent);
                        }

                        Intent intent = new Intent(IndexActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case -3: //有强制更新
                        Message msg = new Message();
                        msg.what = HanderUtil.case3;
                        msg.obj = zjResponse.getDesc();
                        mHandler.sendMessage(msg);
                        break;
                    default://返回其它错误信息
                        Message msg2 = new Message();
                        msg2.what = HanderUtil.case2;
                        msg2.obj = zjResponse.getDesc();
                        mHandler.sendMessage(msg2);
                        break;
                }

            } else {
                Message msg = new Message();
                msg.what = HanderUtil.case2;
                msg.obj = "无法连接到服务器";
                mHandler.sendMessage(msg);
                return;
            }
        }
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case HanderUtil.case1:
                    L.e("getJson() = " + getJson());
                    mServiceHelp.start(getJson());
                    break;
                case HanderUtil.case2:
                    showCustomToast((String) msg.obj, false);
                    Intent intent = new Intent(IndexActivity.this, LandActivity.class);
                    startActivity(intent);
                    finish();
                    break;

                case HanderUtil.case3:
                    new ChekUpdata(IndexActivity.this).showChekUpdata((String) msg.obj, true);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    /**
     * 获取访问的json
     *
     * @return
     */
    private String getJson() {
        ZJRequest zjRequest = new ZJRequest();
        zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 0));
        zjRequest.setLoginName(MyApplication.preferences.getString(Constant.USERNAME, ""));
        zjRequest.setPassword(MyApplication.preferences.getString(Constant.PASSWORD, ""));
        zjRequest.setImei(MyApplication.IMEI);
        zjRequest.setImsi(MyApplication.IMSI);
        zjRequest.setVersion(getResources().getString(R.string.versionName));
        zjRequest.setClient("android");
        return JsonUtil.toJson(zjRequest);
    }

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
				/*if (PingService()) {
					mHandler.sendEmptyMessage(HanderUtil.case1);
				}*/
                Thread.sleep(3000);
                if (!isFast) {
                    L.e("出\n大\n事\n了\n，\n是\n这\n边\n进\n来\n的");
                    finish();
                    return;
                }
                if (!MyApplication.preferences.getBoolean(Constant.HUISHANG_YINGDAO, false)) {
                    Intent mIntent = new Intent(IndexActivity.this, GuideActivity.class);
                    startActivity(mIntent);
                    finish();
                } else {
                    if (MyApplication.preferences.getString(Constant.USERNAME, "").equals("") ||
                            MyApplication.preferences.getString(Constant.PASSWORD, "").equals("")) {
                        Intent intent = new Intent();
                        intent.putExtra("Index", true);
                        intent.setClass(IndexActivity.this, LandActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        mHandler.sendEmptyMessage(HanderUtil.case1);
                    }
                }


            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };
}
