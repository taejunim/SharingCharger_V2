package kr.co.metisinfo.sharingcharger.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.charzin.evzsdk.EvzBLE;
import com.charzin.evzsdk.EvzBLEData;
import com.charzin.evzsdk.EvzBLETagData;
import com.charzin.evzsdk.EvzProtocol;
import com.charzin.evzsdk.EvzScan;

import java.util.ArrayList;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityChargingBinding;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;

public class ChargingActivity extends BaseActivity {

    private static final String TAG = ChargingActivity.class.getSimpleName();

    ActivityChargingBinding binding;

    GlideDrawableImageViewTarget gifImage;

    ApiUtils apiUtils = new ApiUtils();

    EvzBLE mEB;
    EvzBLEData mCurData = new EvzBLEData();

    String ChargerTime = "30";
    final String testTag = "" + System.currentTimeMillis();

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {  // 실행이 끝난후 확인 가능
            Bundle bd = msg.getData();

            // Connect 성공
            if (bd.getBoolean("Connect")) {

                hideLoading();

                setAlertDialog("Connect","충전기 연결 성공","충전기와 연결되었습니다.");

            }else if(bd.getBoolean("Stop")) {

                setAlertDialog("Stop","충전기 종료 성공","충전기가 종료되었습니다.");

            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {


        }
    }

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_charging);

        changeStatusBarColor(false);

