package com.huishangyun.Channel.Orders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.ImageLoad.ImageLoader;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.manager.CartManager;
import com.huishangyun.manager.ProductManager;
import com.huishangyun.model.CartModel;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.Methods;
import com.huishangyun.model.Order_GoodsList;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.yun.R;

public class FenleiActivity extends BaseActivity implements MyXListView.MyXListViewListener {
	
	private ImageView img;
	private TextView title_name;
	/**
	 * 获取手机屏幕分辨率的类和手机的宽高
	 */
	private DisplayMetrics dm; 
	private int widths, heights;
	private PopupWindow popupWindow;
	
	private MyXListView listView;
	private int[] imgs = new int[]{R.drawable.product, R.drawable.product, R.drawable.product, 
			R.drawable.product, R.drawable.product, R.drawable.product};
	
	/**
	 * 用来存放点击过的list子项的集合
	 */
	private int id_goods;
	private List<Integer> list_id;
	private CartModel cartModel;
	
	private TextView tv_car, tv_shoucang;
	private LinearLayout back, more;	
	private FrameLayout gouwuche, shoucang;
	
	private int pageSize = 10;
	private boolean flag = true; 
	private MyAdapter myAdapter;
	private List<Order_GoodsList> data = new ArrayList<Order_GoodsList>();
	private List<Order_GoodsList> data2 = new ArrayList<Order_GoodsList>();	
	private List<Order_GoodsList> data3 = new ArrayList<Order_GoodsList>();	
	private int Manager_ID, Company_ID;
	private int class_ID;
	private BaseActivity baseActivity;
	
    private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1://获取到数据
				listView.stopRefresh();
				//获取上次存入的时间
				listView.setRefreshTime();	
				
				for ( int i = 0; i < data2.size(); i++) {
					data.add(data2.get(i));
				}				
				myAdapter.notifyDataSetChanged();//加载了数据不改变界面
				//每次刷新前把当前时间存入
				Common.postTime(baseActivity.getTime());
				//activity.dismissDialog();
				break;
				
			case HanderUtil.case2://数据获取失败
				listView.stopRefresh();
				listView.setRefreshTime();	
				showCustomToast((String) msg.obj, false);
				break;
				
