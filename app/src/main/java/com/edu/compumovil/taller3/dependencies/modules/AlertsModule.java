package com.edu.compumovil.taller3.dependencies.modules;

import com.edu.compumovil.taller3.utils.AlertsHelper;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AlertsModule {
    @Singleton
    @Provides
    public AlertsHelper provideAlertHelper() {
        return new AlertsHelper();
    }
}
