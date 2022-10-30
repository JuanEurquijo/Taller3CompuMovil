package com.edu.compumovil.taller3.dependencies.modules;

import com.edu.compumovil.taller3.utils.PermissionHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PermissionModule {

    @Singleton
    @Provides
    public PermissionHelper providePermissionHelper() {
        return new PermissionHelper();
    }
}
