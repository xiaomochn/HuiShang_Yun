package com.huishangyun.Channel.Orders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.ImageLoad.ImageLoader;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.asyncimageloader.ImageDownLoader;
import com.huishangyun.manager.ProductFavManager;
import com.huishangyun.manager.ProductManager;
import com.huishangyun.model.CartModel;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Methods;
import com.huishangyun.model.Order_GoodsList;
import com.huishangyun.model.ProductFavs;
import com.huishangyun.App.MyApplication;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.manager.CartManager;
import com.huishangyun.model.Content;
import com.huishangyun.yun.R;

public class DatailsActivity extends BaseActivity {
	/**
	 * 数据库操作
	 */
//	private MySql mySql;
//	private SQLiteDatabase db;	
	
	private LinearLayout back;	
	private FrameLayout gouwuche, shoucang;
	private Button bt_shoucang, bt_xiadan, bt_gouwuche;
	private TextView nub_gouwuche, nub_shoucang, nub, name, price, info;
	private ImageView imgadd, imgdel, img_show;
	private Order_GoodsList order_GoodsList = new Order_GoodsList();
//	private WebView webview;
	/**
	 * 添加产品数量的总数
	 */
	private int count = 1;
	/**
	 * 收藏的数量
	 */
	private int shoucang_nub;
	/**
	 * 工厂和产品id
	 */
	private int Manager_ID, Product_ID;
	private String path = Constant.pathurl + MyApplication.getInstance().getCompanyID() + "/Product/";
	private ImageLoader imageLoader;
	/**
	 * 添加进数据库前要查一遍该商品id，有没有添加过
	 */
	private List<Integer> list_id, list_id2;
	private List<Integer> list_nub;	
	private List<CartModel> data;
	private CartModel cartModel;
	private int widths, heights;
	
	private Handler myHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				nub_shoucang.setText((1 + shoucang_nub) + "");
				nub_shoucang.setVisibility(View.VISIBLE);
				bt_shoucang.setText("已收藏");
				bt_shoucang.setTextColor(getResources().getColor(R.color.bt_color));
				bt_shoucang.setBackgroundColor(getResources().getColor(R.color.bt_background));				
				bt_shoucang.setClickable(false);
				
				showCustomToast("收藏成功", true);
				break;
				
			case HanderUtil.case2:
				showCustomToast((String) msg.obj, false);
				bt_shoucang.setEnabled(true);
//				T.showShort(DatailsActivity.this, (CharSequence) msg.obj);
				break;
				
			case HanderUtil.case3:
//				imageLoader = new ImageLoader(DatailsActivity.this);
//				imageLoader.DisplayImage(path + order_GoodsList.getBigImg(), img_show, false);
				name.setText(order_GoodsList.getName());
				price.setText("￥" + order_GoodsList.getSalePrice() + "/" + order_GoodsList.getUnit_Name());
				L.i("-------------------", "order_GoodsList.getInfo():" + order_GoodsList.getInfo());
			//	webview.loadDataWithBaseURL(null, order_GoodsList.getInfo(), "text/html", "utf-8", null);
				info.setText(order_GoodsList.getInfo());
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
		setContentView(R.layout.activity_datails);
		
		//获得手机的宽度和高度
        DisplayMetrics dm = new DisplayMetrics();
 		getWindowManager().getDefaultDisplay().getMetrics(dm);
 		widths = dm.widthPixels;
 		heights = dm.heightPixels;
 		
//		mySql = new MySql(DatailsActivity.this, "mement.db", null, 1);//这里的名字是创建的数据库文件的名称
//		//根据数据库辅助类得到数据库。
//		db = mySql.getWritableDatabase();
		
		Manager_ID = Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0"));
	    Product_ID = Integer.parseInt(getIntent().getStringExtra("id"));
		//id = Integer.parseInt(getIntent().getStringExtra("id"));//跳转时传过来的产品id
	    L.e("订单清单传来的产品id:" + Product_ID);	
	    
