package kr.co.metisinfo.sharingcharger.view.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityChargerListBinding;
import kr.co.metisinfo.sharingcharger.model.AuthenticateModel;
import kr.co.metisinfo.sharingcharger.model.RechargeEndModel;
import kr.co.metisinfo.sharingcharger.model.RechargeModel;
import kr.co.metisinfo.sharingcharger.model.ReservationModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.CommonUtils;
import kr.co.metisinfo.sharingcharger.view.viewInterface.FragmentDialogInterface;

public class ChargerListActivity extends BaseActivity implements FragmentDialogInterface {

    private static final String TAG = ChargerListActivity.class.getSimpleName();

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //start 함수가 두번 실행되서 임시로 한번만 실행되도록 막음
    boolean checkStart = false;

    ActivityChargerListBinding binding;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    EvzBLE mEB;

    EvzBLEData mCurData = new EvzBLEData();

    EvzBLETagData mCurTag = new EvzBLETagData();

    //실제 충전 시간
    String ChargerTime = "1";

    GlideDrawableImageViewTarget gifImage;
    CountDownTimer timer;

    CommonUtils cu = new CommonUtils();

    ReservationModel reservationModel;

    private int rechargeId;

    private boolean isBackPressed = false;

    Timer chargingTimer = new Timer();                                                              //충전 진행 경과시간을 위한 Timer 선언

    int sec = 0;

    //실제 시작한 시간
    String stChargingTime;

    //충전 시작 종료시간 표시
    String sDate = "";
    String eDate = "";

