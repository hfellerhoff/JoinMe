package com.joinmedevelopment.joinme;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editTextName;
    Spinner spinnerLocation;
    Button buttonSumbit;

    DatabaseReference databaseLocationReports;

    ListView listViewLocationReports;

    List<LocationReport> locationReportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseLocationReports = FirebaseDatabase.getInstance().getReference("location_reports");

        editTextName = (EditText)findViewById(R.id.editTextName);
        spinnerLocation = (Spinner)findViewById(R.id.spinnerLocation);
        buttonSumbit = (Button)findViewById(R.id.buttonSubmit);

        listViewLocationReports = (ListView)findViewById(R.id.listViewLocationReports);

        locationReportList = new ArrayList<>();

        buttonSumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLocationReport();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseLocationReports.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                locationReportList.clear();

                for(DataSnapshot locationReportSnapshot : dataSnapshot.getChildren()){
                    LocationReport locationReport = locationReportSnapshot.getValue(LocationReport.class);

                    locationReportList.add(locationReport);
                }

                LocationReportList adapter = new LocationReportList(MainActivity.this, locationReportList);
                listViewLocationReports.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addLocationReport()
    {
        String name = editTextName.getText().toString();
        String location = spinnerLocation.getSelectedItem().toString();

        if (name.isEmpty())
            Toast.makeText(this, "Location Report Unsuccessful: Enter a name", Toast.LENGTH_LONG).show();

        else {
            String id = databaseLocationReports.push().getKey();

            LocationReport locationReport = new LocationReport(id, name, location);

            databaseLocationReports.child(id).setValue(locationReport);

            Toast.makeText(this, "Location Report Successful", Toast.LENGTH_SHORT).show();
        }
    }
}
