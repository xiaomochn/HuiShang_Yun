package com.huishangyun.Adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huishangyun.Util.L;
import com.huishangyun.model.StockBean;
import com.huishangyun.ImageLoad.ImageLoader;
import com.huishangyun.View.RoundAngleImageView;
import com.huishangyun.yun.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**库存上报中的产品适配器
 * 
 * @author 熊文娟
 *
 */
public class StockProductAdapter extends BaseAdapter {
	private Context context;
	private List<StockBean> list;
	private List<Map<String, Boolean>> state = new ArrayList<Map<String,Boolean>>();
	public StockProductAdapter(Context context,List<StockBean> list) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.list=list;
		for (int i = 0; i < list.size(); i++) {
			Map<String, Boolean> map = new HashMap<String, Boolean>();
			StockBean bean=list.get(i);
			if (i == list.size() - 1) {
				map.put("line", true);
			}else {
				String lastCatalog = list.get(i + 1).getLetter();
				if (bean.getLetter().equals(lastCatalog)) {
					map.put("line", false);
				}else {
					map.put("line", true);
				}
			}
			if (!bean.isIsgroup()) {
				map.put("group", false);//不是组
			}else {
				map.put("group", true);//是组
			}
			state.add(map);
		}
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

	static class ViewHolder{
		public TextView letter;
		public RoundAngleImageView headView;
		public TextView product;
		public RelativeLayout line;
		public RelativeLayout noline;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		final StockBean bean = list.get(position);
		if (!state.get(position).get("group")) {
			//不是组
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.activity_stock_no_classify, null);
                holder = new ViewHolder();
            	holder.letter = (TextView)convertView.findViewById(R.id.txt_letter);
				holder.headView = (RoundAngleImageView)convertView.findViewById(R.id.product_head);
				holder.product=(TextView) convertView.findViewById(R.id.txt_no_group);
				holder.line = (RelativeLayout) convertView.findViewById(R.id.stock_line);
				holder.noline = (RelativeLayout) convertView.findViewById(R.id.stock_line_no);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (position == 0) {
				holder.letter.setVisibility(View.VISIBLE);
			}else {
				String lastCatalog = list.get(position - 1).getLetter();
				if (bean.getLetter().equals(lastCatalog)) {
					holder.letter.setVisibility(View.GONE);
				}else {
					holder.letter.setVisibility(View.VISIBLE);
				}
			}
			boolean line = state.get(position).get("line");
			holder.headView.setImageResource(R.drawable.login_default_avatar);
			if (line) {
				holder.line.setVisibility(View.VISIBLE);
				holder.noline.setVisibility(View.GONE);
			}else {
				holder.line.setVisibility(View.GONE);
				holder.noline.setVisibility(View.VISIBLE);
			}
			holder.letter.setText(bean.getLetter());
			if (!bean.getProduct().equals("")) {
				holder.product.setText(bean.getProduct());
			}else {
				holder.product.setText(bean.getJID());
			}
			holder.product.setTag(bean);
			L.d("position = " + position);
			new ImageLoader(context).DisplayImage(bean.getPhoto(), holder.headView, false);
			
		}
		else {
			//是组
				convertView = LayoutInflater.from(context).inflate(R.layout.activity_stock_classify, null);
                holder = new ViewHolder();
                holder.letter = (TextView)convertView.findViewById(R.id.txt_letter);
				holder.headView = (RoundAngleImageView)convertView.findViewById(R.id.product_head);
				holder.product=(TextView) convertView.findViewById(R.id.txt_group);
				holder.line = (RelativeLayout) convertView.findViewById(R.id.stock_line);
				holder.noline = (RelativeLayout) convertView.findViewById(R.id.stock_line_no);
				convertView.setTag(holder);
			if (position == 0) {
				holder.letter.setVisibility(View.VISIBLE);
			}else {
				String lastCatalog = list.get(position - 1).getLetter();
				if (bean.getLetter().equals(lastCatalog)) {
					holder.letter.setVisibility(View.GONE);
				}else {
					holder.letter.setVisibility(View.VISIBLE);
				}
			}
			boolean line = state.get(position).get("line");
			holder.headView.setImageResource(R.drawable.login_default_avatar);
			if (line) {
				holder.line.setVisibility(View.VISIBLE);
				holder.noline.setVisibility(View.GONE);
			}else {
				holder.line.setVisibility(View.GONE);
				holder.noline.setVisibility(View.VISIBLE);
			}
			holder.letter.setText(bean.getLetter());
			if (!bean.getProduct().equals("")) {
				holder.product.setText(bean.getProduct());
			}else {
				holder.product.setText(bean.getJID());
			}
			holder.product.setTag(bean);
			L.d("position = " + position);
			
		}
		
		return convertView;
	}

}
