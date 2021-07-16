package kr.co.metisinfo.sharingcharger.utils;

import java.util.Map;

import kr.co.metisinfo.sharingcharger.base.WebServiceAPI;
import kr.co.metisinfo.sharingcharger.model.UserModel;
import kr.co.metisinfo.sharingcharger.network.RetrofitFactory;
import okhttp3.RequestBody;
import retrofit2.Response;

public class ApiUtils {

    private static final String TAG = ApiUtils.class.getSimpleName();

    private RetrofitFactory retrofitFactory = new RetrofitFactory();
    private WebServiceAPI webServiceAPI = retrofitFactory.build().create(WebServiceAPI.class);

    private CommonUtils cu = new CommonUtils();

    /**
     * 회원가입
     */
    public Response<Object> signUp(UserModel model) throws Exception {

        Response<Object> response = webServiceAPI.signUp(model).execute();

        return response;
    }

    /**
     * 로그인
     */
    public Response<Object> login(RequestBody email, RequestBody password) throws Exception {

        Response<Object> response = webServiceAPI.login(email, password).execute();

        return response;
    }

    /**
     * 비밀번호 변경(설정)
     */
    public Response<Object> passwordChange(UserModel model) throws Exception {

        Response<Object> response = webServiceAPI.passwordChange(model).execute();

        return response;
    }

    /**
     * 충전기 목록
     */
    public Response<Object> getChargers(UserModel model) throws Exception {

        Response<Object> response = webServiceAPI.getChargers(model).execute();

        return response;
    }

    /**
     * 선택된 충전기 상태
     */
    public Response<Object> getChargerStatus(UserModel model) throws Exception {

        Response<Object> response = webServiceAPI.getChargerStatus(model).execute();

        return response;
    }

    /**
     * 잔여 포인트
     */
    public Response<Object> pointLookup(UserModel model) throws Exception {

        Response<Object> response = webServiceAPI.pointLookup(model).execute();

        return response;
    }

    /**
     * 차감포인트
     */
    public Response<Object> getEstimatedPoints(UserModel model) throws Exception {

        Response<Object> response = webServiceAPI.getEstimatedPoints(model).execute();

        return response;
    }

    /**
     * 예약완료
     */
    public Response<Object> insertReservation(UserModel model) throws Exception {

        Response<Object> response = webServiceAPI.insertReservation(model).execute();

        return response;
    }

    /**
     * 예약취소
     */
    public Response<Object> deleteReservation(UserModel model) throws Exception {

        Response<Object> response = webServiceAPI.deleteReservation(model).execute();

        return response;
    }

    /**
     * 충전시작
     */
    public Response<Object> chargerUseCertification(UserModel model) throws Exception {

        Response<Object> response = webServiceAPI.chargerUseCertification(model).execute();

        return response;
    }

    /**
     * 충전종료
     */
    public Response<Object> endCharging(UserModel model) throws Exception {

        Response<Object> response = webServiceAPI.endCharging(model).execute();

        return response;
    }

    /**
     * 비정상 충전종료
     */
    public Response<Object> chargeNotTerminated(UserModel model) throws Exception {

        Response<Object> response = webServiceAPI.chargeNotTerminated(model).execute();

        return response;
    }

    /**
     * 충전 이력
     */
    public Response<Object> getChargerUsageHistory(UserModel model) throws Exception {

        Response<Object> response = webServiceAPI.getChargerUsageHistory(model).execute();

        return response;
    }

    /**
     * 포인트 이력
     */
    public Response<Object> getPointUsageHistory(UserModel model) throws Exception {

        Response<Object> response = webServiceAPI.getPointUsageHistory(model).execute();

        return response;
    }

    /**
     * 포인트 구매
     */
    public Response<Object> buyPoints(UserModel model) throws Exception {

        Response<Object> response = webServiceAPI.buyPoints(model).execute();

        return response;
    }

    /**
     * 회원 증명서
     */
    public Response<Object> getProofs(UserModel model) throws Exception {

        Response<Object> response = webServiceAPI.getProofs(model).execute();

        return response;
    }

    /**
     * 회원 탈퇴
     */
    public Response<Object> withdrawal(UserModel model) throws Exception {

        Response<Object> response = webServiceAPI.withdrawal(model).execute();

        return response;
    }

}
