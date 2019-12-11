package com.example.food_roulette;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback
{

    private GoogleMap gmap;
    private Button Randombtn;
    private double lat, lng;
    NearbyPlaces nearbyPlaces = new NearbyPlaces();
    String url = "";
    String type = "restaurant";
    String filter = "";

    //On the creation of this activity, it will receive the location of the phone and the type of filter the user wants, and then sets up the map and button to be used later on
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
            //A button to reject the current randomly selected restaurant from the list and gets a new one
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

    //executes these commands when the map is finally displayed on the phone, move screen to its location, creates and fill a object list, send it through the function, and then retrieves the first random restaurant and moves the screen to that restaurant
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        gmap = googleMap;
        gmap.setMyLocationEnabled(true);

        LatLng latlng = new LatLng(lat, lng);
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18));

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

    //Creates the basic url to retrieve the locations we desire by using the conditions of where the phone is located, what type of place we are looking for , and is it open currently
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
