package com.example.panicapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button buttonSave;
    private TextView textLat;
    private TextView textLong;
    private TextView textEmail;
    public static final int LOCATION_REQUEST = 999;
    public static final String NAME = "MAPS_ACTIVITY";
    private String email;
    private DatabaseReference databaseUsers;
    private DatabaseReference databaseMessages;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        textEmail = (TextView) findViewById(R.id.textEmail);
        textEmail.setText(email);
        users = new ArrayList<>();
        activateGPS();
        saveFeature();
        adminUsers();
        messages();
        vibrate();
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

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        LatLng bogota = new LatLng(40.730610, -73.935242);
        mMap.addMarker(new MarkerOptions().position(bogota).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                fireSharedPreferences();
            }
        });
        getLocationUsers();
    }

    private void adminUsers(){
        Button users = (Button) findViewById(R.id.buttonUsuarios);
        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goUsers();
            }
        });
    }

    private void goUsers(){
        Intent intent = new Intent(this, UsersActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    private void activateGPS(){
        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        validatePermissions(locationManager, locationListener);
    }

    private void validatePermissions(LocationManager locationManager, LocationListener locationListener){
        int permFile = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permCoarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if( permFile!= PackageManager.PERMISSION_GRANTED && permCoarse!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
        }else{
            if( locationManager != null ){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    private void fireSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        CameraPosition cp = mMap.getCameraPosition();
        String latitude = Double.toString(cp.target.latitude);
        String longitude = Double.toString(cp.target.longitude);
        editor.putString("latitude", latitude);
        editor.putString("longitude", longitude);
        editor.putString("email", email);
        editor.commit();
        Log.i("MAPS", "Commited");

        readSharedPreferences();
    }

    private void readSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(MapsActivity.NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        textLat = (TextView) findViewById(R.id.textLat);
        textLong = (TextView) findViewById(R.id.textLong);

        textLat.setText(sharedPreferences.getString("latitude", "latitude"));
        textLong.setText(sharedPreferences.getString("longitude", "longitude"));
    }

    private void saveFeature(){
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readSharedPreferences();
            }
        });
    }

    private void getLocationUsers () {
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for( DataSnapshot data : dataSnapshot.getChildren() ){
                    User user = data.getValue(User.class);
                    users.add(user);
                    LatLng location = new LatLng(Double.parseDouble(user.getLatitude()), Double.parseDouble(user.getLongitude()));
                    mMap.addMarker(new MarkerOptions().position(location).title("Marker de " + user.getEmail()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void vibrate() {
        Button vibrar = (Button) findViewById(R.id.buttonPanic);
        vibrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); // Vibrate for 500 milliseconds only
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(500); // deprecated in API 26
                }
            }
        });
        createMessage();
    }

    private void messages(){
        databaseMessages = FirebaseDatabase.getInstance().getReference("messages");
        databaseMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for( DataSnapshot data : dataSnapshot.getChildren() ){
                    CustomMessage message = data.getValue(CustomMessage.class);

                    Toast.makeText(MapsActivity.this, "Alguien pide ayuda : \n" + message.getText(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createMessage(){
        /*CustomMessage message = new CustomMessage("HELP SOS !!");
        String idMessage = databaseMessages.push().getKey();
        databaseMessages.child(idMessage).setValue(message);*/

        try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("3195635187", null, "SOS", null, null);
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "SMS FAILED " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}
