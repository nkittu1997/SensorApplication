package com.example.sensorApplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import net.sqlcipher.database.SQLiteDatabase;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SymptomsScreen extends AppCompatActivity {

    private Spinner spinner;
    RatingBar symptomRatingBar;
    String symptomSelected = "";
    HashMap<String, Float> hm = new HashMap<>();
    private AppDatabase db;
    //Test Purposes
    private AppDatabase db2;
    private UserDetails data = new UserDetails();
    ArrayList<String> al = new ArrayList<>();
    private UserDetails databasevalues = new UserDetails();
    float[] cachedRatings = new float[10];
    //Local array to cache symptom ratings
    //Components required for Location access
    private static final int REQ_CODE = 1;
    public double[] coords = new double[2];
    public boolean status = true;
    public static String passphrase = "somepass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms_screen);
        symptomRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        Button updateButton = (Button) findViewById(R.id.button2);
        spinner = (Spinner) findViewById(R.id.symptoms_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.symptoms_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Gets app database
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    char[] pass = passphrase.toCharArray();
                    final byte[] passphrase1 = SQLiteDatabase.getBytes(pass);
                    db = AppDatabase.getInstance(getApplicationContext(),passphrase1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        symptomRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                int i = spinner.getSelectedItemPosition();
                cachedRatings[i] = v;
                hm.put(symptomSelected, v);
            }
        });


        Button showsymptomsBtn = (Button) findViewById(R.id.showsymptoms);

        showsymptomsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Thread thread = new Thread(new Runnable() {

//                    @Override
//                    public void run() {
                if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(SymptomsScreen.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQ_CODE);
                }
                else{
                    coords = getcurrentLocation();
                    System.out.println("Hello"+coords[0]);

                }
                if(status==true) {
                    startLocationService();
                    status = false;
                }

                System.out.println("world" + hm);
//              UserInfo databasevalues = db.userInfoDao().getLatestData();
                for (Map.Entry me : hm.entrySet()) {
                    if (!me.getKey().equals("id")) {
                        al.add(me.getKey() + ": " + me.getValue());
                    }
                }
                System.out.println("databasevalues" + al);
                ListView listView = (ListView) findViewById(R.id.listview);
                ArrayAdapter adptr = new ArrayAdapter(SymptomsScreen.this, android.R.layout.simple_list_item_1, al);
                listView.setAdapter(adptr);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                data.fever = cachedRatings[0];
                data.cough = cachedRatings[1];
                data.tiredness = cachedRatings[2];
                data.shortnessOfBreath = cachedRatings[3];
                data.muscleAches = cachedRatings[4];
                data.chills = cachedRatings[5];
                data.soreThroat = cachedRatings[6];
                data.runnyNose = cachedRatings[7];
                data.headache = cachedRatings[8];
                data.chestPain = cachedRatings[9];
                data.xcoord = coords[0];
                data.ycoord = coords[1];
                data.timestamp = new Date(System.currentTimeMillis());

                boolean uploadSignsClicked = getIntent().getExtras().getBoolean("uploadSignsClicked");

                //If new row created by Upload Signs button then update that row
                // else create a new row with empty signs and new symptom ratings
                if (uploadSignsClicked == true) {

                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            UserDetails latestData = db.userInfoDao().getLatestData();
                            data.heartRate = latestData.heartRate;
                            data.breathingRate = latestData.breathingRate;
                            data.id = latestData.id;
                            db.userInfoDao().update(data);
                        }
                    });
                    thread.start();

                } else {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db.userInfoDao().insert(data);
                        }
                    });
                    thread.start();
                }
                Toast.makeText(SymptomsScreen.this, "Symptoms updated!", Toast.LENGTH_SHORT).show();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                symptomRatingBar.setRating(cachedRatings[i]);
                symptomSelected = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQ_CODE && grantResults.length>0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getcurrentLocation();
            }
            else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private double[] getcurrentLocation() {

        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        System.out.println(coords[0]);

        LocationServices.getFusedLocationProviderClient(SymptomsScreen.this).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(SymptomsScreen.this).removeLocationUpdates(this);
                if(locationResult!=null && locationResult.getLocations().size()>0){
                    int latestLocation = locationResult.getLocations().size() - 1;
                    double latitude = locationResult.getLocations().get(latestLocation).getLatitude();
                    double longitude = locationResult.getLocations().get(latestLocation).getLongitude();
                    coords[0] = latitude;
                    coords[1] = longitude;
                    System.out.println(coords[0]);
                    System.out.println(coords[1]);
                }
            }
        },Looper.getMainLooper());
        return coords;
    }

    private void startLocationService(){
        Intent intent = new Intent(getApplicationContext(),LocationFetcherService.class);
        intent.setAction("175");
        startService(intent);
        Toast.makeText(this, "Location Service Started", Toast.LENGTH_SHORT).show();
    }

}