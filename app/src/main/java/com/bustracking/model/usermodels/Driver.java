package com.bustracking.model.usermodels;

import android.os.Parcel;
import android.os.Parcelable;

public class Driver implements Parcelable
{
    private String cititzenID, driverID,drivingLicense;
    private User user;

    public Driver(String cititzenID, String driverID, String drivingLicense, User user) {
        this.cititzenID = cititzenID;
        this.driverID = driverID;
        this.drivingLicense = drivingLicense;
        this.user = user;
    }

    public Driver() {
    }

    protected Driver(Parcel in) {
        cititzenID = in.readString();
        driverID = in.readString();
        drivingLicense = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Driver> CREATOR = new Creator<Driver>() {
        @Override
        public Driver createFromParcel(Parcel in) {
            return new Driver(in);
        }

        @Override
        public Driver[] newArray(int size) {
            return new Driver[size];
        }
    };

    public String getCititzenID() {
        return cititzenID;
    }

    public void setCititzenID(String cititzenID) {
        this.cititzenID = cititzenID;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public String getDrivingLicense() {
        return drivingLicense;
    }

    public void setDrivingLicense(String drivingLicense) {
        this.drivingLicense = drivingLicense;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(cititzenID);
        parcel.writeString(driverID);
        parcel.writeString(drivingLicense);
        parcel.writeParcelable(user, i);
    }
}
