package com.ilyap.joystick;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ControlActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        Toast.makeText(getApplicationContext(), "Connected!", Toast.LENGTH_SHORT).show();

        ConnectedThread threadCommand = new ConnectedThread(MainActivity.clientSocket);
        threadCommand.start();

        TextView deviceName = findViewById(R.id.device_name);
        deviceName.setText(MainActivity.clientName);

        ImageButton btnForward = findViewById(R.id.button_forward);
        ImageButton btnBackward = findViewById(R.id.button_backward);
        ImageButton btnLeft = findViewById(R.id.button_left);
        ImageButton btnRight = findViewById(R.id.button_right);
        ImageButton btnStop = findViewById(R.id.button_stop);
        ImageButton btnInfo = findViewById(R.id.button_info);

        btnForward.setOnClickListener(view -> threadCommand.sendCommand("f"));
        btnBackward.setOnClickListener(view -> threadCommand.sendCommand("b"));
        btnLeft.setOnClickListener(view -> threadCommand.sendCommand("l"));
        btnRight.setOnClickListener(view -> threadCommand.sendCommand("r"));
        btnStop.setOnClickListener(view -> threadCommand.sendCommand("s"));

        btnInfo.setOnClickListener(view -> {
                    AlertDialog alert = new AlertDialog.Builder(this)
                            .setTitle("Информация о приложении")
                            .setMessage("\u00a9 2023 Пухов И. Н.")
                            .setIcon(R.drawable.info)
                            .setPositiveButton("ОК", (dialog, which) -> dialog.cancel()).create();

                    alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
                    alert.show();
                }
        );
    }

    private static class ConnectedThread extends Thread {
        private String lastVerticalCommand;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket btSocket) {
            try {
                outputStream = btSocket.getOutputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void sendCommand(String command) {
            String fullCommand = command;

            if (command.equals("f") || command.equals("b") || command.contains("s")) {
                lastVerticalCommand = command;
            } else {
                fullCommand += lastVerticalCommand;
            }

            try {
                outputStream.write(fullCommand.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}