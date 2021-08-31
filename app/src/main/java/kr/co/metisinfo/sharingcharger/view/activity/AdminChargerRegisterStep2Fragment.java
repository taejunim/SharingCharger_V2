package kr.co.metisinfo.sharingcharger.view.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
import android.location.LocationManager;
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
import kr.co.metisinfo.sharingcharger.databinding.FragmentAdminChargerRegisterStep2Binding;



public class AdminChargerRegisterStep2Fragment extends Fragment {

    private FragmentAdminChargerRegisterStep2Binding binding;

    private double gpsX;
    private double gpsY;
    private boolean hasGpsPermission = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("metis", "AdminChargerRegisterStep2Fragment");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_charger_register_step2, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("metis", "AdminChargerRegisterStep2Fragment - onViewCreated");
        super.onViewCreated(view, savedInstanceState);


        LocationManager locationManager = (LocationManager) getContext().getSystemService(AdminMainActivity.LOCATION_SERVICE);

        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGPSEnabled || isNetworkEnabled) {
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
                Location location;
                if (isGPSEnabled) {
                    Log.d("metis", "isGPSEnabled");
                    if (locationManager != null) {

                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (location != null) {
                            Log.d("metis", String.valueOf(location.getLatitude()));
                            Log.d("metis", String.valueOf(location.getLongitude()));
                            gpsX = location.getLongitude();
                            gpsY = location.getLatitude();
                            hasGpsPermission = true;
                        }
                    }
                }
                if (isNetworkEnabled) {
                    Log.d("metis", "isNetworkEnabled");
                    if (locationManager != null) {

                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null) {
                            Log.d("metis", String.valueOf(location.getLatitude()));
                            Log.d("metis", String.valueOf(location.getLongitude()));
                            gpsX = location.getLongitude();
                            gpsY = location.getLatitude();
                            hasGpsPermission = true;
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this.getContext(),"해당 서비스는 위치 권한이 필요한 서비스 입니다. 위치 권한을 허용해주세요.",Toast.LENGTH_SHORT).show();
        }

        setLayout();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setLayout(){
        binding.includeChargerRegisterMenu.circleStep1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.purple)));
        binding.includeChargerRegisterMenu.circleStep2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.deep_purple)));
        binding.includeChargerRegisterMenu.circleStep3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.neutral_tint)));

        binding.includeChargerRegisterMenu.textStep1.setText("");
        binding.includeChargerRegisterMenu.textStep1.setBackground(ContextCompat.getDrawable(getContext(),R.mipmap.check_mark));

        binding.includeChargerRegisterFooter.previousButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.deep_purple)));

        binding.includeChargerRegisterMenu.bottomTextStep1.setTextColor(ContextCompat.getColor(getContext(),R.color.neutral_tint));
        binding.includeChargerRegisterMenu.bottomTextStep2.setTextColor(ContextCompat.getColor(getContext(),R.color.deep_purple));
        binding.includeChargerRegisterMenu.bottomTextStep3.setTextColor(ContextCompat.getColor(getContext(),R.color.neutral_tint));

        binding.includeChargerRegisterFooter.previousButton.setEnabled(true);
        binding.includeChargerRegisterFooter.nextButton.setEnabled(true);

        binding.includeChargerRegisterFooter.previousButton.setOnClickListener(view -> previousButton());
        binding.includeChargerRegisterFooter.nextButton.setOnClickListener(view -> nextButton());
    }

    private void previousButton(){
        Bundle bundle = new Bundle();

        ((AdminMainActivity) getActivity()).chargerRegisterPreviousStep(2, bundle);
    }

    private void nextButton(){
        String chargerName        = binding.editTextChargerName.getText().toString();
        String chargerDescription = binding.editTextDescription.getText().toString();


        if(! hasGpsPermission) Toast.makeText(this.getContext(),"해당 서비스는 위치 권한이 필요한 서비스 입니다. 위치 권한을 허용해주세요.",Toast.LENGTH_SHORT).show();
        else if(checkBlank(chargerName)) {
            //다음 화면으로 넘어갈때 보내는 값
            Bundle bundle = new Bundle();

            bundle.putString("bleNumber", getArguments().getString("bleNumber"));
            bundle.putInt("id", getArguments().getInt("id"));
            bundle.putInt("providerCompanyId", getArguments().getInt("providerCompanyId"));

            bundle.putString("chargerName", (String) chargerName);
            bundle.putString("chargerDescription", (String) chargerDescription);

            bundle.putDouble("gpsX", gpsX);
            bundle.putDouble("gpsY", gpsY);

            ((AdminMainActivity) getActivity()).chargerRegisterNextStep(2, bundle);
        }
    }

    //필수값 체크 충전기명
    private boolean checkBlank(String chargerName){

        boolean result = false;

        if(chargerName.equals("")) Toast.makeText(getContext(), "충전기명을 입력해 주십시오.",Toast.LENGTH_SHORT).show();
        else result = true;

        return result;
    }
}
