package kr.co.metisinfo.sharingcharger.view.activity;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kr.co.metisinfo.sharingcharger.Adapter.ItemTimeRecyclerViewAdapter;
import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivitySettingTimeBinding;
import kr.co.metisinfo.sharingcharger.view.viewInterface.MasterBaseView;

public class TimeSettingActivity extends BaseActivity implements MasterBaseView {

    private static final String TAG = TimeSettingActivity.class.getSimpleName();

    ActivitySettingTimeBinding binding;

    private ItemTimeRecyclerViewAdapter timeAdapter;

    List<String> list = new ArrayList();

    String getTime = "";

    int ChargerTime = 0;

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting_time);

        changeStatusBarColor(false);

        list = Arrays.asList(getResources().getStringArray(R.array.time_setting));

    }

    @Override
    public void initViewModel() {


    }

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnBack.setOnClickListener(view -> {
            finish();
        });

        binding.timeBtn.setOnClickListener(view -> {

            SharedPreferences pref = getSharedPreferences("reservation", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            int ChargerTime = 0;

            if(!getTime.equals("")){

                getTime = getTime.replace("시간","");

                getTime = getTime.trim();

            }
            Log.e(TAG, "getTime :  " + getTime);

            ChargerTime = Integer.parseInt(getTime)*60;

            Log.e(TAG, "ChargerTime :  " + ChargerTime);

            editor.putInt("ChargerTime", ChargerTime);
            editor.commit();

            finish();

        });

    }

    @Override
    public void init() {

        binding.includeHeader.txtTitle.setText(R.string.setting_time);
        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);

        SharedPreferences pref = getSharedPreferences("reservation", MODE_PRIVATE);

        //기본 8시간
        ChargerTime = pref.getInt("ChargerTime", 480);

        initAdapter();

    }

    private void initAdapter() {

        binding.settingTimeTableRecycler.setLayoutManager(new LinearLayoutManager(this));

        timeAdapter = new ItemTimeRecyclerViewAdapter(this, this);

        timeAdapter.getIndex = (ChargerTime/60)-1;

        binding.settingTimeTableRecycler.setAdapter(timeAdapter);

        timeAdapter.setList(list);

    }

    @Override
    public void onClickMasterSeq(Object obj, int position, boolean isClick) {

    }

    @Override
    public void onClickMasterNm(Object obj, int position) {
        getTime = (String) obj;
        Log.e(TAG, "onClickMasterNm time : "+ getTime);
    }
}
