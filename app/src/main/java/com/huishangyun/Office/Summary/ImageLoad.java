package com.huishangyun.Office.Summary;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.huishangyun.Util.L;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.huishangyun.App.MyApplication;

/**
 * 图片下载工具类
 * @author xsl
 *
 */
public class ImageLoad {
	private  DisplayImageOptions options;
	
	/**
	 * 显示图片
	 * @param context 上下文对象
	 * @param Url 图片下载地址
	 * @param imageView 要显示的控件
	 * @param defaultImage 默认图片：R.drawable.XXX
	 * @param angle 圆角角度 ，如果不需要圆角填0，显示圆填90
	 * @param isShowProgress 是否显示进度条
	 */
	@SuppressWarnings("deprecation")
	public  void displayImage(final Context context,String Url,final ImageView imageView,int defaultImage,int angle,final boolean isShowProgress){
		
		//displayer(new RoundedBitmapDisplayer(angle))会导致图片乱显示，所以分三种情况
		if (isShowProgress) {
			options = new DisplayImageOptions.Builder()
			.cacheInMemory(false)//设置下载的图片是否缓存在内存中 
			.cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中 
			.considerExifParams(true)//是否考虑JPEG图像EXIF参数（旋转，翻转）
			.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型// 图片多设置这个
			.imageScaleType(ImageScaleType.EXACTLY)
//			.decodingOptions(decodingOptions)//设置图片的解码配置  
			.resetViewBeforeLoading(true)
			.displayer(new FadeInBitmapDisplayer(300))
			.build();
		}else {
	
		if (angle==0) {
			options = new DisplayImageOptions.Builder()
			.showImageOnLoading(defaultImage)//设置图片在下载期间显示的图片 
			.showImageForEmptyUri(defaultImage)//设置图片Uri为空或是错误的时候显示的图片  
			.showImageOnFail(defaultImage)//设置图片加载/解码过程中错误时候显示的图片
			.imageScaleType(ImageScaleType.EXACTLY)
			.cacheInMemory(false)//设置下载的图片是否缓存在内存中 
			.cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中 
			.resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位  
			.considerExifParams(true)//是否考虑JPEG图像EXIF参数（旋转，翻转）
			.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型// 图片多设置这个
			.build();
		}else {
			//下面这种方法导致图片显示不正常
		    options = new DisplayImageOptions.Builder()
		    .showImageOnLoading(defaultImage) //设置图片在下载期间显示的图片  
		    .showImageForEmptyUri(defaultImage)//设置图片Uri为空或是错误的时候显示的图片  
		    .showImageOnFail(defaultImage)  //设置图片加载/解码过程中错误时候显示的图片
		    .cacheInMemory(false)//设置下载的图片是否缓存在内存中  
		    .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中  
		    .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
		    .imageScaleType(ImageScaleType.EXACTLY)//设置图片以如何的编码方式显示  
		    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型// 图片多设置这个
		    //.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
		    //设置图片加入缓存前，对bitmap进行设置  
		    //.preProcessor(BitmapProcessor preProcessor)  
		    .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位  
		    .displayer(new RoundedBitmapDisplayer(angle))//是否设置为圆角，弧度为多少  
//		    .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间  
//		    .displayer(new SimpleBitmapDisplayer())
		    .build();//构建完成  
		}
	}
	
	    ImageLoader.getInstance().displayImage(Url, imageView, options, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				// TODO Auto-generated method stub
//				L.e("开始加载图片");
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				// TODO Auto-generated method stub
				
				L.e("图片加载失败！");
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				// TODO Auto-generated method stub
				
//				L.e("图片加载成功！");
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				// TODO Auto-generated method stub
//				L.e("图片加载取消");
			}
		}, new ImageLoadingProgressListener() {
			
			@Override
			public void onProgressUpdate(String arg0, View arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				if (isShowProgress) {
					if (arg2 == 0) {
						//加载前取消
						MyApplication.getInstance().showDialog(context, false, "Loading");
						MyApplication.getInstance().showDialog(context, true, "Loading");
					}else if (arg2 == arg3) {
						MyApplication.getInstance().showDialog(context, false, "Loading");
					}
				}
//				L.e("-----------总百分比:" + arg3);
//				L.e("-----------百分比:" + arg2);
				L.e("进度：" + ((arg2*1.0)/arg3)*100 + " %");
			}
		});
	}

}
