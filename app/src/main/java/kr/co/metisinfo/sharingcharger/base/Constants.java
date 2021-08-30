package kr.co.metisinfo.sharingcharger.base;

import kr.co.metisinfo.sharingcharger.service.GpsService;

public class Constants {

    public static final boolean isReal = false;

    //검색 api 사용시 REST API 키 사용해야함
    public static final String KAKAO_SERCH_KEYWORD_KEY = "4332dce3f2f8d3ee87e31884c5c5523d";

    /* 테스트 서버 */
    public static final String DEV_HOST = "http://211.253.37.97:52340";
    public static final String REAL_HOST = "http://211.253.37.97:52340";
    public static final String DAILY_DEV_HOST = "http://211.253.37.97:52340";
    public static final String DAILY_REAL_HOST = "http://211.253.37.97:52340";

    public static final String KAKAO_SEARCH_KEYWORD_HOST = "https://dapi.kakao.com";

    //서버 IP
    public static final String HOST = isReal ? REAL_HOST : DEV_HOST;

    //포트 번호
    public static final String DAILY_HOST = isReal ? DAILY_REAL_HOST : DAILY_DEV_HOST;

    public static final String HEADER_AUTH_KAKAO_SEARCH_KEYWORD = "Authorization";

    public static final String HEADER_KEY_AUTH_TOKEN = "f3535923-801c-47db-9116-a958004b41a6";
    public static final String HEADER_KEY_USER_AGENT_FOR_WEB = "User-Agent";
    public static final String HEADER_KEY_USER_AGENT = "userAgent";
    public static final String HEADER_KEY_LOGIN_ID = "loginId";
    public static final String HEADER_KEY_LOGIN_TYPE_CD = "loginTypeCd";
    public static final String HEADER_KEY_DEVICE_UUID = "deviceUuid";
    public static final String HEADER_KEY_CONTENT_TYPE = "Content-Type";

    public static final String HEADER_USER_AGENT = "DEVICE-AGENT";
    public static final String HEADER_CONTENT_TYPE = "application/json";
    public static final String HEADER_LOGIN_TYPE_CD = "DEVICE";

    public static final String HEADER_KEY_AUTH_KAKAO_SEARCH_KEYWORD = "KakaoAK " + KAKAO_SERCH_KEYWORD_KEY;

    public static final int PAGE_RESERVE = 10001;               // 예약 팝업
    public static final int PAGE_SEARCH_CONDITION = 10003;      // 충전기 조건 입력
    public static final int PAGE_CHARGE_HISTORY = 10004;        // 충전기 이력 조건
    public static final int PAGE_POINT_HISTORY = 10005;         // 포인트 이력 조건
    public static final int PAGE_SEARCH_KEYWORD = 10006;        // 주소 검색
    public static final int PAGE_SEARCH_CHARGER = 10007;        // 충전기 검색

    public static final int PAGE_SETTING_GPS = 10008;          // gps 설정
    public static final int PAGE_PERSONAL_INFORMATION = 10009;          // 개인정보 확인
    public static final int PAGE_POINT_CHARGE = 10010;       //포인트 충전
    public static final int PAGE_SETTING = 10011;        // 설정 확인

    public static GpsService gpsService;
    public static double currentLocationLng;
    public static double currentLocationLat;

    public final static int CHANGE_USER_TYPE = 0x01;
    public final static int CHANGE_PRICE = 0x02;
    public final static int CHANGE_TIME = 0x03;
    public final static int CHANGE_INFORMATION = 0x04;
}
