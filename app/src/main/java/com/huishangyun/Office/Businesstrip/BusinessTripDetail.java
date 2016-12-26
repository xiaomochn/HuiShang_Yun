package com.huishangyun.Office.Businesstrip;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.huishangyun.Channel.Visit.BitmapTools;
import com.huishangyun.Channel.Visit.PhotoHelpDefine;
import com.huishangyun.Channel.Visit.PictureSkim;
import com.huishangyun.Map.LocationUtil;
import com.huishangyun.Office.Attendance.OfficePhotoSkim;
import com.huishangyun.Office.Summary.ImageLoad;
import com.huishangyun.Util.Base64Util;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.CallSystemApp;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.Methods;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.swipelistview.MyXListView.MyXListViewListener;
import com.huishangyun.task.UpLoadFileTask.OnUpLoadResult;
import com.huishangyun.task.UpLoadImgSignText;
import com.huishangyun.yun.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 管理员出差详情，不允许修改包括自己的，要修改从个人进
 *
 * @author xsl
 */
public class BusinessTripDetail extends BaseActivity implements MyXListViewListener, OnUpLoadResult {
    private static final String TAG = null;
    private RelativeLayout back;//返回
    private RelativeLayout addbusinesstrip;//新增出差
    private MyXListView businesstriplistview;
    private BusinessTripAdapter adapter;
    private int Manager_ID;//出差详情id
    public int Company_ID;//公司id
    public int Department_ID;//部门id
    private List<BusinessTripdata> listdata = new ArrayList<BusinessTripdata>();
    private List<BusinessTripdata> list = new ArrayList<BusinessTripdata>();
    private int PageIndex = 1;//页码
    private Double Lng;// 经度
    private Double Lat;// 维度
    private String location;// 地理位置
    private String photoPath;// 图片保存路径
    private String photoPath1;// 图片保存路径
    private String FileName;// 文件名
    private String FileName1;// 文件名
    public static List<String> Img_List = new ArrayList<String>();
    public static List<String> Img_List1 = new ArrayList<String>();
    ProgressDialog pDialog;
    public static List<String> pList = new ArrayList<String>();//图片地址存储
    public static List<String> pList1 = new ArrayList<String>();//图片地址存储
    Calendar calendar = Calendar.getInstance();
    private ViewHolder mHolder;

