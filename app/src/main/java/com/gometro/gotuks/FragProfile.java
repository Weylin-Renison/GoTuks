package com.gometro.gotuks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by wprenison on 2016/07/22.
 */
public class FragProfile extends Fragment
{

    //Views
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
        cachedAddress = sharedPrefs.getString("cachedAddress", "");

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
                //Validate

                //Update server

                //On Success
                    //Set view from start of text
                    etxtFullName.setSelection(0);
                    //Update cached value
                    cachedFullName = etxtFullName.getText().toString();
                    sharedPrefs.edit().putString("cachedFullName", cachedFullName).commit();
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
                //Validate

                //Update server

                //On Success
                    //Set view from start of text
                    etxtPhone.setSelection(0);
                    //Update cached value
                    cachedPhone = etxtPhone.getText().toString();
                    sharedPrefs.edit().putString("cachedPhone", cachedPhone).commit();
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
                //Validate

                //Update server

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
                //Validate

                //Update server

                //On Success
                    //Set view from start of text
                    etxtPassword.setSelection(0);
                    //Update cached value
                    cachedPassword = etxtPassword.getText().toString();
                    sharedPrefs.edit().putString("cachedPassword", cachedPassword).commit();
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
                //Validate

                //Update server

                //On Success
                    //Set view from start of text
                    etxtAddress.setSelection(0);
                    //Updated cached value
                    cachedAddress = etxtAddress.getText().toString();
                    sharedPrefs.edit().putString("cachedAddress", cachedAddress).commit();
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
}
