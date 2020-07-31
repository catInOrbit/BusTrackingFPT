package com.bustracking.utils;

import com.bustracking.model.usermodels.User;
import com.bustracking.model.usermodels.UserLocation;

import java.util.ArrayList;

public class UserAndLocation
{
    private User user;
    private ArrayList<UserLocation> userLocations;
    private ArrayList<User> mUserList = new ArrayList<>();

    public ArrayList<User> getmUserList() {
        return mUserList;
    }

    public void setmUserList(ArrayList<User> mUserList) {
        this.mUserList = mUserList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<UserLocation> getUserLocations() {
        return userLocations;
    }

    public void setUserLocations(ArrayList<UserLocation> userLocations) {
        this.userLocations = userLocations;
    }

    public UserAndLocation() {
    }
}
