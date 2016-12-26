package com.huishangyun.Channel.Visit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Clues.ClueCustomToast;
import com.huishangyun.Channel.Clues.ShowDialogs;
import com.huishangyun.Channel.Orders.Common;
import com.huishangyun.Channel.Plan.CustomersListActivity;
import com.huishangyun.Channel.Plan.SortModel;
import com.huishangyun.Interface.OnDialogDown;
import com.huishangyun.Map.LocationUtil;
import com.huishangyun.Util.Base64Util;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.T;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.manager.VisitManager;
import com.huishangyun.model.CallSystemApp;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.Methods;
import com.huishangyun.task.UpLoadFileTask;
import com.huishangyun.task.UpLoadImgSignText;
import com.huishangyun.yun.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 新增拜访界面
 *
 * @author xsl
 */
public class VisitNewAdd extends BaseActivity implements UpLoadFileTask.OnUpLoadResult, OnDialogDown {

    protected static final String TAG = null;
    private LinearLayout backLayout;//返回
    private ImageView boxImg;//补办的方格图片
    private TextView dateTxt;// 补办的日期
    private EditText note;//描述
    private TextView submitTxt;//提交
    private TextView client;//客户
    private RelativeLayout lineLayout;
    // 分别是客户、标签、补报、定位、日期布局
    private RelativeLayout clientLayout, visit_labels_layout, buBaoLayout,
            dateLayout;
    private LinearLayout locationLayout;
    private ImageView image1;//第一张图片
    private ImageView image2;//第二张图片
    private ImageView image3;//第三张图片
    private boolean isBuBao = true;//判断是否补报
    private boolean IsAdd;
    private Calendar calendar = Calendar.getInstance();
    Dialog dialog = null;//创建日期对话框
    ArrayList<SortModel> arrayList = new ArrayList<SortModel>();

    private String photoPath;//图片保存路径
    private String FileName;//文件名
    public static List<String> Img_List = new ArrayList<String>();
    private int Img_Index1, Img_Index2, Img_Index3;
    private VistList list = new VistList();
    public static List<String> pList = new ArrayList<String>();//图片地址存储
    ProgressDialog pDialog;
    String time;
    String buban_time;
    private TextView txt_sign_loc;//定位地址
    private String Loc;
    private Double Lng = 0.0;//经度
    private Double Lat = 0.0;//维度
    Date d1;
    private int Member_ID;//客户id
    private String MemberName;//客户名称
    private int ManagerID;
    VisitHostoryList visitList = new VisitHostoryList();
    private String newSDpath;//压缩后的sd卡路径

    //标签
    private ArrayList<Dialog_Visit> ItemLists = new ArrayList<Dialog_Visit>();
    private static final int SOURCE_DIALOG = 1;

    private webServiceHelp<VisitTag> webHelp;//获取标签主题数据
    private List<VisitTag> ThemList = new ArrayList<VisitTag>();
    private TextView txt_tags;//接收标签名
    private TextView txt_tagsid;//标签ID

