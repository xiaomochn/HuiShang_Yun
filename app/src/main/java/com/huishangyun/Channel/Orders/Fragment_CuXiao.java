package com.huishangyun.Channel.Orders;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huishangyun.Util.L;
import com.huishangyun.swipelistview.MyXListView;
import com.huishangyun.swipelistview.MyXListView.MyXListViewListener;
import com.huishangyun.yun.R;

public class Fragment_CuXiao extends Fragment implements MyXListViewListener{
	private MyXListView listView;
	private String[] names = new String[]{"润田翠天然含硒矿泉水", "润田翠天然含硒矿泉水润田翠天然含硒矿泉水", "润田翠天然含硒矿泉水", "润田翠天然含硒矿泉水", "润田翠天然含硒矿泉水"}; 
	
	private GetNub getNub;
	
	/**
	 * 用来存放点击过的list子项的集合
	 */
	private List<Integer> list = new ArrayList<Integer>();
 	/**
	 * 创建一个构造方法获取到接口
	 * @paramgetNub
	 */
//	public Fragment_CuXiao(GetNub getNub){
//		this.getNub = getNub;
//	}
	
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_cuxiao, container, false);
		
//		listView = (MyXListView)view.findViewById(R.id.listview);		
//		listView.setAdapter(new myAdapter(getActivity()));
//		listView.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				Intent intent = new Intent(getActivity(), DatailsActivity.class);	
//				startActivity(intent);
//			}
//		});
		return view;
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
				view = inflater.inflate(R.layout.list_cuxiao, null);
				holder.img = (ImageView) view.findViewById(R.id.img);
				holder.tv = (TextView) view.findViewById(R.id.name);
				holder.tv2 = (TextView) view.findViewById(R.id.time);
				holder.tv3 = (TextView) view.findViewById(R.id.cuxiao);
				holder.tv4 = (TextView) view.findViewById(R.id.price);
				holder.img2 = (ImageView) view.findViewById(R.id.imgcar);
									
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
			holder.tv.setText(names[position]);
									
			holder.img2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					if (list.contains(new Integer(position))) {// 如果选中就取消
						holder.img2.setImageResource(R.drawable.shopping_car_grey);
//						list.remove(new Integer(position));
//						getNub.deletNub();
						
					} else {
						holder.img2.setImageResource(R.drawable.shopping_car_blue);
//						list.add(new Integer(position));
//						getNub.addNub();
						
					}
					
					L.e("list:" + list.toString());
				}
			});
			
			return view;
		}
		
		public final class ViewHolder {// 写这个类是为了点击list的时候好调用view
			public ImageView img;
			public TextView tv;
			public TextView tv2;
			public TextView tv3;
			public TextView tv4;
			public ImageView img2;
		}
	}

@Override
public void onRefresh() {
	new Handler().postDelayed(new Runnable() {
		public void run() {
			listView.stopRefresh();
			listView.setRefreshTime();
		}
	}, 2000);
}

@Override
public void onLoadMore() {
	new Handler().postDelayed(new Runnable() {
		public void run() {
			listView.stopLoadMore();
		}
	}, 2000);
}

}
