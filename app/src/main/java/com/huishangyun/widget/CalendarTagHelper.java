package com.huishangyun.widget;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pan on 2015/7/31.
 */
public class CalendarTagHelper {
    private static CalendarTagHelper helper;
    private Map<String, CalendarTag> tags = null;
    private CalendarTag selectCalendarTag = null;

    public static CalendarTagHelper getHelper() {
        if (helper == null) {
            helper = new CalendarTagHelper();
        }
        return helper;
    }

    public Map<String, CalendarTag> getCalendarTags() {
        return tags;
    }

    public void setTags(Map<String, CalendarTag> tags) {
        if (this.tags == null) {
            this.tags = tags;
        } else {
            this.tags.putAll(tags);
        }
    }

 /*   public void setDataTags(List<CalendarTag> calendarTags) {
        if (tags == null) {
            tags = new HashMap<>();
        }
        for (CalendarTag calendarTag : calendarTags) {
            if (getCalendarTag(calendarTag.getDate()) != null) {

            }
        }
    }*/

    public void addTag(String date) {

        if (tags == null) {
            tags = new HashMap<>();
        }
        if (selectCalendarTag != null) {
            selectCalendarTag.setTag2(CalendarTag.TAG_NORMAL);
            if (selectCalendarTag.getTag1() == CalendarTag.TAG_NORMAL) {
                tags.remove(selectCalendarTag.getDate());
//                SimpleLogger.log_e("tags.remove","tags.remove");
            } else {
                tags.put(selectCalendarTag.getDate(), selectCalendarTag);
//                SimpleLogger.log_e("tags.remove false", "tags.remove false");
            }
        }

        CalendarTag calendarTag = getCalendarTag(date);
        if (calendarTag == null) {
            calendarTag = new CalendarTag(date, CalendarTag.TAG_NORMAL, CalendarTag.TAG_SELECT);
        } else {
            calendarTag.setTag2(CalendarTag.TAG_SELECT);
        }
        tags.put(date, calendarTag);
        selectCalendarTag = calendarTag;

//        for (String key : tags.keySet()) {
//            CalendarTag tag = tags.get(key);
//            SimpleLogger.log_e("tags:",tag.toString());
//        }
    }

    public CalendarTag getCalendarTag(String date) {
        if (tags == null || tags.size() == 0) {
            return null;
        }
        return tags.get(date);
    }

    private boolean isLoad = false;

    public boolean isLoad() {
        return isLoad;
    }

    public void setIsLoad(boolean isLoad) {
        this.isLoad = isLoad;
    }
}
