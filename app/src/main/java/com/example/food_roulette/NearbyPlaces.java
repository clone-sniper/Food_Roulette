package com.example.food_roulette;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class NearbyPlaces  extends AsyncTask<Object, String, String>
{

    String GooglePlaceData;
    GoogleMap gmap;
    String url;

    @Override
    protected String doInBackground(Object... objects)
    {
        gmap = (GoogleMap)objects[0];
        url = (String)objects[1];

        RetrieveUrl retrieveUrl = new RetrieveUrl();
        try
        {
            GooglePlaceData = retrieveUrl.readUrl(url);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return GooglePlaceData;
    }

    @Override
    protected void onPostExecute(String s)
    {
        List<HashMap<String, String>>  nearbyPlace = null;
        DataParser dataParser = new DataParser();
        nearbyPlace = dataParser.parse(s);
        RandomPlace(nearbyPlace);
    }

    private void RandomPlace(List<HashMap<String, String>> nearbyPlaces)
    {
        Random random= new Random();

        MarkerOptions markerOptions = new MarkerOptions();
        HashMap<String, String> googlePlace = nearbyPlaces.get(random.nextInt(nearbyPlaces.size()));
        String placeName = googlePlace.get("name");
        String vicinity = googlePlace.get("vicinity");
        double lat = Double.parseDouble(googlePlace.get("lat"));
        double lng = Double.parseDouble(googlePlace.get("lng"));

        LatLng latlng = new LatLng(lat, lng);
        markerOptions.position(latlng);
        markerOptions.title(placeName);
        markerOptions.snippet(vicinity);
        markerOptions.icon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


        gmap.addMarker(markerOptions);
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18));

        Marker mark = gmap.addMarker(markerOptions);
        mark.showInfoWindow();

    }
}
