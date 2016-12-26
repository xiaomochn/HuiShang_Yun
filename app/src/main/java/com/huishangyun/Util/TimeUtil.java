package com.huishangyun.Util;

import com.huishangyun.model.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间操作类
 *
 * @author pan
 */
public class TimeUtil {
    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        return format.format(new Date(time));
    }

    public static String getInfoTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(time));
    }

    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    public static String getMoneTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        return format.format(new Date(time));
    }

    public static String getChatTime(long timesamp) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(timesamp);
        int temp = Integer.parseInt(sdf.format(today))
                - Integer.parseInt(sdf.format(otherDay));

        switch (temp) {
            case 0:
                result = "今天 " + getHourAndMin(timesamp);
                break;
            case 1:
                result = "昨天 " + getHourAndMin(timesamp);
                break;
            case 2:
                result = "前天 " + getHourAndMin(timesamp);
                break;

            default:
                // result = temp + "天前 ";
                if (temp > 0 && temp < 7) {
                    result = temp + "天前 ";
                } else if (temp < 0 && temp > -31) {
                    result = getMoneTime(timesamp);
                } else {
                    result = getTime(timesamp);
                }
                break;
        }

        return result;
    }


    public static String getScreenTime(long timesamp) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(timesamp);
        int temp = Integer.parseInt(sdf.format(today))
                - Integer.parseInt(sdf.format(otherDay));

        switch (temp) {
            case 0:
                result = getHourAndMin(timesamp);
                break;
            case 1:
                result = "昨天 " + getHourAndMin(timesamp);
                break;
            case 2:
                result = "前天 " + getHourAndMin(timesamp);
                break;

            default:
                // result = temp + "天前 ";
                if (temp > 0 && temp < 7) {
                    result = temp + "天前 ";
                } else if (temp < 0 && temp > -31) {
                    result = getMoneTime(timesamp);
                } else {
                    result = getTime(timesamp);
                }
                break;
        }

        return result;
    }

    /**
     * 获取聊天记录具体时间
     *
     * @param timesamp
     * @return
     */
    public static String getChatHistoryTime(long timesamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = new Date(timesamp);
        return sdf.format(date);

    }

    /**
     * 获取聊天记录标题时间
     *
     * @param timesam
     * @return
     */
    public static String getChatHistoryTieliTime(long timesam) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = new Date(timesam);
        return sdf.format(date);

    }

    public static String getLongToString(long timesam) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constant.MS_FORMART);
        Date date = new Date(timesam);
        return sdf.format(date);

    }

    public static String getLongToString(long timesam, String formart) {
        SimpleDateFormat sdf = new SimpleDateFormat(formart);
        Date date = new Date(timesam);
        return sdf.format(date);
    }


    public static long getLongtime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constant.MS_FORMART);
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * 获取对应时间戳
     *
     * @param time
     * @return
     */
    public static long getLocationtime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
        return date.getTime();
    }

    public static String getFileTime(long timesam) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(timesam);
        return sdf.format(date);
    }

    /**
     * 将时间里的'T'替换成空格
     *
     * @param time
     * @return
     */
    public static String getTime(String time) {
        if (time == null || time.equals("")) {
            return "";
        }
        if (time.contains("T")) {
            return time.split("T")[0];
        }
        return time;


    }

    public static String getTimeForT(String time) {
        if (time == null || time.equals("")) {
            return "";
        }
        if (time.contains("T")) {
            return time.split("T")[0] + " " + time.split("T")[1];
        }
        return time;


    }

    public static String getTimeOfDay(String time) {
        if (time == null || time.equals("")) {
            return "";
        }
        if (time.contains("T")) {
            time = time.split("T")[1];
            return time.split(":")[0] + ":" +time.split(":")[1];
        }
        return time;
    }

    /**
     * 判断当前时间是否在时间段内
     *
     * @param start
     * @param end
     * @return 返回true为在时间段内
     */
    public static boolean isOnTheTime(int start, int end) {
        Calendar cal = Calendar.getInstance();// 当前日期
        int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
        int minute = cal.get(Calendar.MINUTE);// 获取分钟
        int minuteOfDay = hour * 60 + minute;// 从0:00分开是到目前为止的分钟数
        if (minuteOfDay >= start && minuteOfDay <= end) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 去掉时间T
     *
     * @param time
     * @return
     */
    public static String getTimeNoT(String time) {
        if (time == null || time.equals("")) {
            return "";
        }
        if (time.contains("T")) {
            return time.split("T")[0];
        }
        return time;
    }


    public static String getTimeOnType(String time, String Type) {
        if (time == null || time.equals("") || Type == null || Type.equals("")) {
            return "";
        }
        //去掉T
        time = getTimeForT(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return getLongToString(date.getTime(), Type);

    }

    /**
     * 获取服务号时间
     * @param time
     * @return
     */
    public static String getChatServiceDate(String time) {
        if (time == null || time.equals("")) {
            return "";
        }
        time = getTimeForT(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return getLongToString(date.getTime(), "yyyy-MM-dd HH:mm:ss");
    }
}
