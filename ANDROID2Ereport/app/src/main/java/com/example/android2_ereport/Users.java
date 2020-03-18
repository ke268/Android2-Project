package com.example.android2_ereport;

import android.widget.TextView;

public class Users {

    String Id;
    String Name;
    String Number;
    String Image;
    String Latitude;
    String Longitude;



    public Users(){


    }
    public Users(String id, String name, String number, String image, String latitude, String longitude) {
        Id = id;
        Name = name;
        Number = number;
        Image = image;
        Latitude = latitude;
        Longitude = longitude;
    }

    public String getId() {
        return Id;
    }

    public String getname() {
        return Name;
    }

    public String getNumber() {
        return Number;
    }

    public String getImage() {
        return Image;
    }

    public String getLatitude() {
        return Latitude;
    }

    public String getLongitude() {
        return Longitude;
    }
}
