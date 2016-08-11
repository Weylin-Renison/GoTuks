package com.gometro.gotuks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.internal.widget.ActionBarOverlayLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by wprenison on 2016/07/27.
 */
public class FragDiagAnnouncements extends DialogFragment
{

    //Views
    private ProgressBar pbLoadingCircle;
    private TextView txtvNoAnnouncements;
    private ExpandableListView elstvAnnouncments;
    private Button btnGotIt;

    //Vars
    private ActivityDisplayStream activity;
    private List<String> lstHeadlines;
    private List<String> lstAnnouncements;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View constructedView = inflater.inflate(R.layout.diag_frag_announcements, container, false);

        //Get Handle on views
        txtvNoAnnouncements = (TextView) constructedView.findViewById(R.id.txtvDFANone);
        elstvAnnouncments = (ExpandableListView) constructedView.findViewById(R.id.elstvDFAAnnouncements);
        elstvAnnouncments.setGroupIndicator(null);
        elstvAnnouncments.setClickable(true);

        btnGotIt = (Button) constructedView.findViewById(R.id.btnDFAGotIt);
        pbLoadingCircle = (ProgressBar) constructedView.findViewById(R.id.pbDFAProgCircle);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return constructedView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().getAttributes().windowAnimations = R.style.dialogWindowAnimations;

        this.activity = (ActivityDisplayStream) getActivity();

        apiRequestAnnouncments();

        btnGotIt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getDialog().dismiss();
            }
        });
    }

    private void apiRequestAnnouncments()
    {
        //Get url
        SharedPreferences prefMang = PreferenceManager.getDefaultSharedPreferences(activity);
        String SERVER_ADDRESS = prefMang.getString("SERVER_ADDRESS", null);
        String SERVER_API_ANNOUNCMENTS = prefMang.getString("SERVER_API_ANNOUNCMENTS", null);

        //Show loading circle
        pbLoadingCircle.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(activity, SERVER_ADDRESS + SERVER_API_ANNOUNCMENTS, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                //Read Json data into array lists
                String jsonStringResponse = new String(responseBody);

                try
                {
                    JSONArray jsonArrayAnnouncements = new JSONArray(jsonStringResponse);

                    lstHeadlines = new ArrayList<String>();
                    lstAnnouncements = new ArrayList<String>();

                    for(int i = 0; i < jsonArrayAnnouncements.length(); i++)
                    {
                        JSONObject jsonObjectAnnouncement = jsonArrayAnnouncements.getJSONObject(i);
                        lstHeadlines.add(jsonObjectAnnouncement.getString("headline"));
                        lstAnnouncements.add(jsonObjectAnnouncement.getString("msg"));
                    }

                    //Hide loading circle
                    pbLoadingCircle.setVisibility(View.GONE);
                    populateAnnouncementList();



                } catch(JSONException e)
                {
                    e.printStackTrace();
                }
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

                getDialog().dismiss();
            }
        });

    }

    private void populateAnnouncementList()
    {
        if(lstHeadlines != null  && lstAnnouncements != null)
        {
            if(lstHeadlines.size() > 0)
            {
                LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                AdapterAnnouncements adapter = new AdapterAnnouncements(activity, inflater, lstHeadlines, lstAnnouncements);

                elstvAnnouncments.setAdapter(adapter);
            }
            else
            {
                elstvAnnouncments.setVisibility(View.INVISIBLE);
                txtvNoAnnouncements.setVisibility(View.VISIBLE);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, txtvNoAnnouncements.getId());
                btnGotIt.setLayoutParams(params);
            }
        }
    }

    private List<String> setFakeDataHeadlines()
    {
        List<String> lstHeadlines = new ArrayList<String>();
        lstHeadlines.add("Headline 1");
        lstHeadlines.add("Headline 2");
        lstHeadlines.add("Headline 3");
        return lstHeadlines;
    }

    private List<String> setFakeDataAnnouncements()
    {
        List<String> lstAnnouncements = new ArrayList<String>();
        lstAnnouncements.add("Announcement 1: All exams suspended until further notice, due to riots");
        lstAnnouncements.add("Announcement 2: Exams venues are now ready, please check listing at hall entrance for your seat & row number");
        lstAnnouncements.add("Announcement 3: Campus security will check every student's id before admission to exam venues, please ensure you have your ID with you.");
        return lstAnnouncements;
    }
}
