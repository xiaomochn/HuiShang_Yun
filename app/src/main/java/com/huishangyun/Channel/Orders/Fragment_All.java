package com.huishangyun.Channel.Orders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.asyncimageloader.ImageDownLoader;
import com.huishangyun.manager.CartManager;
import com.huishangyun.manager.ProductManager;
import com.huishangyun.model.CartModel;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.Methods;
import com.huishangyun.model.Order_GoodsList;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.yun.R;

public class Fragment_All extends Fragment implements MyXListView.MyXListViewListener {
private static final int List = 0;
//	/**
//	 * 数据库操作
//	 */
//	private MySql mySql;
//	private SQLiteDatabase db;
	/**
	 * 要删除的数据库某条数据的id
	 */
	private int id_goods;
	private List<Integer> list_id;
	
	private MyAdapter mAdapter;
	private MyXListView listView;
	private List<Order_GoodsList> data = new ArrayList<Order_GoodsList>();
	private List<Order_GoodsList> data2 = new ArrayList<Order_GoodsList>();	

	private GetNub getNub;
	
	/**
	 * 获取网络情况，系统时间等等的类
	 */
	private BaseActivity baseActivity;
	private int index_start = 1;
	private boolean flag = true; 
	
	private int Manager_ID;
    private int Company_ID;
    private CartModel cartModel;
    
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1://获取到数据
				//下拉的效果在获取网络数据的handle里面做，在线程后操作可能会出现线程没走完，就效果完成的情况。
				//这种情况下data数据改变就能listview的显示，不用通知数据改变。
				listView.stopRefresh();
				listView.setRefreshTime();
				data.clear();
			    for ( int i = 0; i < data2.size(); i++) {//不同的时候data清除情况不同
					data.add(data2.get(i));
				}				
				mAdapter.notifyDataSetChanged();//加载了数据不改变界面
				//activity.dismissDialog();
				break;
				
			case HanderUtil.case2://数据获取失败
				listView.stopRefresh();
				listView.setRefreshTime();
				((OrderMainActivity) getActivity()).showCustomToast((String) msg.obj, false);
				break;
				
			case HanderUtil.case3:
				mAdapter.notifyDataSetChanged();
//				T.showShort(getActivity(), "");
				break;
				
			default:
				break;
			}
		};
	};
	
	public void onAttach(android.app.Activity activity) {
		super.onAttach(activity);
		getNub = (GetNub) activity;
	};
	
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_all, container, false);
		cartModel = new CartModel();
		baseActivity = new BaseActivity();
		
