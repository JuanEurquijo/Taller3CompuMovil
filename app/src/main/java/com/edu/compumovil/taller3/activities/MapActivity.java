package com.edu.compumovil.taller3.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.edu.compumovil.taller3.App;
import com.edu.compumovil.taller3.R;
import com.edu.compumovil.taller3.databinding.ActivityMapBinding;
import com.edu.compumovil.taller3.utils.PermissionHelper;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.auth.FirebaseAuth;

public class MapActivity extends AuthenticatedActivity {
    public static final String TAG = MapActivity.class.getName();
    private ActivityMapBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getApplicationContext()).getAppComponent().inject(this);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        locationService.setLocationCallback(new LocationCallback() {
            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i(TAG, "onLocationResult: " + locationResult.getLastLocation().toString());
                MapFragment fragment = binding.fragmentContainerView.getFragment();
                fragment.updateUserPositionOnMap(locationResult);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        permissionHelper.getLocationPermission(this);
        if (permissionHelper.isMLocationPermissionGranted()) {
            locationService.startLocation();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationService.stopLocation();
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
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Cerrar Sesión");
        builder.setMessage(R.string.textSignOut);

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signOut();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create();
        builder.show();
    }

}