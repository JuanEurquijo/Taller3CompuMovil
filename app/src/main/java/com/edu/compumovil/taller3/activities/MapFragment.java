package com.edu.compumovil.taller3.activities;

import android.content.Context;
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
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
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
import com.google.gson.JsonArray;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class MapFragment extends Fragment {

    FragmentMapBinding binding;
    @Inject
    GeocoderService geocoderService;
    @Inject
    AlertsHelper alertsHelper;
    @Inject
    GeoInfoFromJsonService geoInfoFromJsonService;

    //Map interaction variables
    GoogleMap googleMap;
    static final int INITIAL_ZOOM_LEVEL = 18;
    private final LatLng CENTRAL_PARK = new LatLng(40.7812, -73.9665);
    Marker userPosition;
    Polyline userRoute;
    List<Marker> places = new ArrayList<>();

    //light sensor
    SensorManager sensorManager;
    Sensor lightSensor;
    SensorEventListener lightSensorEventListener;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap map) {
            //Setup the map
            googleMap = map;
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(INITIAL_ZOOM_LEVEL));
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_day_style));
            //Setup the user marker with a default position
            userPosition = googleMap.addMarker(new MarkerOptions()
                    .position(CENTRAL_PARK)
                    .icon(BitmapUtils.getBitmapDescriptor(getContext(), R.drawable.ic_baseline_circle_24))
                    .anchor(0.5f, 0.5f)
                    .zIndex(1.0f));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(CENTRAL_PARK));
            //Setup the route line
            userRoute = googleMap.addPolyline(new PolylineOptions()
                    .color(R.color.light_blue_400)
                    .width(15.0f)
                    .geodesic(true)
                    .zIndex(0.5f));
            //Setup the rest of the markers based in a json file
           // loadGeoInfo();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((App) requireActivity().getApplicationContext()).getAppComponent().inject(this);
        binding = FragmentMapBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(@NotNull SensorEvent sensorEvent) {
                if (sensorEvent.values[0] > 1500) {
                    userRoute.setColor(R.color.light_blue_400);
                    googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_day_style));
                } else {
                    userRoute.setColor(R.color.light_blue_100);
                    googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_night_style));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

    }

    private void findPlaces(){

    }

    @Override
    public void onStart() {
        super.onStart();
        sensorManager.registerListener(lightSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onStop() {
        super.onStop();
        List<LatLng> points = userRoute.getPoints();
        points.clear();
        userRoute.setPoints(points);
    }

    public void updateUserPositionOnMap(@NotNull LocationResult locationResult) {
        userPosition.setPosition(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()));
        List<LatLng> points = userRoute.getPoints();
        points.add(userPosition.getPosition());
        userRoute.setPoints(points);

    }

    private void loadGeoInfo() {

            for (int i = 0; i < geoInfoFromJsonService.getLocationsArray().length(); i++){
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