package com.huishangyun.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.huishangyun.Util.L;
import com.huishangyun.yun.R;

import java.util.Calendar;
import java.util.Map;

/**
 * Created by pan on 2015/7/31.
 */
public class CalendarView extends View {
    // 每一格的宽度
    private float mWidth;
    // 每一格的高度
    private float mHeight;
    // 可见的 View 的总宽度
    private float mOutWidth;
    // 可见的 View 的总高度
    private float mOutHeight;
    // 偏移量，用于整体便宜一下，然后空出来的区域可以画星期文字
    private float OFFSET = 60;
    // 初始化日历对象，获得当前时间
    private Calendar c = Calendar.getInstance();
    private int mYear = c.get(Calendar.YEAR);
    private int mMonth = c.get(Calendar.MONTH) + 1;
    private CalendarUtil mMyCalendar = new CalendarUtil(mYear, mMonth);
    private OnDayClickListener onDayClickListener = null;

    public CalendarView(Context context) {
        super(context);
        init();
    }

    public CalendarView(Context context, int year, int month) {
        super(context);
        mMyCalendar = new CalendarUtil(year, month);
        init();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // 定义画笔
    Paint backgroundPaint;
    Paint hilitePaint;
    Paint lightPaint;
    Paint darkPaint;
    Paint bigNumberPaint;
    Paint weekPaint;
    Paint dataPaint;

    private void init() {
        // 定义画笔
        backgroundPaint = new Paint();
        hilitePaint = new Paint();
        lightPaint = new Paint();
        darkPaint = new Paint();
        bigNumberPaint = new Paint();
        dataPaint = new Paint();
        // 通过资源文件加载颜色，并把颜色值设置给画笔对象
        backgroundPaint.setColor(getResources().getColor(R.color.white));
        hilitePaint.setColor(getResources().getColor(R.color.c_hilite));
        lightPaint.setColor(getResources().getColor(R.color.c_light));
        bigNumberPaint.setColor(getResources().getColor(R.color.c_light));
        darkPaint.setColor(getResources().getColor(R.color.c_dark));
        dataPaint.setColor(getResources().getColor(R.color.c_data));

        // 设置画笔的粗细
        lightPaint.setStrokeWidth(2);
        darkPaint.setStrokeWidth(4);
        dataPaint.setStrokeWidth(4);

        weekPaint = new Paint();
        weekPaint.setColor(getResources().getColor(R.color.c_text));
        /*weekPaint.setAntiAlias(true);
        float weekTextSize = mWidth * 0.34f;
        weekPaint.setTextSize(weekTextSize);
        weekPaint.setTypeface(Typeface.DEFAULT_BOLD);*/
        weekPaint.setStrokeWidth(4);
    }

    /**
     * 当尺寸改变的时候会调用这个方法，一般初次也会调用这个方法，所以可以在这个方法里获得尺寸数据
     *
     * @param w    整个 View 的宽度
     * @param h    整个 View 的高度
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w / 7;
        this.mHeight = mWidth;
        this.mOutWidth = getWidth();
        this.mOutHeight = mHeight * 6;

        OFFSET = mWidth * 10 / 9;
    }

    /**
     * 关键方法，在这里面用代码画控件
     *
     * @param canvas 画布对象
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 画背景
        // 画矩形背景
        canvas.drawRect(0, 0, mOutWidth, mOutHeight + OFFSET, backgroundPaint);
        bigNumberPaint.setTextSize(mOutHeight * 0.8f);
        //  获得这只已经设置了字体大小的画笔的字体大小的像素
        Paint.FontMetrics fontMetrics0 = bigNumberPaint.getFontMetrics();
        // 因为文字的基点是有点特别的，所以需要计算出一个文字的偏移量，用于让文字在格子中居中
        float x0 = mOutWidth / 2;
        float y0 = mOutHeight / 2 - (fontMetrics0.ascent + fontMetrics0.descent) / 2;
        bigNumberPaint.setTextAlign(Paint.Align.CENTER);

        // 画横线，画两条的原因是，这样会有刻出来的效果感觉
        for (int i = 0; i < 7; i++) {
            canvas.drawLine(0, i * mHeight + OFFSET, mOutWidth, i * mHeight + OFFSET, lightPaint);
            //canvas.drawLine(0, i * mHeight + 1 + OFFSET, mOutWidth, i * mHeight + 1 + OFFSET, hilitePaint);
        }
        // 画竖线，同样画两条
        for (int i = 0; i < 8; i++) {
            canvas.drawLine(i * mWidth, OFFSET, i * mWidth, mOutHeight + OFFSET, lightPaint);
            //canvas.drawLine(i * mWidth + 1, OFFSET, i * mWidth, mOutHeight + 1 + OFFSET, hilitePaint);
        }

        // numberPaint 画笔设置。为了画每一个小数字
        Paint numberPaint = new Paint();
        numberPaint.setColor(getResources().getColor(R.color.c_text));
        numberPaint.setStyle(Paint.Style.STROKE);
        numberPaint.setTextSize(mWidth * 0.3f);
        Paint.FontMetrics fontMetrics = numberPaint.getFontMetrics();
        numberPaint.setAntiAlias(true);
        numberPaint.setTextAlign(Paint.Align.CENTER);
        float x = mWidth / 2;
        float y = mHeight / 2 - (fontMetrics.ascent + fontMetrics.descent) / 2;
        // 画每一个小日期数字
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                String day = mMyCalendar.getTileString(i, j);
                //canvas.drawText(day, i * mWidth + x, j * mHeight + y + OFFSET, numberPaint);
                String date = getDate(day);
                if (!TextUtils.isEmpty(date)) {
                    CalendarTag tag = CalendarTagHelper.getHelper().getCalendarTag(date);
                    if (tag != null) {
                        if (tag.getTag2() == CalendarTag.TAG_SELECT) {
                            canvas.drawRect(i * mWidth + 2, j * mHeight + OFFSET  + 2, (i + 1) * mWidth  - 2, (j + 1) * mHeight + OFFSET  - 2, darkPaint);
                        } else {
                            switch (tag.getTag1()) {
                                case CalendarTag.TAG_SELECT:
                                    canvas.drawRect(i * mWidth + 2, j * mHeight + OFFSET  + 2, (i + 1) * mWidth  - 2, (j + 1) * mHeight + OFFSET  - 2, darkPaint);
                                    break;
                                case CalendarTag.TAG_DONE://画有数据的日期！
                                    canvas.drawRect(i * mWidth + 2, j * mHeight + OFFSET  + 2, (i + 1) * mWidth  - 2, (j + 1) * mHeight + OFFSET  - 2, dataPaint);
                                    break;
                                case CalendarTag.TAG_DOING:
                                    canvas.drawRect(i * mWidth, j * mHeight + OFFSET, (i + 1) * mWidth, (j + 1) * mHeight + OFFSET, darkPaint);
                                    break;
                                case CalendarTag.TAG_NOTDONE:
                                    canvas.drawRect(i * mWidth, j * mHeight + OFFSET, (i + 1) * mWidth, (j + 1) * mHeight + OFFSET, darkPaint);
                                    break;
                                case CalendarTag.TAG_NORMAL:
                                    canvas.drawRect(i * mWidth, j * mHeight + OFFSET, (i + 1) * mWidth, (j + 1) * mHeight + OFFSET, darkPaint);
                                    break;

                                default:
                                    canvas.drawRect(i * mWidth, j * mHeight + OFFSET, (i + 1) * mWidth, (j + 1) * mHeight + OFFSET, darkPaint);
                                    break;
                            }
                        }
                        Paint numberPaint2 = new Paint();
                        numberPaint2.setColor(getResources().getColor(R.color.c_hilite));
                        numberPaint2.setStyle(Paint.Style.STROKE);
                        numberPaint2.setTextSize(mWidth * 0.3f);
                        numberPaint2.setAntiAlias(true);
                        numberPaint2.setTextAlign(Paint.Align.CENTER);
                        canvas.drawText(day, i * mWidth + x, j * mHeight + y + OFFSET, numberPaint2);
                    } else {
                        canvas.drawText(day, i * mWidth + x, j * mHeight + y + OFFSET, numberPaint);
                    }
                }
            }
        }

        // 画顶上的星期
        weekPaint.setTextSize(mWidth * 0.34f);
        weekPaint.setAntiAlias(true);
        weekPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("日", 0 * mWidth + x, y + OFFSET - mHeight, weekPaint);
        canvas.drawText("一", 1 * mWidth + x, y + OFFSET - mHeight, weekPaint);
        canvas.drawText("二", 2 * mWidth + x, y + OFFSET - mHeight, weekPaint);
        canvas.drawText("三", 3 * mWidth + x, y + OFFSET - mHeight, weekPaint);
        canvas.drawText("四", 4 * mWidth + x, y + OFFSET - mHeight, weekPaint);
        canvas.drawText("五", 5 * mWidth + x, y + OFFSET - mHeight, weekPaint);
        canvas.drawText("六", 6 * mWidth + x, y + OFFSET - mHeight, weekPaint);
    }

    private void onDayClick(String calendarDay) {
        L.e("onDayClick", "onDayClick " + calendarDay);
        CalendarTagHelper.getHelper().addTag(calendarDay);
        invalidate();
        if (onDayClickListener != null) {
            onDayClickListener.onDayClick(this, calendarDay);
        }
    }

    public void setDay(Map<String, CalendarTag> map) {

        CalendarTagHelper.getHelper().setTags(map);
        invalidate();
    }

    /**
     * 设置今天
     * @param calendarDay
     */
    public void setToDay(String calendarDay) {
        CalendarTagHelper.getHelper().addTag(calendarDay);
        invalidate();
    }

    public String getTime() {
        return mYear + "-" + addZero("" + mMonth);
    }

    private String getDate(String day) {
        if (!TextUtils.isEmpty(day)) {
            return mYear + "-" + addZero("" + mMonth) + "-" + addZero(day);
        } else {
            return "";
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            String calendarDay = addZero(getDayFromLocation(event.getX(), event.getY()));
            if (!TextUtils.isEmpty(calendarDay)) {
                onDayClick(mYear + "-" + addZero("" + mMonth) + "-" + calendarDay);
            } else {

            }
        }
        return true;
    }

    public String addZero(String str) {
        if (!TextUtils.isEmpty(str)) {
            if (str.length() < 2) {
                str = "0" + str;
            }
        }
        return str;
    }

    /**
     * 根据日历数字的位子取出对应日历中的数据
     *
     * @param x
     * @param y
     * @return
     */
    public String getDayFromLocation(float x, float y) {
        int i = (int) (x / mWidth);
        int j = (int) ((y - OFFSET) / mHeight);
        if ((i >= 0 && i < 7) && (j >= 0 && j < 6)) {
            return mMyCalendar.getTileString(i, j);
        }
        return "";
    }

    public void setTime(int year, int month) {
        this.mYear = year;
        this.mMonth = month;
        mMyCalendar.setTime(year, month);
        // 调用这个方法会重绘，刷新控件的样子，它会间接重新调用 onDraw()
        invalidate();
    }

    public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        this.onDayClickListener = onDayClickListener;
    }

    public static abstract interface OnDayClickListener {
        public abstract void onDayClick(CalendarView calendarView, String calendarDay);
    }
}
