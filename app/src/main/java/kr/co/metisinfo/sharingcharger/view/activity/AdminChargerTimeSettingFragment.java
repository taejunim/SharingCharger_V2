package kr.co.metisinfo.sharingcharger.view.activity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.FragmentAdminChargerTimeSettingBinding;
import kr.co.metisinfo.sharingcharger.model.AdminChargerModel;
import kr.co.metisinfo.sharingcharger.model.AllowTimeOfDayModel;
import kr.co.metisinfo.sharingcharger.model.PriceModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.CustomTimePickerDialog;
import kr.co.metisinfo.sharingcharger.utils.DateUtils;
import kr.co.metisinfo.sharingcharger.utils.MyTimePicker;

import static kr.co.metisinfo.sharingcharger.base.Constants.CHANGE_PRICE;
import static kr.co.metisinfo.sharingcharger.base.Constants.CHANGE_TIME;

public class AdminChargerTimeSettingFragment extends Fragment {

    FragmentAdminChargerTimeSettingBinding binding;

    ApiUtils apiUtils = new ApiUtils();

    SimpleDateFormat HHMMFormatter = new SimpleDateFormat("HH:mm");
    SimpleDateFormat HHFormatter = new SimpleDateFormat("HH");
    SimpleDateFormat MMFormatter = new SimpleDateFormat("mm");

    AdminChargerModel adminChargerModel = new AdminChargerModel();

    AllowTimeOfDayModel allowTimeOfDayModel = new AllowTimeOfDayModel();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {  // 실행이 끝난후 확인 가능

            hideLoading(binding.loading);

            switch (msg.what) {
                case CHANGE_TIME :

                    final int resultCode = msg.arg1;

                    if (resultCode == 200) {
                        Toast.makeText(ThisApplication.context, "설정한 시간으로 변경되었습니다.", Toast.LENGTH_LONG).show();
                    } else if (resultCode == 400) {
                        Toast.makeText(ThisApplication.context, "시간 변경에 실패하였습니다.\n문제 지속시 고객센터로 문의주세요.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ThisApplication.context, "서버와 통신이 원활하지 않습니다.\n문제 지속시 고객센터로 문의주세요.", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_charger_time_setting, container, false);
        View view = binding.getRoot();

        Bundle bundle = getArguments();
        adminChargerModel = (AdminChargerModel) bundle.getSerializable("object");

        allowTimeOfDayModel = apiUtils.getAllowTime(adminChargerModel.getId());

        binding.oldOpenTime.setText(DateUtils.convertToHHMM(allowTimeOfDayModel.getOpenTime()));
        binding.oldCloseTime.setText(DateUtils.convertToHHMM(allowTimeOfDayModel.getCloseTime()));
        binding.newOpenTime.setText(DateUtils.convertToHHMM(allowTimeOfDayModel.getPreviousOpenTime()));
        binding.newCloseTime.setText(DateUtils.convertToHHMM(allowTimeOfDayModel.getPreviousCloseTime()));

        binding.newOpenTime.setOnClickListener(v -> {
            setTime(binding.newOpenTime);
        });

        binding.newCloseTime.setOnClickListener(v -> {
            setTime(binding.newCloseTime);
        });

        binding.modificationButton.setOnClickListener(v -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(((AdminMainActivity) getActivity()));
            dialogBuilder.setMessage("충전기 운영 시간 수정시 기존 예약건에 대해서는 적용되지 않고 신규 예약건에 대해서만 반영됩니다.\n수정하시겠습니까?");
            dialogBuilder.setPositiveButton("확인", (dialog, which) ->{

                showLoading(binding.loading);

                AllowTimeOfDayModel allowTimeOfDayModel = new AllowTimeOfDayModel();
                allowTimeOfDayModel.setOpenTime(binding.newOpenTime.getText().toString());
                allowTimeOfDayModel.setCloseTime(binding.newCloseTime.getText().toString());

                allowTimeOfDayModel = apiUtils.changeAllowTime(adminChargerModel.getId(), allowTimeOfDayModel);

                Message msg = new Message();
                msg.what = CHANGE_TIME;
                msg.arg1 = allowTimeOfDayModel.getResponseCode();
                handler.sendMessage(msg);
            });
            dialogBuilder.setNegativeButton("취소", null);
            dialogBuilder.create().show();
        });

        return view;
    }

    private void setTime(TextView textView) {

        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            if (textView == binding.newOpenTime) {
                textView.setText(new SimpleDateFormat("HH:mm").format(calendar.getTime()));
            } else if (textView == binding.newCloseTime) {

                if (isTimeValid(new SimpleDateFormat("HH:mm").format(calendar.getTime()))) {
                    textView.setText(new SimpleDateFormat("HH:mm").format(calendar.getTime()).equals("00:00") ? "23:59" : new SimpleDateFormat("HH:mm").format(calendar.getTime()));
                } else {
                    Toast.makeText(ThisApplication.context, "시간 설정이 올바르지 않습니다.\n다시 설정해주세요.", Toast.LENGTH_SHORT).show();
                    binding.newOpenTime.setText("00:00");
                    binding.newCloseTime.setText("23:59");
                }
            }
        };

        Date originDate = null;
        try {
            originDate = HHMMFormatter.parse(textView.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        CustomTimePickerDialog dialog = new CustomTimePickerDialog(getActivity(), timeSetListener, Integer.parseInt(HHFormatter.format(originDate)), Integer.parseInt(MMFormatter.format(originDate)), true);
        dialog.setCancelable(false);
        dialog.show();
    }

    private boolean isTimeValid(String closeTimeString) {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date openTime = simpleDateFormat.parse(binding.newOpenTime.getText().toString());
            Date closeTime = simpleDateFormat.parse(closeTimeString);

            if (closeTime.getTime() - openTime.getTime() < 1800000) {
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void showLoading(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);

        //해당페이지 이벤트 막기
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void hideLoading(ProgressBar progressBar) {
        progressBar.setVisibility(View.INVISIBLE);

        //이벤트 다시 풀기
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
