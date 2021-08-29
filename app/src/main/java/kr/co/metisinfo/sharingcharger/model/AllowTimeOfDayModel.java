package kr.co.metisinfo.sharingcharger.model;

import lombok.Data;

@Data
public class AllowTimeOfDayModel extends ResponseModel {

    private int id;
    private String openTime;
    private String closeTime;
    private String previousOpenTime;
    private String previousCloseTime;
    private int chargerId;
    private String chargerName;
    private String created;
    private String updated;


}
