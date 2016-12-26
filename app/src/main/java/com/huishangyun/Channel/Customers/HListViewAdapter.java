package com.huishangyun.Channel.Customers;

import java.util.List;

import com.huishangyun.Channel.Plan.BitmapUtil;
import com.huishangyun.yun.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * 横向Listview适配器s
 * @author Pan
 *
 */
public class HListViewAdapter extends BaseAdapter{
	private List<GroupModel> mIconIDs;
	private Context mContext;
	private LayoutInflater mInflater;
	Bitmap iconBitmap;
	private int selectIndex = -1;
	
	public HListViewAdapter(Context context, List<GroupModel> mIconIDs) {
		this.mContext = context;
		this.mIconIDs = mIconIDs;
//		this.mTitles = titles;
		mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mIconIDs.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mIconIDs.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.activity_plan_customers_imgitem, null);
			holder.mImage=(ImageView)convertView.findViewById(R.id.img_list_item);
//			holder.mTitle=(TextView)convertView.findViewById(R.id.text_list_item);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		if(position == selectIndex){
			convertView.setSelected(true);
		}else{
			convertView.setSelected(false);
		}
		holder.mImage.setBackgroundResource(R.drawable.plan_person);
		return convertView;
	}
	
	static class ViewHolder {
//		private TextView mTitle ;
		public ImageView mImage;
	}
	
	private Bitmap getPropThumnail(int id){
		Drawable d = mContext.getResources().getDrawable(id);
		Bitmap b = BitmapUtil.drawableToBitmap(d);
//		Bitmap bb = BitmapUtil.getRoundedCornerBitmap(b, 100);
		int w = mContext.getResources().getDimensionPixelOffset(R.dimen.thumnail_default_width);
		int h = mContext.getResources().getDimensionPixelSize(R.dimen.thumnail_default_height);
		Bitmap thumBitmap = ThumbnailUtils.extractThumbnail(b, w, h);
		
		return thumBitmap;
	}
	public void setSelectIndex(int i){
		selectIndex = i;
	}
	

}
