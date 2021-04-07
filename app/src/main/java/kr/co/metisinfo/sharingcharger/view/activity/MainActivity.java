package kr.co.metisinfo.sharingcharger.view.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.Constants;
import kr.co.metisinfo.sharingcharger.databinding.ActivityMainBinding;
import kr.co.metisinfo.sharingcharger.model.ChargerModel;
import kr.co.metisinfo.sharingcharger.model.ReservationModel;
import kr.co.metisinfo.sharingcharger.model.SearchKeywordModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.DateUtils;

public class MainActivity extends BaseActivity implements MapView.POIItemEventListener, MapView.MapViewEventListener {

    ActivityMainBinding binding;

    private static final String TAG = MainActivity.class.getSimpleName();

    private List<ChargerModel> personalChargeList = new ArrayList<>();  //소유주 충전기 리스트

    ApiUtils apiUtils = new ApiUtils();

    Animation translateTop;
    Animation translateBottom;

    private boolean isPageOpen = false;

    private ArrayList<MapPoint> pointList = new ArrayList<>();  //맵 마커 표시

    private int clickPOIIndex = -1;

    private ChargerModel clickChargerModel;

    private ArrayList<ChargerModel> chargerList = new ArrayList<>();    //충전기 리스트
    private ReservationModel reservationModel;

    private boolean isCurrentLocation = false;  // 현위치로 이동 여부

    private int reserveChargingMinute = 240; // 기본 4시간, 90 => 1시간 30분
    private String reserveRadius = "3 km";         // 예약 반경 거리

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

    Handler handler = new Handler();

    private String centerLocation;


    private boolean checkChange = true;

    private String searchKeyword = "";


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

                    gifImage = new GlideDrawableImageViewTarget(binding.imageLoading);
                    Glide.with(this).load(R.drawable.spinner_loading).into(gifImage);

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


                    Log.e(TAG, "chargingStartYYYY: " + chargingStartYYYY);
                    Log.e(TAG, "chargingStartMM: " + chargingStartMM);
                    Log.e(TAG, "chargingStartDD: " + chargingStartDD);
                    Log.e(TAG, "chargingStartHH: " + chargingStartHH);
                    Log.e(TAG, "chargingStartII: " + chargingStartII);
                    Log.e(TAG, "chargingEndYYYY: " + chargingEndYYYY);
                    Log.e(TAG, "chargingEndMM: " + chargingEndMM);
                    Log.e(TAG, "chargingEndDD: " + chargingEndDD);
                    Log.e(TAG, "chargingEndHH: " + chargingEndHH);
                    Log.e(TAG, "chargingEndII: " + chargingEndII);


                    String temp = data.getStringExtra("checkChange");
                    Log.e(TAG, "checkChange temp: " + temp);
                    checkChange = Boolean.valueOf(temp);

                    Log.e(TAG, "reserveChargingMinute : " + reserveChargingMinute);
                    Log.e(TAG, "checkChange : " + checkChange);
                    Log.e(TAG, "reserveRadius : " + reserveRadius);

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

        Log.e(TAG, "choiceDate : " + choiceDate);
        Log.e(TAG, "DateUtils.getWeek(choiceDate) : " + DateUtils.getWeek(choiceDate));

