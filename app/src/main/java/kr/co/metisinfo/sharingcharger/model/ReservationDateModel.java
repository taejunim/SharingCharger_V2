package kr.co.metisinfo.sharingcharger.model;

import lombok.Data;

/**
 * @ Class Name   : ReservationDateModel.java
 * @ Modification : ReservationDateModel CLASS.
 * @
 * @ 최초 생성일      최초 생성자
 * @ ---------     --------
 * @ 2021.08.12.    임태준
 * @
 * @  수정일          수정자
 * @ ---------    ---------
 * @
 **/
@Data
public class ReservationDateModel {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int reserveChargingMinute;

    private int endYear;
    private int endMonth;
    private int endDay;
    private int endHour;
    private int endMinute;
}
