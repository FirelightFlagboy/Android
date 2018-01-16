package com.lord_arduino.fr.elite_golf;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.integreight.onesheeld.sdk.OneSheeldBaudRateQueryCallback;
import com.integreight.onesheeld.sdk.OneSheeldConnectionCallback;
import com.integreight.onesheeld.sdk.OneSheeldDataCallback;
import com.integreight.onesheeld.sdk.OneSheeldDevice;
import com.integreight.onesheeld.sdk.OneSheeldError;
import com.integreight.onesheeld.sdk.OneSheeldErrorCallback;
import com.integreight.onesheeld.sdk.OneSheeldFirmwareUpdateCallback;
import com.integreight.onesheeld.sdk.OneSheeldManager;
import com.integreight.onesheeld.sdk.OneSheeldScanningCallback;
import com.integreight.onesheeld.sdk.OneSheeldSdk;
import com.integreight.onesheeld.sdk.OneSheeldTestingCallback;
import com.integreight.onesheeld.sdk.SupportedBaudRate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.controlwear.virtual.joystick.android.JoystickView;

import static com.lord_arduino.fr.elite_golf.Menu_principal.FAVORITE_COLOR1;
import static com.lord_arduino.fr.elite_golf.Menu_principal.FAVORITE_COLOR2;

public class bluetooth extends Activity{
    public static final int PERMISSION_REQUEST_CODE = 1;
    private ProgressDialog scanningProgressDialog, connectionProgressDialog, firmwareUpdateProgressDialog;
    private ArrayList<String> connectedDevicesNames,scannedDevicesNames;
    private ArrayList<OneSheeldDevice> oneSheeldScannedDevices, oneSheeldConnectedDevices;
    private ArrayAdapter<String> connectedDevicesArrayAdapter, scannedDevicesArrayAdapter;
    private EditText bluetoothTestingSendingEditText, bluetoothTestingFramesNumberEditText, bluetoothTestingReceivingEditText;
    private TextView oneSheeldNameTextView, bluetoothSentFramesCounterTextView, bluetoothTestingReceivingFramesCounterTextView, Text1, Text2;
    private OneSheeldDevice selectedConnectedDevice = null, selectedScannedDevice = null;
    private ListView listV, connectedDevicesListView, scannedDevicesListView;
    private Spinner baudRateSpinner, firmwareSpinner;
    private BluetoothTestingSendingThread bluetoothTestingSendingThread;
    private String[] title = new String[]{"Rename","Test Board","Query Baud Rate","Set Baud Rate"};
    private OneSheeldManager oneSheeldManager;
    private HashMap<String, String> pendingRename;
    private char[] nameChars = new char[]{};
    private Random random = new Random();
    private boolean isBaudRateQueried = false, balance = false;
    private Dialog bluetoothTestingDialog;
    private ScrollView controller;
    private Button menu, bluetoothTestingResetButton, connectButton, disconnectButton, setting, bluetoothTestingStartButton;
    private JoystickView joystick;
    private View v;
    public Handler uiThreadHandler = new Handler();
    private StringBuilder receivedStringBuilder = new StringBuilder();
    private int cos, sin, output = 0;

