package com.gometro.gotuks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import cz.msebera.android.httpclient.Header;

/**
 * Created by wprenison on 2016/07/13.
 */
public class FragCreateAccount extends Fragment
{
    //Views
    EditText etxtFullName;
    RelativeLayout relLayFullName;
    EditText etxtPhone;
    RelativeLayout relLayPhone;
    EditText etxtEmail;
    RelativeLayout relLayEmail;
    EditText etxtPassword;
    RelativeLayout relLayPassword;
    CheckBox cbPasswordVis;
    Button btnSignUp;
    TextView txtvSignIn;
    ProgressBar pbProgressCircle;

    //vars
    ActivityIntro activity;
    String fullName;
    String phone;
    String email;
    String password;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        //Inflate layout
        View constructedView = inflater.inflate(R.layout.frag_create_account, container, false);

        //Get Handle on views
        etxtFullName = (EditText) constructedView.findViewById(R.id.etxtFCAFullName);
        relLayFullName = (RelativeLayout) constructedView.findViewById(R.id.relLayFCAFullName);
        etxtPhone = (EditText) constructedView.findViewById(R.id.etxtFCAPhone);
        relLayPhone = (RelativeLayout) constructedView.findViewById(R.id.relLayFCAPhone);
        etxtEmail = (EditText) constructedView.findViewById(R.id.etxtFCAEmail);
        relLayEmail = (RelativeLayout) constructedView.findViewById(R.id.relLayFCAEmail);
        etxtPassword = (EditText) constructedView.findViewById(R.id.etxtFCAPassword);
        relLayPassword = (RelativeLayout) constructedView.findViewById(R.id.relLayFCAPassword);
        cbPasswordVis = (CheckBox) constructedView.findViewById(R.id.cbFCAPasswordVisibility);
        btnSignUp = (Button) constructedView.findViewById(R.id.btnFCASignUp);
        txtvSignIn = (TextView) constructedView.findViewById(R.id.txtvFCASignIn);
        pbProgressCircle = (ProgressBar) constructedView.findViewById(R.id.pgFCAProgressCircle);

        return constructedView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        //Get Activity context
        activity = (ActivityIntro) getActivity();

        //Set listners to click anywhere in the row and focus the text field
        relLayFullName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                etxtFullName.requestFocus();
            }
        });

        relLayPhone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                etxtPhone.requestFocus();
            }
        });

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

        //Set listener when password vis is clicked to switch the password field visibility
        cbPasswordVis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked)
            {
                if (checked)
                    etxtPassword.setTransformationMethod(null);
                else
                    etxtPassword.setTransformationMethod(new PasswordTransformationMethod());
            }
        });

        txtvSignIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Move to sign in screen
                activity.navCreateAccountToSignin();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Get Data
                fullName = etxtFullName.getText().toString();
                phone = etxtPhone.getText().toString();
                email = etxtEmail.getText().toString();
                password = etxtPassword.getText().toString();

                //Validate Data
                boolean valid = validateFields();

                if(valid)
                {
                    //Create user
                    //Get url
                    SharedPreferences prefMang = PreferenceManager.getDefaultSharedPreferences(activity);
                    String SERVER_ADDRESS = prefMang.getString("SERVER_ADDRESS", null);
                    String SERVER_API_CREATE_ACCOUNT = prefMang.getString("SERVER_API_CREATE_ACCOUNT", null);
                    String SERVER_SECRET_KEY = prefMang.getString("SERVER_SECRET_KEY", null);
                    String SERVER_API_ORG_CODE = prefMang.getString("SERVER_API_ORG_CODE", null);


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
                    params.put("fullName", fullName);
                    params.put("phone", phone);
                    params.put("email", email);
                    params.put("password", cryptoPassword);
                    params.put("organizationCode", SERVER_API_ORG_CODE);

                    btnSignUp.setText("");
                    pbProgressCircle.setVisibility(View.VISIBLE);

                    AsyncHttpClient client = new AsyncHttpClient();
                    client.setUserAgent("android");
                    client.post(activity, SERVER_ADDRESS + SERVER_API_CREATE_ACCOUNT, params, new AsyncHttpResponseHandler()
                    {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
                        {
                            pbProgressCircle.setVisibility(View.GONE);
                            btnSignUp.setText(R.string.btnFCASignup);

                            cacheData();
                            activity.navMainActivity();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
                        {
                            //Check error code
                            if(statusCode == 400)
                            {
                                String responseJson = new String(responseBody);
                                Toast.makeText(activity, responseJson, Toast.LENGTH_LONG).show();

                                pbProgressCircle.setVisibility(View.GONE);
                                btnSignUp.setText(R.string.btnFCASignup);
                            }
                            else if(statusCode == 0)
                            {
                                pbProgressCircle.setVisibility(View.GONE);
                                btnSignUp.setText(R.string.btnFCASignup);
                                Toast.makeText(activity, "Please check your internet connection and try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });
    }

    private void cacheData()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);

        sharedPrefs.edit().putString("cachedEmail", email)
                .putString("cachedPassword", password)
                .putBoolean("staySignedIn", true)
                .putString("cachedPhone", phone)
                .putString("cachedFullName", fullName).commit();
    }

    private boolean validateFields()
    {
        boolean valid = true;

        //Validates data fields

        //Validate email
        if(email.isEmpty())
        {
            etxtEmail.setError("Required Field");
            valid = false;
        }
        else if(!email.endsWith("@tuks.ac.za")) //Reg matching to whatever pattern needed, in this case must be a tuks email
        {
            etxtEmail.setError("Only student emails are accepted. ie. s230097@tuks.ac.za");
            valid = false;
        }

        //Validate no null values
        if(fullName.isEmpty())
        {
            etxtFullName.setError("Required Field");
            valid = false;
        }

        if(phone.isEmpty())
        {
            etxtPhone.setError("Required Field");
            valid = false;
        }

        if(phone.length() < 10)
        {
            etxtPhone.setError("Invalid phone number");
            valid = false;
        }

        if(password.isEmpty())
        {
            etxtPassword.setError("Required Field");
            valid = false;
        }

        //Validate password constraints
        if(password.length() < 6)
        {
            etxtPassword.setError("Password must be at least 6 characters");
            valid = false;
        }

        return valid;
    }
}
