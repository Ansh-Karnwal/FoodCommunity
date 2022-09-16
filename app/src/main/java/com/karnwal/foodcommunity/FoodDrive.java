package com.karnwal.foodcommunity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class FoodDrive implements Serializable, Parcelable {

    private String name;
    private String address;
    private String foodList;
    private String calendar;
    private String additionalInformation = null;
    private String UUID;
    private String ownerUID;

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getFoodList() {
        return foodList;
    }

    public String getCalendar() {
        return calendar;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFoodList(String foodList) {
        this.foodList = foodList;
    }

    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getOwnerUID() {
        return ownerUID;
    }

    public void setOwnerUID(String ownerUID) {
        this.ownerUID = ownerUID;
    }

    @Override
    public String toString() {
        return "FoodDrive{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", foodList='" + foodList + '\'' +
                ", calendar='" + calendar + '\'' +
                ", additionalInformation='" + additionalInformation + '\'' +
                ", UUID='" + UUID + '\'' +
                ", ownerUID='" + ownerUID + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeString(this.foodList);
        dest.writeString(this.calendar);
        dest.writeString(this.additionalInformation);
        dest.writeString(this.UUID);
        dest.writeString(this.ownerUID);
    }

    public void readFromParcel(Parcel source) {
        this.name = source.readString();
        this.address = source.readString();
        this.foodList = source.readString();
        this.calendar = source.readString();
        this.additionalInformation = source.readString();
        this.UUID = source.readString();
        this.ownerUID = source.readString();
    }

    public FoodDrive() {
    }

    protected FoodDrive(Parcel in) {
        this.name = in.readString();
        this.address = in.readString();
        this.foodList = in.readString();
        this.calendar = in.readString();
        this.additionalInformation = in.readString();
        this.UUID = in.readString();
        this.ownerUID = in.readString();
    }

    public static final Parcelable.Creator<FoodDrive> CREATOR = new Parcelable.Creator<FoodDrive>() {
        @Override
        public FoodDrive createFromParcel(Parcel source) {
            return new FoodDrive(source);
        }

        @Override
        public FoodDrive[] newArray(int size) {
            return new FoodDrive[size];
        }
    };
}