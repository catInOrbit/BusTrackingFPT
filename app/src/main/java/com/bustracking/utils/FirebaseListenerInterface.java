package com.bustracking.utils;

import com.bustracking.model.busmodels.BusLine;
import com.bustracking.model.busmodels.BusRoute;
import com.bustracking.model.busmodels.BusStop;
import com.bustracking.model.usermodels.User;
import com.bustracking.model.usermodels.UserLocation;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public interface FirebaseListenerInterface
{
    public void onCallbackAllBusLineGot(ArrayList<BusLine> busLines);
    public void onCallbackAllUserLocationGot(ArrayList<UserLocation> mUserLocations);
    public void onCallBackAllBusStopsGot(ArrayList<BusStop> busStops);

}
