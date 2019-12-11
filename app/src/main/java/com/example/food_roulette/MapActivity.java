package com.example.food_roulette;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback
{

    private GoogleMap gmap;
    private Button Randombtn;
    private double lat, lng;
    NearbyPlaces nearbyPlaces = new NearbyPlaces();
    String url = "";
    String type = "restaurant";
    String filter = "";

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        filter = getIntent().getStringExtra("filter");
        lat = getIntent().getDoubleExtra("lat", 5);
        lng = getIntent().getDoubleExtra("lng", 6);
        url = getUrl(lat, lng, type, filter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Randombtn = findViewById(R.id.randomizer);
        Randombtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(nearbyPlaces.checklist())
                {
                    startActivity(new Intent(MapActivity.this,EndMenu.class));
                }
                else
                {
                    gmap.clear();
                    nearbyPlaces.getnextRandom();
                    Toast toast = Toast.makeText(MapActivity.this,"There are " + nearbyPlaces.getCount() + " choices left in reserves", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP,0,150);
                    toast.show();
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        gmap = googleMap;
        gmap.setMyLocationEnabled(true);


        Object dataTransfer[] = new Object[3];
        dataTransfer[0] = gmap;
        dataTransfer[1] = url;
        dataTransfer[2] = getString(R.string.places_api_key);
        nearbyPlaces.execute(dataTransfer);
        if(nearbyPlaces.checklist())
        {
            startActivity(new Intent(MapActivity.this,EndMenu.class));
        }
    }

    private String getUrl(double lat, double lng, String type, String filter)
    {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+lat+","+lng);
        googlePlaceUrl.append("&type="+ type);
        googlePlaceUrl.append(filter);
        googlePlaceUrl.append("&opennow=true");
        googlePlaceUrl.append("&key="+getString(R.string.places_api_key));

        return googlePlaceUrl.toString();
    }
}
