package kr.co.metisinfo.sharingcharger.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Map;
import java.util.regex.Pattern;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.ActivityPasswordChangeBinding;
import kr.co.metisinfo.sharingcharger.model.UserModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.viewModel.UserViewModel;
import retrofit2.Response;

public class PasswordChangeActivity extends BaseActivity {

    private static final String TAG = PasswordChangeActivity.class.getSimpleName();

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

            boolean checkPw = true;

            String newPw = binding.newPwInput.getText().toString();
            String newPwCheck = binding.newPwConfirmInput.getText().toString();
            Log.e(TAG, " email : "+ email);

            if(!Pattern.matches(pwPattern, newPw)){

                binding.newPwInput.requestFocus();
                Toast.makeText(this, "비밀번호는 영문, 숫자, 특수 문자 포함하여 최소 6자 이상 16자리 이하로 설정하셔야합니다.", Toast.LENGTH_LONG).show();

                checkPw = false;
            }else if(!newPw.equals(newPwCheck)){

                binding.newPwConfirmInput.requestFocus();
                Toast.makeText(this, "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();

                checkPw = false;
            }

            if(checkPw){
                changePw(email);
            }

        }
        //설정 -> 비밀번호 변경시
        else{

            if(validationCheck()){
                CustomDialog customDialog = new CustomDialog(context, getString(R.string.change_password_confirm_text));

                customDialog.show();

                customDialog.findViewById(R.id.dialog_ok_btn).setOnClickListener(view -> {

                    Log.e(TAG, " 비밀번호 변경 api ");

                    changePw(ThisApplication.staticUserModel.email);

                    customDialog.dismiss();

                });
            }

        }

    }

    private void changePw(String mail){

        UserModel model = new UserModel();
        model.password = binding.newPwConfirmInput.getText().toString();

        try{

            Response<Object> response = apiUtils.changePassword(mail, model);

            //비밀번호 변경 성공
            if(response.code() == 200 && response.body() != null){

                JSONObject json = new JSONObject((Map) response.body());

                Gson gson = new Gson();

                model = gson.fromJson(json.toString(), UserModel.class);

                BackgroundTask task= new BackgroundTask(model);
                task.execute();

                Toast.makeText(this, "비밀번호 변경에 성공하였습니다.", Toast.LENGTH_LONG).show();

                finish();

            }
            //사용자 정보 확인
            else if(response.code() == 204){
                Toast.makeText(this, "아이디 또는 이메일을 다시 확인해 주세요.", Toast.LENGTH_LONG).show();
            }
            //실패
            else{
                Toast.makeText(this, "비밀번호 변경에 실패하였습니다.", Toast.LENGTH_LONG).show();
            }

        }catch (Exception e){
            Toast.makeText(this, "비밀번호 변경에 실패하였습니다.", Toast.LENGTH_LONG).show();
            Log.e(TAG, " changePw Exception : "+ e);
        }

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

            UserModel userModel = userViewModel.selectGetLoginUserEmail(model.email);
            Log.e(TAG, " user : "+ userModel);
            //db에 저장되어있는 user가 있으니 update 하면됨
            if(userModel != null){

                model.pkId = userModel.pkId;
                model.loginId = model.getEmail();
                model.autoLogin = true;

                userViewModel.updateUserPoint(model);

                ThisApplication.staticUserModel = model;

            }
            //db에 저장되어있는 user가 없기때문에 insert
            else{

                model.loginId = model.getEmail();
                userViewModel.insertUser(model);

                ThisApplication.staticUserModel = this.model;

            }
            return true;
        }

        protected void onPostExecute(Boolean isInsert) {

        }
    }

}
