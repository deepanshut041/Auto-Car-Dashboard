package com.example.deepanshu.myapplication;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DriverFragment driverFragment = new DriverFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, driverFragment).commit();
    }
}
