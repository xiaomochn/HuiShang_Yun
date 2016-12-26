package com.huishangyun.Office.Location;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.content.Context;
import android.os.Handler;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.reflect.TypeToken;
import com.huishangyun.Map.LocationUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.model.Methods;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.T;

public class MangerLocation {
	
	private Context ctext;
	private com.huishangyun.Util.webServiceHelp<LocationList> webServiceHelp;
	private double Lng;
	private double Lat;
	private String location;
	private Handler mhHandler = new Handler();
	private OnLoactionInfo onLoactionInfo = null;
	private List<LocationList> list = new ArrayList<LocationList>();
	private boolean isloc = false;

	public  void sendLocationInfo(Context context,boolean isLocation){
		ctext = context;
		isloc = isLocation;
		if (isLocation) {
			webServiceHelp = new webServiceHelp<LocationList>(Methods.SET_LOCATION_INFO, new TypeToken<ZJResponse<LocationList>>(){}.getType());
			webServiceHelp.setOnServiceCallBack(onServiceCallBack);
			//启动定位服务
			LocationUtil.startLocation(ctext, mLocationListener);
		}
	}
	/**
	 * 定义一个接口
	 * @author xsl
	 *
	 */
  public interface OnLoactionInfo {
	public  void onLoactionInfo(List<LocationList> list);
  }
  /**
   * 实现接口监听
   * @param onLoactionInfo
   */
  public void setOnLoactionInfo(OnLoactionInfo onLoactionInfo){
	  this.onLoactionInfo = onLoactionInfo;
  }
	
	
	/**
	 * 定位监听
	 */
	private  BDLocationListener mLocationListener = new BDLocationListener() {

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
				return;
			}
		}
	};

	private  OnGetGeoCoderResultListener getGeoCoderResultListener = new OnGetGeoCoderResultListener() {

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
			if (isloc) {
				//位置获取完整后向服务器提交位置信息
				webServiceHelp.start(getJson(location, Lat, Lng));
				L.e("getJson:" + getJson(location, Lat, Lng));
				
				//15分钟后再次执行定位
				mhHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						//启动定位服务
						LocationUtil.startLocation(ctext, mLocationListener);
						mhHandler.removeCallbacks(this);
					}
				},900000);
				
			}
		}

		@Override
		public void onGetGeoCodeResult(GeoCodeResult arg0) {
			// TODO Auto-generated method stub

		}
	};

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
    
    /**
     * 获取单个或者多个人位置信息
     */
    public void getLoactionInfo(int ManagerID,int DepartmentID,int CompanyID){
    	webServiceHelp<LocationList> webServiceHelp = new webServiceHelp<LocationList>(Methods.GET_LOCATION_INFO, new TypeToken<ZJResponse<LocationList>>(){}.getType());
		webServiceHelp.setOnServiceCallBack(onServiceCallBack1);
		webServiceHelp.start(getJson(ManagerID, DepartmentID,CompanyID));
		L.e("getJson===:" + getJson(ManagerID, DepartmentID,CompanyID));
    }
    
    /**
     * 获取json对象
     * @paramDriver_ID 司机id
     * @return
     */
    private  String getJson(int ManagerID,int DepartmentID,int CompanyID){
        ZJRequest<T> zjRequest = new ZJRequest<T>();
        zjRequest.setManager_ID(ManagerID);
        zjRequest.setDepartment_ID(DepartmentID);
        zjRequest.setCompany_ID(CompanyID);
        return JsonUtil.toJson(zjRequest);
    }
    
    /**
	 * 获取最近一次地理位置信息接口回调
	 */
	private com.huishangyun.Util.webServiceHelp.OnServiceCallBack<LocationList> onServiceCallBack1 = new com.huishangyun.Util.webServiceHelp.OnServiceCallBack<LocationList>() {
		public void onServiceCallBack(boolean haveCallBack,
				ZJResponse<LocationList> zjResponse) {
			if (haveCallBack && zjResponse != null) {
				switch (zjResponse.getCode()) {
				case 0:
                    L.e("获得地理位置信息：" + zjResponse.getResults());
                    list = zjResponse.getResults();
	              if (onLoactionInfo != null ) {
	            	  onLoactionInfo.onLoactionInfo(list);
				  }
                  
					break;
				default:
					 L.e("获得地理位置信息失败");
					break;
				}
			}
		}
	};
	
	
}
	
		
	
