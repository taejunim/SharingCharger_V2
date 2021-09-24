package kr.co.metisinfo.sharingcharger.base;

import android.app.Application;
import android.content.Context;

import kr.co.metisinfo.sharingcharger.model.UserModel;

public class ThisApplication extends Application {

    public static Context context;
    public static UserModel staticUserModel;



    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
    }
}
