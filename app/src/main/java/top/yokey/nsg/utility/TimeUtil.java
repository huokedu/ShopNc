package top.yokey.nsg.utility;

import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    //作用：获取系统当前时间 格式 2016-04-02 15:30:21
    public static String getAll() {
        Time localTime = new Time("Asia/Hong_Kong");
        localTime.setToNow();
        return localTime.format("%Y-%m-%d %H:%M:%S");
    }

    //作用：获取系统当前时间 格式 20160402153021
    public static String getNoChar() {
        Time localTime = new Time("Asia/Hong_Kong");
        localTime.setToNow();
        return localTime.format("%Y%m%d%H%M%S");
    }

    //作用：获取系统当前日期 格式 2016-04-02
    public static String getDate() {
        Time localTime = new Time("Asia/Hong_Kong");
        localTime.setToNow();
        return localTime.format("%Y-%m-%d");
    }

    //作用：获取系统当前时间 格式 15:30:21
    public static String getTime() {
        Time localTime = new Time("Asia/Hong_Kong");
        localTime.setToNow();
        return localTime.format("%H:%M:%S");
    }

    //作用：获取系统当前年份 格式 2016
    public static String getYear() {
        Time localTime = new Time("Asia/Hong_Kong");
        localTime.setToNow();
        return localTime.format("%Y");
    }

    //作用：获取系统当前月份 格式 04
    public static String getMouth() {
        Time localTime = new Time("Asia/Hong_Kong");
        localTime.setToNow();
        return localTime.format("%m");
    }

    //作用：获取今天是什么日子 格式 02
    public static String getDay() {
        Time localTime = new Time("Asia/Hong_Kong");
        localTime.setToNow();
        return localTime.format("%d");
    }

    //作用：获取系统当前小时 格式 23
    public static String getHour() {
        Time localTime = new Time("Asia/Hong_Kong");
        localTime.setToNow();
        return localTime.format("%H");
    }

    //作用：获取系统当前分钟 格式 59
    public static String getMinute() {
        Time localTime = new Time("Asia/Hong_Kong");
        localTime.setToNow();
        return localTime.format("%M");
    }

    //作用：获取系统当前秒数 56
    public static String getSeconds() {
        Time localTime = new Time("Asia/Hong_Kong");
        localTime.setToNow();
        return localTime.format("%S");
    }

    //作用：时间戳转时间
    public static String longToTime(String s) {
        long time = Long.parseLong(s);
        Date date = new Date(time * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    //作用：对时间进行编码 传入的时间格式 2016-04-02 16:06:59
    public static String decode(String time) {

        try {
            int iDayNow = Integer.parseInt(getDay());
            int iDayRec = Integer.parseInt(time.substring(time.lastIndexOf("-") + 1, time.lastIndexOf("-") + 3));
            int iMouthNow = Integer.parseInt(getMouth());
            int iMouthRec = Integer.parseInt(time.substring(time.indexOf("-") + 1, time.indexOf("-") + 3));
            int iYearNow = Integer.parseInt(getYear());
            int iYearRec = Integer.parseInt(time.substring(0, time.indexOf("-")));

            if (iYearNow == iYearRec) {
                if (iMouthNow == iMouthRec) {
                    if (iDayNow == iDayRec) {
                        return time.substring(time.lastIndexOf("-") + 3, time.length() - 3);
                    } else if (iDayNow - iDayRec == 1) {
                        return "昨天" + time.substring(time.lastIndexOf("-") + 3, time.length() - 3);
                    } else if (iDayNow - iDayRec == 2) {
                        return "前天" + time.substring(time.lastIndexOf("-") + 3, time.length() - 3);
                    } else {
                        return iDayNow - iDayRec + " 天前" + time.substring(time.lastIndexOf("-") + 3, time.length() - 3);
                    }
                } else {
                    return (iMouthNow - iMouthRec) + " 个月前";
                }
            } else {
                return (iYearNow - iYearRec) + " 年前";
            }
        } catch (Exception e) {
            return time;
        }

    }

}