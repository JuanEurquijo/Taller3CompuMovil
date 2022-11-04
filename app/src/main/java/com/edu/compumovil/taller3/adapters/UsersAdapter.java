package com.edu.compumovil.taller3.adapters;


import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.edu.compumovil.taller3.R;
import com.edu.compumovil.taller3.activities.Activity;
import com.edu.compumovil.taller3.databinding.UsersAdapterBinding;
import com.edu.compumovil.taller3.models.database.DatabaseRoutes;
import com.edu.compumovil.taller3.models.database.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class UsersAdapter extends ArrayAdapter<UserInfo> {

    public UsersAdapter(@NonNull Context context, @NonNull List<UserInfo> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        UsersAdapterBinding binding;
        UserInfo user = getItem(position);

        if (convertView == null) {
            binding = UsersAdapterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        } else {
            binding = UsersAdapterBinding.bind(convertView);
        }

        binding.userName.setText(String.format("%s %s", user.getName(), user.getLastname()));
        binding.imageUser.setVisibility(View.VISIBLE);

        FirebaseStorage.getInstance().getReference(DatabaseRoutes.getImage(user.getImagePath())).getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(binding.imageUser).load(uri.toString()).into(binding.imageUser);
        });

        return binding.getRoot();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
