package com.example.student.trempme;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.location.places.Place;

import java.util.Locale;

public class Helper {

    public Helper(){

    }
    //set the default app language to lang
    public static void setDefaultLanguage(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
    }

    //checks if location service available
    public static boolean isLocationServicesAvailable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)&&!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //All location services are disabled
            Log.w("is location", "false");
            return false;

        }
        return true;
    }

    //send an intent to main activity with message to close the app
    public static void closeApp(Context context){
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("close_activity",true);
        context.startActivity(i);
    }

    public static float distance (Place aPlace, Place bPlace){
        Location locationA = new Location("point A");

        locationA.setLatitude(aPlace.getLatLng().latitude);
        locationA.setLongitude(aPlace.getLatLng().longitude);

        Location locationB = new Location("point B");

        locationB.setLatitude(bPlace.getLatLng().latitude);
        locationB.setLongitude(bPlace.getLatLng().longitude);

        float distance = locationA.distanceTo(locationB);

        return distance;
    }
}
