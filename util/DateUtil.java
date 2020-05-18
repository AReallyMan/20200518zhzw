package com.zhzw.util;



import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.Calendar;

/**
 * 日期时间工具类
 */
public class DateUtil
{
    private static final int[] dayArray;
    public static String defaultDateFormat;
    public static String defaultTimeFormat;
    private static int weeks;

    static {
        dayArray = new int[] { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        DateUtil.defaultDateFormat = "yyyy-MM-dd";
        DateUtil.defaultTimeFormat = "yyyy-MM-dd HH:mm:ss";
        DateUtil.weeks = 0;
    }

    private static final int getMondayPlus() {
        final Calendar cd = Calendar.getInstance();
        final int dayOfWeek = cd.get(7);
        if (dayOfWeek == 1) {
            return -6;
        }
        return 2 - dayOfWeek;
    }

    public static Date getDateBefore(final Date d, final int day) {
        final Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(5, now.get(5) - day);
        return now.getTime();
    }

    public static final String getCurrentMonday() {
        DateUtil.weeks = 0;
        final int mondayPlus = getMondayPlus();
        final GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(5, mondayPlus);
        final Date monday = currentDate.getTime();
//        final DateFormat df = DateFormat.getDateInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        final String preMonday = df.format(monday);
        return preMonday;
    }

    public static final String addonemonth(final String selectstatetime) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final Date date = sdf.parse(selectstatetime);
        final Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        calender.add(2, 1);
        final String enddate = sdf.format(calender.getTime());
        return enddate;
    }

    public static final String formatCurrentTime(final String pattern) {
        return transferDateToString(new Date(), pattern);
    }

    public static final String transferDateToString(final Date formatDate) {
        return transferDateToString(formatDate, null);
    }

    public static final String transferDateToString(final Date formatDate, String pattern) {
        if (formatDate == null) {
            throw new IllegalArgumentException("日期对象参数不能为空");
        }
        pattern = (isEmpty(pattern) ? DateUtil.defaultDateFormat : pattern);
        final SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(formatDate);
    }

    public static final Date transferStringToDate(final String strDate) {
        return transferStringToDate(strDate, null);
    }

    public static final Date transferStringToDate(final String strDate, String pattern) {
        if (strDate == null) {
            throw new IllegalArgumentException("日期格式字符串不能为空");
        }
        pattern = (isEmpty(pattern) ? DateUtil.defaultDateFormat : pattern);
        final SimpleDateFormat formatter1 = new SimpleDateFormat(pattern);
        try {
            return formatter1.parse(strDate);
        }
        catch (Exception e) {
            throw new RuntimeException("日期字符串格式错误", e);
        }
    }

    public static final String formatDate(String value) {
        final SimpleDateFormat md = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = md.parse(value);
        }
        catch (ParseException e2) {
            md.applyPattern("yyyy/MM/dd");
            try {
                d = md.parse(value);
            }
            catch (ParseException e1) {
                e1.printStackTrace();
                throw new RuntimeException("日期字符串格式错误", e1);
            }
        }
        md.applyPattern("yyyy-MM-dd");
        value = md.format(d);
        return value;
    }

    public static int getCurrentYear() {
        return getTimeField(Calendar.getInstance().getTime(), 1);
    }

    public static int getCurrentMonth() {
        return getTimeField(Calendar.getInstance().getTime(), 2);
    }

    public static int getCurrentDay() {
        return getTimeField(Calendar.getInstance().getTime(), 5);
    }

    public static int getTimeField(final Date date, final int field) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getTimeField(cal, field);
    }

    public static int getTimeField(final Calendar cal, final int field) {
        int fieldValue = cal.get(field);
        fieldValue = ((field == 2) ? (fieldValue + 1) : fieldValue);
        return fieldValue;
    }

    public static int getLastDayOfMonth(final int month) {
        if (month < 1 || month > 12) {
            return -1;
        }
        int retn = 0;
        if (month == 2 && isLeapYear()) {
            retn = 29;
        }
        else {
            retn = DateUtil.dayArray[month - 1];
        }
        return retn;
    }

    public static int getLastDayOfMonth(final int year, final int month) {
        if (month < 1 || month > 12) {
            return -1;
        }
        int retn = 0;
        if (month == 2 && isLeapYear(year)) {
            retn = 29;
        }
        else {
            retn = DateUtil.dayArray[month - 1];
        }
        return retn;
    }

    public static int getLastDayOfMonth(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getLastDayOfMonth(cal);
    }

    public static int getLastDayOfMonth(final Calendar cal) {
        final int year = getTimeField(cal, 1);
        final int month = getTimeField(cal, 2);
        if (month < 1 || month > 12) {
            return -1;
        }
        int retn = 0;
        if (month == 2 && isLeapYear(year)) {
            retn = 29;
        }
        else {
            retn = DateUtil.dayArray[month - 1];
        }
        return retn;
    }

    public static boolean isLeapYear() {
        final Calendar cal = Calendar.getInstance();
        final int year = cal.get(1);
        return isLeapYear(year);
    }

    public static boolean isLeapYear(final int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }

    public static Date backYears(final Date curDate, final int backYearNum) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        final int curYearNum = cal.get(1);
        final int curDayofmonth = cal.get(5);
        if (isLeapYear(curYearNum) && cal.get(2) == 1 && curDayofmonth == 29 && !isLeapYear(curYearNum - backYearNum)) {
            cal.set(5, 28);
        }
        cal.set(1, curYearNum - backYearNum);
        return new Date(cal.getTimeInMillis());
    }

    public static Date addDays(final Date curDate, final int addDaysNum) {
        final long curDateMills = curDate.getTime();
        final long addDaysMills = addDaysNum * 24 * 3600 * 1000;
        return new Date(curDateMills + addDaysMills);
    }

    public static String addStringDays(final String curDate, final int addDaysNum) {
        final Date curdate = transferStringToDate(curDate);
        final Date adddate = addDays(curdate, addDaysNum);
        final String addenddate = transferDateToString(adddate);
        return addenddate;
    }

    private static boolean isEmpty(final String value) {
        return value == null || value.trim().equals("");
    }

    public static List<String> getWeeks(final int year, final int month) {
        final List<String> list = new ArrayList<String>();
        int weeknum = 0;
        int week = 0;
        final Calendar calendar = Calendar.getInstance();
        calendar.set(1, year);
        calendar.set(2, month - 1);
        for (int day = calendar.getActualMaximum(5), i = 1; i <= day; ++i) {
            calendar.set(5, i);
            if (calendar.get(7) == 2) {
                if (weeknum == 0) {
                    week = calendar.get(3);
                }
                list.add(new StringBuilder(String.valueOf(week + weeknum)).toString());
                ++weeknum;
            }
        }
        return list;
    }

    public static String getWeekOfDate(final Date dt) {
        final String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        final Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(7) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    public static void main(final String[] args) {
        final Date date = transferStringToDate("2008-2-29");
        final List<String> weeks = getWeeks(2012, 9);
        String sql = "select t.* from OA_BOSSSCHEDULE t where t.state=2 ";
        for (final String mon : weeks) {
            sql = String.valueOf(sql) + "( t.arrangementtime like '%" + mon + "%'" + " or ";
        }
        sql = sql.substring(0, sql.lastIndexOf("or"));
        System.out.println(String.valueOf(sql) + ")");
    }
}

