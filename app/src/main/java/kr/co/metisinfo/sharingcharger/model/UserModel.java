package kr.co.metisinfo.sharingcharger.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import kr.co.metisinfo.sharingcharger.base.DBConstants;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * UserModel
 */

@Data
@ToString
@NoArgsConstructor      // 생성자 자동생성
@Entity(tableName = DBConstants.TABLE_USER)
public class UserModel extends ApiModel{

    @PrimaryKey(autoGenerate = true)
    public int pkId;          // 필요없는값.

    public String email;

    public String username;

    public String name;

    public int phoneNumber;

    public String password;

    public String passportNumber;

    public int zipcode;

    public String address;

    public String detailAddress;

    public boolean servicePolicyFlag;

    public boolean privatePolicyFlag;

    public boolean autoLogin; // 자동 로그인
}
