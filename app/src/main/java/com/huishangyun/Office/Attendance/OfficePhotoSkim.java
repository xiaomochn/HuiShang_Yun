package com.huishangyun.Office.Attendance;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Channel.Visit.BitmapTools;
import com.huishangyun.Channel.Visit.ViewPagerFixed;
import com.huishangyun.Office.Businesstrip.AddBusinessTrip;
import com.huishangyun.Office.Businesstrip.BusinessTripDetail;
import com.huishangyun.yun.R;


/**
 * sd卡图片预览
 * @author xsl
 *
 */
public class OfficePhotoSkim extends BaseActivity {
	private ImageView again, delete;
	private TextView photo_nub;//页码标识
	private ViewPagerFixed viewpager; // android-support-v4中的滑动组件
	private MyAdapter mAdapter;
	private int img_postion;// 当前显示图片位置
	private List<View> list = new ArrayList<View>(); // 滑动的图片集合
	private LayoutInflater inflater;
	private View item;
	private int index;// 接收的指数，确定那个界面跳转过来
	private int imgselect;//控制图片第几张开始显示

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.photo_customer);
		Intent intent = getIntent();
		index = intent.getIntExtra("index", -1);
		imgselect = intent.getIntExtra("imgselect", 0);
		init();

	}

	/**
	 * 控件初始化
	 */
	private void init() {

		again = (ImageView) findViewById(R.id.again);
		delete = (ImageView) findViewById(R.id.delete);
		photo_nub = (TextView) findViewById(R.id.photo_nub);
		
		//设置初始页码显示效果
		if (index == 1) {
			photo_nub.setText(1 + "/" + MainAttendanceActivity.Img_List.size());
		}else if (index == 2) {
			photo_nub.setText(1 + "/" + AddBusinessTrip.Img_List.size());
		}else if (index == 3) {
			photo_nub.setText(1 + "/" + BusinessTripDetail.Img_List.size());
		}else if (index == 4) {
			photo_nub.setText(1 + "/" + BusinessTripDetail.Img_List1.size());
		}
		
		inflater = LayoutInflater.from(this);
		if (index == 1) {
			for (int i = 0; i < MainAttendanceActivity.Img_List.size(); i++) {
				item = inflater.inflate(R.layout.activity_visit_viewpager_item,
						null);
				list.add(item);
			}
		} else if (index == 2) {
			for (int i = 0; i < AddBusinessTrip.Img_List.size(); i++) {
				item = inflater.inflate(R.layout.activity_visit_viewpager_item,
						null);
				list.add(item);
			}
		} else if (index == 3) {
			for (int i = 0; i < BusinessTripDetail.Img_List.size(); i++) {
				item = inflater.inflate(R.layout.activity_visit_viewpager_item,
						null);
				list.add(item);
			}
		}else if (index == 4) {
			for (int i = 0; i < BusinessTripDetail.Img_List1.size(); i++) {
				item = inflater.inflate(R.layout.activity_visit_viewpager_item,
						null);
				list.add(item);
			}
		}
		viewpager = (ViewPagerFixed) findViewById(R.id.img_show);
		mAdapter = new MyAdapter(list);
		viewpager.setAdapter(mAdapter);
		viewpager.setOnPageChangeListener(new MyListener());
		again.setOnClickListener(new ButtonListener());
		delete.setOnClickListener(new ButtonListener());
		
		
		//设置从那张开始显示
		viewpager.setCurrentItem(imgselect);
	}

	/**
	 * 单击事件处理
	 * 
	 * @author xsl
	 * 
	 */
	public class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.again:
				if (index == 1) {
					MainAttendanceActivity.Img_List.remove(img_postion);
					MainAttendanceActivity.pList.remove(img_postion);
				} else if (index == 2) {
					AddBusinessTrip.Img_List.remove(img_postion);
					AddBusinessTrip.pList.remove(img_postion);
				} else if (index == 3) {
					BusinessTripDetail.Img_List.remove(img_postion);
					BusinessTripDetail.pList.remove(img_postion);
				}else if (index == 4) {
					BusinessTripDetail.Img_List1.remove(img_postion);
					BusinessTripDetail.pList1.remove(img_postion);
				}
               
				setResult(RESULT_OK);//设置返回码
				finish();

				break;
			case R.id.delete:
				// 删除图片(包括sd中的图片)，通知适配器改变重新显示
				// File f = new File(MainAttendanceActivity.Img_List.get(img_postion));
				// f.delete();
				if (index == 1) {
					MainAttendanceActivity.Img_List.remove(img_postion);
					MainAttendanceActivity.pList.remove(img_postion);
				} else if (index == 2) {
					AddBusinessTrip.Img_List.remove(img_postion);
					AddBusinessTrip.pList.remove(img_postion);
				} else if (index == 3) {
					BusinessTripDetail.Img_List.remove(img_postion);
					BusinessTripDetail.pList.remove(img_postion);
				}else if (index == 4) {
					BusinessTripDetail.Img_List1.remove(img_postion);
					BusinessTripDetail.pList1.remove(img_postion);
				}

				list.clear();
				imageChange();

				break;

			default:
				break;
			}
		}

	}

	/**
	 * 页面滑动监听
	 * 
	 * @author xsl
	 * 
	 */
	private class MyListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			img_postion = arg0;
		}

		@Override
		public void onPageSelected(int position) {
			
			if (index == 1) {
				photo_nub.setText(position+1 + "/" + MainAttendanceActivity.Img_List.size());
			}else if (index == 2) {
				photo_nub.setText(position+1 + "/" + AddBusinessTrip.Img_List.size());
			}else if (index == 3) {
				photo_nub.setText(position+1 + "/" + BusinessTripDetail.Img_List.size());
			}else if (index == 4) {
				photo_nub.setText(position+1 + "/" + BusinessTripDetail.Img_List1.size());
			}
			

		}

	}

	/**
	 * 动态改变图片浏览显示
	 */
	private void imageChange() {
		if (MainAttendanceActivity.Img_List.size() > 0
				|| AddBusinessTrip.Img_List.size() > 0
				|| BusinessTripDetail.Img_List.size() > 0) {
			if (index == 1) {
				for (int i = 0; i < MainAttendanceActivity.Img_List.size(); i++) {
					item = inflater.inflate(
							R.layout.activity_visit_viewpager_item, null);
					list.add(item);
				}
			} else if (index == 2) {
				for (int i = 0; i < AddBusinessTrip.Img_List.size(); i++) {
					item = inflater.inflate(
							R.layout.activity_visit_viewpager_item, null);
					list.add(item);
				}

			} else if (index == 3) {
				for (int i = 0; i < BusinessTripDetail.Img_List.size(); i++) {
					item = inflater.inflate(
							R.layout.activity_visit_viewpager_item, null);
					list.add(item);
				}
			}else if (index == 4) {
				for (int i = 0; i < BusinessTripDetail.Img_List1.size(); i++) {
					item = inflater.inflate(
							R.layout.activity_visit_viewpager_item, null);
					list.add(item);
				}
			}
			viewpager = (ViewPagerFixed) findViewById(R.id.img_show);
			mAdapter = new MyAdapter(list);
			viewpager.setAdapter(mAdapter);
			viewpager.setOnPageChangeListener(new MyListener());
		} else {
			finish();
		}
	}

	/**
	 * viewpager适配器
	 * 
	 * @author xsl
	 * 
	 */
	public class MyAdapter extends PagerAdapter {

		private List<View> mPaths;

		public MyAdapter(List<View> list) {
			// TODO Auto-generated constructor stub
			mPaths = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mPaths.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			// TODO Auto-generated method stub
			return view == (View) obj;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView image;
			View view = mPaths.get(position);
			image = (ImageView) view.findViewById(R.id.image);


			// 为了防止加载高像素照片导致的内存溢出，这里使用裁剪了的drawable对象而不直接用照片的Bitmap对象。
			// 这种方法可以解决图片卡的问题
			if (index == 1) {
				Bitmap bitmap = new BitmapTools().getBitmap(MainAttendanceActivity.Img_List.get(position), 480, 856);
				// 载入bitmap
				image.setImageBitmap(bitmap);
			} else if (index == 2) {
				Bitmap bitmap = new BitmapTools().getBitmap(AddBusinessTrip.Img_List.get(position), 240, 320);
				// 载入bitmap
				image.setImageBitmap(bitmap);
			} else if (index == 3) {
				Bitmap bitmap = new BitmapTools().getBitmap(BusinessTripDetail.Img_List.get(position), 240, 320);
				
				// 载入bitmap
				image.setImageBitmap(bitmap);
			}else if (index == 4) {
              Bitmap bitmap = new BitmapTools().getBitmap(BusinessTripDetail.Img_List1.get(position), 240, 320);
				
				// 载入bitmap
				image.setImageBitmap(bitmap);
			}
			container.removeView(mPaths.get(position));
			container.addView(mPaths.get(position));

			return mPaths.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	

}