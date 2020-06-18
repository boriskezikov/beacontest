package ru.boriskezikov.beacon.test;

import android.Manifest;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import ru.boriskezikov.beacon.test.provider.IntentProvider;
import ru.boriskezikov.beacon.test.model.*;

import static ru.boriskezikov.beacon.test.model.Device.MI_BAND;

public class MainActivity extends AppCompatActivity {
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    TextView peripheralTextView;
    FloatingActionButton fab;
    Button startManualIntent;
    boolean isScanningStarted = false;

    private final static int REQUEST_ENABLE_BT = 1;
    private final static String START_SEARCH = "List of devices:";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkPersmissions();
        initState();

        peripheralTextView = (TextView) findViewById(R.id.textview_first4);
        startManualIntent = (Button) findViewById(R.id.button_first4);
        startManualIntent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                startActivity(IntentProvider.getRandomIntent());
            }
        });


        fab = findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_search_button));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initScanning();
                if(isScanningStarted){
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_stop_24));
                }else {
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_search_button));
                }
                Snackbar.make(view, !btAdapter.isEnabled() ? "Please turn on bluetooth" :
                        isScanningStarted ? "Start scanning" : "Stop scanning", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });


    }

    void initState() {
        btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();
        checkBTEnabled();
    }

    boolean checkBTEnabled() {
        if (btAdapter.isEnabled()){
            return true;
        } else {
            enableBT();
            return false;
        }
    }

    void enableBT() {
        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    boolean checkPersmissions(){
        while (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CALL_LOG)) {
                // Show an explanation to the user
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_exit) {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }

        return super.onOptionsItemSelected(item);
    }

    public void initScanning() {
        if (checkBTEnabled()) {
            if (!isScanningStarted) {
                System.out.println("Start scanning");
                peripheralTextView.setText("");
                peripheralTextView.append(START_SEARCH);
                isScanningStarted = true;
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        btScanner.startScan(leScanCallback);
                    }
                });
            } else {
                isScanningStarted = false;
                if (peripheralTextView.getText().toString().equals(START_SEARCH)) {
                    peripheralTextView.setText("");
                    peripheralTextView.append("No devices are founded");
                }
                System.out.println("Stopping scanning");
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        btScanner.stopScan(leScanCallback);
                    }
                });
            }
        }
    }

    void switchButtonLogo() {
        if (isScanningStarted) {
            fab.setBackgroundResource(R.drawable.ic_baseline_stop_24);
        } else
            fab.setBackgroundColor(R.drawable.ic_search_button);
    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            peripheralTextView.append("Device Name: " + result.getDevice().getName() + " rssi: " + result.getRssi() + "\n");
            if(result.getDevice().getName() != null) {
                if (result.getDevice().getName().equals("abeacon_924E") && result.getRssi() > -63) {
                    peripheralTextView.append("Wow! Device Name: " + result.getDevice().getName() + " rssi: " + result.getRssi() + "\n");
                    startActivity(IntentProvider.getRandomIntent());
                }
            }

        }
    };

    static void startSpecificIntentActivity(String deviceName) {
        final Device pidor = Device.valueOf(deviceName);
        switch (pidor){
            case MI_BAND:
                IntentProvider.getSpecificIntent(Intents.YOUTUBE);
                break;
            case BEACON:
                IntentProvider.getSpecificIntent(Intents.MAP);
                break;
        }
    }
}

