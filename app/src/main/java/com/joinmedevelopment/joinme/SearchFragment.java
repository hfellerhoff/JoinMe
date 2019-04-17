package com.joinmedevelopment.joinme;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SearchFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    DatabaseReference databaseReports;
    ArrayList<LocationReport> reportList;

    private View view;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private boolean sortByFriends = true;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SearchFragment newInstance(int columnCount) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.list_location_reports);

        reportList = new ArrayList<LocationReport>();

        databaseReports = FirebaseDatabase.getInstance().getReference("reports");
        DatabaseReference databaseFriends = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getUid() + "/friends");

        final GenericTypeIndicator<HashMap<String, Friend>> typeIndicator = new GenericTypeIndicator<HashMap<String, Friend>>() {};
        final HashMap<String, Friend> friendsMap = new HashMap<>();

        databaseFriends.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Friend> tempMap = dataSnapshot.getValue(typeIndicator);
                if (tempMap != null)
                    friendsMap.putAll(tempMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReports.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reportList.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LocationReport report = snapshot.getValue(LocationReport.class);

                    boolean addReport = false;

                    if (sortByFriends) {
                        for (Friend friend : friendsMap.values()) {
                            if (friend.isFriend() && friend.getId().equals(report.getUserID()))
                                addReport = true;
                        }
                    }
                    else if (report.getUserID() != null)
                        if (report.getUserID().equals(FirebaseAuth.getInstance().getUid()))
                            addReport = false;
                    else
                        addReport = true;


                    if (addReport)
                        reportList.add(report);
                }

                QuickSort.quickSortLocationReport(reportList);

                // Put reports in chronological order from newest to oldest
                Collections.reverse(reportList);

                adapter = new LocationReportAdapter(reportList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(LocationReport locationReport);
    }

    public Boolean isSortByFriends() {return sortByFriends;}

    public void setSortByFriends(boolean sortByFriends) {
        this.sortByFriends = sortByFriends;
    }
}
