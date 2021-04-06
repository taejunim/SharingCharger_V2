package kr.co.metisinfo.sharingcharger.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.charzin.evzsdk.EvzBluetooth;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.Constants;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.ActivityIntroBinding;
import kr.co.metisinfo.sharingcharger.model.UserModel;
import kr.co.metisinfo.sharingcharger.service.GpsService;
import kr.co.metisinfo.sharingcharger.service.NetworkStatus;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;
import kr.co.metisinfo.sharingcharger.view.viewInterface.NetworkStatusInterface;
import kr.co.metisinfo.sharingcharger.viewModel.UserViewModel;
import retrofit2.Response;

public class IntroActivity extends BaseActivity implements NetworkStatusInterface {

    private static final String TAG = IntroActivity.class.getSimpleName();
    ActivityIntroBinding binding;

    Handler handler = new Handler();

    private UserViewModel userViewModel;

    private boolean isLoginSuccess = false;

    NetworkStatus networkStatus;

    UserModel userModel;

    EvzBluetooth mEvzBluetooth;

    ApiUtils apiUtils = new ApiUtils();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case Constants.PAGE_SETTING_GPS:

                Constants.gpsService = new GpsService(this);

                IntroActivity.BackgroundTask task = new IntroActivity.BackgroundTask();
                task.execute();

