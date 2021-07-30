package kr.co.metisinfo.sharingcharger.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import kr.co.metisinfo.sharingcharger.base.DBConstants;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 사용자 모델
 */

@Data
@ToString
@NoArgsConstructor      // 생성자 자동생성
@Entity(tableName = DBConstants.TABLE_USER)
public class UserModel {

    @PrimaryKey(autoGenerate = true)
    public int pkId;          // 필요없는값.

    public String name;     // 이름

    public String email;    // 이메일

    public String password; // 비밀번호

    public String phone;    // 전화번호

    public int point;       // 포인트

    public boolean autoLogin; //자동 로그인

    public String created;  // 생성 일시

    public String loginId;

    public String userType; //유저 타입

    public String username; //소유주 id

    //    public boolean collectUserDataFlag = false; //서비스 이용약관
    public boolean servicePolicyFlag = false; //서비스 이용약관

    public boolean privacyPolicyFlag = false; //개인정보 처리방침 동의여부

    public int id;          // id

    public String currentPassword; // 현재 비밀번호

}
