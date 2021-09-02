package kr.co.metisinfo.sharingcharger.view.activity;

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

import kr.co.metisinfo.sharingcharger.Adapter.ItemAdminChargerManageRecyclerViewAdapter;
import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.databinding.FragmentAdminChargerManageBinding;
import kr.co.metisinfo.sharingcharger.model.AdminChargerModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;

public class AdminChargerManageFragment extends Fragment implements ItemAdminChargerManageRecyclerViewAdapter.OnListItemSelected {

    private ItemAdminChargerManageRecyclerViewAdapter itemAdminChargerManageRecyclerViewAdapter;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private FragmentAdminChargerManageBinding binding;

    ApiUtils apiUtils = new ApiUtils();

    List<AdminChargerModel> adminChargerModelList = new ArrayList<>();

    private int page = 1;
    private boolean chkList = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_charger_manage, container, false);
        View root = binding.getRoot();

        mRecyclerView = binding.chargerManageRecycler;
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(0);
        itemAdminChargerManageRecyclerViewAdapter = new ItemAdminChargerManageRecyclerViewAdapter(this);
        mRecyclerView.setAdapter(itemAdminChargerManageRecyclerViewAdapter);

        getChargerList(page);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

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
                                            getChargerList(page);
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

    private void getChargerList(int page) {

        try {

            Map<String, Object> map = apiUtils.getAdminCharger(page, adminChargerModelList);

            chkList = (boolean) map.get("chkList");

            adminChargerModelList = (List) map.get("list");

            Log.e("metis", "chkList : " + chkList);
            Log.e("metis", "List : " + adminChargerModelList);

            itemAdminChargerManageRecyclerViewAdapter.setList(adminChargerModelList);

        } catch (Exception e) {
            Log.e("metis", "getChargeHistoryList Exception : " + e);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void sendViewDataToFragment(int position) {
        ((AdminMainActivity) getActivity()).selectChargerManageMenu(adminChargerModelList.get(position));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
