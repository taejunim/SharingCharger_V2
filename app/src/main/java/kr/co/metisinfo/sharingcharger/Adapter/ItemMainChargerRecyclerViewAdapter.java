package kr.co.metisinfo.sharingcharger.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;

import kr.co.metisinfo.sharingcharger.databinding.ListMainChargerItemBinding;
import kr.co.metisinfo.sharingcharger.model.ChargerModel;

/**
 * @ Class Name   : ItemMainChargerRecyclerViewAdapter.java
 * @ Modification : ItemMainChargerRecyclerViewAdapter CLASS.
 * @
 * @ 최초 생성일      최초 생성자
 * @ ---------     --------
 * @ 2021.09.10.    임태준
 * @
 * @  수정일          수정자
 * @ ---------    ---------
 * @
 **/
public class ItemMainChargerRecyclerViewAdapter extends RecyclerView.Adapter<ItemMainChargerRecyclerViewAdapter.CowViewHolder> {

    public interface OnListItemSelected {
        void sendViewDataToActivity(int position);
    }

    public OnListItemSelected onListItemSelected;

    private List<ChargerModel> list = new ArrayList<>();

    public ItemMainChargerRecyclerViewAdapter(OnListItemSelected onListItemSelected){
        this.onListItemSelected = onListItemSelected;
    }

    @NonNull
    @Override
    public ItemMainChargerRecyclerViewAdapter.CowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ListMainChargerItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_main_charger_item, parent, false);

        return new ItemMainChargerRecyclerViewAdapter.CowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemMainChargerRecyclerViewAdapter.CowViewHolder cowViewHolder, int position) {

        ListMainChargerItemBinding binding = cowViewHolder.binding;

        ChargerModel chargerModel = list.get(position);

        binding.chargerName.setText(chargerModel.name);

        if (chargerModel.bleNumber.equals("")) {
            binding.chargerBleText.setText("");
        } else {
            String tempBleNumber = "";
            tempBleNumber = chargerModel.bleNumber.replaceAll(":", "");
            tempBleNumber = tempBleNumber.substring(tempBleNumber.length() - 4, tempBleNumber.length());
            binding.chargerBleText.setText( ThisApplication.context.getResources().getString(R.string.charger_manage_charger_ble_text, tempBleNumber));
        }

        binding.chargerAddress.setText(chargerModel.address);
        binding.chargerDetailAddress.setText(chargerModel.detailAddress.equals("") ? "-" : chargerModel.detailAddress);

        String chargerStatusText = "대기중";

        switch (chargerModel.currentStatusType) {
            case "READY" :
                chargerStatusText = "대기중";
                binding.chargerStatus.setTextColor(ContextCompat.getColor(ThisApplication.context, R.color.blue_text));
                break;

            case "RESERVATION" :
                chargerStatusText = "예약중";
                binding.chargerStatus.setTextColor(ContextCompat.getColor(ThisApplication.context, R.color.red));
                break;

            case "CHARGING" :
                chargerStatusText = "충전중";
                binding.chargerStatus.setTextColor(ContextCompat.getColor(ThisApplication.context, R.color.red));
                break;

            case "TROUBLE" :
                chargerStatusText = "점검중";
                binding.chargerStatus.setTextColor(ContextCompat.getColor(ThisApplication.context, R.color.red));
                break;

            case "CLOSE" :
                chargerStatusText = "마감";
                binding.chargerStatus.setTextColor(ContextCompat.getColor(ThisApplication.context, R.color.red));
                break;
        }

        binding.chargerStatus.setText(chargerStatusText);
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<ChargerModel> list) {

        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    class CowViewHolder extends RecyclerView.ViewHolder {

        public ListMainChargerItemBinding binding;

        public CowViewHolder(@NonNull ListMainChargerItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    //갱신 과정에서 포지션이 없는 경우를 방지
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        Log.d("metis",position + "번째 row 클릭됨");
                        onListItemSelected.sendViewDataToActivity(position);
                    }
                }
            });
        }
    }
}

