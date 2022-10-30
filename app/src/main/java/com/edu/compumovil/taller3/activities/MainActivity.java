package com.edu.compumovil.taller3.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.edu.compumovil.taller3.R;
import com.edu.compumovil.taller3.databinding.ActivityMainBinding;

public class MainActivity extends AuthenticatedActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}