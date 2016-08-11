package com.gometro.gotuks;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.style.ParagraphStyle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mapbox.mapboxsdk.overlay.Icon;

import cz.msebera.android.httpclient.Header;

/**
 * Created by wprenison on 14/03/16.
 */
public class FragDiagStopSchedule extends android.support.v4.app.DialogFragment implements CompoundButton.OnCheckedChangeListener
{
    private ActivityDisplayStream activity;
    private TextView txtvStopName;
    private ListView lstvSchedules;
    private CheckBox cbFavStop;
    private int stopIndex;
    private GeoJsonPOI geoJsonStop;
    private RequestParams params;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View constructedView = inflater.inflate(R.layout.diag_stop_schedule, container, false);

        //Get handel on views
        txtvStopName = (TextView) constructedView.findViewById(R.id.txtvDFSSHeading);
        cbFavStop = (CheckBox) constructedView.findViewById(R.id.cbDFSSFav);
        lstvSchedules = (ListView) constructedView.findViewById(R.id.lstvDFSSchedule);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return constructedView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().getAttributes().windowAnimations = R.style.dialogWindowAnimations;

        this.activity = (ActivityDisplayStream) getActivity();

        stopIndex = getArguments().getInt("stopIndex");
        geoJsonStop = activity.lstPoiGeoJsonMarkers.get(stopIndex);

        //Set stop name
        txtvStopName.setText(geoJsonStop.getTitle());

        //populate list view with schedule data
        String[][] schedules = geoJsonStop.getSchedules();
        lstvSchedules.setAdapter(new AdapterStopSchedule(activity, R.layout.item_stop_schedule, schedules));

        //Init Fav Button
        initFav();

    }

    private void initFav()
    {
        //Check if user has fav this stop
        //Get api endpoint
        /*SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
        String SERVER_ADDRESS = sharedPrefs.getString("SERVER_ADDRESS", "");
        String SERVER_API_IS_FAV_STOP = sharedPrefs.getString("SERVER_API_IS_FAV_STOP", "");

        //Prep data
        String email = sharedPrefs.getString("cachedEmail", "");
        String stopId = geoJsonStop.getId();

        //Setup params
        params = new RequestParams();
        params.put("email", email);
        params.put("stopid", stopId);

        //Make request
        AsyncHttpClient clientInitReq =  new AsyncHttpClient();
        clientInitReq.post(activity, SERVER_ADDRESS + SERVER_API_IS_FAV_STOP, params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                Boolean isFavStop = Boolean.parseBoolean(new String(responseBody));

                cbFavStop.setChecked(isFavStop);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                //Check error codes
                if(statusCode == 400)
                {
                    String responseJson = new String(responseBody);
                    Toast.makeText(activity, responseJson, Toast.LENGTH_LONG).show();
                }
                else if(statusCode == 0)
                {
                    Toast.makeText(activity, "Please check your internet connection and try again", Toast.LENGTH_LONG).show();
                }
            }
        });*/

        cbFavStop.setChecked(geoJsonStop.getIsFavStop());

        //Set check listner
        cbFavStop.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
    {
        //Get data and base server address
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
        String SERVER_ADDRESS = sharedPrefs.getString("SERVER_ADDRESS", "");
        String email = sharedPrefs.getString("cachedEmail", "");
        String stopId = geoJsonStop.getId();

        params = new RequestParams();
        params.put("email", email);
        params.put("stopId", stopId);

        if(isChecked)
        {

            activity.updateMarkerAsFav(true, stopIndex);

            //Get endpoint
            String SERVER_API_CREATE_FAV_STOP = sharedPrefs.getString("SERVER_API_CREATE_FAV_STOP", "");

            //Prep params already preped from previous init ai request
            //Send request
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(activity, SERVER_ADDRESS + SERVER_API_CREATE_FAV_STOP, params, new AsyncHttpResponseHandler()
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
                {
                    //check for failed and give error msg as well as reset the check status

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
                {
                    //Check errors
                    if(statusCode == 400)
                    {
                        String responseJson = new String(responseBody);
                        Toast.makeText(activity, responseJson, Toast.LENGTH_LONG).show();
                    }
                    else if(statusCode == 0)
                    {
                        Toast.makeText(activity, "Please check your internet connection and try again", Toast.LENGTH_LONG).show();
                    }

                    activity.updateMarkerAsFav(false, stopIndex);
                }
            });
        }
        else
        {
            activity.updateMarkerAsFav(false, stopIndex);

            //Get Endpoint
            String SERVER_API_REMOVE_FAV_STOP = sharedPrefs.getString("SERVER_API_REMOVE_FAV_STOP", "");

            params = new RequestParams();
            params.put("email", email);
            params.put("stopId", stopId);

            AsyncHttpClient client = new AsyncHttpClient();
            client.post(activity, SERVER_ADDRESS + SERVER_API_REMOVE_FAV_STOP, params, new AsyncHttpResponseHandler()
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
                {
                    //check for failed and give error msg as well as reset the check status

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
                {
                    //Check for errors
                    if(statusCode == 400)
                    {
                        String responseJson = new String(responseBody);
                        Toast.makeText(activity, responseJson, Toast.LENGTH_LONG).show();
                    }
                    else if(statusCode == 0)
                    {
                        Toast.makeText(activity, "Please check your internet connection and try again", Toast.LENGTH_LONG).show();
                    }

//                    Log.d("FailedUnFav", "UnFavStop Failed:" + new String(responseBody));

                    activity.updateMarkerAsFav(true, stopIndex);
                }
            });
        }

    }
}