			case HanderUtil.case3:
				myAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_fenlei_shipin);
		
		baseActivity = new BaseActivity();
		// 获得手机的宽度和高度
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		widths = dm.widthPixels;
		heights = dm.heightPixels;
		
		Manager_ID = Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0"));
		Company_ID = preferences.getInt(Content.COMPS_ID, 1016);
		
		init();
		class_ID = Integer.parseInt(getIntent().getStringExtra("class_ID"));
		L.e("要显示的类别的class_ID：" + class_ID);
		//第一次进来读取数据库的数据，下拉刷新再读取接口更新数据库。
		getData2(class_ID, 1);
	}
	
	/**
	 * 获取到购物车数据的ID集合，和购物车与收藏的条数
	 */
	protected void onResume() {
		//第一次进来和从其他页面过来的时候获取购物车产品 ID 的集合.
		list_id = new ArrayList<Integer>();//每次进入页面要清除之前加载的选中项，在购物车可能删除的			
		List<CartModel> data3 = new ArrayList<CartModel>();
		data3 = CartManager.getInstance(FenleiActivity.this).getCartModels();
		
		for (int i = 0; i < data3.size(); i++) {
			list_id.add(data3.get(i).getProduct_ID());
		}
		L.e("获取到list_id集合：" + list_id.toString());		
		
		//公共的从数据库获取购物车数量
		Common.getCarCounts(FenleiActivity.this, tv_car);
		//tv_car.setText(CartManager.getInstance(OrderMainActivity.this).getSize());
		//获取到收藏个数的方法
		Common.getShouCangCounts(FenleiActivity.this, tv_shoucang);
		//getList();						
		super.onResume();
	}
	
	/**
	 * 从数据库获取数据通知数据改变的方法
	 * 传入一个页码数
	 */
	private void getData2(int class_ID, int pageSize){
		List<Order_GoodsList> data3 = new ArrayList<Order_GoodsList>();
		//从数据库获取的方法
		data3 = ProductManager.getInstance(FenleiActivity.this).getGoodsFromClass(class_ID, pageSize);
		
		if (data3.size() > 0) {			
			img.setVisibility(View.GONE);
				
			for ( int i = 0; i < data3.size(); i++) {
				data.add(data3.get(i));
			}				
			myAdapter.notifyDataSetChanged();
			
		}else {//没有数据读取网络。
			if (pageSize == 1) {
				img.setVisibility(View.VISIBLE);
			}
			
//			data.clear();
//			getList(class_ID);
//			showCustomToast("没有更多数据", false);
		}		
	}
	
	private void init(){
		title_name = (TextView)findViewById(R.id.title_name);
		title_name.setText(getIntent().getStringExtra("name"));
		img = (ImageView)findViewById(R.id.no_img);
		
		tv_car = (TextView)findViewById(R.id.nub_gouwuche);	
		tv_shoucang = (TextView)findViewById(R.id.nub_shoucang);
		listView = (MyXListView)findViewById(R.id.listview);
		listView.setPullLoadEnable(true);//设置能下拉（这两个属性是上拉加载下拉刷新的）
		listView.setMyXListViewListener(this);//设置接口
		
		myAdapter = new MyAdapter(this);
		listView.setAdapter(myAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(FenleiActivity.this, DatailsActivity.class);	
				//把点中的item的商品id传给详情
				if (position == 0 ) {
					return;
				}else {
					//把点击产品的id传给产品详情页。
					intent.putExtra("id", data.get(position - 1).getID() + "");
												
					//把获取到activity的收藏的数量传递到产品详情
					TextView text =(TextView)findViewById(R.id.nub_shoucang);
					intent.putExtra("shoucang_nub", text.getText().toString());					
					startActivity(intent);	
				}	
				
			}
		});
		
		back = (LinearLayout)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();				
			}
		});
		
		more = (LinearLayout)findViewById(R.id.xiala);
		more.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				initPopuptWindow();
				popupWindow.showAsDropDown(v);  
			}
		});
		
		gouwuche = (FrameLayout)findViewById(R.id.gouwuche);
		gouwuche.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(FenleiActivity.this, ShopCarActivity.class);	
				startActivity(intent);
			}
		});
		
		shoucang = (FrameLayout)findViewById(R.id.shoucang);
		shoucang.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(FenleiActivity.this, ShouCangActivity.class);	
				startActivity(intent);			
			}
		});
				
	}
	
	/**
	 * 获取产品列表线程
	 * @param json
	 */
	private void getList(int class_ID){//int index_start
		//实例化请求类
	    ZJRequest zjRequest = new ZJRequest();
	    
	    //设置你需要的属性	    	    					    
	    zjRequest.setManager_ID(Manager_ID);
	    zjRequest.setCompany_ID(Company_ID);
	    zjRequest.setPageIndex(1);
	    //zjRequest.setPageSize(1);
	    zjRequest.setKeywords("");
	    zjRequest.setClass_ID(class_ID);
	    final String json = JsonUtil.toJson(zjRequest);
	    L.e("(（详细类别）)jsonString:" + json);
		new Thread(){
		    	public void run() {
		    		try {
						String jsonString = DataUtil.callWebService(Methods.ORDER_GOODS_LIST, json);
						L.e("（详细类别）json:" + jsonString);
						if (jsonString != null) {
							Type type = new TypeToken<ZJResponse<Order_GoodsList>>(){}.getType();
						    ZJResponse<Order_GoodsList> zjResponse = JsonUtil.fromJson(jsonString, type);
						    if (zjResponse.getCode() == 0) {
						    	data2.clear();
							    data2 = zjResponse.getResults();
							    myHandler.sendEmptyMessage(HanderUtil.case1);
							    
							  //获取到接口数据就在线程里面添加进数据库，在后面可能会不同步。
							    for (int i = 0; i < data2.size(); i++) {									
									ProductManager.getInstance(FenleiActivity.this).saveProductInfo(data2.get(i));
								}
							    
							} else {
								Message message = new Message();
							    message.what = HanderUtil.case2;
							    message.obj = zjResponse.getDesc();
							    myHandler.sendMessage(message);
							}
						}									    
					    
					} catch (Exception e) {
						e.printStackTrace();
					}
		    	};
		    }.start();
	}
	
	
	private class MyAdapter extends BaseAdapter{
		
		private LayoutInflater inflater;// 用来导入布局
		
		public MyAdapter(Context context) {// 构造器
			this.inflater = LayoutInflater.from(context);
		}
		
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			
			final ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.list_all, null);
				holder.img = (ImageView) view.findViewById(R.id.img);
				holder.tv = (TextView) view.findViewById(R.id.name);
				holder.tv2 = (TextView) view.findViewById(R.id.price);
				holder.img2 = (ImageView) view.findViewById(R.id.imgcar);
									
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
			
			//将获取到的图片路径通过一个方法转化为图片。			
			new ImageLoader(FenleiActivity.this).DisplayImage(
					Common.getPath() + data.get(position).getSmallImg(), holder.img, false);
			
			holder.tv.setText(data.get(position).getName());
			holder.tv2.setText("￥" + data.get(position).getSalePrice() +
					"/" + data.get(position).getUnit_Name());
			
			if (list_id.contains(data.get(position).getID())) {
				holder.img2.setImageResource(R.drawable.shopping_car_blue);
				
			}else {
				holder.img2.setImageResource(R.drawable.shopping_car_grey);
			}
			
			holder.img2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					//删除和添加的时候根据商品名称来删除某项和判断这项是否已添加
					id_goods = data.get(position).getID();
					
					if (list_id.contains(id_goods)) { //如果已经包含了就取消
						holder.img2.setImageResource(R.drawable.shopping_car_grey);
												
						//删除集合里面某一数据的方法，list_id.remove()是删除里面某个下标的方法;
						Iterator<Integer> iterator = list_id.iterator(); 
						while(iterator.hasNext()){  
							int e = iterator.next();  
							if(e == id_goods){  
								iterator.remove();  
							}  
						}  											
						//删除数据库的这条数据。
						CartManager.getInstance(FenleiActivity.this).deleteCart(id_goods);
						//dbDel();
						
						deletNub();//点击删除
						L.e("（删除后）list_id:" + list_id.toString());
					} else {			
						cartModel = new CartModel();
						
						cartModel.setSmallImg(data.get(position).getSmallImg());
						cartModel.setProduct_ID(data.get(position).getID());
						cartModel.setProduct_Name(data.get(position).getName());
						cartModel.setPrice((float)data.get(position).getSalePrice());
						cartModel.setUnit_Name(data.get(position).getUnit_Name());						
						cartModel.setQuantity((float)1);
												
						//调用保存进数据库的方法
						CartManager.getInstance(FenleiActivity.this).saveCarts(cartModel);
																							    						
						//已加入的不用走下面
						holder.img2.setImageResource(R.drawable.shopping_car_blue);
						list_id.add(id_goods);
						L.e("（增加后）list_id:" + list_id.toString());
						addNub();																		
					}					
				}
			});
			
			return view;
		}
		
		public final class ViewHolder {// 写这个类是为了点击list的时候好调用view
			public ImageView img;
			public TextView tv;
			public TextView tv2;
			public ImageView img2;
		}
	}
	
	/**  
	* 创建PopupWindow  
	*/   
	public void initPopuptWindow() {	
				
		//获取自定义布局文件pop.xml的视图   
		View popupWindow_view = getLayoutInflater().inflate(R.layout.popupwindow, null,false);   		
		
		//创建PopupWindow实例,把上面得到屏幕宽高设置过来 
		popupWindow = new PopupWindow(popupWindow_view, widths/2, 
				ViewGroup.LayoutParams.WRAP_CONTENT, true);   
		//设置动画效果   
		//popupWindow.setAnimationStyle(R.style.AnimationFade);
		
		//设置此参数获得焦点，否则无法点击 
		popupWindow.setFocusable(true); 
		//防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
		popupWindow.setBackgroundDrawable(new PaintDrawable()); 
		//设置点击窗口外边窗口消失 
		popupWindow.setOutsideTouchable(true); 		
		popupWindow.update();  
		
		LinearLayout dingdan = (LinearLayout)popupWindow_view.findViewById(R.id.dingdan);
		LinearLayout tuidan = (LinearLayout)popupWindow_view.findViewById(R.id.tuidan);
		dingdan.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANG_TYPE,"0")) == 1) {
					L.e("权限：" + Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANG_TYPE,"0")));
					Intent intent = new Intent(FenleiActivity.this, DingDan_CommonActivity.class);
					intent.putExtra("flage", "DING");//因为订单和退单复用了两种权限的订单/退单列表的各个页面，所以要区分开
					startActivity(intent);
				}else {
					Intent intent = new Intent(FenleiActivity.this, DingDanListActivity.class);
					intent.putExtra("flage", "DING");
					startActivity(intent);
				}
				popupWindow.dismiss();
			}
		});
		tuidan.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANG_TYPE,"0")) == 1) {
					L.e("权限：" + Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANG_TYPE,"0")));
					Intent intent = new Intent(FenleiActivity.this, DingDan_CommonActivity.class);
					intent.putExtra("flage", "TUI");
					startActivity(intent);
				}else {
					Intent intent = new Intent(FenleiActivity.this, DingDanListActivity.class);					
					intent.putExtra("flage", "TUI");						
					startActivity(intent);
				}	
				popupWindow.dismiss();
			}
		});
				
	}

	/**
	 * 下拉刷新
	 */
	public void onRefresh() {		
//		if (baseActivity.PingService()) {//当有网时
//			data.clear();
//			getList(class_ID);
//			
//		}else {
//			listView.stopRefresh();
//			//获取系统的时间
//			listView.setRefreshTime();	
//			showCustomToast("当前网络不可用", false);
//		}
		
		listView.stopRefresh();
		//获取系统的时间
		listView.setRefreshTime();	
	}

	/**
	 *  上拉加载 
	 */
	public void onLoadMore() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				listView.stopLoadMore();								
				if (flag == true) {
					flag = false;//走线程之前改为false，走完才能再加载
					pageSize = pageSize + 1;
					getData2(class_ID, pageSize);
					
					flag = true;																				
				}
			}
		}, 2000);
	}
	
	
	
	/**
	 * 点击购物车红点数字加减的方法
	 */
	public void addNub() {	
		int count = Integer.parseInt(tv_car.getText().toString()) + 1;
		tv_car.setVisibility(View.VISIBLE);
		tv_car.setText(count + "");
	}
	
	public void deletNub() {									
		int count = Integer.parseInt(tv_car.getText().toString()) - 1;
		if (count > 0) {
			tv_car.setText(count + "");
			tv_car.setVisibility(View.VISIBLE);
			
		}else {
			count = 0;
			tv_car.setText(count + "");
			tv_car.setVisibility(View.GONE);
		}											
	}
}
