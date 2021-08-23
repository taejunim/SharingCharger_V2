package kr.co.metisinfo.sharingcharger.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.databinding.ListAdminChargerManageItemBinding;
import kr.co.metisinfo.sharingcharger.model.ChargerModel;

public class ItemAdminChargerManageRecyclerViewAdapter extends RecyclerView.Adapter<ItemAdminChargerManageRecyclerViewAdapter.CowViewHolder> {

    public interface OnListItemSelected {
        void sendViewDataToFragment(int position);
    }

    public OnListItemSelected onListItemSelected;

    private List<ChargerModel> list = new ArrayList<>();

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
        binding.pointHistoryStateTxt.setText("충전중");

    }

    @Override
    public int getItemCount() {
        return 13;
        //return list.size();
    }

    public void setList(List<ChargerModel> list) {

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

