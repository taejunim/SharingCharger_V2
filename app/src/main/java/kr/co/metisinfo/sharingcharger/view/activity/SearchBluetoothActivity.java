package kr.co.metisinfo.sharingcharger.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.charzin.evzsdk.EvzBLEData;
import com.charzin.evzsdk.EvzBLEScan;

import java.util.ArrayList;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.Constants;
import kr.co.metisinfo.sharingcharger.databinding.ActivitySearchBluetoothBinding;
import kr.co.metisinfo.sharingcharger.model.ReservationModel;

public class SearchBluetoothActivity extends BaseActivity {

    private static final String TAG = SearchBluetoothActivity.class.getSimpleName();

    ActivitySearchBluetoothBinding binding;

    EvzBLEScan mEBS;
    ArrayList<EvzBLEData> mArrayBLEData = new ArrayList<>();
    GlideDrawableImageViewTarget gifImage;

    ReservationModel reservationModel;

    String reservationTime;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PAGE_SEARCH_CHARGER) {
            if (resultCode == RESULT_OK) {

                assert data != null;
                passingMCurData(data.getParcelableExtra("mCurData"));

            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Glide.with(this).onDestroy();

                viewEnable(true);

            }
        }
    }

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_bluetooth);

        changeStatusBarColor(false);

        binding.includeHeader.txtTitle.setText("충전기 검색");
        binding.includeHeader.btnBack.setVisibility(View.INVISIBLE);
        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);

        reservationModel = (ReservationModel) getIntent().getSerializableExtra("reservationModel");

        reservationTime = getIntent().getStringExtra("reservationTime");

    }

    @Override
    public void initViewModel() {

    }

    @Override
    public void setOnClickListener() {

        binding.btnSearchBluetooth.setOnClickListener(view -> {

            showLoading();
            getBLEScan();

        });
    }

    @Override
    public void init() {

        mEBS = new EvzBLEScan(SearchBluetoothActivity.this);

    }

    public void viewEnable(boolean enable) {

        binding.btnSearchBluetooth.setEnabled(enable);
    }

    public void getBLEScan() {

        mEBS.BLEScan(list -> {

            mArrayBLEData = list;
            EvzBLEData model = null;

            // 예약된 bleNumber와 같으면 충전화면 바로 이동
            if (mArrayBLEData.size() != 0) {

                for (int i = 0; i < mArrayBLEData.size(); i++) {

                    if(reservationModel.bleNumber.equals(mArrayBLEData.get(i).bleAddr)){
                        model = mArrayBLEData.get(i);
                        break;
                    }
                }
            }

            mArrayBLEData.clear();
            if(model != null){
                mArrayBLEData.add(model);
            }

            if(mArrayBLEData.size() == 1){

                passingMCurData(mArrayBLEData.get(0));

            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("충전기 검색");
                builder.setMessage("연결 가능한 충전기를 찾지 못했습니다.\n다시 검색하시겠습니까?");

                builder.setPositiveButton("확인", (dialog, which) -> getBLEScan());

                builder.setNegativeButton("취소", (dialog, which) -> finish());
                builder.show();
            }

        });

    }

    private void showLoading() {

        binding.imageLoading.setVisibility(View.VISIBLE);
        gifImage = new GlideDrawableImageViewTarget(binding.imageLoading);
        Glide.with(this).load(R.drawable.spinner_loading).into(gifImage);

        //해당페이지 이벤트 막기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        viewEnable(false);

    }

    private void passingMCurData(EvzBLEData mCurData) {

        Intent intent = new Intent(SearchBluetoothActivity.this, ChargerListActivity.class);

        intent.putExtra("mCurData", mCurData);

        intent.putExtra("reservationModel", reservationModel);

        intent.putExtra("reservationTime", reservationTime);

        startActivity(intent);

        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
