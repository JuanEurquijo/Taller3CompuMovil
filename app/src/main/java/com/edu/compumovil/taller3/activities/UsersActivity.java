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
import java.util.List;

public class UsersActivity extends Activity {
    public static final String TAG = UsersActivity.class.getName();
    ActivityUsersBinding binding;
    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;
    StorageReference storageReference;
    DatabaseReference reference;
    ValueEventListener listener;
    private ArrayList<UserInfo> users = new ArrayList<>();
    UsersAdapter adapter;
    private ArrayList<String> uuids = new ArrayList<>();
    private FirebaseUser currentUser;
    private Uri profilePictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        reference = mDatabase.getReference(DatabaseRoutes.USERS_PATH);
        storageReference = mStorage.getReference();

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    snapshot.getChildren().forEach(dataSnapshot -> {
                        UserInfo tmpUsr = dataSnapshot.getValue(UserInfo.class);
                        String uuid = dataSnapshot.getRef().getKey();
                            if(!uuids.contains(uuid)){
                                users.add(new UserInfo(tmpUsr.getName(), tmpUsr.getLastname(), storageReference.child("profileImages/" + uuid).getDownloadUrl().toString()));
                                uuids.add(uuid);
                            }
                    });
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ", error.toException());
            }
        };

        adapter = new UsersAdapter(this, users);
        binding.usersList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        reference.addValueEventListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        reference.removeEventListener(listener);
    }
}