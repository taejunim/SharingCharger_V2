package kr.co.metisinfo.sharingcharger.model;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SearchKeywordModel implements Serializable {

    private static final long serialVersionUID = 1L;

    public String addressName;
    public String categoryGroupCode;
    public String categoryGroupName;
    public String categoryName;
    public String distance;
    public String phone;
    public String placeName;
    public String placeUrl;
    public String roadAddressName;
    public String x;
    public String y;

}
