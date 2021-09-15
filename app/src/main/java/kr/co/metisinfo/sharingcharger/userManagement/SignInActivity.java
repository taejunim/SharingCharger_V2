package kr.co.metisinfo.sharingcharger.userManagement;

import android.content.Intent;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.ActivityLoginBinding;
import kr.co.metisinfo.sharingcharger.model.UserModel;
import kr.co.metisinfo.sharingcharger.service.NetworkStatus;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.PreferenceUtil;
import kr.co.metisinfo.sharingcharger.view.activity.MainActivity;
import kr.co.metisinfo.sharingcharger.view.viewInterface.NetworkStatusInterface;
import retrofit2.Response;

public class SignInActivity extends BaseActivity implements NetworkStatusInterface {

    private static final String TAG = SignInActivity.class.getSimpleName();

    ActivityLoginBinding binding;

    private boolean isRegisterBtnClick = false;     // 버튼 더블클릭 막기 위한 boolean 타입 변수

    NetworkStatus networkStatus;

    ApiUtils apiUtils = new ApiUtils();

    @Override
    public void initLayout() {

        networkStatus = new NetworkStatus(this, this);

        networkStatus.registerNetworkCallback();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        changeStatusBarColor(false);
    }

    @Override
    public void initViewModel() {

    }

    @Override
    public void setOnClickListener() {

        binding.loginRegisterBtn.setOnClickListener(view -> goUserRegister());

        binding.loginFindId.setOnClickListener(view -> findId());

        binding.loginResetPw.setOnClickListener(view -> getFindPassword());

        binding.loginBtn.setOnClickListener(view -> { // 로그인 버튼 클릭 리스너

            if (isNetworkStatus) {
                if (!isRegisterBtnClick) {   // 중복 클릭 막기 위함.

                    isRegisterBtnClick = true;

                    getLogin();

                }
            } else {
                Toast.makeText(this, "네트워크 상태를 확인하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void init() {

        //Text 밑줄
        SpannableString content = new SpannableString(getString(R.string.login_find_id));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        binding.loginFindId.setText(content);

        content = new SpannableString(getString(R.string.change_password));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        binding.loginResetPw.setText(content);
    }

    private void getLogin() {

        if (validationCheck()) {

            UserModel userModel = new UserModel();

            userModel.loginId = binding.loginId.getText().toString();
            userModel.email = binding.loginId.getText().toString();
            userModel.password = binding.loginPw.getText().toString();

            try{
                Response<UserModel> response = apiUtils.login(userModel);

                //로그인 성공
                if (response.code() == 200 && response.body() != null){

                    UserModel user = response.body();

                    if(!user.getUserType().equals("General")){
                        user.email = user.getUsername();
                        user.loginId = user.getUsername();

                    } else if (user.getUserType().equals("General")) {
                        user.loginId = user.getEmail();
                    }

                    user.setPassword(userModel.password);
                    user.autoLogin = true;

                    //로그인 값 가져오기
                    PreferenceUtil preferenceUtil = new PreferenceUtil(ThisApplication.context);

                    preferenceUtil.putBoolean("isLogin", true);
                    preferenceUtil.putInt("userId", user.getId());
                    preferenceUtil.putString("name", user.getName());
                    preferenceUtil.putString("email", user.getUserType().equals("General") ? user.getEmail() : user.getUsername());
                    preferenceUtil.putString("password", userModel.getPassword());
                    preferenceUtil.putString("userType", user.getUserType());
                    preferenceUtil.putString("username", user.getUsername());

                    ThisApplication.staticUserModel = user;

                    // TODO 1. 내 계정으로 예약건이 있는지 1차 조회. ( 있을경우, 예약1건만 뿌려줌, 없을경우 Step2로 넘어감 )
                    // TODO 2. 메인 화면 들어가기전 IntroActivity 혹은 LoginActivity에서 로그인 성공시 디폴트 값으로( 적정 요금, 반경 거리, 시작시간, 종료시간) 으로 충전기 정보를 불러옴.
                    // TODO 3. 해당 내용 불러온 후 현재 Activity에서 충전기 목록을 MainActivity로 넘겨주기!!
                    // TODO 4. MainActivity에서는 넘겨 받은 값으로 지도에 마커 뿌려주기

                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);

                    startActivity(intent);
                    finish();

                }
                //로그인정보가 맞지 않을 때
                else if (response.code() == 204) {
                    isRegisterBtnClick = false; // 버튼 다시 클릭 가능하도록 false로 전환

                    Toast.makeText(SignInActivity.this, R.string.login_reject, Toast.LENGTH_SHORT).show();

                    binding.loginId.requestFocus();
                }
                //로그인 실패
                else{
                    isRegisterBtnClick = false; // 버튼 다시 클릭 가능하도록 false로 전환

                    Toast.makeText(SignInActivity.this, R.string.login_reject, Toast.LENGTH_SHORT).show();

                    binding.loginId.requestFocus();
                }

                isRegisterBtnClick = false;
            }catch (Exception e) {

                isRegisterBtnClick = false;
                Log.e("metis","getLogin Exception : "+ e);
            }

        }
    }

    private boolean validationCheck() {

        if (binding.loginId.getText().toString().trim().equals("")) {             // 사용자 이메일 입력하지 않았을 경우

            Toast.makeText(this, R.string.login_email, Toast.LENGTH_SHORT).show();
            binding.loginId.setText("");
            binding.loginId.requestFocus();
            isRegisterBtnClick = false;

            return false;

        } else if (binding.loginPw.getText().toString().equals("")) {      // 비밀번호 입력 x

            Toast.makeText(this, R.string.login_password, Toast.LENGTH_SHORT).show();

            isRegisterBtnClick = false;

            return false;
        }

        return true;
    }

    private void findId() {

        Intent intent = new Intent(this, FindIdActivity.class);

        startActivity(intent);
    }

    private void getFindPassword() {

        Intent intent = new Intent(this, PasswordResetActivity.class);

        startActivity(intent);
    }

    private void goUserRegister() {

        Intent intent = new Intent(this, SignUpActivity.class);

        startActivity(intent);

    }

    @Override
    public void networkStatus(boolean isAvailable) {

        isNetworkStatus = isAvailable;

        isNetworkStatus = true;

        if (isAvailable) {
            Log.e("metis", "로그인 네트워크를 사용할 준비가 되었을 333333");
        } else {
            Log.e("metis", "로그인 네트워크가 끊켰을 4444444");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkStatus.unregisterNetworkCallback();
    }
}
