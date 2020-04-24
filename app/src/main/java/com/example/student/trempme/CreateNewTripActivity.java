package com.example.student.trempme;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CreateNewTripActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback {

    TextView tvStartPlace;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM = 1;


    TextView tvEndPlace;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE_TO = 2;

    Button btnSetDepartureTime, btnSetDate, btnCreateNewTrip;
    TextView tvDepartureTime, tvDate;
    Spinner spinNumberOfAvailableSits;

    private int chosenHour;
    private int chosenMinute;
    private int chosenYear;
    private int chosenMonth;
    private int chosenDayOfMonth;
    private long departureTime;
    private int numberOfTrempists;
    private String toId;
    private String fromId;

    private Place fromPlace;
    private Place toPlace;



    FirebaseUser userAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    LocationListener locationListener;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 3;
    private boolean hasPermissions = false;

    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_trip);

        requestPermissions();
        setAutocompleteFragmentView();
        setGoogleAPIVar();

        if (hasPermissions) {
            //only if the variable "hasPermissions" is true execute func the require permissions
            Log.w("hasPermissions", "True");
            setCurrentPlace();
        }


        setTvDepartureTime();
        setTvDate();

        setBtnSetDepartureTime();
        setBtnSetDate();
        setSpinNumberOfAvailableSits();

        setFirebaseVariables();
        setBtnCreateNewTrip();

        //static func from main activity that keep the screen ltr
        Helper.setDefaultLanguage(this,"en_US ");

    }


    //define Google API variables
    private void setGoogleAPIVar(){
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
    }

    private void setBtnCreateNewTrip() {
        btnCreateNewTrip = findViewById(R.id.btnCreateNewTrip);
        btnCreateNewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNewTrip();
            }
        });
    }

    private void setBtnSetDepartureTime() {
        btnSetDepartureTime = findViewById(R.id.btnSetDepartureTime);
        btnSetDepartureTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
    }

    private void setBtnSetDate() {
        btnSetDate = findViewById(R.id.btnSetDate);
        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }
    //set the Time Textview to the current time
    //seve the time in the chosenHour chosenMinute variables
    private void setTvDepartureTime() {
        tvDepartureTime = findViewById(R.id.tvDepartureTime);
        Calendar myCalender = Calendar.getInstance();
        chosenHour = myCalender.get(Calendar.HOUR_OF_DAY);
        chosenMinute = myCalender.get(Calendar.MINUTE);
        String stringHour = chosenHour + "";
        String stringMinute = chosenMinute + "";
        if (chosenHour < 10) {
            stringHour = "0" + stringHour;
        }
        if (chosenMinute < 10) {
            stringMinute = "0" + stringMinute;
        }

        tvDepartureTime.setText(stringHour + ":" + stringMinute);
    }

    //set the Date Textview to the current Date
    private void setTvDate() {
        tvDate = findViewById(R.id.tvDate);
        Calendar myCalender = Calendar.getInstance();
        chosenYear = myCalender.get(Calendar.YEAR);
        chosenMonth = myCalender.get(Calendar.MONTH);
        chosenDayOfMonth = myCalender.get(Calendar.DAY_OF_MONTH);
        chosenMonth = chosenMonth + 1;
        String stringMonth = chosenMonth + "";
        String stringDay = chosenDayOfMonth + "";
        if (chosenMonth < 10) {
            stringMonth = "0" + stringMonth;
        }
        if (chosenDayOfMonth < 10) {
            stringDay = "0" + stringDay;
        }
        chosenDayOfMonth = myCalender.get(Calendar.DAY_OF_MONTH);
        tvDate.setText(stringDay + "/" + stringMonth + "/" + chosenYear);
    }

    // set the spinner number of available sits
    public void setSpinNumberOfAvailableSits() {
        spinNumberOfAvailableSits=findViewById(R.id.spinNumberOfAvailableSits);//fetch the spinner from layout file
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                .getStringArray(R.array.number_of_trempists_array));//setting the country_array to spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinNumberOfAvailableSits.setAdapter(adapter);
        //if you want to set any action you can do in this listener
        spinNumberOfAvailableSits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                numberOfTrempists = Integer.parseInt(arg0.getItemAtPosition(position).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

    //set the Text View of the auto complete and add on click listener
    private void setAutocompleteFragmentView() {
        tvStartPlace = findViewById(R.id.tvStartPlace);
        //Log.w("VIEW",tvStartPlace.toString());
        //autocompleteFragmentToView=findViewById(R.id.place_autocomplete_fragment_from);
        tvStartPlace.setBackgroundResource(R.drawable.border);
        //autocompleteFragmentFromView.findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        tvStartPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAutocompleteStartClicked();
                //v.setVisibility(View.GONE);
            }
        });

        tvEndPlace = findViewById(R.id.tvEndPlace);
        //Log.w("VIEW",tvEndPlace.toString());
        //autocompleteFragmentToView=findViewById(R.id.place_autocomplete_fragment_to);
        tvEndPlace.setBackgroundResource(R.drawable.border);
        //autocompleteFragmentToView.findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        tvEndPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAutocompleteEndClicked();
                //v.setVisibility(View.GONE);
            }
        });
    }


    //func for tvFrom
    //send an intent to the place autocomplete made by google
    //filter for results only in israel
    private void onAutocompleteStartClicked() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("IL")
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM);
        } catch (GooglePlayServicesRepairableException e) {

        } catch (GooglePlayServicesNotAvailableException e) {

        }

    }

    //func for tvTo
    //send an intent to the place autocomplete made by google
    //filter for results only in israel
    private void onAutocompleteEndClicked() {
        Log.w("TAG", "in onAutocompleteFragmentToClicked");
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("IL")
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_TO);
        } catch (GooglePlayServicesRepairableException e) {

        } catch (GooglePlayServicesNotAvailableException e) {

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // the user search for from place
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM) {
            //the user chose place
            if (resultCode == RESULT_OK) {
                //get the place
                Place place = PlaceAutocomplete.getPlace(this, data);
                //set the tvStartPlace to the place name
                tvStartPlace.setText(place.getName());
                //set the fromId to the placeId
                fromId = place.getId();
                fromPlace=place;
                Log.w("TAG-onActivityResult", "Place: " + place.getName());
                setMap();
                //autocompleteFragmentEditTextFrom.setText(place.getName());
            // if something went wrong
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);

                Log.w("TAG", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        // the user search for from place
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_TO) {
            //the user chose place
            if (resultCode == RESULT_OK) {
                //get the place
                Place place = PlaceAutocomplete.getPlace(this, data);
                //set the tvStartPlace to the place name
                tvEndPlace.setText(place.getName());
                toId = place.getId();
                toPlace=place;
                Log.w("TAG-onActivityResult", "Place: " + place.getName());
                setMap();
                //autocompleteFragmentEditTextTo.setText(place.getName());
                // if something went wrong
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.w("TAG", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    //create time picker
    public void showTimePicker() {
        final Calendar myCalender = Calendar.getInstance();
//        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
//        int minute = myCalender.get(Calendar.MINUTE);

        //listener for the time picker dialog
        //set the time variables according to the user election
        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    myCalender.set(Calendar.MINUTE, minute);
                    chosenHour = hourOfDay;
                    chosenMinute = minute;
                    tvDepartureTime.setText(hourOfDay + ":" + minute);


                }
            }
        };

        //create TimePicherDialog object
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, chosenHour, chosenMinute, true);

        //TimePickerDialog timePickerDialog = new TimePickerDialog(this,, myTimeListener, hour, minute, true);
        timePickerDialog.setTitle("Choose hour:");

        //timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();

    }

    //create Date picker
    // set the date date variables according to the user election
    public void showDatePicker() {
        final Calendar myCalender = Calendar.getInstance();
//        int year=myCalender.get(Calendar.YEAR);
//        int month=myCalender.get(Calendar.MONTH);
//        int dayOfMonth=myCalender.get(Calendar.DAY_OF_MONTH);

        //create the listener for the dialog
        DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalender.set(Calendar.YEAR, year);
                myCalender.set(Calendar.MONTH, month);
                myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                chosenYear = year;
                chosenMonth = month + 1;
                chosenDayOfMonth = dayOfMonth;
                tvDate.setText(dayOfMonth + "/" + chosenMonth + "/" + year);

            }
        };

        //create the dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, myDateListener, chosenYear, chosenMonth-1, chosenDayOfMonth);
        //present the dialog
        datePickerDialog.show();

    }

    //set firebase variables
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //func that change the chosen time and date to milisec
    private boolean dataToMilSec() {
        boolean isMilSec = false;
        String myDate = chosenYear + "/" + chosenMonth + "/" + chosenDayOfMonth + " " + chosenHour + ":" + chosenMinute + ":00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            Date date = sdf.parse(myDate);
            long millis = date.getTime();
            departureTime = millis;
            isMilSec = true;
        } catch (Exception e) {
            Log.w("TAG", e);
        }
        return isMilSec;

    }

    //upload new trip to firebase
    private void postNewTrip() {
        if (dataToMilSec()) {
            //Log.w("FROM", from);
            Log.w("TO", toId + "");
            if(fromId==null||toId==null){
                Toast.makeText(this,"some details are missing",Toast.LENGTH_LONG).show();
            }
            else{
                final String uniqueID = UUID.randomUUID().toString();
                final Trip newTrip = new Trip(uniqueID,fromId,toId, departureTime, numberOfTrempists, userAuth.getUid(),null);
                Query myGroup=myRef;
                myGroup.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Group group=dataSnapshot.getValue(Group.class);
                        List<Trip> trips=group.getTrips();
                        if(trips!=null){
                            myRef.child("trips").child(trips.size()+"").setValue(newTrip);
                        }
                        else{
                            trips=new ArrayList<>();
                            trips.add(newTrip);
                            myRef.child("trips").setValue(trips);
                        }
                        Toast.makeText(CreateNewTripActivity.this,"You have just created a new Trip",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CreateNewTripActivity.this,MainActivity.class));

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

        }
    }

    //request permission for ACCESS_FINE_LOCATION
    private void requestPermissions() {
        Log.w("in set request", "here");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.w("need permission", "here");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            hasPermissions = true;
        }

    }


    //handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    hasPermissions = true;
                    //setCurrentPlace();

                } else {

                    hasPermissions = false;
                }
                return;
            }

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //set the current place to the most Likelihood place according to google places
    private void setCurrentPlace() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
        }else{
            Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                    PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                    double maxLikelihoodScore=0;
                    Place maxLikelihoodPlace = null;
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        if(maxLikelihoodScore<placeLikelihood.getLikelihood()){
                            maxLikelihoodScore=placeLikelihood.getLikelihood();
                            maxLikelihoodPlace=placeLikelihood.getPlace().freeze();
                        }
                        Log.i("TAG", String.format("Place '%s' has likelihood: %g",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));

                    }
                    fromId=maxLikelihoodPlace.getId();
                    tvStartPlace.setText(maxLikelihoodPlace.getName());
                    fromPlace=maxLikelihoodPlace;
                    likelyPlaces.release();
                    setMap();
                }
            });
        }

    }

    private void setMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.clear();
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        if(fromPlace!=null&&toPlace!=null){
            LatLng from = new LatLng(fromPlace.getLatLng().latitude,fromPlace.getLatLng().longitude);
            LatLng to = new LatLng(toPlace.getLatLng().latitude,toPlace.getLatLng().longitude);
            googleMap.addMarker(new MarkerOptions().position(from)
                    .title(""));
            googleMap.addMarker(new MarkerOptions().position(to)
                    .title(""));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(from);
            builder.include(to);
            LatLngBounds bounds = builder.build();
            int padding = 130; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.moveCamera(cu);
            googleMap.animateCamera(cu);
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(from));
//            float zoomLevel = (float) 11.0;
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(from, zoomLevel));

        }else{
            if(fromPlace!=null){
                LatLng from = new LatLng(fromPlace.getLatLng().latitude,fromPlace.getLatLng().longitude);
                googleMap.addMarker(new MarkerOptions().position(from)
                        .title(""));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(from));
                float zoomLevel = (float) 11.0;
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(from, zoomLevel));
            }

        }

    }
}
