package kr.co.metisinfo.sharingcharger.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import kr.co.metisinfo.sharingcharger.R;

import kr.co.metisinfo.sharingcharger.databinding.ListMonthlyProfitPointItemBinding;
import kr.co.metisinfo.sharingcharger.model.AdminMonthlyProfitPointModel;

public class ItemAdminMonthlyProfitPointRecyclerViewAdapter extends RecyclerView.Adapter<ItemAdminMonthlyProfitPointRecyclerViewAdapter.CowViewHolder> {

    private static final String TAG = ItemAdminMonthlyProfitPointRecyclerViewAdapter.class.getSimpleName();

    private List<AdminMonthlyProfitPointModel> list = new ArrayList<>();

    private Context context;

    public ItemAdminMonthlyProfitPointRecyclerViewAdapter(Context context) {

        this.context = context;
    }

    @NonNull
    @Override
    public ItemAdminMonthlyProfitPointRecyclerViewAdapter.CowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ListMonthlyProfitPointItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_monthly_profit_point_item, parent, false);

        return new ItemAdminMonthlyProfitPointRecyclerViewAdapter.CowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CowViewHolder holder, int position) {

        ListMonthlyProfitPointItemBinding binding = holder.binding;

        AdminMonthlyProfitPointModel model = list.get(position);

        DecimalFormat format = new DecimalFormat("###,###");

        binding.monthTxt.setText(model.day.replace("-"," 년 ") + "월");
        binding.monthlyProfitPointTxt.setText(format.format(model.point) + " p");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<AdminMonthlyProfitPointModel> list) {

        this.list.clear();
        this.list.addAll(list);

        notifyDataSetChanged();
    }

    static class CowViewHolder extends RecyclerView.ViewHolder {

        public ListMonthlyProfitPointItemBinding binding;

        public CowViewHolder(@NonNull ListMonthlyProfitPointItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
