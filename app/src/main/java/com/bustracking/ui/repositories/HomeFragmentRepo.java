package com.bustracking.ui.repositories;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.bustracking.R;
import com.bustracking.UserClient;
import com.bustracking.model.busmodels.BusLine;
import com.bustracking.model.busmodels.BusStop;
import com.bustracking.model.functionalmodels.ClusterMarker;
import com.bustracking.model.usermodels.User;
import com.bustracking.model.usermodels.UserLocation;
import com.bustracking.model.viewmodels.DataViewModel;
import com.bustracking.ui.fragment.HomeFragmentNewest;
import com.bustracking.utils.FirebaseListenerInterface;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragmentRepo
{
    private static final String TAG = "HomeFragmentRepo";
    private static HomeFragmentRepo homeFragmentRepoInstance;
    private UserLocation mUserLocation;
    private final FirebaseFirestore mDb;
    private HomeFragmentNewest fragment;
    private BusLine dataToGet;

    private MutableLiveData<ArrayList<User>> mUserListLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<UserLocation>> mUserLocationsLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<BusStop>> mBusStopsLiveData = new MutableLiveData<>();
    private MutableLiveData<UserLocation> mCurrentUserLocation = new MutableLiveData<>();

    public MutableLiveData<ArrayList<User>> getmUserListLiveData() {
        return mUserListLiveData;
    }

    public void setmUserListLiveData(MutableLiveData<ArrayList<User>> mUserListLiveData) {
        this.mUserListLiveData = mUserListLiveData;
    }

    public MutableLiveData<ArrayList<UserLocation>> getmUserLocationsLiveData() {
        return mUserLocationsLiveData;
    }

    public void setmUserLocationsLiveData(MutableLiveData<ArrayList<UserLocation>> mUserLocationsLiveData) {
        this.mUserLocationsLiveData = mUserLocationsLiveData;
    }

    public MutableLiveData<ArrayList<BusStop>> getmBusStopsLiveData() {
        return mBusStopsLiveData;
    }

    public void setmBusStopsLiveData(MutableLiveData<ArrayList<BusStop>> mBusStopsLiveData) {
        this.mBusStopsLiveData = mBusStopsLiveData;
    }

    public static HomeFragmentRepo getInstance(HomeFragmentNewest fragment, BusLine dataToGet)
    {
        if(homeFragmentRepoInstance == null)
            homeFragmentRepoInstance = new HomeFragmentRepo(fragment, dataToGet);
        return homeFragmentRepoInstance;
    }

    public HomeFragmentRepo(HomeFragmentNewest fragment, BusLine dataToGet) {
        this.fragment = fragment;
        this.dataToGet = dataToGet;
        this.mDb = FirebaseFirestore.getInstance();
    }

    public void getUserDetails(FirebaseListenerInterface fli, Context context)
    {
        Log.d(TAG, "getUserDetails is running " + mUserLocation);
        if(mUserLocation == null){
            mUserLocation = new UserLocation();
            DocumentReference userRef = mDb.collection(context.getString(R.string.collection_users))
                    .document(FirebaseAuth.getInstance().getUid());

            userRef.get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        User user = task.getResult().toObject(User.class);
                        mUserLocation.setUser(user);
                        Log.d(TAG, "onComplete: successfully set the user client " + mUserLocation);
                        ((UserClient)(context.getApplicationContext())).setUser(user);
                    }
                }
            });

