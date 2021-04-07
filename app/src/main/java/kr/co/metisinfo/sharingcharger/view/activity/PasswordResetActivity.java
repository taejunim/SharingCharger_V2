package kr.co.metisinfo.sharingcharger.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.databinding.DataBindingUtil;
import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityUserRegisterBinding;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;

public class PasswordResetActivity extends BaseActivity {

    private static final String TAG = PasswordResetActivity.class.getSimpleName();

    ActivityUserRegisterBinding binding;

    CountDownTimer timer;

    private String tempCertificateNo = "1234";      // 임시 인증 번호

    private boolean isCertificationBtn = false;

    ApiUtils apiUtils = new ApiUtils();

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
        binding.registerBtn.setOnClickListener(view -> passwordResetConfirm(this));

        binding.registerCertificationBtn.setOnClickListener(view ->
        {

            if (checkVerificationCode()) {

                String phone = binding.registerPhoneInput.getText().toString().trim();
                Log.e(TAG, "phone : " + phone);

                //인증번호 api

            }
        });
    }

    @Override
    public void init() {

        binding.includeHeader.txtTitle.setText(R.string.change_password);
        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);

        binding.registerPersonalInfoLayout.setVisibility(View.INVISIBLE);
        binding.registerBtn.setText(R.string.change_password_btn);
        binding.registerPwTitle.setVisibility(View.INVISIBLE);
        binding.registerPwTitleStar.setVisibility(View.INVISIBLE);
        binding.registerPwInput.setVisibility(View.INVISIBLE);
        binding.registerConfirmPwTitle.setVisibility(View.INVISIBLE);
        binding.registerConfirmPwTitleStar.setVisibility(View.INVISIBLE);
        binding.registerConfirmPwInput.setVisibility(View.INVISIBLE);

    }

    private boolean validationCheck() {

        if (binding.registerNameInput.getText().toString().trim().equals("")) {             // 사용자 이름 입력하지 않았을 경우

            binding.registerNameInput.setText("");
            binding.registerNameInput.requestFocus();
            Toast.makeText(this, "이름을 입력하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

            return false;
        } else if (binding.registerPhoneInput.getText().toString().equals("")) {      // 전화번호 입력 x

            binding.registerPhoneInput.requestFocus();
            Toast.makeText(this, "전화번호를 입력하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

            return false;
        } else if (binding.registerPhoneInput.getText().toString().length() < 10) { // 전화번호 자리수 부족

            binding.registerPhoneInput.requestFocus();
            Toast.makeText(this, "입력한 전화번호 자리수가 불충분 합니다.", Toast.LENGTH_LONG).show();

            return false;
        } else if (binding.registerEmailInput.getText().toString().trim().equals("")) {     // 이메일 입력하지 않았을 경우

            binding.registerEmailInput.setText("");
            binding.registerEmailInput.requestFocus();
            Toast.makeText(this, "아이디 또는 이메일을 입력하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

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
        } else if (!isCertificationBtn) {

            Toast.makeText(this, "인증요청을 해주세요.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    private void passwordResetConfirm(Context context) {

        if (validationCheck()) {

            Intent intent = new Intent(this, PasswordChangeActivity.class);
            intent.putExtra("activityName", this.getLocalClassName());

            intent.putExtra("userEmail", binding.registerEmailInput.getText().toString());

            startActivity(intent);
            finish();
        }

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

                // TODO : 타이머가 모두 종료될때 어떤 이벤트를 진행할지 처리

            }
        }.start();
    }
}
