package com.huishangyun.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huishangyun.App.MyApplication;
import com.huishangyun.Channel.Visit.PictureSkim;
import com.huishangyun.Office.Summary.ImageLoad;
import com.huishangyun.Util.L;
import com.huishangyun.Util.VisitDetails;
import com.huishangyun.model.Constant;
import com.huishangyun.yun.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 拜访适配器
 * 
 * @author 熊文娟
 * 
 */
public class VisitAdapter extends BaseAdapter {
    private List<VisitDetails> ImageList = new ArrayList<VisitDetails>();
	private static final String TAG = null;
	private List<VisitDetails> list;
	private Context context = null;
	private String time;//当前时间
	Calendar calendar = Calendar.getInstance();
	public static List<Bitmap> bitmaps = new ArrayList<Bitmap>();

	public VisitAdapter(Context context, List<VisitDetails> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
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

	static class ViewHolder {
		private TextView date;// 日期
		private TextView client;// 客户
		private TextView place;// 地点
		private TextView location;// 定位
		private TextView content;// 内容
		private ImageView picture1;// 照片1
		private ImageView picture2;// 照片2
		private ImageView picture3;// 照片3
		private TextView name;// 姓名
		private TextView time;// 时间
		private RelativeLayout topline;// 日期顶部的分割线
		private RelativeLayout bottomline;// 日期底部的分割线
		private TextView bubao;//是否补报
		private TextView telephonevisitTimes;//电话拜访时间
		private LinearLayout telephonevisit;
		private LinearLayout locationVisit;
		private TextView tags;//标签
		private TextView result;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		       //获取当前时间
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				Date date1 = new Date(year - 1900, month, day); // 获取时间转换为Date对象
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
				time = sf.format(date1);

		     Log.e("TAGS","time="+time);
		
		   final ViewHolder holder;
		if (convertView == null) {// 如果缓存convertView为空，则需要创建View
			convertView = LayoutInflater.from(context).inflate(
					R.layout.activity_visit_record_item, null);// 根据context上下文加载布局
			holder = new ViewHolder();
			// 根据自定义的Item布局加载布局
			holder.date = (TextView) convertView
					.findViewById(R.id.txt_visit_bubao_date);
			holder.tags = (TextView) convertView
					.findViewById(R.id.txt_visit_labelName);
			holder.client = (TextView) convertView
					.findViewById(R.id.txt_visit_clientName);
			holder.place = (TextView) convertView
					.findViewById(R.id.txt_visit_detailPlace);
			holder.location = (TextView) convertView
					.findViewById(R.id.txt_visit_Address);
			holder.content = (TextView) convertView
					.findViewById(R.id.txt_visit_describe);
			holder.result= (TextView) convertView.findViewById(R.id.txt_visit_result);
			holder.picture1 = (ImageView) convertView
					.findViewById(R.id.img_visit_picture1);
			holder.picture2 = (ImageView) convertView
					.findViewById(R.id.img_visit_picture2);
			holder.picture3 = (ImageView) convertView
					.findViewById(R.id.img_visit_picture3);
			holder.name = (TextView) convertView
					.findViewById(R.id.txt_visit_name);
			holder.time = (TextView) convertView
					.findViewById(R.id.txt_visit_time);
			holder.topline = (RelativeLayout) convertView
					.findViewById(R.id.visit_top_line);
			holder.bottomline = (RelativeLayout) convertView
					.findViewById(R.id.visit_bottom_line);
			holder.bubao = (TextView) convertView.
					findViewById(R.id.txt_visit_bubao);

			holder.telephonevisitTimes = (TextView) convertView.findViewById(R.id.telephonevisitTimes);
			holder.telephonevisit = (LinearLayout) convertView.findViewById(R.id.telephonevisit);
			holder.locationVisit = (LinearLayout) convertView.findViewById(R.id.locationVisit);
			// 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		if (position == 0) {
			holder.topline.setBackgroundColor(0xfff0f0f0);// 隐藏第一个日期顶部的分割线
		} else {
			holder.topline.setBackgroundColor(0xffe0e0e0);
		}
		holder.bottomline.setBackgroundColor(0xffe0e0e0);
		
		if (backDate1(list.get(position).getBelongDate()).equals(time)) {
			holder.date.setText("今天");
		}else {
			holder.date.setText(backDate(list.get(position).getBelongDate()));
		}
		
	    String Type = (list.get(position).getType()).trim();
	    L.e("拜访类型：" + Type);
    	if (Type.equals("现场拜访")) {
			holder.telephonevisit.setVisibility(View.GONE);
			holder.locationVisit.setVisibility(View.VISIBLE);
			
		}else if (Type.equals("电话拜访")) {
			holder.telephonevisit.setVisibility(View.VISIBLE);
			holder.locationVisit.setVisibility(View.GONE);
			holder.telephonevisitTimes.setText(list.get(position).getNote());
		}
	    	
	
		holder.client.setText(list.get(position).getMember_Name());
		holder.tags.setText(list.get(position).getTags());//标签名
		holder.place.setText(list.get(position).getMember_Adress());
		holder.location.setText(list.get(position).getLoc());
		holder.content.setText(list.get(position).getNote());
		holder.name.setText(list.get(position).getManager_Name());
		holder.time.setText(backDateMin(list.get(position).getAddDateTime()));
		holder.result.setText(list.get(position).getResult());
		
		holder.picture1.setImageResource(R.drawable.visit_img);
		holder.picture2.setImageResource(R.drawable.visit_img);
		holder.picture3.setImageResource(R.drawable.visit_img);
		
		if (list.get(position).getIsAdd()) {
			holder.bubao.setVisibility(View.VISIBLE);
		}else {
			holder.bubao.setVisibility(View.GONE);
		}
		
		// 显示图片
		String s3 = list.get(position).getPicture();
		String[] temp = null;
		temp = s3.split("#");
		
		for (int i = 0; i < temp.length; i++) {
			
			String imageUrl = Constant.pathurl+
					MyApplication.getInstance().getCompanyID() +"/Visit/100x100/"
					+ temp[i];
		
			Log.e(TAG, "imageUrl: " + imageUrl );
			//没有图片的位置隐藏框
			if (temp.length == 0) {
				holder.picture1.setVisibility(View.INVISIBLE);
				holder.picture2.setVisibility(View.INVISIBLE);
				holder.picture3.setVisibility(View.INVISIBLE);
			}else if (temp.length == 1) {
				holder.picture1.setVisibility(View.VISIBLE);
				holder.picture2.setVisibility(View.INVISIBLE);
				holder.picture3.setVisibility(View.INVISIBLE);
			}else if (temp.length == 2) {
				holder.picture1.setVisibility(View.VISIBLE);
				holder.picture2.setVisibility(View.VISIBLE);
				holder.picture3.setVisibility(View.INVISIBLE);
			}else {
				holder.picture1.setVisibility(View.VISIBLE);
				holder.picture2.setVisibility(View.VISIBLE);
				holder.picture3.setVisibility(View.VISIBLE);
			}
			
			if (i==0) {
				//将获取到的图片路径通过一个方法转化为图片。			
				
				new ImageLoad().displayImage(context, imageUrl, holder.picture1, R.drawable.defaultimage02, 0, false);
			}else if (i==1) {
				new ImageLoad().displayImage(context, imageUrl, holder.picture2, R.drawable.defaultimage02, 0, false);
			}else if (i==2) {
				new ImageLoad().displayImage(context, imageUrl, holder.picture3, R.drawable.defaultimage02, 0, false);
			}else {
				
				break;
			}
		
			
		}
						
			/**
			 * 图片查看
			 */
	        holder.picture1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context,PictureSkim.class);
					Log.e(TAG, "-------------------" + position);
					intent.putExtra("index", 1);
					intent.putExtra("imgselect", 0);
					intent.putExtra("imgUri", list.get(position).getPicture());
					context.startActivity(intent);
					
				}
			});
	       
	        holder.picture2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context,PictureSkim.class);
					Log.e(TAG, "-------------------" + position);
					intent.putExtra("index", 1);
					intent.putExtra("imgselect", 1);
					intent.putExtra("imgUri", list.get(position).getPicture());
					context.startActivity(intent);
					
				}
			});
			
          holder.picture3.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context,PictureSkim.class);
					Log.e(TAG, "-------------------" + position);
					intent.putExtra("index", 1);
					intent.putExtra("imgselect", 2);
					intent.putExtra("imgUri", list.get(position).getPicture());
					context.startActivity(intent);
					
				}
			});
		
		return convertView;

	}


	/**
	 * 传入后台接到的时间，返回我们需要的格式： 2014年9月20日
	 * @param date 传入时间参数
	 * @return
	 */
	private String backDate(String date){
		//这里包含0位但不包含5即（0，5】
		String a = date.substring(0,4);
		String b = date.substring(5,7);
		String c = date.substring(8,10);
		String datesString = a + "年" + b + "月" + c + "日";
		
		return datesString;		
	}
	
	/**
	 * 传入后台接到的时间，返回我们需要的格式： 2014年9月20日
	 * @param date 传入时间参数
	 * @return
	 */
	private String backDate1(String date){
		//这里包含0位但不包含5即（0，5】
		String a = date.substring(0,4);
		String b = date.substring(5,7);
		String c = date.substring(8,10);
		String datesString = a + "-" + b + "-" + c;
		
		return datesString;		
	}
	
	/**
	 * 传入后台接到的时间，返回我们需要的格式 ：2014-9-20 17：35
	 * @param date
	 * @return
	 */
	private String backDateMin(String date){
		String[] temp = null;
		temp = date.split("T");
		String a = temp[1].substring(0,5);
		String dataString = temp[0] + " " + a;
		return dataString;		
	}
	
	
	

}
