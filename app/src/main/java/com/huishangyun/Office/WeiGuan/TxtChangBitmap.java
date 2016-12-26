package com.huishangyun.Office.WeiGuan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.huishangyun.Util.DisplayUtil;

/**
 * 将文字转换成图片，头痛脑壳晕
 * @author xiaoxie
 *
 */
public class TxtChangBitmap {

	
public static Bitmap writeImage(Context context,String Str,int sp){
	
	   int sp2 = DisplayUtil.sp2px(context, sp-1);
		
//		int Width = Str.length()*DisplayUtil.sp2px(context, sp);//图片长
//		int width = Layout.getDesiredWidth(Str, 0, Str.length(), paint);
		
		Paint p = new Paint();//创建画笔
		p.setColor(Color.parseColor("#21a5de"));   //画笔颜色
		p.setTextSize(DisplayUtil.sp2px(context, sp));         //画笔粗细
		p.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除.
		Rect rect = new Rect();   
		//返回包围整个字符串的最小的一个Rect区域    
		p.getTextBounds(Str, 0, Str.length(), rect);    
		int Width = rect.width();  
		int Hight = rect.height() + DisplayUtil.sp2px(context,4);
		Bitmap bitmap = Bitmap.createBitmap(Width, Hight, Config.ARGB_8888);//创建画布
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.parseColor("#00000000"));   //背景颜色
	    canvas.drawText(Str, 0, rect.height(), p);
	   
	    return bitmap;
    }


	
}
