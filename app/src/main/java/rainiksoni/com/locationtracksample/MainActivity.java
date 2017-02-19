package rainiksoni.com.locationtracksample;

import android.*;
import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
        ResultCallback<Status>, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, View.OnClickListener {

    private GoogleMap mMap;

    CustomMarkerText iconFactory;

    RecognitionBroadcastReceiver _broadcastReceiver;

    protected GoogleApiClient mGoogleApiClient;

    public static final String KEY_SERVICE_RUNNING = "service_running";

    Marker marker;

    private Button startTracker;

    public static final String ACTION_START_LOCATION_SERVICE = "START_LOCATION_SERVICE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.ACCESS_FINE_LOCATION}, 11);
        }

        updateLocationEveryTwoMinutes();

        buildGoogleApiClient();
        iconFactory = new CustomMarkerText(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        startTracker = (Button) findViewById(R.id.start_tracking);
        startTracker.setOnClickListener(this);
        _broadcastReceiver = new RecognitionBroadcastReceiver();
        if (isServiceStarted()) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            startTracker.setText("Stop Tracking");
        }





    }


    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();


    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(_broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_ACTION));

    }


    public void startRecognition() {

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);

    }


    protected void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(_broadcastReceiver);
    }


    private PendingIntent getActivityDetectionPendingIntent() {


        Intent intent = new Intent(this, RecognitionService.class);


        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = null;

        if (LatLonHolder.getLatLngList().size() > 0) {
            sydney = (LatLng) LatLonHolder.getLatLngList().get(0);
        } else {
            Toast.makeText(MainActivity.this, "Locating current Latitude and Longitude", Toast.LENGTH_SHORT).show();
            return;
        }

        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("Tracking Activity..."))).
                position(sydney).
                snippet("Hell");


        marker = mMap.addMarker(markerOptions);

        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));

        googleMap.animateCamera(CameraUpdateFactory.zoomIn());

        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
    }

    @Override
    public void onResult(@NonNull Status status) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("MainActivity", "Connection failed");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.v("MainActivity", "Connected");

    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.start_tracking) {

            if (!isServiceStarted()) {
                startTracker.setText("Stop Tracking");
                updateLocationEveryTwoMinutes();

                Toast.makeText(this, "Please wait some time data will be updated", Toast.LENGTH_LONG).show();

                setServiceStarted(true);


                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);


                startRecognition();


            } else {

                setServiceStarted(false);
                startTracker.setText("Start Tracking");
                stopActivityRecognitionService();
                stopLocationTracking();

            }


        }
    }


    class RecognitionBroadcastReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

            ArrayList<DetectedActivity> updatedActivities =
                    intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);

            String buffer = "Un-defined";

            if (updatedActivities != null) {
                buffer = Constants.getActivityString(context, updatedActivities.get(0).getType());
            }


            LatLng sydney = null;

            if (LatLonHolder.getLatLngList().size() > 0) {
                sydney = (LatLng) LatLonHolder.getLatLngList().get(0);
            } else {
                Toast.makeText(MainActivity.this, "Locating current Latitude and Longitude", Toast.LENGTH_SHORT).show();
                return;
            }

            mMap.clear();

            Toast.makeText(MainActivity.this,
                    "Current LatLon : " + sydney.latitude + " " + sydney.longitude + " Size: " + LatLonHolder.getLatLngList().size(),
                    Toast.LENGTH_LONG).show();

            MarkerOptions markerOptions = new MarkerOptions().
                    icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(buffer))).
                    position(sydney);

            marker = mMap.addMarker(markerOptions);
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));

            mMap.animateCamera(CameraUpdateFactory.zoomIn());

            mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);


        }
    }


    private void stopActivityRecognitionService() {
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient,
                getActivityDetectionPendingIntent());
    }

    private void stopLocationTracking(){
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = getStartPendingIntent(getApplicationContext());
        manager.cancel(pendingIntent);
    }


    private void updateLocationEveryTwoMinutes() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = getStartPendingIntent(getApplicationContext());
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                getTriggerAt(new Date()),
                5000,
                alarmIntent);
    }


    private static long getTriggerAt(Date now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        return calendar.getTimeInMillis();
    }


    private static PendingIntent getStartPendingIntent(Context context) {
        Intent intent = new Intent(context, LocationRequesterService.class);
        intent.setAction(ACTION_START_LOCATION_SERVICE);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void setServiceStarted(boolean flag) {
        SharedPrefDataStore.putBoolean(getApplicationContext(),
                KEY_SERVICE_RUNNING, flag);
    }

    private boolean isServiceStarted() {
        return SharedPrefDataStore.getBoolean(getApplicationContext(),
                KEY_SERVICE_RUNNING, false);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 11){
            for(int i = 0; i < permissions.length; i++){
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    updateLocationEveryTwoMinutes();
                }
            }
        }
    }
}
