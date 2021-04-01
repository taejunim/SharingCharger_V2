package kr.co.metisinfo.sharingcharger.base;

import kr.co.metisinfo.sharingcharger.model.AuthenticateModel;
import kr.co.metisinfo.sharingcharger.model.PointModel;
import kr.co.metisinfo.sharingcharger.model.RechargeEndModel;
import kr.co.metisinfo.sharingcharger.model.ReservationModel;
import kr.co.metisinfo.sharingcharger.model.UserModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebServiceAPI {

    // 회원가입
    @POST("/api/v1/join")
    Call<UserModel> join(@Body UserModel user);

    // 로그인
    @POST("/api/v1/login")
    Call<UserModel> login(@Body UserModel user);

    // 예약조회
    @GET("/api/v1/reservation/user/{userId}/currently")
    Call<Object> getUserReservation(@Path("userId") int userId);

    // 즐겨찾기 조회(충전기 정보 조회)
    @GET("/api/v1/app/chargers/{id}")
    Call<Object> getChargerInfo(@Path("id") int chargerId);

    // 포인트 등록
    @POST("/api/v1/point")
    Call<PointModel> insertPoint(@Body PointModel pointModel);

    // 예상 포인트 계산을 가져오기 위한 API
    @GET("/api/v1/point/chargers/{chargerId}/calculate")
    Call<Object> getExpectPoint(@Path("chargerId") String chargerId, @Query("startDate") String startDate, @Query("endDate") String endDate);

    // 충전기 예약
    @POST("/api/v1/reservation")
    Call<ReservationModel> insertReservation(@Body ReservationModel reservation);

    // 충전기 예약취소
    @PUT("/api/v1/reservations/{id}/cancel")
    Call<ReservationModel> cancelReservation(@Path("id") String reservationId);

    // 현재 포인트 조회
    @GET("/api/v1/point/users/{userId}")
    Call<Object> getUserPoint(@Path("userId") int userId);

    // 포인트 이력조회
    @GET("/api/v1/point/users/{userId}/history")
    Call<Object> getPoints(@Path("userId") int userId, @Query("startDate") String startDate, @Query("endDate") String endDate, @Query("sort") String sort, @Query("pointUsedType") String pointUsedType, @Query("page") int page, @Query("size") int size);

    // 충전 이력조회
    @GET("/api/v1/recharges/users/{userId}/history")
    Call<Object> getRecharges(@Path("userId") int userId, @Query("startDate") String startDate, @Query("endDate") String endDate, @Query("sort") String sort, @Query("page") int page, @Query("size") int size);

    // 충전 시작전 인증
    @POST("/api/v1/recharge/authenticate/charger/{chargerId}")
    Call<Object> getAuthenticateCharger(@Path("chargerId") int chargerId, @Body AuthenticateModel authenticateModel);

    // 충전 시작 인증
    @POST("/api/v1/recharge/start/charger/{chargerId}")
    Call<Object> startAuthenticateCharger(@Path("chargerId") int chargerId, @Body AuthenticateModel authenticateModel);

    // 충전 종료
    @PUT("/api/v1/recharge/end/charger/{chargerId}")
    Call<Object> endAuthenticateCharger(@Path("chargerId") int chargerId, @Body RechargeEndModel rechargeEndModel);

    // 충전 비종료
    @PUT("/api/v1/recharge/end/charger/{chargerId}/unplanned")
    Call<Object> endAuthenticateChargerUnplanned(@Path("chargerId") int chargerId, @Body RechargeEndModel rechargeEndModel);

    // 충전기 예약 목록 리스트 조회
    @GET("/api/v1/reservations/chargers/{chargerId}")
    Call<Object> getReservationsChargersList(@Path("chargerId") String chargerId, @Query("page") int page, @Query("size") int size, @Query("sort") String sort);

    // 키워드 검색
    @GET("/v2/local/search/keyword.json")
    Call<Object> getSearchKeyword(@Query("query") String query);

    // SMS 인증 정보를 가져오기 위한 API
    @GET("/api/v1/sms/{phone}")
    Call<Object> getSms(@Path("phone") String phone);

    // 개인정보처리방침 약관 정보를 가져오기 위한 API
    @GET("/api/v1/policy/privacy")
    Call<ResponseBody> getPolicyPrivacy();

    // 서비스 이용약관 정보를 가져오기 위한 API
    @GET("/api/v1/policy/service")
    Call<ResponseBody> getPolicyCollect();

    // [APP] 충전기 조회를 위한 API[조건검색]
    @GET("/api/v1/app/chargers")
    Call<Object> getChargers(@Query("startDate") String startDate, @Query("endDate") String endDate, @Query("gpsX") double gpsX, @Query("gpsY") double gpsY, @Query("distance") String distance, @Query("price") String price);

    // 사용자 비밀번호 정보 수정 api
    @PATCH("/api/v1/change/password/{id}")
    Call<Object> changePassword(@Path("id") String id, @Body UserModel userModel);

    // 특정 소유자의 충전기 조회를 위한 API
    @GET("/api/v1/chargers/owner/{hostId}/{hostType}")
    Call<Object> getChargersOwner(@Path("hostId") String hostId, @Path("hostType") String hostType, @Query("acceptType") String acceptType, @Query("currentStatusType") String currentStatusType, @Query("page") int page, @Query("size") int size, @Query("sort") String sort);

}
