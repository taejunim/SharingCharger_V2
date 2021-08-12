package kr.co.metisinfo.sharingcharger.model;

import java.util.Date;

import lombok.Data;

/**
 * @ Class Name   : CurrentReservationModel.java
 * @ Modification : CurrentReservationModel CLASS.
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
public class CurrentReservationModel {

    private Date startDate;
    private Date endDate;
}
