package com.example.student.trempme;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class NetworkChangedReceiver extends BroadcastReceiver{
    private boolean hasConnection=true;
    @Override
    public void onReceive(final Context context, Intent intent) {
        try
        {
            if (isOnline(context)) {
                Log.w("receive", "Online Connect Internet ");

            } else {
                Log.w("receive", "Connectivity Failure !!! ");
                Toast.makeText(context.getApplicationContext(),"No Internet Connection", Toast.LENGTH_LONG).show();
                Helper.closeApp(context);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }





}
