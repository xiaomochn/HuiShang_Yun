package com.huishangyun.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class BitmapUtils {
	 /**
	
	         * 
	
	         * @param b Bitmap
	
	         * <a href="\"http://www.eoeandroid.com/home.php?mod=space&uid=7300\"" target="\"_blank\"">@return</a> 图片存储的位置
	
	         * @throws FileNotFoundException 
	
	         */
	
			private static final int CACHE_SIZE = 100;
			private static final LinkedHashMap<String, Bitmap> imageMap = new LinkedHashMap<String, Bitmap>(
					CACHE_SIZE, .75F, true) {
				private static final long serialVersionUID = 1L;
			
				protected boolean removeEldestEntry(Map.Entry<String, Bitmap> eldest) {
					return size() > CACHE_SIZE;
				}
			};
	
	        public static Bitmap saveImg(Bitmap b,String name) throws Exception{
	        		Bitmap bitmap;
	                String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"suokete/img/";
	
	                File mediaFile = new File(path + File.separator + name + ".jpg");
	                bitmap = (Bitmap) imageMap.get(path);
	                if (bitmap != null) {
	                	L.d("获取缓存");
						return bitmap;
					}
	                if(mediaFile.exists()){
	                	 bitmap = BitmapFactory.decodeFile(mediaFile.getAbsolutePath());
	                	 imageMap.put(path, bitmap);
	                	 L.d("获取文件");
	                     return bitmap;
	
	                }
	
	                if(!new File(path).exists()){
	
	                        new File(path).mkdirs();
	
	                }
	 
	                mediaFile.createNewFile();
	
	                FileOutputStream fos = new FileOutputStream(mediaFile);
	
	                b.compress(Bitmap.CompressFormat.PNG, 100, fos);
	
	                fos.flush();
	
	                fos.close();
	
	                b.recycle();
	
	                b = null;
	
	                System.gc();
	                bitmap = BitmapFactory.decodeFile(mediaFile.getPath());
	                L.d("获取文件2");
	                imageMap.put(path, bitmap);
	                return bitmap;
	
	        }
}
