package com.huishangyun.Channel.Orders;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.huishangyun.yun.R;

/**
 * 没用上的类，退货单列表和订货单共用了
 * @author Administrator
 *
 */
public class Fragment_TuiDanMy extends Fragment {

	private ListView listView;
	private String[] names = new String[]{"南昌纵捷", "亿华科技", "南昌纵捷", "亿华科技", "南昌纵捷", "亿华科技"}; 
	
	/**
	 * 用来存放点击过的list子项的集合
	 */
	private List<Integer> list = new ArrayList<Integer>();
 	/**
	 * 创建一个构造方法获取到接口
	 * @param getNub
	 */
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_all, container, false);
		
		listView = (ListView)view.findViewById(R.id.listview);		
		listView.setAdapter(new myAdapter(getActivity()));
						
		listListener();
		return view;
	}
	
	private void listListener(){
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent intent = new Intent(getActivity(), TuiDanDatailsActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private class myAdapter extends BaseAdapter{
		
		private LayoutInflater inflater;// 用来导入布局

		public myAdapter(Context context) {// 构造器
			this.inflater = LayoutInflater.from(context);
		}
		
		public int getCount() {
			return names.length;
		}

		@Override
		public Object getItem(int position) {
			return names[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			
			final ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.list_dingdan, null);
				
				holder.name = (TextView) view.findViewById(R.id.name);
				holder.tongzhi = (TextView) view.findViewById(R.id.tongzhi);
				holder.nub = (TextView) view.findViewById(R.id.nub);
				holder.price = (TextView) view.findViewById(R.id.price);
				holder.time = (TextView) view.findViewById(R.id.time);
				holder.img = (ImageView) view.findViewById(R.id.img);
				holder.img2 = (ImageView) view.findViewById(R.id.img2);
									
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
						
			holder.name.setText(names[position]);		
															
			return view;
		}
		
		public final class ViewHolder {// 写这个类是为了点击list的时候好调用view
			public TextView name;
			public TextView tongzhi;
			public TextView nub;
			public ImageView img;
			public TextView price;
			public ImageView img2;
			public TextView time;
		}
	}
}
