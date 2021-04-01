package kr.co.metisinfo.sharingcharger.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.NumberFormat;
import java.util.Locale;

import kr.co.metisinfo.sharingcharger.R;

/**
 * @ Class Name   : PointChargingDialog.java
 * @ Modification : POINT CHARGING DIALOG CLASS.
 * @
 * @ 최초 생성일      최초 생성자
 * @ ---------     --------
 * @ 2021.01.04.    고재훈
 * @
 * @  수정일          수정자
 * @ ---------    ---------
 * @
 **/
public class PointChargingDialog extends Dialog  {

    Context context;

    int getPoint = 0;

    public PointChargingDialog(@NonNull Context context, int getPoint) {
        super(context);
        this.context          = context;
        this.getPoint = getPoint;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_charging_dialog);

        TextView textView =  findViewById(R.id.point_charging_value);

        textView.setText("잔여 포인트 : "+ NumberFormat.getInstance(Locale.KOREA).format(getPoint)+"p");

        findViewById(R.id.point_charging_ok_btn).setOnClickListener(view -> goPointOkBtn());        //포인트 충전
        findViewById(R.id.point_charging_no_btn).setOnClickListener(view -> this.dismiss());        //취소
    }

    //포인트 충전 버튼 CLICK.
    public void goPointOkBtn(){

        Intent intent = new Intent(context, PointChargeActivity.class);
        context.startActivity(intent);
        this.dismiss();
    }

}
