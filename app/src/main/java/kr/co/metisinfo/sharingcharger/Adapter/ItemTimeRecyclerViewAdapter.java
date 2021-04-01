package kr.co.metisinfo.sharingcharger.Adapter;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.charzin.evzsdk.EvzBLEData;

import java.util.ArrayList;
import java.util.List;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.databinding.ChargerListItemListBinding;
import kr.co.metisinfo.sharingcharger.view.viewInterface.MasterBaseView;

public class ItemTimeRecyclerViewAdapter extends RecyclerView.Adapter<ItemTimeRecyclerViewAdapter.CowViewHolder> {

    private static final String TAG = ItemTimeRecyclerViewAdapter.class.getSimpleName();

    private List<String> list = new ArrayList<>();

    private MasterBaseView baseView;

    private RadioButton lastCheckedRB = null;

    private Context context;

    private boolean isFirst = true;

    public int getIndex = 7;

    public ItemTimeRecyclerViewAdapter(Context context, MasterBaseView baseView){

        this.context = context;
        this.baseView = baseView;

    }

    @NonNull
    @Override
    public ItemTimeRecyclerViewAdapter.CowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ChargerListItemListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.charger_list_item_list, parent, false);

        return new ItemTimeRecyclerViewAdapter.CowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemTimeRecyclerViewAdapter.CowViewHolder cowViewHolder, int position) {

        ChargerListItemListBinding binding = cowViewHolder.binding;

        binding.chargerListRelativeLayout.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, context.getResources().getDisplayMetrics());

        binding.chargerListTxt.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics()));

        binding.chargerListTxt.setText(list.get(position));

        Log.e(TAG,"position : "+ position);
        Log.e(TAG,"getIndex : "+ getIndex);

        if(isFirst){

            if(position == getIndex){

                lastCheckedRB = binding.chargerListTxt;
                binding.chargerListTxt.setChecked(true);
                isFirst = false;
            }

        }

        //클릭시 연결됨 보이기
        binding.chargerListTxt.setOnClickListener(view -> deviceClick(view, list.get(position)));

    }

    public void deviceClick(View view, String getData) {

        RadioButton getBtn = (RadioButton) view;
        getBtn.setChecked(true);

        if (lastCheckedRB != null) {
            lastCheckedRB.setChecked(false);
        }

        lastCheckedRB = getBtn;

        baseView.onClickMasterNm(getData,1);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<String> list) {

        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    static class CowViewHolder extends RecyclerView.ViewHolder {

        public ChargerListItemListBinding binding;

        public CowViewHolder(@NonNull ChargerListItemListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

