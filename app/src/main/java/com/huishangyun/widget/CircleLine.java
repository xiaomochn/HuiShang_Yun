package com.huishangyun.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Pan on 2015/8/10.
 */
public class CircleLine extends View{
    public CircleLine(Context context) {
        super(context);
    }

    public CircleLine(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xffe6e6e6);
        PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
        paint.setPathEffect(effects);
        canvas.drawCircle(202, 202, 100, paint);
        canvas.drawLine(0, 0, 400, 400, paint);*/
    }
}
