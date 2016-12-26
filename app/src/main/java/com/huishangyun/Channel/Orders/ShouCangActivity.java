package com.huishangyun.Channel.Orders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.ImageLoad.ImageLoader;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.manager.CartManager;
import com.huishangyun.manager.ProductFavManager;
import com.huishangyun.model.CartModel;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.model.Methods;
import com.huishangyun.model.Order_ProductFav;
import com.huishangyun.model.ProductFavs;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.yun.R;

public class ShouCangActivity extends BaseActivity {
	/**
	 * 数据库操作
	 */
//	private MySql mySql;
//	private SQLiteDatabase db;
	/**
	 * 要删除的数据库某条数据的id
	 */
	private int id_goods;
	/**
	 * 用来存放点击过的list子项的集合
	 */
	private List<Integer> list_id;
	private int count;

	private ListView listView;
	private LinearLayout back;
	private FrameLayout gouwuche;
	private TextView tv_car;
	private ImageView img;
	
	private List<ProductFavs> list_data = new ArrayList<ProductFavs>();
	private List<ProductFavs> list_data2 = new ArrayList<ProductFavs>();
	
	private myAdapter mAdapter;
	private boolean isClick = true;	
	private int delID;
	
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
//				listView.stopRefresh();
//				listView.setRefreshTime();
				
				list_data.clear(); 
				for ( int i = 0; i < list_data2.size(); i++) {
					list_data.add(list_data2.get(i));
				}
				mAdapter.notifyDataSetChanged();  
				
				//dismissDialog();
				break;
				
			case HanderUtil.case2:
				showCustomToast((String) msg.obj, false);
				break;
				
			case HanderUtil.case3:
//				list_data.clear(); 
//				for ( int i = 0; i < list_data2.size(); i++) {
//					list_data.add(list_data2.get(i));
//				}				
				//mAdapter.notifyDataSetChanged();
				
				showCustomToast("删除成功", true);
				//从数据库删除
			    ProductFavManager.getInstance(ShouCangActivity.this).deleteProductFav(delID);
			    showList();
			    
				//getList();
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
		setContentView(R.layout.activity_shoucang);		
		
//		mySql = new MySql(this, "mement.db", null, 1);//这里的名字是创建的数据库文件的名称
//		//根据数据库辅助类得到数据库。
//		db = mySql.getWritableDatabase();
		
		tv_car = (TextView)findViewById(R.id.nub_gouwuche);
		img = (ImageView)findViewById(R.id.no_img);
		
		back = (LinearLayout)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		gouwuche = (FrameLayout)findViewById(R.id.gouwuche);
		gouwuche.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ShouCangActivity.this, ShopCarActivity.class);	
				startActivity(intent);
			}
		});
		
		listView = (ListView)findViewById(R.id.listview);			
//		listView.setPullLoadEnable(true);//设置能下拉（这两个属性是上拉加载下拉刷新的）
//		listView.setMyXListViewListener(this);//设置接口
		
		mAdapter = new myAdapter(this);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				TextView tv = (TextView) view.findViewById(R.id.name);
//			    tv.setFocusable(false);
//			    tv.setFocusableInTouchMode(false);
				
				Intent intent = new Intent(ShouCangActivity.this, DatailsActivity.class);	
				intent.putExtra("shoucang_nub", list_data.size() + "");		
				intent.putExtra("id", list_data.get(position).getProduct_ID() + "");
				
				startActivity(intent);
				
