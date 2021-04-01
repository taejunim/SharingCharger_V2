package kr.co.metisinfo.sharingcharger.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
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
import com.charzin.evzsdk.EvzBLEScan;
import com.charzin.evzsdk.EvzBLETagData;
import com.charzin.evzsdk.EvzProtocol;
import com.charzin.evzsdk.EvzScan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.Constants;
import kr.co.metisinfo.sharingcharger.base.WebServiceAPI;
import kr.co.metisinfo.sharingcharger.databinding.ActivityChargerListBinding;
import kr.co.metisinfo.sharingcharger.model.ChargerModel;
import kr.co.metisinfo.sharingcharger.model.ReservationModel;
import kr.co.metisinfo.sharingcharger.network.RetrofitFactory;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.view.viewInterface.FragmentDialogInterface;

public class TestMainPersonalActivity extends BaseActivity implements FragmentDialogInterface {

    private static final String TAG = TestMainPersonalActivity.class.getSimpleName();

    public static Activity MainPersonalActivity;

    public ActivityChargerListBinding binding;

    private ReservationModel reservationModel = null;

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    boolean checkStart = false;

    GlideDrawableImageViewTarget gifImage;

    int ChargerTime = 480; // 8시간
    EvzBLEScan mEBS;
    EvzBLE mEB;

    EvzBLEData mCurData;
    ArrayList<EvzBLEData> mArrayBLEData = new ArrayList<>();
    EvzBLETagData mCurTag = new EvzBLETagData();

    boolean isRecharging = false;

    boolean isConnect = false;
    boolean isStart = false;

    //두번째 충전시 함수 성공이 두번탐....??
    //테스트 해봐야함
    boolean isGetTag = false;
    boolean isSetTag = false;
    boolean isStopTag = false;

