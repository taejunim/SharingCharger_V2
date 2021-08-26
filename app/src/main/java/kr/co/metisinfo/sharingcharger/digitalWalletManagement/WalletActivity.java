package kr.co.metisinfo.sharingcharger.digitalWalletManagement;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import java.text.NumberFormat;
import java.util.Locale;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.ActivityWalletBinding;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.PreferenceUtil;
import kr.co.metisinfo.sharingcharger.view.activity.PurchaseDialog;
import kr.co.metisinfo.sharingcharger.view.activity.PurchaseWebViewActivity;

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

    private final int REQUEST_CODE = 100;                                                           //포인트 구매후 포인트 잔액을 받아오기 위한 임의의 요청번호

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

            PurchaseDialog purchaseDialog = new PurchaseDialog(this);
            purchaseDialog.setDialogListener(new PurchaseDialog.PurchaseDialogListener() {
                @Override
                public void onPurchaseButtonClicked(String cost) {
                    if(cost.equals("")) Toast.makeText(getApplicationContext(), "구매하실 금액을 입력하여 주십시오.", Toast.LENGTH_SHORT).show();
                    else if(cost.equals("0")) Toast.makeText(getApplicationContext(), "포인트 구매는 0원부터 가능합니다.", Toast.LENGTH_SHORT).show();
                    else {
                        Log.d("metis", Integer.parseInt(cost) + "입니당");
                        openWebView(cost);
                    }
                }
            });

            purchaseDialog.show();
        });
    }

    //포인트 충전 웹뷰 열기
    public void openWebView(String cost){

        //로그인 값 가져오기
        PreferenceUtil preferenceUtil = new PreferenceUtil(ThisApplication.context);

        String url = "https://devevzone.evzcharge.com/api/user/jeju_pay?product_amt=" + cost;

        //진우 API에서 didkey 전달되면 sp_user_define1 값에 넣어줘야함.
        url += "&sp_user_define1=" + preferenceUtil.getInt("userId");

        Intent intent = new Intent(this, PurchaseWebViewActivity.class);
        intent.putExtra("url", url);
        //값을 다시 받기위한 임의의 번호 (100)
        startActivityForResult(intent,REQUEST_CODE);
    }
    @Override
    public void init() {

        binding.includeHeader.txtTitle.setText("전자지갑");                                           //HEADER TXT SET
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if(data != null ) {
                String totalPoint = data.getStringExtra("totalPoint");
                binding.txtWalletPoint.setText(totalPoint);
                Toast.makeText(getApplicationContext(), "결제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
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
