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
import kr.co.metisinfo.sharingcharger.databinding.FragmentAdminChargerRegisterStep3Binding;


public class AdminChargerRegisterStep3Fragment extends Fragment {

    private FragmentAdminChargerRegisterStep3Binding binding;

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

        binding.includeChargerRegisterFooter.previousButton.setEnabled(true);
        binding.includeChargerRegisterFooter.nextButton.setEnabled(true);

        binding.includeChargerRegisterFooter.nextButton.setText("등록");

        binding.includeChargerRegisterFooter.previousButton.setOnClickListener(view -> previousButton());
        binding.includeChargerRegisterFooter.nextButton.setOnClickListener(view -> nextButton());
    }

    private void previousButton(){
        Log.d("metis", "AdminChargerRegisterStep2Fragment - previousButton");
    }

    private void nextButton(){
        Log.d("metis", "AdminChargerRegisterStep2Fragment - nextButton");
    }

}
