package com.huishangyun.Office.Attendance;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * 画扇型统计图工具类
 * @author xsl
 *
 */
public class FanTypeView extends View {
	
	
	private List<OfficeList> list;
	private Integer Days;//总天数
	private float Late;//迟到天数
	private float Early;//早退
	private float Leave;//请假
	private float Travel;//出差
	private float Normal;//正常出勤天数
	private float LatePercent;//迟到百分比
	private float EarlyPercent;//早退百分比
	private float LeavePercent;//请假百分比
	private float TravelPercent;//出差百分比
	private float NormalPercent;//正常出勤百分比
	public FanTypeView(Context context, List<OfficeList> list){
		super(context);
		this.list = list;
	}
	
	/**
	 * 处理数据
	 */
	private void manageData(){
		
		if (list.size() != 0) {
			Days = list.get(0).getDays();
			Late = list.get(0).getLate();
			Early = list.get(0).getEarly();
			Leave = list.get(0).getLeave();
			Travel = list.get(0).getTravel();
			Normal = list.get(0).getNormal();
			//测试数据
//			Days = 30;
//			Late = 5;
//			Early = 2;
//			Leave = 3;
//			Travel = 2;
//			Normal = 17;
			LatePercent = Float.parseFloat(new DecimalFormat("0").format((Late*1.0)/Days*360));
			EarlyPercent = Float.parseFloat(new DecimalFormat("0").format((Early*1.0)/Days*360));
			LeavePercent = Float.parseFloat(new DecimalFormat("0").format((Leave*1.0)/Days*360));
			TravelPercent = Float.parseFloat(new DecimalFormat("0").format((Travel*1.0)/Days*360));
			NormalPercent = Float.parseFloat(new DecimalFormat("0").format((Normal*1.0)/Days*360));
		}else {
			LatePercent = 0;
			EarlyPercent = 0;
			LeavePercent = 0;
			TravelPercent = 0;
			NormalPercent = 0;
		}
		
		
		
		
	}
	
	/**
	 * 画图
	 */
	protected void onDraw(Canvas canvas) {
		manageData();
	
		/**
		 * 方法 说明 drawRect 绘制矩形 drawCircle 绘制圆形 drawOval 绘制椭圆 drawPath 绘制任意多边形 
		 * drawLine 绘制直线 drawPoin 绘制点 
		 */  
		// 创建五支画笔
		Paint p1 = new Paint();
		p1.setColor(0xff21a5de);// 设置实际出勤颜色
		p1.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除.
		
		Paint p2 = new Paint();
		p2.setColor(0xff4ed5c7);// 设置出差颜色
		p2.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除.
		
		Paint p3 = new Paint();
		p3.setColor(0xffd44865);// 设置请假颜色
		p3.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除.
		
		Paint p4 = new Paint();
		p4.setColor(0xffb8da8d);// 设置迟到颜色
		p4.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除.
		
		Paint p5 = new Paint();
		p5.setColor(0xfff7a67b);// 设置早退颜色
		p5.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除.
		
		Paint p6 = new Paint();
		p6.setColor(0xffffffff);// 设置百分比颜色
		p6.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除.
		
		
		Paint p7 = new Paint();
		p7.setColor(0xffffffff);// 设置百分比颜色
		p7.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除.
		
		int mXCenter = getWidth() / 2;//获取屏幕宽度
		int mYCenter = getHeight() / 2;//获取屏幕高度
        //屏幕宽高
		int hight = MainAttendanceActivity.H;
		int width = MainAttendanceActivity.W;
		//三目运算取较小值
		int wh = (width < hight) ? hight:width;
		int mXY = (int) ((wh/10.0)*2);
	   //画扇型
	   RectF oval1 = new RectF(mXCenter-mXY, 0, mXCenter+mXY, mXY*2);// 设置个新的长方形，扫描测量  
	   canvas.drawArc(oval1, 200, NormalPercent, true, p1); 
//	   canvas.drawText("50%", mXCenter+90, 150, p6);  //标记百分比
	   
	   RectF oval2 = new RectF(mXCenter-mXY, 0, mXCenter+mXY, mXY*2);
	   canvas.drawArc(oval2, 200 + NormalPercent , TravelPercent, true, p2);
	   
	   RectF oval3 = new RectF(mXCenter-mXY, 0, mXCenter+mXY, mXY*2);
	   canvas.drawArc(oval3, 200 + NormalPercent + TravelPercent, LeavePercent, true, p3);
	   
	   RectF oval4 = new RectF(mXCenter-mXY, 0, mXCenter+mXY, mXY*2);
	   canvas.drawArc(oval4, 200 + NormalPercent + TravelPercent + LeavePercent, LatePercent, true, p4);
	   
	   RectF oval5 = new RectF(mXCenter-mXY, 0, mXCenter+mXY, mXY*2);
	   canvas.drawArc(oval5, 200 + NormalPercent + TravelPercent + LeavePercent + LatePercent, EarlyPercent, true, p5);
	   
	   RectF oval6 = new RectF(mXCenter-mXY, 0, mXCenter+mXY, mXY*2);
	   canvas.drawArc(oval6, 200 + NormalPercent + TravelPercent + LeavePercent + LatePercent + EarlyPercent,
			 360 - (NormalPercent + TravelPercent + LeavePercent + LatePercent + EarlyPercent), true, p7);


	}

}
