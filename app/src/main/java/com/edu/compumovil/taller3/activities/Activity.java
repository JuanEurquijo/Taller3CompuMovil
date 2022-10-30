package com.edu.compumovil.taller3.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.edu.compumovil.taller3.App;
import com.edu.compumovil.taller3.services.CameraService;
import com.edu.compumovil.taller3.services.LocationService;
import com.edu.compumovil.taller3.utils.AlertsHelper;
import com.edu.compumovil.taller3.utils.PermissionHelper;

import javax.inject.Inject;

public class Activity extends AppCompatActivity {
    @Inject
    AlertsHelper alertsHelper;

    @Inject
    PermissionHelper permissionHelper;

    @Inject
    LocationService locationService;

    @Inject
    CameraService cameraService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((App) getApplicationContext()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    protected void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}