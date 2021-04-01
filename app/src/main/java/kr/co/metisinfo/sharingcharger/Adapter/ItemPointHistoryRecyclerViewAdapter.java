package kr.co.metisinfo.sharingcharger.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.databinding.PointHistoryItemListBinding;
import kr.co.metisinfo.sharingcharger.model.PointModel;
import kr.co.metisinfo.sharingcharger.view.activity.PointHistoryActivity;

public class ItemPointHistoryRecyclerViewAdapter extends RecyclerView.Adapter<ItemPointHistoryRecyclerViewAdapter.CowViewHolder> {

    private static final String TAG = ItemPointHistoryRecyclerViewAdapter.class.getSimpleName();

    private List<PointModel> list = new ArrayList<>();

    private Context context;

    public ItemPointHistoryRecyclerViewAdapter(Context context) {

        this.context = context;
    }

    @NonNull
    @Override
    public ItemPointHistoryRecyclerViewAdapter.CowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        PointHistoryItemListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.point_history_item_list, parent, false);

        return new ItemPointHistoryRecyclerViewAdapter.CowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CowViewHolder holder, int position) {

        PointHistoryItemListBinding binding = holder.binding;

        PointModel model = list.get(position);

        String getPointUsedType = "포인트 충전";

        if(model.type.equals("REFUND")){
            getPointUsedType = "부분 환불";
        }else if(model.type.equals("USED")){
            getPointUsedType = "사용";
        }

        String getDate = model.created;

        getDate = getDate.replaceAll("T"," ");

        getDate = getDate.substring(0, getDate.length()-3);

        binding.pointHistoryDateTxt.setText(getDate);

        binding.pointHistoryStateTxt.setText(getPointUsedType);

        String getPoint = String.valueOf(model.point);

        DecimalFormat format = new DecimalFormat("###,###");

        if(getPoint.substring(0,1).equals("-")){
            getPoint = format.format(model.point);
            getPoint = getPoint.replace("-","- ");
            binding.pointHistoryPoint.setTextColor(ContextCompat.getColor(context, R.color.red));
        }else{
            getPoint = "+ "+ format.format(model.point);
            binding.pointHistoryPoint.setTextColor(ContextCompat.getColor(context, R.color.blue_button));
        }

        binding.pointHistoryPoint.setText(getPoint);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<PointModel> list) {

        this.list.clear();
        this.list.addAll(list);

        notifyDataSetChanged();
    }


    static class CowViewHolder extends RecyclerView.ViewHolder {

        public PointHistoryItemListBinding binding;

        public CowViewHolder(@NonNull PointHistoryItemListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
