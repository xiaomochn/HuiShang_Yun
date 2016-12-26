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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.App.MyApplication;
import com.huishangyun.ImageLoad.ImageLoader;
import com.huishangyun.Util.DataUtil;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.JsonUtil;
import com.huishangyun.Util.L;
import com.huishangyun.View.ProgressBar_Loading;
import com.huishangyun.manager.ProductManager;
import com.huishangyun.model.CartModel;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Order_GoodsList;
import com.huishangyun.model.Order_SetOrder;
import com.huishangyun.Util.ZJRequest;
import com.huishangyun.Util.ZJResponse;
import com.huishangyun.model.Methods;
import com.huishangyun.yun.R;



public class GoodsListActivity extends BaseActivity {
	private LinearLayout back;
	private ListView listView;	
	private TextView money, submit, text;

	private List<CartModel> data, data2;
	private MyAdapter myAdapte;
	
	/**
	 * 要删除的数据库某条数据的id
	 */
	private int id_goods;
	private double money_count = 0.0;
	private String goods = "";
	private String results;
	
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				ProgressBar_Loading.dismiss(GoodsListActivity.this);
				data.clear(); 
				for ( int i = 0; i < data2.size(); i++) {
					data.add(data2.get(i));
				}
				initCount();
				myAdapte.notifyDataSetChanged();  				
				//dismissDialog();
				break;
				
			case HanderUtil.case2:
				ProgressBar_Loading.dismiss(GoodsListActivity.this);
				showCustomToast((String) msg.obj, false);
				break;

			case HanderUtil.case3://作为可修改的清单和作为创建退单前的清单两种情况
				ProgressBar_Loading.dismiss(GoodsListActivity.this);
				if (getIntent().getStringExtra("flage").equals("TUI")) {
					showCustomToast("提交成功", true);
//			        T.showShort(DingDanDatailsActivity.this, "提交成功");
					Intent intent2 = new Intent(GoodsListActivity.this, SubmitActivity.class);
					intent2.putExtra("result", results);
					intent2.putExtra("count", money.getText().toString().substring(1, money.getText().toString().length()));
					intent2.putExtra("flage", "TUI");
					L.e("results:" + results);
					L.e("count:" + money.getText().toString().substring(1, money.getText().toString().length()));
					startActivity(intent2);
					
					finish();
				}else {
					Intent intent = new Intent();
					L.e("产品清单传来的总价：" + money.getText().toString());
					intent.putExtra("COUNT", money.getText().toString());
					intent.putExtra("goods", goods);
					intent.putExtra("flage", "qingdan");
					setResult(RESULT_OK, intent);
					finish();
				}
				
				
				break;
				
			case HanderUtil.case4:
//				long nub = CartManager.getInstance(DingDanDatailsActivity.this).deleteAll();
//				L.e("清除购物车返回的nub:" + nub);
//				if (nub > 0) {
//					L.e("数据库删除成功");
//				}else {
//					L.e("数据库删除失败");
//				}
//				showCustomToast("提交成功", true);
////				T.showShort(DingDanDatailsActivity.this, "提交成功");
//				Intent intent2 = new Intent(DingDanDatailsActivity.this, SubmitActivity.class);
//				intent2.putExtra("result", results);
//				intent2.putExtra("count", count.getText().toString().substring(1, count.getText().toString().length()));
//				intent2.putExtra("flage", "TUI");
//				startActivity(intent2);
//				
//				finish();
				break;
				
			case HanderUtil.case5:
				ProgressBar_Loading.dismiss(GoodsListActivity.this);
				showCustomToast("提交成功", true);
				Intent intent = new Intent(GoodsListActivity.this, SubmitActivity.class);
				intent.putExtra("result", results);
				intent.putExtra("count", money.getText().toString());
				intent.putExtra("flage", "TUI");
				startActivity(intent);
				
				finish();
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
		setContentView(R.layout.activity_shoping_car);
		
