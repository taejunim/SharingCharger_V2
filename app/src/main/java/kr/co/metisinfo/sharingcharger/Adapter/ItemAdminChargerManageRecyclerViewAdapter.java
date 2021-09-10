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
import kr.co.metisinfo.sharingcharger.databinding.ListAdminChargerManageItemBinding;
import kr.co.metisinfo.sharingcharger.model.AdminChargerModel;

public class ItemAdminChargerManageRecyclerViewAdapter extends RecyclerView.Adapter<ItemAdminChargerManageRecyclerViewAdapter.CowViewHolder> {

    public interface OnListItemSelected {
        void sendViewDataToFragment(int position);
    }

    public OnListItemSelected onListItemSelected;

    private List<AdminChargerModel> list = new ArrayList<>();

    public ItemAdminChargerManageRecyclerViewAdapter(OnListItemSelected onListItemSelected){
        this.onListItemSelected = onListItemSelected;
    }

    @NonNull
    @Override
    public ItemAdminChargerManageRecyclerViewAdapter.CowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ListAdminChargerManageItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_admin_charger_manage_item, parent, false);

        return new ItemAdminChargerManageRecyclerViewAdapter.CowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdminChargerManageRecyclerViewAdapter.CowViewHolder cowViewHolder, int position) {

        ListAdminChargerManageItemBinding binding = cowViewHolder.binding;

        AdminChargerModel adminChargerModel = list.get(position);

        binding.adminChargerName.setText(adminChargerModel.name);

        if (adminChargerModel.bleNumber.equals("")) {
            binding.adminChargerBleText.setText("");
        } else {
            String tempBleNumber = "";
            tempBleNumber = adminChargerModel.bleNumber.replaceAll(":", "");
            tempBleNumber = tempBleNumber.substring(tempBleNumber.length() - 4, tempBleNumber.length());
            binding.adminChargerBleText.setText( ThisApplication.context.getResources().getString(R.string.charger_manage_charger_ble_text, tempBleNumber));
        }

        binding.adminChargerAddress.setText(adminChargerModel.address);
        binding.adminChargerNumber.setText(String.valueOf(position + 1));
        binding.adminChargerDescription.setText(adminChargerModel.description.equals("") ? "-" : adminChargerModel.description);

        String chargerStatusText = "대기중";

        switch (adminChargerModel.currentStatusType) {
            case "READY" :
                chargerStatusText = "대기중";
                break;

            case "RESERVATION" :
                chargerStatusText = "예약중";
                binding.adminChargerStatus.setTextColor(ContextCompat.getColor(ThisApplication.context, R.color.red));
                break;

            case "CHARGING" :
                chargerStatusText = "충전중";
                binding.adminChargerStatus.setTextColor(ContextCompat.getColor(ThisApplication.context, R.color.red));
                break;
        }

        binding.adminChargerStatus.setText(chargerStatusText);
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<AdminChargerModel> list) {

        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void sendViewDataToFragment(int position){

    }

    class CowViewHolder extends RecyclerView.ViewHolder {

        public ListAdminChargerManageItemBinding binding;

        public CowViewHolder(@NonNull ListAdminChargerManageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    //갱신 과정에서 포지션이 없는 경우를 방지
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        Log.d("metis",position + "번째 row 클릭됨");
                        onListItemSelected.sendViewDataToFragment(position);
                    }
                }
            });
        }
    }
}

