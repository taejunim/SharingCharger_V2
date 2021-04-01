package kr.co.metisinfo.sharingcharger.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.databinding.SearchKeywordItemListBinding;
import kr.co.metisinfo.sharingcharger.model.SearchKeywordModel;
import kr.co.metisinfo.sharingcharger.view.viewInterface.SearchKeywordInterface;

public class ItemSearchKeywordRecyclerViewAdapter extends RecyclerView.Adapter<ItemSearchKeywordRecyclerViewAdapter.CowViewHolder>  {


    private static final String TAG = ItemSearchKeywordRecyclerViewAdapter.class.getSimpleName();

    private List<SearchKeywordModel> list = new ArrayList<>();

    private Context context;
    private SearchKeywordInterface keywordInterface;

    public ItemSearchKeywordRecyclerViewAdapter(Context context, SearchKeywordInterface keywordInterface){

        this.context = context;
        this.keywordInterface = keywordInterface;

    }

    @NonNull
    @Override
    public ItemSearchKeywordRecyclerViewAdapter.CowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchKeywordItemListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.search_keyword_item_list, parent, false);

        return new ItemSearchKeywordRecyclerViewAdapter.CowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemSearchKeywordRecyclerViewAdapter.CowViewHolder holder, int position) {

        SearchKeywordItemListBinding binding = holder.binding;

        SearchKeywordModel model = list.get(position);

        binding.keywordItemPlaceName.setText(model.placeName);
        binding.keywordItemCategoryName.setText(model.categoryGroupName);
        binding.keywordItemAddress.setText(model.addressName);

        if(model.phone == null || model.phone.equals("")){
            binding.keywordPhone.setVisibility(View.GONE);
        }else{
            binding.keywordPhone.setText(model.phone);
        }


        binding.layout.setOnClickListener(view -> {

            keywordInterface.onClickAddr(model, position);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<SearchKeywordModel> list) {

        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    static class CowViewHolder extends RecyclerView.ViewHolder {

        public SearchKeywordItemListBinding binding;

        public CowViewHolder(@NonNull SearchKeywordItemListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
