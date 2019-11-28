package com.example.food_roulette;

import android.content.Context;
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
import java.util.Objects;
import java.util.Random;

public class NearbyPlaces  extends AsyncTask<Object, String, String>
{

    String GooglePlaceData;
    GoogleMap gmap;
    String url;
    List<HashMap<String, String>>  nearbyPlace = null;

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
    protected void onPostExecute(String data)
    {
        Random random = new Random();
        int num;

        DataParser dataParser = new DataParser();
        nearbyPlace = dataParser.parse(data);
        num = random.nextInt(nearbyPlace.size());
        RandomPlace(nearbyPlace, num);

        System.out.println("there are "+nearbyPlace.size() + " left");
    }

/*
    public void excecute(Object[] data)  {
        DataParser dataParser = new DataParser();
        gmap = (GoogleMap)data[0];
        url = (String)data[1];
        RetrieveUrl retrieveUrl = new RetrieveUrl();
        try {
            GooglePlaceData = retrieveUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        nearbyPlace = dataParser.parse(GooglePlaceData);
        Random random= new Random();
        int num = random.nextInt(nearbyPlace.size());
        RandomPlace(nearbyPlace, num);

        //System.out.println("there are "+nearbyPlace.size() + " left");
    }
*/

    private void RandomPlace(List<HashMap<String, String>> nearbyPlaces, int num)
    {
        MarkerOptions markerOptions = new MarkerOptions();
        HashMap<String, String> googlePlace = nearbyPlaces.get(num);
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

    //to be finished later so more 60 results show up instead of 20
    /*
    private void nextpage(String jsonData, List<HashMap<String, String>> listplace)
    {
        String pagetoken = "";
        JSONObject jsonObject;
        String key;

        try
        {
            jsonObject = new JSONObject(jsonData);
            pagetoken = jsonObject.getString("next_page_token");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        if(pagetoken != null)
        {
            RetrieveUrl retrieveUrl = new RetrieveUrl();
            StringBuilder Url = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            Url.append("pagetoken="+pagetoken);
            key = Context.getResources().getString(R.string.places_api_key);
            Url.append("&key=" + key);
        }
    }

    private void nextPages(String data, List<HashMap<String, String>> listplace)
    {
        List<HashMap<String, String>>  nearbyPlace = null;
        DataParser dataParser = new DataParser();
        nearbyPlace = dataParser.parse(data);
        for (int i = 0; i < nearbyPlace.size();i++)
            listplace.add(nearbyPlace.get(i));
    }
*/
}
