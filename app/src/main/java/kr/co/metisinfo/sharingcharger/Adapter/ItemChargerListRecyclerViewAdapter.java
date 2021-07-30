package kr.co.metisinfo.sharingcharger.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.charzin.evzsdk.EvzBLEData;
import com.evzlife.android.blescanner.EVZScanResult;

import java.util.ArrayList;
import java.util.List;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.databinding.ChargerItemListBinding;
import kr.co.metisinfo.sharingcharger.view.viewInterface.CheckListInterface;

public class ItemChargerListRecyclerViewAdapter extends RecyclerView.Adapter<ItemChargerListRecyclerViewAdapter.CowViewHolder> {

    private static final String TAG = ItemChargerListRecyclerViewAdapter.class.getSimpleName();

    private List<EVZScanResult> list = new ArrayList<>();

    private int getIndex = -1;

    private RadioButton lastCheckedRB = null;

    private CheckListInterface checkListInterface;

    public ItemChargerListRecyclerViewAdapter(CheckListInterface checkListInterface) {

        this.checkListInterface = checkListInterface;

    }

    @NonNull
    @Override
    public CowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ChargerItemListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.charger_item_list, parent, false);

        return new CowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CowViewHolder cowViewHolder, int position) {

        ChargerItemListBinding binding = cowViewHolder.binding;

        EVZScanResult mEVZScanResult = list.get(position);

        binding.chargerListTxt.setText(mEVZScanResult.getDevice().getAddress());
        //클릭시 연결됨 보이기
        binding.chargerListTxt.setOnClickListener(view -> deviceClick(view, mEVZScanResult));

    }

    private void deviceClick(View view, EVZScanResult getData) {


        RadioButton getBtn = (RadioButton) view;
        getBtn.setChecked(true);

        if (lastCheckedRB != null) {
            lastCheckedRB.setChecked(false);
        }

        lastCheckedRB = getBtn;

        checkListInterface.onClickItem(getData, 1);

    }

    @Override
    public int getItemCount() {

        Log.e("metis", "list.size() : " + list.size());
        return list.size();
    }


    public void setList(List<EVZScanResult> list) {

        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }


    static class CowViewHolder extends RecyclerView.ViewHolder {

        public ChargerItemListBinding binding;

        public CowViewHolder(@NonNull ChargerItemListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

