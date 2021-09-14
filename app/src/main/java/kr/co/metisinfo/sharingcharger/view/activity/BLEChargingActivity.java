package kr.co.metisinfo.sharingcharger.view.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.charzin.evzsdk.EvzBLE;
import com.charzin.evzsdk.EvzBLEData;
import com.charzin.evzsdk.EvzBLETagData;
import com.charzin.evzsdk.EvzProtocol;
import com.charzin.evzsdk.EvzScan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.metisinfo.sharingcharger.ChargerDialogAdapter;
import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.charger.ChargerSearchActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityChargingBinding;
import kr.co.metisinfo.sharingcharger.dialog.ChargerFinishDialog;
import kr.co.metisinfo.sharingcharger.model.AuthenticateModel;
import kr.co.metisinfo.sharingcharger.model.RechargeEndModel;
import kr.co.metisinfo.sharingcharger.model.RechargeModel;
import kr.co.metisinfo.sharingcharger.model.ReservationModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.CommonUtils;
import kr.co.metisinfo.sharingcharger.utils.PreferenceUtil;
import kr.co.metisinfo.sharingcharger.view.viewInterface.FragmentDialogInterface;

public class BLEChargingActivity extends BaseActivity implements FragmentDialogInterface {

    private static final String TAG = BLEChargingActivity.class.getSimpleName();

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat rechargeTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    PreferenceUtil preferenceUtil = new PreferenceUtil(ThisApplication.context);

    //start 함수가 두번 실행되서 임시로 한번만 실행되도록 막음
    //boolean checkStart = false;

    ActivityChargingBinding binding;

    ApiUtils apiUtils = new ApiUtils();

    EvzBLE mEB;
    EvzBLEData mCurData = new EvzBLEData();
    EvzBLETagData mCurTag = new EvzBLETagData();

    //실제 충전 시간
    String ChargerTime = "1";

    CountDownTimer timer;

    CommonUtils cu = new CommonUtils();

    ReservationModel reservationModel;

    private int rechargeId;

    //private boolean isBackPressed = false;

    Timer chargingTimer = new Timer();                                                              //충전 진행 경과시간을 위한 Timer 선언

    int sec = 0;

    //실제 시작한 시간
    String stChargingTime;

    //충전 시작 종료시간 표시
    String sDate = "";
    String eDate = "";

    //start 함수가 2번 돌아가기 때문에 한번으로 막아줘야함
    boolean checkStart = true;

