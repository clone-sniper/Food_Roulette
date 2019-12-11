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
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class NearbyPlaces  extends AsyncTask<Object, String, String>
{
    GoogleMap gmap;
    String url, key;
    List<HashMap<String, String>>  nearbyPlace = null;
    List<String> datalist = new LinkedList<>();
    DataParser dataParser = new DataParser();
    boolean isitempty;
    int count = 0;
    int num;
    Random random = new Random();

    //When the class is executed, it receives the data to use the google place search API to retrieve the information from the url it was given.
    @Override
    protected String doInBackground(Object... objects)
    {
        gmap = (GoogleMap)objects[0];
        url = (String)objects[1];
        key = (String)objects[2];
        RetrieveUrl retrieveUrl = new RetrieveUrl();
        try {
            System.out.println(url);
            datalist.add(retrieveUrl.readUrl(url));
            url = dataParser.parsepage(datalist.get(0), key);

            while(url != null && count < 2)
            {
                count++;
                Thread.sleep(2000);  //Pagination requires a delay as pagetoken does not become available until after a delay
                datalist.add(retrieveUrl.readUrl(url));
                url = dataParser.parsepage(datalist.get(count), key);
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        return datalist.get(0);
    }

    //Uses the data from the Url and a HashMap List is created
    @Override
    protected void onPostExecute(String data)
    {
        isitempty = false;

        nearbyPlace = dataParser.parse(data);
        for(int i = 1; i <= count; i++)
            nearbyPlace.addAll(dataParser.parse(datalist.get(i)));

        if(nearbyPlace.size() != 0)
        {
            num = random.nextInt(nearbyPlace.size());
            RandomPlace(nearbyPlace, num);
            nearbyPlace.remove(num);
        }
        else
            isitempty = true;
    }

    //User decided to reject the current restaurant so a new one is selected. It is then removed from the reserved so it won't be selected again.
    public void getnextRandom()
    {
        num = random.nextInt(nearbyPlace.size());
        RandomPlace(nearbyPlace, num);
        nearbyPlace.remove(num);
        if(nearbyPlace.size() == 0)
            isitempty = true;
    }

    //Creates a marker on the map and moves the screen to it while popping upa small screen with its name and location.
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

    //Checks if reserve list is empty
    public boolean checklist()
    {
        if(isitempty)
            return true;
        else
            return false;
    }
    //Uses in the Toast to show how many restaurants are left in reserve
    public int getCount()
    {
        return nearbyPlace.size();
    }
}
