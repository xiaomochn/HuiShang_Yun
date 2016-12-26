package com.huishangyun.Adapter;

import java.util.ArrayList;
import java.util.List;

import com.huishangyun.Channel.stock.StockList;
import com.huishangyun.yun.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 库存上报适配器
 * 
 * @author 熊文娟
 * 
 */
public class StockReportAdapter extends BaseAdapter {
	private Context context = null;

	private List<StockList> mlist = new ArrayList<StockList>();

	public StockReportAdapter(Context context, List<StockList> lists) {
		// TODO Auto-generated constructor stub
		this.context = context;
		mlist = lists;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlist.size();
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
		public TextView product;// 产品
		public TextView amountUnit;// 数量及单位
	    private View view1;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {// 如果缓存convertView为空，则需要创建View
			convertView = LayoutInflater.from(context).inflate(
					R.layout.activity_stock_report_item, null);// 根据context上下文加载布局
			holder = new ViewHolder();
			// 根据自定义的Item布局加载布局
			holder.product = (TextView) convertView
					.findViewById(R.id.report_product);
			holder.amountUnit = (TextView) convertView
					.findViewById(R.id.report_amount_unit);
			holder.view1 = (View) convertView
					.findViewById(R.id.view1);
			// 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
//		if (position == 0) {
//			holder.product.setText("产品");
//			holder.amountUnit.setText("数量/单位");
//			holder.view1.setVisibility(View.VISIBLE);
//		}else {
		if (mlist.size()>0) {
			holder.view1.setVisibility(View.GONE);
			holder.product.setText(mlist.get(position).getProduct_Name());
			holder.amountUnit.setText(mlist.get(position).getQuantity() 
					+ "/" + mlist.get(position).getUnit_Name());
		}
		
			
//		}
		
		
		
		return convertView;
	}

}