    private int Cameraflag;//相机指数
    private int ImgIndex;//图片指数
    private BusinessTripdata setoutlist = new BusinessTripdata();
    private BusinessTripdata arrivelist = new BusinessTripdata();
    PopupWindow mPopupWindow;
    private int managerid;//当前登入人id
    private String startPhotoPath;
    private String endPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //刚进来启动定位
        LocationUtil.startLocation(BusinessTripDetail.this, mLocationListener);
        setContentView(R.layout.activity_office_businesstrip_detail);
        Intent intent = getIntent();
        Manager_ID = intent.getIntExtra("Manager_ID", 0);
        Company_ID = preferences.getInt(Content.COMPS_ID, 1016);
        Log.e(TAG, "Company_ID:" + Company_ID);
        Department_ID = preferences.getInt(Constant.HUISHANG_DEPARTMENT_ID, 0);
        managerid = Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0"));
        Log.e(TAG, "Manager_ID:" + Manager_ID);
        Log.e(TAG, "managerid:" + managerid);
        Img_List.clear();
        Img_List1.clear();
        pList.clear();
        pList1.clear();
        init();
        getNetData(Manager_ID, 1, 3, 1);


    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    private void getNetData(final int Manager_ID, final int pageIndex,
                            final int pageSize, final int index) {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                String result = DataUtil.callWebService(
                        Methods.GET_BUSINESSTRIP_LIST,
                        getJson(Manager_ID, pageIndex, pageSize));
                Log.e(TAG, "result:" + result + pageIndex);

                //先判断网络数据是否获取成功，防止网络不好导致程序崩溃
                if (result != null) {
                    // 获取对象的Type
                    Type type = new TypeToken<ZJResponse<BusinessTripdata>>() {
                    }.getType();
                    ZJResponse<BusinessTripdata> zjResponse = JsonUtil.fromJson(result,
                            type);
                    // 获取对象列表
                    listdata.clear();
                    listdata = zjResponse.getResults();
                    Log.e(TAG, "-----------111");
                    if (index == 1) {//刷新
                        handler.sendEmptyMessage(0);
                    } else if (index == 2) {//加载更多
                        handler.sendEmptyMessage(1);

                    }

                } else {
                    handler.sendEmptyMessage(2);
                }
            }
        }.start();
    }

    /**
     * 设置json对象
     *
     * @param Manager_ID 用户编号
     * @param pageSize   单页显示条数
     * @param pageIndex  页码
     * @return
     */
    private String getJson(int Manager_ID, int pageIndex, int pageSize) {

        ZJRequest zjRequest = new ZJRequest();
        // 公司id
        zjRequest.setCompany_ID(Company_ID);
        // 用户编号
        zjRequest.setManager_ID(Manager_ID);
        // 设置部门号，0时为相当没有部门编号查询
        zjRequest.setDepartment_ID(0);
        // 设置页码，默认是1
        zjRequest.setPageIndex(pageIndex);
        // 设置当页显示条数
        zjRequest.setPageSize(pageSize);
        return JsonUtil.toJson(zjRequest);

    }

    /**
     * 提交出发数据
     */
    private void setSetOutNetData(final int ID, final double StartLng, final double StartLat,
                                  final String StartLoc, final String StartNote, final String StartPicture) {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub

                Log.e(TAG, "===========>:" + getJson1(ID, StartLng, StartLat, StartLoc, StartNote, StartPicture));

                String result = DataUtil.callWebService(
                        Methods.SET_BUSINESSTRIP_DETIAL_SETOUT,
                        getJson1(ID, StartLng, StartLat, StartLoc, StartNote, StartPicture));
                Log.e(TAG, "===========>:" + getJson1(ID, StartLng, StartLat, StartLoc, StartNote, StartPicture));

                // 先判断网络数据是否获取成功，防止网络不好导致程序崩溃
                if (result != null) {
                    // 获取对象的Type
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        // Log.e(TAG, "code:" + jsonObject.getInt("Code"));
                        int code = jsonObject.getInt("Code");
                        Log.e(TAG, "code:" + code);

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
    private String getJson1(int ID, double StartLng, double StartLat,
                            String StartLoc, String StartNote, String StartPicture) {

        setoutlist.setAction("Update");
        setoutlist.setID(ID);
        setoutlist.setStartLng(StartLng);
        setoutlist.setStartLat(StartLat);
        setoutlist.setStartLoc(StartLoc);
        setoutlist.setStartNote(StartNote);
        setoutlist.setStartPicture(StartPicture);
        ZJRequest<BusinessTripdata> zjRequest = new ZJRequest<BusinessTripdata>();
        zjRequest.setData(setoutlist);
        return JsonUtil.toJson(zjRequest);

    }

    /**
     * 提交到达数据
     */
    private void setArriveNetData(final int ID, final double ArriveLng, final double ArriveLat,
                                  final String ArriveLoc, final String ArriveNote, final String ArrivePicture) {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Log.e(TAG, "===========>:" + getJson(ID, ArriveLng, ArriveLat, ArriveLoc, ArriveNote, ArrivePicture));

                String result = DataUtil.callWebService(
                        Methods.SET_BUSINESSTRIP_DETIAL_ARRIVE,
                        getJson(ID, ArriveLng, ArriveLat, ArriveLoc, ArriveNote, ArrivePicture));
                Log.e(TAG, "===========>:" + getJson(ID, ArriveLng, ArriveLat, ArriveLoc, ArriveNote, ArrivePicture));

                // 先判断网络数据是否获取成功，防止网络不好导致程序崩溃
                if (result != null) {
                    // 获取对象的Type
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        // Log.e(TAG, "code:" + jsonObject.getInt("Code"));
                        int code = jsonObject.getInt("Code");
                        Log.e(TAG, "code:" + code);

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
    private String getJson(int ID, double ArriveLng, double ArriveLat,
                           String ArriveLoc, String ArriveNote, String ArrivePicture) {

        arrivelist.setAction("Update");
        arrivelist.setID(ID);
        arrivelist.setArriveLng(ArriveLng);
        arrivelist.setArriveLat(ArriveLat);
        arrivelist.setArriveLoc(ArriveLoc);
        arrivelist.setArriveNote(ArriveNote);
        arrivelist.setArrivePicture(ArrivePicture);
        ZJRequest<BusinessTripdata> zjRequest = new ZJRequest<BusinessTripdata>();
        zjRequest.setData(arrivelist);
        return JsonUtil.toJson(zjRequest);

    }


    /**
     * 取消数据提交
     *
     * @param ID
     * @param Note 取消内容
     */
    private void setCancelNetData(final int ID, final String Note) {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Log.e(TAG, "===========>:" + getJson2(ID, Note));

                String result = DataUtil.callWebService(
                        Methods.BUSINESSTRIP_CANCEL,
                        getJson2(ID, Note));

                // 先判断网络数据是否获取成功，防止网络不好导致程序崩溃
                if (result != null) {
                    // 获取对象的Type
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        // Log.e(TAG, "code:" + jsonObject.getInt("Code"));
                        int code = jsonObject.getInt("Code");
                        Log.e(TAG, "code:" + code);

                        if (code == 0) {
                            mHandler.sendEmptyMessage(5);
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
    private String getJson2(int ID, String Note) {

        ZJRequest zjRequest = new ZJRequest();
        zjRequest.setAction("Cancel");
        zjRequest.setID(ID);
        zjRequest.setNote(Note);
        return JsonUtil.toJson(zjRequest);

    }

    /**
     * 更新UI
     */
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://刷新
                    list.clear();
                    for (int i = 0; i < listdata.size(); i++) {
                        list.add(listdata.get(i));
                    }
                    adapter.Updata();
                    break;
                case 1://加载更多

                    if (listdata.size() <= 0) {
                        showDialog("没有更多数据！");

                    } else {

                        for (int i = 0; i < listdata.size(); i++) {
                            list.add(listdata.get(i));
                        }
                        adapter.Updata();
                    }

                    break;
                case 2:
                    showDialog("未获得任何网络数据，请检查网络连接！");
                    break;


                default:
                    break;
            }
        }

        ;
    };


    /**
     * 界面初始化
     */
    private void init() {
        back = (RelativeLayout) findViewById(R.id.back);
        addbusinesstrip = (RelativeLayout) findViewById(R.id.addbusinesstrip);
        businesstriplistview = (MyXListView) findViewById(R.id.businesstriplistview);
        adapter = new BusinessTripAdapter(this, list);
        businesstriplistview.setAdapter(adapter);
        businesstriplistview.setPullLoadEnable(true);
        businesstriplistview.setMyXListViewListener(this);

        back.setOnClickListener(new ButtonClickListener());
        addbusinesstrip.setOnClickListener(new ButtonClickListener());
    }

    private class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back://返回
                    finish();
                    break;
                case R.id.addbusinesstrip:
                    Intent intent = new Intent(BusinessTripDetail.this, AddBusinessTrip.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }

    }


    /**
     * listview适配器
     *
     * @author xsl
     */
    private class BusinessTripAdapter extends BaseAdapter {


        private LayoutInflater mInflater;// 动态布局映射
        private List<BusinessTripdata> Lists;
        private String ArriveNote = "";//到达备注
        private String StartNote = "";//出发备注
        private String time;

        public BusinessTripAdapter(Context context, List<BusinessTripdata> lists) {
            this.Lists = lists;
            this.mInflater = LayoutInflater.from(context);
        }

        public void Updata() {
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
//			return Lists.size();
            return Lists.size();
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

            //获取当前时间
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            Date date1 = new Date(year - 1900, month, day); // 获取时间转换为Date对象
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            time = sf.format(date1);

            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(
                        R.layout.activity_office_businesstrip_detail_item, null);// 根据布局文件实例化view
                holder = new ViewHolder();
                holder.top_line = (RelativeLayout) convertView.findViewById(R.id.top_line);
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.start_address = (TextView) convertView.findViewById(R.id.start_address);
                holder.end_address = (TextView) convertView.findViewById(R.id.end_address);
                holder.startdate = (TextView) convertView.findViewById(R.id.startdate);
                holder.enddate = (TextView) convertView.findViewById(R.id.enddate);


                holder.photo1 = (ImageView) convertView.findViewById(R.id.photo1);
                holder.photo2 = (ImageView) convertView.findViewById(R.id.photo2);
                holder.photo3 = (ImageView) convertView.findViewById(R.id.photo3);

                holder.note = (TextView) convertView.findViewById(R.id.note);

                holder.takephoto = (TextView) convertView.findViewById(R.id.takephoto);
                holder.writenote = (TextView) convertView.findViewById(R.id.writenote);
                holder.setouttxt = (TextView) convertView.findViewById(R.id.setouttxt);
                holder.cancel = (TextView) convertView.findViewById(R.id.cancel);

                holder.getstarttime = (TextView) convertView.findViewById(R.id.getstarttime);
                holder.setout_gps_Address = (TextView) convertView.findViewById(R.id.setout_gps_Address);
                holder.getnote = (TextView) convertView.findViewById(R.id.getnote);
                holder.setout_picture1 = (ImageView) convertView.findViewById(R.id.setout_picture1);
                holder.setout_picture2 = (ImageView) convertView.findViewById(R.id.setout_picture2);
                holder.setout_picture3 = (ImageView) convertView.findViewById(R.id.setout_picture3);

                holder.arrivetime = (TextView) convertView.findViewById(R.id.arrivetime);
                holder.arrive_gps_Address = (TextView) convertView.findViewById(R.id.arrive_gps_Address);
                holder.arrive_note = (TextView) convertView.findViewById(R.id.arrive_note);
                holder.arrive_picture1 = (ImageView) convertView.findViewById(R.id.arrive_picture1);
                holder.arrive_picture2 = (ImageView) convertView.findViewById(R.id.arrive_picture2);
                holder.arrive_picture3 = (ImageView) convertView.findViewById(R.id.arrive_picture3);


                holder.arrive_photo1 = (ImageView) convertView.findViewById(R.id.arrive_photo1);
                holder.arrive_photo2 = (ImageView) convertView.findViewById(R.id.arrive_photo2);
                holder.arrive_photo3 = (ImageView) convertView.findViewById(R.id.arrive_photo3);
                holder.arrive_writenote = (TextView) convertView.findViewById(R.id.arrive_writenote);
                holder.arrive_takephoto = (TextView) convertView.findViewById(R.id.arrive_takephoto);
                holder.arrive_gowritenote = (TextView) convertView.findViewById(R.id.arrive_gowritenote);
                holder.arrive_cancel = (TextView) convertView.findViewById(R.id.arrive_cancel);
                holder.arrive_already = (TextView) convertView.findViewById(R.id.arrive_already);
                holder.cancel_note = (TextView) convertView.findViewById(R.id.cancel_note);
                holder.cancel_datetime = (TextView) convertView.findViewById(R.id.cancel_datetime);


                //显示隐藏操作控件
                holder.view1 = (View) convertView.findViewById(R.id.view1);
                holder.zhaopian = (LinearLayout) convertView.findViewById(R.id.zhaopian);
                holder.view2 = (View) convertView.findViewById(R.id.view2);
                holder.beizhu = (LinearLayout) convertView.findViewById(R.id.beizhu);
                holder.view3 = (View) convertView.findViewById(R.id.view3);
                holder.menu = (LinearLayout) convertView.findViewById(R.id.menu);
                holder.view4 = (View) convertView.findViewById(R.id.view4);
                holder.seoutdetail = (LinearLayout) convertView.findViewById(R.id.seoutdetail);
                holder.view5 = (View) convertView.findViewById(R.id.view5);
                holder.zhaopian1 = (LinearLayout) convertView.findViewById(R.id.zhaopian1);
                holder.view6 = (View) convertView.findViewById(R.id.view6);
                holder.beizhu1 = (LinearLayout) convertView.findViewById(R.id.beizhu1);
                holder.view7 = (View) convertView.findViewById(R.id.view7);
                holder.menu1 = (LinearLayout) convertView.findViewById(R.id.menu1);
                holder.arrivdetail = (LinearLayout) convertView.findViewById(R.id.arrivdetail);
                holder.canceldetail = (LinearLayout) convertView.findViewById(R.id.canceldetail);

                holder.submit_time = (TextView) convertView.findViewById(R.id.submit_time);
                holder.submit_note = (TextView) convertView.findViewById(R.id.submit_note);
                holder.submit_picture1 = (ImageView) convertView.findViewById(R.id.submit_picture1);
                holder.submit_picture2 = (ImageView) convertView.findViewById(R.id.submit_picture2);
                holder.submit_picture3 = (ImageView) convertView.findViewById(R.id.submit_picture3);

                holder.submitNote = (LinearLayout) convertView.findViewById(R.id.submitNote);
                holder.submitphoto = (LinearLayout) convertView.findViewById(R.id.submitphoto);


                convertView.setTag(holder);
            } else {

                holder = (ViewHolder) convertView.getTag();

            }

            holder.submit_time.setText(backDateMin(Lists.get(position).getAddDateTime()));

            if (Lists.get(position).getNote().equals("") || Lists.get(position).getNote() == null) {
                holder.submitNote.setVisibility(View.GONE);
            } else {
                holder.submit_note.setText(Lists.get(position).getNote());
            }
            if (Lists.get(position).getPicture().equals("") || Lists.get(position).getPicture() == null) {
                holder.submitphoto.setVisibility(View.GONE);
            } else {
                holder.submit_picture1.setVisibility(View.GONE);
                holder.submit_picture2.setVisibility(View.GONE);
                holder.submit_picture3.setVisibility(View.GONE);
                String picture[] = Lists.get(position).getPicture().split("#");
                for (int i = 0; i < picture.length; i++) {
                    if (i == 0) {
                        String Img = "http://img.huishangyun.com/UploadFile/huishang/" + Company_ID + "/Travel/100x100/" +
                                picture[0];
//					ImageLoad.displayImage(Img, holder.submit_picture1, R.drawable.defaultimage02, 0);
                        new ImageLoad().displayImage(context, Img, holder.submit_picture1, R.drawable.defaultimage02, 0, false);
                        holder.submit_picture1.setVisibility(View.VISIBLE);
                    } else if (i == 1) {
                        String Img = "http://img.huishangyun.com/UploadFile/huishang/" + Company_ID + "/Travel/100x100/" +
                                picture[1];
//					ImageLoad.displayImage(Img, holder.submit_picture1, R.drawable.defaultimage02, 0);
                        new ImageLoad().displayImage(context, Img, holder.submit_picture2, R.drawable.defaultimage02, 0, false);
                        holder.submit_picture2.setVisibility(View.VISIBLE);
                    } else {
                        String Img = "http://img.huishangyun.com/UploadFile/huishang/" + Company_ID + "/Travel/100x100/" +
                                picture[2];
//					ImageLoad.displayImage(Img, holder.submit_picture1, R.drawable.defaultimage02, 0);
                        new ImageLoad().displayImage(context, Img, holder.submit_picture3, R.drawable.defaultimage02, 0, false);
                        holder.submit_picture3.setVisibility(View.VISIBLE);
                    }
                }


            }


            if (position == 0) {
                holder.top_line.setVisibility(View.INVISIBLE);
            } else {
                holder.top_line.setVisibility(View.VISIBLE);
            }

            if (backDate(Lists.get(position).getStartTime()).equals(time)) {
                holder.date.setText("今天");
            } else {
                holder.date.setText(backDate(Lists.get(position).getStartTime()));
            }
            holder.start_address.setText(Lists.get(position).getStartCity());
            holder.end_address.setText(Lists.get(position).getEndCity());
            holder.startdate.setText(backDate(Lists.get(position).getStartTime()));
            holder.enddate.setText(backDate(Lists.get(position).getEndTime()));
            if (Lists.get(position).getFlag() != 0 && Lists.get(position).getFlag() != 3) {
                holder.getstarttime.setText(backDateMin(Lists.get(position).getStartDateTime()));
                holder.setout_gps_Address.setText(Lists.get(position).getStartLoc());
                holder.getnote.setText(Lists.get(position).getStartNote());
            }
            String setoutImg = "http://img.huishangyun.com/UploadFile/huishang/" + Company_ID + "/Travel/100x100/" +
                    Lists.get(position).getStartPicture();
//				ImageLoad.displayImage(setoutImg, holder.setout_picture1, R.drawable.defaultimage02, 0);
            holder.setout_picture1.setVisibility(View.GONE);
            holder.setout_picture2.setVisibility(View.GONE);
            holder.setout_picture3.setVisibility(View.GONE);
            if (Lists.get(position).getStartPicture() != null && !Lists.get(position).getStartPicture().equals("")) {
                String setoutImgs[] = Lists.get(position).getStartPicture().split("#");
                for (int i = 0; i < setoutImgs.length; i++) {
                    if (i == 0) {
                        setoutImg = "http://img.huishangyun.com/UploadFile/huishang/" + Company_ID + "/Travel/100x100/" +
                                setoutImgs[0];
                        new ImageLoad().displayImage(context, setoutImg, holder.setout_picture1, R.drawable.defaultimage02, 0, false);
                        holder.setout_picture1.setVisibility(View.VISIBLE);
                    } else if (i == 1) {
                        setoutImg = "http://img.huishangyun.com/UploadFile/huishang/" + Company_ID + "/Travel/100x100/" +
                                setoutImgs[1];
                        new ImageLoad().displayImage(context, setoutImg, holder.setout_picture2, R.drawable.defaultimage02, 0, false);
                        holder.setout_picture2.setVisibility(View.VISIBLE);
                    } else {
                        setoutImg = "http://img.huishangyun.com/UploadFile/huishang/" + Company_ID + "/Travel/100x100/" +
                                setoutImgs[2];
                        new ImageLoad().displayImage(context, setoutImg, holder.setout_picture3, R.drawable.defaultimage02, 0, false);
                        holder.setout_picture3.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                new ImageLoad().displayImage(context, setoutImg, holder.setout_picture1, R.drawable.defaultimage02, 0, false);
            }


            if (Lists.get(position).getFlag() == 2) {
                holder.arrivetime.setText(backDateMin(Lists.get(position).getArriveDateTime()));
                holder.arrive_gps_Address.setText(Lists.get(position).getArriveLoc());
                holder.arrive_note.setText(Lists.get(position).getArriveNote());
            }
            holder.arrive_picture1.setVisibility(View.GONE);
            holder.arrive_picture2.setVisibility(View.GONE);
            holder.arrive_picture3.setVisibility(View.GONE);
            String arriveImg = "http://img.huishangyun.com/UploadFile/huishang/" + Company_ID + "/Travel/100x100/" +
                    Lists.get(position).getArrivePicture();
//				ImageLoad.displayImage(arriveImg, holder.arrive_picture1, R.drawable.defaultimage02, 0);
            if (Lists.get(position).getArrivePicture() != null && !Lists.get(position).getArrivePicture().equals("")) {
                String arriveImgs[] = Lists.get(position).getArrivePicture().split("#");
                for (int i = 0; i < arriveImgs.length; i++) {
                    if (i == 0) {
                        arriveImg = "http://img.huishangyun.com/UploadFile/huishang/" + Company_ID + "/Travel/100x100/" +
                                arriveImgs[0];
                        new ImageLoad().displayImage(context, arriveImg, holder.arrive_picture1, R.drawable.defaultimage02, 0, false);
                        holder.arrive_picture1.setVisibility(View.VISIBLE);
                    } else if (i == 1) {
                        arriveImg = "http://img.huishangyun.com/UploadFile/huishang/" + Company_ID + "/Travel/100x100/" +
                                arriveImgs[1];
                        new ImageLoad().displayImage(context, arriveImg, holder.arrive_picture2, R.drawable.defaultimage02, 0, false);
                        holder.arrive_picture2.setVisibility(View.VISIBLE);
                    } else {
                        arriveImg = "http://img.huishangyun.com/UploadFile/huishang/" + Company_ID + "/Travel/100x100/" +
                                arriveImgs[2];
                        new ImageLoad().displayImage(context, arriveImg, holder.arrive_picture3, R.drawable.defaultimage02, 0, false);
                        holder.arrive_picture3.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                new ImageLoad().displayImage(context, arriveImg, holder.arrive_picture1, R.drawable.defaultimage02, 0, false);
            }

            holder.cancel_note.setText(Lists.get(position).getCancelNote());
            holder.cancel_datetime.setText(backDateMin(Lists.get(position).getAddDateTime()));
            if (managerid != Manager_ID) {
                //显示隐藏判断
                if (Lists.get(position).getFlag() == 0) {//提交，未出发
                    //出发地点和结束地点颜色控制
                    holder.start_address.setTextColor(0xffe95811);
                    holder.end_address.setTextColor(0xffe95811);

                    holder.view1.setVisibility(View.GONE);
                    holder.zhaopian.setVisibility(View.GONE);
                    holder.view2.setVisibility(View.GONE);
                    holder.beizhu.setVisibility(View.GONE);
                    holder.view3.setVisibility(View.GONE);
                    holder.menu.setVisibility(View.GONE);
                    holder.view4.setVisibility(View.GONE);
                    holder.seoutdetail.setVisibility(View.GONE);
                    holder.view5.setVisibility(View.GONE);
                    holder.zhaopian1.setVisibility(View.GONE);
                    holder.view6.setVisibility(View.GONE);
                    holder.beizhu1.setVisibility(View.GONE);
                    holder.view7.setVisibility(View.GONE);
                    holder.menu1.setVisibility(View.GONE);
                    holder.arrivdetail.setVisibility(View.GONE);
                    holder.canceldetail.setVisibility(View.GONE);

                } else if (Lists.get(position).getFlag() == 1) {//已出发
                    //出发地点和结束地点颜色控制
                    holder.start_address.setTextColor(0xff21a5de);
                    holder.end_address.setTextColor(0xffe95811);

                    holder.view1.setVisibility(View.GONE);
                    holder.zhaopian.setVisibility(View.GONE);
                    holder.view2.setVisibility(View.GONE);
                    holder.beizhu.setVisibility(View.GONE);
                    holder.view3.setVisibility(View.GONE);
                    holder.menu.setVisibility(View.GONE);
                    holder.view4.setVisibility(View.VISIBLE);
                    holder.seoutdetail.setVisibility(View.VISIBLE);
                    holder.view5.setVisibility(View.GONE);
                    holder.zhaopian1.setVisibility(View.GONE);
                    holder.view6.setVisibility(View.GONE);
                    holder.beizhu1.setVisibility(View.GONE);
                    holder.view7.setVisibility(View.GONE);
                    holder.menu1.setVisibility(View.GONE);
                    holder.arrivdetail.setVisibility(View.GONE);
                    holder.canceldetail.setVisibility(View.GONE);

                } else if (Lists.get(position).getFlag() == 2) {//已到达
                    holder.start_address.setTextColor(0xff21a5de);
                    holder.end_address.setTextColor(0xff21a5de);

                    holder.view1.setVisibility(View.GONE);
                    holder.zhaopian.setVisibility(View.GONE);
                    holder.view2.setVisibility(View.GONE);
                    holder.beizhu.setVisibility(View.GONE);
                    holder.view3.setVisibility(View.GONE);
                    holder.menu.setVisibility(View.GONE);
                    holder.view4.setVisibility(View.VISIBLE);
                    holder.seoutdetail.setVisibility(View.VISIBLE);
                    holder.view5.setVisibility(View.GONE);
                    holder.zhaopian1.setVisibility(View.GONE);
                    holder.view6.setVisibility(View.GONE);
                    holder.beizhu1.setVisibility(View.GONE);
                    holder.view7.setVisibility(View.VISIBLE);
                    holder.menu1.setVisibility(View.GONE);
                    holder.arrivdetail.setVisibility(View.VISIBLE);
                    holder.canceldetail.setVisibility(View.GONE);

                } else if (Lists.get(position).getFlag() == 3) {//取消
                    //出发地点和结束地点颜色控制
                    holder.start_address.setTextColor(0xffe95811);
                    holder.end_address.setTextColor(0xffe95811);

                    holder.view1.setVisibility(View.GONE);
                    holder.zhaopian.setVisibility(View.GONE);
                    holder.view2.setVisibility(View.GONE);
                    holder.beizhu.setVisibility(View.GONE);
                    holder.view3.setVisibility(View.GONE);
                    holder.menu.setVisibility(View.GONE);
                    holder.view4.setVisibility(View.GONE);
                    holder.seoutdetail.setVisibility(View.GONE);
                    holder.view5.setVisibility(View.GONE);
                    holder.zhaopian1.setVisibility(View.GONE);
                    holder.view6.setVisibility(View.GONE);
                    holder.beizhu1.setVisibility(View.GONE);
                    holder.view7.setVisibility(View.VISIBLE);
                    holder.menu1.setVisibility(View.GONE);
                    holder.arrivdetail.setVisibility(View.GONE);
                    holder.canceldetail.setVisibility(View.VISIBLE);
                }
            } else {

                //显示隐藏判断
                if (Lists.get(position).getFlag() == 0) {//提交，未出发
                    //出发地点和结束地点颜色控制
                    holder.start_address.setTextColor(0xffe95811);
                    holder.end_address.setTextColor(0xffe95811);

                    holder.view1.setVisibility(View.GONE);
                    holder.zhaopian.setVisibility(View.GONE);
                    holder.view2.setVisibility(View.GONE);
                    holder.beizhu.setVisibility(View.GONE);
                    holder.view3.setVisibility(View.VISIBLE);
                    holder.menu.setVisibility(View.VISIBLE);
                    holder.view4.setVisibility(View.GONE);
                    holder.seoutdetail.setVisibility(View.GONE);
                    holder.view5.setVisibility(View.GONE);
                    holder.zhaopian1.setVisibility(View.GONE);
                    holder.view6.setVisibility(View.GONE);
                    holder.beizhu1.setVisibility(View.GONE);
                    holder.view7.setVisibility(View.GONE);
                    holder.menu1.setVisibility(View.GONE);
                    holder.arrivdetail.setVisibility(View.GONE);
                    holder.canceldetail.setVisibility(View.GONE);

                } else if (Lists.get(position).getFlag() == 1) {//已出发
                    //出发地点和结束地点颜色控制
                    holder.start_address.setTextColor(0xff21a5de);
                    holder.end_address.setTextColor(0xffe95811);

                    holder.view1.setVisibility(View.GONE);
                    holder.zhaopian.setVisibility(View.GONE);
                    holder.view2.setVisibility(View.GONE);
                    holder.beizhu.setVisibility(View.GONE);
                    holder.view3.setVisibility(View.GONE);
                    holder.menu.setVisibility(View.GONE);
                    holder.view4.setVisibility(View.VISIBLE);
                    holder.seoutdetail.setVisibility(View.VISIBLE);
                    holder.view5.setVisibility(View.GONE);
                    holder.zhaopian1.setVisibility(View.GONE);
                    holder.view6.setVisibility(View.GONE);
                    holder.beizhu1.setVisibility(View.GONE);
                    holder.view7.setVisibility(View.VISIBLE);
                    holder.menu1.setVisibility(View.VISIBLE);
                    holder.arrivdetail.setVisibility(View.GONE);
                    holder.canceldetail.setVisibility(View.GONE);

                } else if (Lists.get(position).getFlag() == 2) {//已到达
                    holder.start_address.setTextColor(0xff21a5de);
                    holder.end_address.setTextColor(0xff21a5de);

                    holder.view1.setVisibility(View.GONE);
                    holder.zhaopian.setVisibility(View.GONE);
                    holder.view2.setVisibility(View.GONE);
                    holder.beizhu.setVisibility(View.GONE);
                    holder.view3.setVisibility(View.GONE);
                    holder.menu.setVisibility(View.GONE);
                    holder.view4.setVisibility(View.VISIBLE);
                    holder.seoutdetail.setVisibility(View.VISIBLE);
                    holder.view5.setVisibility(View.GONE);
                    holder.zhaopian1.setVisibility(View.GONE);
                    holder.view6.setVisibility(View.GONE);
                    holder.beizhu1.setVisibility(View.GONE);
                    holder.view7.setVisibility(View.VISIBLE);
                    holder.menu1.setVisibility(View.GONE);
                    holder.arrivdetail.setVisibility(View.VISIBLE);
                    holder.canceldetail.setVisibility(View.GONE);

                } else if (Lists.get(position).getFlag() == 3) {//取消
                    //出发地点和结束地点颜色控制
                    holder.start_address.setTextColor(0xffe95811);
                    holder.end_address.setTextColor(0xffe95811);

                    holder.view1.setVisibility(View.GONE);
                    holder.zhaopian.setVisibility(View.GONE);
                    holder.view2.setVisibility(View.GONE);
                    holder.beizhu.setVisibility(View.GONE);
                    holder.view3.setVisibility(View.GONE);
                    holder.menu.setVisibility(View.GONE);
                    holder.view4.setVisibility(View.GONE);
                    holder.seoutdetail.setVisibility(View.GONE);
                    holder.view5.setVisibility(View.GONE);
                    holder.zhaopian1.setVisibility(View.GONE);
                    holder.view6.setVisibility(View.GONE);
                    holder.beizhu1.setVisibility(View.GONE);
                    holder.view7.setVisibility(View.VISIBLE);
                    holder.menu1.setVisibility(View.GONE);
                    holder.arrivdetail.setVisibility(View.GONE);
                    holder.canceldetail.setVisibility(View.VISIBLE);
                }

            }


            OnClickListener mListener = new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    switch (v.getId()) {
                        case R.id.takephoto://出发拍照
                            if (Img_List.size() <= 0) {
                                FileName = System.currentTimeMillis() + ".jpg";
                                photoPath = Constant.SAVE_IMG_PATH + File.separator
                                        + FileName;
                                Log.e(TAG, "==========>:" + photoPath);
                                CallSystemApp.callCamera(BusinessTripDetail.this, photoPath);
                                Cameraflag = 1;
                                mHolder = holder;
                            }
                            break;
                        case R.id.writenote://出发备注
                            holder.view2.setVisibility(View.VISIBLE);
                            holder.beizhu.setVisibility(View.VISIBLE);
                            break;
                        case R.id.arrive_takephoto://到达拍照
                            if (Img_List1.size() <= 0) {
                                FileName1 = System.currentTimeMillis() + ".jpg";
                                photoPath1 = Constant.SAVE_IMG_PATH + File.separator
                                        + FileName1;
                                CallSystemApp.callCamera(BusinessTripDetail.this, photoPath1);
                                Cameraflag = 2;
                                mHolder = holder;
                            }
//					    	holder.view5.setVisibility(View.VISIBLE);
//					    	holder.zhaopian1.setVisibility(View.VISIBLE);
                            break;
                        case R.id.arrive_gowritenote://到达备注
                            holder.view6.setVisibility(View.VISIBLE);
                            holder.beizhu1.setVisibility(View.VISIBLE);
                            break;
                        case R.id.setouttxt://出发提交按钮
                            Log.e(TAG, "----------------->jinlail");
                            //启动定位服务
                            LocationUtil.startLocation(BusinessTripDetail.this, mLocationListener);
                            int ID = Lists.get(position).getID();
                            StartNote = holder.note.getText().toString().trim();
                            Log.e(TAG, "----------------->jinlail" + StartNote);
                            String StartPicture = "";
                            if (pList.size() > 0) {
                                for (int i = 0; i < pList.size(); i++) {
                                    if (i > 0) {
                                        StartPicture = StartPicture + "#";
                                    }
                                    StartPicture = StartPicture + pList.get(i);
                                }
                                /*StartPicture = pList.get(0);*/
                            }
                            //if (iswrite()) {
                            setSetOutNetData(ID, Lng, Lat, location, StartNote, StartPicture);
                            //}


                            break;

                        case R.id.arrive_already://到达提交按钮

                            //启动定位服务
                            LocationUtil.startLocation(BusinessTripDetail.this, mLocationListener);
                            int arriveID = Lists.get(position).getID();
                            ArriveNote = holder.arrive_writenote.getText().toString().trim();
                            Log.e(TAG, "----------------->jinlail" + ArriveNote);
                            String ArrivePicture = "";
                            if (pList1.size() > 0) {
                                for (int i = 0; i < pList1.size(); i++) {
                                    if (i > 0) {
                                        ArrivePicture = ArrivePicture + "#";
                                    }
                                    ArrivePicture = ArrivePicture + pList1.get(i);
                                }
                            }

                            //if (arriveiswrite()) {
                            setArriveNetData(arriveID, Lng, Lat, location, ArriveNote, ArrivePicture);
                            //}


                            break;

                        case R.id.cancel://开始取消按钮
                            pop(Lists.get(position).getID());

                            break;

                        case R.id.arrive_cancel://到达取消按钮

                            pop(Lists.get(position).getID());

                            break;
                        case R.id.submit_picture1:
                            Intent intent2 = new Intent(context, PictureSkim.class);
                            intent2.putExtra("index", 4);
                            intent2.putExtra("imgselect", 0);
                            intent2.putExtra("imgUri", Lists.get(position).getPicture());
                            context.startActivity(intent2);
                            break;
                        case R.id.submit_picture2:
                            Intent intent21 = new Intent(context, PictureSkim.class);
                            intent21.putExtra("index", 4);
                            intent21.putExtra("imgselect", 1);
                            intent21.putExtra("imgUri", Lists.get(position).getPicture());
                            context.startActivity(intent21);
                            break;
                        case R.id.submit_picture3:
                            Intent intent22 = new Intent(context, PictureSkim.class);
                            intent22.putExtra("index", 4);
                            intent22.putExtra("imgselect", 2);
                            intent22.putExtra("imgUri", Lists.get(position).getPicture());
                            context.startActivity(intent22);
                            break;
                        case R.id.photo1://出发拍照

                            if (Img_List.size() >= 1) {
                                Intent intent1 = new Intent(BusinessTripDetail.this, OfficePhotoSkim.class);
                                intent1.putExtra("index", 3);
                                intent1.putExtra("imgselect", 0);
                                startActivityForResult(intent1, 1);
                                ImgIndex = 1;
                                mHolder = holder;

                            } else {

                                FileName = getSystemTime() + ".jpg";
                                photoPath = Constant.SAVE_IMG_PATH + File.separator
                                        + FileName;

                                CallSystemApp.callCamera(BusinessTripDetail.this, photoPath);
                                Cameraflag = 1;
                                mHolder = holder;
                            }
                            break;
                        case R.id.photo2://出发拍照

                            if (Img_List.size() >= 2) {
                                Intent intent1 = new Intent(BusinessTripDetail.this, OfficePhotoSkim.class);
                                intent1.putExtra("index", 3);
                                intent1.putExtra("imgselect", 1);
                                startActivityForResult(intent1, 1);
                                ImgIndex = 1;
                                mHolder = holder;

                            } else {

                                FileName = getSystemTime() + ".jpg";
                                photoPath = Constant.SAVE_IMG_PATH + File.separator
                                        + FileName;

                                CallSystemApp.callCamera(BusinessTripDetail.this, photoPath);
                                Cameraflag = 1;
                                mHolder = holder;
                            }
                            break;
                        case R.id.photo3://出发拍照

                            if (Img_List.size() >= 3) {
                                Intent intent1 = new Intent(BusinessTripDetail.this, OfficePhotoSkim.class);
                                intent1.putExtra("index", 3);
                                intent1.putExtra("imgselect", 2);
                                startActivityForResult(intent1, 1);
                                ImgIndex = 1;
                                mHolder = holder;

                            } else {

                                FileName = getSystemTime() + ".jpg";
                                photoPath = Constant.SAVE_IMG_PATH + File.separator
                                        + FileName;

                                CallSystemApp.callCamera(BusinessTripDetail.this, photoPath);
                                Cameraflag = 1;
                                mHolder = holder;
                            }
                            break;
                        case R.id.arrive_photo1://到达拍照

                            if (Img_List1.size() >= 1) {
                                Intent intent1 = new Intent(BusinessTripDetail.this, OfficePhotoSkim.class);
                                intent1.putExtra("index", 4);
                                intent1.putExtra("imgselect", 0);
                                startActivityForResult(intent1, 1);
                                ImgIndex = 2;
                                mHolder = holder;
                            } else {

                                FileName1 = getSystemTime() + ".jpg";
                                photoPath1 = Constant.SAVE_IMG_PATH + File.separator
                                        + FileName1;

                                CallSystemApp.callCamera(BusinessTripDetail.this, photoPath1);
                                Cameraflag = 2;
                                mHolder = holder;

                            }
                            break;
                        case R.id.arrive_photo2://到达拍照

                            if (Img_List1.size() >= 2) {
                                Intent intent1 = new Intent(BusinessTripDetail.this, OfficePhotoSkim.class);
                                intent1.putExtra("index", 4);
                                intent1.putExtra("imgselect", 1);
                                startActivityForResult(intent1, 1);
                                ImgIndex = 2;
                                mHolder = holder;
                            } else {

                                FileName1 = getSystemTime() + ".jpg";
                                photoPath1 = Constant.SAVE_IMG_PATH + File.separator
                                        + FileName1;

                                CallSystemApp.callCamera(BusinessTripDetail.this, photoPath1);
                                Cameraflag = 2;
                                mHolder = holder;

                            }
                            break;
                        case R.id.arrive_photo3://到达拍照

                            if (Img_List1.size() >= 3) {
                                Intent intent1 = new Intent(BusinessTripDetail.this, OfficePhotoSkim.class);
                                intent1.putExtra("index", 4);
                                intent1.putExtra("imgselect", 2);
                                startActivityForResult(intent1, 1);
                                ImgIndex = 2;
                                mHolder = holder;
                            } else {

                                FileName1 = getSystemTime() + ".jpg";
                                photoPath1 = Constant.SAVE_IMG_PATH + File.separator
                                        + FileName1;

                                CallSystemApp.callCamera(BusinessTripDetail.this, photoPath1);
                                Cameraflag = 2;
                                mHolder = holder;

                            }
                            break;
                        case R.id.setout_picture1://出发照片浏览
                            Intent intent = new Intent(context, PictureSkim.class);
                            Log.e(TAG, "-------------------" + position);
                            intent.putExtra("index", 4);
                            intent.putExtra("imgselect", 0);
                            intent.putExtra("imgUri", Lists.get(position).getStartPicture());
                            context.startActivity(intent);
                            break;
                        case R.id.setout_picture2://出发照片浏览
                            Intent intenta = new Intent(context, PictureSkim.class);
                            Log.e(TAG, "-------------------" + position);
                            intenta.putExtra("index", 4);
                            intenta.putExtra("imgselect", 1);
                            intenta.putExtra("imgUri", Lists.get(position).getStartPicture());
                            context.startActivity(intenta);
                            break;
                        case R.id.setout_picture3://出发照片浏览
                            Intent intentb = new Intent(context, PictureSkim.class);
                            Log.e(TAG, "-------------------" + position);
                            intentb.putExtra("index", 4);
                            intentb.putExtra("imgselect", 2);
                            intentb.putExtra("imgUri", Lists.get(position).getStartPicture());
                            context.startActivity(intentb);
                            break;
                        case R.id.arrive_picture1://到达照片浏览
                            Intent intent1 = new Intent(context, PictureSkim.class);
                            Log.e(TAG, "-------------------" + position);
                            intent1.putExtra("index", 4);
                            intent1.putExtra("imgselect", 0);
                            intent1.putExtra("imgUri", Lists.get(position).getArrivePicture());
                            context.startActivity(intent1);
                            break;
                        case R.id.arrive_picture2://到达照片浏览
                            Intent intent11 = new Intent(context, PictureSkim.class);
                            Log.e(TAG, "-------------------" + position);
                            intent11.putExtra("index", 4);
                            intent11.putExtra("imgselect", 1);
                            intent11.putExtra("imgUri", Lists.get(position).getArrivePicture());
                            context.startActivity(intent11);
                            break;
                        case R.id.arrive_picture3://到达照片浏览
                            Intent intent12 = new Intent(context, PictureSkim.class);
                            Log.e(TAG, "-------------------" + position);
                            intent12.putExtra("index", 4);
                            intent12.putExtra("imgselect", 2);
                            intent12.putExtra("imgUri", Lists.get(position).getArrivePicture());
                            context.startActivity(intent12);
                            break;

                        default:
                            break;
                    }
                }

            };
            holder.submit_picture1.setOnClickListener(mListener);
            holder.submit_picture2.setOnClickListener(mListener);
            holder.submit_picture3.setOnClickListener(mListener);
            holder.photo1.setOnClickListener(mListener);
            holder.photo2.setOnClickListener(mListener);
            holder.photo3.setOnClickListener(mListener);
            holder.setouttxt.setOnClickListener(mListener);
            holder.arrive_photo1.setOnClickListener(mListener);
            holder.arrive_photo2.setOnClickListener(mListener);
            holder.arrive_photo3.setOnClickListener(mListener);
            holder.arrive_already.setOnClickListener(mListener);
            holder.cancel.setOnClickListener(mListener);
            holder.takephoto.setOnClickListener(mListener);
            holder.writenote.setOnClickListener(mListener);
            holder.arrive_takephoto.setOnClickListener(mListener);
            holder.arrive_gowritenote.setOnClickListener(mListener);
            holder.setout_picture1.setOnClickListener(mListener);
            holder.setout_picture2.setOnClickListener(mListener);
            holder.setout_picture3.setOnClickListener(mListener);
            holder.arrive_picture1.setOnClickListener(mListener);
            holder.arrive_picture2.setOnClickListener(mListener);
            holder.arrive_picture3.setOnClickListener(mListener);
            holder.arrive_cancel.setOnClickListener(mListener);


            return convertView;
        }

        /**
         * 提交出发输入检查
         *
         * @return
         */
        private boolean iswrite() {
            // TODO Auto-generated method stub
            if (pList.size() <= 0) {
                showDialog("请拍照后提交！");
                return false;
            }
            if (StartNote.trim().equals("")) {
                showDialog("请填写备注！");
                return false;
            }
            return true;
        }

        /**
         * 提交到达输入检查
         *
         * @return
         */
        private boolean arriveiswrite() {
            // TODO Auto-generated method stub
            if (pList1.size() <= 0) {
                showDialog("请拍照后提交！");
                return false;
            }
            if (ArriveNote.trim().equals("")) {
                showDialog("请填写备注！");
                return false;
            }
            return true;
        }

    }


    /**
     * toast
     * 提醒方法
     */
    public void showDialog(String TXT) {
        ClueCustomToast.showToast(BusinessTripDetail.this,
                R.drawable.toast_warn, TXT);

    }

    /**
     * 静态缓存
     *
     * @author xsl
     */
    static class ViewHolder {

        private TextView submit_time;//提交时间
        private TextView submit_note;//提交备注
        private ImageView submit_picture1;//提交照片
        private ImageView submit_picture2;//提交照片
        private ImageView submit_picture3;//提交照片
        //顶头竖线
        private RelativeLayout top_line;
        //标题日期
        private TextView date;
        //出发地点
        private TextView start_address;
        //结束地点
        private TextView end_address;
        //开始日期
        private TextView startdate;
        //结束日期
        private TextView enddate;
        //拍照照片
        private ImageView photo1;
        private ImageView photo2;
        private ImageView photo3;
        //提交备注
        private TextView note;
        //拍照按钮
        private TextView takephoto;
        //备注按钮
        private TextView writenote;
        //出发按钮
        private TextView setouttxt;
        //取消按钮
        private TextView cancel;


        //获得出发日期
        private TextView getstarttime;
        //获得出发定位
        private TextView setout_gps_Address;
        //出发备注
        private TextView getnote;
        //出发图
        private ImageView setout_picture1;
        private ImageView setout_picture2;
        private ImageView setout_picture3;

        //获得到达日期
        private TextView arrivetime;
        //获得到底定位
        private TextView arrive_gps_Address;
        //到达备注
        private TextView arrive_note;
        //到达图
        private ImageView arrive_picture1;
        private ImageView arrive_picture2;
        private ImageView arrive_picture3;


        //到达拍照
        private ImageView arrive_photo1;
        private ImageView arrive_photo2;
        private ImageView arrive_photo3;
        //到达备注填写
        private TextView arrive_writenote;
        //到达拍照按钮
        private TextView arrive_takephoto;
        //到达填写备注按钮
        private TextView arrive_gowritenote;
        //到达取消按钮
        private TextView arrive_cancel;
        //到达提交按钮
        private TextView arrive_already;
        //取消事由
        private TextView cancel_note;
        //取消时间
        private TextView cancel_datetime;

        private View view1;
        private LinearLayout zhaopian;
        private View view2;
        private LinearLayout beizhu;
        private View view3;
        private LinearLayout menu;
        private View view4;
        private LinearLayout seoutdetail;
        private View view5;

        private LinearLayout zhaopian1;
        private View view6;
        private LinearLayout beizhu1;
        private View view7;
        private LinearLayout menu1;
        private LinearLayout arrivdetail;

        private LinearLayout canceldetail;


        private LinearLayout submitNote;//提交备注
        private LinearLayout submitphoto;//提交照片

    }


    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                businesstriplistview.stopRefresh();
                businesstriplistview.stopLoadMore();
                businesstriplistview.setRefreshTime();

            }
        }, 2000);
        PageIndex = 1;
        getNetData(Manager_ID, 1, 3, 1);

    }

    /**
     * 上拉加载
     */
    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                businesstriplistview.stopRefresh();
                businesstriplistview.stopLoadMore();
                businesstriplistview.setRefreshTime();

            }
        }, 2000);
        PageIndex += 1;
        getNetData(Manager_ID, PageIndex, 3, 2);
    }

    /**
     * 传入后台接到的时间，返回我们需要的格式
     *
     * @param date 传入时间参数
     * @return
     */
    private String backDate(String date) {
        //这里包含0位但不包含5即（0，5】
        String a = date.substring(0, 4);
        String b = date.substring(5, 7);
        String c = date.substring(8, 10);
        String datesString = a + "-" + b + "-" + c;

        return datesString;
    }

    /**
     * 传入后台接到的时间，返回我们需要的格式 ：2014-9-20 17：35
     *
     * @param date
     * @return
     */
    private String backDateMin(String date) {
        String[] temp = null;
        temp = date.split("T");
        String a = temp[1].substring(0, 5);
        String dataString = temp[0] + " " + a;
        return dataString;
    }

    /**
     * 异步更新数据
     */
    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    new ClueCustomToast().showToast(BusinessTripDetail.this,
                            R.drawable.toast_sucess, "提交成功！");
                    getNetData(Manager_ID, 1, 3, 1);

                    break;
                case 1:
                    new ClueCustomToast().showToast(BusinessTripDetail.this,
                            R.drawable.toast_defeat, "提交失败！");

                    break;
                case 2:

                    if (Cameraflag == 1) {
                        //从sd卡里获取图片进行压缩处理//photoPath为完整路径
                        Bitmap bitmap = new BitmapTools().getBitmap(startPhotoPath, 120, 240);
                        // 对图片进行剪切成100*100后显示
                        Bitmap bitmap1 = new BitmapTools().cutBitmap(bitmap, 100, 100);
                        if (mHandler != null) {
                            if (Img_List.size() == 1) {
                                mHolder.photo2.setImageBitmap(bitmap1);
                            } else if (Img_List.size() == 2) {
                                mHolder.photo3.setImageBitmap(bitmap1);
                            } else {
                                mHolder.photo1.setImageBitmap(bitmap1);
                            }

                        }
                        Img_List.add(startPhotoPath);
                        mHolder.view1.setVisibility(View.VISIBLE);
                        mHolder.zhaopian.setVisibility(View.VISIBLE);
                    } else if (Cameraflag == 2) {
                        // 从holder.photo1.setImageBitmap(bitmap1);sd卡里获取图片进行压缩处理//photoPath为完整路径
                        Bitmap bitmap = new BitmapTools().getBitmap(endPhotoPath, 120, 240);
                        // 对图片进行剪切成100*100后显示
                        Bitmap bitmap1 = new BitmapTools().cutBitmap(bitmap, 100, 100);
                        if (Img_List1.size() == 1) {
                            mHolder.arrive_photo2.setImageBitmap(bitmap1);
                        } else if (Img_List1.size() == 2) {
                            mHolder.arrive_photo3.setImageBitmap(bitmap1);
                        } else {
                            mHolder.arrive_photo1.setImageBitmap(bitmap1);
                        }

                        Img_List1.add(endPhotoPath);
                        mHolder.view5.setVisibility(View.VISIBLE);
                        mHolder.zhaopian1.setVisibility(View.VISIBLE);
                    }

                    break;

                case 3:
                    File file = (File) msg.obj;
                    if (Cameraflag == 1) {
                        // 传图片到服务器
                        startPhotoPath = file.getAbsolutePath();
                        setImageToNet(file.getAbsolutePath());
                    } else if (Cameraflag == 2) {
                        // 传图片到服务器
                        endPhotoPath = file.getAbsolutePath();
                        setImageToNet(file.getAbsolutePath());
                    }

                    break;
                case 4:
                    showDialog("提交失败，请检查网络连接！");
                    break;

                case 5:
                    new ClueCustomToast().showToast(BusinessTripDetail.this,
                            R.drawable.toast_sucess, "取消成功！");
                    getNetData(Manager_ID, 1, 3, 1);
                    mPopupWindow.dismiss();
                    break;
                case 7:
                    //对图片进行压缩
                    new Thread() {
                        public void run() {
                            File file = null;
                            String newPath = Constant.SAVE_IMG_PATH + File.separator + System.currentTimeMillis() + ".jpg";
                            try {
                                if (Cameraflag == 1) {
                                    file = PhotoHelpDefine.compressImage(photoPath, newPath, MyApplication.photoWidth, MyApplication.photoHeigh);
//                    			file = PhotoHelp.compressImage(photoPath, newPath);
                                } else if (Cameraflag == 2) {
                                    file = PhotoHelpDefine.compressImage(photoPath1, newPath, MyApplication.photoWidth, MyApplication.photoHeigh);
//								file = PhotoHelp.compressImage(photoPath1, newPath);
                                }
                                Message msg = new Message();
                                msg.obj = file;
                                msg.what = 3;
                                mHandler.sendMessage(msg);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                mHandler.sendEmptyMessage(8);
                                e.printStackTrace();
                                return;
                            }
                        }

                        ;
                    }.start();

                    break;
                case 8:
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
        if (ImgIndex == 1) {
            // 所有ImageView重置
            mHolder.photo1.setImageResource(R.drawable.visit_img);
            mHolder.photo2.setImageResource(R.drawable.visit_img);
            mHolder.photo3.setImageResource(R.drawable.visit_img);
            if (Img_List.size() >= 1) {
                // 从sd卡里获取图片进行压缩处理
                Bitmap bitmap = new BitmapTools().getBitmap(Img_List.get(0), 480, 800);
                // 对图片进行剪切成100*100后显示
                Bitmap bitmap1 = new BitmapTools().cutBitmap(bitmap, 100, 100);
                mHolder.photo1.setImageBitmap(bitmap1);
                if (Img_List.size() >= 2) {
                    Bitmap bitmap2 = new BitmapTools().getBitmap(Img_List.get(1), 480, 800);
                    // 对图片进行剪切成100*100后显示
                    Bitmap bitmap12 = new BitmapTools().cutBitmap(bitmap2, 100, 100);
                    mHolder.photo2.setImageBitmap(bitmap12);

                    if (Img_List.size() >= 3) {
                        Bitmap bitmap23 = new BitmapTools().getBitmap(Img_List.get(2), 480, 800);
                        // 对图片进行剪切成100*100后显示
                        Bitmap bitmap123 = new BitmapTools().cutBitmap(bitmap23, 100, 100);
                        mHolder.photo3.setImageBitmap(bitmap123);
                    }
                }


            }
        } else if (ImgIndex == 2) {
            mHolder.arrive_photo1.setImageResource(R.drawable.visit_img);
            if (Img_List1.size() >= 1) {
                // 从sd卡里获取图片进行压缩处理
                Bitmap bitmap = new BitmapTools().getBitmap(Img_List1.get(0), 480, 800);
                // 对图片进行剪切成100*100后显示
                Bitmap bitmap1 = new BitmapTools().cutBitmap(bitmap, 100, 100);
                mHolder.arrive_photo1.setImageBitmap(bitmap1);
                if (Img_List1.size() >= 2) {
                    // 从sd卡里获取图片进行压缩处理
                    Bitmap bitmap2 = new BitmapTools().getBitmap(Img_List1.get(1), 480, 800);
                    // 对图片进行剪切成100*100后显示
                    Bitmap bitmap12 = new BitmapTools().cutBitmap(bitmap2, 100, 100);
                    mHolder.arrive_photo2.setImageBitmap(bitmap12);

                    if (Img_List1.size() >= 3) {
                        // 从sd卡里获取图片进行压缩处理
                        Bitmap bitmap23 = new BitmapTools().getBitmap(Img_List1.get(2), 480, 800);
                        // 对图片进行剪切成100*100后显示
                        Bitmap bitmap123 = new BitmapTools().cutBitmap(bitmap23, 100, 100);
                        mHolder.arrive_photo3.setImageBitmap(bitmap123);
                    }
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

                    mHandler.sendEmptyMessage(7);
                    pDialog = ProgressDialog
                            .show(this, "请等待...", "正在上传图片...", true);
                    pDialog.setCancelable(true);

                    break;
                case 1:

                    if (Cameraflag == 1) {
                        // 重新调用相机拍照
                        FileName = getSystemTime() + ".jpg";
                        photoPath = Constant.SAVE_IMG_PATH + File.separator + FileName;
                        CallSystemApp.callCamera(BusinessTripDetail.this, photoPath);
                    } else if (Cameraflag == 2) {
                        // 重新调用相机拍照
                        FileName1 = getSystemTime() + ".jpg";
                        photoPath1 = Constant.SAVE_IMG_PATH + File.separator + FileName1;
                        CallSystemApp.callCamera(BusinessTripDetail.this, photoPath1);
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
        SimpleDateFormat  formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date  curDate=new  Date(System.currentTimeMillis());//获取当前时间
        String  time =formatter.format(curDate);

        String companyID = preferences.getInt(Content.COMPS_ID, 1016) + "";
        L.e("上传公司id——companyID：" + companyID);
        String modulePage = "Travel";//图片服务器文件夹名称
        String picData = "";
        UpLoadImgSignText upLoadImgSignText = null;
        try {
            picData = Base64Util.encodeBase64File(path);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (Cameraflag == 1) {
            upLoadImgSignText = new UpLoadImgSignText(picData,
                    modulePage, FileName, companyID, time + "\n" + location);
        } else if (Cameraflag == 2) {
            upLoadImgSignText = new UpLoadImgSignText(picData,
                    modulePage, FileName1, companyID, time + "\n" + location);
        }
        upLoadImgSignText.setUpLoadResult(BusinessTripDetail.this);
        new Thread(upLoadImgSignText).start();
    }

    @Override
    public void onUpLoadResult(String FileName, String FilePath,
                               boolean isSuccess) {
        // TODO Auto-generated method stub

        if (isSuccess) {
            if (Cameraflag == 1) {
                pList.add(FilePath);
                // 图片上传成功
                Log.e(TAG, "FilePath:" + FilePath);
                Log.e(TAG, "pList:" + pList.get(0));
                pDialog.dismiss();
                new ClueCustomToast().showToast(BusinessTripDetail.this,
                        R.drawable.toast_sucess, "图片上传成功");
                mHandler.sendEmptyMessage(2);
            } else if (Cameraflag == 2) {
                pList1.add(FilePath);
                // 图片上传成功
                L.i("------------->:" + FilePath);
                pDialog.dismiss();
                new ClueCustomToast().showToast(BusinessTripDetail.this,
                        R.drawable.toast_sucess, "图片上传成功");
                mHandler.sendEmptyMessage(2);
            }


        } else {
            // 图片上传失败
            L.i("------------->:" + "上传失败");
            new ClueCustomToast().showToast(BusinessTripDetail.this,
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
     * 取消PopupWindow
     * 实现固定对话框在界面底部显示
     */
    private void pop(final int id) {

        View view = getLayoutInflater().inflate(R.layout.activity_office_business_cancel_popwindow, null);
        final EditText reasonText = (EditText) view.findViewById(R.id.reason);
        TextView cancel_TXT = (TextView) view.findViewById(R.id.cancel_TXT);
        mPopupWindow = new PopupWindow(view,
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        //设置可以获取焦点，否则弹出菜单中的EditText是无法获取输入的
        mPopupWindow.setFocusable(true);
        //这句是为了防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        //防止虚拟软键盘被弹出菜单遮住
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mPopupWindow.setAnimationStyle(R.style.AnimBottom);
        mPopupWindow.showAtLocation(findViewById(R.id.main), Gravity.RIGHT | Gravity.BOTTOM, 0, 0);

        cancel_TXT.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (reasonText.getText().toString().trim().equals("")) {
                    showDialog("请填写取消事由！");
                } else {
                    setCancelNetData(id, reasonText.getText().toString().trim());
                }


            }
        });


    }


}
