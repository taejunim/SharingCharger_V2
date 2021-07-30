package kr.co.metisinfo.sharingcharger.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import kr.co.metisinfo.sharingcharger.base.Constants;

public class GpsService extends Service implements LocationListener {

    private static final String TAG = GpsService.class.getSimpleName();
    private final Context mContext;
    Location location;
//    double lat = -1;
//    double lng = -1;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1;
    protected LocationManager locationManager;

    private boolean gpsStatus = false;
    public GpsService(Context context) {
        this.mContext = context;

        gpsStatus = getLocation();
    }


    public boolean isGpsStatus() {

        return gpsStatus;
    }

    public boolean getLocation() {

        try {



            locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);

            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {



                return false;

            } else {

                startUsingGPS();

                return true;
            }



/*


                int hasFineLocationPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
                int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);

                if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
                    if(locationManager != null) {


                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if(location != null) {
                            Constants.currentLocationLng = location.getLongitude();
                            Constants.currentLocationLat = location.getLatitude();
                        }
                    }
                }

*//*                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.


//                 locationManager.getCurrentLocation();

                    return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }*//*



            } else {




                if(isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if(locationManager != null) {

                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if(location != null) {
                            Constants.currentLocationLng = location.getLongitude();
                            Constants.currentLocationLat = location.getLatitude();
                        }
                    }
                }


                if(isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if(locationManager != null) {

                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if(location != null) {
                            Constants.currentLocationLng = location.getLongitude();
                            Constants.currentLocationLat = location.getLatitude();
                        }
                    }
                }
            }*/

        } catch (Exception e) {

            e.printStackTrace();

        }

        return true;
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {

        String provider = location.getProvider();
        Constants.currentLocationLng = location.getLongitude();
        Constants.currentLocationLat = location.getLatitude();
        double altitude = location.getAltitude();


        Log.e("metis", "위치정보 : " + provider + "\n" +
                "위도 : " + Constants.currentLocationLng + "\n" +
                "경도 : " + Constants.currentLocationLat + "\n" +
                "고도  : " + altitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void stopUsingGPS() {

        if(locationManager != null) {
            locationManager.removeUpdates(GpsService.this);
        }
    }

    public void startUsingGPS() {

        try {


            locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);

            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled || isNetworkEnabled) {

                int hasFineLocationPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
                int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);

                if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

                    if (isGPSEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {

                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (location != null) {
                                Constants.currentLocationLng = location.getLongitude();
                                Constants.currentLocationLat = location.getLatitude();
                            }
                        }
                    }


                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {

                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if (location != null) {
                                Constants.currentLocationLng = location.getLongitude();
                                Constants.currentLocationLat = location.getLatitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }
}
