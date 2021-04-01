package kr.co.metisinfo.sharingcharger.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.charzin.evzsdk.EvzBLEData;
import com.charzin.evzsdk.EvzBLEScan;

import java.util.ArrayList;

import kr.co.metisinfo.sharingcharger.Adapter.ItemChargerListRecyclerViewAdapter;
import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.Constants;
import kr.co.metisinfo.sharingcharger.databinding.ChargerDialogBinding;
import kr.co.metisinfo.sharingcharger.model.ReservationModel;
import kr.co.metisinfo.sharingcharger.view.viewInterface.MasterBaseView;

public class TestChargerDialog extends BaseActivity implements MasterBaseView {

    private static final String TAG = TestChargerDialog.class.getSimpleName();
    // 리스트뷰 어뎁터
    private ItemChargerListRecyclerViewAdapter listAdapter;

    ChargerDialogBinding binding;

    ArrayList<EvzBLEData> mArrayBLEData;

    ReservationModel reservationModel;

    EvzBLEScan mEBS;

    EvzBLEData mCurData;

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.charger_dialog);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    }

    @Override
    public void initViewModel() {

    }

    @Override
    public void setOnClickListener() {

        binding.dialogNoBtn.setOnClickListener(view -> {

            Intent intent = new Intent();

            setResult(RESULT_CANCELED, intent);

            finish();
        });

        binding.dialogOkBtn.setOnClickListener(view -> {

            if(mCurData != null) {

                Intent intent = new Intent();
                intent.putExtra("mCurData", mCurData);

                Log.e(TAG, "mCurData :  " + mCurData);


                setResult(RESULT_OK, intent);

                finish();

            } else {
                Toast.makeText(this, "충전기 목록을 선택하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void init() {

        initAdapter();

        mEBS = new EvzBLEScan(TestChargerDialog.this);

        getBLEScan();


    }

    public void getBLEScan() {

        mEBS.BLEScan(list -> {

            mArrayBLEData = list;
            if (mArrayBLEData.size() > 0) {
                listAdapter.setList(mArrayBLEData);
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("충전기 검색");
                builder.setMessage("연결 가능한 충전기를 찾지 못했습니다.\n다시 검색하시겠습니까?");

                builder.setPositiveButton("확인", (dialog, which) -> {
                    getBLEScan();
                });

                builder.setNegativeButton("취소", (dialog, which) -> {
                });
                builder.show();
            }
        });
    }

    private void initAdapter() {

        reservationModel = (ReservationModel) getIntent().getSerializableExtra("reservationModel");

        binding.chargerListRecycler.setLayoutManager(new LinearLayoutManager(this));

        listAdapter = new ItemChargerListRecyclerViewAdapter(this);

        listAdapter.reservationModel = this.reservationModel;

        binding.chargerListRecycler.setAdapter(listAdapter);
    }


    @Override
    public void onClickMasterSeq(Object obj, int position, boolean isClick) {

    }

    @Override
    public void onClickMasterNm(Object obj, int position) {

        mCurData = (EvzBLEData) obj;
    }
}
