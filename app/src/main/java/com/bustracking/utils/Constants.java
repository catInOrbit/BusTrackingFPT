package com.bustracking.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
    /*variable declaration*/
    public static final String NA = "N/A";
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;

    /*Date format*/
    public static class DateFormat {
        public static String dd_MM = "dd-MMM";
        public static String  dd_MM_yyyy = "dd - MMM - yyyy";
        public static final SimpleDateFormat DAY_MONTH_FORMATTER = new SimpleDateFormat(dd_MM, Locale.getDefault());
        public static final SimpleDateFormat  DAY_MONTH_YEAR_FORMATTER= new SimpleDateFormat(dd_MM_yyyy, Locale.getDefault());

    }
    /*intent data*/
    public static class intentdata
    {
        public static String CARDDETAIL="carddetail";
        public static String TRAVELLERNAME="TravellerName";
        public static String TYPECOACH="typecoach";
        public static String PRICE="price";
        public static String HOLD="hold";
        public static String PACKAGE="package";
        public static String OFFER="offer";
        public static String TRIP_KEY="trip_key";
        public static String SEARCH_BUS="search_bus";
        public static String PACKAGE_NAME="package_name";
        public static String CARDFLAG="cardflag";

        public static String ITEMBUSADAPTERDATA="adapterData";
        public static String ITEMBUSADAPTERCHOSENTYPE="chosenType";

        public static String intent_bus_stops_selectable_adapter="intent_bus_stops_adapter";
        public static String intent_bus_route_selectable_adapter="intent_bus_route_adapter";
        public static String intent_bus_line_selectable_adapter="intent_bus_line_adapter";


    }

}
