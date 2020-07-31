package com.bustracking.model.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.bustracking.model.busmodels.BusLine;
import com.bustracking.model.busmodels.BusStop;
import com.bustracking.model.usermodels.User;
import com.bustracking.model.usermodels.UserLocation;
import com.bustracking.ui.fragment.HomeFragmentNewest;
import com.bustracking.ui.repositories.HomeFragmentRepo;
import com.bustracking.utils.FirebaseListenerInterface;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class DataViewModel extends ViewModel
{
    private static final String TAG = "DataViewModel";
    private MutableLiveData<ArrayList<User>> mUserListLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<UserLocation>> mUserLocationsLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<BusStop>> mBusStopsLiveData = new MutableLiveData<>();

    private ArrayList<UserLocation> userLocationInitial = new ArrayList<>();
    private MutableLiveData<UserLocation> userLocation = new MutableLiveData<>();

    private HomeFragmentRepo homeFragmentRepo;
    private HomeFragmentNewest displayFragment;
    private BusLine dataTemplate;

    public void getUserLocationInitial(FirebaseListenerInterface fli, Context context)
    {
        homeFragmentRepo.getUserDetails(fli, context);
        Log.d(TAG, "getUserLocationInitial called");
    }

    public void init(HomeFragmentNewest displayFragment, BusLine dataTemplate)
    {
        this.displayFragment = displayFragment;
        this.dataTemplate = dataTemplate;
        this.homeFragmentRepo = HomeFragmentRepo.getInstance(displayFragment, dataTemplate);
    }

    public MutableLiveData<ArrayList<User>> getmUserListLiveData()
    {
        homeFragmentRepo.getmUserListLiveData().observe(displayFragment, new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                mUserListLiveData.setValue(users);
            }
        });
        return mUserListLiveData;
    }

    public MutableLiveData<ArrayList<UserLocation>> getmUserLocationsLiveData()
    {
        homeFragmentRepo.getmUserLocationsLiveData().observe(displayFragment, new Observer<ArrayList<UserLocation>>() {
            @Override
            public void onChanged(ArrayList<UserLocation> userLocations)
            {
                Log.d(TAG, "Detected change in user locations in repository");

                mUserLocationsLiveData.setValue(userLocations);

                for (UserLocation userLocationGet: userLocations)
                {
                    if(userLocationGet.getUser() != null)
                    {
                        if(userLocationGet.getUser().getUser_id().equals(FirebaseAuth.getInstance().getUid()))
                            userLocation.setValue(userLocationGet);
                    }
                }
            }
        });

        return mUserLocationsLiveData;
    }

    public MutableLiveData<ArrayList<BusStop>> getmBusStopsLiveData()
    {
        homeFragmentRepo.getmBusStopsLiveData().observe(displayFragment, new Observer<ArrayList<BusStop>>() {
            @Override
            public void onChanged(ArrayList<BusStop> busStops) {
                Log.d(TAG, "Detected change in bus stops in repository");

                mBusStopsLiveData.setValue(busStops);
            }
        });
        return mBusStopsLiveData;
    }

    public MutableLiveData<UserLocation> getUserLocation()
    {
        return userLocation;
    }

    public DataViewModel()
    {
    }

}
