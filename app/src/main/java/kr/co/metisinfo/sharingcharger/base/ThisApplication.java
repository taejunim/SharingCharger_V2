package kr.co.metisinfo.sharingcharger.base;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;

import kr.co.metisinfo.sharingcharger.model.UserModel;
import kr.co.metisinfo.sharingcharger.network.RetrofitHeader;
import kr.co.metisinfo.sharingcharger.network.RetrofitInterceptor;
import okhttp3.OkHttpClient;


public class ThisApplication extends Application {

    public static String uuid = "";
    public static String sessionToken = "";
    public static Context context;
    public static String userCd = "";
    public static UserModel staticUserModel;

    public static RetrofitHeader header = new RetrofitHeader();

    static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        uuid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

    }

    // 최초 로그인시에 사용할수도 있고 안할수도 있음.
    public static void setAddInterceptor() {
        httpClient.addInterceptor(new RetrofitInterceptor(header));
    }
}
