package com.edu.compumovil.taller3.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.edu.compumovil.taller3.R;
import com.edu.compumovil.taller3.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends Activity {
    //bindings
    public static final String TAG = LoginActivity.class.getName();
    ActivityLoginBinding binding;

    // Autenticación
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Authentication
        mAuth = FirebaseAuth.getInstance();

        // Comportamiento
        binding.buttonLogin.setOnClickListener(view -> doLogin());
        binding.signUpButton.setOnClickListener(view -> startActivity(new Intent(this, SignUpActivity.class)));
        binding.labelForgotPass.setOnClickListener(view -> doPassReset());


    }


    public static Intent createIntent(android.app.Activity activity) {
        return new Intent(activity, LoginActivity.class);
    }
    private void doLogin() {
        // Variables de pass e email
        String pass, email;
        super.hideKeyboard();

        // Obtener campo de Email
        try {
            email = Objects.requireNonNull(binding.email.getEditText()).getText().toString();
            if (email.isEmpty()) throw new Exception();
        } catch (Exception e) {
            alertsHelper.shortSimpleSnackbar(binding.getRoot(), getString(R.string.mail_error_label));
            binding.email.setErrorEnabled(true);
            binding.email.setError(getString(R.string.mail_error_label));
            return;
        }

        // Obtener campo de Password
        try {
            pass = Objects.requireNonNull(binding.pasword.getEditText()).getText().toString();
            if (pass.isEmpty()) throw new Exception();

        } catch (Exception e) {
            alertsHelper.shortSimpleSnackbar(binding.getRoot(), getString(R.string.error_pass_label));
            binding.pasword.setErrorEnabled(true);
            binding.pasword.setError(getString(R.string.error_pass_label));
            return;
        }

        // Autenticación
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    Log.i(TAG, "(Success) Authentication");
                    alertsHelper.shortToast(this, getString(R.string.success_login));
                    startActivity(new Intent(LoginActivity.this, MapActivity.class));
                })
                .addOnFailureListener(e -> {
                    Log.i(TAG, "(Failure) Authentication");
                    Log.e(TAG + "(Firebase)", e.getLocalizedMessage());
                    alertsHelper.indefiniteSnackbar(binding.getRoot(), e.getLocalizedMessage());
                });

        // Mostrar intento de autenticación
        alertsHelper.shortSimpleSnackbar(binding.getRoot(), getString(R.string.wait_login));
        binding.pasword.getEditText().getText().clear();
    }

    private void doPassReset() {
        // Variables
        String email;
        super.hideKeyboard();

        // Obtener el valor
        try {
            email = Objects.requireNonNull(binding.email.getEditText()).getText().toString();
            if (email.isEmpty()) throw new Exception();
        } catch (Exception e) {
            alertsHelper.shortSimpleSnackbar(binding.getRoot(), getString(R.string.mail_error_label));
            binding.email.setErrorEnabled(true);
            binding.email.setError(getString(R.string.mail_error_label));
            return;
        }

        // Enviar solicitud de reestablecimiento
        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(authResult ->
                        alertsHelper.indefiniteSnackbar(binding.getRoot(), "Revise su correo."))
                .addOnFailureListener(e ->
                        alertsHelper.indefiniteSnackbar(binding.getRoot(), e.getLocalizedMessage()));
    }
}