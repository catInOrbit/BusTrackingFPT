package com.bustracking.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import com.bustracking.BaseActivity;
import com.bustracking.R;
import com.bustracking.ui.activity.BarcodeActivity;
import com.bustracking.ui.activity.ProfileSettingsActivity;
import com.bustracking.ui.activity.SettingActivity;
import com.bustracking.ui.activity.SignInActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class MoreFragment extends Fragment implements View.OnClickListener {

    /*variable declaration*/
    public static final String mTitle = "More";
    private TextView mTvProfileSettings, mTvWallet, mTvCards, mTvReferEarn, mTvHelp, mTvLogout, mTvSetting, mBarcodeScanner;
    private String mFlag = "1";

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;


    /* create view */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, null);
        initLayouts(view);
        initializeListeners();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /* initialize listener */
    private void initializeListeners() {
        mTvProfileSettings.setOnClickListener(this);
        mTvWallet.setOnClickListener(this);
        mTvCards.setOnClickListener(this);
        mTvReferEarn.setOnClickListener(this);
        mTvHelp.setOnClickListener(this);
        mTvLogout.setOnClickListener(this);
        mTvSetting.setOnClickListener(this);
        mBarcodeScanner.setOnClickListener(this);
    }

    /* init layout */
    private void initLayouts(View view) {
        mTvProfileSettings = view.findViewById(R.id.tvProfileSettings);
        mTvWallet = view.findViewById(R.id.tvWallet);
        mTvCards = view.findViewById(R.id.tvCards);
        mTvReferEarn = view.findViewById(R.id.tvReferEarn);
        mTvHelp = view.findViewById(R.id.tvHelp);
        mTvLogout = view.findViewById(R.id.tvLogout);
        mTvSetting = view.findViewById(R.id.tvSetting);
        mBarcodeScanner = view.findViewById(R.id.barcodeSanner);
    }

    /* onClick listener */
    @Override
    public void onClick(View v) {
        if (v == mTvProfileSettings)
            ((BaseActivity) Objects.requireNonNull(getActivity())).startActivity(ProfileSettingsActivity.class);
        else if (v == mTvSetting)
            ((BaseActivity) Objects.requireNonNull(getActivity())).startActivity(SettingActivity.class);
        else if (v == mBarcodeScanner)
            ((BaseActivity) Objects.requireNonNull(getActivity())).startActivity(BarcodeActivity.class);

        else if (v == mTvLogout)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.text_confirmation)).setMessage(getString(R.string.msg_logout));
            builder.setPositiveButton(getString(R.string.text_yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(getActivity(), SignInActivity.class));
                        }
                    });
            builder.setNegativeButton(getString(R.string.text_no),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
