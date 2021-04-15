package kr.co.metisinfo.sharingcharger.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.databinding.ChargeHistoryItemListBinding;

public class ItemChargeHistoryRecyclerViewAdapter extends RecyclerView.Adapter<ItemChargeHistoryRecyclerViewAdapter.CowViewHolder> {

    private static final String TAG = ItemChargeHistoryRecyclerViewAdapter.class.getSimpleName();

  //  private List<RechargeModel> list = new ArrayList<>();

    public ItemChargeHistoryRecyclerViewAdapter() {

    }

    @NonNull
    @Override
    public CowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ChargeHistoryItemListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.charge_history_item_list, parent, false);

        return new CowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CowViewHolder holder, int position) {

        ChargeHistoryItemListBinding binding = holder.binding;


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

//        return list.size();
        return 0;
    }

//    public void setList(List<RechargeModel> list) {
//
//        this.list.clear();
//        this.list.addAll(list);
//
//        notifyDataSetChanged();
//    }


    static class CowViewHolder extends RecyclerView.ViewHolder {

        public ChargeHistoryItemListBinding binding;

        public CowViewHolder(@NonNull ChargeHistoryItemListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
