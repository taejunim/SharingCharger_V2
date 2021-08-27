package kr.co.metisinfo.sharingcharger.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.Location;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.kakao.kakaonavi.options.RpOption;
import com.kakao.kakaonavi.options.VehicleType;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.Constants;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.charger.ChargerSearchActivity;
import kr.co.metisinfo.sharingcharger.charger.ChargerReservationActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityMainBinding;
import kr.co.metisinfo.sharingcharger.dialog.ChargerFinishDialog;
import kr.co.metisinfo.sharingcharger.dialog.InstantChargingDialog;
import kr.co.metisinfo.sharingcharger.dialog.PointChargingDialog;
import kr.co.metisinfo.sharingcharger.model.AllowTimeOfDayModel;
import kr.co.metisinfo.sharingcharger.model.BookmarkModel;
import kr.co.metisinfo.sharingcharger.model.ChargerModel;
import kr.co.metisinfo.sharingcharger.model.CurrentReservationModel;
import kr.co.metisinfo.sharingcharger.model.ReservationDateModel;
import kr.co.metisinfo.sharingcharger.model.RechargeModel;
import kr.co.metisinfo.sharingcharger.model.ReservationModel;
import kr.co.metisinfo.sharingcharger.model.SearchKeywordModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.CommonUtils;
import kr.co.metisinfo.sharingcharger.utils.DateUtils;
import kr.co.metisinfo.sharingcharger.view.viewInterface.FragmentDialogInterface;
import kr.co.metisinfo.sharingcharger.viewModel.BookmarkViewModel;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements MapView.POIItemEventListener, MapView.MapViewEventListener, FragmentDialogInterface {

    ActivityMainBinding binding;

    private static final String TAG = MainActivity.class.getSimpleName();

    private List<ChargerModel> personalChargeList = new ArrayList<>();  //소유주 충전기 리스트

    ApiUtils apiUtils = new ApiUtils();

    Animation translateTop;
    Animation translateBottom;

    private boolean isPageOpen = false;

    private ArrayList<MapPoint> pointList = new ArrayList<>();  //맵 마커 표시

    private MapPOIItem beforeClickMapPOIItem;   //마커 아이템 값

    private int clickPOIIndex = -1;

    private ChargerModel clickChargerModel;

    private ArrayList<ChargerModel> chargerList = new ArrayList<>();    //충전기 리스트
    private ReservationModel reservationModel;

    private boolean isCurrentLocation = false;  // 현위치로 이동 여부

    private int reserveChargingMinute = 240; // 기본 4시간, 90 => 1시간 30분
    private String reserveRadius = "3 km";         // 예약 반경 거리
    private String reserveType = "BLE";            // 예약 충전기 타입

    private int chargingStartYYYY;          // 충전 시작 년도
    private int chargingStartMM;            // 충전 시작 월
    private int chargingStartDD;            // 충전 시작 일

    private int chargingStartHH;            // 충전 시작 시
    private int chargingStartII;            // 충전 시작 분

    private int chargingEndYYYY;            // 충전 종료 년도
    private int chargingEndMM;              // 충전 종료 월
    private int chargingEndDD;              // 충전 종료 일

    private int chargingEndHH;              // 충전 종료 시
    private int chargingEndII;              // 충전 종료 분

    private double locationLat = Constants.currentLocationLat;  //위도 , Y
    private double locationLng = Constants.currentLocationLng;  //경도 , X

    private String reservationTime;

    GlideDrawableImageViewTarget gifImage;

    MapPOIItem[] mapPOIItems;

    private boolean isSearchKeywordMarkerClick = false;

    private long backKeyPressedTime = 0;

    private CustomDialog reservationCancelDialog;

    private BookmarkViewModel bookmarkViewModel;

    Handler handler = new Handler();

    private String centerLocation;

    private String chkRecharge = "";

    private String gerUserType = "";

    /* 20.12.28 즉시충전을 위한 변수 추가 START */
    private String intntChgSTime = "";                                                            //즉시충전 다이어로그의 충전 시간을 위한 변수
    private String intntChgETime = "";                                                            //즉시충전 다이어로그의 충전 시간을 위한 변수
    /* 20.12.28 즉시충전을 위한 변수 추가 END */

    // true : 즉시충전, false : 예약충전
    private boolean isInstantCharge = true;

    private String searchKeyword = "";

    CommonUtils commonUtils = new CommonUtils();

    //소유주 충전결과 화면 표시
    boolean ownerResult = false;

    boolean isFirst = false;

    SimpleDateFormat ymdFormatter = new SimpleDateFormat("yyyy-MM-dd'T'");
    SimpleDateFormat fullDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat HHMMFormatter = new SimpleDateFormat("HH:mm");
    SimpleDateFormat ymdHmFormatter = new SimpleDateFormat("yyyyMMddHHmm");

    List<CurrentReservationModel> currentReservationList = new ArrayList<>();
    List<CurrentReservationModel> openCloseTimeList = new ArrayList<>();

    ReservationDateModel reservationDateModel = new ReservationDateModel();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            //예약했을때
            case Constants.PAGE_RESERVE:

                if (resultCode == RESULT_OK) {

                    binding.reservationDetailCloseImg.performClick();

                    reloadInfo();

                }
                break;

            //충전기 조건 검색
            case Constants.PAGE_SEARCH_CONDITION:

                if (resultCode == RESULT_OK) {

                    isSearchKeywordMarkerClick = false;

                    binding.editSearch.setText("");

                    /*gifImage = new GlideDrawableImageViewTarget(binding.imageLoading);
                    Glide.with(this).load(R.mipmap.spinner_loading).into(gifImage);*/
                    showLoading(binding.loading);

                    reserveChargingMinute = data.getIntExtra("chargingMinute", -1);

                    chargingStartYYYY = data.getIntExtra("chargingStartYYYY", -1);
                    chargingStartMM = data.getIntExtra("chargingStartMM", -1);
                    chargingStartDD = data.getIntExtra("chargingStartDD", -1);
                    chargingStartHH = data.getIntExtra("chargingStartHH", -1);
                    chargingStartII = data.getIntExtra("chargingStartII", -1);

                    chargingEndYYYY = data.getIntExtra("chargingEndYYYY", -1);
                    chargingEndMM = data.getIntExtra("chargingEndMM", -1);
                    chargingEndDD = data.getIntExtra("chargingEndDD", -1);
                    chargingEndHH = data.getIntExtra("chargingEndHH", -1);
                    chargingEndII = data.getIntExtra("chargingEndII", -1);

                    reserveRadius = data.getStringExtra("reserveRadius");
                    reserveType = data.getStringExtra("reserveType");

                    String temp = data.getStringExtra("isInstantCharge");
                    Log.e("metis", "isInstantCharge temp: " + temp);
                    isInstantCharge = Boolean.valueOf(temp);
                    if (isInstantCharge) {
                        binding.btnReservation.setText(R.string.go_charge);
                    } else {
                        binding.btnReservation.setText(R.string.go_reserve);

                        reservationDateModel.setYear(chargingStartYYYY);
                        reservationDateModel.setMonth(chargingStartMM);
                        reservationDateModel.setDay(chargingStartDD);
                        reservationDateModel.setHour(chargingStartHH);
                        reservationDateModel.setMinute(chargingStartII);
                        reservationDateModel.setReserveChargingMinute(reserveChargingMinute);
                        reservationDateModel.setEndYear(chargingStartYYYY);
                        reservationDateModel.setEndMonth(chargingStartMM);
                        reservationDateModel.setEndDay(chargingStartDD);
                        reservationDateModel.setEndHour(chargingEndHH);
                        reservationDateModel.setEndMinute(chargingEndII);
                    }

                    Log.e("metis", "reserveChargingMinute : " + reserveChargingMinute);
                    Log.e("metis", "isInstantCharge : " + isInstantCharge);
                    Log.e("metis", "reserveRadius : " + reserveRadius);

                    setTimeInit();

                    reloadInfo();
                }

                break;

            //키워드 검색, 즐겨찾기
            case Constants.PAGE_SEARCH_KEYWORD:

                // 내 위치중심, 지도중심 구분해야함
                if (resultCode == RESULT_OK) {

                    SearchKeywordModel model = (SearchKeywordModel) data.getSerializableExtra("keyword");

                    //키워드검색인지 즐겨찾기 인지 구분
                    String getType = data.getStringExtra("type");

                    centerLocation = data.getStringExtra("centerLocation");

                    binding.editSearch.setText(model.placeName);

                    searchKeyword = model.placeName;

                    binding.layoutTop1.requestFocus();

                    goSearchPosition(model);

                }

                break;

            //소유주 전환 완료 -> 사이드 메뉴 다시 그리기
            case Constants.PAGE_SETTING:
                if (resultCode == Activity.RESULT_OK) {
                    boolean isUserTypeChange = data.getBooleanExtra("isUserTypeChange",false);

                    if (isUserTypeChange) {
                        setUpDrawer(binding.drawerLayout.getId(), binding.listSlidermenu.getId());
                    }
                }

                break;
        }
    }

    /*
     * 메인 화면 하단 SET TIME TEXTVIEW
     * */
    private void setTimeInit() {

        binding.txtChargingTime.setText(DateUtils.generateTimeToString(reserveChargingMinute));

        String mm = String.format(Locale.KOREA, "%02d", chargingStartMM);
        String dd = String.format(Locale.KOREA, "%02d", chargingStartDD);

        String choiceDate = chargingStartYYYY + mm + dd;

        String startHh = String.format(Locale.KOREA, "%02d", chargingStartHH);
        String startIi = String.format(Locale.KOREA, "%02d", chargingStartII);

        String endHh = String.format(Locale.KOREA, "%02d", chargingEndHH);
        String endIi = String.format(Locale.KOREA, "%02d", chargingEndII);

        Log.e("metis", "choiceDate : " + choiceDate);
        Log.e("metis", "DateUtils.getWeek(choiceDate) : " + DateUtils.getWeek(choiceDate));

        binding.txtChargingTerm.setText(mm + "/" + dd + " " + DateUtils.getWeek(choiceDate) + " " + startHh + ":" + startIi + " ~ " + endHh + ":" + endIi);
    }

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        bookmarkViewModel = new ViewModelProvider(this).get(BookmarkViewModel.class);

        changeStatusBarColor(false);

        if (ThisApplication.staticUserModel != null) {
            gerUserType = ThisApplication.staticUserModel.userType;
        }

    }

    @Override
    public void initViewModel() {

        Log.e("metis", "lat : " + Constants.currentLocationLat + ", lng : " + Constants.currentLocationLng);
        //현재 시간 표시
        setTime();

        //메인 지도 화면, 충전기 정보 가져오기
        //addChargerInfo(Constants.currentLocationLat, Constants.currentLocationLng);

        binding.mapView.setMapViewEventListener(this);
        binding.mapView.setPOIItemEventListener(this);

        //마커 표시
        //createDefaultMarker(binding.mapView);

        //test 예약 있을때
        /*binding.layoutChargingInfo.setVisibility(View.INVISIBLE);
        binding.layoutReservationInfo.setVisibility(View.VISIBLE);*/
        //test
    }

    @Override
    public void setOnClickListener() {

        //예약 있을시
        binding.layoutReservationInfo.setOnClickListener(view -> {

            clickPOIIndex = 1;

            CheckBookmarkBackgroundTask task = new CheckBookmarkBackgroundTask(this, "ReservationInfo", Integer.parseInt(binding.txtChgrDetailNm.getTag().toString()));
            task.execute();

            binding.layoutReservationDetailInfo.setVisibility(View.VISIBLE);
            binding.layoutReservationDetailInfo.startAnimation(translateTop);
        });

        //예약 닫기버튼
        binding.reservationDetailCloseImg.setOnClickListener(View -> {
            binding.layoutReservationDetailInfo.startAnimation(translateBottom);
            binding.layoutReservationDetailInfo.setVisibility(View.INVISIBLE);
        });

        //충전시작 버튼 클릭
        binding.chgrDetailStart.setOnClickListener(view -> {

            //binding.reservationDetailCloseImg.performClick();
            //test

            /*
             * 1. 소유주 or 사용자 확인
             * 2. 소유주충전기일 경우 -> 소유주 화면
             * 3. 사용자
             *  - 충전 시간 확인
             *  - 현재시간에서 예약완료 시간 구해서 실제 충전 시간 보내기
             * */

            //소유주의 충전기일 경우 바로 소유주화면이동
            Log.e("metis", "gerUserType : " + gerUserType);
            if (gerUserType != null && gerUserType.equals("Personal")) {

                SharedPreferences pref = getSharedPreferences("reservation", MODE_PRIVATE);

                String getActivity = pref.getString("activity", "");
                Log.e("metis", "getActivity : " + getActivity);

                if (!getActivity.equals("BLEChargingActivity")) {

                    Log.e("metis", "chkRecharge : " + chkRecharge);
                    startOwnerActivity();
                    return;
                }
            }

            Log.e("metis", "chgrDetailStart return : ");

            //사용자 일때
            try {
                boolean isTime = commonUtils.checkRechargeTime(reservationModel);
                if (!isTime) {
                    Toast.makeText(MainActivity.this, "현재 예약시간이 아니라 충전 시작을 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    Log.e("metis", "현재 예약시간이 아니라 충전 시작을 할 수 없습니다.");
                } else {
                    //시작전 시간확인
                    Intent intent = new Intent(this, ChargerSearchActivity.class);

                    chkRecharge = "ChargerSearchActivity";
                    Log.e("metis", "chkRecharge is " + chkRecharge);

                    intent.putExtra("reservationModel", reservationModel);

                    // 충전을 바로 하지 않을 수 있기 때문에 현재시간에서 예약완료 시간을 구해서 보내줘야함
                    Date nowDt = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date endDt = format.parse(reservationModel.getEndDate().replaceAll("T", " "));

                    long diff = endDt.getTime() - nowDt.getTime();
                    long min = diff / (60 * 1000);

                    Log.e("metis", "min : " + min);
                    Log.e("metis", "reservationTime : " + reservationTime);

                    intent.putExtra("reservationTime", String.valueOf(min));

                    startActivity(intent);
                }
            } catch (ParseException e) {
                Log.e("metis", "chgrDetailStart ParseException : " + e);
            }

            hideLayout();

        });

        //예약취소 버튼 클릭
        binding.chgrDetailCancel.setOnClickListener(view -> {

            showReservationDialog();
        });

        //충전기 즐겨찾기 추가
        binding.imageFavorite.setOnClickListener(view -> {
            checkBookmark(view, chargerList.get(clickPOIIndex).id);
        });

        //예약화면 즐겨찾기
        binding.imageFavoriteDetail.setOnClickListener(view -> {
            checkBookmark(view, Integer.parseInt(binding.txtChgrDetailNm.getTag().toString()));
        });

        binding.layoutChargingInfo.setOnClickListener(view -> {

            showSearchCondition();
        });

        // 현재 위치 버튼
        binding.btnCurrentLocation.setOnClickListener(view -> {

            if (!isCurrentLocation) {   // 현 위치로 움직임

                binding.mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

                binding.btnCurrentLocation.setImageResource(R.mipmap.current_location_on);
                isCurrentLocation = true;

            } else {                    // 현 위치로 움직임 막기

                binding.mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                binding.mapView.setShowCurrentLocationMarker(false);

                binding.btnCurrentLocation.setImageResource(R.mipmap.current_location_off);

                isCurrentLocation = false;
            }
        });

        //충전하기 클릭
        binding.btnReservation.setOnClickListener(view -> {

            /*
             * 1. 소유주의 충전기일 경우 바로 소유주화면이동
             * 2. 즉시충전/예약하기 구분
             * 3. 현재 포인트 가져오기
             * 4. 예상 포인트 가져오기
             * 5. 현재 - 예상 : 충전가능한지 확인
             * */
            Log.e("metis", "gerUserType : " + gerUserType);
            if (gerUserType != null && gerUserType.equals("Personal")) {
                Log.e("metis", "personalChargeList.size() : " + personalChargeList.size());
                for (int i = 0; i < personalChargeList.size(); i++) {

                    Log.e("metis", "personalChargeList.get(i).getBleNumber() : " + personalChargeList.get(i).getBleNumber());
                    Log.e("metis", "chargerList.get(clickPOIIndex).getBleNumber() : " + chargerList.get(clickPOIIndex).getBleNumber());

                    if (personalChargeList.get(i).getBleNumber().equals(chargerList.get(clickPOIIndex).getBleNumber())) {

                        startOwnerActivity();

                        return;
                    }
                }
            }

            //소유주일 경우 바로 소유주화면이동

            Log.e("metis", "return : ");

            //넘기기 전에 예약진행인지 잔액부족인지 구분해야함 , chargerId
            Intent intent = new Intent(this, ChargerReservationActivity.class);

            //충전시간 보내주기
            intent.putExtra("chargerId", chargerList.get(clickPOIIndex).id);

            int expectPoint = 0;
            int currentPoint = 0;

            String sDate = "";
            String eDate = "";

            try {
                //실시간 포인트 가져오기
                currentPoint = apiUtils.getUserPoint();

                Log.d(TAG, "충전하기 버튼 클릭 시 현재 포인트 : " + currentPoint);

                //계산된 api에서 값 가져와야함

                sDate = String.format(Locale.KOREA, "%04d", chargingStartYYYY) + "-" + String.format(Locale.KOREA, "%02d", chargingStartMM) + "-" + String.format(Locale.KOREA, "%02d", chargingStartDD) + "T" + String.format(Locale.KOREA, "%02d", chargingStartHH) + ":" + String.format(Locale.KOREA, "%02d", chargingStartII) + ":00";
                eDate = String.format(Locale.KOREA, "%04d", chargingEndYYYY) + "-" + String.format(Locale.KOREA, "%02d", chargingEndMM) + "-" + String.format(Locale.KOREA, "%02d", chargingEndDD) + "T" + String.format(Locale.KOREA, "%02d", chargingEndHH) + ":" + String.format(Locale.KOREA, "%02d", chargingEndII) + ":00";

                expectPoint = apiUtils.getExpectPoint(String.valueOf(chargerList.get(clickPOIIndex).id), sDate, eDate);

            } catch (Exception e) {
                Log.e("metis", "binding.btnReservation.setOnClickListener Exception : " + e);
            }


            if (currentPoint >= expectPoint) {                                                      //포인트가 부족하지 않으면,
                intent.putExtra("reservation", "true");
            } else {                                                                                  //포인트 충전
                intent.putExtra("reservation", "false");
            }

            //현재 잔여포인트
            intent.putExtra("currentPoint", currentPoint);
            //예상 포인트
            intent.putExtra("expectPoint", expectPoint);
            //예약시간
            intent.putExtra("reservationSDate", sDate);
            intent.putExtra("reservationEDate", eDate);
            //충전기이름
            intent.putExtra("chargerName", chargerList.get(clickPOIIndex).name);

            if (isInstantCharge) {                                                                        //즉시 충전

                ReservationModel rModel = new ReservationModel();

                rModel.startDate = sDate;
                rModel.endDate = eDate;
                rModel.reservationType = "RESERVE";
                rModel.chargerId = chargerList.get(clickPOIIndex).id;
                rModel.userId = ThisApplication.staticUserModel.getId();
                rModel.expectPoint = expectPoint;

                Log.d(TAG, "보유 포인트: " + currentPoint);
                Log.d(TAG, "계산 포인트: " + expectPoint);

                /*21.01.04 즉시 충전 시, 포인트 체크 후 수정 START*/
                if (currentPoint < expectPoint) {                                                   //포인트가 부족하면,

                    //현재 포인트 보내줘야함
                    PointChargingDialog pcd = new PointChargingDialog(this, currentPoint);
                    pcd.setCancelable(false);                                                       //DIALOG BACKGROUND CLICK FALSE
                    pcd.show();
                } else {

                    reservationTime = String.valueOf(reserveChargingMinute);                        //minute과 Time의 2개 있는 이유를 모르겠음..minute을 Time에 담아주는것도 없고..

                    InstantChargingDialog icd = new InstantChargingDialog(this, chargerList.get(clickPOIIndex).name, intntChgSTime + " ~ " + intntChgETime, rModel, reservationTime);

                    chkRecharge = "ChargerSearchActivity";
                    icd.setCancelable(false);                                                       //DIALOG BACKGROUND CLICK FALSE
                    icd.show();                                                                     //충전하기 DIALOG SHOW
                }
                /*21.01.04 즉시 충전 시, 포인트 체크 후 수정 END*/
            } else {

                startActivityForResult(intent, Constants.PAGE_RESERVE);
            }

            hideLayout();
        });

        // 충전기 길 안내
        binding.btnNavi.setOnClickListener(view -> {

            Log.e("metis", "클릭한 충전기명 : " + clickChargerModel.name + "\n위도 : " + clickChargerModel.gpsY + "\n경도 : " + clickChargerModel.gpsX);

            goNavigation(clickChargerModel.name, clickChargerModel.gpsX, clickChargerModel.gpsY);

        });

        //즐겨찾기 길 안내
        binding.btnNaviDetail.setOnClickListener(view -> {

            String getGps = binding.btnNaviDetail.getTag().toString();

            String[] value = getGps.split(",");

            goNavigation(binding.txtChgrDetailNm.getText().toString(), Double.parseDouble(value[0]), Double.parseDouble(value[1]));

        });

        // 검색
        binding.editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        // 검색 동작
                        break;
                    default:
                        // 기본 엔터키 동작
                        if (v.getText().toString().trim().length() == 0) {

                            Toast.makeText(MainActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();

                        } else {

                            Intent intent = new Intent(MainActivity.this, SearchKeywordActivity.class);

                            startActivityForResult(intent, Constants.PAGE_SEARCH_KEYWORD);

                        }
                }
                return true;
            }
        });

        binding.editSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {

                if (isFocus) {

                    Intent intent = new Intent(MainActivity.this, SearchKeywordActivity.class);

                    binding.layoutTop1.requestFocus();
                    intent.putExtra("keyword", binding.editSearch.getText().toString());
                    startActivityForResult(intent, Constants.PAGE_SEARCH_KEYWORD);
                }
            }
        });
    }

    private void showReservationDialog() {

        reservationCancelDialog = new CustomDialog(this, "해당 예약건을 취소하시겠습니까?");

        reservationCancelDialog.show();

        reservationCancelDialog.findViewById(R.id.dialog_no_btn).setOnClickListener(view -> {

            reservationCancelDialog.dismiss();
        });

        reservationCancelDialog.findViewById(R.id.dialog_ok_btn).setOnClickListener(view -> {

            //예약취소
            try {
                //예약취소
                boolean result = apiUtils.cancelReservation(Integer.toString(reservationModel.id));

                if(result){
                    if (ThisApplication.staticUserModel.getUserType().equals("Personal")) {
                        SharedPreferences pref = getSharedPreferences("reservation", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("activity", null);
                        editor.commit();
                    }
                }else{
                    Log.e("metis", "취소할 예약건이 없음");
                    Toast.makeText(getApplicationContext(), "취소에 실패하였습니다.\n문제 지속시 고객센터로 문의주세요.", Toast.LENGTH_SHORT).show();
                }

                setTime();

                binding.reservationDetailCloseImg.performClick();
                reservationCancelDialog.cancel();
                reloadInfo();

            } catch (Exception e) {

                Log.e("metis", "예약취소 실패 ");
                Log.e("metis", "error : " + e.getMessage());

                binding.reservationDetailCloseImg.performClick();
                reservationCancelDialog.cancel();
                reloadInfo();

            }
        });
    }

    public void checkBookmark(View view, int chargerId) {
        BookmarkModel model = new BookmarkModel();

        model.userId = ThisApplication.staticUserModel.id;
        model.chargerId = chargerId;

        BookMarkTask task = new BookMarkTask(model);
        task.execute();
    }

    @Override
    public void btnClick(boolean btnType) {
        setTime();
        reloadInfo();
        chkRecharge = "";
    }

    // < >안에 들은 자료형은 순서대로 doInBackground, onProgressUpdate, onPostExecute의 매개변수 자료형(내가 사용할 매개변수타입을 설정하면된다)
    class BookMarkTask extends AsyncTask<Integer, Integer, Boolean> {

        private BookmarkModel model;

        public BookMarkTask(BookmarkModel model) {
            super();
            this.model = model;
        }

        protected void onPreExecute() {

        }

        protected Boolean doInBackground(Integer... values) {

            BookmarkModel bookModel = bookmarkViewModel.selectOneBookmark(ThisApplication.staticUserModel.id, model.chargerId);
            if (bookModel == null) {
                bookmarkViewModel.insertBookmark(model);

                return true;

            } else {
                bookmarkViewModel.deleteBookmarkItem(ThisApplication.staticUserModel.id, model.chargerId);

                return false;
            }
        }

        protected void onPostExecute(Boolean isInsert) {

            if (isInsert) {
                binding.imageFavorite.setBackground(getDrawable(R.mipmap.star_on));
                binding.imageFavorite.setTag("on");
            } else {
                binding.imageFavorite.setBackground(getDrawable(R.mipmap.star_off));
                binding.imageFavorite.setTag("off");
            }
        }
    }

    public void startOwnerActivity(){

        chkRecharge = "OwnerActivity";
        Intent intent = new Intent(this, OwnerActivity.class);
        startActivity(intent);
    }

    //지도 움직일때마다 주소 가져오기
    private void SearchAddress() {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = new ArrayList<>();

        try {
            addresses = geocoder.getFromLocation(locationLat, locationLng, 7);
        } catch (IOException ioException) {
            //네트워크 문제
            Log.e("metis", "지오코더 서비스 사용불가");
        } catch (IllegalArgumentException illegalArgumentException) {

            Log.e("metis", "잘못된 GPS 좌표");
        }

        Log.e("metis", "addresses size : " + addresses.size());
        if (addresses.size() > 0) {
            Address address = addresses.get(0);
            binding.editSearch.setText(address.getAddressLine(0).replaceAll("대한민국 ", ""));
            Log.e("metis", "주소 발견 : " + address);

        }
    }

    @Override
    public void init() {

        translateTop = AnimationUtils.loadAnimation(this, R.anim.translate_top);
        translateBottom = AnimationUtils.loadAnimation(this, R.anim.translate_bottom);

        //3.애니메이션에 옵션을 적용시킨다.
        SlidingAnimationListener listener = new SlidingAnimationListener();
        translateTop.setAnimationListener(listener);
        translateBottom.setAnimationListener(listener);

        //지도 위치
        showAll();

        setMenuClass(this);
        setUpDrawer(binding.drawerLayout.getId(), binding.listSlidermenu.getId());
        setActionBarDrawerToggle(binding.drawerLayout, binding.btnMenu);

        addActivitys(this);

        //해당페이지 이벤트 막기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        handler.postDelayed(r, 1000); // 1초 뒤에 Runnable 객체 수행

        //소유자일 경우 소유자 화면 이동

        //test
        //Intent intent = new Intent(this,OwnerActivity.class);
        //startActivity(intent);
        //test

    }

    Runnable r = () -> {

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        /*if (!this.isDestroyed()) {
            Glide.with(this).onDestroy();
        }*/
        if (binding.loading.getVisibility() == View.VISIBLE) {
            binding.loading.setVisibility(View.INVISIBLE);
        }

    };

    private void setTime() {

        /*현재시간으로 수정 START*/
        Calendar nowCal = Calendar.getInstance(Locale.getDefault());                                //시작시간을 위한 켈린더 선언

        if (isInstantCharge) {
            chargingStartYYYY = nowCal.get(Calendar.YEAR);
            chargingStartMM = nowCal.get(Calendar.MONTH) + 1;
            chargingStartDD = nowCal.get(Calendar.DAY_OF_MONTH);
            chargingStartHH = nowCal.get(Calendar.HOUR_OF_DAY);
            chargingStartII = nowCal.get(Calendar.MINUTE);

            nowCal.add(Calendar.MINUTE, reserveChargingMinute);                                         //이용시간 계산

            chargingEndYYYY = nowCal.get(Calendar.YEAR);
            chargingEndMM = nowCal.get(Calendar.MONTH) + 1;
            chargingEndDD = nowCal.get(Calendar.DAY_OF_MONTH);
            chargingEndHH = nowCal.get(Calendar.HOUR_OF_DAY);
            chargingEndII = nowCal.get(Calendar.MINUTE);

        } else {
            chargingStartYYYY = reservationDateModel.getYear();
            chargingStartMM = reservationDateModel.getMonth();
            chargingStartDD = reservationDateModel.getDay();
            chargingStartHH = reservationDateModel.getHour();
            chargingStartII = reservationDateModel.getMinute();

            chargingEndYYYY = reservationDateModel.getEndYear();
            chargingEndMM = reservationDateModel.getEndMonth();
            chargingEndDD = reservationDateModel.getEndDay();
            chargingEndHH = reservationDateModel.getEndHour();
            chargingEndII = reservationDateModel.getEndMinute();
        }
        /*현재시간으로 수정 END*/

        //하단 시간 표시
        setTimeInit();
    }

    private void showSearchCondition() {

        setTimeInit();

        Intent intent = new Intent(this, MarkerSearchConditionActivity.class);

        intent.putExtra("chargingMinute", reserveChargingMinute);          // 최초 4시간 디폴트값 넘기기.
        intent.putExtra("chargingStartWeek", DateUtils.getWeek(
                String.format(Locale.KOREA, "%02d", chargingStartYYYY)
                        + String.format(Locale.KOREA, "%02d", chargingStartMM)
                        + String.format(Locale.KOREA, "%02d", chargingStartDD)));

        intent.putExtra("chargingStartYYYY", chargingStartYYYY);    // 충전 시작 년도
        intent.putExtra("chargingStartMM", chargingStartMM);        // 충전 시작 월
        intent.putExtra("chargingStartDD", chargingStartDD);        // 충전 시작 일
        intent.putExtra("chargingStartHH", chargingStartHH);        // 충전 시작 시
        intent.putExtra("chargingStartII", chargingStartII);        // 충전 시작 분

        intent.putExtra("chargingEndWeek", DateUtils.getWeek(
                String.format(Locale.KOREA, "%02d", chargingEndYYYY)
                        + String.format(Locale.KOREA, "%02d", chargingEndMM)
                        + String.format(Locale.KOREA, "%02d", chargingEndDD)));

        intent.putExtra("chargingEndYYYY", chargingEndYYYY);        // 충전 종료 년도
        intent.putExtra("chargingEndMM", chargingEndMM);            // 충전 종료 월
        intent.putExtra("chargingEndDD", chargingEndDD);            // 충전 종료 일
        intent.putExtra("chargingEndHH", chargingEndHH);            // 충전 종료 시
        intent.putExtra("chargingEndII", chargingEndII);            // 충전 종료 분

        intent.putExtra("reserveRadius", reserveRadius);              // 예약 반경 거리
        intent.putExtra("reserveType", reserveType);              // 예약 충전기 타입
        
        intent.putExtra("isInstantCharge", String.valueOf(isInstantCharge));     //즉시, 예약

        Log.e("metis", "isInstantCharge : " + isInstantCharge);

        startActivityForResult(intent, Constants.PAGE_SEARCH_CONDITION);

        overridePendingTransition(R.anim.translate_top, R.anim.translate_bottom);
    }

    private void createSearchKeywordMarker(MapView mapView, SearchKeywordModel model) {

        double latPoint = Double.parseDouble(model.y);
        double lngPoint = Double.parseDouble(model.x);

        MapPOIItem mDefaultMarker = new MapPOIItem();

        String name = model.placeName;

        mDefaultMarker.setItemName(name);
        mDefaultMarker.setTag(9999);
        mDefaultMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(latPoint, lngPoint));

        mDefaultMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        mDefaultMarker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        mDefaultMarker.setCustomImageResourceId(R.mipmap.search_keyword_marker_64);
        mDefaultMarker.setCustomSelectedImageResourceId(R.mipmap.search_keyword_marker_64);

        mDefaultMarker.setShowCalloutBalloonOnTouch(true);                     // POI 클릭시 말풍선 보여주는지 여부

        mapView.addPOIItem(mDefaultMarker);

        mapPOIItems = mapView.getPOIItems();

    }

    private void createDefaultMarker(MapView mapView) {

        mapView.removeAllPOIItems();
        MapPOIItem mDefaultMarker = new MapPOIItem();

        /*mDefaultMarker.setItemName("마커 기준 조건 검색");
        mDefaultMarker.setTag(0);
        mDefaultMarker.setMapPoint(pointList.get(0));
        mDefaultMarker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        mDefaultMarker.setCustomSelectedImageResourceId(R.mipmap.current_location_48);
        mDefaultMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        mDefaultMarker.setCustomImageResourceId(R.mipmap.current_location_48);
        mDefaultMarker.setShowCalloutBalloonOnTouch(false);                     // POI 클릭시 말풍선 보여주는지 여부

        mapView.addPOIItem(mDefaultMarker);*/

        for (int i = 0; i < pointList.size(); i++) {

            mDefaultMarker = new MapPOIItem();

            String name = chargerList.get(i).name;

            mDefaultMarker.setItemName(name);
            mDefaultMarker.setTag(i);
            mDefaultMarker.setMapPoint(pointList.get(i));
            mDefaultMarker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            //mDefaultMarker.setCustomSelectedImageResourceId(R.mipmap.seleted_marker);

            if (chargerList.get(i).currentStatusType.equals("READY")) {           // 대기 중

                mDefaultMarker.setCustomSelectedImageResourceId(R.mipmap.seleted_marker);
                mDefaultMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                mDefaultMarker.setCustomImageResourceId(R.mipmap.blue_marker_40);
            } else {
                mDefaultMarker.setCustomSelectedImageResourceId(R.mipmap.seleted_marker_red);
                mDefaultMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                mDefaultMarker.setCustomImageResourceId(R.mipmap.red_marker_40);
            }

            mDefaultMarker.setShowCalloutBalloonOnTouch(false);                     // POI 클릭시 말풍선 보여주는지 여부
            mapView.addPOIItem(mDefaultMarker);
        }
        mapPOIItems = mapView.getPOIItems();
    }

    private void showAll() {

        int padding = 5;
        float minZoomLevel = 4;
        float maxZoomLevel = 10;

        //pointList.add(MapPoint.mapPointWithGeoCoord(Constants.currentLocationLat, Constants.currentLocationLng));

        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Constants.currentLocationLat, Constants.currentLocationLng);

        if (checkLocationServiceStatus()) {

            MapPointBounds bounds = new MapPointBounds(mapPoint, mapPoint);
            binding.mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds, padding, minZoomLevel, maxZoomLevel));
        }
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

        int chargerId;

        //페이지가 오픈 안될때도 있음
        //클릭 시 마다 false 초기화 해줌
        isPageOpen = false;

        setTime(); //시간 새로고침

        try {
            Log.e("metis", "onPOIItemSelected else");
            int tag = mapPOIItem.getTag();

            if (tag >= 0) {
                Log.e("metis", "onPOIItemSelected tag != 0");
                Log.e("metis", "mapPOIItem.getTag() : " + mapPOIItem.getTag());
                clickPOIIndex = mapPOIItem.getTag();

                if (beforeClickMapPOIItem == null) {

                    beforeClickMapPOIItem = mapPOIItem;

                    if (!isPageOpen) {

                        //충전기 상세정보
                        chargerId = setChargerDetailInfo();

                    } else {
                        chargerId = -1;
                    }
                } else {

                    if (beforeClickMapPOIItem == mapPOIItem) {

                        if (!isPageOpen) {

                            //충전기 상세정보
                            chargerId = setChargerDetailInfo();

                        } else {
                            chargerId = -1;
                        }
                    } else {

                        isPageOpen = false;

                        //충전기 상세정보
                        chargerId = setChargerDetailInfo();

                    }
                    beforeClickMapPOIItem = mapPOIItem;
                }

                if (chargerId != -1) {
                    clickChargerModel = chargerList.get(clickPOIIndex);
                }

            }
        } catch (Exception e) {
            Log.e("metis", "onPOIItemSelected  Exception : " + e);
        }

        mapView.setMapCenterPoint(mapPOIItem.getMapPoint(), true);
    }

    //충전기 상세정보
    public int setChargerDetailInfo(){

        binding.txtChgrNm.setText(chargerList.get(clickPOIIndex).name);
        //주소 및 충전 요금 즐겨찾기
        setChargerInfo();

        binding.layoutChgrInfo.setVisibility(View.VISIBLE);
        binding.layoutChgrInfo.startAnimation(translateTop);

        binding.layoutChargingInfo.setVisibility(View.INVISIBLE);

        return chargerList.get(clickPOIIndex).id;
    }

    public void setChargerInfo() {

        Log.e("metis", "clickPOIIndex : " + clickPOIIndex);
        Log.e("metis", "chargerList : " + chargerList.size());

        binding.txtChgrAddrNm.setText(chargerList.get(clickPOIIndex).address);
        binding.txtChgrAmtValue.setText(chargerList.get(clickPOIIndex).rangeOfFee);

        //즐겨찾기 확인
        CheckBookmarkBackgroundTask task = new CheckBookmarkBackgroundTask(this, "ChargerInfo", chargerList.get(clickPOIIndex).id);
        task.execute();

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

        if (mapPOIItem.getTag() == 0 || mapPOIItem.getTag() == 9999) {

        }
        // 키워드 검색 후 말풍선 클릭
    }

    public void goNavigation(String name, double getX, double getY) {

        KakaoNaviService.getInstance().navigate(this, KakaoNaviParams.newBuilder(
                Location.newBuilder(name, getX, getY).build()).setNaviOptions(
                NaviOptions.newBuilder()
                        .setCoordType(CoordType.WGS84)
                        .setVehicleType(VehicleType.FIRST)
                        .setRpOption(RpOption.FAST).build()
        ).build());
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

        if (isSearchKeywordMarkerClick) {    // 키워드 검색 후 말풍선 클릭

        } else {                            // 그 외

        }
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

        // 맵의 중앙이 이동될때마다 마커 중앙에 포인트 마커도 같이 움직인다.
        //mapView.getPOIItems()[0].setMapPoint(mapPoint);

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) { // 마커 밖에 지도 클릭 했을 경우.

        hideLayout();
    }

    private void hideLayout() {
        if (clickPOIIndex != -1) {

            clickPOIIndex = -1;

            if (binding.layoutChgrInfo.getVisibility() == View.VISIBLE) {

                binding.layoutChgrInfo.startAnimation(translateBottom);
                BottomSheetBehavior.from(binding.layoutChgrInfo).setState(BottomSheetBehavior.STATE_COLLAPSED);

            } else if (binding.layoutReservationDetailInfo.getVisibility() == View.VISIBLE) {

                binding.layoutReservationDetailInfo.startAnimation(translateBottom);
                binding.layoutReservationDetailInfo.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

        // 맵의 중앙이 이동될때마다 마커 중앙에 포인트 마커도 같이 움직인다.
        // 맵을 더블클릭 하여 줌이 달라졌을때의 중앙 지점으로 마커 이동
        //mapView.getPOIItems()[0].setMapPoint(mapPoint);
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

        if (searchKeyword.equals("")) {
            locationLat = mapPoint.getMapPointGeoCoord().latitude;
            locationLng = mapPoint.getMapPointGeoCoord().longitude;

            Log.e("metis", "locationLat : " + locationLat);
            Log.e("metis", "locationLng : " + locationLng);

            SearchAddress();
        }
        searchKeyword = "";
    }

    class SlidingAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            //애니메이션이 끝날때 동작

            if (isPageOpen) {
                //애니메이션이 끝날때 페이지가 열려있는 상태다하면
                //오른쪽으로 열려있다. 즉 보이지 않는다.

                binding.layoutChgrInfo.setVisibility(View.INVISIBLE);

                binding.layoutChargingInfo.setVisibility(View.VISIBLE);

                isPageOpen = false;
            } else {

                binding.layoutChgrInfo.bringChildToFront(binding.mapView);
                binding.layoutChgrInfo.setClickable(true);
                binding.layoutChgrInfo.setFocusable(true);
                isPageOpen = true;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    /**
     * 키워드 검색 내용으로 지도에 마커 뿌려줌.
     *
     * @param model searchKeywordModel
     */
    private void goSearchPosition(SearchKeywordModel model) {

        isSearchKeywordMarkerClick = true;

        Log.e("metis", "mapView  2 : " + model.x + " , " + model.y);

        double latPoint = Double.parseDouble(model.y);
        double lngPoint = Double.parseDouble(model.x);

        int padding = 5;
        float minZoomLevel = 3;
        float maxZoomLevel = 10;
        MapPointBounds bounds = new MapPointBounds(MapPoint.mapPointWithGeoCoord(latPoint, lngPoint), MapPoint.mapPointWithGeoCoord(latPoint, lngPoint));
        binding.mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds, padding, minZoomLevel, maxZoomLevel));
    }

    /**
     * 마커 정보 add
     *
     * @param lat 위도
     * @param lng 경도
     */
    private void addChargerInfo(double lat, double lng) {

        //list 초기화
        pointList.clear();
        chargerList.clear();

        /*gifImage = new GlideDrawableImageViewTarget(binding.imageLoading);
        Glide.with(this).load(R.mipmap.spinner_loading).into(gifImage);*/
        showLoading(binding.loading);

        //예약 상태 확인, 예약없음, 예약 있음, 충전 중
        //reservationModel = apiUtils.getReservationStatus();

        if (reservationModel != null) {

            //userType 체크, 예약 있을시 예약화면 표시 or 충전중 표시
            checkRecharge();

            SearchAddress();

            binding.layoutChargingInfo.setVisibility(View.INVISIBLE);

            //예약클릭시 디테일정보 표시
            setReservationDetailInfo(reservationModel);

            binding.layoutReservationInfo.setVisibility(View.VISIBLE);

            pointList.add(MapPoint.mapPointWithGeoCoord(Constants.currentLocationLat, Constants.currentLocationLng));

        }
        //예약없을 시 충전기 검색
        else {

            binding.layoutChargingInfo.setVisibility(View.VISIBLE);
            binding.layoutReservationInfo.setVisibility(View.INVISIBLE);

            /*ChargerModel chargerModel;

            chargerModel = new ChargerModel();

            chargerModel.id = -1;
            chargerModel.address = "현재위치";
            chargerModel.gpsY = lat;
            chargerModel.gpsX = lng;
            chargerModel.name = "현재위치";

            chargerList.add(chargerModel);*/

            getChargersAPI();

            for (int i = 0; i < chargerList.size(); i++) {

                pointList.add(MapPoint.mapPointWithGeoCoord(chargerList.get(i).gpsY, chargerList.get(i).gpsX));
            }

            createDefaultMarker(binding.mapView);
        }
    }

    public void getChargersAPI() {

        SearchAddress();

        Log.e("metis", "getChargersAPI");

        String tempRadius = reserveRadius;

        if (tempRadius.equals("전체")) {
            tempRadius = "";
        } else {
            tempRadius = tempRadius.replaceAll("km", "").trim();
        }
        Log.e("metis", "tempRadius : " + tempRadius);

        Log.e("metis", "gpsX : " + Constants.currentLocationLat);
        Log.e("metis", "gpsY : " + Constants.currentLocationLng);

        String tempStartDate = String.format(Locale.KOREA, "%04d", chargingStartYYYY) + "-" + String.format(Locale.KOREA, "%02d", chargingStartMM) + "-" + String.format(Locale.KOREA, "%02d", chargingStartDD) + "T" + String.format(Locale.KOREA, "%02d", chargingStartHH) + ":" + String.format(Locale.KOREA, "%02d", chargingStartII) + ":00";

        Log.e("metis", "tempStartDate  : " + tempStartDate);

        String tempEndDate = String.format(Locale.KOREA, "%04d", chargingEndYYYY) + "-" + String.format(Locale.KOREA, "%02d", chargingEndMM) + "-" + String.format(Locale.KOREA, "%02d", chargingEndDD) + "T" + String.format(Locale.KOREA, "%02d", chargingEndHH) + ":" + String.format(Locale.KOREA, "%02d", chargingEndII) + ":00";
        Log.e("metis", "endDate : " + tempEndDate);

        try {

            //충전기 리스트 가져오기
            Map<String, Object> map = apiUtils.getChargers(tempStartDate, tempEndDate, tempRadius, chargerList);

            boolean result = (boolean) map.get("result");

            if(result){
                chargerList = (ArrayList<ChargerModel>) map.get("list");
            }else{
                Toast.makeText(getApplicationContext(), "충전기 목록을 가져오는데 실패하였습니다. 충전기 검색을 다시 해주세요.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("metis", "getChargersAPI Exception : " + e);
        }
    }

    public void setReservationDetailInfo(ReservationModel reservationModel) {

        String getStartDate = reservationModel.startDate;

        Log.e("metis", "getStartDate :" + getStartDate);

        String getEndDate = reservationModel.endDate;

        Log.e("metis", "getEndDate :" + getEndDate);

        Calendar startCal = commonUtils.setCalendarDate(getStartDate);
        Calendar endCal = commonUtils.setCalendarDate(getEndDate);

        //요일 구하기
        String getWeek = DateUtils.getWeek(
                String.format(Locale.KOREA, "%02d", startCal.get(Calendar.YEAR))
                        + String.format(Locale.KOREA, "%02d", startCal.get(Calendar.MONTH) + 1)
                        + String.format(Locale.KOREA, "%02d", startCal.get(Calendar.DAY_OF_MONTH)));

        //총 몇시간 충전인지 구하기
        long diff = endCal.getTimeInMillis() - startCal.getTimeInMillis();

        long min = diff / (60 * 1000);
        long hour = diff / (60 * 60 * 1000);
        long day = diff / (24 * 60 * 60 * 1000);

        reservationTime = String.valueOf(min);

        String totalTime = "";

        if ((hour - day * 24) != 0) {
            totalTime = (hour - day * 24) + "시간 ";
        }
        if ((min - hour * 60) != 0) {
            totalTime += (min - hour * 60) + "분";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        String timeTerm = String.format(Locale.KOREA, "%02d", startCal.get(Calendar.MONTH) + 1) + "/" + String.format(Locale.KOREA, "%02d", startCal.get(Calendar.DAY_OF_MONTH)) + " " + getWeek + " " + sdf.format(startCal.getTime()) + " ~ " + sdf.format(endCal.getTime());

        binding.txtReservationTime.setText(totalTime);
        binding.txtReservationTerm.setText(timeTerm);

        binding.reservationDetailTotalTime.setText(totalTime);
        binding.reservationDetailDetailTime.setText(timeTerm);
        binding.txtChgrDetailNm.setText(reservationModel.chargerName);

        binding.txtChgrDetailNm.setTag(reservationModel.chargerId);

        //"제주 제주시 관광대학로 111(아라일동)"
        binding.txtChgrAddrDetailNm.setText(reservationModel.chargerAddress);

        //"178p"
        binding.txtChgrDetailAmtValue.setText(reservationModel.rangeOfFee);

        //즐겨찾기 확인
        CheckBookmarkBackgroundTask task = new CheckBookmarkBackgroundTask(this, "ReservationInfo", reservationModel.chargerId);
        task.execute();

        //네비이미지
        binding.btnNaviDetail.setTag(reservationModel.gpsX + "," + reservationModel.gpsY);

    }

    public void checkRecharge() {

        try {

            binding.layoutReservationInfoRechargeTxt.setText("예약");
            binding.mainRechargingTxt.setVisibility(View.INVISIBLE);
            binding.chgrDetailCancel.setVisibility(View.VISIBLE);
            binding.chgrDetailStart.setText("충전 시작");

            Log.e("metis", " gerUserType is " + gerUserType);

            //현재시간이 예약 start,end Date 범위안에 있고 예약상태가 KEEP일경우 (충전중)
            if (commonUtils.checkRechargeTime(reservationModel) && reservationModel.state.equals("KEEP")) {
                binding.layoutReservationInfoRechargeTxt.setText("충전중");
                binding.mainRechargingTxt.setVisibility(View.VISIBLE);
                binding.chgrDetailCancel.setVisibility(View.GONE);
                binding.chgrDetailStart.setText("충전기 연결");

                SharedPreferences pref = getSharedPreferences("reservation", MODE_PRIVATE);

                // UserType이 소유주 이고
                if (gerUserType != null && gerUserType.equals("Personal") && !chkRecharge.equals("onResume")) {
                    String getActivity = pref.getString("activity", "");

                    //소유주 화면에서 충전 중일시 소유주 화면으로 이동
                    if (getActivity.equals("OwnerActivity")) {
                        startOwnerActivity();
                    }
                }

            }
            //충전중인 상황에서 자동종료
            else if (!commonUtils.checkRechargeTime(reservationModel) && reservationModel.state.equals("KEEP")) {
                mainRechargeModel();
            }

            else {
                clickPOIIndex = -1;

                if (binding.layoutChgrInfo.getVisibility() == View.VISIBLE) {

                    binding.layoutChgrInfo.startAnimation(translateBottom);
                    BottomSheetBehavior.from(binding.layoutChgrInfo).setState(BottomSheetBehavior.STATE_COLLAPSED);

                } else if (binding.layoutReservationDetailInfo.getVisibility() == View.VISIBLE) {

                    binding.layoutReservationDetailInfo.startAnimation(translateBottom);
                    binding.layoutReservationDetailInfo.setVisibility(View.INVISIBLE);
                }

                if (!this.isDestroyed()) {
                    Glide.with(this).onDestroy();
                }
            }

        } catch (Exception e) {
            Log.e("metis", "checkRecharge Exception : " + e);
        }

    }

    // 메인화면에서 충전결과 화면 표시.
    public void mainRechargeModel() {

        try {

            SharedPreferences pref = getSharedPreferences("reservation", MODE_PRIVATE);
            String prefTime = pref.getString("time", "");

            ownerResult = true;

            //modal 추가
            /*충전결과 MSG를 위하여 MODEL에 SET START*/
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            RechargeModel rModel = new RechargeModel();   //충전결과 MSG MODEL SET

            Date oldDate = format.parse(prefTime);
            Date nowDate = format.parse(reservationModel.getEndDate().replaceAll("T", " "));
            Log.e("metis", "oldDate : " + format.format(oldDate));
            Log.e("metis", "nowDate : " + format.format(nowDate));

            long diff = nowDate.getTime() - oldDate.getTime();
            long second = diff / 1000;

            rModel.reservationPoint = reservationModel.expectPoint;
            rModel.rechargePoint = 0;
            rModel.chargingTime = commonUtils.chargingTime((int) second);                                  //sec를 hh:mm:ss로 변환 CALL
            rModel.startRechargeDate = prefTime;
            rModel.endRechargeDate = commonUtils.timeSecCalculation(rModel.startRechargeDate, (int) second);   //sec 시간 계산(yyyy-MM-dd HH:mm:ss) CALL

            showChargerFinishDialog(rModel);
            /*충전결과 MSG를 위하여 MODEL에 SET END*/

            SharedPreferences.Editor editor = pref.edit();
            editor.putString("activity", null);
            editor.putString("time", null);
            editor.commit();

        } catch (Exception e) {
            Log.e("metis", "checkRecharge Exception : " + e);
        }
    }

    /*
     * CHARGER FINNISH DIALOG SHOW
     * */
    private void showChargerFinishDialog(RechargeModel rModel) {

        ChargerFinishDialog cfd = new ChargerFinishDialog(this, rModel, this);

        cfd.getType = "Main";
        cfd.setCancelable(false);                                                                   //DIALOG BACKGROUND CLICK FALSE
        cfd.show();
    }

    // < >안에 들은 자료형은 순서대로 doInBackground, onProgressUpdate, onPostExecute의 매개변수 자료형(내가 사용할 매개변수타입을 설정하면된다)
    class CheckBookmarkBackgroundTask extends AsyncTask<Integer, Integer, Boolean> {

        String getType;
        int getChargerId;
        Context context;
        Response<Object> response;

        public CheckBookmarkBackgroundTask(Context context, String getType, int getChargerId) {
            super();

            this.context = context;
            this.getType = getType;
            this.getChargerId = getChargerId;

        }

        protected void onPreExecute() {

        }

        protected Boolean doInBackground(Integer... values) {

            BookmarkModel model = bookmarkViewModel.selectOneBookmark(ThisApplication.staticUserModel.id, getChargerId);

            Log.e("metis", "getChargerId");

            Log.e("metis", String.valueOf(getChargerId));

            try {
                response = apiUtils.getReservationsChargersList(String.valueOf(getChargerId));

            } catch (Exception e) {
                e.printStackTrace();
            }

            return model != null;
        }

        protected void onPostExecute(Boolean isBoolean) {

            if (getType.equals("ChargerInfo")) {

                if (isBoolean) {
                    binding.imageFavorite.setBackground(getDrawable(R.mipmap.star_on));
                    binding.imageFavorite.setTag("on");
                } else {
                    binding.imageFavorite.setBackground(getDrawable(R.mipmap.star_off));
                    binding.imageFavorite.setTag("off");
                }

                if (response.code() == 200) {

                    //예약 이용가능 시간
                    JSONObject json = new JSONObject((Map) response.body());
                    Log.e("metis", "json : " + json);

                    //yyyyMMddHHmm
                    String getFullSDate = String.format(Locale.KOREA, "%04d", chargingStartYYYY) + String.format(Locale.KOREA, "%02d", chargingStartMM) + String.format(Locale.KOREA, "%02d", chargingStartDD) + String.format(Locale.KOREA, "%02d", chargingStartHH) + String.format(Locale.KOREA, "%02d", chargingStartII);

                    Log.e("metis", "getFullSdate : " + getFullSDate);

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    Calendar startCal = Calendar.getInstance();

                    startCal.set(Integer.parseInt(getFullSDate.substring(0, 4)), Integer.parseInt(getFullSDate.substring(4, 6)), Integer.parseInt(getFullSDate.substring(6, 8)), Integer.parseInt(getFullSDate.substring(8, 10)), Integer.parseInt(getFullSDate.substring(10, 12)), 00);
                    //month+1이 나오기 때문에 다시 -1 해줌
                    startCal.add(Calendar.MONTH, -1);

                    String currentTime = format.format(startCal.getTime());
                    intntChgSTime = currentTime.substring(currentTime.length() - 8, currentTime.length());
                    Log.e("metis", "currentTime : " + currentTime);

                    System.out.println("충전 분 :" + reserveChargingMinute);

                    try {

                        //openTime, closeTime 가져오기
                        JSONObject chargerAllowTimeObject = json.getJSONObject("chargerAllowTime");

                        List<AllowTimeOfDayModel> availableTimeList = new ArrayList<>();

                        for (int i=0; i<2; i++) {
                            AllowTimeOfDayModel allowTimeOfDayModel = new AllowTimeOfDayModel();
                            allowTimeOfDayModel.setId(i);

                            if (i == 0) {
                                allowTimeOfDayModel.setOpenTime(chargerAllowTimeObject.get("todayOpenTime").toString());
                                allowTimeOfDayModel.setCloseTime(chargerAllowTimeObject.get("todayCloseTime").toString());
                            } else if (i == 1) {
                                allowTimeOfDayModel.setOpenTime(chargerAllowTimeObject.get("tomorrowOpenTime").toString());
                                allowTimeOfDayModel.setCloseTime(chargerAllowTimeObject.get("tomorrowCloseTime").toString());
                            }

                            availableTimeList.add(allowTimeOfDayModel);
                        }

                        //예약 목록
                        JSONObject reservationObject = json.getJSONObject("reservations");

                        JSONArray reservationArray = reservationObject.getJSONArray("content");

                        List<ReservationModel> reservationList = new ArrayList<>();

                        for (int i = 0; i < reservationArray.length(); i++) {
                            Log.e("metis", reservationArray.get(i).toString());

                            Gson gson = new Gson();
                            ReservationModel reservationModel = gson.fromJson(reservationArray.get(i).toString(), ReservationModel.class);

                            reservationList.add(reservationModel);
                        }
                        reservationList = commonUtils.sortReservationList(reservationList);

                        //txt 그리는 부분
                        LinearLayout layoutText = binding.layoutChgrTimeAvailableTxt;
                        layoutText.removeAllViews();

                        //이용 가능 시간대 라벨 구하기
                        List<String> labelList = getAvailablePeriodLabelList(json, availableTimeList, reservationList, getFullSDate);

                        if (labelList.size() > 0) {
                            binding.timeAvailableLayout.setVisibility(View.VISIBLE);
                            binding.txtAllTime.setVisibility(View.INVISIBLE);

                            for (int i=0; i<labelList.size(); i++) {
                                setLinearLayoutText(layoutText, context, labelList.get(i));
                            }

                        } else {

                            binding.timeAvailableLayout.setVisibility(View.INVISIBLE);
                            binding.txtAllTime.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (response.code() == 400) {
                    Toast.makeText(context, "서버와 통신이 원활하지 않습니다. 문제가 지속될 시 고객센터로 문의주십시오.", Toast.LENGTH_SHORT).show();
                }

            } else {

                if (isBoolean) {
                    binding.imageFavoriteDetail.setBackground(getDrawable(R.mipmap.star_on));
                    binding.imageFavoriteDetail.setTag("on");
                } else {
                    binding.imageFavoriteDetail.setBackground(getDrawable(R.mipmap.star_off));
                    binding.imageFavoriteDetail.setTag("off");
                }
            }
        }
    }

    public List<String> getAvailablePeriodLabelList(JSONObject jsonObject, List<AllowTimeOfDayModel> availableTimeList, List<ReservationModel> reservationList, String selectedStartDateString) {

        currentReservationList.clear();
        openCloseTimeList.clear();

        Date nowDate = new Date();
        Date selectedStartDate = null;

        String today = ymdFormatter.format(nowDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        calendar.add(Calendar.DATE, 1);

        String tomorrow = ymdFormatter.format(calendar.getTime());

        try {
            selectedStartDate = ymdHmFormatter.parse(selectedStartDateString);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (int i=0; i<reservationList.size(); i++) {
            addReservationList(reservationList.get(i).getStartDate(), reservationList.get(i).getEndDate(), "reservation");
        }

        for (int i=0; i<availableTimeList.size(); i++) {

            AllowTimeOfDayModel allowTimeOfDayModel = new AllowTimeOfDayModel();
            allowTimeOfDayModel = availableTimeList.get(i);

            String startTime = "";
            String endTime = "";

            if (i == 0) {
                startTime = today + allowTimeOfDayModel.getOpenTime();
                endTime = today + allowTimeOfDayModel.getCloseTime();
            } else if (i ==1) {
                startTime = tomorrow + allowTimeOfDayModel.getOpenTime();
                endTime = tomorrow + allowTimeOfDayModel.getCloseTime();
            }

            addReservationList(startTime, endTime, "openClose");
        }

        List<String> resultList = new ArrayList<>();

        if (currentReservationList.size() == 0) {
            //예약 없을 때
            if (HHMMFormatter.format(openCloseTimeList.get(0).getStartDate()).equals("00:00") && HHMMFormatter.format(openCloseTimeList.get(0).getEndDate()).equals("23:59") &&
                    HHMMFormatter.format(openCloseTimeList.get(1).getStartDate()).equals("00:00") && HHMMFormatter.format(openCloseTimeList.get(1).getEndDate()).equals("23:59")) {
                // 항시 충전 가능
            }

            else {
                //현재 시간보다 openTime이 클 경우 ex) 현재 - 18:00, openTime - 19:00
                if (selectedStartDate.getTime() < openCloseTimeList.get(0).getStartDate().getTime()) {
                    String label = HHMMFormatter.format(openCloseTimeList.get(0).getStartDate()) + " ~ " + HHMMFormatter.format(openCloseTimeList.get(0).getEndDate());
                    resultList.add(label);
                } else {

                    if (check30Minute(selectedStartDate.getTime(), openCloseTimeList.get(0).getEndDate().getTime())) {
                        String label = HHMMFormatter.format(selectedStartDate) + " ~ " + HHMMFormatter.format(openCloseTimeList.get(0).getEndDate());
                        resultList.add(label);
                    }
                }

                String label = HHMMFormatter.format(openCloseTimeList.get(1).getStartDate()) + " ~ " + HHMMFormatter.format(openCloseTimeList.get(1).getEndDate());
                resultList.add(label);
            }

        } else {


            for (int i=0; i<currentReservationList.size(); i++) {

                //시작 일시가 오늘, 종료 일시가 내일
                if (ymdFormatter.format(currentReservationList.get(i).getStartDate()).equals(today) &&
                        ymdFormatter.format(currentReservationList.get(i).getEndDate()).equals(tomorrow)) {

                    if (currentReservationList.get(i).getStartDate().getTime() < openCloseTimeList.get(0).getEndDate().getTime()) {
                        openCloseTimeList.get(0).setEndDate(currentReservationList.get(i).getStartDate());
                    }
                    if (currentReservationList.get(i).getEndDate().getTime() > openCloseTimeList.get(1).getStartDate().getTime()) {
                        openCloseTimeList.get(1).setStartDate(currentReservationList.get(i).getEndDate());
                    }
                    currentReservationList.remove(i);
                }
            }

            //예약이 오늘에서 내일로 넘어가는 즉시충전건 뿐일때
            if (currentReservationList.size() == 0) {
                String label = HHMMFormatter.format(openCloseTimeList.get(1).getStartDate()) + " ~ " + HHMMFormatter.format(openCloseTimeList.get(1).getEndDate());
                resultList.add(label);
            } else {

                int tomorrowReservationExist = 0;
                //예약 있읕때
                for (int i=0; i<currentReservationList.size(); i++) {
                    if (ymdFormatter.format(currentReservationList.get(i).getStartDate()).equals(tomorrow)
                            || ymdFormatter.format(currentReservationList.get(i).getEndDate()).equals(tomorrow)) {
                        tomorrowReservationExist += 1;
                    }

                    if (i == 0) {
                        //첫번째 예약이 오늘일때
                        if (ymdFormatter.format(currentReservationList.get(i).getStartDate()).equals(today)
                                && ymdFormatter.format(currentReservationList.get(i).getEndDate()).equals(today)) {
                            //현재 시간보다 openTime이 클 경우 ex) 현재 - 18:00, openTime - 19:00
                            if (selectedStartDate.getTime() < openCloseTimeList.get(0).getStartDate().getTime()) {
                                if (check30Minute(openCloseTimeList.get(0).getStartDate().getTime(), recalculateBefore30Minute(currentReservationList.get(i).getStartDate()).getTime())) {
                                    String label = HHMMFormatter.format(openCloseTimeList.get(0).getStartDate()) + " ~ " + HHMMFormatter.format(recalculateBefore30Minute(currentReservationList.get(i).getStartDate()));
                                    resultList.add(label);
                                }

                            } else {
                                if (selectedStartDate.getTime() < currentReservationList.get(i).getStartDate().getTime()) {
                                    if (check30Minute(selectedStartDate.getTime(), recalculateBefore30Minute(currentReservationList.get(i).getStartDate()).getTime())) {
                                        String label = HHMMFormatter.format(selectedStartDate) + " ~ " + HHMMFormatter.format(recalculateBefore30Minute(currentReservationList.get(i).getStartDate()));
                                        resultList.add(label);
                                    }
                                }
                            }
                        } else {
                            //현재 시간보다 openTime이 클 경우 ex) 현재 - 18:00, openTime - 19:00
                            if (selectedStartDate.getTime() < openCloseTimeList.get(0).getEndDate().getTime()) {
                                if (check30Minute(selectedStartDate.getTime(), openCloseTimeList.get(0).getEndDate().getTime())) {
                                    String label = HHMMFormatter.format(selectedStartDate) + " ~ " + HHMMFormatter.format(openCloseTimeList.get(0).getEndDate());
                                    resultList.add(label);
                                }
                            }
                            /*String label = HHMMFormatter.format(openCloseTimeList.get(1).getStartDate()) + " ~ " + HHMMFormatter.format(reservationList.get(i).getStartDate());
                            resultList.add(label);*/
                        }
                    }

                    if (i != currentReservationList.size() -1) {

                        if (ymdFormatter.format(currentReservationList.get(i).getEndDate()).equals(today)) {
                            if (ymdFormatter.format(currentReservationList.get(i+1).getEndDate()).equals(today)) {
                                if (check30Minute(recalculateAfter30Minute(currentReservationList.get(i).getEndDate()).getTime(), recalculateBefore30Minute(currentReservationList.get(i+1).getStartDate()).getTime())) {
                                    String label = HHMMFormatter.format(recalculateAfter30Minute(currentReservationList.get(i).getEndDate())) + " ~ "
                                            + HHMMFormatter.format(recalculateBefore30Minute(currentReservationList.get(i+1).getStartDate()));
                                    resultList.add(label);
                                }

                            } else {
                                if (check30Minute(recalculateAfter30Minute(currentReservationList.get(i).getEndDate()).getTime(), openCloseTimeList.get(0).getEndDate().getTime())) {
                                    String label = HHMMFormatter.format(recalculateAfter30Minute(currentReservationList.get(i).getEndDate())) + " ~ "
                                            + HHMMFormatter.format(openCloseTimeList.get(0).getEndDate());
                                    resultList.add(label);
                                }
                            }
                        }
                        //내일 예약일 때
                        else if (ymdFormatter.format(currentReservationList.get(i).getStartDate()).equals(tomorrow)
                                && ymdFormatter.format(currentReservationList.get(i).getEndDate()).equals(tomorrow)) {
                            if (tomorrowReservationExist > 1) {
                                if (check30Minute(recalculateAfter30Minute(currentReservationList.get(i).getEndDate()).getTime(), recalculateBefore30Minute(currentReservationList.get(i+1).getStartDate()).getTime())) {
                                    String label = HHMMFormatter.format(recalculateAfter30Minute(currentReservationList.get(i).getEndDate())) + " ~ "
                                            + HHMMFormatter.format(recalculateBefore30Minute(currentReservationList.get(i+1).getStartDate()));
                                    resultList.add(label);
                                }

                            } else {

                                if (check30Minute(openCloseTimeList.get(1).getStartDate().getTime(), recalculateBefore30Minute(currentReservationList.get(i).getStartDate()).getTime())) {
                                    String label = HHMMFormatter.format(openCloseTimeList.get(1).getStartDate()) + " ~ "
                                            + HHMMFormatter.format(recalculateBefore30Minute(currentReservationList.get(i).getStartDate()));
                                    resultList.add(label);
                                }

                                if (check30Minute(recalculateAfter30Minute(currentReservationList.get(i).getEndDate()).getTime(), recalculateBefore30Minute(currentReservationList.get(i+1).getStartDate()).getTime())) {
                                    String label = HHMMFormatter.format(recalculateAfter30Minute(currentReservationList.get(i).getEndDate())) + " ~ "
                                            + HHMMFormatter.format(recalculateBefore30Minute(currentReservationList.get(i+1).getStartDate()));
                                    resultList.add(label);
                                }
                            }
                        }
                    }

                    //마지막 예약
                    if (i == currentReservationList.size() -1) {

                        if (tomorrowReservationExist > 1) {
                            if (check30Minute(recalculateAfter30Minute(currentReservationList.get(i).getEndDate()).getTime(), openCloseTimeList.get(1).getEndDate().getTime())) {
                                String label = HHMMFormatter.format(recalculateAfter30Minute(currentReservationList.get(i).getEndDate())) + " ~ "
                                        + HHMMFormatter.format(openCloseTimeList.get(1).getEndDate());
                                resultList.add(label);
                            }

                        } else if (tomorrowReservationExist == 1) {
                            if (check30Minute(openCloseTimeList.get(1).getStartDate().getTime(), recalculateBefore30Minute(currentReservationList.get(i).getStartDate()).getTime())) {
                                String label = HHMMFormatter.format(openCloseTimeList.get(1).getStartDate()) + " ~ "
                                        + HHMMFormatter.format(recalculateBefore30Minute(currentReservationList.get(i).getStartDate()).getTime());
                                resultList.add(label);
                            }

                            if (check30Minute(recalculateAfter30Minute(currentReservationList.get(i).getEndDate()).getTime(), openCloseTimeList.get(1).getEndDate().getTime())) {
                                String label = HHMMFormatter.format(recalculateAfter30Minute(currentReservationList.get(i).getEndDate())) + " ~ "
                                        + HHMMFormatter.format(openCloseTimeList.get(1).getEndDate());
                                resultList.add(label);
                            }

                        } else {
                            if (check30Minute(recalculateAfter30Minute(currentReservationList.get(i).getEndDate()).getTime(), openCloseTimeList.get(1).getEndDate().getTime())) {
                                String label = HHMMFormatter.format(recalculateAfter30Minute(currentReservationList.get(i).getEndDate())) + " ~ "
                                        + HHMMFormatter.format(openCloseTimeList.get(1).getEndDate());
                                resultList.add(label);
                            }

                            String label = HHMMFormatter.format(openCloseTimeList.get(1).getStartDate()) + " ~ "
                                    + HHMMFormatter.format(openCloseTimeList.get(1).getEndDate());
                            resultList.add(label);
                        }
                    }
                }
            }
        }

        return resultList;
    }

    /**
     * 예약은 전후 30분 불가능하므로 시간 재계산
     * */
    //예약 시작 시간 30분 전 Date 구하기
    private Date recalculateBefore30Minute(Date originDate) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(originDate);
        calendar.add(Calendar.MINUTE, -30);

        return calendar.getTime();
    }

    //예약 종료 시간 30분 후 Date 구하기
    private Date recalculateAfter30Minute(Date originDate) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(originDate);
        calendar.add(Calendar.MINUTE, 30);

        return calendar.getTime();
    }

    //이용 가능 시간 라벨 구하기 위해 충전기 Open/Close Time List , 현재 예약 List 구하기
    private void addReservationList(String startDateString, String endDateString, String arrayType) {

        Date startDate = null;
        Date endDate = null;
        try {
            startDate = fullDateFormatter.parse(startDateString);
            endDate = fullDateFormatter.parse(endDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);

        CurrentReservationModel currentReservationModel = new CurrentReservationModel();
        currentReservationModel.setStartDate(startDate);
        currentReservationModel.setEndDate(endDate);

        if (arrayType.equals("reservation")) {
            currentReservationList.add(currentReservationModel);
        } else if (arrayType.equals("openClose")) {
            openCloseTimeList.add(currentReservationModel);
        }
    }

    //최소 충전 가능 시간 30분 체크
    private boolean check30Minute(long startTime, long endTime) {

        if (endTime - startTime < 1800000) {
            return false;
        }

        return true;
    }

    public void setLinearLayoutText(LinearLayout layoutText, Context context, String txt) {

        LinearLayout.LayoutParams viewParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

        viewParam.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        viewParam.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());

        TextView textView = new TextView(context);

        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setText(txt);
        textView.setBackground(ContextCompat.getDrawable(this, R.drawable.border_mint_30));
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(12);

        textView.setLayoutParams(viewParam);
        layoutText.addView(textView);
    }

    public boolean checkLocationServiceStatus() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void onBackPressed() {

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {

            backKeyPressedTime = System.currentTimeMillis();

            Toast.makeText(this, "'뒤로'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();

            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {

            finish();
        }
    }

    //메인화면 초기화
    public void reloadInfo() {

        //예약 상태 확인, 예약없음, 예약 있음, 충전 중
        reservationModel = apiUtils.getReservationStatus();

        addChargerInfo(Constants.currentLocationLat, Constants.currentLocationLng);
        //createDefaultMarker(binding.mapView);

        //해당페이지 이벤트 막기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        handler.postDelayed(r, 1000); // 1초 뒤에 Runnable 객체 수행

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("metis", "onResume");
        Log.e("metis", "chkRecharge : " + chkRecharge);
        try {

            //reloadInfo();

            if (!isFirst) {

                isFirst = true;
            }
            //처음 한번에는 동작할 필요 없음
            else {
                if (chkRecharge.equals("ChargerSearchActivity") || chkRecharge.equals("OwnerActivity")) {
                    isPageOpen = true;
                    Log.e("metis", "ChargerSearchActivity");
                    chkRecharge = "onResume";

                    setTime();
                    //reloadInfo();

                    chkRecharge = "";
                }

                if (reservationModel != null && !commonUtils.checkRechargeTime(reservationModel) && reservationModel.state.equals("KEEP")) {
                    // 경과시간 지나고나서 충전 종료 됐을때, 메인화면에서 충전결과 화면 표시.
                    mainRechargeModel();
                }
            }

            reloadInfo();

        } catch (Exception e) {
            Log.e("metis", "onResume Exception : " + e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (Constants.gpsService != null)
            Constants.gpsService.stopUsingGPS();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (activityList.size() > 0) {

            for (int i = 0; i < activityList.size(); i++) {

                if (activityList.get(i) == this) {

                    activityList.remove(i);
                    i--;
                }
            }
        }
    }
}
