package kr.co.metisinfo.sharingcharger.view.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import java.text.NumberFormat;
import java.util.Locale;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.ActivityPointChargeBinding;
import kr.co.metisinfo.sharingcharger.model.PointModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;

public class PointChargeActivity extends BaseActivity {

    private static final String TAG = PointChargeActivity.class.getSimpleName();

    ActivityPointChargeBinding binding;

    private boolean isPointBtnClick = false;     // 버튼 더블클릭 막기 위한 boolean 타입 변수

    ApiUtils apiUtils = new ApiUtils();

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_point_charge);

        changeStatusBarColor(false);

    }

    @Override
    public void initViewModel() {


    }

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnBack.setOnClickListener(view -> finish());

        binding.pointChargeCancel.setOnClickListener(view -> finish());

        binding.pointChargeOk.setOnClickListener(view -> {

            if (!isPointBtnClick) {

                isPointBtnClick = true;

                pointCharge();
            }

        });

    }

    @Override
    public void init() {

        binding.includeHeader.txtTitle.setText("포인트 충전");
        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);

        getCurrentPoint();
    }

    private void pointCharge() {

        if (Integer.parseInt(binding.chargingPointEdit.getText().toString()) < 1000) {
            Toast.makeText(this, "1000포인트 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
            isPointBtnClick = false;
        } else if (binding.pointCardEd1.getText().toString().trim().length() != 4) {
            Toast.makeText(this, "카드번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            binding.pointCardEd1.requestFocus();
            isPointBtnClick = false;
        } else if (binding.pointCardEd2.getText().toString().trim().length() != 4) {
            Toast.makeText(this, "카드번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            binding.pointCardEd2.requestFocus();
            isPointBtnClick = false;
        } else if (binding.pointCardEd3.getText().toString().trim().length() != 4) {
            Toast.makeText(this, "카드번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            binding.pointCardEd3.requestFocus();
            isPointBtnClick = false;
        } else if (binding.pointCardEd4.getText().toString().trim().length() != 4) {
            Toast.makeText(this, "카드번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            binding.pointCardEd4.requestFocus();
            isPointBtnClick = false;
        } else if (binding.pointTerm1.getText().toString().trim().length() != 2) {
            Toast.makeText(this, "유효기간을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            binding.pointTerm1.requestFocus();
            isPointBtnClick = false;
        } else if (binding.pointTerm2.getText().toString().trim().length() != 2) {
            Toast.makeText(this, "유효기간을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            binding.pointTerm2.requestFocus();
            isPointBtnClick = false;
        } else if (binding.pointPw.getText().toString().trim().length() != 2) {
            Toast.makeText(this, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            binding.pointPw.requestFocus();
            isPointBtnClick = false;
        }

        if (isPointBtnClick) {

            PointModel pointModel = new PointModel();
            pointModel.point = Integer.parseInt(binding.chargingPointEdit.getText().toString());
            pointModel.pointUsedType = "PURCHASE";
            pointModel.userId = ThisApplication.staticUserModel.getId();

            try {

                boolean insertPoint = apiUtils.insertPoint(pointModel);

                if(insertPoint){
                    int tempPoint = Integer.parseInt(binding.chargingPointEdit.getText().toString());

                    Toast.makeText(this, NumberFormat.getInstance(Locale.KOREA).format(tempPoint) + " 포인트가 충전 되었습니다.", Toast.LENGTH_SHORT).show();
                    //금액
                    Intent intent = new Intent();

                    setResult(RESULT_OK, intent);

                    finish();

                }else {
                    Toast.makeText(this, "충전에 실패하였습니다.", Toast.LENGTH_SHORT).show();

                }

            } catch (Exception e) {
                Log.e(TAG, "Exception : " + e);
            }

        }

        Log.e(TAG, "금액 : " + binding.chargingPointEdit.getText().toString());

    }


    private void getCurrentPoint() {

        //실시간 포인트 가져오기
        try {

            int point = apiUtils.getUserPoint();

            binding.pointChargeCurrentPoint.setText(NumberFormat.getInstance(Locale.KOREA).format(point));

        } catch (Exception e) {
            Log.e(TAG, "pointDialog Exception : " + e);
        }

    }
}
