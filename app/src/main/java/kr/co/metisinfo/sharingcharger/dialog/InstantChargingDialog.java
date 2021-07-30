package kr.co.metisinfo.sharingcharger.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.charger.ChargerSearchActivity;
import kr.co.metisinfo.sharingcharger.model.ReservationModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;

import static android.content.Context.MODE_PRIVATE;

/**
 * @ Class Name   : InstantChargingDialog.java
 * @ Modification : INSTANT CHARGING DIALOG CLASS.
 * @
 * @ 최초 생성일      최초 생성자
 * @ ---------     --------
 * @ 2020.12.24.    고재훈
 * @
 * @  수정일          수정자
 * @ ---------    ---------
 * @
 **/
public class InstantChargingDialog extends Dialog  {

    Context context;

    private String chargingName     = "";
    private String chargeTime       = "";
    private String reservationTime  = "";

    private ReservationModel reservationModel;

    private static final String TAG = InstantChargingDialog.class.getSimpleName();

    ApiUtils apiUtils = new ApiUtils();

    public InstantChargingDialog(@NonNull Context context, String chargingName, String chargeTime, ReservationModel reservationModel, String reservationTime) {
        super(context);
        this.context          = context;
        this.chargingName     = chargingName;                                                       //충전기 명
        this.chargeTime       = chargeTime;                                                         //충전 시간
        this.reservationModel = reservationModel;                                                   //예약 모델
        this.reservationTime  = reservationTime;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instant_charging_dialog);

        TextView tv_charger_nm_txt = findViewById(R.id.charger_nm_txt);
        tv_charger_nm_txt.setText(String.valueOf(chargingName));                                    //충전기 명 SET

        TextView tv_charging_time_txt = findViewById(R.id.charging_time_txt);
        tv_charging_time_txt.setText(String.valueOf(chargeTime));                                   //충전 시간 SET

        findViewById(R.id.instant_charging_ok_btn).setOnClickListener(view -> goChargingOkBtn());    //충전
        findViewById(R.id.instant_charging_no_btn).setOnClickListener(view -> this.dismiss());      //취소
    }

    //충전 버튼 CLICK.
    public void goChargingOkBtn(){

       goReservation();

    }

    private void goReservation() {

        // reservation api
        try {

            ReservationModel model = apiUtils.goReservation(reservationModel);

            //예약 성공
            if(model != null){

                if (ThisApplication.staticUserModel.getUserType().equals("Personal")) {
                    SharedPreferences pref = context.getSharedPreferences("reservation", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("activity", "ChargerListActivity");
                    editor.commit();
                }

                Intent intent = new Intent(context, ChargerSearchActivity.class);
                intent.putExtra("reservationModel", model);
                intent.putExtra("reservationTime", reservationTime);

                context.startActivity(intent);
                Log.e("metis", "예약 성공.");
            }

            //예약 실패
            else{
                Toast.makeText(context, "예약에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                Log.e("metis", "예약에 실패하였습니다.");
            }

            this.dismiss();
        } catch (Exception e) {
            Log.e("metis", "Exception : " + e);
            this.dismiss();
        }

    }

}
