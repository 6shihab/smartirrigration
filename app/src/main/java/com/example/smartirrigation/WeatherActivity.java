package com.example.smartirrigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class WeatherActivity extends AppCompatActivity {
    public static final String DEGREE_SIGN="\u2103";
    public StringBuilder maxMinStringBuilder;
    TextView tempTextview,maxMinTextview,precipitationTextview,precipitationTypeTextview,currentConditionTextView,weatherDescriptionTextView;
    TextView recomandationTextview;
    String moisture="0";
    String ph="0";
    String temperature="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);



        tempTextview=findViewById(R.id.tempTextView);
        maxMinTextview=findViewById(R.id.maxMinTextviewID);
        precipitationTextview=findViewById(R.id.precipitationTextviewID);
        precipitationTypeTextview=findViewById(R.id.precipitationTypeTextviewID);
        currentConditionTextView=findViewById(R.id.currentConditionTextViewID);
        weatherDescriptionTextView=findViewById(R.id.weatherDescriptionTextViewID);
        recomandationTextview=findViewById(R.id.recomandationTextviewID);

        runSavedWeatherData();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            moisture = bundle.getString("moisture");
            ph = bundle.getString("ph");
            temperature = bundle.getString("temperature");
        }

        maxMinStringBuilder=new StringBuilder();
        callVisualCrossingWeatherAPI();




        BottomNavigationView bottomNav = (BottomNavigationView)findViewById(R.id.bottomNagivationView);
        bottomNav.setSelectedItemId(R.id.recieve);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.recieve:
                        try{
                            Intent intent = new Intent(WeatherActivity.this,MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }catch (Exception e ){
                            Toast.makeText(WeatherActivity.this, "Data not available", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.data:
                        try{
                            Intent intent = new Intent(WeatherActivity.this,dataActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }catch (Exception e ){
                            Toast.makeText(WeatherActivity.this, "Data not available", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.about:
                        try{
                            Intent intent = new Intent(WeatherActivity.this,AboutActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }catch (Exception e ){
                            Toast.makeText(WeatherActivity.this, "Data not available", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                }
                return false;
            }
        });

    }

    private void runSavedWeatherData() {
        SharedPreferences sharedPreferences1=getSharedPreferences("WeatherDetails", Context.MODE_PRIVATE);
        if (sharedPreferences1.contains("temperature") && sharedPreferences1.contains("maxmintemperature")) {
            tempTextview.setText(sharedPreferences1.getString("temperature", "25"));
            maxMinTextview.setText(sharedPreferences1.getString("maxmintemperature", "wait"));
            precipitationTextview.setText(sharedPreferences1.getString("precipitation", "wait"));
            precipitationTypeTextview.setText(sharedPreferences1.getString("precipitationtype", "Wait"));
            currentConditionTextView.setText(sharedPreferences1.getString("currentweathercondition", "wait"));
            weatherDescriptionTextView.setText(sharedPreferences1.getString("weatherdescription", "wait"));

            String[] all_data=new String[7];
            for (int i=0;i<7;i++){
                all_data[i]=sharedPreferences1.getString(String.valueOf(i),"wait");
            }
            TableLayout weathertableLayout = (TableLayout) findViewById(R.id.weatherdataTable);

            for (String row : all_data) {
                TableRow tableRow = new TableRow(WeatherActivity.this);
                String[] parameters = row.split("-", 4);
                for (int i=0; i<4; i++){
                    TextView textView = new TextView(WeatherActivity.this);
                    textView.setText(parameters[i]);
                    textView.setTextSize(15);
                    textView.setPadding(12,10,0, 10);
                    textView.setGravity(Gravity.LEFT);
                    tableRow.addView(textView);
                }
                weathertableLayout.addView(tableRow);
            }

        }
    }

    public void callVisualCrossingWeatherAPI(){

        String lat;
        String lon;
        lat="24.0129";
        lon="89.2591";
        JSONArray jsonDays;

        String url="https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"+lat+","+lon+"/next7days?unitGroup=metric&key=Y3MS4XF2KV3B9A727FE98P5P7&contentType=json";

        RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonDays=response.getJSONArray("days");
                    SharedPreferences sharedPreferences=getSharedPreferences("WeatherDetails", Context.MODE_PRIVATE);
                    SharedPreferences.Editor spEditor=sharedPreferences.edit();

                    String temperature=response.getJSONObject("currentConditions").getString("temp");
                    spEditor.putString("temperature",temperature);
                    tempTextview.setText(temperature);
                    maxMinStringBuilder.append(jsonDays.getJSONObject(0).getString("tempmax")+"\u2103");
                    maxMinStringBuilder.append(" / ");
                    maxMinStringBuilder.append(jsonDays.getJSONObject(0).getString("tempmin")+"\u2103");
                    String dayOfTheWeek = new SimpleDateFormat("EEE").format(new Date());
                    maxMinStringBuilder.append("  "+dayOfTheWeek);
                    spEditor.putString("maxmintemperature",maxMinStringBuilder.toString());
                    maxMinTextview.setText(maxMinStringBuilder);
                    String precipitation="Precipitation: "+jsonDays.getJSONObject(0).getString("precipprob")+"%";
                    spEditor.putString("precipitation",precipitation);
                    precipitationTextview.setText(precipitation);
                    if (jsonDays.getJSONObject(0).getString("preciptype")!="null"){
                        String precipitationtype="Type: "+jsonDays.getJSONObject(0).getString("preciptype");
                        spEditor.putString("precipitationtype",precipitationtype);
                        precipitationTypeTextview.setText(precipitationtype);
                    }
                    else {
                        spEditor.putString("precipitationtype","Type: No Rain");
                        precipitationTypeTextview.setText("Type: No Rain");
                    }
                    String currentweathercondition=response.getJSONObject("currentConditions").getString("conditions");
                    currentConditionTextView.setText(currentweathercondition);
                    spEditor.putString("currentweathercondition",currentweathercondition);
                    String weatherdescription=response.getString("description");
                    weatherDescriptionTextView.setText(weatherdescription);
                    spEditor.putString("weatherdescription",weatherdescription);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd   EEE");

                    String[] all_data=new String[7];
                    for (int i=0;i<7;i++){
                        Calendar calendar = new GregorianCalendar();
                        calendar.add(Calendar.DATE, i);
                        all_data[i]=sdf.format(calendar.getTime())+"-"+jsonDays.getJSONObject(i)
                                .getString("conditions")+"-"+(int)Double.parseDouble(jsonDays.getJSONObject(i)
                                .getString("tempmax"))+"\u2103"+"/"+(int)Double.parseDouble(jsonDays.getJSONObject(i)
                                .getString("tempmin"))+"\u2103"+"-"+jsonDays.getJSONObject(i)
                                .getString("precipprob")+"%";
                        spEditor.putString(String.valueOf(i),all_data[i]);
                    }

                    spEditor.commit();
                    TableLayout weathertableLayout = (TableLayout) findViewById(R.id.weatherdataTable);
                    weathertableLayout.removeAllViews();

                    for (String row : all_data) {
                        TableRow tableRow = new TableRow(WeatherActivity.this);
                        String[] parameters = row.split("-", 4);
                        for (int i=0; i<4; i++){
                            TextView textView = new TextView(WeatherActivity.this);
                            textView.setText(parameters[i]);
                            textView.setTextSize(15);
                            textView.setPadding(12,10,0, 10);
                            textView.setGravity(Gravity.LEFT);
                            tableRow.addView(textView);
                        }
                        weathertableLayout.addView(tableRow);
                    }

                    recomandation(jsonDays);


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Error Occur",Toast.LENGTH_LONG).show();
                }

            }

            private void recomandation(JSONArray jsonDays) throws JSONException {


                if (Integer.parseInt(moisture)>15 && Integer.parseInt(moisture)<25){
                    Double day0precipitationProb=Double.parseDouble(jsonDays.getJSONObject(0).getString("precipprob"));
                    Double day1precipitationProb=Double.parseDouble(jsonDays.getJSONObject(1).getString("precipprob"));
                    Double day2precipitationProb=Double.parseDouble(jsonDays.getJSONObject(2).getString("precipprob"));
                    if (day0precipitationProb>30.0 || day1precipitationProb >35.0 || day2precipitationProb>40){
                        recomandationTextview.setText("Your Water Level is Low. But there is chance of raining within 2 days. Not need to watering Land.");
                    } else {
                        recomandationTextview.setText("Your Water Level is Low. There is no chance of raining withing 2 days. Watering your Land.");
                    }
                }
                else if (Integer.parseInt(moisture)>0 && Integer.parseInt(moisture)<15){
                    recomandationTextview.setText("Immediately Watering Your Land. Don't Wait for Rain. Currently, the water level is in danger. ");
                }
                else if (Integer.parseInt(moisture)>25){
                    recomandationTextview.setText("Your moisture and Temperature is Alright. Do not Need To watering Your Land.");
                }else {
                    recomandationTextview.setText("Please Connect Soil Moisture Device. If Connected Please Wait a few seconds.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error Occur Reload",Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
    }


}