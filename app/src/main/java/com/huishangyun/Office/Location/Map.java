package com.huishangyun.Office.Location;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.reflect.TypeToken;
import com.gotye.api.GotyeUser;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.T;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.Util.webServiceHelp;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Activity.Chat;
import com.huishangyun.Channel.Plan.CustomersListActivity;
import com.huishangyun.Channel.Plan.SortModel;
import com.huishangyun.Map.Location;
import com.huishangyun.Office.Location.MangerLocation.OnLoactionInfo;
import com.huishangyun.Office.Summary.ImageLoad;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.model.Methods;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.model.Constant;
import com.huishangyun.model.MessageType;
import com.huishangyun.yun.R;

/**
 * 位置显示
 * 
 */
public class Map extends BaseActivity implements OnLoactionInfo{
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;

	MapView mMapView;
	BaiduMap mBaiduMap;
	GeoCoder mSearch;
	OnGetGeoCoderResultListener listener;

	// UI相关
	private RelativeLayout back;
	boolean isFirstLoc = true;// 是否首次定位
	private String Loc;
	private TextView UserName;
	private LinearLayout selectLayout;
	private ArrayList<SortModel> arrayList;
	private TextView name,duty;//姓名，职务
	private ImageView img;//头像显示
	private View view;
	private TextView location;
	private Location receiverlocation;
	private LinearLayout call,message,chat;
	private String Mobile,JID,Name;
	private boolean isReceiver = false;
	private double mLat;//我的维度
	private double mLng;//我的经度
	 // 用MapController完成地图控制 
	private Map mMapController = null;
	private MangerLocation getonLoactionInfo;
	private boolean isNetReceiver = false;
	private TextView dateTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_office_location_map);
		getonLoactionInfo = new MangerLocation();
		getonLoactionInfo.setOnLoactionInfo(this);
		//注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constant.HUISHANG_ACTION_LOCATION);
		intentFilter.setPriority(Integer.MAX_VALUE);
		this.registerReceiver(receiver, intentFilter);
		getLocationDetails();
		//创建InfoWindow展示的view  
		view = LayoutInflater.from(Map.this)
				.inflate(R.layout.activity_office_location_map_text, null);
		location = (TextView) view.findViewById(R.id.location);
		name = (TextView) view.findViewById(R.id.name);
		duty = (TextView) view.findViewById(R.id.duty);
		img = (ImageView) view.findViewById(R.id.img);
		call = (LinearLayout) view.findViewById(R.id.call);
		message = (LinearLayout) view.findViewById(R.id.message);
		chat = (LinearLayout) view.findViewById(R.id.chat);
		
		
		back = (RelativeLayout) findViewById(R.id.back);
		UserName = (TextView) findViewById(R.id.UserName);
		dateTime = (TextView) findViewById(R.id.dateTime);
		selectLayout = (LinearLayout) findViewById(R.id.selectLayout);
		UserName.setText(preferences.getString(Constant.XMPP_MY_REAlNAME, ""));
		back.setOnClickListener(onClickListener);
		selectLayout.setOnClickListener(onClickListener);
		call.setOnClickListener(onClickListener);
		message.setOnClickListener(onClickListener);
		chat.setOnClickListener(onClickListener);
	
		mSearch = GeoCoder.newInstance();//创建地理编码检索实例
		mSearch.setOnGetGeoCodeResultListener(listener);
		mCurrentMode = LocationMode.NORMAL;
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		
//		// 修改为自定义marker
//		mCurrentMarker = BitmapDescriptorFactory
//				.fromResource(R.drawable.office_location);
//		mBaiduMap
//				.setMyLocationConfigeration(new MyLocationConfiguration(
//						mCurrentMode, true, mCurrentMarker));
		mCurrentMarker = null;
		mBaiduMap
		.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, null));
		
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		
	}
	


	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null || mBaiduMap == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			mLat = location.getLatitude();
			mLng = location.getLongitude();
			if (isFirstLoc) {
				isFirstLoc = false;
				final LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
				mHandler.postDelayed(new Runnable() {
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
				}, 1000);
				
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.selectLayout:
				Intent intent = new Intent(Map.this, CustomersListActivity.class);
				//选择个客户/人员界面，客户传值“0”，人员传值“1”（注意传String类型）
				intent.putExtra("mode", "1");
				//多选传0，单选传1
				intent.putExtra("select", 1);
				//传递分组名称
				intent.putExtra("groupName", "组");
				intent.putExtra("Tittle", "选择人员");
				startActivityForResult(intent, HanderUtil.case1);
				break;
			case R.id.call:
				if (Mobile != null) {
					intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + Mobile)); 
				    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					//启动
					startActivity(intent);
				} else {
					showCustomToast("无法获取联系人资料", false);
				}		
				break;
			case R.id.message:
				if (Mobile != null) {
					intent = new Intent();
					//系统默认的action，用来打开默认的短信界面
					intent.setAction(Intent.ACTION_SENDTO);
					//需要发短息的号码
					intent.setData(Uri.parse("smsto:" + Mobile));
					startActivity(intent);
				} else {
					showCustomToast("无法获取联系人资料", false);
				}
				break;
			case R.id.chat:
				//打开聊天界面
				intent = new Intent(Map.this, Chat.class);
				intent.putExtra("JID", JID);
				intent.putExtra("type", 2);
				intent.putExtra("name", Name);
				intent.putExtra("messtype", 0);
				intent.putExtra("chat_name", Name);
				intent.putExtra("Sign", "");
				GotyeUser gotyeUser = new GotyeUser(JID);
				intent.putExtra("user", gotyeUser);
				startActivity(intent);
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * 为地理位置添加图层信息
	 */
	private void setMessageLayout(double Lat, double Lng){
		
		//定义Maker坐标点  
		LatLng point = new LatLng(Lat, Lng);  
		//构建Marker图标  
		BitmapDescriptor bitmap = BitmapDescriptorFactory  
		    .fromResource(R.drawable.office_location);  
		//构建MarkerOption，用于在地图上添加Marker  
		OverlayOptions option1 = new MarkerOptions()  
		    .position(point)  
		    .icon(bitmap);  
		//在地图上添加Marker，并显示  
		mBaiduMap.addOverlay(option1);
		//定位到图层位置
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(point);
		mBaiduMap.animateMapStatus(u);
		//关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);

//	    // 反Geo搜索
//		mSearch.reverseGeoCode(new ReverseGeoCodeOption()
//				.location(point));
//		LatLng infoPoint = new LatLng(receiverlocation.getLatitude(), receiverlocation.getLongitude());  
//		mBaiduMap.addOverlay(new MarkerOptions().position(infoPoint)
//				.icon(BitmapDescriptorFactory
//						.fromView(view)));
		  
		L.e("为地理位置添加图层信息");
		// 反Geo搜索
		mSearch.reverseGeoCode(new ReverseGeoCodeOption()
				.location(point));
		
	}
	

	/**
	 * 创建地理编码检索监听者； 
	 */
	private void getLocationDetails(){
		
		 listener = new OnGetGeoCoderResultListener() {  
		    public void onGetGeoCodeResult(GeoCodeResult result) {  
		        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
		            //没有检索到结果  
		        }  
		        //获取地理编码结果  
		    }  
		 
		    @Override  
		    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {  
		        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
		            //没有找到检索结果  
		        	L.e("反向地理编码失败");
		        }  
		        //获取反向地理编码结果  
		        Loc = result.getAddress();
		        L.e("地理位置：" + Loc);
		        mHandler.sendEmptyMessage(HanderUtil.case4);
		        
		    }  
		};
	}
		
	
	

	
	/**
	 * 发送定位消息
	 * @param OFUserName 用户名
	 */
	private void sendMessage1(String OFUserName) {
		/*GotyeUser gotyeUser = new GotyeUser(OFUserName);
		MessageData<Location> messageData = new MessageData<Location>();
		messageData.setMessageCategory(MessageType.MESSAGE_LOCATION_GET);
		Location location = new Location();
		//location.setAppName("");
		messageData.setMessageContent(location);
		GotyeMessage message = GotyeMessage.createTextMessage(gotyeUser, JsonUtil.toJson(messageData));
		MyApplication.getInstance().getGotyeAPI().sendMessage(message);
		//删除消息
		MyApplication.getInstance().getGotyeAPI().deleteMessage(message);*/
		webServiceHelp<T> mServiceHelp = new webServiceHelp<>(Methods.SEND_SYS_MSG, new TypeToken<ZJResponse<T>>(){}.getType());
		mServiceHelp.setOnServiceCallBack(new webServiceHelp.OnServiceCallBack<T>() {

			@Override
			public void onServiceCallBack(boolean haveCallBack,
					ZJResponse<T> zjResponse) {
				// TODO Auto-generated method stub
				if (haveCallBack && zjResponse != null) {
					switch (zjResponse.getCode()) {
					case 0:
						L.e("发送位置命令成功");
						break;

					default:
						showCustomToast(zjResponse.getDesc(), false);
						MyApplication.getInstance().showDialog(Map.this, false, "正在请求位置");
						break;
					}
				} else {
					showCustomToast("服务器连接失败", false);
					MyApplication.getInstance().showDialog(Map.this, false, "正在请求位置");
				}
			}
		});
		ZJRequest zjRequest = new ZJRequest();
		zjRequest.setOFUserName(OFUserName);
		zjRequest.setNote("0*0*" + MessageType.MESSAGE_LOCATION_GET + "*" + MyApplication.preferences.getString(Constant.XMPP_LOGIN_NAME, ""));
		mServiceHelp.start(JsonUtil.toJson(zjRequest));
		L.e("发送成功");
		isReceiver = false;
		showDialog("正在请求实时位置信息");
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!isReceiver) {
					dismissDialog();
					showDialog("实时获取失败,正在请求云端数据");
					mHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							isNetReceiver = false;
							mHandler.sendEmptyMessage(HanderUtil.case5);
						}
					}, 3000);
					
				}
			}
		}, 20000);
	}
	

	/**
	 * 接收对方定位信息
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			receiverlocation = (Location) intent.getSerializableExtra(MessageType.MESSAGE_LOCATION_RESULT);
			mHandler.sendEmptyMessage(HanderUtil.case3);
			L.e("接收对方定位信息");
			isReceiver = true;
			dismissDialog();
		}
	};
	
	
	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.unRegisterLocationListener(myListener);
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		mSearch.destroy();
		this.unregisterReceiver(receiver);//注销广播
		mBaiduMap = null;
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		if (arg0 == HanderUtil.case1 && arg1 == RESULT_OK) {
	        Bundle bundle = arg2.getBundleExtra("bundle");//得到新Activity 关闭后返回的数据
	        arrayList = (ArrayList<SortModel>) bundle.getSerializable("result");//接收泛型
	        if (arrayList.size()!=0) {
	        	//清除覆盖物
	     		mBaiduMap.clear(); 
	        	mHandler.sendEmptyMessage(HanderUtil.case2);
	        	dateTime.setVisibility(View.INVISIBLE);
			}
		}
		
		super.onActivityResult(arg0, arg1, arg2);
	}

	/**
	 * 异步数据处理
	 */
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HanderUtil.case2:
				UserName.setText(arrayList.get(0).getCompany_name());
				name.setText(arrayList.get(0).getCompany_name());
				duty.setText(arrayList.get(0).getManger_Name());
				Mobile = arrayList.get(0).getMobile(); 
				JID = arrayList.get(0).getOFUserName();
				Name = arrayList.get(0).getCompany_name();
				location.setText("");
				String person_url = Constant.pathurl+
						MyApplication.getInstance().getCompanyID()+"/Photo/100x100/" + arrayList.get(0).getPerson_img();
				new ImageLoad().displayImage(context, person_url, img, R.drawable.defaultimage02, 10, false);
				sendMessage1(arrayList.get(0).getOFUserName());
				break;
			case HanderUtil.case3:
				setMessageLayout(receiverlocation.getLatitude(), receiverlocation.getLongitude());
				break;
			case HanderUtil.case4:
				location.setText(Loc);//设置显示信息  
				LatLng point = new LatLng(receiverlocation.getLatitude(), receiverlocation.getLongitude());  
				//创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量 
				InfoWindow mInfoWindow = new InfoWindow(view, point, -80);  
				//显示InfoWindow  
				mBaiduMap.showInfoWindow(mInfoWindow);
				
				//处理缩放 sdk 缩放级别范围： [3.0,19.0]
				float zoomLevel = (float) 15.0;
				MapStatusUpdate u1 = MapStatusUpdateFactory.zoomTo(zoomLevel);
				mBaiduMap.animateMapStatus(u1);
				//关闭定位图层
				mBaiduMap.setMyLocationEnabled(false);
				
