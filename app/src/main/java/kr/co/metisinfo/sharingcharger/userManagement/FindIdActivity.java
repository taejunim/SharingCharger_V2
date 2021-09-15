package kr.co.metisinfo.sharingcharger.userManagement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.charger.ChargerSearchActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityFindIdBinding;
import kr.co.metisinfo.sharingcharger.databinding.ActivityUserRegisterBinding;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.view.activity.BLEChargingActivity;

public class FindIdActivity extends BaseActivity {

    private static final String TAG = FindIdActivity.class.getSimpleName();

    ActivityFindIdBinding binding;

    CountDownTimer timer;

    private String tempCertificateNo = "";      // 임시 인증 번호

    private boolean isCertificationBtn = false;

    ApiUtils apiUtils = new ApiUtils();

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_id);

        changeStatusBarColor(false);

    }

    @Override
    public void initViewModel() {

    }

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnBack.setOnClickListener(view -> finish());
        //binding.registerBtn.setOnClickListener(view -> passwordResetConfirm(this));
        binding.registerBtn.setOnClickListener(view -> findId());

        binding.certificationButton.setOnClickListener(view ->
        {

            if (checkVerificationCode()) {

                String phone = binding.userPhoneInput.getText().toString().trim();
                Log.e("metis", "phone : " + phone);

                try {

                    tempCertificateNo = apiUtils.getSms(phone);

                    if (tempCertificateNo != null) {
                        Log.e(TAG, "response : " + tempCertificateNo);

                        if (tempCertificateNo.contains(".")) {
                            tempCertificateNo = tempCertificateNo.substring(0, tempCertificateNo.indexOf("."));
                        }

                        Log.e(TAG, "tempCertificateNo : " + tempCertificateNo);

                        isCertificationBtn = true;
                        binding.remainingTimeLayout.setVisibility(View.VISIBLE);
                        countDown("0300");
                    } else {
                        Toast.makeText(FindIdActivity.this, "인증요청에 실패하였습니다. 관리자에게 문의하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(FindIdActivity.this, "인증요청에 실패하였습니다. 관리자에게 문의하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "registerCertificationBtn Exception: " + e);
                }

            }
        });
    }

    @Override
    public void init() {

        binding.includeHeader.txtTitle.setText(R.string.login_find_id);
        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);
    }

    private boolean validationCheck() {

        if (binding.userNameInput.getText().toString().trim().equals("")) {             // 사용자 이름 입력하지 않았을 경우

            binding.userNameInput.setText("");
            binding.userNameInput.requestFocus();
            Toast.makeText(this, "이름을 입력하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

            return false;
        } else if (binding.userPhoneInput.getText().toString().equals("")) {      // 전화번호 입력 x

            binding.userPhoneInput.requestFocus();
            Toast.makeText(this, "전화번호를 입력하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

            return false;
        } else if (binding.userPhoneInput.getText().toString().length() < 10) { // 전화번호 자리수 부족

            binding.userPhoneInput.requestFocus();
            Toast.makeText(this, "입력한 전화번호 자리수가 불충분 합니다.", Toast.LENGTH_LONG).show();

            return false;
        }
        //인증번호 일치하는지 확인
        else if (binding.certificationInput.getText().toString().equals("")) {

            binding.certificationInput.requestFocus();
            Toast.makeText(this, "인증번호를 입력하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

            return false;
        } else if (!tempCertificateNo.equals(binding.certificationInput.getText().toString())) {

            binding.certificationInput.requestFocus();
            Toast.makeText(this, "인증번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();

            return false;
        } else if (!isCertificationBtn) {

            Toast.makeText(this, "인증요청을 해주세요.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void findId() {
        if (validationCheck()) {
            showAlertDialog("아래의 이메일로 가입되어 있습니다.", "teerjwi21@naver.com");
        }
    }

    public void showAlertDialog(String title, String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);

        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("확인", (dialog, which) ->{

            dialog.dismiss();
        });

        AlertDialog alertDialog = builder.show();
        TextView messageText = (TextView) alertDialog.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
        alertDialog.show();
    }

    private boolean checkVerificationCode() {

        if (binding.userPhoneInput.getText().toString().trim().equals("")) {     // 전화번호 입력하지 않았을 경우

            binding.userPhoneInput.setText("");
            binding.userPhoneInput.requestFocus();
            Toast.makeText(this, "전화번호를 입력하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

            return false;
        } else if (binding.userPhoneInput.getText().length() < 10) {

            binding.userPhoneInput.requestFocus();
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

                binding.remainingTime.setText(min + ":" + second);
            }

            // 제한시간 종료시
            public void onFinish() {

                // 변경 후
                binding.remainingTime.setText("재인증 요청");
                tempCertificateNo = "";
                isCertificationBtn = false;

                // TODO : 타이머가 모두 종료될때 어떤 이벤트를 진행할지 처리

            }
        }.start();
    }
}
