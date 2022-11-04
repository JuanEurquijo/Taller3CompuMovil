package com.edu.compumovil.taller3.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.edu.compumovil.taller3.R;
import com.edu.compumovil.taller3.activities.AuthenticatedActivity;
import com.edu.compumovil.taller3.databinding.ActivityFollowBinding;
import com.edu.compumovil.taller3.databinding.ActivityLoginBinding;
import com.edu.compumovil.taller3.models.database.DatabaseRoutes;
import com.edu.compumovil.taller3.models.database.UserInfo;
import com.edu.compumovil.taller3.utils.PermissionHelper;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class FollowActivity extends AuthenticatedActivity {

    public static final String TAG = FollowActivity.class.getName();
    ActivityFollowBinding binding;

    private String friendUuid = null;
    private FollowMapFragment fragment = null;
    private ValueEventListener listener = null;

    private UserInfo friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFollowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        friendUuid = getIntent().getStringExtra("user");
        alertsHelper.shortToast(this, "Estás siguiendo al usuario");

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friend = snapshot.getValue(UserInfo.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ", error.toException());
            }
        };

        locationService.setLocationCallback(new LocationCallback() {
            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                // Put on map
                fragment = binding.fragmentFollowContainerView.getFragment();
                LatLng location = new LatLng(
                        Objects.requireNonNull(locationResult.getLastLocation()).getLatitude(),
                        locationResult.getLastLocation().getLongitude()
                );

                fragment.updatePositionOnMap(FollowMapFragment.WHO.USER,
                        location, R.drawable.ic_baseline_circle_24);

                if (friend != null)
                    fragment.updatePositionOnMap(FollowMapFragment.WHO.FRIEND, new LatLng(
                            friend.getLatitude(),
                            friend.getLongitude()
                    ), R.drawable.ic_baseline_circle_red);

                // Update location
                mDatabase.getReference(DatabaseRoutes.getUser(currentUser.getUid()))
                        .child("latitude").setValue(location.latitude);
                mDatabase.getReference(DatabaseRoutes.getUser(currentUser.getUid()))
                        .child("longitude").setValue(location.longitude);
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

        mDatabase.getReference(DatabaseRoutes.getUser(friendUuid))
                .addValueEventListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationService.stopLocation();

        mDatabase.getReference(DatabaseRoutes.getUser(friendUuid))
                .removeEventListener(listener);
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