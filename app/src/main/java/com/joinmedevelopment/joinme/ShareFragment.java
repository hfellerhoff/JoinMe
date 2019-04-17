package com.joinmedevelopment.joinme;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ShareFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference databaseReports;
    DatabaseReference databaseUsers;

    // UI Elements
    Button buttonSubmit;
    Spinner spinnerWhere;

    public boolean locationReportSubmitted;
    private String locationReportID = "";

    //constructors
    public ShareFragment() {

    }


    public static ShareFragment newInstance(String param1, String param2) {
        ShareFragment fragment = new ShareFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //create UI for Share
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReports = FirebaseDatabase.getInstance().getReference("reports");
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        // Update submit button UI based on if report has been submitted
        if (currentUser != null) {
            databaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    HashMap<String, Object> hashMap = (HashMap)dataSnapshot.child(currentUser.getUid()).getValue();
                    if (hashMap != null) {
                        locationReportSubmitted = (Boolean)hashMap.get("reportSubmitted");
                    }
                    else {
                        locationReportSubmitted = false;
                    }

                    updateUI();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_share, container, false);

        buttonSubmit = (Button) view.findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocationReport();
            }
        });

        spinnerWhere = (Spinner) view.findViewById(R.id.spinnerWhere);
        updateUI();
        return view;
    }

    //changes the location of a user
    private void updateLocationReport() {
        if (locationReportSubmitted) {
            deleteLocationReport();
        }
        else {
            addLocationReport();
        }
    }

    //checks a user into a location
    private void addLocationReport() {
        String name = currentUser.getDisplayName();
        String location = spinnerWhere.getSelectedItem().toString();

        locationReportID = databaseReports.push().getKey();

        new LocationReport(locationReportID, name, location);

        locationReportSubmitted = true;
        updateUI();
    }

    //checks a user out of a location
    public void deleteLocationReport() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (locationReportSubmitted) {
            databaseReports.child(currentUser.getUid()).removeValue();
            databaseUsers.child(currentUser.getUid()).child("reportSubmitted").setValue(false);
            databaseUsers.child(currentUser.getUid()).child("reportID").setValue(null);
        }

        locationReportSubmitted = false;
        updateUI();
    }

    //updates UI when status of location report changes
    private void updateUI() {
        if (locationReportSubmitted) {
            buttonSubmit.setText(R.string.button_check_out);

            databaseReports.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(LocationReport.class) != null) {
                        String location = dataSnapshot.getValue(LocationReport.class).getLocation();
                        String[] locations = getResources().getStringArray(R.array.locations);

                        int i = 0;
                        for (String arrayLocation : locations) {
                            if (location.equals(arrayLocation))
                                spinnerWhere.setSelection(i);
                            else
                                i++;

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            spinnerWhere.setEnabled(false);
        }
        else {
            buttonSubmit.setText(R.string.button_check_in);
            spinnerWhere.setEnabled(true);
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    //returns true if the user has checked in
    public void setLocationReportSubmitted(boolean locationReportSubmitted) {
        this.locationReportSubmitted = locationReportSubmitted;
    }
}
