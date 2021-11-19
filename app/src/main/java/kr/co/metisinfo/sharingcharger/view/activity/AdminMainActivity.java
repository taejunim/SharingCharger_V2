package kr.co.metisinfo.sharingcharger.view.activity;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityAdminMainBinding;
import kr.co.metisinfo.sharingcharger.model.AdminChargerModel;

public class AdminMainActivity extends BaseActivity {

    ActivityAdminMainBinding binding;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    AdminChargerManageFragment adminChargerManageFragment;

    ArrayList<ImageView> subMenuButtonList = new ArrayList<>();

    AdminChargerModel adminChargerModel = new AdminChargerModel();

    @Override
    public void initLayout() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_main);

        binding.includeHeader.layoutHeaderMenu.setBackground(getDrawable(R.mipmap.add));
        binding.includeHeader.layoutHeaderMenu.getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        binding.includeHeader.layoutHeaderMenu.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

        //선택된 메뉴 버튼 색 변경을 위해 배열에 담기
        subMenuButtonList.add(binding.includeChargerManageMenu.btnChargerDetailInformation);
        subMenuButtonList.add(binding.includeChargerManageMenu.btnChargerPriceInformation);
        subMenuButtonList.add(binding.includeChargerManageMenu.btnChargerTimeSetting);
        subMenuButtonList.add(binding.includeChargerManageMenu.btnChargerInformationEdit);
        subMenuButtonList.add(binding.includeChargerManageMenu.btnChargeHistory);
    }

    @Override
    public void initViewModel() {}

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnBack.setOnClickListener(view -> {

            //백버튼 클릭시 쌓인 프래그먼트가 없으면 소유주 충전기 관리 화면 종료
            if(fragmentManager.getBackStackEntryCount() == 0) {
                finish();
            }

            //최근 프래그먼트 화면 종료
            else {
                fragmentManager.popBackStack("fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                setFragment("main", null, adminChargerModel);
            }
        });

        //충전기 정보 등록
        binding.includeHeader.btnMenu.setOnClickListener(view -> {
            setFragment("registerMenu", null, adminChargerModel);
        });

        //충전기 상세정보
        binding.includeChargerManageMenu.btnChargerDetailInformation.setOnClickListener(view -> {
            setFragment("subMenu", binding.includeChargerManageMenu.btnChargerDetailInformation, adminChargerModel);
        });

        //충전기 금액정보
        binding.includeChargerManageMenu.btnChargerPriceInformation.setOnClickListener(view -> {
            setFragment("subMenu", binding.includeChargerManageMenu.btnChargerPriceInformation, adminChargerModel);
        });

        //충전기 시간설정
        binding.includeChargerManageMenu.btnChargerTimeSetting.setOnClickListener(view -> {
            setFragment("subMenu", binding.includeChargerManageMenu.btnChargerTimeSetting, adminChargerModel);
        });

        //충전기 정보수정
        binding.includeChargerManageMenu.btnChargerInformationEdit.setOnClickListener(view -> {
            setFragment("subMenu", binding.includeChargerManageMenu.btnChargerInformationEdit, adminChargerModel);
        });

        //충전기 충전 이력
        binding.includeChargerManageMenu.btnChargeHistory.setOnClickListener(view -> {
            setFragment("subMenu", binding.includeChargerManageMenu.btnChargeHistory, adminChargerModel);
        });
    }

    @Override
    public void init() {
        binding.includeHeader.txtTitle.setText(R.string.dashboard_title);
        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);
        binding.includeChargerManageMenu.chargerManageMenu.setVisibility(View.GONE);

        adminChargerManageFragment = new AdminChargerManageFragment();

        //프래그먼트 초기화
        fragmentManager = getSupportFragmentManager();

        setFragment("main", null, null);
        addActivitys(this);
    }

    public void setFragment(String type, ImageView view, Object object){

        //소유주 충전기 관리 메인
        if (type == "main") {

            //submenu 숨기기
            binding.includeChargerManageMenu.chargerManageMenu.setVisibility(View.GONE);

            fragmentTransaction = fragmentManager.beginTransaction();

            binding.includeHeader.txtTitle.setText(R.string.charger_manage_title);
            binding.includeHeader.btnMenu.setVisibility(View.VISIBLE);
            fragmentTransaction.replace(R.id.fragment_container, adminChargerManageFragment); //fragmentTransaction 로 프래그먼트 교체

        }

        //소유주 충전기 등록 화면
        else if(type == "registerMenu"){

            binding.includeHeader.txtTitle.setText("충전기 등록");
            //충전기 정보 등록 숨기기
            binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);
            //submenu 숨기기
            binding.includeChargerManageMenu.chargerManageMenu.setVisibility(View.GONE);
            fragmentTransaction = fragmentManager.beginTransaction();
            AdminChargerRegisterStep1Fragment adminChargerRegisterStep1Fragment = new AdminChargerRegisterStep1Fragment();
            fragmentTransaction.replace(R.id.fragment_container, adminChargerRegisterStep1Fragment);
            fragmentTransaction.addToBackStack("fragment"); //뒤로가기 기능을 위해 fragment를 stack 에 쌓음

        }else {

            binding.includeHeader.txtTitle.setText("충전기 관리");
            //충전기 정보 등록 숨기기
            binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);
            //submenu 보이게
            binding.includeChargerManageMenu.chargerManageMenu.setVisibility(View.VISIBLE);

            AdminChargerModel tempAdminChargerModel = (AdminChargerModel) object;

            //충전기 공유 타입이 "항시 공유" 또는 "부분 공유" 가 있는데 이에 따라 충전기 운영 시간 설정 버튼을 숨기거나 보여짐
            if (tempAdminChargerModel.sharedType.equals("SHARING")) {
                binding.includeChargerManageMenu.btnChargerTimeSetting.setVisibility(View.GONE);
            } else {
                binding.includeChargerManageMenu.btnChargerTimeSetting.setVisibility(View.VISIBLE);
            }

            //현재 보고있는 화면의 버튼만 보라색 처리
            int index = 0;
            for( int i = 0; i<  subMenuButtonList.size() ; i ++ ) {

                if(subMenuButtonList.get(i) == view) {
                    index = i;
                    subMenuButtonList.get(i).setColorFilter(ContextCompat.getColor(this, R.color.purple));
                }
                else subMenuButtonList.get(i).setColorFilter(ContextCompat.getColor(this, R.color.gray_button));
            }

            fragmentTransaction = fragmentManager.beginTransaction();

            //화면이 바뀔 때 Object 데이터 전달
            Bundle bundle = new Bundle();
            bundle.putSerializable("object", (AdminChargerModel) object);

            switch (index){
                case 0 : //충전기 상세정보
                    AdminChargerDetailInformationFragment adminChargerDetailInformationFragment = new AdminChargerDetailInformationFragment();
                    adminChargerDetailInformationFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_container, adminChargerDetailInformationFragment);
                    fragmentTransaction.addToBackStack("fragment"); // 충전기 관리 메인 화면에서 목록중 충전기를 클릭할 경우 충전기 상세 정보 프래그먼트로 진입
                    break;
                case 1 : //충전기 단가정보
                    AdminChargerPriceSettingFragment adminChargerPriceSettingFragment = new AdminChargerPriceSettingFragment();
                    adminChargerPriceSettingFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_container, adminChargerPriceSettingFragment);
                    break;
                case 2 : //충전기 시간설정
                    AdminChargerTimeSettingFragment adminChargerTimeSettingFragment = new AdminChargerTimeSettingFragment();
                    adminChargerTimeSettingFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_container, adminChargerTimeSettingFragment);
                    break;
                case 3 : //충전기 정보수정
                    AdminChargerInformationEditFragment adminChargerInformationEditFragment = new AdminChargerInformationEditFragment();
                    adminChargerInformationEditFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_container, adminChargerInformationEditFragment);
                    break;

                case 4 : //충전기 충전 이력
                    AdminChargeHistoryFragment adminChargeHistoryFragment = new AdminChargeHistoryFragment();
                    adminChargeHistoryFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_container, adminChargeHistoryFragment);
                    break;
            }
        }

        fragmentTransaction.commit();
    }

    public void selectChargerManageMenu(AdminChargerModel receivedAdminChargerModel){
        adminChargerModel = receivedAdminChargerModel;
        setFragment("subMenu", binding.includeChargerManageMenu.btnChargerDetailInformation, receivedAdminChargerModel);
    }

    //충전기 등록 화면에서 다음 단계로 갈 경우
    public void chargerRegisterNextStep(int step, Bundle bundle){
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (step){
            case 1 :
                AdminChargerRegisterStep2Fragment adminChargerRegisterStep2Fragment = new AdminChargerRegisterStep2Fragment();
                adminChargerRegisterStep2Fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment_container, adminChargerRegisterStep2Fragment);
                break;
            case 2 :
                AdminChargerRegisterStep3Fragment adminChargerRegisterStep3Fragment = new AdminChargerRegisterStep3Fragment();
                adminChargerRegisterStep3Fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment_container, adminChargerRegisterStep3Fragment);
                break;

            //충전기 등록 완료시 충전기 관리 화면으로 돌아옴
            case 3 :
                fragmentManager.popBackStack("fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                adminChargerManageFragment = new AdminChargerManageFragment();

                //플래그먼트 초기화
                fragmentManager = getSupportFragmentManager();

                setFragment("main", null, null);
        }

        if(step != 3) fragmentTransaction.commit();
    }

    //충전기 등록 화면에서 전단계로 갈 경우
    public void chargerRegisterPreviousStep(int step, Bundle bundle){
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (step){
            case 2 :
                AdminChargerRegisterStep1Fragment adminChargerRegisterStep1Fragment = new AdminChargerRegisterStep1Fragment();
                adminChargerRegisterStep1Fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment_container, adminChargerRegisterStep1Fragment);
                fragmentTransaction.addToBackStack("fragment");
                break;
            case 3 :
                AdminChargerRegisterStep2Fragment adminChargerRegisterStep2Fragment = new AdminChargerRegisterStep2Fragment();
                adminChargerRegisterStep2Fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment_container, adminChargerRegisterStep2Fragment);
                break;
        }

        fragmentTransaction.commit();
    }
}
