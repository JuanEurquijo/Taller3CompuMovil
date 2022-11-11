package com.edu.compumovil.taller3.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.edu.compumovil.taller3.R;
import com.edu.compumovil.taller3.models.database.DatabaseRoutes;
import com.edu.compumovil.taller3.models.database.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class NotificationWorker extends Worker {

    private static final String TAG = NotificationWorker.class.getName();
    private static final String CHANNEL_ID = "firebaseNotificationChannel";
    private static ValueEventListener listener;
    private static HashMap<String, Boolean> users = new HashMap<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public NotificationWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Notificaciones de conexión", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

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

                        assert userinfo != null;
                        if (users.containsKey(uuid) &&
                                (Boolean.TRUE.equals(users.get(uuid)) != userinfo.isAvailable())
                                && userinfo.isAvailable()) {
                            sendNotification(userinfo.getName());
                        }
                        users.put(uuid, userinfo.isAvailable());
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ", error.toException());
            }
        };
    }

    private void sendNotification(String name) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_circle_24)
                .setContentTitle("Notificación de conexión")
                .setContentText("El usuario " + name + " ahora está en línea")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1, builder.build());
    }

    @NonNull
    @Override
    public Result doWork() {
        FirebaseDatabase.getInstance().getReference(DatabaseRoutes.USERS_PATH).addValueEventListener(listener);
        return Result.success();
    }
}
