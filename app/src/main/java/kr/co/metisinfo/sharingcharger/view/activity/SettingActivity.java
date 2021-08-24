package kr.co.metisinfo.sharingcharger.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.ActivitySettingBinding;
import kr.co.metisinfo.sharingcharger.model.UserModel;
import kr.co.metisinfo.sharingcharger.userManagement.ChangePasswordActivity;
import kr.co.metisinfo.sharingcharger.userManagement.SignInActivity;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.PreferenceUtil;
import kr.co.metisinfo.sharingcharger.viewModel.UserViewModel;
import lombok.val;
import retrofit2.Response;

import static kr.co.metisinfo.sharingcharger.base.Constants.CHANGE_USER_TYPE;

public class SettingActivity extends BaseActivity {

    ActivitySettingBinding binding;

    private boolean isRegisterBtnClick = false;     // 버튼 더블클릭 막기 위한 boolean 타입 변수

    private String getType = null;

    ApiUtils apiUtils = new ApiUtils();

    PreferenceUtil preferenceUtil = new PreferenceUtil(ThisApplication.context);

    private boolean isUserTypeChange = false;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {  // 실행이 끝난후 확인 가능

            hideLoading(binding.loading);

            switch (msg.what) {
                case CHANGE_USER_TYPE :

                    final int resultCode = msg.arg1;

                    if (resultCode == 201) {
                        UserModel userModel = new UserModel();

                        userModel.loginId = preferenceUtil.getString("email");
                        userModel.password = preferenceUtil.getString("password");

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
                                preferenceUtil.putBoolean("isLogin", true);
                                preferenceUtil.putInt("userId", user.getId());
                                preferenceUtil.putString("name", user.getName());
                                preferenceUtil.putString("email", user.getUserType().equals("General") ? user.getEmail() : user.getUsername());
                                preferenceUtil.putString("password", userModel.getPassword());
                                preferenceUtil.putString("userType", user.getUserType());
                                preferenceUtil.putString("username", user.getUsername());

                                ThisApplication.staticUserModel = user;

                                Toast.makeText(SettingActivity.this, "소유주로 전환 되었습니다.\n소유중인 충전기를 등록해주세요.", Toast.LENGTH_LONG).show();

                                isUserTypeChange = true;

                            }
                            //로그인정보가 맞지 않을 때
                            else if (response.code() == 204) {
                                isRegisterBtnClick = false; // 버튼 다시 클릭 가능하도록 false로 전환

                                Toast.makeText(SettingActivity.this, R.string.setting_change_user_type_error, Toast.LENGTH_SHORT).show();
                            }
                            //로그인 실패
                            else{
                                isRegisterBtnClick = false; // 버튼 다시 클릭 가능하도록 false로 전환

                                Toast.makeText(SettingActivity.this, R.string.setting_change_user_type_error, Toast.LENGTH_SHORT).show();
                            }

                            isRegisterBtnClick = false;
                        }catch (Exception e) {

                            isRegisterBtnClick = false;
                            Log.e("metis","getLogin Exception : "+ e);
                        }

                    } else if (resultCode == 400) {
                        Toast.makeText(SettingActivity.this, "소유주 전환에 실패하였습니다.\n문제 지속시 고객센터로 문의주세요.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SettingActivity.this, "소유주 전환에 실패하였습니다.\n문제 지속시 고객센터로 문의주세요.", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);

        changeStatusBarColor(false);
    }

    @Override
    public void initViewModel() {

        //결제 카드 설정 생길때 추가해야함
        binding.settingCardLayout.setVisibility(View.GONE);
        binding.settingView.setVisibility(View.GONE);
        getType = getIntent().getStringExtra("getTagName");

        if(getType!= null && getType.contains("Personal")){
            binding.settingView1.setVisibility(View.VISIBLE);
            binding.settingTimeLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnBack.setOnClickListener(view ->  {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("isUserTypeChange", isUserTypeChange);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
        binding.settingPasswordLayout.setOnClickListener(view -> goChangePassword());

        binding.settingChangeUserTypeLayout.setOnClickListener(view -> changeUserType());

        // 결제 카드 설정 추가해야함
        binding.settingCardLayout.setOnClickListener(view -> goCardSetting());

        //소유주 시간 설정
        binding.settingTimeLayout.setOnClickListener(view -> {


        });

        binding.settingLogoutBtn.setOnClickListener(view -> { // 회원가입 버튼 클릭 리스너

            if (!isRegisterBtnClick) {   // 중복 클릭 막기 위함.

                isRegisterBtnClick = true;

                goLogout();
            }
        });
    }

    @Override
    public void init() {

        binding.includeHeader.txtTitle.setText("설정");
        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);

        addActivitys(this);
    }

    private void goChangePassword() {

        Intent intent = new Intent(this, ChangePasswordActivity.class);
        intent.putExtra("activityName", this.getLocalClassName());

        startActivity(intent);
    }

    private void changeUserType() {

        Log.d("metis", "goChangeUserType");

        if (!ThisApplication.staticUserModel.getUserType().equals("Personal")) {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingActivity.this);
            dialogBuilder.setMessage("소유주로 전환하시겠습니까?\n다시 일반사용자로 전환하시려면 고객센터를 통해 전환 가능합니다.");
            dialogBuilder.setPositiveButton("전환", (dialog, which) ->{
                PreferenceUtil preferenceUtil = new PreferenceUtil(ThisApplication.context);

                int userId = preferenceUtil.getInt("userId");

                showLoading(binding.loading);

                int resultCode = apiUtils.changeUserType(userId);

                Message msg = new Message();
                msg.what = CHANGE_USER_TYPE;
                msg.arg1 = resultCode;
                handler.sendMessage(msg);
            });
            dialogBuilder.setNegativeButton("취소", null);
            dialogBuilder.create().show();

        } else {
            Toast.makeText(SettingActivity.this, "현재 소유주 입니다.", Toast.LENGTH_LONG).show();
        }
    }

    private void goCardSetting() {

    }

    private void goLogout() {

        BackgroundTask task = new BackgroundTask(ThisApplication.staticUserModel);
        task.execute();

    }

    // < >안에 들은 자료형은 순서대로 doInBackground, onProgressUpdate, onPostExecute의 매개변수 자료형(내가 사용할 매개변수타입을 설정하면된다)
    class BackgroundTask extends AsyncTask<Integer, Integer, Boolean> {

        UserModel userModel;

        public BackgroundTask(UserModel userModel) {
            super();
            this.userModel = userModel;

        }

        protected void onPreExecute() {

        }

        protected Boolean doInBackground(Integer... values) {

            //로그인 값 가져오기
            PreferenceUtil preferenceUtil = new PreferenceUtil(ThisApplication.context);

            preferenceUtil.putBoolean("isLogin", false);
            preferenceUtil.putInt("userId", 0);
            preferenceUtil.putString("name", "");
            preferenceUtil.putString("email", "");
            preferenceUtil.putString("password", "");
            preferenceUtil.putString("userType", "");
            preferenceUtil.putString("username", "");

            //TODO 로그아웃시 추후에 로컬디비에 지우거나 업데이트 해야할 로직이 있으면 여기에!

            return true;
        }

        protected void onPostExecute(Boolean isUpdate) {

            if (isUpdate) {

                Log.e("metis", "로그아웃 성공");

                if(activityList.size() > 0) {

                    for (int i = 0; i < activityList.size(); i++) {

                        Log.e("metis","activityList.get(i).getClass() : "+activityList.get(i).getClass());

                        if (activityList.get(i).getClass().equals(MainActivity.class)) {

                            if(!activityList.get(i).isFinishing()){

                                activityList.get(i).finish();
                                activityList.remove(i);
                                i--;
                            }

                        }
                    }
                    Intent intent = new Intent(SettingActivity.this, SignInActivity.class);

                    startActivity(intent);

                    finish();

                }

            } else {

                Log.e("metis", "로그아웃 실패");
            }

            ThisApplication.staticUserModel = null;
            isRegisterBtnClick = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(activityList.size() > 0) {
            for (int i = 0; i < activityList.size(); i++) {

                if (activityList.get(i) == this) {
                    activityList.remove(i);
                    i--;
                }
            }
        }
    }
}
