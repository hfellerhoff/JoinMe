package com.joinmedevelopment.joinme;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
    public void onBindViewHolder(@NonNull final LocationReportViewHolder locationReportViewHolder, final int i) {
        LocationReport report = reports.get(i);
        locationReportViewHolder.textViewName.setText(report.getName());
        locationReportViewHolder.textViewLocation.setText(report.getLocation());
        locationReportViewHolder.textViewTime.setText(getTimePassed(report.getTimeCreated()));
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



