package com.example.smartirrigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

//        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNagivationView);
//
//        BottomNavigationView bottomNav = findViewById(R.id.bottomNagivationView);
//        bottomNav.setOnNavigationItemSelectedListener(navListener);
        BottomNavigationView bottomNav = (BottomNavigationView)findViewById(R.id.bottomNagivationView);
        bottomNav.setSelectedItemId(R.id.about);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.recieve:
                        try{
                            Intent intent = new Intent(AboutActivity.this, com.example.smartirrigation.MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }catch (Exception e ){
                            Toast.makeText(AboutActivity.this, "Data not available", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.data:
                        try{
                            Intent intent = new Intent(AboutActivity.this,dataActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }catch (Exception e ){
                            Toast.makeText(AboutActivity.this, "Data not available", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.about:

                        return true;
                }
                return false;
            }
        });
    }

}