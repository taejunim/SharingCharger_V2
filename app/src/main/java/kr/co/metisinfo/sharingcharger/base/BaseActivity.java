package kr.co.metisinfo.sharingcharger.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.ble.BleTestActivity;
import kr.co.metisinfo.sharingcharger.ble.ScanActivity;
import kr.co.metisinfo.sharingcharger.model.MenuHeaderVO;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.userManagement.ChargerFavoriteActivity;
import kr.co.metisinfo.sharingcharger.charger.ChargerUseHistoryActivity;
import kr.co.metisinfo.sharingcharger.view.activity.SettingActivity;
import kr.co.metisinfo.sharingcharger.digitalWalletManagement.WalletActivity;
import kr.co.metisinfo.sharingcharger.view.activity.WebViewActivity;

import static kr.co.metisinfo.sharingcharger.base.Constants.PAGE_SEARCH_KEYWORD;

/**
 * 추상화 클래스로 해당 클래스를 extends 하면 initLayout(), initViewModel(), setOnClickListener(), init() 함수들을 추가해야됨
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    public static ArrayList<Activity> activityList = new ArrayList<>();

    private DrawerLayout layoutId;

    Context startClass;

    private HashMap<String, List<String>> listDataChild;
    private List<MenuHeaderVO> listDataHeader;

    View view_Group;

    public boolean isNetworkStatus = true;

    ApiUtils apiUtils = new ApiUtils();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initLayout();
        initViewModel();
        setOnClickListener();

        init();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


    /**
     * Layout 초기화. onCreate보다 제일 먼저 호출 함.
     */
    public abstract void initLayout();

    /**
     * viewModel 초기화. onCreate보다 두번째로 호출 함. 완료 후 onCreate 호출
     */
    public abstract void initViewModel();

    public abstract void setOnClickListener();

    public abstract void init();


    public void changeStatusBarColor(boolean isFullScreen) {

        if (isFullScreen) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                window.setStatusBarColor(Color.TRANSPARENT);

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                Window window = getWindow();

                window.setStatusBarColor(Color.BLACK);
            }

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                Window window = getWindow();

                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                //window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                //window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


                window.setStatusBarColor(Color.TRANSPARENT);

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                Window window = getWindow();

                window.setStatusBarColor(Color.BLACK);
            }
        }
    }


    private DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {

        @Override
        public void onDrawerStateChanged(int status) {

        }

        @Override
        public void onDrawerSlide(@NonNull View view, float slideArg) {

        }

        @Override
        public void onDrawerOpened(@NonNull View view) {
//            getActionBar().setTitle(mDrawerTitle);
            // calling onPrepareOptionsMenu() to hide action bar icons
            invalidateOptionsMenu();
        }

        @Override
        public void onDrawerClosed(@Nullable View view) {
//            getActionBar().setTitle(mTitle);
            // calling onPrepareOptionsMenu() to show action bar icons
            invalidateOptionsMenu();
        }
    };

    // 자식 클래스의 Context를 받아와 상단 메뉴바를 공통으로 사용하기 위함.
    public void setMenuClass(Context context) {

        startClass = context;
    }

    public void addActivitys(Activity activity) {

        activityList.add(activity);
    }

    /**
     * Get the names and icons references to build the drawer menu...
     */
    public void setUpDrawer(int drawerLayoutId, int listSliderMenuId) {

        layoutId = findViewById(drawerLayoutId);

        ExpandableListView sliderMenuId = findViewById(listSliderMenuId);

        layoutId.setDrawerListener(mDrawerListener);
        prepareListData();

        ExpandableListAdapter listAdapter = new ExpandableListAdapter(startClass, listDataHeader, listDataChild);

        sliderMenuId.setAdapter(listAdapter);

        sliderMenuId.setOnGroupClickListener((parent, v, groupPosition, id) -> {

            if (groupPosition == 0) {

                //충전기 사용 이력
                Intent intent = new Intent(startClass, ChargerUseHistoryActivity.class);
                //startActivity(intent);
                startActivityForResult(intent, PAGE_SEARCH_KEYWORD);
                close_navi();

            } else if (groupPosition == 1) {

                //전자지갑
                Intent intent = new Intent(startClass, WalletActivity.class);
                startActivity(intent);

            } else if (groupPosition == 2) {

                //즐겨찾기
                Intent intent = new Intent(startClass, ChargerFavoriteActivity.class);
                //startActivity(intent);
                startActivityForResult(intent, PAGE_SEARCH_KEYWORD);
                close_navi();


            } else if (groupPosition == 3) {

                //회원 증명서
                Intent intent = new Intent(startClass, WebViewActivity.class);
                intent.putExtra("getTagName", "setting");                             //WEBVIEW GBN PARAM -> setting은 회원 증명서
                startActivity(intent);
                close_navi();

            } else if (groupPosition == 4) {

                //고객센터

            }

            return false;
        });

        sliderMenuId.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {

            v.setSelected(true);

            if (view_Group != null) {
                view_Group.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            view_Group = v;
            view_Group.setBackgroundColor(Color.parseColor("#ffffff"));

            sliderMenuId.setItemChecked(childPosition, true);
            layoutId.closeDrawer(GravityCompat.START);


            if (groupPosition == 1 && childPosition == 0) {

                Toast.makeText(startClass, "포인트이력 기간별", Toast.LENGTH_LONG).show();
            } else if (groupPosition == 1 && childPosition == 1) {

                Toast.makeText(startClass, "포인트 이력 일자별", Toast.LENGTH_LONG).show();

            }

            return false;
        });
    }


    private void prepareListData() {

        Log.e(TAG, "prepareListData START : " + startClass.getClass().getSimpleName());

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        String[] array = getResources().getStringArray(R.array.nav_drawer_items);

        for (int i = 0; i < Arrays.asList(array).size(); i++) {

            MenuHeaderVO vo = new MenuHeaderVO();
            vo.setMenuHeaderNm(array[i]);
            vo.setShowFlag(false);

            listDataHeader.add(vo);
        }

        // 충전기 사용 이력
        String[] historyCharging = getResources().getStringArray(R.array.history_charging);
        List<String> listHistoryCharging = Arrays.asList(historyCharging);

        // 전자지갑
        String[] electWallet = getResources().getStringArray(R.array.elect_wallet);
        List<String> listElectWallet = Arrays.asList(electWallet);

        // 즐겨찾기
        String[] favorite = getResources().getStringArray(R.array.favorite);
        List<String> listFavoriteCharger = Arrays.asList(favorite);

        // 회원 증명서
        String[] membership = getResources().getStringArray(R.array.membership);
        List<String> listMembership = Arrays.asList(membership);

        // 고객센터
        String[] customerCenter = getResources().getStringArray(R.array.customer_center);
        List<String> listCustomerCenter = Arrays.asList(customerCenter);

        listDataChild.put(listDataHeader.get(0).getMenuHeaderNm(), listHistoryCharging);
        listDataChild.put(listDataHeader.get(1).getMenuHeaderNm(), listElectWallet);
        listDataChild.put(listDataHeader.get(2).getMenuHeaderNm(), listFavoriteCharger);
        listDataChild.put(listDataHeader.get(3).getMenuHeaderNm(), listMembership);
        listDataChild.put(listDataHeader.get(4).getMenuHeaderNm(), listCustomerCenter);

    }

    /**
     * 설정 버튼 눌렀을 때
     *
     * @param view
     */
    public void show_settings(View view) {

        //설정화면
        Intent intent = new Intent(startClass, SettingActivity.class);

        startActivity(intent);

        close_navi();

    }

    /**
     * 네비게이션 클로즈
     */
    public void close_navi() {

        layoutId.closeDrawer(GravityCompat.START);
    }

    public void setActionBarDrawerToggle(DrawerLayout drawerLayout, ImageButton btn) {

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle((Activity) startClass, drawerLayout, null, R.string.app_name, R.string.app_name) {

            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);

        btn.setOnClickListener(v -> {

            RelativeLayout layout1 = drawerLayout.findViewById(R.id.layout_reservation_detail_info);

            if (layout1.getVisibility() == View.VISIBLE) {
                layout1.setVisibility(View.INVISIBLE);
            }

            setUserInfo(drawerLayout);

            Button goCharging = drawerLayout.findViewById(R.id.go_charging);

            //충전하기
            goCharging.setOnClickListener(view -> {

            });

        });

        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<MenuHeaderVO> _listDataHeader;

        private HashMap<String, List<String>> _listDataChild;

        public ExpandableListAdapter(Context context, List<MenuHeaderVO> listDataHeader, HashMap<String, List<String>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition).getMenuHeaderNm()).get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }


        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {

                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_item, null);
            }

            TextView txtListChild = convertView.findViewById(R.id.lblListItem);

            txtListChild.setText(childText);
            return convertView;
        }


        @Override
        public int getChildrenCount(int groupPosition) {

            try {
                return this._listDataChild.get(this._listDataHeader.get(groupPosition).getMenuHeaderNm()).size();
            } catch (NullPointerException e) {
                return 0;
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition).getMenuHeaderNm();
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }


        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            String headerTitle = (String) getGroup(groupPosition);

            if (convertView == null) {

                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_group, null);
            }

            ImageView txt_plusminus = convertView.findViewById(R.id.plus_txt);

            // 메뉴에 자식이 있을 경우 펼쳐보기 화살표를 보여 준다.
            if (getChildrenCount(groupPosition) != 0) {

                if (isExpanded) {
                    txt_plusminus.setBackground(getDrawable(R.mipmap.btn_dropdown));
                } else {
                    txt_plusminus.setBackground(getDrawable(R.mipmap.btn_dropdown));
                }
            } else {

                // 메뉴에 자식이 없을 경우 펼쳐보기 화살표를 보여주지 않는다.
                txt_plusminus.setBackground(null);
            }

            TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);

            lblListHeader.setText(headerTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    public void setUserInfo(DrawerLayout drawerLayout) {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);

            TextView txtName = drawerLayout.findViewById(R.id.txt_name);
            TextView txtEmail = drawerLayout.findViewById(R.id.txt_email);
            TextView txtPoint = drawerLayout.findViewById(R.id.txt_point);

//            txtName.setText(ThisApplication.staticUserModel.getName());
//            txtEmail.setText(ThisApplication.staticUserModel.getEmail());

            TextView txtReserveTime = drawerLayout.findViewById(R.id.txt_reserve);
            TextView txtReserveSpot = drawerLayout.findViewById(R.id.txt_reserve_spot);

            try {

                //사용자 예약 상태 가져오기
//                txtReserveTime.setText("");
//                txtReserveSpot.setText("");

                //실시간 포인트 가져오기
//                txtPoint.setText("");


            } catch (Exception e) {
                Log.e(TAG, "Exception : " + e);

                txtReserveTime.setText("");
                txtReserveSpot.setText("");

            }

            txtEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "txtEmail click ");

                    Intent intent = new Intent(startClass, ScanActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //다른화면 전환시 닫기
        if (layoutId != null) {
            close_navi();
        }

    }

}
