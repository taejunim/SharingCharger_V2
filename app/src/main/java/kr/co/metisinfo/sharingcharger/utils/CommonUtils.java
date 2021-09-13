package kr.co.metisinfo.sharingcharger.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.model.CurrentReservationModel;
import kr.co.metisinfo.sharingcharger.model.ReservationModel;

public class CommonUtils {

    private static final String TAG = CommonUtils.class.getSimpleName();

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String val2(int str) {

        String val = Integer.toString(str);

        if (val.length() == 1)
            val = "0" + val;

        return val;
    }

    /**
     * 이메일 형식 체크
     * @param email 이메일
     * @return 이메일 형식 여부
     */
    public static boolean isValidEmail(String email) {

        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);

        if(m.matches()) {
            return true;
        }

        return false;
    }

    /**
     * 날짜 정렬
     * @param list      List<ReservationModel>
     * @return list     List<ReservationModel>
     */
    public List<ReservationModel> dateSort(List<ReservationModel> list) {

        Collections.sort(list, new Comparator<ReservationModel>() {
            @Override
            public int compare(ReservationModel s1, ReservationModel s2) {

                Date d1 = new Date();
                Date d2 = new Date();
                try {

                    String temp1 = s1.getStartDate().replaceAll("T", " ");
                    String temp2 = s2.getStartDate().replaceAll("T", " ");

                    System.out.println("temp1 : " + temp1);
                    System.out.println("temp2 : " + temp2);

                    d1 = format.parse(temp1);
                    d2 = format.parse(temp2);

                    System.out.println("d1 : " + format.parse(temp1));
                    System.out.println("d2 : " + format.parse(temp2));

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (d1.getTime() < d2.getTime()) {
                    return -1;
                } else if (d1.getTime() > d2.getTime()) {
                    return 1;
                }
                return 0;
            }
        });

        return list;
    }

    /**
     * 날짜 정렬
     * @param list      List<ReservationModel>
     * @return list     List<ReservationModel>
     */
    public List<ReservationModel> sortReservationList(List<ReservationModel> list) {

        Collections.sort(list, new Comparator<ReservationModel>() {
            @Override
            public int compare(ReservationModel s1, ReservationModel s2) {

                Date d1 = new Date();
                Date d2 = new Date();
                try {

                    String temp1 = s1.getStartDate().replaceAll("T", " ");
                    String temp2 = s2.getStartDate().replaceAll("T", " ");

                    d1 = format.parse(temp1);
                    d2 = format.parse(temp2);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (d1.getTime() < d2.getTime()) {
                    return -1;
                } else if (d1.getTime() > d2.getTime()) {
                    return 1;
                }
                return 0;
            }
        });

        return list;
    }

    /**
     * 예약되어 있는 시간이 현재시간 범위에 있는지 확인
     * @param model         ReservationModel
     * @return boolean      true, false
     * @throws Exception    Exception
     */
    public boolean checkRechargeTime(ReservationModel model) throws ParseException {

        String sDate = model.startDate;
        String eDate = model.endDate;

        sDate = sDate.substring(0, 4) + sDate.substring(5, 7) + sDate.substring(8, 10) + sDate.substring(11, 13) + sDate.substring(14, 16);
        eDate = eDate.substring(0, 4) + eDate.substring(5, 7) + eDate.substring(8, 10) + eDate.substring(11, 13) + eDate.substring(14, 16);

        SimpleDateFormat dateSet = new SimpleDateFormat("yyyyMMddHHmm");

        Date date = new Date();
        Date start_time = dateSet.parse(sDate);
        Date end_time = dateSet.parse(eDate);

        if (date.getTime() >= start_time.getTime() && date.getTime() <= end_time.getTime()) {
            Log.e("metis", "time :  정상");
            return true;
        } else {
            Log.e("metis", "time :  아님");
            return false;
        }
    }

    /**
     * String to Calendar
     * @param getDate         String
     * @return cal      Calendar
     * @throws Exception    Exception
     */
    public Calendar setCalendarDate(String getDate){

        Calendar cal = Calendar.getInstance();

        cal.set(Integer.parseInt(getDate.substring(0, 4)), Integer.parseInt(getDate.substring(5, 7)), Integer.parseInt(getDate.substring(8, 10)), Integer.parseInt(getDate.substring(11, 13)), Integer.parseInt(getDate.substring(14, 16)));
        cal.add(Calendar.MONTH, -1);

        return cal;
    }

    /**
     * sec를 hh:mm:ss로 변환
     * @param sec       sec
     * @return          hh:mm:ss
     */
    public String chargingTime(int sec) {

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        Calendar timeCal= Calendar.getInstance();
        timeCal.set(Calendar.HOUR_OF_DAY,0);
        timeCal.set(Calendar.MINUTE,0);
        timeCal.set(Calendar.SECOND,sec);

        return timeFormat.format(timeCal.getTime());
    }

    /**
     * sec 시간 계산(yyyy-MM-dd HH:mm:ss)
     * @param startTime     yyyy-MM-dd HH:mm:ss
     * @param sec           sec
     * @return              yyyy-MM-dd HH:mm:ss
     * @throws Exception    Exception
     */
    public String timeSecCalculation(String startTime, int sec) throws Exception {

        Calendar cal = Calendar.getInstance();
        cal.setTime(format.parse(startTime));
        cal.add(Calendar.SECOND, sec);

        return format.format(cal.getTime());
    }

    /**
     * 날짜 포맷
     * @param getDate     yyyy-MM-ddTHH:mm:ss
     * @return temp       yyyy-MM-ddTHH:mm
     */
    public String setDateFormat(String getDate){

        String temp = "";

        temp = getDate;
        temp = temp.replace("T", " ");
        temp = temp.substring(0, temp.length() - 3);

        return temp;
    }

    /**
     * 초 계산
     * @param oldTime       yyyy-MM-dd HH:mm:ss
     * @param chargerTime   chargerTime
     * @return sec          sec
     * @throws Exception    Exception
     */
    public int getSecond(String oldTime, String chargerTime) throws Exception {

        Date oldDate = format.parse(oldTime);
        Date nowDate = new Date();
        Log.e("metis", "oldDate : " + format.format(oldDate));
        Log.e("metis", "nowDate : " + format.format(nowDate));

        long diff = nowDate.getTime() - oldDate.getTime();
        long second = diff / 1000;

        return Integer.parseInt(chargerTime) * 60 - (int) second;
    }

    //동적으로 layoutParam 설정을 위해 float -> DP 로 변경
    public int convertToDp(Float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, ThisApplication.context.getResources().getDisplayMetrics());
    }

    //기기의 너비구해서 60% 값 리턴
    public int getPercentHeight(Activity activity, int percent) {

        Display display = activity.getWindowManager().getDefaultDisplay();  // in Activity

        Point size = new Point();
        display.getRealSize(size); // or getSize(size)
        int height = size.y;
        return height * percent / 100;
    }
}

