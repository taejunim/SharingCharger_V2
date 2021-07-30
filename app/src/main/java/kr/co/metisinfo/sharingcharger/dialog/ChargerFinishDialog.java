package kr.co.metisinfo.sharingcharger.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.model.RechargeModel;
import kr.co.metisinfo.sharingcharger.view.viewInterface.FragmentDialogInterface;

/**
 * @ Class Name   : ChargerFinishDialog.java
 * @ Modification : CHARGER FINISH DIALOG CLASS.
 * @
 * @ 최초 생성일      최초 생성자
 * @ ---------     --------
 * @ 2020.12.23.    고재훈
 * @
 * @  수정일          수정자
 * @ ---------    ---------
 * @
 **/
public class ChargerFinishDialog extends Dialog  {

    Context context;
    RechargeModel rModel;
    private static final String TAG = ChargerFinishDialog.class.getSimpleName();

    FragmentDialogInterface fdi;

    public String getType = "";


    public ChargerFinishDialog(@NonNull Context context, RechargeModel rModel, FragmentDialogInterface fdi) {
        super(context);
        this.context = context;
        this.rModel  = rModel;                                                                                  //충전 관련 MODEL 생성자
        this.fdi     = fdi;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.charger_finish_dialog);

        TextView tv_first_deduction_point_txt = findViewById(R.id.first_deduction_point_txt);
        tv_first_deduction_point_txt.setText(String.valueOf(rModel.reservationPoint));                          //선차감 포인트 SET

        TextView tv_prediction_refund_point_txt = findViewById(R.id.prediction_refund_point_txt);
        tv_prediction_refund_point_txt.setText(String.valueOf(rModel.reservationPoint-rModel.rechargePoint));   //환불 포인트 SET

        TextView tv_filling_amount_txt = findViewById(R.id.filling_amount_txt);
        tv_filling_amount_txt.setText(String.valueOf(rModel.reservationPoint - (rModel.reservationPoint-rModel.rechargePoint)));                                       //실제 소진 포인트 SET

        TextView tv_charg_start_time_txt = findViewById(R.id.charg_start_time_txt);
        tv_charg_start_time_txt.setText(rModel.startRechargeDate);                                              //충전 시작 시간 SET

        TextView tv_charg_end_time_txt = findViewById(R.id.charg_end_time_txt);
        tv_charg_end_time_txt.setText(rModel.endRechargeDate);                                                  //충전 종료 시간 SET

        TextView tv_charging_time_txt = findViewById(R.id.charging_time_txt);
        tv_charging_time_txt.setText(rModel.chargingTime);                                                      //충전 시간 SET

        //불러오는 화면이 main일 경우 실제소진포인트화면은 안보이게 수정함
        if(getType.equals("Main")){

            findViewById(R.id.filling_amount).setVisibility(View.GONE);
            findViewById(R.id.filling_amount_txt).setVisibility(View.GONE);
            findViewById(R.id.filling_amount_kwh).setVisibility(View.GONE);

        }

        findViewById(R.id.dialog_ok_btn).setOnClickListener(view -> dialogFinish(fdi));
    }

    /*
    * DIALOG FINISH
    *
    * */
    public void dialogFinish(FragmentDialogInterface fdi){

        this.dismiss();
        fdi.btnClick(true);
    }

}
