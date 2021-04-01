package kr.co.metisinfo.sharingcharger.view.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityHistorySearchConditionBinding;
import kr.co.metisinfo.sharingcharger.utils.DateUtils;

public class HistorySearchConditionActivity extends BaseActivity {

    private static final String TAG = HistorySearchConditionActivity.class.getSimpleName();

    ActivityHistorySearchConditionBinding binding;

    private boolean dateTimePickerFlag = false;

    Calendar maxDate = Calendar.getInstance();

    private String startDate = "";
    private String endDate = "";

    private String getMonth = "1개월";
    private String getType = "전체";
    private String getArray = "최신순";

    private boolean firstClick = false;

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_history_search_condition);

        changeStatusBarColor(false);

    }

    @Override
    public void initViewModel() {

    }

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnClose.setOnClickListener(view -> finish());
        binding.includeHeader.btnReload.setOnClickListener(view -> setReload());
        binding.historySearchBtn.setOnClickListener(view -> searchCondition());

    }

    private void searchCondition() {

        Intent intent = new Intent();

        Log.e(TAG, "getMonth : " + getMonth);

        if (getMonth.equals("직접선택")) {
            getMonth = binding.historySearchStartDate.getText().toString() + "," + binding.historySearchEndDate.getText().toString();
        }

        intent.putExtra("getMonth", getMonth);
        intent.putExtra("getType", getType);
        intent.putExtra("getArray", getArray);

        setResult(RESULT_OK, intent);

        finish();

    }

    @Override
    public void init() {

        Intent intent = getIntent();
        String tagNames = intent.getStringExtra("activityName");

        //충전 이력화면일 경우 유형을 숨겨야함 charge, point
        if (tagNames.contains("Charge")) {

            binding.historySearchMiddleLine.setVisibility(View.GONE);
            binding.historySearchTypeTitle.setVisibility(View.GONE);
            binding.historySearchTypeLayout.setVisibility(View.GONE);

        }
        //point에만 유형이 필요함
        else{
            getType = intent.getStringExtra("getType");
            if (getType.equals("PURCHASE")) {
                getType = "포인트 충전";
                setHistoryTextClick(binding.historySearchSecondType);
            } else if (getType.equals("USED")) {
                getType = "사용";
                setHistoryTextClick(binding.historySearchThirdType);
            } else if (getType.equals("REFUND")) {
                getType = "부분 환불";
                setHistoryTextClick(binding.historySearchFourthType);
            } else {
                getType = "전체";
                setHistoryTextClick(binding.historySearchFirstType);
            }
        }

        getMonth = intent.getStringExtra("getMonthType");
        startDate = intent.getStringExtra("getStartDate");
        endDate = intent.getStringExtra("getEndDate");

        binding.historySearchEndDate.setText(setDate(0));

        Log.e(TAG, "getMonth : " + getMonth);

        if (getMonth.equals("1개월")) {
            binding.historySearchStartDate.setText(setDate(1));
            setHistoryTextClick(binding.historySearchFirstTerm);
        } else if (getMonth.equals("3개월")) {
            binding.historySearchStartDate.setText(setDate(3));
            setHistoryTextClick(binding.historySearchSecondTerm);
        } else if (getMonth.equals("6개월")) {
            binding.historySearchStartDate.setText(setDate(6));
            setHistoryTextClick(binding.historySearchThirdTerm);
        } else {
            firstClick = true;
            setHistoryTextClick(binding.historySearchFourthTerm);
            binding.historySearchStartDate.setText(startDate);
            binding.historySearchEndDate.setText(endDate);
        }
        firstClick = false;

        getArray = intent.getStringExtra("getArray");
        if (getArray.equals("DESC")) {
            getArray = "최신순";
            setHistoryTextClick(binding.historySearchFirstArray);
        } else {
            getArray = "과거순";
            setHistoryTextClick(binding.historySearchSecondArray);
        }

    }

    public void setHistoryTextClick(View view) {

        TextView selectedTextView = (TextView) view;

        ViewGroup viewGroup = (ViewGroup) view.getParent();

        LinearLayout ll = findViewById(viewGroup.getId());
        int childCount = ll.getChildCount();

        boolean textViewFlag = false;

        for (int i = 0; i < childCount; i++) {
            View getView = ll.getChildAt(i);

            if (getView instanceof TextView) {

                TextView textView = (TextView) getView;

                if (ll.getResources().getResourceEntryName(ll.getId()).equals("history_search_term_layout")) {
                    //직접 선택 이라면 dialog
                    if (!selectedTextView.getText().toString().contains("개월") && textViewFlag == false) {

                        dateTimePickerFlag = false;
                        ShowDatePicker();

                    }
                    //개월
                    else if (selectedTextView.getText().toString().contains("개월") && textViewFlag == false) {

                        binding.historySearchEndDate.setText(setDate(0));
                        binding.historySearchStartDate.setText(setDate(Integer.parseInt(selectedTextView.getText().toString().replaceAll("개월", ""))));

                        getMonth = selectedTextView.getText().toString();
                    }
                    textViewFlag = true;
                } else if (ll.getResources().getResourceEntryName(ll.getId()).equals("history_search_type_layout")) {
                    getType = selectedTextView.getText().toString();
                } else {
                    getArray = selectedTextView.getText().toString();
                }

                textView.setBackgroundResource(R.color.transparent);
                textView.setTextAppearance(R.style.history_selected_none);

            }
        }

        selectedTextView.setBackgroundResource(R.color.blue_button);
        selectedTextView.setTextAppearance(R.style.history_selected_selected);

    }

    public String setDate(int getMonth) {

        String getStringDate = DateUtils.setOperationDate("minus", getMonth * 30, "yyyyMMdd");

        int startYYYY = Integer.parseInt(getStringDate.substring(0, 4));
        int startMM = Integer.parseInt(getStringDate.substring(4, 6));
        int startDD = Integer.parseInt(getStringDate.substring(6, 8));

        String getDate = startYYYY + "-" + String.format("%02d", startMM) + "-" + String.format("%02d", startDD);

        return getDate;
    }


    private DatePickerDialog.OnDateSetListener callbackMethod = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            //시작만 클릭했을 경우
            if (!dateTimePickerFlag) {
                dateTimePickerFlag = true;
                startDate = year + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth);
                ShowDatePicker();
            } else {
                endDate = year + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth);

                if (CheckDate(startDate, endDate)) {
                    Toast.makeText(HistorySearchConditionActivity.this, "초기 세팅된 시간보다 이전 시간은 선택할 수 없습니다.\n충전 시작 일시를 다시 선택하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
                } else {
                    binding.historySearchStartDate.setText(startDate);
                    binding.historySearchEndDate.setText(endDate);

                    getMonth = startDate + "," + endDate;

                }
            }

        }
    };

    private void ShowDatePicker() {

        String getStringDate = setDate(0);
        getStringDate = getStringDate.replaceAll("-", "");

        int startYYYY = Integer.parseInt(getStringDate.substring(0, 4));
        int startMM = Integer.parseInt(getStringDate.substring(4, 6));
        int startDD = Integer.parseInt(getStringDate.substring(6, 8));

        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, startYYYY, (startMM - 1), startDD);

        maxDate.set(startYYYY, startMM - 1, startDD);
        dialog.getDatePicker().setMaxDate(maxDate.getTime().getTime()); // DatePicker의 최대 일자 세팅

        if (!firstClick) {
            dialog.show();
        }
    }

    //시작날짜와 종료날짜 체크
    private boolean CheckDate(String date1, String date2) {

        boolean tf = true;

        //날짜 계산
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            Date FirstDate = format.parse(date1);
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(FirstDate);

            Date SecondDate = format.parse(date2);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(SecondDate);
            Log.e("TAG", "FirstDate : " + format.format(FirstDate));
            Log.e("TAG", "SecondDate : " + format.format(SecondDate));
//            1. cal1 == cal2 일 경우 return 0
//
//            2. cal1 > cal2 일 경우 return 1
//
//            3. cal1 < cal2 일 경우 return -1

            // -1일경우 false
            Log.e("TAG", "cal1 compareTo cal2 : " + cal1.compareTo(cal2));
            if(cal1.compareTo(cal2) == -1){
                tf = false;
            }

        }catch (Exception e){
            Log.e("TAG", "Exception CheckDate: " + e);
        }

        return tf;
    }

    private void setReload() {

        setHistoryTextClick(binding.historySearchFirstTerm);
        setHistoryTextClick(binding.historySearchFirstType);
        setHistoryTextClick(binding.historySearchFirstArray);

    }

}
