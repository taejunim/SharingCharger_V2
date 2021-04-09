package kr.co.metisinfo.sharingcharger.view.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.ActivitySettingBinding;
import kr.co.metisinfo.sharingcharger.model.UserModel;
import kr.co.metisinfo.sharingcharger.viewModel.UserViewModel;

public class SettingActivity extends BaseActivity {

    private static final String TAG = SettingActivity.class.getSimpleName();

    ActivitySettingBinding binding;

    private boolean isRegisterBtnClick = false;     // 버튼 더블클릭 막기 위한 boolean 타입 변수

    private UserViewModel userViewModel;

    private String getType = null;

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);

        changeStatusBarColor(false);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
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

        binding.includeHeader.btnBack.setOnClickListener(view -> finish());
        binding.settingPasswordLayout.setOnClickListener(view -> goChangePassword());

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

        //비밀번호 변경

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

            //user Model update

            //TODO 로그아웃시 추후에 로컬디비에 지우거나 업데이트 해야할 로직이 있으면 여기에!

            return true;
        }

        protected void onPostExecute(Boolean isUpdate) {

            if (isUpdate) {

                Log.e(TAG, "로그아웃 성공");

                if(activityList.size() > 0) {

                    for (int i = 0; i < activityList.size(); i++) {

                        Log.e(TAG,"activityList.get(i).getClass() : "+activityList.get(i).getClass());

                        if (activityList.get(i).getClass().equals(MainActivity.class)) {

                            if(!activityList.get(i).isFinishing()){

                                activityList.get(i).finish();
                                activityList.remove(i);
                                i--;
                            }

                        }
                    }
                    Intent intent = new Intent(SettingActivity.this, LoginActivity.class);

                    startActivity(intent);

                    finish();

                }

            } else {

                Log.e(TAG, "로그아웃 실패");
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
