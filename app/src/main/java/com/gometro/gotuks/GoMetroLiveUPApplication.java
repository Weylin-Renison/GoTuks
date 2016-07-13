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

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        setupServerSettings(sharedPrefs);
        runOnNewVersion(sharedPrefs);

        toggelCachedEmail(sharedPrefs, false);
    }

    private void setupServerSettings(SharedPreferences sharedPrefs)
    {
        sharedPrefs.edit().putString("SERVER_ADDRESS", SERVER_ADDRESS).commit();
        sharedPrefs.edit().putString("SERVER_API_DOWNSTREAM", SERVER_API_DOWNSTREAM).commit();
    }

    private void runOnNewVersion(SharedPreferences sharedPrefs)
    {
        int lastVersionRun = sharedPrefs.getInt("lastVersionRun", 0);

        if(lastVersionRun < BuildConfig.VERSION_CODE)
        {
            //Run any new update code
        }
    }

    //Toggels weither an email is cached or not for testing purpouses
    private void toggelCachedEmail(SharedPreferences sharedPerfs, boolean cached)
    {
        String email = "wprenison@tuks.ac.za";

        if(cached)
            sharedPerfs.edit().putString("cachedEmail", email).commit();
        else
            sharedPerfs.edit().remove("cachedEmail").commit();
    }
}
