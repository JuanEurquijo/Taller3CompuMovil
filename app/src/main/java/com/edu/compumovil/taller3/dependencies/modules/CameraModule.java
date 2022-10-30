package com.edu.compumovil.taller3.dependencies.modules;

import com.edu.compumovil.taller3.services.CameraService;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class CameraModule {
    @Singleton
    @Provides
    public CameraService provideCameraService() {
        return new CameraService();
    }
}
