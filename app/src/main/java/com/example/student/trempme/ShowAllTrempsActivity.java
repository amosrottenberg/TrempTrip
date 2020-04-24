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
import java.util.Stack;

public class ShowAllTrempsActivity extends AppCompatActivity {
    ListView lvTrempList;

    List<TrempListObject> trempList=new ArrayList<>();
    List<Place> placeList=new ArrayList<>();
    TrempListAdapter trempListAdapter;
    Tremp tremp;

    FirebaseUser userAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    int sizeOfPlaceList;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_tremps);
        Log.w("allTremps","here");
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

    // set the reference to group for the user
    public void setMyRef() {
        Query myUser=database.getReference().child("User").child(userAuth.getUid());

        myUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.w("setMyRef",dataSnapshot.toString());
                String groupOfUser=dataSnapshot.getValue(String.class);
                myRef=database.getReference().child("Group").child(groupOfUser);
                setTrempList();
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

    //set the adapter of the list view to the trempListAdapter object
    public void setLvTrempList(){
        Log.w("LV","here");
        lvTrempList=findViewById(R.id.lvTrempList);
        trempListAdapter=new TrempListAdapter(this, 0,0,trempList);
        lvTrempList.setAdapter(trempListAdapter);

    }

    //set trempList to all of the group's tremps
    //when finish, call setPlaceList()
    public void setTrempList(){
        Query allTremps=myRef.child("tremps").orderByChild("departureTime");

        allTremps.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    tremp = singleSnapshot.getValue(Tremp.class);
                    Log.w("event listener",tremp.getTrempId()+"");
                    trempList.add(new TrempListObject(tremp,null,null,null,null));

                    Log.w("event listener",tremp.getFromId()+"");

                }
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
        for(TrempListObject tremp:trempList){
            getStartAndEndName(tremp.getFromId(),tremp.getToId());
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
     * the size fo tr List times 2.
     * if so, it call completeTrempListObject() method
     * (for each tremp their are two places)
     */
    private void canContinueToLv(int sizeOfPlaceList){
        Log.w("can cuntinue", sizeOfPlaceList+" "+trempList.size());
        if(sizeOfPlaceList==trempList.size()*2){
            Log.w("can cuntinue", "yes");
            completeTrempListObject();
        }
    }

    //add the places names to the TrempListObject
    //add user name and phone number to the TrempListObject
    //when finish, call setLvMyTrempsRequests()
    private void completeTrempListObject(){
        int i = 0;
        for(final TrempListObject tremp:trempList){
            tremp.setFromName(placeList.get(i *2).getName().toString());
            tremp.setToName(placeList.get((i *2)+1).getName().toString());
            Query userQuery=myRef.child("users").orderByChild("userId").equalTo(tremp.getUserId());
            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                        User user=singleSnapshot.getValue(User.class);
                        tremp.setUserName(user.getFullName());
                        tremp.setUserPhoneNumber(user.getPhoneNumber());
                    }
                    setLvTrempList();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            i++;
        }
    }
}
