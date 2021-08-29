package kr.co.metisinfo.sharingcharger.view.activity;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.FragmentAdminChargerPriceSettingBinding;
import kr.co.metisinfo.sharingcharger.model.AdminChargerModel;
import kr.co.metisinfo.sharingcharger.model.PriceModel;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.utils.CustomTextWatcher;

import static kr.co.metisinfo.sharingcharger.base.Constants.CHANGE_PRICE;

public class AdminChargerPriceSettingFragment extends Fragment {

    FragmentAdminChargerPriceSettingBinding binding;

    InputMethodManager imm = (InputMethodManager) ThisApplication.context.getSystemService(Context.INPUT_METHOD_SERVICE);

    ApiUtils apiUtils = new ApiUtils();

    AdminChargerModel adminChargerModel = new AdminChargerModel();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {  // 실행이 끝난후 확인 가능

            hideLoading(binding.loading);

            switch (msg.what) {
                case CHANGE_PRICE :

                    final int resultCode = msg.arg1;

                    if (resultCode == 200) {
                        Toast.makeText(ThisApplication.context, "설정한 단가로 변경되었습니다.", Toast.LENGTH_LONG).show();
                    } else if (resultCode == 400) {
                        Toast.makeText(ThisApplication.context, "단가 변경에 실패하였습니다.\n문제 지속시 고객센터로 문의주세요.", Toast.LENGTH_LONG).show();
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_charger_price_setting, container, false);
        View view = binding.getRoot();

        Bundle bundle = getArguments();
        adminChargerModel = (AdminChargerModel) bundle.getSerializable("object");

        binding.newPriceInput.addTextChangedListener(new CustomTextWatcher(binding.newPriceInput));

        binding.priceToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int priceId = binding.priceToggle.getCheckedRadioButtonId();
                RadioButton price = view.findViewById(priceId);

                if (price.getText().toString().equals("직접 입력")) {
                    binding.newPriceInput.setText("");
                    binding.newPriceInput.requestFocus();
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                } else {
                    binding.newPriceInput.setText(price.getText().toString());
                }
            }
        });

        binding.modificationButton.setOnClickListener(v -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(((AdminMainActivity) getActivity()));
            dialogBuilder.setMessage("설정한 단가로 변경하시겠습니까?\n단가 정보 변경시 기존 예약건에 대해서는 적용되지 않고 신규 예약건에 대해서만 반영됩니다.\n변경하시겠습니까? ");
            dialogBuilder.setPositiveButton("확인", (dialog, which) ->{

                showLoading(binding.loading);

                int price = Integer.parseInt(binding.newPriceInput.getText().toString().trim().replaceAll(",",""));

                PriceModel priceModel = new PriceModel();
                priceModel.setPrice(price);
                priceModel.setUserId(ThisApplication.staticUserModel.id);

                int resultCode = apiUtils.changePrice(adminChargerModel.getId(), priceModel);

                Message msg = new Message();
                msg.what = CHANGE_PRICE;
                msg.arg1 = resultCode;
                handler.sendMessage(msg);
            });
            dialogBuilder.setNegativeButton("취소", null);
            dialogBuilder.create().show();
        });

        return view;
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

}
