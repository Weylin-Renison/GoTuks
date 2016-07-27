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
    private final String SERVER_SECRET_KEY = "e2`_=MUjd=owE>V?k@PLtAXBdoLi@HrvHER1F8odwYEaH5o64yjIkP/=t;TEj0iw";
    private final String SERVER_API_DOWNSTREAM = "/downStream";
    private final String SERVER_API_LOGIN = "/loginAppUser";
    private final String SERVER_API_RESET_PASSWORD = "/resetAppUserPassword";
    private final String SERVER_API_CREATE_ACCOUNT = "/registerAppUser";
    private final String SERVER_API_ORG_CODE = "yy#6Xx+f";
    private final String SERVER_API_ANOUNCMENTS = "/getAnouncments";

    @Override
    public void onCreate()
    {
        super.onCreate();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        setupServerSettings(sharedPrefs);
        runOnNewVersion(sharedPrefs);

        //toggelCachedEmail(sharedPrefs, false);
//        clearPrefs(sharedPrefs);
    }

    private void setupServerSettings(SharedPreferences sharedPrefs)
    {
        sharedPrefs.edit().putString("SERVER_ADDRESS", SERVER_ADDRESS)
                .putString("SERVER_SECRET_KEY", SERVER_SECRET_KEY)
                .putString("SERVER_API_DOWNSTREAM", SERVER_API_DOWNSTREAM)
                .putString("SERVER_API_LOGIN", SERVER_API_LOGIN)
                .putString("SERVER_API_RESET_PASSWORD", SERVER_API_RESET_PASSWORD)
                .putString("SERVER_API_CREATE_ACCOUNT", SERVER_API_CREATE_ACCOUNT)
                .putString("SERVER_API_ORG_CODE", SERVER_API_ORG_CODE)
                .putString("SERVER_API_ANOUNCMENTS", SERVER_API_ANOUNCMENTS).commit();
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

    //Clears shared prefs
    private void clearPrefs(SharedPreferences sharedPrefs)
    {
        sharedPrefs.edit().remove("cachedPassword").remove("staySignedIn").commit();
    }
}
