package kr.co.metisinfo.sharingcharger.digitalWalletManagement;

import android.content.Intent;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import java.text.NumberFormat;
import java.util.Locale;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.ActivityWalletBinding;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.view.activity.PointChargeActivity;

/**
 * @ Class Name   : WalletActivity.java
 * @ Modification : WALLET ACTIVITY CLASS
 * @
 * @ 최초 생성일      최초 생성자
 * @ ---------    ---------
 * @ 2021.04.14.    고재훈
 * @
 * @  수정일          수정자
 * @ ---------    ---------
 * @
 **/
public class WalletActivity extends BaseActivity {

    ActivityWalletBinding binding;                                                                  //Databinding을 사용하기 위한 변수 선언

    ApiUtils apiUtils = new ApiUtils();

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet);           //XML bind
        changeStatusBarColor(false);

        binding.txtName.setText(ThisApplication.staticUserModel.getName() + "님의 Wallet");

        //실시간 포인트 가져오기
        int getPoint = 0;
        try {
            getPoint = apiUtils.getUserPoint();
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.txtWalletPoint.setText(NumberFormat.getInstance(Locale.KOREA).format(getPoint));
    }

    @Override
    public void initViewModel() {}

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnBack.setOnClickListener(view -> finish());                         //HEADER BACK BTN

        //충전하기
        binding.purchasePoint.setOnClickListener(view -> {

            Intent intent = new Intent(this, PointChargeActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void init() {

        binding.includeHeader.txtTitle.setText("전자지갑");                                           //HEADER TXT SET
    }

    /**
     * 포인트 사용이력 화면 이동
     * @param view
     */
    public void go_point_history(View view) {

        //포인트 사용이력 화면
        Intent intent = new Intent(this, PointUseHistoryActivity.class);
        startActivity(intent);
    }
}
