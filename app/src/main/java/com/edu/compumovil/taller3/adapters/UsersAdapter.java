package com.edu.compumovil.taller3.adapters;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edu.compumovil.taller3.R;
import com.edu.compumovil.taller3.activities.Activity;
import com.edu.compumovil.taller3.databinding.UsersAdapterBinding;
import com.edu.compumovil.taller3.models.database.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class UsersAdapter extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<UserInfo> items;


    public UsersAdapter(Activity activity, ArrayList<UserInfo> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getNumId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
           LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           convertView = inflater.inflate(R.layout.users_adapter, null);

        }
        UserInfo user = items.get(position);

        ImageView uri = (ImageView) convertView.findViewById(R.id.imageUser);
        uri.setImageURI(user.getImage());
        TextView text = (TextView) convertView.findViewById(R.id.userName);
        text.setText(user.getName() + " " + user.getLastname());

        return convertView;
    }
}