		name = (TextView)findViewById(R.id.name);
		price = (TextView)findViewById(R.id.price);
//		webview = (WebView)findViewById(R.id.webview);
		info = (TextView) findViewById(R.id.info);
		img_show = (ImageView)findViewById(R.id.img_shou);
		
		//动态改变图片的控件
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) img_show.getLayoutParams(); 
		linearParams.height = (int) ((widths/8)*5);
		img_show.setLayoutParams(linearParams);
				
		//数据库有就读数据库， 没有联网查
		order_GoodsList = ProductManager.getInstance(this).getGoodsList(Product_ID);
		if (order_GoodsList != null) {
			
			//第一次下载
			ImageDownLoader mImageDownLoader = new ImageDownLoader(DatailsActivity.this);
			String mImageUrl = path + order_GoodsList.getBigImg();
			Bitmap bitmap = mImageDownLoader.downloadImage(mImageUrl, new ImageDownLoader.onImageLoaderListener() {
				public void onImageLoader(Bitmap bitmap, String url) {
					if(img_show != null && bitmap != null){
						
						img_show.setImageBitmap(bitmap);
												
					}				
				}
			});
			
			//获取Bitmap, 内存中没有就去手机或者sd卡中获取
			Bitmap bitmap2 = mImageDownLoader.showCacheBitmap(mImageUrl.replaceAll("[^\\w]", ""));
			if(bitmap2 != null){				
				img_show.setImageBitmap(bitmap2);
			}else{			
				img_show.setImageDrawable(getResources().getDrawable(R.drawable.defaultimage01));
			}
			
			
			name.setText(order_GoodsList.getName());
			L.e("产品详情价格：" + order_GoodsList.getSalePrice());
			price.setText("￥" + order_GoodsList.getSalePrice() + "/" + order_GoodsList.getUnit_Name());

			L.e("产品详情介绍：" + order_GoodsList.getInfo());
			// 载入这个html页面
			//webview.loadDataWithBaseURL(null, order_GoodsList.getInfo(), "text/html", "utf-8", null);
			info.setText(order_GoodsList.getInfo());

		}else {
			getGoodsDatails();
		}
									    
	    init();	
	    initEdt();
	}
	
	@Override
	protected void onResume() {		
		//第一次进来和从其他页面过来的时候获取购物车产品 ID 的集合.
		list_id = new ArrayList<Integer>();//每次进入页面要清除之前加载的选中项，在购物车可能删除的	
		List<CartModel> data3 = CartManager.getInstance(DatailsActivity.this).getCartModels();
		
		for (int i = 0; i < data3.size(); i++) {
			list_id.add(data3.get(i).getProduct_ID());
		}
		L.e("获取到list_id集合：" + list_id.toString());
						
		//上面获取购物车id集合时候读了一遍购物车数据库，这里就不用调公共的在读一遍了。
		nub_gouwuche.setText(data3.size() + "");	
		if (data3.size() > 0) {
			nub_gouwuche.setVisibility(View.VISIBLE);
		}else {
			nub_gouwuche.setVisibility(View.INVISIBLE);
		}

		//获取收藏产品 ID 的集合.（作用是为了判断该商品是否已被收藏了）
		list_id2 = new ArrayList<Integer>();
		List<ProductFavs> data4 = ProductFavManager.getInstance(context).getProductFavs();
		
		for (int i = 0; i < data4.size(); i++) {
			list_id2.add(data4.get(i).getProduct_ID());
		}
		L.e("list_id2:" + list_id2.toString());
		L.e("Product_ID:" + Product_ID);
		if (list_id2.contains(Product_ID)) {
			L.e("进入已收藏");
			bt_shoucang.setText("已收藏");
			bt_shoucang.setTextColor(getResources().getColor(R.color.bt_color));
			bt_shoucang.setBackgroundColor(getResources().getColor(R.color.bt_background));				
			bt_shoucang.setClickable(false);
		}else {
			bt_shoucang.setText("收藏");
			bt_shoucang.setTextColor(getResources().getColor(R.color.h_white));	
			bt_shoucang.setBackgroundResource(R.drawable.title_background);
			bt_shoucang.setClickable(true);
		}
		//上面获取收藏id集合时候读了一遍收藏数据库，这里就不用调公共的在读一遍了。
		nub_shoucang.setText(data4.size() + "");	
		if (data4.size() > 0) {
			nub_shoucang.setVisibility(View.VISIBLE);
		}else {
			nub_shoucang.setVisibility(View.INVISIBLE);
		}
		
		super.onResume();
	}
	
	private void getGoodsDatails(){
				
//		String danjia = price.getText().toString();//￥45.00/箱
//		这里用indexOf("￥")里面放数字会出现越界问题。
//		price_money = danjia.substring(danjia.indexOf("￥")+1, danjia.indexOf("/"));
		
		//实例化请求类
	    ZJRequest zjRequest = new ZJRequest();
	    
	    //设置你需要的属性	    	    					    
	    zjRequest.setID(Product_ID);
	    final String json = JsonUtil.toJson(zjRequest);
	    L.e("(产品详情)jsonString:" + json);
	    
		new Thread(){
			public void run() {
				try {				    
					String jsonString = DataUtil.callWebService(Methods.ORDER_GOODS_DATAILS, json);
					L.e("json:" + jsonString);
					
					if (jsonString != null) {
						Type type = new TypeToken<ZJResponse<Order_GoodsList>>(){}.getType();
					    ZJResponse zjResponse = JsonUtil.fromJson(jsonString, type);
					    
					    Message message = new Message();
					    if (zjResponse.getCode() == 0) {
					    	order_GoodsList = (Order_GoodsList) zjResponse.getResult();
					    	message.what = HanderUtil.case3;
						    myHandler.sendMessage(message);
						    
						} else {								
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
	 * 对话框中的控件
	 */
	private TextView nub_dialog;
	private EditText edt_dialog;
	private void initEdt(){
		nub = (TextView)findViewById(R.id.nub);//添加减少的数量	
		nub.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				LayoutInflater layoutInflater = LayoutInflater.from(DatailsActivity.this);
				View view = layoutInflater.inflate(R.layout.alertdialog_goodsnub, null);
				final AlertDialog dialog = new AlertDialog.Builder(DatailsActivity.this).create();
				dialog.setView(view, 0, 0, 0, 0);					
				
				TextView price = (TextView)view.findViewById(R.id.price);
				price.setText("￥" + order_GoodsList.getSalePrice());
				
				nub_dialog = (TextView)view.findViewById(R.id.nub);
				nub_dialog.setText("x" + nub.getText().toString());
								 
				edt_dialog = (EditText)view.findViewById(R.id.edt);
				edt_dialog.setText(nub.getText().toString());
				edt_dialog.setSelection(edt_dialog.getText().length());//是光标始终在数字后面
				//edt焦点监听，一获取焦点就给dialog弹出软键盘。
				edt_dialog.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					   public void onFocusChange(View v, boolean hasFocus) {
					       if (hasFocus) {
					    	   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					       }
					   }
				});
				
				edt_dialog.addTextChangedListener(new TextWatcher() {					
					public void afterTextChanged(Editable s) {
						nub_dialog.setText("x" + edt_dialog.getText().toString());
						
					}
					
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						// TODO Auto-generated method stub						
					}
					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
						// TODO Auto-generated method stub						
					}					
				});
				
				ImageView del = (ImageView)view.findViewById(R.id.del);
				del.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (edt_dialog.getText().toString().trim().equals("")) {
							edt_dialog.setText("");
						}else {
							int nubs = Integer.valueOf(edt_dialog.getText().toString());
							if (nubs > 1) {
								edt_dialog.setText(nubs - 1 + "");
								
							}else {
								edt_dialog.setText("");
							}
						}
					}
				});
				
				ImageView add = (ImageView)view.findViewById(R.id.add);
				add.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (edt_dialog.getText().toString().trim().equals("")) {
							edt_dialog.setText(1 + "");
						}else {
							int nubs = Integer.valueOf(edt_dialog.getText().toString());
							if (nubs > 0) {
								edt_dialog.setText(nubs + 1 + "");
								
							}else {
								edt_dialog.setText(1 + "");
							}
						}
					}
				});
									
				Button cancel = (Button)view.findViewById(R.id.cancel);
				cancel.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();							
					}
				});
				
				Button confirm = (Button)view.findViewById(R.id.confirm);
				confirm.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {

						if (!edt_dialog.getText().toString().equals("") && 
								!edt_dialog.getText().toString().substring(0, 1).equals("0")) {
							
							nub.setText(edt_dialog.getText().toString());						    								
							dialog.dismiss();
						}														
					}
				});
														
				//点击外面不能取消对话框
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();	
				
			}
		});
	}
	
	
	private void init(){
								
		nub_gouwuche = (TextView)findViewById(R.id.nub_gouwuche);
		
		//获取从activity传给fragment的再传过来的收藏数量
		nub_shoucang = (TextView)findViewById(R.id.nub_shoucang);
		
		//从产品列表和收藏列表都能跳转过来，都需要传递收藏列表的数量
		if (getIntent().getStringExtra("shoucang_nub") != null) {
			shoucang_nub = Integer.parseInt(getIntent().getStringExtra("shoucang_nub"));
			nub_shoucang.setText(shoucang_nub + "");
			if (shoucang_nub > 0) {
				nub_shoucang.setVisibility(View.VISIBLE);
			}
		}
		
		
		back = (LinearLayout)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();				
			}
		});
		
		shoucang = (FrameLayout)findViewById(R.id.shoucang);
		
		shoucang.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(DatailsActivity.this, ShouCangActivity.class);	
				startActivity(intent);			
			}
		});
		gouwuche = (FrameLayout)findViewById(R.id.gouwuche);
		gouwuche.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(DatailsActivity.this, ShopCarActivity.class);	
				startActivity(intent);
			}
		});
				
		bt_shoucang = (Button)findViewById(R.id.bt_shoucang);		
		bt_gouwuche = (Button)findViewById(R.id.bt_guwuche);
		bt_shoucang.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//实例化请求类
			    ZJRequest zjRequest = new ZJRequest(); 
			    //业务员id
			    zjRequest.setManager_ID(Integer.parseInt(preferences.getString(Constant.HUISHANGYUN_UID, "0")));
			    //Company_ID，这个是所有都要的
			    zjRequest.setCompany_ID(preferences.getInt(Content.COMPS_ID, 1016));
			    //产品的id
			    zjRequest.setProduct_ID(Integer.parseInt(getIntent().getStringExtra("id")));
				bt_shoucang.setEnabled(false);
			    //zjRequest.setData(order_ProductFav);
			    //把要传的参数组装成json字符串。
