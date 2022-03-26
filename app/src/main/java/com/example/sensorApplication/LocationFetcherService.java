package com.example.sensorApplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import net.sqlcipher.database.SQLiteDatabase;

public class LocationFetcherService extends Service {

    private AppDatabase db;
    private UserDetails data = new UserDetails();
    private static final int REQ_CODE = 1;
    public double[] coords = new double[2];
    public static String passphrase = "somepass";

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if(locationResult!=null && locationResult.getLocations().size()>0){
                int latestLocation = locationResult.getLocations().size() - 1;
                double latitude = locationResult.getLocations().get(latestLocation).getLatitude();
                double longitude = locationResult.getLocations().get(latestLocation).getLongitude();
                coords[0] = latitude;
                coords[1] = longitude;
                System.out.println(latitude);
                System.out.println(longitude);
                try{
                    putcoords(latitude,longitude);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };

    private void putcoords(double latitude, double longitude) {
        char[] pass = passphrase.toCharArray();
        final byte[] passphrase1 = SQLiteDatabase.getBytes(pass);
        db = AppDatabase.getInstance(getApplicationContext(),passphrase1);
        UserDetails userDetails;
        userDetails = db.userInfoDao().getLatestData();
        userDetails.xcoord = latitude;
        userDetails.ycoord = longitude;
        db.userInfoDao().update(userDetails);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent!=null){
            String action = intent.getAction();
            if(action!=null){
                if(action=="175"){
                    startLocationServices();
                }
                else if(action=="120"){
                    stopLocationServices();
                }
            }
        }

        return START_STICKY;

    }

    @SuppressLint("MissingPermission")
    private void startLocationServices(){
        String channel_id = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),channel_id);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        System.out.println("This is a test");

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            if(notificationManager!=null && notificationManager.getNotificationChannel(channel_id)==null){
                NotificationChannel notificationChannel = new NotificationChannel(channel_id,"Location Service",NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("Location Service channel");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(15*60000);
        locationRequest.setFastestInterval(60000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest,locationCallback,Looper.getMainLooper());
        startForeground(175,builder.build());
    }

    private void stopLocationServices(){
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

}
