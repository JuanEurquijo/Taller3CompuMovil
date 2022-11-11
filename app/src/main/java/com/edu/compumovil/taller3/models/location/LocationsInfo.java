package com.edu.compumovil.taller3.models.location;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class LocationsInfo {
    public ArrayList<GeoInfo> locations;
}
