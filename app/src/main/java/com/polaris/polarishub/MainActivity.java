package com.polaris.polarishub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.polaris.polarishub.Backend.ServerManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ServerManager SM = new ServerManager(this);
        
    }
}
