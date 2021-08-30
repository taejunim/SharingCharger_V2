package kr.co.metisinfo.sharingcharger.view.activity;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityAdminMainBinding;
import kr.co.metisinfo.sharingcharger.model.AdminChargerModel;
import lombok.val;

public class AdminMainActivity extends BaseActivity {

    ActivityAdminMainBinding binding;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    AdminDashboardFragment adminDashboardFragment;
    AdminChargerManageFragment adminChargerManageFragment;

    ArrayList<ImageView> footerButtonList = new ArrayList<>();
    ArrayList<ImageView> subMenuButtonList = new ArrayList<>();

    AdminChargerModel adminChargerModel = new AdminChargerModel();

    @Override
    public void initLayout() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_main);

        binding.includeHeader.layoutHeaderMenu.setBackground(getDrawable(R.mipmap.add));
        binding.includeHeader.layoutHeaderMenu.getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        binding.includeHeader.layoutHeaderMenu.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

        //선택된 메뉴 버튼 색 변경을 위해 배열에 담기
        footerButtonList.add(binding.includeFooter.btnDashboardImage);
        footerButtonList.add(binding.includeFooter.btnChargerManageImage);
        //footerButtonList.add(binding.includeFooter.btnAlarmImage);
        subMenuButtonList.add(binding.includeChargerManageMenu.btnChargerDetailInformation);
        subMenuButtonList.add(binding.includeChargerManageMenu.btnChargerPriceInformation);
        subMenuButtonList.add(binding.includeChargerManageMenu.btnChargerTimeSetting);
        subMenuButtonList.add(binding.includeChargerManageMenu.btnChargerInformationEdit);
    }

    @Override
    public void initViewModel() {}

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnBack.setOnClickListener(view -> {
            //finish();

            if(fragmentManager.getBackStackEntryCount() == 0) {
                finish();
            } else {
                fragmentManager.popBackStack("AdminChargerManageFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                setFragment("footerMenu", binding.includeFooter.btnChargerManageImage, adminChargerModel);
            }
        });

        //충전기 정보 등록
        binding.includeHeader.btnMenu.setOnClickListener(view -> {
            setFragment("registerMenu", binding.includeFooter.btnDashboardImage, adminChargerModel);
        });

        //대시보드 버튼
        binding.includeFooter.btnDashboard.setOnClickListener(view -> {
            fragmentManager.popBackStack("AdminChargerManageFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            setFragment("footerMenu", binding.includeFooter.btnDashboardImage, adminChargerModel);
        });
        //충전기 관리
        binding.includeFooter.btnChargerManage.setOnClickListener(view -> {
            setFragment("footerMenu", binding.includeFooter.btnChargerManageImage, adminChargerModel);
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
    }

    @Override
    public void init() {
        binding.includeHeader.txtTitle.setText(R.string.dashboard_title);
        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);
        binding.includeChargerManageMenu.chargerManageMenu.setVisibility(View.GONE);

        adminDashboardFragment = new AdminDashboardFragment();
        adminChargerManageFragment = new AdminChargerManageFragment();

        //플래그먼트 초기화
        fragmentManager = getSupportFragmentManager();

        setFragment("footerMenu", binding.includeFooter.btnDashboardImage, null);
        addActivitys(this);
    }

    public void setFragment(String type, ImageView view, Object object){

        if(type == "footerMenu"){
            //submenu 숨기기
            binding.includeChargerManageMenu.chargerManageMenu.setVisibility(View.GONE);

            int index = 0;
            for( int i = 0; i<  footerButtonList.size() ; i ++ ) {

                if(footerButtonList.get(i) == view) {
                    index = i;
                    footerButtonList.get(i).setColorFilter(ContextCompat.getColor(this, R.color.purple));
                }
                else footerButtonList.get(i).setColorFilter(ContextCompat.getColor(this, R.color.gray_button));
            }

            fragmentTransaction = fragmentManager.beginTransaction();

            switch (index){
                case 0 : //대시보드
                    binding.includeHeader.txtTitle.setText(R.string.dashboard_title);
                    binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);
                    fragmentTransaction.replace(R.id.fragment_container, adminDashboardFragment);
                    break;
                case 1 : //충전기 관리
                    binding.includeHeader.txtTitle.setText(R.string.charger_manage_title);
                    binding.includeHeader.btnMenu.setVisibility(View.VISIBLE);
                    fragmentTransaction.replace(R.id.fragment_container, adminChargerManageFragment);
                    break;
                /*case 2 : //알림 설정
                    Toast.makeText(this,"알림설정 구현해야됨", Toast.LENGTH_SHORT).show();
                    break;*/
            }

        } else if(type == "registerMenu"){

            binding.includeHeader.txtTitle.setText("충전기 등록");
            //충전기 정보 등록 숨기기
            binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);
            //submenu 숨기기
            binding.includeChargerManageMenu.chargerManageMenu.setVisibility(View.GONE);
            fragmentTransaction = fragmentManager.beginTransaction();
            AdminChargerRegisterStep1Fragment adminChargerRegisterStep1Fragment = new AdminChargerRegisterStep1Fragment();
            fragmentTransaction.replace(R.id.fragment_container, adminChargerRegisterStep1Fragment);

        }else {

            binding.includeHeader.txtTitle.setText("충전기 관리");
            //충전기 정보 등록 숨기기
            binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);
            //submenu 보이게
            binding.includeChargerManageMenu.chargerManageMenu.setVisibility(View.VISIBLE);

            AdminChargerModel tempAdminChargerModel = (AdminChargerModel) object;

            if (tempAdminChargerModel.sharedType.equals("SHARING")) {
                binding.includeChargerManageMenu.btnChargerTimeSetting.setVisibility(View.GONE);
            } else {
                binding.includeChargerManageMenu.btnChargerTimeSetting.setVisibility(View.VISIBLE);
            }

            int index = 0;
            for( int i = 0; i<  subMenuButtonList.size() ; i ++ ) {

                if(subMenuButtonList.get(i) == view) {
                    index = i;
                    subMenuButtonList.get(i).setColorFilter(ContextCompat.getColor(this, R.color.purple));
                }
                else subMenuButtonList.get(i).setColorFilter(ContextCompat.getColor(this, R.color.gray_button));
            }

            fragmentTransaction = fragmentManager.beginTransaction();

            Bundle bundle = new Bundle();
            bundle.putSerializable("object", (AdminChargerModel) object);

            switch (index){
                case 0 : //충전기 상세정보
                    AdminChargerDetailInformationFragment adminChargerDetailInformationFragment = new AdminChargerDetailInformationFragment();
                    adminChargerDetailInformationFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_container, adminChargerDetailInformationFragment);
                    fragmentTransaction.addToBackStack("AdminChargerManageFragment");
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
            }
        }

        fragmentTransaction.commit();
    }

    public void selectChargerManageMenu(AdminChargerModel receivedAdminChargerModel){
        adminChargerModel = receivedAdminChargerModel;
        setFragment("subMenu", binding.includeChargerManageMenu.btnChargerDetailInformation, receivedAdminChargerModel);
    }

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
            case 3 :
                setFragment("footerMenu", binding.includeFooter.btnChargerManageImage, adminChargerModel);
        }

        if(step != 3) fragmentTransaction.commit();
    }

    public void chargerRegisterPreviousStep(int step, Bundle bundle){
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (step){
            case 2 :
                AdminChargerRegisterStep1Fragment adminChargerRegisterStep1Fragment = new AdminChargerRegisterStep1Fragment();
                adminChargerRegisterStep1Fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment_container, adminChargerRegisterStep1Fragment);
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
