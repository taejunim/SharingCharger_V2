package kr.co.metisinfo.sharingcharger.base;

import java.util.Map;

import kr.co.metisinfo.sharingcharger.model.UserModel;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface WebServiceAPI {

    // 회원가입
    @POST("/user/v1/signup")
    Call<Object> signUp(@Body UserModel user);

    // 로그인
    @Multipart
    @POST("/user/login")
    Call<Object> login(@Part("email") RequestBody email, @Part("password") RequestBody password);

    // 비밀번호 변경(설정)
    @PUT("/user/v1/password")
    Call<Object> passwordChange(@Body UserModel user);


    // 충전기 목록
    @GET("/shared-charger/v1")
    Call<Object> getChargers(@Body UserModel user);

    // 선택된 충전기 상태
    @POST("/shared-charger/v1/charger-status")
    Call<Object> getChargerStatus(@Body UserModel user);

    // 잔여 포인트
    @GET("/ElectricWalletmanagement/PointLookup")
    Call<Object> pointLookup(@Body UserModel user);

    // 차감포인트
    @GET("/shared-charger/v1/points")
    Call<Object> getEstimatedPoints(@Body UserModel user);

    // 예약완료
    @POST("/shared-charger/v1/reservation")
    Call<Object> insertReservation(@Body UserModel user);

    // 예약취소
    @DELETE("/shared-charger/v1/reservation")
    Call<Object> deleteReservation(@Body UserModel user);

    // 충전시작
    @POST("/shared-charger/v1/charger-use-certification")
    Call<Object> chargerUseCertification(@Body UserModel user);

    // 충전종료
    @POST("/shared-charger/v1/end-charging")
    Call<Object> endCharging(@Body UserModel user);

    // 비정상 충전종료
    @POST("/shared-charger/v1/charge-not-terminated")
    Call<Object> chargeNotTerminated(@Body UserModel user);

    // 충전이력
    @GET("/shared-charger/v1/history")
    Call<Object> getChargerUsageHistory(@Body UserModel user);

    // 포인트 이력
    @GET("/ElectricWalletmanagement/PointUsageHistory")
    Call<Object> getPointUsageHistory(@Body UserModel user);

    // 포인트 구매
    @POST("/ElectricWalletmanagement/BuyPoints")
    Call<Object> buyPoints(@Body UserModel user);

    // 회원 증명서
    @GET("/user/v1/proofs")
    Call<Object> getProofs(@Body UserModel user);

    // 회원 탈퇴
    @POST("/user/v1/withdrawal")
    Call<Object> withdrawal(@Body UserModel user);

    // 키워드 검색
    @GET("/v2/local/search/keyword.json")
    Call<Object> getSearchKeyword(@Query("query") String query);

}
