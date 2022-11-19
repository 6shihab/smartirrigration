package com.example.smartirrigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class dataActivity extends AppCompatActivity implements View.OnClickListener {

    com.example.smartirrigation.DBHelper dbHelper;
    private Button deleteAllButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        dbHelper = new com.example.smartirrigation.DBHelper(this);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNagivationView);
        bottomNav.setSelectedItemId(R.id.data);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.recieve:
                        try{
                            Intent intent = new Intent(dataActivity.this, com.example.smartirrigation.MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }catch (Exception e ){
                            Toast.makeText(dataActivity.this, "Data not available", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.data:

                        return true;
                    case R.id.about:
                        try{
                            Intent intent = new Intent(dataActivity.this, com.example.smartirrigation.AboutActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }catch (Exception e ){
                            Toast.makeText(dataActivity.this, "Data not available", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                }
                return false;
            }
        });

        ArrayList<String> all_data = new ArrayList<String>();
        all_data = dbHelper.getAllData();

        TableLayout tableLayout = (TableLayout) findViewById(R.id.dataTable);
        deleteAllButton=(Button) findViewById(R.id.deleteAllButtonID);
        deleteAllButton.setOnClickListener(this);

        for (String row : all_data) {
            TableRow tableRow = new TableRow(this);
            String[] parameters = row.split("-", 8);
            for (int i=0; i<6; i++){
                TextView textView = new TextView(this);
                textView.setText(parameters[i]);
                textView.setTextSize(18);
                textView.setPadding(10,0,10, 0);
                textView.setGravity(Gravity.CENTER);
                tableRow.addView(textView);
            }
            tableLayout.addView(tableRow);
        }



    }


    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.deleteAllButtonID){
           try {
               dbHelper.deleteAll();
               Intent intent = new Intent(dataActivity.this, com.example.smartirrigation.dataActivity.class);
               startActivity(intent);
               overridePendingTransition(0,0);

           } catch (Exception e) {
               Toast.makeText(dataActivity.this, "Data not available", Toast.LENGTH_SHORT).show();
           }
        }
    }
}