package com.example.student.trempme;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {

    Timer timer;
    TimerTask timerTask;
    String TAG = "Timers";
    int Your_X_SECS = 180;

    FirebaseUser userAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    List<Tremp> currentTremps=new ArrayList<>();
    List<Trip> currentTrips=new ArrayList<>();
    User user;

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        startTimer();

        return START_STICKY;
    }


    @Override
    public void onCreate(){
        Log.e(TAG, "onCreate");


    }

    @Override
    public void onDestroy(){
        Log.e(TAG, "onDestroy");
        stoptimertask();
        super.onDestroy();


    }


    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, Your_X_SECS*1000); //
        //timer.schedule(timerTask, 5000,1000); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        startCommend();
                    }
                });
            }
        };
    }

    private void sendNotification(){
        Intent intent = new Intent(this, ShowAllTripsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//        NotificationCompat.Builder mBuilder =
//            new NotificationCompat.Builder(this)
//                    .setSmallIcon(R.drawable.places_ic_search)
//                    .setContentTitle("See all Trips")
//                    .setContentText("Some may fit your tremps ")
//                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                    .setContentIntent(pendingIntent); //Required on Gingerbread and below
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//
//        // notificationId is a unique int for each notification that you must define
//        notificationManager.notify(0, mBuilder.build());

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.places_ic_search)
                        .setContentTitle("see all Trips")
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentText("Some may fit your tremps");
        int NOTIFICATION_ID = 0;


        builder.setContentIntent(pendingIntent);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());
    }


    private void hasMatch(){
        Log.w("has match","here");
        boolean needToSendNotification=false;
        if (currentTremps!=null&&currentTremps.size()!=0){
            List<Tremp> newCurrentTremps=new ArrayList<>();
            copyList(currentTremps,newCurrentTremps);
            int i=0;
            for (Tremp tremp : currentTremps) {
                if(tremp.getUserId().equals(userAuth.getUid())){
                    if (!tremp.isNotificationSent()){
                        for (Trip trip : currentTrips){
                            //if (isShortDistance(tremp.getFromId(),trip.getFromId())&&isShortDistance(tremp.getToId(),trip.getToId())&& !trip.getUserId().equals(userAuth.getUid())) {
                            if (trip.getFromId().equals(tremp.getFromId())&&trip.getToId().equals(tremp.getToId()) && !trip.getUserId().equals(userAuth.getUid())) {
                                if(isTimeClose(tremp,trip)){
                                    newCurrentTremps.remove(i);
                                    tremp.setNotificationSent(true);
                                    newCurrentTremps.add(tremp);
                                    needToSendNotification=true;
                                }

                            }
                        }
                    }
                }
                i++;
            }
            if(newCurrentTremps.size()==0){
                sendNotification();
            }
            myRef.child("tremps").setValue(newCurrentTremps);
            currentTremps.clear();
            newCurrentTremps.clear();
            currentTrips.clear();

        }
        if(needToSendNotification){
            sendNotification();
        }
    }

    private boolean isTimeClose(Tremp tremp, Trip trip){
        boolean timeClose=false;
        long trempTime=tremp.getDepartureTime();
        long tripTime=trip.getDepartureTime();
        if(Math.abs(trempTime-tripTime)<3600000*2)
            timeClose=true;
        return timeClose;
    }

    private void deleteTrempOrTrip(){
        Query groupTremps=myRef.child("tremps");

        Log.w("PRINT QUERY",groupTremps+"");

        groupTremps.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.w("deleteTrempOrTrip","dataChange-tremp");
                long currentTime= System.currentTimeMillis();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                   Tremp tremp =singleSnapshot.getValue(Tremp.class);
                   currentTremps.add(tremp);
                   if(tremp.getDepartureTime()+3600000*24<currentTime){
                       Log.w("deleteTrempOrTrip","remove tremp");
                        currentTremps.remove(tremp);
                   }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }
        });

        Query groupTrips=myRef.child("trips");

        Log.w("PRINT QUERY",groupTrips+"");

        groupTrips.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.w("deleteTrempOrTrip","dataChange-trip");
                long currentTime= System.currentTimeMillis();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Trip trip =singleSnapshot.getValue(Trip.class);
                    currentTrips.add(trip);
                    if(trip.getDepartureTime()+3600000*24<currentTime){
                        Log.w("deleteTrempOrTrip","remove trip");
                        currentTrips.remove(trip);
                    }
                }
                myRef.child("trips").setValue(currentTrips);
                hasMatch();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }

        });

    }

    public void startCommend(){
        setFirebaseVariables();
    }

    private void setFirebaseVariables() {
        database = FirebaseDatabase.getInstance();
        userAuth = FirebaseAuth.getInstance().getCurrentUser();
        setMyRef();
    }
    public void setMyRef() {
        Query myUser=database.getReference().child("User").child(userAuth.getUid());

        myUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if(!dataSnapshot.getValue().getClass().equals(String.class)) {
                    Log.w("notific","User is object user");
                }
                else {
                    Log.w("setMyRef",dataSnapshot.toString());
                    String groupOfUser=dataSnapshot.getValue(String.class);
                    myRef=database.getReference().child("Group").child(groupOfUser);
                    deleteTrempOrTrip();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void copyList(List<Tremp> oldTremps, List<Tremp> newTremps){
        for(Tremp tremp : oldTremps){
            newTremps.add(tremp);
        }
    }

//    private Place getPlace(String placeId){
//        final Place[] place = new Place[1];
//        GeoDataClient mGeoDataClient = Places.getGeoDataClient(this, null);
//        mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
//            @Override
//            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
//                if (task.isSuccessful()) {
//                    PlaceBufferResponse places = task.getResult();
//                    place[0] = places.get(0).freeze();
//                    Log.w("Place by id", "Place found: " + place[0].getName());
//                } else {
//                    Log.w("Place by id", "Place not found.");
//                }
//            }
//
//        });
//        return place[0];
//    }
//
//    private boolean isShortDistance(String aPlaceId, String bPlaceId){
//        final boolean[] shortDistance = {false};
//        final Place[] aPlace = new Place[1];
//        GeoDataClient mGeoDataClient = Places.getGeoDataClient(this, null);
//        mGeoDataClient.getPlaceById(aPlaceId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
//            @Override
//            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
//                if (task.isSuccessful()) {
//                    PlaceBufferResponse places = task.getResult();
//                    aPlace[0] = places.get(0).freeze();
//                    Log.w("Place by id", "Place found: " + aPlace[0].getName());
//                } else {
//                    Log.w("Place by id", "Place not found.");
//                }
//            }
//
//        });
//
//        final Place[] bPlace = new Place[1];
//        mGeoDataClient.getPlaceById(bPlaceId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
//            @Override
//            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
//                if (task.isSuccessful()) {
//                    PlaceBufferResponse places = task.getResult();
//                    bPlace[0] = places.get(0).freeze();
//                    Log.w("Place by id", "Place found: " + bPlace[0].getName());
//                    if(Helper.distance(aPlace[0],bPlace[0])<1500){
//                        shortDistance[0] =true;
//                    }
//                } else {
//                    Log.w("Place by id", "Place not found.");
//                }
//            }
//
//        });
////        Place aPlace=getPlace(aPlaceId);
////        Place bPlace=getPlace(bPlaceId);
//
//
//        return shortDistance[0];
//    }


}
