package com.bluenet.root.mygreen;

import android.bluetooth.*;
import android.content.Intent;
import android.app.*;
import android.content.*;
import android.util.Log;
import java.io.*;
import java.util.*;
import android.net.wifi.*;

public class mainBluetooth extends Activity {

    //private final static int REQUEST_ENABLE_BT = 1;
    protected final static String appUUID = "590e797d-0d32-4391-a705-";
    protected String deviceMACAddress;
    protected UUID deviceUUID;

    protected BluetoothAdapter mBluetoothAdapter;

    protected Set<BluetoothDevice>
            PairedBTs = new HashSet<BluetoothDevice>(),
            NeighboredBTs = new HashSet<BluetoothDevice>(),
            NewNeighboredBTs = new HashSet<BluetoothDevice>();


    private final BroadcastReceiver BTReceiver= new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("Android : ", "Device found ->" + device.getName() + ", " + device.getAddress());
                NewNeighboredBTs.add(device);
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d("Android : ", "Discovery done");
                unregisterReceiver(BTReceiver);
                NeighboredBTs.clear();
                NeighboredBTs.addAll(NewNeighboredBTs);
            }
        }
    };

    protected void initBTModules(){
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        deviceMACAddress = manager.getConnectionInfo().getMacAddress();
        Log.d("Android : ", "MAC -> " + deviceMACAddress);
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < deviceMACAddress.length(); i++)
            if (Character.isLetterOrDigit(deviceMACAddress.charAt(i)))
                temp.append(deviceMACAddress.charAt(i));
        deviceUUID = UUID.fromString(appUUID + temp.toString());
    }
    protected void turnOnBluetooth() {
        if (mBluetoothAdapter == null) mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null)
            if (!mBluetoothAdapter.isEnabled()) {
                //int result = 0;
                //Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivityForResult(turnOn, REQUEST_ENABLE_BT);
                mBluetoothAdapter.enable();
                //onActivityResult(REQUEST_ENABLE_BT, result, turnOn);
                //if (result == RESULT_CANCELED) return false;
            }
        Log.d("Android : ", "Bluetooth on");
    }
    protected void turnOffBluetooth() {
        if (mBluetoothAdapter == null) mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null)
        if (mBluetoothAdapter.isEnabled()) mBluetoothAdapter.disable();
    }
    protected void getBTPairs() {
        if (mBluetoothAdapter == null) mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) PairedBTs = mBluetoothAdapter.getBondedDevices();
    }
    protected String getBTMessage() {
        BluetoothServerSocket mainServerSocket = null;
        String currentName = "HelloWorld";
        UUID currentUUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
        BluetoothSocket socket = null;
        InputStream mainInStream = null;
        OutputStream mainOutStream = null;
        byte[] receivedMsg = new byte[1024];

        try {
            mainServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(currentName, currentUUID);
            Log.d("Android : ", "Server main socket added");
        } catch (IOException e) {}

        try {
            socket = mainServerSocket.accept();
            Log.d("Android : ", "Server main socket accepted");
            try {
                mainInStream = socket.getInputStream();
                mainOutStream = socket.getOutputStream();
            } catch (IOException e) {}

        } catch (IOException e) {}

        if (socket != null) {
                // Do work to manage the connection
                try {
                    mainInStream.read(receivedMsg);
                    Log.d("Android : ", "Message received");
                } catch (IOException e) {}
        }
        try {
            socket.close();
        } catch (IOException e) { }
        Log.d("Android : ", "Close socket");

        try {
            mainServerSocket.close();
        } catch (IOException e) { }
        Log.d("Android : ", "Close server socket");
        String finalMsg;
        try {
            finalMsg = new String(receivedMsg, "UTF8");
        } catch (UnsupportedEncodingException e) {
            finalMsg = "Nothing";
        }
        return finalMsg;
    }

    protected void sendBTMessage(String message, BluetoothDevice device) {

        BluetoothSocket mmSocket = null;
        BluetoothDevice mmDevice = device;
        UUID currentUUID = deviceUUID;
        OutputStream mainOutStream = null;

        try {
            mmSocket = device.createRfcommSocketToServiceRecord(currentUUID);
        } catch (IOException e) {}


        Log.d("Android : ", "(1) Send: Channel created");


        try {
            mmSocket.connect();
        } catch (IOException connectException) {
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }
        Log.d("Android : ", "(2) Send: Socket connected");
        try {
            mainOutStream = mmSocket.getOutputStream();
        } catch (IOException e) {}

        try {
            mainOutStream.write(message.getBytes());
        } catch (IOException e) {}

        Log.d("Android : ", "(3) Send: Msg sent");
        try {
            mmSocket.close();
        } catch (IOException e) { }
        Log.d("Android : ", "(4) Send: Socket closed");
    }

    protected void getBTNeighbors() {
        if (mBluetoothAdapter == null) mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null)
            if (mBluetoothAdapter.isEnabled() && (!mBluetoothAdapter.isDiscovering())) {
                Log.d("Android : ", "Start geting neighbors");
                NewNeighboredBTs.clear();
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                registerReceiver(BTReceiver, filter);
                Log.d("Android : ", "Discovery started");
                mBluetoothAdapter.startDiscovery();
            }
    }
}