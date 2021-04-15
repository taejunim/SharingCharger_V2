package kr.co.metisinfo.sharingcharger.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.databinding.PointHistoryItemListBinding;

public class ItemPointHistoryRecyclerViewAdapter extends RecyclerView.Adapter<ItemPointHistoryRecyclerViewAdapter.CowViewHolder> {

    private static final String TAG = ItemPointHistoryRecyclerViewAdapter.class.getSimpleName();

    //private List<PointModel> list = new ArrayList<>();

    private Context context;

    public ItemPointHistoryRecyclerViewAdapter(Context context) {

        this.context = context;
    }

    @NonNull
    @Override
    public CowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        PointHistoryItemListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.point_history_item_list, parent, false);

        return new CowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CowViewHolder holder, int position) {

        PointHistoryItemListBinding binding = holder.binding;

    }

    @Override
    public int getItemCount() {

        //return list.size();
        return 0;
    }

//    public void setList(List<PointModel> list) {
//
//        this.list.clear();
//        this.list.addAll(list);
//
//        notifyDataSetChanged();
//    }

    static class CowViewHolder extends RecyclerView.ViewHolder {

        public PointHistoryItemListBinding binding;

        public CowViewHolder(@NonNull PointHistoryItemListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
