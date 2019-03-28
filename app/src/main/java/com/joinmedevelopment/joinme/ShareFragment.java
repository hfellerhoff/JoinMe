package com.joinmedevelopment.joinme;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


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
    DatabaseReference databaseLocationReports;

    // UI Elements
    Button buttonSumbit;
    Spinner spinnerLocation;

    private boolean locationReportSubmitted = false;
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

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        databaseLocationReports = FirebaseDatabase.getInstance().getReference("location_reports");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_share, container, false);

        buttonSumbit = (Button) view.findViewById(R.id.buttonSubmit);
        buttonSumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocationReport();
            }
        });

        spinnerLocation = (Spinner) view.findViewById(R.id.spinnerLocation);

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
        String location = spinnerLocation.getSelectedItem().toString();

        locationReportID = databaseLocationReports.push().getKey();

        LocationReport locationReport = new LocationReport(locationReportID, name, location);
        databaseLocationReports.child(locationReportID).setValue(locationReport);

        locationReportSubmitted = true;
        updateUI();
    }

    private void deleteLocationReport() {
        databaseLocationReports.child(locationReportID).removeValue();
        locationReportSubmitted = false;

        updateUI();
    }

    private void updateUI() {
        if (locationReportSubmitted) {
            buttonSumbit.setText(R.string.button_sign_out);
        }
        else {
            buttonSumbit.setText(R.string.button_sign_in);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
