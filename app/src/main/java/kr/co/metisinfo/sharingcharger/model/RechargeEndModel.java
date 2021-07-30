package kr.co.metisinfo.sharingcharger.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RechargeEndModel {

    public int rechargeId;
    public int rechargeMinute;
    public int rechargePoint = 0;
    public double rechargeKwh;

}
