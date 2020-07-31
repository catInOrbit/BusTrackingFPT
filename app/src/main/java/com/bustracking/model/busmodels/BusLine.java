package com.bustracking.model.busmodels;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;


public class BusLine implements Parcelable
{
    private String lineName, lineID;
    private Date timestamp;

    private ArrayList<BusStop> busStops = new ArrayList<>();

    protected BusLine(Parcel in) {
        lineName = in.readString();
        lineID = in.readString();
        busStops = in.createTypedArrayList(BusStop.CREATOR);
        busRoutes = in.createTypedArrayList(BusRoute.CREATOR);
    }

    public static final Creator<BusLine> CREATOR = new Creator<BusLine>() {
        @Override
        public BusLine createFromParcel(Parcel in) {
            return new BusLine(in);
        }

        @Override
        public BusLine[] newArray(int size) {
            return new BusLine[size];
        }
    };

    public ArrayList<BusStop> getBusStops() {
        return busStops;
    }

    public void setBusStops(ArrayList<BusStop> busStops) {
        this.busStops = busStops;
    }

    @Exclude
    private ArrayList<BusRoute> busRoutes = new ArrayList<>();

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getLineID() {
        return lineID;
    }

    public void setLineID(String lineID) {
        this.lineID = lineID;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<BusRoute> getBusRoutes() {
        return busRoutes;
    }

    public void setBusRoutes(ArrayList<BusRoute> busRoutes) {
        this.busRoutes = busRoutes;
    }

    public BusLine() {
    }

    @Override
    public String toString() {
        return "BusLine{" +
                "lineName='" + lineName + '\'' +
                ", line_Id='" + lineID + '\'' +
                ", timestamp=" + timestamp +
                ", busRoutes=" + busRoutes +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(lineName);
        parcel.writeString(lineID);
        parcel.writeTypedList(busStops);
        parcel.writeTypedList(busRoutes);
    }
}
