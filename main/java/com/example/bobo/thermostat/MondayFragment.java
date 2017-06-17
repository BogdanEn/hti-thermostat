package com.example.bobo.thermostat;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import util.HeatingSystem;
import util.Switch;
import util.WeekProgram;

import static com.example.bobo.thermostat.R.id.addScheduleButton;

import static com.example.bobo.thermostat.R.id.testButton;
import static java.lang.Thread.sleep;


/**
 * A simple {@link Fragment} subclass.
 */
public class MondayFragment extends Fragment implements View.OnClickListener {

    // the list and its interface(adapter)
    ListView lv;
    CustomAdapter adapter;
    // the thread to update constantly the list
    Thread MondayListThread;
    // auto-updates
    boolean autoUpdateList = true;
    int UPDATE_INTERVAL_MdL = 3000;
    // the array of switches for Monday
    ArrayList<Switch> MondaySwitches = new ArrayList<>();
    int[] IMAGES = new int[100];
    Switch s,s2;
    WeekProgram wpg;
    //checking if updating has been requested
    boolean updating = false;
    int nr_switch = 0;
    boolean state_switch = false;
    String time_switch;
    String type_switch;
    // view
    View v;
    // buttons
    Button addSchedule_button;
    Button test_button;

    public MondayFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_monday, container, false);


        // START

        // declaring the list
        lv = (ListView) v.findViewById(R.id.Lista);

        // declaring the buttons
        addSchedule_button = (Button) v.findViewById(addScheduleButton);
        test_button = (Button) v.findViewById(testButton);
        // setting the clicks
        addSchedule_button.setOnClickListener(this);
        test_button.setOnClickListener(this);
        // creating the thread
        MondayListThread = new Thread();

        // setting the thread and starting it
        retrieveData();
        return v;
    }

    Handler writeSending = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Sending...", Toast.LENGTH_SHORT).show();
        }
    };

    Handler writeUpdateSuccesful = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Update succesful!", Toast.LENGTH_SHORT).show();
        }
    };

    // unsuccesful because of the communication with the server, not a server error!
    Handler writeUpdateUnsuccesful = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Update unsuccesful! Try later!", Toast.LENGTH_SHORT).show();
        }
    };

    // for the catch statement, it does not relate with the content of the list
    // like for ex: too many day switches
    Handler writeServerErrorMsg = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Toast.makeText(getActivity().getApplicationContext(),
                    ((String)msg.obj), Toast.LENGTH_SHORT).show();
        }
    };

    // retrieving data every 5 seconds
    public void retrieveData() {
        MondayListThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (autoUpdateList) {
                        if (isNetworkAvailable()) {

                            // Retrieve the week program
                            //for (int i = 0; i < (wpg.data.get("Monday").size()); i++) {
                            // s = wpg.data.get("Monday").get(i);
                            //  MondaySwitches.add(s);
                            //}

                            // Get the week program
                            wpg = HeatingSystem.getWeekProgram();
                            // clearing the old switches
                            MondaySwitches.clear();
                            // Get the switches for Monday
                            MondaySwitches = wpg.getDay("Monday");
                            // checking to see if there was any update request
                            // in these way we see if the communication with the server was succesful
                            if (updating) {
                                s2 = MondaySwitches.get(nr_switch);
                                Log.e("nr_switch:", String.valueOf(nr_switch));
                                Log.e("time_switch:", String.valueOf(time_switch) + " " + s2.getTime());
                                Log.e("type_switch:", String.valueOf(type_switch) + " " + s2.getType());
                                Log.e("state_switch:", String.valueOf(state_switch) + " " + s2.getState());
                                // check if the current switch is the same with the desired switch
                                if (s2.getTime().equals(time_switch) && s2.getType().equals(type_switch)
                                        && (s2.getState() == state_switch)) {
                                    writeUpdateSuccesful.sendEmptyMessage(0);
                                    updating = false;
                                    nr_switch = 0;
                                    time_switch = "";
                                    type_switch = "";
                                    Log.e("RESULT","TRUE");
                                } else {
                                    writeUpdateUnsuccesful.sendEmptyMessage(0);
                                    updating = false;
                                    nr_switch = 0;
                                    time_switch = "";
                                    type_switch = "";
                                    //retrieveData();
                                    Log.e("RESULT","FALSE");
                                }
                            }
                            displayData();
                        } else {
                            // closing the thread to save battery and to have more control
                            //closeNetwork();
                        }
                        sleep(UPDATE_INTERVAL_MdL);
                    }
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        });
        // starting the thread for auto-updating the list
        MondayListThread.start();

    }

    // setting the correct images for each switch
    public int[] arrayOfImages() {
        for (int i = 0; i < MondaySwitches.size(); i++) {
            s = MondaySwitches.get(i);
            if (s.getType().equals("day")) {
                IMAGES[i] = R.drawable.sun;
                // Log.e("Error", Integer.toString(IMAGES[i]));
            } else if (s.getType().equals("night")) {
                IMAGES[i] = R.drawable.moon;
            }
        }
        //Log.e("Error","D");
        //Log.e("SIZE",Integer.toString(MondaySwitches.size()));
        return IMAGES;
    }

    // displaying the listview
    public void displayData() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // setting the correct images for each switch
                arrayOfImages();

                // checking to see if it is an update of the list
                // or if it is the creation of the list
                // in this way the scroll position is maintained
                if (lv.getAdapter() == null) {
                    adapter = new CustomAdapter(getActivity(), MondaySwitches, IMAGES);
                    lv.setAdapter(adapter);
                }
                else {
                    adapter.updateData(MondaySwitches,IMAGES); //update your adapter's data
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    // adding a new switch when button is pressed
    Switch s1;
    public void addSchedule (View v) {
        //!Switch s = new Switch("day",true, "13:00");

        // creating the desired switch
        updating = true;
        nr_switch = 3;
        state_switch = false;
        type_switch = "night";
        time_switch = "05:55";
        s1 = new Switch(type_switch,state_switch, time_switch);



        //!MondaySwitches.add(s);
        //arrayOfImages();
        //displayData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    synchronized(this) {

                        //wpg.setDefault();
                        //wpg.data.get("Monday").remove(3); - does not work
                        //!wpg.data.get("Monday").set(3, new Switch("day", true, "12:00"));

                        // updating the new switch to the server
                        wpg = HeatingSystem.getWeekProgram();
                        wpg.data.get("Monday").set(nr_switch, s1);
                        HeatingSystem.setWeekProgram(wpg);
                        writeSending.sendEmptyMessage(0);
                        wait(2000);
                        retrieveData();
                    }
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                    // in case of updating error, quite technical error
                    Message msg = Message.obtain();
                    msg.obj = String.valueOf(e);
                    writeServerErrorMsg.sendMessage(msg);
                }
            }
        }).start();

    }

    // resetting the entire schedule to default
    public void reset() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    wpg.setDefault();
                    HeatingSystem.setWeekProgram(wpg);
                    writeSending.sendEmptyMessage(0);

                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                    // in case of updating error, quite technical error
                }
            }
        }).start();
    }

    public void generate() {
        reset();
        for (int i = 1; i <=100;i++) {
            s1 = new Switch("day", true, "13:00");
            //Log.e("Generate","Generating");
        }
    }

    // testing something, does not matter
    public void test(View v) {
        generate();
    }

    // listening for clicks !!!!!!!!!!!!!!!!!!!!!!!!!!
    @Override
    public void onClick(View v) {
        if (v.getId() == addScheduleButton) {
            addSchedule(v);
        } else if (v.getId() == testButton) {
            test(v);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.monday, menu);
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
            Log.e("D","D");
        }
        //
        if (id == R.id.action_Monday) {
            //Intent myIntent = new Intent(getActivity().getApplicationContext(), MondayActivity.class);
            //myIntent.putExtra("key", 0); //Optional parameters
            //ThermostatActivity.this.startActivity(myIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    // checking to see if there is internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

