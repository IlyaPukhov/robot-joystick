package com.ilyap.joystick;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
        ImageButton btnForward = findViewById(R.id.button_forward);
        ImageButton btnBackward = findViewById(R.id.button_backward);
        ImageButton btnLeft = findViewById(R.id.button_left);
        ImageButton btnRight = findViewById(R.id.button_right);
        ImageButton btnStop = findViewById(R.id.button_stop);


        btnForward.setOnClickListener(view -> {
            view.startAnimation(animAlpha);
            threadCommand.sendCommand("f");
        });
        btnBackward.setOnClickListener(view -> {
            view.startAnimation(animAlpha);
            threadCommand.sendCommand("b");
        });
        btnLeft.setOnClickListener(view -> {
            view.startAnimation(animAlpha);
            threadCommand.sendCommand("l");
        });
        btnRight.setOnClickListener(view -> {
            view.startAnimation(animAlpha);
            threadCommand.sendCommand("r");
        });
        btnStop.setOnClickListener(view -> {
            view.startAnimation(animAlpha);
            threadCommand.sendCommand("s");
        });
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