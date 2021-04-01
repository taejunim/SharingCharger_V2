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

import java.util.ArrayList;
import java.util.List;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.ChargerListItemListBinding;
import kr.co.metisinfo.sharingcharger.model.ReservationModel;
import kr.co.metisinfo.sharingcharger.view.viewInterface.MasterBaseView;

public class ItemChargerListRecyclerViewAdapter extends RecyclerView.Adapter<ItemChargerListRecyclerViewAdapter.CowViewHolder> {

    private static final String TAG = ItemChargerListRecyclerViewAdapter.class.getSimpleName();

    private List<EvzBLEData> list = new ArrayList<>();

    private int getIndex = -1;

    private RadioButton lastCheckedRB = null;

    private MasterBaseView baseView;

    public ReservationModel reservationModel;

    public ItemChargerListRecyclerViewAdapter(MasterBaseView baseView) {

        this.baseView = baseView;

    }

    @NonNull
    @Override
    public ItemChargerListRecyclerViewAdapter.CowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ChargerListItemListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.charger_list_item_list, parent, false);


        return new ItemChargerListRecyclerViewAdapter.CowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemChargerListRecyclerViewAdapter.CowViewHolder cowViewHolder, int position) {

        ChargerListItemListBinding binding = cowViewHolder.binding;

        EvzBLEData getData = list.get(position);

        if(reservationModel != null){

            if(reservationModel.bleNumber.equals(getData.bleAddr)){
                String temAddr = getData.bleName;
                binding.chargerListTxt.setText(reservationModel.chargerName +" - "+ temAddr.substring(temAddr.length() - 4, temAddr.length()));
            }else{
                binding.chargerListTxt.setText(getData.bleAddr);
            }

        }else{
            binding.chargerListTxt.setText(getData.bleAddr);
        }



        //클릭시 연결됨 보이기
        binding.chargerListTxt.setOnClickListener(view -> deviceClick(view, getData));

    }

    private void deviceClick(View view, EvzBLEData getData) {


        RadioButton getBtn = (RadioButton) view;
        getBtn.setChecked(true);

        if (lastCheckedRB != null) {
            lastCheckedRB.setChecked(false);
        }

        lastCheckedRB = getBtn;

        baseView.onClickMasterNm(getData,1);

    }

    @Override
    public int getItemCount() {

        Log.e(TAG, "list.size() : " + list.size());
        return list.size();
    }


    public void setList(List<EvzBLEData> list) {

        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }


    static class CowViewHolder extends RecyclerView.ViewHolder {

        public ChargerListItemListBinding binding;

        public CowViewHolder(@NonNull ChargerListItemListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

