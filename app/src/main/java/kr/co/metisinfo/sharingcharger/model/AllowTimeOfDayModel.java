package kr.co.metisinfo.sharingcharger.model;

import lombok.Data;

@Data
public class AllowTimeOfDayModel {

    private int id;
    private String openTime;
    private String closeTime;
    private String created;
    private String updated;
    private String day;

}
