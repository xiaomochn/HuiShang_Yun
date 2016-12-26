package com.huishangyun.Office.WeiGuan;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.huishangyun.Channel.Visit.PictureSkim;
import com.huishangyun.Office.Summary.ImageLoad;
import com.huishangyun.App.MyApplication;
import com.huishangyun.model.Constant;
import com.huishangyun.yun.R;

public class mGirdViewBaseAdapter extends BaseAdapter {

	private List<String> list;
	private LayoutInflater inflater;
	private int image_end;
	private Context context;
	private String ImgStr;
	public mGirdViewBaseAdapter(Context context,List<String> list,int image_end){
		inflater = LayoutInflater.from(context);
		this.list = list;
		this.image_end = image_end;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
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


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.activity_onlooks_girdview_item, null);
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.imageBig = (ImageView) convertView.findViewById(R.id.imageBig);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.image.setImageResource(R.drawable.defaultimage02);
		if (position<image_end) {
			
			String ImageUrl = Constant.pathurl+
	                MyApplication.getInstance().getCompanyID()+"/Action/100x100/" + list.get(position);
			new ImageLoad().displayImage(context, ImageUrl, holder.image, R.drawable.defaultimage02, 0, false);
			
			if (position==0) {
				ImgStr = getImageUri(list);
			}
			holder.image.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(context,PictureSkim.class);
						intent.putExtra("index", 5);
						intent.putExtra("imgselect", position);
						intent.putExtra("imgUri", ImgStr);
//						L.e("图片地址：" +ImgStr);
						context.startActivity(intent);
					}
				});
			
		}else {
			 holder.image.setImageResource(R.drawable.onlooks_record);
			 holder.image.setOnClickListener(new OnClickListener() {
				 
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//跳出播放录音窗口
					holder.image.setTag(list.get(position));
					new LoadFileTools(context).loadData(holder.image);
				}
			});
		}
		
		
		return convertView;
	}
	
	private static final class ViewHolder{
		private ImageView image;
		private ImageView imageBig;
	}
	
	private String getImageUri(List<String> list){
		StringBuffer stringBuffer = new StringBuffer("");
		for (int i = 0; i < image_end; i++) {
			if (i > 0) {
				stringBuffer.append("#");
				stringBuffer.append(list.get(i));
			}else {
				stringBuffer.append(list.get(i));
			}
		}
		return stringBuffer.toString();
	}

}
