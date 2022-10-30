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

public class AuthenticatedActivity extends Activity{

    protected FirebaseUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((App) getApplicationContext()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
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

    protected void signOut(){
        FirebaseAuth.getInstance().signOut();
        startActivity(LoginActivity.createIntent(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.logoutButton:
                signOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
