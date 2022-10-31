package com.edu.compumovil.taller3.services;

import android.content.Context;
import android.util.Log;

import com.edu.compumovil.taller3.models.location.LocationsInfo;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import lombok.Getter;

@Getter
public class GeoInfoFromJsonService {
    public static final String TAG = GeoInfoFromJsonService.class.getName();
    public static final String LOCATIONS_FILE = "locations.json";
    private final Context context;
    private LocationsInfo locations;
    private  JSONArray locationsArray;

    public GeoInfoFromJsonService(Context context) {
        this.context = context;
        loadGeoInfoFromJson();
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open(LOCATIONS_FILE);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Log.e(TAG, String.format("loadJSONFromAsset: error reading the %s file.", LOCATIONS_FILE), ex);
            return null;
        }
        return json;
    }

    public void loadGeoInfoFromJson() {
        locations = new Gson().fromJson(loadJSONFromAsset(), LocationsInfo.class);

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            locationsArray = obj.getJSONArray("locations");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, String.format("loadGeoInfoFromJson: loaded %d registries.", e.getMessage()));
        }

    }
}
