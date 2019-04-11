package com.joinmedevelopment.joinme;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class LocationReportAdapter extends RecyclerView.Adapter<LocationReportAdapter.LocationReportViewHolder>{

    private static ArrayList<LocationReport> reports;
    // private final OnListFragmentInteractionListener mListener;

    public static class LocationReportViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView textViewName;
        TextView textViewLocation;
        TextView textViewTime;

        LocationReportViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardView);
            textViewName = (TextView) itemView.findViewById(R.id.textViewFriendName);
            textViewLocation = (TextView) itemView.findViewById(R.id.textViewLocation);
            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
        }
    }

    public LocationReportAdapter(final ArrayList<LocationReport> reports) {
        this.reports = reports;

    }

    @NonNull
    @Override
    public LocationReportViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item, viewGroup, false);
        LocationReportViewHolder holder = new LocationReportViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocationReportViewHolder locationReportViewHolder, int i) {
        locationReportViewHolder.textViewName.setText(reports.get(i).getName());
        locationReportViewHolder.textViewLocation.setText(reports.get(i).getLocation());
        locationReportViewHolder.textViewTime.setText(getTimePassed(reports.get(i).getTimeCreated()));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public String getTimePassed(long timeCreated) {
        long timePassed = (System.currentTimeMillis() - timeCreated) / 1000;
        String timePassedString = "Just now";

        if (timePassed > 0) {
            timePassedString = "second";

            if (timePassed >= 60) {
                timePassed /= 60;
                timePassedString = "minute";

                if (timePassed >= 60) {
                    timePassedString = "hour";
                    timePassed /= 60;

                    if (timePassed >= 24) {
                        timePassedString = "day";
                        timePassed /= 24;
                    }
                }
            }

            if (timePassed > 1)
                timePassedString += "s";

            timePassedString += " ago";

            timePassedString = Long.toString(timePassed) + " " + timePassedString;
        }

        return timePassedString;
    }

}



