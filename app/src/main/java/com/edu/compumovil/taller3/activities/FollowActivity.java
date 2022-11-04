package com.edu.compumovil.taller3.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.edu.compumovil.taller3.R;
import com.edu.compumovil.taller3.activities.AuthenticatedActivity;

public class FollowActivity extends AuthenticatedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
    }
}