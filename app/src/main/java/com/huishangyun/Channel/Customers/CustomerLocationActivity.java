package com.huishangyun.Channel.Customers;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Adapter.PoiAdapter;
import com.huishangyun.Map.LocationUtil;
import com.huishangyun.Util.L;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.model.PoiUtils;
import com.huishangyun.yun.R;
/**
 * 定位界面
 * @author Pan
 *
 */
public class CustomerLocationActivity extends BaseActivity implements BDLocationListener {
	private MapView mMapView;
	private MyXListView mListView;
	private BaiduMap mBaiduMap;
	private boolean isFirstLoc = true;// 是否首次定位
	private boolean isFirstRec = true;//是否首次反地理
	private double Latitude = 0;//纬度
	private double Longitude = 0;//经度
	private PoiSearch mPoiSearch;
	private List<PoiUtils> mList = new ArrayList<PoiUtils>();
	private PoiAdapter poiAdapter;
	private int pageNum = 0;
	private int selectNum = -1;
	private LatLng ll;
	private Button submit;
	private String addres;
	private ReverseGeoCodeResult.AddressComponent address;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_location);
		initBackTitle("选择当前位置");
		initView();
		showDialog("正在定位...");
		setResult(RESULT_CANCELED);
	}
	
	/**
	 * 实例化组件
	 */
	private void initView() {
		mMapView = (MapView) this.findViewById(R.id.customer_bmapView);
		mListView = (MyXListView) this.findViewById(R.id.customer_location_list);
		mListView.setMyXListViewListener(myXListViewListener);
		mListView.setPullLoadEnable(true);//设置能下拉（这两个属性是上拉加载下拉刷新的）
		mBaiduMap = mMapView.getMap();
		//开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
		// 定位初始化
		LocationUtil.startLocation(this, CustomerLocationActivity.this);
		poiAdapter = new PoiAdapter(CustomerLocationActivity.this, mList);
		mListView.setAdapter(poiAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				L.e("选中第" + arg2);
				if (arg2 == 0) {
					return;
				}
				if (selectNum == -1) {
					PoiUtils poiUtils = mList.get(arg2 - 1);
					poiUtils.setIsCheked(true);
					mList.remove(arg2 - 1);
					mList.add(arg2 - 1, poiUtils);
					selectNum = arg2 - 1;
				} else {
					PoiUtils poiUtils = mList.get(selectNum);
					poiUtils.setIsCheked(false);
					mList.remove(selectNum);
					mList.add(selectNum, poiUtils);
					
					PoiUtils poiUtils2 = mList.get(arg2 - 1);
					poiUtils2.setIsCheked(true);
					mList.remove(arg2 - 1);
					mList.add(arg2 - 1, poiUtils2);
					selectNum = arg2 - 1;
				}
				poiAdapter.notifyDataSetChanged();
				
			}
		});
		submit = (Button) findViewById(R.id.location_submit);
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//这里返回地址和坐标
				if (selectNum != -1) {
					Intent intent = new Intent();
					intent.putExtra("Name", mList.get(selectNum).getPoiInfo().address);
					LatLng location = mList.get(selectNum).getPoiInfo().location;
					intent.putExtra("Latitude", location.latitude);
					intent.putExtra("Longitude", location.longitude);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					showCustomToast("请选择地址", false);
				}
				
			}
		});
	}
	
	/**
	 * listview刷新监听
	 */
	private MyXListView.MyXListViewListener myXListViewListener = new MyXListView.MyXListViewListener() {
		
		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			pageNum = 0;
			mPoiSearch.searchNearby(new PoiNearbySearchOption().location(ll).keyword("公司企业").radius(300).pageCapacity(20).pageNum(pageNum));
		}
		
		@Override
		public void onLoadMore() {
			// TODO Auto-generated method stub
			pageNum++;
			mPoiSearch.searchNearby(new PoiNearbySearchOption().location(ll).keyword("公司企业").radius(300).pageCapacity(20).pageNum(pageNum));
		}
	};
	
	@Override
	public void onReceiveLocation(BDLocation location) {
		// TODO Auto-generated method stub
		if (location == null || mMapView == null) {
			L.e("result = null;");
			return;
		}
		if (isFirstLoc) {
			L.e("Poi = " + location.getPoi());
			MyLocationData locData = new MyLocationData.Builder()
			.accuracy(location.getRadius())
			// 此处设置开发者获取到的方向信息，顺时针0-360
			.direction(100).latitude(location.getLatitude())
			.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			isFirstLoc = false;
			ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
			//获取纬度
			Latitude = location.getLatitude();
			//获取经度
			Longitude = location.getLongitude();
			
			//开启反地理编码
			LocationUtil.startReverseGeoCode(Latitude, Longitude, listener);
			//mPoiSearch.searchInBound(new PoiBoundSearchOption().);
		}
		
	}
	
	
	OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {

		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {
			// TODO Auto-generated method stub
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
	            //没有检索到结果  
	        }  
	        //获取地理编码结果 
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			// TODO Auto-generated method stub
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
	            //没有找到检索结果  
				showCustomToast("没有找到结果", false);
	        }
			mBaiduMap.clear();
			/*mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.icon_gcoding)));*/
			address = result.getAddressDetail();
			addres = address.province + address.city + address.district + address.street + address.streetNumber;
			View view = LayoutInflater.from(CustomerLocationActivity.this)
					.inflate(R.layout.map_view, null);
			TextView addressView = (TextView) view.findViewById(R.id.map_textview);
			addressView.setText(addres);
			mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
					.icon(BitmapDescriptorFactory
							.fromView(view)));
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
					.getLocation()));
			L.e("地址为:"+ addres);
			mPoiSearch.searchNearby(new PoiNearbySearchOption().location(ll)
					.keyword("公司企业")
					.radius(300).pageCapacity(20).pageNum(pageNum));
			
		}  
		
	};
	
	/**
	 * poi搜索监听
	 */
	OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){  
	    public void onGetPoiResult(PoiResult result){ 
	    	mListView.stopLoadMore();
	    	mListView.stopRefresh();
	    	mListView.setRefreshTime();
	    	dismissDialog();
	    	//获取POI检索结果  
	    	if (pageNum == 0) {
	    		mList.clear();
			}
	    	if (result.getAllPoi() == null || result.getAllPoi().size() == 0) {
				showCustomToast("没有更多了", false);
				return;
			}
	    	for (PoiInfo poiInfo : result.getAllPoi()) {
	    		PoiUtils poiUtils = new PoiUtils();
	    		poiUtils.setPoiInfo(poiInfo);
	    		poiUtils.setIsCheked(false);
	    		mList.add(poiUtils);
			}
	    	L.e("获取到" + mList.size() + "个兴趣点");
	    	poiAdapter.notifyDataSetChanged();
	    }  
	    public void onGetPoiDetailResult(PoiDetailResult result){  
	    //获取Place详情页检索结果  
	    }  
	};
	
	@Override
	public void onReceivePoi(BDLocation poiLocation) {}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mMapView.onPause();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mMapView.onResume();
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// 退出时销毁定位
		LocationUtil.stopLocation();
		LocationUtil.stopReverseGeoCode();
		mPoiSearch.destroy();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

}
