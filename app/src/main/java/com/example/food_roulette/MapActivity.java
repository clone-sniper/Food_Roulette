package com.example.food_roulette;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback
{

    private GoogleMap gmap;
    private FusedLocationProviderClient current_location;
    private Location lastknown;
    private LocationCallback callback;
    private View mapview;
    private Button Randombtn;
    private final float DEFAULT_ZOOM= 18;
    String type = "restaurant";
    int radius = 16000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Randombtn = findViewById(R.id.randomizer);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapview = mapFragment.getView();
        current_location = LocationServices.getFusedLocationProviderClient(MapActivity.this);

        Randombtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               gmap.clear();
               String url = getUrl(lastknown.getLatitude(), lastknown.getLongitude(), type);
               Object dataTransfer[] = new Object[2];
               dataTransfer[0] = gmap;
               dataTransfer[1] = url;

               NearbyPlaces nearbyPlaces = new NearbyPlaces();
               nearbyPlaces.execute(dataTransfer);
            }
        });
    }



    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMyLocationEnabled(true);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(MapActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(MapActivity.this, new OnSuccessListener<LocationSettingsResponse>()
        {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse)
            {
                getDeviceLocation();
            }
        });
        task.addOnFailureListener(MapActivity.this, new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                if(e instanceof ResolvableApiException)
                {
                    ResolvableApiException resolvableApiException = (ResolvableApiException)  e;
                    try {
                        resolvableApiException.startResolutionForResult(MapActivity.this, 400);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 400)
        {
            if(resultCode == RESULT_OK)
            {
                getDeviceLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation()
    {
        current_location.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task)
            {
                if(task.isSuccessful())
                {
                    lastknown = task.getResult();
                    if(lastknown != null)
                    {
                        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastknown.getLatitude(), lastknown.getLongitude()),DEFAULT_ZOOM));
                    }
                     else
                    {
                        LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setInterval(10000);
                        locationRequest.setFastestInterval(5000);
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        callback = new LocationCallback()
                        {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                if (locationResult == null)
                                    return;
                                lastknown = locationResult.getLastLocation();
                                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastknown.getLatitude(),lastknown.getLongitude()),DEFAULT_ZOOM));
                                current_location.removeLocationUpdates(callback);
                            }
                        };
                        current_location.requestLocationUpdates(locationRequest,callback, null);
                    }
                }

            }
        });
    }

    private String getUrl(double lat, double lng, String type)
    {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+lat+","+lng);
        googlePlaceUrl.append("&radius="+radius);
        googlePlaceUrl.append("&type="+type);
        googlePlaceUrl.append("&maxprice="+2);
        googlePlaceUrl.append("&opennow=true");
        googlePlaceUrl.append("&key="+getString(R.string.places_api_key));

        return googlePlaceUrl.toString();
    }
}
