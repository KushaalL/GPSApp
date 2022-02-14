package com.example.finalgpsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {
    TextView lat;
    TextView lon;
    TextView addy;
    TextView dis;
    LocationManager lm;
    String currentAddy;
    Geocoder geocoder;
    double distance = 0;
    List<Location> locList = new ArrayList();
    List<Address> addyList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lat = findViewById(R.id.textViewLatitude);
        lon = findViewById(R.id.textViewLongitude);
        addy = findViewById(R.id.textViewAddress);
        dis = findViewById(R.id.textViewDistance);

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        else
        {
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,1,MainActivity.this);
            geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) )
                {
                    Toast.makeText(MainActivity.this, "GPS Permission Granted", Toast.LENGTH_LONG).show();
                    lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,1,MainActivity.this);
                    geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                }
            }
            else
                Toast.makeText(this, "GPS Permission Denied", Toast.LENGTH_LONG).show();
            return;
        }
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        lat.setText(String.valueOf(latitude));
        lon.setText(String.valueOf(longitude));

        locList.add(location);

        if(locList.size() > 1)
        {
            Location currentLocation = locList.get(locList.size()-2);
            Location newLocation = locList.get(locList.size()-1);

            distance += currentLocation.distanceTo(newLocation);
            double roundDistance = Math.round(distance*100.0)/100.0;
            dis.setText(String.valueOf(roundDistance) + " Meters");
        }

        try {
            addyList = geocoder.getFromLocation(latitude,longitude,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address userAddress = addyList.get(0);
        if(addyList != null)
        {
            for(int i =0; i <= userAddress.getMaxAddressLineIndex(); i++)
            {
                currentAddy = userAddress.getAddressLine(i);
            }
            addy.setText(currentAddy);
        }
        else
        {
            Toast.makeText(MainActivity.this, "No Selected Address", Toast.LENGTH_SHORT).show();
        }
    }

}