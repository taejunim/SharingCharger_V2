package kr.co.metisinfo.sharingcharger.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.metisinfo.sharingcharger.Adapter.ItemSearchKeywordRecyclerViewAdapter;
import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.WebServiceAPI;
import kr.co.metisinfo.sharingcharger.databinding.ActivitySearchKeywordBinding;
import kr.co.metisinfo.sharingcharger.model.SearchKeywordModel;
import kr.co.metisinfo.sharingcharger.network.RetrofitFactory;
import kr.co.metisinfo.sharingcharger.view.viewInterface.SearchKeywordInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchKeywordActivity extends BaseActivity implements SearchKeywordInterface {

    ActivitySearchKeywordBinding binding;

    private static final String TAG = SearchKeywordActivity.class.getSimpleName();

    private ItemSearchKeywordRecyclerViewAdapter keywordAdapter;

    private List<SearchKeywordModel> list;

    private String centerLocation = "내위치";

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_keyword);

        changeStatusBarColor(false);

    }

    @Override
    public void initViewModel() {


    }

    @Override
    public void setOnClickListener() {

        binding.btnClose.setOnClickListener(view -> {

            Intent intent = new Intent();

            setResult(RESULT_CANCELED, intent);

            finish();

        });

        binding.searchKeywordMyPlaceTxt.setOnClickListener(view -> {
            checkCPlaceClick(true);
        });

        binding.searchKeywordMyPlaceImg.setOnClickListener(view -> {
            checkCPlaceClick(true);
        });

        binding.searchKeywordMapPlaceTxt.setOnClickListener(view -> {
            checkCPlaceClick(false);
        });

        binding.searchKeywordMapPlaceImg.setOnClickListener(view -> {
            checkCPlaceClick(false);
        });

        // 검색
        binding.editSearchKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        // 검색 동작
                        break;
                    default:
                        // 기본 엔터키 동작
                        if (v.getText().toString().trim().length() == 0) {

                            Toast.makeText(SearchKeywordActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();

                        } else {

                            searchKeyword(v.getText().toString().trim());

                        }
                }
                return true;
            }
        });

    }

    @Override
    public void init() {

        initAdapter();
        checkCPlaceClick(true);

        binding.editSearchKeyword.requestFocus();
    }

    private void searchKeyword(String keyword) {

        RetrofitFactory retrofitFactory = new RetrofitFactory();

        WebServiceAPI webServiceAPI = retrofitFactory.buildKakao().create(WebServiceAPI.class);

        webServiceAPI.getSearchKeyword(keyword).enqueue(new Callback<Object>() {

            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {

                // 호출한 url 응답 성공시
                if (response.isSuccessful()) {

                    if (response.body() != null) {

                        try {

                            JSONObject json = new JSONObject((Map) response.body());

                            JSONArray contacts = json.getJSONArray("documents");

                            if (list == null) {
                                list = new ArrayList<>();
                            } else {
                                list.clear();
                            }

                            for (int i = 0; i < contacts.length(); i++) {
                                Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

                                SearchKeywordModel vo = gson.fromJson(contacts.getJSONObject(i).toString(), SearchKeywordModel.class);

                                list.add(vo);
                            }
                            keywordAdapter.setList(list);
                        } catch (JSONException je) {

                            Log.e(TAG, "JSONException : " + je.getMessage());
                        }

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {

                // url 호출 후 오류 났을 때
                Log.e(TAG, "getSearchKeyword error : " + t.getMessage());
            }
        });

    }

    private void initAdapter() {

        binding.searchKeywordRecycler.setLayoutManager(new LinearLayoutManager(this));

        keywordAdapter = new ItemSearchKeywordRecyclerViewAdapter(this, this);

        binding.searchKeywordRecycler.setAdapter(keywordAdapter);

    }

    public void checkCPlaceClick(boolean chk) {

        if (chk) {
            binding.searchKeywordMyPlaceTxt.setTextColor(Color.BLACK);
            binding.searchKeywordMyPlaceImg.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.correct_blue));

            binding.searchKeywordMapPlaceTxt.setTextColor(Color.GRAY);
            binding.searchKeywordMapPlaceImg.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.correct_gray));

            centerLocation = "내위치";

        } else {
            binding.searchKeywordMapPlaceTxt.setTextColor(Color.BLACK);
            binding.searchKeywordMapPlaceImg.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.correct_blue));

            binding.searchKeywordMyPlaceTxt.setTextColor(Color.GRAY);
            binding.searchKeywordMyPlaceImg.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.correct_gray));

            centerLocation = "지도";

        }

    }

    @Override
    public void onClickSearchKeyword(Object obj, int position) {

        SearchKeywordModel model = (SearchKeywordModel) obj;

        Intent intent = new Intent();

        intent.putExtra("keyword", model);

        intent.putExtra("centerLocation", centerLocation);

        setResult(RESULT_OK, intent);

        finish();
    }

}
