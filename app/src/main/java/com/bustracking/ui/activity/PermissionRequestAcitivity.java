package com.bustracking.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.bustracking.R;

import static com.bustracking.utils.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;


public class PermissionRequestAcitivity extends AppCompatActivity {

    private Switch locationSwitch,storageSwitch;
    private boolean mLocationPermissionGranted, mStoragePermissionGranted = false;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_request_acitivity);
        locationSwitch = (Switch) findViewById(R.id.highLocationSwitch);
        storageSwitch = (Switch)findViewById(R.id.enableStorageSwitch);

        if(isStorageGranted() && isLocationGranted())
        {
            Intent intent = new Intent(PermissionRequestAcitivity.this,SignInActivity.class);
            startActivity(intent);
        }

        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    getLocationPermission();
            }
        });

        storageSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked)
                getStoragePermission();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPermissionRunnable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mRunnable);
    }

    private void getPermissionRunnable()
    {
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                if(isLocationGranted() && isStorageGranted())
                {
                    Intent intent = new Intent(PermissionRequestAcitivity.this,SignInActivity.class);
                    startActivity(intent);
                }
                mHandler.postDelayed(mRunnable, 100);
            }
        }, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }

    private boolean getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionGranted = true;
            return true;
        } else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return false;
        }
    }

    private boolean isStorageGranted()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
            return false;
    }

    private boolean isLocationGranted()
    {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }

        return false;
    }


    public boolean getStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                mStoragePermissionGranted = true;
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
        }
        return false;
    }

}
