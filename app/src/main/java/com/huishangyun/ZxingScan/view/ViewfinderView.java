/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huishangyun.ZxingScan.view;

import java.util.Collection;
import java.util.HashSet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.huishangyun.ZxingScan.camera.CameraManager;
import com.huishangyun.yun.R;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {
	private String TAG = "ViewfinderView.java";
	private boolean isDebug = false;

	/** 存储透明度数据，主要用于雷蛇线的显示显示至隐藏的动画。 */
	private static final int[] SCANNER_ALPHA = { 0, 64, 128, 192, 255, 192,
			128, 64 };
	private static final long ANIMATION_DELAY = 20L;
	private static final int OPAQUE = 0xFF;

	private final Paint paint;
	private Bitmap resultBitmap;
	/** 扫描中。掩饰部分颜色 */
	private final int maskColor;
	/** 扫描到结果后。掩饰部分颜色 */
	private final int resultColor;
	/** 矩形扫描区域边框颜色 */
	private final int frameColor;
	//四个角的颜色
	private final int jiaoColor;
	/** 中间线条颜色 */
	// private final int laserColor;
	/** 中间显示条显示高度 */
	// private int laserHeight = 0;
	/** 扫描中，点点的颜色（黄色） */
	private final int resultPointColor;
	private int scannerAlpha;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;

	// 中间滑动线的最顶端位置
	private int slideTop;
	// 中间滑动线的最底端位置
	private int slideBottom;
	boolean isFirst;
	private Context mContext;

	// 手机的屏幕密度   
    private static float density; 
    
    // 四个绿色边角对应的长度  
    private int ScreenRate; 
    
    // 四个绿色边角对应的宽度  
    private static final int CORNER_WIDTH = 10; 
    
	// This constructor is used when the class is built from an XML resource.
	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		
		density = context.getResources().getDisplayMetrics().density;  
        //将像素转换成dp  
        ScreenRate = (int)(20 * density);  
        
        		
		// Initialize these once for performance rather than calling them every
		// time in onDraw().
		paint = new Paint();
		Resources resources = getResources();
		// 扫描中。掩饰部分颜色
		maskColor = resources.getColor(R.color.viewfinder_mask);
		// 扫描到结果后。掩饰部分颜色
		resultColor = resources.getColor(R.color.result_view);
		// 矩形扫描区域边框颜色
		frameColor = resources.getColor(R.color.viewfinder_frame);
		//四个角的颜色
		jiaoColor = resources.getColor(R.color.jiao);
		// 中间线条颜色
		// laserColor = resources.getColor(R.color.viewfinder_laser);
		// 扫描中，点点的颜色（黄色）
		resultPointColor = resources.getColor(R.color.possible_result_points);
		scannerAlpha = 0;
		possibleResultPoints = new HashSet<ResultPoint>(5);
	}

	@Override
	public void onDraw(Canvas canvas) {
		Rect frame = null;
		try {
			frame = CameraManager.get().getFramingRect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (frame == null) {
			return;
		}

		// 初始化中间线滑动的最上边和最下边
		if (!isFirst) {
			isFirst = true;
			slideTop = frame.top;
			slideBottom = frame.bottom;
		}

		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// 画阴影区
		// Draw the exterior (i.e. outside the framing rect) darkened
		paint.setColor(resultBitmap != null ? resultColor : maskColor);
		canvas.drawRect(0, 0, width, frame.top, paint); // 上矩形
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint); // 左矩形
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
				paint); // 右矩形
		canvas.drawRect(0, frame.bottom + 1, width, height, paint); // 下矩形

		// 绘制提示的文字
		Paint paint2 = new Paint();
		// paint2.setTypeface(Typeface.create("宋体",Typeface.BOLD));
		paint2.setTextSize(mContext.getResources().getDimension(R.dimen.text_6));
		paint2.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿
		float a = paint2.measureText("将二维码/条码放入框内，即可自动扫描"); // 取得字符串宽度
		paint2.setColor(getResources().getColor(R.color.result_text));
		canvas.drawText("将二维码/条码放入框内，即可自动扫描", (width - a) / 2,
				frame.bottom + getResources().getDimension(R.dimen.width_10_80), paint2);

		// 扫描到结果，则直接显示结果
		if (resultBitmap != null) {
			paint.setAlpha(OPAQUE);
			canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);

			if (isDebug)
				Log.e("ZHB", "宽：" + frame.width() + "高：" + frame.height()
						+ ",区域宽高：" + resultBitmap.getWidth() + ","
						+ resultBitmap.getHeight());
		}
		// 扫描中，则
		else {
			// 绘制矩形边框
			paint.setColor(frameColor);
			canvas.drawRect(frame.left, frame.top, frame.right + 1,
					frame.top + 2, paint); // 上边
			canvas.drawRect(frame.left, frame.top + 2, frame.left + 2,
					frame.bottom - 1, paint); // 左边
			canvas.drawRect(frame.right - 1, frame.top, frame.right + 1,
					frame.bottom - 1, paint); // 右边
			canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1,
					frame.bottom + 1, paint); // 下边

			
			
			 //画扫描框边上的角，总共8个部分  
            paint.setColor(jiaoColor);  
            canvas.drawRect(frame.left, frame.top, frame.left + ScreenRate,  
                    frame.top + CORNER_WIDTH, paint);  
            canvas.drawRect(frame.left, frame.top, frame.left + CORNER_WIDTH, frame.top  
                    + ScreenRate, paint);  
            canvas.drawRect(frame.right - ScreenRate, frame.top, frame.right,  
                    frame.top + CORNER_WIDTH, paint);  
            canvas.drawRect(frame.right - CORNER_WIDTH, frame.top, frame.right, frame.top  
                    + ScreenRate, paint);  
            canvas.drawRect(frame.left, frame.bottom - CORNER_WIDTH, frame.left  
                    + ScreenRate, frame.bottom, paint);  
            canvas.drawRect(frame.left, frame.bottom - ScreenRate,  
                    frame.left + CORNER_WIDTH, frame.bottom, paint);  
            canvas.drawRect(frame.right - ScreenRate, frame.bottom - CORNER_WIDTH,  
                    frame.right, frame.bottom, paint);  
            canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom - ScreenRate,  
                    frame.right, frame.bottom, paint);

            
			// 绘制中间的线,每次刷新界面，中间的线往下移动5个像素
			slideTop += 5;
			if (slideTop >= frame.bottom) {
				slideTop = frame.top;
			}

			Rect lineRect = new Rect();
			lineRect.left = frame.left;
			lineRect.right = frame.right;
			lineRect.top = slideTop;
			lineRect.bottom = slideTop + 18;
			canvas.drawBitmap(((BitmapDrawable) (getResources()
					.getDrawable(R.drawable.qrcode_scan_line))).getBitmap(),
					null, lineRect, paint);

			Collection<ResultPoint> currentPossible = possibleResultPoints;
			Collection<ResultPoint> currentLast = lastPossibleResultPoints;
			if (currentPossible.isEmpty()) {
				lastPossibleResultPoints = null;
			} else {
				possibleResultPoints = new HashSet<ResultPoint>(5);
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(OPAQUE);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentPossible) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 6.0f, paint);
				}
			}
			if (currentLast != null) {
				paint.setAlpha(OPAQUE / 2);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentLast) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 3.0f, paint);
				}
			}

			// 只刷新扫描框的内容，其他地方不刷新
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
					frame.right, frame.bottom);
		}
	}

	public void drawViewfinder() {
		resultBitmap = null;
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		possibleResultPoints.add(point);
	}

}
