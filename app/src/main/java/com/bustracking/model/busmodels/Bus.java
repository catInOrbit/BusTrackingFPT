package com.bustracking.model.busmodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.bustracking.model.usermodels.User;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Bus implements Parcelable
{
    private GeoPoint geo_point;
    private @ServerTimestamp
    Date timestamp;
    private String licensePlate,busBranch, currentRoute, currentLine;
    private User driver;

    public Bus(GeoPoint geo_point, Date timestamp, String licensePlate, String busBranch, String currentRoute, String currentLine, User driver) {
        this.geo_point = geo_point;
        this.timestamp = timestamp;
        this.licensePlate = licensePlate;
        this.busBranch = busBranch;
        this.currentRoute = currentRoute;
        this.currentLine = currentLine;
        this.driver = driver;
    }

    public Bus() {
    }

    protected Bus(Parcel in) {
        licensePlate = in.readString();
        busBranch = in.readString();
        currentRoute = in.readString();
        currentLine = in.readString();
        driver = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Bus> CREATOR = new Creator<Bus>() {
        @Override
        public Bus createFromParcel(Parcel in) {
            return new Bus(in);
        }

        @Override
        public Bus[] newArray(int size) {
            return new Bus[size];
        }
    };

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getBusBranch() {
        return busBranch;
    }

    public void setBusBranch(String busBranch) {
        this.busBranch = busBranch;
    }

    public String getCurrentRoute() {
        return currentRoute;
    }

    public void setCurrentRoute(String currentRoute) {
        this.currentRoute = currentRoute;
    }

    public String getCurrentLine() {
        return currentLine;
    }

    public void setCurrentLine(String currentLine) {
        this.currentLine = currentLine;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(licensePlate);
        parcel.writeString(busBranch);
        parcel.writeString(currentRoute);
        parcel.writeString(currentLine);
        parcel.writeParcelable(driver, i);
    }
}
