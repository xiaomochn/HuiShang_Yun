package com.huishangyun.Adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huishangyun.model.GroupBean;
import com.huishangyun.yun.R;

public class GroupAdapter extends BaseAdapter{
	
	private List<GroupBean> list;
	private LayoutInflater inflater;
	
	public GroupAdapter(Context context,List<GroupBean> list){
		this.list = list;
		this.inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		GroupBean gBean = list.get(position);
		ViewHoalder hoalder = null;
		if (convertView == null) {
			hoalder = new ViewHoalder();
			convertView = inflater.inflate(R.layout.item_group, null);
			hoalder.name = (TextView)convertView.findViewById(R.id.group_name);
			convertView.setTag(hoalder);
		}else {
			hoalder = (ViewHoalder) convertView.getTag();
		}
		hoalder.name.setText(gBean.getName());
		hoalder.name.setTag(gBean);
		return convertView;
	}
	
	static class ViewHoalder{
		private TextView name;
	}

}
