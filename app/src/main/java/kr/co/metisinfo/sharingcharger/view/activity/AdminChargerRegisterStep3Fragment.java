package kr.co.metisinfo.sharingcharger.view.activity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.FragmentAdminChargerRegisterStep3Binding;
import kr.co.metisinfo.sharingcharger.model.AdminChargerModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.PreferenceUtil;


public class AdminChargerRegisterStep3Fragment extends Fragment {

    private FragmentAdminChargerRegisterStep3Binding binding;
    ApiUtils apiUtils = new ApiUtils();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("metis", "AdminChargerRegisterStep3Fragment");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_charger_register_step3, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("metis","AdminChargerRegisterStep3Fragment - onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        setLayout();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setLayout(){

        binding.includeChargerRegisterMenu.circleStep1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.purple)));
        binding.includeChargerRegisterMenu.circleStep2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.purple)));
        binding.includeChargerRegisterMenu.circleStep3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.deep_purple)));

        binding.includeChargerRegisterMenu.textStep1.setText("");
        binding.includeChargerRegisterMenu.textStep1.setBackground(ContextCompat.getDrawable(getContext(),R.mipmap.check_mark));
        binding.includeChargerRegisterMenu.textStep2.setText("");
        binding.includeChargerRegisterMenu.textStep2.setBackground(ContextCompat.getDrawable(getContext(),R.mipmap.check_mark));

        binding.includeChargerRegisterMenu.bottomTextStep1.setTextColor(ContextCompat.getColor(getContext(),R.color.neutral_tint));
        binding.includeChargerRegisterMenu.bottomTextStep2.setTextColor(ContextCompat.getColor(getContext(),R.color.neutral_tint));
        binding.includeChargerRegisterMenu.bottomTextStep3.setTextColor(ContextCompat.getColor(getContext(),R.color.deep_purple));


        binding.includeChargerRegisterFooter.previousButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.deep_purple)));

        binding.includeChargerRegisterFooter.previousButton.setEnabled(true);
        binding.includeChargerRegisterFooter.nextButton.setEnabled(true);

        binding.includeChargerRegisterFooter.nextButton.setText("등록");

        binding.includeChargerRegisterFooter.previousButton.setOnClickListener(view -> previousButton());
        binding.includeChargerRegisterFooter.nextButton.setOnClickListener(view -> nextButton());
    }

    private void previousButton(){
        Log.d("metis", "AdminChargerRegisterStep2Fragment - previousButton");
        Bundle bundle = new Bundle();

        bundle.putString("bleNumber", getArguments().getString("bleNumber"));
        bundle.putInt("id", getArguments().getInt("id"));
        bundle.putInt("providerCompanyId", getArguments().getInt("providerCompanyId"));

        ((AdminMainActivity) getActivity()).chargerRegisterPreviousStep(3, bundle);
    }

    //등록 버튼
    private void nextButton(){

        Log.d("metis", "AdminChargerRegisterStep3Fragment - nextButton");

        PreferenceUtil preferenceUtil = new PreferenceUtil(ThisApplication.context);

        String email = preferenceUtil.getString("email");

        String detailAddress = binding.editTextDetailAddress.getText().toString();
        String parkingFeeDescription = binding.editParkingFeeDescription.getText().toString();

        AdminChargerModel adminChargerModel = new AdminChargerModel();

        adminChargerModel.setId(getArguments().getInt("id"));

        //좌표만 보내면 되기 떄문에 주소는 빈값으로 보냄
        adminChargerModel.setAddress("");
        adminChargerModel.setGpsX(getArguments().getDouble("gpsX"));
        adminChargerModel.setGpsY(getArguments().getDouble("gpsY"));

        //충전기 상태 기본값
        adminChargerModel.setCurrentStatusType("READY");
        adminChargerModel.setOwnerType("Personal");

        adminChargerModel.setBleNumber(getArguments().getString("bleNumber"));
        adminChargerModel.setCableFlag(binding.cableExistY.isChecked());

        adminChargerModel.setDetailAddress(detailAddress);

        adminChargerModel.setOwnerName(email);
        adminChargerModel.setName(getArguments().getString("chargerName"));
        adminChargerModel.setDescription(getArguments().getString("chargerDescription"));


        adminChargerModel.setParkingFeeDescription(parkingFeeDescription);
        adminChargerModel.setParkingFeeFlag(binding.parkingFeeY.isChecked());
        adminChargerModel.setProviderCompanyId(getArguments().getInt("providerCompanyId"));


        if(binding.sharing.isChecked()) adminChargerModel.setSharedType("SHARING");
        else adminChargerModel.setSharedType("PARTIAL_SHARING");


        Log.d("metis", adminChargerModel.toString());

        AdminChargerModel result = apiUtils.assignCharger(adminChargerModel);
        int resultCode = result.getResponseCode();

        if(resultCode == 200) {
            Toast.makeText(getContext(),"충전기 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();

            ((AdminMainActivity) getActivity()).chargerRegisterNextStep(3, null);
        } else if(resultCode == 400){
            Toast.makeText(getContext(),"이미 사용중인 충전기명 입니다. 충전기명을 다시 설정해주세요.\n문제 지속시 고객센터로 문의주세요.", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(),"충전기 등록에 실패하였습니다." + result.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


}
