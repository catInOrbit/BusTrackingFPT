package com.bustracking.ui.activity;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import com.bustracking.BaseActivity;
import com.bustracking.R;
import com.bustracking.model.busmodels.BusLine;
import com.bustracking.model.busmodels.BusRoute;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.bustracking.utils.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;


public class SplashActivity extends BaseActivity {

    /*variable declaration*/
    private ImageView mIVLogo;
    private boolean mPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        initLayouts();
        initializeListeners();
    }

    /* init layout */
    private void initLayouts() {

        mIVLogo = findViewById(R.id.ivLogo);
    }

    /* initialize listener */
    private void initializeListeners() {
        Glide.with(this).load(R.raw.ic_logo).into(mIVLogo);

        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run()
                                      {
                                              startActivity(PermissionRequestAcitivity.class);
                                              onBackPressed();
                                      }
                                  },
                2000);
    }




}