    ApiUtils apiUtils = new ApiUtils();

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {  // 실행이 끝난후 확인 가능
            Bundle bd = msg.getData();

            // 메시지를 받고 처리할 부분
            if (bd.getBoolean("getTag")) {

                chargerFrameClick(binding.frameStart);
            } else if (bd.getBoolean("Stop")) {

                setSharedPreferences(false);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            mCurData = data.getParcelableExtra("mCurData");

            if (mCurData != null) {
                BLEConnect(mCurData);
            }

        }
    }

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_charger_list);

        changeStatusBarColor(false);

    }

    @Override
    public void initViewModel() {

    }

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);

        binding.includeHeader.btnBack.setOnClickListener(view -> finish());

        // 충전 시작 버튼
        binding.frameStart.setOnClickListener(view -> {

            Log.e(TAG, "ChargerTime : " + ChargerTime);

            Log.e(TAG, "reservationModel.bleNumber : " + reservationModel.bleNumber);
            Log.e(TAG, "mCurData.bleAddr : " + mCurData.bleAddr);

            if (reservationModel.bleNumber.equals(mCurData.bleAddr)) {
                checkStart = true;
                mCurData.useTime = ChargerTime;
                BLEStart(mCurData);

            } else {
                checkStart = false;
                Toast.makeText(ChargerListActivity.this, R.string.m_check_charger, Toast.LENGTH_SHORT).show();
            }
        });

        // 충전 완료 버튼
        binding.frameEnd.setOnClickListener(view -> {

            if (reservationModel.bleNumber.equals(mCurData.bleAddr)) {
                BLEStop(mCurData);

            } else {
                Toast.makeText(ChargerListActivity.this, R.string.m_check_charger, Toast.LENGTH_SHORT).show();
            }

            chargingTimer.cancel();                                                                 //Timer 종료
        });

        //충전기 연결 버튼
        binding.searchChargerBtn.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.bluetooth_find_device);
            builder.setMessage(R.string.m_reload_charger_list);

            builder.setPositiveButton(R.string.ok, (dialog, which) -> {

                Intent intent = new Intent(this, SearchBluetoothActivity.class);

                intent.putExtra("reservationModel", reservationModel);

                intent.putExtra("reservationTime", ChargerTime);

                startActivity(intent);

                finish();
            });

            builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            });
            builder.show();
        });
    }

    @Override
    public void init() {

        binding.includeHeader.txtTitle.setText(R.string.charge);

        //사용자 화면에서는 필요없음, 소유주 화면 같이 사용함
        binding.listTextDummy2.setVisibility(View.GONE);
        binding.customerCenterBtn.setVisibility(View.GONE);

        mEB = new EvzBLE(ChargerListActivity.this);

        mCurData = getIntent().getParcelableExtra("mCurData");

        reservationModel = (ReservationModel) getIntent().getSerializableExtra("reservationModel");

        if (reservationModel != null) {

            sDate = cu.setDateFormat(reservationModel.getStartDate());
            eDate = cu.setDateFormat(reservationModel.getEndDate());

        }

        ChargerTime = getIntent().getStringExtra("reservationTime");

        Log.e(TAG, "JH ChargerTime: " + ChargerTime);

        binding.frameStart.setEnabled(false);
        binding.frameEnd.setEnabled(false);

        if (mCurData != null) {
            BLEConnect(mCurData);
        }

        /**
         * 블루투스가 끊켰을 경우에 타는 콜백 함수
         * 사용자가 강제로 블루투스를 off
         * 충전기과 거리가 멀어져서 블루투스가 끊켰을 경우
         */

        mEB.BLEDisConnect(new EvzScan.BLEDisConnect() {
            @Override
            public void disConnect(int code) {

                if (bluetoothAdapter != null) {
                    if (!bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.enable();
                    }
                }

                Log.e(TAG, "BLE DISCONNECT code : " + code);
                Toast.makeText(getApplicationContext(), "BLEDisConnect 충전기 연결에 실패하였습니다. 충전기 검색을 다시 해주세요.", Toast.LENGTH_SHORT).show();
                finish();

            }
        });

    }

    public void getTag() {

        Log.e(TAG, "getTag Call()");

        mEB.BLEGetTag(mCurData, new EvzProtocol.BLEGetTag() {

            @Override
            public void Success(ArrayList<EvzBLETagData> _data) {

                Log.e(TAG, "BLEGetTag Success");
                if (_data.size() > 0) {

                    Log.e(TAG, "BLEGetTag size : " + _data.size());

                    for (int i = 0; i < _data.size(); i++) {

                        EvzBLETagData _tag = _data.get(i);
                        mCurTag = _tag;

                        Log.e("KDH", "num = " + _tag.Number + "  useTime = " + _tag.useTime + "  kwh = " + _tag.kwh);

                        try {
                            //
                            RechargeEndModel model = new RechargeEndModel();

                            model.rechargeId = Integer.parseInt(_tag.Number);
                            model.rechargeMinute = Integer.parseInt(_tag.useTime);
                            model.rechargeKwh = Double.valueOf(_tag.kwh);

                            Log.e(TAG, "rechargeId is " + model.rechargeId);
                            Log.e(TAG, "_tag.Number is " + _tag.Number);
                            Log.e(TAG, "endAuthenticateCharger reservationModel is " + reservationModel);

                            //getTagNumber == rechargeId 현재 내가 충전하고 있음 (정상종료)
                            if (_tag.Number != null && _tag.Number.equals(String.format(Locale.KOREA, "%013d", rechargeId))) {

                                RechargeModel rechargeEndModel = apiUtils.endAuthenticateCharger(reservationModel.chargerId, model, stChargingTime);

                                if (rechargeEndModel != null) {
                                    //충전 결과 표시
                                    showChargerFinishDialog(rechargeEndModel);
                                    BLEDelOneTag();

                                }
                            }
                            // 비정상 종료
                            else {

                                boolean result = apiUtils.endAuthenticateChargerUnplanned(reservationModel.chargerId, model);

                                if (result) {
                                    BLEDelOneTag();
                                }
                            }

                        } catch (Exception e) {

                            Log.e(TAG, "BLEGetTag Exception : " + e);
                        }

                    }

                } else {
                    Log.e(TAG, "BLEGetTag size == 0");
                }

            }

            @Override
            public void Fail(int code, String s) {

                Log.e(TAG, "BLEGetTag Fail Code : " + code);
                Toast.makeText(ChargerListActivity.this, "BLEGetTag 실패하였습니다.", Toast.LENGTH_LONG).show();
                timerFinish();
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
                timerFinish();
            }

            @Override
            public void Fail(int code, String msg) {

                Log.e(TAG, "BLEDelOneTag Fail code : " + code);
                Toast.makeText(ChargerListActivity.this, "BLEDelOneTag 실패하였습니다.", Toast.LENGTH_LONG).show();

                timerFinish();
            }
        });
    }

    //충전시작, 충전완료 버튼
    public void chargerFrameClick(View view) {

        if (view.getId() == R.id.frame_start) {

            binding.frameStart.setBackground(ContextCompat.getDrawable(ChargerListActivity.this, R.drawable.border_blue_50));
            binding.frameStart.setEnabled(true);

            binding.frameEnd.setBackground(ContextCompat.getDrawable(ChargerListActivity.this, R.drawable.border_gray_50));
            binding.frameEnd.setEnabled(false);

        } else if (view.getId() == R.id.frame_end) {

            binding.frameStart.setBackground(ContextCompat.getDrawable(ChargerListActivity.this, R.drawable.border_gray_50));
            binding.frameStart.setEnabled(false);

            binding.frameEnd.setBackground(ContextCompat.getDrawable(ChargerListActivity.this, R.drawable.border_red_50));
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

    /*
     * CHARGER FINNISH DIALOG SHOW
     * */
    private void showChargerFinishDialog(RechargeModel rModel) {

        ChargerFinishDialog cfd = new ChargerFinishDialog(this, rModel, this);
        cfd.setCancelable(false);                                                                   //DIALOG BACKGROUND CLICK FALSE
        cfd.show();
    }

    public void BLEConnect(EvzBLEData mCurData) {

        countDown(1000 * 7);
        Log.e(TAG, "BLEConnect Call()");

        mEB.BLEConnect(mCurData, new EvzScan.BLEConnect() {

            @Override
            public void Success() {
                Log.e(TAG, "BLE Connect Success");

                //충전 상태 가져오기
                reservationModel = apiUtils.getReservationStatus();

                try {
                    String temAddr = mCurData.bleName;

                    //충전 정보 text 표시
                    binding.chargerListStartTime.setText("충전 시작 : " + sDate);
                    binding.chargerListEndTime.setText("충전 종료 : " + eDate);

                    if (reservationModel.bleNumber.equals(mCurData.bleAddr)) {
                        binding.chargerListName.setText(reservationModel.getChargerName() + " - " + temAddr.substring(temAddr.length() - 4, temAddr.length()));
                        binding.chargerListAddress.setText(reservationModel.getChargerAddress());
                    } else {
                        binding.chargerListName.setText(mCurData.bleAddr);
                    }

                    //현재 충전중
                    if (reservationModel != null && reservationModel.state.equals("KEEP")) {
                        SharedPreferences pref = getSharedPreferences("reservation", MODE_PRIVATE);
                        String oldTime = pref.getString("time", "");

                        Log.e(TAG, "mCurData : " + mCurData.bleName);

                        //충전기 연결 버튼 비활성화
                        searchChargerBtnEnabled();

                        rechargeId = pref.getInt("rechargeId", 0);
                        stChargingTime = oldTime;
                        if (!oldTime.equals("")) {

                            //초 계산 값 가져오기
                            sec = cu.getSecond(oldTime, ChargerTime);

                            if (sec <= 0) {
                                BLEStop(mCurData);
                            } else {
                                ChargingTimerStart();
                            }

                            Log.e(TAG, "diff : " + sec);
                        }

                        Log.e(TAG, "reservationModel.bleNumber : " + reservationModel.bleNumber);
                        Log.e(TAG, "mCurData.bleAddr : " + mCurData.bleAddr);

                        binding.chargerRechargeIdTxt.setText(String.valueOf(rechargeId));
                        binding.chargerRechargeTxt.setVisibility(View.VISIBLE);
                        binding.chargerRechargeIdTxt.setVisibility(View.VISIBLE);

                        chargerFrameClick(binding.frameEnd);
                    }
                    //현재 충전중 X
                    else {

                        new Handler().postDelayed(new Runnable() {// 0.5 초 후에 실행
                            @Override
                            public void run() {
                                // 실행할 동작 코딩

                                Message msg1 = mHandler.obtainMessage();  //사용할 핸들러를 이용해서 보낼 메시지 객체 생성
                                Bundle b1 = new Bundle();    //메시지를 담을 번들 생성
                                b1.putBoolean("getTag", true);    //번들에 메시지 추가
                                msg1.setData(b1);    //메세지에 번들을 넣는다.

                                getTag();

                                mHandler.sendMessage(msg1);     //메세지를 핸들러로 넘긴다.
                            }
                        }, 4500);

                        //다이얼로그로 추가
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChargerListActivity.this);

                        builder.setTitle(R.string.m_success_connect);
                        builder.setMessage(R.string.m_success_charger_connect);

                        builder.setPositiveButton("확인", (dialog, which) -> dialog.dismiss());

                        builder.show();
                    }

                } catch (Exception e) {
                    Log.e(TAG, "BLEConnect Success Exception : " + e);
                }

            }

            @Override
            public void Fail(int code, String msg) {

                Log.e(TAG, "BLE Connect Fail code : " + code + " : " + msg);
                Toast.makeText(ChargerListActivity.this, "BLEConnect 충전기 연결에 실패하였습니다.\n충전기 연결을 다시 시도하여 주시기 바랍니다.", Toast.LENGTH_SHORT).show();

                //버튼 비활성화
                disableBtn();
                timerFinish();
            }
        });

    }

    public void BLEStop(EvzBLEData mCurData) {

        countDown(1000 * 3);
        Log.e(TAG, "BLEStop Call()");
        //Stop
        mEB.BLEStop(mCurData, new EvzProtocol.BLEStop() {
            @Override
            public void Success() {

                Log.e(TAG, "BLEStop Success");

                timerFinish();

                new Handler().postDelayed(new Runnable() {// 0.5 초 후에 실행
                    @Override
                    public void run() {
                        // 실행할 동작 코딩
                        Message msg1 = mHandler.obtainMessage();  //사용할 핸들러를 이용해서 보낼 메시지 객체 생성
                        Bundle b1 = new Bundle();    //메시지를 담을 번들 생성
                        b1.putBoolean("Stop", true);    //번들에 메시지 추가
                        msg1.setData(b1);    //메세지에 번들을 넣는다.

                        getTag();
                        mHandler.sendMessage(msg1);     //메세지를 핸들러로 넘긴다.
                    }
                }, 2500);

                ChargingTimerStop();                                                                //충전진행 경과시간 End
            }

            @Override
            public void Fail(int code, String msg) {

                Log.e(TAG, "BLEStop Fail code : " + code);

                Toast.makeText(ChargerListActivity.this, "BLEStop 충전 종료를 실패하였습니다.\n다시 시도하여 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                timerFinish();
                finish();

            }
        });
    }

    public void BLEStart(EvzBLEData mCurData) {

        countDown(1000 * 20);

        Log.e(TAG, "BLEStart Call()");

        mEB.BLEStart(mCurData, new EvzProtocol.BLEStart() {
            @Override
            public void Success() {
                Log.e(TAG, " start Success ()");

                //충전기 연결 버튼 비활성화
                searchChargerBtnEnabled();

                //충전 인증 성공 시 충전api
                boolean chk = false;

                if (checkStart) {

                    SimpleDateFormat formatT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());

                    AuthenticateModel model = new AuthenticateModel();
                    model.rechargeStartDate = formatT.format(cal.getTime());
                    model.reservationId = reservationModel.id;
                    model.userId = reservationModel.userId;

                    try {
                        Log.e(TAG, " getAuthenticateCharger ()");

                        //충전 시작 전 인증
                        boolean result = apiUtils.getAuthenticateCharger(reservationModel.chargerId, model);

                        if (result) {
                            chk = true;

                            //충전 시작 인증
                            rechargeId = apiUtils.startAuthenticateCharger(reservationModel.chargerId, model);

                            if (rechargeId != -1) {
                                chk = true;

                                binding.chargerRechargeIdTxt.setText(String.valueOf(rechargeId));
                                binding.chargerRechargeTxt.setVisibility(View.VISIBLE);
                                binding.chargerRechargeIdTxt.setVisibility(View.VISIBLE);
                                mCurData.setTag = String.format(Locale.KOREA, "%013d", rechargeId);

                                setSharedPreferences(true);

                                Log.e(TAG, "BLEStart Success");
                                BLESetTag();
                                sec = Integer.parseInt(ChargerTime) * 60;
                                ChargingTimerStart();                                               //충전진행 경과시간 START
                            }
                        }
                        Log.e(TAG, "chk : " + chk);
                        if (!chk) {
                            setSharedPreferences(false);
                            BLEStop(mCurData);
                            Toast.makeText(ChargerListActivity.this, "충전을 시작할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        checkStart = false;
                        setSharedPreferences(false);
                        BLEStop(mCurData);
                        Toast.makeText(ChargerListActivity.this, "인증에 실패하였습니다1.", Toast.LENGTH_SHORT).show();
                    }

                    checkStart = false;
                }

            }

            @Override
            public void Fail(int code, String msg) {

                Log.e(TAG, "BLEStart Fail code : " + code + " : " + msg);

                Toast.makeText(ChargerListActivity.this, "BLEStart 충전 시작에 실패하였습니다.\n다시 시도하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
                timerFinish();
            }

            @Override
            public void unPlug(int i) {
                Log.e(TAG, "BLEStart unPlug");
                Toast.makeText(ChargerListActivity.this, "충전기에 플러그가 꼽혀있지 않습니다.\n충전기에 플러그를 꼽고 다시 시도하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
                timerFinish();
            }
        });
    }

    public void BLESetTag() {

        Log.e(TAG, "BLESetTag Call()");

        mEB.BLESetTag(mCurData, new EvzProtocol.BLESetTag() {
            @Override
            public void Success() {
                Log.e(TAG, "BLESetTag Success");

                timerFinish();
                chargerFrameClick(binding.frameEnd);
            }

            @Override
            public void Fail(int code, String msg) {
                Log.e(TAG, "BLESetTag Fail code : " + code);

                Toast.makeText(ChargerListActivity.this, "BLESetTag 충전기 연결에 실패하였습니다.\n충전기 연결을 다시 시도하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
                timerFinish();
            }
        });
    }

    /**
     * 충전기 연결 버튼 비활성화
     */
    private void searchChargerBtnEnabled() {

        binding.searchChargerBtn.setClickable(false);
        binding.searchChargerBtn.setBackground(ContextCompat.getDrawable(ChargerListActivity.this, R.drawable.button_border_gray));
        binding.searchChargerBtn.setEnabled(false);
    }

    /**
     * 타이머 종료
     */
    private void timerFinish() {
        if (timer != null) {
            timer.onFinish();
            timer.cancel();
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
            Log.e(TAG, "removeLoading Exception : " + e);
        }

    }

    /**
     * 카운트 다운 타이머
     *
     * @param time 시간 ex) 3초 : 3000
     */
    public void countDown(long time) {

        isBackPressed = false;

        showLoading();

        timer = new CountDownTimer(time, 1000) {

            // 특정 시간마다 뷰 변경
            public void onTick(long millisUntilFinished) {

            }

            // 제한시간 종료시
            public void onFinish() {
                isBackPressed = true;
                binding.searchChargerBtn.setEnabled(true);
                Log.e(TAG, "countDownTimer.onFinish() CALL()");

                removeLoading();
            }

        }.start();
    }

    public void setSharedPreferences(boolean check) {

        SharedPreferences pref = getSharedPreferences("reservation", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if (check) {
            Date now = new Date();

            Log.e(TAG, "setSharedPreferences rechargeId: " + rechargeId);
            editor.putInt("rechargeId", rechargeId);
            editor.putString("time", format.format(now));
            editor.putString("activity", "ChargerListActivity");

            stChargingTime = format.format(now);
        } else {
            editor.putInt("rechargeId", 0);
            editor.putString("time", null);
            editor.putString("activity", null);
        }
        editor.commit();
    }

    /*
     * 충전진행 경과시간 START
     */
    public void ChargingTimerStart() {

        TextView chargingTimerTx = (TextView) findViewById(R.id.charging_timer);

        TimerTask TT = new TimerTask() {

            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {                                                             //ui쪽에서는 runOnUiTread로 돌려야함.

                        if (sec != 0) {
                            sec--;

                            chargingTimerTx.setText(cu.chargingTime(sec));   //sec를 hh:mm:ss로 변환 CALL 하고 TEXT에 SET
                        } else {
                            BLEStop(mCurData);
                        }

                    }
                });
            }
        };

        chargingTimer.schedule(TT, 0, 1000);                                          //Timer 실행
    }

    /*
     * 충전진행 경과시간 End
     */
    public void ChargingTimerStop() {

        TextView chargingTimerTx = (TextView) findViewById(R.id.charging_timer);
        chargingTimerTx.setText("00:00:00");
        chargingTimer.cancel();                                                                     //Timer 종료
    }

    public void onBackPressed() {

        if (isBackPressed) {
            finish();
        }

    }

    @Override
    protected void onDestroy() {

        //mEB 종료 해줘야함
        if (mEB != null) {
            mEB.Destroy();
        }
        chargingTimer.cancel();
        super.onDestroy();
    }

    @Override
    public void btnClick(boolean btnType) {

        finish();
    }
}
