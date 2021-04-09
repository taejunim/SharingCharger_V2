package kr.co.metisinfo.sharingcharger.base;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WebServiceAPI {

    // 키워드 검색
    @GET("/v2/local/search/keyword.json")
    Call<Object> getSearchKeyword(@Query("query") String query);

}
