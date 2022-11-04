package com.edu.compumovil.taller3.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;
import com.edu.compumovil.taller3.adapters.UsersAdapter;
import com.edu.compumovil.taller3.databinding.ActivitySignUpBinding;
import com.edu.compumovil.taller3.databinding.ActivityUsersBinding;
import com.edu.compumovil.taller3.databinding.FragmentMapBinding;
import com.edu.compumovil.taller3.models.database.DatabaseRoutes;
import com.edu.compumovil.taller3.models.database.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UsersActivity extends AuthenticatedActivity {
    public static final String TAG = UsersActivity.class.getName();
    ActivityUsersBinding binding;

    ValueEventListener listener;
    UsersAdapter adapter;

    HashMap<String, UserInfo> users = new HashMap<>();
    ArrayList<UserInfo> userInformation = new ArrayList<>();

    private FirebaseUser currentUser;
    private Uri profilePictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Create on DatabaseData change listener
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // For every child
                    snapshot.getChildren().forEach(dataSnapshot -> {
                        // Get information
                        UserInfo userinfo = dataSnapshot.getValue(UserInfo.class);
                        String uuid = dataSnapshot.getRef().getKey();

                        // Append or update information
                        if (userinfo != null && userinfo.isAvailable()) {
                            userinfo.imagePath = uuid;
                            users.put(uuid, userinfo);
                        }
                    });
                }

                userInformation.clear();
                userInformation.addAll(users.values());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ", error.toException());
            }
        };

        adapter = new UsersAdapter(this, userInformation);
        binding.usersList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.getReference(DatabaseRoutes.USERS_PATH).addValueEventListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDatabase.getReference(DatabaseRoutes.USERS_PATH).removeEventListener(listener);
    }
}