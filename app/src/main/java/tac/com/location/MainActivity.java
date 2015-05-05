package tac.com.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.locationhelper.LocationService;


public class MainActivity extends ActionBarActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.textview);
    }
    @Override
    protected void onStart() {
        super.onStart();
        LocationService.initLocationHelper(this, 0, 0);
        LocationService.startSendData(this);
    }

    @Override
    protected void onStop() {
        LocationService.endSendData(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Register mMessageReceiver to receive messages.
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationService.BROADCAST_LOCATION_SUCCESS);
        filter.addAction(LocationService.BROADCAST_LOCATION_ERROR);
        registerReceiver(mMessageReceiver, filter);
    }



    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    // handler for received location intents
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(LocationService.BROADCAST_LOCATION_SUCCESS.equals(action)){
                double lat = intent.getDoubleExtra(LocationService.LOCATION_LAT, 0);
                double longitude = intent.getDoubleExtra(LocationService.LOCATION_LONG, 0);
                mTextView.setText("Coordinate: lat: " + lat + " long: " + longitude);
            }else if(LocationService.BROADCAST_LOCATION_ERROR.equals(action)){
                String str = intent.getStringExtra(LocationService.LOCATION_ERROR_MSG);
                mTextView.setText(str);
            }
        }
    };

}
