package kr.co.metisinfo.sharingcharger.view.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.databinding.FragmentAdminChargerDetailInformationBinding;
import kr.co.metisinfo.sharingcharger.model.AdminChargerModel;

//소유주 충전기 상
public class AdminChargerDetailInformationFragment extends Fragment {

    FragmentAdminChargerDetailInformationBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_charger_detail_information, container, false);
        View view = binding.getRoot();

        //충전기 관리 화면으로 부터 받은 데이터
        Bundle bundle = getArguments();
        AdminChargerModel adminChargerModel = (AdminChargerModel) bundle.getSerializable("object");

        binding.chargerDetailInformationTitle.setText(adminChargerModel.getName());
        binding.textBleNumber.setText(adminChargerModel.getBleNumber());
        binding.textPlaceName.setText(adminChargerModel.getProviderCompanyName() == null ? "-" : adminChargerModel.getProviderCompanyName());
        binding.textAddress.setText(adminChargerModel.getAddress());
        binding.textFreeParking.setText(adminChargerModel.isParkingFeeFlag() ? "유료주차" : "무료주차");
        binding.textFreeParkingDetail.setText(adminChargerModel.getParkingFeeDescription().equals("") ? "-" : adminChargerModel.getParkingFeeDescription());
        binding.textDescription.setText(adminChargerModel.getDescription().equals("") ? "-" : adminChargerModel.getDescription());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //image 보라색으로
        binding.imageBleNumber.setColorFilter(ContextCompat.getColor(getContext(), R.color.purple));
        binding.imagePlaceName.setColorFilter(ContextCompat.getColor(getContext(), R.color.purple));
        binding.imageAddress.setColorFilter(ContextCompat.getColor(getContext(), R.color.purple));
        binding.imageParking.setColorFilter(ContextCompat.getColor(getContext(), R.color.purple));
        binding.imageDescription.setColorFilter(ContextCompat.getColor(getContext(), R.color.purple));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