//			    final String json = JsonUtil.toJson(zjRequest);
			    			    			    	  			 
				new Thread(){
					public void run() {
						try {
							//封装进Insert的
							ZJRequest<GetData> zjRequest = new ZJRequest<GetData>();
							GetData mData = new GetData();
							mData.setAction("Insert");
							mData.setManager_ID(Manager_ID);
							mData.setProduct_ID(Product_ID);
							zjRequest.setData(mData);
							String json = JsonUtil.toJson(zjRequest);
						    L.e("(添加收藏)jsonString:" + json);
						    
							String jsonString = DataUtil.callWebService(Methods.ORDER_SHOUCANG_ADD, json);
							L.e("(添加收藏)json:" + jsonString);
							if (jsonString != null) {
								Type type = new TypeToken<ZJResponse<ProductFavs>>(){}.getType();
							    ZJResponse zjResponse = JsonUtil.fromJson(jsonString, type);
							    
							    Message message = new Message();
							    if (zjResponse.getCode() == 0) {
									
							    	ProductFavs proFavs = new ProductFavs();
							    	proFavs.setProduct_ID(order_GoodsList.getID());
							    	proFavs.setProduct_Name(order_GoodsList.getName());
							    	proFavs.setPrice((float)order_GoodsList.getSalePrice());
							    	proFavs.setUnit_Name(order_GoodsList.getUnit_Name());
							    	proFavs.setSmallImg(order_GoodsList.getSmallImg());
							    	
							    	//获取到接口数据就在线程里面添加进数据库，在后面可能会不同步。								   								
									ProductFavManager.getInstance(DatailsActivity.this).saveProducts(proFavs);
								  								    
							    	message.what = HanderUtil.case1;
								    myHandler.sendMessage(message);								    
								} else {								
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
		});

		
		cartModel = new CartModel();
//		cartModel = CartManager.getInstance(DatailsActivity.this).getModelForID(order_GoodsList.getID());
		L.e("cartModel:" + cartModel);
		
		//下订单就是把该商品加入购物车，然后再跳转页面，订单的清单页面就是购物车页面。
		bt_xiadan = (Button)findViewById(R.id.bt_xiadan);
		bt_xiadan.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {	
				
				if (list_id.contains(order_GoodsList.getID())) {					
					//这里还有一个获取到该项产品原来的数量获取不到（问题未解决）。
					//dbUpdate(order_GoodsList.getID(), 2);
					
					//改变数据库产品数量的方法,购物车有的话先获取该实体类	
					cartModel = CartManager.getInstance(DatailsActivity.this).getModelForID(order_GoodsList.getID());
					cartModel.setQuantity((float)Integer.parseInt(nub.getText().toString()));										
					CartManager.getInstance(DatailsActivity.this).saveCarts(cartModel);
				}else {
//					dbAdd(imageLoader.memoryCache.get(path + order_GoodsList.getSmallImg()), order_GoodsList.getID(), order_GoodsList.getName(),
//							order_GoodsList.getSyn_Price_SalePrice(), order_GoodsList.getUnit_Name(), 
//							Integer.parseInt(nub.getText().toString()), order_GoodsList.getClass_ID());
					cartModel.setSmallImg(order_GoodsList.getSmallImg());
					cartModel.setProduct_ID(order_GoodsList.getID());
					cartModel.setProduct_Name(order_GoodsList.getName());
					cartModel.setPrice(order_GoodsList.getSalePrice());
					cartModel.setUnit_Name(order_GoodsList.getUnit_Name());						
					cartModel.setQuantity((float)Integer.parseInt(nub.getText().toString()));				
					//调用保存进数据库的方法
					CartManager.getInstance(DatailsActivity.this).saveCarts(cartModel);
				}
			
				//因为有两个页面都能向订货单传递金额，那边传的是String，所以这边也转为String。
//				String count = String.valueOf(order_GoodsList.getSyn_Price_SalePrice() * Integer.parseInt(nub.getText().toString()));
//				L.e("price_money:" + order_GoodsList.getSyn_Price_SalePrice());
//				L.e("count:" + count);
				
				Intent intent = new Intent(DatailsActivity.this, DingDanActivity.class);
//				intent.putExtra("count", count);
				intent.putExtra("flage", "xiangqing");
				startActivity(intent);
			}
		});
		bt_gouwuche.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				nub_gouwuche.setVisibility(View.VISIBLE);
				bt_gouwuche.setTextColor(getResources().getColor(R.color.bt_color));
				bt_gouwuche.setBackgroundColor(getResources().getColor(R.color.bt_background));
				bt_gouwuche.setClickable(false);
				
				if (list_id.contains(order_GoodsList.getID())) {					
					//这里还有一个获取到该项产品原来的数量获取不到（问题未解决）。
					//dbUpdate(order_GoodsList.getID(), 2);
					
//					nub_gouwuche.setText(nub_gouwuche.getText().toString());//包含和不包含加1或不加1
					
					//改变数据库产品数量的方法,购物车有的话先获取该实体类	
					cartModel = CartManager.getInstance(DatailsActivity.this).getModelForID(order_GoodsList.getID());
					cartModel.setQuantity((float)Integer.parseInt(nub.getText().toString()));
					L.e("getID：" + order_GoodsList.getID());
					L.e("包含时传递的数量：" + Integer.parseInt(nub.getText().toString()));
					
					CartManager.getInstance(DatailsActivity.this).saveCarts(cartModel);
				}else {
//					dbAdd(imageLoader.memoryCache.get(path + order_GoodsList.getSmallImg()), order_GoodsList.getID(), order_GoodsList.getName(),
//							order_GoodsList.getSyn_Price_SalePrice(), order_GoodsList.getUnit_Name(), 
//							Integer.parseInt(nub.getText().toString()), order_GoodsList.getClass_ID());
				
					nub_gouwuche.setText((Integer.valueOf(nub_gouwuche.getText().toString()) + 1) + "");
					
					cartModel.setSmallImg(order_GoodsList.getSmallImg());
					cartModel.setProduct_ID(order_GoodsList.getID());
					cartModel.setProduct_Name(order_GoodsList.getName());
					cartModel.setPrice(order_GoodsList.getSalePrice());
					cartModel.setUnit_Name(order_GoodsList.getUnit_Name());	
					cartModel.setAddDateTime("");
					cartModel.setQuantity((float)Integer.parseInt(nub.getText().toString()));
																						
					L.e("不包含时传递的数量：" + Float.parseFloat(nub.getText().toString()));
					//调用保存进数据库的方法
					CartManager.getInstance(DatailsActivity.this).saveCarts(cartModel);
				}				
			}
		});
		
		imgadd = (ImageView)findViewById(R.id.add);
		imgdel = (ImageView)findViewById(R.id.del);
		imgadd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				count = count + 1;
				nub.setText(String.valueOf(count));
			}
		});
		imgdel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				if (count <= 2) {
					count = 1;
					nub.setText(String.valueOf(count));
				}else {
					count = count - 1;
					nub.setText(String.valueOf(count));
				}				
			}
		});
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
//		values.put("img", Common.saveIcon(bitmap));
//		values.put("id", id);		
//		values.put("name", names);
//		values.put("price", prices);
//		values.put("unit", unit);
//		values.put("nub", nubs);//个数添加的时候默认是1，后面要能添加减少
//		values.put("class_ID", class_ID);
//		
//		long rowid = db.insert("shopping_cart", null, values);
//		if(rowid == -1){
//			Toast.makeText(DatailsActivity.this, "未添加成功！",Toast.LENGTH_LONG).show();
//			
//		}else{
//			//Toast.makeText(DatailsActivity.this, "已成功添加到购物车！",Toast.LENGTH_LONG).show();
//		}						
//	}
	
	/**
	 * 查询到加入数据库的商品id，并放入一个集合
	 * 
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
//	}
	
	/**
	 * 添加进数据库之前，查一遍若有则数量加1
	 * 产品数量变化后更改的方法
	 * 数据库更改的字段要和创建的字段一样
	 * id, img, name, price, unit, nub, class_ID
	 */	 	
