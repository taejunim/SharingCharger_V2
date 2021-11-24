package kr.co.metisinfo.sharingcharger.view.activity;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import kr.co.metisinfo.sharingcharger.Adapter.ItemAdminMonthlyProfitPointRecyclerViewAdapter;
import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityAdminMonthlyProfitPointBinding;
import kr.co.metisinfo.sharingcharger.model.AdminMonthlyProfitPointModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.CommonUtils;
import kr.co.metisinfo.sharingcharger.utils.DateUtils;

//소유주의 월별 수익 포인트
public class AdminMonthlyProfitPointActivity extends BaseActivity {

    private static final String TAG = AdminMonthlyProfitPointActivity.class.getSimpleName();

    ActivityAdminMonthlyProfitPointBinding binding;

    private ItemAdminMonthlyProfitPointRecyclerViewAdapter adminMonthlyProfitPointRecyclerViewAdapter;

    List<AdminMonthlyProfitPointModel> list = new ArrayList<>();

    ApiUtils apiUtils = new ApiUtils();
    CommonUtils commonUtils = new CommonUtils();

    public String currentYear = "";
    public String searchYear = "";
    int searchMonth = 12;

    public int duration = 10;
    public String[] years = new String[duration];

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_monthly_profit_point);

        changeStatusBarColor(false);

    }

    @Override
    public void initViewModel() {

        try {

            initializeDate(0);

            //현재 년도에서 10년 전까지 계산
            for(int i = 0; i < duration ; i ++){
                years[i] = (Integer.parseInt(searchYear) - i) + " 년";
            }

            //현재년도 ~ 10년 전년도까지 데이터 팝업
            ArrayAdapter<String> year_items = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, years );

            binding.spinnerYear.setAdapter(year_items);

            //년도 클릭
            binding.spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    searchYear = years[position].replaceAll(" 년","");
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                    getAdminMonthlyProfitPoint();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        } catch (Exception e) {
            Log.e(TAG, "point Exception : " + e);
        }

    }

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnBack.setOnClickListener(view -> finish());

    }

    @Override
    public void init() {

        binding.includeHeader.txtTitle.setText(R.string.dashboard_monthly_profit_point_title );
        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);

        RelativeLayout.LayoutParams imageLayoutParams = new RelativeLayout.LayoutParams(commonUtils.convertToDp(20F), commonUtils.convertToDp(21.3F));
        imageLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        imageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        imageLayoutParams.setMarginEnd(commonUtils.convertToDp(15F));

        binding.includeHeader.layoutHeaderMenu.setBackground(ContextCompat.getDrawable(this, R.mipmap.menu_list));
        binding.includeHeader.layoutHeaderMenu.setLayoutParams(imageLayoutParams);

        initAdapter();

    }

    private void initAdapter() {

        binding.monthlyProfitPointRecycler.setLayoutManager(new LinearLayoutManager(this));

        adminMonthlyProfitPointRecyclerViewAdapter = new ItemAdminMonthlyProfitPointRecyclerViewAdapter(this);

        binding.monthlyProfitPointRecycler.setAdapter(adminMonthlyProfitPointRecyclerViewAdapter);

    }

    private void getAdminMonthlyProfitPoint() {

        try {
            //선택한 년도의 월별 수익 포인트 API 요청
            Map<String, Object> map = apiUtils.getAdminMonthlyProfitPoint(searchYear);
            list = (List) map.get("list");

            if(searchYear.equals(currentYear)){
                for(int i = 0; i < 12 - searchMonth; i ++)
                list.remove(list.size()-1);
            }

            sortMonthlyProfitPointList(); //조회된 데이터 월별로 정렬
            adminMonthlyProfitPointRecyclerViewAdapter.setList(list);

        } catch (Exception e) {
            Log.e(TAG, "getAdminMonthlyProfitPoint Exception : " + e);
        }
    }

    //날짜 초기화
    public void initializeDate(int getMonth) {

        //현재 월에서 1월 까지 구하기
        String getStringDate = DateUtils.setOperationDate("minus", getMonth * 30, "yyyyMMdd");

        currentYear = getStringDate.substring(0, 4);
        searchYear = getStringDate.substring(0, 4);
        searchMonth = Integer.parseInt(getStringDate.substring(4, 6));
    }

    /**
     * 월별 수익 포인트 리스트 내림차순 정렬
     */
    public void sortMonthlyProfitPointList() {

        Collections.sort(list, new Comparator<AdminMonthlyProfitPointModel>() {
            @Override
            public int compare(AdminMonthlyProfitPointModel s1, AdminMonthlyProfitPointModel s2) {

                int temp1 = Integer.parseInt(s1.getDay().substring(s2.getDay().length()-2));
                int temp2 = Integer.parseInt(s2.getDay().substring(s2.getDay().length()-2));

                if (temp1 > temp2) {
                    return -1;
                } else if (temp1 < temp2) {
                    return 1;
                } else return 0;
            }
        });
    }
}
