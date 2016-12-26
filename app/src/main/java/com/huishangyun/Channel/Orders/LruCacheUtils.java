package com.huishangyun.Channel.Orders;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.huishangyun.Util.L;

public class LruCacheUtils {
	
	/**
	 * 内存缓存技术。在Android中，有一个叫做LruCache类专门用来做图片缓存处理的。
	 * 它有一个特点，当缓存的图片达到了预先设定的值的时候，那么近期使用次数最少的图片就会被回收掉
	 * 步骤：（1）要先设置缓存图片的内存大小，我这里设置为手机内存的1/8,        
                （2）LruCache里面的键值对分别是URL和对应的图片
                 （3）重写了一个叫做sizeOf的方法，返回的是图片数量。
	 */	
	private LruCache<String, Bitmap> mMemoryCache;
	/**
	 * 手机内存的获取方式
	 */
	private int MAXMEMONRY = (int) (Runtime.getRuntime() .maxMemory() / 1024);

	private LruCacheUtils() {

         if (mMemoryCache == null)

             mMemoryCache = new LruCache<String, Bitmap>(MAXMEMONRY / 8) {
                 protected int sizeOf(String key, Bitmap bitmap) {
                     // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                    return bitmap.getRowBytes() * bitmap.getHeight() / 1024;

                 }

                 protected void entryRemoved(boolean evicted, String key,
                         Bitmap oldValue, Bitmap newValue) {

                    L.v("hard cache is full , push to soft cache");
               
                 }
             };
	}
	
	
	/**
	 *  移除和清除缓存是必须要做的事，因为图片缓存处理不当就会报内存溢出，所以一定要引起注意。
	 */
   //清空缓存
	public void clearCache() {
         if (mMemoryCache != null) {

             if (mMemoryCache.size() > 0) {

                 L.d("CacheUtils：mMemoryCache.size() " + mMemoryCache.size());

                 mMemoryCache.evictAll();

                 L.d("CacheUtils：mMemoryCache.size()" + mMemoryCache.size());

             }

             mMemoryCache = null;
         }
     }

	//添加图片到缓存
     public synchronized void addBitmapToMemoryCache(String key, Bitmap bitmap) {

         if (mMemoryCache.get(key) == null) {

             if (key != null && bitmap != null)

                 mMemoryCache.put(key, bitmap);

         } else

             L.i("the res is aready exits");
     }


     //缓存中取得图片
     public synchronized Bitmap getBitmapFromMemCache(String key) {

         Bitmap bm = mMemoryCache.get(key);

         if (key != null) {

             return bm;

         }
         return null;
     }



     /**
      * 移除缓存
      * @param key
      */
     public synchronized void removeImageCache(String key) {

         if (key != null) {

             if (mMemoryCache != null) {

                 Bitmap bm = mMemoryCache.remove(key);

                 if (bm != null)

                     bm.recycle();

             }
         }
     }


}
