package kr.co.metisinfo.sharingcharger.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PurchaseModel {

    public int id;
    public int userId;
    public String username;
    public int paidAmount;
    public int cancelAmount;
    public int approvalNumber;
    public String approvalDate;
    public int userPoint;
    public String paymentType;
    public String paymentSuccessType;

}