                break;
        }
    }


    @Override
    public void initLayout() {

        networkStatus = new NetworkStatus(this, this);

        networkStatus.registerNetworkCallback();

        if (checkLocationServiceStatus()) {

            Log.e(TAG, "GPS 가져올 수 있음");
        } else {
            Log.e(TAG, "GPS 가져올 수 없음");
        }

        Log.d(TAG, "Hash key : " + getKeyHash(getApplicationContext()));

        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        changeStatusBarColor(false);

        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(binding.imageLoading);
        Glide.with(this).load(R.drawable.intro_1440).into(gifImage);
    }

    @Override
    public void initViewModel() {

    }

    @Override
    public void setOnClickListener() {

    }

    @Override
    public void init() {

        mEvzBluetooth = new EvzBluetooth(IntroActivity.this);

        checkPermission();

    }

    private void checkPermission() {

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                /*.setRationaleMessage("현재 위치를 보여주기 위해 권한을 설정해주세요.")
                .setDeniedMessage("거부하셨습니다.\n[설정] > [권한] 에서 권한을 허용할 수 있어요.")*/
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.RECEIVE_SMS)
                .check();
    }


    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {

            mEvzBluetooth.setBluetooth(true);

            Constants.gpsService = new GpsService(IntroActivity.this);

            if (!Constants.gpsService.isGpsStatus()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(IntroActivity.this);

                builder.setTitle("GPS");
                builder.setMessage("GPS가 꺼져있어 위치정보를 가져올 수 없습니다.\nGPS를 켜 시겠습니까?");
                builder.setPositiveButton("확인", (dialog, which) -> {

                    Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(gpsOptionsIntent, Constants.PAGE_SETTING_GPS);
                });

                builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
                    IntroActivity.BackgroundTask task = new IntroActivity.BackgroundTask();
                    task.execute();
                });

                builder.show();

            } else {
                IntroActivity.BackgroundTask task = new IntroActivity.BackgroundTask();
                task.execute();
            }

            Log.e(TAG, "currentLocationLat : " + Constants.currentLocationLat);
            Log.e(TAG, "currentLocationLng : " + Constants.currentLocationLng);
            // TODO : 인트로 이미지 변경
            // TODO : 이미지 로딩 시간 변경
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            IntroActivity.BackgroundTask task = new IntroActivity.BackgroundTask();
            task.execute();
        }
    };

    @Override
    public void networkStatus(boolean isAvailable) {

        isNetworkStatus = isAvailable;

        isNetworkStatus = true;
        if (isNetworkStatus) {
//        if (isAvailable) {
/*
            if(!isFirstOpen) {
                getLogin(userModel);
            }*/

            Log.e(TAG, "네트워크를 사용할 준비가 되었을 때11111111111111");
        } else {
            Log.e(TAG, "네트워크가 끊켰을 때22222222222222222");
        }
    }


    /**
     * 카카오 키해시로 개발 및 배포 시에 카카오 개발자 사이트에 등록해야됨.
     *
     * @param context getApplicationContext
     * @return 카카오 keyHash
     */
    public String getKeyHash(final Context context) {

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            if (packageInfo == null)
                return null;

            for (Signature signature : packageInfo.signatures) {
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
                } catch (NoSuchAlgorithmException e) {
                    Log.w(TAG, "Unable to get MessageDigest. signature=" + signature, e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getKeyHash error : " + e.toString());
        }

        return null;
    }


    Runnable r = () -> {

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

        startActivity(intent);

        finish();
    };

    // 오토 로그인 성공 시
    Runnable m = () -> {

        // TODO 1. 내 계정으로 예약건이 있는지 1차 조회. ( 있을경우, 예약1건만 뿌려줌, 없을경우 Step2로 넘어감 )
        // TODO 2. 메인 화면 들어가기전 IntroActivity 혹은 LoginActivity에서 로그인 성공시 디폴트 값으로( 적정 요금, 반경 거리, 시작시간, 종료시간) 으로 충전기 정보를 불러옴.
        // TODO 3. 해당 내용 불러온 후 현재 Activity에서 충전기 목록을 MainActivity로 넘겨주기!!
        // TODO 4. MainActivity에서는 넘겨 받은 값으로 지도에 마커 뿌려주기

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(intent);

        finish();

    };

    private void getLogin(UserModel userModel) {

        try {
            Response<UserModel> response = apiUtils.login(userModel);

            //로그인 성공
            if (response.code() == 200 && response.body() != null) {

                UserModel user = response.body();

                user.pkId = userModel.pkId;
                if (user.getUserType().equals("General")) {
                    user.loginId = userModel.getEmail();
                } else {
                    user.loginId = userModel.getUsername();
                    user.email = user.getUsername();
                }

                user.autoLogin = true;

                Log.e(TAG, "response UserModel : " + user);

                //새로운 유저정보를 로컬디비에 저장함
                userViewModel.updateUserPoint(user);
                ThisApplication.staticUserModel = user;
                isLoginSuccess = true;

                handler.postDelayed(m, 3000); // 2초 뒤에 Runnable 객체 수행

            }
            //로그인정보가 맞지 않을 때
            else if (response.code() == 204) {
                userViewModel.deleteUser(userModel.email);
                Toast.makeText(getApplicationContext(), R.string.login_reject, Toast.LENGTH_LONG).show();
                handler.postDelayed(r, 1000); // 1초 뒤에 Runnable 객체 수행
            }
            //로그인 실패
            else {
                Toast.makeText(getApplicationContext(), R.string.login_reject, Toast.LENGTH_LONG).show();
                handler.postDelayed(r, 1000); // 1초 뒤에 Runnable 객체 수행
            }

        } catch (Exception e) {

            Log.e(TAG, "getLogin response : " + e);
            handler.postDelayed(r, 1000); // 1초 뒤에 Runnable 객체 수행
            isLoginSuccess = false;
        }

    }

    public boolean checkLocationServiceStatus() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkStatus.unregisterNetworkCallback();
    }


    // < >안에 들은 자료형은 순서대로 doInBackground, onProgressUpdate, onPostExecute의 매개변수 자료형(내가 사용할 매개변수타입을 설정하면된다)
    class BackgroundTask extends AsyncTask<Integer, Integer, Boolean> {

        public BackgroundTask() {
            super();
        }

        protected void onPreExecute() {

        }

        protected Boolean doInBackground(Integer... values) {

            //db에 오토로그인 된 유저가 있는지 확인
            UserModel getUser = userViewModel.selectAutoLoginUser(true);

            //오토로그인 된 유저가 있다면 실제 서버에 로그인
            if (getUser != null) {

                if (!isLoginSuccess) {
                    userModel = getUser;

                    if (getUser.getUserType().equals("General")) {
                        userModel.loginId = userModel.getEmail();
                    } else {
                        userModel.loginId = userModel.getUsername();
                        userModel.email = userModel.getUsername();
                    }

                    Log.e(TAG, "userModel : " + getUser.toString());

                    getLogin(userModel);
                } else {

                    handler.postDelayed(r, 1000); // 2초 뒤에 Runnable 객체 수행

                }
            } else {

                handler.postDelayed(r, 1000); // 1초 뒤에 Runnable 객체 수행

            }
            return true;
        }

        protected void onPostExecute(Boolean isInsert) {

        }
    }
}
