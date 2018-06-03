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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView xText, yText, zText, ipText, portText;
    private Button cb, ma, leftIndicator, rightIndicator;

    private Sensor mySensor;
    private SensorManager SM;

    private float[] eventData;

    public Socket client;
    public boolean connected;

    private float manual;
    private float LRIndicator;

    private int countTimer;

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
        ma = findViewById(R.id.btn_manual);

        eventData = new float[5];
        connected = false;

        manual = 0;
        LRIndicator = 0;
        countTimer = 0;

        leftIndicator = findViewById(R.id.btn_left);
        rightIndicator = findViewById(R.id.btn_right);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        xText.setText("X: " + event.values[0]);
        yText.setText("Y: " + event.values[1]);
        zText.setText("Z: " + event.values[2]);

        eventData[0] = event.values[0];
        eventData[1] = event.values[1];
        eventData[2] = event.values[2];
        eventData[3] = manual;
        eventData[4] = LRIndicator;

        if (connected) {
            cb.setText("Succed!");
            Thread send_data = new Thread(new SendData());
            send_data.start();
        } else {
            cb.setText("Connect");
        }

        if (countTimer > 0) {
            countTimer -= 1;
            if (countTimer == 0) {
                leftIndicator.setBackgroundColor(0xFF748C08);
                rightIndicator.setBackgroundColor(0xFF748C08);
            }
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

            //sendAsString();
            sendAsBytes();
        }

        void sendAsString() {
            try {
                PrintWriter writer = new PrintWriter(client.getOutputStream());
                writer.print(eventData[0]);
                writer.print(eventData[1]);
                writer.print(eventData[2]);
                writer.print(eventData[3]);
                writer.print(eventData[4]);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void sendAsBytes() {
            try {
                byte byteArray[] = new byte[20];
                ByteBuffer bbuffer = ByteBuffer.wrap(byteArray);

                FloatBuffer buffer = bbuffer.asFloatBuffer();
                buffer.put(eventData);

                client.getOutputStream().write(byteArray, 0, 20);
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
    }

    /**
     * Update control method
     */
    public void manualAutomated(View view) {
        manual = (manual + 1) % 2;

        if (manual < 0.5) {
            ma.setText("Automated");
            ma.setBackgroundColor(0xFF748C08);//"@android:color/holo_green_dark"
        } else {
            ma.setText("Manual");
            ma.setBackgroundColor(0xFFCC0000);
        }
    }

    /**
     * Activate left indicator
     */
    public void activeLeft(View view) {
        LRIndicator = 1;
        countTimer = 30 * 2;
        leftIndicator.setBackgroundColor(0xFFCC0000);
    }

    /**
     * Activate right indicator
     */
    public void activateRight(View view) {
        LRIndicator = -1;
        countTimer = 30 * 2;
        rightIndicator.setBackgroundColor(0xFFCC0000);
    }
}
