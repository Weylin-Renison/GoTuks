package com.gometro.gotuks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.rtp.RtpStream;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by wprenison on 2016/07/22.
 */
public class FragProfile extends Fragment
{

    //Views
    ProgressBar pbProgressCircle;
    TextView txtvFullName;
    TextView txtvAddress;
    EditText etxtFullName;
    CheckBox cbFullName;
    EditText etxtPhone;
    CheckBox cbPhone;
    EditText etxtEmail;
    CheckBox cbEmail;
    EditText etxtPassword;
    CheckBox cbPassword;
    EditText etxtAddress;
    CheckBox cbAddress;
    Button btnSignout;

    //Vars
    ActivityDisplayStream activity;
    InputMethodManager keyboard;

    //Cache var for editing of vields
    private SharedPreferences sharedPrefs;
    private String cachedFullName;
    private String cachedPhone;
    private String cachedEmail;
    private String cachedPassword;
    private String cachedAddress;

    private boolean doneWasUsed = false;    //Keeps track if done button on keyboard was used to finish editing field or cancel button

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View constructedView = inflater.inflate(R.layout.frag_profile, container, false);

        //Get handle on views
        pbProgressCircle = (ProgressBar) constructedView.findViewById(R.id.pgFPProgressCircle);
        txtvFullName = (TextView) constructedView.findViewById(R.id.txtvFPSetFullName);
        txtvAddress = (TextView) constructedView.findViewById(R.id.txtvFPSetAddress);
        etxtFullName = (EditText) constructedView.findViewById(R.id.etxtFPFullName);
        cbFullName = (CheckBox) constructedView.findViewById(R.id.cbFPEditFullName);
        etxtPhone = (EditText) constructedView.findViewById(R.id.etxtFPPhone);
        cbPhone = (CheckBox) constructedView.findViewById(R.id.cbFPEditPhone);
        etxtEmail = (EditText) constructedView.findViewById(R.id.etxtFPEmail);
        cbEmail = (CheckBox) constructedView.findViewById(R.id.cbFPEditEmail);
        etxtPassword = (EditText) constructedView.findViewById(R.id.etxtFPPassword);
        cbPassword = (CheckBox) constructedView.findViewById(R.id.cbFPEditPassword);
        etxtAddress = (EditText) constructedView.findViewById(R.id.etxtFPAddress);
        cbAddress = (CheckBox) constructedView.findViewById(R.id.cbFPEditAddress);
        btnSignout = (Button) constructedView.findViewById(R.id.btnFPSignOut);

        return constructedView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        activity = (ActivityDisplayStream) getActivity();

