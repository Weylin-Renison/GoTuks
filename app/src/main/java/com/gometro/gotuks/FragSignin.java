package com.gometro.gotuks;

import android.animation.Animator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by wprenison on 2016/07/13.
 */
public class FragSignin extends Fragment implements InterfaceSignin
{

    //Views
    TextView txtvSignInHeading;
    TextView txtvForgotPasswoordHeading;
    TextView txtvDescForgotPassword;
    ImageView imgvLogo;
    EditText etxtEmail;
    RelativeLayout relLayEmail;
    View vDiv1;
    EditText etxtPassword;
    RelativeLayout relLayPassword;
    CheckBox cbPasswordVis;
    View vDiv2;
    CheckBox cbStaySignedIn;
    TextView txtvForgotPassword;
    LinearLayout linLayCreateAccount;
    TextView txtvCreateAccount;
    public Button btnSignIn;
    ProgressBar pgProgessCircle;

    //Vars
    ActivityIntro activity;
    String email;
    String password;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        //Inflate layout
        View constuctedView = inflater.inflate(R.layout.frag_signin, container, false);

        //Get Handle on any views
        txtvSignInHeading = (TextView) constuctedView.findViewById(R.id.txtvFSSignin);
        imgvLogo = (ImageView) constuctedView.findViewById(R.id.imgvFSLogo);
        etxtEmail = (EditText) constuctedView.findViewById(R.id.etxtFSEmail);
        relLayEmail = (RelativeLayout) constuctedView.findViewById(R.id.relLayFSEmail);
        vDiv1 = constuctedView.findViewById(R.id.vFSDivider1);
        etxtPassword = (EditText) constuctedView.findViewById(R.id.etxtFSPassword);
        relLayPassword = (RelativeLayout) constuctedView.findViewById(R.id.relLayFSPassword);
        cbPasswordVis = (CheckBox) constuctedView.findViewById(R.id.cbFSPasswordVisibility);
        vDiv2 = constuctedView.findViewById(R.id.vFSDivider2);
        cbStaySignedIn = (CheckBox) constuctedView.findViewById(R.id.cbFSStaySignedIn);
        txtvForgotPassword = (TextView) constuctedView.findViewById(R.id.txtvFSForgotPassword);
        linLayCreateAccount = (LinearLayout) constuctedView.findViewById(R.id.linLayFSCreateAccount);
        txtvCreateAccount = (TextView) constuctedView.findViewById(R.id.txtvFSCreateAccount);
        btnSignIn = (Button) constuctedView.findViewById(R.id.btnFSSignin);
        pgProgessCircle = (ProgressBar) constuctedView.findViewById(R.id.pgFSProgressCircle);

        txtvForgotPasswoordHeading = (TextView) constuctedView.findViewById(R.id.txtvFSForgotPasswordHeading);
        txtvDescForgotPassword = (TextView) constuctedView.findViewById(R.id.txtvFSDescForgotPassword);

        return constuctedView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        activity = (ActivityIntro) getActivity();

        //Set layout listners to focus fields when clicked on
        relLayEmail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                etxtEmail.requestFocus();

