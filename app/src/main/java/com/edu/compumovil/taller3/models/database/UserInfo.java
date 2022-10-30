package com.edu.compumovil.taller3.models.database;

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
    private long numId;
    private double latitude;
    private double longitude;
    private long createdAt;
    private long lastLogin;
}
