package kr.co.metisinfo.sharingcharger.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PointModel {

    public int point;
    public String pointUsedType;
    public int userId;

    public String type;
    public String username;
    public String created;

    public int systemPoint;
    public int cashPoint;
}
