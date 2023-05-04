package com.ilyap.joystick;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private ListView listView;
    private ArrayList<String> pairedDeviceArrayList;
    public static BluetoothSocket clientSocket;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ActivityResultLauncher<Intent> btActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    findArduino();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonStartFind = findViewById(R.id.button_start_find);
        listView = findViewById(R.id.list_device);
        buttonStartFind.setOnClickListener(v -> {
            if (permissionGranted()) {
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                checkBluetoothEnabled();
            }
        });
    }

    private boolean permissionGranted() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
            }
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    private void checkBluetoothEnabled() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            btActivityResultLauncher.launch(enableIntent);
        } else {
            findArduino();
        }
    }

    @SuppressLint("MissingPermission")
    private void findArduino() {
        Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
        if (pairedDevice.size() > 0) {
            pairedDeviceArrayList = new ArrayList<>();
            for (BluetoothDevice device : pairedDevice) {
                pairedDeviceArrayList.add(device.getAddress() + "/" + device.getName());
            }
        }

        ArrayAdapter<String> pairedDeviceAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.item_device, R.id.item_device_textView, pairedDeviceArrayList);
        listView.setAdapter(pairedDeviceAdapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            final String itemMAC = listView.getItemAtPosition(i).toString().split("/", 2)[0];

            executorService.submit(() -> {
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(itemMAC);
                try {
                    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
                    socket.connect();
                    if (socket.isConnected()) {
                        clientSocket = socket;
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), ActivityControl.class);
                        startActivity(intent);
                    }
                } catch (IOException ignored) {
                    findArduino();
                }
            });
        });
    }
}