package com.example.changjie.accelerometer;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView xText, yText, zText, ipText, portText;
    private Button cb;

    private Sensor mySensor;
    private SensorManager SM;

    private float[] eventData;

    public Socket client;
    public boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create our sensor manager
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Accelerometer sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Rotation Sensor
        //mySensor = SM.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // Register sensor Listener
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_GAME);

        // Assign TextView
        xText = (TextView) findViewById(R.id.xText);
        yText = (TextView) findViewById(R.id.yText);
        zText = (TextView) findViewById(R.id.zText);

        ipText = findViewById(R.id.ipAddr);
        portText = findViewById(R.id.portText);
        cb = findViewById(R.id.connect_button);

        eventData = new float[3];
        connected = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        xText.setText("X: " + event.values[0]);
        yText.setText("Y: " + event.values[1]);
        zText.setText("Z: " + event.values[2]);

        eventData[0] = event.values[0];
        eventData[1] = event.values[1];
        eventData[2] = event.values[2];

        if (connected) {
            Thread send_data = new Thread(new SendData());
            send_data.start();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not in use

    }

    class StartClient implements Runnable {

        @Override
        public void run() {
            String ip_addr = ipText.getText().toString();
            int Port = Integer.parseInt(portText.getText().toString());

            try {
                client = new Socket(ip_addr, Port);
                //client = new Socket("192.168.2.19", 31007);
                connected = true;
            } catch (IOException e) {
                //cb.setText("Failed!");
                e.printStackTrace();
            }
        }
    }

    class SendData implements Runnable {
        @Override
        public void run() {
            try {
                PrintWriter writer = new PrintWriter(client.getOutputStream());
                writer.print(eventData[0]);
                writer.print(eventData[1]);
                writer.print(eventData[2]);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Connect to a TCP server
     */
    public void connectToSever(View view) {
        Thread start_client = new Thread(new StartClient());
        start_client.start();
        cb.setText("Succed!");
    }

}