        //Evz BLE 컨트롤
        mEB = new EvzBLE(ChargingActivity.this);
        mCurData = new EvzBLEData();

    }

    @Override
    public void initViewModel() {

        showLoading();

    }

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);

        binding.includeHeader.btnBack.setOnClickListener(view -> finish());

        // 충전 시작 버튼
        binding.frameStart.setOnClickListener(view -> {
            mCurData.useTime = "2";
            BLEStart();

        });

        // 충전 완료 버튼
        binding.frameEnd.setOnClickListener(view -> {

            BLEStop();

        });

        //충전기 연결 버튼
        binding.searchChargerBtn.setOnClickListener(view -> {

            setAlertDialog("SearchCharger","충전기 검색","충전기를 다시 검색하시겠습니까?");

        });
    }

    @Override
    public void init() {

        binding.includeHeader.txtTitle.setText("충전");

        //사용자 화면에서는 필요없음, 소유주 화면 같이 사용함
        binding.listTextDummy2.setVisibility(View.INVISIBLE);
        binding.customerCenterBtn.setVisibility(View.INVISIBLE);

        disableBtn();

        //스캔데이터를 받어온다.
        mCurData.mEVZScanResult = getIntent().getParcelableExtra("mEVZScanResult");

        if(mCurData != null){
            BLEConnect();
        }

        BLEDisConnect();

    }

    //AlertDialog 추가
    public void setAlertDialog(String getType, String title, String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(ChargingActivity.this, R.style.AlertDialogStyle);

        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("확인", (dialog, which) ->{

            if(getType.equals("Connect")){
                dialog.dismiss();
                chargerFrameClick(binding.frameStart);
            }else if(getType.equals("Stop")){
                finish();
            }else{
                Intent intent = new Intent(this, SearchChargerActivity.class);

                startActivity(intent);

                finish();
            }

        });

        if(getType.equals("SearchCharger")){
            builder.setNegativeButton("취소", (dialog, which) -> {
            });
        }


        builder.show();

    }

    //충전시작, 충전완료 버튼
    public void chargerFrameClick(View view) {

        if (view.getId() == R.id.frame_start) {

            binding.frameStart.setBackground(ContextCompat.getDrawable(ChargingActivity.this, R.drawable.border_blue_50));
            binding.frameStart.setEnabled(true);

            binding.frameEnd.setBackground(ContextCompat.getDrawable(ChargingActivity.this, R.drawable.border_gray_50));
            binding.frameEnd.setEnabled(false);

        } else if (view.getId() == R.id.frame_end) {

            binding.frameStart.setBackground(ContextCompat.getDrawable(ChargingActivity.this, R.drawable.border_gray_50));
            binding.frameStart.setEnabled(false);

            binding.frameEnd.setBackground(ContextCompat.getDrawable(ChargingActivity.this, R.drawable.border_red_50));
            binding.frameEnd.setEnabled(true);
        }
    }

    //버튼 비활성화
    public void disableBtn() {

        binding.frameEnd.setBackground(ContextCompat.getDrawable(this, R.drawable.border_gray_50));
        binding.frameEnd.setEnabled(false);

        binding.frameStart.setBackground(ContextCompat.getDrawable(this, R.drawable.border_gray_50));
        binding.frameStart.setEnabled(false);
    }

    /**
     * 충전기 연결 버튼 비활성화
     */
    private void searchChargerBtnEnabled() {

        binding.searchChargerBtn.setClickable(false);
        binding.searchChargerBtn.setBackground(ContextCompat.getDrawable(ChargingActivity.this, R.drawable.button_border_gray));
        binding.searchChargerBtn.setEnabled(false);
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

    //BLEConnect
    public void BLEConnect() {

        mEB.BLEConnect(mCurData, new EvzScan.BLEConnect() {
            @Override
            public void Success() {
                Log.e(TAG, "BLE Connect Success");

                new Handler().postDelayed(new Runnable() {// 0.5 초 후에 실행
                    @Override
                    public void run() {
                        // 실행할 동작 코딩

                        Message msg = mHandler.obtainMessage();  //사용할 핸들러를 이용해서 보낼 메시지 객체 생성
                        Bundle b1 = new Bundle();    //메시지를 담을 번들 생성
                        b1.putBoolean("Connect", true);    //번들에 메시지 추가
                        msg.setData(b1);    //메세지에 번들을 넣는다.

                        BLEGetTag();

                        mHandler.sendMessage(msg);     //메세지를 핸들러로 넘긴다.
                    }
                }, 2500);

            }

            @Override
            public void Fail(int code, String msg) {
                Log.e(TAG, "BLE Connect Fail Code = "+code);
            }

        });
    }

    //BLEDisConnect
    public void BLEDisConnect() {
        mEB.BLEDisConnect(new EvzScan.BLEDisConnect() {
            @Override
            public void disConnect(int code) {
                Log.e(TAG, "BLEDisConnect Code = "+code);
            }
        });
    }

    //BLEStart
    public void BLEStart() {
        mEB.BLEStart(mCurData, new EvzProtocol.BLEStart() {
            @Override
            public void Success() {
                Log.e(TAG, "BLEStart Success");
                mCurData.setTag = testTag;
                BLESetTag();
            }

            @Override
            public void Fail(int code, String msg) {
                Log.e(TAG, "BLEStart Fail Code = "+code);
            }

            @Override
            public void unPlug(int i) {
                Log.e(TAG, "BLEStart unPlug Code = "+i);
            }
        });
    }

    //BLESetTag
    public void BLESetTag() {
        mEB.BLESetTag(mCurData, new EvzProtocol.BLESetTag() {
            @Override
            public void Success() {
                Log.e(TAG, "BLESetTag Success");
                chargerFrameClick(binding.frameEnd);
            }

            @Override
            public void Fail(int code, String msg) {
                Log.e(TAG, "BLESetTag Fail Code = "+code);
            }
        });
    }

    //BLEStop
    public void BLEStop() {
        mEB.BLEStop(mCurData, new EvzProtocol.BLEStop() {
            @Override
            public void Success() {
                Log.e(TAG, "BLEStop Success");

                new Handler().postDelayed(new Runnable() {// 0.5 초 후에 실행
                    @Override
                    public void run() {
                        // 실행할 동작 코딩

                        Message msg = mHandler.obtainMessage();  //사용할 핸들러를 이용해서 보낼 메시지 객체 생성
                        Bundle b1 = new Bundle();    //메시지를 담을 번들 생성
                        b1.putBoolean("Stop", true);    //번들에 메시지 추가
                        msg.setData(b1);    //메세지에 번들을 넣는다.

                        BLEGetTag();

                        mHandler.sendMessage(msg);     //메세지를 핸들러로 넘긴다.
                    }
                }, 2500);

            }

            @Override
            public void Fail(int code, String msg) {
                Log.e(TAG, "BLEStop Fail Code = "+code);
            }

            @Override
            public void unPlug(int code) {
                Log.e(TAG, "BLEStop unPlug Code = "+code);
            }
        });
    }

    //BLEGetTag
    public void BLEGetTag() {
        mEB.BLEGetTag(mCurData, new EvzProtocol.BLEGetTag() {
            @Override
            public void Success(ArrayList<EvzBLETagData> _data) {
                Log.e(TAG, "BLEGetTag Success = " + _data.size());
                if (_data.size() == 0) {
                    Log.e(TAG, "BLEGetTag Size == 0 ");
                } else {

                    for (int i = 0; i < _data.size(); i++) {

                        EvzBLETagData _tag = _data.get(i);

                        Log.e(TAG, "BLEGetTag  Number : "+ _tag.Number +" , kWh : "+ _tag.kwh + " , useTime : "+ _tag.useTime);

                        BLEDelOneTag(_tag);
                    }
                }
            }

            @Override
            public void Fail(int code, String msg) {
                Log.e(TAG, "BLEGetTag Fail Code = "+code);
            }
        });
    }

    //BLEDelOneTag
    public void BLEDelOneTag(EvzBLETagData mCurTag){
        mEB.BLEDelOneTag(mCurTag, new EvzProtocol.BLEDelOneTag() {
            @Override
            public void Success() {

                runOnUiThread(() ->  Log.e(TAG, "BLEDelOneTag Success") );
            }

            @Override
            public void Fail(int code, String msg) {
                runOnUiThread(() ->  Log.e(TAG, "BLEDelOneTag Fail Code = " +code) );
            }
        });
    }



    @Override
    protected void onDestroy() {
        mEB.Destroy();
        super.onDestroy();

    }

}
