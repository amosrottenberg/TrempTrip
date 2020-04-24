package com.example.student.trempme;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TripListAdapter extends ArrayAdapter<TripListObject> {
    Context context;
    List<TripListObject> trips;

    /**
     * constructor for the TrempListAdapter
     * @param context
     * @param resource
     * @param textViewResourceId
     * @param trips
     */
    public TripListAdapter(Context context, int resource, int textViewResourceId, List<TripListObject> trips) {
        super(context, resource, textViewResourceId, trips);
        this.context=context;
        this.trips =trips;


    }

    /**
     * creates the list item layout and return it
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.travel_list_object, parent, false);
        TextView tvFrom = (TextView) view.findViewById(R.id.tvFrom);
        TextView tvTo = (TextView) view.findViewById(R.id.tvTo);
        TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        TextView tvFullName=view.findViewById(R.id.tvFullName);
        TextView tvPhoneNumber=view.findViewById(R.id.tvPhoneNumber);
        TripListObject trip = trips.get(position);
        tvFrom.setText(trip.getFromName());
        tvTo.setText(trip.getToName());
        tvFullName.setText(trip.getUserName());
        tvPhoneNumber.setText(trip.getUserPhoneNumber());

        long ms = trip.getDepartureTime();
        Date date = new Date(ms);
        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm");
        Log.w("getTripTime",dateformat.format(date));
        tvTime.setText(dateformat.format(date));

        dateformat = new SimpleDateFormat("dd/MM");
        Log.w("getTrempDate",dateformat.format(date));
        tvDate.setText(dateformat.format(date));




        return view;
    }


}
