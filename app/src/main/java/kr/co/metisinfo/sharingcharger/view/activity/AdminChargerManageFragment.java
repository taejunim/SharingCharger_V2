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

import kr.co.metisinfo.sharingcharger.Adapter.ItemAdminChargerManageRecyclerViewAdapter;
import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.databinding.FragmentAdminChargerManageBinding;


public class AdminChargerManageFragment extends Fragment implements ItemAdminChargerManageRecyclerViewAdapter.OnListItemSelected {

    private ItemAdminChargerManageRecyclerViewAdapter itemAdminChargerManageRecyclerViewAdapter;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_charger_manage, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.charger_manage_recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(0);
        itemAdminChargerManageRecyclerViewAdapter = new ItemAdminChargerManageRecyclerViewAdapter(this);
        mRecyclerView.setAdapter(itemAdminChargerManageRecyclerViewAdapter);
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("metis","AdminChargerManageFragment - onViewCreated");
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void sendViewDataToFragment(int position) {
        Log.d("metis","플래그먼트로 값 전달 " + position);
        ((AdminMainActivity) getActivity()).selectChargerManageMenu(position);
    }
}
