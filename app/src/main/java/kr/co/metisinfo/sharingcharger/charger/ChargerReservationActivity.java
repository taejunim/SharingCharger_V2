package kr.co.metisinfo.sharingcharger.charger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import java.text.NumberFormat;
import java.util.Locale;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.Constants;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.ActivityReservationProgressBinding;
import kr.co.metisinfo.sharingcharger.model.ReservationModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.view.activity.PointChargeActivity;

import static kr.co.metisinfo.sharingcharger.base.Constants.PAGE_POINT_CHARGE;

public class ChargerReservationActivity extends BaseActivity {

    private static final String TAG = ChargerReservationActivity.class.getSimpleName();

    ActivityReservationProgressBinding binding;

    // 시작, 종료 시간
    private String sDate = "";
    private String eDate = "";

    // 예상 포인트
    private int ePoint = 0;

    // 현재 포인트
    private int cPoint = 0;

    private boolean isReservationBtnClick = false;     // 버튼 더블클릭 막기 위한 boolean 타입 변수

    ApiUtils apiUtils = new ApiUtils();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case Constants.PAGE_POINT_CHARGE:

                if (resultCode == RESULT_OK) {

                    try{

                        cPoint = apiUtils.getUserPoint();

                        binding.reservationProgressCurrentPoint.setText(NumberFormat.getInstance(Locale.KOREA).format(cPoint) + "p");

                        int chkPoint = cPoint - ePoint;
                        binding.reservationProgressPoint.setText(NumberFormat.getInstance(Locale.KOREA).format(chkPoint));

                        //예약가능
                        if (chkPoint >= 0) {
                            setPossibility();
                        }
                        //예약불가능
                        else {
                            setImpossibility();
                        }

                    }catch (Exception e){
                        Log.e("metis", "onActivityResult Exception : " + e);
                    }
                }

                break;
        }
    }

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_reservation_progress);

        changeStatusBarColor(false);

    }

    @Override
    public void initViewModel() {

    }

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnBack.setOnClickListener(view -> finish());

        binding.reservationProgressBtn.setOnClickListener(view -> {
            if (!isReservationBtnClick) {

                isReservationBtnClick = true;
                goReservation();
            }

        });

        //포인트 충전
        binding.reservationProgressPointBtn.setOnClickListener(view -> {

            //포인트 충전 화면이동
            Intent intent = new Intent(this, PointChargeActivity.class);
            startActivityForResult(intent, PAGE_POINT_CHARGE);
        });

    }

    public void setPossibility() {

        binding.reservationProgressPointBtn.setVisibility(View.INVISIBLE);
        binding.reservationProgressCurrentPoint.setBackgroundResource(R.drawable.border_blue_30);
        binding.reservationProgressPoint.setTextColor(ContextCompat.getColor(this, R.color.blue_button));

        binding.reservationProgressBtn.setBackground(ContextCompat.getDrawable(this, R.color.blue_button));
        binding.reservationProgressBtn.setEnabled(true);
    }

    public void setImpossibility() {

        binding.reservationProgressPointBtn.setVisibility(View.VISIBLE);
        binding.reservationProgressCurrentPoint.setBackgroundResource(R.drawable.border_red_30);
        binding.reservationProgressPoint.setTextColor(ContextCompat.getColor(this, R.color.red));

        binding.reservationProgressBtn.setBackground(ContextCompat.getDrawable(this, R.color.whitegray));
        binding.reservationProgressBtn.setEnabled(false);
    }

    @Override
    public void init() {

        binding.includeHeader.txtTitle.setText(R.string.reservation_progress_title);
        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);

        String possibility = getIntent().getStringExtra("reservation");

        //예약 진행 가능
        if (Boolean.parseBoolean(possibility)) {
            setPossibility();
        }
        //잔액부족
        else {
            setImpossibility();
        }

        binding.reservationProgressPlaceName.setText(getIntent().getStringExtra("chargerName"));

        sDate = getIntent().getStringExtra("reservationSDate");
        eDate = getIntent().getStringExtra("reservationEDate");

        String tempDate1 = sDate.replace('T', ' ');

        String tempDate2 = eDate.replace('T', ' ');

        String setTime = tempDate1.substring(0, tempDate1.length() - 3) + " ~ " + tempDate2.substring(0, tempDate2.length() - 3);

        binding.reservationProgressTime.setText(setTime);

        cPoint = getIntent().getIntExtra("currentPoint", 0);
        ePoint = getIntent().getIntExtra("expectPoint", 0);

        binding.reservationProgressCurrentPoint.setText(NumberFormat.getInstance(Locale.KOREA).format(cPoint) + "p");
        binding.reservationPointDeductionTxt.setText(NumberFormat.getInstance(Locale.KOREA).format(ePoint));

        binding.reservationProgressPoint.setText(NumberFormat.getInstance(Locale.KOREA).format(cPoint - ePoint));
    }

    private void goReservation() {

        // 예약 API
        int chargerId = getIntent().getIntExtra("chargerId", -1);

        ReservationModel reservationModel = new ReservationModel();
        reservationModel.startDate = sDate;
        reservationModel.endDate = eDate;
        reservationModel.reservationType = "RESERVE";
        reservationModel.chargerId = chargerId;
        reservationModel.userId = ThisApplication.staticUserModel.getId();
        reservationModel.expectPoint = ePoint;

        try {

            ReservationModel model = apiUtils.goReservation(reservationModel);

            //예약 성공
            if(model != null){

                if (ThisApplication.staticUserModel.getUserType().equals("Personal")) {
                    SharedPreferences pref = getSharedPreferences("reservation", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("activity", "BLEChargingActivity");
                    editor.commit();
                }

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);

                finish();

            }
            //예약 실패
            else{

                Toast.makeText(ChargerReservationActivity.this, "예약에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                isReservationBtnClick = false;
                Log.e(TAG, "예약에 실패하였습니다.");

            }

        } catch (Exception e) {
            isReservationBtnClick = false;
            Log.e(TAG, "goReservation Exception : " + e);
        }
    }
}
