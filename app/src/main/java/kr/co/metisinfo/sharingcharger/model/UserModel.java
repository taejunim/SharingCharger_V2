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
public class UserModel {

    @PrimaryKey(autoGenerate = true)
    public int pkId;          // 필요없는값.

    public boolean autoLogin; // 자동 로그인

    public String username; // username

    public String name;     // name

    public String password; // 비밀번호

    public String phonenumber; // 휴대폰번호

    public String owner;    // owner

}
