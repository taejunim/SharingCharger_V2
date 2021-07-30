package kr.co.metisinfo.sharingcharger.model;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ReservationModel implements Serializable {

    public String bleNumber;
    public String cancelDate;
    public int chargerId;
    public String chargerName;
    public String created;
    public String endDate;
    public int expectPoint;
    public int id;
    public String startDate;
    public String state;
    public String updated;
    public int userId;
    public String username;

    public double gpsX;
    public double gpsY;

    public String reservationType;

    public String chargerZipcode;
    public String chargerAddress;
    public String chargerDetailAddress;
    public String rangeOfFee;


}
