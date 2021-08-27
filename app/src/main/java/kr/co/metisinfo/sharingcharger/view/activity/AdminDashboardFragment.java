package kr.co.metisinfo.sharingcharger.view.activity;

import android.os.Bundle;
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
import kr.co.metisinfo.sharingcharger.databinding.FragmentAdminDashboardBinding;
import kr.co.metisinfo.sharingcharger.model.AdminDashboardModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;

public class AdminDashboardFragment extends Fragment {

    private FragmentAdminDashboardBinding binding;

    ApiUtils apiUtils = new ApiUtils();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_dashboard, container, false);
        View root = binding.getRoot();

        AdminDashboardModel adminDashboardModel = apiUtils.getAdminDashboard();

        setDashboardText(adminDashboardModel.getResponseCode(), adminDashboardModel);

        return root;
    }

    private void setDashboardText(int responseCode, AdminDashboardModel adminDashboardModel) {

        if (responseCode == 200) {
            binding.dashboardChargerCount.setText(getResources().getString(R.string.dashboard_charger_count, String.valueOf(adminDashboardModel.getCurrentChargerCount()), String.valueOf(adminDashboardModel.getOwnChargerCount())));
            binding.dashboardMonthlyUseCount.setText(getResources().getString(R.string.dashboard_monthly_use_count, String.valueOf(adminDashboardModel.getMonthlyRechargeCount())));
            binding.dashboardMonthlyErrorCount.setText(getResources().getString(R.string.dashboard_monthly_error_count, String.valueOf(adminDashboardModel.getMonthlyChargerErrorCount())));
            binding.dashboardMonthlyProfitPoint.setText(getResources().getString(R.string.dashboard_monthly_profit_point, String.valueOf(adminDashboardModel.getMonthlyCumulativePoint())));
            binding.dashboardMonthlyReservationCount.setText(getResources().getString(R.string.dashboard_monthly_reservation_count, String.valueOf(adminDashboardModel.getMonthlyReserveCount())));
            binding.dashboardMonthlyChargeAmount.setText(getResources().getString(R.string.dashboard_monthly_charge_amount, String.valueOf(adminDashboardModel.getMonthlyRechargeKwh())));
        } else {
            binding.dashboardChargerCount.setText(getResources().getString(R.string.dashboard_charger_count, "-", "-"));
            binding.dashboardMonthlyUseCount.setText(getResources().getString(R.string.dashboard_monthly_use_count, "-"));
            binding.dashboardMonthlyErrorCount.setText(getResources().getString(R.string.dashboard_monthly_error_count, "-"));
            binding.dashboardMonthlyProfitPoint.setText(getResources().getString(R.string.dashboard_monthly_profit_point, "-"));
            binding.dashboardMonthlyReservationCount.setText(getResources().getString(R.string.dashboard_monthly_reservation_count, "-"));
            binding.dashboardMonthlyChargeAmount.setText(getResources().getString(R.string.dashboard_monthly_charge_amount, "-"));

            Toast.makeText(getContext(), adminDashboardModel.getMessage(), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //icon 하얗게
        binding.iconChargerCount.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));
        binding.iconMonthlyUseCount.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));
        binding.iconMonthlyErrorCount.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));
        binding.iconMonthlyProfitPoint.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));
        binding.iconMonthlyReservationCount.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));
        binding.iconMonthlyChargeAmount.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
