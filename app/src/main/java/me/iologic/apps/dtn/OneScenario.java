package me.iologic.apps.dtn;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class OneScenario extends AppCompatActivity {

    static final int REQUEST_ENABLE_BT=1;
    BluetoothAdapter mBluetoothAdapter; // The Only Bluetooth Adapter Used.
    int noOfPeers = 0;
    BluetoothConnectT serverConnect;
    BluetoothConnectClientT clientConnect;
    BluetoothBytesT streamData;
    BluetoothDevice btDeviceConnectedGlobal; // To get Device Name
    BluetoothSocket SocketGlobal; // To store socket
    ArrayList<BluetoothDevice> btDevicesFoundList = new ArrayList<BluetoothDevice>(); // Store list of bluetooth devices.
    String getGoodOldName;

    Handler btClientConnectionStatus;
    Handler btServerConnectionStatus;
    Bundle bundle;


    private static String SERVER_CONNECTION_SUCCESSFUL;
    private static String SERVER_CONNECTION_FAIL;

    private static String CLIENT_CONNECTION_SUCCESSFUL;
    private static String CLIENT_CONNECTION_FAIL;

    private static String NOT_YET_CONNECTED;

    TextView btStatusText;
    TextView peerStatusText;
    TextView messageReceived;
    TextView currentStatusText;
    EditText EditMessageBox;
    Button sendMsgBtn;

    boolean toastShown = false; // Client Re-Connection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_scenario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        btStatusText = (TextView) findViewById(R.id.btStatus);
        peerStatusText = (TextView) findViewById(R.id.peerStatus);
        messageReceived = (TextView) findViewById(R.id.messageStatus);
        EditMessageBox = (EditText) findViewById(R.id.messageBox);
        sendMsgBtn = (Button) findViewById(R.id.sendMsg);
        currentStatusText = (TextView) findViewById(R.id.currentStatus);

        btStatusText.setSelected(true); // For Horizontal Scrolling
        messageReceived.setSelected(true); // For Horizontal Scrolling

        btServerConnectionStatus = new Handler();
        btClientConnectionStatus = new Handler();
        bundle = new Bundle();

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        startBluetooth();
        sendMessage();
    }

    protected void startBluetooth()
    {
        String discMessage = "Discoverability set to ON";
        String btEnabledMessage = "Bluetooth is Enabled";
        Toast btDeviceDiscoverToast = Toast.makeText(getApplicationContext(), discMessage, Toast.LENGTH_SHORT);
        btDeviceDiscoverToast.show();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        getGoodOldName = mBluetoothAdapter.getName();

            if (mBluetoothAdapter == null) {
                btStatusText.setText("Bluetooth Not Found!");
            } else if (!mBluetoothAdapter.isEnabled()) {
               // mBluetoothAdapter.enable();
         //   Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

          //  startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); // Calls onActivityResult */
                setBtName();

                Toast btDeviceEnableToast = Toast.makeText(getApplicationContext(), btEnabledMessage, Toast.LENGTH_SHORT);
                btDeviceEnableToast.show();

                Handler qBhandler = new Handler();
                qBhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        discBluetoothDevices();
                    }
                }, 2000);

            } else if (mBluetoothAdapter.isEnabled()) {
                btStatusText.setText("Bluetooth is already enabled!");
                setBtName();
                Handler qBhandler = new Handler();
                qBhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        discBluetoothDevices();
                    }
                }, 2000);

            }

    }

    private void setBtName(){
        String btDeviceName = "DTN-"+ Build.SERIAL;
        String message = "Bluetooth Device Name: " + btDeviceName;
        mBluetoothAdapter.setName(btDeviceName);
        Toast btDeviceNameToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        btDeviceNameToast.show();

        serverConnection(); // Let's start the Server
    }

    protected void discBluetoothDevices() {
        if(mBluetoothAdapter.isDiscovering())
        {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
        btStatusText.setText("Discovering Bluetooth Devices...");
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btDevicesFoundList.add(device);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                String discBDevice = "Found Device: " + deviceName;
                noOfPeers++;
                Toast toast = Toast.makeText(getApplicationContext(), discBDevice, Toast.LENGTH_SHORT);
                toast.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                btStatusText.setText("Discovery Period Finished");
                connectDevice();
            }
            peerStatusText.setText("No of Peers Found: " + noOfPeers);
        }
    };

    public void connectDevice(){

        String btDeviceName = "DTN-";
        CLIENT_CONNECTION_FAIL = "Client Connection Failed!";

        btClientConnectionStatus = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.arg1 == 1)
                {
                    Toast toast = Toast.makeText(getApplicationContext(), CLIENT_CONNECTION_SUCCESSFUL, Toast.LENGTH_SHORT);
                    toast.show();
                    currentStatusText.setText("CLIENT");
                    SocketGlobal = clientConnect.getClientSocket();
                    streamData = new BluetoothBytesT(SocketGlobal, btMessageStatus);
                    streamData.start();
                } else if(msg.arg1 == -1) {
                    if (toastShown == false) {
                        Toast toast = Toast.makeText(getApplicationContext(), CLIENT_CONNECTION_FAIL, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                        clientConnect.run(); // Keep Trying To Connect If It Fails

                    toastShown = true;
                }
            }
        };

        for(BluetoothDevice btDevice : btDevicesFoundList) {
            if ((btDevice.getName().contains(btDeviceName)) && (btDevice != null)) {
                btDeviceConnectedGlobal = btDevice;
                clientConnect = new BluetoothConnectClientT(btDevice, mBluetoothAdapter, btClientConnectionStatus);
                clientConnect.start();
            }

        }
            if(!(btDeviceConnectedGlobal == null)) {
                CLIENT_CONNECTION_SUCCESSFUL = "Client Connected To:" + btDeviceConnectedGlobal.getName();
            } else {
                Log.e("DTN", "No Device Found With Name DTN");
            }
    }

    private void serverConnection(){

        SERVER_CONNECTION_SUCCESSFUL ="Server is successfully connected!";
        SERVER_CONNECTION_FAIL = "Server failed to connect";

        btServerConnectionStatus = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.arg1 == 1)
                {
                    Toast toast = Toast.makeText(getApplicationContext(), SERVER_CONNECTION_SUCCESSFUL, Toast.LENGTH_SHORT);
                    toast.show();
                    currentStatusText.setText("SERVER");
                    SocketGlobal = serverConnect.getClientSocket();
                    streamData = new BluetoothBytesT(SocketGlobal, btMessageStatus);
                    streamData.start();
                } else if(msg.arg1 == -1){
                    Toast toast = Toast.makeText(getApplicationContext(), SERVER_CONNECTION_FAIL, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        };


        serverConnect = new BluetoothConnectT(mBluetoothAdapter, btServerConnectionStatus);
        serverConnect.start();
    }

    private final Handler btMessageStatus = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == Constants.MessageConstants.MESSAGE_WRITE){
                btStatusText.setText("Message is sent: " + EditMessageBox.getText());
            } else if(msg.what == Constants.MessageConstants.MESSAGE_TOAST) {
                String statusMessage = bundle.getString("status");
                btStatusText.setText(statusMessage);
            } else if(msg.what == Constants.MessageConstants.MESSAGE_READ){
                byte[] writeBuf = (byte[]) msg.obj;
                String writeMessage = new String(writeBuf);
                messageReceived.setText(writeMessage);
            }
        }
    };

    public void sendMessage(){

        NOT_YET_CONNECTED = "I am not yet connected to any phone";

            sendMsgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!(SocketGlobal == null)) {
                        byte[] test = EditMessageBox.getText().toString().getBytes();
                        streamData.write((EditMessageBox.getText().toString()).getBytes());
                    } else {
                    Toast toast = Toast.makeText(getApplicationContext(), NOT_YET_CONNECTED, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

    });
    }


    @Override
    protected void onDestroy() {
        mBluetoothAdapter.setName(getGoodOldName);
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }



}
