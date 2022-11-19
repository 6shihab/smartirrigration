package com.example.smartirrigation;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    TextView myLabel;
    TextView moistureValue;
    TextView phValue;
    TextView temperatureValue;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    String moisture="10";
    String ph="11";
    String temperature="12";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button connect = (Button)findViewById(R.id.connect);
        Button saveButton = (Button)findViewById(R.id.save);
        Button weatherButton=(Button)findViewById(R.id.weatherButtonID);
        myLabel = (TextView)findViewById(R.id.info);
        moistureValue = (TextView)findViewById(R.id.tds);
        phValue = (TextView)findViewById(R.id.ph);
        temperatureValue = (TextView)findViewById(R.id.temperature);
//        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNagivationView);

        BottomNavigationView bottomNav = (BottomNavigationView)findViewById(R.id.bottomNagivationView);
        bottomNav.setSelectedItemId(R.id.recieve);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.recieve:

                        return true;
                    case R.id.data:
                        try{
                            Intent intent = new Intent(MainActivity.this,com.example.smartirrigation.dataActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }catch (Exception e ){
                            Toast.makeText(MainActivity.this, "Data not available", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.about:
                        try{
                            Intent intent = new Intent(MainActivity.this,com.example.smartirrigation.AboutActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }catch (Exception e ){
                            Toast.makeText(MainActivity.this, "Data not available", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                }
                return false;
            }
        });




        //save Button
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try{
                    Intent intent = new Intent(MainActivity.this, com.example.smartirrigation.SaveActivity.class);
                    intent.putExtra("moisture", moisture);
                    intent.putExtra("ph", ph);
                    intent.putExtra("temperature", temperature);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }catch (Exception e ){
                    Toast.makeText(MainActivity.this, "Data not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                    intent.putExtra("moisture", moisture);
                    intent.putExtra("ph", ph);
                    intent.putExtra("temperature", temperature);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }catch (Exception e ){
                    Toast.makeText(MainActivity.this, "Data not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Open Button
        connect.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    boolean deviceFound = findBT();
                    if (deviceFound){
                        openBT();
                    }
                    else {
                        myLabel.setText("Device Not Found");
                    }
                }
                catch (IOException ex) { }
            }
        });

    }

    boolean findBT(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            myLabel.setText("No Bluetooth Adapter Available");
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("HC-05"))
                {
                    mmDevice = device;
                    myLabel.setText("Testing Device Found");
                    return true;
                }
            }
        }
        return false;
    }

    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

        myLabel.setText("Testing Device Connected");
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition-1];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    String[] parameters = data.split("-", 5);

                                    moisture=parameters[0];
                                    ph=parameters[1];
                                    temperature=parameters[2];

                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            moistureValue.setText(moisture);
                                            phValue.setText(ph);
                                            temperatureValue.setText(temperature);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }
}