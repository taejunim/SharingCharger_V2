package kr.co.metisinfo.sharingcharger.digitalWalletManagement;

import android.content.Intent;
import android.util.Log;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kr.co.metisinfo.sharingcharger.Adapter.ItemPointHistoryRecyclerViewAdapter;
import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityHistoryBinding;
import kr.co.metisinfo.sharingcharger.model.PointModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.CommonUtils;
import kr.co.metisinfo.sharingcharger.utils.DateUtils;
import kr.co.metisinfo.sharingcharger.view.activity.HistorySearchConditionActivity;

import static kr.co.metisinfo.sharingcharger.base.Constants.PAGE_POINT_HISTORY;

public class PointUseHistoryActivity extends BaseActivity {

    private static final String TAG = PointUseHistoryActivity.class.getSimpleName();

    ActivityHistoryBinding binding;

    private ItemPointHistoryRecyclerViewAdapter historyAdapter;

    List<PointModel> list = new ArrayList<>();

    private int index = 1;

    private String getType = "ALL";
    private String getArray = "DESC";

    private String getStartDate = "";
    private String getEndDate = "";

    private String getMonthType = "1개월";

    private boolean chkList = false;

    ApiUtils apiUtils = new ApiUtils();
    CommonUtils commonUtils = new CommonUtils();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            list = new ArrayList<>();

            index = 1;

            String getMonth = data.getStringExtra("getMonth");

            getArray = "DESC";

            if (!data.getStringExtra("getArray").equals("최신순")) {
                getArray = "ASC";
            }

            getType = "ALL";

            if (data.getStringExtra("getType").equals("포인트 충전")) {
                getType = "PURCHASE";
            } else if (data.getStringExtra("getType").equals("사용")) {
                getType = "USED";
            } else if (data.getStringExtra("getType").equals("부분 환불")) {
                getType = "REFUND";
            }

            Log.e(TAG, "getArray : " + getArray + " getType : " + getType + " Index : " + index);

            if (getMonth.contains("개월")) {
                getMonthType = getMonth;
                getMonth = getMonth.replace("개월", "");

                getStartDate = setDate(Integer.parseInt(getMonth));
                getEndDate = setDate(0);
                getPointHistoryList(getStartDate, getEndDate, getArray, getType, index);

            } else {
                getMonthType = "직접선택";
                String[] getValue = getMonth.split(",");
                //date로 들어옴

                getStartDate = getValue[0];
                getEndDate = getValue[1];
                getPointHistoryList(getStartDate, getEndDate, getArray, getType, index);

            }

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
        try {

            int getPoint = apiUtils.getUserPoint();
            binding.currentPointTxt.setText(NumberFormat.getInstance(Locale.KOREA).format(getPoint) + "p");

            if (getPoint < 0) {
                binding.currentPointTxt.setBackground(ContextCompat.getDrawable(PointUseHistoryActivity.this, R.drawable.border_red_30));
            }

        } catch (Exception e) {
            Log.e(TAG, "point Exception : " + e);
        }

    }

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnBack.setOnClickListener(view -> finish());
        binding.includeHeader.btnMenu.setOnClickListener(view -> showSearchCondition());

        binding.chargeHistoryRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                //Log.e("metis","onScrollStateChanged");
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                Log.e("metis", "onScrolled");
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;

                Log.e("metis", "lastVisibleItemPosition : " + lastVisibleItemPosition);
                Log.e("metis", "itemTotalCount : " + itemTotalCount);

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
                    Log.e("metis", "onScrolled Exception : " + e);
                }
            }

        });

    }

    @Override
    public void init() {

        binding.includeHeader.txtTitle.setText(R.string.point_history_title);

        RelativeLayout.LayoutParams imageLayoutParams = new RelativeLayout.LayoutParams(commonUtils.convertToDp(20F), commonUtils.convertToDp(21.3F));
        imageLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        imageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        imageLayoutParams.setMarginEnd(commonUtils.convertToDp(15F));

        binding.includeHeader.layoutHeaderMenu.setBackground(ContextCompat.getDrawable(this, R.mipmap.menu_list));
        binding.includeHeader.layoutHeaderMenu.setLayoutParams(imageLayoutParams);

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

        try {

            Map<String, Object> map = apiUtils.getPoints(startDate, endDate, sort, getType, pageIndex, list);

            chkList = (boolean) map.get("chkList");

            list = (List) map.get("list");

            Log.e(TAG, "chkList : " + chkList);
            Log.e(TAG, "List : " + list);

            historyAdapter.setList(list);

        } catch (Exception e) {
            Log.e(TAG, "getPointHistoryList Exception : " + e);
        }

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
