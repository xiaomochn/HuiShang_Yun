package com.huishangyun.Channel.Customers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.Channel.Visit.BitmapTools;
import com.huishangyun.Util.Base64Util;
import com.huishangyun.Util.HanderUtil;
import com.huishangyun.Util.L;
import com.huishangyun.Util.PhotoHelp;
import com.huishangyun.model.CallSystemApp;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.task.UpLoadFileTask.OnUpLoadResult;
import com.huishangyun.task.UpLoadImgSignText;
import com.huishangyun.yun.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Customer_Photo extends BaseActivity implements OnUpLoadResult{
	private static final String TAG = "Customer_Photo.java";
	private static final boolean isDebug = false;
	
	private ImageView again, delete;
	private TextView photo_nub;
	private String photoPath = "";
	private ViewPager viewpager; // android-support-v4中的滑动组件
	private List<ImageView> list; // 滑动的图片集合
	private Intent intent;
//	private ProgressDialog pDialog;
	private int oldPosition = 0;
	private String path01 = "";//重拍后照片的完整路径
	private String path01_name = "";//重拍后照片的名称
	private String path_new;
	
	private List<String> path = new ArrayList<String>();
	private List<String> path_serve = new ArrayList<String>();
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HanderUtil.case1:
				File file = (File) msg.obj;
				setImageToNet(file.getAbsolutePath(), file.getName());
				break;
				
			case HanderUtil.case2:
				dismissDialog();
				showCustomToast("上传失败", false);
				break;
				
			default:
				break;
			}
		};
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.photo_customer);
		setResult(RESULT_CANCELED);
		photo_nub = (TextView) findViewById(R.id.photo_nub);
		again = (ImageView) findViewById(R.id.again);
		delete = (ImageView) findViewById(R.id.delete);
		viewpager = (ViewPager) findViewById(R.id.img_show);

		Log.e("----------------", "运行Customer_Photo   onCreate");
		
		init();
		clickListener();
		viewpager.setCurrentItem(CustomerSet.currentItem);
		
		photo_nub.setText(CustomerSet.currentItem + 1 + "/" + CustomerSet.path.size());
	}

	
	/** 
     * 根据指定的图像路径和大小来获取缩略图 
     * @param imagePath 图像的路径 
     * @param width 指定输出图像的宽度 
     * @param height 指定输出图像的高度 
     * @return 生成的缩略图 
     */  
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        
        BitmapFactory.Options options = new BitmapFactory.Options();  
        //只允许读取图片的参数
        options.inJustDecodeBounds = true;  
        //获取这个图片的宽和高，注意此处的bitmap为null  
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        //读取到图片的高宽 
        int h = options.outHeight;
        int w = options.outWidth;
        //计算原图片的宽高比和自己传入的宽高比
        int oldBe = w / h;
        int newBe = width / height;
        // 若传入的比例大说明
        if (newBe > oldBe){
        	width = height * w / h;
        }
        // 适应宽
        else{
        	height = width * h / w;
        }
        
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
//        Log.e(TAG, "加载的图片大小：" + bitmap.getByteCount());
      
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象  ,这里的缩略图用的是另外的一个方法。
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        Log.e(TAG, "宽：" + bitmap.getWidth() + "，高：" + bitmap.getHeight());
        
        return bitmap;
    }


    
	@SuppressLint("ResourceAsColor")
	private void init() {
		Log.e("-----------------", "进入init");
		
		list = new ArrayList<ImageView>();
		for (int i = 0; i < CustomerSet.path.size(); i++) {
			String path = CustomerSet.path.get(i);
			ImageView imageView = new ImageView(this);
						
/*			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);//获取屏幕的宽高
			
			//为了防止加载高像素照片导致的内存溢出，这里使用裁剪了的drawable对象而不直接用照片的Bitmap对象。
//			Drawable drawable = SaoMiaoActivity.resizeImage(BitmapFactory.decodeFile(addImage.getImagepath()),dm.widthPixels,dm.heightPixels-getStatusBarHeight());
//			imageView.setImageDrawable(drawable);
									
//			Bitmap bitmap = getImageThumbnail(addImage.getImagepath(), dm.widthPixels, dm.heightPixels-getStatusBarHeight());//减去获取的状态栏的高度			
			
			Bitmap bitmap = CustomerSet.getLoacalBitmap(path);
			imageView.setImageBitmap(bitmap);
			imageView.setBackgroundColor(Color.BLACK);//R.color.color_hei
									
//			imageView.setScaleType(ScaleType.CENTER_CROP);
*/			
						
			Bitmap bitmap = new BitmapTools().getBitmap(path, 480, 800);						
			imageView.setImageBitmap(bitmap);
			list.add(imageView);						
		}
		/*MyAdapter myAdapter = new MyAdapter();
		myAdapter.notifyDataSetChanged();*/
		viewpager.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
		// 设置一个监听器，当ViewPager中的页面改变时调用
		viewpager.setOnPageChangeListener(new MyPageChangeListener());
		
	}

		
	private void clickListener() {
		again.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				path01_name = getSystemTime() + ".jpg";
				path01 = Constant.SAVE_IMG_PATH + File.separator + path01_name;
				CallSystemApp.callCamera(Customer_Photo.this, path01);
				
			}
		});
		delete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_OK);//启动这个activity要返回结果
				switch (CustomerSet.currentItem) {
				case 0:					
					CustomerSet.path.remove(0);
					CustomerSet.path_serve.remove(0);
					Log.i("==============", "删除后的list大小："+CustomerSet.path.size());
					if (CustomerSet.path.size() > 0) {
						init();
					} else {
						finish();
					}
					break;

				case 1:
					CustomerSet.path.remove(1);	
					CustomerSet.path_serve.remove(1);
					Log.i("==============", "删除后的list大小："+CustomerSet.path.size());
					if (CustomerSet.path.size() > 0) {
						init();
						viewpager.setCurrentItem(0);
					} else {
						finish();
					}
					
//					// 如果删除之后，后面没有图片，则切第一张图片
//					if (CustomerSet.path.size() == 1){
//						CustomerSet.currentItem = 0;
//					}else{
//						// 界面显示第三张图
//					}
					
					break;

				case 2:
					CustomerSet.path.remove(2);
					CustomerSet.path_serve.remove(2);
					Log.i("==============", "删除后的list大小："+CustomerSet.path.size());
					if (CustomerSet.path.size() > 0) {
						init();
						viewpager.setCurrentItem(1);
					} else {
						finish();
					}

					break;
													
				default:
					break;
				}

			}
		});
	}

	/**
	 * 程序在未经同意的情况下销毁调用此方法
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
				
	}
	
	/**
	 * 程序真正销毁了调用此方法
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		Log.e("----------------", "进入Customer_Photo   onRestoreInstanceState");

	}
	
	@Override
	protected void onDestroy() {
		Log.e("----------------", "运行Customer_Photo   onDestroy");
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == CallSystemApp.EXTRA_TAKE_PHOTOS && arg1 == RESULT_OK) {
			
			path_new = Constant.SAVE_IMG_PATH + File.separator + getSystemTime() + ".jpg";
			showNotDialog("正在上传图片...");
			new Thread(){
				public void run() {
					File file;
					try {
						
						file = PhotoHelp.compressImage(path01, path_new);
						Message msg = new Message();
						msg.obj = file;
						msg.what = HanderUtil.case1;
						mHandler.sendMessage(msg);
						
					} catch (Exception e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(HanderUtil.case2);
					}
				};
			}.start();
							
//			setImageToNet(path01, path01_name);
					
		}
	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		
		public void onPageSelected(int position) {
			Log.e("-----------------", "进入MyPageChangeListener");
			Log.i("-------------------","当前的currentItem:"+CustomerSet.currentItem);
			
			
			CustomerSet.currentItem = position;
			oldPosition = position;
			photo_nub.setText(position + 1 + "/" + CustomerSet.path.size());
			Log.e("--------------------", "调用了：onPageSelected");
		}

		public void onPageScrollStateChanged(int arg0) {
			Log.e("--------------------", "调用了：onPageScrollStateChanged");
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
			Log.e("--------------------", "调用了：onPageScrollStateChanged");
		}
	}

	/**
	 * 填充ViewPager页面的适配器
	 */
	private class MyAdapter extends PagerAdapter {
				
		public int getCount() {
			Log.e("-----------------", "进入MyAdapter");
			return list.size();
		}

		public Object instantiateItem(View arg0, int arg1) {
												 			
            ((ViewPager) arg0).addView(list.get(arg1));
                     			
 			return list.get(arg1);
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		public Parcelable saveState() {
			return null;
		}

		public void startUpdate(View arg0) {

		}

		public void finishUpdate(View arg0) {

		}
	}
	
   /**
	 * 传图片到服务器
	 */
	private void setImageToNet(String path, String FileName) {
		//获得系统时间2014-09-20
		//获得系统时间2014-09-20
		SimpleDateFormat  formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date  curDate=new  Date(System.currentTimeMillis());//获取当前时间
		String  time =formatter.format(curDate);
		
		String companyID = preferences.getInt(Content.COMPS_ID, 1016) + "";
		String modulePage = "Member";
		String picData = "";
		try {
			picData = Base64Util.encodeBase64File(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dismissDialog();
			return;
		}
	   UpLoadImgSignText upLoadImgSignText = new UpLoadImgSignText(picData, modulePage, FileName, companyID, time);
	   upLoadImgSignText.setUpLoadResult(Customer_Photo.this);
	   new Thread(upLoadImgSignText).start();
	}

	
	/**
	 * 继承上传图片接口  实现的方法
	 */	
	public void onUpLoadResult(String FileName, String FilePath,
			boolean isSuccess) {
		dismissDialog();
		if (isSuccess) {
			setResult(RESULT_OK);
			Log.e("------------", "当前图片索引：" + viewpager.getCurrentItem());
			switch (viewpager.getCurrentItem()) {
			case 0:
				CustomerSet.path.set(0, path01);
				CustomerSet.path_serve.set(0, FilePath);
				path01 = "";
				path01_name = "";
				init();
				viewpager.setCurrentItem(0);
				break;
				
			case 1:
				CustomerSet.path.set(1, path01);
				CustomerSet.path_serve.set(1, FilePath);
				path01 = "";
				path01_name = "";
				init();
				viewpager.setCurrentItem(1);
				break;
				
			case 2:
				CustomerSet.path.set(2, path01);
				CustomerSet.path_serve.set(2, FilePath);
				path01 = "";
				path01_name = "";
				init();
				viewpager.setCurrentItem(2);
				break;
				
			default:
				break;
			}
//			CustomerSet.path_serve.add(FilePath);			
			showCustomToast("上传成功", true);
			L.e("-------------", "服务端图片地址：" + FilePath);
			
		}else {
			showCustomToast("上传失败", false);
			L.e("-------------" ,"服务端图片上传失败！");
		}
	}
		
	
	/** 
     * 获取状态栏高度  
     * @return 
     */  
    public int getStatusBarHeight(){  
        Class<?> c = null;  
        Object obj = null;  
        java.lang.reflect.Field field = null;  
        int x = 0;  
        int statusBarHeight = 0;  
        try{  
            c = Class.forName("com.android.internal.R$dimen");  
            obj = c.newInstance();  
            field = c.getField("status_bar_height");  
            x = Integer.parseInt(field.get(obj).toString());  
            statusBarHeight = getResources().getDimensionPixelSize(x);  
            return statusBarHeight;  
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }  
        return statusBarHeight;  
    }  
}
