package com.gometro.gotuks;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by wprenison on 14/03/16.
 */
public class AdapterStopSchedule extends ArrayAdapter
{

    private Context context;
    private int layoutRes;
    private String [][] schedules;

    public AdapterStopSchedule(Context context, int resource, String [][] schedules)
    {
        super(context, resource);
        this.context = context;
        this.layoutRes = resource;
        this.schedules = schedules;
    }

    @Override
    public int getCount()
    {
        return schedules.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutRes, parent, false);
        }

        //Get handel on views
        TextView routeNameHeading = (TextView) convertView.findViewById(R.id.txtvISSRouteName);
        TextView scheduleDetail = (TextView) convertView.findViewById(R.id.txtvISSTripTimes);
        Button routeColor = (Button) convertView.findViewById(R.id.btnISSRouteColor);

        //Set route color
        if(Build.VERSION.SDK_INT >= 21)
            routeColor.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(schedules[position][0])));
        else
            routeColor.setBackgroundColor(Color.BLACK);

        //populate data
        routeNameHeading.setText(schedules[position][1]);
        scheduleDetail.setText(schedules[position][2]);

        return convertView;
    }
}
