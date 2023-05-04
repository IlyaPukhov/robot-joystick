package com.ilyap.joystick;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ActivityControl extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        ConnectedThread threadCommand = new ConnectedThread(MainActivity.clientSocket);
        threadCommand.start();


        ImageButton btnForward = findViewById(R.id.button_forward);
        ImageButton btnBackward = findViewById(R.id.button_backward);
        ImageButton btnLeft = findViewById(R.id.button_left);
        ImageButton btnRight = findViewById(R.id.button_right);
        ImageButton btnStop = findViewById(R.id.button_stop);


        btnForward.setOnClickListener(view -> threadCommand.sendCommand("f"));
        btnBackward.setOnClickListener(view -> threadCommand.sendCommand("b"));
        btnLeft.setOnClickListener(view -> threadCommand.sendCommand("l"));
        btnRight.setOnClickListener(view -> threadCommand.sendCommand("r"));
        btnStop.setOnClickListener(view -> threadCommand.sendCommand("s"));
    }

    private static class ConnectedThread extends Thread {
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket btSocket) {
            try {
                outputStream = btSocket.getOutputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void sendCommand(String command) {
            try {
                outputStream.write(command.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}