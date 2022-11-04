package com.edu.compumovil.taller3.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.edu.compumovil.taller3.App;
import com.edu.compumovil.taller3.R;
import com.edu.compumovil.taller3.databinding.FragmentFollowMapBinding;
import com.edu.compumovil.taller3.services.GeocoderService;
import com.edu.compumovil.taller3.utils.AlertsHelper;
import com.edu.compumovil.taller3.utils.BitmapUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

public class FollowMapFragment extends Fragment {

    @Inject
    GeocoderService geocoderService;
    @Inject
    AlertsHelper alertsHelper;

    FragmentFollowMapBinding binding;

    static final int INITIAL_ZOOM_LEVEL = 18;
    protected GoogleMap googleMap;
    public Marker userPosition = null;
    public Marker friendPosition = null;
    public Polyline userRoute;

    public enum WHO {
        USER,
        FRIEND
    }

    SensorManager sensorManager;
    Sensor lightSensor;
    protected SensorEventListener lightSensorEventListener;

    private final OnMapReadyCallback callback = map -> {
        //Setup the map
        googleMap = map;
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(INITIAL_ZOOM_LEVEL));
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_day_style));
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((App) requireActivity().getApplicationContext()).getAppComponent().inject(this);
        binding = FragmentFollowMapBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Initialize fragment
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

    public void updatePositionOnMap(WHO who, @NotNull LatLng location, int icon) {
        if (who == WHO.USER) {
            if (userPosition == null) {
                userPosition = googleMap.addMarker(new MarkerOptions()
                        .position(location)
                        .icon(BitmapUtils.getBitmapDescriptor(getContext(), icon))
                        .anchor(0.5f, 0.5f)
                        .zIndex(1.0f));
            }

            // Add position
            assert userPosition != null;
            userPosition.setPosition(location);
        } else {
            if (friendPosition == null) {
                friendPosition = googleMap.addMarker(new MarkerOptions()
                        .position(location)
                        .icon(BitmapUtils.getBitmapDescriptor(getContext(), icon))
                        .anchor(0.5f, 0.5f)
                        .zIndex(1.0f));
            }

            // Add position
            assert friendPosition != null;
            friendPosition.setPosition(location);
        }

        updateInformation();
    }

    public void beforeDestruction() {
        sensorManager.unregisterListener(lightSensorEventListener);
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

    protected void updateInformation() {
        if (friendPosition != null) {
            // Animate camera
            LatLngBounds bound = LatLngBounds.builder()
                    .include(userPosition.getPosition())
                    .include(friendPosition.getPosition()).build();

            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound, 100));

            // Distance
            float[] results = new float[1];
            Location.distanceBetween(userPosition.getPosition().latitude, userPosition.getPosition().longitude,
                    friendPosition.getPosition().latitude,
                    friendPosition.getPosition().longitude, results);

            binding.distanceAppart.setText(String.format("Distancia = %.2f km", (results[0] / 1000)));

            // Paint route
            GoogleDirection.withServerKey("AIzaSyAsxlwNAB-VkSxlgCVFxlabXUuBHTLJ8bs")
                    .from(userPosition.getPosition()).to(friendPosition.getPosition())
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(@Nullable Direction direction) {
                            if (direction != null && direction.isOK()) {
                                if (userRoute != null)
                                    userRoute.remove();

                                // Obtener y dibujar la dirección
                                ArrayList<LatLng> directions = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                                userRoute = googleMap.addPolyline(DirectionConverter.createPolyline(
                                        requireContext(), directions, 5, Color.MAGENTA
                                ));

                                Log.w("Route", "Calculando nueva ruta");

                            } else {
                                assert direction != null;
                                Log.e("Route", "No fue posible encontrar la ruta: " + direction.getErrorMessage());
                            }
                        }

                        @Override
                        public void onDirectionFailure(@NonNull Throwable t) {
                            Log.e("Route", t.getLocalizedMessage());
                        }
                    });
        }
    }
}