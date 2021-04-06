package kr.co.metisinfo.sharingcharger.view.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityMarkerSearchConditionBinding;
import kr.co.metisinfo.sharingcharger.utils.DateUtils;
import kr.co.metisinfo.sharingcharger.utils.MyTimePicker;

public class MarkerSearchConditionActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = MarkerSearchConditionActivity.class.getSimpleName();

    ActivityMarkerSearchConditionBinding binding;

    private int chargingMinute = -1;        // 총 xx 분

    private int chargingStartYYYY = -1;     // 메인으로 부터 넘어온 년도.
    private int chargingStartMM = -1;
    private int chargingStartDD = -1;
    private int chargingStartHH = -1;
    private int chargingStartII = -1;

    private int chargingEndYYYY = -1;
    private int chargingEndMM = -1;
    private int chargingEndDD = -1;
    private int chargingEndHH = -1;
    private int chargingEndII = -1;

    private String chargingStartWeek = "";
    private String chargingEndWeek = "";

    private String selectStartWeek;

    private String selectStartFullDate;
    private int selectStartYYYY = 0;
    private int selectStartMM = 0;
    private int selectStartDD = 0;

    private int selectStartHH = 0;
    private int selectStartII = 0;

    private String selectEndFullDate;
    private int selectEndYYYY = 0;
    private int selectEndMM = 0;
    private int selectEndDD = 0;

    private int selectEndHH = 0;
    private int selectEndII = 0;

    private String reserveRadius = "";

    private int oldPosition = 0;

    private boolean checkChange = true;

    Calendar minDate = Calendar.getInstance(Locale.getDefault());
    Calendar maxDate = Calendar.getInstance(Locale.getDefault());

    List<CharSequence> distanceEntry = new ArrayList<>();
    ArrayList<String> timeList = new ArrayList<>();

    int startWeek = 0;
    int endWeek = 0;

    @Override
    public void initLayout() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_marker_search_condition);

        changeStatusBarColor(false);

        maxDate.add(Calendar.DATE, 1);
    }

    @Override
    public void initViewModel() {

    }

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnClose.setOnClickListener(view -> {

            Intent intent = new Intent();

            setResult(RESULT_CANCELED, intent);

            finish();

        });

        binding.layoutSDate.setOnClickListener(this::clickDatePicker);

        binding.layoutEDate.setOnClickListener(this::clickDatePicker);

        binding.txtKm.setOnClickListener(view -> binding.spinnerLengthRange.performClick());

        binding.dropdown3.setOnClickListener(view -> binding.spinnerLengthRange.performClick());

        binding.layoutEDate.setOnClickListener(view -> binding.spinnerTimeRange.performClick());

        binding.includeHeader.btnReload.setOnClickListener(view -> {

            binding.spinnerTimeRange.setSelection(7);  // 기본 4시간
            binding.spinnerLengthRange.setSelection(1); // 거리 Spinner에서 두번째 값을 강제 선택
            chargeBtnClick(binding.goCharging);

        });

        // 확인 버튼 클릭 시
        binding.btnOk.setOnClickListener(view -> {

            if (dateValidationCheck()) {

                Intent intent = new Intent();

                intent.putExtra("chargingMinute", chargingMinute);

                intent.putExtra("chargingStartWeek", chargingStartWeek);

                intent.putExtra("chargingStartYYYY", Integer.parseInt(selectStartFullDate.substring(0, 4)));
                intent.putExtra("chargingStartMM", Integer.parseInt(selectStartFullDate.substring(4, 6)));
                intent.putExtra("chargingStartDD", Integer.parseInt(selectStartFullDate.substring(6, 8)));
                intent.putExtra("chargingStartHH", Integer.parseInt(selectStartFullDate.substring(8, 10)));
                intent.putExtra("chargingStartII", Integer.parseInt(selectStartFullDate.substring(10, 12)));

                intent.putExtra("chargingEndWeek", chargingEndWeek);
                intent.putExtra("chargingEndYYYY", Integer.parseInt(selectEndFullDate.substring(0, 4)));
                intent.putExtra("chargingEndMM", Integer.parseInt(selectEndFullDate.substring(4, 6)));
                intent.putExtra("chargingEndDD", Integer.parseInt(selectEndFullDate.substring(6, 8)));
                intent.putExtra("chargingEndHH", Integer.parseInt(selectEndFullDate.substring(8, 10)));
                intent.putExtra("chargingEndII", Integer.parseInt(selectEndFullDate.substring(10, 12)));

                intent.putExtra("reserveRadius", binding.spinnerLengthRange.getSelectedItem().toString());

                Log.e(TAG, "btnOk checkChange : " + checkChange);

                intent.putExtra("checkChange", String.valueOf(checkChange));

                Log.e(TAG, "binding.spinnerLengthRange.getSelectedItem().toString()1 : " + binding.spinnerLengthRange.getSelectedItem().toString());

                setResult(RESULT_OK, intent);

                finish();
            }
        });
    }

    @Override
    public void init() {

        distanceEntry = Arrays.asList(getResources().getTextArray(R.array.array_distance_radius));

        setTimeList();

        ArrayAdapter<CharSequence> lengthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, distanceEntry);

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, timeList);

        binding.spinnerTimeRange.setAdapter(timeAdapter);

        //충전 시간 변경
        binding.spinnerTimeRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                String getTime = timeList.get(position).trim();

                int totalMinute = calculationMinute(getTime);

                //충전일이 2일이 넘어가는지 확인
                checkDate("onItemSelected", totalMinute, 0, 0);

                if (startWeek == endWeek) {
                    Toast.makeText(MarkerSearchConditionActivity.this, "최대 2일을 넘어서 선택할 수 없습니다.", Toast.LENGTH_LONG).show();
                    binding.spinnerTimeRange.setSelection(oldPosition);
                    return;
                }

                oldPosition = position;
                binding.txtEDate.setText(getTime);
                binding.txtChargingTime.setText(getTime);

                setTimeTitle(getTime);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerLengthRange.setAdapter(lengthAdapter);
        binding.spinnerLengthRange.setOnItemSelectedListener(this);

        initAll(true);

    }

    /**
     * 충전 시작일자 달력 DatePickerListener
     */
    private DatePickerDialog.OnDateSetListener startDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            selectStartYYYY = year;
            selectStartMM = monthOfYear + 1;
            selectStartDD = dayOfMonth;

            if (chargingStartDD == selectStartDD) { // 일자가 같은 경우 시간 선택을 현재 시간 이전 날짜는 선택안되게 막기!
                Log.e(TAG, "chargingStartDD == selectStartDD : ");
                Log.e(TAG, "chargingStartDD == selectStartDD : Time : " + selectStartFullDate.substring(8, 10) + " : " + selectEndFullDate.substring(10, 12));
                MyTimePicker timePicker = new MyTimePicker(MarkerSearchConditionActivity.this, startTimePickerListener, Integer.parseInt(selectStartFullDate.substring(8, 10)), Integer.parseInt(selectEndFullDate.substring(10, 12)), true);

                Objects.requireNonNull(timePicker.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                timePicker.setCancelable(false);
                timePicker.show();

            } else {

                Log.e(TAG, "chargingStartDD != selectStartDD : ");
                MyTimePicker dialog = new MyTimePicker(MarkerSearchConditionActivity.this, startTimePickerListener, 0, 0, true);

                dialog.setCancelable(false);

                dialog.show();
            }
        }
    };

    private TimePickerDialog.OnTimeSetListener startTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

            Log.e(TAG, "selectStartMM : " + selectStartMM);
            Log.e(TAG, "minDate : " + minDate.get(Calendar.MONTH));

            if (selectStartYYYY == minDate.get(Calendar.YEAR)) {

                if (selectStartMM == minDate.get(Calendar.MONTH) + 1) {

                    if (selectStartDD == minDate.get(Calendar.DATE)) {

                        Log.e(TAG, "selectedHour : " + selectedHour);
                        Log.e(TAG, "minDate.get(Calendar.HOUR) : " + minDate.get(Calendar.HOUR_OF_DAY));

                        Log.e(TAG, "selectedMinute : " + selectedMinute);
                        Log.e(TAG, "minDate.get(Calendar.MINUTE) : " + minDate.get(Calendar.MINUTE));

                        if ((selectedHour <= minDate.get(Calendar.HOUR_OF_DAY) && selectedMinute < minDate.get(Calendar.MINUTE) || selectedHour < minDate.get(Calendar.HOUR_OF_DAY))) {

                            Toast.makeText(MarkerSearchConditionActivity.this, "초기 세팅된 시간보다 이전 시간은 선택할 수 없습니다.\n충전 시작 일시를 다시 선택하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

                            DatePickerDialog dialog = new DatePickerDialog(MarkerSearchConditionActivity.this, startDatePickerListener, Integer.parseInt(selectStartFullDate.substring(0, 4)), Integer.parseInt(selectStartFullDate.substring(4, 6)) - 1, Integer.parseInt(selectStartFullDate.substring(6, 8)));
                            dialog.getDatePicker().setMinDate(minDate.getTime().getTime()); // DatePicker의 최소 일자 세팅
                            dialog.getDatePicker().setMaxDate(maxDate.getTime().getTime()); // DatePicker의 최대 일자 세팅

                            dialog.show();

                            return;
                        }
                    }
                }
            }

            int tempMinute = calculationMinute(binding.spinnerTimeRange.getSelectedItem().toString());

            checkDate("onTimeSet", tempMinute, selectedHour, selectedMinute);

            //2일이 지나면 선택X
            if (startWeek == endWeek) {
                Toast.makeText(MarkerSearchConditionActivity.this, "최대 2일을 넘어서 선택할 수 없습니다.", Toast.LENGTH_LONG).show();

                selectStartYYYY = chargingStartYYYY;
                selectStartMM = chargingStartMM;
                selectStartDD = chargingStartDD;
                DatePickerDialog dialog = new DatePickerDialog(MarkerSearchConditionActivity.this, startDatePickerListener, chargingStartYYYY, chargingStartMM - 1, chargingStartDD);

                dialog.getDatePicker().setMinDate(minDate.getTime().getTime()); // DatePicker의 최소 일자 세팅
                dialog.getDatePicker().setMaxDate(maxDate.getTime().getTime()); // DatePicker의 최대 일자 세팅

                dialog.show();

                return;
            }

            String mm = String.format("%02d", selectStartMM);
            String dd = String.format("%02d", selectStartDD);

            String choiceDate = selectStartYYYY + mm + dd;

            selectStartWeek = DateUtils.getWeek(choiceDate);
            selectStartHH = selectedHour;
            selectStartII = selectedMinute;

            binding.txtSDate.setText(mm + "/" + dd + " " + selectStartWeek + " " + String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute));

            selectStartFullDate = String.format("%04d", selectStartYYYY) + String.format("%02d", selectStartMM) + String.format("%02d", selectStartDD) + String.format("%02d", selectStartHH) + String.format("%02d", selectStartII);

            chargeBtnClick(binding.reserveCharging);

            setChargingFullDate(true);
        }
    };

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void setChargingFullDate(boolean isSDate) {

        if (isSDate) {

            binding.txtChargingDate1.setText(
                    String.format("%02d", selectStartMM)
                            + "/" + String.format("%02d", selectStartDD)
                            + " " + selectStartWeek
                            + " " + String.format("%02d", selectStartHH)
                            + ":" + String.format("%02d", selectStartII)
            );

        } else {

            binding.txtChargingDate2.setText(String.format("%02d", selectEndHH) + ":" + String.format("%02d", selectEndII));
        }
        Log.e(TAG, "txtChargingDate1 : " + selectStartMM);

        long time = DateUtils.getCompareTwoDate(selectStartFullDate, selectEndFullDate);

        Log.e(TAG, "시작일자 종료일자 차이 : " + time);

        String temp = DateUtils.generateTimeToString((int) time);

        chargingMinute = (int) time;

        binding.txtChargingTime.setText(temp);
    }

    private void clickDatePicker(View view) {

        DatePickerDialog dialog = null;

        if (view.getId() == binding.layoutSDate.getId()) {

            Log.e(TAG, "layoutSDate : ");
            Log.e(TAG, "layoutSDate : " + Integer.parseInt(selectStartFullDate.substring(0, 4)) + "-" + Integer.parseInt(selectStartFullDate.substring(4, 6)) + "-" + Integer.parseInt(selectStartFullDate.substring(6, 8)));

            dialog = new DatePickerDialog(MarkerSearchConditionActivity.this, startDatePickerListener, Integer.parseInt(selectStartFullDate.substring(0, 4)), Integer.parseInt(selectStartFullDate.substring(4, 6)) - 1, Integer.parseInt(selectStartFullDate.substring(6, 8)));
        }

        dialog.getDatePicker().setMinDate(minDate.getTime().getTime()); // DatePicker의 최소 일자 세팅
        dialog.getDatePicker().setMaxDate(maxDate.getTime().getTime()); // DatePicker의 최대 일자 세팅
        dialog.setCancelable(false);

        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        binding.txtKm.setText(distanceEntry.get(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * 초기화 함수
     * <p>
     * isInit == true : 최초 진입
     * isInit == false : reLoad 버튼 클릭릭
     * param isInit 최초 진입 여부
     */
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void initAll(boolean isInit) {

        if (isInit) {

            chargingMinute = getIntent().getIntExtra("chargingMinute", -1);

            chargingStartWeek = getIntent().getStringExtra("chargingStartWeek");
            chargingStartYYYY = getIntent().getIntExtra("chargingStartYYYY", -1);
            chargingStartMM = getIntent().getIntExtra("chargingStartMM", -1);
            chargingStartDD = getIntent().getIntExtra("chargingStartDD", -1);
            chargingStartHH = getIntent().getIntExtra("chargingStartHH", -1);
            chargingStartII = getIntent().getIntExtra("chargingStartII", -1);

            chargingEndWeek = getIntent().getStringExtra("chargingEndWeek");
            chargingEndYYYY = getIntent().getIntExtra("chargingEndYYYY", -1);
            chargingEndMM = getIntent().getIntExtra("chargingEndMM", -1);
            chargingEndDD = getIntent().getIntExtra("chargingEndDD", -1);
            chargingEndHH = getIntent().getIntExtra("chargingEndHH", -1);
            chargingEndII = getIntent().getIntExtra("chargingEndII", -1);

            reserveRadius = getIntent().getStringExtra("reserveRadius");

            String temp = getIntent().getStringExtra("checkChange");

            Log.e(TAG, "checkChange temp : " + temp);

            checkChange = Boolean.valueOf(temp);

            selectStartFullDate = String.format("%04d", chargingStartYYYY) + String.format("%02d", chargingStartMM) + String.format("%02d", chargingStartDD) + String.format("%02d", chargingStartHH) + String.format("%02d", chargingStartII);
            selectEndFullDate = String.format("%04d", chargingEndYYYY) + String.format("%02d", chargingEndMM) + String.format("%02d", chargingEndDD) + String.format("%02d", chargingEndHH) + String.format("%02d", chargingEndII);

            Log.e(TAG, "checkChange isInit : " + checkChange);

        } else {

            chargingMinute = 30;

            selectStartFullDate = setFullDateTime(true);
            selectEndFullDate = setFullDateTime(false);   // 종료 일시 yyyyMMDDHHII

            chargingStartYYYY = Integer.parseInt(selectStartFullDate.substring(0, 4));
            chargingStartMM = Integer.parseInt(selectStartFullDate.substring(4, 6));
            chargingStartDD = Integer.parseInt(selectStartFullDate.substring(6, 8));
            chargingStartHH = Integer.parseInt(selectStartFullDate.substring(8, 10));
            chargingStartII = Integer.parseInt(selectStartFullDate.substring(10, 12));

            chargingEndYYYY = Integer.parseInt(selectEndFullDate.substring(0, 4));
            chargingEndMM = Integer.parseInt(selectEndFullDate.substring(4, 6));
            chargingEndDD = Integer.parseInt(selectEndFullDate.substring(6, 8));
            chargingEndHH = Integer.parseInt(selectEndFullDate.substring(8, 10));
            chargingEndII = Integer.parseInt(selectEndFullDate.substring(10, 12));
        }

        selectStartYYYY = chargingStartYYYY;
        selectStartMM = chargingStartMM;
        selectStartDD = chargingStartDD;
        selectStartHH = chargingStartHH;
        selectStartII = chargingStartII;

        selectEndYYYY = chargingEndYYYY;
        selectEndMM = chargingEndMM;
        selectEndDD = chargingEndDD;
        selectEndHH = chargingEndHH;
        selectEndII = chargingEndII;

        binding.txtChargingTime.setText(DateUtils.generateTimeToString(chargingMinute));

        binding.txtChargingDate1.setText(
                String.format("%02d", chargingStartMM)
                        + "/" + String.format("%02d", chargingStartDD)
                        + " " + chargingStartWeek
                        + " " + String.format("%02d", chargingStartHH)
                        + ":" + String.format("%02d", chargingStartII)
        );

        binding.txtChargingDate2.setText(String.format("%02d", chargingEndHH) + ":" + String.format("%02d", chargingEndII));

        binding.txtSDate.setText(
                String.format("%02d", chargingStartMM)
                        + "/" + String.format("%02d", chargingStartDD)
                        + " " + chargingStartWeek
                        + " " + String.format("%02d", chargingStartHH)
                        + ":" + String.format("%02d", chargingStartII));

        binding.spinnerTimeRange.setSelection(timeList.indexOf(DateUtils.generateTimeToString(chargingMinute).trim()), true); //시간 Spinner에서 선택된 시간 값을 강제 선택

        binding.spinnerLengthRange.setSelection(distanceEntry.indexOf(reserveRadius), true);

        Log.e(TAG, "checkChange isInit else : " + checkChange);
        if (checkChange) {
            chargeBtnClick(binding.goCharging);
        } else {
            chargeBtnClick(binding.reserveCharging);
        }

    }

    /**
     * YYYYMMDDHHII 형식의 시작일자, 종료일자 가자오는 함수.
     * 시작일자에는
     *
     * @param isStartTime true : 시작일자, false : 종료 일자
     * @return YYYYMMDDHHII 일자
     */
    private String setFullDateTime(boolean isStartTime) {

        String dateTime;
        int currTime = Integer.parseInt(DateUtils.addTimeGetTime(0).substring(3));

        // 현재 분이 0분과 30분 사이일 때
        if (0 <= currTime && currTime < 30) {

            int standardTime = 30 - currTime;

            if (!isStartTime) {
                standardTime += chargingMinute;
            }

            String currDateTime = DateUtils.nowDateTime();
            dateTime = DateUtils.dateAddTime(currDateTime, standardTime);

        } else {

            // 현재 분이 30분과 59분 사이일 때

            int standardTime = 60 - currTime;

            if (!isStartTime) {
                standardTime += chargingMinute;
            }

            String currDateTime = DateUtils.nowDateTime();
            dateTime = DateUtils.dateAddTime(currDateTime, standardTime);
        }

        return dateTime;
    }

    /**
     * 확인 버튼 클릭 시 시작일자, 종료일자 선택 가능 여부 벨리데이션 체크
     *
     * @return 확인 선택 가능 여부
     */
    private boolean dateValidationCheck() {

        if (chargingMinute <= 0) {

            Toast.makeText(this, "충전기 사용 시작일자, 종료일자를 확인하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();

            return false;
        } else {
            return true;
        }
    }

    private void setTimeList() {

        timeList.add("30분");
        for (int i = 1; i < 11; i++) {
            timeList.add(i + "시간");

            if (i < 10) {
                timeList.add(i + "시간 " + "30분");
            }
        }
    }

    //충전일이 2일이 넘어가는지 확인
    private void checkDate(String type, int totalMinute, int selectedHour, int selectedMinute) {

        Calendar todayCal = Calendar.getInstance();
        todayCal.setTime(new Date());
        todayCal.add(Calendar.DATE, 2);
        startWeek = todayCal.get(Calendar.DAY_OF_WEEK);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:ss");

        Log.e(TAG,"startWeek : " + simpleDateFormat.format(todayCal.getTime()));
        Log.e(TAG,"startWeek : " + startWeek);

        Calendar calendar = Calendar.getInstance();

        //충전 시간 변경
        if (type.equals("onItemSelected")) {
            calendar.set(selectStartYYYY, selectStartMM, selectStartDD, selectStartHH, selectStartII);
        }
        //TimePicker 시간 변경
        else {
            calendar.set(selectStartYYYY, selectStartMM, selectStartDD, selectedHour, selectedMinute);
        }

        calendar.add(Calendar.MONTH, -1);
        calendar.add(Calendar.MINUTE, totalMinute);

        endWeek = calendar.get(Calendar.DAY_OF_WEEK);
        System.out.println("endWeek : " + simpleDateFormat.format(calendar.getTime()));
        System.out.println("endWeek : " + endWeek);

    }

    private int calculationMinute(String getTime) {

        int totalMinute = 30;

        if (getTime.contains("시간") && getTime.contains("분")) {
            getTime = getTime.replace("시간", "");
            getTime = getTime.replace("분", "");

            String[] splitString = getTime.split(" ");
            totalMinute = (Integer.parseInt(splitString[0]) * 60) + Integer.parseInt(splitString[1]);

        } else if (getTime.contains("시간") && !getTime.contains("분")) {
            getTime = getTime.replace("시간", "");
            totalMinute = Integer.parseInt(getTime) * 60;
        }

        return totalMinute;
    }

    private void setTimeTitle(String time) {

        int totalMinute = calculationMinute(time);

        Calendar nowCal = Calendar.getInstance(Locale.getDefault());
        Calendar addCal = Calendar.getInstance(Locale.getDefault());

        Log.e(TAG, "getMonth : " + nowCal.get(Calendar.MONTH));

        //예약 충전
        if (!checkChange) {

            int StartYYYY = Integer.parseInt(selectStartFullDate.substring(0, 4));
            int StartMM = Integer.parseInt(selectStartFullDate.substring(4, 6));
            int StartDD = Integer.parseInt(selectStartFullDate.substring(6, 8));
            int StartHH = Integer.parseInt(selectStartFullDate.substring(8, 10));
            int StartII = Integer.parseInt(selectStartFullDate.substring(10, 12));

            Log.e(TAG, "StartMM 예약 " + StartMM);

            nowCal.set(StartYYYY, StartMM - 1, StartDD, StartHH, StartII);
            addCal.set(StartYYYY, StartMM - 1, StartDD, StartHH, StartII);
            Log.e(TAG, "getMonth 예약 " + nowCal.get(Calendar.MONTH));

            System.out.println("onItemSelected end : " + selectStartFullDate);
            System.out.println("onItemSelected end : " + StartYYYY + "-" + StartMM + "-" + StartDD + " " + StartHH + ":" + StartII);

        }

        Log.e(TAG, "getMonth 끝 " + nowCal.get(Calendar.MONTH));

        addCal.add(Calendar.MINUTE, totalMinute);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        nowCal.add(Calendar.MONTH, 1);
        addCal.add(Calendar.MONTH, 1);

        String getToday = getWeek(nowCal);
        String addToday = getWeek(addCal);

        nowCal.add(Calendar.MONTH, -1);
        addCal.add(Calendar.MONTH, -1);
        Log.e(TAG, "getToday : " + getToday);
        Log.e(TAG, "addToday : " + addToday);

        Log.e(TAG, "nowCal.get(Calendar.MONTH) " + nowCal.get(Calendar.MONTH));

        //같은 요일
        if (getToday.equals(addToday)) {

            binding.txtChargingDate1.setText(String.format(Locale.KOREA, "%02d", nowCal.get(Calendar.MONTH) + 1) + "/" + String.format(Locale.KOREA, "%02d", nowCal.get(Calendar.DAY_OF_MONTH)) + " " + getToday + " " + sdf.format(nowCal.getTime()));
            binding.txtChargingDate2.setText(sdf.format(addCal.getTime()));
        } else {
            binding.txtChargingDate1.setText(String.format(Locale.KOREA, "%02d", nowCal.get(Calendar.MONTH) + 1) + "/" + String.format(Locale.KOREA, "%02d", nowCal.get(Calendar.DAY_OF_MONTH)) + " " + getToday + " " + sdf.format(nowCal.getTime()));
            binding.txtChargingDate2.setText(String.format(Locale.KOREA, "%02d", addCal.get(Calendar.MONTH) + 1) + "/" + String.format(Locale.KOREA, "%02d", addCal.get(Calendar.DAY_OF_MONTH)) + " " + addToday + " " + sdf.format(addCal.getTime()));
        }

        chargingMinute = totalMinute;

        chargingStartWeek = getToday;
        selectStartYYYY = nowCal.get(Calendar.YEAR);
        selectStartMM = nowCal.get(Calendar.MONTH) + 1;
        selectStartDD = nowCal.get(Calendar.DAY_OF_MONTH);
        selectStartHH = nowCal.get(Calendar.HOUR_OF_DAY);
        selectStartII = nowCal.get(Calendar.MINUTE);

        Log.e(TAG, "selectStartMM : " + selectStartMM);

        chargingEndWeek = addToday;
        selectEndYYYY = addCal.get(Calendar.YEAR);
        selectEndMM = addCal.get(Calendar.MONTH) + 1;
        selectEndDD = addCal.get(Calendar.DAY_OF_MONTH);
        selectEndHH = addCal.get(Calendar.HOUR_OF_DAY);
        selectEndII = addCal.get(Calendar.MINUTE);

        selectStartFullDate = String.format(Locale.KOREA, "%04d", selectStartYYYY) + String.format(Locale.KOREA, "%02d", selectStartMM) + String.format(Locale.KOREA, "%02d", selectStartDD) + String.format(Locale.KOREA, "%02d", selectStartHH) + String.format(Locale.KOREA, "%02d", selectStartII);
        selectEndFullDate = String.format(Locale.KOREA, "%04d", selectEndYYYY) + String.format(Locale.KOREA, "%02d", selectEndMM) + String.format(Locale.KOREA, "%02d", selectEndDD) + String.format(Locale.KOREA, "%02d", selectEndHH) + String.format(Locale.KOREA, "%02d", selectEndII);

        Log.e(TAG, "selectStartFullDate : " + selectStartFullDate);
        Log.e(TAG, "selectEndFullDate : " + selectEndFullDate);
    }

    private String getWeek(Calendar getCal) {

        return DateUtils.getWeek(
                String.format(Locale.KOREA, "%02d", getCal.get(Calendar.YEAR))
                        + String.format(Locale.KOREA, "%02d", getCal.get(Calendar.MONTH))
                        + String.format(Locale.KOREA, "%02d", getCal.get(Calendar.DAY_OF_MONTH)));

    }

    public void chargeBtnClick(View view) {

        TextView selectedTextView = (TextView) view;

        //즉시 충전
        if (selectedTextView.getText().toString().contains("즉시")) {
            checkChange = true;

            binding.reserveCharging.setBackgroundResource(R.color.transparent);
            binding.reserveCharging.setTextAppearance(R.style.history_selected_none);

            binding.layoutSDate.setEnabled(false);
            binding.txtSDate.setTextColor(ContextCompat.getColor(this, R.color.gray));
        }
        //예약 충전
        else {
            checkChange = false;

            binding.goCharging.setBackgroundResource(R.color.transparent);
            binding.goCharging.setTextAppearance(R.style.history_selected_none);

            binding.layoutSDate.setEnabled(true);
            binding.txtSDate.setTextColor(ContextCompat.getColor(this, R.color.black));

            if (!selectStartFullDate.substring(selectStartFullDate.length() - 2, selectStartFullDate.length()).equals("00") && !selectStartFullDate.substring(selectStartFullDate.length() - 2, selectStartFullDate.length()).equals("30")) {
                selectStartFullDate = setFullDateTime(true);
            }

            Calendar getWeekCal = Calendar.getInstance();
            getWeekCal.set(Integer.parseInt(selectStartFullDate.substring(0, 4)), Integer.parseInt(selectStartFullDate.substring(4, 6)), Integer.parseInt(selectStartFullDate.substring(6, 8)), Integer.parseInt(selectStartFullDate.substring(8, 10)), Integer.parseInt(selectStartFullDate.substring(10, 12)));

            binding.txtSDate.setText(
                    String.format(Locale.KOREA, "%02d", Integer.parseInt(selectStartFullDate.substring(4, 6)))
                            + "/" + String.format(Locale.KOREA, "%02d", Integer.parseInt(selectStartFullDate.substring(6, 8)))
                            + " " + getWeek(getWeekCal)
                            + " " + String.format(Locale.KOREA, "%02d", Integer.parseInt(selectStartFullDate.substring(8, 10)))
                            + ":" + String.format(Locale.KOREA, "%02d", Integer.parseInt(selectStartFullDate.substring(10, 12))));

        }
        selectedTextView.setBackgroundResource(R.color.blue_button);
        selectedTextView.setTextAppearance(R.style.history_selected_selected);

        setTimeTitle(binding.spinnerTimeRange.getSelectedItem().toString());
    }
}
