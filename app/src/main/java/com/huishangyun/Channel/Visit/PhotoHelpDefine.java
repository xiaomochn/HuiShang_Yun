package com.huishangyun.Channel.Visit;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.graphics.Bitmap;

import com.huishangyun.Util.FileUtils;

/**
 * 先等比例要缩到要求尺寸，然后再原图压缩
 * @author xsl
 *
 */
public class PhotoHelpDefine {

	public static File compressImage(String oldpath, String newPath,int reqWith,int reqHeight ) throws Exception{  
//		Bitmap image = BitmapFactory.decodeFile(oldpath);   
		Bitmap image = BitmapTools.getBitmap(oldpath, reqWith, reqHeight);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 80;  
        baos.reset();//重置baos即清空baos  
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
       
        byte[] bytes = baos.toByteArray();
		
		File file = null ;
		file = FileUtils.getFileFromBytes(bytes, newPath);
        return file;  
    } 
	
	
	
}
