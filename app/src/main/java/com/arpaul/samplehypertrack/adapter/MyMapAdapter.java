package com.arpaul.samplehypertrack.adapter;

import android.content.Context;

import com.arpaul.samplehypertrack.R;
import com.hypertrack.lib.HyperTrackMapAdapter;
import com.hypertrack.lib.HyperTrackMapFragment;

/**
 * Created by Aritra Ranjan on 5/12/2017.
 */

public class MyMapAdapter extends HyperTrackMapAdapter {
    public MyMapAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public int getHeroMarkerIconForActionID(HyperTrackMapFragment hyperTrackMapFragment, String actionID) {
        return R.mipmap.ic_launcher_round;
    }
}
