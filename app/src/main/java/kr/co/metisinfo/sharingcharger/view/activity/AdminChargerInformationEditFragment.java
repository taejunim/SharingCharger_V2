package kr.co.metisinfo.sharingcharger.view.activity;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.Constants;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.FragmentAdminChargerInformationEditBinding;
import kr.co.metisinfo.sharingcharger.model.AdminChargerModel;
import kr.co.metisinfo.sharingcharger.model.AllowTimeOfDayModel;
import kr.co.metisinfo.sharingcharger.model.SearchKeywordModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;

import static kr.co.metisinfo.sharingcharger.base.Constants.CHANGE_INFORMATION;
import static kr.co.metisinfo.sharingcharger.base.Constants.CHANGE_TIME;

public class AdminChargerInformationEditFragment extends Fragment {

    FragmentAdminChargerInformationEditBinding binding;

    ApiUtils apiUtils = new ApiUtils();

    AdminChargerModel adminChargerModel = new AdminChargerModel();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {  // 실행이 끝난후 확인 가능

            hideLoading(binding.loading);

            switch (msg.what) {
                case CHANGE_INFORMATION :

                    final int resultCode = msg.arg1;

                    if (resultCode == 200) {
                        Toast.makeText(ThisApplication.context, "설정한 정보로 변경되었습니다.", Toast.LENGTH_LONG).show();
                    } else if (resultCode == 400) {
                        Toast.makeText(ThisApplication.context, "정보 변경에 실패하였습니다.\n문제 지속시 고객센터로 문의주세요.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ThisApplication.context, "서버와 통신이 원활하지 않습니다.\n문제 지속시 고객센터로 문의주세요.", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //키워드 검색, 즐겨찾기
            case Constants.PAGE_SEARCH_KEYWORD:
                // 내 위치중심, 지도중심 구분해야함
                if (resultCode == RESULT_OK) {
                    SearchKeywordModel model = (SearchKeywordModel) data.getSerializableExtra("keyword");
                    binding.editAddress.setText(model.roadAddressName);
                }

                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_charger_information_edit, container, false);
        View view = binding.getRoot();

        Bundle bundle = getArguments();
        adminChargerModel = (AdminChargerModel) bundle.getSerializable("object");

        binding.editChargerName.setText(adminChargerModel.getName());
        binding.editAddress.setText(adminChargerModel.getAddress());
        binding.editDetailAddress.setText(adminChargerModel.getDetailAddress());

        if (adminChargerModel.parkingFeeFlag) {
            binding.parkingFeeTrue.setChecked(true);
        } else {
            binding.parkingFeeFalse.setChecked(true);
        }

        if (adminChargerModel.cableFlag) {
            binding.cableExistTrue.setChecked(true);
        } else {
            binding.cableExistFalse.setChecked(true);
        }

        binding.editAddress.setOnClickListener(v ->  {
            Intent intent = new Intent(getActivity(), SearchKeywordActivity.class);
            startActivityForResult(intent, Constants.PAGE_SEARCH_KEYWORD);
        });

        binding.editParkingFeeDescription.setText(adminChargerModel.parkingFeeDescription);

        binding.modificationButton.setOnClickListener(v -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(((AdminMainActivity) getActivity()));
            dialogBuilder.setMessage("입력하신 정보로 수정하시겠습니까?");
            dialogBuilder.setPositiveButton("확인", (dialog, which) ->{

                showLoading(binding.loading);

                adminChargerModel.setName(binding.editChargerName.getText().toString());
                adminChargerModel.setAddress(binding.editAddress.getText().toString());
                adminChargerModel.setDetailAddress(binding.editDetailAddress.getText().toString());
                adminChargerModel.setGpsX(0);
                adminChargerModel.setGpsY(0);

                int parkingFeeId = binding.parkingFeeToggle.getCheckedRadioButtonId();
                RadioButton parkingFee = view.findViewById(parkingFeeId);

                adminChargerModel.setParkingFeeFlag(parkingFee.getText().toString().equals("있음") ? true : false);

                int cableExistId = binding.cableExistToggle.getCheckedRadioButtonId();
                RadioButton cableExist = view.findViewById(cableExistId);

                adminChargerModel.setCableFlag(cableExist.getText().toString().equals("있음") ? true : false);

                adminChargerModel.setParkingFeeDescription(binding.editParkingFeeDescription.getText().toString());

                adminChargerModel = apiUtils.changeChargerInformation(adminChargerModel.getId(), adminChargerModel);

                Message msg = new Message();
                msg.what = CHANGE_INFORMATION;
                msg.arg1 = adminChargerModel.getResponseCode();
                handler.sendMessage(msg);
            });
            dialogBuilder.setNegativeButton("취소", null);
            dialogBuilder.create().show();
        });

        return view;
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
}
