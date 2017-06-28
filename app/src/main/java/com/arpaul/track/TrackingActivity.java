package com.arpaul.track;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.arpaul.track.adapter.MyMapAdapter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.hypertrack.lib.HyperTrackMapFragment;
import com.hypertrack.lib.MapFragmentCallback;
import com.hypertrack.lib.models.Action;

import java.util.List;

/**
 * Created by Aritra Ranjan on 5/12/2017.
 */

public class TrackingActivity extends AppCompatActivity {

    private HyperTrackMapFragment htMapFragment;
    private MapFragmentCallback callback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        htMapFragment = (HyperTrackMapFragment) getSupportFragmentManager().findFragmentById(R.id.htMapfragment);
        htMapFragment.setHTMapAdapter(new MyMapAdapter(this));

        callback = new MapFragmentCallback() {
            @Override
            public void onMapReadyCallback(HyperTrackMapFragment hyperTrackMapFragment, GoogleMap map) {
                super.onMapReadyCallback(hyperTrackMapFragment, map);
                Toast.makeText(TrackingActivity.this, "On Map Ready callback", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onHeroMarkerAdded(HyperTrackMapFragment hyperTrackMapFragment, String actionID, Marker heroMarker) {
                super.onHeroMarkerAdded(hyperTrackMapFragment, actionID, heroMarker);
                Toast.makeText(TrackingActivity.this, "Hero Marker Added callback", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onActionStatusChanged(List<String> changedStatusActionIds, List<Action> changedStatusActions) {
                super.onActionStatusChanged(changedStatusActionIds, changedStatusActions);
            }

            @Override
            public void onActionRefreshed(List<String> refreshedActionIds, List<Action> refreshedActions) {
                super.onActionRefreshed(refreshedActionIds, refreshedActions);
            }
        };
        htMapFragment.setMapFragmentCallback(callback);

    }
}
