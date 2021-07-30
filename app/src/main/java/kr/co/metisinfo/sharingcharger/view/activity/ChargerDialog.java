package kr.co.metisinfo.sharingcharger.view.activity;

import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.charzin.evzsdk.EvzBLEData;
import com.evzlife.android.blescanner.EVZScanResult;

import java.util.ArrayList;
import java.util.List;

import kr.co.metisinfo.sharingcharger.Adapter.ItemChargerListRecyclerViewAdapter;
import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ChargerDialogBinding;
import kr.co.metisinfo.sharingcharger.view.viewInterface.CheckListInterface;

public class ChargerDialog extends BaseActivity implements CheckListInterface {

    private static final String TAG = ChargerDialog.class.getSimpleName();

    // 리스트뷰 어뎁터
    private ItemChargerListRecyclerViewAdapter listAdapter;

    ChargerDialogBinding binding;

    List<EVZScanResult> mScData;

    EVZScanResult mEVZScanResult;

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

            if(mEVZScanResult != null) {

                Intent intent = new Intent();
                intent.putExtra("mEVZScanResult", mEVZScanResult);

                Log.e("metis", "mEVZScanResult :  " + mEVZScanResult);

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

        mScData = getIntent().getParcelableArrayListExtra("mScData");

        listAdapter.setList(mScData);
    }

    private void initAdapter() {

        binding.chargerListRecycler.setLayoutManager(new LinearLayoutManager(this));

        listAdapter = new ItemChargerListRecyclerViewAdapter(this);

        binding.chargerListRecycler.setAdapter(listAdapter);
    }

    @Override
    public void onClickItem(Object obj, int position) {
        mEVZScanResult = (EVZScanResult) obj;
    }
}
