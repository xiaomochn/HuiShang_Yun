package com.huishangyun.Channel.Visit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

public class ShowPoritionPictureActivity {
	
	public ShowPoritionPictureActivity(){
		
	}
	
	
	/**
	 * 获得截取图片中间固定尺寸后的图片
	 * @return
	 */
	public Bitmap getBitmap(Bitmap bitmap){
	
//	Bitmap picRes; 
    
    Bitmap showPic; 
     
    //获取原图片的宽和高  
    int picWidth; 
    int picHeight; 
     
    PoritionView poritonView = null; 
    

    
    DisplayMetrics dm = new DisplayMetrics(); 
         
        // 得到屏幕的长和宽  
        int screenWidth = dm.widthPixels;                //水平分辨率  
        int screenHeight = dm.heightPixels;              //垂直分辨率  
         
         
//       picRes = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl); 
        // 得到图片的长和宽  
        picWidth = bitmap.getWidth(); 
        picHeight = bitmap.getHeight(); 
         
        // 计算缩放率，新尺寸除原始尺寸  
        float scaleWidth = ((float) screenWidth ) / picWidth; 
        float scaleHeight = ((float) screenHeight ) / picHeight; 
         
        // 创建操作图片用的matrix对象  
        Matrix matrix = new Matrix(); 
        // 缩放图片动作  
        matrix.postScale(scaleWidth, scaleHeight); 
        // 新得到的图片是原图片经过变换填充到整个屏幕的图片  
//        Bitmap picNewRes = Bitmap.createBitmap(picRes, 0, 0,picWidth, picHeight, matrix, true);
        
        //从图片中间剪切要求的大小
        Bitmap picNewRes = Bitmap.createBitmap(bitmap, 0, 0,picWidth, picHeight, matrix, true);
        
        // bitmap = Bitmap.createBitmap(400, 480, Bitmap.Config.ARGB_8888);  
        // canvas=new Canvas();           
        // canvas.setBitmap(bitmap);   
         
        showPic = Bitmap.createBitmap(picNewRes, screenWidth/2-50, screenHeight/2-50, 100, 100); 
         
        poritonView.setBitmapShow(showPic, screenWidth/2-50, screenHeight/2-50); 
//        setContentView(poritonView); 
    
      return showPic;
	}


//新建PoritionView类代码：





 
public class PoritionView extends View { 
    private Bitmap showPic = null; 
    private int startX = 0; 
    private int startY = 0; 
    public PoritionView(Context context) { 
        super(context); 
        // TODO Auto-generated constructor stub  
    } 
 
    @Override 
    protected void onDraw(Canvas canvas) { 
        // TODO Auto-generated method stub  
        super.onDraw(canvas); 
        canvas.drawBitmap(showPic, startX, startY, null); 
    } 
    public void setBitmapShow(Bitmap b, int x, int y) 
    { 
        showPic = b; 
        startX = x; 
        startY = y; 
    } 
    
    
} 


}
