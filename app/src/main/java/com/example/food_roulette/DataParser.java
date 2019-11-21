package com.example.food_roulette;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser
{
    private HashMap<String, String> getPlace(JSONObject googlePlaceJson)
    {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String placename = "na";
        String vicinity = "na";
        String lat = "";
        String lng = "";
        String ref = "";

          try
            {
                if(!googlePlaceJson.isNull("name"))
                {
                    placename = googlePlaceJson.getString("name");
                }
                if(!googlePlaceJson.isNull("vicinity"))
                {
                    vicinity = googlePlaceJson.getString("vicinity");
                }
                lat = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
                lng = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
                ref = googlePlaceJson.getString("reference");

                googlePlaceMap.put("name",placename);
                googlePlaceMap.put("vicinity", vicinity);
                googlePlaceMap.put("lat",lat);
                googlePlaceMap.put("lng", lng);
                googlePlaceMap.put("reference", ref);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return googlePlaceMap;
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray)
    {
        int count = jsonArray.length();
        List<HashMap<String, String>> placelist = new ArrayList<>();
        HashMap<String, String> placeMap = null;

        for(int i = 0; i < count; i++)
        {
            try
            {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placelist.add(placeMap);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return placelist;
    }

    public List<HashMap<String, String>> parse(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try
        {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }
}