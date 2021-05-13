package kr.co.metisinfo.sharingcharger.charger;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.co.metisinfo.sharingcharger.Adapter.ItemChargeHistoryRecyclerViewAdapter;
import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityHistoryBinding;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.DateUtils;
import kr.co.metisinfo.sharingcharger.view.activity.HistorySearchConditionActivity;

import static kr.co.metisinfo.sharingcharger.base.Constants.PAGE_CHARGE_HISTORY;

public class ChargerUseHistoryActivity extends BaseActivity {

    private static final String TAG = ChargerUseHistoryActivity.class.getSimpleName();

    ActivityHistoryBinding binding;

    private ItemChargeHistoryRecyclerViewAdapter historyAdapter;

//    List<RechargeModel> list = new ArrayList<>();

    private int index = 1;

    private String getArray = "DESC";

    private String getStartDate = "";
    private String getEndDate = "";

    private String getMonthType = "1개월";

    private boolean chkList = false;

    ApiUtils apiUtils = new ApiUtils();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

        }

    }

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_history);

        changeStatusBarColor(false);

    }

    @Override
    public void initViewModel() {

        binding.currentPointTxt.setVisibility(View.GONE);
        binding.currentPointTitle.setVisibility(View.GONE);
        binding.historyView.setVisibility(View.GONE);

    }

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnBack.setOnClickListener(view -> finish());
        binding.includeHeader.btnMenu.setOnClickListener(view -> showSearchCondition());

        binding.chargeHistoryRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                Log.e(TAG, "onScrolled");
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;

                Log.e(TAG, "lastVisibleItemPosition : " + lastVisibleItemPosition);
                Log.e(TAG, "itemTotalCount : " + itemTotalCount);

                try {
                    if (lastVisibleItemPosition == itemTotalCount) {

                        if (chkList) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 해당 작업을 처리함.
                                            index++;
                                            getChargeHistoryList(getStartDate, getEndDate, getArray, index);
                                        }
                                    });
                                }
                            }).start();
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onScrolled Exception : " + e);
                }
            }

        });

    }

    @Override
    public void init() {

        binding.includeHeader.txtTitle.setText(R.string.charge_history_title);
        binding.includeHeader.layoutHeaderMenu.setBackground(ContextCompat.getDrawable(this, R.mipmap.menu_list));

        initAdapter();

    }

    private void initAdapter() {

        binding.chargeHistoryRecycler.setLayoutManager(new LinearLayoutManager(this));

        historyAdapter = new ItemChargeHistoryRecyclerViewAdapter();

        binding.chargeHistoryRecycler.setAdapter(historyAdapter);

        getStartDate = setDate(1);
        getEndDate = setDate(0);
        getChargeHistoryList(getStartDate, getEndDate, getArray, index);

    }

    private void getChargeHistoryList(String startDate, String endDate, String getType, int index) {

        try {

            //list 담기
            //historyAdapter.setList(list);

        } catch (Exception e) {
            Log.e(TAG, "getChargeHistoryList Exception : " + e);
        }
    }

    private void showSearchCondition() {

        Intent intent = new Intent(this, HistorySearchConditionActivity.class);

        //HistorySearchConditionActivity를 같이 쓰기때문에 부모 activity로 구분해야함
        intent.putExtra("activityName", this.getLocalClassName());

        intent.putExtra("getArray", getArray);
        intent.putExtra("getMonthType", getMonthType);
        intent.putExtra("getStartDate", getStartDate);
        intent.putExtra("getEndDate", getEndDate);

        startActivityForResult(intent, PAGE_CHARGE_HISTORY);

        overridePendingTransition(R.anim.translate_top, R.anim.translate_bottom);
    }

    public String setDate(int getMonth) {

        String getStringDate = DateUtils.setOperationDate("minus", getMonth * 30, "yyyyMMdd");

        int startYYYY = Integer.parseInt(getStringDate.substring(0, 4));
        int startMM = Integer.parseInt(getStringDate.substring(4, 6));
        int startDD = Integer.parseInt(getStringDate.substring(6, 8));

        String getDate = startYYYY + "-" + String.format("%02d", startMM) + "-" + String.format("%02d", startDD);

        return getDate;
    }

}