//				//设置两点使两点都在范围内,地图加载完才有用
//				final LiOverlayManager overlayManager = new LiOverlayManager(mBaiduMap);
//				mBaiduMap.setOnMarkerClickListener(overlayManager);
//			    List<OverlayOptions> asList = new ArrayList<OverlayOptions>();
//			    OverlayOptions option1 = new MarkerOptions().position(point);
//			    asList.add(option1);
//			    LatLng mPoint = new LatLng(mLat, mLng);  
//			    OverlayOptions ooA = new MarkerOptions().position(mPoint);
//			    asList.add(ooA);
//	            overlayManager.setData(asList);	           
//	            overlayManager.addToMap();
//	            overlayManager.zoomToSpan();
              
				break;
			case HanderUtil.case5:
			
				getonLoactionInfo.getLoactionInfo(arrayList.get(0).getID(), 
						Integer.parseInt(arrayList.get(0).getGroup_ID()), MyApplication.getInstance().getCompanyID());
				
				//5s后判断是否获取到网络数据
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (!isNetReceiver) {
						    dismissDialog();
							showCustomToast("请求地理位置超时", false);
						}
					}
				},3000);
				break;
			case HanderUtil.case6:
				L.e("接收到了数据");
				setLocinfo((List<LocationList>) msg.obj);
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onLoactionInfo(List<LocationList> list) {
		// TODO Auto-generated method stub
		dismissDialog();
		if (list.size()>0) {
		if (list.get(0).getLocation() == null || list.get(0).getLatitude() == null) {
			return;
		}
		isNetReceiver = true;
		Message msg = mHandler.obtainMessage();
		msg.what = HanderUtil.case6;
		msg.obj = list;
		msg.sendToTarget();
		}
	}

	private void setLocinfo(List<LocationList> list){
		//定义Maker坐标点  
		LatLng point = new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude());  
		//构建Marker图标  
		BitmapDescriptor bitmap = BitmapDescriptorFactory  
		    .fromResource(R.drawable.office_location);  
		//构建MarkerOption，用于在地图上添加Marker  
		OverlayOptions option1 = new MarkerOptions()  
		    .position(point)  
		    .icon(bitmap);  
		//在地图上添加Marker，并显示  
		mBaiduMap.addOverlay(option1);
		//定位到图层位置
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(point);
		mBaiduMap.animateMapStatus(u);
		
		location.setText(list.get(0).getLocation());//设置显示信息  
		dateTime.setVisibility(View.VISIBLE);
		dateTime.setText("获取时间:" + list.get(0).getAddDateTime().replace("T", " "));
		//创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量 
		InfoWindow mInfoWindow = new InfoWindow(view, point, -80);  
		//显示InfoWindow  
		mBaiduMap.showInfoWindow(mInfoWindow);
		//关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//处理缩放 sdk 缩放级别范围： [3.0,19.0]
				float zoomLevel = (float) 15.0;
				MapStatusUpdate u1 = MapStatusUpdateFactory.zoomTo(zoomLevel);
				mBaiduMap.animateMapStatus(u1);
			}
		}, 1000);
	
	}
}
