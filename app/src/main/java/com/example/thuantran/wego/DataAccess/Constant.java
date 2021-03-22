package com.example.thuantran.wego.DataAccess;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class Constant {

    public static String GEOFIRE_LOCATION_TRIP_PASSENGER = "GEOFIRE_LOCATION_TRIP_PASSENGER";
    public static String GEOFIRE_LOCATION_TRIP_DRIVER    = "GEOFIRE_LOCATION_TRIP_DRIVER";
    public static String USER = "USER";
    public static String PROFILE = "PROFILE";
    public static String MCAR = "MCAR";
    public static String LAST_SMS = "LAST_SMS";
    public static String REVIEW = "REVIEW";
    public static String REPORT = "REPORT";
    public static String ADDRESS = "ADDRESS";
    public static String TRIP_PASSENGER = "TRIP_PASSENGER";
    public static String TRIP_PASSENGER_BOOKNOW = "TRIP_PASSENGER_BOOKNOW";
    public static String TRIP_DRIVER = "TRIP_DRIVER";
    public static String mRELATION = "mRELATION";
    public static String SMS = "SMS";
    public static String PROMOCODE = "PROMOCODE";


    public static LatLng BOUND_CORNER_NW = new LatLng(10.900258, 106.454250);
    public static LatLng BOUND_CORNER_SE = new LatLng(10.680868, 106.842264);

    public static float radius   = 2;


    public static int MIN_PERCENT  = 10;
    public static int MAX_PERCENT  = 10;

    public static int DR_MAXSELECT = 5;
    public static int DR_PERCENT   = 2;

}
