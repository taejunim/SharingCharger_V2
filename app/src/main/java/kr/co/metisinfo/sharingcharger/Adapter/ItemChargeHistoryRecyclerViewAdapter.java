package kr.co.metisinfo.sharingcharger.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.ChargeHistoryItemListBinding;
import kr.co.metisinfo.sharingcharger.model.RechargeModel;

public class ItemChargeHistoryRecyclerViewAdapter extends RecyclerView.Adapter<ItemChargeHistoryRecyclerViewAdapter.CowViewHolder> {

    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    private List<RechargeModel> list = new ArrayList<>();

    public ItemChargeHistoryRecyclerViewAdapter() {

    }

    @NonNull
    @Override
    public ItemChargeHistoryRecyclerViewAdapter.CowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ChargeHistoryItemListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.charge_history_item_list, parent, false);
        return new ItemChargeHistoryRecyclerViewAdapter.CowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CowViewHolder holder, int position) {

        ChargeHistoryItemListBinding binding = holder.binding;

        RechargeModel model = list.get(position);

        binding.chargeHistoryNameTxt.setText(model.chargerName);
        binding.chargeHistoryDateTxt.setText(setDate(model.startRechargeDate, model.endRechargeDate));

        if (model.reservationPoint >= 0) {
            binding.chargeHistoryReservationPointTxt.setText(ThisApplication.context.getResources().getString(R.string.charge_history_reservation_point, decimalFormat.format(model.reservationPoint)));
        } else {
            binding.chargeHistoryReservationPointTxt.setText("정산중");
        }

        // 충전시 일때 rechargePoint 0이기 때문에 endRechargeDate이 null 이면 정산중
        if(model.endRechargeDate == null){

            binding.chargeHistoryRechargePointTxt.setText(ThisApplication.context.getResources().getString(R.string.charge_history_recharge_point,  "정산중"));
            binding.chargeHistoryRefundPointTxt.setText(ThisApplication.context.getResources().getString(R.string.charge_history_refund_point,  "정산중"));

        }else{

            if (model.rechargePoint >= 0) {
                binding.chargeHistoryRechargePointTxt.setText(ThisApplication.context.getResources().getString(R.string.charge_history_recharge_point, decimalFormat.format(model.rechargePoint)));
            } else {
                binding.chargeHistoryRechargePointTxt.setText(ThisApplication.context.getResources().getString(R.string.charge_history_recharge_point,  "정산 오류"));
            }

            if (model.refundPoint >= 0) {
                binding.chargeHistoryRefundPointTxt.setText(ThisApplication.context.getResources().getString(R.string.charge_history_refund_point, decimalFormat.format(model.refundPoint)));
            } else {
                binding.chargeHistoryRefundPointTxt.setText(ThisApplication.context.getResources().getString(R.string.charge_history_recharge_point,  "정산 오류"));
            }
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
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<RechargeModel> list) {

        this.list.clear();
        this.list.addAll(list);

        notifyDataSetChanged();
    }


    static class CowViewHolder extends RecyclerView.ViewHolder {

        public ChargeHistoryItemListBinding binding;

        public CowViewHolder(@NonNull ChargeHistoryItemListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
