package com.huishangyun.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.reflect.TypeToken;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Map.LocationUtil;
import com.huishangyun.Office.Location.LocationList;
import com.huishangyun.Office.Location.MangerLocation;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service {

    private com.huishangyun.Util.webServiceHelp<LocationList> webServiceHelp;
    private String location;
    private double Lng;
    private double Lat;
    private Timer timer;
    private TimerTask task;
    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.e("LocationService Is Run onStartCommand");

        flags = START_STICKY;
        L.e("开始定位");
        webServiceHelp = new webServiceHelp<LocationList>(Methods.SET_LOCATION_INFO, new TypeToken<ZJResponse<LocationList>>(){}.getType());
        webServiceHelp.setOnServiceCallBack(onServiceCallBack);
        /* try {
            timer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //判断是否进行定位
        if (isLocationTime()) {
            LocationUtil.startLocation(getApplicationContext(), mLocationListener);
        }*/
        try {
            timer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                //启动定位服务
                //LocationUtil.startLocation(getApplicationContext(), mLocationListener);
                mHandler.sendEmptyMessage(HanderUtil.case1);
            }
        };
        //取出时间间隔，默认十五分钟
        long timeSpace = MyApplication.getInstance().getSharedPreferences().getInt(Constant.HUISHANG_LOCATION_FERQUENCY, 15) * 60 * 1000;
        if (timeSpace > 0)
            timer.schedule(task, 0,timeSpace);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webServiceHelp.removeOnServiceCallBack();
        try {
            timer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //mHandler.removeCallbacks(runnable);
    }

    /**
     * 接口回调
     */
    private com.huishangyun.Util.webServiceHelp.OnServiceCallBack<LocationList> onServiceCallBack = new com.huishangyun.Util.webServiceHelp.OnServiceCallBack<LocationList>() {
        public void onServiceCallBack(boolean haveCallBack,
                                      ZJResponse<LocationList> zjResponse) {
            if (haveCallBack && zjResponse != null) {
                switch (zjResponse.getCode()) {
                    case 0:
                        L.e("15分钟一次提交地理位置信息成功！");
                        break;

                    default:
                        L.e("提交地理位置信息失败！");
                        break;
                }
            }
        }
    };

    /**
     * 定时器发出指令做定位操作
     */
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HanderUtil.case1:
                    L.e("收到定时器发出的信息，开始定位");
                    if (isLocationTime()) {
                        LocationUtil.startLocation(getApplicationContext(), mLocationListener);
                    }/* else {
                        reStartLocation();
                    }*/
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 判断是否在定位时间段内
     * @return
     */
    private boolean isLocationTime() {
        long startTime = TimeUtil.getLocationtime(MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_LOCATION_START_DATA, ""));
        long endTime = TimeUtil.getLocationtime(MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_LOCATION_END_DATA, "")) + 86400000;
        if (System.currentTimeMillis() < startTime || System.currentTimeMillis() > endTime) {
            L.e("不在定位日期内");
            return false;
        }
        int day = getWeekDay();
        boolean isWeek = false;
        String[] locationWeekDay = MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_LOCATION_WEEK,"2,3").split(",");
        for (int i = 0; i < locationWeekDay.length; i++) {
            if (Integer.parseInt(locationWeekDay[i]) == day) {
                isWeek = true;
            }
        }
        if (!isWeek) {
            L.e("不在定位星期");
            return false;
        }
        String timeList = MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_LOCATION_TIMELIST, "07:30:00|18:00:00");
        L.e("timeList = " + timeList);
        int start = 0;
        int end = 0;
        if (timeList.contains("#")) {
            String[] firstList = timeList.split("#");
            String[] startList = firstList[0].split("\\|");
            String[] endList = firstList[firstList.length - 1].split("\\|");
            L.e("开始时间" + startList[0]);
            L.e("结束时间" + endList[endList.length - 1]);
            start = (Integer.parseInt(startList[0].split(":")[0]) * 60) + Integer.parseInt(startList[0].split(":")[1]);
            end = (Integer.parseInt(endList[endList.length - 1].split(":")[0]) * 60) + Integer.parseInt(endList[endList.length - 1].split(":")[1]);
        } else {
            String[] firstList = timeList.split("\\|");
            L.e("开始时间" + firstList[0]);
            L.e("结束时间" + firstList[firstList.length - 1]);
            start = (Integer.parseInt(firstList[0].split(":")[0]) *60 ) + Integer.parseInt(firstList[0].split(":")[1]);
            end = (Integer.parseInt(firstList[firstList.length - 1].split(":")[0]) *60 ) + Integer.parseInt(firstList[firstList.length - 1].split(":")[1]);
        }
        /*String[] locationTimeDay = MyApplication.getInstance().getSharedPreferences().getString(Constant.HUISHANG_LOCATION_TIMELIST, "07:30:00|18:00:00").split("|");
        L.e("locationTimeDay = " + locationTimeDay[0]);
         = loc*/
        if (!TimeUtil.isOnTheTime(start, end)) {
            L.e("不在时间段内");
            return false;
        }
        L.e("符合定位条件");
        return true;

    }

    /**
     * 获取当前星期几
     * @return
     */
    private int getWeekDay() {
        Calendar c = Calendar.getInstance();
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        int day = 0;
        if("1".equals(mWay)){
            day = 7;
        }else if("2".equals(mWay)){
            day = 1;
        }else if("3".equals(mWay)){
            day = 2;
        }else if("4".equals(mWay)){
            day = 3;
        }else if("5".equals(mWay)){
            day = 4;;
        }else if("6".equals(mWay)){
            day = 5;
        }else if("7".equals(mWay)){
            day = 6;
        }
        return day;
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

            if (location != null) {
                Lat = (Double) location.getLatitude();// 经度
                Lng = (Double) location.getLongitude();// 维度
                LocationUtil.stopLocation();
                LocationUtil.startReverseGeoCode(Lat, Lng,
                        getGeoCoderResultListener);
//				if (isloc) {
//					//位置获取完整后向服务器提交位置信息
//					webServiceHelp.start(getJson("高新", Lat, Lng));
//					L.e("getJson:" + getJson("高新北大道口", Lat, Lng));
//					//15分钟后再次执行定位
//					mhHandler.postDelayed(new Runnable() {
//
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							//启动定位服务
//							LocationUtil.startLocation(ctext, mLocationListener);
//							mhHandler.removeCallbacks(this);
//						}
//					},900000);
//				}
            }else {
                L.e("定位失败");
                //reStartLocation();
                return;
            }
        }
    };

    private OnGetGeoCoderResultListener getGeoCoderResultListener = new OnGetGeoCoderResultListener() {

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            // TODO Auto-generated method stub
            if (result == null) {
                L.e("转换失败");
                //reStartLocation();
                return;
            }

            location = result.getAddress();// 地理位置
            if (location.equals("null")) {
                location = "";
            }
            LocationUtil.stopReverseGeoCode();
            //位置获取完整后向服务器提交位置信息
            webServiceHelp.start(getJson(location, Lat, Lng));
            L.e("getJson:" + getJson(location, Lat, Lng));

            //15分钟后再次执行定位
           // mHandler.postDelayed(runnable,900000);
            //reStartLocation();
        }

        @Override
        public void onGetGeoCodeResult(GeoCodeResult arg0) {
            // TODO Auto-generated method stub

        }
    };

    /**
     * 重新开启定位
     */
   /* private void reStartLocation() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                //启动定位服务
                //LocationUtil.startLocation(getApplicationContext(), mLocationListener);
                mHandler.sendEmptyMessage(HanderUtil.case1);
            }
        };
        timer.schedule(task, (MyApplication.getInstance().getSharedPreferences().getInt(Constant.HUISHANG_LOCATION_FERQUENCY, 15) * 60 * 1000));
    }*/

   /* private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            //启动定位服务
            LocationUtil.startLocation(getApplicationContext(), mLocationListener);
            mHandler.removeCallbacks(this);
        }
    };*/

    /**
     * 获取json对象
     * @param
     * @return
     */
    private  String getJson(String address,double lat,double lng){
        ZJRequest<LocationList> zjRequest = new ZJRequest<LocationList>();
        LocationList list = new LocationList();
        list.setType(1);
        list.setAddress(address);
        list.setAddTime(getDate());
        list.setLat(lat);
        list.setLng(lng);
        list.setLatitude(lat);
        list.setLongitude(lng);
        list.setManager_ID(MyApplication.getInstance().getManagerID());
        list.setReason("");
        list.setStatus(1);
        zjRequest.setData(list);
        return JsonUtil.toJson(zjRequest);
    }

    /**
     * 获取系统当前时间，返回固定格式
     * @return 2015-03-08 14:00:01
     */
    private  String getDate(){
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(currentTime);
        System.out.println(formatter.format(date));
        return formatter.format(date);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