                //Set selection just before @ symbol
                etxtEmail.setSelection(etxtEmail.getText().toString().indexOf("@"));
            }
        });

        relLayPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                etxtPassword.requestFocus();
                etxtPassword.setSelection(etxtPassword.getText().toString().length());
            }
        });


        //Set password visibility depending on check box listiner
        cbPasswordVis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked)
            {
                if(checked)
                    etxtPassword.setTransformationMethod(null);
                else
                    etxtPassword.setTransformationMethod(new PasswordTransformationMethod());
            }
        });

        //Set on Click listners
        txtvForgotPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                activity.forgotPasswprdVissible = true;
                animateForgotPassword(true);
            }
        });

        txtvCreateAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Move to create account screen
                activity.navSigninToCreateAccount();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //The state that the button is in, because this button serves 2 functions, sign in and reset
                //password depending on the tet on the button
                final String btnState = btnSignIn.getText().toString();

                //Get data values
                email = etxtEmail.getText().toString().trim();
                password = etxtPassword.getText().toString();

                //Validte data
                if(btnState.equalsIgnoreCase("Sign In"))
                {
                    if(validateFields(false))
                    {
                        //Authenticate User

                        //Get url
                        SharedPreferences prefMang = PreferenceManager.getDefaultSharedPreferences(activity);
                        String SERVER_ADDRESS = prefMang.getString("SERVER_ADDRESS", null);
                        String SERVER_API_LOGIN = prefMang.getString("SERVER_API_LOGIN", null);
                        String SERVER_SECRET_KEY = prefMang.getString("SERVER_SECRET_KEY", null);

                        //Crypto password
                        String cryptoPassword = null;
                        try
                        {
                            HashHelper hashHlp = HashHelper.getInstance();
                            cryptoPassword = hashHlp.hmacSha1(password, SERVER_SECRET_KEY);
                        } catch(UnsupportedEncodingException e)
                        {
                            e.printStackTrace();
                        } catch(NoSuchAlgorithmException e)
                        {
                            e.printStackTrace();
                        } catch(InvalidKeyException e)
                        {
                            e.printStackTrace();
                        }

                        //Request password reset
                        RequestParams params = new RequestParams();
                        params.put("email", email);
                        params.put("password", cryptoPassword);

                        pgProgessCircle.setVisibility(View.VISIBLE);
                        btnSignIn.setText("");

                        final AsyncHttpClient client = new AsyncHttpClient();
                        client.setUserAgent("android");
                        client.post(activity, SERVER_ADDRESS + SERVER_API_LOGIN, params, new AsyncHttpResponseHandler()
                        {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
                            {
                                cacheData(responseBody);
                                activity.navMainActivity();

                                pgProgessCircle.setVisibility(View.GONE);
                                btnSignIn.setText(R.string.btnFSSignin);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
                            {
                                //Check error codes
                                if(statusCode == 400)
                                {
                                    String responseJson = new String(responseBody);
                                    try
                                    {
                                        JSONObject jsonRespones = new JSONObject(responseJson);
                                        String msg = jsonRespones.getString("msg");
                                        etxtEmail.setError(msg);

                                    } catch(JSONException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                else if(statusCode == 0)
                                {
                                    Toast.makeText(activity, "Please check your internet connection and try again", Toast.LENGTH_LONG).show();
                                }

                                pgProgessCircle.setVisibility(View.GONE);
                                btnSignIn.setText(R.string.btnFSSignin);
                            }
                        });

                    }

                } else
                {
                    if(validateFields(true))
                    {
                        //Get url
                        SharedPreferences prefMang = PreferenceManager.getDefaultSharedPreferences(activity);
                        String SERVER_ADDRESS = prefMang.getString("SERVER_ADDRESS", null);
                        String SERVER_API_RESET_PASSWORD = prefMang.getString("SERVER_API_RESET_PASSWORD", null);

                        //Request password reset
                        RequestParams params = new RequestParams();
                        params.put("email", email);

                        pgProgessCircle.setVisibility(View.VISIBLE);
                        btnSignIn.setText("");

                        final AsyncHttpClient client = new AsyncHttpClient();
                        client.setUserAgent("android");
                        client.post(activity, SERVER_ADDRESS + SERVER_API_RESET_PASSWORD, params, new AsyncHttpResponseHandler()
                        {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
                            {
                                Toast.makeText(activity, "Please check your email for your new password", Toast.LENGTH_LONG).show();
                                animateForgotPassword(false);
                                pgProgessCircle.setVisibility(View.GONE);
                                btnSignIn.setText(R.string.btnFSSignin);

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
                            {
//                                Log.e("statusCode Check", statusCode + "");
                                //Check possible error codes
                                if(statusCode == 400)
                                {
                                    String responseJson = new String(responseBody);
                                    try
                                    {
                                        JSONObject jsonRespones = new JSONObject(responseJson);
                                        String msg = jsonRespones.getString("msg");
                                        etxtEmail.setError(msg);

                                    } catch(JSONException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                else if(statusCode == 0)
                                {
                                   Toast.makeText(activity, "Please check your internet connection and try again", Toast.LENGTH_LONG).show();
                                }

                                pgProgessCircle.setVisibility(View.GONE);
                                btnSignIn.setText(R.string.btnFSResetPassword);
                            }
                        });

                    }
                }

            }
        });

        //Initialize with cached data
        initFrag();
    }

    private void initFrag()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);

        String cachedEmail = sharedPrefs.getString("cachedEmail", "");
        String cachedPassword = sharedPrefs.getString("cachedPassword", "");
        boolean staySignedIn = sharedPrefs.getBoolean("staySignedIn", true);

        //Pre populate email if there is a cached value
        if(!cachedEmail.isEmpty())
            etxtEmail.setText(cachedEmail);

        if(!staySignedIn)
        {
            cbStaySignedIn.setChecked(false);
        }
        else
        {
            if(!cachedPassword.isEmpty())
            {
                etxtPassword.setText(cachedPassword);

                //Authenticate
                btnSignIn.performClick();
            }
        }
    }

    private void cacheData(byte[] responseBody)
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);

        sharedPrefs.edit().putString("cachedEmail", email).commit();

        if(cbStaySignedIn.isChecked())
            sharedPrefs.edit().putString("cachedPassword", password).putBoolean("staySignedIn", true).commit();
        else
            sharedPrefs.edit().putBoolean("staySignedIn", false).commit();

        //Check response body for extra cache values
        String jsonStringUser = new String(responseBody);

        try
        {
            JSONObject jsonUser = new JSONObject(jsonStringUser);

            String fullName = jsonUser.getString("fullName");
            String phone = jsonUser.getString("phone");
            String address = jsonUser.getString("address");

            sharedPrefs.edit().putString("cachedFullName", fullName)
                    .putString("cachedPhone", phone)
                    .putString("cachedAddress", address).commit();

        } catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    private boolean validateFields(boolean forgotPasswordValidation)
    {
        boolean valid = true;

        //Check null values
        if(password.isEmpty() && !forgotPasswordValidation)
        {
            etxtPassword.setError("Required Field");
            valid = false;
        }

        //Check specific email constraints
        if(email.isEmpty())
        {
            etxtEmail.setError("Required Field");
            valid = false;
        } //else if(!email.endsWith("@tuks.ac.za")) TODO:Renable validation for @tuks.ac.za
//        {
//            etxtEmail.setError("Only university emails are accepted");
//            valid = false;
//        }

        return valid;
    }

    public void animateForgotPassword(boolean forward)
    {
        //Changes are reversed of bool is false and is called from activity pop back stack not from fragment
        //animate and make control changes based on boolean
        if(forward)
        {
            //Prepare Animations
//            final Animation animFadeIn = AnimationUtils.loadAnimation(activity, R.anim.abc_fade_in);
//            animFadeIn.setDuration(250);
//            animFadeIn.setFillAfter(true);
//
//            Animation animFadeOutToIn = AnimationUtils.loadAnimation(activity, R.anim.abc_fade_out);
//            animFadeOutToIn.setDuration(250);
//            animFadeOutToIn.setFillAfter(true);
//            animFadeOutToIn.setAnimationListener(new Animation.AnimationListener()
//            {
//                @Override
//                public void onAnimationStart(Animation animation)
//                {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation)
//                {
//                    txtvForgotPasswoordHeading.startAnimation(animFadeIn);
//                    txtvDescForgotPassword.startAnimation(animFadeIn);
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation)
//                {
//
//                }
//            });
//
//            //Animate layout hide other fields and controls and show reset pass word views
//            //Hide signin heading and logo, show forgot password heading and description
//            txtvSignInHeading.startAnimation(animFadeOutToIn);
//            imgvLogo.startAnimation(animFadeOutToIn);

            //Crossfade views

            txtvForgotPasswoordHeading.setAlpha(0f);
            txtvForgotPasswoordHeading.setVisibility(View.VISIBLE);

            txtvDescForgotPassword.setAlpha(0f);
            txtvDescForgotPassword.setVisibility(View.VISIBLE);

            txtvForgotPasswoordHeading.animate().alpha(1f).setDuration(300).setListener(null);
            txtvDescForgotPassword.animate().alpha(1f).setDuration(300).setListener(null);

            txtvSignInHeading.animate().alpha(0f).setDuration(300).setListener(new Animator.AnimatorListener()
            {

                @Override
                public void onAnimationStart(Animator animator)
                {

                }

                @Override
                public void onAnimationEnd(Animator animator)
                {
                    txtvSignInHeading.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator)
                {

                }

                @Override
                public void onAnimationRepeat(Animator animator)
                {

                }

            });

            imgvLogo.animate().alpha(0f).setDuration(300).setListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animator)
                {

                }

                @Override
                public void onAnimationEnd(Animator animator)
                {
                    imgvLogo.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator)
                {

                }

                @Override
                public void onAnimationRepeat(Animator animator)
                {

                }
            });


            //Hide password field and controls
            relLayPassword.animate().alpha(0f).setDuration(300).start();
            relLayPassword.setVisibility(View.INVISIBLE);
            cbStaySignedIn.animate().alpha(0f).setDuration(300).start();
            cbStaySignedIn.setVisibility(View.INVISIBLE);
            txtvForgotPassword.animate().alpha(0f).setDuration(300).start();
            txtvForgotPassword.setVisibility(View.INVISIBLE);
            linLayCreateAccount.animate().alpha(0f).setDuration(300).start();
            linLayCreateAccount.setVisibility(View.INVISIBLE);
            vDiv2.animate().alpha(0f).setDuration(300).start();
            vDiv2.setVisibility(View.INVISIBLE);

