package com.bustracking.model.busmodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;

public class BusRoute implements Parcelable
{
    private ArrayList<BusRoute> busRoutes;
    @ServerTimestamp
    private Date timestamp, timeStart, timeEnd;
    private String routeName;
    private String currentBusID;

    public String getCurrentBusID() {
        return currentBusID;
    }

    public void setCurrentBusID(String currentBusID) {
        this.currentBusID = currentBusID;
    }

    public String getCurrentDriverID() {
        return currentDriverID;
    }

    public void setCurrentDriverID(String currentDriverID) {
        this.currentDriverID = currentDriverID;
    }

    private String currentDriverID;


    protected BusRoute(Parcel in) {
        busRoutes = in.createTypedArrayList(BusRoute.CREATOR);
        routeName = in.readString();

        timestamp = new Date(in.readLong());
        timeStart = new Date(in.readLong());
        timeEnd = new Date(in.readLong());
    }

    public static final Creator<BusRoute> CREATOR = new Creator<BusRoute>() {
        @Override
        public BusRoute createFromParcel(Parcel in) {
            return new BusRoute(in);
        }

        @Override
        public BusRoute[] newArray(int size) {
            return new BusRoute[size];
        }
    };

    public BusRoute(ArrayList<BusRoute> busRoutes, Date timestamp, Date timeStart, Date timeEnd, String routeName, String currentBusID, String currentDriverID) {
        this.busRoutes = busRoutes;
        this.timestamp = timestamp;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.routeName = routeName;
        this.currentBusID = currentBusID;
        this.currentDriverID = currentDriverID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(busRoutes);
        parcel.writeString(routeName);
        parcel.writeString(currentBusID);
        parcel.writeString(currentDriverID);
        parcel.writeLong(timeStart.getTime());
        parcel.writeLong(timeEnd.getTime());
    }

    public ArrayList<BusRoute> getBusRoutes() {
        return busRoutes;
    }

    public void setBusRoutes(ArrayList<BusRoute> busRoutes) {
        this.busRoutes = busRoutes;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public static Creator<BusRoute> getCREATOR() {
        return CREATOR;
    }

    public BusRoute() {
    }
}
