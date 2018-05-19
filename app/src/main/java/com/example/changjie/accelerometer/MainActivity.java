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

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private TextView xText, yText, zText;
    private Sensor mySensor;
    private SensorManager SM;

    public Socket client;
    public boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create our sensor manager
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);

        // Accelerometer sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Rotation Sensor
        //mySensor = SM.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // Register sensor Listener
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Assign TextView
        xText = (TextView)findViewById(R.id.xText);
        yText = (TextView)findViewById(R.id.yText);
        zText = (TextView)findViewById(R.id.zText);

        connected = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        xText.setText("X: " + event.values[0]);
        yText.setText("Y: " + event.values[1]);
        zText.setText("Z: " + event.values[2]);

        try {
            if(connected){
                PrintWriter writer = new PrintWriter(client.getOutputStream());
                writer.print(event.values[0]);
                writer.print(event.values[1]);
                writer.print(event.values[2]);
                writer.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not in use

    }

    class SartClient implements Runnable{

        @Override
        public void run() {

        }
    }
    /**
     * Connect to a TCP server
     * */
    public void connectToSever(View view)
    {
        EditText text_ip = findViewById(R.id.ipAddr);
        EditText text_port = findViewById(R.id.portText);
        String ip_addr = text_ip.getText().toString();
        int Port = Integer.parseInt(text_port.getText().toString());

        try {
            //client = new Socket(ip_addr, Port);
            client = new Socket("192.168.2.19", 31007);
            connected = true;
            Button cb = findViewById(R.id.connect_button);
            cb.setText("Succed!");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
