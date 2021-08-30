package kr.co.metisinfo.sharingcharger.model;


import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
public class AdminChargerModel extends ResponseModel implements Serializable {
    public String address;
    public String bleNumber;
    public boolean cableFlag;
    public String created;
    public String currentStatusType;
    public String description;
    public String detailAddress;
    public double gpsX;  // 경도
    public double gpsY;  // 위도
    public int id;       // 장치 id?
    public String name;  // 충전기 이름?
    public String ownerName;
    public String ownerType;
    public String parkingFeeDescription;
    public boolean parkingFeeFlag;
    public int providerCompanyId;
    public String providerCompanyName;
    public String rangeOfFee;
    public String sharedType;
    public String updated;
}
