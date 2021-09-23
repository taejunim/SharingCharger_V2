package kr.co.metisinfo.sharingcharger.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.AdminChargeHistoryItemBinding;
import kr.co.metisinfo.sharingcharger.model.AdminChargeHistoryModel;
import kr.co.metisinfo.sharingcharger.view.activity.AdminChargeHistoryFragment;

public class ItemAdminChargeHistoryRecyclerViewAdapter extends RecyclerView.Adapter<ItemAdminChargeHistoryRecyclerViewAdapter.CowViewHolder> {

    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    private List<AdminChargeHistoryModel> list = new ArrayList<>();

    public ItemAdminChargeHistoryRecyclerViewAdapter(AdminChargeHistoryFragment adminChargeHistoryFragment) {
    }

    @NonNull
    @Override
    public ItemAdminChargeHistoryRecyclerViewAdapter.CowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        AdminChargeHistoryItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.admin_charge_history_item, parent, false);
        return new ItemAdminChargeHistoryRecyclerViewAdapter.CowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CowViewHolder holder, int position) {

        AdminChargeHistoryItemBinding binding = holder.binding;

        AdminChargeHistoryModel model = list.get(position);

        binding.adminChargeHistoryChargerName.setText(ThisApplication.context.getResources().getString(R.string.admin_charge_history_charger_name, String.valueOf(model.chargerName)));

        if (model.id >= 0) {
            binding.adminChargeHistoryRechargeNumber.setText(ThisApplication.context.getResources().getString(R.string.charge_history_recharge_number, String.valueOf(model.id)));
        }

        binding.adminChargeHistoryReservationDate.setText(ThisApplication.context.getResources().getString(R.string.admin_charge_history_reservation_date, setDate(model.reservationStartDate, model.reservationEndDate)));
        binding.adminChargeHistoryChargeDate.setText(ThisApplication.context.getResources().getString(R.string.admin_charge_history_charge_date, setDate(model.startRechargeDate, model.endRechargeDate)));

        if (model.ownerPoint > 0 && !ThisApplication.staticUserModel.username.equals(model.username)) {
            binding.adminChargeHistoryProfitPoint.setText(ThisApplication.context.getResources().getString(R.string.admin_charge_history_profit_point, decimalFormat.format(model.ownerPoint)) + " p");
        } else if (model.ownerPoint <= 0 && ThisApplication.staticUserModel.username.equals(model.username)){
            binding.adminChargeHistoryProfitPoint.setText(ThisApplication.context.getResources().getString(R.string.admin_charge_history_profit_point, "소유주 본인 충전"));
        } else {
            binding.adminChargeHistoryProfitPoint.setText(ThisApplication.context.getResources().getString(R.string.admin_charge_history_profit_point, "알 수 없음"));
        }
    }
    private String setDate(String getSDate, String getEDate) {

        String strDate = "";

        if(getSDate != null){
            strDate += getSDate.substring(0, 10) + " " + getSDate.substring(11, 16);
        }

        if(getEDate != null){
            strDate += " ~ " + getEDate.substring(0, 10) + " " + getEDate.substring(11, 16);
        }
        
        return strDate;
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<AdminChargeHistoryModel> list) {

        this.list.clear();
        this.list.addAll(list);

        notifyDataSetChanged();
    }


    static class CowViewHolder extends RecyclerView.ViewHolder {

        public AdminChargeHistoryItemBinding binding;

        public CowViewHolder(@NonNull AdminChargeHistoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
