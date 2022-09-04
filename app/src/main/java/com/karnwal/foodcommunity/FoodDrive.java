package com.karnwal.foodcommunity;

public class FoodDrive {
    private String name;
    private String address;
    private String foodList;
    private String calendar;
    private String additionalInformation = null;

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

    @Override
    public String toString() {
        return "FoodDrive{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", foodList='" + foodList + '\'' +
                ", calendar='" + calendar + '\'' +
                ", additionalInformation='" + additionalInformation + '\'' +
                '}';
    }
}