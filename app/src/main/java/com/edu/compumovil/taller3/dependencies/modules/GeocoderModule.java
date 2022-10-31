package com.edu.compumovil.taller3.dependencies.modules;

import android.app.Application;

import com.edu.compumovil.taller3.services.GeocoderService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lombok.AllArgsConstructor;

@Module
@AllArgsConstructor
public class GeocoderModule {
    private final Application application;

    @Provides
    @Singleton
    Application providesApplication(){
        return application;
    }

    @Provides
    public GeocoderService provideGeoCoderService() {
        return new GeocoderService(application.getApplicationContext());
    }
}
