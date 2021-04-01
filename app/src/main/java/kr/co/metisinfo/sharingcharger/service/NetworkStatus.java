package kr.co.metisinfo.sharingcharger.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

import kr.co.metisinfo.sharingcharger.view.viewInterface.NetworkStatusInterface;

public class NetworkStatus extends ConnectivityManager.NetworkCallback{

    NetworkStatusInterface networkStatusInterface;

    Context context;
    ConnectivityManager manager;

    private NetworkRequest mNetworkRequest;

//    private ConnectivityManager.NetworkCallback mNetworkCallback;

    public NetworkStatus(Context context, NetworkStatusInterface statusInterface) {

        this.context = context;
        this.networkStatusInterface = statusInterface;

        manager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);

        mNetworkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
    }

    /**
     * 네트워크 상태 변경 감지 및 현재 상태를 보여줌.
     *
     * 1. 최초에는 네트워크 현재상태를 리턴
     * 2. 네트워크 상태 변경 감지 시 콜백 함수로 탐.
     * @return 현재 네트워크 상태
     */
    /*public boolean getConnectNetworkStatus() {

        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        mNetworkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(@NonNull Network network) {

                // 네트워크를 사용할 준비가 되었을 때
                networkStatusInterface.networkStatus(true);
            }

            @Override
            public void onLost(@NonNull Network network) {

                // 네트워크가 끊켰을 때
                networkStatusInterface.networkStatus(false);
            }
        };

        manager.registerNetworkCallback(builder.build(), mNetworkCallback);

        NetworkInfo mobile = manager.getNetworkInfo(manager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(manager.TYPE_WIFI);

        return mobile.isConnected() || wifi.isConnected();

    }
*/

    @Override
    public void onAvailable(@NonNull Network network) {

        // 네트워크를 사용할 준비가 되었을 때
        networkStatusInterface.networkStatus(true);
    }

    @Override
    public void onLost(@NonNull Network network) {

        // 네트워크가 끊켰을 때
        networkStatusInterface.networkStatus(false);
    }




    public void registerNetworkCallback() {

        this.manager.registerNetworkCallback(mNetworkRequest, this);
    }


    /**
     * 네트워크 상태 콜백 메서드 해제 함수.
     *
     * 특정 Activity가 Destroy 될 경우 해당 함수를 호출 할것.
     */
    public void unregisterNetworkCallback() {

        manager.unregisterNetworkCallback(this);

    }
}
