package kr.co.metisinfo.sharingcharger.utils;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

}

