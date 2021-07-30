package kr.co.metisinfo.sharingcharger.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AuthenticateModel {

    public String rechargeStartDate;
    public int reservationId;
    public int userId;

}
