package kr.co.metisinfo.sharingcharger.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {
    public static final String DATE_SIMPLE = "yyyy-MM-dd";
    public static final String DATE_SIMPLE_JS = "yyyyMMdd";

    public static final String TIME_SIMPLE = "HH:mm";
    public static final String TIME_HMS = "HH:mm:ss";

    public static final String FULL_DATE_SIMPLE_JS = "yyyyMMddHHmm";

    public static final String FULL_DATE_WITH_MILLISECOND_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final SimpleDateFormat hmFormat = new SimpleDateFormat(TIME_SIMPLE);
    public static final SimpleDateFormat hmsFormat = new SimpleDateFormat(TIME_HMS);

    public static final SimpleDateFormat fullDateWithMillisecondFormat = new SimpleDateFormat(FULL_DATE_WITH_MILLISECOND_FORMAT);

    public static String getFullDateWithMillisecond(Date date) {

        return fullDateWithMillisecondFormat.format(date);
    }

    public static final String convertToHHMM(String originDateString) {

        Date originDate = null;

        try {
            originDate = hmsFormat.parse(originDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return hmFormat.format(originDate);

    }

    public static String nowDateTime() {

        return getDateYyyy() + CommonUtils.val2(getDateMm()) + CommonUtils.val2(getDateDd()) + getDateHour() + getDateMinute();
    }

    /**
     * 현재 년 - YYYY
     */
    public static int getDateYyyy() {

        Calendar cal = Calendar.getInstance(Locale.KOREA);

        return cal.get(Calendar.YEAR);
    }

    /**
     * 현재 월 - mm
     */
    public static int getDateMm() {

        Calendar cal = Calendar.getInstance(Locale.KOREA);

        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 현재 일 - dd
     */
    public static int getDateDd() {

        return Calendar.getInstance(Locale.KOREA).get(Calendar.DATE);
    }

    /**
     * 현재 시각 - hh
     *
     * @return
     */
    public static String getDateHour() {

        return String.format("%02d", Calendar.getInstance(Locale.KOREA).get(Calendar.HOUR_OF_DAY));
    }

    /**
     * 현재 분 - mm
     *
     * @return
     */
    public static String getDateMinute() {

        return String.format("%02d", Calendar.getInstance(Locale.KOREA).get(Calendar.MINUTE));
    }

    /**
     * 특정 일자의 요일 가져오기
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static String getWeek(String date) {

        String day = "";

        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_SIMPLE_JS, Locale.KOREA);
            Date nDate = dateFormat.parse(date);

            Calendar cal = Calendar.getInstance();
            cal.setTime(nDate);

            int dayNum = cal.get(Calendar.DAY_OF_WEEK);

            switch (dayNum) {
                case 1:
                    day = "(일)";
                    break;
                case 2:
                    day = "(월)";
                    break;
                case 3:
                    day = "(화)";
                    break;
                case 4:
                    day = "(수)";
                    break;
                case 5:
                    day = "(목)";
                    break;
                case 6:
                    day = "(금)";
                    break;
                case 7:
                    day = "(토)";
                    break;

            }

            return day;
        } catch (Exception e) {

            return "";
        }
    }

    public static String setOperationDate(String mode, int value, String format) {

        SimpleDateFormat fmt;

        switch (format) {
            case DATE_SIMPLE:
                fmt = new SimpleDateFormat(DATE_SIMPLE, Locale.KOREA);
                break;
            case DATE_SIMPLE_JS:
            default:
                fmt = new SimpleDateFormat(DATE_SIMPLE_JS, Locale.KOREA);
                break;
        }

        GregorianCalendar cal = new GregorianCalendar();

        switch (mode) {
            case "plus":
                cal.add(GregorianCalendar.DATE, value); //현재날짜에 value 값을 더한다.
                break;
            case "minus":
                cal.add(GregorianCalendar.DATE, -value); //현재날짜에 value 값을 뺀다.
                break;
            default:
                break;
        }

        return fmt.format(cal.getTime());
    }

    /**
     * 시간을 더하여 시간을 계산
     *
     * @param time 더할 분(minute)
     * @return
     */
    public static String addTimeGetTime(int time) {

        SimpleDateFormat fmt = new SimpleDateFormat(TIME_SIMPLE, Locale.KOREA);

        GregorianCalendar cal = new GregorianCalendar();

        cal.add(GregorianCalendar.MINUTE, time); //현재 시간에 value 값을 더한다.

        return fmt.format(cal.getTime());
    }

    public static long getCompareTwoDate(String sDate, String eDate) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FULL_DATE_SIMPLE_JS, Locale.KOREA);

        try {

            Date startDate = simpleDateFormat.parse(sDate);
            Date endDate = simpleDateFormat.parse(eDate);

            return (endDate.getTime() - startDate.getTime()) / (1000 * 60);
        } catch (ParseException pe) {

            return 0;
        }
    }

    /**
     * 분 을 일 + 시간 + 분 으로 변환
     * @param time 분
     * @return ex)  150 -> 2시간 30분
     */
    public static String generateTimeToString(int time) {

        int day, hour, minute;

        day = time / 60 / 24;
        hour = time / 60 % 24;
        minute = time % 60;

        String temp;

        temp = day >= 1 ? day + "일 " : "";
        temp += hour >= 1 ? hour + "시간 " : "";
        temp += minute >= 1 ? minute + "분" : "";

        return temp;
    }

    /**
     * 특정 일시에 특정 분을 더한 일시를 리턴한다.
     * @param standardDate  기준 일시
     * @param addMinute     더할 분
     * @return              기준 일시에 분(minute)를 더한 일자
     */
    public static String dateAddTime(String standardDate, int addMinute) {

        try {
            DateFormat date = new SimpleDateFormat(FULL_DATE_SIMPLE_JS);

            Calendar cal = Calendar.getInstance();

            Date regDate = date.parse(standardDate);
            cal.setTime(regDate);

            cal.add(Calendar.MINUTE, addMinute);

            return date.format(cal.getTime());

        } catch (ParseException pe) {

            return standardDate;
        }
    }
}
