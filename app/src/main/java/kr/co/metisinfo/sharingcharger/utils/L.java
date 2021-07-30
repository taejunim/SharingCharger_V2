package kr.co.metisinfo.sharingcharger.utils;

import android.util.Log;

import kr.co.metisinfo.sharingcharger.BuildConfig;

public class L {

    public static void d(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, getLoggerLocation() + message);
        }
    }

    public static void d(String TAG) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, getLoggerLocation());
        }
    }

    public static void v(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, getLoggerLocation() + message);
        }
    }

    public static void v(String TAG) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, getLoggerLocation());
        }
    }

    public static void w(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, getLoggerLocation() + message);
        }
    }

    public static void w(String TAG) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, getLoggerLocation());
        }
    }

    public static void i(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, getLoggerLocation() + message);
        }
    }

    public static void i(String TAG) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, getLoggerLocation());
        }
    }

    public static void e(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.e("metis", getLoggerLocation() + message);
        }
    }

    public static void e(String TAG) {
        if (BuildConfig.DEBUG) {
            Log.e("metis", getLoggerLocation());
        }
    }

    private static String getLoggerLocation() {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(ste.getFileName().substring(0, ste.getFileName().indexOf(".")));
        sb.append(" > ");
        sb.append(ste.getMethodName());
        sb.append(" > #");
        sb.append(ste.getLineNumber());
        sb.append("] ");

        return sb.toString();
    }
}
