package com.huishangyun.Channel.Customers;

import java.util.List;

import com.huishangyun.Util.L;
import com.huishangyun.Util.TimeUtil;
import com.huishangyun.model.Members;
import com.huishangyun.yun.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 客户列表适配器
 * @author Pan
 *
 */
public class CustomerListAdapter extends BaseAdapter{
	private List<Members> mList;
	private Context mContext;
	public CustomerListAdapter(Context mContext, List<Members> mList) {
		// TODO Auto-generated constructor stub
		this.mList = mList;
		this.mContext = mContext;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder mHolder;
		if (convertView == null) {
			mHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_customer_list, null);
			mHolder.title = (TextView) convertView.findViewById(R.id.customer_item_title);
			mHolder.name = (TextView) convertView.findViewById(R.id.customer_item_name);
			mHolder.phone = (TextView) convertView.findViewById(R.id.customer_item_phone);
			mHolder.time = (TextView) convertView.findViewById(R.id.customer_item_time);
			mHolder.last = (TextView) convertView.findViewById(R.id.customer_item_last);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		String Mobile = mList.get(position).getMobile();
		mHolder.title.setText(mList.get(position).getRealName().trim());
		mHolder.name.setText(mList.get(position).getContact().trim());
		mHolder.phone.setText(getStr(Mobile).trim());
		L.e("最后拜访时间:" + mList.get(position).getVisitTime());
		if (mList.get(position).getVisitTime() == null
				|| mList.get(position).getVisitTime().equals("")) {//判断是否要显示文本框
			mHolder.last.setVisibility(View.GONE);
		} else {
			mHolder.last.setVisibility(View.VISIBLE);
		}
		mHolder.time.setText(TimeUtil.getTime(mList.get(position).getVisitTime()));
		return convertView;
	}
	
	
	static class ViewHolder {
		public TextView title;
		public TextView name;
		public TextView phone;
		public TextView time;
		public TextView last;
	}
	
	private String getStr(String str) {
		if (str == null || str.equals("")) {
			return "";
		}
		return str;
		
	}

}
