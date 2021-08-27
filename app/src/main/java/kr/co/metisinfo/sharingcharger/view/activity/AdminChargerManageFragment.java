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

        try {
            adminChargerModelList = apiUtils.getAdminCharger();
        } catch (Exception e) {
            e.printStackTrace();
        }
        itemAdminChargerManageRecyclerViewAdapter.setList(adminChargerModelList);

        return root;
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
