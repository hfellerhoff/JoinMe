package com.joinmedevelopment.joinme;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class LocationReportList extends ArrayAdapter<LocationReport> {

    private Activity context;
    private List<LocationReport> locationReportList;

    public LocationReportList(Activity context, List<LocationReport> locationReportList){
        super(context, R.layout.location_layout, locationReportList);
        this.context = context;
        this.locationReportList = locationReportList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.location_layout, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewLocation = (TextView) listViewItem.findViewById(R.id.textViewLocation);

        LocationReport locationReport = locationReportList.get(position);

        textViewName.setText(locationReport.getName());
        textViewLocation.setText(locationReport.getLocation());

        return listViewItem;
    }
}
