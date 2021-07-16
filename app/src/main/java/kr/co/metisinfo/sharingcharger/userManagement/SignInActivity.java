package kr.co.metisinfo.sharingcharger.userManagement;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.ActivityLoginBinding;
import kr.co.metisinfo.sharingcharger.model.UserModel;
import kr.co.metisinfo.sharingcharger.service.NetworkStatus;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.view.activity.MainActivity;
import kr.co.metisinfo.sharingcharger.view.viewInterface.NetworkStatusInterface;
import kr.co.metisinfo.sharingcharger.viewModel.UserViewModel;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends BaseActivity implements NetworkStatusInterface {

    private static final String TAG = SignInActivity.class.getSimpleName();

    ActivityLoginBinding binding;


    private boolean isRegisterBtnClick = false;     // 버튼 더블클릭 막기 위한 boolean 타입 변수

    private UserViewModel userViewModel;

    NetworkStatus networkStatus;

    ApiUtils apiUtils = new ApiUtils();

    @Override
    public void initLayout() {

        networkStatus = new NetworkStatus(this, this);

        networkStatus.registerNetworkCallback();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        changeStatusBarColor(false);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

    }

    @Override
    public void initViewModel() {

    }

    @Override
    public void setOnClickListener() {

        binding.loginRegisterBtn.setOnClickListener(view -> goUserRegister());

        binding.loginResetPw.setOnClickListener(view -> getFindPassword());

        binding.loginBtn.setOnClickListener(view -> { // 로그인 버튼 클릭 리스너

            if (isNetworkStatus) {
                if (!isRegisterBtnClick) {   // 중복 클릭 막기 위함.

                    isRegisterBtnClick = true;

                    try {
                        getLogin();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } else {
                Toast.makeText(this, "네트워크 상태를 확인하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void init() {

        //Text 밑줄
        SpannableString content = new SpannableString(getString(R.string.change_password));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        binding.loginResetPw.setText(content);
    }


    private void getLogin() throws Exception {

        if (validationCheck()) {

            UserModel userModel = new UserModel();

            userModel.email = binding.loginId.getText().toString();
            userModel.password = binding.loginPw.getText().toString();

            RequestBody email = RequestBody.create(MediaType.parse("text/plain"), userModel.email);
            RequestBody password = RequestBody.create(MediaType.parse("text/plain"), userModel.password);

            Response<Object> response = apiUtils.login(email, password);

            if(response.isSuccessful()) {
                if(response.body() != null) {

                    JSONObject json = new JSONObject((Map) response.body());

                    String status = json.getString("status");

                    if(status.equals("success")) {
                        JSONObject data = json.getJSONObject("data");

                        ThisApplication.token = data.getString("token");

                        BackgroundTask task = new BackgroundTask(userModel);
                        task.execute();

                    }

                    isRegisterBtnClick = false;
                }
            } else {

                Log.d(TAG, "err Msg : " + response.message());
                isRegisterBtnClick = false;
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
            Log.e(TAG, "로그인 네트워크를 사용할 준비가 되었을 333333");
        } else {
            Log.e(TAG, "로그인 네트워크가 끊켰을 4444444");
        }
    }

    // < >안에 들은 자료형은 순서대로 doInBackground, onProgressUpdate, onPostExecute의 매개변수 자료형(내가 사용할 매개변수타입을 설정하면된다)
    class BackgroundTask extends AsyncTask<Integer, Integer, Boolean> {

        private UserModel userModel;

        public BackgroundTask(UserModel userModel) {
            super();

            this.userModel = userModel;
        }

        protected void onPreExecute() {

        }

        protected Boolean doInBackground(Integer... values) {

            UserModel model = userViewModel.selectGetLoginUserEmail(this.userModel.getEmail());

            // 로컬디비에 이메일, 비밀번호로 조회해서 저장된 계정이 없으면 로컬 디비에 저장
            if (model == null) {

                Log.e(TAG, "Login insert UserModel : " + userModel.toString());

                /*if(userModel.getUserType().equals("General")){
                    userModel.email = userModel.getEmail();
                }else{
                    userModel.email = userModel.getUsername();
                }*/

                userModel.autoLogin = true;
                userViewModel.insertUser(this.userModel);

                ThisApplication.staticUserModel = this.userModel;
                return true;

            } else {
                // 로컬디비에 저장 된게 있으면 업데이트

/*                if (userModel.getUserType().equals("General")) {
                    userModel.loginId = model.getEmail();
                } else {
                    userModel.loginId = model.getUsername();
                    userModel.email = userModel.getUsername();
                }*/

                userModel.autoLogin = true;
                userModel.pkId = model.getPkId();
                Log.e(TAG, "Login update UserModel : " + userModel.toString());

//                userViewModel.updateUserPoint(userModel);
                ThisApplication.staticUserModel = userModel;

                return false;
            }
        }

        protected void onPostExecute(Boolean isInsert) {

            isRegisterBtnClick = false;

            if (isInsert) {

                Log.e(TAG, "계정 정보 저장");

            } else {
                Log.e(TAG, "계정 정보 수정");
            }

            // TODO 1. 내 계정으로 예약건이 있는지 1차 조회. ( 있을경우, 예약1건만 뿌려줌, 없을경우 Step2로 넘어감 )
            // TODO 2. 메인 화면 들어가기전 IntroActivity 혹은 LoginActivity에서 로그인 성공시 디폴트 값으로( 적정 요금, 반경 거리, 시작시간, 종료시간) 으로 충전기 정보를 불러옴.
            // TODO 3. 해당 내용 불러온 후 현재 Activity에서 충전기 목록을 MainActivity로 넘겨주기!!
            // TODO 4. MainActivity에서는 넘겨 받은 값으로 지도에 마커 뿌려주기

            Intent intent = new Intent(SignInActivity.this, MainActivity.class);

            startActivity(intent);
            finish();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkStatus.unregisterNetworkCallback();
    }
}
