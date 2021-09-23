package kr.co.metisinfo.sharingcharger.Adapter;

import android.content.Context;
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
import kr.co.metisinfo.sharingcharger.model.PurchaseModel;

public class ItemPointHistoryRecyclerViewAdapter extends RecyclerView.Adapter<ItemPointHistoryRecyclerViewAdapter.CowViewHolder> {

    private static final String TAG = ItemPointHistoryRecyclerViewAdapter.class.getSimpleName();

    private List<PurchaseModel> list = new ArrayList<>();

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

        PurchaseModel model = list.get(position);

        String getPointUsedType = "";
        String point = "";

        DecimalFormat format = new DecimalFormat("###,###");

        if(model.getPoint() != 0) point = format.format(model.point);

        String type = model.getType();

        //구매 / 지급 포인트 앞에 + 추가
        if(type.equals("PURCHASE") || type.equals("GIVE") ){
            point = "+" + point;
            binding.pointHistoryPoint.setTextColor(ContextCompat.getColor(context, R.color.blue_button));
            if(type.equals("PURCHASE")) getPointUsedType = "구매";
            else getPointUsedType = "포인트 지급";

        //구매 취소 / 환전 / 회수
        } else if(model.getType().equals("PURCHASE_CANCEL") || model.getType().equals("EXCHANGE") || model.getType().equals("WITHDRAW")){
            binding.pointHistoryPoint.setTextColor(ContextCompat.getColor(context, R.color.red));
            if(type.equals("PURCHASE_CANCEL")) getPointUsedType = "구매 취소";
            else if(type.equals("EXCHANGE")) getPointUsedType = "포인트 환전";
            else getPointUsedType = "포인트 회수";
        } else getPointUsedType = "-";

        binding.pointHistoryPoint.setText(point);

        //타입에 따라 승인번호를 보여줄지 / 사유를 보여줄지
        if(type.equals("PURCHASE") || type.equals("PURCHASE_CANCEL")){

            binding.pointHistoryApprovalNumberTxt.setText("승인번호 : " + model.targetName);
            binding.pointHistoryReasonTxt.setVisibility(View.GONE);

        } else if(type.equals("GIVE") || type.equals("WITHDRAW")){

            binding.pointHistoryApprovalNumberTxt.setText("");
            binding.pointHistoryReasonTxt.setText(model.targetName);

        } else {

            binding.pointHistoryApprovalNumberTxt.setText("");
            binding.pointHistoryReasonTxt.setVisibility(View.GONE);

        }

        String getDate = model.created;

        getDate = getDate.replaceAll("T"," ");
        getDate = getDate.substring(0, getDate.length()-3);

        binding.pointHistoryDateTxt.setText(getDate);
        binding.pointHistoryStateTxt.setText(getPointUsedType);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    public void setList(List<PurchaseModel> list) {

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
