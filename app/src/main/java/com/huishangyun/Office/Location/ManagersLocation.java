package com.huishangyun.Office.Location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.reflect.TypeToken;
import com.gotye.api.GotyeUser;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Activity.Chat;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Plan.CustomersListActivity;
import com.huishangyun.Channel.Plan.SortModel;
import com.huishangyun.Map.Location;
import com.huishangyun.Map.LocationUtil;
import com.huishangyun.Office.Attendance.MarqueeText;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.T;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.manager.DepartmentManager;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Managers;
import com.huishangyun.model.MessageType;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pan on 2015/6/19.
 */
public class ManagersLocation extends BaseActivity{
    private RelativeLayout back;
    private MapView mMapView;
    private LinearLayout selectLayout;
    private TextView UserName;
    private MarqueeText dateTime;
    private BaiduMap mBaiduMap;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private boolean isFirstLoc = true;

    private webServiceHelp<LocationList> serviceHelp;
    private boolean isExit = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_location_map);
        initView();
        isExit = false;
    }

    /**
     * 实例化组件
     */
    private void initView() {
        back = (RelativeLayout) findViewById(R.id.back);
        selectLayout = (LinearLayout) findViewById(R.id.selectLayout);
        mMapView = (MapView) findViewById(R.id.bmapView);
        UserName = (TextView) findViewById(R.id.UserName);
        dateTime = (MarqueeText) findViewById(R.id.dateTime);
        mBaiduMap = mMapView.getMap();
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, null));
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        serviceHelp = new webServiceHelp<LocationList>(Methods.GET_LOCATION_INFO, new TypeToken<ZJResponse<LocationList>>(){}.getType());
        serviceHelp.setOnServiceCallBack(onServiceCallBack);

        //设置监听
        back.setOnClickListener(listener);
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        selectLayout.setOnClickListener(listener);
        UserName.setText(MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_DEPARTMENT_NAME, ""));
        //定位当前位置
        LocationUtil.startLocation(this, new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null || mMapView == null || mBaiduMap == null)
                    return;
                /*MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                                // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData);
                double mLat = location.getLatitude();
                double mLng = location.getLongitude();*/
                if (isFirstLoc) {
                    //2015.0722  陈总要求不显示当前人员位置,比例尺还原为5公里
                    isFirstLoc = false;
                    final LatLng ll = new LatLng(location.getLatitude(),
                            location.getLongitude());
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                    mBaiduMap.animateMapStatus(u);
                    /*new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            //处理缩放 sdk 缩放级别范围： [3.0,19.0]
                            float zoomLevel = (float) 15.0;
                            MapStatusUpdate u1 = MapStatusUpdateFactory.zoomTo(zoomLevel);
                            if (mBaiduMap != null) {
                                mBaiduMap.animateMapStatus(u1);
                            }

                        }
                    }, 1000);*/
                    showNotDialog("");
                    serviceHelp.start(getJson(0));
                }
            }

            @Override
            public void onReceivePoi(BDLocation bdLocation) {

            }
        });
    }

    private String getJson(int Manager_ID){
        ZJRequest zjRequest = new ZJRequest();
        zjRequest.setManager_ID(Manager_ID);
        zjRequest.setDepartment_ID(MyApplication.getInstance().getSharedPreferences().getInt(Constant.HUISHANG_DEPARTMENT_ID, 0));
        zjRequest.setCompany_ID(MyApplication.getInstance().getCompanyID());
        return JsonUtil.toJson(zjRequest);
    }

    /**
     * 获取后台数据返回通知
     */
    private webServiceHelp.OnServiceCallBack<LocationList> onServiceCallBack = new webServiceHelp.OnServiceCallBack<LocationList>() {
        @Override
        public void onServiceCallBack(boolean haveCallBack, ZJResponse<LocationList> zjResponse) {
            dismissDialog();
            if (haveCallBack) {
                switch (zjResponse.getCode()) {
                    case 0:
                        //获取到位置集合
                        List<LocationList> locationLists = zjResponse.getResults();
                        for (LocationList locationList : locationLists) {
                            //判断经纬度是否为空
                            if (locationList.getLatitude() == null || locationList.getLongitude() == null)
                                continue;
                            //定义Maker坐标点
                            LatLng point = new LatLng(locationList.getLatitude(), locationList.getLongitude());
                            //构建Marker图标
                            /*BitmapDescriptor bitmap = BitmapDescriptorFactory
                                    .fromResource(R.drawable.office_location);*/
                            TextView textView = new TextView(ManagersLocation.this);
                            textView.setGravity(Gravity.CENTER);
                            textView.setText(locationList.getRealName());
                            textView.setBackgroundResource(R.drawable.custom_info_bubble);
                            textView.setTextColor(0xAA646464);
                            textView.setTextSize(17);
                            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(textView);
                            //构建MarkerOption，用于在地图上添加Marker
                            OverlayOptions option = new MarkerOptions()
                                    .position(point)
                                    .icon(bitmap).title(locationList.getRealName());
                            //在地图上添加Marker，并显示
                            Marker marker = (Marker)mBaiduMap.addOverlay(option);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("info", locationList);
                            marker.setExtraInfo(bundle);
                            mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    //点击显示InfoWindow
                                    LocationList locationList = (LocationList)marker.getExtraInfo().get("info");
                                    showInfoWindow(locationList);
                                    return true;
                                }
                            });
                        }
                        //如果返回结果大于0显示第一个人位置
                       /* if (locationLists.size() > 0)
                            showInfoWindow(locationLists.get(0));*/
                        break;
                    default:
                        showCustomToast(zjResponse.getDesc() + "", false);
                        break;
                }
            } else {
                showCustomToast("获取云数据失败!", false);
            }
        }
    };

    /**
     * 显示弹出框
     * @param locationList
     */
    private void showInfoWindow(LocationList locationList) {
        LatLng point = new LatLng(locationList.getLatitude(), locationList.getLongitude());
        //显示信息
        //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
        View view = LayoutInflater.from(ManagersLocation.this)
                .inflate(R.layout.activity_office_location_map_text, null);
        TextView location = (TextView) view.findViewById(R.id.location);
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView duty = (TextView) view.findViewById(R.id.duty);
        ImageView img = (ImageView) view.findViewById(R.id.img);
        LinearLayout call = (LinearLayout) view.findViewById(R.id.call);
        LinearLayout message = (LinearLayout) view.findViewById(R.id.message);
        LinearLayout chat = (LinearLayout) view.findViewById(R.id.chat);

        String locationStr = locationList.getLocation();
        StringBuilder stringBuilder = new StringBuilder(locationStr);
        if (stringBuilder.length() > 18 ) {
            stringBuilder.insert(17, "\n");
        }
        location.setText(stringBuilder.toString());//设置显示信息
        name.setText(locationList.getRealName());
        final Managers managerInfo = DepartmentManager.getInstance(ManagersLocation.this).getManagerInfo(locationList.getID());
        img.setImageResource(R.drawable.contact_person);
        if (managerInfo != null) {
            duty.setText(managerInfo.getRole_Name().trim());
            ImageLoader.getInstance().displayImage("http://img.huishangyun.com/UploadFile/huishang/"
                    + MyApplication.getInstance().getCompanyID() + "/Photo/"
                    + managerInfo.getPhoto(), img, MyApplication.getInstance().getOptions());
            chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //打开聊天界面
                    Intent intent = new Intent(ManagersLocation.this, Chat.class);
                    intent.putExtra("JID", managerInfo.getOFUserName());
                    intent.putExtra("type", 2);
                    intent.putExtra("name", managerInfo.getRealName());
                    intent.putExtra("messtype", 0);
                    intent.putExtra("chat_name", managerInfo.getRealName());
                    intent.putExtra("Sign", "");
                    GotyeUser gotyeUser = new GotyeUser(managerInfo.getOFUserName());
                    intent.putExtra("user", gotyeUser);
                    startActivity(intent);
                }
            });
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + managerInfo.getMobile()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //启动
                    startActivity(intent);
                }
            });

            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    //系统默认的action，用来打开默认的短信界面
                    intent.setAction(Intent.ACTION_SENDTO);
                    //需要发短息的号码
                    intent.setData(Uri.parse("smsto:" + managerInfo.getMobile()));
                    startActivity(intent);
                }
            });
        }

        UserName.setText(locationList.getRealName());
        L.e("获取时间:" + locationList.getAddDateTime());
        dateTime.setText("获取时间:" + locationList.getAddDateTime().replace("T", " "));
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(view);
        InfoWindow mInfoWindow = new InfoWindow(view, point, -50);
        //为弹出的InfoWindow添加点击事件
        mBaiduMap.showInfoWindow(mInfoWindow);
        //设置中心点
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(point);
        mBaiduMap.animateMapStatus(u);
    }

    /**
     * 点击事件监听器
     */
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    finish();
                    break;
                case R.id.selectLayout:
                    Intent intent = new Intent(ManagersLocation.this, CustomersListActivity.class);
                    //选择个客户/人员界面，客户传值“0”，人员传值“1”（注意传String类型）
                    intent.putExtra("mode", "1");
                    //多选传0，单选传1
                    intent.putExtra("select", 1);
                    //传递分组名称
                    intent.putExtra("groupName", "组");
                    intent.putExtra("Tittle", "选择人员");
                    startActivityForResult(intent, HanderUtil.case1);
                    break;
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HanderUtil.case1 && resultCode == RESULT_OK) {
            Bundle bundle = data.getBundleExtra("bundle");//得到新Activity 关闭后返回的数据
            ArrayList<SortModel> arrayList = (ArrayList<SortModel>) bundle.getSerializable("result");//接收泛型
            if (arrayList.size()!=0) {
                //设置名称
                UserName.setText(arrayList.get(0).getCompany_name());
                webServiceHelp<LocationHistory> webServiceHelp = new webServiceHelp<LocationHistory>(Methods.HUISHANG_GetLocationList,
                        new TypeToken<ZJResponse<LocationHistory>>(){}.getType());
                //获取结果返回监听
                webServiceHelp.setOnServiceCallBack(new webServiceHelp.OnServiceCallBack<LocationHistory>() {
                    @Override
                    public void onServiceCallBack(boolean haveCallBack, ZJResponse<LocationHistory> zjResponse) {
                        if (isExit)
                            return;
                        dismissDialog();
                        if (haveCallBack) {
                            switch (zjResponse.getCode()) {
                                case 0:
                                    final List<LocationHistory> locationHistories = zjResponse.getResults();
                                    List<LatLng> latLngs = new ArrayList<LatLng>();
                                    if (locationHistories.size() > 0){
                                        mBaiduMap.clear();
                                        // 关闭定位图层
                                        mBaiduMap.setMyLocationEnabled(false);
                                        for (LocationHistory locationHistory : locationHistories){
                                            LatLng latLng = new LatLng(locationHistory.getLatitude(),locationHistory.getLongitude());
                                            latLngs.add(latLng);
                                            //构建Marker图标
                                            BitmapDescriptor bitmap = BitmapDescriptorFactory
                                                    .fromResource(R.drawable.office_location);
                                            //构建MarkerOption，用于在地图上添加Marker
                                            OverlayOptions option = new MarkerOptions()
                                                    .position(latLng)
                                                    .icon(bitmap);
                                            //在地图上添加Marker，并显示
                                            Marker marker = (Marker)mBaiduMap.addOverlay(option);
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("info", locationHistory);
                                            marker.setExtraInfo(bundle);
                                            mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                                                @Override
                                                public boolean onMarkerClick(Marker marker) {
                                                    //点击显示InfoWindow
                                                    LocationHistory locationList = (LocationHistory)marker.getExtraInfo().get("info");
                                                    LatLng point = new LatLng(locationList.getLatitude(), locationList.getLongitude());
                                                    TextView textView = new TextView(ManagersLocation.this);
                                                    textView.setBackgroundResource(R.drawable.custom_info_bubble);
                                                    textView.setGravity(Gravity.CENTER);
                                                    textView.setTextSize(17);
                                                    textView.setTextColor(0xAA646464);
                                                    textView.setText(locationList.getAddTime().replace("T", " ") + "\n" + locationList.getAddress());
                                                    InfoWindow mInfoWindow = new InfoWindow(textView, point, -70);
                                                    //为弹出的InfoWindow添加点击事件
                                                    mBaiduMap.showInfoWindow(mInfoWindow);
                                                    //设置中心点
                                                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(point);
                                                    mBaiduMap.animateMapStatus(u);
                                                    return true;
                                                }
                                            });
                                        }
                                        //绘制路线
                                        if (locationHistories.size() > 0){
                                            OverlayOptions overlayOptions =  new PolylineOptions().width(3)
                                                    .color(0xAAFF0000).points(latLngs).visible(true);
                                            mBaiduMap.addOverlay(overlayOptions);
                                        }
                                        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLngs.get(0));
                                        mBaiduMap.animateMapStatus(u);
                                    } else {
                                        showCustomToast("今日暂无记录!", false);
                                    }
                                    break;
                                default:
                                    showCustomToast(zjResponse.getDesc() + "", false);
                                    break;
                            }
                        } else {
                            showCustomToast("查询失败!",false);
                        }
                    }
                });
                ZJRequest zjRequest = new ZJRequest();
                zjRequest.setManager_ID(arrayList.get(0).getID());
                zjRequest.setCompany_ID(MyApplication.getInstance().getCompanyID());
                zjRequest.setBelongDate(TimeUtil.getLongToString(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
                showDialog("");
                //开始获取数据
                webServiceHelp.start(JsonUtil.toJson(zjRequest));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serviceHelp.removeOnServiceCallBack();
        LocationUtil.stopLocation();
        LocationUtil.stopReverseGeoCode();
        isExit = true;
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
    }


}
