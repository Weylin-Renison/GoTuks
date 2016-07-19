package com.gometro.gotuks;

import android.content.Context;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

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
                    Toast.makeText(activity, "Create User", Toast.LENGTH_LONG).show();
                }

            }
        });
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
