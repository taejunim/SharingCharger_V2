package kr.co.metisinfo.sharingcharger.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PurchaseModel {

    public int id;
    public String username;
    public int point;
    public String type;
    public String created;
    public int pointTargetId;
    public String targetName;

}
