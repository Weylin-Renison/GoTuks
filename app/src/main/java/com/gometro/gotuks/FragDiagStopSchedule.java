package com.gometro.gotuks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by wprenison on 14/03/16.
 */
public class FragDiagStopSchedule extends android.support.v4.app.DialogFragment
{
    private ActivityDisplayStream activity;
    private TextView txtvStopName;
    private ListView lstvSchedules;
    private int stopIndex;
    private GeoJsonPOI geoJsonStop;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View constructedView = inflater.inflate(R.layout.diag_stop_schedule, container, false);

        //Get handel on views
        txtvStopName = (TextView) constructedView.findViewById(R.id.txtvDiagFragStopName);
        lstvSchedules = (ListView) constructedView.findViewById(R.id.lstvDiagFragStopSchedule);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return constructedView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().getAttributes().windowAnimations = R.style.dialogWindowAnimations;

        this.activity = (ActivityDisplayStream) getActivity();

        stopIndex = getArguments().getInt("stopIndex");
        geoJsonStop = activity.lstPoiGeoJsonMarkers.get(stopIndex);

        //Set stop name
        txtvStopName.setText(geoJsonStop.getTitle());

        //populate list view with schedule data
        String[][] schedules = geoJsonStop.getSchedules();
        lstvSchedules.setAdapter(new AdapterStopSchedule(activity, R.layout.item_stop_schedule, schedules));

    }
}
