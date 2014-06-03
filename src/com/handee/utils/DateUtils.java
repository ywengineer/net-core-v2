package com.handee.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Class description goes here.
 *
 * @author Mark Yong
 * @version 2011-3-1 下午05:57:08 1.0.0.0
 * @email ywengineer@gmail.com
 */
public class DateUtils {
    public static final long MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;

    public static final long MILLISECONDS_PER_MIN = 60 * 1000;
    /**
     * Base ISO 8601 Date format yyyyMMdd i.e., 20021225 for the 25th day of
     * December in the year 2002
     */
    public static final String ISO_DATE_FORMAT = "yyyyMMdd";

    /**
     * Expanded ISO 8601 Date format yyyy-MM-dd i.e., 2002-12-25 for the 25th
     * day of December in the year 2002
     */
    public static final String ISO_EXPANDED_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * yyyy-MM-dd hh:mm:ss
     */
    public static String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * yyyy-MM-dd_HH:mm:ss
     */
    public static String DATETIME_PATTERN_ = "yyyy-MM-dd_HH:mm:ss";


    /**
     * Default lenient setting for getDate.
     */
    private static boolean LENIENT_DATE = false;

    // private static final DateFormat format = new
    // SimpleDateFormat("yyyy-MM-dd");

    /**
     * 计算两个日期相差分数
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 相差天数
     */
    public static int dateDiffMin(long begin, long end) {
        long diff = end - begin;
        return (int) (diff / MILLISECONDS_PER_MIN);
    }

