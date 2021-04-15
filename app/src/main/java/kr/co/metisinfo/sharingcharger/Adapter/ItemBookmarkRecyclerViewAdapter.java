package kr.co.metisinfo.sharingcharger.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.databinding.BookmarkItemListBinding;
import kr.co.metisinfo.sharingcharger.model.ChargerModel;

public class ItemBookmarkRecyclerViewAdapter extends RecyclerView.Adapter<ItemBookmarkRecyclerViewAdapter.CowViewHolder> {

    private static final String TAG = ItemBookmarkRecyclerViewAdapter.class.getSimpleName();

    private List<ChargerModel> list = new ArrayList<>();

    private Context context;


    public ItemBookmarkRecyclerViewAdapter(Context context){

        this.context = context;

    }

    @NonNull
    @Override
    public CowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        BookmarkItemListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.bookmark_item_list, parent, false);

        return new CowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CowViewHolder cowViewHolder, int position) {

        BookmarkItemListBinding binding = cowViewHolder.binding;

        ChargerModel model = list.get(position);

        binding.bookmarkNameTxt.setText(model.name);

        binding.bookmarkAddressTxt.setText(model.address);

                                                                //실제 데이터 받아올때 model 넘겨줘야함(이름, 위도, 경도)
        binding.bookmarkNavigationBtn.setOnClickListener(view -> goMap(model));

        binding.bookmarkDeleteBtn.setOnClickListener(view -> deleteBtnClick(model.id));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<ChargerModel> list) {

        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    private void goMap(ChargerModel model) {

  //   baseView.onClickMasterNm(model,1);

    }

    private void deleteBtnClick(int id) {

    //    baseView.onClickMasterSeq(new Object(), id,true);

    }

    static class CowViewHolder extends RecyclerView.ViewHolder {

        public BookmarkItemListBinding binding;

        public CowViewHolder(@NonNull BookmarkItemListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

