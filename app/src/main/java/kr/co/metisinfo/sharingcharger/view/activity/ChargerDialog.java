package kr.co.metisinfo.sharingcharger.view.activity;

import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.charzin.evzsdk.EvzBLEData;

import java.util.ArrayList;

import kr.co.metisinfo.sharingcharger.Adapter.ItemChargerListRecyclerViewAdapter;
import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ChargerDialogBinding;
import kr.co.metisinfo.sharingcharger.model.ReservationModel;
import kr.co.metisinfo.sharingcharger.view.viewInterface.MasterBaseView;

public class ChargerDialog extends BaseActivity implements MasterBaseView {

    private static final String TAG = ChargerDialog.class.getSimpleName();

    // 리스트뷰 어뎁터
    private ItemChargerListRecyclerViewAdapter listAdapter;

    ChargerDialogBinding binding;

    ArrayList<EvzBLEData> mArrayBLEData;

    ReservationModel reservationModel;

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

        mArrayBLEData = getIntent().getParcelableArrayListExtra("mArrayBLEData");

        listAdapter.setList(mArrayBLEData);
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