//	protected void dbUpdate(int id, int count) {
//		//获取到购物车产品数量的集合
//		query();
//		ContentValues values = new ContentValues();
//		values.put("nub", count);
//		
//		String where = "id = " + id;
//		int i = db.update("shopping_cart", values, where, null);
//		if(i > 0)
//			L.i("myDbDemo：数据更新成功！");
//		else
//			L.i("myDbDemo：数据未更新！");
//	}
	
	/**
	 * 查询的公共方法  Bitmap bitmap, String names, Double prices, int nubs
	 * @param db
	 * @param bitmap
	 * @param name
	 * @param price
	 * @param nub
	 * @return
	 */
//	public void query(){			
//		list_nub = new ArrayList<Integer>();
//		
//		//id, img, name, price, unit, nub, class_ID		
//		Cursor cursor = db.query("shopping_cart", null, null, null, null, null, "_id ASC");
//		cursor.moveToFirst();
//		if (cursor != null && cursor.getCount() != 0) {			
//			while (!cursor.isAfterLast()) {		
////				Map<String, Object> item = new HashMap<String, Object>();
////				item.put("number",  cursor.getInt(6));				
//				L.e("number：" + cursor.getInt(6));
//				list_nub.add(cursor.getInt(6));
//				cursor.moveToNext();
//			}
//		}
//	} 
}
