package com.huishangyun.Channel.Plan;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.huishangyun.Channel.Visit.BitmapTools;
import com.huishangyun.Util.L;
import com.huishangyun.App.MyApplication;
import com.huishangyun.model.Constant;
import com.huishangyun.yun.R;

public class HorizontalListViewAdapter extends BaseAdapter{
	private List<SortModel> mIconIDs;
//	private String[] mTitles;
	private Context mContext;
	private LayoutInflater mInflater;
	Bitmap iconBitmap;
	private int selectIndex = -1;

	public HorizontalListViewAdapter(Context context,  List<SortModel> ids){
		this.mContext = context;
		this.mIconIDs = ids;
//		this.mTitles = titles;
		mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
	}
	@Override
	public int getCount() {
		return mIconIDs.size();
	}
	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.activity_plan_customers_imgitem, null);
			holder.mImage=(ImageView)convertView.findViewById(R.id.img_list_item);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		if(position == selectIndex){
			convertView.setSelected(true);
		}else{
			convertView.setSelected(false);
		}
		holder.mImage.setImageResource(R.drawable.plan_person);
		String person_url = Constant.pathurl+
				MyApplication.getInstance().getCompanyID()+"/Photo/" + mIconIDs.get(position).getPerson_img();
		L.e("person_url:" + person_url);
		holder.mImage.setTag(person_url);
		new BitmapTools().downLoadImage(holder.mImage, 100, 100,true,mContext);
		
		return convertView;
	}

	private static class ViewHolder {
//		private TextView mTitle ;
		private ImageView mImage;
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