package com.edu.compumovil.taller3.dependencies.components;

import androidx.fragment.app.Fragment;

import com.edu.compumovil.taller3.activities.Activity;
import com.edu.compumovil.taller3.activities.FollowMapFragment;
import com.edu.compumovil.taller3.activities.MapFragment;
import com.edu.compumovil.taller3.dependencies.modules.AlertsModule;
import com.edu.compumovil.taller3.dependencies.modules.CameraModule;
import com.edu.compumovil.taller3.dependencies.modules.GeoInfoModule;
import com.edu.compumovil.taller3.dependencies.modules.GeocoderModule;
import com.edu.compumovil.taller3.dependencies.modules.LocationModule;
import com.edu.compumovil.taller3.dependencies.modules.PermissionModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AlertsModule.class, CameraModule.class, PermissionModule.class,
        LocationModule.class, GeocoderModule.class, GeoInfoModule.class})
public interface ApplicationComponent {
    void inject(Activity activity);

    void inject(MapFragment fragment);
    void inject(FollowMapFragment fragment);
}
