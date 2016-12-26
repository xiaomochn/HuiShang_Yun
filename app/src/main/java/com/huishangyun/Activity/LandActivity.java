package com.huishangyun.Activity;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Util.FileTools;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.PreferenceConstants;
import com.huishangyun.Util.PreferenceUtils;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.LoginResult;
import com.huishangyun.model.Methods;
import com.huishangyun.service.HSChatService;
import com.huishangyun.service.LocationService;
import com.huishangyun.task.ChekUpdata;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.register.ForgetPasswordActivity;
import com.huishangyun.register.RegisterMainActivity;
import com.huishangyun.service.PhoneService;
import com.huishangyun.yun.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LandActivity extends BaseActivity{
    private ImageView companyImg;
    private TextView companyName;
    private EditText loginName;
    private EditText loginPassword;
    private Button loginBtn;
    private webServiceHelp<LoginResult> mServiceHelp;
    private TextView forgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land);
        initView();
        setCompTitle(preferences.getString(Content.COMPS_NAME, "请选择或注册公司 >>"),
                preferences.getString(Content.COMPS_IMGURL, ""));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 校验SD卡
        checkMemoryCard();
        // 检测网络和版本
        validateInternet();
        // 初始化xmpp配置

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        mServiceHelp.removeOnServiceCallBack();
        super.onDestroy();
    }

    private void initView() {
        companyImg = (ImageView) findViewById(R.id.land_companyimg);
        companyName = (TextView) findViewById(R.id.land_companyname);
        loginName = (EditText) findViewById(R.id.land_name);
        loginPassword = (EditText) findViewById(R.id.land_password);
        loginBtn = (Button) findViewById(R.id.land_loginbtn);
        loginBtn.setOnClickListener(mListener);
        companyName.setOnClickListener(mListener);
        mServiceHelp = new webServiceHelp<LoginResult>(Methods.MANAGER_CHEK_LOGIN,
                new TypeToken<ZJResponse<LoginResult>>(){}.getType());
        mServiceHelp.setOnServiceCallBack(onServiceCallBack);
        loginName.setText(MyApplication.preferences.getString(Constant.USERNAME, ""));
        loginPassword.setText(MyApplication.preferences.getString(Constant.PASSWORD, ""));
        companyImg.setOnClickListener(mListener);
        forgetPassword = (TextView) findViewById(R.id.forget_password);
        forgetPassword.setOnClickListener(mListener);
    }

    /**
     * 是否满足登陆条件
     */
    private boolean isCanLogin() {
        if (!validateInternet())//无网络连接时直接返回
            return false;
        if (loginName.getText().toString() == null
                || loginName.getText().toString().equals("")) {
            showCustomToast("账号不为空!",false);
            return false;
        } else if (loginName.getText().toString().trim().length() < 2) {
            showCustomToast("账号长度低于2位",false);
            return false;
        } else if (loginPassword.getText().toString() == null
                || loginPassword.getText().toString().equals("")) {
            showCustomToast("请输入密码!",false);
            return false;
        } else if (loginPassword.getText().toString().trim().length() < 6) {
            showCustomToast("密码长度低于6位",false);
            return false;
        } else if(preferences.getInt(Content.COMPS_ID, 0) == 0){
            showCustomToast("请先选择公司",false);
            return false;
        }
        return true;
    }

    private OnClickListener mListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.land_loginbtn:
                    closeInput();
                    if (isCanLogin()) {
                        //showDialog("登陆信息验证中...");
                        MyApplication.getInstance().showDialog(LandActivity.this, true, "登陆信息验证中...");
                        mServiceHelp.start(getJson());
                    }
                    break;

                /**
                 * 设置分销商
                 */
                case R.id.land_companyname:
                    //设置分销商标识
				/*Intent intent2 = new Intent(LandActivity.this, CompanSettingActivity.class);*/
                    Intent intent2 = new Intent(LandActivity.this, RegisterMainActivity.class);
                    startActivityForResult(intent2, 0);
                    break;
                case R.id.land_companyimg:
                    Intent intent3 = new Intent(LandActivity.this, RegisterMainActivity.class);
                    startActivityForResult(intent3, 0);
                    break;
                case R.id.forget_password:
                	if (MyApplication.getInstance().getCompanyID() == 0) {
						showCustomToast("请先选择公司", false);
					} else {
						Intent intent4 = new Intent(LandActivity.this, ForgetPasswordActivity.class);
	                    startActivityForResult(intent4, 0);
					}
                	
                	break;

                default:
                    break;
            }
        }
    };

    /**
     * 获取访问的json
     * @return
     */
    private String getJson() {
        ZJRequest zjRequest = new ZJRequest();
        zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 0));
        zjRequest.setLoginName(loginName.getText().toString().trim());
        zjRequest.setPassword(loginPassword.getText().toString().trim());
        zjRequest.setVersion(getResources().getString(R.string.versionName));
        zjRequest.setImei(MyApplication.IMEI);
        zjRequest.setImsi(MyApplication.IMSI);
        zjRequest.setClient("android");
        return JsonUtil.toJson(zjRequest);
    }

    /**
     * 登录结果返回监听
     */
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
                        MyApplication.preferences.edit().putString(Constant.USERNAME, loginName.getText().toString().trim()).commit();
                        MyApplication.preferences.edit().putString(Constant.PASSWORD, loginPassword.getText().toString().trim()).commit();
                        MyApplication.preferences.edit().putBoolean(Constant.HUISHANG_SCREEN_SHOW, false).commit();
                        L.e("zjResponse.getDesc() = " + zjResponse.getDesc());
                        if (zjResponse.getDesc() != null && !zjResponse.getDesc().equals("")) {//有更新
                            MyApplication.preferences.edit().putBoolean(Constant.HUISHANG_HAVE_UPDATA, true).commit();
                            MyApplication.preferences.edit().putString(Constant.HUISHANG_CHEKNEW_URL, zjResponse.getDesc()).commit();
                        } else { //无更新
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
                        mHandler.sendEmptyMessage(HanderUtil.case1);
                        break;
                    case -3: //有强制更新
                        Message msg = new Message();
                        msg.what = HanderUtil.case3;
                        msg.obj = zjResponse.getDesc();
                        mHandler.sendMessage(msg);
                        break;

                    default: //返回其它错误
                        Message msg2 = new Message();
                        msg2.what = HanderUtil.case2;
                        msg2.obj = zjResponse.getDesc();
                        mHandler.sendMessage(msg2);
                        break;
                }
				
				/*if (zjResponse.getCode() != 0 && zjResponse.getCode() != -3) {//服务器返回错误
					
					return;
				} else if (zjResponse.getCode() == -3) { //有更新
					
					return;
				}*/

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
            //dismissDialog();
            MyApplication.getInstance().showDialog(LandActivity.this, false, "登陆信息验证中...");
            switch (msg.what) {
                case HanderUtil.case1:
                	Intent imIntent = new Intent(LandActivity.this, HSChatService.class);
					startService(imIntent);
					if (!MyApplication.isPhoneServiceRun) {
						Intent phoneIntent = new Intent(LandActivity.this, PhoneService.class);
						startService(phoneIntent);
					}
                    if (MyApplication.preferences.getInt(Constant.HUISHANG_LOCATION_TYPE, -1) == 0) {
                        Intent locationIntent = new Intent(LandActivity.this, LocationService.class);
                        startService(locationIntent);
                    }

                    Intent intent = new Intent(LandActivity.this, MainActivity.class);//GeTeLaKeActivity
                    MyApplication.isExit = false;
                    startActivity(intent);


                    finish();
                    break;

                case HanderUtil.case2:
                    showCustomToast((String) msg.obj, false);
                    break;
                case HanderUtil.case3:
                    dismissDialog();
                    new ChekUpdata(LandActivity.this).showChekUpdata((String) msg.obj, true);
                    break;

                default:
                    break;
            }
        };
    };


    /**
     * 接收设置返回后的分销商标识
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 1) {
            //返回分销商标识并下载图片（data里附带分销商标识，通过分销商标识向服务器下载图片）
            Bundle bundle = data.getExtras();
            preferences.edit().putString(Content.COMPS_IMGURL, bundle.getString("path")).commit();
            preferences.edit().putInt(Content.COMPS_ID, bundle.getInt("ID")).commit();
            preferences.edit().putString(Content.COMPS_NAME, bundle.getString("name")).commit();
            preferences.edit().putString(Content.COMPS_DOMAIN, bundle.getString("AdminDomain")).commit();
            setCompTitle(bundle.getString("name"),bundle.getString("path"));
        }
    }

    /**
     * 获取分销商logo和名称
     * @param name
     * @param path
     */
    private void setCompTitle(String name,final String path) {
        companyName.setText(name);
        companyImg.setImageResource(R.drawable.crmlogin_inlogo);
        FileTools.decodePhoto(Constant.pathurl +MyApplication.getInstance().getCompanyID() + "/Logo/" + MyApplication.LandLogSize + "/" + path, companyImg);
    }
}
