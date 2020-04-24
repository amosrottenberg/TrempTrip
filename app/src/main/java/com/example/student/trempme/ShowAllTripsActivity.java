package com.example.student.trempme;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
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
import java.util.List;

public class ShowAllTripsActivity extends AppCompatActivity {

    ListView lvTripList;

    List<TripListObject> tripList=new ArrayList<>();
    List<Place> placeList=new ArrayList<Place>();
    TripListAdapter tripListAdapter;
    Trip trip;

    FirebaseUser userAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    int sizeOfPlaceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_trips);
        setFirebaseVariables();
        setGoogleAPIVar();
        //static func Helper main activity that keep the screen ltr
        Helper.setDefaultLanguage(this,"en_US ");
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
                Log.w("setMyRef",dataSnapshot.toString());
                String groupOfUser=dataSnapshot.getValue(String.class);
                myRef=database.getReference().child("Group").child(groupOfUser);
                setTripList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setGoogleAPIVar(){
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
    }



    //set the adapter of the list view to the tripListAdapter object
    public void setLvTripList(){
        Log.w("LV","here");
        lvTripList=findViewById(R.id.lvTrempList);
        tripListAdapter=new TripListAdapter(this, 0,0,tripList);
        lvTripList.setAdapter(tripListAdapter);

    }

    //set tripList to all of the group's trips
    //when finish, call setPlaceList()
    public void setTripList(){
        Query allTrips=myRef.child("trips").orderByChild("departureTime");

        allTrips.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    trip = singleSnapshot.getValue(Trip.class);
                    tripList.add(new TripListObject(trip,null,null,null,null));

                    //Log.w("event listener",trip.getFromId()+"");

                }
                //setLvTripList();
                setPlaceList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("event listener","cancelled");

            }




        });


    }

    //set the places list
    //call getStartAndEndName() for each trip
    private void setPlaceList(){
        Log.w("PlaceList","here");
        for(Trip trip:tripList){
            getStartAndEndName(trip.getFromId(),trip.getToId());

        }
    }






    /**
    *get the place of the startPlaceId and the endPlaceId
    *add the places to placesList
    *call canContinueToLv() to check if placesList contain all places
    * @param startPlaceId
    * @param endPlaceId
     */

    private void getStartAndEndName(String startPlaceId,String endPlaceId){
        Log.w("Place by id", "here");
        final Place[] myPlaces=new Place[2];
        mGeoDataClient.getPlaceById(startPlaceId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();
                    Place place = places.get(0).freeze();
                    myPlaces[0]=place;
                    Log.w("Place by id", "Place found: " + place.getName());
                    Log.w("myPlaces-start",myPlaces[0].getName()+"");
                    placeList.add(place);
                    places.release();
                    sizeOfPlaceList++;
                    canContinueToLv(sizeOfPlaceList);
                } else {
                    Log.w("Place by id", "Place not found.");
                }
            }

        });

        mGeoDataClient.getPlaceById(endPlaceId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();
                    Place place = places.get(0).freeze();
                    myPlaces[1]=place;
                    Log.w("Place by id", "Place found: " + place.getName());
                    Log.w("myPlaces-end"," "+myPlaces[1].getName());
                    //placeList.add(myPlaces);
                    placeList.add(place);
                    places.release();
                    sizeOfPlaceList++;
                    canContinueToLv(sizeOfPlaceList);

                } else {
                    Log.w("Place by id", "Place not found.");
                }
            }

        });

    }

     /**
     *this method checks if the size of the placesList is equals to
     * the size fo tripList List times 2.
     * * (for each trip their are two places)
     * if so, it call completeTripListObject() method
     */
    private void canContinueToLv(int sizeOfPlaceList){
        Log.w("can cuntinue", sizeOfPlaceList+" "+tripList.size());
        if(sizeOfPlaceList==tripList.size()*2){
            Log.w("can cuntinue", "yes");
            completeTripListObject();
        }
    }

    //add the places names to the TripListObject
    //add user name and phone number to the TripListObject
    //when finish, call setLvMyTripsRequests()
    private void completeTripListObject(){
        int i = 0;
        for(final TripListObject trip:tripList){
            trip.setFromName(placeList.get(i *2).getName().toString());
            trip.setToName(placeList.get((i *2)+1).getName().toString());
            Query userQuery=myRef.child("users").orderByChild("userId").equalTo(trip.getUserId());
            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                        User user=singleSnapshot.getValue(User.class);
                        trip.setUserName(user.getFullName());
                        trip.setUserPhoneNumber(user.getPhoneNumber());
                    }
                    setLvTripList();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            i++;
        }
    }
}
