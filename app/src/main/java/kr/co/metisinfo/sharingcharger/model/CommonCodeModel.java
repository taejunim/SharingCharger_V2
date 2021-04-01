package kr.co.metisinfo.sharingcharger.model;

import androidx.room.Entity;

import kr.co.metisinfo.sharingcharger.base.DBConstants;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor      // 생성자 자동생성
@Entity(tableName = DBConstants.TABLE_COMMON_CODE, primaryKeys = {"comGrpCd", "comCd"})
public class CommonCodeModel {

    public String comGrpCd;     // 공통 그룹 코드
    public String comCd;        // 공통 코드
    public String comCdNm;      // 공통 코드 명
    public String remark;       // 비고
}
