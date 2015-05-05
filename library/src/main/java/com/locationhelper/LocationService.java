package com.locationhelper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;


/**
 * Created by dima on 1/26/15.
 */
public class LocationService extends Service implements com.google.android.gms.location.LocationListener  {

    private static final String TAG = "LocationService";

    private static final int MIN_FASTEST_INTERVAL = 15000; // 2.5 min
    private static final int MIN_INTERVAL = 30000; // 5 min

    public static final String INIT_LISTEN_LOCATION = "initListenLocation";
    public static final String START_LISTEN_LOCATION = "startListenLocation";
    public static final String END_LISTEN_LOCATION   = "endListenLocation";

    public static final String BROADCAST_LOCATION_SUCCESS = "location_success";
    public static final String BROADCAST_LOCATION_ERROR = "location_error";

    public static final String LOCATION_LAT  = "location_lat";
    public static final String LOCATION_LONG = "location_long";
    public static final String LOCATION_ERROR_MSG = "msg";

    public static final String INTERVAL = "distance";
    public static final String FASTEST_INTERVAL = "time";

    private Thread mTriggerService;

    private LocationHelper mLocationHelper;

    private Handler mLooperHandler;

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Data", "onStartCommand");
        if(intent == null){
            Log.d("Data", "Intent == null");
            return super.onStartCommand(intent, flags, startId);
        }
        if(INIT_LISTEN_LOCATION.equals(intent.getAction())){
            initLocationHelper(intent.getIntExtra(INTERVAL, MIN_INTERVAL), intent.getIntExtra(FASTEST_INTERVAL, MIN_FASTEST_INTERVAL));
        } else if(START_LISTEN_LOCATION.equals(intent.getAction())){
            addLocationListener();
            return  START_REDELIVER_INTENT;
        } else if(END_LISTEN_LOCATION.equals(intent.getAction())) {
            stopLocationListener();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void initLocationHelper(int interval, int fastestInterval) {
        if(mLocationHelper != null){
            mLooperHandler.getLooper().quit();
            stopLocationListener();
        }
        mLocationHelper = new LocationHelper(interval, fastestInterval, this, new OnConnectionFailed());
    }

    private void stopLocationListener() {
        if(mLocationHelper != null){
            mLooperHandler.getLooper().quit();
            mLocationHelper.stopCoordinateListening();
        }
    }

    private void addLocationListener()
    {
        if(mLocationHelper == null){
            Log.d(TAG, "addLocationListener error");
            sendError("LocationHelper is not init. Init helper before start listening location");
            return;
        }
        Log.d(TAG, "addLocationListener");

        // Start thread which listen location
        mTriggerService = new Thread(new Runnable(){
            public void run(){
                try{
                    Looper.prepare();//Initialise the current thread as a looper.
                    mLooperHandler = new Handler();
                    initLocationManager();
                    Looper.loop();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }, "LocationThread");
        mTriggerService.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Strart listening location
     */
    private void initLocationManager() {
        if(mLocationHelper.isLocationEnabled(getApplicationContext())){
            mLocationHelper.initLocationManager(getApplicationContext());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Intent intent = new Intent();
        intent.setAction(BROADCAST_LOCATION_SUCCESS);
        intent.putExtra(LOCATION_LAT, location.getLatitude());
        intent.putExtra(LOCATION_LONG, location.getLongitude());
        sendBroadcast(intent);
    }

    private void sendError(String msg){
        Log.d(TAG, "sendError");
        Intent intent = new Intent();
        intent.setAction(BROADCAST_LOCATION_ERROR);
        intent.putExtra(LOCATION_ERROR_MSG, msg);
        sendBroadcast(intent);
    }
    private class OnConnectionFailed implements GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            sendError(connectionResult.toString());
        }
    }

    public static void initLocationHelper(Context context, int interval, int fastest_interval) {
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction(LocationService.INIT_LISTEN_LOCATION);
        intent.putExtra(LocationService.INTERVAL, interval);
        intent.putExtra(LocationService.FASTEST_INTERVAL, fastest_interval);
        context.startService(intent);
    }

    public static void startSendData(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction(LocationService.START_LISTEN_LOCATION);
        context.startService(intent);
    }

    public static void endSendData(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction(LocationService.END_LISTEN_LOCATION);
        context.startService(intent);
    }
}
