package com.gometro.gotuks;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by wprenison on 2015/09/30.
 */
public class GoMetroLiveUPApplication extends Application
{

    private final String SERVER_ADDRESS = "http://live.gometro.co.za:9000"; //"http://159.8.180.6:9001";//"http://159.8.180.6:9001";
    private final String SERVER_API_DOWNSTREAM = "/downStream";

    @Override
    public void onCreate()
    {
        super.onCreate();

        setupServerSettings();
    }

    private void setupServerSettings()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefs.edit().putString("SERVER_ADDRESS", SERVER_ADDRESS).commit();
        sharedPrefs.edit().putString("SERVER_API_DOWNSTREAM", SERVER_API_DOWNSTREAM).commit();
    }
}
