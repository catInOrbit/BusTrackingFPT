package com.bustracking.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bustracking.R;
import com.bustracking.model.busmodels.BusLine;
import com.bustracking.model.busmodels.BusRoute;
import com.bustracking.model.busmodels.BusStop;
import com.bustracking.ui.activity.BusListActivity;
import com.bustracking.ui.activity.DashboardActivity;
import com.bustracking.ui.fragment.HomeFragmentNewest;
import com.bustracking.utils.Constants;

import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


public class ItemBusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /*variable declaration*/
    private Context mContext;

    //TODO: Replacement of BusModel to BusLine for data extraction
    private Object[] dataObject;
    /*constructor*/

    private String chosenType = "";

    private static String TAG = "ItemBusAdapter";

    private static final int BUS_LINE_VIEW = 0;
    private static final int BUS_ROUTE_VIEW = 1;

    public ItemBusAdapter(Context aCtx, Object[] dataObject, String chosenType) {
        /* initialize parameter*/
        this.mContext = aCtx;
        this.dataObject = dataObject;
        this.chosenType = chosenType;
    }

    /*  inflate layout */
    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if(viewType == BUS_LINE_VIEW)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_available_bus_line, parent, false);
            return new LineItemHolder(view);
        }
        else if (viewType == BUS_ROUTE_VIEW)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_available_bus_route, parent, false);
            return new RouteItemHolder(view);
        }

        return null;
    }

    /*bind viewholder*/
    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if(viewHolder instanceof LineItemHolder)
        {

            ArrayList<BusLine> busLines = new ArrayList<>();
            for(Object objectData : dataObject)
            {
                BusLine busLine = (BusLine) objectData;
                busLines.add(busLine);
            }


            BusLine mBusModel = busLines.get(position);
            ((LineItemHolder) viewHolder).mLineNumber.setText(String.valueOf(position));
            ((LineItemHolder) viewHolder).mlineName.setText(mBusModel.getLineName());

            ((LineItemHolder) viewHolder).mRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    Intent i = new Intent(mContext, BusListActivity.class);

                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    BusLine busLineDataPass = busLines.get(position);

                    chosenType = "R";
                    i.putExtra(Constants.intentdata.ITEMBUSADAPTERDATA, busLineDataPass);
                    i.putExtra(Constants.intentdata.ITEMBUSADAPTERCHOSENTYPE, chosenType);
                    mContext.startActivity(i);
                }
            });
        }

        else if (viewHolder instanceof RouteItemHolder)
        {

            ArrayList<BusRoute> busRoutes = ((BusLine) dataObject[0]).getBusRoutes();

            BusRoute mBusModel = busRoutes.get(position);

            Log.d(this.getClass().getName(), "Creating view holder for item number: " + position);

            ((RouteItemHolder) viewHolder).mRouteName.setText(mBusModel.getRouteName());

            Date dateTimeNow = new Date();

            if(dateTimeNow.after(mBusModel.getTimeStart()) && dateTimeNow.before(mBusModel.getTimeEnd()))
                ((RouteItemHolder) viewHolder).mRelativeLayout.setBackgroundColor(Color.parseColor("#ffc17a"));

            String startTimeString = String.valueOf(mBusModel.getTimeStart().getHours());
            String endTimeString = String.valueOf(mBusModel.getTimeEnd().getHours());

            ((RouteItemHolder) viewHolder).mStartTime.setText(startTimeString);
            ((RouteItemHolder) viewHolder).mEndTime.setText(endTimeString);
            ((RouteItemHolder) viewHolder).mTotalDuration.setText("---");

            //Creating view for every bus stops in bus line

            ArrayList<BusStop> busStops = ((BusLine)dataObject[0]).getBusStops();

            for (BusStop busStop : busStops)
            {
                TextView textView = new TextView(mContext);
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "font/googlesansbold.ttf");
                textView.setTypeface(typeface);
                textView.setText(busStop.getName());
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.textheader));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.font_medium));
                ((RouteItemHolder) viewHolder).busStopList.addView(textView);
                ((RouteItemHolder) viewHolder).busStopList.invalidate();
            }

            ((RouteItemHolder) viewHolder).mRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    //TODO: Code navigation for clicking a route --> to dashboard --> homeFragment with bus stops

                    ArrayList<BusStop> busStopsDataPass = ((BusLine) dataObject[0]).getBusStops();
                    BusRoute currentSelectedRoute = ((BusLine) dataObject[0]).getBusRoutes().get(position);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(Constants.intentdata.intent_bus_stops_selectable_adapter, busStopsDataPass);
                    bundle.putString(Constants.intentdata.intent_bus_route_selectable_adapter, currentSelectedRoute.getRouteName());

