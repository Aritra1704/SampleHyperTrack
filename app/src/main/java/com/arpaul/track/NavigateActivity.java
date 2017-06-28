package com.arpaul.track;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arpaul.track.dataObjects.TrackDO;
import com.arpaul.track.services.TrackService;
import com.arpaul.utilitieslib.CalendarUtils;
import com.arpaul.utilitieslib.LogUtils;
import com.arpaul.utilitieslib.PermissionUtils;
import com.google.android.gms.maps.model.LatLng;
import com.hypertrack.lib.HyperTrack;
import com.hypertrack.lib.callbacks.HyperTrackCallback;
import com.hypertrack.lib.models.Action;
import com.hypertrack.lib.models.ActionParamsBuilder;
import com.hypertrack.lib.models.ErrorResponse;
import com.hypertrack.lib.models.Place;
import com.hypertrack.lib.models.SuccessResponse;

import java.util.ArrayList;
import java.util.HashMap;

import static com.arpaul.track.common.ApplicationInstance.ACTION_TRACK_LOCATION;
import static com.arpaul.track.common.ApplicationInstance.INTENT_LOCATION;
import static com.arpaul.track.common.ApplicationInstance.INTENT_TRACK;
import static com.arpaul.track.common.ApplicationInstance.LAT_DESTINATION;
import static com.arpaul.track.common.ApplicationInstance.LON_DESTINATION;

/**
 * Created by Aritra Ranjan on 5/12/2017.
 */

public class NavigateActivity extends BaseActivity {

    private final String TAG = "NavigateActivity";

    private Button btnStopTrack, btnLogout;
    private TextView tvCAddress;
    private String userid = "", address = "";
    private LatLng currentLoc = null;
    boolean mBound = false;
    private ArrayList<String> arrAction = new ArrayList<>();
    private ArrayList<Place> arrExpectPlace = new ArrayList<>();
    private LocationReceiver brLocation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        if(getIntent().hasExtra("userid"))
            userid = getIntent().getStringExtra("userid");
        if(getIntent().hasExtra("selectedLoc"))
            currentLoc = (LatLng) getIntent().getExtras().get("selectedLoc");
        if(getIntent().hasExtra("address"))
            address = getIntent().getStringExtra("address");

