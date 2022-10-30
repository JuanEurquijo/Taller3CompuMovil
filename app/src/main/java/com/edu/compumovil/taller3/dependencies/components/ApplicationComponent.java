package com.edu.compumovil.taller3.dependencies.components;

import com.edu.compumovil.taller3.activities.Activity;
import com.edu.compumovil.taller3.activities.MainActivity;
import com.edu.compumovil.taller3.dependencies.modules.AlertsModule;
import com.edu.compumovil.taller3.dependencies.modules.CameraModule;
import com.edu.compumovil.taller3.dependencies.modules.LocationModule;
import com.edu.compumovil.taller3.dependencies.modules.PermissionModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AlertsModule.class, CameraModule.class, PermissionModule.class, LocationModule.class})
public interface ApplicationComponent {
    void inject(Activity activity);
    void inject(MainActivity activity);
}
