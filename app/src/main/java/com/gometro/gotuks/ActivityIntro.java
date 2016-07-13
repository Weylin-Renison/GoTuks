package com.gometro.gotuks;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

/**
 * Created by wprenison on 2016/07/11.
 */
public class ActivityIntro extends AppCompatActivity
{
    //Views
    FrameLayout flayContent;
    pl.droidsonroids.gif.GifImageView imgvBg;
    ViewSwitcher vsBg;

    //Fragment Vars
    FragmentManager fragMang;
    FragWelcome fragWelcome;
    FragSignin fragSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //Hide status notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Get handle on views
        flayContent = (FrameLayout) findViewById(R.id.flayAIContent);
        imgvBg = (pl.droidsonroids.gif.GifImageView) findViewById(R.id.imgvAIBackground);
        vsBg = (ViewSwitcher) findViewById(R.id.vsAIBackground);

        //Get handle on fragment manager for global use
        fragMang = getSupportFragmentManager();

        //If we have a previously cached email display login directly otherwise display a welcoming screen
        if(checkCachedEmail())
            displaySignIn();
        else
            displayWelcome();

    }

    //Checks if a cached email exists
    private boolean checkCachedEmail()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPrefs.contains("cachedEmail");
    }

    private void displayWelcome()
    {
        //Create fragment and store global for efficiency
        fragWelcome = new FragWelcome();

        //Display frag welcome
        FragmentTransaction fragTrans = fragMang.beginTransaction();
        fragTrans.add(R.id.flayAIContent, fragWelcome);
        fragTrans.commit();

    }

    private void displaySignIn()
    {
        //Swap bg to still blurred image without a transition
        vsBg.showNext();
//        imgvBg.setImageResource(R.drawable.bg_blurred);

        //Create fragment and store global for efficiency
        fragSignin = new FragSignin();

        //Display frag signin
        FragmentTransaction fragTrans = fragMang.beginTransaction();
        fragTrans.add(R.id.flayAIContent, fragSignin);
        fragTrans.commit();
    }

    public void displayCreateAccount()
    {

    }

    //Transition to login screen from welcome screen
    public void onClickDisplaySignIn(View view)
    {
        //Transition bg to still blurred bg
        vsBg.showNext();

        //Transition to sign in screen from welcome screen
        fragSignin = new FragSignin();
        FragmentTransaction fragTrans = fragMang.beginTransaction();
        fragTrans
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .remove(fragWelcome)
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .add(R.id.flayAIContent, fragSignin)
                .addToBackStack("ToSignInScreen")
                .commit();

    }

    //Transition to login screen from welcome screen
    public void onClickDisplayCreateAccount(View view)
    {
        //Transition bg to still blurred bg
        vsBg.showNext();

        //Transition to sign in screen from welcome screen
        FragCreateAccount fragCreateAccount = new FragCreateAccount();
        FragmentTransaction fragTrans = fragMang.beginTransaction();
        fragTrans
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .remove(fragWelcome)
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .add(R.id.flayAIContent, fragCreateAccount)
                .addToBackStack("ToCreateAccountScreen")
                .commit();
    }

    @Override
    public void onBackPressed()
    {
        vsBg.showPrevious();
        super.onBackPressed();
    }
}
