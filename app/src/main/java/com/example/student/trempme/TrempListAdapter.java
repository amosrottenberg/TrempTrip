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

public class TrempListAdapter extends ArrayAdapter<TrempListObject> {
    Context context;
    List<TrempListObject> tremps;

    /**
     * constructor for the TrempListAdapter
     * @param context
     * @param resource
     * @param textViewResourceId
     * @param tremps
     */
    public TrempListAdapter(Context context, int resource, int textViewResourceId, List<TrempListObject> tremps) {
        super(context, resource, textViewResourceId, tremps);
        this.context=context;
        this.tremps =tremps;


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
        TrempListObject tremp = tremps.get(position);
        //Log.w("place position",places.get(position*2)+""+places.get((position*2)+1));
        tvFrom.setText(tremp.getFromName());
        tvTo.setText(tremp.getToName());
        tvFullName.setText(tremp.getUserName());
        tvPhoneNumber.setText(tremp.getUserPhoneNumber());

        long ms = tremp.getDepartureTime();
        Date date = new Date(ms);
        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm");
        Log.w("getTrempTime",dateformat.format(date));
        tvTime.setText(dateformat.format(date));

        dateformat = new SimpleDateFormat("dd/MM");
        Log.w("getTrempDate",dateformat.format(date));
        tvDate.setText(dateformat.format(date));

        return view;
    }



}



