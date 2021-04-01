package kr.co.metisinfo.sharingcharger.model;

import java.io.Serializable;

import lombok.Data;

/**
 * 사용가능 충전기 검색 관련 Model
 */

@Data
public class MarkerSearchConditionModel implements Serializable {


    private static final long serialVersionUID = 1L;

    private long searchLat;   // 위도
    private long searchLng;   // 경도
    private int searchRadiusRange = -1;      // 반경 범위 ( 3 : 3km, 5 : 5km ... )
    private int searchPriceRange = -1;       // 금액 범위 ( 100 : 100원대, 200 : 200원대 ... )

    private String searchFullDateTime = ""; // 충전 시간 풀 ( 7/20 (금) 22:30 ~ 23:00 )
    private int chargingTime = -1;          // 충전 시간 ( 30 : 30분, 90 : 1시간 30분 ...)

    private String searchStartDateTime = ""; // 충전 시작 일시
    private String searchEndDateTime = "";   // 충전 종료 일시

}
