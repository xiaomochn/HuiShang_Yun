package com.huishangyun.Adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.stock.StockList;
import com.huishangyun.model.Constant;
import com.huishangyun.yun.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 库存记录适配器
 * 
 * @author 熊文娟
 * 
 */
public class StockRecordAdapter extends BaseAdapter {

	private Context context = null;
	List<StockList> list = new ArrayList<StockList>();
	Calendar calendar = Calendar.getInstance();
	private String time;//当前时间

	public StockRecordAdapter(Context context, List<StockList> itemLists) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = itemLists;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}


	static class ViewHolder {
		public TextView txt_stock_date;// 日期
		public TextView txt_stock_time;// 时间
		public TextView txt_stock_name;// 登录名
		public TextView unit;// 单位、数量
		public TextView product;// 产品
		public RelativeLayout date_bar;// 日期顶部
		public RelativeLayout login_inf_bar;// 登录名及提交时间
		public LinearLayout unit_bar;// 产品、数量及单位
		public View view1;
		public View view2;
		public View view3;
		public View view;
		
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		
		//获取当前时间
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		Date date1 = new Date(year - 1900, month, day); // 获取时间转换为Date对象
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		time = sf.format(date1);

		if (convertView == null) {// 如果缓存convertView为空，则需要创建View
			convertView = LayoutInflater.from(context).inflate(
					R.layout.activity_stock_record_myitem, null);// 根据context上下文加载布局
			holder = new ViewHolder();
			// 根据自定义的Item布局加载布局
			holder.txt_stock_date = (TextView) convertView
					.findViewById(R.id.txt_stock_date);
			holder.txt_stock_time = (TextView) convertView
					.findViewById(R.id.txt_stock_time);
			holder.txt_stock_name = (TextView) convertView
					.findViewById(R.id.txt_stock_name);
			holder.unit = (TextView) convertView.findViewById(R.id.unit);
			holder.product = (TextView) convertView.findViewById(R.id.product);
			holder.date_bar = (RelativeLayout) convertView
					.findViewById(R.id.date_bar);
			holder.login_inf_bar = (RelativeLayout) convertView
					.findViewById(R.id.login_inf_bar);
			holder.unit_bar = (LinearLayout) convertView
					.findViewById(R.id.product_bar);
			
			holder.view = (View) convertView
					.findViewById(R.id.view);
			holder.view1 = (View) convertView
					.findViewById(R.id.view1);
			holder.view2 = (View) convertView
					.findViewById(R.id.view2);
			holder.view3 = (View) convertView
					.findViewById(R.id.view3);
			
			// 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		/*****************************显示隐藏控制Start***************************/
		
		//当当天日期进来只有一条数据的时候还有问题
		if (list.size()<=1 ) {
			
			holder.login_inf_bar.setVisibility(View.VISIBLE);// 登入名条
			holder.date_bar.setVisibility(View.VISIBLE);// 日期条
			holder.unit_bar.setVisibility(View.VISIBLE);// 产品单位条
			
			holder.view.setVisibility(View.VISIBLE);
			holder.view1.setVisibility(View.VISIBLE);
			holder.view2.setVisibility(View.VISIBLE);
			holder.view3.setVisibility(View.VISIBLE);
			
		}else {
			
		
		if (position == 0) {
			//第一个显示方式
			holder.login_inf_bar.setVisibility(View.GONE);// 登入名条
			holder.date_bar.setVisibility(View.VISIBLE);// 日期条
			holder.unit_bar.setVisibility(View.VISIBLE);// 产品单位条
			
			holder.view.setVisibility(View.VISIBLE);
			holder.view1.setVisibility(View.VISIBLE);
			holder.view2.setVisibility(View.VISIBLE);
			holder.view3.setVisibility(View.GONE);

		} else if (position == list.size() - 1) {
             //最后一个显示方式
			holder.login_inf_bar.setVisibility(View.VISIBLE);
			holder.date_bar.setVisibility(View.GONE);
			holder.unit_bar.setVisibility(View.GONE);
		
			holder.view.setVisibility(View.GONE);
			holder.view1.setVisibility(View.GONE);
			holder.view2.setVisibility(View.VISIBLE);
			holder.view3.setVisibility(View.VISIBLE);

		} else {
              //当前日期等于下一个日期并且当前日期等于上一个日期
			if (list.get(position).getBelongDate().trim()
					.equals(list.get(position + 1).getBelongDate().trim())
					&& list.get(position).getBelongDate().trim()
							.equals(list.get(position - 1).getBelongDate().trim())) {

				holder.login_inf_bar.setVisibility(View.GONE);
				holder.date_bar.setVisibility(View.GONE);
				holder.unit_bar.setVisibility(View.GONE);
				
				holder.view.setVisibility(View.GONE);
				holder.view1.setVisibility(View.GONE);
				holder.view3.setVisibility(View.GONE);
				holder.view2.setVisibility(View.VISIBLE);
				

			} else if ((list.get(position).getBelongDate().trim()
					.equals(list.get(position + 1).getBelongDate().trim()) && !list
					.get(position).getBelongDate().trim()
					.equals(list.get(position - 1).getBelongDate().trim()))) {
				
                //当前日期不等于上一个日期但等于下一个日期 
				holder.login_inf_bar.setVisibility(View.GONE);// 登入名条
				holder.date_bar.setVisibility(View.VISIBLE);// 日期条
				holder.unit_bar.setVisibility(View.VISIBLE);// 产品单位条
				
				holder.view.setVisibility(View.VISIBLE);
				holder.view1.setVisibility(View.VISIBLE);
				holder.view2.setVisibility(View.VISIBLE);
				holder.view3.setVisibility(View.GONE);

			}else if ((!list.get(position).getBelongDate().trim()
					.equals(list.get(position + 1).getBelongDate().trim()) && list
					.get(position).getBelongDate().trim()
					.equals(list.get(position - 1).getBelongDate().trim()))) {
				
				//当前日期 不等于下一个日期 并且等于上一个日期
				holder.login_inf_bar.setVisibility(View.VISIBLE);
				holder.date_bar.setVisibility(View.GONE);
				holder.unit_bar.setVisibility(View.GONE);
				
				holder.view.setVisibility(View.GONE);
				holder.view1.setVisibility(View.GONE);
				holder.view2.setVisibility(View.VISIBLE);
				holder.view3.setVisibility(View.VISIBLE);
				
			}
		}
		}
        /****************************显示隐藏控制End**********************************/
		
//		if (backDate1(list.get(position).getBelongDate()).equals(time)  time.) {
		if (time.contains(list.get(position).getBelongDate())) {
			holder.txt_stock_date.setText("今天");
		}else {
			holder.txt_stock_date.setText(list.get(position).getBelongDate());
		}
		
//		holder.txt_stock_date.setText(list.get(position).getBelongDate());
		
		holder.txt_stock_time.setText(backTime());//当前登录时间
		holder.txt_stock_name.setText(MyApplication.preferences.getString(Constant.XMPP_MY_REAlNAME, ""));//登入人姓名
		holder.unit.setText(list.get(position).getQuantity() + "/"
				+ list.get(position).getUnit_Name());
		holder.product.setText(list.get(position).getProduct_Name());

		return convertView;
	}
	
	/**
	 * 获取当前系统时间
	 * @return
	 */
	private String backTime(){
		
	        //设置默认的时间
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);
			Date date1 = new Date(year - 1900, month, day,hour,minute); // 获取时间转换为Date对象
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String time = sf.format(date1);
			
			return time;
	}
	
	/**
	 * 传入后台接到的时间，返回我们需要的格式： 2014年9月20日
	 * @param date 传入时间参数
	 * @return
	 */
	private String backDate1(String date){
		//这里包含0位但不包含5即（0，5】
		String a = date.substring(0,4);
		String b = date.substring(5,7);
		String c = date.substring(8,10);
		String datesString = a + "-" + b + "-" + c;
		
		return datesString;		
	}
		
	}
