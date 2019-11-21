package com.example.food_roulette;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class PermissionActivity extends AppCompatActivity
{
    private Button btngrant;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permissions);
        btngrant = findViewById(R.id.btn_permission);
        if (ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(PermissionActivity.this, MapActivity.class));
            finish();
            return;
        }
        btngrant.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ActivityCompat.requestPermissions(PermissionActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                startActivity(new Intent(PermissionActivity.this, MapActivity.class));
            }
        }
    }
}

