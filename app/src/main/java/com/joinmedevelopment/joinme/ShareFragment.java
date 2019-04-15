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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShareFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShareFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShareFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
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

    public ShareFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShareFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShareFragment newInstance(String param1, String param2) {
        ShareFragment fragment = new ShareFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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



        // Inflate the layout for this fragment
        return view;
    }

    private void updateLocationReport() {
        if (locationReportSubmitted) {
            deleteLocationReport();
        }
        else {
            addLocationReport();
        }
    }

    private void addLocationReport() {
        String name = currentUser.getDisplayName();
        String location = spinnerWhere.getSelectedItem().toString();

        locationReportID = databaseReports.push().getKey();

        new LocationReport(locationReportID, name, location);

        locationReportSubmitted = true;
        updateUI();
    }

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

    private void updateUI() {
        if (locationReportSubmitted) {
            buttonSubmit.setText(R.string.button_check_out);

            databaseReports.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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

    // TODO: Rename method, update argument and hook method into UI event
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setLocationReportSubmitted(boolean locationReportSubmitted) {
        this.locationReportSubmitted = locationReportSubmitted;
    }
}
