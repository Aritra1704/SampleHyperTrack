package com.arpaul.track;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.arpaul.track.adapter.ActionsAdapter;
import com.arpaul.track.adapter.MapAdapter;
import com.arpaul.track.listener.MyMapFragmentCallback;
import com.arpaul.utilitieslib.LogUtils;
import com.arpaul.utilitieslib.UnCaughtException;
import com.hypertrack.lib.HyperTrack;
import com.hypertrack.lib.HyperTrackMapAdapter;
import com.hypertrack.lib.HyperTrackMapFragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Aman Jain on 06/03/17.
 */

public class YourMapActivity extends AppCompatActivity {

    private Button btnLogout;
    private String userid = "";
    private RecyclerView rvActions;
    private ArrayList<String> actions = new ArrayList<>();
    private ActionsAdapter adapter;
    public HashMap<String, Boolean> hashAction = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(YourMapActivity.this,"aritra.pal@zippr.com",getString(R.string.app_name)));
        setContentView(R.layout.content_track);

        if(getIntent().hasExtra("userid"))
            userid = getIntent().getStringExtra("userid");
        if(getIntent().hasExtra("actionid"))
            actions = getIntent().getStringArrayListExtra("actionid");

        initUI();

        for(String action : actions) {
            hashAction.put(action, true);
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String key : hashAction.keySet()) {
                    Boolean value = hashAction.get(key);
                    LogUtils.infoLog(key, value + "");
                    if(value)
                        HyperTrack.completeAction(key);
                }

                HyperTrack.stopTracking();
                Intent mainActivityIntent = new Intent(YourMapActivity.this, TrackActionActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainActivityIntent);
                finish();
            }
        });
    }

    private void initUI(){

        /** Initialize Map Fragment added in Activity Layout to getMapAsync
         *  Once map is created onMapReady callback will be fire with GoogleMap object
         */
        HyperTrackMapFragment hyperTrackMapFragment; hyperTrackMapFragment =
                (HyperTrackMapFragment) getSupportFragmentManager().findFragmentById(R.id.htMapfragment);

        /**
         * Call the method below to enable UI customizations for Live Tracking View, an instance of HyperTrackMapAdapter needs to be set as depicted below
         */
        HyperTrackMapAdapter mapAdapter = new MapAdapter(this);
        hyperTrackMapFragment.setHTMapAdapter(mapAdapter);

         /*
         * Call the method below to register for any callbacks/updates on Live Tracking View/Map
         */
        MyMapFragmentCallback mapFragmentCallback = new MyMapFragmentCallback();
        hyperTrackMapFragment.setMapFragmentCallback(mapFragmentCallback);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        rvActions = (RecyclerView) findViewById(R.id.rvActions);
        adapter = new ActionsAdapter(this, actions);
        rvActions.setAdapter(adapter);
    }

}
