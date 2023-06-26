package com.mskennedy.batteryangel.activity.accounts.login;

import android.app.Activity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.FirebaseDatabase;
import com.mskennedy.batteryangel.R;
import com.mskennedy.batteryangel.activity.MainActivity;
import com.mskennedy.batteryangel.databinding.ActivityLoginBinding;
import com.mskennedy.batteryangel.models.MutablePrefs;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        SharedPreferences preferences = getSharedPreferences("alertPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // User is already logged in, navigate to the Main Menu activity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish(); // Optionally, finish the LoginActivity to prevent going back
            return; // Exit the method to prevent further execution of LoginActivity
        }

        firebaseAuth = FirebaseAuth.getInstance();

        final ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final Button registerButton = binding.register;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            registerButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed();
            }
            if (loginResult.getSuccess() != null) {
                // Store UID for login persistence.
                SharedPreferences.Editor sP = new MutablePrefs(getApplicationContext()).getSharedPrefsEditor();
                sP.putBoolean("isLoggedIn", true);
                sP.putString("uid_persist", firebaseAuth.getUid());
                sP.apply();
                signIn(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
            setResult(Activity.RESULT_OK);

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

            finish();
        });

        loginViewModel.getRegistrationResult().observe(this, registrationResult -> {
            if (registrationResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (registrationResult.getError() != null) {
                showRegistrationFailed();
            }
            if (registrationResult.getSuccess() != null) {
                Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                // Automatically perform login after successful registration
                loginViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(usernameEditText.getText().toString(),
                    passwordEditText.getText().toString());
        });

        registerButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            String email = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        loadingProgressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                            // Automatically perform login after successful registration
                            loginViewModel.login(email, password);
                        } else {
                            Toast.makeText(getApplicationContext(), "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void showLoginFailed() {
        Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
    }

    private void showRegistrationFailed() {
        Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
    }

    private void signIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-in successful, update UI with the signed-in user's information
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        String welcome = getString(R.string.welcome) + user.getDisplayName();
                        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
                    } else {
                        // Handle errors occurred during sign-in
                        Exception exception = task.getException();
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
