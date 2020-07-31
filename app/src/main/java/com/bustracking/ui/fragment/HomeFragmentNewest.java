package com.bustracking.ui.fragment;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bustracking.BaseActivity;
import com.bustracking.R;
import com.bustracking.model.busmodels.Bus;
import com.bustracking.model.functionalmodels.BookingModel;
import com.bustracking.model.functionalmodels.ClusterMarker;
import com.bustracking.model.viewmodels.DataViewModel;
import com.bustracking.model.functionalmodels.NewOfferModel;
import com.bustracking.model.functionalmodels.PolylineData;
import com.bustracking.model.busmodels.BusLine;
import com.bustracking.model.busmodels.BusRoute;
import com.bustracking.model.usermodels.User;
import com.bustracking.model.usermodels.UserLocation;
import com.bustracking.model.busmodels.BusStop;
import com.bustracking.services.LocationService;
import com.bustracking.ui.activity.BusListActivity;
import com.bustracking.ui.activity.BusStopsViewModel;
import com.bustracking.ui.activity.OffersActivity;
import com.bustracking.ui.adapter.NewOfferAdapter;
import com.bustracking.ui.adapter.UpcommingStationsAdapter;
import com.bustracking.ui.repositories.HomeFragmentRepo;
import com.bustracking.utils.Constants;
import com.bustracking.utils.FirebaseListenerInterface;
import com.bustracking.utils.MyClusterManagerRenderer;
import com.bustracking.utils.map.HttpConnection;
import com.bustracking.utils.map.PathJSONParser;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ACTIVITY_SERVICE;
import static com.bustracking.utils.Constants.MAPVIEW_BUNDLE_KEY;
import static com.bustracking.utils.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.bustracking.utils.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class HomeFragmentNewest extends Fragment implements View.OnClickListener, UpcommingStationsAdapter.onClickListener,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnPolylineClickListener
{
    /*variable declaration*/
    public static final String mTitle = "Home";
    private AutoCompleteTextView mEdFromCity, mEdToCity;
    public static String mFrom, mTo;
    private int mValue = 0;
    private View mView;
    private ArrayList<NewOfferModel> mNewOfferList;
    private ArrayList<BookingModel> mUpcommingStation;
    private RecyclerView mRvNewOffer, mRvRecentSearch;
    private ImageView mIvSwap, mIvDescrease, mIvIncrease, mSearch;
    private TextView mTvAC, mTvNonAc, mTvSleeper, mTvSeater, mTvCount, mTvViewNewOffers;
    private Calendar mDepartDateCalendar;
    private UpcommingStationsAdapter mUpcomingStationAdapter;
    private TextView mEdDepartDate;
    private ClusterManager<ClusterMarker> mClusterManager;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private GeoApiContext mGeoApiContext;
    private UserLocation mUserPosition;
    private ArrayList<UserLocation> mUserLocations = new ArrayList<>();
    private ArrayList<User> mUserList = new ArrayList<>();
    private GoogleMap mGoogleMap;
    private ArrayList<PolylineData> mPolyLinesData = new ArrayList<>();
    private LatLngBounds mMapBoundary;
    private UserLocation mUserLocation;
    private RelativeLayout homeFragmentMain;
    private CustomNestedScrollView mainCustomNestedScrollView;
    private boolean mLocationPermissionGranted = false;
    private NestedScrollView mHomeFragmentNestedScrollView;

    private ArrayList<BusStop> busStops = new ArrayList<>();
    private Handler mhandler = new Handler();


    private Marker mSelectedMarker = null;
    private ArrayList<Marker> mTripMarkers = new ArrayList<>();
    private UserLocation updatedUserLocation;

    public static  HomeFragmentNewest homeFragmentNewest;
    private ListenerRegistration mUserListEventListener;
    private ListenerRegistration mBusStopsListener;

    private BusStopsViewModel busStopsViewModel;

    private boolean isMapReady = false;
    private boolean tripWire = false;

    private FusedLocationProviderClient mFusedLocationClient;


    //TAGS AND CONST
    public static final String TAG = "HomeFragmentNewest";
    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;

    private  BusStopsViewModel model;


    private Context mContext;
    //widgets
    private RecyclerView mUserListRecyclerView;
    private MapView mMapView;
    private RelativeLayout mMapContainer;

    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    private DataViewModel dataViewModel;
    private ArrayList<Marker> addedMarkersOnMapArray = new ArrayList<>();

//     private  Observer<ArrayList<BusStop>> busStopObserver;

    //Update interval int
    private static final int LOCATION_UPDATE_INTERVAL = 1000;
    private ListenerRegistration mUserLocationsListener;

    private final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mDepartDateCalendar.set(Calendar.YEAR, year);
            mDepartDateCalendar.set(Calendar.MONTH, monthOfYear);
            mDepartDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateLabel();
        }

    };
    private boolean mIsAcSelected, mIsNonAcSelected, mIsSeaterSelected, mIsSleeperSelected;
    private String[] city;

    //Singleton pattern
    public static HomeFragmentNewest newInstance()
    {
        if(homeFragmentNewest == null)
        {
            Log.d(TAG, "Created new HomeFragment");
            homeFragmentNewest = new HomeFragmentNewest();
        }

        return homeFragmentNewest;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        BusLine dataFromAdapter = null;
//        if (mUserLocations.size() == 0)
//        {
//            // make sure the list doesn't duplicate by navigating back
//            if (getArguments() != null)
//            {
//                if(getArguments().getParcelableArrayList(getString(R.string.intent_user_list)) != null)
//                {
//                    final ArrayList<User> users = getArguments().getParcelableArrayList(getString(R.string.intent_user_list));
//                    mUserList.addAll(users);
//                }
//
//                if(getArguments().getParcelableArrayList(getString(R.string.intent_user_locations)) != null)
//                {
//                    final ArrayList<UserLocation> locations = getArguments().getParcelableArrayList(getString(R.string.intent_user_locations));
//                    mUserLocations.addAll(locations);
//                }
//
//                if(getArguments().getParcelable(getString(R.string.intent_bus_stops_selectable_adapter)) != null)
//                {
//                    dataFromAdapter = getArguments().getParcelable(getString(R.string.intent_bus_stops_selectable_adapter));
//                }
//
//                Log.d(TAG, "onCreate locations retreived for " + mUserLocations.size() + " users");
//
//
//                model = new BusStopsViewModel();
//
//            }
//        }
//
//        dataViewModel = ViewModelProviders.of(this).get(DataViewModel.class);
//        dataViewModel.init(this, dataFromAdapter);
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
//
////        dataViewModel.getmBusStopsLiveData().observe(this, new Observer<ArrayList<BusStop>>()
////        {
////            @Override
////            public void onChanged(ArrayList<BusStop> busStops)
////            {
////                addBusStopsMarker();
////
////                if(busStops.size() > 0)
////                {
////                    String url = getMapsApiDirectionsUrl(busStops);
////                    HomeFragmentNewest.ReadTask downloadTask = new  HomeFragmentNewest.ReadTask();
////                    downloadTask.execute(url);
////                }
////            }
////        });
//
//        dataViewModel.getmUserLocationsLiveData().observe(this, new Observer<ArrayList<UserLocation>>() {
//            @Override
//            public void onChanged(ArrayList<UserLocation> userLocations)
//            {
//                Log.d(TAG, "checking for users location update");
//                adjustMapLocationMarkers();
//            }
//        });
    }


    private void resetMap(){
        if(mGoogleMap != null) {
            mGoogleMap.clear();

            if(mClusterManager != null){
                mClusterManager.clearItems();
            }

            if (mClusterMarkers.size() > 0) {
                mClusterMarkers.clear();
                mClusterMarkers = new ArrayList<>();
            }

            if(mPolyLinesData.size() > 0){
                mPolyLinesData.clear();
                mPolyLinesData = new ArrayList<>();
            }
        }
    }

    /* create view */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);

        IntentFilter intentFilter = new IntentFilter("UPCOMING");

        Log.d(TAG, "onCreateView called");

        initView(view);
        String busRouteFromAdapter = "";
        ArrayList<BusStop> busStopsFromParcel = new ArrayList<>();
        if(getArguments() != null)
        {
            Log.d(TAG, "Parceble retreived from Fragment: " + getArguments().toString());
            if(getArguments().getParcelableArrayList(getString(R.string.intent_bus_stops_selectable_adapter)) == null)
            {
                if(getArguments().getParcelableArrayList(getString(R.string.intent_bus_stops)) != null)
                {
                    busStopsFromParcel = getArguments().getParcelableArrayList(getString(R.string.intent_bus_stops));
                    busStops.addAll(busStopsFromParcel);
                }
            }

            else
            {
                Log.d(TAG, "Retreiving BusStops from parceble");
                busStops = getArguments().getParcelableArrayList(getString(R.string.intent_bus_stops_selectable_adapter));
            }

            busRouteFromAdapter = getArguments().getString(getString(R.string.intent_bus_route_selectable_adapter));
        }


        getData(busStops, busRouteFromAdapter);
        setAdapters();
        setListener();



        if (mDepartDateCalendar == null) {
            mDepartDateCalendar = Calendar.getInstance();
        }


        updateLabel();

        mMapView = view.findViewById(R.id.user_list_map_main);
        view.findViewById(R.id.btn_full_screen_map).setOnClickListener(this);