        binding.txtChargingTerm.setText(mm + "/" + dd + " " + DateUtils.getWeek(choiceDate) + " " + startHh + ":" + startIi + " ~ " + endHh + ":" + endIi);
    }

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        changeStatusBarColor(false);

    }

    @Override
    public void initViewModel() {

        Log.e(TAG, "lat : " + Constants.currentLocationLat + ", lng : " + Constants.currentLocationLng);
        //현재 시간 표시
        setTime();

        //메인 지도 화면, 충전기 정보 가져오기
        addChargerInfo(Constants.currentLocationLat, Constants.currentLocationLng);

        binding.mapView.setMapViewEventListener(this);
        binding.mapView.setPOIItemEventListener(this);

        //마커 표시
        createDefaultMarker(binding.mapView);
    }

    @Override
    public void setOnClickListener() {

        //예약 있을시
        binding.layoutReservationInfo.setOnClickListener(view -> {

            clickPOIIndex = 1;

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

            /*
             * 1. 소유주 or 사용자 확인
             * 2. 소유주충전기일 경우 -> 소유주 화면
             * 3. 사용자
             *  - 충전 시간 확인
             *  - 현재시간에서 예약완료 시간 구해서 실제 충전 시간 보내기
             * */

        });

        //예약취소 버튼 클릭
        binding.chgrDetailCancel.setOnClickListener(view -> {

            showReservationDialog();

        });

        //충전기 즐겨찾기 추가
        binding.imageFavorite.setOnClickListener(view -> {

        });

        //예약화면 즐겨찾기
        binding.imageFavoriteDetail.setOnClickListener(view -> {

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

        /*
         * 충전하기 클릭
         * - 수정버전 Comment(2020.12.24. 재훈.)
         * - 기존 예약하기 클릭에서 button txt를 충전하기로 변경.
         * - 즉시충전/예약하기 구분에 따라 수정.
         * - 즉시충전 -> 즉시충전 시 포인트 차감 등의 화면등을 생략하고 바로 충전기 검색 화면 이동.
         * - 예약하기 -> 기존과 동일.
         * - if else 문에서 if문 안에는 수정버전, else문 안에는 기존꺼 그대로.
         */

        //충전하기 클릭
        binding.btnReservation.setOnClickListener(view -> {

            Log.e(TAG, "충전기 연결 : ");
            /*
             * 1. 소유주의 충전기일 경우 바로 소유주화면이동
             * 2. 즉시충전/예약하기 구분
             * 3. 현재 포인트 가져오기
             * 4. 예상 포인트 가져오기
             * 5. 현재 - 예상 : 충전가능한지 확인
             * */

        });

        // 충전기 길 안내
        binding.btnNavi.setOnClickListener(view -> {

            Log.e(TAG, "클릭한 충전기명 : " + clickChargerModel.name + "\n위도 : " + clickChargerModel.gpsY + "\n경도 : " + clickChargerModel.gpsX);

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

    //지도 움직일때마다 주소 가져오기
    private void SearchAddress() {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = new ArrayList<>();

        try {
            addresses = geocoder.getFromLocation(locationLat, locationLng, 7);
        } catch (IOException ioException) {
            //네트워크 문제
            Log.e(TAG, "지오코더 서비스 사용불가");
        } catch (IllegalArgumentException illegalArgumentException) {

            Log.e(TAG, "잘못된 GPS 좌표");
        }

        Log.e(TAG, "addresses size : " + addresses.size());
        if (addresses.size() > 0) {
            Address address = addresses.get(0);
            binding.editSearch.setText(address.getAddressLine(0).replaceAll("대한민국 ", ""));
            Log.e(TAG, "주소 발견 : " + address);

        }
    }

    private void showReservationDialog() {

        reservationCancelDialog = new CustomDialog(this, getString(R.string.m_cancel_reservation));

        reservationCancelDialog.show();

        reservationCancelDialog.findViewById(R.id.dialog_no_btn).setOnClickListener(view -> {

            reservationCancelDialog.dismiss();
        });

        reservationCancelDialog.findViewById(R.id.dialog_ok_btn).setOnClickListener(view -> {

            //예약취소

        });
    }

    @Override
    public void init() {

        //가이드 수정
//        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
//
//        startActivity(intent);

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
        handler.postDelayed(r, 3000); // 1초 뒤에 Runnable 객체 수행

        //소유자일 경우 소유자 화면 이동

    }

    Runnable r = () -> {

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if (!this.isDestroyed()) {
            Glide.with(this).onDestroy();
        }

    };

    /*
     * 20.12.29. 메인 시간세팅 수정.
     * 기존에는 현재 시간이 아닌 00시 또는 30분으로 시간설정을 현재 시간으로 변경함.(기존것은 밑에 주석.)
     */
    private void setTime() {

        /*20.12.29. 현재시간으로 수정 START*/
        Calendar nowCal = Calendar.getInstance(Locale.getDefault());                                //시작시간을 위한 켈린더 선언
        Calendar addCal = Calendar.getInstance(Locale.getDefault());                                //이용시간 계산을 위한 켈린더 선언

        addCal.add(Calendar.MINUTE, reserveChargingMinute);                                         //이용시간 계산

        chargingStartYYYY = nowCal.get(Calendar.YEAR);
        chargingStartMM = nowCal.get(Calendar.MONTH) + 1;
        chargingStartDD = nowCal.get(Calendar.DAY_OF_MONTH);
        chargingStartHH = nowCal.get(Calendar.HOUR_OF_DAY);
        chargingStartII = nowCal.get(Calendar.MINUTE);

        chargingEndYYYY = addCal.get(Calendar.YEAR);
        chargingEndMM = addCal.get(Calendar.MONTH) + 1;
        chargingEndDD = addCal.get(Calendar.DAY_OF_MONTH);
        chargingEndHH = addCal.get(Calendar.HOUR_OF_DAY);
        chargingEndII = addCal.get(Calendar.MINUTE);
        /*20.12.29. 현재시간으로 수정 END*/

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

        intent.putExtra("checkChange", String.valueOf(checkChange));     //즉시, 예약

        Log.e(TAG, "checkChange : " + checkChange);

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

        mDefaultMarker.setItemName("마커 기준 조건 검색");
        mDefaultMarker.setTag(0);
        mDefaultMarker.setMapPoint(pointList.get(0));
        mDefaultMarker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        mDefaultMarker.setCustomSelectedImageResourceId(R.mipmap.current_location_48);
        mDefaultMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        mDefaultMarker.setCustomImageResourceId(R.mipmap.current_location_48);
        mDefaultMarker.setShowCalloutBalloonOnTouch(false);                     // POI 클릭시 말풍선 보여주는지 여부

        mapView.addPOIItem(mDefaultMarker);

        for (int i = 1; i < pointList.size(); i++) {

            mDefaultMarker = new MapPOIItem();

            String name = chargerList.get(i).name;

            mDefaultMarker.setItemName(name);
            mDefaultMarker.setTag(i);
            mDefaultMarker.setMapPoint(pointList.get(i));
            mDefaultMarker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            mDefaultMarker.setCustomSelectedImageResourceId(R.mipmap.black_marker_64);

            if (chargerList.get(i).currentStatusType.equals("READY")) {           // 대기 중

                mDefaultMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                mDefaultMarker.setCustomImageResourceId(R.mipmap.blue_marker_40);
            } else {
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

        if (checkLocationServiceStatus()) {

            MapPointBounds bounds = new MapPointBounds(pointList.get(0), pointList.get(0));
            binding.mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds, padding, minZoomLevel, maxZoomLevel));
        }
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

        int chargerId = -1;

        //페이지가 오픈 안될때도 있음
        //클릭 시 마다 false 초기화 해줌
        isPageOpen = false;

        try {
            if (isSearchKeywordMarkerClick) {    // 키워드 검색 후 말풍선 클릭

            } else {                            // 그 외
                Log.e(TAG, "onPOIItemSelected else");
                int tag = mapPOIItem.getTag();

                if (tag != 0) {
                    Log.e(TAG, "onPOIItemSelected tag != 0");
                    Log.e(TAG, "mapPOIItem.getTag() : " + mapPOIItem.getTag());
                    clickPOIIndex = mapPOIItem.getTag();

                    chargerId = chargerList.get(clickPOIIndex).id;

                    binding.layoutChgrInfo.setVisibility(View.VISIBLE);
                    binding.layoutChgrInfo.startAnimation(translateTop);

                    binding.layoutChargingInfo.setVisibility(View.INVISIBLE);

                    if (chargerId != -1) {
                        clickChargerModel = chargerList.get(clickPOIIndex);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "onPOIItemSelected  Exception : " + e);
        }
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
        mapView.getPOIItems()[0].setMapPoint(mapPoint);

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) { // 마커 밖에 지도 클릭 했을 경우.

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
        mapView.getPOIItems()[0].setMapPoint(mapPoint);
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

            Log.e(TAG, "locationLat : " + locationLat);
            Log.e(TAG, "locationLng : " + locationLng);

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

        for (int i = 1; i < mapPOIItems.length; i++) {

            binding.mapView.removePOIItem(mapPOIItems[i]);

        }

        createSearchKeywordMarker(binding.mapView, model);

        Log.e(TAG, "mapView  2 : " + model.x + " , " + model.y);

        //키워드 검색 시 지도중심일 경우 중심좌표는 그대로 나둠
        if (centerLocation == null || !centerLocation.equals("지도")) {
            double latPoint = Double.parseDouble(model.y);
            double lngPoint = Double.parseDouble(model.x);

            int padding = 5;
            float minZoomLevel = 3;
            float maxZoomLevel = 10;
            MapPointBounds bounds = new MapPointBounds(MapPoint.mapPointWithGeoCoord(latPoint, lngPoint), MapPoint.mapPointWithGeoCoord(latPoint, lngPoint));
            binding.mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds, padding, minZoomLevel, maxZoomLevel));
        }
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

        gifImage = new GlideDrawableImageViewTarget(binding.imageLoading);
        Glide.with(this).load(R.drawable.spinner_loading).into(gifImage);

        SearchAddress();

        ChargerModel chargerModel = new ChargerModel();

        chargerModel.id = -1;
        chargerModel.address = "현재위치";
        chargerModel.gpsY = lat;
        chargerModel.gpsX = lng;
        chargerModel.name = "현재위치";

        chargerList.add(chargerModel);

        //dummy start
        List<Map<String, String>> test = new ArrayList<>();
        Map<String, String> testM = new HashMap<>();
        testM.put("gpsX","126.5676455");
        testM.put("gpsY","33.4524403");

        test.add(testM);

        testM = new HashMap<>();
        testM.put("gpsX","126.5620128");
        testM.put("gpsY","33.4515312");
        test.add(testM);

        for(int j = 0; j < test.size(); j++){
            ChargerModel c = new ChargerModel();

            c.id = j+1;
            c.address = "test"+j;
            c.gpsY = Double.parseDouble(test.get(j).get("gpsY"));
            c.gpsX = Double.parseDouble(test.get(j).get("gpsX"));
            c.currentStatusType = "READY";
            c.name = "test"+j;

            chargerList.add(c);
        }
        //dummy end

        for (int i = 0; i < chargerList.size(); i++) {

            pointList.add(MapPoint.mapPointWithGeoCoord(chargerList.get(i).gpsY, chargerList.get(i).gpsX));
        }
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

        addChargerInfo(Constants.currentLocationLat, Constants.currentLocationLng);
        createDefaultMarker(binding.mapView);

        //해당페이지 이벤트 막기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        handler.postDelayed(r, 2000); // 1초 뒤에 Runnable 객체 수행

    }


    @Override
    protected void onResume() {
        super.onResume();

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