//				if (position == 0) {
//					return;
//					
//				}else{
//					Intent intent = new Intent(ShouCangActivity.this, DatailsActivity.class);	
//					intent.putExtra("shoucang_nub", list_data.size() + "");				
//					intent.putExtra("id", list_data.get(position-1).getProduct_ID() + "");
//					
//					startActivity(intent);
//				}							
			}
		});		
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, final View view,
					final int position, long id) {
				//实例化请求类
			    ZJRequest zjRequest = new ZJRequest();
			    //账户id：Manager_ID
			    zjRequest.setManager_ID(Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0")));
			    //点击列表的id
			    delID = list_data.get(position).getProduct_ID();
			    
			    zjRequest.setID(delID);
			    final String json = JsonUtil.toJson(zjRequest);
			    L.e("（删除）jsonString:" + json);
			    
			    AlertDialog.Builder builder = new Builder(ShouCangActivity.this);
				builder.setMessage("确认删除吗？");					 
				builder.setTitle("提示");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int which) {
			 						    
				    //从服务器接口删除
//				    ImageView img = (ImageView) view.findViewById(R.id.imgcar);
//				    img.setFocusable(false);				   				    
				    delList(json);	
				    				    
				    }					 
				});					  
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int which) {					 

				   dialog.dismiss();					 
				   }					 
				});					  
				builder.create().show();
			    
				return true;
			}
		});
		listView.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				 //当此选中的item的子控件需要获得焦点时  
		        parent.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);  
			}

			public void onNothingSelected(AdapterView<?> parent) {
				parent.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS); 
			}
		});
											
	}
	
	/**
	 * 读取数据库显示列表的方法，显示和删除的时候要调用
	 */
	private void showList(){
		list_data.clear();
		list_data2.clear();
		list_data2 = ProductFavManager.getInstance(ShouCangActivity.this).getProductFavs();
		
		if (list_data2.size() == 0) {
			img.setVisibility(View.VISIBLE);
//			getList();
			
		}else {
			for ( int i = 0; i < list_data2.size(); i++) {
				list_data.add(list_data2.get(i));
			}			 
		}
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onResume() {
//		dbFindID();
		//第一次进来和从其他页面过来的时候获取购物车产品 ID 的集合.
		list_id = new ArrayList<Integer>();//每次进入页面要清除之前加载的选中项，在购物车可能删除的			
		List<CartModel> data3 = new ArrayList<CartModel>();
		data3 = CartManager.getInstance(ShouCangActivity.this).getCartModels();
		for (int i = 0; i < data3.size(); i++) {
			list_id.add(data3.get(i).getProduct_ID());
		}
		L.e("获取到list_id集合：" + list_id.toString());
				
		//公共的从数据库获取购物车数量
		Common.getCarCounts(ShouCangActivity.this, tv_car);
		
		//第一次进来先读取数据库的数据。
		showList();
		
		super.onResume();
	}
	
	/**
	 * 获取收藏列表线程
	 * @param json
	 */
	private void getList(){
		//实例化请求类
	    ZJRequest zjRequest = new ZJRequest(); 
	    //账户id：Manager_ID
	    zjRequest.setManager_ID(Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0")));
	    //Company_ID
	    zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 1016));
	    zjRequest.setPageIndex(1);
	    zjRequest.setPageSize(100);
	    zjRequest.setKeywords("");
	    final String json = JsonUtil.toJson(zjRequest);
	    L.e("（收藏）json:" + json);
	    
	    //showDialog("正在加载...");
	    
		new Thread(){
	    	public void run() {
	    		try {
					String jsonString = DataUtil.callWebService(Methods.ORDER_SHOUCANG_LIST, json);
					L.e("（收藏）jsonString:" + jsonString);
					if (jsonString != null) {
						Type type = new TypeToken<ZJResponse<ProductFavs>>(){}.getType();
					    ZJResponse zjResponse = JsonUtil.fromJson(jsonString, type);
					    if (zjResponse.getCode() == 0) {
					    	
					    	list_data2.clear();//因为第一次加载和删除都要调用，所以这里先clear一下。
						    list_data2 = zjResponse.getResults();
						    
						    //获取到接口数据就在线程里面添加进数据库，在后面可能会不同步。
						    for (int i = 0; i < list_data2.size(); i++) {								
								ProductFavManager.getInstance(ShouCangActivity.this).saveProducts(list_data2.get(i));
						    }							
							
						    myHandler.sendEmptyMessage(HanderUtil.case1);
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
	 * 删除收藏线程
	 * @param json
	 */
	private void delList(final String json){
		 new Thread(){
		    	public void run() {
		    		try {
						String jsonString = DataUtil.callWebService(Methods.ORDER_SHOUCANG_DEL, json);
						L.e("(删除)json:" + jsonString);
						if (jsonString != null) {
							Type type = new TypeToken<ZJResponse<Order_ProductFav>>(){}.getType();
						    ZJResponse<Order_ProductFav> zjResponse = JsonUtil.fromJson(jsonString, type);
						    if (zjResponse.getCode() == 0) {
							    myHandler.sendEmptyMessage(HanderUtil.case3);
							    
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
	
	private class myAdapter extends BaseAdapter{
		
		private LayoutInflater inflater;// 用来导入布局

		public myAdapter(Context context) {// 构造器
			this.inflater = LayoutInflater.from(context);
		}
		
		public int getCount() {
			return list_data.size();
		}

		@Override
		public Object getItem(int position) {
			return list_data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		/**
		 *此方法返回false使item不可点击 
		 */
//		public boolean isEnabled(int position) {
//			// TODO Auto-generated method stub
//			return false;
//		}

		public View getView(final int position, View view, ViewGroup parent) {
			
			final ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.list_all, null);
				holder.rel = (RelativeLayout) view.findViewById(R.id.rel);
				holder.img = (ImageView) view.findViewById(R.id.img);
				holder.tv = (TextView) view.findViewById(R.id.name);
				holder.tv2 = (TextView) view.findViewById(R.id.price);
				holder.img2 = (ImageView) view.findViewById(R.id.imgcar);
									
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
						
			holder.tv.setText(list_data.get(position).getProduct_Name());
			holder.tv2.setText("￥" + list_data.get(position).getPrice() + 
					"/" + list_data.get(position).getUnit_Name());
			
			//将获取到的图片路径通过一个方法转化为图片。			
			new ImageLoader(ShouCangActivity.this).DisplayImage(
					Common.getPath() + list_data.get(position).getSmallImg(), holder.img, false);
			
			if (list_id.contains(new Integer(list_data.get(position).getProduct_ID()))) {
				holder.img2.setImageResource(R.drawable.shopping_car_blue);
				
			}else {
				holder.img2.setImageResource(R.drawable.shopping_car_grey);
			}
									
			holder.img2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
//					holder.rel.setFocusable(false);
//					holder.rel.setFocusableInTouchMode(false);
					
					id_goods = list_data.get(position).getProduct_ID();
					if (list_id.contains(id_goods)) {  //如果选中就取消
						holder.img2.setImageResource(R.drawable.shopping_car_grey);
						list_id.remove(new Integer(id_goods));//删除list集合里面的数据
						Common.deletNub(tv_car);
						
						//dbDel();//选中的再点击就删除
						//删除数据库的这条数据。
						CartManager.getInstance(ShouCangActivity.this).deleteCart(id_goods);
						L.e("（删除后）list_id:" + list_id.toString());
					} else {
						holder.img2.setImageResource(R.drawable.shopping_car_blue);
						list_id.add(new Integer(id_goods));
						Common.addNub(tv_car);	
						L.e("（增加后）list_id:" + list_id.toString());
						
//						Resources res = getResources();
//						Bitmap bmp = BitmapFactory.decodeResource(res, imgs[position]); 						
						//(Bitmap bitmap, int id, String names, Double prices, String unit, int nubs, int class_ID)
//						dbAdd(bmp, list_data.get(position).getProduct_ID(), list_data.get(position).getProduct_Name(), 
//								prices[position], list_data.get(position).getUnits(), 1, 0);
						
						CartModel cartModel = new CartModel();
						cartModel.setSmallImg(list_data.get(position).getSmallImg());
						cartModel.setProduct_ID(list_data.get(position).getProduct_ID());
						cartModel.setProduct_Name(list_data.get(position).getProduct_Name());
						cartModel.setPrice((float)list_data.get(position).getPrice());
						cartModel.setUnit_Name(list_data.get(position).getUnit_Name());						
						cartModel.setQuantity((float)1);
												
						//调用保存进数据库的方法
						CartManager.getInstance(ShouCangActivity.this).saveCarts(cartModel);
					}
				}
			});
			
			return view;
		}
		
		public final class ViewHolder {// 写这个类是为了点击list的时候好调用view
			public RelativeLayout rel;
			public ImageView img;
			public TextView tv;
			public TextView tv2;
			public ImageView img2;
		}
	}

	
//	@Override
//	public void onRefresh() {
//		new Handler().postDelayed(new Runnable() {
//			public void run() {
//				
//				getList();
//			}
//		}, 2000);
//	}
//
//	@Override
//	public void onLoadMore() {
//		new Handler().postDelayed(new Runnable() {
//			public void run() {
//				listView.stopLoadMore();
//			}
//		}, 2000);
//	}	
	
	
	/**
	 * 获取到书库中已有的商品的id
	 */
//	protected void dbFindID() {
//		list_id = new ArrayList<Integer>();//每次进入页面要清除之前加载的选中项，在购物车可能删除的	
//		
//		Cursor cursor = db.query("shopping_cart", null, null, null, null, null,
//				"_id ASC");
//
//		cursor.moveToFirst();				
//		while (!cursor.isAfterLast()) {	
//			
//			list_id.add(cursor.getInt(1));
//			cursor.moveToNext();			
//		}
//		L.e("（先查一遍数据库商品的id）list_id:" + list_id.toString());		
//	}
	
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
//			Toast.makeText(ShouCangActivity.this, "未添加成功！",
//					Toast.LENGTH_LONG).show();
//		}else{
//			Toast.makeText(ShouCangActivity.this, "已成功添加到购物车！",
//					Toast.LENGTH_LONG).show();
//		}
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
