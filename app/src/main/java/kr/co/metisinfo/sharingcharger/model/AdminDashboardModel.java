package kr.co.metisinfo.sharingcharger.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AdminDashboardModel extends ResponseModel {

    private int userId;
    private int ownChargerCount;
    private int currentChargerCount;
    private int monthlyChargerErrorCount;
    private int monthlyReserveCount;
    private int monthlyRechargeCount;
    private int monthlyCumulativePoint;
    private int currentPoint;
    private double monthlyRechargeKwh;
}