    private double SignLat;//签到维度
    private double SignLng;//签到经度
    private TextView txt_sign_time;//签到时间
    private RelativeLayout visit_sign_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_new);
        ManagerID = MyApplication.getInstance().getManagerID();
        visitList.setManagerID(ManagerID);
        //接收传值
        Intent intent = getIntent();
        Member_ID = intent.getIntExtra("Member_ID", 0);
        MemberName = intent.getStringExtra("MemberName");
        initView();

    }

    private void setNetData(final int Member_ID, final String TagID, final String Tags, final String Note, final boolean IsAdd,
                            final String BelongDate, final Double Lat, final Double Lng,
                            final String Loc, final String Picture, final int Manager_ID, final Double SignLat, final Double SignLng, final String SignTime) {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                String jsonText = getJson(Member_ID, TagID, Tags, Note, IsAdd, BelongDate, Lat, Lng, Loc, Picture, Manager_ID, SignLat, SignLng, SignTime);

                String result = DataUtil.callWebService(Methods.VISIT_CREATE, jsonText);
                //先判断网络数据是否获取成功，防止网络不好导致程序崩溃
                if (result != null) {
                    // 获取对象的Type
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int code = jsonObject.getInt("Code");
                        //Log.e("TAGS", "code------:" + code);
                        //假如code返回为0则表示删除成功,否则为失败
                        if (code == 0) {
                            mHandler.sendEmptyMessage(0);
                        } else {
                            mHandler.sendEmptyMessage(5);
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    mHandler.sendEmptyMessage(5);

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
    private String getJson(int Member_ID, String TagID, String Tags, String Note,
                           boolean IsAdd, String BelongDate, Double Lat, Double Lng,
                           String Loc, String Picture, int Manager_ID, Double SignLat, Double SignLng, String SignTime) {
        //Log.e("TAGS","Lng="+Lng+","+Lat);
        list.setAction("Insert");
        //id新增传入0
        list.setID(0);
        list.setMember_ID(Member_ID);
        list.setTagID(TagID);
        list.setTags(Tags);
        list.setNote(Note);
        list.setIsAdd(IsAdd);
        list.setBelongDate(BelongDate);
        list.setLng(Lng);
        list.setLat(Lat);
        list.setLoc(Loc);
        list.setPicture(Picture);
        list.setManager_ID(Manager_ID);
        list.setSignLat(SignLat);
        list.setSignLng(SignLng);
        list.setSignTime(SignTime);
        list.setType("现场拜访");
        ZJRequest<VistList> zjRequest = new ZJRequest<VistList>();
        zjRequest.setData(list);
        return JsonUtil.toJson(zjRequest);

    }

    /**
     * 初始化列表数据
     */
    private void setItemDate() {
        // TODO Auto-generated method stub
        for (int j = 0; j < ThemList.size(); j++) {
            Dialog_Visit as = new Dialog_Visit();
            as.setTag_Id(ThemList.get(j).getID());
            as.setTag_Name(ThemList.get(j).getName());
            ItemLists.add(as);
        }
    }

    @Override
    public void onDialogDown(int position, int type) {
        // TODO Auto-generated method stub
        switch (type) {
            case SOURCE_DIALOG:
                ArrayList<Dialog_Visit> result = showDialogs.getResult();

                String tagsName = "";
                String tagsID = "";

                for (Dialog_Visit d : result) {
                    if (d.getState() == 1) {
                        if (TextUtils.isEmpty(tagsName)) {
                            tagsName += d.getTag_Name();
                            tagsID += d.getTag_Id();
                        } else {
                            tagsName += ',' + d.getTag_Name();
                            tagsID += ',' + d.getTag_Id();
                        }
                    }
                }
                txt_tagsid.setText(tagsID);//标签ID
                txt_tags.setText(tagsName);//标签名称
                Log.e("TAGS", "tagsID值=" + tagsID);
                break;

            default:
                break;
        }
    }

    /**
     * 获取标签接口回调
     */
    private webServiceHelp.OnServiceCallBack<VisitTag> onServiceCallBackTheme = new webServiceHelp.OnServiceCallBack<VisitTag>() {

        @Override
        public void onServiceCallBack(boolean haveCallBack,
                                      ZJResponse<VisitTag> zjResponse) {
            // TODO Auto-generated method stub
            if (haveCallBack && zjResponse != null) {
                switch (zjResponse.getCode()) {
                    case 0:
                        //访问成功
                        ThemList = zjResponse.getResults();
                        ItemLists.clear();
                        setItemDate();
                        break;
                    default:
                        //错误码

                        break;
                }
            } else {
                //接口访问失败
                L.e("接口访问失败，请检查网络！");
            }
        }
    };

    /**
     * 初始化组件
     */
    private void initView() {

        webHelp = new webServiceHelp<VisitTag>(Methods.GET_VISIT_TAGS, new TypeToken<ZJResponse<VisitTag>>() {
        }.getType());
        webHelp.setOnServiceCallBack(onServiceCallBackTheme);
        webHelp.start(getVisitTagJson());

        // TODO Auto-generated method stub
        //获取资源
        boxImg = (ImageView) findViewById(R.id.img_visit_bubao_box);
        //日期时间
        dateTxt = (TextView) findViewById(R.id.txt_visit_selectDate);
        //获取标签名称
        txt_tags = (TextView) findViewById(R.id.txt_tags);
        //获取标签ID
        txt_tagsid = (TextView) findViewById(R.id.txt_tagsid);

        image1 = (ImageView) findViewById(R.id.visit_new_photo1);
        image2 = (ImageView) findViewById(R.id.visit_new_photo2);
        image3 = (ImageView) findViewById(R.id.visit_new_photo3);
        backLayout = (LinearLayout) findViewById(R.id.visit_new_back);
        lineLayout = (RelativeLayout) findViewById(R.id.visit_line);
        submitTxt = (TextView) findViewById(R.id.txt_visit_submit);
        clientLayout = (RelativeLayout) findViewById(R.id.visit_client_layout);

        visit_labels_layout = (RelativeLayout) findViewById(R.id.visit_labels_layout);//标签

        buBaoLayout = (RelativeLayout) findViewById(R.id.visit_bubao_layout);
        dateLayout = (RelativeLayout) findViewById(R.id.visit_date_layout);
        locationLayout = (LinearLayout) findViewById(R.id.visit_location_layout);
        client = (TextView) findViewById(R.id.txt_clientName);
        note = (EditText) findViewById(R.id.edit_visit_content);
        txt_sign_loc = (TextView) findViewById(R.id.txt_sign_loc);
        visit_sign_layout = (RelativeLayout) findViewById(R.id.visit_sign_layout);//定位签到
        txt_sign_time = (TextView) findViewById(R.id.txt_sign_time);

        //初始化数据
        Img_List.clear();
        pList.clear();
        // 所有ImageView重置
        image1.setImageResource(R.drawable.visit_img);
        image2.setImageResource(R.drawable.visit_img);
        image3.setImageResource(R.drawable.visit_img);
        try {
            if (!MemberName.equals("无")) {
                client.setText(MemberName);
            } else {
                showImage();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            L.e("ui数据获取失败");
        }

        // 添加监听事件
        image1.setOnClickListener(new myOnClickListener());
        image2.setOnClickListener(new myOnClickListener());
        image3.setOnClickListener(new myOnClickListener());
        clientLayout.setOnClickListener(new myOnClickListener());
        buBaoLayout.setOnClickListener(new myOnClickListener());
        dateLayout.setOnClickListener(new myOnClickListener());
        locationLayout.setOnClickListener(new myOnClickListener());
        submitTxt.setOnClickListener(new myOnClickListener());
        backLayout.setOnClickListener(new myOnClickListener());
        visit_sign_layout.setOnClickListener(new myOnClickListener());
        visit_labels_layout.setOnClickListener(new myOnClickListener());//标签
    }

    /**
     * 获取标签主题json
     *
     * @return
     */
    private String getVisitTagJson() {
        ZJRequest<T> zjRequest = new ZJRequest<T>();
        zjRequest.setCompany_ID(MyApplication.getInstance().getCompanyID());
        return JsonUtil.toJson(zjRequest);

    }

    /*
    *
    * 提交的方法
    * */
    private void SubmitData() {
        //获取登录人id
        int Manager_ID = Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0"));

        if (arrayList.size() > 0) {
            Member_ID = arrayList.get(0).getID();//客户编号
        }
        String SignTime = txt_sign_time.getText().toString().trim();
        String SignLoc = txt_sign_loc.getText().toString();
        String TagID = txt_tagsid.getText().toString().trim();//标签ID集合
        String Tags = txt_tags.getText().toString().trim();//标签集合
        String BelongDate = null;
        if (IsAdd) {
            BelongDate = buban_time;//补办日期
        } else {
            BelongDate = time;//上报日期
        }
        String Note = note.getText().toString();//描述
        String Picture = getConnectString(pList);

        if (isWrite()) {
            setNetData(Member_ID, TagID, Tags, Note, IsAdd, BelongDate, Lat, Lng, SignLoc, Picture, Manager_ID, SignLat, SignLng, SignTime);
        }
    }

    ShowDialogs showDialogs;

    private class myOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stu
            switch (v.getId()) {
                case R.id.visit_new_back:// 返回
                    //是否提交
                    visitList.setIsSubmit(0);
                    saveData();//按返回键前对数据保存
                    finish();
                    break;
                case R.id.txt_visit_submit:// 提交
                    if (isWrite()) {
                        LocationUtil.startLocation(VisitNewAdd.this, mLocationListener1); // 定位功能
                        showNotDialog("获取当前位置");
                        // pDialog = ProgressDialog.show(VisitNewAdd.this, "请等待...", "正在提交...", true);
                        // pDialog.setCancelable(true);
                    }
                    break;
                case R.id.visit_client_layout:// 客户
                    Intent intent = new Intent(VisitNewAdd.this, CustomersListActivity.class);
                    //选择个客户/人员界面，客户传值“0”，人员传值“1”（注意传String类型）
                    intent.putExtra("mode", "0");
                    //多选传0，单选传1
                    intent.putExtra("select", 1);
                    //传递分组名称
                    intent.putExtra("groupName", "分类");
                    intent.putExtra("Tittle", "选择客户");
                    startActivityForResult(intent, 3);

                    break;
                case R.id.visit_labels_layout://标签
                    //Toast.makeText(getApplicationContext(),"点击了标签",Toast.LENGTH_SHORT).show();
                    visit_labels_layout.setClickable(true);
                    showDialogs = new ShowDialogs(VisitNewAdd.this, SOURCE_DIALOG, ItemLists, "选择标签(可多选)", VisitNewAdd.this, txt_tagsid.getText().toString(), txt_tags.getText().toString());

                    showDialogs.customDialog();
                    break;
                case R.id.visit_bubao_layout:// 补办
                    if (isBuBao) {// 补办，则显示日期这一行
                        boxImg.setBackgroundResource(R.drawable.visit_select_box);
                        dateLayout.setVisibility(View.VISIBLE);
                        lineLayout.setVisibility(View.VISIBLE);
                        isBuBao = false;
                        IsAdd = true;
                    } else {// 不补办，则隐藏日期这一行
                        boxImg.setBackgroundResource(R.drawable.visit_box);
                        dateLayout.setVisibility(View.GONE);
                        lineLayout.setVisibility(View.GONE);
                        isBuBao = true;
                        IsAdd = false;
                    }

                    break;
                case R.id.visit_date_layout:// 日期
                    try {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        Date date1 = new Date(year - 1900, month, day); // 获取时间转换为Date对象
                        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                        String time = sf.format(date1);
                        d1 = sf.parse(time);

                        dialog = new DatePickerDialog(VisitNewAdd.this,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        // TODO Auto-generated method stub

                                        try {
                                            Date date1 = new Date(year - 1900, monthOfYear, dayOfMonth); // 获取时间转换为Date对象
                                            SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
                                            String time1 = sf1.format(date1);
                                            SimpleDateFormat sf2 = new SimpleDateFormat("yyyy年MM月dd日");
                                            String time2 = sf2.format(date1);
                                            Date d2 = sf1.parse(time1);

                                            long diff1 = d1.getTime() - d2.getTime();
                                            long days1 = diff1 / (1000 * 60 * 60 * 24);
                                            if (days1 >= 1) {
                                                dateTxt.setText(time2);
                                                buban_time = time1;
                                            } else {
                                                ClueCustomToast.showToast(VisitNewAdd.this,
                                                        R.drawable.toast_warn, "您只能补报今天之前的日期！");
                                            }

                                        } catch (ParseException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }


                                    }
                                }, calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH));
                        dialog.show();

                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                    break;
                case R.id.visit_new_photo1:// 照片1

                    if (Img_List.size() >= 1) {
                        Intent intent1 = new Intent(VisitNewAdd.this, VisitPhotoManage.class);
                        intent1.putExtra("index", 1);
                        intent1.putExtra("imgselect", 0);
                        startActivityForResult(intent1, 1);

                    } else {
//					T.showShort(VisitNewAdd.this, "拍照1");

                        FileName = getSystemTime() + ".jpg";
                        photoPath = Constant.SAVE_IMG_PATH + File.separator
                                + FileName;
                        CallSystemApp.callCamera(VisitNewAdd.this, photoPath);

                    }

                    break;
                case R.id.visit_new_photo2:// 照片2

                    if (Img_List.size() >= 2) {
                        Intent intent1 = new Intent(VisitNewAdd.this, VisitPhotoManage.class);
                        intent1.putExtra("index", 1);
                        intent1.putExtra("imgselect", 1);
                        startActivityForResult(intent1, 1);
                    } else {

                        FileName = getSystemTime() + ".jpg";
                        photoPath = Constant.SAVE_IMG_PATH + File.separator
                                + FileName;

                        CallSystemApp.callCamera(VisitNewAdd.this, photoPath);
                    }
                    break;
                case R.id.visit_new_photo3:// 照片3
                    if (Img_List.size() >= 3) {
                        Intent intent1 = new Intent(VisitNewAdd.this, VisitPhotoManage.class);
                        intent1.putExtra("index", 1);
                        intent1.putExtra("imgselect", 2);
                        startActivityForResult(intent1, 1);
                    } else {
                        FileName = getSystemTime() + ".jpg";
                        photoPath = Constant.SAVE_IMG_PATH + File.separator
                                + FileName;
                        CallSystemApp.callCamera(VisitNewAdd.this, photoPath);
                    }
                    break;

                case R.id.visit_sign_layout:// 签到
                    LocationUtil.startLocation(VisitNewAdd.this, mLocationListener); // 定位功能
                    SetDataTime();//获取系统时间得方法
                    break;
                default:
                    break;
            }
        }

    }

    /*
    * 获取系统时间得方法
    * */
    private void SetDataTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        txt_sign_time.setText(formatter.format(curDate));//系统时间
    }

    /**
     * 异步更新数据
     */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    dismissDialog();
                    new ClueCustomToast().showToast(VisitNewAdd.this,
                            R.drawable.toast_sucess, "提交成功！");
                    visitList.setIsSubmit(1);
                    saveData();
                    //TODO 重复了
                    // VisitManager.getInstance(context).saveVisitData(visitList);
                    VisitNewAdd.this.setResult(1);
                    VisitNewAdd.this.finish();
                    break;
                case 1:
                    //给客户控件赋值
                    client.setText(arrayList.get(0).getCompany_name());
                    Member_ID = arrayList.get(0).getID();//客户编号
                    break;
                //获取图片路径，并使用图片
                case 2:

                    //从sd卡里获取图片进行压缩处理//photoPath为完整路径
                    //Bitmap bitmap = new BitmapTools().getBitmap(photoPath, 120, 240);

                    //对图片进行剪切成100*100后显示
                    Bitmap bitmap1 = Common.getImageThumbnail(newSDpath, 80, 80);
                    if (Img_List.size() == 1) {
                        image2.setImageBitmap(bitmap1);
                    } else if (Img_List.size() == 2) {
                        image3.setImageBitmap(bitmap1);
                    } else {
                        image1.setImageBitmap(bitmap1);
                    }
                    Img_List.add(newSDpath);
                    break;
                case 3:
                    ClueCustomToast.showToast(VisitNewAdd.this,
                            R.drawable.toast_defeat, "您只能补报今天之前的日期！");
                    break;
                case 4:
                    //对图片进行压缩
                    new Thread() {
                        public void run() {
                            File file;
                            String newPath = Constant.SAVE_IMG_PATH + File.separator + getSystemTime() + ".jpg";
                            try {
//                    		photoPath = Constant.SAVE_IMG_PATH + "/20150123" +".jpg";
//                    		L.e("图片地址：" + photoPath);
                                file = PhotoHelpDefine.compressImage(photoPath, newPath, MyApplication.photoWidth, MyApplication.photoHeigh);
//        				    	file = PhotoHelp.compressImage(photoPath, newPath);
                                Message msg = new Message();
                                msg.obj = file;
                                msg.what = 6;
                                mHandler.sendMessage(msg);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                mHandler.sendEmptyMessage(7);
                                e.printStackTrace();
                                return;
                            }
                        }

                        ;
                    }.start();

                    break;
                case 5:
                    dismissDialog();
                    ClueCustomToast.showToast(VisitNewAdd.this,
                            R.drawable.toast_defeat, "提交失败！");
                    break;
                case 6:
                    File file = (File) msg.obj;
                    newSDpath = file.getAbsolutePath();
                    setImageToNet(file.getAbsolutePath());
                    break;
                case 7:
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
        image1.setImageResource(R.drawable.visit_img);
        image2.setImageResource(R.drawable.visit_img);
        image3.setImageResource(R.drawable.visit_img);
        if (Img_List.size() >= 1) {
            //从sd卡里获取图片进行压缩处理
            Bitmap bitmap = new BitmapTools().getBitmap(Img_List.get(0), 480, 800);
            //对图片进行剪切成100*100后显示
            Bitmap bitmap1 = new BitmapTools().cutBitmap(bitmap, 100, 100);
            image1.setImageBitmap(bitmap1);
            //Log.e("-------------------------", "path" + Img_List.get(0));
            if (Img_List.size() >= 2) {
                //从sd卡里获取图片进行压缩处理
                Bitmap bitmap2 = new BitmapTools().getBitmap(Img_List.get(1), 480, 800);
                //对图片进行剪切成100*100后显示
                Bitmap bitmap3 = new BitmapTools().cutBitmap(bitmap2, 100, 100);
                image2.setImageBitmap(bitmap3);
                if (Img_List.size() >= 3) {
                    //从sd卡里获取图片进行压缩处理
                    Bitmap bitmap4 = new BitmapTools().getBitmap(Img_List.get(2), 480, 800);
                    //对图片进行剪切成100*100后显示
                    Bitmap bitmap5 = new BitmapTools().cutBitmap(bitmap4, 100, 100);
                    image3.setImageBitmap(bitmap5);
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
                case CallSystemApp.EXTRA_TAKE_PHOTOS://拍照返回
                    L.d("拍照返回");
                    mHandler.sendEmptyMessage(4);
                    pDialog = ProgressDialog.show(this, "请等待...", "正在上传图片...", true);
                    pDialog.setCancelable(true);

                    break;
                case 1:
                    //重新调用相机拍照
                    FileName = getSystemTime() + ".jpg";
                    photoPath = Constant.SAVE_IMG_PATH + File.separator
                            + FileName;
                    CallSystemApp.callCamera(VisitNewAdd.this, photoPath);

                    break;

                case 3://选择客户返回
                    System.out.println("resultCode==" + resultCode + "requestCode==" + requestCode);
                    Bundle bundle = data.getBundleExtra("bundle");//得到新Activity 关闭后返回的数据
                    arrayList = (ArrayList<SortModel>) bundle.getSerializable("result");//接收泛型
                    if (arrayList.size() != 0) {
                        mHandler.sendEmptyMessage(1);
                    }
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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String signText = formatter.format(curDate) + "\n" + Loc;
        ;

        String companyID = preferences.getInt(Content.COMPS_ID, 1016) + "";
        String modulePage = "Visit";
        String picData = "";
        try {
            picData = Base64Util.encodeBase64File(path);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        UpLoadImgSignText upLoadImgSignText = new UpLoadImgSignText(picData, modulePage, FileName, companyID, signText);
        upLoadImgSignText.setUpLoadResult(VisitNewAdd.this);
        new Thread(upLoadImgSignText).start();
    }

    @Override
    public void onUpLoadResult(String FileName, String FilePath,
                               boolean isSuccess) {
        // TODO Auto-generated method stub

        if (isSuccess) {
            pList.add(FilePath);
            L.e("pList'''''''':" + pList.size());
            //图片上传成功
            L.i("------------->:" + FilePath);
            pDialog.dismiss();
            new ClueCustomToast().showToast(VisitNewAdd.this,
                    R.drawable.toast_sucess, "图片上传成功");
            mHandler.sendEmptyMessage(2);


        } else {
            //图片上传失败
            L.i("------------->:" + "上传失败");
            new ClueCustomToast().showToast(VisitNewAdd.this,
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
            SignLat = (double) location.getLatitude();//经度
            SignLng = (double) location.getLongitude();//维度
            LocationUtil.stopLocation();//这句代码有bug
            LocationUtil.startReverseGeoCode(SignLat, SignLng, getGeoCoderResultListener);
        }
    };

    /**
     * 签到定位监听
     */
    private BDLocationListener mLocationListener1 = new BDLocationListener() {

        @Override
        public void onReceivePoi(BDLocation arg0) {
            // TODO Auto-generated method stub

        }
        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            Lat = (double) location.getLatitude();//经度
            Lng = (double) location.getLongitude();//维度
            if (Lng > 0.0) {
                updataDialog("正在提交数据");
                SubmitData();//调用提交的方法
            }
            Log.e("TAGS", "Lng=" + Lng);
            LocationUtil.stopLocation();
        }
    };


    OnGetGeoCoderResultListener getGeoCoderResultListener = new OnGetGeoCoderResultListener() {

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            // TODO Auto-generated method stub
            if (result == null) {
                return;
            }
            txt_sign_loc.setText(result.getAddress());//设置地理位置
            Loc = result.getAddress();
            try {
                if (Loc.equals("")) {
                    Loc = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            LocationUtil.stopReverseGeoCode();
        }

        @Override
        public void onGetGeoCodeResult(GeoCodeResult arg0) {
            // TODO Auto-generated method stub

        }
    };


    /**
     * 判断是否输入完成
     *
     * @return
     */
    private boolean isWrite() {
        if (client.getText().toString().equals("选择客户")) {
            showDialog("请选择客户！");
            return false;
        }
        if (IsAdd && dateTxt.getText().toString().trim().equals("")) {
            showDialog("请选择选择补报日期！");
            return false;
        }
        if (Img_List.size() <= 0) {
            showDialog("您至少拍一张照片！");
            return false;
        }
        if (note.getText().toString().equals("")) {
            showDialog("描述不能为空！");
            return false;
        }
        if (txt_sign_time.getText().toString().equals("") || txt_sign_time.getText().toString().equals("GPS签到")) {
            showDialog("请先签到！");
            return false;
        }
        return true;


    }

    public void showDialog(String TXT) {
        new ClueCustomToast().showToast(VisitNewAdd.this,
                R.drawable.toast_warn, TXT);

    }


    /**
     * 数据保存
     */
    private void saveData() {
        if (visitList == null) {
            return;
        }
        try {
            //服务器地址
            visitList.setUpUrl(getConnectString(pList));
            //拍照后图片绝对路径
            Img_List.add(photoPath);
            //本地图片绝对路径拼接
            visitList.setPhotoUrl(getConnectString(Img_List));
            //客户名称
            visitList.setCustormerName(client.getText().toString().trim());
            //客户id
            visitList.setCustormerID(Member_ID);
            //标签名称
            visitList.setTags(txt_tags.getText().toString().trim());
            //标签ID
            visitList.setTagID(txt_tagsid.getText().toString().trim());
            //签到时间
            visitList.setSignTime(txt_sign_time.getText().toString().trim());
            //签到地址
            visitList.setSignLoc(txt_sign_loc.getText().toString().trim());

            //是否补报、补报日期
            if (IsAdd) {
                visitList.setIsReport(1);
                visitList.setReportDate(buban_time);
            } else {
                visitList.setIsReport(0);
            }
            //描述
            visitList.setNote(note.getText().toString().trim());
            //保存或者更新数据
            long i = VisitManager.getInstance(context).saveVisitData(visitList);
            if (i > 0) {
                L.e("数据保存成功！");
            } else {
                L.e("数据保存失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 图片地址拼接
     *
     * @return
     */
    private String getConnectString(List<String> list) {
        //图片地址拼接
        StringBuffer mangerid = new StringBuffer("");
        L.e("list.size:" + list.size());
        for (int i = 0; i < list.size(); i++) {
            L.e("list:" + list.get(i));

            if (i > 0) {
                mangerid.append("#");
            }
            mangerid.append(list.get(i) + "");

        }
        String Picture = mangerid.toString().trim();
        return Picture;
    }

    /**
     * activity销毁前要进行数据保存
     * 防止系统回收内存
     */
    protected void onDestroy() {
        // TODO Auto-generated method stub
        webHelp.removeOnServiceCallBack();
        //|| visitList.getIsSubmit() == null易注释
//        if (visitList.getIsSubmit() == null){
//            visitList.setIsSubmit(0);
//        }
        //是否提交
//        if (visitList.getIsSubmit() == 0) {
//            visitList.setIsSubmit(0);
//        } else {
//            visitList.setIsSubmit(1);
//        }
        L.e("销毁activity");
        saveData();
        super.onDestroy();

    }

    /**
     * 按物理按键返回前数据保存
     */
    public void onBackPressed() {
        // TODO Auto-generated method stub
        //是否提交
        visitList.setIsSubmit(0);
        saveData();
        super.onBackPressed();
    }

    /**
     * 显示临时保存数据
     */
    @SuppressWarnings("null")
    private void showImage() {
        List<VisitHostoryList> list = new ArrayList<VisitHostoryList>();
        list = VisitManager.getInstance(context).getVisitData(ManagerID);
        boolean isNew = true;
        Bitmap bitmap = null;
        String[] temper = null;
        String[] temper1 = null;
        if (list != null && list.size() > 0) {
            if (list.get(0).getIsSubmit() == 0) {
                String CustormerName = list.get(0).getCustormerName();
                if (!CustormerName.equals("选择客户")) {
                    isNew = false;
                }
                if (isNew) {
                    SetDataTime();
                    LocationUtil.startLocation(VisitNewAdd.this, mLocationListener);
                } else {
                    client.setText(CustormerName);
                    //如果是补报
                    if (list.get(0).getIsReport() == 1) {
                        IsAdd = true;
                        buban_time = list.get(0).getReportDate();
                        dateTxt.setText(subString(list.get(0).getReportDate()));
                        isBuBao = false;
                        boxImg.setBackgroundResource(R.drawable.visit_select_box);
                        dateLayout.setVisibility(View.VISIBLE);
                        lineLayout.setVisibility(View.VISIBLE);
                    }
                    Member_ID = list.get(0).getCustormerID();
                    note.setText(list.get(0).getNote());

                    //标签名称
                    txt_tagsid.setText(list.get(0).getTagID());
                    txt_tags.setText(list.get(0).getTags());
                    txt_sign_time.setText(list.get(0).getSignTime());
                    txt_sign_loc.setText(list.get(0).getSignLoc());

                    L.e("显示数据" + list.get(0).getPhotoUrl());

                    if (list.get(0).getPhotoUrl() != null && !(list.get(0).getPhotoUrl()).equals("")
                            && list.get(0).getUpUrl() != null && !(list.get(0).getUpUrl()).equals("")) {
                        temper = (list.get(0).getUpUrl()).split("#");
                        temper1 = (list.get(0).getPhotoUrl()).split("#");
                        if (temper.length >= 1) {
                            bitmap = Common.getImageThumbnail(temper1[0], 80, 80);
                            image1.setImageBitmap(bitmap);
                            Img_List.add(temper1[0]);
                            pList.add(temper[0]);
                        }
                        if (temper.length >= 2) {
                            bitmap = Common.getImageThumbnail(temper1[1], 80, 80);
                            image2.setImageBitmap(bitmap);
                            Img_List.add(temper1[1]);
                            pList.add(temper[1]);
                        }
                        if (temper.length >= 3) {
                            bitmap = Common.getImageThumbnail(temper1[2], 80, 80);
                            image3.setImageBitmap(bitmap);
                            Img_List.add(temper1[2]);
                            pList.add(temper[2]);
                        }
                    }

                }
            }
        }

    }

    /**
     * 转换字符串拼接
     *
     * @param str
     * @return
     */
    private String subString(String str) {
        String a = str.substring(0, 4);
        String b = str.substring(5, 7);
        String c = str.substring(8, 10);
        return a + "年" + b + "月" + c + "日";
    }


}
