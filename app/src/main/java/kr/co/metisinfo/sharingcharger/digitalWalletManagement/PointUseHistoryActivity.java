package kr.co.metisinfo.sharingcharger.digitalWalletManagement;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.co.metisinfo.sharingcharger.Adapter.ItemPointHistoryRecyclerViewAdapter;
import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityHistoryBinding;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.DateUtils;
import kr.co.metisinfo.sharingcharger.view.activity.HistorySearchConditionActivity;

import static kr.co.metisinfo.sharingcharger.base.Constants.PAGE_POINT_HISTORY;

public class PointUseHistoryActivity extends BaseActivity {

    private static final String TAG = PointUseHistoryActivity.class.getSimpleName();

    ActivityHistoryBinding binding;

    private ItemPointHistoryRecyclerViewAdapter historyAdapter;

    //List<PointModel> list = new ArrayList<>();

    private int index = 1;

    private String getType = "ALL";
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

        //현재포인트 가져오기

    }

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnBack.setOnClickListener(view -> finish());
        binding.includeHeader.btnMenu.setOnClickListener(view -> showSearchCondition());

        binding.chargeHistoryRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                //Log.e(TAG,"onScrollStateChanged");
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

                                            getPointHistoryList(getStartDate, getEndDate, getArray, getType, index);
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

        binding.includeHeader.txtTitle.setText(R.string.point_history_title);
        binding.includeHeader.layoutHeaderMenu.setBackground(ContextCompat.getDrawable(this, R.mipmap.menu_list));

        initAdapter();

    }

    private void initAdapter() {

        binding.chargeHistoryRecycler.setLayoutManager(new LinearLayoutManager(this));

        historyAdapter = new ItemPointHistoryRecyclerViewAdapter(this);

        binding.chargeHistoryRecycler.setAdapter(historyAdapter);

        getStartDate = setDate(1);
        getEndDate = setDate(0);

        getPointHistoryList(getStartDate, getEndDate, getArray, getType, index);

    }

    private void getPointHistoryList(String startDate, String endDate, String sort, String getType, int pageIndex) {

        //historyAdapter.setList(list);

    }

    private void showSearchCondition() {

        Intent intent = new Intent(this, HistorySearchConditionActivity.class);

        //HistorySearchConditionActivity를 같이 쓰기때문에 부모 activity로 구분해야함
        intent.putExtra("activityName", this.getLocalClassName());

        intent.putExtra("getType", getType);
        intent.putExtra("getArray", getArray);
        intent.putExtra("getMonthType", getMonthType);
        intent.putExtra("getStartDate", getStartDate);
        intent.putExtra("getEndDate", getEndDate);

        startActivityForResult(intent, PAGE_POINT_HISTORY);

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
