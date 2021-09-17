package kr.co.metisinfo.sharingcharger.userManagement;

import android.app.Dialog;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.metisinfo.sharingcharger.Adapter.IdDialogAdapter;
import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityFindIdBinding;
import kr.co.metisinfo.sharingcharger.model.UserModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.CommonUtils;

public class FindIdActivity extends BaseActivity {

    private static final String TAG = FindIdActivity.class.getSimpleName();

    ActivityFindIdBinding binding;

    CountDownTimer timer;

    private String tempCertificateNo = "";      // 임시 인증 번호

    private boolean isCertificationBtn = false;

    ApiUtils apiUtils = new ApiUtils();

    public static Dialog idDialog;

    CommonUtils commonUtils = new CommonUtils();

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
        binding.registerBtn.setOnClickListener(view -> findId());

        binding.certificationButton.setOnClickListener(view -> {

            binding.certificationInput.requestFocus();

            if (checkVerificationCode()) {

                String phone = binding.userPhoneInput.getText().toString().trim();

                try {

                    tempCertificateNo = apiUtils.getSms(phone);

                    if (tempCertificateNo != null) {

                        if (tempCertificateNo.contains(".")) {
                            tempCertificateNo = tempCertificateNo.substring(0, tempCertificateNo.indexOf("."));
                        }

                        isCertificationBtn = true;
                        binding.remainingTimeLayout.setVisibility(View.VISIBLE);
                        countDown("0300");
                    } else {
                        Toast.makeText(FindIdActivity.this, "인증요청에 실패하였습니다. 관리자에게 문의하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
                        binding.userPhoneInput.requestFocus();
                    }

                } catch (Exception e) {
                    Toast.makeText(FindIdActivity.this, "인증요청에 실패하였습니다. 관리자에게 문의하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "registerCertificationBtn Exception: " + e);
                    binding.userPhoneInput.requestFocus();
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

            try {
                Map<String, Object> map = apiUtils.findId(binding.userNameInput.getText().toString().trim(), binding.userPhoneInput.getText().toString());

                boolean result = (boolean) map.get("result");

                if(result){
                    List<UserModel> idList = (ArrayList<UserModel>) map.get("list");

                    showIdDialog(idList);
                }else{
                    Toast.makeText(getApplicationContext(), "아이디 목록을 가져오는데 실패하였습니다.\n문제 지속시 고객센터로 문의주세요.", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Log.e("metis", "findId Exception : " + e);
            }
        }
    }

    private void showIdDialog(List<UserModel> idList) {

        idDialog = new Dialog(this);

        LayoutInflater inf = getLayoutInflater();
        View dialogView = inf.inflate(R.layout.id_dialog, null);
        TextView titleText = dialogView.findViewById(R.id.dialog_title);
        Button confirmButton = dialogView.findViewById(R.id.confirm_button);

        titleText.setText(idList.size() > 0 ? "현재 가입된 이메일입니다." : "입력하신 정보와 일치하는 아이디가 없습니다.");

        confirmButton.setOnClickListener(v -> {
            idDialog.dismiss();
        });

        idDialog.setContentView(dialogView); // Dialog에 선언했던 layout 적용
        idDialog.setCancelable(false); // 외부 터치나 백키로 dimiss 시키는 것 막음

        /*
        다음 4줄의 코드는 RecyclerView를 정의하기 위한 View, Adapter선언 코드이다.
        1. RecyclerView id 등록
        2. 수직방향으로 보여줄 예정이므로 LinearLayoutManager 등록
           2차원이면 GridLayoutManager 등 다른 Layout을 선택
        3. adapter에 topic Array 넘겨서 출력되게끔 전달
        4. adapter 적용
        */
        RecyclerView dialogRecyclerView = (RecyclerView) dialogView.findViewById(R.id.dialogRecyclerView);
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        IdDialogAdapter adapter = new IdDialogAdapter(idList);
        dialogRecyclerView.setAdapter(adapter);

        idDialog.getWindow().setBackgroundDrawableResource(R.drawable.alert_dialog_border);
        idDialog.getWindow().setLayout(commonUtils.getPercentWidth(this, 80), WindowManager.LayoutParams.WRAP_CONTENT);
        idDialog.show(); // Dialog 출력
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
