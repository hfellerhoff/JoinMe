package com.joinmedevelopment.joinme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SearchFragment.OnListFragmentInteractionListener,
        ShareFragment.OnFragmentInteractionListener{

    private FirebaseAuth mAuth;

    // Used for fragments
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private static int RC_SIGN_IN = 100;
    private boolean userInfoUpdated = false;
    private final boolean SIGN_IN_REQUIRED = true;

    private UserInformation userInformation;

    SearchFragment searchFragment;
    ShareFragment shareFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mSectionsPagerAdapter = new MainActivity.SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {
                if (!userInfoUpdated)
                    updateNavigationUserInformation();
            }

            @Override
            public void onDrawerOpened(@NonNull View view) {

            }

            @Override
            public void onDrawerClosed(@NonNull View view) {

            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity(FriendActivity.class);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null)
            signInUser();
        else
            initializeApp();
    }


    public void changeActivity(Class c)
    {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    public void signInUser() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
//             ,new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

        initializeApp();
    }

    public void signOutUser() {
        //shareFragment.deleteLocationReport();

        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });

        userInfoUpdated = false;

        if (SIGN_IN_REQUIRED)
            signInUser();
    }

    public void initializeApp() {
        initializeUser();
        initializeFragments();
    }

    public void initializeUser() {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getUid());
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(UserInformation.class) != null) {
                    setUserInformation(dataSnapshot.getValue(UserInformation.class));
                }
                else {
                    if (FirebaseAuth.getInstance().getCurrentUser() == null)
                        changeActivity(MainActivity.class);
                    else {
                        UserInformation userInformation = new UserInformation();
                        setUserInformation(userInformation);
                        userInformation.updateUserInformation();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userInfoUpdated = false;
    }

    public void initializeFragments() {
        searchFragment = new SearchFragment();
        shareFragment = new ShareFragment();
    }

    public boolean userSignedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return !(user == null);
    }

    public void deleteUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });

        user.delete();

        if (SIGN_IN_REQUIRED)
            signInUser();
    }

    public void updateNavigationUserInformation() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String name, email;

        if (user != null)
        {
            name = user.getDisplayName();
            email = user.getEmail();
        }
        else {
            name = getString(R.string.nav_header_title);
            email = getString(R.string.nav_header_subtitle);
        }

        TextView textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        textViewUserName.setText(name);

        TextView textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewUserEmail.setText(email);

        userInfoUpdated = true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                updateNavigationUserInformation();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Function for communicating with SearchFragment
    @Override
    public void onListFragmentInteraction(LocationReport locationReport) {

    }

    // Function for communicating with ShareFragment
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_test_app_1) {
            testApp(1);
        } else if (id == R.id.nav_test_app_2) {
            testApp(2);
        } else if (id == R.id.nav_test_app_3) {
            testApp(3);
        } else if (id == R.id.nav_sign_out) {
            signOutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
//                    searchFragment = new SearchFragment();
                    return searchFragment;
                case 1:
//                    shareFragment = new ShareFragment();
                        return shareFragment;
                default:
//                    searchFragment = new SearchFragment();
                    return searchFragment;
            }

            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            // return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }

    private void setUserInformation(UserInformation userInformation) {
        this.userInformation = userInformation;
    }

    private void testApp(int testNumber) {
        if (testNumber == 1) {
            new Friend("JDHFH38RH3H745", "John Smith", true, userInformation.getUserID());
            new UserInformation("JDHFH38RH3H745", "John Smith", "johnsmith@luc.edu", false, null);
            new LocationReport("JDHFH38RH3H745", "JAHSEUWFUWERN", "John Smith", "Information Commons");
            Toast.makeText(this, "User \"John Smith\" added.", Toast.LENGTH_SHORT).show();
        }
        else if (testNumber == 2){
            new Friend("ASDHF8ERH2UHA2", "Allison Brown", true, userInformation.getUserID());
            new UserInformation("ASDHF8ERH2UHA2", "Allison Brown", "allisonbrown@luc.edu", false, null);
            new LocationReport("ASDHF8ERH2UHA2", "JHWHUEH34AJSD", "Allison Brown", "Cudahy Library");
            Toast.makeText(this, "User \"Allison Brown\" added.", Toast.LENGTH_SHORT).show();
        }
        else if (testNumber == 3) {
            new Friend("COMP271ISGREAT", "Mark Albert", true, userInformation.getUserID());
            new UserInformation("COMP271ISGREAT", "Mark Albert", "mva@cs.luc.edu", false, null);
            new LocationReport("COMP271ISGREAT", "COMP271AWYEAH", "Mark Albert", "Cudahy Library");
            Toast.makeText(this, "User \"Mark Albert\" added.", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "All test users already added.", Toast.LENGTH_SHORT).show();

    }

}
