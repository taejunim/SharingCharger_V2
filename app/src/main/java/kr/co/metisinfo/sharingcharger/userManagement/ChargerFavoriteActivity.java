package kr.co.metisinfo.sharingcharger.userManagement;

import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import kr.co.metisinfo.sharingcharger.Adapter.ItemBookmarkRecyclerViewAdapter;
import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityBookmarkBinding;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.view.activity.CustomDialog;

public class ChargerFavoriteActivity extends BaseActivity{

    private static final String TAG = ChargerFavoriteActivity.class.getSimpleName();

    ActivityBookmarkBinding binding;

    private ItemBookmarkRecyclerViewAdapter bookmarkAdapter;

    private CustomDialog bookmarkDialog;

    ApiUtils apiUtils = new ApiUtils();

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_bookmark);

        changeStatusBarColor(false);

    }

    @Override
    public void initViewModel() {

        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);

        // bookmarkAdapter.setList(list);

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

        bookmarkAdapter = new ItemBookmarkRecyclerViewAdapter(this);

        binding.bookmarkTableRecycler.setAdapter(bookmarkAdapter);

    }

}
