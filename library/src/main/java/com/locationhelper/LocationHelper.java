package com.locationhelper;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by dima on 2/24/15.
 */
public class LocationHelper {

    public static final String TAG = LocationHelper.class.getName();

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private int mInterval;
    private int mFastestInterval;
    private LocationListener mLocationListener;
    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener;

    public LocationHelper(int interval, int fastestInterval, LocationListener locationListener, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener){
        mInterval = interval;
        mFastestInterval = fastestInterval;
        mLocationListener = locationListener;
        mOnConnectionFailedListener = onConnectionFailedListener;
    }

    /**
     * Strart listening location
     */
    public void initLocationManager(Context context) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(mInterval);
        mLocationRequest.setFastestInterval(mFastestInterval);
//        mLocationRequest.setInterval(mInterval);
//        mLocationRequest.setFastestInterval(mFastestInterval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d(TAG, "onConnected");
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient, mLocationRequest, mLocationListener);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d(TAG, "onConnectionSuspended");
                    }

                })
                .addOnConnectionFailedListener(mOnConnectionFailedListener)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void stopCoordinateListening(){
        Log.d(TAG, "stopCoordinateListening");
        if(mGoogleApiClient != null && mLocationListener != null
            && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, mLocationListener);
        }
    }

    public boolean isLocationEnabled(Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }
}