    /**
     * 去掉时间的日期毫秒数，即参数所表示的某一天凌晨的毫秒数。
     *
     * @param timeMils 毫秒数
     * @return 日期所表示的毫秒数
     */
    public static long cutTime(long timeMils) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMils);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 计算两个日期相差天数
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 相差天数
     */
    public static int dateDiff(long begin, long end) {
        long diff = end - begin;
        return (int) (diff / MILLISECONDS_PER_DAY);
    }

    public static Date getDate(long timeMils) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMils);
        return cal.getTime();
    }

    /**
     * 计算两个日期相差天数
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 相差天数
     */
    public static int dateDiff(Date begin, Date end) {
        return dateDiff(begin.getTime(), end.getTime());
    }

    /**
     * 是否是同一天
     *
     * @param src
     * @param dest
     * @return
     */
    public static boolean isSameDay(Date src, Date dest) {
        String DATE_FORMAT = "yyyy-MM-dd";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
        String date1Str = sdf.format(src);
        String date2Str = sdf.format(dest);
        return date1Str.equals(date2Str);
    }

    /**
     * 今天是星期几.
     * <p/>
     * 星期天是0.
     *
     * @return
     */
    public static int getDayOfWeek() {
        return getDayOfWeek(Calendar.getInstance());
    }

    /**
     * 获取当前时间的小时。
     * <p/>
     * 24小时表示
     *
     * @return 小时（几点）
     */
    public static int getHour() {
        return getHour(Calendar.getInstance());
    }

    /**
     * 获取当前时间的分钟。
     *
     * @return 分
     */
    public static int getMinutes() {
        return getMinutes(Calendar.getInstance());
    }

    /**
     * 获取当前时间的秒。
     *
     * @return 秒
     */
    public static int getSeconds() {
        return getSeconds(Calendar.getInstance());
    }

    /**
     * 获取当前日期的月份
     *
     * @return 月份
     */
    public static int getMonth() {
        return getMonth(Calendar.getInstance());
    }

    /**
     * 获取指定日期的月份。
     * <p/>
     * 从1开始。
     *
     * @param calendar 日期
     * @return 月份
     */
    public static int getMonth(Calendar calendar) {
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取指定时间的秒。
     *
     * @return 秒
     */
    public static int getSeconds(Calendar cal) {
        return cal.get(Calendar.SECOND);
    }

    /**
     * 获取指定时间的小时。
     *
     * @return 小时
     */
    public static int getHour(Calendar cal) {
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取指定时间的分。
     *
     * @return 分
     */
    public static int getMinutes(Calendar cal) {
        return cal.get(Calendar.MINUTE);
    }

    /**
     * 指定日期是星期几.
     * <p/>
     * 星期天是0.
     *
     * @param cal 日历
     * @return
     */
    public static int getDayOfWeek(Calendar cal) {
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 指定日期是星期几.
     * <p/>
     * 星期天是0.
     *
     * @param date 日期
     * @return
     */
    public static int getDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getDayOfWeek(cal);
    }

    /**
     * 指定日期的当前分钟
     *
     * @param date 日期
     * @return
     */
    public static int getMinutesOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getMinutes(cal);
    }

    /**
     * 指定日期的当前小时.
     *
     * @param date 日期
     * @return
     */
    public static int getHourOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getHour(cal);
    }

    /**
     * 指定日期是星期几.
     * <p/>
     * 星期天是0.
     *
     * @param timeMils 日期
     * @return
     */
    public static int getDayOfWeek(long timeMils) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMils);
        return getDayOfWeek(cal);
    }

    /**
     * 拷贝时间
     *
     * @param date
     * @return
     */
    public static Date copyFrom(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getTime();
    }


    /**
     * 日期计算
     *
     * @param isoString 时间表示的字符串
     * @param fmt       时间格式
     * @param field     需要计算的时间字段常量
     * @param amount    增加或者减少的时间
     * @return 计算之后的时间
     */
    public static String dateIncrease(String isoString, String fmt, int field, int amount) {

        try {
            Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
            cal.setTime(stringToDate(isoString, fmt, true));
            cal.add(field, amount);

            return dateToString(cal.getTime(), fmt);

        } catch (Exception ex) {
            return null;
        }
    }

    public static Date stringToDate(String dateText, String format, boolean lenient) {

        if (dateText == null) {

            return null;
        }

        DateFormat df = null;

        try {

            if (format == null) {
                df = new SimpleDateFormat();
            } else {
                df = new SimpleDateFormat(format);
            }

            // setLenient avoids allowing dates like 9/32/2001
            // which would otherwise parse to 10/2/2001
            df.setLenient(false);

            return df.parse(dateText);
        } catch (ParseException e) {

            return null;
        }
    }

    public static Date stringToDate(String dateString, String format) {

        return stringToDate(dateString, format, LENIENT_DATE);
    }

    public static Date stringToDate(String dateString) {

        return stringToDate(dateString, ISO_EXPANDED_DATE_FORMAT, LENIENT_DATE);
    }

    public static String dateToString(Date date, String pattern) {

        if (date == null) {

            return null;
        }

        try {

            SimpleDateFormat sfDate = new SimpleDateFormat(pattern);
            sfDate.setLenient(false);

            return sfDate.format(date);
        } catch (Exception e) {

            return null;
        }
    }

    public static String dateToString(Date date) {
        return dateToString(date, ISO_EXPANDED_DATE_FORMAT);
    }

    public static Date getCurrentDateTime() {
        java.util.Calendar calNow = java.util.Calendar.getInstance();
        java.util.Date dtNow = calNow.getTime();

        return dtNow;
    }

    public static String getCurrentDateString(String pattern) {
        return dateToString(getCurrentDateTime(), pattern);
    }

    public static String getCurrentDateString() {
        return dateToString(getCurrentDateTime(), ISO_EXPANDED_DATE_FORMAT);
    }

    public static String dateToStringWithTime(Date date) {

        return dateToString(date, DATETIME_PATTERN);
    }

    public static Date dateIncreaseByDay(Date date, int days) {

        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    public static Date dateIncreaseByMonth(Date date, int mnt) {

        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(date);
        cal.add(Calendar.MONTH, mnt);

        return cal.getTime();
    }

    public static Date dateIncreaseByYear(Date date, int mnt) {

        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(date);
        cal.add(Calendar.YEAR, mnt);

        return cal.getTime();
    }

    public static String dateIncreaseByDay(String date, int days) {
        return dateIncreaseByDay(date, ISO_DATE_FORMAT, days);
    }

    public static String dateIncreaseByDay(String date, String fmt, int days) {
        return dateIncrease(date, fmt, Calendar.DATE, days);
    }

    /**
     * 鏃ユ湡澧炲姞-鎸夌澧炲姞
     *
     * @param date
     * @param sec
     * @return java.util.Date
     */
    public static Date dateIncreaseBySec(Date date, int sec) {

        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(date);

        cal.add(Calendar.SECOND, sec);

        return cal.getTime();
    }

    /**
     * 将指定日期转换为指定的格式
     *
     * @param src    时间字符串
     * @param srcfmt 原时间格式
     * @param desfmt 目标时间格式
     * @return 转换之后的时间字符串
     */
    public static String stringToString(String src, String srcfmt, String desfmt) {
        return dateToString(stringToDate(src, srcfmt), desfmt);
    }

    /**
     * 比较指定日期与当前日期的大小。
     * <p/>
     * 如果指定时间大小当前时间，返回true.
     *
     * @param date 指定日期
     * @return 是否大于当前时间
     */
    public static boolean compareCurrentDate(Date date) {
        Date now = getCurrentDateTime();
        if (date.compareTo(now) > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 指定日期的偏移量与当前日期的比较。
     * <p/>
     * 如果大于当前日期，返回true。
     *
     * @param date   指定日期
     * @param offSec 时间偏移量（秒）
     * @return 是否大于当前时间
     */
    public static boolean compareCurrentDate(Date date, int offSec) {
        Date now = getCurrentDateTime();
        Date newDate = dateIncreaseBySec(date, offSec);
        if (newDate.compareTo(now) > 0) {
            return true;
        } else {
            return false;
        }
    }
}
