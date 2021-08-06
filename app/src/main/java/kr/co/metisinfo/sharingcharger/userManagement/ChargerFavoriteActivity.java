package kr.co.metisinfo.sharingcharger.userManagement;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import kr.co.metisinfo.sharingcharger.Adapter.ItemBookmarkRecyclerViewAdapter;
import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.ActivityBookmarkBinding;
import kr.co.metisinfo.sharingcharger.model.ChargerModel;
import kr.co.metisinfo.sharingcharger.model.SearchKeywordModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.view.activity.CustomDialog;
import kr.co.metisinfo.sharingcharger.view.viewInterface.MasterBaseView;
import kr.co.metisinfo.sharingcharger.viewModel.BookmarkViewModel;

public class ChargerFavoriteActivity extends BaseActivity implements MasterBaseView {

    private static final String TAG = ChargerFavoriteActivity.class.getSimpleName();

    ActivityBookmarkBinding binding;

    private ItemBookmarkRecyclerViewAdapter bookmarkAdapter;

    private CustomDialog bookmarkDialog;

    private BookmarkViewModel bookmarkViewModel;

    ApiUtils apiUtils = new ApiUtils();

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_bookmark);

        changeStatusBarColor(false);

        bookmarkViewModel = new ViewModelProvider(this).get(BookmarkViewModel.class);

    }

    @Override
    public void initViewModel() {

        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);

        bookmarkViewModel.selectAllBookmark(ThisApplication.staticUserModel.id).observe(this, bookmarkResponse -> {

            if(bookmarkResponse != null) {

                List<ChargerModel> list = new ArrayList<>();

                try{

                    for(int i = 0 ; i < bookmarkResponse.size(); i++){

                        ChargerModel vo = apiUtils.getChargerInfo(bookmarkResponse.get(i).chargerId);

                        if(vo != null){
                            list.add(vo);
                        }

                    }

                    bookmarkAdapter.setList(list);
                }catch (Exception e ){

                    Log.e(TAG,"getChargerInfo Exception : " + e);
                }

            }
        });

    }

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnBack.setOnClickListener(view -> finish());

    }

    @Override
    public void init() {

        binding.includeHeader.txtTitle.setText(R.string.bookmark_title);

        initAdapter();

    }

    private void initAdapter() {

        binding.bookmarkTableRecycler.setLayoutManager(new LinearLayoutManager(this));

        bookmarkAdapter = new ItemBookmarkRecyclerViewAdapter(this, this);

        binding.bookmarkTableRecycler.setAdapter(bookmarkAdapter);

    }

    @Override
    public void onClickMasterSeq(Object obj, int getId, boolean isClick) {

        bookmarkDialog = new CustomDialog(this, "삭제하시겠습니까?");

        bookmarkDialog.show();

        bookmarkDialog.findViewById(R.id.dialog_no_btn).setOnClickListener(view -> bookmarkDialog.dismiss());

        bookmarkDialog.findViewById(R.id.dialog_ok_btn).setOnClickListener(view -> {

            bookmarkDialog.dismiss();
            bookmarkViewModel.deleteBookmarkItem(ThisApplication.staticUserModel.id, getId);

        });

    }

    @Override
    public void onClickMasterNm(Object obj, int position) {

        ChargerModel getModel = (ChargerModel) obj;

        SearchKeywordModel model = new SearchKeywordModel();

        model.x = String.valueOf(getModel.gpsX);
        model.y = String.valueOf(getModel.gpsY);
        model.placeName = getModel.name;

        Intent intent = new Intent();

        intent.putExtra("keyword", model);
        intent.putExtra("type", "bookmark");

        setResult(RESULT_OK, intent);

        finish();

    }
}
