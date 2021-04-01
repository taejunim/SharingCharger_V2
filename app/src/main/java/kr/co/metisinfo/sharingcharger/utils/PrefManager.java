package kr.co.metisinfo.sharingcharger.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "androidhive-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_CHECK_PERMISSION = "checkPermissionOK";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setCheckPermission(boolean isFirstTime) {
        editor.putBoolean(IS_CHECK_PERMISSION, isFirstTime);
        editor.commit();
    }

    public boolean isCheckPermission() {
        return pref.getBoolean(IS_CHECK_PERMISSION, true);
    }


    /**
     * 설정값 저장
     * @param act
     * @param key
     * @param value
     */
    public static void save(Context act , String key , int value) {

        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(act);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void saveString(Context act , String key , String value) {

        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(act);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 설정 값 불려오기
     * @param act
     * @param key
     * @param defaultVal
     * @return
     */
    public static int clallValue(Context act , String key, int defaultVal) {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(act);

        return mPref.getInt(key, defaultVal);

    }
    public static String clallValueString(Context act , String key, String defaultVal) {

        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(act);

        return mPref.getString(key, defaultVal);

    }

    /**
     * 삭제 하기
     * @param act
     * @param key
     */
    public static void delete (Context act , String key) {
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(act);
        SharedPreferences.Editor editor = mPref.edit();
        editor.remove(key);
        editor.clear();
        editor.commit();

    }

}
