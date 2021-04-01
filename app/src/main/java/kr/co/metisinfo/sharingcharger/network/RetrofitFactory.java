package kr.co.metisinfo.sharingcharger.network;


import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import kr.co.metisinfo.sharingcharger.BuildConfig;
import kr.co.metisinfo.sharingcharger.base.Constants;
import kr.co.metisinfo.sharingcharger.utils.L;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private static final String TAG = RetrofitFactory.class.getSimpleName();

    public Retrofit build() {

        return build(null);
    }

    public Retrofit buildKakao() {

        return build_KAKAO_SEARCH_KEYWORD();
    }

    public Retrofit build(String cookies) {

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        if (!TextUtils.isEmpty(cookies)) {
            clientBuilder.addInterceptor(chain -> {
                Map<String, String> headerMap = getDefaultHeader();

                headerMap.put("Cookie", cookies);

                if (BuildConfig.DEBUG) {
                    L.d(TAG, "--> HEADER");
                    for (String key : headerMap.keySet()) {
                        L.d(TAG, key + " : " + headerMap.get(key));
                    }
                    L.d(TAG, "--> END HEADER");
                }

                Headers headers = Headers.of(headerMap);
                Request request = chain.request();

                return chain.proceed(request.newBuilder()
                        .headers(headers)
                        .method(request.method(), request.body())
                        .build());
            });
        }

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        return new Retrofit.Builder()
                .baseUrl(BuildConfig.DEBUG ? Constants.DEV_HOST : Constants.HOST)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public Retrofit build_KAKAO_SEARCH_KEYWORD() {

        OkHttpClient clientBuilder = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader(Constants.HEADER_AUTH_KAKAO_SEARCH_KEYWORD, Constants.HEADER_KEY_AUTH_KAKAO_SEARCH_KEYWORD)
                        .build();
                return chain.proceed(request);

            }
        }).build();

        return new Retrofit.Builder()
                .baseUrl(Constants.KAKAO_SEARCH_KEYWORD_HOST)
                .client(clientBuilder)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

    }

    private Map<String, String> getDefaultHeader() {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(Constants.HEADER_KEY_USER_AGENT_FOR_WEB, Constants.HEADER_USER_AGENT);
        headerMap.put(Constants.HEADER_KEY_USER_AGENT, Constants.HEADER_USER_AGENT);
        headerMap.put(Constants.HEADER_KEY_CONTENT_TYPE, Constants.HEADER_CONTENT_TYPE);
        headerMap.put(Constants.HEADER_KEY_LOGIN_TYPE_CD, Constants.HEADER_LOGIN_TYPE_CD);
        headerMap.put(Constants.HEADER_KEY_DEVICE_UUID, "ffffffff-f980-c439-c32c-ad9000000000");
        headerMap.put(Constants.HEADER_AUTH_KAKAO_SEARCH_KEYWORD, Constants.HEADER_KEY_AUTH_KAKAO_SEARCH_KEYWORD);

        return headerMap;
    }
}
