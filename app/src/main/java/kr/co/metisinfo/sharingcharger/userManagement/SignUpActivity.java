package kr.co.metisinfo.sharingcharger.userManagement;

import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import java.util.regex.Pattern;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityUserRegisterBinding;
import kr.co.metisinfo.sharingcharger.model.UserModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.CommonUtils;
import retrofit2.Response;

public class SignUpActivity extends BaseActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    ActivityUserRegisterBinding binding;

    private String tempCertificateNo = "1234";      // 임시 인증 번호

    private boolean isRegisterBtnClick = false;     // 버튼 더블클릭 막기 위한 boolean 타입 변수

    private boolean isCertificationBtn = false;

    private boolean checkPersonalInfo1 = false;

    private boolean checkPersonalInfo2 = false;

    CountDownTimer timer;

    String pwPattern = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{5,15}.$";

    ApiUtils apiUtils = new ApiUtils();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            String getTagName = data.getStringExtra("getTagName");

            if (getTagName.equals("PersonalInfo1")) {

                checkPersonalInfo1 = true;
                binding.registerPersonalInfo1Txt.setText("동의");
            } else {

                checkPersonalInfo2 = true;
                binding.registerPersona2Info1Txt.setText("동의");
            }

        }

    }

    @Override
    public void initLayout() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_register);

        changeStatusBarColor(false);

    }

    @Override
    public void initViewModel() {

    }

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnBack.setOnClickListener(view -> finish());

        binding.registerCertificationBtn.setOnClickListener(view -> {

            //인증번호 가져오기

            if (checkVerificationCode()) {

                String phone = binding.registerPhoneInput.getText().toString().trim();
                Log.e(TAG, "phone : " + phone);


            }

        });

        binding.registerPersonalInfo1Btn.setOnClickListener(view -> {
            Log.e(TAG, "btn1");
            //개인정보

        });

        binding.registerPersonalInfo2Btn.setOnClickListener(view -> {

            //개인정보 처리방침

        });

        binding.registerBtn.setOnClickListener(view -> { // 회원가입 버튼 클릭 리스너

            if (!isRegisterBtnClick) {   // 중복 클릭 막기 위함.

                isRegisterBtnClick = true;


                //임시
                isCertificationBtn = true;
                checkPersonalInfo1 = true;
                checkPersonalInfo2 = true;

                userJoin();
            }
        });

    }

    @Override
    public void init() {

        binding.includeHeader.txtTitle.setText(R.string.register_user_title);
        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);
        binding.registerPhoneTitle.setText("이메일");

    }

    private boolean checkVerificationCode() {

        if (binding.registerPhoneInput.getText().toString().trim().equals("")) {     // 전화번호 입력하지 않았을 경우

            binding.registerPhoneInput.setText("");
            binding.registerPhoneInput.requestFocus();
            Toast.makeText(this, "전화번호를 입력하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

            return false;
        } else if (binding.registerPhoneInput.getText().length() < 10) {

            binding.registerPhoneInput.requestFocus();
            Toast.makeText(this, "입력한 전화번호 자리수가 불충분 합니다.", Toast.LENGTH_LONG).show();

            return false;
        }

        return true;
    }

    private void userJoin() {

     //   if (validationCheck()) {

            UserModel userModel = new UserModel();

            userModel.name = binding.registerNameInput.getText().toString();
            userModel.username = binding.registerEmailInput.getText().toString();
            userModel.password = binding.registerPwInput.getText().toString();
            userModel.phonenumber = binding.registerPhoneInput.getText().toString();

            /*userModel.name = "aa";
            userModel.username = "aaa";
            userModel.password = "aaa";
            userModel.phonenumber = "00000000000";
            userModel.owner = "Gong Yoo";*/

            try{
                //Response<Object> response = apiUtils.signUp(userModel);
                Log.e(TAG,"=====response=====");
                //Log.e(TAG, response.toString());

            }catch (Exception e){
                Log.e(TAG,"userJoin Exception : "+ e);
            }

            finish();


//        } else {
//
//            isRegisterBtnClick = false;
//        }

    }

    /**
     * 회원가입 인풋박스 벨리데이션 체크
     */
    private boolean validationCheck() {

        if (binding.registerNameInput.getText().toString().trim().equals("")) {             // 사용자 이름 입력하지 않았을 경우

            binding.registerNameInput.setText("");
            binding.registerNameInput.requestFocus();
            Toast.makeText(this, "이름을 입력하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

            return false;
        } else if (binding.registerEmailInput.getText().toString().trim().equals("")) {     // 이메일 입력하지 않았을 경우

            binding.registerEmailInput.setText("");
            binding.registerEmailInput.requestFocus();
            Toast.makeText(this, "이메일을 입력하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

            return false;
        } else if (!CommonUtils.isValidEmail(binding.registerEmailInput.getText().toString())) {
            binding.registerEmailInput.requestFocus();
            Toast.makeText(this, "이메일 형식으로 입력하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
            return false;
        } else if (binding.registerPhoneInput.getText().toString().equals("")) {      // 전화번호 입력 x

            binding.registerPhoneInput.requestFocus();
            Toast.makeText(this, "전화번호를 입력하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

            return false;
        } else if (binding.registerPhoneInput.getText().toString().length() < 10) { // 전화번호 자리수 부족

            binding.registerPhoneInput.requestFocus();
            Toast.makeText(this, "입력한 전화번호 자리수가 불충분 합니다.", Toast.LENGTH_LONG).show();

            return false;
        } else if (!isCertificationBtn) {

            Toast.makeText(this, "인증요청을 해주세요.", Toast.LENGTH_LONG).show();
            return false;
        }
        //인증번호 일치하는지 확인
        else if (binding.registerCertificationInput.getText().toString().equals("")) {

            binding.registerCertificationInput.requestFocus();
            Toast.makeText(this, "인증번호를 입력하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

            return false;
        } else if (!tempCertificateNo.equals(binding.registerCertificationInput.getText().toString())) {

            binding.registerCertificationInput.requestFocus();
            Toast.makeText(this, "인증번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();

            return false;
        } else if (binding.registerPwInput.getText().toString().equals("")) {        // 비번 입력 하지 않았을 경우

            binding.registerPwInput.requestFocus();
            Toast.makeText(this, "비밀번호를 입력하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

            return false;
        } else if (!Pattern.matches(pwPattern, binding.registerPwInput.getText().toString())) {    // 비번 입력 하지 않았을 경우

            binding.registerPwInput.requestFocus();
            Toast.makeText(this, "비밀번호는 영문, 숫자, 특수 문자 포함하여 최소 6자 이상 16자리 이하로 설정하셔야합니다.", Toast.LENGTH_LONG).show();

            return false;
        } else if (binding.registerConfirmPwInput.getText().toString().equals("")) { // 비번 확인 입력 하지 않았을 경우

            binding.registerConfirmPwInput.requestFocus();
            Toast.makeText(this, "비밀번호 확인을 입력하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

            return false;
        } else if (!binding.registerPwInput.getText().toString().equals(binding.registerConfirmPwInput.getText().toString())) {  // 비번, 비번 확인이 다른 경우

            binding.registerConfirmPwInput.requestFocus();
            Toast.makeText(this, "입력한 두 비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();

            return false;
        }
        //개인정보 동의여부
        if (!checkPersonalInfo1 && !checkPersonalInfo2 || checkPersonalInfo1 && !checkPersonalInfo2 || !checkPersonalInfo1 && checkPersonalInfo2) {

            Toast.makeText(this, "개인정보 내용을 확인해 주세요.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    /**
     * 카운트 다운 타이머
     *
     * @param time hhii
     */
    public void countDown(String time) {

        long conversionTime;

        // 1000 단위가 1초
        // 60000 단위가 1분
        // 60000 * 3600 = 1시간

        String getMin = time.substring(0, 2);
        String getSecond = time.substring(2, 4);

        // "00"이 아니고, 첫번째 자리가 0 이면 제거
        if (getMin.substring(0, 1).equals("0")) {
            getMin = getMin.substring(1, 2);
        }

        if (getSecond.substring(0, 1).equals("0")) {
            getSecond = getSecond.substring(1, 2);
        }

        // 변환시간
        conversionTime = Long.valueOf(getMin) * 60 * 1000 + Long.valueOf(getSecond) * 1000;

        // 첫번째 인자 : 원하는 시간 (예를들어 30초면 30 x 1000(주기))
        // 두번째 인자 : 주기( 1000 = 1초)

        if (timer != null) {

            timer.cancel();
        }

        timer = new CountDownTimer(conversionTime, 1000) {

            // 특정 시간마다 뷰 변경
            public void onTick(long millisUntilFinished) {

                // 분단위
                long getMin = millisUntilFinished - (millisUntilFinished / (60 * 60 * 1000));
                String min = String.valueOf(getMin / (60 * 1000)); // 몫

                // 초단위
                String second = String.valueOf((getMin % (60 * 1000)) / 1000); // 나머지

                // 분이 한자리면 0을 붙인다
                if (min.length() == 1) {
                    min = "0" + min;
                }

                // 초가 한자리면 0을 붙인다
                if (second.length() == 1) {
                    second = "0" + second;
                }

                binding.txtCountDown.setText(min + ":" + second);
            }

            // 제한시간 종료시
            public void onFinish() {

                // 변경 후
                binding.txtCountDown.setText("재인증 요청");
                isCertificationBtn = false;
                // TODO : 타이머가 모두 종료될때 어떤 이벤트를 진행할지 처리

            }
        }.start();
    }
}
