package com.gometro.gotuks;

import android.graphics.Color;

import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.views.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wprenison on 11/03/16.
 */
public class GeoJsonPath
{
    private String title;
    private String description;
    private String color;
    private int width;
    private JSONArray cords;    //2d array containing sets of cords

    public GeoJsonPath(JSONObject pathProperties, JSONObject pathGeometry)
    {
        try
        {
            title = pathProperties.getString("title");
            description = pathProperties.getString("description");
            color = pathProperties.getString("stroke");
            width = pathProperties.getInt("stroke-width");
            cords = pathGeometry.getJSONArray("coordinates");
        }
        catch(JSONException je)
        {
            je.printStackTrace();
        }
    }

    public PathOverlay paintPath(MapView mvMap)
    {
        PathOverlay pathLine = new PathOverlay(Color.parseColor(color), width);

        //Add cords
        for(int i = 0; i < cords.length(); i++)
        {
            try
            {
                JSONArray cordSet = cords.getJSONArray(i);
                pathLine.addPoint(cordSet.getDouble(1), cordSet.getDouble(0));
            }
            catch(JSONException je)
            {
                je.printStackTrace();
            }

        }

        mvMap.addOverlay(pathLine);

        return pathLine;
    }

    public PathOverlay getPathOverlay()
    {
        PathOverlay pathLine = new PathOverlay(Color.parseColor(color), width);

        //Add cords
        for(int i = 0; i < cords.length(); i++)
        {
            try
            {
                JSONArray cordSet = cords.getJSONArray(i);
                pathLine.addPoint(cordSet.getDouble(0), cordSet.getDouble(1));
            }
            catch(JSONException je)
            {
                je.printStackTrace();
            }

        }

        return pathLine;
    }
}
