package com.huishangyun.Channel.Orders;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.huishangyun.ImageLoad.ImageLoader;
import com.huishangyun.Util.L;
import com.huishangyun.manager.CartManager;
import com.huishangyun.model.CartModel;
import com.huishangyun.yun.R;

public class Shoping_CarActivity extends Activity{
	
	/**
	 * 获取购物车数据库的数据
	 */
//	private MySql mySql;
//	private SQLiteDatabase db;
	/**
	 * 要删除的数据库某条数据的id
	 */
	private int id_goods;
	private List<Integer> id_list = new ArrayList<Integer>();
	
	private LinearLayout back;
	private ListView listView;	
	private TextView money, submit, text;
	
	private List<CartModel> data, data2;
	/**
	 * 总的金额
	 */
	private float money_count = 0;
	/**
	 * 为下面总的产品数定义的变量
	 */
//	private CartModel cartModel;
	private MyAdapter myAdapte;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_shoping_car);//产品清单的布局和购物车的一样，这里复用。
		
//		mySql = new MySql(this, "mement.db", null, 1);
//		db = mySql.getWritableDatabase();
		if (getIntent().getStringExtra("flage") != null) {
			text = (TextView) findViewById(R.id.text);
			text.setText("产品清单");
		}		
		listView = (ListView)findViewById(R.id.listview);
		myAdapte = new MyAdapter(this);	
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(Shoping_CarActivity.this, DatailsActivity.class);					
				//把点击产品的id传给产品详情页。
				intent.putExtra("id", data.get(position).getProduct_ID() + "");	
				L.e("购物车传给详情的ID：" + data.get(position).getProduct_ID());
				L.e("购物车传给详情的名称：" + data.get(position).getProduct_Name());
				startActivity(intent);	
			}
		});
		
		//query();
		//调用查询数据库的方法	
		data =new ArrayList<CartModel>();
		data2 =new ArrayList<CartModel>();
		data2 = CartManager.getInstance(Shoping_CarActivity.this).getCartModels();
		for (int i = 0; i < data2.size(); i++) {
			data.add(data2.get(i));
		}
		listView.setAdapter(myAdapte);
		
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
												
		//count = (TextView)findViewById(R.id.count);				
		money = (TextView)findViewById(R.id.money);
		//一进activity获取产品总数金额总数
		initCount();
		
		submit = (TextView)findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//更改
				for (int i = 0; i < data.size(); i++) {								
					CartManager.getInstance(Shoping_CarActivity.this).saveCarts(data.get(i));
				}
				//删除数据库的这条数据。
				for (int j = 0; j < id_list.size(); j++) {
					CartManager.getInstance(Shoping_CarActivity.this).deleteCart(id_list.get(j));
				}
				
				
				//如果是作为清单列表使用就不用传总价，因为一进订单界面会重新计算购物车总价
				if (getIntent().getStringExtra("flage") != null) {																		
					
				}else {//作为购物车使用	 点提交时一定会跳到订单界面的								
					Intent intent = new Intent(Shoping_CarActivity.this, DingDanActivity.class);
					intent.putExtra("flage", "gouwuche");
					L.e("购物车传来的总价：" + money.getText().toString());
					intent.putExtra("COUNT", money.getText().toString());
					startActivity(intent);
				}								
			}
		});
	}
	
	/**
	 * 把获取产品总数和总价的方法集成在一起，以便几处的调用
	 */
	private void initCount(){
		/*nub_count = 0;
		for (int i = 0; i < data.size(); i++) {		
			nub_count = nub_count + (Integer)data.get(i).get("number");		
			L.e("nub_count:" + (Integer)data.get(i).get("number"));
		}
	    count.setText(nub_count + "件");	*/
	    
	    money_count = 0;
	    for (int i = 0; i < data.size(); i++) {					
			money_count = money_count + data.get(i).getPrice() * data.get(i).getQuantity();
			
			L.e("money_count:" + data.get(i).getPrice() * data.get(i).getQuantity());
		}
	    money.setText(String.valueOf(money_count));
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
			L.e("img" + data.get(position).getSmallImg());
			//将获取到的图片路径通过一个方法转化为图片。			
			new ImageLoader(Shoping_CarActivity.this).DisplayImage(
					Common.getPath() + data.get(position).getSmallImg(), holder.img, false);
			//holder.img.setImageBitmap(Bytes2Bimap((byte[])data.get(position).get("byte")));
			holder.tv.setText(data.get(position).getProduct_Name());
			holder.tv2.setText("￥" + data.get(position).getPrice() + "/" + data.get(position).getUnit_Name());			
			
			//把传过来的flort类型数量转换为没有小数点
			String goods_nub = data.get(position).getQuantity().toString();
			holder.edt.setText(goods_nub.substring(0, goods_nub.indexOf(".")));	
			
			//这里更改数据库的方法不是指定某条数据的id，而是获取这个实体类，更改某一项。	
			final CartModel cartModel = data.get(position);
			holder.add.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					int nub_count1 = Integer.parseInt(holder.edt.getText().toString());				
					nub_count1 = nub_count1 + 1;
					holder.edt.setText(nub_count1 + "");
					
					//点击一次增加，加一次这个item的单价	
					money_count = money_count + data.get(position).getPrice();						
					money.setText(String.valueOf(money_count));

					//改变数据库产品数量的方法	
					cartModel.setQuantity((float)nub_count1);
					data.remove(position);
					data.add(position, cartModel);	
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
					//改变数据库产品数量的方法
					cartModel.setQuantity((float)nub_count2);
					data.remove(position);
					data.add(position, cartModel);	
					initCount();
				}
			});
			
			holder.img2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
										
					AlertDialog.Builder builder = new Builder(Shoping_CarActivity.this);
					builder.setMessage("确认删除吗？");					 
					builder.setTitle("提示");
					builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int which) {
				 		
					    //根据这个名称作为索引来更新或删除某一条数据，后期要改为商品唯一id的
						id_goods = data.get(position).getProduct_ID();
						id_list.add(id_goods);						
						data.remove(position);
						myAdapte.notifyDataSetChanged();
					    //dbDel();					    
					    //query();	
					    
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
					
					LayoutInflater layoutInflater = LayoutInflater.from(Shoping_CarActivity.this);
					View view = layoutInflater.inflate(R.layout.alertdialog_goodsnub, null);
					final AlertDialog dialog = new AlertDialog.Builder(Shoping_CarActivity.this).create();
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
							int nubs = Integer.valueOf(edt.getText().toString());
							if (nubs > 0) {
								edt.setText(nubs - 1 + "");
								
							}else {
								edt.setText(1 + "");
							}
							
						}
					});
					
					ImageView add = (ImageView)view.findViewById(R.id.add);
					add.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {							
							int nubs = Integer.valueOf(edt.getText().toString());
							if (nubs > 0) {
								edt.setText(nubs + 1 + "");
								
							}else {
								edt.setText(1 + "");
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

								cartModel.setQuantity(Float.parseFloat(holder.edt.getText().toString()));
								L.e("更改后：" + cartModel.getQuantity());
								data.remove(position);
								data.add(position, cartModel);
								
								//改变数据库产品数量的方法
//								cartModel.setQuantity(Float.parseFloat(edt.getText().toString()));										
//								CartManager.getInstance(Shoping_CarActivity.this).saveCarts(cartModel);
								//更改数据库后再查一遍再计算总价  
								//query ();
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
				if (edt.getText().toString().trim().equals("")) {
					edt.setText("1");
					nub.setText("x1");
				}else {
					nub.setText("x" + edt.getText().toString());
				}								
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
	 * 查询的公共方法  Bitmap bitmap, String names, Double prices, int nubs
	 * @param db
	 * @param bitmap
	 * @param name
	 * @param price
	 * @param nub
	 * @return
	 */
//	public void query (){			
//		data = new ArrayList<Map<String,Object>>();
//		
//		//id, img, name, price, unit, nub, class_ID		
//		Cursor cursor = db.query("shopping_cart", null, null, null, null, null, "_id ASC");
//		cursor.moveToFirst();
//		if (cursor != null && cursor.getCount() != 0) {			
//			while (!cursor.isAfterLast()) {		
//				Map<String, Object> item = new HashMap<String, Object>();
//				item.put("_id", cursor.getInt(0));
//				item.put("id", cursor.getInt(1));
//				item.put("byte", cursor.getBlob(cursor.getColumnIndex("img")));//这个"img"是创建数据库时的字段
//				
//				item.put("name",  cursor.getString(3));
//				item.put("prices",  cursor.getDouble(4));
//				item.put("unit",  cursor.getString(5));
//				item.put("number",  cursor.getInt(6));
//				
//				Log.e("-------------", "_id:" + cursor.getInt(0));
//				Log.e("-------------", "id:" + cursor.getInt(1));
//				Log.e("-------------", "name：" + cursor.getString(3));
//				Log.e("-------------", "prices：" + cursor.getDouble(4));
//				Log.e("-------------", "unit：" + cursor.getString(5));
//				Log.e("-------------", "number：" + cursor.getInt(6));
//				data.add(item);
//				cursor.moveToNext();
//			}
//		}		
//		
//		listView.setAdapter(new myAdapter(this));
//	} 
	
	/**
	 * 从数据库删除的方法
	 */
//	protected void dbDel() {
//		// TODO Auto-generated method stub
//		String where = "id = " + id_goods;
//		Log.e("=============", "where:" + where);
//		int i = db.delete("shopping_cart", where, null);
//		if(i > 0){
//			Log.i("==============","数据删除成功！");
//		}else{
//			Log.i("==============","数据未删除！");
//		}
//	}
	
	/**
	 * 产品数量变化后更改的方法
	 * 数据库更改的字段要和创建的字段一样
	 * id, img, name, price, unit, nub, class_ID
	 */	 	
//	protected void dbUpdate(int id, int count) {
//		// TODO Auto-generated method stub
//		ContentValues values = new ContentValues();
//		values.put("nub", count);
//		
//		String where = "id = " + id;
//		int i = db.update("shopping_cart", values, where, null);
//		if(i > 0)
//			Log.i("myDbDemo","数据更新成功！");
//		else
//			Log.i("myDbDemo","数据未更新！");
//	}
	
	/**
	 * 将数据库byte形态的图片转化为bigmap
	 * @param b
	 * @return
	 */
//	private Bitmap Bytes2Bimap(byte[] b){
//	      if(b.length != 0){
//	       return BitmapFactory.decodeByteArray(b, 0, b.length);
//	       
//	      }else {
//	       return null;
//	      }
//	} 
}
