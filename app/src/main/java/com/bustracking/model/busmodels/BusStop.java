package com.bustracking.model.busmodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class BusStop implements Parcelable
{
    private GeoPoint position;
    private @ServerTimestamp
    Date timestamp;

    private String name, stationID;

    public BusStop(GeoPoint position, Date timestamp, String name, String stationID) {
        this.position = position;
        this.timestamp = timestamp;
        this.name = name;
        this.stationID = stationID;
    }

    public String getStationID() {
        return stationID;
    }

    public void setStationID(String stationID) {
        this.stationID = stationID;
    }

    public BusStop() {
    }

    protected BusStop(Parcel in)
    {
        name = in.readString();
        Double lat = in.readDouble();
        Double lng = in.readDouble();
        position = new GeoPoint(lat, lng);
        stationID = in.readString();
    }

    public static final Creator<BusStop> CREATOR = new Creator<BusStop>() {
        @Override
        public BusStop createFromParcel(Parcel in) {
            return new BusStop(in);
        }

        @Override
        public BusStop[] newArray(int size) {
            return new BusStop[size];
        }
    };

    public GeoPoint getPosition() {
        return position;
    }

    public void setPosition(GeoPoint position) {
        this.position = position;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeDouble(position.getLatitude());
        parcel.writeDouble(position.getLongitude());
        parcel.writeString(stationID);
    }


}