        //get Handel on shared prefs
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);

        init();
        initEditCheckboxes();
    }

    private void init()
    {

        //Check for cahced values in order to not make uneeded api requests
        cachedFullName = sharedPrefs.getString("cachedFullName", "");
        cachedPhone = sharedPrefs.getString("cachedPhone", "");
        cachedEmail = sharedPrefs.getString("cachedEmail", "");
        cachedPassword = sharedPrefs.getString("cachedPassword", "");
        cachedAddress = sharedPrefs.getString("cachedAddress", "No Address Listed");

        if(cachedAddress.isEmpty() || cachedAddress.equalsIgnoreCase("null"))
            cachedAddress = "No Address Listed";

        if(cachedFullName.isEmpty())
        {
            //Api request for user details
        }

        //populate fields
        txtvFullName.setText(cachedFullName);
        txtvAddress.setText(cachedAddress);
        etxtFullName.setText(cachedFullName);
        etxtPhone.setText(cachedPhone);
        etxtEmail.setText(cachedEmail);
        etxtPassword.setText(cachedPassword);
        etxtAddress.setText(cachedAddress);

        btnSignout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //TODO: Clear tokens

                //Clear cached data TODO: Update clear cached values on signout as new fields are added
                sharedPrefs.edit().remove("cachedPassword").remove("cachedFullName").remove("cachedPhone").remove("cachedAddress").commit();

                //Move to Intro Activity
                Intent intentIntro = new Intent(activity, ActivityIntro.class);
                activity.startActivity(intentIntro);
                activity.finish();
            }
        });
    }

    private void initEditCheckboxes()
    {
        keyboard = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        //Set listners for each edit checkbox
        cbFullName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked)
            {
                if(checked)
                {
                    clearAllOtherEditChecks(cbFullName);
                    etxtFullName.setEnabled(true);
                    etxtFullName.requestFocus();
                    etxtFullName.setSelection(cachedFullName.length());
                    keyboard.showSoftInput(etxtFullName, InputMethodManager.SHOW_IMPLICIT);
                }
                else
                {
                    if(!doneWasUsed)
                        etxtFullName.setText(cachedFullName);
                    else
                        doneWasUsed = false; //reset

                    etxtFullName.setEnabled(false);
                }
            }
        });

        cbPhone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked)
            {
                if(checked)
                {
                    clearAllOtherEditChecks(cbPhone);
                    etxtPhone.setEnabled(true);
                    etxtPhone.requestFocus();
                    etxtPhone.setSelection(cachedPhone.length());
                    keyboard.showSoftInput(etxtPhone, InputMethodManager.SHOW_IMPLICIT);
                }
                else
                {
                    if(!doneWasUsed)
                        etxtPhone.setText(cachedPhone);
                    else
                        doneWasUsed = false; //reset

                    etxtPhone.setEnabled(false);

                }
            }
        });

        cbEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked)
            {
                if(checked)
                {
                    clearAllOtherEditChecks(cbEmail);
                    etxtEmail.setEnabled(true);
                    etxtEmail.requestFocus();
                    etxtEmail.setSelection(cachedEmail.length());
                    keyboard.showSoftInput(etxtEmail, InputMethodManager.SHOW_IMPLICIT);
                }
                else
                {
                    if(!doneWasUsed)
                        etxtEmail.setText(cachedEmail);
                    else
                        doneWasUsed = false;

                    etxtEmail.setEnabled(false);
                }
            }
        });

        cbPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked)
            {
                if(checked)
                {
                    clearAllOtherEditChecks(cbPassword);
                    etxtPassword.setEnabled(true);
                    etxtPassword.requestFocus();
                    keyboard.showSoftInput(etxtPassword, InputMethodManager.SHOW_IMPLICIT);

                    //Dont show password with mask
                    etxtPassword.setTransformationMethod(null);
                    etxtPassword.setSelection(cachedPassword.length());
                }
                else
                {
                    if(!doneWasUsed)
                        etxtPassword.setText(cachedPassword);
                    else
                        doneWasUsed = false; //reset

                    etxtPassword.setEnabled(false);

                    //Re alppy mask
                    etxtPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        cbAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked)
            {
                if(checked)
                {
                    clearAllOtherEditChecks(cbAddress);
                    etxtAddress.setEnabled(true);
                    etxtAddress.requestFocus();
                    etxtAddress.setSelection(cachedAddress.length());
                    keyboard.showSoftInput(etxtAddress, InputMethodManager.SHOW_IMPLICIT);
                }
                else
                {
                    if(!doneWasUsed)
                        etxtAddress.setText(cachedAddress);
                    else
                        doneWasUsed = false; //reset

                    etxtAddress.setEnabled(false);
                }
            }
        });

        //Set keyboard done listeners for each edit text
        etxtFullName.setOnEditorActionListener(new EditText.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
            {
                //Update server validation done in update server method
                updateServerUser("fullName");

                //On Success
                    //Set view from start of text
                    etxtFullName.setSelection(0);
                    //Lock field
                    doneWasUsed = true;
                    cbFullName.setChecked(false);
                return false;
            }
        });

        etxtPhone.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
            {
                //Update server validation done in update server method
                updateServerUser("phone");

                //On Success
                    //Set view from start of text
                    etxtPhone.setSelection(0);
                    //Lock field
                    doneWasUsed = true;
                    cbPhone.setChecked(false);
                return false;
            }
        });

        etxtEmail.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
            {
                //Update server validation done in update server method
//                updateServerUser("");

                //On Success
                    //Set view from start of text
                    etxtEmail.setSelection(0);
                    //Update Cached value
                    cachedEmail = etxtEmail.getText().toString();
                    sharedPrefs.edit().putString("cachedEmail", cachedEmail).commit();
                    //Lock field
                    doneWasUsed = true;
                    cbEmail.setChecked(false);
                return false;
            }
        });

        etxtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
            {
                //Update server validation done in update server method
                updateServerUser("password");

                //On Success
                    //Set view from start of text
                    etxtPassword.setSelection(0);
                    //Lock field
                    doneWasUsed = true;
                    cbPassword.setChecked(false);
                return false;
            }
        });

        etxtAddress.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
            {
                //Update server validation done in update server method
                updateServerUser("address");

                //On Success
                    //Set view from start of text
                    etxtAddress.setSelection(0);
                    //Lock field
                    doneWasUsed = true;
                    cbAddress.setChecked(false);
                return false;
            }
        });

    }

    private void clearAllOtherEditChecks(CheckBox checkToIgnore)
    {
        int ignoreId = checkToIgnore.getId();

        if(cbFullName.getId() != ignoreId)
            cbFullName.setChecked(false);

        if(cbPhone.getId() != ignoreId)
            cbPhone.setChecked(false);

        if(cbEmail.getId() != ignoreId)
            cbEmail.setChecked(false);

        if(cbPassword.getId() != ignoreId)
            cbPassword.setChecked(false);

        if(cbAddress.getId() != ignoreId)
            cbAddress.setChecked(false);
    }

    private void updateServerUser(String fieldToUpdate)
    {
        RequestParams params = new RequestParams();
        params.put("email", cachedEmail);

        //Check which field to update
        switch(fieldToUpdate)
        {
            case "fullName":
                params.put("fullName", etxtFullName.getText().toString().trim());
                break;

            case "phone":
                //Validate more than 10 digits
                String newPhone = etxtPhone.getText().toString();
                if(newPhone.length() < 10)
                    etxtPhone.setError("Invalid phone number");
                else
                    params.put("phone", newPhone);
                break;

            case "password":
                //Validate password
                String newPassword = etxtPassword.getText().toString();
                if(newPassword.length() < 6)
                {
                    etxtPassword.setError("Password must be at least 6 characters");
                }
                else
                {
                    //Hash password with server secret
                    HashHelper hashHlp = HashHelper.getInstance();
                    String secretKey = sharedPrefs.getString("SERVER_SECRET_KEY", null);
                    try
                    {
                        String newPasswordHash = hashHlp.hmacSha1(newPassword, secretKey);
                        params.put("password", newPasswordHash);
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
                }

                break;

            case "address":
                params.put("address", etxtAddress.getText().toString().trim());
                break;
        }

        //Get endpoint
        final String SERVER_ADDRESS = sharedPrefs.getString("SERVER_ADDRESS", null);
        final String SERVER_API_UPDATE_USER = sharedPrefs.getString("SERVER_API_UPDATE_USER", null);

        //Display loading feedback
        pbProgressCircle.setVisibility(View.VISIBLE);

        //Make update post call
        AsyncHttpClient client = new AsyncHttpClient();
        client.setUserAgent("android");
        client.post(activity, SERVER_ADDRESS + SERVER_API_UPDATE_USER, params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                //Hide progress circle
                pbProgressCircle.setVisibility(View.GONE);
                recacheData();
                Toast.makeText(activity, "Updated successfully", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                pbProgressCircle.setVisibility(View.GONE);

                //Check errors
                if(statusCode == 400)
                {
                    String responseJson = new String(responseBody);
                    Toast.makeText(activity, responseJson, Toast.LENGTH_LONG).show();
                }
                else if(statusCode == 0)
                {
                    pbProgressCircle.setVisibility(View.GONE);
                    Toast.makeText(activity, "Please check your internet connection and try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void recacheData()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);

        cachedFullName = etxtFullName.getText().toString().trim();
        cachedPhone = etxtPhone.getText().toString();
        cachedPassword = etxtPassword.getText().toString();
        cachedAddress = etxtAddress.getText().toString().trim();

        sharedPrefs.edit().putString("cachedFullName", cachedFullName)
                .putString("cachedPhone", cachedPhone)
                .putString("cachedPassword", cachedPassword)
                .putString("cachedAddress", cachedAddress).commit();
    }
}