    private OneSheeldScanningCallback scanningCallBack = new OneSheeldScanningCallback(){
        @Override
        public void onDeviceFind(final OneSheeldDevice device){
            uiThreadHandler.post(new Runnable(){
                @Override
                public void run(){
                    oneSheeldScannedDevices.add(device);
                    scannedDevicesNames.add(device.getName());
                    scannedDevicesArrayAdapter.notifyDataSetChanged();
                }
            });
        }
        @Override
        public void onScanFinish(List<OneSheeldDevice> foundDevices){
            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    scanningProgressDialog.dismiss();
                }
            });
        }
    };
    private OneSheeldTestingCallback testingCallback = new OneSheeldTestingCallback() {
        @Override
        public void onFirmwareTestResult(final OneSheeldDevice device, final boolean isPassed) {
            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(bluetooth.this, device.getName() + ": Firware test result:" + (isPassed ? "Correct" : "Failed"), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onLibraryTestResult(final OneSheeldDevice device, final boolean isPassed) {
            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(bluetooth.this, device.getName() + ": Library test result: " + (isPassed ? "Correct" : "Failed"), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onFirmwareTestTimeOut(final OneSheeldDevice device) {
            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(bluetooth.this, device.getName() + ":ERROR, firmware test timeout", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onLibraryTestTimeOut(final OneSheeldDevice device) {
            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(bluetooth.this, device.getName() + ":ERROR, library test timeout",Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
    private OneSheeldDataCallback dataCallback = new OneSheeldDataCallback() {
        @Override
        public void onSerialDataReceive(OneSheeldDevice device, int data) {
            receivedStringBuilder.append((char) data);
            if (receivedStringBuilder.length() >= bluetoothTestingReceivingEditText.getText().toString().length()){
                String compareString = receivedStringBuilder.substring(0, bluetoothTestingReceivingEditText.getText().toString().length());
                if (compareString.equals(bluetoothTestingReceivingEditText.getText().toString())) {
                    receivedStringBuilder.delete(0, bluetoothTestingReceivingEditText.getText().toString().length());
                    uiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            bluetoothTestingReceivingFramesCounterTextView.setText(String.valueOf((Integer.valueOf(bluetoothSentFramesCounterTextView.getText().toString() + 1))));
                                                    }
                    });
                    if (receivedStringBuilder.length()>0)receivedStringBuilder.deleteCharAt(0);
                }
            }
        }
    };
    private OneSheeldBaudRateQueryCallback baudRateQueryCallback = new OneSheeldBaudRateQueryCallback() {
        @Override
        public void onBaudRateQueryResponse(final OneSheeldDevice device, final SupportedBaudRate supportedBaudRate) {
            if (isBaudRateQueried){
                uiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(bluetooth.this, device.getName() + (supportedBaudRate != null ? ": Current baud rate: " + supportedBaudRate.getBaudRate() : ": Device responded with an unsupported baud rate"), Toast.LENGTH_SHORT).show();
                    }
                });
                isBaudRateQueried = false;
            }
        }
    };
    private OneSheeldFirmwareUpdateCallback firmwareUpdateCallback = new OneSheeldFirmwareUpdateCallback() {
        @Override
        public void onStart(OneSheeldDevice device) {
            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    firmwareUpdateProgressDialog.show();
                    firmwareUpdateProgressDialog.setMessage("Starting..");

                }
            });
        }
    };
    private OneSheeldConnectionCallback connectionCallback = new OneSheeldConnectionCallback() {
        @Override
        public void onConnect(final OneSheeldDevice device) {
            oneSheeldScannedDevices.remove(device);
            oneSheeldConnectedDevices.add(device);
            final String deviceName = device.getName();
            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(scannedDevicesNames.indexOf(deviceName) >= 0){
                        scannedDevicesNames.remove(deviceName);
                        connectedDevicesNames.add(deviceName);
                        connectedDevicesArrayAdapter.notifyDataSetChanged();
                        scannedDevicesArrayAdapter.notifyDataSetChanged();
                    }
                    connectButton.setEnabled(false);
                    disconnectButton.setEnabled(false);
                    controller.setVisibility(View.INVISIBLE);
                }
            });
            connectionProgressDialog.dismiss();
            device.addTestingCallback(testingCallback);
            device.addDataCallback(dataCallback);
            device.addBaudRateQueryCallback(baudRateQueryCallback);
            device.addFirmwareUpdateCallback(firmwareUpdateCallback);
        }

        @Override
        public void onDisconnect(final OneSheeldDevice device) {
            final String deviceName = device.getName();
            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (connectedDevicesNames.indexOf(deviceName) >=0){
                        connectedDevicesNames.remove(deviceName);
                        connectedDevicesArrayAdapter.notifyDataSetChanged();
                    }
                    connectButton.setEnabled(false);
                    disconnectButton.setEnabled(false);
                    controller.setVisibility(View.INVISIBLE);
                    connectionProgressDialog.dismiss();
                    oneSheeldConnectedDevices.remove(device);
                    if (!scannedDevicesNames.contains(device.getName()) && !oneSheeldScannedDevices.contains(device)){
                        oneSheeldScannedDevices.add(device);
                        scannedDevicesNames.add(device.getName());
                    }
                    scannedDevicesArrayAdapter.notifyDataSetChanged();
                    bluetoothTestingDialog.dismiss();
                }
            });
        }
    };

    private OneSheeldErrorCallback errorCallback = new OneSheeldErrorCallback() {
        @Override
        public void onError(final OneSheeldDevice device, final OneSheeldError error) {
            if (connectionProgressDialog !=null)
                connectionProgressDialog.dismiss();
            if (scanningProgressDialog !=null)
                scanningProgressDialog.dismiss();
            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(bluetooth.this, "Error: " + error.toString() + (device != null ? " in " + device.getName() : ""),Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private AdapterView.OnItemClickListener scannedDevicesListViewClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            selectedScannedDevice = oneSheeldScannedDevices.get(i);
            connectButton.setEnabled(true);
        }
    };
    private AdapterView.OnItemClickListener connectedDevicesListViewClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            selectedConnectedDevice = oneSheeldConnectedDevices.get(i);
            oneSheeldNameTextView.setText(selectedConnectedDevice.getName());
            controller.setVisibility(View.VISIBLE);
            disconnectButton.setEnabled(true);
        }
    };

    class BluetoothTestingSendingThread extends Thread{

        AtomicBoolean stopRequested;
        OneSheeldDevice device;
        String string;
        int count;

        BluetoothTestingSendingThread(OneSheeldDevice device, String string, int count) {
            stopRequested = new AtomicBoolean(false);
            this.device = device;
            this.string = string;
            this.count = count;
            start();
        }

        private void stopRunning() {
            if (this.isAlive())
                this.interrupt();
            stopRequested.set(true);
        }

        @Override
        public void run() {
            for (int i = 1; i <= count && !this.isInterrupted() && !stopRequested.get(); i++) {
                device.sendSerialData(string.getBytes(Charset.forName("US-ASCII")));
                final int counter = i;
                uiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!stopRequested.get())
                            bluetoothSentFramesCounterTextView.setText(String.valueOf(counter));
                    }
                });
            }
            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (!stopRequested.get()) {
                        bluetoothTestingSendingEditText.setEnabled(true);
                        bluetoothTestingFramesNumberEditText.setEnabled(true);
                        bluetoothTestingStartButton.setEnabled(true);
                    }
                }
            });
        }
    }

    private boolean checkAndAskForLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)){

                AlertDialog.Builder locationPermissionExplanationDialog = new AlertDialog.Builder(this);

                locationPermissionExplanationDialog.setMessage("Bluetooth scan needs location permission.").setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(bluetooth.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                bluetooth.PERMISSION_REQUEST_CODE);
                    }
                }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(bluetooth.this, "We can't start scanning until you grant the location permission", Toast.LENGTH_SHORT).show();
                    }
                }).create();
                locationPermissionExplanationDialog.show();
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},bluetooth.PERMISSION_REQUEST_CODE);
            }
            return false;
        }else {
            return true;
        }
    }

    public void onClickScan(View v){
        if(checkAndAskForLocationPermission()){
            scanningProgressDialog.show();
            oneSheeldManager.setScanningTimeOut(20);
            oneSheeldManager.cancelScanning();
            scannedDevicesNames.clear();
            scannedDevicesArrayAdapter.notifyDataSetChanged();
            oneSheeldScannedDevices.clear();
            oneSheeldManager.scan();
        }
    }
    public void onClickConnect(View v){
        if (selectedScannedDevice != null){
            oneSheeldManager.cancelScanning();
            connectionProgressDialog.setMessage("Please wait while connecting to " + selectedScannedDevice.getName());
            connectionProgressDialog.show();
            selectedScannedDevice.connect();
        }
    }
    public void onClickDisconnect(View v){
        if (selectedConnectedDevice !=null){
            selectedConnectedDevice.disconnect();
            selectedConnectedDevice = null;
            controller.setVisibility(View.INVISIBLE);
        }
    }
    public void onClickFirmwareUpdate(View v){
        if (selectedConnectedDevice != null){
            InputStream is;
            try {
                is = getAssets().open("firmwares/" + firmwareSpinner.getSelectedItem().toString());
                byte[] fileBytes = new byte[is.available()];
                is.read(fileBytes);
                is.close();
                selectedConnectedDevice.update(fileBytes);
            }catch (IOException ignored){

            }
        }
    }
    public void onClickShowSettings(int v){
        Text1.setVisibility(v);
        baudRateSpinner.setVisibility(v);
        Text2.setVisibility(v);
        firmwareSpinner.setVisibility(v);
        listV.setVisibility(v);

    }

    public void onClickRename(){
        if (selectedConnectedDevice != null){
            pendingRename.put(selectedConnectedDevice.getAddress(), selectedConnectedDevice.getName());
            selectedConnectedDevice.rename("1Sheeld #" + (selectedConnectedDevice.isTypePlus() ? getRandomChars(2) : getRandomChars(4)));
        }
    }
    public void onClickTestBoard(){
        if (selectedConnectedDevice != null){
            selectedConnectedDevice.test();
        }
    }
    public void onClickQueryBaudRate(){
        if (selectedConnectedDevice != null){
            isBaudRateQueried = true;
            selectedConnectedDevice.queryBaudRate();
        }
    }
    public void onClickSetBaudRate(){
        if (selectedConnectedDevice != null && baudRateSpinner != null) {
            if (baudRateSpinner.getSelectedItem().toString().equals("9600")) {
                selectedConnectedDevice.setBaudRate(SupportedBaudRate._9600);
            } else if (baudRateSpinner.getSelectedItem().toString().equals("14400")) {
                selectedConnectedDevice.setBaudRate(SupportedBaudRate._14400);
            } else if (baudRateSpinner.getSelectedItem().toString().equals("19200")) {
                selectedConnectedDevice.setBaudRate(SupportedBaudRate._19200);
            } else if (baudRateSpinner.getSelectedItem().toString().equals("28800")) {
                selectedConnectedDevice.setBaudRate(SupportedBaudRate._28800);
            } else if (baudRateSpinner.getSelectedItem().toString().equals("38400")) {
                selectedConnectedDevice.setBaudRate(SupportedBaudRate._38400);
            } else if (baudRateSpinner.getSelectedItem().toString().equals("57600")) {
                selectedConnectedDevice.setBaudRate(SupportedBaudRate._57600);
            } else if (baudRateSpinner.getSelectedItem().toString().equals("115200")) {
                selectedConnectedDevice.setBaudRate(SupportedBaudRate._115200);
            }
        }
    }
    public void onClickBluetoothTestingDialog() {
        resetBluetoothTesting();
        bluetoothSentFramesCounterTextView.setText("0");
        bluetoothTestingReceivingFramesCounterTextView.setText("0");
        bluetoothTestingSendingEditText.setText("a0b1c2d3e4f5g6h7i8j9");
        bluetoothTestingFramesNumberEditText.setText("10000");
        bluetoothTestingReceivingEditText.setText("a0b1c2d3e4f5g6h7i8j9");
        bluetoothTestingStartButton.setEnabled(true);
        bluetoothTestingSendingEditText.setEnabled(true);
        bluetoothTestingFramesNumberEditText.setEnabled(true);
        bluetoothTestingDialog.show();
    }
    private void resetBluetoothTesting() {
        if (bluetoothTestingSendingThread != null)
            bluetoothTestingSendingThread.stopRunning();
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                bluetoothSentFramesCounterTextView.setText("0");
                bluetoothTestingReceivingFramesCounterTextView.setText("0");
                bluetoothTestingSendingEditText.setEnabled(true);
                bluetoothTestingFramesNumberEditText.setEnabled(true);
                bluetoothTestingStartButton.setEnabled(true);
            }
        });
        receivedStringBuilder = new StringBuilder();
    }
    private void startBluetoothTesting() {
        if (selectedConnectedDevice != null) {
            if (bluetoothTestingSendingThread != null)
                bluetoothTestingSendingThread.stopRunning();
            bluetoothTestingSendingThread = new BluetoothTestingSendingThread(selectedConnectedDevice, bluetoothTestingSendingEditText.getText().toString(), Integer.valueOf(bluetoothTestingFramesNumberEditText.getText().toString()));
        }
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                bluetoothTestingStartButton.setEnabled(false);
                bluetoothTestingSendingEditText.setEnabled(false);
                bluetoothTestingFramesNumberEditText.setEnabled(false);

            }
        });
    }
    private String getRandomChars(int a){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < a; i++){
            builder.append(nameChars[random.nextInt(nameChars.length)]);
        }
        return builder.toString();
    }
    void initBluetoothTestingDialog() {
        bluetoothTestingDialog = new Dialog(this);
        bluetoothTestingDialog.setContentView(R.layout.testing_dialog);
        bluetoothTestingDialog.setCanceledOnTouchOutside(false);
        bluetoothTestingSendingEditText = (EditText) bluetoothTestingDialog.findViewById(R.id.bluetoothTestingSendingEditText);
        bluetoothTestingFramesNumberEditText = (EditText) bluetoothTestingDialog.findViewById(R.id.bluetoothTestingFramesNumberEditText);
        bluetoothTestingReceivingEditText = (EditText) bluetoothTestingDialog.findViewById(R.id.bluetoothTestingReceivingEditText);
        bluetoothSentFramesCounterTextView = (TextView) bluetoothTestingDialog.findViewById(R.id.bluetoothSentFramesCounterTextView);
        bluetoothTestingReceivingFramesCounterTextView = (TextView) bluetoothTestingDialog.findViewById(R.id.bluetoothTestingReceivingFramesCounterTextView);
        bluetoothTestingStartButton = (Button) bluetoothTestingDialog.findViewById(R.id.bluetoothTestingStartButton);
        bluetoothTestingResetButton = (Button) bluetoothTestingDialog.findViewById(R.id.bluetoothTestingResetButton);
        bluetoothTestingStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBluetoothTesting();
            }
        });
        bluetoothTestingResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBluetoothTesting();
            }
        });
        resetBluetoothTesting();
        bluetoothTestingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                resetBluetoothTesting();
            }
        });
        bluetoothTestingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                resetBluetoothTesting();
            }
        });
    }
    private void initRandomChars(){
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ++ch){
            tmp.append(ch);
        }
        for (char ch = 'A'; ch <= 'Z'; ++ch){
            tmp.append(ch);
        }
        nameChars = tmp.toString().toCharArray();
    }
    private void initOneSheeldSdk(){
        OneSheeldSdk.setDebugging(true);
        OneSheeldSdk.init(this);
        oneSheeldManager = OneSheeldSdk.getManager();
        oneSheeldManager.setConnectionRetryCount(1);
        oneSheeldManager.setAutomaticConnectingRetriesForClassicConnections(true);
        oneSheeldManager.addScanningCallback(scanningCallBack);
        oneSheeldManager.addConnectionCallback(connectionCallback);
        oneSheeldManager.addErrorCallback(errorCallback);
    }
    private void initScanningProgressDialog(){
        scanningProgressDialog = new ProgressDialog(this);
        scanningProgressDialog.setMessage("Please wait..");
        scanningProgressDialog.setTitle("Scannig");
        scanningProgressDialog.setCancelable(true);
        scanningProgressDialog.setCanceledOnTouchOutside(true);
        scanningProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                oneSheeldManager.cancelScanning();
            }
        });
    }
    private void initConnectionProgressDialog() {
        connectionProgressDialog = new ProgressDialog(this);
        connectionProgressDialog.setMessage("Please wait while connecting..");
        connectionProgressDialog.setTitle("Connecting");
        connectionProgressDialog.setCancelable(false);
        connectionProgressDialog.setCanceledOnTouchOutside(false);
    }
    private void initFirmwareUpdateProgressDialog() {
        firmwareUpdateProgressDialog = new ProgressDialog(this);
        firmwareUpdateProgressDialog.setTitle("Updating Firmware..");
        firmwareUpdateProgressDialog.setCancelable(false);
        firmwareUpdateProgressDialog.setCanceledOnTouchOutside(false);
    }
    private int calculLonguer(int angle, int lenght, boolean cos_sin){
        double rad = Math.toRadians(angle);
        if (cos_sin) rad = lenght*Math.cos(rad);
        else rad = lenght*Math.sin(rad);
        lenght = (int)(rad+0.5d);
        return lenght;
    }
    private int map(int in, int in_min, int in_max, int out_min, int out_max){
        in = (in - in_min)*(out_max - out_min)/(in_max - in_min) + out_min;
        return in;
    }
    private void LadderSin(int t){
        if(sin == output) return;
        else {
            if (sin >= output) output = output + t;
            else if (output >= -100) output = output - t;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth);

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        v = (View)findViewById(R.id.View);
        v.setBackgroundColor(Color.parseColor(pref.getString(FAVORITE_COLOR1, "#3F51B5")));
        Window w = getWindow();
        w.setStatusBarColor(Color.parseColor(pref.getString(FAVORITE_COLOR2, "#303F9F")));

        Text1 = (TextView)findViewById(R.id.Text1);
        Text2 = (TextView)findViewById(R.id.Text2);

        oneSheeldConnectedDevices = new ArrayList<>();
        oneSheeldScannedDevices = new ArrayList<>();

        pendingRename = new HashMap<>();

        connectedDevicesNames = new ArrayList<>();
        connectedDevicesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, connectedDevicesNames);
        connectedDevicesListView = (ListView)findViewById(R.id.Connected_List);
        connectedDevicesListView.setAdapter(connectedDevicesArrayAdapter);
        connectedDevicesListView.setOnItemClickListener(connectedDevicesListViewClickListener);

        scannedDevicesNames = new ArrayList<>();
        scannedDevicesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scannedDevicesNames);
        scannedDevicesListView = (ListView)findViewById(R.id.Scanned_List);
        scannedDevicesListView.setAdapter(scannedDevicesArrayAdapter);
        scannedDevicesListView.setOnItemClickListener(scannedDevicesListViewClickListener);

        connectButton = (Button)findViewById(R.id.Connect);
        connectButton.setEnabled(false);

        disconnectButton = (Button)findViewById(R.id.Disconnect);
        disconnectButton.setEnabled(false);

        controller = (ScrollView) findViewById(R.id.Controller);
        controller.setVisibility(View.GONE);

        oneSheeldNameTextView = (TextView)findViewById(R.id.selected_1sheeld_name);

        baudRateSpinner = (Spinner)findViewById(R.id.Baud_Rate);
        ArrayList<String> baudRates = new ArrayList<>();
        for (SupportedBaudRate baudRate : SupportedBaudRate.values())
            baudRates.add(String.valueOf(baudRate.getBaudRate()));
        ArrayAdapter<String>baudRateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, baudRates);
        baudRateSpinner.setAdapter(baudRateAdapter);

        firmwareSpinner = (Spinner)findViewById(R.id.firmwareSpinner);
        ArrayList<String> firmwares = new ArrayList<>();
        try {
            Collections.addAll(firmwares, getAssets().list("firmwares"));
        }catch (IOException ignored){
        }
        ArrayAdapter<String> firmwaresAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, firmwares);
        firmwareSpinner.setAdapter(firmwaresAdapter);

        initScanningProgressDialog();
        initConnectionProgressDialog();
        initFirmwareUpdateProgressDialog();
        initRandomChars();
        initBluetoothTestingDialog();
        initOneSheeldSdk();

        menu = (Button)findViewById(R.id.Menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(bluetooth.this, Menu_principal.class);
                startActivity(intent);
            }
        });

        listV = (ListView)findViewById(R.id.ItemList);
        listV.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, title));
        setting = (Button)findViewById(R.id.Settings);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (balance){
                    onClickShowSettings(View.GONE);
                }else {
                    onClickShowSettings(View.VISIBLE);
                }
                balance = !balance;
            }
        });

        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0: onClickRename();
                        break;
                    case 1: onClickTestBoard();
                        break;
                    case 2: onClickQueryBaudRate();
                        break;
                    case 3: onClickSetBaudRate();
                        break;
                    case 4: onClickBluetoothTestingDialog();
                        break;
                }
            }
        });

        joystick = (JoystickView)findViewById(R.id.joystick);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                cos = calculLonguer(angle, strength, true);
                sin = calculLonguer(angle, strength, false);

                LadderSin(3);

                sin = map(output,-100,100,0,255);
                cos = map(cos,-100,100,0,255);
                if(selectedConnectedDevice != null) {
                    selectedConnectedDevice.analogWrite(5,cos);
                    selectedConnectedDevice.analogWrite(6,sin);
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        oneSheeldManager.cancelScanning();
        oneSheeldManager.disconnectAll();
        bluetoothTestingDialog.dismiss();
        super.onDestroy();
    }

}