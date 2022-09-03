package com.karnwal.foodcommunity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FoodDrive {
    private String name;
    private String address;
    private String foodList;
    private Calendar calendar;
    private String additionalInformation = null;

    public FoodDrive(String name, String address, String foodList, Calendar calendar) {
        this.name = name;
        this.address = address;
        this.foodList = foodList;
        this.calendar = calendar;
    }

    public FoodDrive(String name, String address, String foodList, Calendar calendar, String additionalInformation) {
        this.name = name;
        this.address = address;
        this.foodList = foodList;
        this.calendar = calendar;
        this.additionalInformation = additionalInformation;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getFoodList() {
        return foodList;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public String getDate() {
        Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String inActiveDate = null;
        try {
            inActiveDate = simpleDateFormat.format(date);
            return inActiveDate;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return "";
        }
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

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }
}