//            Animation animSlideFieldDown = AnimationUtils.loadAnimation(activity, R.anim.slide_field_down);
//            animSlideFieldDown.setFillAfter(true);
//
//            relLayEmail.startAnimation(animSlideFieldDown);

            relLayEmail.animate().translationY(400f).setDuration(250).start();

            //Change button text
            btnSignIn.setText(R.string.btnFSResetPassword);
        } else
        {
            //Reverse animation and control changes
            //Prepare Animations
            final Animation animFadeIn = AnimationUtils.loadAnimation(activity, R.anim.abc_fade_in);
            animFadeIn.setDuration(300);
            animFadeIn.setFillBefore(true);
//
//            Animation animFadeOutToIn = AnimationUtils.loadAnimation(activity, R.anim.abc_fade_out);
//            animFadeOutToIn.setDuration(250);
//            animFadeOutToIn.setFillEnabled(true);
//            animFadeOutToIn.setFillAfter(true);
//            animFadeOutToIn.setAnimationListener(new Animation.AnimationListener()
//            {
//                @Override
//                public void onAnimationStart(Animation animation)
//                {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation)
//                {
//                    txtvSignInHeading.startAnimation(animFadeIn);
//                    imgvLogo.startAnimation(animFadeIn);
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation)
//                {
//
//                }
//            });
//
//            //Animate layout hide other fields and controls and show reset pass word views
//            txtvForgotPasswoordHeading.startAnimation(animFadeOutToIn);
//            txtvDescForgotPassword.startAnimation(animFadeOutToIn);

            txtvSignInHeading.setAlpha(0f);
            txtvSignInHeading.setVisibility(View.VISIBLE);

            imgvLogo.setAlpha(0f);
            imgvLogo.setVisibility(View.VISIBLE);

            txtvSignInHeading.animate().alpha(1f).setDuration(300).setListener(null);
            imgvLogo.animate().alpha(1f).setDuration(300).setListener(null);

            txtvForgotPasswoordHeading.animate().alpha(0f).setDuration(300).setListener(new Animator.AnimatorListener()
            {

                @Override
                public void onAnimationStart(Animator animator)
                {

                }

                @Override
                public void onAnimationEnd(Animator animator)
                {
                    txtvForgotPasswoordHeading.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator)
                {

                }

                @Override
                public void onAnimationRepeat(Animator animator)
                {

                }

            });

            txtvDescForgotPassword.animate().alpha(0f).setDuration(300).setListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animator)
                {

                }

                @Override
                public void onAnimationEnd(Animator animator)
                {
                    txtvDescForgotPassword.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator)
                {

                }

                @Override
                public void onAnimationRepeat(Animator animator)
                {

                }
            });

