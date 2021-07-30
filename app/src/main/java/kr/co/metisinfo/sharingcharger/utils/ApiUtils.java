package kr.co.metisinfo.sharingcharger.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.metisinfo.sharingcharger.base.Constants;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.base.WebServiceAPI;
import kr.co.metisinfo.sharingcharger.model.AuthenticateModel;
import kr.co.metisinfo.sharingcharger.model.ChargerModel;
import kr.co.metisinfo.sharingcharger.model.PointModel;
import kr.co.metisinfo.sharingcharger.model.RechargeEndModel;
import kr.co.metisinfo.sharingcharger.model.RechargeModel;
import kr.co.metisinfo.sharingcharger.model.ReservationModel;
import kr.co.metisinfo.sharingcharger.model.UserModel;
import kr.co.metisinfo.sharingcharger.network.RetrofitFactory;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ApiUtils {

    private static final String TAG = ApiUtils.class.getSimpleName();

    private RetrofitFactory retrofitFactory = new RetrofitFactory();
    private WebServiceAPI webServiceAPI = retrofitFactory.build().create(WebServiceAPI.class);

    private CommonUtils cu = new CommonUtils();

    /**
     * 회원가입
     */
    public UserModel signUp(UserModel userModel) throws Exception {

        UserModel model = null;

        Response<UserModel> response = webServiceAPI.signUp(userModel).execute();

        if (response.code() == 201 && response.body() != null) {

            model = response.body();
        }

        return model;
    }

    /**
     * 정보 동의
     **/
    public String getPolicy(String getTagName) throws Exception {

        String getText = null;

        Response<ResponseBody> response;

        //서비스 이용약관 정보
        if (getTagName.equals("PersonalInfo1")) {
            response = webServiceAPI.getPolicyCollect().execute();
        }
        //개인정보처리방침 약관 정보
        else {
            response = webServiceAPI.getPolicyPrivacy().execute();
        }

        if (response.code() == 200) {

            //바로 String 담아서 loadData 시 문자열이 잘림
            ByteBuffer buffer = ByteBuffer.allocate((int) response.body().contentLength());
            buffer.put(response.body().bytes());
            byte[] bytes = new byte[buffer.position()];
            buffer.flip();
            buffer.get(bytes);

            getText = new String(bytes);

        }

        return getText;
    }

    /**
     * 로그인
     */
    public Response<UserModel> login(UserModel model) throws Exception {

        Response<UserModel> response = webServiceAPI.login(model).execute();

        return response;
    }

    /**
     * 비밀번호 리셋
     **/
    public Response<Object> passwordReset(String username, UserModel userModel) throws Exception {

        Response<Object> response = webServiceAPI.passwordReset(username, userModel).execute();

        return response;
    }

    /**
     * 비밀번호 변경(설정)
     */
    public Response<Object> passwordChange(String username, UserModel model) throws Exception {

        Response<Object> response = webServiceAPI.passwordChange(username, model).execute();

        return response;
    }

    /**
     * 예약 상태 확인
     **/
    public ReservationModel getReservationStatus() {

        ReservationModel model = null;

        try {
            Response<Object> response = webServiceAPI.getUserReservation(ThisApplication.staticUserModel.id).execute();

            if (response.code() == 200) {

                JSONObject json = new JSONObject((Map) response.body());

                Gson gson = new Gson();

                model = gson.fromJson(json.toString(), ReservationModel.class);
            }

        } catch (Exception e) {
            Log.e("metis", " getReservationStatus Exception1 : " + e);

            return model;
        }

        return model;
    }

    /**
     * 충전기 예약 목록 리스트 조회
     **/
    public Response<Object> getReservationsChargersList(String chargerId) throws Exception {

        Response<Object> response = webServiceAPI.getReservationsChargersList(chargerId, 1, 10, "ASC").execute();

        return response;
    }

    /**
     * 충전 예약 취소
     **/
    public boolean cancelReservation(String reservationId) throws Exception {

        Response<ReservationModel> response = webServiceAPI.cancelReservation(reservationId).execute();

        if(response.code() == 200){

            return true;
        }

        return false;
    }

    /**
     * 실시간 유저 포인트
     **/
    public int getUserPoint() throws Exception {

        Response<Object> getPoint = webServiceAPI.getUserPoint(ThisApplication.staticUserModel.id).execute();

        if (getPoint.code() == 200 && getPoint.body() != null) {

            return (int) Double.parseDouble(getPoint.body().toString());
        }

        return 0;
    }

    /**
     * 층전 예상 포인트
     **/
    public int getExpectPoint(String chargerId, String startDate, String endDate) throws Exception {

        Response<Object> getPoint = webServiceAPI.getExpectPoint(chargerId, startDate, endDate).execute();

        if (getPoint.code() == 200 && getPoint.body() != null) {

            return (int) Double.parseDouble(getPoint.body().toString());
        }

        return 0;
    }

    /**
     * 포인트 충전
     **/
    public boolean insertPoint(PointModel model) throws Exception {

        Response<PointModel> pointResponse = webServiceAPI.insertPoint(model).execute();

        if (pointResponse.code() == 201) {

            return true;
        }

        return false;
    }

    /**
     * 충전 예약
     **/
    public ReservationModel goReservation(ReservationModel reservationModel) throws Exception {

        ReservationModel model = null;

        Response<ReservationModel> response = webServiceAPI.insertReservation(reservationModel).execute();

        if (response.code() == 201 && response.body() != null) {

            model = response.body();
            Log.e("metis", "예약완료 : " + model);

        }

        return model;
    }

    /**
     * 충전기 리스트
     **/
    public Map<String, Object> getChargers(String startDate, String endDate, String distance, ArrayList<ChargerModel> chargerList) throws Exception {

        Map<String, Object> map = new HashMap<>();

        Response<Object> response = webServiceAPI.getChargers(startDate, endDate, Constants.currentLocationLng, Constants.currentLocationLat, distance, "").execute();

        if (response.code() == 200) {

            Type type = new TypeToken<List<ChargerModel>>() {
            }.getType();
            Gson gson = new Gson();

            String jsonResult = gson.toJson(response.body());
            List<ChargerModel> list = gson.fromJson(jsonResult, type);

            Log.e("metis", "getChargers list : : " + list);

            for (int i = 0; i < list.size(); i++) {

                chargerList.add(list.get(i));
            }

            map.put("result", true);
            map.put("list", chargerList);
        } else {
            map.put("result", false);
        }

        return map;
    }

    /**
     * 충전기 정상 종료
     **/
    public RechargeModel endAuthenticateCharger(int chargerId, RechargeEndModel rechargeEndModel, String stChargingTime) throws Exception {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        RechargeModel model = null;

        Response<Object> response = webServiceAPI.endAuthenticateCharger(chargerId, rechargeEndModel).execute();

        if(response.code() == 200 && response.body() != null){
            JSONObject json = new JSONObject((Map) response.body());
            Gson gSon = new Gson();
            model = gSon.fromJson(json.toString(), RechargeModel.class);

            Date oldDate = format.parse(stChargingTime);
            Date nowDate = new Date();
            Log.e("metis", "oldDate : " + format.format(oldDate));
            Log.e("metis", "nowDate : " + format.format(nowDate));

            long diff = nowDate.getTime() - oldDate.getTime();
            long second = diff / 1000;

            model.chargingTime = cu.chargingTime((int) second);                                  //sec를 hh:mm:ss로 변환 CALL
            model.startRechargeDate = stChargingTime;
            model.endRechargeDate = cu.timeSecCalculation(model.startRechargeDate, (int) second);   //sec 시간 계산(yyyy-MM-dd HH:mm:ss) CALL

        }

        return model;
    }

    /**
     * 충전기 비정상 종료
     **/
    public boolean endAuthenticateChargerUnplanned(int chargerId, RechargeEndModel rechargeEndModel) throws Exception {

        Response<Object> response = webServiceAPI.endAuthenticateChargerUnplanned(chargerId, rechargeEndModel).execute();

        if(response.code() == 200){

            Log.e("metis", "endAuthenticateChargerUnplanned : " + response.body().toString());
            return true;
        }

        return false;
    }

    /**
     * 충전기 시작전 인증
     **/
    public boolean getAuthenticateCharger(int chargerId, AuthenticateModel model) throws Exception {

        Response<Object> response = webServiceAPI.getAuthenticateCharger(chargerId, model).execute();

        if(response.code() == 201 && response.body() != null && response.body().toString().equals("true")){

            return true;
        }

        return  false;
    }

    /**
     * 충전기 시작 인증
     **/
    public int startAuthenticateCharger(int chargerId, AuthenticateModel model) throws Exception {

        Response<Object> response = webServiceAPI.startAuthenticateCharger(chargerId, model).execute();

        if(response.code() == 201 && response.body() != null){

            String temp = response.body().toString();

            //double형태로 받음
            temp = temp.substring(temp.indexOf("=") + 1, temp.indexOf("."));
            Log.e("metis", "temp : " + temp);

            // rechargeId 리턴
            return Integer.parseInt(temp);
        }

        return -1;
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
