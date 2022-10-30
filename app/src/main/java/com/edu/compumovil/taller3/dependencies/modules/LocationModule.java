package com.edu.compumovil.taller3.dependencies.modules;

import android.app.Application;

import com.edu.compumovil.taller3.services.LocationService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lombok.AllArgsConstructor;

@Module
@AllArgsConstructor
public class LocationModule {
    private final Application application;

    @Provides
    @Singleton
    Application providesApplication(){
        return application;
    }

    @Provides
    public LocationService provideLocationService() {
        return new LocationService(application.getApplicationContext());
    }
}
