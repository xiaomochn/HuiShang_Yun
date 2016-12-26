package com.huishangyun.widget;

import java.util.Calendar;

/**
 * Created by pan on 2015/7/31.
 */
public class CalendarUtil {
    int mYear, mMonth;

    public CalendarUtil(int year, int month) {
        mYear = year;
        mMonth = month;
    }

    public static int[] getDayArray(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, (month - 1), 1);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        //System.out.println(year + "年" + month + "月1号是星期" + (day - 1));
        int[] monthDay = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
            monthDay[1]++;

        int[] m = new int[42];
        int i = 1;
        for (int j = day - 1; j < monthDay[month - 1] + day - 1; j++) {
            m[j] = i;
            i++;
        }
        return m;
    }

    private int getTile(int x, int y) {
        int[] intArray = getDayArray(mYear, mMonth);
        return intArray[y * 7 + x];
    }

    public String getTileString(int x, int y) {
        int v = getTile(x, y);
        if (v == 0) {
            return "";
        } else
            return String.valueOf(v);
    }

    public void setTime(int year, int month) {
        this.mYear = year;
        this.mMonth = month;
    }
}
