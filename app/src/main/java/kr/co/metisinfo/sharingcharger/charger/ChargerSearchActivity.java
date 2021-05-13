package kr.co.metisinfo.sharingcharger.charger;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.charzin.evzsdk.EvzBluetooth;
import com.evzlife.android.blescanner.EVZScanCallbacks;
import com.evzlife.android.blescanner.EVZScanManager;
import com.evzlife.android.blescanner.EVZScanResult;

import java.util.List;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivitySearchChargerBinding;
import kr.co.metisinfo.sharingcharger.view.activity.BLEChargingActivity;
import kr.co.metisinfo.sharingcharger.view.activity.CustomDialog;

public class ChargerSearchActivity extends BaseActivity {

    private static final String TAG = ChargerSearchActivity.class.getSimpleName();

    ActivitySearchChargerBinding binding;

    GlideDrawableImageViewTarget gifImage;

    //임시 5분충전
    String reservationTime = "5";

    EvzBluetooth mEvzBluetooth;

    //스캔 관련.
    private EVZScanManager mScanner;
    List<EVZScanResult> mScData;
    EVZScanResult mEVZScanResult;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {  // 실행이 끝난후 확인 가능
            Bundle bd = msg.getData();

            // bluetooth 활성화
            if (bd.getBoolean("bluetooth")) {

                getBLEScan();
            }
        }
    };

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_charger);

        changeStatusBarColor(false);

        binding.includeHeader.txtTitle.setText("충전기 검색");
        binding.includeHeader.btnBack.setVisibility(View.INVISIBLE);
        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);

        // reservationTime = getIntent().getStringExtra("reservationTime");

    }

    @Override
    public void initViewModel() {

    }

    @Override
    public void setOnClickListener() {

        binding.btnSearchDevice.setOnClickListener(view -> {

            /*
            * 블루투스 연결해제 후 충전기 검색 버튼 클릭 시 에러남
            * 강제로 활성화 시킨 후 2.5초 후 scan 시작함(바로 시작 시 에러남)
            * */
            mEvzBluetooth.setBluetooth(true);
            showLoading();

            new Handler().postDelayed(new Runnable() {// 2.5 초 후에 실행
                @Override
                public void run() {
                    Message msg = mHandler.obtainMessage();  //사용할 핸들러를 이용해서 보낼 메시지 객체 생성
                    Bundle b1 = new Bundle();    //메시지를 담을 번들 생성
                    b1.putBoolean("bluetooth", true);    //번들에 메시지 추가
                    msg.setData(b1);    //메세지에 번들을 넣는다.

                    mHandler.sendMessage(msg);     //메세지를 핸들러로 넘긴다.

                }
            }, 2500);

        });
    }

    @Override
    public void init() {

        mScanner = new EVZScanManager();
        mEvzBluetooth = new EvzBluetooth(ChargerSearchActivity.this);

        mEvzBluetooth.setBluetooth(true);

    }

    public void getBLEScan() {

        mScanner.startScan(new EVZScanCallbacks() {

            @Override
            public void onScanFinished(@NonNull List<EVZScanResult> results) {
                mScData = results;
                if (mScData.size() > 0) {
                    Log.e(TAG, "onScan > 0");

                    for (int i = 0; i < mScData.size(); i++) {
                        Log.e(TAG, "BLE : " + mScData.get(i).getDevice().getAddress());
                    }

                    // api 적용시 예약된 BLE로 바로 보내줌
                    mEVZScanResult = mScData.get(0);
                    passingMEVZScanResult();
                } else {
                    Log.e(TAG, "onScan = 0");
                    scanFailed();
                }

            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.e(TAG, "onScanFailed");
                scanFailed();
            }
        });

    }

    private void scanFailed() {
        hideLoading();
        CustomDialog customDialog = new CustomDialog(this, "연결 가능한 충전기를 찾지 못했습니다.\n다시 검색하시겠습니까?");

        customDialog.show();

        customDialog.findViewById(R.id.dialog_ok_btn).setOnClickListener(view -> {
            Log.e(TAG, "customDialog_ok_btn");
            customDialog.dismiss();
            showLoading();
            getBLEScan();
        });
    }

    private void showLoading() {

        binding.imageLoading.setVisibility(View.VISIBLE);
        gifImage = new GlideDrawableImageViewTarget(binding.imageLoading);
        Glide.with(this).load(R.mipmap.spinner_loading).into(gifImage);

        //해당페이지 이벤트 막기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void hideLoading() {
        binding.imageLoading.setVisibility(View.INVISIBLE);

        //이벤트 다시 풀기
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void passingMEVZScanResult() {

        Intent intent = new Intent(ChargerSearchActivity.this, BLEChargingActivity.class);

        intent.putExtra("mEVZScanResult", mEVZScanResult);

        intent.putExtra("reservationTime", reservationTime);

        startActivity(intent);

        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {

        if (mScanner != null) {
            mScanner.release();
            mScanner = null;
        }

        super.onDestroy();
    }
}
