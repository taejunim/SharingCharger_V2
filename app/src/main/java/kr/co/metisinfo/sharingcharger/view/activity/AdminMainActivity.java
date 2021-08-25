package kr.co.metisinfo.sharingcharger.view.activity;

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

public class AdminMainActivity extends BaseActivity {

    ActivityAdminMainBinding binding;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    AdminDashboardFragment adminDashboardFragment;
    AdminChargerManageFragment adminChargerManageFragment;

    ArrayList<ImageView> footerButtonList = new ArrayList<>();
    ArrayList<ImageView> subMenuButtonList = new ArrayList<>();


    @Override
    public void initLayout() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_main);

        binding.includeHeader.layoutHeaderMenu.setBackground(getDrawable(R.mipmap.add));
        binding.includeHeader.layoutHeaderMenu.getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        binding.includeHeader.layoutHeaderMenu.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

        //선택된 메뉴 버튼 색 변경을 위해 배열에 담기
        footerButtonList.add(binding.includeFooter.btnDashboardImage);
        footerButtonList.add(binding.includeFooter.btnChargerManageImage);
        footerButtonList.add(binding.includeFooter.btnAlarmImage);
        subMenuButtonList.add(binding.includeChargerManageMenu.btnChargerDetailInformation);
        subMenuButtonList.add(binding.includeChargerManageMenu.btnChargerPriceInformation);
        subMenuButtonList.add(binding.includeChargerManageMenu.btnChargerTimeSetting);
        subMenuButtonList.add(binding.includeChargerManageMenu.btnChargerInformationEdit);

    }

    @Override
    public void initViewModel() {

    }

    @Override
    public void setOnClickListener() {

        binding.includeHeader.btnBack.setOnClickListener(view -> finish());

        //충전기 정보 등록
        binding.includeHeader.btnMenu.setOnClickListener(view -> {
            //구현해야됨
        });

        //대시보드 버튼
        binding.includeFooter.btnDashboard.setOnClickListener(view -> {
            setFragment("footerMenu", binding.includeFooter.btnDashboardImage);
        });
        //충전기 관리
        binding.includeFooter.btnChargerManage.setOnClickListener(view -> {
            setFragment("footerMenu", binding.includeFooter.btnChargerManageImage);
        });

        //충전기 상세정보
        binding.includeChargerManageMenu.btnChargerDetailInformation.setOnClickListener(view -> {
            setFragment("subMenu", binding.includeChargerManageMenu.btnChargerDetailInformation);
        });

        //충전기 금액정보
        binding.includeChargerManageMenu.btnChargerPriceInformation.setOnClickListener(view -> {
            setFragment("subMenu", binding.includeChargerManageMenu.btnChargerPriceInformation);
        });

        //충전기 시간설정
        binding.includeChargerManageMenu.btnChargerTimeSetting.setOnClickListener(view -> {
            setFragment("subMenu", binding.includeChargerManageMenu.btnChargerTimeSetting);
        });

        //충전기 정보수정
        binding.includeChargerManageMenu.btnChargerInformationEdit.setOnClickListener(view -> {
            setFragment("subMenu", binding.includeChargerManageMenu.btnChargerInformationEdit);
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

        setFragment("footerMenu", binding.includeFooter.btnDashboardImage);
        addActivitys(this);
    }

    public void setFragment(String type, ImageView view){

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
                    binding.includeHeader.btnMenu.setVisibility(View.VISIBLE);
                    fragmentTransaction.replace(R.id.fragment_container, adminChargerManageFragment);
                    break;
                case 2 : //알림 설정
                    Toast.makeText(this,"알림설정 구현해야됨", Toast.LENGTH_SHORT).show();
                    break;
            }

        } else {

            binding.includeHeader.txtTitle.setText("충전기 관리");
            //충전기 정보 등록 숨기기
            binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);
            //submenu 보이게
            binding.includeChargerManageMenu.chargerManageMenu.setVisibility(View.VISIBLE);

            int index = 0;
            for( int i = 0; i<  subMenuButtonList.size() ; i ++ ) {

                if(subMenuButtonList.get(i) == view) {
                    index = i;
                    subMenuButtonList.get(i).setColorFilter(ContextCompat.getColor(this, R.color.purple));
                }
                else subMenuButtonList.get(i).setColorFilter(ContextCompat.getColor(this, R.color.gray_button));
            }

            fragmentTransaction = fragmentManager.beginTransaction();

            switch (index){
                case 0 : //충전기 상세정보
                    AdminChargerDetailInformationFragment adminChargerDetailInformationFragment = new AdminChargerDetailInformationFragment();
                    fragmentTransaction.replace(R.id.fragment_container, adminChargerDetailInformationFragment);
                    break;
                case 1 : //충전기 단가정보
                    AdminChargerPriceSettingFragment adminChargerPriceSettingFragment = new AdminChargerPriceSettingFragment();
                    fragmentTransaction.replace(R.id.fragment_container, adminChargerPriceSettingFragment);
                    break;
                case 2 : //충전기 시간설정
                    AdminChargerTimeSettingFragment adminChargerTimeSettingFragment = new AdminChargerTimeSettingFragment();
                    fragmentTransaction.replace(R.id.fragment_container, adminChargerTimeSettingFragment);
                    break;
                case 3 : //충전기 정보수정
                    AdminChargerInformationEditFragment adminChargerInformationEditFragment = new AdminChargerInformationEditFragment();
                    fragmentTransaction.replace(R.id.fragment_container, adminChargerInformationEditFragment);
                    break;
            }
        }

        fragmentTransaction.commit();
    }

    public void selectChargerManageMenu(int position){
        //recyclerView에서 무슨 값을 받아와야할지 모르니까, 일단 샘플성으로 position 받아옴
        setFragment("subMenu", binding.includeChargerManageMenu.btnChargerDetailInformation);
    }

}
