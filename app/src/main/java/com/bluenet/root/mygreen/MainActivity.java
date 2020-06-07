package com.bluenet.root.mygreen;

import android.app.Activity;
import android.bluetooth.*;
import android.content.Intent;
import android.widget.Button;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.util.*;

public class MainActivity extends mainBluetooth {

    Button b1, b2, b3, b4, b5, b6;
    TextView t1, t2, t3;
    BluetoothDevice tempDevice;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the activity is about to become visible. */
    @Override
    protected void onStart() {
        super.onStart();
        /**Class resources*/

        initBTModules();

        b1 = (Button)findViewById(R.id.b1);
        b2 = (Button)findViewById(R.id.b2);
        b3 = (Button)findViewById(R.id.b3);
        b4 = (Button)findViewById(R.id.b4);
        b5 = (Button)findViewById(R.id.b5);
        b6 = (Button)findViewById(R.id.b6);

        t1 = (TextView)findViewById(R.id.t1);
        t2 = (TextView)findViewById(R.id.t2);
        t3 = (TextView)findViewById(R.id.t3);

        b1.setOnClickListener(b1Click);
        b2.setOnClickListener(b2Click);
        b3.setOnClickListener(b3Click);
        b4.setOnClickListener(b4Click);
        b5.setOnClickListener(b5Click);
        b6.setOnClickListener(b6Click);

    }

    /** Called when the activity has become visible. */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /** Called when another activity is taking focus. */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /** Called when the activity is no longer visible. */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /** Action listeners */
    private View.OnClickListener b1Click = new View.OnClickListener() {
        public void onClick(View v) {
            turnOnBluetooth();
            t1.setText("Bluetooth On");
        }
    };
    private View.OnClickListener b2Click = new View.OnClickListener() {
        public void onClick(View v) {
            turnOffBluetooth();
            t1.setText("Bluetooth Off");
        }
    };
    private View.OnClickListener b3Click = new View.OnClickListener() {
        public void onClick(View v) {
            t2.setText("Neighbours: ");
            for (BluetoothDevice device : NeighboredBTs) {
                t2.append(device.getAddress() + ", " + device.getName() + " <->  ");
                tempDevice = device;
            }
            getBTNeighbors();
        }
    };
    private View.OnClickListener b4Click = new View.OnClickListener() {
        public void onClick(View v) {
            getBTPairs();
            t3.setText("Pairs: ");
            for (BluetoothDevice device : PairedBTs) {
                t3.append(device.getAddress() + ", " + device.getName() + " <->  ");
            }
        }
    };
    private View.OnClickListener b5Click = new View.OnClickListener() {
        public void onClick(View v) {
            t3.setText("Msg received -> " + getBTMessage());
        }
    };

    private View.OnClickListener b6Click = new View.OnClickListener() {
        public void onClick(View v) {
            sendBTMessage("Hello!", tempDevice);
            t3.setText("Sent msg -> Hello!");
        }
    };
}