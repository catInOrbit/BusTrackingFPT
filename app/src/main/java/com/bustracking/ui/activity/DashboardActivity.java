package com.bustracking.ui.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;

import com.bustracking.BaseActivity;
import com.bustracking.R;
import com.bustracking.model.busmodels.BusLine;
import com.bustracking.model.usermodels.User;
import com.bustracking.model.usermodels.UserLocation;
import com.bustracking.model.busmodels.BusStop;
import com.bustracking.services.LocationService;
import com.bustracking.ui.fragment.HomeFragmentNewest;
import com.bustracking.ui.fragment.MoreFragment;
import com.bustracking.ui.fragment.MyBookingFragment;
import com.bustracking.utils.UserAndLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import static com.bustracking.utils.Constants.ERROR_DIALOG_REQUEST;
import static com.bustracking.utils.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.bustracking.utils.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;


public class DashboardActivity extends BaseActivity implements
        View.OnClickListener {

    /*variable declaration*/
    private TextView mTvTitle;
    private ImageView mIvNotification, mIvHome, mIvPackages, mIvBooking, mIvOther;
    private HomeFragmentNewest mHomeFragmentNewest = new HomeFragmentNewest();

//    private FragmentOffers mFragmentOffers = new FragmentOffers();
    private MyBookingFragment mMyBookingFragment = new MyBookingFragment();
    private MoreFragment mMoreFragment = new MoreFragment();
    private LinearLayout mLlHome, mLllPackages, mLlBooking, mLlMore;


    //-----------Mutable Live Data of array list data--------------------
    private MutableLiveData<ArrayList<User>> mUserListLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<UserLocation>> mUserLocationsLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<BusStop>> mBusStopsLiveData = new MutableLiveData<>();
    //------------------------------------------------------------------


    public MutableLiveData<ArrayList<User>> getmUserListLiveData() {
        return mUserListLiveData;
    }

    public void setmUserListLiveData(MutableLiveData<ArrayList<User>> mUserListLiveData) {
        this.mUserListLiveData = mUserListLiveData;
    }

    private UserLocation mUserLocation;

    private FirebaseFirestore mDb;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mLocationPermissionGranted = false;
    private UserAndLocation userAndLocation = new UserAndLocation();
    private boolean isAlreadyPassToFragment = false;

    //Listeners
    private ListenerRegistration mUserListEventListener;
    private ListenerRegistration mUserLocationsListener;
    private ListenerRegistration mBusStopsListener;

    private Handler mHandler = new Handler();

    private Runnable mRunnable;
    private static final int LOCATION_UPDATE_INTERVAL = 1000;

    private MediatorLiveData dashboardData = new MediatorLiveData();


    private boolean fragmentPopped = false;

    private static final String TAG = "DashboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initLayouts();
        initializeListeners();

        mDb = FirebaseFirestore.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        setSelected(mIvHome);

        fillHomeFragmentData(mHomeFragmentNewest);
        loadFragment(mHomeFragmentNewest);
        startLocationService();
    }

    /* init layout */
    @SuppressLint("ClickableViewAccessibility")
    public void initLayouts()
    {
        mTvTitle = findViewById(R.id.tvTitle);
        mIvNotification = findViewById(R.id.ivNotification);
        mLlHome = findViewById(R.id.llHome);
        mLllPackages = findViewById(R.id.llPackage);
        mLlBooking = findViewById(R.id.llBooking);
        mLlMore = findViewById(R.id.llMore);
        mIvHome = findViewById(R.id.ivHome);
        mIvPackages = findViewById(R.id.ivPackages);
        mIvBooking = findViewById(R.id.ivBooking);
        mIvOther = findViewById(R.id.ivMore);
        mTvTitle.setText(HomeFragmentNewest.mTitle);

    }

    /* initialize listener */
    public void initializeListeners() {
        mIvNotification.setOnClickListener(this);
        mLlHome.setOnClickListener(this);
        mLllPackages.setOnClickListener(this);
        mLlBooking.setOnClickListener(this);
        mLlMore.setOnClickListener(this);
        SetNotificationImage(mIvNotification);

    }

    /* set selected item in bottom navigation */
    private void setSelected(ImageView mBarImg) {
        mBarImg.setBackground(getResources().getDrawable(R.drawable.bg_tint_icon));

    }

    /* Update UI */
    private void updateUi() {
        mIvHome.setImageResource(R.drawable.ic_home);
        mIvHome.setBackground(null);
        mIvPackages.setImageResource(R.drawable.ic_package);
        mIvPackages.setBackground(null);
        mIvBooking.setImageResource(R.drawable.ic_booking);
        mIvBooking.setBackground(null);
        mIvOther.setImageResource(R.drawable.ic_fill);
        mIvOther.setBackground(null);

    }

    /* onBack press */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /* onClick listener */
    @Override
    public void onClick(View v) {

        if (v == mIvNotification) {
            Intent notificationIntent = new Intent(DashboardActivity.this, NotificationActivity.class);
//            startActivity(NotificationActivity.class);
            startActivity(notificationIntent);
            return;
        }
        updateUi();

        switch (v.getId())
        {
            case R.id.llHome:
//                if (!mHomeFragmentNewest.isVisible()) {
//                    mTvTitle.setText(HomeFragmentNewest.mTitle);

                    Intent thisIntent = new Intent(this, DashboardActivity.class);
                    startActivity(thisIntent);

//                    loadFragment(mHomeFragmentNewest);
//                    getLocationPermission();
//                }

                setSelected(mIvHome);
                mIvHome.setImageResource(R.drawable.ic_home_fill);
                break;
            case R.id.llPackage:
                Intent busListIntent = new Intent(this, BusListActivity.class);
                startActivity(busListIntent);
                setSelected(mIvPackages);
                mIvPackages.setImageResource(R.drawable.ic_package_fill);
                break;
            case R.id.llBooking:
                if (!mMyBookingFragment.isVisible()) {
                    mTvTitle.setText(MyBookingFragment.mTitle);
                    destroyOldFragment(mHomeFragmentNewest);

                    loadFragment(mMyBookingFragment);

                }
                setSelected(mIvBooking);
                mIvBooking.setImageResource(R.drawable.ic_booking_fill);
                break;
            case R.id.llMore:
                if (!mMoreFragment.isVisible()) {
                    mTvTitle.setText(MoreFragment.mTitle);

                    destroyOldFragment(mHomeFragmentNewest);
                    loadFragment(mMoreFragment);

//                    inflateHomeFragment();
                }
                setSelected(mIvOther);
                mIvOther.setImageResource(R.drawable.ic_more_fill2);
                break;

        }
    }

    private void destroyOldFragment(Fragment fragment)
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(mHomeFragmentNewest);
        trans.commit();
        manager.popBackStack();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 101: {
                if (resultCode == RESULT_OK && null != data)
                {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (mHomeFragmentNewest.isVisible()) {
                       mHomeFragmentNewest.ChangeDestination(result.get(0));
                    } else {
                        loadFragment(mHomeFragmentNewest);
                        //TODO:DATA FILLER BREAKING WARNING
//                        inflateHomeFragment();
                    }
                }
                break;
            }

            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
