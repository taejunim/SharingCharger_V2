package kr.co.metisinfo.sharingcharger.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtil {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    // Shared preferences file name
    String preferenceName = "SharingCharger_V2.0";

    public PreferenceUtil(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * 설정값 저장
     * @param key
     * @param value
     */
    public void putInt(String key , int value) {

        editor.putInt(key, value);
        editor.apply();
    }

    public void putString(String key , String value) {

        editor.putString(key, value);
        editor.apply();
    }

    public void putBoolean(String key , Boolean value) {

        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 설정 값 불려오기
     * @param key
     * @return value
     */

    public Integer getInt(String key) {

        return sharedPreferences.getInt(key,-1);
    }

    public String getString(String key) {

        return sharedPreferences.getString(key,"");
    }

    public Boolean getBoolean(String key) {

        return sharedPreferences.getBoolean(key,false);
    }

    /**
     * 삭제 하기
     * @param context
     * @param key
     */
    public static void delete (Context context , String key) {

        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mPref.edit();
        editor.remove(key);
        editor.clear();
        editor.apply();
    }
}
