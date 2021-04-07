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
import android.widget.TextView;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.Constants;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.ActivityChargerListBinding;
import kr.co.metisinfo.sharingcharger.model.AuthenticateModel;
import kr.co.metisinfo.sharingcharger.model.ChargerModel;
import kr.co.metisinfo.sharingcharger.model.RechargeEndModel;
import kr.co.metisinfo.sharingcharger.model.RechargeModel;
import kr.co.metisinfo.sharingcharger.model.ReservationModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.CommonUtils;
import kr.co.metisinfo.sharingcharger.view.viewInterface.FragmentDialogInterface;
import retrofit2.Response;

public class MainPersonalActivity extends BaseActivity implements FragmentDialogInterface {

    private static final String TAG = MainPersonalActivity.class.getSimpleName();

    public static Activity MainPersonalActivity;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat formatT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public ActivityChargerListBinding binding;

    private ReservationModel reservationModel = null;

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    //start 함수가 두번 실행되서 임시로 한번만 실행되도록 막음
    boolean checkStart = false;

    private List<ChargerModel> chargerList = new ArrayList<>();

    GlideDrawableImageViewTarget gifImage;

    int ChargerTime = 480; // 8시간

    int chargerId = 0;

    CommonUtils cu = new CommonUtils();

    private int rechargeId = 0;

    EvzBLEScan mEBS;
    EvzBLE mEB;

    EvzBLEData mCurData;
    ArrayList<EvzBLEData> mArrayBLEData = new ArrayList<>();
    EvzBLETagData mCurTag = new EvzBLETagData();

    CountDownTimer timer;

    boolean isRecharging = false;
    boolean isConnect = false;
    boolean isStart = false;
    boolean isStop = false;

    Timer chargingTimer = new Timer();                                                              //충전 진행 경과시간을 위한 Timer 선언
    TimerTask TT;

    int sec = 0;

    //실제 시작한 시간
    String stChargingTime;

    ApiUtils apiUtils = new ApiUtils();

