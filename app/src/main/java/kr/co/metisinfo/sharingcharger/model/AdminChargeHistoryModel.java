package kr.co.metisinfo.sharingcharger.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AdminChargeHistoryModel {

    public int      id;
    public int      chargerId;                                                                      //충전기 ID
    public int      reservationId;
    public String   chargerName;                                                                    //충전기 명
    public String   username;
    public String   reservationStartDate;
    public String   reservationEndDate;
    public String   startRechargeDate;                                                              //충전 시작 시간
    public String   endRechargeDate;                                                                //충전 종료 시간
    public int      reservationPoint;                                                               //선차감 포인트
    public int      rechargePoint;                                                                  //실제 충전 포인트
    public int      refundPoint;                                                                  //예상 환불 포인트
    public int      ownerPoint;                                                                  //예상 환불 포인트
    public String   created;
    public String   updated;
}