        btnStopTrack = (Button) findViewById(R.id.btnStopTrack);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        tvCAddress = (TextView) findViewById(R.id.tvCAddress);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HyperTrack.stopTracking();
                Intent mainActivityIntent = new Intent(NavigateActivity.this, TrackActionActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainActivityIntent);
                finish();
            }
        });

        btnStopTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Boolean> hashAction = new HashMap<>();
                for(String action : arrAction) {
                    hashAction.put(action, true);
                }
                for (String key : hashAction.keySet()) {
                    Boolean value = hashAction.get(key);
                    LogUtils.infoLog(key, value + "");
                    if(value)
                        HyperTrack.completeAction(key);
                }
                stopLocalTrack();
            }
        });

        startHypertrack();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (new PermissionUtils().checkPermission(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}) != PackageManager.PERMISSION_GRANTED) {
                new PermissionUtils().verifyPermission(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION});
            } else
                startLocalTrack();
        } else
            startLocalTrack();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (mBound) {
//            Intent intent = new Intent(this, TrackService.class);
//            bindService(intent, mServerConn, Context.BIND_AUTO_CREATE);
//        }
        registerBroadcast();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (mBound) {
//            unbindService(mServerConn);
//        }
        unregisterBroadcast();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            int location = 0;
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(android.Manifest.permission.ACCESS_COARSE_LOCATION) &&
                        grantResult == PackageManager.PERMISSION_GRANTED) {
                    location++;
                } else if (permission.equals(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                        grantResult == PackageManager.PERMISSION_GRANTED) {
                    location++;
                }
            }

            if(location == 2) {
                startLocalTrack();
            } else {
                Toast.makeText(NavigateActivity.this, "Allow app to access location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Call this method when user has successfully logged in
     */
    private void startHypertrack() {
        LatLng expectedLatLng = new LatLng(LAT_DESTINATION, LON_DESTINATION);

        arrExpectPlace.clear();
        arrAction.clear();
        Place expectedPlace = new Place().setLocation(expectedLatLng.latitude, expectedLatLng.longitude)
//                    .setAddress(currentLatLng)
                .setName("Delivery1");
        arrExpectPlace.add(expectedPlace);

        if(currentLoc != null) {
            Place expectedPlace2 = new Place().setLocation(currentLoc.latitude, currentLoc.longitude)
                    .setAddress(address)
                    .setName("Delivery2");
            arrExpectPlace.add(expectedPlace2);
        }

        Place expectPlace = arrExpectPlace.get(0);

        assignAction(expectPlace, 0);
    }

    void assignAction(final Place expectPlace , final int i) {
        ActionParamsBuilder builder = new ActionParamsBuilder();
        String dateFormat = CalendarUtils.DATE_TIME_FORMAT;

        builder.setExpectedPlace(expectPlace)
                .setType(Action.ACTION_TYPE_STOPOVER)
                .setExpectedAt(CalendarUtils.getDateFromString(CalendarUtils.getDateinPattern(dateFormat),dateFormat))
                .setLookupId(i + " 1");

        HyperTrack.createAndAssignAction(builder.build(), new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse response) {
                // Handle createAndAssignAction success here
                Action action = (Action) response.getResponseObject();
                arrAction.add(action.getId());
                Toast.makeText(NavigateActivity.this, "action: " + action.getLookupId(), Toast.LENGTH_SHORT).show();

                if((i + 1) < arrExpectPlace.size()) {
                    int posi = i + 1;
                    Place place = arrExpectPlace.get(posi);
                    assignAction(place, posi);
                }
                else
                    startTracking(userid, arrAction);
            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                Toast.makeText(NavigateActivity.this, errorResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                hideProgressbar();
            }
        });
    }

    void startTracking(final String userid, final ArrayList<String> actions) {
        HyperTrack.startTracking(new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse successResponse) {
                // Hide Login Button loader

                Toast.makeText(NavigateActivity.this, "Success" + successResponse.getResponseObject(), Toast.LENGTH_SHORT).show();

                // Call trackAction API method with action ID for tracking.
                // Start YourMapActivity containing HyperTrackMapFragment view with the customization on success
                // response of trackAction method
                HyperTrack.trackAction(actions, new HyperTrackCallback() {
                    @Override
                    public void onSuccess(@NonNull SuccessResponse response) {
                        // Start User Session by starting MainActivity
                        Intent mainActivityIntent = new Intent(NavigateActivity.this, NavigateActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mainActivityIntent.putExtra("userid", userid);
                        mainActivityIntent.putExtra("actionid", actions);
                        startActivity(mainActivityIntent);
                        finish();
                    }

                    @Override
                    public void onError(@NonNull ErrorResponse errorResponse) {
                        Toast.makeText(NavigateActivity.this, "Error Occurred while trackActions: " + errorResponse.getErrorMessage(),
                                Toast.LENGTH_LONG).show();
                        hideProgressbar();
                    }
                });
            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                Toast.makeText(NavigateActivity.this, "Error" + " " + errorResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                hideProgressbar();
            }
        });
    }

    protected ServiceConnection mServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.debugLog(TAG, "onServiceConnected");

            TrackService.LocalBinder binder = (TrackService.LocalBinder) service;
//            mService = binder.getService();
            mBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.debugLog(TAG, "onServiceDisconnected");
        }
    };

    public void startLocalTrack() {
        mBound = true;

        Intent intent = new Intent(this, TrackService.class);
        TrackDO objTrackDO = new TrackDO();
        objTrackDO.user_id = userid;
        intent.putExtra(INTENT_TRACK, objTrackDO);
//        bindService(intent, mServerConn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    public void stopLocalTrack() {
        mBound = false;

        stopService(new Intent(this, TrackService.class));
//        unbindService(mServerConn);
    }

    private void registerBroadcast(){
        try {
            brLocation = new LocationReceiver();
            registerReceiver(brLocation, new IntentFilter(ACTION_TRACK_LOCATION));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void unregisterBroadcast(){
        try {
            unregisterReceiver(brLocation);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class LocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra(INTENT_LOCATION)) {
                TrackDO objTrack = (TrackDO) intent.getExtras().get(INTENT_LOCATION);
                if(objTrack != null)
                    tvCAddress.setText(objTrack.address);
            }
        }
    }
}
