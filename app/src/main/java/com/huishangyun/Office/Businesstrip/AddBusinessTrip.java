package com.huishangyun.Office.Businesstrip;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Channel.Visit.BitmapTools;
import com.huishangyun.Channel.Visit.PhotoHelpDefine;
import com.huishangyun.Map.LocationUtil;
import com.huishangyun.Office.Attendance.OfficePhotoSkim;
import com.huishangyun.Util.Base64Util;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.model.CallSystemApp;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.Methods;
import com.huishangyun.task.UpLoadFileTask.OnUpLoadResult;
import com.huishangyun.task.UpLoadImgSignText;
import com.huishangyun.yun.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddBusinessTrip extends BaseActivity implements OnUpLoadResult {
    protected static final String TAG = null;
    private RelativeLayout back;// 返回
    private TextView setout_address;// 出发地点
    private TextView arrive_address;// 到达地点
    private LinearLayout chufariqi;
    private TextView setout_date;// 出发日期
    private LinearLayout jieshuriqi;
    private TextView arrive_date;// 结束日期
    private View view1, view2;// 分割线
    private LinearLayout zhaopian;
    private ImageView photo1;// 照片
    private ImageView photo2;
    private ImageView photo3;
    private LinearLayout beizhu;

    private TextView takephoto;// 拍照
    private TextView writenote;// 备注
    private TextView note;
    private TextView setout;// 出发
    private TextView submit;// 提交
    Calendar calendar = Calendar.getInstance();
    private BusinessTripdata list = new BusinessTripdata();
    private Double Lng;// 经度
    private Double Lat;// 维度
    private String location;// 地理位置
    private String photoPath;// 图片保存路径
    private String FileName;// 文件名
    public static List<String> Img_List = new ArrayList<String>();
    ProgressDialog pDialog;
    public static List<String> pList = new ArrayList<String>();//图片地址存储
    private String mTpye;
    private TextView replace;
    private String newSDpath;
    private String isSubmit = "0";
    private int ManagerID;//登入人id
    private int Company_ID;
    private String Desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_businesstrip_add);
        ManagerID = MyApplication.getInstance().getManagerID();
        Company_ID = MyApplication.getInstance().getCompanyID();
        //启动定位服务
        LocationUtil.startLocation(AddBusinessTrip.this, mLocationListener);
        mTpye = preferences.getString(Constant.HUISHANG_TYPE, "0");
        init();

    }

    /**
     * 提交数据
     *
     * @param startCity 出发城市
     * @param endCity   到达城市
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param picture   照片地址名称
     * @param note      备注
     * @param lat       经度
     * @param lng       维度
     * @param loc       定位地点
     */
    private void getNetData(final String startCity, final String endCity,
                            final String startTime, final String endTime, final String picture,
                            final String note, final double lat, final double lng,
                            final String loc, final int Flag) {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub

                L.e("===========>:" + getJson(startCity, endCity, startTime, endTime,
                        picture, note, lat, lng, loc, Flag));

                String result = DataUtil.callWebService(
                        Methods.SET_BUSINESSTRIP,
                        getJson(startCity, endCity, startTime, endTime,
                                picture, note, lat, lng, loc, Flag));

                L.e("返回数据：" + result);
                // 先判断网络数据是否获取成功，防止网络不好导致程序崩溃
                if (result != null) {
                    // 获取对象的Type
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        // Log.e(TAG, "code:" + jsonObject.getInt("Code"));
                        int code = jsonObject.getInt("Code");
                        Desc = jsonObject.getString("Desc");
                        L.e("Desc:" + Desc);

                        if (code == 0) {
                            mHandler.sendEmptyMessage(0);
                        } else {
                            mHandler.sendEmptyMessage(1);
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    mHandler.sendEmptyMessage(4);

                }

            }
        }.start();
    }

    /**
     * 设置json对象
     *
     * @param
     * @return
     */
    private String getJson(String startCity, String endCity, String startTime,
                           String endTime, String picture, String note, double lat,
                           double lng, String loc, int Flag) {
        if (Flag == 0) {
            list.setAction("Insert");
            list.setNote(note);
            list.setPicture(picture);
        } else if (Flag == 1) {
            list.setAction("Insert");
            list.setStartPicture(picture);
            list.setStartNote(note);
        }
        // id新增传入0
        list.setID(0);
        list.setStartCity(startCity);
        list.setEndCity(endCity);
        list.setStartTime(startTime);
        list.setEndTime(endTime);
        list.setStartLat(lat);
        list.setStartLng(lng);
        list.setStartLoc(loc);
        //提交flag为0，出发1，到达2,3为取消
        list.setFlag(Flag);
        list.setManager_ID(Integer.parseInt(preferences.getString(
                Constant.HUISHANGYUN_UID, "0")));
        ZJRequest<BusinessTripdata> zjRequest = new ZJRequest<BusinessTripdata>();
        zjRequest.setData(list);
        return JsonUtil.toJson(zjRequest);

    }

    /**
     * 初始化控件
     */
    private void init() {
        back = (RelativeLayout) findViewById(R.id.back);
        setout_address = (TextView) findViewById(R.id.setout_address);
        arrive_address = (TextView) findViewById(R.id.arrive_address);
        chufariqi = (LinearLayout) findViewById(R.id.chufariqi);
        setout_date = (TextView) findViewById(R.id.setout_date);
        jieshuriqi = (LinearLayout) findViewById(R.id.jieshuriqi);
        arrive_date = (TextView) findViewById(R.id.arrive_date);
        view1 = (View) findViewById(R.id.view1);
        view2 = (View) findViewById(R.id.view2);
        zhaopian = (LinearLayout) findViewById(R.id.zhaopian);
        photo1 = (ImageView) findViewById(R.id.photo1);
        photo2 = (ImageView) findViewById(R.id.photo2);
        photo3 = (ImageView) findViewById(R.id.photo3);
        beizhu = (LinearLayout) findViewById(R.id.beizhu);
        takephoto = (TextView) findViewById(R.id.takephoto);
        writenote = (TextView) findViewById(R.id.writenote);
        setout = (TextView) findViewById(R.id.setout);
        submit = (TextView) findViewById(R.id.submit);
        note = (TextView) findViewById(R.id.note);
        replace = (TextView) findViewById(R.id.replace);

        //初始化数据
        Img_List.clear();
        pList.clear();
        // 所有ImageView重置
        photo1.setImageResource(R.drawable.visit_img);
        photo2.setImageResource(R.drawable.visit_img);
        photo3.setImageResource(R.drawable.visit_img);
        try {
            getUiData();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            L.e("UI数据获取失败");
        }
        back.setOnClickListener(new ButtonClickListener());
        chufariqi.setOnClickListener(new ButtonClickListener());
        jieshuriqi.setOnClickListener(new ButtonClickListener());
        photo1.setOnClickListener(new ButtonClickListener());
        photo2.setOnClickListener(new ButtonClickListener());
        photo3.setOnClickListener(new ButtonClickListener());
        takephoto.setOnClickListener(new ButtonClickListener());
        writenote.setOnClickListener(new ButtonClickListener());
        setout.setOnClickListener(new ButtonClickListener());
        submit.setOnClickListener(new ButtonClickListener());
    }

    /**
     * 单击事件处理
     *
     * @author xsl
     */
    private class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:// 返回
                    isSubmit = "0";
                    saveUiDate();
                    finish();
                    break;
                case R.id.chufariqi:// 出发日期
                    dateSelect(setout_date, "");
                    break;
                case R.id.jieshuriqi:// 结束日期
                    if (!(setout_date.getText().toString().trim()).equals("")) {
                        dateSelect(arrive_date, backDate(setout_date.getText().toString().trim()));
                        L.e("日期：" + backDate(setout_date.getText().toString().trim()));
                    } else {
                        dateSelect(arrive_date, "");
                    }
                    break;
                case R.id.photo1:// 照片

                    if (Img_List.size() >= 1) {
                        Intent intent1 = new Intent(AddBusinessTrip.this, OfficePhotoSkim.class);
                        intent1.putExtra("index", 2);
                        intent1.putExtra("imgselect", 0);
                        startActivityForResult(intent1, 1);
                    } else {

                        FileName = getSystemTime() + ".jpg";
                        photoPath = Constant.SAVE_IMG_PATH + File.separator
                                + FileName;
                        CallSystemApp.callCamera(AddBusinessTrip.this, photoPath);
                    }

                    break;
                case R.id.photo2:// 照片2

                    if (Img_List.size() >= 2) {
                        Intent intent1 = new Intent(AddBusinessTrip.this, OfficePhotoSkim.class);
                        intent1.putExtra("index", 2);
                        intent1.putExtra("imgselect", 1);
                        startActivityForResult(intent1, 1);
                    } else {

                        FileName = getSystemTime() + ".jpg";
                        photoPath = Constant.SAVE_IMG_PATH + File.separator
                                + FileName;
                        CallSystemApp.callCamera(AddBusinessTrip.this, photoPath);

                    }
                    break;
                case R.id.photo3:// 照片3

                    if (Img_List.size() >= 3) {
                        Intent intent1 = new Intent(AddBusinessTrip.this, OfficePhotoSkim.class);
                        intent1.putExtra("index", 2);
                        intent1.putExtra("imgselect", 2);
                        startActivityForResult(intent1, 1);
                    } else {

                        FileName = getSystemTime() + ".jpg";
                        photoPath = Constant.SAVE_IMG_PATH + File.separator
                                + FileName;
                        CallSystemApp.callCamera(AddBusinessTrip.this, photoPath);

                    }
                    break;
                case R.id.takephoto:// 拍照
                    if (Img_List.size() <= 0) {
                        FileName = getSystemTime() + ".jpg";
                        photoPath = Constant.SAVE_IMG_PATH + File.separator
                                + FileName;
                        CallSystemApp.callCamera(AddBusinessTrip.this, photoPath);
                    }

                    break;
                case R.id.writenote:// 备注
                    beizhu.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.VISIBLE);
                    break;
                case R.id.setout:// 出发
                    String imgString = "";
                    if (pList.size() > 0) {
                        //imgString = pList.get(0);
                        for (int i = 0; i < pList.size(); i++) {
                            if (i>0) {
                                imgString = imgString + "#";
                            }
                            imgString = imgString + pList.get(i);
                        }
                    }


                    if (startIsWrite() && compareDate()) {
                        getNetData(setout_address.getText().toString().trim(),
                                arrive_address.getText().toString().trim(),
                                setout_date.getText().toString().trim(),
                                arrive_date.getText().toString().trim(), imgString,
                                note.getText().toString().trim(), Lng, Lat, location, 1);
                    }
                    break;

                case R.id.submit:// 提交
                    String imgString1 = "";
                    if (pList.size() > 0) {
                        //imgString = pList.get(0);
                        for (int i = 0; i < pList.size(); i++) {
                            if (i>0) {
                                imgString1 = imgString1 + "#";
                            }
                            imgString1 = imgString1 + pList.get(i);
                        }
                    }

                    if (isWrite()) {
                        getNetData(setout_address.getText().toString().trim(),
                                arrive_address.getText().toString().trim(),
                                setout_date.getText().toString().trim(),
                                arrive_date.getText().toString().trim(), imgString1,
                                note.getText().toString().trim(), Lng, Lat, location, 0);
                    }

                    break;
                default:
                    break;
            }

        }

    }

    /**
     * 日期选择对话框
     * 选择出发日期和结束日期
     *
     * @param widgetname 控件名称
     */
    private void dateSelect(final TextView widgetname, String time) {
        String sysTimeStr;
        final long sysTime = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        if (time.equals("") || time == null) {
            sysTimeStr = format.format(new Date(sysTime));
        } else {
            sysTimeStr = time;
        }

        final DefineDatePickDialog dateTimePicKDialog = new DefineDatePickDialog(
                AddBusinessTrip.this, sysTimeStr);//初始时间
        //dateTimePicKDialog.dateTimePicKDialog(widgetname);
        AlertDialog alertDialog = dateTimePicKDialog.dateTimePicKDialog()
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        try {
                            SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            String time1 = sf1.format(new Date(sysTime));//当前时间
                            Date d1 = sf1.parse(time1);
                            Date d2 = sf1.parse(dateTimePicKDialog.getDataTime());
                            long diff1 = d2.getTime() - d1.getTime();
                            long days1 = diff1 / (1000 * 60 * 60 * 24);
                            if (days1 >= 0) {
                                if (widgetname == setout_date) {
                                    if (arrive_date.getText().toString().trim().equals("")) {
                                        widgetname.setText(dateTimePicKDialog.getDataTime());
                                    } else {
                                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                        Date d01 = df.parse(arrive_date.getText().toString());
                                        Date d02 = d2;
                                        long diff = d02.getTime() - d01.getTime();
                                        long days = diff / (1000 * 60 * 60 * 24);
                                        if (days <= 0) {
                                            widgetname.setText(dateTimePicKDialog.getDataTime());
                                        } else {
                                            showDialog("选择的开始日期不能大于到达日期！");
                                        }
                                    }


                                } else if (widgetname == arrive_date) {
                                    if (setout_date.getText().toString().trim().equals("")) {

                                        widgetname.setText(dateTimePicKDialog.getDataTime());

                                    } else {
                                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                        Date d01 = df.parse(setout_date.getText().toString());
                                        Date d02 = df.parse(dateTimePicKDialog.getDataTime());
                                        long diff = d02.getTime() - d01.getTime();
                                        //比较大小不按天算，精确到毫秒
//							long days = diff/ (1000 * 60 * 60 * 24);
                                        if (diff >= 0) {
                                            widgetname.setText(dateTimePicKDialog.getDataTime());
                                        } else {
                                            showDialog("选择的到达日期不能小于开始日期！");
                                        }
                                    }

                                }
                            } else {
                                showDialog("请选择今天或以后的日期！");
                            }

                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        dialog.dismiss();
                    }
                }).
                        setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        }).show();
        dateTimePicKDialog.setAlertDialog(alertDialog);

    }

    /**
     * 但点出发时，对比开始日期是否是今天，如果不是，要求重新选择出发日期。
     *
     * @return
     */
    private boolean compareDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Date date1 = new Date(year - 1900, month, day); // 获取时间转换为Date对象
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String time = sf.format(date1);
        try {
            final Date d1 = sf.parse(time);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date d2 = df.parse(setout_date.getText().toString());

            long diff = d2.getTime() - d1.getTime();
            long days = diff / (1000 * 60 * 60 * 24);
            if (days == 0) {
                return true;

            } else {
                showDialog("请选择今天作为出发日期！");
                return false;
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 判断是否输入完成
     *
     * @return
     */
    private boolean isWrite() {
        if (setout_address.getText().toString().trim().equals("")) {
            showDialog("请填写出发地点！");
            return false;
        }
        if (arrive_address.getText().toString().trim().equals("")) {
            showDialog("请填写到达地点！");
            return false;
        }

        if (setout_date.getText().toString().trim().equals("")) {
            showDialog("请选择出发时间！");
            return false;
        }

        if (arrive_date.getText().toString().trim().equals("")) {
            showDialog("请选择到达时间！");
            return false;
        }

        return true;

    }


    /**
     * 判断是否输入完成
     *
     * @return
     */
    private boolean startIsWrite() {
        if (setout_address.getText().toString().trim().equals("")) {
            showDialog("请填写出发地点！");
            return false;
        }
        if (arrive_address.getText().toString().trim().equals("")) {
            showDialog("请填写到达地点！");
            return false;
        }

        if (setout_date.getText().toString().trim().equals("")) {
            showDialog("请选择出发时间！");
            return false;
        }

        if (arrive_date.getText().toString().trim().equals("")) {
            showDialog("请选择到达时间！");
            return false;
        }

//        if (pList.size() <= 0) {
//            showDialog("请拍照后出发！");
//            return false;
//        }
//        if (note.getText().toString().trim().equals("")) {
//            showDialog("请填写出发备注！");
//            return false;
//        }//易注释掉

        return true;

    }

    public void showDialog(String TXT) {
        new ClueCustomToast().showToast(AddBusinessTrip.this,
                R.drawable.toast_warn, TXT);

    }

    /**
     * 异步更新数据
     */
    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    new ClueCustomToast().showToast(AddBusinessTrip.this,
                            R.drawable.toast_sucess, "提交成功！");

                    Intent intent = new Intent();
                    intent.setAction("BUSINESSTRIP_LIST_REFRESH");
                    if (mTpye.equals("1")) {

                        intent.putExtra("mflag", 1);
                        AddBusinessTrip.this.sendBroadcast(intent);
                    } else {
                        intent.putExtra("mflag", 2);
                        AddBusinessTrip.this.sendBroadcast(intent);
                    }
                    isSubmit = "1";
                    saveUiDate();
                    finish();

                    break;
                case 1:
                    ClueCustomToast.showToast(AddBusinessTrip.this,
                            R.drawable.toast_defeat, Desc);
                    break;
                case 2:

                    // 从sd卡里获取图片进行压缩处理//photoPath为完整路径
                /*Bitmap bitmap = new BitmapTools().getBitmap(newSDpath, 120, 240);
				// 对图片进行剪切成100*100后显示
				Bitmap bitmap1 = new BitmapTools().cutBitmap(bitmap, 100, 100);
				photo1.setImageBitmap(bitmap1);
				Img_List.add(newSDpath);*/
                    Bitmap bitmap = new BitmapTools().getBitmap(newSDpath, 120, 240);
                    // 对图片进行剪切成100*100后显示
                    Bitmap bitmap1 = new BitmapTools().cutBitmap(bitmap, 100, 100);
                    if (Img_List.size() == 1) {
                        photo2.setImageBitmap(bitmap1);

//            		image2.setImageBitmap( BitmapFactory.decodeFile(path,null));
                    } else if (Img_List.size() == 2) {
                        photo3.setImageBitmap(bitmap1);

                    } else {
                        photo1.setImageBitmap(bitmap1);
                    }

                    Img_List.add(newSDpath);

                    break;

                case 3:
                    // 传图片到服务器
                    File file = (File) msg.obj;
                    newSDpath = file.getAbsolutePath();
                    setImageToNet(file.getAbsolutePath());
                    break;
                case 4:
                    showDialog("未获得任何数据，请检查网络连接！");
                    break;
                case 5:
                    //对图片进行压缩
                    new Thread() {
                        public void run() {
                            File file;
                            String newPath = Constant.SAVE_IMG_PATH + File.separator + getSystemTime() + ".jpg";
                            try {
                                file = PhotoHelpDefine.compressImage(photoPath, newPath, MyApplication.photoWidth, MyApplication.photoHeigh);
//        					file = PhotoHelp.compressImage(photoPath, newPath);
                                Message msg = new Message();
                                msg.obj = file;
                                msg.what = 3;
                                mHandler.sendMessage(msg);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                mHandler.sendEmptyMessage(6);
                                e.printStackTrace();
                                return;
                            }
                        }

                        ;
                    }.start();
                    break;
                case 6:
                    pDialog.dismiss();
                    showCustomToast("上传失败", false);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    /**
     * activity从暂停后重新启动改变拍照图片显示
     */
    protected void onResume() {
        super.onResume();
        // 所有ImageView重置
        photo1.setImageResource(R.drawable.visit_img);
        photo2.setImageResource(R.drawable.visit_img);
        photo3.setImageResource(R.drawable.visit_img);
		/*if (Img_List.size() >= 1) {
			// 从sd卡里获取图片进行压缩处理
			Bitmap bitmap = new BitmapTools().getBitmap(Img_List.get(0), 480, 800);
			// 对图片进行剪切成100*100后显示
			Bitmap bitmap1 = new BitmapTools().cutBitmap(bitmap, 100, 100);
			photo1.setImageBitmap(bitmap1);

		}*/
        if (Img_List.size() >= 1) {
            //从sd卡里获取图片进行压缩处理
            Bitmap bitmap = new BitmapTools().getBitmap(Img_List.get(0), 480, 800);
            //对图片进行剪切成100*100后显示
            Bitmap bitmap1 = new BitmapTools().cutBitmap(bitmap, 100, 100);
            photo1.setImageBitmap(bitmap1);
            if (Img_List.size() >= 2) {
                //从sd卡里获取图片进行压缩处理
                Bitmap bitmap2 = new BitmapTools().getBitmap(Img_List.get(1), 480, 800);
                //对图片进行剪切成100*100后显示
                Bitmap bitmap3 = new BitmapTools().cutBitmap(bitmap2, 100, 100);
                photo2.setImageBitmap(bitmap3);
                if (Img_List.size() >= 3) {
                    //从sd卡里获取图片进行压缩处理
                    Bitmap bitmap4 = new BitmapTools().getBitmap(Img_List.get(1), 480, 800);
                    //对图片进行剪切成100*100后显示
                    Bitmap bitmap5 = new BitmapTools().cutBitmap(bitmap4, 100, 100);
                    photo3.setImageBitmap(bitmap5);
                }
            }
        }

    }

    ;

    /**
     * 对返回数据处理
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        L.i("resultCode==" + resultCode + "requestCode==" + requestCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CallSystemApp.EXTRA_TAKE_PHOTOS:// 拍照返回
                    L.d("拍照返回");
                    mHandler.sendEmptyMessage(5);
                    pDialog = ProgressDialog
                            .show(this, "请等待...", "正在上传图片...", true);
                    pDialog.setCancelable(true);

                    break;
                case 1:
                    // 重新调用相机拍照
                    FileName = getSystemTime() + ".jpg";
                    photoPath = Constant.SAVE_IMG_PATH + File.separator + FileName;
                    CallSystemApp.callCamera(AddBusinessTrip.this, photoPath);

                    break;

                default:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 传图片到服务器
     */
    private void setImageToNet(String path) {
        //获得系统时间2014-09-20
        SimpleDateFormat  formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date  curDate=new  Date(System.currentTimeMillis());//获取当前时间
        String  time =formatter.format(curDate);

        String companyID = preferences.getInt(Content.COMPS_ID, 1016) + "";
        String modulePage = "Travel";//图片服务器文件夹名称
        String picData = "";
        try {
            picData = Base64Util.encodeBase64File(path);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        UpLoadImgSignText upLoadImgSignText = new UpLoadImgSignText(picData,
                modulePage, FileName, companyID, time + "\n" + location);
        upLoadImgSignText.setUpLoadResult(AddBusinessTrip.this);
        new Thread(upLoadImgSignText).start();
    }

    @Override
    public void onUpLoadResult(String FileName, String FilePath,
                               boolean isSuccess) {
        // TODO Auto-generated method stub

        if (isSuccess) {
            pList.add(FilePath);
            // 图片上传成功
            L.i("------------->:" + FilePath);
            pDialog.dismiss();
            new ClueCustomToast().showToast(AddBusinessTrip.this,
                    R.drawable.toast_sucess, "图片上传成功");
            view1.setVisibility(View.VISIBLE);
            zhaopian.setVisibility(View.VISIBLE);
            mHandler.sendEmptyMessage(2);

        } else {
            // 图片上传失败
            L.i("------------->:" + "上传失败");
            new ClueCustomToast().showToast(AddBusinessTrip.this,
                    R.drawable.toast_defeat, "图片上传失败！");
            pDialog.dismiss();
        }
    }

    /**
     * 定位监听
     */
    private BDLocationListener mLocationListener = new BDLocationListener() {

        @Override
        public void onReceivePoi(BDLocation arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub

            Lng = (Double) location.getLatitude();// 经度
            Lat = (Double) location.getLongitude();// 维度
            LocationUtil.stopLocation();
            LocationUtil.startReverseGeoCode(Lng, Lat,
                    getGeoCoderResultListener);
        }
    };


    OnGetGeoCoderResultListener getGeoCoderResultListener = new OnGetGeoCoderResultListener() {

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            // TODO Auto-generated method stub
            if (result == null) {
                return;
            }

            location = result.getAddress();// 地理位置
            if (location.equals("null")) {
                location = "";
            }
            LocationUtil.stopReverseGeoCode();
        }

        @Override
        public void onGetGeoCodeResult(GeoCodeResult arg0) {
            // TODO Auto-generated method stub

        }
    };

    /**
     * 字符串分割再组合
     *
     * @param date
     * @return
     */
    private String backDate(String date) {
        String a = date.substring(0, 4);
        String b = date.substring(5, 7);
        String c = date.substring(8, 10);
        String d = date.substring(11, 16);
        return a + "年" + b + "月" + c + "日" + " " + d;
    }

    /**
     * 保存ui界面数据
     */
    private void saveUiDate() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("isSubmit", isSubmit);
        map.put("SetOutAddress", setout_address.getText().toString().trim());
        map.put("ArriveAddress", arrive_address.getText().toString().trim());
        map.put("SetOutDate", setout_date.getText().toString().trim());
        map.put("ArriveDate", arrive_date.getText().toString().trim());
        map.put("note", note.getText().toString().trim());
        if (Img_List.size() > 0 && pList.size() > 0) {
            String PhotoPath = "";
            String PhotoUrl = "";
            for (int i = 0; i < Img_List.size(); i++) {
                if (i > 0) {
                    PhotoPath = PhotoPath + "#";
                    PhotoUrl = PhotoUrl + "#";
                }
                PhotoPath = PhotoPath + Img_List.get(i);
                PhotoUrl = PhotoUrl + pList.get(i);
            }
            map.put("PhotoPath", PhotoPath);
            map.put("PhotoUrl", PhotoUrl);
        }

        boolean flag = BusinessSharedPrefreces.saveData(getContext(), "BussinessTripData" + Company_ID + ManagerID, map);
        if (flag) {
            L.e("出差新增UI数据保存成功!");
        } else {
            L.e("出差新增UI数据保存失败!");
        }
    }

    /**
     * 获取ui界面数据，并在UI上初始化
     */
    private void getUiData() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map = BusinessSharedPrefreces.getData(getContext(), "BussinessTripData" + Company_ID + ManagerID);
        if (map.size() > 0) {
            if ((map.get("isSubmit")).equals("0")) {
                setout_address.setText(map.get("SetOutAddress") + "");
                arrive_address.setText(map.get("ArriveAddress") + "");
                setout_date.setText(map.get("SetOutDate") + "");
                arrive_date.setText(map.get("ArriveDate") + "");
                if (!(map.get("note")).equals("")) {
                    note.setText(map.get("note") + "");
                    beizhu.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.VISIBLE);
                }
                L.e("PhotoPath:" + map.get("PhotoPath"));
                L.e("PhotoUrl:" + map.get("PhotoUrl"));
                String PhotoPath = (String) map.get("PhotoPath");
                String PhotoUrl = (String) map.get("PhotoUrl");
                if (PhotoPath != null && PhotoUrl != null && PhotoPath.length() > 0 && PhotoUrl.length() > 0
                        && !PhotoPath.equals("null") && !PhotoUrl.equals("null")) {
                    String PhotoPaths[] = PhotoPath.split("#");
                    String PhotoUrls[] = PhotoUrl.split("#");
                    for (int i = 0; i < PhotoPaths.length; i++) {
                        Img_List.add(PhotoPaths[i]);
                        pList.add(PhotoUrls[i]);
                        if (i == 0) {
                            Bitmap bitmap = new BitmapTools().getBitmap(Img_List.get(0), 480, 800);
                            // 对图片进行剪切成100*100后显示
                            bitmap = new BitmapTools().cutBitmap(bitmap, 100, 100);
                            photo1.setImageBitmap(bitmap);
                        } else if (i == 1) {
                            Bitmap bitmap = new BitmapTools().getBitmap(Img_List.get(1), 480, 800);
                            // 对图片进行剪切成100*100后显示
                            bitmap = new BitmapTools().cutBitmap(bitmap, 100, 100);
                            photo2.setImageBitmap(bitmap);
                        } else {
                            Bitmap bitmap = new BitmapTools().getBitmap(Img_List.get(2), 480, 800);
                            // 对图片进行剪切成100*100后显示
                            bitmap = new BitmapTools().cutBitmap(bitmap, 100, 100);
                            photo2.setImageBitmap(bitmap);
                        }
                    }


                    zhaopian.setVisibility(View.VISIBLE);
                    view1.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        LocationUtil.stopLocation();
        LocationUtil.stopReverseGeoCode();
        if (isSubmit.equals("0")) {
            isSubmit = "0";
            saveUiDate();
        } else if (isSubmit.equals("1")) {
            isSubmit = "1";
            BusinessSharedPrefreces.ClearData(getContext(), "BussinessTripData" + Company_ID + ManagerID);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        saveUiDate();
        super.onBackPressed();
    }
}
