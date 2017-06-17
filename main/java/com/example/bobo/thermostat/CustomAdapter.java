package com.example.bobo.thermostat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import util.Switch;

/**
 * Created by Bobo on 6/14/2017.
 */

public class CustomAdapter extends BaseAdapter {
    Context c;
    int[] images = new int[100];
    ArrayList<Switch> switches;

    public CustomAdapter(Context c, ArrayList<Switch> switches, int[] images) {
        this.c = c;
        this.switches = switches;
        this.images = images;
    }

    // updating data, or basically making a connection between the new switch from xActivity
    // to the old switch from the adapter
    public void updateData(ArrayList<Switch> switches, int[] images) {
        this.switches = switches;
        this.images = images;
    }

    @Override
    public int getCount() {
        return switches.size();
    }

    @Override
    public Object getItem(int i) {
        return switches.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            ///LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //view = vi.inflate(R.layout.model,null);
            view = LayoutInflater.from(c).inflate(R.layout.custom_adapter, viewGroup, false);
            //view = getLayoutInflater().inflate(R.layout.model, viewGroup, false);
        }
        final Switch s = (Switch) this.getItem(i);
        ImageView img = (ImageView) view.findViewById(R.id.imageView);
        TextView temperatureTxt = (TextView) view.findViewById(R.id.temperature);
        TextView dateTxt = (TextView) view.findViewById(R.id.date);
        temperatureTxt.setText(s.getType());
        dateTxt.setText(s.getTime());
        img.setImageResource(images[i]);
        //if ((temperatureTxt.getText().toString()).equals("day")) {
        //   img.setImageResource(R.drawable.sun);
        //    Log.e("Error", "Day");
        //}
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(c, s.getTime(), Toast.LENGTH_SHORT).show();
                //v.setBackgroundColor(Color.BLUE);
                //v.setSelected(true);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent myIntent = new Intent(c, SwitchActivity.class);
                //myIntent.putExtra("key", 0); //Optional parameters
                c.startActivity(myIntent);
                return true;
            }
        });
        return view;
    }
}
