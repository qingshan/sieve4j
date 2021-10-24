package com.surfront.sieve.matcher;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimeMatcher implements Matcher, Serializable {
    public static final String[] WEEKDAYS = new String[]{
            "Mon",
            "Tue",
            "Wed",
            "Thu",
            "Fri",
            "Sat",
            "Sun",
    };

    protected Date startDate;
    protected Date endDate;
    protected boolean[] weekdays;
    protected int[] startMinutes;
    protected int[] endMinutes;

    public TimeMatcher(String pattern) {
        //2010-12-21..2010-12-27 Mon,Tue,Wed,Thu,Fri 01:00-09:00,12:00-23:00
        String[] values = split(pattern, " ");
        Date startDate = null;
        Date endDate = null;
        String[] weekdays = null;
        String[] timeframes = null;
        for (String value : values) {
            if (value.contains("..")) {
                int index = value.indexOf("..");
                startDate = index == 0 ? null : parseDate(value.substring(0, index));
                endDate = index == value.length() - 2 ? null : parseDate(value.substring(index + 2));
            } else {
                String[] tokens = split(value, ",");
                if (isWeekdays(tokens)) {
                    weekdays = tokens;
                } else {
                    timeframes = tokens;
                }
            }
        }
        if (weekdays == null) {
            weekdays = WEEKDAYS;
        }
        if (timeframes == null) {
            timeframes = new String[] {"00:00-24:00"};
        }
        this.startDate = startDate;
        this.endDate = endDate;
        this.weekdays = getWeekdays(weekdays);
        int[] startMinutes = new int[timeframes.length];
        int[] endMinutes = new int[timeframes.length];
        for (int i = 0; i < timeframes.length; i++) {
            String frame = timeframes[i];
            if (frame != null) {
                int index = frame.indexOf('-');
                startMinutes[i] = getMinutes(frame.substring(0, index));
                endMinutes[i] = getMinutes(frame.substring(index + 1));
            }
        }
        this.startMinutes = startMinutes;
        this.endMinutes = endMinutes;
    }

    public TimeMatcher(Date startDate, Date endDate, String[] weekdays, String[] timeframes) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.weekdays = getWeekdays(weekdays);
        int[] startMinutes = new int[timeframes.length];
        int[] endMinutes = new int[timeframes.length];
        for (int i = 0; i < timeframes.length; i++) {
            String frame = timeframes[i];
            if (frame != null) {
                int index = frame.indexOf('-');
                startMinutes[i] = getMinutes(frame.substring(0, index));
                endMinutes[i] = getMinutes(frame.substring(index + 1));
            }
        }
        this.startMinutes = startMinutes;
        this.endMinutes = endMinutes;
    }

    public boolean match(String text) {
        return match(parseDateTime(text));
    }

    public boolean match(Date date) {
        if (date == null) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekday = calendar.get(Calendar.DAY_OF_WEEK) -2;
        if (weekday < 0) {
            //It's sunday
            weekday = 6;
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int minutes = hour * 60 + minute;
        calendar.clear();
        calendar.set(year, month - 1, day);
        if (!contains(startDate, endDate, calendar.getTime())) {
            return false;
        }
        return match(weekday, minutes);
    }

    private boolean match(int weekday, int minutes) {
        if (weekdays[weekday]) {
            if (isBetween(startMinutes, endMinutes, minutes)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isBetween(int[] lowValues, int[] highValues, int value) {
        for (int i = 0; i < lowValues.length; i++) {
            if (value >= lowValues[i] && value <= highValues[i]) {
                return true;
            }
        }
        return false;
    }

    private static int getMinutes(String time) {
        int index = time.indexOf(":");
        int hour = Integer.parseInt(time.substring(0, index));
        int minute = Integer.parseInt(time.substring(index + 1));
        return hour * 60 + minute;
    }

    private static boolean isWeekdays(String[] days) {
        for (String day : days) {
            if (indexOf(WEEKDAYS, day) == -1) {
                return false;
            }
        }
        return true;
    }

    private static boolean[] getWeekdays(String[] days) {
        boolean[] weekdays = new boolean[7];
        for (String day : days) {
            int index = indexOf(WEEKDAYS, day);
            weekdays[index] = true;
        }
        return weekdays;
    }

    private static boolean contains(Date startDate, Date endDate, Date date) {
        if (startDate != null && date.before(startDate)) {
            return false;
        }
        if (endDate != null && date.after(endDate)) {
            return false;
        }
        return true;
    }

    private static <T> int indexOf(T[] values, T value) {
        for (int i = 0; i < values.length; i++) {
            if (value.equals(values[i])) {
                return i;
            }
        }
        return -1;
    }

    private static String[] split(String text, String delimiter) {
        if (text == null) {
            return new String[0];
        }
        List<String> values = new ArrayList<String>();
        for (StringTokenizer st = new StringTokenizer(text, delimiter); st.hasMoreTokens();) {
            String value = st.nextToken();
            values.add(value);
        }
        return values.toArray(new String[0]);
    }

    private static Date parseDate(String date) {
        return parse("yyyy-MM-dd", date);
    }

    private static Date parseDateTime(String date) {
        return parse("yyyy-MM-dd HH:mm:ss", date);
    }

    private static Date parse(String pattern, String date) {
        if (date == null) {
            return null;
        }
        try {
            DateFormat format = new SimpleDateFormat(pattern);
            return format.parse(date);
        } catch (Exception e) {
            return null;
        }
    }
}

