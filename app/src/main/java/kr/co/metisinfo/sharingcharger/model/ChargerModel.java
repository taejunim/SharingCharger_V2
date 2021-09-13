package kr.co.metisinfo.sharingcharger.model;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ChargerModel {

    public String addrCode;        // 주소 코드
    public String chargerStateCode;// 충전기 상태 코드


    // 진우 DTO
    public String acceptType;
    public String address;
    public String detailAddress;
    public String bleNumber;
    public String created;
    public String currentStatusType;
    public String description;
    public String zipcode;
    public double gpsX;  // 경도
    public double gpsY;  // 위도
    public int id;       // 장치 id?
    public String name;  // 충전기 이름?
    public String ownerName;
    public String ownerType;
    public String parkingFeeDescription;
    public boolean parkingFeeFlag;
    public String rangeOfFee;
    public String updated;

}
