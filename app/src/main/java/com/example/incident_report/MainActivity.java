package com.example.incident_report;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameLayout;

    private FirebaseAuth mAuth;

    private Button mLogout;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        checkLoggedIn();

        checkLocationPermission();

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_framelayout,
                    new HomeFragment()).commit();
        }

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_report:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_framelayout,
                                new HomeFragment()).commit();
                        break;
                    case R.id.nav_profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_framelayout,
                                new ProfileFragment()).commit();
                        break;
                    case R.id.nav_post:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_framelayout,
                                new PostFragment()).commit();
                        break;
                    case R.id.nav_history:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_framelayout,
                                new HistoryFragment()).commit();
                        break;
                }
                return true;
            }
        });

    }

    private void initComponents(){
        mLogout = (Button) findViewById(R.id.buttonLogout);

        bottomNav = findViewById(R.id.main_bottom_navigation);
        frameLayout = findViewById(R.id.main_framelayout);

        mAuth = FirebaseAuth.getInstance();

    }

    private void checkLoggedIn() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (mAuth.getCurrentUser() != null){
            if (user != null) {
                if (user.isEmailVerified()) {
//                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                else{
                    mAuth.signOut();
                    Toast.makeText(getApplicationContext(), "Please verify your account!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        }
        else{
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                                //buildGoogleApiClient();
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }

    }

}