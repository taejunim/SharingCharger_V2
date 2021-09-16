package kr.co.metisinfo.sharingcharger.model;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AdminMonthlyProfitPointModel extends ResponseModel implements Serializable {

    public String day;
    public int point;

}