    RechargeModel rechargeEndModel = new RechargeModel();

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {  // 실행이 끝난후 확인 가능
            Bundle bd = msg.getData();

            // Connect 성공
            if (bd.getBoolean("Connect")) {

                hideLoading(binding.loading);

                setAlertDialog("Connect","충전기 연결 성공","충전기와 연결되었습니다.");

            }else if(bd.getBoolean("Stop")) {

                hideLoading(binding.loading);

                setAlertDialog("Stop","충전기 종료 성공","충전기가 종료되었습니다.");

            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            mCurData = data.getParcelableExtra("mCurData");

            if (mCurData != null) {
                BLEConnect();
            }
        }
    }

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_charging);

        changeStatusBarColor(false);

        //Evz BLE 컨트롤
        mEB = new EvzBLE(BLEChargingActivity.this);
        //mCurData = new EvzBLEData();

    }

    @Override
    public void initViewModel() {

        showLoading(binding.loading);

    }

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);

        binding.includeHeader.btnBack.setOnClickListener(view -> finish());

        // 충전 시작 버튼
        binding.frameStart.setOnClickListener(view -> {
            /*mCurData.useTime = "2";
            showLoading();
            BLEStart();*/

            Log.e("metis", "ChargerTime : " + ChargerTime);
            Log.e("metis", "reservationModel.bleNumber : " + reservationModel.bleNumber);
            Log.e("metis", "mCurData.mEVZScanResult.getDevice().toString() : " + mCurData.mEVZScanResult.getDevice().toString());

            if (reservationModel.bleNumber.equals(mCurData.mEVZScanResult.getDevice().toString())) {
                checkStart = true;
                mCurData.useTime = ChargerTime;
                BLEStart();

            } else {
                checkStart = false;
                Toast.makeText(BLEChargingActivity.this, "충전기를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }

        });

        // 충전 종료 버튼
        binding.frameEnd.setOnClickListener(view -> {
            /*showLoading();
            BLEStop();*/

            if (reservationModel.bleNumber.equals(mCurData.mEVZScanResult.getDevice().toString())) {
                BLEStop();

            } else {
                Toast.makeText(BLEChargingActivity.this, "충전기를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }

            chargingTimer.cancel();                                                                 //Timer 종료

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

        reservationModel = (ReservationModel) getIntent().getSerializableExtra("reservationModel");

        if (reservationModel != null) {

            sDate = cu.setDateFormat(reservationModel.getStartDate());
            eDate = cu.setDateFormat(reservationModel.getEndDate());
        }

        ChargerTime = getIntent().getStringExtra("reservationTime");
        Log.e("metis", "JH ChargerTime: " + ChargerTime);

        BLEDisConnect();

        BLEPlugState();

    }

    //AlertDialog 추가
    public void setAlertDialog(String getMsgType, String title, String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(BLEChargingActivity.this, R.style.AlertDialogStyle);

        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("확인", (dialog, which) ->{

            if(getMsgType.equals("Connect")){
                dialog.dismiss();
                chargerFrameClick(binding.frameStart);
            }else if(getMsgType.equals("Stop")){
                showChargerFinishDialog(rechargeEndModel);
            }else{
                Intent intent = new Intent(this, ChargerSearchActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if(getMsgType.equals("SearchCharger")){
            builder.setNegativeButton("취소", (dialog, which) -> {
            });
        }

        builder.show();
    }

    //충전시작, 충전종료 버튼
    public void chargerFrameClick(View view) {

        if (view.getId() == R.id.frame_start) {

            binding.frameStart.setBackground(ContextCompat.getDrawable(BLEChargingActivity.this, R.drawable.border_blue_50));
            binding.frameStart.setEnabled(true);

            binding.frameEnd.setBackground(ContextCompat.getDrawable(BLEChargingActivity.this, R.drawable.border_gray_50));
            binding.frameEnd.setEnabled(false);

        } else if (view.getId() == R.id.frame_end) {

            binding.frameStart.setBackground(ContextCompat.getDrawable(BLEChargingActivity.this, R.drawable.border_gray_50));
            binding.frameStart.setEnabled(false);

            binding.frameEnd.setBackground(ContextCompat.getDrawable(BLEChargingActivity.this, R.drawable.border_red_50));
            binding.frameEnd.setEnabled(true);
        }
    }

    /**
     * 충전 시작, 종료 버튼 비활성화
     */
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
        binding.searchChargerBtn.setBackground(ContextCompat.getDrawable(BLEChargingActivity.this, R.drawable.button_border_gray));
        binding.searchChargerBtn.setEnabled(false);
    }

    private void createMessage(String getType){

        Message msg = mHandler.obtainMessage();  //사용할 핸들러를 이용해서 보낼 메시지 객체 생성
        Bundle b1 = new Bundle();    //메시지를 담을 번들 생성
        b1.putBoolean(getType, true);    //번들에 메시지 추가
        msg.setData(b1);    //메세지에 번들을 넣는다.

        mHandler.sendMessage(msg);     //메세지를 핸들러로 넘긴다.
    }

    //BLEConnect
    public void BLEConnect() {

        //countDown(1000 * 7);
        showLoading(binding.loading);

        mEB.BLEConnect(mCurData, new EvzScan.BLEConnect() {
            @Override
            public void Success() {
                Log.e("metis", "BLE Connect Success");

                /*new Handler().postDelayed(new Runnable() {// 2.5 초 후에 실행
                    @Override
                    public void run() {

                        createMessage("Connect");
                        BLEGetTag();

                    }
                }, 2500);*/
                //충전 상태 가져오기
                reservationModel = apiUtils.getReservationStatus();

                try {
                    //String temAddr = mCurData.bleName;
                    String temAddr = mCurData.mEVZScanResult.getName().toString();

                    //충전 정보 text 표시
                    binding.chargerListStartTime.setText("충전 시작 : " + sDate);
                    binding.chargerListEndTime.setText("충전 종료 : " + eDate);

                    if (reservationModel.bleNumber.equals(mCurData.mEVZScanResult.getDevice().toString())) {
                        binding.chargerListName.setText(reservationModel.getChargerName() + " - " + temAddr.substring(temAddr.length() - 4, temAddr.length()));
                        binding.chargerListAddress.setText(reservationModel.getChargerAddress());
                    } else {
                        binding.chargerListName.setText(mCurData.mEVZScanResult.getDevice().toString());
                    }

                    //현재 충전중
                    if (reservationModel != null && reservationModel.state.equals("KEEP")) {
                        SharedPreferences pref = getSharedPreferences("SharingCharger_V2.0", MODE_PRIVATE);
                        String oldTime = pref.getString("time", "");

                        Log.e("metis", "mCurData : " + mCurData.bleName);

                        //충전기 연결 버튼 비활성화
                        searchChargerBtnEnabled();

                        rechargeId = pref.getInt("rechargeId", 0);
                        stChargingTime = oldTime;
                        if (!oldTime.equals("")) {

                            ChargerTime = preferenceUtil.getString("ChargerTime");
                            //초 계산 값 가져오기
                            sec = cu.getSecond(oldTime, ChargerTime);

                            if (sec <= 0) {
                                BLEStop();
                            } else {
                                ChargingTimerStart();
                            }

                            Log.e("metis", "diff : " + sec);
                        }

                        Log.e("metis", "reservationModel.bleNumber : " + reservationModel.bleNumber);
                        Log.e("metis", "mCurData.mEVZScanResult.getDevice().toString() : " + mCurData.mEVZScanResult.getDevice().toString());

                        /*binding.chargerRechargeIdTxt.setText(String.valueOf(rechargeId));
                        binding.chargerRechargeTxt.setVisibility(View.VISIBLE);
                        binding.chargerRechargeIdTxt.setVisibility(View.VISIBLE);*/

                        chargerFrameClick(binding.frameEnd);
                        hideLoading(binding.loading);
                    }
                    //현재 충전중 X
                    else {

                        /*new Handler().postDelayed(new Runnable() {// 0.5 초 후에 실행
                            @Override
                            public void run() {
                                // 실행할 동작 코딩

                                Message msg1 = mHandler.obtainMessage();  //사용할 핸들러를 이용해서 보낼 메시지 객체 생성
                                Bundle b1 = new Bundle();    //메시지를 담을 번들 생성
                                b1.putBoolean("getTag", true);    //번들에 메시지 추가
                                msg1.setData(b1);    //메세지에 번들을 넣는다.

                                BLEGetTag();

                                mHandler.sendMessage(msg1);     //메세지를 핸들러로 넘긴다.
                            }
                        }, 4500);*/

                        /*//다이얼로그로 추가
                        AlertDialog.Builder builder = new AlertDialog.Builder(BLEChargingActivity.this);

                        builder.setTitle("충전기 연결 성공");
                        builder.setMessage("충전기와 연결되었습니다.");

                        builder.setPositiveButton("확인", (dialog, which) -> dialog.dismiss());

                        builder.show();*/

                        new Handler().postDelayed(new Runnable() {// 2.5 초 후에 실행
                            @Override
                            public void run() {

                                createMessage("Connect");
                                BLEGetTag();

                            }
                        }, 500);
                    }

                } catch (Exception e) {
                    Log.e("metis", "BLEConnect Success Exception : " + e);
                    hideLoading(binding.loading);
                }

            }

            @Override
            public void Fail(int code, String msg) {
                Log.e("metis", "BLE Connect Fail code : " + code+" : "+ msg);
                Toast.makeText(BLEChargingActivity.this, "충전기 연결에 실패하였습니다.\n충전기 연결을 다시 시도하여 주시기 바랍니다.", Toast.LENGTH_SHORT).show();

                hideLoading(binding.loading);

                //버튼 비활성화
                disableBtn();
                timerFinish();
            }

        });
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

                            chargingTimerTx.setText(cu.remainChargingTime(sec));   //sec를 hh:mm:ss로 변환 CALL 하고 TEXT에 SET
                        } else {
                            BLEStop();
                        }

                    }
                });
            }
        };

        chargingTimer.schedule(TT, 0, 1000);                                          //Timer 실행
    }

    //BLEDisConnect
    public void BLEDisConnect() {
        mEB.BLEDisConnect(new EvzScan.BLEDisConnect() {
            @Override
            public void disConnect(int code) {

                Toast.makeText(BLEChargingActivity.this,"충전기 연결이 끊어졌습니다.",Toast.LENGTH_LONG).show();
                Log.e("metis", "BLEDisConnect Code = "+code);
                hideLoading(binding.loading);

            }
        });
    }

    //BLEStart
    public void BLEStart() {

        showLoading(binding.loading);

        mEB.BLEStart(mCurData, new EvzProtocol.BLEStart() {
            @Override
            public void Success() {

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
                        Log.e("metis", " getAuthenticateCharger ()");

                        //충전 시작 전 인증
                        boolean result = apiUtils.getAuthenticateCharger(reservationModel.chargerId, model);

                        if(result){

                            Date currentDate = new Date();
                            Date endDate = dateFormatter.parse(reservationModel.getEndDate());

                            long diff = endDate.getTime() - currentDate.getTime();
                            long minute = diff / (60 * 1000);

                            if (minute > 0) {
                                ChargerTime = String.valueOf(minute);
                                mCurData.useTime = ChargerTime;
                                preferenceUtil.putString("ChargerTime", ChargerTime);

                            }

                            chk = true;

                            //충전 시작 인증
                            rechargeId = apiUtils.startAuthenticateCharger(reservationModel.chargerId, model);

                            if(rechargeId != -1){
                                chk = true;

                                /*binding.chargerRechargeIdTxt.setText(String.valueOf(rechargeId));
                                binding.chargerRechargeTxt.setVisibility(View.VISIBLE);
                                binding.chargerRechargeIdTxt.setVisibility(View.VISIBLE);*/
                                mCurData.setTag = String.format(Locale.KOREA, "%013d", rechargeId);

                                setSharedPreferences(true);

                                Log.e("metis", "BLEStart Success");
                                BLESetTag();
                                sec = Integer.parseInt(ChargerTime) * 60;
                                ChargingTimerStart();                                               //충전진행 경과시간 START
                            }
                        }
                        Log.e("metis", "chk : " + chk);
                        if (!chk) {
                            hideLoading(binding.loading);
                            setSharedPreferences(false);
                            BLEStop();
                            Toast.makeText(BLEChargingActivity.this, "충전을 시작할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        hideLoading(binding.loading);
                        checkStart = false;
                        setSharedPreferences(false);
                        BLEStop();
                        Toast.makeText(BLEChargingActivity.this, "인증에 실패하였습니다1.", Toast.LENGTH_SHORT).show();
                    }

                    checkStart = false;
                }
            }

            @Override
            public void Fail(int code, String msg) {
                //hideLoading();
                Log.e("metis", "BLEStart Fail Code = "+code);
                Toast.makeText(BLEChargingActivity.this, "BLEStart 충전 시작에 실패하였습니다.\n다시 시도하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
                hideLoading(binding.loading);
                timerFinish();
            }

            /*@Override
            public void unPlug(int i) {
                Log.e("metis", "BLEStart unPlug");
                Toast.makeText(BLEChargingActivity.this, "충전기에 플러그가 꼽혀있지 않습니다.\n충전기에 플러그를 꼽고 다시 시도하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
                timerFinish();
            }*/

        });
    }

    public void setSharedPreferences(boolean check) {

        SharedPreferences pref = getSharedPreferences("SharingCharger_V2.0", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if (check) {
            Date now = new Date();

            Log.e("metis", "setSharedPreferences rechargeId: " + rechargeId);
            editor.putInt("rechargeId", rechargeId);
            editor.putString("time", format.format(now));
            editor.putString("rechargeStartTime", rechargeTimeFormat.format(now));
            editor.putString("activity", "BLEChargingActivity");

            stChargingTime = format.format(now);
        } else {
            editor.putInt("rechargeId", 0);
            editor.putString("time", null);
            editor.putString("rechargeStartTime", null);
            editor.putString("activity", null);
        }
        editor.commit();
    }

    //BLESetTag
    public void BLESetTag() {
        mEB.BLESetTag(mCurData, new EvzProtocol.BLESetTag() {
            @Override
            public void Success() {
                Log.e("metis", "BLESetTag Success");
                hideLoading(binding.loading);
                searchChargerBtnEnabled();
                timerFinish();
                chargerFrameClick(binding.frameEnd);

                if (preferenceUtil.getInt("rechargeId") > 0) {
                    binding.chargerListStartTime.setText("충전 시작 : " + preferenceUtil.getString("rechargeStartTime"));    
                }
            }

            @Override
            public void Fail(int code, String msg) {
                hideLoading(binding.loading);
                Log.e("metis", "BLESetTag Fail Code = "+code);
                Toast.makeText(BLEChargingActivity.this, "BLESetTag 충전기 연결에 실패하였습니다.\n충전기 연결을 다시 시도하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
                timerFinish();
            }
        });
    }

    //BLEStop
    public void BLEStop() {

        showLoading(binding.loading);

        mEB.BLEStop(mCurData, new EvzProtocol.BLEStop() {
            @Override
            public void Success() {
                Log.e("metis", "BLEStop Success");

                timerFinish();

                new Handler().postDelayed(new Runnable() {// 2.5 초 후에 실행
                    @Override
                    public void run() {

                        BLEGetTag();
                    }
                }, 1000);

                ChargingTimerStop();                                                                //충전진행 경과시간 End
            }

            @Override
            public void Fail(int code, String msg) {

                Log.e("metis", "BLEStop Fail Code = "+code);

                Toast.makeText(BLEChargingActivity.this, "충전 종료를 실패하였습니다.\n다시 시도하여 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                timerFinish();
                finish();
            }
        });
    }

    /*
     * 충전진행 경과시간 End
     */
    public void ChargingTimerStop() {

        TextView chargingTimerTx = (TextView) findViewById(R.id.charging_timer);
        chargingTimerTx.setText("00:00");
        chargingTimer.cancel();                                                                     //Timer 종료
    }

    //BLEGetTag
    public void BLEGetTag() {
        mEB.BLEGetTag(mCurData, new EvzProtocol.BLEGetTag() {
            @Override
            public void Success(ArrayList<EvzBLETagData> _data) {
                Log.e("metis", "BLEGetTag Success = " + _data.size());
                /*if (_data.size() == 0) {
                    Log.e("metis", "BLEGetTag Size == 0 ");
                } else {

                    for (int i = 0; i < _data.size(); i++) {

                        EvzBLETagData _tag = _data.get(i);

                        Log.e("metis", "BLEGetTag  Number : "+ _tag.Number +" , kWh : "+ _tag.kwh + " , useTime : "+ _tag.useTime);

                        BLEDelOneTag(_tag);
                    }
                }*/

                Log.e("metis", "BLEGetTag Success");
                if (_data.size() > 0) {

                    Log.e("metis", "BLEGetTag size : " + _data.size());

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

                            Log.e("metis", "rechargeId is " + model.rechargeId);
                            Log.e("metis", "_tag.Number is " + _tag.Number);
                            Log.e("metis", "endAuthenticateCharger reservationModel is " + reservationModel);

                            //getTagNumber == rechargeId 현재 내가 충전하고 있음 (정상종료)
                            if (_tag.Number != null && _tag.Number.equals(String.format(Locale.KOREA, "%013d", rechargeId))) {

                                rechargeEndModel = apiUtils.endAuthenticateCharger(reservationModel.chargerId, model, stChargingTime);

                                if(rechargeEndModel != null){
                                    BLEDelOneTag(_tag);
                                    setAlertDialog("Stop","충전기 종료 성공","충전기가 종료되었습니다.");
                                }
                            }
                            // 비정상 종료
                            else {

                                boolean result = apiUtils.endAuthenticateChargerUnplanned(reservationModel.chargerId, model);

                                if(result){
                                    BLEDelOneTag(_tag);
                                }
                            }

                        } catch (Exception e) {

                            Log.e("metis", "BLEGetTag Exception : " + e);
                        }

                    }

                } else {
                    Log.e("metis", "BLEGetTag size == 0");
                }
            }

            @Override
            public void Fail(int code, String msg) {
                Log.e("metis", "BLEGetTag Fail Code = "+code);
                Toast.makeText(BLEChargingActivity.this, "BLEGetTag 실패하였습니다.", Toast.LENGTH_LONG).show();
                hideLoading(binding.loading);
                timerFinish();
            }
        });
    }

    /**
     * 타이머 종료
     */
    private void timerFinish(){
        if (timer != null) {
            timer.onFinish();
            timer.cancel();
        }
    }

    /*
     * CHARGER FINNISH DIALOG SHOW
     * */
    private void showChargerFinishDialog(RechargeModel rModel) {

        Dialog dialog = new Dialog(this);

        LayoutInflater inf = getLayoutInflater();
        View dialogView = inf.inflate(R.layout.charger_finish_dialog, null);
        Button confirmButton = dialogView.findViewById(R.id.dialog_ok_btn);


        TextView tv_first_deduction_point_txt = dialogView.findViewById(R.id.first_deduction_point_txt);
        tv_first_deduction_point_txt.setText(String.valueOf(rModel.reservationPoint));                          //선차감 포인트 SET

        TextView tv_prediction_refund_point_txt = dialogView.findViewById(R.id.prediction_refund_point_txt);
        tv_prediction_refund_point_txt.setText(String.valueOf(rModel.reservationPoint-rModel.rechargePoint));   //환불 포인트 SET

        TextView tv_filling_amount_txt = dialogView.findViewById(R.id.filling_amount_txt);
        tv_filling_amount_txt.setText(String.valueOf(rModel.reservationPoint - (rModel.reservationPoint-rModel.rechargePoint)));                                       //실제 소진 포인트 SET

        TextView reservationStartTimeText = dialogView.findViewById(R.id.reservation_start_time_txt);
        reservationStartTimeText.setText(rModel.reservationStartDate);                                              //충전 시작 시간 SET

        TextView reservationEndTimeText = dialogView.findViewById(R.id.reservation_end_time_txt);
        reservationEndTimeText.setText(rModel.reservationEndDate);                                                  //충전 종료 시간 SET

        TextView tv_charg_start_time_txt = dialogView.findViewById(R.id.charg_start_time_txt);
        tv_charg_start_time_txt.setText(rModel.startRechargeDate);                                              //충전 시작 시간 SET

        TextView tv_charg_end_time_txt = dialogView.findViewById(R.id.charg_end_time_txt);
        tv_charg_end_time_txt.setText(rModel.endRechargeDate);                                                  //충전 종료 시간 SET

        TextView tv_charging_time_txt = dialogView.findViewById(R.id.charging_time_txt);
        tv_charging_time_txt.setText(rModel.chargingTime);                                                      //충전 시간 SET

        confirmButton.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.setContentView(dialogView); // Dialog에 선언했던 layout 적용
        dialog.setCancelable(false); // 외부 터치나 백키로 dimiss 시키는 것 막음

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.alert_dialog_border);
        dialog.getWindow().setLayout(cu.getPercentWidth(this, 90), WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show(); // Dialog 출력
    }

    //BLEDelOneTag
    public void BLEDelOneTag(EvzBLETagData mCurTag){
        mEB.BLEDelOneTag(mCurTag, new EvzProtocol.BLEDelOneTag() {
            @Override
            public void Success() {
                runOnUiThread(() ->  {
                    Log.e("metis", "BLEDelOneTag Success");
                    timerFinish();
                });
            }

            @Override
            public void Fail(int code, String msg) {
                runOnUiThread(() ->  {
                    Log.e("metis", "BLEDelOneTag Fail Code = " +code);
                    Toast.makeText(BLEChargingActivity.this, "BLEDelOneTag 실패하였습니다.", Toast.LENGTH_LONG).show();

                    timerFinish();
                });
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
                hideLoading(binding.loading);
                Log.e("metis", "PlugState Plug = "+ code);
            }
        });
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
