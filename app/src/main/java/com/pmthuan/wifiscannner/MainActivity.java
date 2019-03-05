package com.pmthuan.wifiscannner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends WearableActivity {

    private WifiManager mwifimanager;
    private ListView mlistview;
    private Button mScanbutton, mquitbutton;
    private List<ScanResult> mresults;
    private ArrayList<String> marrayList = new ArrayList<>();
    private ArrayAdapter madapter;
    private PowerManager mPowermanager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Todo: Keep wake log running all the time while app is running
        mPowermanager = (PowerManager) getSystemService(POWER_SERVICE);
        final PowerManager.WakeLock wakeLock = mPowermanager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "WifiScan::Wake_logs_tag");
        wakeLock.acquire();

        mScanbutton = findViewById(R.id.scanBtn);
        mScanbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanWifi();
            }
        });

        mlistview = findViewById(R.id.listWifi);
        mwifimanager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (mwifimanager.isWifiEnabled()) {
            Toast.makeText(this, "Wifi is disabled, please enable it", Toast.LENGTH_LONG).show();
            mwifimanager.setWifiEnabled(true);
        }

        madapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, marrayList);
        mlistview.setAdapter(madapter);
        scanWifi();

        mquitbutton = findViewById(R.id.quitBtn);
        mquitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
                wakeLock.release();
            }
        });

    }

    private void scanWifi() {
        marrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(mwifimanager.SCAN_RESULTS_AVAILABLE_ACTION));
        mwifimanager.startScan();
        Toast.makeText(this, "Scanning Wifi..", Toast.LENGTH_SHORT).show();

    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mresults = mwifimanager.getScanResults();
            unregisterReceiver(this);

            for (ScanResult scanResult : mresults) {
                marrayList.add(scanResult.SSID + " - " + scanResult.capabilities);
                madapter.notifyDataSetChanged();
            }
        }
    };
}
