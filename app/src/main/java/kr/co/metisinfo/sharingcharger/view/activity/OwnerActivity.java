package kr.co.metisinfo.sharingcharger.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.charzin.evzsdk.EvzBLE;
import com.charzin.evzsdk.EvzBLEData;
import com.charzin.evzsdk.EvzBLETagData;
import com.charzin.evzsdk.EvzBluetooth;
import com.charzin.evzsdk.EvzProtocol;
import com.charzin.evzsdk.EvzScan;
import com.evzlife.android.blescanner.EVZScanCallbacks;
import com.evzlife.android.blescanner.EVZScanManager;
import com.evzlife.android.blescanner.EVZScanResult;

import java.util.ArrayList;
import java.util.List;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.Constants;
import kr.co.metisinfo.sharingcharger.databinding.ActivityChargingBinding;

public class OwnerActivity extends BaseActivity {

    private static final String TAG = OwnerActivity.class.getSimpleName();

    ActivityChargingBinding binding;

    GlideDrawableImageViewTarget gifImage;

    EvzBluetooth mEvzBluetooth;

    //스캔 관련.
    private EVZScanManager mScanner;
    List<EVZScanResult> mScData;

    EvzBLE mEB;
    EvzBLEData mCurData = new EvzBLEData();

    final String testTag = "" + System.currentTimeMillis();

