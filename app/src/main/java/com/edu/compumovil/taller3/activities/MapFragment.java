package com.edu.compumovil.taller3.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.edu.compumovil.taller3.App;
import com.edu.compumovil.taller3.R;
import com.edu.compumovil.taller3.databinding.FragmentMapBinding;
import com.edu.compumovil.taller3.services.GeoInfoFromJsonService;
import com.edu.compumovil.taller3.services.GeocoderService;
import com.edu.compumovil.taller3.utils.AlertsHelper;
import com.edu.compumovil.taller3.utils.BitmapUtils;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonArray;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import javax.inject.Inject;


public class MapFragment extends Fragment {

    @Inject
    GeocoderService geocoderService;
    @Inject
    AlertsHelper alertsHelper;
    @Inject
    GeoInfoFromJsonService geoInfoFromJsonService;

    protected FragmentMapBinding binding;

    //Map interaction variables
    static final int INITIAL_ZOOM_LEVEL = 18;
    protected GoogleMap googleMap;
    protected Marker userPosition;
    protected Polyline userRoute;
    protected boolean init = false;

    //light sensor
    protected SensorManager sensorManager;
    protected Sensor lightSensor;
    protected SensorEventListener lightSensorEventListener;

    protected Function<Boolean, Boolean> userAvailabilityCallback;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap map) {
            //Setup the map
            googleMap = map;
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(INITIAL_ZOOM_LEVEL));
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_day_style));
            //Setup the rest of the markers based in a json file
            loadGeoInfo();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((App) requireActivity().getApplicationContext()).getAppComponent().inject(this);
        binding = FragmentMapBinding.inflate(inflater);
        binding.toolbar.inflateMenu(R.menu.main_menu);
        ((AppCompatActivity) this.requireActivity()).setSupportActionBar(binding.toolbar);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutButton:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                break;
            case R.id.status:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        userAvailabilityCallback.apply(true);
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        userAvailabilityCallback.apply(false);
                    }
                    item.setChecked(false);
                }
                break;
            case R.id.availableUsers:
                startActivity(new Intent(getContext(), UsersActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void beforeDestruction() {
        sensorManager.unregisterListener(lightSensorEventListener);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        lightSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(@NotNull SensorEvent sensorEvent) {

                if (sensorEvent.values[0] > 150) {
                    if (userRoute != null)
                        userRoute.setColor(R.color.light_blue_400);
                    if (googleMap != null)
                        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_day_style));
                } else {
                    if (userRoute != null)
                        userRoute.setColor(R.color.light_blue_100);
                    if (googleMap != null)
                        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_night_style));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        sensorManager.registerListener(lightSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(lightSensorEventListener);
    }

    public void updateUserPositionOnMap(@NotNull LocationResult locationResult) {
        if (!init) {
            userPosition = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()))
                    .icon(BitmapUtils.getBitmapDescriptor(getContext(), R.drawable.ic_baseline_circle_24))
                    .anchor(0.5f, 0.5f)
                    .zIndex(1.0f));
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(INITIAL_ZOOM_LEVEL));
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(userPosition.getPosition()));
            init = true;
        }
        userPosition.setPosition(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()));

    }

    public void onUserActiveCallback(Function<Boolean, Boolean> userAvailabilityCallback) {
        this.userAvailabilityCallback = userAvailabilityCallback;
    }

    private void loadGeoInfo() {

        for (int i = 0; i < geoInfoFromJsonService.getLocationsArray().length(); i++) {
            try {
                JSONObject location = geoInfoFromJsonService.getLocationsArray().getJSONObject(i);
                MarkerOptions newMarker = new MarkerOptions();
                newMarker.position(new LatLng(location.getDouble("latitude"), location.getDouble("longitude")));
                newMarker.title(location.getString("name"));
                googleMap.addMarker(newMarker);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}