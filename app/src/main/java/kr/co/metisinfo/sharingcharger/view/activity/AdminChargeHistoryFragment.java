package kr.co.metisinfo.sharingcharger.view.activity;

import static android.app.Activity.RESULT_OK;

import static kr.co.metisinfo.sharingcharger.base.Constants.PAGE_CHARGE_HISTORY;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.metisinfo.sharingcharger.Adapter.ItemAdminChargeHistoryRecyclerViewAdapter;
import kr.co.metisinfo.sharingcharger.Adapter.ItemAdminChargerManageRecyclerViewAdapter;
import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.FragmentAdminChargeHistoryBinding;
import kr.co.metisinfo.sharingcharger.model.AdminChargeHistoryModel;
import kr.co.metisinfo.sharingcharger.model.AdminChargerModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.CommonUtils;

public class AdminChargeHistoryFragment extends Fragment implements ItemAdminChargerManageRecyclerViewAdapter.OnListItemSelected {

    private ItemAdminChargeHistoryRecyclerViewAdapter itemAdminChargeHistoryRecyclerViewAdapter;
    
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FragmentAdminChargeHistoryBinding binding;

    CommonUtils commonUtils = new CommonUtils();
    ApiUtils apiUtils = new ApiUtils();

    AdminChargerModel adminChargerModel = new AdminChargerModel();
    List<AdminChargeHistoryModel> adminChargeHistoryModelList = new ArrayList<>();

    private String getArray = "DESC";
    private String getStartDate = "";
    private String getEndDate = "";
    private String getMonthType = "1개월";
    private int page = 1;
    private boolean chkList = false;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            adminChargeHistoryModelList = new ArrayList<>();

            page = 1;

            String getMonth = data.getStringExtra("getMonth");
            getArray = "DESC";

            if (!data.getStringExtra("getArray").equals("최신순")) {
                getArray = "ASC";
            }

            if (getMonth.contains("개월")) {
                getMonthType = getMonth;
                getMonth = getMonth.replace("개월", "");

                getStartDate = commonUtils.setDate(Integer.parseInt(getMonth));
                getEndDate = commonUtils.setDate(0);
                getAdminChargeHistoryList(getStartDate, getEndDate, getArray, page);

            } else {
                getMonthType = "직접선택";
                String[] getValue = getMonth.split(",");

                getStartDate = getValue[0];
                getEndDate = getValue[1];
                getAdminChargeHistoryList(getStartDate, getEndDate, getArray, page);

            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_charge_history, container, false);
        View root = binding.getRoot();

        Bundle bundle = getArguments();
        adminChargerModel = (AdminChargerModel) bundle.getSerializable("object");

        binding.searchCondition.setOnClickListener(v -> {
            showSearchCondition();
        });

        recyclerView = binding.chargeHistoryRecyclerView;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.scrollToPosition(0);
        itemAdminChargeHistoryRecyclerViewAdapter = new ItemAdminChargeHistoryRecyclerViewAdapter(this);
        recyclerView.setAdapter(itemAdminChargeHistoryRecyclerViewAdapter);

        getStartDate = commonUtils.setDate(1);
        getEndDate = commonUtils.setDate(0);

        getAdminChargeHistoryList(getStartDate, getEndDate, getArray, page);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

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
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 해당 작업을 처리함.
                                            page++;
                                            getAdminChargeHistoryList(getStartDate, getEndDate, getArray, page);
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

        return root;
    }

    private void getAdminChargeHistoryList(String startDate, String endDate, String getType, int page) {

        try {

            Map<String, Object> map = apiUtils.getAdminChargeHistory(adminChargerModel.id, startDate, endDate, getType,  page, adminChargeHistoryModelList);

            chkList = (boolean) map.get("chkList");

            adminChargeHistoryModelList = (List) map.get("list");

            Log.e("metis", "chkList : " + chkList);
            Log.e("metis", "List : " + adminChargeHistoryModelList);

            itemAdminChargeHistoryRecyclerViewAdapter.setList(adminChargeHistoryModelList);

        } catch (Exception e) {
            Log.e("metis", "getChargeHistoryList Exception : " + e);
        }
    }

    private void showSearchCondition() {

        Intent intent = new Intent(getActivity(), HistorySearchConditionActivity.class);

        //HistorySearchConditionActivity를 같이 쓰기때문에 부모 activity로 구분해야함
        intent.putExtra("activityName", "Charge");

        intent.putExtra("getArray", getArray);
        intent.putExtra("getMonthType", getMonthType);
        intent.putExtra("getStartDate", getStartDate);
        intent.putExtra("getEndDate", getEndDate);

        startActivityForResult(intent, PAGE_CHARGE_HISTORY);

        getActivity().overridePendingTransition(R.anim.translate_top, R.anim.translate_bottom);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void sendViewDataToFragment(int position) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}
