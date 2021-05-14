package kr.co.metisinfo.sharingcharger.ble;

import android.Manifest;
import android.app.Activity;
import android.widget.Button;
import android.widget.Toast;

import com.charzin.evzsdk.EvzBLE;
import com.charzin.evzsdk.EvzBLEData;
import com.charzin.evzsdk.EvzBLETagData;
import com.charzin.evzsdk.EvzProtocol;
import com.charzin.evzsdk.EvzScan;
import com.nabinbhandari.android.permissions.PermissionHandler;

import java.util.ArrayList;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.view.activity.MainActivity;


public class BleTestActivity extends Activity {

    EvzBLE mEB;
    EvzBLEData mCurData = new EvzBLEData();
    EvzBLETagData mCurTag = new EvzBLETagData();
    String ChargerTime  = "30";
    final String testTag = ""+ System.currentTimeMillis();

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_test);

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


        //Evz BLE 컨트롤
        mEB = new EvzBLE(BleTestActivity.this);
        mCurData = new EvzBLEData();
        //스캔데이터를 받어온다.
        mCurData.mEVZScanResult = getIntent().getParcelableExtra("scan");

        if(mCurData != null)
        {
            mEB.BLEConnect(mCurData, new EvzScan.BLEConnect() {
                @Override
                public void Success()
                {
                    Toast.makeText(getApplicationContext(), "BLE Connect Success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void Fail(int code, String msg) {

                }

            });
        }


        mEB.BLEDisConnect(new EvzScan.BLEDisConnect() {
            @Override
            public void disConnect(int code) {

                Toast.makeText(getApplicationContext(), "BLEDisConnect "+code, Toast.LENGTH_SHORT).show();
            }
        });

        //plug 상태값 확인
        mEB.BLEPlugState(new EvzProtocol.BLEPlugState() {
            @Override
            public void PlugState(int code) {
                Toast.makeText(getApplicationContext(), "Plug = "+code, Toast.LENGTH_SHORT).show();
            }
        });


        Button chargerStart = (Button)findViewById(R.id.button2);
        chargerStart.setText("Charger Start : "+ChargerTime+" min");

        chargerStart.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(android.view.View v) {
                mCurData.useTime = ChargerTime;
                mEB.BLEStart(mCurData,new EvzProtocol.BLEStart() {
                    @Override
                    public void Success()
                    {
                        Toast.makeText(BleTestActivity.this, "BLEStart Success", Toast.LENGTH_SHORT).show();
                        android.util.Log.e("KDH", "start Success");
                    }

                    @Override
                    public void Fail(int code, String msg) {
                        Toast.makeText(BleTestActivity.this, "BLEStart Fail : "+code, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });


        Button bt_tag = (Button)findViewById(R.id.button5);
        bt_tag.setText("SetTag : "+testTag);
        //BLE setTag
        bt_tag.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                mCurData.setTag = testTag;
                mEB.BLESetTag(mCurData, new EvzProtocol.BLESetTag() {
                    @Override
                    public void Success() {
                        Toast.makeText(BleTestActivity.this, "SET_TAG SUCC", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void Fail(int code, String msg) {
                        Toast.makeText(BleTestActivity.this, "SET_TAG Fail = "+code, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //Stop
        findViewById(R.id.button6).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                mEB.BLEStop(mCurData, new EvzProtocol.BLEStop() {
                    @Override
                    public void Success() {
                        Toast.makeText(BleTestActivity.this, "STOP SUCC", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void Fail(int code, String msg) {
                        Toast.makeText(BleTestActivity.this, "STOP Fail = "+code, Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });


        //getTag
        findViewById(R.id.button7).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                android.util.Log.e("KDH", "onClick BLEGetTag Success");
                mEB.BLEGetTag(mCurData, new EvzProtocol.BLEGetTag() {
                    @Override
                    public void Success(ArrayList<EvzBLETagData> _data)
                    {
                        android.util.Log.e("KDH", "BLEGetTag Success = "+_data.size());
                        if(_data.size() == 0)
                        {
                            Toast.makeText(BleTestActivity.this, "BLEGetTag SUCC SIZE 0", Toast.LENGTH_SHORT).show();
                        }
                        {
                            StringBuffer sb = new StringBuffer();
                            for (int i = 0; i < _data.size(); i++)
                            {
                                EvzBLETagData _tag = _data.get(i);

                                sb.append("num = "+_tag.Number + " /kWh = "+_tag.kwh +" /useTime =  "+_tag.useTime+"\n");
                            }

                            Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void Fail(int code, String msg) {
                        Toast.makeText(BleTestActivity.this, "BLEGetTag Fail = "+code, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //tag del all
        findViewById(R.id.button8).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                mEB.BLEDelAllTag(new EvzProtocol.BLEDelAllTag() {
                    @Override
                    public void Success() {
                        runOnUiThread(() -> Toast.makeText(BleTestActivity.this, "BLEDelAllTag Success", Toast.LENGTH_SHORT).show());

                    }

                    @Override
                    public void Fail(int code, String msg) {
                        runOnUiThread(() -> Toast.makeText(BleTestActivity.this, "BLEDelAllTag Fail = "+code, Toast.LENGTH_SHORT).show());

                    }
                });
            }
        });


        Button btn_del_one = (Button)findViewById(R.id.button9);
        btn_del_one.setText("TagDelOne : "+testTag);
        mCurTag.Number = testTag;

        btn_del_one.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                mEB.BLEDelOneTag(mCurTag, new EvzProtocol.BLEDelOneTag() {
                    @Override
                    public void Success() {
                        runOnUiThread(() -> Toast.makeText(BleTestActivity.this, "btn_del_one Success", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void Fail(int code, String msg) {
                        runOnUiThread(() ->  Toast.makeText(BleTestActivity.this, "BLEDelOneTag Fail = "+code, Toast.LENGTH_SHORT).show());
                    }
                });
            }
        });


        findViewById(R.id.button13).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                mEB.BLEUserDis(new EvzProtocol.BLEUserDis() {
                    @Override
                    public void Success() {
                        Toast.makeText(getApplicationContext(), "DIS", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void Fail(int i, String s) {
                        Toast.makeText(getApplicationContext(), "code = "+i, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        findViewById(R.id.button).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                mEB.BLECalibration(new EvzProtocol.BLECalibration() {
                    @Override
                    public void Success() {

                    }

                    @Override
                    public void Fail(int i, String s) {

                    }
                });
            }
        });

    }


    @Override
    protected void onDestroy() {
        mEB.Destroy();
        super.onDestroy();
    }


}
