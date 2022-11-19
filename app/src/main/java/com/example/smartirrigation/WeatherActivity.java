package com.example.smartirrigation;

import android.content.Intent;
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
                    String temperature=Integer.toString((int)Double.parseDouble(jsonDays.getJSONObject(0).getString("temp")));
                    tempTextview.setText(temperature);
                    maxMinStringBuilder.append(jsonDays.getJSONObject(0).getString("tempmax")+"\u2103");
                    maxMinStringBuilder.append(" / ");
                    maxMinStringBuilder.append(jsonDays.getJSONObject(0).getString("tempmin")+"\u2103");
                    String dayOfTheWeek = new SimpleDateFormat("EEE").format(new Date());
                    maxMinStringBuilder.append("  "+dayOfTheWeek);
                    maxMinTextview.setText(maxMinStringBuilder);
                    precipitationTextview.setText("Precipitation: "+jsonDays.getJSONObject(0).getString("precipprob")+"%");
                    if (jsonDays.getJSONObject(0).getString("preciptype")!="null"){
                        precipitationTypeTextview.setText("Type: "+jsonDays.getJSONObject(0).getString("preciptype"));
                    }
                    else {
                        precipitationTypeTextview.setText("Type: No Rain");
                    }
                    currentConditionTextView.setText(jsonDays.getJSONObject(0).getString("conditions"));
                    weatherDescriptionTextView.setText(response.getString("description"));
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd   EEE");

                    String[] all_data=new String[7];
                    for (int i=0;i<7;i++){
                        Calendar calendar = new GregorianCalendar();
                        calendar.add(Calendar.DATE, i);
                        all_data[i]=sdf.format(calendar.getTime())+"-"+jsonDays.getJSONObject(i)
                                .getString("conditions")+"-"+jsonDays.getJSONObject(i)
                                .getString("tempmax")+"\u2103"+"/"+jsonDays.getJSONObject(i)
                                .getString("tempmin")+"\u2103";
                    }
                    TableLayout weathertableLayout = (TableLayout) findViewById(R.id.weatherdataTable);

                    for (String row : all_data) {
                        TableRow tableRow = new TableRow(WeatherActivity.this);
                        String[] parameters = row.split("-", 3);
                        for (int i=0; i<3; i++){
                            TextView textView = new TextView(WeatherActivity.this);
                            textView.setText(parameters[i]);
                            textView.setTextSize(16);
                            textView.setPadding(10,0,10, 0);
                            textView.setGravity(Gravity.LEFT);
                            tableRow.addView(textView);
                        }
                        weathertableLayout.addView(tableRow);
                    }



                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Error Occur",Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error Occur",Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
    }


}