    //두번째 충전시 함수 성공이 두번탐....??
    //테스트 해봐야함
    boolean isGetTag = false;
    boolean isSetTag = false;
    boolean isStopTag = false;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {  // 실행이 끝난후 확인 가능
            Bundle bd = msg.getData();

            if (bd.getBoolean("Connect")) {
                checkStart = true;
                if (reservationModel == null) {
                    checkChargerState(chargerId);
                } else if (reservationModel.state.equals("RESERVE")) {
                    BLEStart(mCurData);
                } else if (reservationModel.state.equals("KEEP")) {
                    chargerFrameClick(binding.frameEnd);
                }
            } else if (bd.getBoolean("Stop")) {

                ChargingTimerStop();
                removeLoading();
                setSharedPreferences(false);
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PAGE_SEARCH_CHARGER) {
            if (resultCode == RESULT_OK) {

                mCurData = data.getParcelableExtra("mCurData");

                //선택된 충전기가 소유주의 충전기인지 확인
                Log.e(TAG, "mCurData : " + mCurData.bleAddr);

                boolean checkOwner = false;

                //선택된 bleNumber
                String tempBleAddr = mCurData.bleAddr;
                tempBleAddr = tempBleAddr.substring(tempBleAddr.length() - 4, tempBleAddr.length());
                tempBleAddr = tempBleAddr.trim();

                if (!tempBleAddr.contains(":")) {

                    for (int i = 0; i < chargerList.size(); i++) {

                        ChargerModel chargerModel = chargerList.get(i);

                        //소유자가 가지고 있는 bleNumber
                        String tempCm = chargerModel.getBleNumber();
                        tempCm = tempCm.replaceAll(":", "");
                        tempCm = tempCm.substring(tempCm.length() - 4, tempCm.length());

                        //예약 X
                        if (reservationModel == null) {

                            //선택된 충전기와 소유주의 충전기가 맞다면
                            if (tempBleAddr.equals(tempCm)) {
                                setReserveInfo(chargerModel, tempBleAddr);
                                checkOwner = true;

                                break;
                            }
                        }
                        //예약 O or 충전 중
                        else {

                            String tempRe = reservationModel.bleNumber;
                            tempRe = tempRe.replaceAll(":", "");
                            tempRe = tempRe.substring(tempRe.length() - 4, tempRe.length());
                            if (tempRe.equals(tempBleAddr)) {

                                setReserveInfo(chargerModel, tempBleAddr);
                                checkOwner = true;

                                break;

                            }
                        }
                    }
                }

                if (checkOwner) {

                    showLoading();
                    BLEConnect(mCurData);

                } else {
                    Toast.makeText(MainPersonalActivity.this, "충전기의 소유주가 아닙니다.", Toast.LENGTH_SHORT).show();
                }

            }

        }
    }

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_charger_list);

        changeStatusBarColor(false);

        MainPersonalActivity = MainPersonalActivity.this;

    }

    @Override
    public void initViewModel() {

        binding.includeHeader.txtTitle.setText(R.string.personal_use_charger);
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
        binding.customerCenterBtn.setOnClickListener(view -> {
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

            //처음 예약할때
            if (reservationModel == null) {
                isStopTag = true;
                showLoading();
                getBLEScan();
            }else if(reservationModel != null && reservationModel.state.equals("RESERVE")){
                isStopTag = true;
                getBLEScan();
            }
            //예약은 했는데 connect or start 실패 시
            else {
                //connect 실패 시
                if (!isConnect) {
                    isStopTag = true;
                    BLEConnect(mCurData);
                }
                //start 실패 시
                else if (!isStart) {
                    showLoading();
                    checkStart = true;
                    BLEStart(mCurData);
                }
            }

        });

        binding.frameEnd.setOnClickListener(view -> {
            showLoading();

            if (isRecharging) {
                getBLEScan();
            } else {
                isStopTag = true;
                BLEStop(mCurData);

                isStart = false;
                isStop = false;
            }
        });
    }

    @Override
    public void init() {

        mEBS = new EvzBLEScan(MainPersonalActivity.this);
        mEB = new EvzBLE(MainPersonalActivity.this);

        //
        /*
        * 1. 충전중인지 아닌지 확인
        * 2. 소유주가 충전기를 가지고있는지 확인
        * */

        disableBtn();


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

                for (int i = 0; i < mArrayBLEData.size(); i++) {
                    EvzBLEData getData = mArrayBLEData.get(i);

                    for (int j = 0; j < chargerList.size(); j++) {

                        ChargerModel model = chargerList.get(j);

                        if (getData.bleAddr.equals(model.bleNumber)) {

                            String temAddr = getData.bleName;
                            mArrayBLEData.get(i).bleAddr = model.name + " - " + temAddr.substring(temAddr.length() - 4, temAddr.length());
                        }
                    }
                }

                Intent intent = new Intent(MainPersonalActivity.this, ChargerDialog.class);

                intent.putParcelableArrayListExtra("mArrayBLEData", mArrayBLEData);

                startActivityForResult(intent, Constants.PAGE_SEARCH_CHARGER);

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle(R.string.bluetooth_find_device);
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

            binding.frameStart.setBackground(ContextCompat.getDrawable(MainPersonalActivity.this, R.drawable.border_blue_50));
            binding.frameStart.setEnabled(true);

            binding.frameEnd.setBackground(ContextCompat.getDrawable(MainPersonalActivity.this, R.drawable.border_gray_50));
            binding.frameEnd.setEnabled(false);

        } else if (view.getId() == R.id.frame_end) {

            binding.frameStart.setBackground(ContextCompat.getDrawable(MainPersonalActivity.this, R.drawable.border_gray_50));
            binding.frameStart.setEnabled(false);

            binding.frameEnd.setBackground(ContextCompat.getDrawable(MainPersonalActivity.this, R.drawable.border_red_50));
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

    //시간 30분 00
    private String setTime(Calendar cal) {

        String getDate = "";

        int getMinute = cal.get(Calendar.MINUTE);

        if (getMinute != 0 && getMinute != 30) {
            Log.e(TAG, "setTime getMinute:" + getMinute);
            if (getMinute < 30) {
                cal.add(Calendar.MINUTE, 30 - getMinute);
            } else {
                cal.add(Calendar.MINUTE, 60 - getMinute);
            }

        }

        getDate = format.format(cal.getTime());
        Log.e(TAG, "setTime getMinute:" + getMinute);

        return getDate;
    }

    //충전기가 사용가능한지 확인
    private void checkChargerState(int chargerId) {

        showLoading();

        removeLoading();

        //예약하기
    }

    /*
     * CHARGER FINNISH DIALOG SHOW
     * */
    private void showChargerFinishDialog(RechargeModel rModel) {

        ChargerFinishDialog cfd = new ChargerFinishDialog(this, rModel, this);
        cfd.setCancelable(false);                                                                   //DIALOG BACKGROUND CLICK FALSE
        cfd.show();

        //확인버튼 클릭시 dismiss 수정
        cfd.findViewById(R.id.dialog_ok_btn).setOnClickListener(view -> {
            cfd.dismiss();

        });

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

                            try {

                                RechargeEndModel model = new RechargeEndModel();

                                model.rechargeId = Integer.parseInt(_tag.Number);
                                model.rechargeMinute = Integer.parseInt(_tag.useTime);
                                model.rechargeKwh = Double.valueOf(_tag.kwh);

                                Log.e(TAG, "setTag is " + mCurData.setTag);
                                Log.e(TAG, "_tag.Number is " + _tag.Number);
                                Log.e(TAG, "rechargeId is " + model.rechargeId);
                                Log.e(TAG, "chargerId " + chargerId);

                                //종료 api

                            } catch (Exception e) {

                                Log.e(TAG, "getTag Exception : " + e);
                            }
                        }
                    } else {
                        Log.e(TAG, "BLEGetTag size == 0");
                    }
                }
            }

            @Override
            public void Fail(int code, String s) {

                Log.e(TAG, "BLEGetTag Fail Code : " + code);
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

                if (isStop) {

                    BLEUserDis();
                    mCurTag = new EvzBLETagData();
                    mCurData = new EvzBLEData();
                    isStop = false;
                }
            }

            @Override
            public void Fail(int code, String msg) {

                Log.e(TAG, "BLEDelOneTag Fail code : " + code);
                Toast.makeText(MainPersonalActivity.this, "BLEDelOneTag 충전기 연결에 실패하였습니다.\n충전기 연결을 다시 시도하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
                timerFinish();
            }
        });
    }

    public void BLEConnect(EvzBLEData mCurData) {

        Log.e(TAG, "BLEConnect Call()");

        mEB.BLEConnect(mCurData, new EvzScan.BLEConnect() {

            @Override
            public void Success() {
                Log.e(TAG, "BLE Connect Success");

                isConnect = true;

                if (isRecharging && !isStopTag) {
                    isStopTag = true;
                    BLEStop(mCurData);

                } else {
                    isGetTag = true;
                    getTag();

                    try {

                        new Handler().postDelayed(new Runnable() {// 0.5 초 후에 실행
                            @Override
                            public void run() {

                                Message msg1 = mHandler.obtainMessage();  //사용할 핸들러를 이용해서 보낼 메시지 객체 생성
                                Bundle b1 = new Bundle();    //메시지를 담을 번들 생성
                                b1.putBoolean("Connect", true);    //번들에 메시지 추가
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

                Toast.makeText(MainPersonalActivity.this, "BLEConnect 충전기 연결에 실패하였습니다.\n충전기 연결을 다시 시도하여 주시기 바랍니다.", Toast.LENGTH_SHORT).show();

                timerFinish();
                removeLoading();
                Log.e(TAG, "msg : " + msg);
                Log.e(TAG, "msg = " + msg);
            }
        });

    }

    public void BLEStop(EvzBLEData mCurData) {

        Log.e(TAG, "BLEStop Call()");
        //Stop
        mEB.BLEStop(mCurData, new EvzProtocol.BLEStop() {
            @Override
            public void Success() {

                Log.e(TAG, "BLEStop Success");

                if (isStopTag) {

                    isStopTag = false;

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

                Toast.makeText(MainPersonalActivity.this, "BLEStop 충전 종료를 실패하였습니다.\n다시 시도하여 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                timerFinish();

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
                boolean chk = false;

                isStart = true;

                if (checkStart) {

                    checkStart = false;

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    Log.e(TAG, " reservationModel : " + reservationModel);
                    AuthenticateModel model = new AuthenticateModel();
                    model.rechargeStartDate = formatT.format(cal.getTime());
                    model.reservationId = reservationModel.id;
                    model.userId = reservationModel.userId;


                        //충전 인증 api


                    checkStart = false;
                    removeLoading();
                }
            }

            @Override
            public void Fail(int code, String msg) {
                removeLoading();
                isStart = false;
                Log.e(TAG, "BLEStart Fail code : " + code + " : " + msg);
                Toast.makeText(MainPersonalActivity.this, "BLEStart 충전 시작에 실패하였습니다.\n다시 시도하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
                timerFinish();
            }

            @Override
            public void unPlug(int i) {
                removeLoading();
                Log.e(TAG, "BLEStart unPlug");
                Toast.makeText(MainPersonalActivity.this, "충전기에 플러그가 꼽혀있지 않습니다.\n충전기에 플러그를 꼽고 다시 시도하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
                timerFinish();
            }
        });
    }

    public void BLESetTag() {

        mEB.BLESetTag(mCurData, new EvzProtocol.BLESetTag() {
            @Override
            public void Success() {
                Log.e(TAG, "BLESetTag Success");

                if (isSetTag) {

                    isSetTag = false;
                    sec = ChargerTime * 60;
                    ChargingTimerStart();
                    timerFinish();

                    chargerFrameClick(binding.frameEnd);
                }
            }

            @Override
            public void Fail(int code, String msg) {
                removeLoading();
                Log.e(TAG, "BLESetTag Fail code : " + code);

                Toast.makeText(MainPersonalActivity.this, "BLESetTag 충전기 연결에 실패하였습니다.\n충전기 연결을 다시 시도하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
                timerFinish();
            }
        });

    }

    public void BLEUserDis() {

        mEB.BLEUserDis(new EvzProtocol.BLEUserDis() {
            @Override
            public void Success() {

                //Evz 초기화
                mEBS = new EvzBLEScan(MainPersonalActivity.this);
                mEB = new EvzBLE(MainPersonalActivity.this);
                Log.e("TAG", "BLEUserDis Success");

                reservationModel = null;
            }

            @Override
            public void Fail(int i, String s) {

                Log.e("TAG", "BLEUserDis Fail");
            }
        });
    }

    /*
     * 충전진행 경과시간 START
     */
    public void ChargingTimerStart() {

        Log.e(TAG, "ChargingTimerStart()");

        TextView chargingTimerTx = (TextView) findViewById(R.id.charging_timer);

        TT = new TimerTask() {

            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {                         //ui쪽에서는 runOnUiTread로 돌려야함.

                        if (sec != 0) {
                            sec--;
                            chargingTimerTx.setText(cu.chargingTime(sec));   //sec를 hh:mm:ss로 변환 CALL 하고 TEXT에 SET
                        } else {
                            ChargingTimerStop();
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

        if (TT != null) {
            TT.cancel();
        }

        TextView chargingTimerTx = (TextView) findViewById(R.id.charging_timer);
        chargingTimerTx.setText("00:00:00");
        chargingTimer.cancel();                                                                     //Timer 종료

        chargingTimer = new Timer();

    }

    //예약 정보 세팅
    public void setReserveInfo(ChargerModel chargerModel, String tempBleAddr) {

        chargerId = chargerModel.id;
        mCurData.bleAddr = chargerModel.getBleNumber();

        binding.chargerListName.setText(chargerModel.getName() + " - " + tempBleAddr);
        binding.chargerListAddress.setText(chargerModel.getAddress());

    }

    //예약 상세 정보 세팅
    public void setReservationDetailInfo(){

        String sDate = cu.setDateFormat(reservationModel.getStartDate());
        String eDate = cu.setDateFormat(reservationModel.getEndDate());

        binding.chargerListStartTime.setText("충전 시작 : " + sDate);
        binding.chargerListEndTime.setText("충전 종료 : " + eDate);

        binding.chargerRechargeIdTxt.setText(String.valueOf(rechargeId));
        binding.chargerRechargeTxt.setVisibility(View.VISIBLE);
        binding.chargerRechargeIdTxt.setVisibility(View.VISIBLE);

    }

    public void setSharedPreferences(boolean check) {

        try {
            SharedPreferences pref = getSharedPreferences("reservation", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            if (check) {

                Date now = new Date();
                Log.e(TAG, "setSharedPreferences rechargeId: " + rechargeId);
                editor.putInt("rechargeId", rechargeId);
                editor.putString("time", format.format(now));
                editor.putString("activity", "MainPersonalActivity");

                //충전 시작하고 소유주가 충전기본시간을 변경할 수 있기 때문에 현재 충전시간을 저장함
                editor.putString("reservationTime", String.valueOf(ChargerTime));

                stChargingTime = format.format(now);

            } else {
                editor.putInt("rechargeId", 0);
                editor.putString("time", null);
                editor.putString("activity", null);
                editor.putString("reservationTime", null);

                isRecharging = false;
                chargerFrameClick(binding.frameStart);
            }

            editor.commit();
        } catch (Exception e) {
            Log.e(TAG, "setSharedPreferences : " + e);
        }
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

    /**
     * 타이머 종료
     */
    private void timerFinish() {
        if (timer != null) {
            timer.onFinish();
            timer.cancel();
        }
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
        chargingTimer.cancel();

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