//                    chosenType = "L";
//
//                    Intent intent = new Intent("UPCOMING");
//                    intent.putExtra(Constants.intentdata.intent_bus_stops_selectable_adapter, busStopsDataPass);
//                    mContext.sendBroadcast(intent);

                    Intent dashboardActivityIntent = new Intent(mContext, DashboardActivity.class);
                    dashboardActivityIntent.putExtra(Constants.intentdata.intent_bus_stops_selectable_adapter, busStopsDataPass);
                    dashboardActivityIntent.putExtra(Constants.intentdata.intent_bus_route_selectable_adapter, currentSelectedRoute.getRouteName());
                    dashboardActivityIntent.putExtra(Constants.intentdata.intent_bus_line_selectable_adapter, ((BusLine) dataObject[0]));


                    mContext.startActivity(dashboardActivityIntent);
                }
            });
        }

    }

    /*item count*/
    @Override
    public int getItemCount()
    {
        if(chosenType.equals("L"))
           return countBusLines();
        else
            return countBusRoutes();
    }

    private int countBusLines()
    {
        ArrayList<BusLine> busLines = new ArrayList<>();
        for(Object objectData : dataObject)
        {
            BusLine busLine = (BusLine) objectData;
            busLines.add(busLine);
        }

        return  busLines.size();
    }

    private int countBusRoutes()
    {
        ArrayList<BusLine> busLines = new ArrayList<>();
        for(Object objectData : dataObject)
        {
            BusLine busLine = (BusLine) objectData;
            busLines.add(busLine);
        }

        return busLines.get(0).getBusRoutes().size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if(chosenType.equals("L"))
            return BUS_LINE_VIEW;
        else if(chosenType.equals("R"))
            return BUS_ROUTE_VIEW;
        return -1;
    }



    /*view holder*/
    class RouteItemHolder extends RecyclerView.ViewHolder {

        private TextView mRouteName, mStartTime, mEndTime, mTotalDuration, mTvHold, mTvPrice, mTvTypeCoach;
        private RelativeLayout mRelativeLayout;
        private LinearLayout busStopList;

        RouteItemHolder(View itemView)
        {
            super(itemView);
            mRouteName = itemView.findViewById(R.id.tvTravellerName);
            mStartTime = itemView.findViewById(R.id.tvStartTime);
            mEndTime = itemView.findViewById(R.id.tvEndTime);
            mTotalDuration = itemView.findViewById(R.id.tvTotalDuration);
            mTvHold = itemView.findViewById(R.id.tvHold);
            mTvPrice = itemView.findViewById(R.id.tvPrice);
            mTvTypeCoach = itemView.findViewById(R.id.tvTypeCoach);
            mRelativeLayout = itemView.findViewById(R.id.rlMain);
            busStopList = itemView.findViewById(R.id.busStops);
        }

    }

    class LineItemHolder extends RecyclerView.ViewHolder
    {
        private TextView mlineName, mLineNumber;
        private RelativeLayout mRelativeLayout;

        LineItemHolder(View itemView)
        {
            super(itemView);
            mlineName = itemView.findViewById(R.id.lineName);
            mLineNumber = itemView.findViewById(R.id.lineNumber);
            mRelativeLayout = itemView.findViewById(R.id.rlMain);
        }

    }

}