//                    getUserDetails();
//                    startLocationService();
                }
                else{
                    getLocationPermission();
                }
            }
        }
    }

    private void fillHomeFragmentData(HomeFragmentNewest homeFragmentNewest)
    {
        hideSoftKeyboard();

        FragmentManager manager = getSupportFragmentManager();
        Fragment homeFragmentFind = manager.findFragmentByTag("HomeFragmentNewest");

//            Fragment homeFragmentNewest = new HomeFragmentNewest();

        Bundle bundle = new Bundle();

        FragmentTransaction transaction = null;

//            fragmentPopped = manager.popBackStackImmediate(homeFragment.getClass().getName(), 0);

        Intent intent = getIntent();
        Bundle bundlePass = new Bundle();

        if(intent.hasExtra(getString(R.string.intent_bus_stops_selectable_adapter)))
        {
            ArrayList<BusStop> busStopsFromAdapter = (ArrayList<BusStop>) getIntent().getExtras().get(getString(R.string.intent_bus_stops_selectable_adapter));
            Log.i(TAG, "Found Busstops data from adapter");

            if(busStopsFromAdapter != null)
            {
                Log.i(TAG, "Setting bus stops fragment data" + busStopsFromAdapter.toString());

                bundlePass.putParcelableArrayList(getString(R.string.intent_bus_stops_selectable_adapter),busStopsFromAdapter);
            }
        }

        if(intent.hasExtra(getString(R.string.intent_bus_route_selectable_adapter)))
        {

            String busRouteName
                    = (String) getIntent().getExtras().get(getString(R.string.intent_bus_route_selectable_adapter));
            Log.i(TAG, "Setting bus stop route fragment data " + busRouteName);

            bundlePass.putString(getString(R.string.intent_bus_route_selectable_adapter),busRouteName);
        }

        if(intent.hasExtra(getString(R.string.intent_bus_line_selectable_adapter)))
        {
            BusLine busLine
                    = (BusLine) getIntent().getExtras().get(getString(R.string.intent_bus_line_selectable_adapter));

            bundlePass.putParcelable(getString(R.string.intent_bus_line_selectable_adapter),busLine);
        }

        homeFragmentNewest.setArguments(bundlePass);
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
//            getUserDetails();
        } else
            {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        startSavingUserLocationRunnable();

        if(checkMapServices()){
            if(mLocationPermissionGranted){
            }

            else{
                getLocationPermission();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mUserListEventListener != null)
            mUserListEventListener.remove();

        if(mUserLocationsListener != null)
            mUserLocationsListener.remove();
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(DashboardActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(DashboardActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void startSavingUserLocationRunnable()
    {
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                startLocationService();
                mHandler.postDelayed(mRunnable, LOCATION_UPDATE_INTERVAL);
            }
        }, LOCATION_UPDATE_INTERVAL);
    }

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, LocationService.class);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                startForegroundService(serviceIntent);
            }else{
                Log.d(TAG, "LOCSER STARTING");
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(LocationService.class.getName().equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }

    private void stopLocationUpdates(){
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


}


    //--------------


