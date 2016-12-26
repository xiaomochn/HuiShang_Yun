package com.huishangyun.Adapter;

import java.util.List;

import com.huishangyun.Channel.Task.MenuMoths;
import com.huishangyun.yun.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * <p>时间,优先级,类别适配器</p>
 * @author Pan
 * @version 慧商云APP V1.0 2014-08-27 16:01
 */
public class TaskMenuAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private List<MenuMoths> mList;
	
	public TaskMenuAdapter(Context context, List<MenuMoths> mList) {
		this.mContext = context;
		this.mList = mList;
		this.mInflater = LayoutInflater.from(mContext);
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
			convertView = mInflater.inflate(R.layout.item_task_type, null);
			mHolder = new ViewHolder();
			mHolder.img = (ImageView) convertView.findViewById(R.id.task_menu_img);
			mHolder.name = (TextView) convertView.findViewById(R.id.task_menu_name);
			mHolder.co = (TextView) convertView.findViewById(R.id.task_menu_index);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		setImg(mHolder.img, mList.get(position).getType());
		if (mList.get(position).getType() == 1) {
			
			mHolder.name.setText(mList.get(position).getTitle() + "月份");
		} else {
			mHolder.name.setText(mList.get(position).getTitle());
		}
		
		mHolder.co.setText(mList.get(position).getCo()+"");
		return convertView;
	}
	
	/**
	 * 设置图标
	 * @param imageView
	 */
	private void setImg(ImageView imageView, int Type) {
		switch (Type) {
		case 0:
			imageView.setImageResource(R.drawable.task_listitem_type);
			break;
		case 1:
			imageView.setImageResource(R.drawable.task_listitem_time);
			break;
		case 2:
			imageView.setImageResource(R.drawable.task_listitem_pro);
			break;
		case 3:
			imageView.setImageResource(R.drawable.customer_group);
			break;
		case 4:
			imageView.setImageResource(R.drawable.customer_prog);
			break;
		case 5:
			imageView.setImageResource(R.drawable.customer_type);
			break;
		default:
			break;
		}
	}
	
	static class ViewHolder {
		public ImageView img;
		public TextView name;
		public TextView co;
	}

}
