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
import android.widget.ProgressBar;
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
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;;


public class AdminChargerRegisterStep1Fragment extends Fragment {

    private FragmentAdminChargerRegisterStep1Binding binding;

    EvzBluetooth mEvzBluetooth;
    private EVZScanManager mScanner;
    List<EVZScanResult> mScData;

    CountDownTimer timer;

    public static String selectedChargerBLEText = "";
    public static Dialog dialog;

    ApiUtils apiUtils = new ApiUtils();
    AdminChargerModel adminChargerModel = new AdminChargerModel();

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {  // 실행이 끝난후 확인 가능
            Bundle bd = msg.getData();

            // bluetooth 활성화
            if (bd.getBoolean("bluetooth")) {

                getBLEScan();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("metis", "AdminChargerRegisterStep1Fragment");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_charger_register_step1, container, false);
        View root = binding.getRoot();

        mScanner = new EVZScanManager();
        mEvzBluetooth = new EvzBluetooth(getActivity());
        mEvzBluetooth.setBluetooth(true);

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

        binding.includeChargerRegisterFooter.nextButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.bright_white)));

        binding.includeChargerRegisterFooter.previousButton.setEnabled(false);
        binding.includeChargerRegisterFooter.nextButton.setEnabled(false);

        binding.includeChargerRegisterFooter.nextButton.setOnClickListener(view -> nextButton());

        binding.chargerSearchButton.setOnClickListener(view -> checkChargerInformation());
    }

    private void nextButton(){
        Log.d("metis", "AdminChargerRegisterStep1Fragment - nextButton bleNumber : " + selectedChargerBLEText);

        //다음 화면으로 넘어갈때 보내는 값
        Bundle bundle = new Bundle();
        bundle.putString("bleNumber", (String)adminChargerModel.getBleNumber());
        bundle.putInt("id", adminChargerModel.getId());
        bundle.putInt("providerCompanyId", adminChargerModel.getProviderCompanyId());

        ((AdminMainActivity) getActivity()).chargerRegisterNextStep(1, bundle);
    }

    private void checkChargerInformation(){

        Log.d("metis", "checkChargerInformation");
        String bleNumber = "A1:23:45:67:89:18".replaceAll(":","");

        adminChargerModel = apiUtils.getChargerInformationFromBleNumber(bleNumber);

        if(adminChargerModel.getResponseCode() == 200) {

            binding.includeChargerRegisterFooter.nextButton.setEnabled(true);
            binding.includeChargerRegisterFooter.nextButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.deep_purple)));

        } else {

            Log.d("metis", "차지인에 등록되지 않은 충전기");
            Toast.makeText(getContext(), "등록할수 없는 충전기 입니다.",Toast.LENGTH_SHORT).show();
        }

    }

    public void getBLEScan() {

        countDown(1000 * 5);

        mScanner.startScan(new EVZScanCallbacks() {

            @Override
            public void onScanFinished(@NonNull List<EVZScanResult> results) {
                mScData = results;
                if (mScData.size() > 0) {
                    hideLoading(binding.loading);
                    Log.e("metis", "onScan > 0");

                    String[] bleArray = new String[mScData.size()];
                    for (int i = 0; i < mScData.size(); i++) {
                        Log.e("metis", "BLE : " + mScData.get(i).getDevice().getAddress());

                        bleArray[i] = mScData.get(i).getDevice().getAddress();
                    }

                    showAlertDialogTopic(bleArray);
                } else {
                    Log.e("metis", "onScan = 0");
                    scanFailed();
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.e("metis", "onScanFailed");
                scanFailed();
            }
        });
    }

    private void showAlertDialogTopic(String[] bleArray) {

        dialog = new Dialog(getActivity());

        LayoutInflater inf = getLayoutInflater();
        View dialogView = inf.inflate(R.layout.charger_search_dialog, null);

        dialog.setContentView(dialogView); // Dialog에 선언했던 layout 적용
        dialog.setCanceledOnTouchOutside(true); // 외부 touch 시 Dialog 종료

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
        dialog.getWindow().setLayout(getDialogWidth(), WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show(); // Dialog 출력
    }

    //기기의 너비구해서 60% 값 리턴
    private int getDialogWidth() {

        Display display = getActivity().getWindowManager().getDefaultDisplay();  // in Activity

        Point size = new Point();
        display.getRealSize(size); // or getSize(size)
        int width = size.x;
        return width * 60 / 100;
    }

    private void scanFailed() {
        hideLoading(binding.loading);

        CustomDialog customDialog = new CustomDialog(getActivity(), "연결 가능한 충전기를 찾지 못했습니다.\n다시 검색하시겠습니까?");
        customDialog.show();
        customDialog.findViewById(R.id.dialog_ok_btn).setOnClickListener(view -> {
            Log.e("metis", "customDialog_ok_btn");
            customDialog.dismiss();
            showLoading(binding.loading);
            getBLEScan();
        });
    }

    /**
     * 카운트 다운 타이머
     * @param time 시간 ex) 3초 : 3000
     */
    public void countDown(long time) {

        showLoading(binding.loading);

        timer = new CountDownTimer(time, 1000) {

            // 특정 시간마다 뷰 변경
            public void onTick(long millisUntilFinished) {
            }

            // 제한시간 종료시
            public void onFinish() {
                hideLoading(binding.loading);
            }

        }.start();
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