		text = (TextView)findViewById(R.id.text);
		if (getIntent().getStringExtra("flage").equals("DING") || 
				getIntent().getStringExtra("flage").equals("TUI")) {
			text.setText("产品清单");//看要不要做修改
		}else {
			
		}		
		init();
	}
	
	/**
	 * 一些控件的点击方法
	 */
	private void init(){
		back = (LinearLayout)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		money = (TextView)findViewById(R.id.money);
		
		listView = (ListView)findViewById(R.id.listview);
		myAdapte = new MyAdapter(this);
		data =new ArrayList<CartModel>();
		data2 =new ArrayList<CartModel>();				
		listView.setAdapter(myAdapte);
		
		//传入订单id读取该订单的清单列表
		L.e("该订单的产品清单：" + getIntent().getStringExtra("order_id"));
		ProgressBar_Loading.showProgressBar(GoodsListActivity.this, true, "Loading....");
		getGoodsLists(getIntent().getStringExtra("order_id"));	
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(GoodsListActivity.this, DatailsActivity.class);
				//把点击产品的id传给产品详情页。
				intent.putExtra("id", data.get(arg2).getProduct_ID() + "");
				L.e("订单清单的产品id:" + data.get(arg2).getProduct_ID());							
				//把获取到activity的收藏的数量传递到产品详情
//				TextView text =(TextView) getActivity().findViewById(R.id.nub_shoucang);
//				intent.putExtra("shoucang_nub", text.getText().toString());					
				startActivity(intent);	

			}
		});		
				
		submit = (TextView)findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				if (data.size() > 0) {
					for (int i = 0; i < data.size(); i++) {
						goods = goods + data.get(i).getProduct_ID() + "," + data.get(i).getUnit_ID() + "," + 
								data.get(i).getPrice() + "," + data.get(i).getQuantity() + "#";
						
						L.e("getProduct_ID：" + data.get(i).getProduct_ID());
						L.e("getUnit_ID：" + data.get(i).getUnit_ID());
						L.e("getAmount：" + data.get(i).getPrice());
						L.e("getQuantity：" + data.get(i).getQuantity());
					}
					goods = goods.substring(0, goods.length() - 1);
					
					L.e("清单界面提交时的字符串：" + goods);
//					Intent intent = new Intent();
//					intent.putExtra("goods", goods);
//					intent.putExtra("COUNT", money.getText().toString());
//					setResult(RESULT_OK, intent);
//					finish();
					
					//判断是订货单的清单还有从定货单创建退单的清单。
					if (getIntent().getStringExtra("flage").equals("DING")) {
						ProgressBar_Loading.showProgressBar(GoodsListActivity.this, true, "Loading....");
						submitOrder(goods);//修改订货单
					}else if(getIntent().getStringExtra("flage").equals("TUI")){
						ProgressBar_Loading.showProgressBar(GoodsListActivity.this, true, "Loading....");
						setTuiOrder(goods);//由订货单创建退货单
					}
					
										
				}else {
					showCustomToast("清单不能为空", false);
				}
										
			}
		});
	}
	
	/**
	 * 获取订单产品列表
	 */
	private void getGoodsLists(String orderID){
		//封装进Insert的
		ZJRequest zjRequest = new ZJRequest();			
		zjRequest.setOrderID(orderID);
		
	    final String json = JsonUtil.toJson(zjRequest);		
		L.e("(获取订单产品列表)json:" + json);
	    
		new Thread(){
			public void run() {
				try {			
					String jsonString = DataUtil.callWebService(Methods.ORDER_DINGDAN_GOODS, json);
					L.e("(获取订单产品列表)jsonString:" + jsonString);
					
					if (jsonString != null) {						
						Type type = new TypeToken<ZJResponse<CartModel>>(){}.getType();
					    ZJResponse zjResponse = JsonUtil.fromJson(jsonString, type);
					    
					    Message message = new Message();
					    if (zjResponse.getCode() == 0) {
					    	data2.clear();
					    	data2 = zjResponse.getResults();					    	
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
	
	
	/**
	 * 自定义的BaseAdapter类
	 * @author Administrator
	 *
	 */
	private class MyAdapter extends BaseAdapter{
		
		public LayoutInflater inflater;// 用来导入布局		
		
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

		private TextView nub;
		private EditText edt;
		public View getView(final int position, View view, ViewGroup parent) {
			
			final ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.list_gouwuche, null);
				holder.img = (ImageView) view.findViewById(R.id.img);
				holder.tv = (TextView) view.findViewById(R.id.name);
				holder.tv2 = (TextView) view.findViewById(R.id.price);
				holder.edt = (TextView) view.findViewById(R.id.nub);
				holder.img2 = (ImageView) view.findViewById(R.id.delet);
				holder.add = (ImageView) view.findViewById(R.id.add);
				holder.del = (ImageView) view.findViewById(R.id.del);
								
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
			int Product_ID = data.get(position).getProduct_ID();
			L.e("Product_ID:" + Product_ID);
			if (Product_ID > 0) {
				Order_GoodsList order_GoodsList =
						ProductManager.getInstance(GoodsListActivity.this).getGoodsList(Product_ID);
				
				L.e("order_GoodsList:" + order_GoodsList);
				if(order_GoodsList != null){
					new ImageLoader(GoodsListActivity.this).DisplayImage(
							Common.getPath() + order_GoodsList.getSmallImg(), holder.img, false);
				}
												
				holder.tv.setText(data.get(position).getProduct_Name());
				holder.tv2.setText("￥" + data.get(position).getPrice() + "/" + data.get(position).getUnit_Name());			
				
				//把传过来的flort类型数量转换为没有小数点
				String goods_nub = data.get(position).getQuantity().toString();
				holder.edt.setText(goods_nub.substring(0, goods_nub.indexOf(".")));	
			}
																	
			//这里更改数据库的方法不是指定某条数据的id，而是获取这个实体类，更改某一项。	
			final CartModel cartModel = data.get(position);
			holder.add.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					int nub_count1 = Integer.parseInt(holder.edt.getText().toString());				
					nub_count1 = nub_count1 + 1;
					holder.edt.setText(nub_count1 + "");
					data.get(position).setQuantity((float)nub_count1);
					//点击一次增加，加一次这个item的单价	
					money_count = money_count + data.get(position).getPrice();						
					money.setText(String.valueOf(money_count));
										
				}
			});
			
			holder.del.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {	
					
					int nub_count2 = Integer.parseInt(holder.edt.getText().toString());					
					if (nub_count2 - 1 < 1) {//小于1，赋值1总价不变
						nub_count2 = 1;
						holder.edt.setText(nub_count2 + "");
						
					}else {//大于或等于1						
						nub_count2 = nub_count2 - 1;
						holder.edt.setText(nub_count2 + "");
						//点击一次减少，减一次这个item的单价
						money_count = money_count - data.get(position).getPrice();			
						money.setText(String.valueOf(money_count));

					}
					data.get(position).setQuantity((float)nub_count2);
				}
			});
			
			holder.img2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
										
					AlertDialog.Builder builder = new Builder(GoodsListActivity.this);
					builder.setMessage("确认删除吗？");					 
					builder.setTitle("提示");
					builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int which) {
				 		
					    L.e("删除前data:" +data.toString());
					    data.remove(position);
					    L.e("删除后data:" +data.toString());
					    myAdapte.notifyDataSetChanged();
					    //这里获取总数和总价和删除按钮那的一样
						initCount();
					    }					 
					});					  
					builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					   public void onClick(DialogInterface dialog, int which) {					 

					   dialog.dismiss();					 
					   }					 
					});					  
					builder.create().show();					 										
				}
			});
			
			
			holder.edt.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					LayoutInflater layoutInflater = LayoutInflater.from(GoodsListActivity.this);
					View view = layoutInflater.inflate(R.layout.alertdialog_goodsnub, null);
					final AlertDialog dialog = new AlertDialog.Builder(GoodsListActivity.this).create();
					dialog.setView(view, 0, 0, 0, 0);					
					
					TextView price = (TextView)view.findViewById(R.id.price);
					price.setText("￥" + data.get(position).getPrice());
					
					nub = (TextView)view.findViewById(R.id.nub);
					nub.setText("x" + holder.edt.getText().toString());
										
					edt = (EditText)view.findViewById(R.id.edt);
					edt.setText(holder.edt.getText().toString());
					edt.setSelection(edt.getText().length());//是光标始终在数字后面
					//edt焦点监听，一获取焦点就给dialog弹出软键盘。
					edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
						   public void onFocusChange(View v, boolean hasFocus) {
						       if (hasFocus) {
						    	   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
						       }
						   }
					});
					edt.addTextChangedListener(new GoodsNub());
					
					ImageView del = (ImageView)view.findViewById(R.id.del);
					del.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							if (edt.getText().toString().trim().equals("")) {
								edt.setText("");
							}else {
								int nubs = Integer.valueOf(edt.getText().toString());
								if (nubs > 1) {
									edt.setText(nubs - 1 + "");
									
								}else {
									edt.setText("");
								}
							}
						}
					});
					
					ImageView add = (ImageView)view.findViewById(R.id.add);
					add.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							if (edt.getText().toString().trim().equals("")) {
								edt.setText(1 + "");
							}else {
								int nubs = Integer.valueOf(edt.getText().toString());
								if (nubs > 0) {
									edt.setText(nubs + 1 + "");
									
								}else {
									edt.setText(1 + "");
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
							
							if (!edt.getText().toString().equals("") && 
									!edt.getText().toString().substring(0, 1).equals("0")) {
								
								holder.edt.setText(edt.getText().toString());
								
								//计算总价的方法是根据data数据来的，这里填写数量后也更改data的数量
								data.get(position).setQuantity(Float.parseFloat(edt.getText().toString()));
								initCount();
							    								
								dialog.dismiss();
							}														
						}
					});
															
					//点击外面不能取消对话框
					dialog.setCanceledOnTouchOutside(false);
					dialog.show();									
				}
			});		
						
			return view;
		}
		
		class GoodsNub implements TextWatcher{
			public void afterTextChanged(Editable s) {
				nub.setText("x" + edt.getText().toString());
				
//				if (edt.getText().toString().trim().equals("")) {
//					nub.setText("x1");
//				}else {
//					nub.setText("x" + edt.getText().toString());
//				}								
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {							
			}
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {							
			}							
		}
		
		public class ViewHolder {// 写这个类是为了点击list的时候好调用view
			public ImageView img;
			public TextView tv;
			public TextView tv2;
			public TextView edt;
			public ImageView add;
			public ImageView del;
			public ImageView img2;
		}	
						
	}
	
	/**
	 * 把获取产品总数和总价的方法集成在一起，以便几处的调用
	 */
	private void initCount(){	    
	    money_count = 0;
	    for (int i = 0; i < data.size(); i++) {	
	    	L.e("测试getPrice:" + data.get(i).getPrice());
	    	L.e("测试getQuantity:" + data.get(i).getQuantity());
	    	L.e("测试money_count:" + data.get(i).getPrice() * data.get(i).getQuantity());
			money_count = money_count + data.get(i).getPrice() * data.get(i).getQuantity();
					
		}
	    money.setText(String.valueOf(money_count));
	}
	
	
	/**
	 * 修改订单
	 */
	private void submitOrder(String goods){
		int Manager_ID = Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANGYUN_UID,"0"));//业务员编号
		Intent intent = getIntent();
		//封装进Insert的
		ZJRequest<Order_SetOrder> zjRequest = new ZJRequest<Order_SetOrder>();
		Order_SetOrder order_SetOrder = new Order_SetOrder();
					
		order_SetOrder.setAction("Update");
		order_SetOrder.setOrderID(intent.getStringExtra("order_id"));
		
		if (MyApplication.preferences.getInt("STATE", 0) == -1){
			order_SetOrder.setType(-1);//订单类型
		}else if(MyApplication.preferences.getInt("STATE", 0) == 1){
			order_SetOrder.setType(1);//订单类型
		}		
		order_SetOrder.setManager_ID(Manager_ID);//业务员编号
		order_SetOrder.setManager_Name(MyApplication.getInstance().getSharedPreferences().
				getString(Constant.XMPP_MY_REAlNAME, ""));
		
		L.e("复制时要传递的Member_ID：" + Integer.parseInt(intent.getStringExtra("Member_ID")));
		
		
		order_SetOrder.setMember_ID(Integer.parseInt(intent.getStringExtra("Member_ID")));//客户id
		order_SetOrder.setMember_Name(intent.getStringExtra("Member_Name"));//客户名称
		order_SetOrder.setReceiveName(intent.getStringExtra("ReceiveName"));//收货人姓名
		order_SetOrder.setReceiveTel(intent.getStringExtra("Mobile"));
		order_SetOrder.setReceiveMobile(intent.getStringExtra("Mobile"));
		
		order_SetOrder.setReceiveAddress(intent.getStringExtra("Address"));		
		order_SetOrder.setReceiveDate(intent.getStringExtra("Date").substring(0,10));//期望到货时间
		order_SetOrder.setNote(intent.getStringExtra("Note"));//备注
		order_SetOrder.setPrice(Float.parseFloat(money.getText().toString()));
		order_SetOrder.setArray_ID(goods);//产品清单的数据组成的字符串
		
		zjRequest.setData(order_SetOrder);		
		final String json = JsonUtil.toJson(zjRequest);
	    L.e("(修改订单)json:" + json);
	    
		new Thread(){
			public void run() {
				try {			
					String jsonString = DataUtil.callWebService(Methods.ORDER_DINGDAN_SET, json);
					L.e("(修改订单)jsonString:" + jsonString);
					
					if (jsonString != null) {						
						Type type = new TypeToken<ZJResponse<String>>(){}.getType();
						ZJResponse<String> zjResponse = JsonUtil.fromJson(jsonString, type);
					    
					    Message message = new Message();
					    if (zjResponse.getCode() == 0) {
					    	results = zjResponse.getResult();						    	
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
	 * 创建退货单
	 */
	private void setTuiOrder(String goods){
		//封装进Insert的
		ZJRequest<Order_SetOrder> zjRequest = new ZJRequest<Order_SetOrder>();
		Order_SetOrder order_SetOrder = new Order_SetOrder();
		
		order_SetOrder.setType(-1);//订单类型
		int Manager_ID = Integer.parseInt(MyApplication.preferences.getString(Constant.HUISHANGYUN_UID,"0"));//业务员编号
		order_SetOrder.setManager_ID(Manager_ID);//业务员编号
		order_SetOrder.setManager_Name(MyApplication.getInstance().getSharedPreferences().
				getString(Constant.XMPP_MY_REAlNAME, ""));
		
		Intent intent = getIntent();
		order_SetOrder.setMember_ID(Integer.parseInt(intent.getStringExtra("Member_ID")));//客户id
		order_SetOrder.setMember_Name(intent.getStringExtra("Member_Name"));//客户名称
		order_SetOrder.setReceiveName(intent.getStringExtra("ReceiveName"));//收货人姓名
		order_SetOrder.setReceiveTel(intent.getStringExtra("Mobile"));
		order_SetOrder.setReceiveMobile(intent.getStringExtra("Mobile"));
		
		order_SetOrder.setReceiveAddress(intent.getStringExtra("Address"));		
		order_SetOrder.setReceiveDate(intent.getStringExtra("Date").substring(0,10));//期望到货时间
		order_SetOrder.setNote(intent.getStringExtra("Note"));//备注
		order_SetOrder.setPrice(Float.parseFloat(money.getText().toString()));
		order_SetOrder.setArray_ID(goods);//产品清单的数据组成的字符串
				
		zjRequest.setData(order_SetOrder);		
		final String json = JsonUtil.toJson(zjRequest);
	    L.e("(创建退货单)json:" + json);
	    
		new Thread(){
			public void run() {
				try {			
					String jsonString = DataUtil.callWebService(Methods.ORDER_DINGDAN_SET, json);
					L.e("(创建退货单)jsonString:" + jsonString);
					
					if (jsonString != null) {						
						Type type = new TypeToken<ZJResponse<String>>(){}.getType();
					    ZJResponse<String> zjResponse = JsonUtil.fromJson(jsonString, type);
					    
					    Message message = new Message();
					    if (zjResponse.getCode() == 0) {
					    	results = zjResponse.getResult();				    	
					    	message.what = HanderUtil.case5;
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
	
}
