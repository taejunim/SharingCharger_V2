package kr.co.metisinfo.sharingcharger.view.activity;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.charzin.evzsdk.EvzBluetooth;
import com.evzlife.android.blescanner.EVZScanCallbacks;
import com.evzlife.android.blescanner.EVZScanManager;
import com.evzlife.android.blescanner.EVZScanResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kr.co.metisinfo.sharingcharger.ChargerDialogAdapter;
import kr.co.metisinfo.sharingcharger.R;

import kr.co.metisinfo.sharingcharger.databinding.FragmentAdminChargerRegisterStep1Binding;;

import kr.co.metisinfo.sharingcharger.model.AdminChargerModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.CommonUtils;;


public class AdminChargerRegisterStep1Fragment extends Fragment {

    private FragmentAdminChargerRegisterStep1Binding binding;

    public static TextView chargerBleText;

    EvzBluetooth mEvzBluetooth;
    private EVZScanManager mScanner;
    List<EVZScanResult> mScData;

    public static Dialog dialog;

    ApiUtils apiUtils = new ApiUtils();
    AdminChargerModel adminChargerModel = new AdminChargerModel();

    CommonUtils commonUtils = new CommonUtils();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("metis", "AdminChargerRegisterStep1Fragment");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_charger_register_step1, container, false);
        View root = binding.getRoot();

        mScanner = new EVZScanManager();
        mEvzBluetooth = new EvzBluetooth(getActivity());
        mEvzBluetooth.setBluetooth(true);

        chargerBleText = root.findViewById(R.id.charger_search_ble_text);

        binding.chargerSearchButton.setOnClickListener(v -> {
            /*
             * 블루투스 연결해제 후 충전기 검색 버튼 클릭 시 에러남
             * 강제로 활성화 시킨 후 2.5초 후 scan 시작함(바로 시작 시 에러남)
             * */
            mEvzBluetooth.setBluetooth(true);

            getBLEScan();
        });

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

        binding.chargerSearchButton.setOnClickListener(view -> getBLEScan());

        binding.includeChargerRegisterFooter.nextButton.setOnClickListener(view -> checkChargerInformation());
    }

    private void nextButton(){

        //다음 화면으로 넘어갈때 보내는 값
        Bundle bundle = new Bundle();
        bundle.putString("bleNumber", (String)adminChargerModel.getBleNumber());
        bundle.putInt("id", adminChargerModel.getId());
        bundle.putInt("providerCompanyId", adminChargerModel.getProviderCompanyId());

        ((AdminMainActivity) getActivity()).chargerRegisterNextStep(1, bundle);
    }

    private void checkChargerInformation(){

        String bleNumber = chargerBleText.getText().toString().replaceAll(":","");

        adminChargerModel = apiUtils.getChargerInformationFromBleNumber(bleNumber);

        if (bleNumber.equals("")) {
            Toast.makeText(getContext(), "BLE가 선택되지 않았습니다.\nBLE 검색후 선택해주세요.",Toast.LENGTH_SHORT).show();
           return;
        }

        if(adminChargerModel.getResponseCode() == 200) {

            nextButton();

        } else {
            Toast.makeText(getContext(), "등록되지 않은 BLE 입니다. \n고객센터로 문의주세요.",Toast.LENGTH_SHORT).show();
        }
    }

    public void getBLEScan() {

        ((AdminMainActivity)getActivity()).showLoading(binding.loading);

        mScanner.startScan(new EVZScanCallbacks() {

            @Override
            public void onScanFinished(@NonNull List<EVZScanResult> results) {
                mScData = results;
                if (mScData.size() > 0) {
                    ((AdminMainActivity)getActivity()).hideLoading(binding.loading);

                    String[] bleArray = new String[mScData.size()];
                    for (int i = 0; i < mScData.size(); i++) {
                        bleArray[i] = mScData.get(i).getDevice().getAddress();
                    }

                    showAlertDialogTopic(bleArray);
                } else {
                    scanFailed();
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                scanFailed();
            }
        });
    }

    private void showAlertDialogTopic(String[] bleArray) {

        dialog = new Dialog(getActivity());

        LayoutInflater inf = getLayoutInflater();
        View dialogView = inf.inflate(R.layout.charger_search_dialog, null);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        Button rescanButton = dialogView.findViewById(R.id.rescan_button);

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        rescanButton.setOnClickListener(v -> {
            dialog.dismiss();
            getBLEScan();
        });

        dialog.setContentView(dialogView); // Dialog에 선언했던 layout 적용
        dialog.setCancelable(false); // 외부 터치나 백키로 dimiss 시키는 것 막음

        ArrayList<String> arrayList = new ArrayList<>(); // recyclerView에 들어갈 Array
        arrayList.addAll(Arrays.asList(bleArray)); // Array에 사전에 정의한 Topic 넣기
        /*
        다음 4줄의 코드는 RecyclerView를 정의하기 위한 View, Adapter선언 코드이다.
        1. RecyclerView id 등록
        2. 수직방향으로 보여줄 예정이므로 LinearLayoutManager 등록
           2차원이면 GridLayoutManager 등 다른 Layout을 선택
        3. adapter에 topic Array 넘겨서 출력되게끔 전달
        4. adapter 적용
        */
        RecyclerView dialogRecyclerView = (RecyclerView) dialogView.findViewById(R.id.dialogRecyclerView);
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ChargerDialogAdapter adapter = new ChargerDialogAdapter(arrayList);
        dialogRecyclerView.setAdapter(adapter);

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.alert_dialog_border);
        dialog.getWindow().setLayout(commonUtils.getPercentWidth(getActivity(), 60), WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show(); // Dialog 출력
    }

    private void scanFailed() {
        //hideLoading(binding.loading);
        ((AdminMainActivity)getActivity()).hideLoading(binding.loading);

        CustomDialog customDialog = new CustomDialog(getActivity(), "연결 가능한 충전기를 찾지 못했습니다.\n다시 검색하시겠습니까?");
        customDialog.show();
        customDialog.findViewById(R.id.dialog_ok_btn).setOnClickListener(view -> {
            customDialog.dismiss();
            ((AdminMainActivity)getActivity()).showLoading(binding.loading);
            getBLEScan();
        });
    }
}
