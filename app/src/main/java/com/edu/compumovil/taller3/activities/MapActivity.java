package com.edu.compumovil.taller3.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.edu.compumovil.taller3.App;
import com.edu.compumovil.taller3.R;
import com.edu.compumovil.taller3.databinding.ActivityMapBinding;
import com.edu.compumovil.taller3.models.database.DatabaseRoutes;
import com.edu.compumovil.taller3.services.NotificationWorker;
import com.edu.compumovil.taller3.utils.PermissionHelper;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class MapActivity extends AuthenticatedActivity {

    public static final String TAG = MapActivity.class.getName();
    private ActivityMapBinding binding;

    MapFragment fragment = null;

    boolean userIsActive = false;
    Function<Boolean, Boolean> userAvailabilityCallback = (userActive -> {
        userIsActive = userActive;
        setUserState(userActive);
        return userActive;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Create the view
        super.onCreate(savedInstanceState);
        ((App) getApplicationContext()).getAppComponent().inject(this);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set location callback
        locationService.setLocationCallback(new LocationCallback() {
            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i(TAG, "onLocationResult: " + Objects.requireNonNull(locationResult.getLastLocation()));
                fragment = binding.fragmentContainerView.getFragment();
                fragment.updateUserPositionOnMap(locationResult);
                fragment.onUserActiveCallback(userAvailabilityCallback);

                // Update location
                Location location = locationResult.getLastLocation();
                mDatabase.getReference(DatabaseRoutes.getUser(currentUser.getUid())).child("latitude").setValue(location.getLatitude());
                mDatabase.getReference(DatabaseRoutes.getUser(currentUser.getUid())).child("longitude").setValue(location.getLongitude());
            }
        });

        // Create worker
        WorkRequest seekActiveUsersRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class).build();
        WorkManager.getInstance(this).enqueue(seekActiveUsersRequest);
        Log.d(TAG, "Starting notification worker");
    }

    @Override
    protected void onStart() {
        super.onStart();
        permissionHelper.getLocationPermission(this);
        if (permissionHelper.isMLocationPermissionGranted()) {
            locationService.startLocation();
        }

        if (userIsActive)
            setUserState(true);
    }

    protected void setUserState(boolean active) {
        if (active) {
            alertsHelper.shortToast(this, "Usuario activo");
            mDatabase.getReference(DatabaseRoutes.getUser(currentUser.getUid())).child("available").setValue(true);
        } else {
            alertsHelper.shortToast(this, "Usuario inactivo");
            mDatabase.getReference(DatabaseRoutes.getUser(currentUser.getUid())).child("available").setValue(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationService.stopLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setUserState(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.PERMISSIONS_LOCATION) {
            permissionHelper.getLocationPermission(this);
            if (permissionHelper.isMLocationPermissionGranted()) {
                locationService.startLocation();
            }
        }
    }

    @Override
    protected void signOut() {
        fragment.beforeDestruction();
        setUserState(false);
        super.signOut();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Cerrar Sesión");
        builder.setMessage(R.string.textSignOut);

        builder.setPositiveButton("SI", (dialog, which) -> signOut());
        builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());

        builder.create();
        builder.show();
    }

}