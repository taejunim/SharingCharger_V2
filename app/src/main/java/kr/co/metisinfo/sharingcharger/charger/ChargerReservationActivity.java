package kr.co.metisinfo.sharingcharger.charger;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.Constants;
import kr.co.metisinfo.sharingcharger.databinding.ActivityReservationProgressBinding;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;

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
                        //포인트 충전후 포인트 확인

                    }catch (Exception e){
                        Log.e(TAG, "onActivityResult Exception : " + e);
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

//        String tempDate1 = sDate.replace('T', ' ');
//
//        String tempDate2 = eDate.replace('T', ' ');
//
//        String setTime = tempDate1.substring(0, tempDate1.length() - 3) + " ~ " + tempDate2.substring(0, tempDate2.length() - 3);
//
//        binding.reservationProgressTime.setText(setTime);
//
//        cPoint = getIntent().getIntExtra("currentPoint", 0);
//        ePoint = getIntent().getIntExtra("expectPoint", 0);
//
//        binding.reservationProgressCurrentPoint.setText(NumberFormat.getInstance(Locale.KOREA).format(cPoint) + "p");
//        binding.reservationPointDeductionTxt.setText(NumberFormat.getInstance(Locale.KOREA).format(ePoint));
//
//        binding.reservationProgressPoint.setText(NumberFormat.getInstance(Locale.KOREA).format(cPoint - ePoint));

    }

    private void goReservation() {

        // 예약 API
        int chargerId = getIntent().getIntExtra("chargerId", -1);


    }
}
