package com.edu.compumovil.taller3;

import android.app.Application;

import com.edu.compumovil.taller3.dependencies.components.ApplicationComponent;
import com.edu.compumovil.taller3.dependencies.components.DaggerApplicationComponent;
import com.edu.compumovil.taller3.dependencies.modules.GeoInfoModule;
import com.edu.compumovil.taller3.dependencies.modules.GeocoderModule;
import com.edu.compumovil.taller3.dependencies.modules.LocationModule;

import lombok.Getter;

@Getter
public class App extends Application {
    ApplicationComponent appComponent = DaggerApplicationComponent.builder()
            .locationModule(new LocationModule(this))
            .geocoderModule(new GeocoderModule(this))
            .geoInfoModule(new GeoInfoModule(this))
            .build();
}