//		mySql = new MySql(getActivity(), "mement.db", null, 1);//这里的名字是创建的数据库文件的名称
//		//根据数据库辅助类得到数据库。
//		db = mySql.getWritableDatabase();
				
		listView = (MyXListView)view.findViewById(R.id.listview);
		listView = (MyXListView)view.findViewById(R.id.listview);	
		listView.setPullLoadEnable(true);//设置能下拉（这两个属性是上拉加载下拉刷新的）
		listView.setMyXListViewListener(this);//设置接口
		
		Manager_ID = Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANGYUN_UID,"0"));
		Company_ID = MyApplication.preferences.getInt(Content.COMPS_ID, 1016);
		
		mAdapter = new MyAdapter(getActivity(), listView);
		listView.setAdapter(mAdapter);				
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(), DatailsActivity.class);
				//把点中的item的商品id传给详情
				if (position == 0 ) {
					return;
				}else {
					//把点击产品的id传给产品详情页。
					intent.putExtra("id", data.get(position - 1).getID() + "");
												
					//把获取到activity的收藏的数量传递到产品详情
					TextView text =(TextView) getActivity().findViewById(R.id.nub_shoucang);
					intent.putExtra("shoucang_nub", text.getText().toString());					
					startActivity(intent);	
				}				
			}
		});
			
		//onResume方法第一要清空data，加载的时候不用。
		data.clear();
		getData(1);
		return view;
	}
	
	/**
	 * 第一次进来onCreateView和onResume都会走
	 * 滑到第三个再划过来和第一次运行一样
	 * 跳到其他activity再回来则相当于暂停只会运行onResume所以要data.clear();
	 */
	public void onResume() {			
		//dbFindID();
		
		//第一次进来和从其他页面过来的时候获取购物车产品 ID 的集合.
		list_id = new ArrayList<Integer>();//每次进入页面要清除之前加载的选中项，在购物车可能删除的			
		List<CartModel> data3 = new ArrayList<CartModel>();
		data3 = CartManager.getInstance(getActivity()).getCartModels();
		
		for (int i = 0; i < data3.size(); i++) {
			list_id.add(data3.get(i).getProduct_ID());
		}
		L.e("获取到list_id集合：" + list_id.toString());
							
		super.onResume();
	}
	
	/**
	 * 获取产品列表线程
	 * @param json
	 */
	private void getList(int index_start){//int index_start
		//实例化请求类
	    ZJRequest zjRequest = new ZJRequest();
	    
	    //设置你需要的属性	    	    					    
	    zjRequest.setManager_ID(Manager_ID);
	    zjRequest.setCompany_ID(Company_ID);
	    zjRequest.setPageIndex(index_start);
	    zjRequest.setPageSize(10);
	    zjRequest.setKeywords("");
	    final String json = JsonUtil.toJson(zjRequest);
	    L.e("(Fragment_All)jsonString:" + json);
		new Thread(){
		    	public void run() {
		    		try {
						String jsonString = DataUtil.callWebService(Methods.ORDER_GOODS_LIST, json);
						L.e("json:" + jsonString);
						if (jsonString != null) {
							Type type = new TypeToken<ZJResponse<Order_GoodsList>>(){}.getType();
						    ZJResponse<Order_GoodsList> zjResponse = JsonUtil.fromJson(jsonString, type);
						    if (zjResponse.getCode() == 0) {
						    	data2.clear();						    	
							    data2 = zjResponse.getResults();							   
							    myHandler.sendEmptyMessage(HanderUtil.case1);
							    
							    //获取到接口数据就在线程里面添加进数据库，在后面可能会不同步。
							    for (int i = 0; i < data2.size(); i++) {									
									ProductManager.getInstance(getActivity()).saveProductInfo(data2.get(i));
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
	
	/**
	 * 记录是否刚打开程序，用于解决进入程序不滚动屏幕，不会下载图片的问题。
	 * 参考http://blog.csdn.net/guolin_blog/article/details/9526203#comments
	 */
	private boolean isFirstEnter = true;
	
	/**
	 * 一屏中第一个item的位置
	 */
	private int mFirstVisibleItem;
	
	//一屏中所有item的个数
	private ImageDownLoader mImageDownLoader;
	private int mVisibleItemCount;		
	private class MyAdapter extends BaseAdapter{
		
		private LayoutInflater inflater;// 用来导入布局
		private MyXListView listView;

		public MyAdapter(Context context, MyXListView listView) {// 构造器
			this.inflater = LayoutInflater.from(context);
			this.listView = listView;
			mImageDownLoader = new ImageDownLoader(getActivity());	
//			listView.setOnScrollListener(this);
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
			
			holder.tv.setText(data.get(position).getName());
			holder.tv2.setText("￥" + data.get(position).getSalePrice() +
					"/" + data.get(position).getUnit_Name());
//			holder.img.setImageResource(R.drawable.defaultimage02);
//			//将获取到的图片路径通过一个方法转化为图片。			
//			new ImageLoader(getActivity()).DisplayImage(
//					MyApplication.imgPath + data.get(position).getSmallImg(), holder.img, false);
			
			
			//第一次下载		
			String mImageUrl = Common.getPath() + data.get(position).getSmallImg();
			L.i("xiaotu :" + mImageUrl);
			Bitmap bitmap = mImageDownLoader.downloadImage(mImageUrl, new ImageDownLoader.onImageLoaderListener() {
				public void onImageLoader(Bitmap bitmap, String url) {
					if(holder.img != null && bitmap != null){
						
						holder.img.setImageBitmap(bitmap);												
					}else {
						
					}				
				}
			});
			
			//获取Bitmap, 内存中没有就去手机或者sd卡中获取
			Bitmap bitmap2 = mImageDownLoader.showCacheBitmap(mImageUrl.replaceAll("[^\\w]", ""));
			if(bitmap2 != null){				
				holder.img.setImageBitmap(bitmap2);
			}else{			
				holder.img.setImageDrawable(getResources().getDrawable(R.drawable.defaultimage02));
			}
			
			
			//给ImageView设置Tag, 后面showImage的时候要用上
//			String mImageUrl = MyApplication.imgPath + data.get(position).getSmallImg();
//			holder.img.setTag(mImageUrl);
//							
//			//获取Bitmap, 内存中没有就去手机或者sd卡中获取，这一步在getView中会调用，比较关键的一步
//			Bitmap bitmap = mImageDownLoader.showCacheBitmap(mImageUrl.replaceAll("[^\\w]", ""));
//			if(bitmap != null){	
//				L.e("showCacheBitmap    bitmap != null");
//				holder.img.setImageBitmap(bitmap);
//				
//			}else{		
//				L.e("showCacheBitmap    bitmap == null");
//				holder.img.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.defaultimage02));
//			}
			
			
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
						CartManager.getInstance(getActivity()).deleteCart(id_goods);
						//dbDel();
						
						getNub.deletNub();//点击删除
						L.e("（删除后）list_id:" + list_id.toString());
					} else {			
//						Resources res = getResources();
//						Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.ic_launcher); 
						
//						(Bitmap bitmap, int id, String names, Double prices, String unit, int nubs, int class_ID)
//						dbAdd(bmp, data.get(position).getID(), data.get(position).getName(), 
//								data.get(position).getSyn_Price_SalePrice(), data.get(position).getUnit_Name(), 1, 0);
						
						cartModel.setSmallImg(data.get(position).getSmallImg());
						cartModel.setProduct_ID(data.get(position).getID());
						cartModel.setProduct_Name(data.get(position).getName());
						cartModel.setPrice((float)data.get(position).getSalePrice());
						cartModel.setUnit_Name(data.get(position).getUnit_Name());						
						cartModel.setQuantity((float)1);
						
						//调用保存进数据库的方法
						CartManager.getInstance(getActivity()).saveCarts(cartModel);
																							    						
						//已加入的不用走下面
						holder.img2.setImageResource(R.drawable.shopping_car_blue);
						list_id.add(id_goods);
						L.e("（增加后）list_id:" + list_id.toString());
						getNub.addNub();																		
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
		
		/**
		 * 以下是实现图片显示和滑动的关系，但不明白其用意
		 */
//		@Override
//		public void onScrollStateChanged(AbsListView view, int scrollState) {
//			//仅当GridView静止时才去下载图片，GridView滑动时取消所有正在下载的任务  
//			L.e("进入onScrollStateChanged");
//			if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
//				showImage(mFirstVisibleItem, mVisibleItemCount);
//			}else{
//				cancelTask();
//			}
//			
//		}
//
//		/**
//		 * GridView滚动的时候调用的方法，刚开始显示GridView也会调用此方法
//		 */
//		@Override
//		public void onScroll(AbsListView view, int firstVisibleItem,
//				int visibleItemCount, int totalItemCount) {
//			L.e("进入onScroll");
//			mFirstVisibleItem = firstVisibleItem;
//			mVisibleItemCount = visibleItemCount;
//			// 因此在这里为首次进入程序开启下载任务。 
//			if(isFirstEnter && visibleItemCount > 0){
//				showImage(mFirstVisibleItem, mVisibleItemCount);
//				isFirstEnter = false;
//			}
//		}
		
		/**
		 * 显示当前屏幕的图片，先会去查找LruCache，LruCache没有就去sd卡或者手机目录查找，在没有就开启线程去下载
		 * @param firstVisibleItem
		 * @param visibleItemCount
		 */
		private void showImage(int firstVisibleItem, int visibleItemCount){
			Bitmap bitmap = null;
			for(int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++){
				String mImageUrl = Common.getPath() + data.get(i).getSmallImg();
				final ImageView mImageView = (ImageView) listView.findViewWithTag(mImageUrl);
				
				bitmap = mImageDownLoader.downloadImage(mImageUrl, new ImageDownLoader.onImageLoaderListener() {
					public void onImageLoader(Bitmap bitmap, String url) {
						if(mImageView != null && bitmap != null){
							mImageView.setImageBitmap(bitmap);
						}
						
					}
				});
						
				if(bitmap != null){
					mImageView.setImageBitmap(bitmap);
				}else{
//					mImageView.setImageResource(R.drawable.defaultimage02);
//					mImageView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.defaultimage02));
				}
			}
		}
		
		/**
		 * 取消下载任务
		 */
		public void cancelTask(){
			mImageDownLoader.cancelTask();
		}
		
	}
	
	@Override
	public void onDestroyView() {
		L.e("调用了onDestroyView方法！");
		data.clear();
		super.onDestroyView();
	}
	


	/**
	 * 从数据库获取数据通知数据改变的方法
	 * 传入一个页码数
	 */
	private void getData(int pageSize){
		List<Order_GoodsList> data3 = new ArrayList<Order_GoodsList>();
		//从数据库获取的方法
		data3 = ProductManager.getInstance(getActivity()).getGoodsLists(pageSize);
		
		if (data3.size() > 0) {		
			for ( int i = 0; i < data3.size(); i++) {
				data.add(data3.get(i));
				L.i("小图："+ data3.get(i).getSmallImg());
			}				
			mAdapter.notifyDataSetChanged();
			
		}else {
			((OrderMainActivity) getActivity()).showCustomToast("没有更多数据", false);
		}		
	}
	
	
	/**
	 * 实现下拉刷新    
	 */
	public void onRefresh() {
		listView.stopRefresh();
		//获取系统的时间
		listView.setRefreshTime();	
		
		//线程里面就添加进了数据库。
//		int index = 1;
//		if (baseActivity.PingService()) {
//			getList(1);
//			
//		}else {
//			listView.stopRefresh();
//			//获取系统的时间
//			listView.setRefreshTime();	
//		}				
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		L.i("产品界面onStop()");
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		index_start = 1;
		L.i("产品界面onPause()");
	}
	
	
	/**
	 * 上拉加载的方法
	 */
	@Override
	public void onLoadMore() {//上拉加载
		
		listView.stopLoadMore();
		if (flag == true) {
			flag = false;//走线程之前改为false，走完才能再加载
//					isRemind = true;
			index_start = index_start + 1;
			getData(index_start);//上拉加载的时候不需要清除																								
		}
				
				
//				if (data4.size() > 0) {
//					if (flag == true) {
//						flag = false;//走线程之前改为false，走完才能再加载
//						index_start = index_start + 1;
//						getData(index_start);
//						
//						flag = true;																				
//					}
//				}else {					
//					if (flag == true) {
//						flag = false;//走线程之前改为false，走完才能再加载
//						index_start = index_start + 1;
//						getList(index_start);//上拉加载的时候不需要清除
//						
//						flag = true;																				
//					}
//				}				
						
	}
	
	
	
	/**
	 * 添加进数据库的方法
	 * @param bitmap
	 * @param names
	 * @param prices
	 * @param nubs
	 */
//	private void dbAdd(Bitmap bitmap, int id, String names, Double prices, String unit, int nubs, int class_ID) {  		
//		ContentValues values = new ContentValues();
//		
//		values.put("id", id);
//		values.put("img", Common.saveIcon(bitmap));
//		values.put("name", names);
//		values.put("price", prices);
//		values.put("unit", unit);
//		values.put("nub", nubs);//个数添加的时候默认是1，后面要能添加减少
//		values.put("class_ID", class_ID);
//		
//		long rowid = db.insert("shopping_cart", null, values);
//		if(rowid == -1){
//			Toast.makeText(getActivity().getApplicationContext(), "未添加成功！",
//					Toast.LENGTH_LONG).show();
//		}else{
//			Toast.makeText(getActivity().getApplicationContext(), "已成功添加到购物车！",
//					Toast.LENGTH_LONG).show();
//		}
//	}
	
	/**
	 * 查询到加入数据库的商品id，并放入一个集合
	 * 后面加上这些id的商品购物都为选中的颜色。
	 */
//	protected void dbFindID() {
//		list_id = new ArrayList<Integer>();//每次进入页面要清除之前加载的选中项，在购物车可能删除的	
//		
//		Cursor cursor = db.query("shopping_cart", null, null, null, null, null,
//				"_id ASC");
//		cursor.moveToFirst();				
//		while (!cursor.isAfterLast()) {	
//			
//			list_id.add(cursor.getInt(1));
//			cursor.moveToNext();			
//		}
//		L.e("（先查一遍数据库商品的id）list_id:" + list_id.toString());
//		
////		for (int i = 0; i < data.size(); i++) {
////			L.e("id:" + data.get(i).get("map_id").toString());
////			if (list_id.contains(Integer.valueOf(data.get(i).get("map_id").toString()))) {				
////				
////				ImageView oView = (ImageView)listView.getChildAt(i).findViewById(R.id.imgcar);								
////				oView.setImageResource(R.drawable.shopping_car_blue);
////				
////				Toast.makeText(getActivity(), i +"项已被选中", Toast.LENGTH_LONG).show();
////			}else {		
////			}
////		}
//	}
	
	/**
	 * 从数据库删除的方法
	 */
//	protected void dbDel() {
//		// TODO Auto-generated method stub
//		String where = "id = " + id_goods;
//		L.e("where:" + where);
//		int i = db.delete("shopping_cart", where, null);
//		if(i > 0){
//			L.i("数据删除成功！");
//		}else{
//			L.i("数据未删除！");
//		}		
//	}
}
