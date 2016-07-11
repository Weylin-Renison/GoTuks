package com.gometro.gotuks;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wprenison on 11/03/16.
 */
public class GeoJsonPOI
{
    private Marker marker;
    private String title;
    private String description;
    private double lat;
    private double lon;
    private String[][] schedules;

    public GeoJsonPOI(JSONObject poiProperties, JSONObject poiGeometry)
    {
        try
        {
            title = poiProperties.getString("title");
            description = poiProperties.getString("description");
            JSONArray cords = poiGeometry.getJSONArray("coordinates");
            lon = cords.getDouble(0);
            lat = cords.getDouble(1);
        }
        catch (JSONException je)
        {
            je.printStackTrace();
        }
    }

    public GeoJsonPOI(JSONObject poiProperties, JSONObject poiGeometry, JSONArray poiSchedules)
    {
        try
        {
            title = poiProperties.getString("title");
            description = poiProperties.getString("description");
            JSONArray cords = poiGeometry.getJSONArray("coordinates");
            lon = cords.getDouble(0);
            lat = cords.getDouble(1);

            int noOfSchedules = poiSchedules.length();
            schedules = new String[noOfSchedules][2];

            for(int i = 0; i < noOfSchedules; i++)
            {
                JSONArray jsonArraySchedule = poiSchedules.getJSONArray(i);
                String[] schedule = new String[3];
                schedule[0] = jsonArraySchedule.getString(0);
                schedule[1] = jsonArraySchedule.getString(1);
                schedule[2] = jsonArraySchedule.getString(2);
                schedules[i] = schedule;
            }
        }
        catch (JSONException je)
        {
            je.printStackTrace();
        }
    }

    public Marker paintPOI(MapView mvMap)
    {
        Marker poiMarker = new Marker(title, description, new LatLng(lat,lon));
        mvMap.addMarker(poiMarker);

        return poiMarker;
    }

    public Marker getPOIMarker()
    {
        Marker poiMarker = new Marker(title, description, new LatLng(lat,lon));
        return poiMarker;
    }

    public void setMarker(Marker marker) {this.marker = marker;}

    public String getTitle(){return title;}
    public Marker getMarker(){return marker;}

    public boolean hasSchedule()
    {
        if(schedules != null)
            return true;
        else
            return false;
    }

    public String[][] getSchedules(){return schedules;}

}
