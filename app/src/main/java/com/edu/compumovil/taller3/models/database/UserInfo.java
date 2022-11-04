package com.edu.compumovil.taller3.models.database;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private String name;
    private String lastname;
    private boolean available;
    private String imagePath;
    private long numId;
    private double latitude;
    private double longitude;
    private long createdAt;
    private long lastLogin;

    public UserInfo(String name, String lastname, long numId, double latitude, double longitude, long createdAt, long lastLogin) {
        this.name = name;
        this.lastname = lastname;
        this.numId = numId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    public UserInfo(String name, String lastname, String imagePath) {
        this.imagePath = imagePath;
        this.name = name;
        this.lastname = lastname;
    }
}
