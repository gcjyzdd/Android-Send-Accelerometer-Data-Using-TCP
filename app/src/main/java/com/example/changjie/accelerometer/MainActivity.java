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
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private TextView xText, yText, zText;
    private Sensor mySensor;
    private SensorManager SM;

    private ServerSocket serverSocket;
    Handler UIHandler;
    Thread Thread1 = null;
    private EditText EDITTEXT;
    public static final int PORT = 31005;

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

        EDITTEXT = (EditText)findViewById(R.id.ipAddr);
        UIHandler = new Handler();

        this.Thread1 = new Thread(new Thread1());
        this.Thread1.start();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        xText.setText("X: " + event.values[0]);
        yText.setText("Y: " + event.values[1]);
        zText.setText("Z: " + event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not in use

    }

    /**
     * Connect to a TCP server
     * */
    public void connectToSever(View view)
    {

    }

    class Thread1 implements Runnable
    {
        public void run()
        {
            Socket socket = null;
            try{
                serverSocket = new ServerSocket(PORT);
            }
            catch (IOException e){
                e.printStackTrace();
            }
            while(!Thread.currentThread().isInterrupted()){
                try{
                    socket = serverSocket.accept();

                    Thread2 commThread = new Thread2(socket);
                    new Thread(commThread).start();
                    return;
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

        }
    }

    class Thread2 implements Runnable
    {
        private Socket clientSocket;
        private BufferedReader input;

        public Thread2(Socket clientSocket)
        {
            this.clientSocket = clientSocket;
            try{
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try{
                    String read = input.readLine();
                    if(read!= null)
                    {
                        UIHandler.post(new updateUIThread(read));
                    }
                    else
                    {
                        Thread1 = new Thread(new Thread1());
                        Thread1.start();
                        return;
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    class updateUIThread implements Runnable
    {
        private String msg;

        public updateUIThread(String str){this.msg = str;}

        @Override
        public void run() {
            EDITTEXT.setText(msg);
        }
    }



}
