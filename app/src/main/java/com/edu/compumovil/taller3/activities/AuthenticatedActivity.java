package com.edu.compumovil.taller3.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.edu.compumovil.taller3.App;
import com.edu.compumovil.taller3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class AuthenticatedActivity extends Activity {

    protected FirebaseUser currentUser;
    protected FirebaseDatabase mDatabase;
    protected FirebaseStorage mStorage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((App) getApplicationContext()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
    }

    protected boolean isAuthenticated() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isAuthenticated()) {
            startActivity(LoginActivity.createIntent(this));
        }
    }

    protected void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(LoginActivity.createIntent(this));
    }

}
