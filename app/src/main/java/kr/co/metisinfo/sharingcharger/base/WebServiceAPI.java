package kr.co.metisinfo.sharingcharger.base;

import kr.co.metisinfo.sharingcharger.model.UserModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WebServiceAPI {

    // 회원가입
    @POST("/user/v1/signup")
    Call<Object> signUp(@Body UserModel user);

    // 로그인
    // /user​/v1​/login

    // 비밀번호 변경(설정)
    // /user/v1/password

    // 충전기 목록
    // /shared-charger​/v1

    // 선택된 충전기 상태
    // /shared-charger/v1/charger-status

    // 잔여 포인트
    // /ElectricWalletmanagement/PointLookup

    // 차감포인트
    // /shared-charger​/v1​/points

    // 예약완료
    // /shared-charger/v1/reservation

    // 예약취소
    // /shared-charger​/v1​/reservation

    // 충전시작
    // /shared-charger/v1/charger-use-certification

    // 충전종료
    // /shared-charger/v1/end-charging

    // 비정상 충전종료
    // /shared-charger/v1/charge-not-terminated

    // 충전이력
    // /shared-charger/v1/history

    // 포인트 이력
    // /ElectricWalletmanagement/PointUsageHistory

    // 포인트 구매
    // /ElectricWalletmanagement​/BuyPoints

    // 회원 증명서
    // /user/v1/proofs

    // 회원 탈퇴
    // /user/v1/withdrawal

    // 키워드 검색
    @GET("/v2/local/search/keyword.json")
    Call<Object> getSearchKeyword(@Query("query") String query);

}