    boolean isDelOne = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PAGE_SEARCH_CHARGER) {
            if (resultCode == RESULT_OK) {

                mCurData = data.getParcelableExtra("mCurData");

                BLEConnect(mCurData);

            }

        }
    }

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_charger_list);

        changeStatusBarColor(false);

        MainPersonalActivity = TestMainPersonalActivity.this;

    }

    @Override
    public void initViewModel() {

        // binding.includeHeader.btnBack.setVisibility(View.INVISIBLE);
        binding.includeHeader.txtTitle.setText("내 충전기 사용");
        binding.includeHeader.layoutHeaderMenu.setBackground(getDrawable(R.mipmap.ico_edit2));
        binding.includeHeader.layoutHeaderMenu.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 23, getResources().getDisplayMetrics());

        binding.searchChargerBtn.setVisibility(View.INVISIBLE);

    }

    @Override
    public void setOnClickListener() {

        //뒤로가기 버튼
        binding.includeHeader.btnBack.setOnClickListener(view -> {

            finish();

        });

        //다른 충전기 사용 버튼
        binding.customerCenterBtn.setOnClickListener(view ->{
            finish();
        });

        //설정 버튼
        binding.includeHeader.btnMenu.setOnClickListener(view -> {

            Intent intent = new Intent(this, SettingActivity.class);

            intent.putExtra("getTagName", this.getLocalClassName());

            startActivityForResult(intent, Constants.PAGE_SETTING);

        });

        //충전기 시작 버튼 누름
        binding.frameStart.setOnClickListener(view -> {

            Log.e(TAG, "ChargerTime : " + ChargerTime);

            isGetTag = true;

            showLoading();
            getBLEScan();

        });

        binding.frameEnd.setOnClickListener(view -> {
            showLoading();

            isGetTag = true;
            isStopTag = true;
            BLEStop(mCurData);
        });

    }

    @Override
    public void init() {

        mEBS = new EvzBLEScan(TestMainPersonalActivity.this);

        mEB = new EvzBLE(TestMainPersonalActivity.this);

        disableBtn();

        chargerFrameClick(binding.frameStart);

        //로딩
        showLoading();
        removeLoading();

        try {

            BLEDisConnect();

        } catch (Exception e) {
            Log.e(TAG, "BLE DISCONNECT Exception : " + e);
        }

    }

    //버튼 비활성화
    public void disableBtn() {

        binding.frameEnd.setBackground(ContextCompat.getDrawable(this, R.drawable.border_gray_50));
        binding.frameEnd.setEnabled(false);

        binding.frameStart.setBackground(ContextCompat.getDrawable(this, R.drawable.border_gray_50));
        binding.frameStart.setEnabled(false);
    }


    public void getBLEScan() {

        mArrayBLEData = new ArrayList<>();

        mEBS.BLEScan(list -> {

            mArrayBLEData = list;
            removeLoading();
            if (mArrayBLEData.size() > 0) {

                Intent intent = new Intent(TestMainPersonalActivity.this, ChargerDialog.class);

                intent.putParcelableArrayListExtra("mArrayBLEData", mArrayBLEData);

                startActivityForResult(intent, Constants.PAGE_SEARCH_CHARGER);

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("충전기 검색");
                builder.setMessage("연결 가능한 충전기를 찾지 못했습니다.\n다시 검색하시겠습니까?");

                builder.setPositiveButton("확인", (dialog, which) -> {
                    showLoading();
                    getBLEScan();
                });

                builder.setNegativeButton("취소", (dialog, which) -> {
                });
                builder.show();
            }
        });


    }

    //충전시작, 충전완료 버튼
    public void chargerFrameClick(View view) {

        if (view.getId() == R.id.frame_start) {

            binding.frameStart.setBackground(ContextCompat.getDrawable(TestMainPersonalActivity.this, R.drawable.border_blue_50));
            binding.frameStart.setEnabled(true);

            binding.frameEnd.setBackground(ContextCompat.getDrawable(TestMainPersonalActivity.this, R.drawable.border_gray_50));
            binding.frameEnd.setEnabled(false);

        } else if (view.getId() == R.id.frame_end) {

            binding.frameStart.setBackground(ContextCompat.getDrawable(TestMainPersonalActivity.this, R.drawable.border_gray_50));
            binding.frameStart.setEnabled(false);

            binding.frameEnd.setBackground(ContextCompat.getDrawable(TestMainPersonalActivity.this, R.drawable.border_red_50));
            binding.frameEnd.setEnabled(true);
        }
    }

    /**
     * 로딩 이미지 시작
     */
    private void showLoading() {

        gifImage = new GlideDrawableImageViewTarget(binding.imageLoading);
        Glide.with(this).load(R.drawable.spinner_loading).into(gifImage);

        //해당페이지 이벤트 막기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    /**
     * 로딩 이미지 종료
     */
    private void removeLoading() {

        try {

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Glide.with(this).onDestroy();
        } catch (Exception e) {
            Log.e(TAG, "Exception removeLoading : " + e);
        }

    }

    public void getTag() {

        Log.e(TAG, "getTag Call()");

        mEB.BLEGetTag(mCurData, new EvzProtocol.BLEGetTag() {

            @Override
            public void Success(ArrayList<EvzBLETagData> _data) {

                Log.e(TAG, "BLEGetTag Success");

                if (isGetTag) {

                    isGetTag = false;

                    if (_data.size() > 0) {

                        Log.e(TAG, "BLEGetTag size : " + _data.size());

                        for (int i = 0; i < _data.size(); i++) {

                            EvzBLETagData _tag = _data.get(i);
                            mCurTag = _tag;

                            Log.e("KDH", "num = " + _tag.Number + "  useTime = " + _tag.useTime + "  kwh = " + _tag.kwh);

                            BLEDelOneTag();

                        }

                        // TODO 모든 Tag 값을 서버로 전송해야하는지.

                    } else {
                        Log.e(TAG, "BLEGetTag size == 0");
                    }
                }


            }

            @Override
            public void Fail(int code, String s) {

                Log.e(TAG, "BLEGetTag Fail Code : " + code);
                //Toast.makeText(MainPersonalActivity.this, "BLEGetTag 충전기 연결에 실패하였습니다.\n충전기 연결을 다시 시도하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

            }
        });
    }


    public void BLEDelOneTag() {

        Log.e(TAG, "BLEDelOneTag Call()");
        Log.e(TAG, "BLEDelOneTag : TagNumber => " + mCurTag.Number);

        mEB.BLEDelOneTag(mCurTag, new EvzProtocol.BLEDelOneTag() {
            @Override
            public void Success() {

                Log.e(TAG, "BLEDelOneTag Success");


            }

            @Override
            public void Fail(int code, String msg) {

                Log.e(TAG, "BLEDelOneTag Fail code : " + code);
                Toast.makeText(TestMainPersonalActivity.this, "BLEDelOneTag 충전기 연결에 실패하였습니다.\n충전기 연결을 다시 시도하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void BLEConnect(EvzBLEData mCurData) {

        //  countDown(1000 * 7, 999);
        Log.e(TAG, "BLEConnect Call()");

        mEB.BLEConnect(mCurData, new EvzScan.BLEConnect() {

            @Override
            public void Success() {
                Log.e(TAG, "BLE Connect Success");

                isConnect = true;

                checkStart = true;

                if (isRecharging && !isStopTag) {
                    isStopTag = true;
                    BLEStop(mCurData);

                } else {
                    isGetTag = true;
                    getTag();

                    try {
                        Handler mHandler = new Handler() {
                            public void handleMessage(Message msg) {  // 실행이 끝난후 확인 가능
                                Bundle bd = msg.getData();

                                BLEStart(mCurData);
                            }
                        };

                        new Handler().postDelayed(new Runnable() {// 0.5 초 후에 실행
                            @Override
                            public void run() {
                                // 실행할 동작 코딩

                                Message msg1 = mHandler.obtainMessage();  //사용할 핸들러를 이용해서 보낼 메시지 객체 생성
                                Bundle b1 = new Bundle();    //메시지를 담을 번들 생성
                                b1.putBoolean("getTag", true);    //번들에 메시지 추가
                                msg1.setData(b1);    //메세지에 번들을 넣는다.

                                mHandler.sendMessage(msg1);     //메세지를 핸들러로 넘긴다.
                            }
                        }, 4500);

                    } catch (Exception e) {
                        Log.e(TAG, "BLE Connect handleMessage Exception" + e);
                    }
                }

            }

            @Override
            public void Fail(int code, String msg) {

                isConnect = false;
                Log.e(TAG, "BLE Connect Fail code : " + code);

                Toast.makeText(TestMainPersonalActivity.this, "BLEConnect 충전기 연결에 실패하였습니다.\n충전기 연결을 다시 시도하여 주시기 바랍니다.", Toast.LENGTH_SHORT).show();

                removeLoading();
                Log.e(TAG, "msg : " + msg);
                Log.e(TAG, "msg = " + msg);
            }
        });

    }


    public void BLEStop(EvzBLEData mCurData) {

        //    countDown(1000 * 3, 1);
        Log.e(TAG, "BLEStop Call()");
        //Stop
        mEB.BLEStop(mCurData, new EvzProtocol.BLEStop() {
            @Override
            public void Success() {

                Log.e(TAG, "BLEStop Success");

                if (isStopTag) {

                    isStopTag = false;

                    Handler mHandler = new Handler() {
                        public void handleMessage(Message msg) {  // 실행이 끝난후 확인 가능
                            Bundle bd = msg.getData();
                            if (bd.getBoolean("Stop")) {
                                // 메시지를 받고 처리할 부분
                                //get -> 진우 서버 -> 맞으면 delete
                                chargerFrameClick(binding.frameStart);
                                removeLoading();
                                BLEUserDis();

                            }

                        }
                    };

                    new Handler().postDelayed(new Runnable() {// 0.5 초 후에 실행
                        @Override
                        public void run() {
                            // 실행할 동작 코딩
                            Message msg1 = mHandler.obtainMessage();  //사용할 핸들러를 이용해서 보낼 메시지 객체 생성
                            Bundle b1 = new Bundle();    //메시지를 담을 번들 생성
                            b1.putBoolean("Stop", true);    //번들에 메시지 추가
                            msg1.setData(b1);    //메세지에 번들을 넣는다.

                            //stop 실행 후 setSharedPreferences함수 실행
                            isGetTag = true;
                            getTag();
                            mHandler.sendMessage(msg1);     //메세지를 핸들러로 넘긴다.
                        }
                    }, 2500);

                }

            }

            @Override
            public void Fail(int code, String msg) {

                Log.e(TAG, "BLEStop Fail code : " + code);

                Toast.makeText(TestMainPersonalActivity.this, "BLEStop 충전 종료를 실패하였습니다.\n다시 시도하여 주시기 바랍니다.", Toast.LENGTH_SHORT).show();

            }
        });
    }


    public void BLEStart(EvzBLEData mCurData) {
        mCurData.useTime = String.valueOf(ChargerTime);
        Log.e(TAG, "BLEStart ChargerTime :" + ChargerTime);
        Log.e(TAG, "BLEStart mCurData.useTime :" + mCurData.useTime);
        Log.e(TAG, "BLEStart Call()");

        mEB.BLEStart(mCurData, new EvzProtocol.BLEStart() {
            @Override
            public void Success() {
                Log.e(TAG, " start Success ()");
                //충전 인증 성공 시 충전api

                isStart = true;

                if (checkStart) {
                    Log.e(TAG, " start Log ()");
                    checkStart = false;
                    isSetTag = true;
                    mCurData.setTag = String.format(Locale.KOREA, "%013d", 1);

                    BLESetTag();

                    removeLoading();
                }

            }

            @Override
            public void Fail(int code, String msg) {
                removeLoading();
                isStart = false;
                Log.e(TAG, "BLEStart Fail code : " + code);
                Log.e(TAG, "BLEStart Fail msg : " + msg);
                Toast.makeText(TestMainPersonalActivity.this, "BLEStart 충전 시작에 실패하였습니다.\n다시 시도하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

            }

            @Override
            public void unPlug(int i) {
                removeLoading();
                Log.e(TAG, "BLEStart unPlug");
                Toast.makeText(TestMainPersonalActivity.this, "충전기에 플러그가 꼽혀있지 않습니다.\n충전기에 플러그를 꼽고 다시 시도하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void BLESetTag() {

        mEB.BLESetTag(mCurData, new EvzProtocol.BLESetTag() {
            @Override
            public void Success() {
                Log.e(TAG, "BLESetTag Success");

                if (isSetTag) {
                    Log.e(TAG, "BLESetTag Log");
                    isSetTag = false;
                    chargerFrameClick(binding.frameEnd);

                }

            }

            @Override
            public void Fail(int code, String msg) {
                removeLoading();
                Log.e(TAG, "BLESetTag Fail code : " + code);

                Toast.makeText(TestMainPersonalActivity.this, "BLESetTag 충전기 연결에 실패하였습니다.\n충전기 연결을 다시 시도하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

            }
        });

    }

    public void BLEUserDis() {

        mEB.BLEUserDis(new EvzProtocol.BLEUserDis() {
            @Override
            public void Success() {
                mEBS = new EvzBLEScan(TestMainPersonalActivity.this);
                mEB = new EvzBLE(TestMainPersonalActivity.this);
                Log.e("TAG", "BLEUserDis Success");

                reservationModel = null;

            }

            @Override
            public void Fail(int i, String s) {

                Log.e("TAG", "BLEUserDis Fail");
            }
        });

    }

    public void BLEDisConnect() {

        mEB.BLEDisConnect(new EvzScan.BLEDisConnect() {
            @Override
            public void disConnect(int code) {
                isRecharging = true;

                if (bluetoothAdapter != null) {
                    if (!bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.enable();
                    }
                }

                isConnect = false;
                removeLoading();
                Toast.makeText(getApplicationContext(), "BLEDisConnect 충전기 연결에 실패하였습니다. 충전기 검색을 다시 해주세요.", Toast.LENGTH_SHORT).show();
                Log.e("TAG", "BLEDisConnect");
            }
        });

    }

    public void onBackPressed() {

        finish();

    }

    @Override
    protected void onDestroy() {

        if (mEB != null) {
            Log.e(TAG, "onDestroy");

            mEB.Destroy();
        }

        super.onDestroy();

    }

    @Override
    public void btnClick(boolean btnType) {

        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pref = getSharedPreferences("reservation", MODE_PRIVATE);

        ChargerTime = pref.getInt("ChargerTime", 480);

    }

}
