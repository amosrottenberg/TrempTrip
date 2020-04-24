package com.example.student.trempme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;


public class MainActivity extends AppCompatActivity{
    FirebaseUser userAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    Button btnGoToNewTremp,btnGoToNewTrip,btnShowAllTremps,btnShowAllTrips;

    boolean shouldStartService=true;

    private BroadcastReceiver mNetworkReceiver;


    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Intent intent=new Intent(this,MyTripsActivity.class);
        //startActivity(intent);

        //set the NetworkChangedReceiver
        mNetworkReceiver = new NetworkChangedReceiver();
        registerNetworkBroadcast();
        SetIntentButtons();
        //static func Helper main activity that keep the screen ltr
        Helper.setDefaultLanguage(this,"en_US ");




    }

    /**
     * first check if user connected
     if so, check if he/she has all details
     if so, set the myRef
     else,
     goto JoinGroupActivity
     else, goto SingInActivity
     */
    @Override
    protected void onStart() {
        super.onStart();
        setFirebaseVariables();
        Log.e("PRINT USER AUTH",userAuth+"");
        if(userAuth!=null){

            final Query myUser=myRef.child("User").child(userAuth.getUid());

            Log.e("PRINT QUERY",myUser+"");

            myUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(!dataSnapshot.getValue().getClass().equals(String.class)) {
                        User user = dataSnapshot.getValue(User.class);
                        Log.w("My User", user.getUserId());
                        //startService(new Intent(MainActivity.this, NotificationService.class));
                        goToJoinGroup();
                    }
                    else {
                        Log.w("user is string", "true");
                        startService(new Intent(MainActivity.this, NotificationService.class));
                        String groupOfUser = dataSnapshot.getValue(String.class);
                        setMyRef(groupOfUser);
                    }


                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("TAG", "onCancelled", databaseError.toException());
                }
            });

        }else{
            Intent intent=new Intent(MainActivity.this,SignInActivity.class);
            startActivity(intent);
        }


    }



    private void setFirebaseVariables(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        userAuth = FirebaseAuth.getInstance().getCurrentUser();
    }

    // set the reference to group for the user
    public void setMyRef(String groupId) {
        myRef=myRef.child("Group").child(groupId);
        Query q=myRef;
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Group g=dataSnapshot.getValue(Group.class);
                //Log.w("user",g.getUsers().get(0).getFullName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //send an intent to JoinGroupActivity
    public void goToJoinGroup(){

        Intent intent =new Intent(MainActivity.this,JoinGroupActivity.class);
        startActivity(intent);
    }

    private void SetIntentButtons(){
        //goto AskForTrempActivity
        btnGoToNewTremp=findViewById(R.id.btnGoToCreateTremp);
        btnGoToNewTremp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,AskForTrempActivity.class);
                startActivity(intent);
            }
        });
        //goto CreateNewTripActivity
        btnGoToNewTrip=findViewById(R.id.btnGoToCreateTrip);
        btnGoToNewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,CreateNewTripActivity.class);
                startActivity(intent);
            }
        });
        //goto ShowAllTrempsActivity
        btnShowAllTremps=findViewById(R.id.btnShowAllTremps);
        btnShowAllTremps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ShowAllTrempsActivity.class);
                startActivity(intent);
            }
        });
        //goto ShowAllTripsActivity
        btnShowAllTrips=findViewById(R.id.btnShowAllTrips);
        btnShowAllTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ShowAllTripsActivity.class);
                startActivity(intent);
            }
        });

    }


    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.myTrips:
                //goto MyTripsActivity
                intent =new Intent(this,MyTripsActivity.class);
                startActivity(intent);
                return true;
            case R.id.myTrempsRequests:
                //goto MyTrempsRequestsActivity
                intent =new Intent(this,MyTrempsRequestsActivity.class);
                startActivity(intent);
                return true;
            case R.id.signOut:
                signOut();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    stop notification service
    user sign out
    goto SignIn
     */
    private void signOut(){
        shouldStartService=false;
        Intent intent = new Intent(this, NotificationService.class);
        stopService(intent);
        FirebaseAuth.getInstance().signOut();
        intent=new Intent(this,SignInActivity.class);
        startActivity(intent);

    }

    //start broadcast receiver
    private void registerNetworkBroadcast() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        }
    }
    //stop the receiver
    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //if their is no internet connection, close  app
        if(intent.getBooleanExtra("close_activity",false)){
            System.exit(0);

        }
    }

}
