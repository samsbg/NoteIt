package com.codepath.noteit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.noteit.databinding.ActivityLoginBinding;
import com.codepath.noteit.models.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        View view = binding.getRoot();
        setContentView(view);

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.etUsername.getText().toString();
                String password = binding.etPassword.getText().toString();
                loginUser(username, password);
            }
        });

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.etUsername.getText().toString();
                String password = binding.etPassword.getText().toString();
                signUpUser(username, password);
            }
        });
    }

    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e("LoginActivity", "Issue with login", e);
                    Toast.makeText(LoginActivity.this, "Issue with login!", Toast.LENGTH_SHORT);
                    return;
                }
                goMainActivity();
            }
        });
    }

    private void signUpUser(String username, String password) {
        User user = new User();

        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("LoginActivity", "Issue with sign up", e);
                    Toast.makeText(LoginActivity.this, "Issue with sign up!", Toast.LENGTH_SHORT);
                    return;
                }
                goMainActivity();
            }
        });


    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}