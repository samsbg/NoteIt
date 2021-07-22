package com.codepath.noteit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.codepath.noteit.R;
import com.codepath.noteit.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    ActivityMainBinding binding;
    GoogleSignInClient googleClient;

    final int RC_SIGN_IN = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.your_web_app_client_id))
                .build();
        googleClient = GoogleSignIn.getClient(MainActivity.this, gso);

        binding.ibUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                popup.setOnMenuItemClickListener(MainActivity.this);
                popup.inflate(R.menu.menu_user_icon);
                popup.show();
            }
        });

        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.iAdd:
                        PopupMenu popup = new PopupMenu(MainActivity.this, findViewById(R.id.iAdd));
                        popup.setOnMenuItemClickListener(MainActivity.this);
                        popup.inflate(R.menu.menu_add);
                        popup.show();
                        return true;
                    case R.id.iCalendar:
                        Intent i = new Intent(MainActivity.this, CalendarActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.iStats:
                        Intent j = new Intent(MainActivity.this, StatisticsActivity.class);
                        startActivity(j);
                        return true;
                    default: return true;
                }
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.iLogOut:
                ParseUser.logOut();
                Intent i  = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                return true;
            case R.id.iNote:
                Intent j = new Intent(this, NoteEditorActivity.class);
                startActivity(j);
            case R.id.iConnectGoogle:
                Intent k = googleClient.getSignInIntent();
                startActivityForResult(k, RC_SIGN_IN);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Log.d("OAuth login", "User signed in to google");
            Toast.makeText(getApplicationContext(), "Successful login", Toast.LENGTH_LONG).show();

        } catch (ApiException e) {
            Log.w("OAuth login", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getApplicationContext(), "Error while signing in, code="+ e.getStatusCode(), Toast.LENGTH_LONG).show();
        }
    }
}