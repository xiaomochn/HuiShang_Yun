package com.huishangyun.Channel.Visit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huishangyun.Activity.BaseActivity;
import com.huishangyun.PhotoView.AttacherImageView;
import com.huishangyun.Util.L;
import com.huishangyun.model.Constant;
import com.huishangyun.model.Content;
import com.huishangyun.yun.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



/***
 * 
 * 查看网络大图
 * 
 * @author xsl
 * 
 * 
 * 
 */
@SuppressLint("NewApi")
public class PictureSkim extends BaseActivity {

	private static final String TAG = null;
	private ViewPagerFixed view_pager;
	private LayoutInflater inflater;
	private ImageView image;
	private View item ;
	private MyAdapter adapter ;
//	private ImageView[] indicator_imgs = new ImageView[7];//存放引到图片数组
	String img;
	List<String> Imagelist = new ArrayList<String>();
	List<String> ImagelistSmall = new ArrayList<String>();
	private TextView photo_nub;//照片页码
	private ImageView again;//重拍
	private ImageView delete;//删除
	private int Index =-1;
	private int imgselect;
	public ProgressBar progressBar1;
	private ProgressBar progressdefine;
	private int Company_ID;
	private DisplayImageOptions options;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_visit_skim_photo);
		Company_ID = preferences.getInt(Content.COMPS_ID, 1016);
		initOptions();
		init();
	}
	
	/**
	 * 初始化options
	 */
	private void initOptions(){
		options = new DisplayImageOptions.Builder()
		.cacheInMemory(false)//设置下载的图片是否缓存在内存中 
		.cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中 
		.considerExifParams(true)//是否考虑JPEG图像EXIF参数（旋转，翻转）
		.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型// 图片多设置这个
		.imageScaleType(ImageScaleType.EXACTLY)
//		.decodingOptions(decodingOptions)//设置图片的解码配置  
		.resetViewBeforeLoading(true)
		.displayer(new FadeInBitmapDisplayer(300))
		.build();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ImageLoader.getInstance().clearMemoryCache();
		L.e("清除缓存" + ImageLoader.getInstance().getMemoryCache());
	}
	
	/**
	 * 控件初始化
	 */
	private void init(){
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		photo_nub = (TextView) findViewById(R.id.photo_nub);
		again = (ImageView) findViewById(R.id.again);
		delete = (ImageView) findViewById(R.id.delete);
		again.setVisibility(View.INVISIBLE);
		delete.setVisibility(View.INVISIBLE);
		//接收传值
		Intent intent = getIntent();
		Index = intent.getIntExtra("index", -1);
		img  = intent.getStringExtra("imgUri");
		imgselect = intent.getIntExtra("imgselect", 0);
		String[] temp = null;
		temp = img.split("#");
		for (int i = 0; i < temp.length; i++) {
			
			if (Index==1) {
				
				Imagelist.add(Constant.pathurl+ Company_ID + "/Visit/" + temp[i]);
				ImagelistSmall.add(Constant.pathurl+ Company_ID + "/Visit/100x100/" + temp[i]);
				
			}else if (Index==2) {
				
				Imagelist.add(Constant.pathurl+ Company_ID + "/Display/" + temp[i]);
				ImagelistSmall.add(Constant.pathurl+ Company_ID + "/Display/100x100/" + temp[i]);
				
			}else if (Index==3) {
				
				Imagelist.add(Constant.pathurl+ Company_ID + "/Competition/" + temp[i]);
				ImagelistSmall.add(Constant.pathurl+ Company_ID + "/Competition/100x100/" + temp[i]);
			}else if (Index==4) {
				Imagelist.add(Constant.pathurl+ Company_ID + "/Travel/" + temp[i]);
				ImagelistSmall.add(Constant.pathurl+ Company_ID + "/Travel/100x100/" + temp[i]);
			}else if (Index==5) {
				Imagelist.add(Constant.pathurl+ Company_ID + "/Action/" + temp[i]);
				ImagelistSmall.add(Constant.pathurl+ Company_ID + "/Action/100x100/" + temp[i]);
				again.setVisibility(View.INVISIBLE);
				delete.setVisibility(View.INVISIBLE);
				photo_nub.setVisibility(View.VISIBLE);
			}
			
		}
		
		
		
		
		
		photo_nub.setText(1 + "/" + Imagelist.size());
		view_pager = (ViewPagerFixed) findViewById(R.id.img_show);
		List<View> list = new ArrayList<View>();
		inflater = LayoutInflater.from(this);

		/**
		 * 创建多个item （每一条viewPager都是一个item）
		 * 从服务器获取完数据（如文章标题、url地址） 后，再设置适配器
		 */
		for (int i = 0; i < temp.length; i++) {
			item = inflater.inflate(R.layout.activity_visit_viewpager_item, null);
//			ImageView imageView = (ImageView) item.findViewById(R.id.image);
//			imageView.setBackgroundColor(Color.parseColor("#e60000"));
			list.add(item);
		}
		
		//创建适配器， 把组装完的组件传递进去
		adapter = new MyAdapter(list);
		view_pager.setAdapter(adapter);
		
		//绑定动作监听器：如翻页的动画
		view_pager.setOnPageChangeListener(new MyListener());
		
		//设置从那张开始显示
		view_pager.setCurrentItem(imgselect);
		
		
	}

	
	
	
	
	/**
	 * 适配器，负责装配 、销毁  数据  和  组件 。
	 */
	private class MyAdapter extends PagerAdapter {

		private List<View> mList;

		
		private AsyncImageLoader asyncImageLoader;
		
		public MyAdapter(List<View> list) {
			mList = list;
			asyncImageLoader = new AsyncImageLoader();  
		}

		
		
		/**
		 * Return the number of views available.
		 */
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		
		/**
		 * Remove a page for the given position.
		 * 滑动过后就销毁 ，销毁当前页的前一个的前一个的页！
		 * instantiateItem(View container, int position)
		 * This method was deprecated in API level . Use instantiateItem(ViewGroup, int)
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView(mList.get(position));
			
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0==arg1;
		}

		
		/**
		 * Create the page for the given position.
		 */
		@Override
		public Object instantiateItem(final ViewGroup container, final int position) {
			
			View view = mList.get(position);
			image = ((AttacherImageView) view.findViewById(R.id.image));
			progressdefine = (ProgressBar) view.findViewById(R.id.progressdefine);
			image.setTag(Imagelist.get(position));
		    ImageLoader.getInstance().displayImage(Imagelist.get(position), image, options, new ImageLoadingListener() {
					
					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub
//						L.e("开始加载图片");
					}
					
					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
						// TODO Auto-generated method stub
						progressdefine.setVisibility(View.GONE);
						L.e("图片加载失败！");
					}
					
					@Override
					public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
						// TODO Auto-generated method stub
						
//						L.e("图片加载成功！");
						progressdefine.setVisibility(View.GONE);
					}
					
					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub
//						L.e("图片加载取消");
						progressdefine.setVisibility(View.GONE);
					}
				}, new ImageLoadingProgressListener() {
					
					@Override
					public void onProgressUpdate(String arg0, View arg1, int arg2, int arg3) {
						// TODO Auto-generated method stub
						
							if (arg2 >= 0 && arg2 < arg3) {
								//加载
								progressdefine.setVisibility(View.VISIBLE);
							}else if (arg2 == arg3) {
								progressdefine.setVisibility(View.GONE);
							}
						}

//						L.e("进度：" + ((arg2*1.0)/arg3)*100 + " %");
						
					
				});
			progressBar1.setVisibility(View.INVISIBLE);
			container.removeView(mList.get(position));
			container.addView(mList.get(position));
			return mList.get(position);
		}

	}
	
	
	/**
	 * 动作监听器，可异步加载图片
	 *
	 */
	private class MyListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int state) {
			// TODO Auto-generated method stub
			
		}

		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int position) {
			photo_nub.setText(position+1 + "/" + Imagelist.size());
//			image.setTag(Imagelist.get(position));
//			new BitmapTools().downLoadImage(image, 480, 800, false,PictureSkim.this);
			
		}
		
		
	}
	
	

	/**
	 * 异步加载图片
	 */
	static class AsyncImageLoader {

		// 软引用，使用内存做临时缓存 （程序退出，或内存不够则清除软引用）
		private HashMap<String, SoftReference<Drawable>> imageCache;

		public AsyncImageLoader() {
			imageCache = new HashMap<String, SoftReference<Drawable>>();
		}

		/**
		 * 定义回调接口
		 */
		public interface ImageCallback {
			public void imageLoaded(Drawable imageDrawable, String imageUrl);
		}

		
		/**
		 * 创建子线程加载图片
		 * 子线程加载完图片交给handler处理（子线程不能更新ui，而handler处在主线程，可以更新ui）
		 * handler又交给imageCallback，imageCallback须要自己来实现，在这里可以对回调参数进行处理
		 *
		 * @param imageUrl ：须要加载的图片url
		 * @param imageCallback：
		 * @return
		 */
		public Drawable loadDrawable(final String imageUrl,
				final ImageCallback imageCallback) {
			
			//如果缓存中存在图片  ，则首先使用缓存
			if (imageCache.containsKey(imageUrl)) {
				SoftReference<Drawable> softReference = imageCache.get(imageUrl);
				Drawable drawable = softReference.get();
				if (drawable != null) {
					imageCallback.imageLoaded(drawable, imageUrl);//执行回调
					return drawable;
				}
			}

			/**
			 * 在主线程里执行回调，更新视图
			 */
			final Handler handler = new Handler() {
				public void handleMessage(Message message) {
					imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
				}
			};

			
			/**
			 * 创建子线程访问网络并加载图片 ，把结果交给handler处理
			 */
			new Thread() {
				@Override
				public void run() {
					Drawable drawable = loadImageFromUrl(imageUrl);
					// 下载完的图片放到缓存里
					imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
					Message message = handler.obtainMessage(0, drawable);
					handler.sendMessage(message);
				}
			}.start();
			
			return null;
		}

		
		/**
		 * 下载图片  （注意HttpClient 和httpUrlConnection的区别）
		 */
		public Drawable loadImageFromUrl(String url) {

			try {
				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000*15);
				HttpGet get = new HttpGet(url);
				HttpResponse response;

				response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					Drawable d = Drawable.createFromStream(entity.getContent(),
							"src");

					return d;
				} else {
					return null;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		//清除缓存
		public void clearCache() {

			if (this.imageCache.size() > 0) {

				this.imageCache.clear();
			}

		}

	}
	
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		super.onBackPressed();
	}


	
	
	

}
