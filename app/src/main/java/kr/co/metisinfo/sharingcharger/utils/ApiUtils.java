package kr.co.metisinfo.sharingcharger.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
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
import kr.co.metisinfo.sharingcharger.model.AdminChargerModel;
import kr.co.metisinfo.sharingcharger.model.AdminDashboardModel;
import kr.co.metisinfo.sharingcharger.model.AllowTimeOfDayModel;
import kr.co.metisinfo.sharingcharger.model.AuthenticateModel;
import kr.co.metisinfo.sharingcharger.model.ChargerModel;
import kr.co.metisinfo.sharingcharger.model.AdminMonthlyProfitPointModel;
import kr.co.metisinfo.sharingcharger.model.PointModel;
import kr.co.metisinfo.sharingcharger.model.PriceModel;
import kr.co.metisinfo.sharingcharger.model.PurchaseModel;
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
     * 이메일 중복체크
     */
    public Boolean checkDuplicate(String userEmail) {

        Response<UserModel> response = null;
        try {
            response = webServiceAPI.checkDuplicate(userEmail).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response.code() == 204) {
            return true;
        }

        return false;
    }

    /**
     * 사용자 아이디 찾기
     **/
    public Map<String, Object> findId(String userName, String phone) throws Exception {

        Map<String, Object> map = new HashMap<>();
        Log.e("metis", " findId");

        Response<Object> response = webServiceAPI.findId(userName, phone).execute();

        if (response.code() == 200) {

            Type type = new TypeToken<List<UserModel>>() {}.getType();
            Gson gson = new Gson();

            String jsonResult = gson.toJson(response.body());
            List<UserModel> list = gson.fromJson(jsonResult, type);

            map.put("result", true);
            map.put("list", list);

        } else {
            map.put("result", false);
        }

        return map;
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
     * sms 문자
     **/
    public String getSms(String phoneNumber) throws Exception {

        String certificateNo = null;

        Response<Object> response = webServiceAPI.getSms(phoneNumber).execute();

        if (response.code() == 200 && response.body() != null) {

            certificateNo = response.body().toString();

        }

        return certificateNo;
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

        Response<ReservationModel> response;

        PreferenceUtil preferenceUtil = new PreferenceUtil(ThisApplication.context);

        Boolean isInstantCharging = preferenceUtil.getBoolean("isInstantCharging");

        //즉시 충전 취소
        if (isInstantCharging) {
            response = webServiceAPI.cancelInstantCharging(reservationId).execute();
        }

        //예약 취소
        else {
            response = webServiceAPI.cancelReservation(reservationId).execute();
        }

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

            map.put("result", true);
            map.put("list", list);
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

            Date oldDate = format.parse(model.startRechargeDate);
            Date nowDate = format.parse(model.endRechargeDate);

            long diff = nowDate.getTime() - oldDate.getTime();
            long second = diff / 1000;

            model.chargingTime = cu.chargingTime((int) second);                                  //sec를 hh:mm:ss로 변환 CALL
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
     * 포인트 이력 조회
     **/
    public Map<String, Object> getPoints(String username, String startDate, String endDate, String sort, String paymentType, int pageIndex, List<PurchaseModel> list) throws Exception {

        Map<String, Object> map = new HashMap<>();

        //신용 걸제 완료 구분 -> ALL 하드코딩
        String paymentSuccessType = "ALL";

        Response<Object> response = webServiceAPI.getPoints(username, startDate, endDate, sort, paymentSuccessType, paymentType, pageIndex, 10).execute();

        if (response.code() == 200 && response.body() != null) {
            JSONObject json = new JSONObject((Map) response.body());

            JSONArray contacts = json.getJSONArray("content");

            if (contacts.length() == 0) {
                map.put("chkList", false);
            } else {
                map.put("chkList", true);
            }
            for (int i = 0; i < contacts.length(); i++) {
                Gson gson = new Gson();

                PurchaseModel vo = gson.fromJson(contacts.getJSONObject(i).toString(), PurchaseModel.class);

                list.add(vo);
            }

            map.put("list", list);
        }

        return map;
    }

    /**
     * 충전 이력 조회
     **/
    public Map<String, Object> getRecharges(String startDate, String endDate, String getType, int index, List<RechargeModel> list) throws Exception {

        Map<String, Object> map = new HashMap<>();

        Response<Object> response = webServiceAPI.getRecharges(ThisApplication.staticUserModel.id, startDate, endDate, getType, index, 10).execute();

        if (response.code() == 200 && response.body() != null) {
            JSONObject json = new JSONObject((Map) response.body());

            JSONArray contacts = json.getJSONArray("content");

            if (contacts.length() == 0) {
                map.put("chkList", false);
            } else {
                map.put("chkList", true);
            }

            for (int i = 0; i < contacts.length(); i++) {
                Gson gson = new Gson();

                RechargeModel vo = gson.fromJson(contacts.getJSONObject(i).toString(), RechargeModel.class);

                list.add(vo);
            }

            map.put("list", list);
        }

        return map;
    }

    /**
     * 충전기 정보
     **/
    public ChargerModel getChargerInfo(int chargerId) throws Exception {

        ChargerModel model = null;

        Response<Object> response = webServiceAPI.getChargerInfo(chargerId).execute();

        if (response.code() == 200 && response.body() != null) {
            JSONObject json = new JSONObject((Map) response.body());


            Gson gson = new Gson();

            model = gson.fromJson(json.toString(), ChargerModel.class);
        }

        return model;
    }

    /**
     * 소유주 전환
     **/
    public int changeUserType(int userId) {

        Response<Object> response = null;

        try {
            response = webServiceAPI.changeUserType(userId).execute();
        } catch (Exception e) {
            Log.e("metis", " changeUserType Exception1 : " + e);
        }

        if(response.code() == 201 && response.body() != null){
            return response.code();
        } else if (response.code() == 400) {
            return response.code();
        } else {
           return response.code();
        }
    }

    /**
     * 관리자 충전기 단가 변경
     **/
    public int changePrice(int chargerId, PriceModel priceModel) {

        Response<Object> response = null;

        try {
            response = webServiceAPI.changePrice(chargerId, priceModel).execute();
        } catch (Exception e) {
            Log.e("metis", " changeUserType Exception1 : " + e);
        }

        if(response.code() == 200 && response.body() != null){
            return response.code();
        } else if (response.code() == 400) {
            return response.code();
        } else {
            return response.code();
        }
    }

    /**
     * 관리자 대시보드 정보
     **/
    public AdminDashboardModel getAdminDashboard() {

        AdminDashboardModel adminDashboardModel = new AdminDashboardModel();

        try {
            Response<Object> response = webServiceAPI.getAdminDashboard(ThisApplication.staticUserModel.id).execute();

            if (response.code() == 200) {

                JSONObject json = new JSONObject((Map) response.body());

                Gson gson = new Gson();

                adminDashboardModel = gson.fromJson(json.toString(), AdminDashboardModel.class);
                adminDashboardModel.setResponseCode(response.code());
            } else if (response.code() == 204) {
                adminDashboardModel.setMessage("사용자가 존재하지 않습니다.\n문제 지속시 고객센터로 문의주세요.");
            } else if (response.code() == 400) {
                adminDashboardModel.setMessage("요청 파라미터가 올바르지 않습니다.\n문제 지속시 고객센터로 문의주세요.");
            } else if (response.code() == 404) {
                adminDashboardModel.setMessage("요청하신 API를 찾을 수 없습니다.\n문제 지속시 고객센터로 문의주세요.");
            } else if (response.code() == 500) {
                adminDashboardModel.setMessage("서버에 문제가 발생 하였습니다.\n문제 지속시 고객센터로 문의주세요.");
            }

        } catch (Exception e) {
            Log.e("metis", " getAdminDashboard Exception1 : " + e);

            adminDashboardModel.setMessage("오류가 발생 하였습니다.\n문제 지속시 고객센터로 문의주세요.");

            return adminDashboardModel;
        }

        return adminDashboardModel;
    }

    /**
     * 관리자 충전기 리스트
     **/
    public Map<String, Object> getAdminCharger(int page, List<AdminChargerModel> adminChargerModelList) throws Exception {

        //List<AdminChargerModel> adminChargerModelList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        Log.e("metis", " getAdminCharger");

        Response<Object> response = webServiceAPI.getAdminCharger(String.valueOf(ThisApplication.staticUserModel.id), "Personal", "ALL", page, "ALL", 10, "ASC").execute();

        if (response.code() == 200) {

            JSONObject json = new JSONObject((Map) response.body());
            JSONArray jsonArray = json.getJSONArray("content");

            if (jsonArray.length() == 0) {
                map.put("chkList", false);
            } else {
                map.put("chkList", true);
            }

            Gson gson = new Gson();

            for (int i = 0; i < jsonArray.length(); i++) {
                AdminChargerModel adminChargerModel = gson.fromJson(jsonArray.getJSONObject(i).toString(), AdminChargerModel.class);
                adminChargerModelList.add(adminChargerModel);
            }

            map.put("list", adminChargerModelList);
        }

        return map;
    }

    /**
     * 관리자 충전기 이용 가능 시간 정보
     **/
    public AllowTimeOfDayModel getAllowTime(int chargerId) {

        AllowTimeOfDayModel allowTimeOfDayModel = new AllowTimeOfDayModel();

        try {
            Response<Object> response = webServiceAPI.getAllowTime(chargerId).execute();

            if (response.code() == 200) {

                JSONObject json = new JSONObject((Map) response.body());

                Gson gson = new Gson();

                allowTimeOfDayModel = gson.fromJson(json.toString(), AllowTimeOfDayModel.class);
                allowTimeOfDayModel.setResponseCode(response.code());
            } else if (response.code() == 204) {
                allowTimeOfDayModel.setMessage("충전기가 존재하지 않습니다.\n문제 지속시 고객센터로 문의주세요.");
            } else if (response.code() == 400) {
                allowTimeOfDayModel.setMessage("요청 파라미터가 올바르지 않습니다.\n문제 지속시 고객센터로 문의주세요.");
            } else if (response.code() == 404) {
                allowTimeOfDayModel.setMessage("요청하신 API를 찾을 수 없습니다.\n문제 지속시 고객센터로 문의주세요.");
            } else if (response.code() == 500) {
                allowTimeOfDayModel.setMessage("서버에 문제가 발생 하였습니다.\n문제 지속시 고객센터로 문의주세요.");
            }

        } catch (Exception e) {
            Log.e("metis", " getAdminDashboard Exception1 : " + e);

            allowTimeOfDayModel.setMessage("오류가 발생 하였습니다.\n문제 지속시 고객센터로 문의주세요.");

            return allowTimeOfDayModel;
        }

        return allowTimeOfDayModel;
    }

    /**
     * 충전기 이용 가능 시간 수정
     **/
    public AllowTimeOfDayModel changeAllowTime(int chargerId, AllowTimeOfDayModel allowTimeOfDayModel) {

        AllowTimeOfDayModel resultAllowTimeOfDayModel = new AllowTimeOfDayModel();

        try {
            Response<Object> response = webServiceAPI.changeAllowTime(chargerId, allowTimeOfDayModel).execute();

            if (response.code() == 200) {

                JSONObject json = new JSONObject((Map) response.body());

                Gson gson = new Gson();

                resultAllowTimeOfDayModel = gson.fromJson(json.toString(), AllowTimeOfDayModel.class);
                resultAllowTimeOfDayModel.setResponseCode(response.code());
            } else if (response.code() == 204) {
                resultAllowTimeOfDayModel.setMessage("충전기가 존재하지 않습니다.\n문제 지속시 고객센터로 문의주세요.");
            } else if (response.code() == 400) {
                resultAllowTimeOfDayModel.setMessage("요청 파라미터가 올바르지 않습니다.\n문제 지속시 고객센터로 문의주세요.");
            } else if (response.code() == 404) {
                resultAllowTimeOfDayModel.setMessage("요청하신 API를 찾을 수 없습니다.\n문제 지속시 고객센터로 문의주세요.");
            } else if (response.code() == 500) {
                resultAllowTimeOfDayModel.setMessage("서버에 문제가 발생 하였습니다.\n문제 지속시 고객센터로 문의주세요.");
            }

        } catch (Exception e) {
            Log.e("metis", " getAdminDashboard Exception1 : " + e);

            resultAllowTimeOfDayModel.setMessage("오류가 발생 하였습니다.\n문제 지속시 고객센터로 문의주세요.");

            return resultAllowTimeOfDayModel;
        }

        return resultAllowTimeOfDayModel;
    }

    /**
     * 충전기 정보 수정
     **/
    public AdminChargerModel changeChargerInformation(int chargerId, AdminChargerModel adminChargerModel) {

        AdminChargerModel resultAdminChargerModel = new AdminChargerModel();

        try {
            Response<Object> response = webServiceAPI.changeChargerInformation(chargerId, adminChargerModel).execute();

            if (response.code() == 200) {

                JSONObject json = new JSONObject((Map) response.body());

                Gson gson = new Gson();

                resultAdminChargerModel = gson.fromJson(json.toString(), AdminChargerModel.class);
                resultAdminChargerModel.setResponseCode(response.code());
            } else if (response.code() == 204) {
                resultAdminChargerModel.setMessage("충전기가 존재하지 않습니다.\n문제 지속시 고객센터로 문의주세요.");
            } else if (response.code() == 400) {
                resultAdminChargerModel.setMessage("요청 파라미터가 올바르지 않습니다.\n문제 지속시 고객센터로 문의주세요.");
            } else if (response.code() == 404) {
                resultAdminChargerModel.setMessage("요청하신 API를 찾을 수 없습니다.\n문제 지속시 고객센터로 문의주세요.");
            } else if (response.code() == 500) {
                resultAdminChargerModel.setMessage("서버에 문제가 발생 하였습니다.\n문제 지속시 고객센터로 문의주세요.");
            }

        } catch (Exception e) {
            Log.e("metis", " AdminChargerModel Exception1 : " + e);

            resultAdminChargerModel.setMessage("오류가 발생 하였습니다.\n문제 지속시 고객센터로 문의주세요.");

            return resultAdminChargerModel;
        }

        return resultAdminChargerModel;
    }


    /**
     * 소유자 충전기 등록
     **/
    public AdminChargerModel assignCharger(AdminChargerModel adminChargerModel) {

        try {

            Response<Object> response = webServiceAPI.assignBleCharger(adminChargerModel.getId(), adminChargerModel).execute();

            if (response.code() == 200) {
                JSONObject json = new JSONObject((Map) response.body());

                Gson gson = new Gson();

                adminChargerModel = gson.fromJson(json.toString(), AdminChargerModel.class);
                adminChargerModel.setResponseCode(response.code());

            } else if (response.code() == 204) {
                adminChargerModel.setMessage("해당 ID의 충전기가 존재하지 않습니다.\n문제 지속시 고객센터로 문의주세요.");
            } else if (response.code() == 400) {
                adminChargerModel.setMessage("요청 파라미터가 올바르지 않습니다.\n문제 지속시 고객센터로 문의주세요.");
            } else if (response.code() == 404) {
                adminChargerModel.setMessage("요청하신 API를 찾을 수 없습니다.\n문제 지속시 고객센터로 문의주세요.");
            } else if (response.code() == 500) {
                adminChargerModel.setMessage("서버에 문제가 발생 하였습니다.\n문제 지속시 고객센터로 문의주세요.");
            }

        } catch (Exception e) {
            Log.e("metis", " getChargerInformationFromBleNumber Exception1 : " + e);

            adminChargerModel.setMessage("오류가 발생 하였습니다.\n문제 지속시 고객센터로 문의주세요.");

            return adminChargerModel;
        }

        return adminChargerModel;
    }

    /**
     * 충전기 등록시 bleNumber로 차지인에 등록된 충전기인지 조회
     **/
    public AdminChargerModel getChargerInformationFromBleNumber(String bleNumber) {

        AdminChargerModel adminChargerModel = new AdminChargerModel();

        try {
            Response<Object> response = webServiceAPI.getChargerInformationFromBleNumber(bleNumber, "DESC", 1, 10).execute();
            if (response.code() == 200) {

                JSONObject json = new JSONObject((Map) response.body());

                JSONArray content = json.getJSONArray("content");

                if(content.length() > 0 ) {
                    Gson gson = new Gson();
                    adminChargerModel = gson.fromJson(content.getJSONObject(0).toString(), AdminChargerModel.class);
                    adminChargerModel.setResponseCode(response.code());
                }

            } else if (response.code() == 204) {
                adminChargerModel.setMessage("해당 ID의 충전기가 존재하지 않습니다.\n문제 지속시 고객센터로 문의주세요.");
            } else if (response.code() == 400) {
                adminChargerModel.setMessage("요청 파라미터가 올바르지 않습니다.\n문제 지속시 고객센터로 문의주세요.");
            } else if (response.code() == 404) {
                adminChargerModel.setMessage("요청하신 API를 찾을 수 없습니다.\n문제 지속시 고객센터로 문의주세요.");
            } else if (response.code() == 500) {
                adminChargerModel.setMessage("서버에 문제가 발생 하였습니다.\n문제 지속시 고객센터로 문의주세요.");
            }

        } catch (Exception e) {
            Log.e("metis", " getChargerInformationFromBleNumber Exception1 : " + e);

            adminChargerModel.setMessage("오류가 발생 하였습니다.\n문제 지속시 고객센터로 문의주세요.");

            return adminChargerModel;
        }

        return adminChargerModel;
    }

    /**
     * 소유주 월별 수익 포인트
     **/
    public Map<String, Object> getAdminMonthlyProfitPoint(String searchYear) throws Exception{

        Map<String, Object> map = new HashMap<>();
        Log.e("metis", " getAdminCharger" + ThisApplication.staticUserModel.id);

        String searchType = "MONTH";
        String searchMonth = "12";

        Response<Object> response = webServiceAPI.getAdminMonthlyProfitPoint(String.valueOf(ThisApplication.staticUserModel.id), searchType, searchYear, searchMonth).execute();

        if (response.code() == 200) {
            Log.d("metis", response.body().toString());
            //JSONObject json = new JSONObject((Map) response.body());

            JSONArray json = new JSONArray(response.body().toString());
            Gson gson = new Gson();
            List<AdminMonthlyProfitPointModel> adminMonthlyProfitPointModelList = new ArrayList<>();

            for (int i = 0; i < json.length(); i++) {
                AdminMonthlyProfitPointModel adminMonthlyProfitPointModel = gson.fromJson(json.getJSONObject(i).toString(), AdminMonthlyProfitPointModel.class);
                Log.d("metis",adminMonthlyProfitPointModel.toString());
                adminMonthlyProfitPointModelList.add(adminMonthlyProfitPointModel);
            }

            map.put("list", adminMonthlyProfitPointModelList);
        }

        return map;
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
