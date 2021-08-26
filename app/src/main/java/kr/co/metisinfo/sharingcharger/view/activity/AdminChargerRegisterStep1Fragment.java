package kr.co.metisinfo.sharingcharger.view.activity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.databinding.FragmentAdminChargerRegisterStep1Binding;;

public class AdminChargerRegisterStep1Fragment extends Fragment {

    private FragmentAdminChargerRegisterStep1Binding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("metis", "AdminChargerRegisterStep1Fragment");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_charger_register_step1, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("metis","AdminChargerManageFragment - onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        setLayout();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setLayout(){

        binding.includeChargerRegisterMenu.circleStep1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.deep_purple)));
        binding.includeChargerRegisterMenu.circleStep2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.neutral_tint)));
        binding.includeChargerRegisterMenu.circleStep3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.neutral_tint)));

        binding.includeChargerRegisterFooter.previousButton.setEnabled(false);
        binding.includeChargerRegisterFooter.nextButton.setEnabled(true);

        binding.includeChargerRegisterFooter.nextButton.setOnClickListener(view -> nextButton());
    }

    private void nextButton(){
        Log.d("metis", "AdminChargerRegisterStep1Fragment - nextButton");
        ((AdminMainActivity) getActivity()).chargerRegisterNextStep(1);
    }
}
