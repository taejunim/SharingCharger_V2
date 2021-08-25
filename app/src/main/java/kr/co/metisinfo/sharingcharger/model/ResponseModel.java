package kr.co.metisinfo.sharingcharger.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ResponseModel {

    private int responseCode;
    private String message;
}
