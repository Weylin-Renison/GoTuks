package com.gometro.gotuks;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.disklrucache.Util;

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
            }
        });

        relLayPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                etxtPassword.requestFocus();
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
                String btnState = btnSignIn.getText().toString();

                //Get data values
                email = etxtEmail.getText().toString().trim();
                password = etxtPassword.getText().toString();

                //Validte data
                if(btnState.equalsIgnoreCase("Sign In"))
                {
                    if(validateFields(false))
                    {
                        //Authenticate User
                        Toast.makeText(activity, "Auth User", Toast.LENGTH_LONG).show();
                        activity.navMainActivity();
                    }

                } else
                {
                    if(validateFields(true))
                    {
                        //Request password reset
                        Toast.makeText(activity, "Req pass reset", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
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
        } else if(!email.endsWith("@tuks.ac.za"))
        {
            etxtEmail.setError("Only university emails are accepted");
            valid = false;
        }

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