//TODO: Commented out working work
            getAllUsers(fli, context);
        }
    }

    private void getAllUsers(FirebaseListenerInterface fli, Context context){

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mDb.setFirestoreSettings(settings);

        //Get collection of users
        CollectionReference usersRef = mDb
                .collection(context.getString(R.string.collection_users));


        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<User> tempUserList = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    User user = doc.toObject(User.class);
                    tempUserList.add(user);
                }

                mUserListLiveData.setValue(tempUserList);
                getUsersLocation(tempUserList, fli, context);
            }
        });

        Log.d(TAG, "Getting all users: number of users retrieved: " );
    }

    private void getUsersLocation(ArrayList<User> usersPass, FirebaseListenerInterface fli, Context context)
    {

        CollectionReference userLocationsRef = mDb.collection(context.getString(R.string.collection_user_locations));
        userLocationsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<UserLocation> tempUserLocations = new ArrayList<>();

                for (QueryDocumentSnapshot doc : task.getResult())
                {
                    tempUserLocations.add(doc.toObject(UserLocation.class));
                }

                fli.onCallbackAllUserLocationGot(tempUserLocations);
                mUserLocationsLiveData.setValue(tempUserLocations);

                Log.d(TAG, "repo Getting location " + tempUserLocations.size());


                //TODO: fillHomeFragmentData
                getAllBusStops(context);
            }
        });


        userLocationsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
            {
                ArrayList<UserLocation> tempUserLocations = new ArrayList<>();
                if(queryDocumentSnapshots != null)
                {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots)
                    {
                        tempUserLocations.add(doc.toObject(UserLocation.class));
                    }

                    Log.d(TAG, "repo detected user location change ");

                    mUserLocationsLiveData.setValue(tempUserLocations);
                }
            }
        });
    }

    private void getAllBusStops(Context context)
    {

        //TODO: DEMO BUS STOPS DATA --> STATIONS ARE IN BUS LINE COLLECTION
        // -------------------------------------


//        Bundle bundles = fragment.getActivity().getIntent().getExtras();
        ArrayList<BusStop> busStopsFromAdapter = new ArrayList<>();

//        if(bundles != null && bundles.get(fragment.getString(R.string.intent_bus_stops_selectable_adapter)) != null)
//        {
//            busStopsFromAdapter = (ArrayList<BusStop>) bundles.get(fragment.getString(R.string.intent_bus_stops_selectable_adapter));
//        }

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mDb.setFirestoreSettings(settings);
        CollectionReference busStopRef;
        if(dataToGet != null)
        {
            busStopRef = mDb.
                    collection(context.getString(R.string.collection_schedule)).
                    document(dataToGet.getLineID()).
                    collection(context.getString(R.string.collection_bus_stops));
        }

        else
            busStopRef = mDb
                .collection(context.getString(R.string.collection_bus_stops));



        ArrayList<BusStop> finalBusStopsFromAdapter = busStopsFromAdapter;
        busStopRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<BusStop> tempBusStops = new ArrayList<>();

                int i = 0;
                for (QueryDocumentSnapshot doc : task.getResult())
                {
                    BusStop busStop = doc.toObject(BusStop.class);

                    if(!finalBusStopsFromAdapter.isEmpty())
                    {
                        if(busStop.getStationID().equals(finalBusStopsFromAdapter.get(i).getStationID()))
                        {
                            GeoPoint getGeoFirebase = (GeoPoint) doc.get("position");
                            busStop.setPosition(getGeoFirebase);
                            tempBusStops.add(busStop);
                        }
                    }

                    else
                    {
                        GeoPoint getGeoFirebase = (GeoPoint) doc.get("position");
                        busStop.setPosition(getGeoFirebase);
                        tempBusStops.add(busStop);
                    }
                    i++;
                }

                Log.d(TAG, "repo Getting bus stops ");


                mBusStopsLiveData.setValue(tempBusStops);
            }
        });

        busStopRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
            {
                ArrayList<BusStop> tempBusStops = new ArrayList<>();
                if(queryDocumentSnapshots != null)
                {

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots)
                    {
                        BusStop busStop = new BusStop();

                        GeoPoint getGeoFirebase = (GeoPoint) doc.get("position");
                        busStop.setPosition(getGeoFirebase);
                        tempBusStops.add(busStop);
                    }

                    mBusStopsLiveData.setValue(tempBusStops);
                    Log.d(TAG, "repo detects busSTops change ");

                }
            }
        });
        // -------------------------------------
    }










}
