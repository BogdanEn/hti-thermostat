package com.example.bobo.thermostat;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SignalStrength;
import android.text.InputType;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Thread.sleep;

// webserver
import util.*;

public class ThermostatActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // setting the OverviewFragment to be displayed once the app is opened
        setTitle("Overview");
        OverviewFragment fragment = new OverviewFragment();

        FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
        fm.replace(R.id.frame, fragment, "fragment1");
        fm.commit();
    }


    public void reconnectNetwork() {

        int numberReconnections = 0;
        for (numberReconnections = 1; numberReconnections <= 3; numberReconnections++) {
            Toast.makeText(getApplicationContext(), "Connecting (" + numberReconnections + ")", Toast.LENGTH_SHORT).show();
            if (isNetworkAvailable()) {
                // reconnecting and starting the auto-update threads
                //autoCurrent = true;
                //autoDayNight = true;
                //nightDayTemperature();
                //currentTemperature();
                // informing the user that reconnection was successful
                Toast.makeText(getApplicationContext(), "Connected! Retrieving data, please wait", Toast.LENGTH_SHORT).show();
                return;//!!
            }
        }
        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();

    };

    // Android's garbage

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.thermostat, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_reconnect) {
            //reconnectNetwork();
        }
        //
        if (id == R.id.action_Monday) {
            //Intent myIntent = new Intent(ThermostatActivity.this, MondayActivity.class);
            //myIntent.putExtra("key", 0); //Optional parameters
            //ThermostatActivity.this.startActivity(myIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.Overview) {
            setTitle("Overview");
            OverviewFragment fragment = new OverviewFragment();

            FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
            fm.replace(R.id.frame, fragment, "fragment1");
            fm.commit();
        } else if (id == R.id.Monday) {
            //myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            setTitle("Monday");
            MondayFragment fragment = new MondayFragment();

            FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
            fm.replace(R.id.frame, fragment, "fragment1");
            fm.commit();

        } else if (id == R.id.Tuesday) {
            setTitle("Tuesday");
            TuesdayFragment fragment = new TuesdayFragment();

            FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
            fm.replace(R.id.frame, fragment, "fragment1");
            fm.commit();

        } else if (id == R.id.Wednesday) {

        } else if (id == R.id.Thursday) {

        } else if (id == R.id.Friday) {

        } else if (id == R.id.Saturday) {

        } else if (id == R.id.Sunday) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    /* Check if user is connected to a network */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
