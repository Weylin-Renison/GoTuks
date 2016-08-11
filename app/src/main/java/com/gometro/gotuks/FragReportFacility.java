package com.gometro.gotuks;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by wprenison on 2016/08/08.
 */
public class FragReportFacility extends Fragment
{

    //Views
    private EditText etxtDescription;
    private RelativeLayout relLayDescription;
    private RelativeLayout relLayPhoto;
    private TextView txtvAddPhoto;
    private ImageView imgvPhotoPreview;
    public TextView txtvLocation;
    private RelativeLayout relLayLocation;
    private Button btnSendReport;

    //Vars
    private ActivityDisplayStream activity;
    private Bitmap photo;
    public double locLat;
    public double locLon;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View constructedView = inflater.inflate(R.layout.frag_report_facility, container, false);

        //Get handle on views
        etxtDescription = (EditText) constructedView.findViewById(R.id.etxtFRFDescription);
        relLayDescription = (RelativeLayout) constructedView.findViewById(R.id.relLayFRFDescription);
        relLayPhoto = (RelativeLayout) constructedView.findViewById(R.id.relLayFRFPhoto);
        txtvAddPhoto = (TextView) constructedView.findViewById(R.id.txtvFRFAddPhoto);
        imgvPhotoPreview = (ImageView) constructedView.findViewById(R.id.imgvFRFPhotoPreview);
        txtvLocation = (TextView) constructedView.findViewById(R.id.txtvFRFLocation);
        relLayLocation = (RelativeLayout) constructedView.findViewById(R.id.relLayFRFLocation);
        btnSendReport = (Button) constructedView.findViewById(R.id.btnFRFSendReport);

        return constructedView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        activity = (ActivityDisplayStream) getActivity();

        //Set click listeners for wrapping layouts, so the user can click anywhere in the row
        relLayDescription.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                etxtDescription.requestFocus();
            }
        });

        relLayPhoto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                txtvAddPhoto.performClick();
            }
        });

        imgvPhotoPreview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                txtvAddPhoto.performClick();
            }
        });

        relLayLocation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                    txtvLocation.performClick();
            }
        });

        //Set click listeners for user interaction
        txtvAddPhoto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Allow user to take a photo and remember the uri also set the image preview of the photo
                Intent intentCapturePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intentCapturePhoto, 0);
            }
        });

        txtvLocation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Allow user to select a location from either a map or searching an address
                FragDiagSelectLocation fragDiagSelectlocation = new FragDiagSelectLocation();
                fragDiagSelectlocation.show(activity.getSupportFragmentManager(), "DiagFragSelectLoc");
            }
        });

        btnSendReport.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(validate())
                {
                    //Collect all required data
                    String description = etxtDescription.getText().toString().trim();
                    String location = txtvLocation.getText().toString().trim();

                    //Make post request and upload the report to server
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
                    String SERVER_ADDRESS = sharedPrefs.getString("SERVER_ADDRESS", "");
                    String SERVER_API_UPLOAD_REPORT = sharedPrefs.getString("SERVER_API_UPLOAD_REPORT", "");

                    RequestParams params = new RequestParams();
                    params.put("description", description);
                    params.put("photo", photo);
                    params.put("locText", location);
                    params.put("locLat", locLat);
                    params.put("locLon", locLon);

                    AsyncHttpClient client = new AsyncHttpClient();
                    client.post(activity, SERVER_ADDRESS + SERVER_API_UPLOAD_REPORT, params, new AsyncHttpResponseHandler()
                    {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
                        {
                            Toast.makeText(activity, "Report Sent", Toast.LENGTH_LONG).show();

                            //Clear all data fields if successful
                            etxtDescription.setText("");
                            imgvPhotoPreview.setImageBitmap(null);
                            txtvLocation.setText("Select Location");
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
                                    Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();

                                } catch(JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            else if(statusCode == 0)
                            {
                                Toast.makeText(activity, "Please check your internet connection and try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

    }

    private boolean validate()
    {
        boolean valid = true;

        //check description is not empty
        if(etxtDescription.getText().toString().trim().isEmpty())
        {
            valid = false;
            etxtDescription.setError("Required");
        }

        //Check location is selected
        if(txtvLocation.getText().toString().trim().equalsIgnoreCase("Select Location"))
        {
            valid = false;
            txtvLocation.setError("Required");
        }

        return valid;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        //Capture Camera
        if(requestCode == 0)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                //Set image preview
                photo = (Bitmap) data.getExtras().get("data");
                imgvPhotoPreview.setImageBitmap(photo);
            }
        }
    }
}
