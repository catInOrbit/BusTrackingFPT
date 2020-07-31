package com.bustracking.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

//import com.iqonicthemes.qibus_softui.ui.activity.QIBusSoftUISoftUISplashActivity;

import com.bustracking.BaseActivity;
import com.bustracking.R;

public class SelectionActivity extends BaseActivity implements View.OnClickListener {
    /*variable declaration*/
    private Button mBtnContinue, btnSoftUiContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_selection);
        initLayouts();
        initializeListeners();
    }

    /* init layout */
    private void initLayouts() {
        mBtnContinue = findViewById(R.id.btnSignIn);
        btnSoftUiContinue = findViewById(R.id.btnSoftUiContinue);
    }

    /* initialize listener */

    @SuppressLint("ClickableViewAccessibility")
    private void initializeListeners() {
        mBtnContinue.setOnClickListener(this);
        mBtnContinue.setStateListAnimator(null);
        btnSoftUiContinue.setOnClickListener(this);
        btnSoftUiContinue.setStateListAnimator(null);
    }


    /* onClick listener */
    @Override
    public void onClick(View v) {
        if (v == mBtnContinue) {
            startActivity(SignInActivity.class);
            finish();
        }
        if (v == btnSoftUiContinue) {
//            startActivity(QIBusSoftUISoftUISplashActivity.class);
            finish();
        }
    }
}
