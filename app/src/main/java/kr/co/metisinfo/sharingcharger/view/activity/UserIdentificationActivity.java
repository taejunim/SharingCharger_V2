package kr.co.metisinfo.sharingcharger.view.activity;

import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityUserIdentificationBinding;
import kr.co.metisinfo.sharingcharger.model.UserModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.DateUtils;

public class UserIdentificationActivity extends BaseActivity {

    private static final String TAG = UserIdentificationActivity.class.getSimpleName();

    ActivityUserIdentificationBinding binding;

    ApiUtils apiUtils = new ApiUtils();

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_identification);

        changeStatusBarColor(false);

        try {
            UserModel userModel = apiUtils.getUserIdentification();

            if (userModel.getResponseCode() == 200) {
                binding.userIdentificationUserName.setText(userModel.name);
                binding.userIdentificationUserId.setText(userModel.email);
                binding.userIdentificationDid.setText(userModel.did.equals("") ? "-" : userModel.did);
                binding.userIdentificationCreatedDate.setText(userModel.did.equals("") ? "-" : DateUtils.convertToCreatedDate(userModel.created));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initViewModel() {

    }

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnBack.setOnClickListener(view -> finish());
    }

    @Override
    public void init() {

        binding.includeHeader.txtTitle.setText(R.string.user_identification_title);
        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);
    }
}
