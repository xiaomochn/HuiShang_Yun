package com.huishangyun.widget;

/**
 * Created by pan on 2015/7/31.
 */
public class CalendarTag {
    public static final int TAG_NORMAL = 1;
    public static final int TAG_DONE = 2;
    public static final int TAG_DOING = 3;
    public static final int TAG_NOTDONE = 4;
    public static final int TAG_SELECT = 5;
    private String date;
    private int tag1;
    private int tag2;

    public CalendarTag(String date, int tag1, int tag2) {
        this.date = date;
        this.tag1 = tag1;
        this.tag2 = tag2;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTag1() {
        return tag1;
    }

    public void setTag1(int tag1) {
        this.tag1 = tag1;
    }

    public int getTag2() {
        return tag2;
    }

    public void setTag2(int tag2) {
        this.tag2 = tag2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarTag calendarTag = (CalendarTag) o;

        if (!date.equals(calendarTag.date)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return date.hashCode() * 37;
    }

    @Override
    public String toString() {
        return "CalendarTag{" +
                "date='" + date + '\'' +
                ", tag1=" + tag1 +
                ", tag2=" + tag2 +
                '}';
    }
}
