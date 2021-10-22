package kr.co.metisinfo.sharingcharger.model;

import lombok.Data;

/**
 * 사용자 모델
 */

@Data
public class UserModel extends ResponseModel {

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
    public boolean servicePolicyFlag = false; //서비스 이용약관
    public boolean privacyPolicyFlag = false; //개인정보 처리방침 동의여부
    public int id;          // id
    public String currentPassword; // 현재 비밀번호
    public String did;
}
