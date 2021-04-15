package kr.co.metisinfo.sharingcharger.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityChargingBinding;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;

public class ChargingActivity extends BaseActivity {

    private static final String TAG = ChargingActivity.class.getSimpleName();

    ActivityChargingBinding binding;

    ApiUtils apiUtils = new ApiUtils();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {


        }
    }

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_charging);

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

            chargerFrameClick(binding.frameEnd);
        });

        // 충전 완료 버튼
        binding.frameEnd.setOnClickListener(view -> {

            chargerFrameClick(binding.frameStart);
        });

        //충전기 연결 버튼
        binding.searchChargerBtn.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("충전기 검색");
            builder.setMessage("충전기를 다시 검색하시겠습니까?");

            builder.setPositiveButton("확인", (dialog, which) -> {

                Intent intent = new Intent(this, SearchChargerActivity.class);

                startActivity(intent);

                finish();
            });

            builder.setNegativeButton("취소", (dialog, which) -> {
            });
            builder.show();
        });
    }

    @Override
    public void init() {

        binding.includeHeader.txtTitle.setText("충전");

        //사용자 화면에서는 필요없음, 소유주 화면 같이 사용함
        binding.listTextDummy2.setVisibility(View.INVISIBLE);
        binding.customerCenterBtn.setVisibility(View.INVISIBLE);

        chargerFrameClick(binding.frameStart);

    }

    //충전시작, 충전완료 버튼
    public void chargerFrameClick(View view) {

        if (view.getId() == R.id.frame_start) {

            binding.frameStart.setBackground(ContextCompat.getDrawable(ChargingActivity.this, R.drawable.border_blue_50));
            binding.frameStart.setEnabled(true);

            binding.frameEnd.setBackground(ContextCompat.getDrawable(ChargingActivity.this, R.drawable.border_gray_50));
            binding.frameEnd.setEnabled(false);

        } else if (view.getId() == R.id.frame_end) {

            binding.frameStart.setBackground(ContextCompat.getDrawable(ChargingActivity.this, R.drawable.border_gray_50));
            binding.frameStart.setEnabled(false);

            binding.frameEnd.setBackground(ContextCompat.getDrawable(ChargingActivity.this, R.drawable.border_red_50));
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

    /**
     * 충전기 연결 버튼 비활성화
     */
    private void searchChargerBtnEnabled(){

        binding.searchChargerBtn.setClickable(false);
        binding.searchChargerBtn.setBackground(ContextCompat.getDrawable(ChargingActivity.this, R.drawable.button_border_gray));
        binding.searchChargerBtn.setEnabled(false);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

}
