package com.huishangyun.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PhotoHelp {
	public static File compressImage(String oldpath, String newPath) throws Exception{  
		Bitmap image = BitmapFactory.decodeFile(oldpath);   
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 30;  
        //while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
        baos.reset();//重置baos即清空baos  
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
//            options -= 10;//每次都减少10
//            if (options == 10) {
//				break;
//			}
       // }  
        /*ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
*/      byte[] bytes = baos.toByteArray();
		
		File file = null ;
		file = FileUtils.getFileFromBytes(bytes, newPath);
        return file;  
    } 
}
