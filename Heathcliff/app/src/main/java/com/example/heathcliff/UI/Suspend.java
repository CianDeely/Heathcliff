package com.example.heathcliff.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.heathcliff.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Suspend extends AppCompatActivity {
    BottomNavigationView topNavBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suspend);
        BottomNavigationView topNavBar = (BottomNavigationView) findViewById(R.id.topNavBar);
        Menu menu = topNavBar.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        topNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_home:
                        startActivity(new Intent(Suspend.this, LandingPage.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.ic_shelter_requests:
                        startActivity(new Intent(Suspend.this, ShelterRequests.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.ic_reports:
                        startActivity(new Intent(Suspend.this, Reports.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.ic_suspend:
                        startActivity(new Intent(Suspend.this, Suspend.class));
                        overridePendingTransition(0, 0);
                        break;
                }
                return false;
            }
        });
    }
}