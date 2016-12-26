package com.huishangyun.Adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.huishangyun.yun.R;

public class ContactAdapter extends BaseAdapter {
	public Context context;
	public LayoutInflater layoutInflater;
	public List<Map<String, Object>> list;

	private View parentView;

	public ContactAdapter(Context context, List<Map<String, Object>> list) {
		this.context = context;
		this.list = list;
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parentView = layoutInflater.inflate(R.layout.groupdata_line_parent,
				null);
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
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		final Map<String, Object> map = list.get(position);

		Item item = null;
		Parent parent = null;

		final String role = map.get("role").toString();
		final String groupName = map.get("groupName").toString();
		final boolean visibile = (Boolean) map.get("visibile");
		if (role.equals("parent")) {
			parent = new Parent();
			parent.imgIcon = (ImageView) parentView
					.findViewById(R.id.img_groupdata_line_parent_icon1);
			parent.tvGroupName = (TextView) parentView
					.findViewById(R.id.tv_groupdata_line_parent_name);

			parent.tvGroupName.setText(groupName);
			if (visibile)
				parent.imgIcon.setImageResource(R.drawable.arr01_up);
			else
				parent.imgIcon.setImageResource(R.drawable.arr01_down);

			return parentView;
		} else if (role.equals("item")) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.groupdata_line,
						null);
				item = new Item();
				item.tvTelNumber = (TextView) convertView
						.findViewById(R.id.tv_groupdata_line_telnums);
				item.tvUserName = (TextView) convertView
						.findViewById(R.id.tv_groupdata_line_name);
				item.imgIcon1 = (ImageView) convertView
						.findViewById(R.id.img_groupdata_line_icon1);
				item.imgIcon2 = (ImageView) convertView
						.findViewById(R.id.img_groupdata_line_icon2);

				convertView.setTag(item);
			} else {
				item = (Item) convertView.getTag();
			}

			String userName = map.get("userName").toString();
			item.tvUserName.setText(userName.split("@")[0]);
			item.tvTelNumber.setText(userName);

			Log.d("debug -->>", "userName == " + userName);

			if (visibile) {
				convertView.setLayoutParams(new ListView.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				Log.d("debug -->>", "设置可见");
			} else {
				convertView.setLayoutParams(new ListView.LayoutParams(0, 0));
				Log.e("debug -->>", "设置不可见");
			}

			return convertView;
		}

		return null;
	}

	class Parent {
		// tv_groupdata_line_parent_name
		// img_groupdata_line_parent_icon1
		TextView tvGroupName;
		ImageView imgIcon;
	}

	class Item {
		// img_groupdata_line_icon1
		// tv_groupdata_line_name
		TextView tvUserName, tvTelNumber;
		ImageView imgIcon1, imgIcon2;
	}

}
