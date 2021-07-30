package kr.co.metisinfo.sharingcharger.base;

import androidx.room.Ignore;

import kr.co.metisinfo.sharingcharger.model.AuthenticateModel;
import kr.co.metisinfo.sharingcharger.model.PointModel;
import kr.co.metisinfo.sharingcharger.model.RechargeEndModel;
import kr.co.metisinfo.sharingcharger.model.ReservationModel;
import kr.co.metisinfo.sharingcharger.model.UserModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebServiceAPI {

    // 회원가입
    @POST("/api/v1/join")
    Call<UserModel> signUp(@Body UserModel user);

    // 개인정보처리방침 약관 정보를 가져오기 위한 API
    @GET("/api/v1/policy/privacy")
    Call<ResponseBody> getPolicyPrivacy();

    // 서비스 이용약관 정보를 가져오기 위한 API
    @GET("/api/v1/policy/service")
    Call<ResponseBody> getPolicyCollect();

    // 로그인
    @POST("/api/v1/login")
    Call<UserModel> login(@Body UserModel user);

    // 비밀번호 리셋
    @PATCH("/api/v1/reset/password/{username}")
    Call<Object> passwordReset(@Path("username") String username, @Body UserModel userModel);

    // 비밀번호 변경(설정)
    @PUT("/api/v1/change/password/{username}")
    Call<Object> passwordChange(@Path("username") String username, @Body UserModel userModel);

    // 예약조회
    @GET("/api/v1/reservation/user/{userId}/currently")
    Call<Object> getUserReservation(@Path("userId") int userId);

    // 충전 종료
    @PUT("/api/v1/recharge/end/charger/{chargerId}")
    Call<Object> endAuthenticateCharger(@Path("chargerId") int chargerId, @Body RechargeEndModel rechargeEndModel);

    // 충전 비종료
    @PUT("/api/v1/recharge/end/charger/{chargerId}/unplanned")
    Call<Object> endAuthenticateChargerUnplanned(@Path("chargerId") int chargerId, @Body RechargeEndModel rechargeEndModel);

    // 충전 시작전 인증
    @POST("/api/v1/recharge/authenticate/charger/{chargerId}")
    Call<Object> getAuthenticateCharger(@Path("chargerId") int chargerId, @Body AuthenticateModel authenticateModel);

    // 충전 시작 인증
    @POST("/api/v1/recharge/start/charger/{chargerId}")
    Call<Object> startAuthenticateCharger(@Path("chargerId") int chargerId, @Body AuthenticateModel authenticateModel);

    // 충전기 예약 목록 리스트 조회
    @GET("/api/v1/reservations/chargers/{chargerId}")
    Call<Object> getReservationsChargersList(@Path("chargerId") String chargerId, @Query("page") int page, @Query("size") int size, @Query("sort") String sort);

    // 충전기 예약취소
    @PUT("/api/v1/reservations/{id}/cancel")
    Call<ReservationModel> cancelReservation(@Path("id") String reservationId);

    // 현재 포인트 조회
    @GET("/api/v1/point/users/{userId}")
    Call<Object> getUserPoint(@Path("userId") int userId);

    // 예상 포인트 계산을 가져오기 위한 API
    @GET("/api/v1/point/chargers/{chargerId}/calculate")
    Call<Object> getExpectPoint(@Path("chargerId") String chargerId, @Query("startDate") String startDate, @Query("endDate") String endDate);

    // 포인트 등록
    @POST("/api/v1/point")
    Call<PointModel> insertPoint(@Body PointModel pointModel);

    // [APP] 충전기 조회를 위한 API[조건검색]
    @GET("/api/v1/app/chargers")
    Call<Object> getChargers(@Query("startDate") String startDate, @Query("endDate") String endDate, @Query("gpsX") double gpsX, @Query("gpsY") double gpsY, @Query("distance") String distance, @Query("price") String price);

    // 선택된 충전기 상태
    @POST("/shared-charger/v1/charger-status")
    Call<Object> getChargerStatus(@Body UserModel user);

    // 잔여 포인트
    @GET("/ElectricWalletmanagement/PointLookup")
    Call<Object> pointLookup(@Body UserModel user);

    // 차감포인트
    @GET("/shared-charger/v1/points")
    Call<Object> getEstimatedPoints(@Body UserModel user);

    // 충전기 예약
    @POST("/api/v1/reservation")
    @Ignore
    Call<ReservationModel> insertReservation(@Body ReservationModel reservation);

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
