package com.gometro.gotuks;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.common.api.Result;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by wprenison on 2015/12/02.
 */
public class IntentServiceGeocoder extends IntentService
{

    protected ResultReceiver resultReceiver;
    private static final String TAG = "IntentServiceGeocoder";

    public IntentServiceGeocoder()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        resultReceiver = intent.getParcelableExtra("resultReceiver");
        int fetchType = intent.getIntExtra("fetchType", 0);
        String errorMsg = "";

        //Use address name to for geocoding
        if(fetchType == 0)
        {
            String addressName = intent.getStringExtra("addressName");

            try
            {
                addresses = geocoder.getFromLocationName(addressName, 1);
            }
            catch(IOException ioe)
            {
                errorMsg = "Service Not Available: " + ioe.getMessage();
                Log.e(TAG, errorMsg);
            }
        }
        else if(fetchType == 1) //Use location for geocoding
        {
            Location location = intent.getParcelableExtra("location");

            try
            {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            }
            catch (IOException ioe)
            {
                errorMsg = "Service Not Available: " + ioe.getMessage();
                Log.e(TAG, errorMsg);
            }
            catch (IllegalArgumentException iae)
            {
                errorMsg = "Illegal location Latitude = " + location.getLatitude()
                        + " Longitude = " + location.getLongitude() + " Error: " + iae.getMessage();
                Log.e(TAG, errorMsg);
            }
        }
        else
        {
            errorMsg = "Invalid gecoder fetch type";
            Log.e(TAG, errorMsg);
        }

        if(addresses == null || addresses.size() == 0)
        {
            if(errorMsg.isEmpty())
                errorMsg = "Not Found";

            sendResult(0, errorMsg, null);
        }
        else
        {
            Address address = addresses.get(0);

            if(fetchType == 1)   //Override cords for nearby addresses that still classify as almost the same address but nothing more accurate could be found
            {
                Location location = intent.getParcelableExtra("location");
                address.setLatitude(location.getLatitude());
                address.setLongitude(location.getLongitude());
            }

            sendResult(1, null, address);
        }
    }

    private void sendResult(int resultCode, String msg, Address address)
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable("addressResult", address);
        bundle.putString("errorMsg", msg);

        resultReceiver.send(resultCode, bundle);
    }
}
