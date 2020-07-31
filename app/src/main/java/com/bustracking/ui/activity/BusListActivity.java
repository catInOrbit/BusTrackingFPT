package com.bustracking.ui.activity;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bustracking.BaseActivity;
import com.bustracking.R;
import com.bustracking.model.busmodels.BusModel;
import com.bustracking.model.busmodels.BusLine;
import com.bustracking.model.busmodels.BusRoute;
import com.bustracking.model.busmodels.BusStop;
import com.bustracking.model.usermodels.User;
import com.bustracking.model.usermodels.UserLocation;
import com.bustracking.ui.adapter.ItemBusAdapter;
import com.bustracking.utils.Constants;
import com.bustracking.utils.FirebaseListenerInterface;
import com.bustracking.utils.GenericToObjectConversion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class BusListActivity extends BaseActivity implements View.OnClickListener{

    /*variable declaration*/
    private RecyclerView mRvBuses;
    private List<BusModel> mBusList;
    private ImageView mIvBack, mIvFilter, mIvPrevious, mIvNext;
    private TextView mTvDate, mTvTitle;
    private Calendar mDepartDateCalendar;
    private FirebaseFirestore mDb;

    private ListenerRegistration busesUpdateListener;


    private ArrayList<BusLine> busLines = new ArrayList<>();


    private static String TAG = "BusListActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_list);

        initLayouts();
        initializeListeners();


        mDb = FirebaseFirestore.getInstance();
//        getBusLines(mDb);
        getNestedBusLines(mDb, new FirebaseListenerInterface() {


            @Override
            public void onCallbackAllBusLineGot(ArrayList<BusLine> busLines)
            {
                fillBusListDetail(busLines);
            }

            @Override
            public void onCallbackAllUserLocationGot(ArrayList<UserLocation> mUserLocations) {

            }

            @Override
            public void onCallBackAllBusStopsGot(ArrayList<BusStop> busStops) {

            }
        });

    }

    private void getNestedBusLines(FirebaseFirestore firebaseFirestoreMdb, FirebaseListenerInterface fli)
    {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();

        firebaseFirestoreMdb.setFirestoreSettings(settings);

        ArrayList<BusLine> busLines = new ArrayList<>();

       CollectionReference scheduleCollection = firebaseFirestoreMdb.collection(getString(R.string.collection_schedule));
       scheduleCollection.get().addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task)
           {

               for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult())
               {
                   ArrayList<BusStop> stations = new ArrayList<>();
                   ArrayList<BusRoute> routes = new ArrayList<>();

                   BusLine busLine = queryDocumentSnapshot.toObject(BusLine.class);
                   busLines.add(busLine);

                   CollectionReference stationCollection = firebaseFirestoreMdb.
                           collection(getString(R.string.collection_schedule)).
                           document(busLine.getLineID()).collection(getString(R.string.collection_bus_stops));
                   stations.clear();
                   stationCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<QuerySnapshot> task)
                       {
                           for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult())
                           {
                               BusStop busStop = queryDocumentSnapshot.toObject(BusStop.class);
                               stations.add(busStop);
                           }

                           busLine.setBusStops(stations);
                       }
                   });
                           firebaseFirestoreMdb.collection(getString(R.string.collection_schedule))
                                   .document(busLine.getLineID())
                                   .collection(getString(R.string.collection_bus_routes))
                   .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<QuerySnapshot> task)
                       {
//                           ArrayList<Bus> buses = new ArrayList<>();
                           for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult())
                           {
                               BusRoute busRoute = queryDocumentSnapshot.toObject(BusRoute.class);
                               routes.add(busRoute);
                           }
                           busLine.setBusRoutes(routes);

                           fli.onCallbackAllBusLineGot(busLines);
                       }
                   });
               }
           }
       });
    }

    /* initialize listener */
    private void initializeListeners() {
        mIvBack.setOnClickListener(this);
        mIvFilter.setOnClickListener(this);
        mIvPrevious.setOnClickListener(this);
        mIvNext.setOnClickListener(this);

        String mTitle=getIntent().getStringExtra(Constants.intentdata.TRIP_KEY);
        String mSearchTitle=getIntent().getStringExtra(Constants.intentdata.SEARCH_BUS);
        String mPackageTitle=getIntent().getStringExtra(Constants.intentdata.PACKAGE_NAME);

        if(mTitle!=null) {
            mTvTitle.setText(mTitle);
        }
        if(mSearchTitle!=null)
        {
            mTvTitle.setText(mSearchTitle);
        }
        if(mPackageTitle!=null)
        {
            mTvTitle.setText(mPackageTitle);
        }
    }


    private void fillBusListDetail(ArrayList<BusLine> busLines) {
        mBusList = new ArrayList<>();
        mRvBuses.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

//        mBusList.add(new BusModel(getString(R.string.lbl_travelname), getString(R.string.lbl_starttime1), getString(R.string.text_am),getString(R.string.lbl_endtime1),getString(R.string.text_pm),getString(R.string.lbl_totalDuration), getString(R.string.lbl_hold), getString(R.string.lbl_type2), 3, getString(R.string.lbl_price1)));

        Bundle intentBundle = getIntent().getExtras();

        Object[] data = null;

        if(intentBundle == null || intentBundle.get(Constants.intentdata.ITEMBUSADAPTERDATA) == null)
        {
            data = GenericToObjectConversion.convertBusLineInstanceToObject(busLines);
            mRvBuses.setAdapter(new ItemBusAdapter(this, data, "L"));
        }

        else
        {

            ArrayList<BusLine> tempList = new ArrayList<>();
            BusLine busLineFromIntent =
                    (BusLine) intentBundle.get(Constants.intentdata.ITEMBUSADAPTERDATA);
            tempList.add(busLineFromIntent);

            data = GenericToObjectConversion.convertBusLineInstanceToObject(tempList);
            mRvBuses.setAdapter(new ItemBusAdapter(this, data, "R"));
        }


        RunLayoutAnimation(mRvBuses);

        mDepartDateCalendar = Calendar.getInstance();
        mTvDate.setText(Constants.DateFormat.DAY_MONTH_YEAR_FORMATTER.format(mDepartDateCalendar.getTime()));
    }


    /* init layout */
    private void initLayouts() {
        mRvBuses = findViewById(R.id.rvBus);
        mIvBack = findViewById(R.id.ivBack);
        mIvFilter = findViewById(R.id.ivFilter);
        mIvPrevious = findViewById(R.id.ivPrevious);
        mIvNext = findViewById(R.id.ivNext);
        mTvDate = findViewById(R.id.tvDate);
        mTvTitle = findViewById(R.id.tvTitle);
    }

    /* onClick listener */
    @Override
    public void onClick(View v) {
        if (v == mIvBack) {
            onBackPressed();
        } else if (v == mIvPrevious) {
            mDepartDateCalendar.add(Calendar.DATE, -1);
            mTvDate.setText(Constants.DateFormat.DAY_MONTH_YEAR_FORMATTER.format(mDepartDateCalendar.getTime()));
        } else if (v == mIvNext) {
            mDepartDateCalendar.add(Calendar.DATE, 1);
            mTvDate.setText(Constants.DateFormat.DAY_MONTH_YEAR_FORMATTER.format(mDepartDateCalendar.getTime()));

        } else if (v == mIvFilter) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_filter);
            dialog.setCancelable(true);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));


            final TextView tvMax = dialog.findViewById(R.id.endprice);
            final Button mBtnApply = dialog.findViewById(R.id.btnApply);
            ImageView mIvClose = dialog.findViewById(R.id.ivClose);

            mBtnApply.setStateListAnimator(null);
            mBtnApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            AppCompatSeekBar rangeSeekbar1 = dialog.findViewById(R.id.rangeSeekbar1);
            rangeSeekbar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChangedValue = 100;
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChangedValue = progress;
                    tvMax.setText(String.valueOf(progressChangedValue));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    tvMax.setText(String.valueOf(progressChangedValue));
                }
            });

            mIvClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }





}
