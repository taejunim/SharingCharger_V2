package kr.co.metisinfo.sharingcharger.view.viewInterface;

public interface DeviceStatusCallback {

    void setDeviceStatus(int status);

    int getDeviceStatus(); // 0 : disconnect, 1 : connect
}
