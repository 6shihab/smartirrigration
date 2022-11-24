package com.example.smartirrigation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class SaveActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private FusedLocationProviderClient mFusedLocationClient;

    DatabaseReference databaseReference;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;


    private LocationManager locationManager;
    private LocationListener Listener;
    TextView myLabel;
    TextView moistureValue;
    TextView phValue;
    TextView temperatureValue;
    TextView latitudeValue;
    TextView longitudeValue;
    DBHelper dbHelper;
    TextView info;
    RequestQueue queue;

    String moisture="0";
    String ph="0";
    String temperature="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        databaseReference= FirebaseDatabase.getInstance().getReference("SoilParameter/");


        myLabel = (TextView)findViewById(R.id.info);
        moistureValue = (TextView)findViewById(R.id.mositure);
        phValue = (TextView)findViewById(R.id.ph);
        temperatureValue = (TextView)findViewById(R.id.temperature);
        latitudeValue = (TextView)findViewById(R.id.latitude);
        longitudeValue = (TextView)findViewById(R.id.longitude);
        info = (TextView) findViewById(R.id.info);
        queue = Volley.newRequestQueue(this);
        dbHelper = new DBHelper(this);
        Button confirm = (Button)findViewById(R.id.confirm);
        Button cancel = (Button)findViewById(R.id.cancel);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        BottomNavigationView bottomNav = (BottomNavigationView)findViewById(R.id.bottomNagivationView);
        bottomNav.setSelectedItemId(R.id.recieve);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.recieve:
                        try{
                            Intent intent = new Intent(SaveActivity.this,MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }catch (Exception e ){
                            Toast.makeText(SaveActivity.this, "Data not available", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.data:
                        try{
                            Intent intent = new Intent(SaveActivity.this,dataActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }catch (Exception e ){
                            Toast.makeText(SaveActivity.this, "Data not available", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.about:
                        try{
                            Intent intent = new Intent(SaveActivity.this,AboutActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }catch (Exception e ){
                            Toast.makeText(SaveActivity.this, "Data not available", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                }
                return false;
            }
        });



        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            moisture = bundle.getString("moisture");
            ph = bundle.getString("ph");
            temperature = bundle.getString("temperature");
            moistureValue.setText(moisture+"%");
            phValue.setText(ph);
            temperatureValue.setText(temperature);
        }

        cancel.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try{
                    Intent intent = new Intent(SaveActivity.this,MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }catch (Exception e ){
                    Toast.makeText(SaveActivity.this, "Data not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (Integer.parseInt(moisture)==0 && Integer.parseInt(ph)==0 && Integer.parseInt(temperature)==0){
                    Toast.makeText(getApplicationContext(),"Please Connect Soil Testing Device",Toast.LENGTH_SHORT).show();
                    info.setText("Something wrong to add Data");

                }else {
                    try{
                        dbHelper.insertData(moisture, ph, temperature,latitudeValue.getText().toString(),longitudeValue.getText().toString());
                        Toast.makeText(SaveActivity.this, "Data Added", Toast.LENGTH_SHORT).show();
                        saveData();
                    }catch (Exception e ){
                        Toast.makeText(SaveActivity.this, "Data don't Add", Toast.LENGTH_SHORT).show();
                        info.setText("Something Wrong, check internet connection. Data do not added on Firebase Webserver");
                    }
                }

            }
        });


    }
    private void saveData() {
        String id=databaseReference.push().getKey();
        String currentDate = new SimpleDateFormat("dd MMM yy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        SoilParameter soilParameter=new SoilParameter(id,currentDate,currentTime,moisture,ph,temperature,latitudeValue.getText().toString().trim(),longitudeValue.getText().toString().trim());
        databaseReference.child(id).setValue(soilParameter);
        info.setText("Data Added on Firebase Webserver");

    }



    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();

                            latitudeValue.setText(Double.toString(mLastLocation.getLatitude()));
                            longitudeValue.setText(Double.toString(mLastLocation.getLongitude()));
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                            showSnackbar(getString(R.string.no_location_detected));
                        }
                    }
                });
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(R.id.main_activity_container);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(SaveActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }
}