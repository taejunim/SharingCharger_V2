package kr.co.metisinfo.sharingcharger.userManagement;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import java.util.regex.Pattern;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.ActivityPasswordChangeBinding;
import kr.co.metisinfo.sharingcharger.model.UserModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.view.activity.CustomDialog;
import kr.co.metisinfo.sharingcharger.viewModel.UserViewModel;

public class ChangePasswordActivity extends BaseActivity {

    private static final String TAG = ChangePasswordActivity.class.getSimpleName();

    ActivityPasswordChangeBinding binding;

    private UserViewModel userViewModel;

    private String tagNames = "";

    private String email = "";

    String pwPattern = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{5,15}.$";

    ApiUtils apiUtils = new ApiUtils();

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_password_change);

        changeStatusBarColor(false);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

    }

    @Override
    public void initViewModel() {

    }

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnBack.setOnClickListener(view -> finish());
        binding.changePwBtn.setOnClickListener(view -> passwordChangeConfirm(this));

    }

    private void passwordChangeConfirm(Context context) {

        Log.e(TAG, " tagNames :  "+ tagNames);

        //로그인화면-> 비밀번호 변경시
        if (tagNames.contains("Reset")) {

            //test
            finish();
            //test

//            boolean checkPw = true;
//
//            String newPw = binding.newPwInput.getText().toString();
//            String newPwCheck = binding.newPwConfirmInput.getText().toString();
//            Log.e(TAG, " email : "+ email);
//
//            if(!Pattern.matches(pwPattern, newPw)){
//
//                binding.newPwInput.requestFocus();
//                Toast.makeText(this, "비밀번호는 영문, 숫자, 특수 문자 포함하여 최소 6자 이상 16자리 이하로 설정하셔야합니다.", Toast.LENGTH_LONG).show();
//
//                checkPw = false;
//            }else if(!newPw.equals(newPwCheck)){
//
//                binding.newPwConfirmInput.requestFocus();
//                Toast.makeText(this, "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
//
//                checkPw = false;
//            }
//
//            if(checkPw){
//                changePw(email);
//            }

        }
        //설정 -> 비밀번호 변경시
        else{

            if(validationCheck()){
                CustomDialog customDialog = new CustomDialog(context, getString(R.string.m_password_confirm_text));

                customDialog.show();

                customDialog.findViewById(R.id.dialog_ok_btn).setOnClickListener(view -> {

                    Log.e(TAG, " 비밀번호 변경 api ");

               //     changePw(ThisApplication.staticUserModel.email);

                    customDialog.dismiss();

                    //test
                    finish();
                    //test
                });
            }

        }

    }

    private void changePw(String mail){

        UserModel model = new UserModel();
        model.password = binding.newPwConfirmInput.getText().toString();

       //비밀번호 변경

    }

    private boolean validationCheck() {

        String oldPw = ThisApplication.staticUserModel.getPassword();

        String oldPwCheck = binding.currentPwInput.getText().toString();

        String newPw = binding.newPwInput.getText().toString();
        String newPwCheck = binding.newPwConfirmInput.getText().toString();

        if(!oldPw.equals(oldPwCheck)){

            binding.currentPwInput.requestFocus();
            Toast.makeText(this, "현재 비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
            return false;

        }else if(!Pattern.matches(pwPattern, newPw)){

            binding.newPwInput.requestFocus();
            Toast.makeText(this, "비밀번호는 영문, 숫자, 특수 문자 포함하여 최소 6자 이상 16자리 이하로 설정하셔야합니다.", Toast.LENGTH_LONG).show();
            return false;

        }else if(!newPw.equals(newPwCheck)){

            binding.newPwConfirmInput.requestFocus();
            Toast.makeText(this, "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
            return false;

        }
        return true;
    }

    @Override
    public void init() {

        binding.includeHeader.txtTitle.setText(R.string.change_password);
        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        tagNames = intent.getStringExtra("activityName");

        if (tagNames.contains("Reset")) {
            binding.currentPwTitle.setVisibility(View.GONE);
            binding.currentPwStar.setVisibility(View.GONE);
            binding.currentPwInput.setVisibility(View.GONE);

            email = intent.getStringExtra("userEmail");
        }

    }

    class BackgroundTask extends AsyncTask<Integer, Integer, Boolean> {

        UserModel model = new UserModel();

        public BackgroundTask(UserModel model) {
            super();

            this.model = model;

        }

        protected void onPreExecute() {

        }

        protected Boolean doInBackground(Integer... values) {

            //user db 업데이트
            return true;
        }

        protected void onPostExecute(Boolean isInsert) {

        }
    }

}
