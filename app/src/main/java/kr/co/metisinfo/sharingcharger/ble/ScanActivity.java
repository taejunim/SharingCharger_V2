package kr.co.metisinfo.sharingcharger.ble;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.widget.Toast;

import com.charzin.evzsdk.EvzBluetooth;
import com.evzlife.android.blescanner.EVZScanCallbacks;
import com.evzlife.android.blescanner.EVZScanManager;
import com.evzlife.android.blescanner.EVZScanResult;
import com.nabinbhandari.android.permissions.PermissionHandler;

import kr.co.metisinfo.sharingcharger.R;


public class ScanActivity extends Activity {

    EvzBluetooth mEvzBluetooth;

    //스캔 관련.
    private EVZScanManager mScanner;
    java.util.List<EVZScanResult> mScData;
    EVZScanResult mEVZScanResult;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_activity);

        mScanner = new EVZScanManager();

        String[] permissions =
                {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET,
                };

        com.nabinbhandari.android.permissions.Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
//                Toast.makeText(MainActivity.this, "Camera+Storage granted.", Toast.LENGTH_SHORT).show();
            }
        });


        mEvzBluetooth = new EvzBluetooth(ScanActivity.this);

        boolean isBluetooth = mEvzBluetooth.setBluetooth(true);
        if(isBluetooth)
        {
            Toast.makeText(ScanActivity.this, "Bluetooth ON", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(ScanActivity.this, "Bluetooth OFF", Toast.LENGTH_SHORT).show();
        }


        findViewById(R.id.button12).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                mScanner.startScan(new EVZScanCallbacks() {
                    @Override
                    public void onScanFinished( java.util.List<EVZScanResult> results) {
                        mScData = results;
                        if(mScData.size() > 0)
                        {
                            String str[] = new String[mScData.size()];
//
                            for (int i = 0; i < mScData.size(); i++)
                            {
                                str[i] = mScData.get(i).getDevice().getAddress();
                            }
                            showDialogButtonClick(str);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"NO SCAN", Toast.LENGTH_SHORT).show();
                        }

                    }
                    @Override
                    public void onScanFailed(int errorCode) {
                        Toast.makeText(getApplicationContext(),"onScanFailed = "+errorCode, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        findViewById(R.id.button22).setOnClickListener(v -> {
            Intent it = new Intent(ScanActivity.this, BleTestActivity.class);
            it.putExtra("scan", mEVZScanResult);
            startActivity(it);

        });


        findViewById(R.id.button3).setOnClickListener(view ->{

            //10,000
            Intent it = new Intent(ScanActivity.this, SampleWebView.class);
            it.putExtra("url", "http://119.65.249.4:2190/api/user/jeju_pay?product_amt=10000"+"&user_info1=210407");
            startActivity(it);

        });
        findViewById(R.id.button10).setOnClickListener(view ->{
            //30,000
            Intent it = new Intent(ScanActivity.this, SampleWebView.class);
            it.putExtra("url", "http://119.65.249.4:2190/api/user/jeju_pay?product_amt=30000"+"&user_info1=210407");
            startActivity(it);

        });
        findViewById(R.id.button11).setOnClickListener(view ->{
            //50,000
            Intent it = new Intent(ScanActivity.this, SampleWebView.class);
            it.putExtra("url", "http://119.65.249.4:2190/api/user/jeju_pay?product_amt=50000"+"&user_info1=210407");
            startActivity(it);
        });

    }

    private void showDialogButtonClick(final String choiceList[]) {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(ScanActivity.this);
        builder.setTitle("BLE SCAN");
        int selected = 0; // select at 0

        builder.setSingleChoiceItems(
                choiceList,
                selected,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        if(mScData.size() > 0)
                        {
                            mEVZScanResult = mScData.get(which);
                            Toast.makeText(getApplicationContext(), "BLE ID  = "+mEVZScanResult.getDevice().getAddress(), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "NO SCAN DATA, TRY SCAN", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        if(mScanner != null) {
            mScanner.release();
            mScanner = null;
        }
        super.onDestroy();
    }
}
