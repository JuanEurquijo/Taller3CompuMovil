package com.edu.compumovil.taller3.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.edu.compumovil.taller3.R;
import com.edu.compumovil.taller3.databinding.ActivitySignUpBinding;
import com.edu.compumovil.taller3.models.database.DatabaseRoutes;
import com.edu.compumovil.taller3.models.database.UserInfo;
import com.edu.compumovil.taller3.utils.PermissionHelper;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.Objects;

public class SignUpActivity extends Activity {
    public static final String TAG = SignUpActivity.class.getName();

    private ActivitySignUpBinding binding;

    FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    double latitude;
    double longitude;
    Uri imageData = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();

        binding.buttonSignUp.setOnClickListener(view -> doSignup());
        binding.signInButton.setOnClickListener(view -> startActivity(new Intent(this, LoginActivity.class)));

        binding.cameraApp.setOnClickListener(view -> {
            permissionHelper.getCameraPermission(this);
            if (permissionHelper.isMCameraPermissionGranted()) {
                cameraService.startCamera(this);
            }
        });

        binding.galleryApp.setOnClickListener(view -> cameraService.startGallery(this));

        locationService.setLocationCallback(new LocationCallback() {
            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i(TAG, "onLocationResult: " + Objects.requireNonNull(locationResult.getLastLocation()));
                latitude = locationResult.getLastLocation().getLatitude();
                longitude = locationResult.getLastLocation().getLongitude();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PermissionHelper.PERMISSIONS_REQUEST_CAMERA:
                    binding.imageContact.setImageURI(cameraService.getPhotoURI());
                    imageData = cameraService.getPhotoURI();
                    break;

                case PermissionHelper.PERMISSIONS_REQUEST_GALLERY:
                    assert data != null;
                    binding.imageContact.setImageURI(data.getData());
                    imageData = data.getData();
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!permissionHelper.isMCameraPermissionGranted())
            permissionHelper.getCameraPermission(this);

        permissionHelper.getLocationPermission(this);
        if (permissionHelper.isMLocationPermissionGranted()) {
            locationService.startLocation();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationService.stopLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (permissionHelper.isMLocationPermissionGranted()) {
            locationService.startLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.PERMISSIONS_LOCATION) {
            permissionHelper.getLocationPermission(this);
            if (permissionHelper.isMLocationPermissionGranted()) {
                locationService.startLocation();
            }
        }
    }

    private void doSignup() {
        // Variables
        String email, pass, name, lastname, numid;

        // Validaciones

        try {
            name = Objects.requireNonNull(binding.userName.getEditText()).getText().toString();
            if (name.isEmpty()) throw new Exception();
        } catch (Exception e) {
            alertsHelper.shortSimpleSnackbar(binding.getRoot(), getString(R.string.name_error_label));
            binding.userName.setErrorEnabled(true);
            binding.userName.setError(getString(R.string.name_error_label));
            return;
        }

        try {
            lastname = Objects.requireNonNull(binding.lastname.getEditText()).getText().toString();
            if (lastname.isEmpty()) throw new Exception();
        } catch (Exception e) {
            alertsHelper.shortSimpleSnackbar(binding.getRoot(), getString(R.string.lastname_error_label));
            binding.lastname.setErrorEnabled(true);
            binding.lastname.setError(getString(R.string.lastname_error_label));
            return;
        }

        try {
            numid = Objects.requireNonNull(binding.numId.getEditText()).getText().toString();
            if (numid.isEmpty()) throw new Exception();
        } catch (Exception e) {
            alertsHelper.shortSimpleSnackbar(binding.getRoot(), getString(R.string.numId_error_label));
            binding.numId.setErrorEnabled(true);
            binding.numId.setError(getString(R.string.numId_error_label));
            return;
        }

        try {
            email = Objects.requireNonNull(binding.email.getEditText()).getText().toString();
            if (email.isEmpty()) throw new Exception();
        } catch (Exception e) {
            alertsHelper.shortSimpleSnackbar(binding.getRoot(), getString(R.string.mail_error_label));
            binding.email.setErrorEnabled(true);
            binding.email.setError(getString(R.string.mail_error_label));
            return;
        }


        try {
            pass = Objects.requireNonNull(binding.password.getEditText()).getText().toString();
            if (pass.isEmpty()) throw new Exception();
        } catch (Exception e) {
            alertsHelper.shortSimpleSnackbar(binding.getRoot(), getString(R.string.error_pass_label));
            binding.password.setErrorEnabled(true);
            binding.password.setError(getString(R.string.error_pass_label));
            return;
        }

        try {
            if (binding.imageContact.getDrawable() == null) throw new Exception();
        } catch (Exception e) {
            alertsHelper.shortSimpleSnackbar(binding.getRoot(), getString(R.string.image_error_label));
            return;
        }


        // Crear usuario
        if (imageData != null)
            mAuth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener(authResult -> {
                Log.i(TAG, "(Success) Firebase user creation");

                // Añadir información en la base de datos
                String currentUser = Objects.requireNonNull(authResult.getUser()).getUid();

                try {
                    DatabaseReference reference = mDatabase.getReference(DatabaseRoutes.getUser(
                            Objects.requireNonNull(currentUser)));

                    // Create user
                    UserInfo newUser = new UserInfo(
                            Objects.requireNonNull(binding.userName.getEditText().getText().toString()),
                            Objects.requireNonNull(binding.lastname.getEditText().getText().toString()),
                            Long.parseLong(binding.numId.getEditText().getText().toString()),
                            latitude,
                            longitude,
                            new Date().getTime(),
                            new Date().getTime());

                    // Save user on Database
                    reference.setValue(newUser)
                            .addOnSuccessListener(unused -> {
                                Log.i(TAG, "(Success) Firebase RTD user details saved");
                                alertsHelper.shortToast(this, getString(R.string.success_signup));
                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> alertsHelper.shortSimpleSnackbar(binding.getRoot(), e.getLocalizedMessage()));

                    StorageReference storageImages = mStorage.getReference(DatabaseRoutes.getImage(currentUser));
                    storageImages.putFile(imageData)
                            .addOnSuccessListener(runnable -> Log.i(TAG, "Image saved"))
                            .addOnFailureListener(runnable -> Log.e(TAG, "Image not saved"));

                } catch (NullPointerException e) {
                    Log.e(TAG, "(Error) Firebase RTD user creation");
                    alertsHelper.longSimpleSnackbar(binding.getRoot(), getString(R.string.unknown_error));
                }

            }).addOnFailureListener(e -> {
                Log.i(TAG, "(Failure) Firebase");
                alertsHelper.indefiniteSnackbar(binding.getRoot(), e.getLocalizedMessage());
            });
        else alertsHelper.indefiniteSnackbar(binding.getRoot(), "Failed to upload image");

        // Mostrar
        super.hideKeyboard();
        alertsHelper.shortSimpleSnackbar(binding.getRoot(), getString(R.string.wait_signup));
    }
}