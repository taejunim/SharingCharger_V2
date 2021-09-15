package kr.co.metisinfo.sharingcharger.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RechargeModel {

    public int      chargerId;                                                                      //충전기 ID
    public String   chargerName;                                                                    //충전기 명
    public String   created;
    public String   endRechargeDate;                                                                //충전 종료 시간
    public int      id;
    public int      rechargePoint;                                                                  //실제 충전 포인트
    public String   reservationEndDate;
    public int      reservationPoint;                                                               //선차감 포인트
    public String   reservationStartDate;
    public String   startRechargeDate;                                                              //충전 시작 시간
    public String   updated;
    public String   username;
    public double   rechargeKwh;                                                                    //충전량
    public int      rechargeMinute;                                                                 //충전 시간
    public String   chargingTime;                                                                   //경과 시간
    public int      refundPoint;                                                                  //예상 환불 포인트
}
