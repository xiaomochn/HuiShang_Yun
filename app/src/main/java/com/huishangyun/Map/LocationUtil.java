package com.huishangyun.Map;

import android.content.Context;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;

/**
 * 定位帮助类
 * @author Pan
 *
 */
public class LocationUtil {
	private static LocationClient mLocClient = null;
	private static GeoCoder mSearch;
	
	/**
	 * 开启定位
	 * @param context 上下文对象
	 * @param mBdLocationListener 定位SDK监听函数
	 */
	public static void startLocation(Context context, BDLocationListener mBdLocationListener) {
		mLocClient = new LocationClient(context);
		mLocClient.registerLocationListener(mBdLocationListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(30000);
		option.setAddrType("all");
		option.disableCache(true);
		option.setPoiNumber(20);
		option.setPoiDistance(200);
		option.setPoiExtraInfo(true);
		option.setPriority(LocationClientOption.GpsFirst);
		mLocClient.setLocOption(option);
		mLocClient.setAK("HhHeEi0ZV5uwEEVPHsTXxk3u");
		mLocClient.start();
		mLocClient.requestLocation();
	}
	
	/**
	 * 关闭定位
	 */
	public static void stopLocation() {
		if (mLocClient != null) {
			mLocClient.stop();
			mLocClient = null;
		}
	}
	
	/**
	 * 开始反地理编码
	 * @param Latitude-纬度
	 * @param Longitude-经度
	 * @param listener-地理编码监听
	 */
	public static void startReverseGeoCode(double Latitude, double Longitude, OnGetGeoCoderResultListener listener) {
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(listener);
		LatLng ptCenter = new LatLng(Latitude, Longitude);
		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
	}

	/**
	 * 关闭地理编码
	 */
	public static void stopReverseGeoCode() {
		if (mSearch != null) {
			mSearch.destroy();
			mSearch = null;
		}
	}
}
