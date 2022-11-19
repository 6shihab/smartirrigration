package com.example.smartirrigation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class WeatherActivity extends AppCompatActivity {
    public static final String DEGREE_SIGN="\u2103";
    String moisture="0";
    String ph="0";
    String temperature="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            moisture = bundle.getString("moisture");
            ph = bundle.getString("ph");
            temperature = bundle.getString("temperature");
        }


    }
}