package com.gometro.gotuks;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wprenison on 2016/07/27.
 */
public class AdapterAnnouncements extends BaseExpandableListAdapter
{

    //Vars
    Activity context;
    LayoutInflater inflater;
    List<String> lstHeadlines = new ArrayList<String>();
    List<String> lstAnnouncments = new ArrayList<String>();

    public AdapterAnnouncements(Activity context, LayoutInflater inflater, List<String> lstHeadlines, List<String> lstAnnouncments)
    {
        this.context = context;
        this.inflater = inflater;
        this.lstHeadlines = lstHeadlines;
        this.lstAnnouncments = lstAnnouncments;
    }

    @Override
    public int getGroupCount()
    {
        return lstHeadlines.size();
    }

    @Override
    public int getChildrenCount(int i)
    {
        return 1;
    }

    @Override
    public Object getGroup(int i)
    {
        return lstHeadlines.get(i);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return lstAnnouncments.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup viewGroup)
    {
        //Inflate Group View
        if(view == null)
        {
            view = inflater.inflate(R.layout.item_headline, viewGroup, false);
        }

        //Get handle on views
        CheckedTextView txtvHeadline = (CheckedTextView) view.findViewById(R.id.txtvIteamHeadline);
        txtvHeadline.setChecked(isExpanded);

        //populate data
        txtvHeadline.setText(lstHeadlines.get(groupPosition));

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup)
    {
        //inflate child view
        if(view == null)
        {
            view = inflater.inflate(R.layout.item_announcement, viewGroup, false);
        }

        //Get Handel on views
        TextView txtvAnnouncement = (TextView) view.findViewById(R.id.txtvItemAnouncement);

        //Populate data
        txtvAnnouncement.setText(lstAnnouncments.get(i));

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1)
    {
        return false;
    }
}
