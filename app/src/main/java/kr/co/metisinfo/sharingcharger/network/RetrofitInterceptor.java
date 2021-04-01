package kr.co.metisinfo.sharingcharger.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetrofitInterceptor implements Interceptor {
    private final RetrofitHeader header;

    public RetrofitInterceptor(RetrofitHeader header) {
        this.header = header;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request original = chain.request();
        Response response;

        if (header.headerValue != null) {
            Request request = original.newBuilder()
                    .header(header.headerName, header.headerValue)
                    .method(original.method(), original.body())
                    .build();
            response = chain.proceed(request);
        } else {
            response = chain.proceed(original);
        }

        return response;
    }
}