    //start 함수가 2번 돌아가기 때문에 한번으로 막아줘야함
    boolean startFlag = true;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {  // 실행이 끝난후 확인 가능
            Bundle bd = msg.getData();

            // Connect 성공
            if (bd.getBoolean("Connect")) {

                Log.e("metis","Connect handler");

              //  BLEStart();
                hideLoading();

            }else if(bd.getBoolean("Stop")) {

                hideLoading();

                setAlertDialog("Stop","충전기 종료 성공","충전기가 종료되었습니다.");

            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PAGE_SEARCH_CHARGER) {

            if (resultCode == RESULT_OK) {
                mCurData.mEVZScanResult = data.getParcelableExtra("mEVZScanResult");
                mCurData.useTime = "2";
                BLEConnect();
            }else if(resultCode == RESULT_CANCELED){
                hideLoading();
            }

        }
    }

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_charging);

        changeStatusBarColor(false);

    }

    @Override
    public void initViewModel() {

        binding.includeHeader.txtTitle.setText("내 충전기 사용");
        binding.includeHeader.layoutHeaderMenu.setBackground(getDrawable(R.mipmap.ico_edit2));
        binding.includeHeader.layoutHeaderMenu.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 23, getResources().getDisplayMetrics());

        binding.searchChargerBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setOnClickListener() {

        //충전기 시작 버튼 누름
        binding.frameStart.setOnClickListener(view -> {

            showLoading();
            getBLEScan();
        });

        //충전기 종료 버튼 누름
        binding.frameEnd.setOnClickListener(view -> {
            showLoading();
            BLEStop();
        });

        binding.customerCenterBtn.setOnClickListener(view -> {
            BLEStart();
        });

    }

    @Override
    public void init() {

        mScanner = new EVZScanManager();
        mEvzBluetooth = new EvzBluetooth(OwnerActivity.this);

        mEvzBluetooth.setBluetooth(true);

        //Evz BLE 컨트롤
        mEB = new EvzBLE(OwnerActivity.this);
        mCurData = new EvzBLEData();

        hideLoading();

        chargerFrameClick(binding.frameStart);

        BLEDisConnect();

        BLEPlugState();

    }

    public void getBLEScan() {

        mScanner.startScan(new EVZScanCallbacks() {

            @Override
            public void onScanFinished(@NonNull List<EVZScanResult> results) {
                mScData = results;
                if (mScData.size() > 0) {
                    Log.e("metis", "onScan > 0");

                    Intent intent = new Intent(OwnerActivity.this, ChargerDialog.class);

                    intent.putParcelableArrayListExtra("mScData", (ArrayList<? extends Parcelable>) mScData);

                    startActivityForResult(intent, Constants.PAGE_SEARCH_CHARGER);

                } else {
                    Log.e("metis", "onScan = 0");
                    hideLoading();
                }

            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.e("metis", "onScanFailed");
                hideLoading();
            }
        });

    }

    //AlertDialog 추가
    public void setAlertDialog(String getType, String title, String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(OwnerActivity.this, R.style.AlertDialogStyle);

        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("확인", (dialog, which) ->{

            if(getType.equals("Connect")){
                dialog.dismiss();
                chargerFrameClick(binding.frameStart);
            }else if(getType.equals("Stop")){
                //finish();
                startFlag = true;
                chargerFrameClick(binding.frameStart);
                dialog.dismiss();
            }

        });

        builder.show();

    }

    //충전시작, 충전완료 버튼
    public void chargerFrameClick(View view) {

        if (view.getId() == R.id.frame_start) {

            binding.frameStart.setBackground(ContextCompat.getDrawable(OwnerActivity.this, R.drawable.border_blue_50));
            binding.frameStart.setEnabled(true);

            binding.frameEnd.setBackground(ContextCompat.getDrawable(OwnerActivity.this, R.drawable.border_gray_50));
            binding.frameEnd.setEnabled(false);

        } else if (view.getId() == R.id.frame_end) {

            binding.frameStart.setBackground(ContextCompat.getDrawable(OwnerActivity.this, R.drawable.border_gray_50));
            binding.frameStart.setEnabled(false);

            binding.frameEnd.setBackground(ContextCompat.getDrawable(OwnerActivity.this, R.drawable.border_red_50));
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

    private void createMessage(String getType){

        Message msg = mHandler.obtainMessage();  //사용할 핸들러를 이용해서 보낼 메시지 객체 생성
        Bundle b1 = new Bundle();    //메시지를 담을 번들 생성
        b1.putBoolean(getType, true);    //번들에 메시지 추가
        msg.setData(b1);    //메세지에 번들을 넣는다.

        mHandler.sendMessage(msg);     //메세지를 핸들러로 넘긴다.
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
                Log.e("metis", "BLE Connect Success");
                BLEGetTag();

                new Handler().postDelayed(new Runnable() {// 2.5 초 후에 실행
                    @Override
                    public void run() {

                        createMessage("Connect");
                        BLEStart();
                      //  BLEStart();
                    }
                }, 4000);

            }

            @Override
            public void Fail(int code, String msg) {
                Log.e("metis", "BLE Connect Fail Code = "+code);
            }

        });
    }

    //BLEDisConnect
    public void BLEDisConnect() {
        mEB.BLEDisConnect(new EvzScan.BLEDisConnect() {
            @Override
            public void disConnect(int code) {

                Toast.makeText(OwnerActivity.this,"BLEDisConnect",Toast.LENGTH_LONG).show();
                Log.e("metis", "BLEDisConnect Code = "+code);
                //finish();

            }
        });
    }

    //BLEStart
    public void BLEStart() {
        mEB.BLEStart(mCurData, new EvzProtocol.BLEStart() {
            @Override
            public void Success() {

                if(startFlag){

                    startFlag = false;
                    Log.e("metis", "BLEStart Success");
                    mCurData.setTag = testTag;
                    BLESetTag();
                }
            }

            @Override
            public void Fail(int code, String msg) {
                hideLoading();
                Log.e("metis", "BLEStart Fail Code = "+code);
            }

        });
    }

    //BLESetTag
    public void BLESetTag() {
        mEB.BLESetTag(mCurData, new EvzProtocol.BLESetTag() {
            @Override
            public void Success() {
                Log.e("metis", "BLESetTag Success");
                hideLoading();
                chargerFrameClick(binding.frameEnd);
            }

            @Override
            public void Fail(int code, String msg) {
                hideLoading();
                Log.e("metis", "BLESetTag Fail Code = "+code);
            }
        });
    }

    //BLEStop
    public void BLEStop() {
        mEB.BLEStop(mCurData, new EvzProtocol.BLEStop() {
            @Override
            public void Success() {
                Log.e("metis", "BLEStop Success");
                BLEGetTag();
                new Handler().postDelayed(new Runnable() {// 2.5 초 후에 실행
                    @Override
                    public void run() {

                        BLEUserDis();
                        createMessage("Stop");

                    }
                }, 2500);

            }

            @Override
            public void Fail(int code, String msg) {
                hideLoading();
                Log.e("metis", "BLEStop Fail Code = "+code);
            }

        });
    }

    //BLEGetTag
    public void BLEGetTag() {
        mEB.BLEGetTag(mCurData, new EvzProtocol.BLEGetTag() {
            @Override
            public void Success(ArrayList<EvzBLETagData> _data) {
                Log.e("metis", "BLEGetTag Success = " + _data.size());
                if (_data.size() == 0) {
                    Log.e("metis", "BLEGetTag Size == 0 ");
                } else {

                    for (int i = 0; i < _data.size(); i++) {

                        EvzBLETagData _tag = _data.get(i);

                        Log.e("metis", "BLEGetTag  Number : "+ _tag.Number +" , kWh : "+ _tag.kwh + " , useTime : "+ _tag.useTime);

                        BLEDelOneTag(_tag);
                    }
                }
            }

            @Override
            public void Fail(int code, String msg) {
                Log.e("metis", "BLEGetTag Fail Code = "+code);
            }
        });
    }

    //BLEDelOneTag
    public void BLEDelOneTag(EvzBLETagData mCurTag){
        mEB.BLEDelOneTag(mCurTag, new EvzProtocol.BLEDelOneTag() {
            @Override
            public void Success() {

                runOnUiThread(() ->  Log.e("metis", "BLEDelOneTag Success") );
            }

            @Override
            public void Fail(int code, String msg) {
                runOnUiThread(() ->  Log.e("metis", "BLEDelOneTag Fail Code = " +code) );
            }
        });
    }

    //BLEUserDis
    public void BLEUserDis(){
        mEB.BLEUserDis(new EvzProtocol.BLEUserDis() {
            @Override
            public void Success() {
                Log.e("metis", "BLEUserDis Success");
            }

            @Override
            public void Fail(int i, String s) {
                Log.e("metis", "BLEUserDis Fail");
            }
        });
    }

    //plug 상태값 확인
    public void BLEPlugState(){
        mEB.BLEPlugState(new EvzProtocol.BLEPlugState() {
            @Override
            public void PlugState(int code) {
                Toast.makeText(getApplicationContext(), "충전기에 플러그가 꼽혀있지 않습니다.\n충전기에 플러그를 꼽고 다시 시도하여 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                hideLoading();
                Log.e("metis", "PlugState Plug = "+ code);
            }
        });
    }

    @Override
    protected void onDestroy() {

        if (mScanner != null) {
            mScanner.release();
            mScanner = null;
        }

        if(mEB != null){
            mEB.Destroy();
        }

        super.onDestroy();
    }
}