//        view.findViewById(R.id.btn_reset_map).setOnClickListener(this);
        mMapContainer = view.findViewById(R.id.map_container_main);


        mMapContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                Log.d(TAG, "MAP IS BEING PRESSED");
                mHomeFragmentNestedScrollView.setNestedScrollingEnabled(false);
                return true;
            }
        });

        initGoogleMap(savedInstanceState);

        setUserPosition();




        return view;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
//            getUserDetails();
        } else
        {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    private void setUserPosition() {
        for (UserLocation userLocation : mUserLocations)
        {
            if(userLocation.getUser() == null)
            {
                Log.e(TAG, "Entering null in setUserPosition");
                return;
            }


            if (userLocation.getUser().getUser_id().equals(FirebaseAuth.getInstance().getUid())) {
                mUserPosition = userLocation;
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 101: {
                if (resultCode == RESULT_OK && null != data)
                {

                }
                break;
            }

            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
//                    getUserDetails();
                }
                else{
                    getLocationPermission();
                }
            }
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);



    }




    /* update label */
    private void updateLabel() {
        mEdDepartDate.setText(Constants.DateFormat.DAY_MONTH_YEAR_FORMATTER.format(mDepartDateCalendar.getTime()));


    }

    /* set adapter */
    private void setAdapters() {
        mRvNewOffer.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        mRvNewOffer.setAdapter(new NewOfferAdapter(getActivity(), mNewOfferList));

        mUpcomingStationAdapter = new UpcommingStationsAdapter(getActivity(), mUpcommingStation);
        mRvRecentSearch.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        mRvRecentSearch.setAdapter(mUpcomingStationAdapter);
        mUpcomingStationAdapter.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void stopLocationUpdates(){
        mHandler.removeCallbacks(mRunnable);
    }


    /* set listener */
    private void setListener() {
        mSearch.setOnClickListener(this);
        mTvViewNewOffers.setOnClickListener(this);
        mTvAC.setOnClickListener(this);
        mTvNonAc.setOnClickListener(this);
        mTvSleeper.setOnClickListener(this);
        mTvSeater.setOnClickListener(this);
        mIvSwap.setOnClickListener(this);
        mIvDescrease.setOnClickListener(this);
        mIvIncrease.setOnClickListener(this);
        mEdDepartDate.setOnClickListener(this);




        mEdFromCity.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEdFromCity.length() > 0) {
                    mView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    mView.setAlpha(0.2f);
                } else {
                    mView.setBackgroundColor(getResources().getColor(R.color.view_color));
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        mEdToCity.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //  if (validate()) {
                    mFrom = mEdFromCity.getText().toString();
                    mTo = mEdToCity.getText().toString();
                    Intent intent = new Intent(getActivity(), BusListActivity.class);
                    intent.putExtra(Constants.intentdata.TRIP_KEY, mEdFromCity.getText().toString() + " To " + mEdToCity.getText().toString());
                    startActivity(intent);
                    //  }
                    return true;
                }
                return false;
            }
        });
    }

    /* init view */
    private void initView(View view) {
        mRvNewOffer = view.findViewById(R.id.rvNewOffers);
        mTvViewNewOffers = view.findViewById(R.id.tvViewallNewOffer);
        mIvSwap = view.findViewById(R.id.ivSwap);
        mSearch = view.findViewById(R.id.btnSearch);
        mTvAC = view.findViewById(R.id.tvAc);
        mTvNonAc = view.findViewById(R.id.tvNonAc);
        mTvSleeper = view.findViewById(R.id.tvSleeper);
        mTvSeater = view.findViewById(R.id.tvSeater);
        mEdDepartDate = view.findViewById(R.id.edOneWay);
        mEdFromCity = view.findViewById(R.id.edFromCity);
        mEdToCity = view.findViewById(R.id.edToCity);
        mIvDescrease = view.findViewById(R.id.ivDescrease);
        mIvIncrease = view.findViewById(R.id.ivIncrease);
        mTvCount = view.findViewById(R.id.tvCount);
        mView = view.findViewById(R.id.view2);
        mRvRecentSearch = view.findViewById(R.id.rvRecentSearch);
        city = new String[]{getString(R.string.lbl_mumbai), getString(R.string.lbl_surat), getString(R.string.lbl_delhi), getString(R.string.lbl_pune)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_items, city);
        mEdFromCity.setThreshold(1);
        mEdFromCity.setAdapter(adapter);
        mEdToCity.setThreshold(1);
        mEdToCity.setAdapter(adapter);

        mIvDescrease.setVisibility(View.INVISIBLE);

        mHomeFragmentNestedScrollView = view.findViewById(R.id.homeFragmentNestedScroll);
        homeFragmentMain = view.findViewById(R.id.homeFragmentDaddyIssue);
        mainCustomNestedScrollView = view.findViewById(R.id.homeFragmentNestedScroll);

    }

    /* et data */
    private void getData(ArrayList<BusStop> busStops, String selectedRoute) {

        mUpcommingStation = new ArrayList<>();

        if(!busStops.isEmpty())
        {
            for(BusStop busStop : busStops)
            {
                BookingModel bookingModel = new BookingModel(busStop.getName(), selectedRoute);
                mUpcommingStation.add(bookingModel);
            }
        }

        else
        {
            mUpcommingStation.add(new BookingModel("Please select your route", getString(R.string.lbl_date)));
        }

        mNewOfferList = new ArrayList<>();
        mNewOfferList.add(new NewOfferModel(getString(R.string.lbl_offer1)));
        mNewOfferList.add(new NewOfferModel(getString(R.string.lbl_offer2)));
        mNewOfferList.add(new NewOfferModel(getString(R.string.lbl_offer1)));
        mNewOfferList.add(new NewOfferModel(getString(R.string.lbl_offer2)));
        mNewOfferList.add(new NewOfferModel(getString(R.string.lbl_offer2)));
    }


    /* onClick listener */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.edOneWay:

                DatePickerDialog datePickerDialogs = new DatePickerDialog(getActivity(), date, mDepartDateCalendar
                        .get(Calendar.YEAR), mDepartDateCalendar.get(Calendar.MONTH),
                        mDepartDateCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialogs.getDatePicker().setMinDate(new Date().getTime());
                datePickerDialogs.show();
                break;
            case R.id.ivSwap:
                String mFromCity = mEdFromCity.getText().toString();
                String mToCity = mEdToCity.getText().toString();
                Animation startRotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
                mIvSwap.startAnimation(startRotateAnimation);
                mFromCity = mFromCity + mToCity;
                mToCity = mFromCity.substring(0, mFromCity.length() - mToCity.length());
                mFromCity = mFromCity.substring(mToCity.length());


                mEdFromCity.setText(mFromCity);
                mEdToCity.setText(mToCity);
                mEdFromCity.setSelection(mFromCity.length());
                mEdToCity.setSelection(mToCity.length());
                break;
            case R.id.ivDescrease:
                mValue = mValue - 1;
                mTvCount.setText(String.valueOf(mValue));

                if (mValue <= 1) {

                    ((BaseActivity)Objects.requireNonNull(getActivity())).invisibleView(mIvDescrease);

                } else {

                    ((BaseActivity)getActivity()).showView(mIvDescrease);
                    mTvCount.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    mTvCount.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                break;
            case R.id.ivIncrease:
                mValue = mValue + 1;

                if (mValue < 1) {
                    mValue = 1;

                } else {

                    if (mValue == 1) ((BaseActivity)getActivity()).invisibleView(mIvDescrease);
                    else
                        ((BaseActivity)Objects.requireNonNull(getActivity())).showView(mIvDescrease);
                    mTvCount.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    mTvCount.setText(String.valueOf(mValue));
                    mTvCount.setTextColor(getResources().getColor(R.color.colorPrimary));

                }
                break;
            case R.id.tvAc:
                if (!mIsAcSelected) {
                    enable((TextView)v);
                    mIsAcSelected = true;
                } else {
                    disable((TextView)v);
                    mIsAcSelected = false;

                }
                break;
            case R.id.tvNonAc:
                if (!mIsNonAcSelected) {
                    enable((TextView)v);
                    mIsNonAcSelected = true;
                } else {
                    disable((TextView)v);
                    mIsNonAcSelected = false;

                }
                break;
            case R.id.tvSleeper:
                if (!mIsSleeperSelected) {
                    enable((TextView)v);
                    mIsSleeperSelected = true;
                } else {
                    disable((TextView)v);
                    mIsSleeperSelected = false;

                }
                break;
            case R.id.tvSeater:
                if (!mIsSeaterSelected) {
                    enable((TextView)v);
                    mIsSeaterSelected = true;
                } else {
                    disable((TextView)v);
                    mIsSeaterSelected = false;

                }
                break;
            case R.id.btnSearch:
                //    if (validate()) {

                mFrom = mEdFromCity.getText().toString();
                mTo = mEdToCity.getText().toString();
                Intent intent = new Intent(getActivity(), BusListActivity.class);
                intent.putExtra(Constants.intentdata.TRIP_KEY, mFrom + " To " + mTo);
                startActivity(intent);
                //    }
                break;
            case R.id.tvViewallNewOffer:
                Intent i = new Intent(getActivity(), OffersActivity.class);
                i.putExtra(Constants.intentdata.OFFER, mNewOfferList);
                startActivity(i);
                break;

            case R.id.btn_full_screen_map:
                Log.d(TAG, "MAP IS BEING PRESSED");

                mainCustomNestedScrollView.setNestedScrollingEnabled(!mainCustomNestedScrollView.isScrollable());

                break;
        }

    }

    /* validation */
    private boolean validate() {
        boolean flag = true;
        if (TextUtils.isEmpty(mEdFromCity.getText())) {
            flag = false;
            ((BaseActivity)Objects.requireNonNull(getActivity())).showToast(getString(R.string.msg_from));
        } else if (TextUtils.isEmpty(mEdToCity.getText())) {
            flag = false;
            ((BaseActivity)Objects.requireNonNull(getActivity())).showToast(getString(R.string.msg_to));
        }
        return flag;
    }

    /* disable textview */
    private void disable(TextView textView) {
        textView.setTextColor(getResources().getColor(R.color.textchild));
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), R.color.textchild), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    /* enable textview */
    private void enable(TextView textView) {
        textView.setTextColor(getResources().getColor(R.color.startcolor));
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), R.color.startcolor), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    /* onClick listener */
    @Override
    public void onClick(BookingModel BookingModel) {

        Intent intent = new Intent(getActivity(), BusListActivity.class);
        intent.putExtra(Constants.intentdata.SEARCH_BUS, BookingModel.getDestination());
        startActivity(intent);
    }
    /* change destination */
    public void ChangeDestination(String result)
    {
        if (mEdFromCity.length() == 0) {
            mEdFromCity.setText(result);
        } else {
            mEdToCity.setText(result);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
//        stopLocationUpdates();

    }

    @Override
    public void onResume() {
        super.onResume();
//        startSavingUserLocationRunnable();
        mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        stopLocationUpdates();
        mMapView.onDestroy();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void adjustMapLocationMarkers(){
        Log.d(TAG, "retrieveUserLocations: retrieving location of all users in the chatroom.");

        try{
            Log.d(TAG, "retrieveUserLocations retreiving " + mClusterMarkers.size() + "markers");
            for(final ClusterMarker clusterMarker: mClusterMarkers){

                updatedUserLocation = dataViewModel.getUserLocation().getValue();
                for (int i = 0; i < mClusterMarkers.size(); i++) {
                    try {
                        if (mClusterMarkers.get(i).getUser().getUser_id().equals(updatedUserLocation.getUser().getUser_id())) {

                            LatLng updatedLatLng = new LatLng(
                                    updatedUserLocation.getGeo_point().getLatitude(),
                                    updatedUserLocation.getGeo_point().getLongitude()
                            );
                            Log.d(TAG, "retrieveUserLocations marker set for position " + updatedLatLng.toString());

                            mClusterMarkers.get(i).setPosition(updatedLatLng);
                            mClusterManagerRenderer.setUpdateMarker(mClusterMarkers.get(i));
                        }

                    } catch (NullPointerException e) {
                        Log.e(TAG, "retrieveUserLocations: NullPointerException: " + e.getMessage());
                    }
                }

                mClusterManager.cluster();
            }



        }catch (IllegalStateException e){
            Log.e(TAG,  "retrieveUserLocations: Fragment was destroyed during Firestore query. Ending query." + e.getMessage() );
        }
    }

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this.getContext(), LocationService.class);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                getActivity().startForegroundService(serviceIntent);
            }else{
                Log.d(TAG, "LOCSER STARTING");
                getActivity().startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager)getActivity(). getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(LocationService.class.getName().equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }



    //-----------------------------------------------
    //TODO: Pass locations to method
    private String getMapsApiDirectionsUrl(ArrayList<BusStop> busStops)
    {
        StringBuilder waypoints = new StringBuilder();

        String origin = "origin=" + busStops.get(0).getPosition().getLatitude() + "," + busStops.get(0).getPosition().getLongitude();

        String fileType = "json?";

        waypoints.append("waypoints=");

        for(int i = 0; i < busStops.size() - 1; i++)
        {
            if(i != 0)
            {
                waypoints.append(busStops.get(i).getPosition().getLatitude());
                waypoints.append(",");
                waypoints.append(busStops.get(i).getPosition().getLongitude());
                waypoints.append("|");
            }

            if(i == busStops.size() - 2)
            {
                waypoints.append(busStops.get(i).getPosition().getLatitude());
                waypoints.append(",");
                waypoints.append(busStops.get(i).getPosition().getLongitude());
            }
        }


        String destination = "destination=" + busStops.get(busStops.size() - 1).getPosition().getLatitude() + "," + busStops.get(busStops.size() - 1).getPosition().getLongitude();
        String sensor = "sensor=false";
        String apiKey = "key=" + "AIzaSyDys80kcuWvfDK0vhmlNAvnDVSNn4ec19A";

        String url = "https://maps.googleapis.com/maps/api/directions/" + fileType + origin + "&" + waypoints + "&" + destination + "&" + apiKey;

//        String url = "\n" +
//                "https://maps.googleapis.com/maps/api/directions/json?origin=40.747387,-73.886224&waypoints=40.742691, -73.895826|40.728632, -73.905661&destination=40.746782, -73.976741&sensor=false&key=AIzaSyDys80kcuWvfDK0vhmlNAvnDVSNn4ec19A";

        return url;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.e("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new HomeFragmentNewest.ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                Log.e(TAG,e.getMessage()) ;
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            Log.d(TAG, "onPostExecute routes size: " + routes.size());

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(10);
                polyLineOptions.color(Color.BLUE);
            }
            if(polyLineOptions != null)
                mGoogleMap.addPolyline(polyLineOptions);
        }
    }


    //--------------------------------------------
    private void addBusStopsMarker(){

        if(mGoogleMap != null){

//            resetMap();

            if(mClusterManager == null){
                mClusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), mGoogleMap);
            }
            if(mClusterManagerRenderer == null){
                mClusterManagerRenderer = new MyClusterManagerRenderer(
                        getActivity(),
                        mGoogleMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }
            mGoogleMap.setOnInfoWindowClickListener(this);

            Log.d(TAG, "Begin adding marker for " + mUserLocations.size() + " users");


            }

        if(mClusterManager != null)
            mClusterManager.cluster();

            if(dataViewModel.getmBusStopsLiveData().getValue() != null)
            {
                for(BusStop busStop: dataViewModel.getmBusStopsLiveData().getValue())
                {
                    if (mGoogleMap != null)
                    {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(busStop.getPosition().getLatitude(), busStop.getPosition().getLongitude()));

                        addedMarkersOnMapArray.add(mGoogleMap.addMarker(markerOptions
                                .title(busStop.getName())));
                        Log.i(TAG, "Marker position: " +  markerOptions.getPosition().toString());
                    }
                }
                setCameraViewToCluster(addedMarkersOnMapArray);
            }

    }


    private void addLocationMarkers(ArrayList<UserLocation> userLocations)
    {
        if(mGoogleMap != null){

            resetMap();

            if(mClusterManager == null){
                mClusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), mGoogleMap);
            }
            if(mClusterManagerRenderer == null){
                mClusterManagerRenderer = new MyClusterManagerRenderer(
                        getActivity(),
                        mGoogleMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }
            mGoogleMap.setOnInfoWindowClickListener(this);

            Log.d(TAG, "Begin adding marker for " + userLocations.size() + " users");


        }


        int i = 0;
        for(UserLocation userLocation: userLocations){

            Log.d(TAG, "addMapMarkers: location: " + userLocation.getGeo_point().toString());
            try{
                String snippet = "";
                if(userLocation.getUser() != null)
                {
                    if(userLocation.getUser().getUser_id().equals(FirebaseAuth.getInstance().getUid())){
                        snippet = "This is you";

                        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                            @Override
                            public void onMapLoaded() {
//                                            if(userLocation != null)
//                                setCameraViewToBus(userLocation);
                            }
                        });
                    }
                    else{
                        snippet = "Determine route to " + userLocation.getUser().getUsername() + "?";
                    }

                    int avatar = R.drawable.busicon; // set the default avatar

                    try
                    {
                        avatar = Integer.parseInt(userLocation.getUser().getAvatar());
                    }catch (NumberFormatException e){
                        Log.d(TAG, "addMapMarkers: no avatar for " + userLocation.getUser().getUsername() + ", setting default.");
                    }

                    ClusterMarker newClusterMarker = new ClusterMarker(
                            new LatLng(userLocation.getGeo_point().getLatitude(), userLocation.getGeo_point().getLongitude()),
                            userLocation.getUser().getUsername(),
                            snippet,
                            avatar,
                            userLocation.getUser()
                    );
                    mClusterManager.addItem(newClusterMarker);
                    mClusterMarkers.add(newClusterMarker);
                }

            }catch (NullPointerException e){
                    Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage() );
            }
        }
        mClusterManager.cluster();
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_api_key))
                    .build();
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {
        if(marker.getSnippet().equals("This is you")){
            marker.hideInfoWindow();
        }
        else{

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(marker.getSnippet())
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            resetSelectedMarker();
                            mSelectedMarker = marker;
                            calculateDirections(marker);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void calculateDirections(Marker marker){
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        mUserPosition.getGeo_point().getLatitude(),
                        mUserPosition.getGeo_point().getLongitude()
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {

                Log.d(TAG, "onResult: successfully retrieved directions.");
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }


    private void resetSelectedMarker(){
        if(mSelectedMarker != null){
            mSelectedMarker.setVisible(true);
            mSelectedMarker = null;
            removeTripMarkers();
        }
    }

    private void removeTripMarkers(){
        for(Marker marker: mTripMarkers){
            marker.remove();
        }
    }

    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);
                if(mPolyLinesData.size() > 0){
                    for(PolylineData polylineData: mPolyLinesData){
                        polylineData.getPolyline().remove();
                    }
                    mPolyLinesData.clear();
                    mPolyLinesData = new ArrayList<>();
                }

                double duration = 999999999;
                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mGoogleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.dark_gray));
                    polyline.setClickable(true);
                    mPolyLinesData.add(new PolylineData(polyline, route.legs[0]));

                    // highlight the fastest route and adjust camera
                    double tempDuration = route.legs[0].duration.inSeconds;
                    if(tempDuration < duration){
                        duration = tempDuration;
                        onPolylineClick(polyline);
                        zoomRoute(polyline.getPoints());
                    }

                    mSelectedMarker.setVisible(false);
                }
            }
        });
    }



    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mGoogleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 50;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mGoogleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }


    @Override
    public void onPolylineClick(Polyline polyline) {

        int index = 0;
        for(PolylineData polylineData: mPolyLinesData){
            index++;
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.blue));
                polylineData.getPolyline().setZIndex(1);

                LatLng endLocation = new LatLng(
                        polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );

                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(endLocation)
                        .title("Trip #" + index)
                        .snippet("Duration: " + polylineData.getLeg().duration
                        ));

                mTripMarkers.add(marker);

                marker.showInfoWindow();
            }
            else{
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.dark_gray));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        //TODO: BREAKING WARNING
        addBusStopsMarker();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        BusLine dataFromAdapter = null;



        // make sure the list doesn't duplicate by navigating back
        if (getArguments() != null)
        {
            if(getArguments().getParcelableArrayList(getString(R.string.intent_user_list)) != null)
            {
                final ArrayList<User> users = getArguments().getParcelableArrayList(getString(R.string.intent_user_list));
                mUserList.addAll(users);
            }

            if(getArguments().getParcelableArrayList(getString(R.string.intent_user_locations)) != null)
            {
                final ArrayList<UserLocation> locations = getArguments().getParcelableArrayList(getString(R.string.intent_user_locations));
                mUserLocations.addAll(locations);
            }

            if(getArguments().getParcelable(getString(R.string.intent_bus_stops_selectable_adapter)) != null)
            {
                dataFromAdapter = getArguments().getParcelable(getString(R.string.intent_bus_stops_selectable_adapter));
            }

            Log.d(TAG, "onCreate locations retreived for " + mUserLocations.size() + " users");


            model = new BusStopsViewModel();

        }

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel.class);
        dataViewModel.init(HomeFragmentNewest.newInstance(), dataFromAdapter);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());


        dataViewModel.getUserLocationInitial(new FirebaseListenerInterface() {

            @Override
            public void onCallbackAllBusLineGot(ArrayList<BusLine> busLines) {

            }


            @Override
            public void onCallbackAllUserLocationGot(ArrayList<UserLocation> userLocations)
            {
                addLocationMarkers(userLocations);
            }

            @Override
            public void onCallBackAllBusStopsGot(ArrayList<BusStop> busStops) {

            }
        }, mContext);


        dataViewModel.getmBusStopsLiveData().observe(this, new Observer<ArrayList<BusStop>>()
        {
            @Override
            public void onChanged(ArrayList<BusStop> busStops)
            {
                addBusStopsMarker();

                if(busStops.size() > 0)
                {
                    String url = getMapsApiDirectionsUrl(busStops);
                    HomeFragmentNewest.ReadTask downloadTask = new  HomeFragmentNewest.ReadTask();
                    downloadTask.execute(url);
                }
            }
        });

        dataViewModel.getmUserLocationsLiveData().observe(this, new Observer<ArrayList<UserLocation>>() {
            @Override
            public void onChanged(ArrayList<UserLocation> userLocations)
            {
                Log.d(TAG, "checking for users location update");
                adjustMapLocationMarkers();
            }
        });
    }


    private void setCameraViewToBus(UserLocation mUserLocation) {

        // Set a boundary to start
        double bottomBoundary = mUserLocation.getGeo_point().getLatitude() - .1;
        double leftBoundary = mUserLocation.getGeo_point().getLongitude() - .1;
        double topBoundary = mUserLocation.getGeo_point().getLatitude() + .1;
        double rightBoundary = mUserLocation.getGeo_point().getLongitude() + .1;
        mMapBoundary = new LatLngBounds(
                new LatLng(bottomBoundary, leftBoundary),
                new LatLng(topBoundary, rightBoundary)
        );

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
    }


    private void setCameraViewToCluster(ArrayList<Marker> markers)
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        if(!markers.isEmpty())
        {
            for(Marker m : markers)
            {
                builder.include(m.getPosition());
            }

            LatLngBounds bounds = builder.build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 25, 25, 5);
            mGoogleMap.animateCamera(cameraUpdate);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

}