//            Animation animSlideFieldUp = AnimationUtils.loadAnimation(activity, R.anim.slide_field_up);
//            animSlideFieldUp.setFillEnabled(true);
//            animSlideFieldUp.setFillAfter(true);
//            animSlideFieldUp.setAnimationListener(new Animation.AnimationListener()
//            {
//                @Override
//                public void onAnimationStart(Animation animation)
//                {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation)
//                {
//                    //UnHide password field and controls
//                    animFadeIn.setDuration(100);
//                    relLayPassword.startAnimation(animFadeIn);
//                    cbStaySignedIn.startAnimation(animFadeIn);
//                    txtvForgotPassword.startAnimation(animFadeIn);
//                    linLayCreateAccount.startAnimation(animFadeIn);
//                    vDiv2.startAnimation(animFadeIn);
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation)
//                {
//
//                }
//            });
//
//            relLayEmail.startAnimation(animSlideFieldUp);

            relLayEmail.animate().translationY(0f).start();

            //UnHide password field and controls
            relLayPassword.animate().alpha(1f).setDuration(300).start();
            relLayPassword.setVisibility(View.VISIBLE);
            cbStaySignedIn.animate().alpha(1f).setDuration(300).start();
            cbStaySignedIn.setVisibility(View.VISIBLE);
            txtvForgotPassword.animate().alpha(1f).setDuration(300).start();
            txtvForgotPassword.setVisibility(View.VISIBLE);
            linLayCreateAccount.animate().alpha(1f).setDuration(300).start();
            linLayCreateAccount.setVisibility(View.VISIBLE);
            vDiv2.animate().alpha(1f).setDuration(300).start();
            vDiv2.setVisibility(View.VISIBLE);

            //Change button text
            btnSignIn.setText(R.string.btnFSSignin);
        }
    }
}
