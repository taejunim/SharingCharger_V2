package kr.co.metisinfo.sharingcharger.view.activity;

import android.content.Intent;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityWalletBinding;

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

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet);           //XML bind
        changeStatusBarColor(false);
    }

    @Override
    public void initViewModel() {}

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnBack.setOnClickListener(view -> finish());                         //HEADER BACK BTN
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
        Intent intent = new Intent(this, PointHistoryActivity.class);
        startActivity(intent);
    }

}
